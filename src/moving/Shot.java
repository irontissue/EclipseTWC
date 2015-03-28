package moving;

import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.geom.AffineTransform;
import java.util.ArrayList;
import java.util.logging.Level;

/**
 * Contains all info/data for a single bullet/shot and updates all information
 * for it.
 * 
 * @author EclipseTWC
 */
public class Shot
{
    public double speed, range, angle, gravity, currentRange = 0;
    public double x, y, xspeed, yspeed;
    
    public ArrayList<Point> cellsHit = new ArrayList();
    public ArrayList<Point> shotLocs = new ArrayList();
    public ArrayList<Enemy> enemiesHit = new ArrayList();
    
    public int damage;
    
    public boolean friendly;
    public boolean crit = false;
    private boolean remove = false;
    public boolean melee;
    public boolean piercesEnemies, piercesWalls;
    public boolean attacked = false;
    public boolean playerHit = false;
    public boolean meleeShootingRight;
    public boolean reverseImage = false;
    
    private AffineTransform at;
    
    public Image shotImage;
    
    private Game myGame;
    
    private Object referencePoint;
    private Player p;
    private Enemy e;
    
    /**
     * Default constructor.
     * 
     * @param myGame            A copy of the current Game.
     * @param x                 The x-location of the shot on the map.
     * @param y                 The y-location of the shot on the map.
     * @param damageRange       The shot's damage, represented by an int array of two values (lower bound - upper bound)
     * @param spreadAngle       The error in angle in the shot, in degrees. Each shot has a certain "error" when shooting, which is called the "spread" angle.
     * @param angle             The angle at which the shot is fired.
     * @param speed             The shot's speed in pixels/frame (each frame is 1/60th of a second).
     * @param range             The shot's range in pixels.
     * @param grav              The acceleration downwards of the shot, in pixels/frame (each frame is 1/60th of a second).
     * @param piercesEnemies    Whether this shot pierces enemies (hits multiple enemies or not).
     * @param piercesWalls      Whether this shot pierces walls (can hit multiple blocks or not).
     * @param friendly          Whether it is a friendly bullet or not (friendly bullets, which are the player's bullets, harm enemies only, and vice-versa).
     * @param melee             Whether this is a melee shot (if it's a melee shot then it must follow the Object that holds the melee weapon).
     * @param referencePoint    The reference point which the shot will stick to if this is a melee weapon.
     * @param shotImage         The shot's image.
     */
    public Shot(Game myGame, int x, int y, double angle, double speed, double range, double grav, int spreadAngle, boolean piercesEnemies, boolean piercesWalls, int[] damageRange, boolean friendly, boolean melee, Object referencePoint, Image shotImage)
    {
        //System.out.println(Grid.CELL_SIZE);
        try
        {
            this.x = x;
            this.y = y;
            this.speed = speed;
            int rand = (int)(Math.random()*2);
            if(rand == 0)
                this.angle = angle+(Math.toRadians((int)(Math.random()*(spreadAngle+1))));
            else
                this.angle = angle-(Math.toRadians((int)(Math.random()*(spreadAngle+1))));
            this.range = range;
            this.piercesEnemies = piercesEnemies;
            this.piercesWalls = piercesWalls;
            this.shotImage = shotImage;
            shotLocs.add(new Point(0,0));
            for(int zz = (int)Math.round(shotImage.getWidth(null)/2); zz > 0; zz -= Grid.CELL_SIZE)
            {
                int xx = (int)Math.round(zz*Math.cos(angle));
                int yy = (int)Math.round(zz*Math.sin(angle));
                shotLocs.add(new Point(xx,yy));
                shotLocs.add(new Point(-xx,-yy));
            }
            for(int zz = (int)Math.round(shotImage.getHeight(null)/2); zz > 0; zz -= Grid.CELL_SIZE)
            {
                int xx = (int)Math.round(zz*Math.cos(angle+(Math.PI/2.0)));
                int yy = (int)Math.round(zz*Math.sin(angle+(Math.PI/2.0)));
                shotLocs.add(new Point(xx,yy));
                shotLocs.add(new Point(-xx,-yy));
            }
            damage = (int) (Math.random()*(damageRange[1]-damageRange[0])+damageRange[0]);
            this.friendly = friendly;
            this.melee = melee;
            gravity = grav;
            xspeed = (Math.cos(this.angle)*speed);
            yspeed = (Math.sin(this.angle)*speed);
            this.myGame = myGame;
            this.referencePoint = referencePoint;
            if(this.referencePoint != null)
            {
                if(this.referencePoint.getClass().equals(Player.class))
                {
                    p = (Player) this.referencePoint;
                }
                else //reference point is player
                {
                    e = (Enemy) this.referencePoint;
                }
            }
            if(melee)
            {
                if(p != null)
                {
                    if(myGame.mouseX >= Game.cameraX+myGame.player.x)
                    {
                        this.angle-=Math.toRadians(range/2);
                        meleeShootingRight = true;
                    }
                    else
                    {
                        this.angle+=Math.toRadians(range/2);
                        meleeShootingRight = false;
                    }
                }
                else
                {
                    if(myGame.player.x >= e.x)
                    {
                        this.angle-=Math.toRadians(range/2);
                        meleeShootingRight = true;
                    }
                    else
                    {
                        this.angle+=Math.toRadians(range/2);
                        meleeShootingRight = false;
                    }
                }
            }
            else
            {
                if(friendly && myGame.mouseX < Game.cameraX+myGame.player.x)
                {
                    this.speed *= -1;
                    this.xspeed *= -1;
                    this.yspeed *= -1;
                }
            }
            updateShot();
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
            Main.logger.log(Level.SEVERE, "Error in Shot() constructor in Shot.class.");
        }
    }
    
