package moving;

import java.awt.Image;
import java.util.HashMap;

/**
 * A stronger version of the Chaser.
 * 
 * @author EclipseTWC
 */
public class HyperChaser extends Chaser
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
    public HyperChaser(String name, int x, int y, HashMap<Item, Double> drops, Image img, double difficulty)
    {
        super(name,x,y,drops,img, difficulty);
        health = 100;
        maxHealth = health;
        level = 40;
        xp = 100;
        currAtt = 0; attSpeed = 22;
    }
}
