package moving;

import java.awt.Color;
import java.awt.Image;

/**
 * A Melee Weapon. Has some special properties such as arcLength and swingSpeed,
 * unique to melee weapons.
 * 
 * @author EclipseTWC
 */
public class MeleeWeapon extends Weapon
{
    public int arcLength, swingSpeed; //in degrees
    
    public Object referencePoint;
    
    public boolean piercesEnemies = false;
    public boolean piercesWalls = false;
    
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
     * @param arcLength         The arc length of the weapon, in degrees.
     * @param swingSpeed        The swing speed of the weapon, in degrees/frame (each frame is 1/60th of a second approximately).
     * @param piercesEnemies    Whether this weapon pierces enemies (hits multiple enemies or not).
     * @param piercesWalls      Whether this weapon pierces walls (can hit multiple blocks or not).
     * @param img               The Weapon's icon.
     * @param shotImage         The weapon's bullet image.
     */
    public MeleeWeapon(String name, String description, int xOnMap, int yOnMap, boolean isOnMap, double attSpeed, int[] damage, int arcLength, int swingSpeed, boolean piercesEnemies, boolean piercesWalls, Image img, Image shotImage)
    {
        super(name, description, xOnMap, yOnMap, isOnMap, attSpeed, damage, img, shotImage);
        this.arcLength = arcLength;
        this.swingSpeed = swingSpeed;
        this.piercesEnemies = piercesEnemies;
        this.piercesWalls = piercesWalls;
        checkWidth.put(name, Color.BLACK);
        checkWidth.put(description, Color.BLUE);
        checkWidth.put(("Damage: " + damage[0] + "-" + damage[1]), Color.RED);
        if(attSpeed != 0)
        {
            checkWidth.put(("Att Spd: " + attSpeed), Color.RED);
        }
        checkWidth.put(("Arc Length: " + arcLength), Color.RED);
        checkWidth.put(("Swing Speed: " + swingSpeed), Color.RED);
        checkWidth.put(("Pierces Enemies: " + piercesEnemies), Color.RED);
        checkWidth.put(("Pierces Walls: " + piercesWalls), Color.RED);
    }
    
    /**
     * Sets the reference point for this weapon. THIS MUST BE CALLED IMMEDIATELY
     * AFTER CREATING A MELEE WEAPON!
     * 
     * @param referencePoint The Object which the weapon is bound to.
     */
    public void setReferencePoint(Object referencePoint)
    {
        this.referencePoint = referencePoint;
    }
    
    /**
     * Returns an array of shots to add to the game. This is called when the
     * melee weapon is "swung" or "fired".
     * 
     * @param xAdd      The x position of the shot.
     * @param yAdd      The y position of the shot.
     * @param angle     The angle at which the shot is fired.
     * @param myGame    A copy of the current Game.
     * @return An array of Shots to add to the Game.
     */
    @Override
    public Shot[] getShotsToAdd(int xAdd, int yAdd, double angle, Game myGame)
    {
        Shot[] shots = new Shot[1];
        shots[0] = new Shot(myGame, (int) (Math.round(xAdd)+(Math.cos(angle)*40)), (int) (Math.round(yAdd)-10+(Math.sin(angle)*40)), angle, swingSpeed, arcLength, 0, 0, piercesEnemies, piercesWalls, damage, true, true, myGame.player, shotImage);
        return shots;
    }
}
