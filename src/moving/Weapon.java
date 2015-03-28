package moving;

import java.awt.Image;

/**
 * A general "shell" for a weapon.
 * 
 * @author EclipseTWC
 */
public class Weapon extends Item
{
    public Image shotImage;
    
    public double attSpeed;
    
    public int[] damage;
    
    /**
     * Default constructor.
     * 
     * @param name              The name of the melee weapon.
     * @param description       The description of the melee weapon.
     * @param xOnMap            The x-location of the weapon on the map, if it is on the map.
     * @param yOnMap            The y-location of the weapon on the map, if it is on the map.
     * @param isOnMap           Whether the item is on the map or not.
     * @param attSpeed          The weapon's attack speed increase.
     * @param damage            The weapon's damage, represented by an int array of two values (lower bound - upper bound)
     * @param img               The Weapon's icon.
     * @param shotImage         The weapon's bullet image.
     */
    public Weapon(String name, String description, int xOnMap, int yOnMap, boolean isOnMap, double attSpeed, int[] damage, Image img, Image shotImage)
    {
        super(name, description, xOnMap, yOnMap, isOnMap, img);
        this.shotImage = shotImage;
        this.damage = damage;
        this.attSpeed = attSpeed;
    }
    
    /**
     * A method that returns all "shots" that this weapon fires. The game
     * will call this method when it needs the weapon to fire shots. The array
     * of shots that is returned will be then added to the list of "shots" in
     * the Game class.
     * 
     * @param xAdd      The x-position to add the shots to.
     * @param yAdd      The y-position to add the shots to.
     * @param angle     The angle to fire the shot at.
     * @param myGame    A copy of the current Game.
     * @return An array that contains all shots that the Game needs to add.
     */
    public Shot[] getShotsToAdd(int xAdd, int yAdd, double angle, Game myGame)
    {
        return null;
    }
}
