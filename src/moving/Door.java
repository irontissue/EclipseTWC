package moving;

import java.awt.Image;

/**
 * Creates a door.
 * 
 * @author EclipseTWC
 */
public class Door
{
    public String mapLink;
    
    public int x, y;
    
    public Image img;
    
    /**
     * Default constructor.
     * 
     * @param x         The x-position of the door.
     * @param y         The y-position of the door.
     * @param mapLink   The map to which this door will take the player to.
     * @param img       The door's Image.
     */
    public Door(int x, int y, String mapLink, Image img)
    {
        this.x = x;
        this.y = y;
        this.mapLink = mapLink;
        this.img = img;
    }
}
