package moving;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;
import java.util.LinkedHashMap;

/**
 * Manages and stores all values for an Item. This particular class is a
 * generic "shell" for an item. Classes that extend this class get into more
 * particular items, such as PotionJar and RangedWeapon.
 * 
 * @author EclipseTWC
 */
public class Item
{
    public Image img;
    
    public String name, description;
    
    public double xOnMap, yOnMap;
    
    public double grav = -(Math.random()*3)-3;
    public double xSpeed = (Math.random()*7)-3;
    
    public boolean isOnMap;
    
    public static final int NOT_ON_MAP_COORD = -99999999;
    
    public int removeTimer = 0;
    public int removeAt = 3000;
    
    public Font descFont = new Font(Game.DEFAULT_FONT, Font.PLAIN, 12);
    
    public LinkedHashMap<String, Color> checkWidth = new LinkedHashMap();
    
    /**
     * Default constructor.
     * 
     * @param name          The name of the item.
     * @param description   The item's description.
     * @param xOnMap        The x-location of the item on the map, if it is on the map.
     * @param yOnMap        The y-location of the item on the map, if it is on the map.
     * @param isOnMap       Whether the item is on the map or not.
     * @param img           The Image of the item.
     */
    public Item(String name, String description, int xOnMap, int yOnMap, boolean isOnMap, Image img)
    {
        //System.out.println(Grid.CELL_SIZE);
        this.name = name;
        this.description = description;
        this.xOnMap = xOnMap;
        this.yOnMap = yOnMap;
        this.isOnMap = isOnMap;
        this.img = img;
    }
    
    /**
     * Updates the item's position and checks for collision.
     * 
     * @param myGame A copy of the current Game.
     */
    public void updatePosition(Game myGame)
    {
        boolean isOnSolidGround = false;
        removeTimer++;
        int xxMax = img.getWidth(null);
        if(xxMax <= Grid.CELL_SIZE)
        {
            xxMax = Grid.CELL_SIZE+1;
        }
        int yyMax = img.getHeight(null);
        if(yyMax <= Grid.CELL_SIZE)
        {
            yyMax = Grid.CELL_SIZE+1;
        }
        if(Math.abs(xSpeed) > 0.05)
        {
            if(xSpeed > 0)
            {
                xSpeed -= 0.1;
            }
            else if(xSpeed < 0)
            {
                xSpeed += 0.1;
            }
        }
        else
        {
            xSpeed = 0;
        }
        xOnMap+=xSpeed;
        try
        {
            for(int xx = (int)Math.round(xOnMap)/Grid.CELL_SIZE; xx < ((int)(Math.round(xOnMap)+xxMax)/Grid.CELL_SIZE); xx++)
            {
                Cell cell = myGame.grid.cells[xx][((int)Math.round(yOnMap)+img.getHeight(null))/Grid.CELL_SIZE];
                if(cell.isSolid && cell.isReal == 0)
                {
                    isOnSolidGround = true;
                    xx = ((int)Math.round(xOnMap)+img.getWidth(null)/Grid.CELL_SIZE);
                }
            }
            for(int yy = (int)Math.round(yOnMap)/Grid.CELL_SIZE; yy < ((int)(Math.round(yOnMap)+yyMax)/Grid.CELL_SIZE); yy++)
            {
                Cell cellRight = myGame.grid.cells[((int)Math.round(xOnMap)+img.getWidth(null))/Grid.CELL_SIZE][yy];
                Cell cellLeft = myGame.grid.cells[((int)Math.round(xOnMap))/Grid.CELL_SIZE][yy];
                if(cellRight.isSolid && cellRight.isReal == 0)
                {
                    yy = ((int)Math.round(yOnMap)+img.getHeight(null)/Grid.CELL_SIZE);
                    xSpeed = 0;
                    xOnMap-=5;
                }
                if(cellLeft.isSolid && cellLeft.isReal == 0)
                {
                    yy = ((int)Math.round(yOnMap)+img.getHeight(null)/Grid.CELL_SIZE);
                    xSpeed = 0;
                    xOnMap+=5;
                }
            }
        }
        catch(Exception ex)
        {
            
        }
        if(!isOnSolidGround)
        {
            grav+=0.2;
        }
        else
        {
            grav = -grav*0.4;
            if(Math.abs(grav) < 0.1)
            {
                grav = 0;
            }
            yOnMap = myGame.grid.cells[0][((int)Math.round(yOnMap+img.getHeight(null)))/Grid.CELL_SIZE].getDrawCoord().y - img.getHeight(null);
        }
        if(grav > 8)
        {
            grav = 8;
        }
        yOnMap+=grav;
    }
    