    /**
     * Draws the shot.
     * 
     * @param g The object on which the graphics is drawn.
     */
    public void drawShot(Graphics g)
    {
        try
        {
            Graphics2D g2 = (Graphics2D) g;
            g2.drawImage(shotImage, at, null);
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Main.logger.log(Level.SEVERE, "Error in drawShot() method in Shot.class.");
        }
    }
    
    /**
     * Updates the shot and rotates the image based on its angle.
     */
    public void updateShot()
    {
        try
        {
            yspeed += gravity;
            x+=xspeed;
            y+=yspeed;
            if(melee)
            {
                if(meleeShootingRight)
                {
                    if(p != null)
                    {
                        x = p.x+(Math.cos(angle)*Math.pow(shotImage.getWidth(null), 2.0/3)*3.2);
                        y = p.y+(Math.sin(angle)*Math.pow(shotImage.getWidth(null), 2.0/3)*3.2)-2;
                    }
                    else
                    {
                        x = e.x+(Math.cos(angle)*Math.pow(shotImage.getWidth(null), 2.0/3)*3.2)+e.img.getWidth(null)/2;
                        y = e.y+(Math.sin(angle)*Math.pow(shotImage.getWidth(null), 2.0/3)*3.2)+e.img.getHeight(null)/2;
                    }
                    angle+=Math.toRadians(speed);
                }
                else
                {
                    if(p != null)
                    {
                        x = p.x-(Math.cos(angle)*Math.pow(shotImage.getWidth(null), 2.0/3)*3.2);
                        y = p.y-(Math.sin(angle)*Math.pow(shotImage.getWidth(null), 2.0/3)*3.2)-2;
                    }
                    else
                    {
                        x = e.x-(Math.cos(angle)*Math.pow(shotImage.getWidth(null), 2.0/3)*3.2)+e.img.getWidth(null)/2;
                        y = e.y-(Math.sin(angle)*Math.pow(shotImage.getWidth(null), 2.0/3)*3.2)+e.img.getHeight(null)/2;
                    }
                    angle-=Math.toRadians(speed);
                }
            }
            if(speed != 0)
            {
                currentRange+=speed;
            }
            else
            {
                currentRange+=1;
            }
            if(Math.abs(currentRange) >= range)
            {
                remove = true;
            }
            if(!melee)
            {
                angle = Math.atan(yspeed/xspeed);
            }
            shotLocs.clear();
            shotLocs.add(new Point(0,0));
            for(int zz = (int)Math.round(shotImage.getWidth(null)/2); zz > 0; zz -= Grid.CELL_SIZE)
            {
                int xx = (int)Math.round(zz*Math.cos(angle));
                int yy = (int)Math.round(zz*Math.sin(angle));
                shotLocs.add(new Point(xx,yy));
                shotLocs.add(new Point(-xx,-yy));
            }
            for(int zz = (int)Math.round(shotImage.getHeight(null)/2); zz > 0; zz -= Grid.CELL_SIZE)
            {
                int xx = (int)Math.round(zz*Math.cos(angle+(Math.PI/2.0)));
                int yy = (int)Math.round(zz*Math.sin(angle+(Math.PI/2.0)));
                shotLocs.add(new Point(xx,yy));
                shotLocs.add(new Point(-xx,-yy));
            }
            at = new AffineTransform();
            at.translate(Math.round(x)-shotImage.getWidth(null)/2,Math.round(y)-shotImage.getHeight(null)/2);
            if(melee)
            {
                if(meleeShootingRight)
                {
                    at.rotate(angle, shotImage.getWidth(null)/2, shotImage.getHeight(null)/2);
                }
                else
                {
                    at.rotate(angle+Math.toRadians(180), shotImage.getWidth(null)/2, shotImage.getHeight(null)/2);
                }
            }
            else
            {
                if(xspeed >= 0)
                {
                    at.rotate(angle, shotImage.getWidth(null)/2, shotImage.getHeight(null)/2);
                }
                else
                {
                    at.rotate(angle+Math.toRadians(180), shotImage.getWidth(null)/2, shotImage.getHeight(null)/2);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Main.logger.log(Level.SEVERE, "Error in updateShot() method in Shot.class.");
        }
    }
    
    /**
     * Checks to see if this shot needs to be removed from the "shots" array
     * in the Game class.
     * 
     * @return True if this shot needs to be removed, false if not.
     */
    public boolean needsToBeRemoved()
    {
        try
        {
            return remove;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Main.logger.log(Level.SEVERE, "Error in needsToBeRemoved() method in Shot.class.");
            return true;
        }
    }
    
    /**
     * Sets whether the Shot needs to be removed or not.
     * 
     * @param nRemove The new value for whether the shot should be removed or not.
     */
    public void setRemove(boolean nRemove)
    {
        try
        {
            remove = nRemove;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Main.logger.log(Level.SEVERE, "Error in setRemove() method in Shot.class.");
        }
    }
    
    /**
     * Returns the coordinates of the shot.
     * 
     * @return A Point representing the location of the Shot.
     */
    public Point getCoord()
    {
        try
        {
            return new Point((int) Math.round(x), (int) Math.round(y));
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Main.logger.log(Level.SEVERE, "Error in getCoord() method in Shot.class.");
            return null;
        }
    }
    
    /**
     * Returns the damage that the shot does.
     * 
     * @return the damage the shot does.
     */
    public int getDamage()
    {
        try
        {
            return damage;
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Main.logger.log(Level.SEVERE, "Error in geDamage() method in Shot.class.");
            return 0;
        }
    }
}
