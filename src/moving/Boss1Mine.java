package moving;

import java.awt.Image;
import java.util.HashMap;

/**
 * The bomb/mine that the Boss1 releases in his second battle phase. This mine
 * self destructs when it reacher 0 health, and it constantly loses health
 * (kind of like a timer). The player's attacks will barely scratch it, thus not
 * influencing the health timer.
 * 
 * @author EclipseTWC
 */
public class Boss1Mine extends Enemy
{    
    private double xSpeed, ySpeed;
    
    private boolean destructed = false;
    
    /**
     * Default constructor.
     * 
     * @param name  Enemy's name.
     * @param x     The x-position of the enemy on the map.
     * @param y     The y-position of the enemy on the map.
     * @param drops A HashMap that represents the drops of the enemy (Item, and the chance of it dropping from the enemy once the enemy dies.)
     * @param img   The Image of the Enemy.
     * @param xSpeed The x-direction movement speed of the enemy.
     * @param ySpeed the y-direction movement speed of the enemy.
     */
    public Boss1Mine(String name, int x, int y, HashMap<Item, Double> drops, Image img, double xSpeed, double ySpeed, double difficulty)
    {
        super(name, x, y, drops, img, difficulty);
        health = 60000000; maxHealth = 60000000;
        level = 1; xp = 0;
        currAtt = 0; attSpeed = 20;
        gravConstant = 0;
        this.xSpeed = xSpeed;
        this.ySpeed = ySpeed;
    }
    
    /**
     * Makes the enemy shoot/attack.
     * 
     * @param myGame A copy of the current Game.
     */
    @Override
    public void shotsAdd(Game myGame)
    {
        health -= 305000;
        if(health <= 0 && !destructed)
        {
            destructed = true;
            int[] dmgR = {(int)(12*difficulty),(int)(12*difficulty)};
            for(double i = 0; i < Math.PI*2; i += Math.PI/4)
            {
                myGame.shots.add(new Shot(myGame, (int)x+img.getWidth(null)/2, (int)y+img.getHeight(null)/2, i, 0.5, 75, 0, 0, true, true, dmgR, false, false, null, AssetManager.getImage("spike")));
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
        x+=xSpeed;
        y+=ySpeed;
        for(int yy = (int)Math.round(y)/Grid.CELL_SIZE; yy < ((int)(Math.round(y)+currImg.getHeight(null))/Grid.CELL_SIZE); yy++)
        {
            Cell cRight = myGame.grid.cells[((int)Math.round(x)+currImg.getWidth(null))/Grid.CELL_SIZE][yy];
            Cell cLeft = myGame.grid.cells[(int)Math.round(x)/Grid.CELL_SIZE][yy];
            if(cRight.isSolid && cRight.isReal == 0)
            {
                yy = ((int)Math.round(y)+currImg.getHeight(null)/Grid.CELL_SIZE);
                xSpeed = 0;
            }
            if(cLeft.isSolid && cLeft.isReal == 0)
            {
                yy = ((int)Math.round(y)+currImg.getHeight(null)/Grid.CELL_SIZE);
                ySpeed = 0;
            }
        }
    }
}
