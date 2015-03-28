package moving;

import java.awt.Color;
import java.awt.Image;
import java.util.HashMap;


/**
 * A boss enemy.
 * 
 * @author EclipseTWC
 */
public class Boss1 extends Enemy
{
    private int phase = 0;
    private int currAtt2 = 0, currAtt3 = 0;
    private int chaseTimer = 0;
    private int spd = 0;
    
    /**
     * Default constructor.
     * 
     * @param name  Enemy's name.
     * @param x     The x-position of the enemy on the map.
     * @param y     The y-position of the enemy on the map.
     * @param drops A HashMap that represents the drops of the enemy (Item, and the chance of it dropping from the enemy once the enemy dies.)
     * @param img   The Image of the Enemy.
     */
    public Boss1(String name, int x, int y, HashMap<Item, Double> drops, Image img, double difficulty)
    {
        super(name, x,y,drops,img, difficulty);
        health = 5000; maxHealth = 5000;
        level = 51;
        xp = 1000;
        currAtt = 0; attSpeed = -999; //for bosses, using a variable for attSpeed is very burdening.
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
        currAtt2++;
        currAtt3++;
        if(phase == 0)
        {
            if(currAtt >= 0 && currAtt%7 == 0)
            {
                int[] dmgR = {(int)(20*difficulty),(int)(25*difficulty)};
                double randSpd = (Math.random()*5)+3.5;
                double randAng = (Math.random()*(Math.PI/4.0))+Math.PI/6.0;
                double randGrav = (Math.random()*0.22)+0.05;
                if(myGame.player.x < x+currImg.getWidth(null)/2)
                {
                    myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, randAng, -randSpd, 3000, randGrav, 0, false, false, dmgR, false, false, null, AssetManager.getImage("biggerShot")));
                }
                else
                {
                    myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, -randAng, randSpd, 3000, randGrav, 0, false, false, dmgR, false, false, null, AssetManager.getImage("biggerShot")));
                }
            }
        }
        else if(phase == 1)
        {
            if(currAtt >= 0 && currAtt%60 == 0)
            {
                int[] dmgR = {(int)(25*difficulty),(int)(25*difficulty)};
                for(double i = 0; i < Math.PI*2.0; i+= Math.PI/6)
                {
                    myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, i, 7, 70, 0, 0, true, true, dmgR, false, false, null, AssetManager.getImage("spike")));
                }
            }
            if(currAtt2 >= 0 && currAtt2%120 == 0)
            {
                double xspd = (myGame.player.x-x-currImg.getWidth(null)/2)/100.0;
                double yspd = (myGame.player.y-y-currImg.getHeight(null)/2)/100.0;
                myGame.enemies.add(new Boss1Mine("boss1mine", (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, new HashMap(), AssetManager.getImage("boss1mine"), xspd, yspd, difficulty));
            }
        }
        else if(phase == 2)
        {
            if(currAtt >= 0 && currAtt%60==0)
            {
                int[] dmgR = {(int)(20*difficulty),(int)(20*difficulty)};
                if(myGame.player.x < x+currImg.getWidth(null))
                {
                    Shot s = new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, 0, 6, 72, 0, 0, true, true, dmgR, false, true, this, AssetManager.getImage("legendssword"));
                    /*s.xspeed = -s.xspeed;
                    s.yspeed = -s.yspeed;*/
                    myGame.shots.add(s);
                }
                else
                {
                    myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, 0, 6, 72, 0, 0, true, true, dmgR, false, true, this, AssetManager.getImage("legendssword")));
                }
            }
            if(currAtt2 >=0 && currAtt2%50==0)
            {
                int[] dmgR = {(int)(10*difficulty),(int)(10*difficulty)};
                for(double i = Math.PI/4.0; i <= 3*Math.PI/4.0; i+= Math.PI/16.0)
                {
                    myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, i, -3.5, 4000, 0.035, 0, false, true, dmgR, false, false, null, AssetManager.getImage("hugeshot")));
                    myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, i, -4.8, 4000, 0.035, 0, false, true, dmgR, false, false, null, AssetManager.getImage("hugeshot")));
                    myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, i, -6.1, 4000, 0.035, 0, false, true, dmgR, false, false, null, AssetManager.getImage("hugeshot")));
                }
            }
        }
        else if(phase == 3)
        {
            if(currAtt >= 0 && currAtt%150==0)
            {
                myGame.enemies.add(new Chaser("chaser", (int)x+currImg.getWidth(null)/2-AssetManager.getImage("chaser").getWidth(null)/2, (int)y, new HashMap(), AssetManager.getImage("chaser"), difficulty));
            }
            if(currAtt2 >= 0 && currAtt2%7 == 0)
            {
                int[] dmgR = {(int)(20*difficulty),(int)(25*difficulty)};
                double randSpd = (Math.random()*5)+3.5;
                double randAng = (Math.random()*(Math.PI/4.0))+Math.PI/6.0;
                double randGrav = (Math.random()*0.22)+0.05;
                if(myGame.player.x < x+currImg.getWidth(null)/2)
                {
                    myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, randAng, -randSpd, 3000, randGrav, 0, false, false, dmgR, false, false, null, AssetManager.getImage("biggerShot")));
                }
                else
                {
                    myGame.shots.add(new Shot(myGame, (int)x+currImg.getWidth(null)/2, (int)y+currImg.getHeight(null)/2, -randAng, randSpd, 3000, randGrav, 0, false, false, dmgR, false, false, null, AssetManager.getImage("biggerShot")));
                }
            }
        }
        if(health <= 4000 && phase == 0)
        {
            phase = 1;
            currAtt = -200;
            currAtt2 = -200;
            currAtt3 = -200;
            myGame.message = new UnlockedMessage("Do not interfere with my plans!", false, currImg);
            myGame.message.update = true;
        }
        else if(health <= 2500 && phase == 1)
        {
            phase = 2;
            currAtt = -200;
            currAtt2 = -200;
            currAtt3 = -200;
            myGame.message = new UnlockedMessage("I'M WARNING YOU!!", false, currImg);
            myGame.message.update = true;
        }
        else if(health <= 700 && phase == 2)
        {
            phase = 3;
            currAtt = -200;
            currAtt2 = -200;
            currAtt3 = -200;
            myGame.message = new UnlockedMessage("GRRAHHHHH! Heal!", false, currImg);
            myGame.message.update = true;
            health += 1000;
            damages.clear();
            damages.add(new DamageText(g, this, "+1000", new Color(120,255,120)));
        }
        else if(health <= 0)
        {
            Music.stopAudio();
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
        if(phase == 1)
        {
            if(myGame.player.x < x+currImg.getWidth(null)/2)
            {
                x-=0.7;
            }
            else
            {
                x+=0.7;
            }
        }
        else if(phase == 2)
        {
            chaseTimer++;
            if(chaseTimer >= 185)
            {
                chaseTimer = 0;
                spd = 0;
            }
            else if(chaseTimer == 120)
            {
                if(myGame.player.x > x+currImg.getWidth(null))
                {
                    spd = 8;
                }
                else if(myGame.player.x < x)
                {
                    spd = -8;
                }
            }
            x+=spd;
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
