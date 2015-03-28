package moving;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

/**
 * A single cell on the map. Similar to the Cell class, but modified for use
 * specifically in the level creator.
 * 
 * @author EclipseTWC
 */
public class ELCCell
{
    public static final int CELL_SIZE = 15;
    public static final int REAL = 0, FAKE = 1;
    
    private int x, y;
    private int isReal;
    
    private String type;
    
    private Image img;
    
    public static String[] types = {"null", "water",
        "villageStone", "villageWood",
        "forestStone", "forestDirt",
        "clockBrick", "clockWood",
        "iceRock", "snowBlock"};
    
    /**
     * Default constructor.
     * 
     * @param x         The x-position of the cell.
     * @param y         The y-position of the cell.
     * @param type      A String that represents the type of the cell.
     * @param isReal    An int that represents whether the cell is real or fake. See final constants above.
     */
    public ELCCell(int x, int y, String type, int isReal)
    {
        this.x = x;
        this.y = y;
        this.type = type;
        this.isReal = isReal;
        img = AssetManager.getImage(type);
    }
    
    /**
     * Draws the cell.
     * 
     * @param g The object on which the graphics is drawn.
     */
    public void drawCell(Graphics g)
    {
        if(!type.equals("null") && !type.equals("water") && y/CELL_SIZE-1 > -1 && (ELCWorker.grid[x/CELL_SIZE][y/CELL_SIZE-1].type.equals("null") || ELCWorker.grid[x/CELL_SIZE][y/CELL_SIZE-1].type.equals("water")))
        {
            String s = type+"Top";
            img = AssetManager.getImage(s);
        }
        else
        {
            img = AssetManager.getImage(type);
        }
        g.drawImage(img, x, y, CELL_SIZE, CELL_SIZE, null);
        if(isReal == FAKE)
        {
            g.setColor(new Color(200,200,200,150));
            g.fillRect(x, y, CELL_SIZE, CELL_SIZE);
        }
    }
    
    /**
     * Sets the cell's type.
     * 
     * @param newType The new cell type.
     */
    public void setType(String newType)
    {
        this.type = newType;
    }
    
    /**
     * Fetches the cell's type.
     * 
     * @return The cell's type.
     */
    public String getType()
    {
        return type;
    }
    
    /**
     * Fetches the cell's x-coordinate.
     * 
     * @return the cell's x-coordinate.
     */
    public int getX()
    {
        return x;
    }
    
    /**
     * Fetches the cell's y-coordinate.
     * 
     * @return the cell's y-coordinate.
     */
    public int getY()
    {
        return y;
    }
    
    /**
     * Fetches whether the cell is real or not.
     * 
     * @return Whether the cell is real or not.
     */
    public int isReal()
    {
        return isReal;
    }
    
    /**
     * Sets the cell to real or not.
     * 
     * @param isReal The new "real" value.
     */
    public void setReal(int isReal)
    {
        this.isReal = isReal;
    }
}
