package moving;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * The default enemy. Controls enemy actions, movement, and drawing. Classes that
 * extend this class can be made into custom enemies (such as Boss1.java).
 * 
 * @author EclipseTWC
 */
public class Enemy
{
    public Image currImg;
    public Image img;
    public Image img2;
    
    public String name;
    
    private Game myGame;
    
    public Graphics g;
    
    public int health, maxHealth;
    public int level, xp;
    public int changeTimer = 0;
    private final int SPEED = 2;
    private int xSpeed = SPEED;
    public double x, y;
    public double grav = 0;
    public double gravConstant = 0.23;
    public double currAtt = 0, attSpeed = 60;
    public double difficulty;
    
    public boolean removed = false;
    public boolean faceRight = false;
    
    public ArrayList<DamageText> damages = new ArrayList();
    
    public HashMap<Item, Double> drops = new HashMap();
    
    /**
     * Default constructor.
     * 
     * @param name          Enemy's name.
     * @param x             The x-position of the enemy on the map.
     * @param y             The y-position of the enemy on the map.
     * @param drops         A HashMap that represents the drops of the enemy (Item, and the chance of it dropping from the enemy once the enemy dies.)
     * @param img           The Image of the Enemy.
     * @param difficulty    A double that will multiply the enemy's xp, health, and damage by the amount given; difficulty has a capped range
     *                      from 1.0 to 5.0.
     */
    public Enemy(String name, int x, int y, HashMap<Item, Double> drops, Image img, double difficulty)
    {
        this.name = name;
        this.x = x;
        this.y = y;
        this.img = img;
        this.img2 = AssetManager.getImage(AssetManager.getImageSource(img)+"2");
        currImg = img;
        this.drops = drops;
        this.level = 5;
        this.xp = (int)(10*difficulty);
        this.health = (int)(10*difficulty);
        maxHealth = health;
    }
    
    /**
     * Draws the enemy.
     * 
     * @param g The object on which to draw the graphics.
     */
    public void drawEnemy(Graphics g)
    {
        this.g = g;
        g.setColor(Color.RED);
        g.fillRect((int)Math.round(x), (int)Math.round(y)-8, (int)Math.round(currImg.getWidth(null)*(health*1.0/maxHealth)), 5);
        //g.drawString(health+"", (int)Math.round(x), (int)Math.round(y)-15);
        if(Math.abs(xSpeed) > 0)
        {
            changeTimer++;
            if(changeTimer >= 30)
            {
                changeTimer = 0;
                if(currImg.equals(img))
                {
                    currImg = img2;
                }
                else
                {
                    currImg = img;
                }
            }
        }
        else
        {
            currImg = img;
        }
        if(faceRight)
        {
            g.drawImage(currImg, (int)Math.round(x)+currImg.getWidth(null), (int)Math.round(y), -currImg.getWidth(null), currImg.getHeight(null), null);
        }
        else
        {
            g.drawImage(currImg, (int)Math.round(x), (int)Math.round(y), null);
        }
        for(int i = 0; i < damages.size(); i++)
        {
            damages.get(i).drawText(g);
        }
    }
    
    /**
     * Updates the enemy and its collision/movement.
     * 
     * @param myGame    A copy of the current Game.
     * @return          Whether the enemy is dead (true) or not (false)
     */
    public boolean updateEnemy(Game myGame) //returns whether this enemy is dead (true) or not (false)
    {
        this.myGame = myGame;
        if(health <= 0)
        {
            health = 0;
            //SoundEffect.playAudio(new File("resources/audio/effects/enemyDead.wav"));
            removed = true;
        }
        while(damages.size() > 10)
        {
            damages.remove(0);
        }
        move(myGame);
        boolean isOnSolidGround = false;
        try
        {
            for(int xx = (int)Math.round(x)/Grid.CELL_SIZE; xx <= ((int)(Math.round(x)+currImg.getWidth(null))/Grid.CELL_SIZE); xx++)
            {
                Cell cell = myGame.grid.cells[xx][((int)Math.round(y)+currImg.getHeight(null))/Grid.CELL_SIZE];
                if(cell.isSolid && cell.isReal == 0)
                {
                    isOnSolidGround = true;
                    xx = ((int)Math.round(x)+currImg.getWidth(null)/Grid.CELL_SIZE);
                }
            }
            Cell cell2 = myGame.grid.cells[((int)(Math.round(x)+currImg.getWidth(null))/Grid.CELL_SIZE)][((int)Math.round(y)+currImg.getHeight(null))/Grid.CELL_SIZE];
            if(cell2.isSolid && cell2.isReal == 0)
            {
                isOnSolidGround = true;
            }
            for(int yy = (int)Math.round(y)/Grid.CELL_SIZE; yy < ((int)(Math.round(y)+currImg.getHeight(null))/Grid.CELL_SIZE); yy++)
            {
                Cell cRight = myGame.grid.cells[((int)Math.round(x)+currImg.getWidth(null))/Grid.CELL_SIZE][yy];
                Cell cLeft = myGame.grid.cells[(int)Math.round(x)/Grid.CELL_SIZE][yy];
                if(cRight.isSolid && cRight.isReal == 0)
                {
                    yy = ((int)Math.round(y)+currImg.getHeight(null)/Grid.CELL_SIZE);
                    x = cRight.getDrawCoord().x-currImg.getWidth(null)-5;
                }
                if(cLeft.isSolid && cLeft.isReal == 0)
                {
                    yy = ((int)Math.round(y)+currImg.getHeight(null)/Grid.CELL_SIZE);
                    x = cLeft.getDrawCoord().x+Grid.CELL_SIZE+5;
                }
            }
        }
        catch(Exception e)
        {
            
        }
        if(!isOnSolidGround || grav < 0)
        {
            grav+=gravConstant;
        }
        else
        {
            grav = 0;
            y = myGame.grid.cells[0][((int)Math.round(y+currImg.getHeight(null)))/Grid.CELL_SIZE].getDrawCoord().y - currImg.getHeight(null);
        }
        if(grav > 8)
        {
            grav = 8;
        }
        y+=grav;
        shotsAdd(myGame);
        for(int i = 0; i < damages.size(); i++)
        {
            boolean removeDamage = damages.get(i).updateText();
            if(removeDamage)
            {
                damages.remove(i);
                i--;
            }
        }
        return removed;
    }
    
