package moving;

import java.awt.Image;
import java.util.HashMap;

/**
 * A flying enemy. Although we named it UFO, it appears as an evil bird in-game.
 * 
 * @author EclipseTWC
 */
public class UFO extends Enemy
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
    public UFO(String name, int x, int y, HashMap<Item, Double> drops, Image img, double difficulty)
    {
        super(name,x,y,drops,img,difficulty);
        health = 15;
        maxHealth = health;
        xp = 15;
        currAtt = 0; attSpeed = 120;
        gravConstant = 0;
    }
    
    /**
     * Moves the enemy.
     * 
     * @param myGame A copy of the current Game.
     */
    @Override
    public void move(Game myGame)
    {
        if(myGame.player.y-y < 170)
        {
            y-=2.5;
        }
        if(myGame.player.y-y > 200)
        {
            y+=2;
        }
        if(myGame.player.x-x-currImg.getWidth(null)/2 > 5)
        {
            x+=1;
        }
        else if(myGame.player.x-x-currImg.getWidth(null)/2 < -5)
        {
            x-=1;
        }
        else
        {
            x+=(int)(Math.random()*7)-3;
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
        if(currAtt>attSpeed)
        {
            currAtt = 0;
            int[] dmgR = {(int)(20*difficulty),(int)(30*difficulty)};
            myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)+3, 0, 0, 1000, 0.23, 0, false, true, dmgR, false, false, null, AssetManager.getImage("ufoshot")));
            Sounds.addAudio(AssetManager.getSoundEffect("special"));
        }
    }
}
