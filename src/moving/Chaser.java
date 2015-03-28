package moving;

import java.awt.Image;
import java.util.HashMap;

/**
 * An enemy that chases you and jumps if you are above it.
 * 
 * @author EclipseTWC
 */
public class Chaser extends Enemy
{
    private int chaseTimer = 0;
    private int jumpTimer = 0;
    
    /**
     * Default constructor.
     * 
     * @param name  Enemy's name.
     * @param x     The x-position of the enemy on the map.
     * @param y     The y-position of the enemy on the map.
     * @param drops A HashMap that represents the drops of the enemy (Item, and the chance of it dropping from the enemy once the enemy dies.)
     * @param img   The Image of the Enemy.
     * @param difficulty
     */
    public Chaser(String name, int x, int y, HashMap<Item, Double> drops, Image img, double difficulty)
    {
        super(name, x,y,drops,img, difficulty);
        health = 20;
        maxHealth = health;
        level = 5;
        xp = 20;
        currAtt = 0; attSpeed = 60;
    }
    
    /**
     * Moves the enemy.
     * 
     * @param myGame A copy of the current Game.
     */
    @Override
    public void move(Game myGame)
    {
        chaseTimer++;
        if(chaseTimer == 100)
        {
            chaseTimer = 0;
        }
        else if(chaseTimer > 60)
        {
            if(myGame.player.x > x+currImg.getWidth(null))
            {
                x += 5;
            }
            else if(myGame.player.x < x)
            {
                x -= 5;
            }
        }
        if(myGame.player.y < y)
        {
            jumpTimer++;
            if(jumpTimer >= 100)
            {
                jumpTimer = 0;
                grav = -6.5;
            }
        }
        if(myGame.player.x > x+img.getWidth(null)/2)
        {
            faceRight = true;
        }
        else
        {
            faceRight = false;
        }
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
            currAtt = 0;
            if(myGame.player.x > x+currImg.getWidth(null)/2)
            {
                int[] dmgR = {(int)(4*difficulty),(int)(4*difficulty)};
                myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, Math.toRadians(20), 8, 100, 0, 0, false, false, dmgR, false, false, null, AssetManager.getImage("shot4")));
                myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, 0, 8, 100, 0, 0, false, false, dmgR, false, false, null, AssetManager.getImage("shot4")));
                myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, Math.toRadians(-20), 8, 100, 0, 0, false, false, dmgR, false, false, null, AssetManager.getImage("shot4")));
            }
            else
            {
                int[] dmgR = {(int)(4*difficulty),(int)(4*difficulty)};
                myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, Math.toRadians(20), -8, 100, 0, 0, false, false, dmgR, false, false, null, AssetManager.getImage("shot4")));
                myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, 0, -8, 100, 0, 0, false, false, dmgR, false, false, null, AssetManager.getImage("shot4")));
                myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, Math.toRadians(-20), -8, 100, 0, 0, false, false, dmgR, false, false, null, AssetManager.getImage("shot4")));
            }
            Sounds.addAudio(AssetManager.getSoundEffect("wallHit"));
        }
    }
}