    /**
     * Makes the enemy shoot/attack.
     * 
     * @param myGame A copy of the current Game.
     */
    public void shotsAdd(Game myGame)
    {
        currAtt++;
        if(currAtt > attSpeed)
        {
            currAtt = 0;
            double angle;
            if(myGame.player.x > x+currImg.getWidth(null)/2)
            {
                angle = 0;
            }
            else
            {
                angle = Math.PI;
            }
            int[] dmgRange = {(int)(8*difficulty),(int)(12*difficulty)};
            try
            {
                myGame.shots.add(new Shot(myGame, (int)Math.round(x+currImg.getWidth(null)/2), (int)Math.round(y+currImg.getHeight(null)/2), angle, 3, 200, 0, 5, false, false, dmgRange, false, false, this, AssetManager.getImage("shot2")));
            }
            catch (Exception ex)
            {
                ex.printStackTrace();
            }
        }
    }
    
    /**
     * Moves the enemy.
     * 
     * @param myGame A copy of the current Game.
     */
    public void move(Game myGame)
    {
        x+=xSpeed;
        try
        {
            for(int yy = (int)Math.round(y)/Grid.CELL_SIZE; yy < ((int)(Math.round(y)+currImg.getHeight(null))/Grid.CELL_SIZE); yy++)
            {
                Cell cRight = myGame.grid.cells[((int)Math.round(x)+currImg.getWidth(null))/Grid.CELL_SIZE][yy];
                Cell cLeft = myGame.grid.cells[(int)Math.round(x)/Grid.CELL_SIZE][yy];
                if(cRight.isSolid && cRight.isReal == 0)
                {
                    xSpeed = -SPEED;
                }
                if(cLeft.isSolid && cLeft.isReal == 0)
                {
                    xSpeed = SPEED;
                }
            }
        }
        catch(Exception ex)
        {
            
        }
        if(xSpeed > 0)
        {
            faceRight = true;
        }
        else
        {
            faceRight = false;
        }
    }
    
    /**
     * Fetches the location of the enemy.
     * 
     * @return A point that represents the location of the enemy.
     */
    public Point getCoord()
    {
        return new Point((int)Math.round(x),(int)Math.round(y));
    }
    
    /**
     * Checks to see if the given Point hits the enemy.
     * 
     * @param p The test point.
     * @return  Whether the test point hits the enemy.
     */
    public boolean hits(Point p)
    {
        if(p.x > x && p.y > y && p.x < x+currImg.getWidth(null) && p.y < y+currImg.getHeight(null))
        {
            return true;
        }
        return false;
    }
    
    /**
     * Fetches the enemy's current health.
     * 
     * @return The enemy's current health.
     */
    public int getHealth()
    {
        return health;
    }
    
    /**
     * Sets the enemy's current health to the indicated value.
     * 
     * @param newHealth The new health amount.
     */
    public void setHealth(int newHealth)
    {
        health = newHealth;
    }
    
    /**
     * Fetches the enemy's maximum health.
     * 
     * @return The enemy's maximum health.
     */
    public int getMaxHealth()
    {
        return maxHealth;
    }
    
    /**
     * Fetches the enemy's level. We were originally going to have a use for this,
     * but it turned out that we didn't need to use it.
     * 
     * @return The level of the enemy.
     */
    public int getLevel()
    {
        return level;
    }
    
    /**
     * Fetches the experience points this enemy gives once the player kills it.
     * 
     * @return The experience points of the enemy.
     */
    public int getXP()
    {
        return xp;
    }
    
    /**
     * Fetches the current image of the enemy.
     * 
     * @return the enemy's image.
     */
    public Image getImage()
    {
        return currImg;
    }
}