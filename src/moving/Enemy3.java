package moving;

import java.awt.Image;
import java.util.HashMap;

/**
 * An Example enemy. This enemy is never used in the game.
 * 
 * @author EclipseTWC
 */
public class Enemy3 extends Enemy
{
    private int moveRange = 0, moveSpeed = 1;
    private int currAtt2 = 0, attSpeed2 = 60;
    
    /**
     * Default constructor.
     * 
     * @param name  Enemy's name.
     * @param x     The x-position of the enemy on the map.
     * @param y     The y-position of the enemy on the map.
     * @param drops A HashMap that represents the drops of the enemy (Item, and the chance of it dropping from the enemy once the enemy dies.)
     * @param img   The Image of the Enemy.
     */
    public Enemy3(String name, int x, int y, HashMap<Item, Double> drops, Image img, double difficulty)
    {
        super(name,x,y,drops,img,difficulty);
        health = 500;
        maxHealth = health;
        level = 10;
        xp = 100;
        currAtt = 0; attSpeed = 60; //currAtt is a timer that increases by 1 every frame, and once it reaches attSpeed, the shot(s) will be fired.
    }
    
    /**
     * Makes the enemy shoot/attack.
     * 
     * @param myGame A copy of the current Game.
     */
    @Override
    public void shotsAdd(Game myGame) //Adding the shot itself is the complicated part. If you want to add more shots just make a for loop.
    {
        //Refer to Shot.java to see what the parameters for the shots are. The shot is very customizable due to the large number of
        //paramters, but it can get complicated.
        //If you want to add different sets of bullets that fire at a different rate, go up and make another private int called currAtt2
        //and attSpeed2, then set attSpeed2 to whatever you want (remember, 60 means it will fire every 1 second), and in this method,
        //add an if() statement. See the example below.
        currAtt++; //This ticks the timer
        currAtt2++;//This too
        if(currAtt > attSpeed)
        {
            int[] dmgRange = {(int)(0*difficulty),(int)(0*difficulty)}; //The damage range of the bullet. Try to make enemy bullets have constant damage, {a,a}.
            for(int ang = 0; ang < Math.PI*2; ang+= Math.PI/2) //This for loop will add a shot every PI/2 radians, so a total of 4 shots.
            {
                myGame.shots.add(new Shot(myGame, (int)x, (int)y, 0, 0, 0, 0, 0, false, false, dmgRange, false, false, null, null));
            }
            currAtt = 0; //Don't forget this line
        }
        if(currAtt2 > attSpeed2)
        {
            //Add a shot here
            currAtt2 = 0;
        }
    }
    
    /**
     * Moves the enemy.
     * 
     * @param myGame A copy of the current Game.
     */
    @Override
    public void move(Game myGame) //This particular code will move the enemy back and forth 60 pixels.
    {
        moveRange+=moveSpeed;
        x+=moveSpeed;
        if(moveRange > 60)
        {
            moveRange = 60;
            moveSpeed = -moveSpeed;
        }
        else if(moveRange < 0)
        {
            moveRange = 0;
            moveSpeed = -moveSpeed;
        }
    }
}
