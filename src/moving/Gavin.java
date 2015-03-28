package moving;

import java.awt.Image;
import java.util.HashMap;
import java.util.logging.Level;
import java.util.logging.Logger;

/**
 * We did not want to rename this enemy as an honor to one of our team members.
 * It IS an actual enemy in game, although it appears as a ferocious Komodo Dragon.
 * 
 * @author EclipseTWC
 */
public class Gavin extends Enemy
{    
    private int attSpeedStop = 70;
    
    /**
     * Default constructor.
     * 
     * @param name  Enemy's name.
     * @param x     The x-position of the enemy on the map.
     * @param y     The y-position of the enemy on the map.
     * @param drops A HashMap that represents the drops of the enemy (Item, and the chance of it dropping from the enemy once the enemy dies.)
     * @param img   The Image of the Enemy.
     */
    public Gavin(String name, int x, int y, HashMap<Item, Double> drops, Image img, double difficulty)
    {
        super(name, x, y, drops, img, difficulty);
        health = 120;
        maxHealth = health;
        level = 20;
        xp = 80;
        currAtt = 0; attSpeed = 50;
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
        if(currAtt > attSpeedStop)
        {
            currAtt = 0;
        }
        else if(currAtt > attSpeed && currAtt%4 == 0)
        {
            double rand = Math.random();
            double angle = Math.atan((double)(myGame.player.y-y-currImg.getHeight(null)/2)/(myGame.player.x-x-currImg.getWidth(null)/2));
            double spd = 3;
            int[] dmgRange = {(int)(8*difficulty),(int)(8*difficulty)};
            String imgS = "shot";
            if(rand > 0.9)
            {
                dmgRange[0] = (int)(17*difficulty); dmgRange[1] = (int)(17*difficulty);
                spd = 2;
                imgS = "shot3";
            }
            try
            {
                if(myGame.player.x <= x+currImg.getWidth(null)/2)
                {
                    myGame.shots.add(new Shot(myGame, (int)Math.round(x+currImg.getWidth(null)/2), (int)Math.round(y+currImg.getHeight(null)/2), angle, -spd, 250, 0, 0, false, false, dmgRange, false, false, this, AssetManager.getImage(imgS)));
                }
                else
                {
                    myGame.shots.add(new Shot(myGame, (int)Math.round(x+currImg.getWidth(null)/2), (int)Math.round(y+currImg.getHeight(null)/2), angle, spd, 250, 0, 0, false, false, dmgRange, false, false, this, AssetManager.getImage(imgS)));
                }
            }
            catch (Exception ex)
            {
                Logger.getLogger(Enemy.class.getName()).log(Level.SEVERE, null, ex);
            }
        }
    }
    
    /**
     * Moves the enemy.
     * 
     * @param myGame A copy of the current Game.
     */
    @Override
    public void move(Game myGame)
    {
        if(Math.abs(myGame.player.x-x-currImg.getWidth(null)/2) < 270)
        {
            x -= 2*Math.abs(myGame.player.x-x-currImg.getWidth(null)/2)/(myGame.player.x-x-currImg.getWidth(null)/2);
        }
        else
        {
            x += (int)(Math.random()*9)-4;
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
}
