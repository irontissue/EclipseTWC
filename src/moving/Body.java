package moving;

import java.awt.Image;

/**
 * A Body armor. We don't have any armor made right now due to time restraints.
 * 
 * @author EclipseTWC
 */
public class Body extends Item
{
    public Body(String name, String description, int xOnMap, int yOnMap, boolean isOnMap, Image img)
    {
        super(name, description, xOnMap, yOnMap, isOnMap, img);
    }
}
