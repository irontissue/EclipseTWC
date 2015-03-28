package moving;

import java.awt.Image;
import java.util.HashMap;

/**
 * An Enemy made just for testing purposes. Gives an incredible amount of
 * experience points, but takes a while to kill.
 * 
 * @author EclipseTWC
 */
public class TestEnemy extends Enemy
{
    /**
     * Default constructor.
     * 
     * @param name  Enemy's name.
     * @param x     The x-position of the enemy on the map.
     * @param y     The y-position of the enemy on the map.
     * @param drops A HashMap that represents the drops of the enemy (Item, and the chance of it dropping from the enemy once the enemy dies.)
     * @param img   The Image of the Enemy.
     */
    public TestEnemy(String name, int x, int y, HashMap<Item, Double> drops, Image img, double difficulty)
    {
        super(name,x,y,drops,img,difficulty);
        health = 50000;
        maxHealth = health;
        xp = 10000;
    }
    
    /**
     * Makes the enemy shoot/attack. The TestEnemy does not attack.
     * 
     * @param myGame A copy of the current Game.
     */
    @Override
    public void shotsAdd(Game myGame)
    {
        
    }
    
    /**
     * Moves the enemy. The TestEnemy does not move.
     * 
     * @param myGame A copy of the current Game.
     */
    @Override
    public void move(Game myGame)
    {
        
    }
}
