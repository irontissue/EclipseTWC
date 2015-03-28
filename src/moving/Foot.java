package moving;

import java.awt.Image;

/**
 * A Foot armor. We don't have any armor made right now due to time restraints.
 * 
 * @author EclipseTWC
 */
public class Foot extends Item
{
    public Foot(String name, String description, int xOnMap, int yOnMap, boolean isOnMap, Image img)
    {
        super(name, description, xOnMap, yOnMap, isOnMap, img);
    }
}
