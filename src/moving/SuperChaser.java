package moving;

import java.awt.Image;
import java.util.HashMap;

/**
 * A stronger version of the Chaser. Has the same movement behavior as the chaser.
 * 
 * @author EclipseTWC
 */
public class SuperChaser extends Chaser
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
    public SuperChaser(String name, int x, int y, HashMap<Item, Double> drops, Image img, double difficulty)
    {
        super(name,x,y,drops,img, difficulty);
        health = 50;
        maxHealth = health;
        level = 15;
        xp = 50;
        currAtt = 0; attSpeed = 40;
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
                int[] dmgR = {(int)(7*difficulty),(int)(7*difficulty)};
                myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, Math.toRadians(20), 10, 150, 0, 0, false, false, dmgR, false, false, null, AssetManager.getImage("shot4")));
                myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, 0, 10, 150, 0, 0, false, false, dmgR, false, false, null, AssetManager.getImage("shot4")));
                myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, Math.toRadians(-20), 10, 150, 0, 0, false, false, dmgR, false, false, null, AssetManager.getImage("shot4")));
            }
            else
            {
                int[] dmgR = {(int)(7*difficulty),(int)(7*difficulty)};
                myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, Math.toRadians(20), -10, 150, 0, 0, false, false, dmgR, false, false, null, AssetManager.getImage("shot4")));
                myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, 0, -10, 150, 0, 0, false, false, dmgR, false, false, null, AssetManager.getImage("shot4")));
                myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, Math.toRadians(-20), -10, 150, 0, 0, false, false, dmgR, false, false, null, AssetManager.getImage("shot4")));
            }
            Sounds.addAudio(AssetManager.getSoundEffect("wallHit"));
        }
    }
}