    /**
     * Draws the item description (the box that appears when you hover over an
     * item with your mouse in the game).
     * 
     * @param g The object on which to draw the graphics.
     * @param x The x coordinate to draw the item description at.
     * @param y The y coordinate to draw the item description at.
     */
    public void drawItemDescription(Graphics g, int x, int y)
    {
        g.setFont(descFont);
        FontMetrics fm = g.getFontMetrics();
        ArrayList<String> keys = new ArrayList();
        for(String s : checkWidth.keySet())
        {
            keys.add(s);
        }
        ArrayList<Color> vals = new ArrayList();
        for(Color c : checkWidth.values())
        {
            vals.add(c);
        }
        for(int i = 0; i < keys.size(); i++)
        {
            String s = keys.get(i);
            if(fm.stringWidth(s) > 250)
            {
                String selec = "";
                String[] ssplit = s.split(" ");
                int indexOfS = keys.indexOf(s);
                keys.remove(indexOfS);
                Color c = vals.get(indexOfS);
                vals.remove(indexOfS);
                i--;
                int index = 0;
                while(index < ssplit.length)
                {
                    if(fm.stringWidth(selec + " " + ssplit[index]) > 250)
                    {
                        keys.add(indexOfS, selec);
                        vals.add(indexOfS, c);
                        indexOfS++;
                        selec = ssplit[index];
                        if(index == ssplit.length-1)
                        {
                            keys.add(indexOfS, selec);
                            vals.add(indexOfS, c);
                            indexOfS++;
                            selec = "";
                        }
                    }
                    else if(index == ssplit.length-1)
                    {
                        selec += " " + ssplit[index];
                        keys.add(indexOfS, selec);
                        vals.add(indexOfS, c);
                        indexOfS++;
                        selec = "";
                    }
                    else
                    {
                        if(selec.length() == 0)
                        {
                            selec += ssplit[index];
                        }
                        else
                        {
                            selec += " " + ssplit[index];
                        }
                    }
                    index++;
                }
            }
        }
        String longest = "";
        for(String s : keys)
        {
            if(fm.stringWidth(s) > fm.stringWidth(longest))
            {
                longest = s;
            }
        }
        int width = fm.stringWidth(longest);
        if(x+width+12 > Game.WIDTH-Game.cameraX)
        {
            x -= width+12;
        }
        if(y+keys.size()*13+9 > Game.HEIGHT-Game.cameraY)
        {
            y -= keys.size()*13+9;
        }
        g.setColor(Color.BLACK);
        g.drawRoundRect(x, y, width+12, keys.size()*13+9, 20, 20);
        g.drawRoundRect(x-1, y-1, width+13, keys.size()*13+10, 20, 20);
        g.setColor(new Color(110,110,110,245));
        g.fillRoundRect(x, y, width+12, keys.size()*13+9, 20, 20);
        for(int i = 0; i < keys.size(); i++)
        {
            g.setColor(vals.get(i));
            g.drawString(keys.get(i), x+6, y+15+13*i);
        }
    }
    
    /**
     * Used for exporting the item to the map file. This is only used if the item
     * is on the map.
     * 
     * @return A String representing this item's data.
     */
    public String export()
    {
        return (name + "," + xOnMap + "," + yOnMap);
    }
}
