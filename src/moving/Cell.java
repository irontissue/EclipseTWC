package moving;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Point;

/**
 * A single Cell in the Grid of Cells that represents the entire map on which
 * the player moves.
 * 
 * @author EclipseTWC
 */
public class Cell
{
    private Image img;
    
    public static final int NULL = 0, WATER = 1,
            VILLAGE_STONE = 2, VILLAGE_WOOD = 3,
            FOREST_STONE = 4, FOREST_WOOD = 5,
            CLOCK_METAL = 6, CLOCK_WOOD = 7,
            ICE_STONE = 8, SNOW_BLOCK = 9,
            MOON_STONE = 10, MOON_DUST_BLOCK = 11;
    
    private int type;
    public int isReal;
    private int gridX, gridY, drawX, drawY;
    public int health;
    
    public boolean updated = false; //If it is "updated", then (if there's water
                                    //on this panel) the water won't be updated.
                                    //Prevents a water particle from going across
                                    //the map instantly.
    public boolean isSolid = false;
    public boolean discovered = false;
    
    /**
     * Default constructor. Sets the type of the cell (ground, etc). Informs the
     * cell of what position in the grid it is in, and sets the drawing
     * coordinate of the cell based on that position. Sets the cell's health,
     * too.
     * 
     * @param cellType  int representing the type of cell it is, e.g. ground. See the final constants above.
     * @param x         The cell's x position on the grid
     * @param y         The cell's y position on the grid
     * @param health    The cell's health.
     */
    public Cell(int cellType, int x, int y, int health, int isReal)
    {
        //System.out.println(Grid.CELL_SIZE);
        setType(cellType);
        gridX = x;
        gridY = y;
        drawX = gridX*Grid.CELL_SIZE;
        drawY = gridY*Grid.CELL_SIZE;
        this.health = health;
        this.isReal = isReal;
    }
    
    /**
     * Draws the cell.
     * 
     * @param g The object on which the graphics is drawn.
     */
    public void drawCell(Graphics g)
    {
        if(discovered)
        {
            if(type == WATER)
            {
                int r = 0;
                int gr = 0;
                int b = (int) (Math.random()*136)+120;
                g.setColor(new Color(r, gr, b, 100));
                g.fillRect(drawX, drawY, Grid.CELL_SIZE, Grid.CELL_SIZE);
            }
            else
            {
                if(type != NULL && type != WATER && gridY-1 > -1 && (Game.grid.cells[gridX][gridY-1].type == NULL || Game.grid.cells[gridX][gridY-1].type == WATER))
                {
                    String s = ELCCell.types[type]+"Top";
                    img = AssetManager.getImage(s);
                }
                else
                {
                    if(type == NULL)
                        img = null;
                    else
                        img = AssetManager.getImage(ELCCell.types[type]);
                }
                g.drawImage(img, drawX, drawY, Grid.CELL_SIZE, Grid.CELL_SIZE, null);
                if(isReal == 1)
                {
                    g.setColor(new Color(0,0,0,3));
                    g.fillRect(drawX, drawY, Grid.CELL_SIZE, Grid.CELL_SIZE);
                }
            }
            if(health != 10)
            {
                g.setFont(new Font(Game.DEFAULT_FONT, Font.PLAIN, 11));
                g.setColor(Color.BLACK);
                g.drawString(health + "", drawX, drawY+img.getHeight(null)/2);
            }
        }
    }
    
    /**
     * Fetches the cell's type.
     * 
     * @return an int that represents the cell's type.
     */
    public int getType()
    {
        return type;
    }
    
    /**
     * Sets the cell's type to the indicated new type.
     * 
     * @param newType The new celltype.
     */
    public void setType(int newType)
    {
        type = newType;
        if(!(type == NULL || type == WATER))
        {
            isSolid = true;
        }
        else
        {
            isSolid = false;
        }
        if(type == VILLAGE_STONE)
        {
            img = AssetManager.getImage("villageStone");
        }
        else if(type == VILLAGE_WOOD)
        {
            img = AssetManager.getImage("villageWood");
        }
        else if(type == FOREST_STONE)
        {
            img = AssetManager.getImage("forestStone");
        }
        else if(type == FOREST_WOOD)
        {
            img = AssetManager.getImage("forestWood");
        }
        else if(type == CLOCK_METAL)
        {
            img = AssetManager.getImage("clockMetal");
        }
        else if(type == CLOCK_WOOD)
        {
            img = AssetManager.getImage("clockWood");
        }
        else if(type == ICE_STONE)
        {
            img = AssetManager.getImage("iceStone");
        }
        else if(type == SNOW_BLOCK)
        {
            img = AssetManager.getImage("snowBlock");
        }
        else if(type == MOON_STONE)
        {
            img = AssetManager.getImage("moonStone");
        }
        else if(type == MOON_DUST_BLOCK)
        {
            img = AssetManager.getImage("moonDustBlock");
        }
        else if(type == WATER)
        {
            img = AssetManager.getImage("water");
        }
        else if(type == NULL)
        {
            img = null;
        }
    }
    
    /**
     * Sets the drawing location of the cell.
     * 
     * @param newX The new x-location of the cell.
     * @param newY The new y-location of the cell.
     */
    public void setDrawCoord(int newX, int newY)
    {
        drawX = newX;
        drawY = newY;
    }
    
    /**
     * Fetches the drawing location of the cell.
     * 
     * @return The drawing location of the cell.
     */
    public Point getDrawCoord()
    {
        return new Point(drawX, drawY);
    }
    
    /**
     * Fetches the cell's image.
     * 
     * @return The image of the cell.
     */
    public Image getImage()
    {
        return img;
    }
    
    /**
     * Checks if the cell is indestructible or not.
     * 
     * @return True if it is indestructible, false if not.
     */
    public boolean isIndestructible()
    {
        if(type == VILLAGE_STONE || type == FOREST_STONE || type == CLOCK_METAL || type == ICE_STONE || type == MOON_STONE)
        {
            return true;
        }
        else
        {
            return false;
        }
    }
}
