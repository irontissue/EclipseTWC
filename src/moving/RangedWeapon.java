package moving;

import java.awt.Color;
import java.awt.Image;

/**
 * A Ranged Weapon. Has some special properties such as gravity and range,
 * unique to ranged weapons.
 * 
 * @author EclipseTWC
 */
public class RangedWeapon extends Weapon
{
    public double speed, range, gravity, shotAngleSep;
    
    public int spreadAngle;
    public int numShots;
    
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
     * @param spreadAngle       The error in angle in the weapon, in degrees. Each weapon has a certain "error" when shooting, which is called the "spread" angle.
     * @param numShots          The number of shots fired by this weapon.
     * @param shotAngleSep      The separation angle between each shot, if multiple shots are fired.
     * @param speed             The shot's speed in pixels/frame (each frame is 1/60th of a second).
     * @param range             The shot's range in pixels.
     * @param gravity           The acceleration downwards of the shot, in pixels/frame (each frame is 1/60th of a second).
     * @param piercesEnemies    Whether this weapon pierces enemies (hits multiple enemies or not).
     * @param piercesWalls      Whether this weapon pierces walls (can hit multiple blocks or not).
     * @param img               The Weapon's icon.
     * @param shotImage         The weapon's bullet image.
     */
    public RangedWeapon(String name, String description, int xOnMap, int yOnMap, boolean isOnMap, double attSpeed, int[] damage, int spreadAngle, int numShots, int shotAngleSep, Image img, Image shotImage, double speed, double range, double gravity, boolean piercesEnemies, boolean piercesWalls)
    {
        super(name, description, xOnMap, yOnMap, isOnMap, attSpeed, damage, img, shotImage);
        this.speed = speed;
        this.range = range;
        this.gravity = gravity;
        this.piercesEnemies = piercesEnemies;
        this.piercesWalls = piercesWalls;
        this.spreadAngle = spreadAngle;
        this.numShots = numShots;
        this.shotAngleSep = Math.toRadians(shotAngleSep);
        checkWidth.put(name, Color.BLACK);
        checkWidth.put(description, Color.BLUE);
        checkWidth.put(("Damage: " + damage[0] + "-" + damage[1]), Color.RED);
        if(attSpeed != 0)
        {
            checkWidth.put(("Att Spd: " + attSpeed), Color.RED);
        }
        checkWidth.put(("Shot Speed: " + speed), Color.RED);
        checkWidth.put(("Range: " + range), Color.RED);
        if(gravity != 0)
        {
            checkWidth.put(("Gravity: " + gravity), Color.RED);
        }
        checkWidth.put(("Spread : " + spreadAngle), Color.RED);
        if(numShots > 1)
        {
            checkWidth.put(("Shoots " + numShots + " shots"), Color.RED);
            checkWidth.put(((int)Math.round(Math.toDegrees(shotAngleSep)) + " spread between each shot."), Color.RED);
        }
        checkWidth.put(("Pierces Enemies: " + piercesEnemies), Color.RED);
        checkWidth.put(("Pierces Walls: " + piercesWalls), Color.RED);
    }
    
    /**
     * Returns an array of shots to add to the game. This is called when the
     * ranged weapon is "fired".
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
        Shot[] shots = new Shot[numShots];
        if(numShots > 1)
        {
            for(int i = 0; i < shots.length; i++)
            {
                double change = (i*shotAngleSep)-(numShots-1)/2.0*shotAngleSep;
                shots[i] = new Shot(myGame, xAdd, yAdd, angle+change, speed, range, gravity, spreadAngle, piercesEnemies, piercesWalls, damage, true, false, null, shotImage);
            }
        }
        else
        {
            shots[0] = new Shot(myGame, xAdd, yAdd, angle, speed, range, gravity, spreadAngle, piercesEnemies, piercesWalls, damage, true, false, null, shotImage);
        }
        return shots;
    }
}
