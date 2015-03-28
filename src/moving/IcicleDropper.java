package moving;

import java.awt.Image;
import java.util.HashMap;

/**
 * A stationary enemy with high health and gives a LOT of experience. Most people
 * give up trying to kill it due to the sheer amount of time it takes.
 * This enemy drops deadly sludge bombs at slightly randomized intervals. Just
 * avoid it and keep going.
 * 
 * @author EclipseTWC
 */
public class IcicleDropper extends Enemy
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
    public IcicleDropper(String name, int x, int y, HashMap<Item, Double> drops, Image img, double difficulty)
    {
        super(name,x,y,drops,img,difficulty);
        health = 100000;
        maxHealth = health;
        xp = 1000;
        currAtt = 0; attSpeed = 100;
        gravConstant = 0;
    }
    
    /**
     * Makes the enemy shoot/attack.
     * 
     * @param myGame A copy of the current Game.
     */
    @Override
    public void shotsAdd(Game myGame)
    {
        currAtt++;
        if(currAtt > attSpeed)
        {
            currAtt = (int)(Math.random()*21);
            Image shotImage = AssetManager.getImage("icicle");
            int[] dmgR = {50,50};
            myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2+shotImage.getHeight(null)+2, 0, 0, 5000, 0.23, 0, false, false, dmgR, false, false, null, shotImage));
        }
    }
    
    /**
     * Moves the enemy. This particular enemy does NOT move.
     * 
     * @param myGame A copy of the current Game.
     */
    @Override
    public void move(Game myGame)
    {
        
    }
}
