package moving;

import java.awt.Image;

/**
 * A treasure chest. It contains items that will be released to the map once
 * the chest is opened.
 * 
 * @author EclipseTWC
 */
public class TreasureChest
{
    public double xOnMap, yOnMap;
    
    public double grav;
    
    public boolean opened = false;
    
    public Item[] items;
    
    public Image img;
    
    /**
     * Default constructor.
     * 
     * @param xOnMap    The x-position of the treasure chest.
     * @param yOnMap    The y-position of the treasure chest.
     * @param img       The treasure chest's image.
     * @param items     An array of items that the chest can drop.
     */
    public TreasureChest(int xOnMap, int yOnMap, Image img, Item[] items)
    {
        this.xOnMap = xOnMap;
        this.yOnMap = yOnMap;
        this.img = img;
        this.items = items;
    }
    
    /**
     * Updates the treasure chest's position on the map and checks for collision.
     * 
     * @param myGame A copy of the current Game.
     */
    public void updatePosition(Game myGame)
    {
        boolean isOnSolidGround = false;
        int xxMax = img.getWidth(null);
        if(xxMax <= Grid.CELL_SIZE)
        {
            xxMax = Grid.CELL_SIZE+1;
        }
        for(int xx = (int)Math.round(xOnMap)/Grid.CELL_SIZE; xx < ((int)(Math.round(xOnMap)+xxMax)/Grid.CELL_SIZE); xx++)
        {
            if(myGame.grid.cells[xx][((int)Math.round(yOnMap)+img.getHeight(null))/Grid.CELL_SIZE].isSolid)
            {
                isOnSolidGround = true;
                xx = ((int)Math.round(xOnMap)+img.getWidth(null)/Grid.CELL_SIZE);
            }
        }
        if(!isOnSolidGround)
        {
            grav+=0.2;
        }
        else
        {
            grav = 0;
            yOnMap = myGame.grid.cells[0][((int)Math.round(yOnMap+img.getHeight(null)))/Grid.CELL_SIZE].getDrawCoord().y - img.getHeight(null);
        }
        if(grav > 8)
        {
            grav = 8;
        }
        yOnMap+=grav;
    }
    
    /**
     * Makes the treasure chest opened.
     */
    public void makeOpened()
    {
        items = null;
        opened = true;
        img = AssetManager.getImage("villageChestOpened");
    }
    
    /**
     * Fetches a String that represents the treasure chest, to be exported to
     * a map file.
     * 
     * @return A String that represents this treasure chest's data.
     */
    public String exportTC()
    {
        String s = (int)xOnMap + "," + (int)yOnMap + "," + AssetManager.getImageSource(img);
        if(items != null)
        for(Item i : items)
        {
            if(i != null && i.name != null)
                s += "," + i.name;
            else
                s += ",null";
        }
        return s;
    }
}
