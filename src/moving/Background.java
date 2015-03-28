package moving;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;

/**
 * Manages the background, drawing it, and moving it around.
 * 
 * @author EclipseTWC
 */
public class Background
{
    public static final int STATIC = 0, REPEAT_HORIZ = 1, REPEAT_VERTICAL = 2, REPEAT_ALL = 3; //Repeat types.
    
    private int rType;
    private double x,y;
    
    private Image img;
    
    /**
     * Default constructor. Creates the background.
     * 
     * @param x             X position of the background.
     * @param y             Y position of the background.
     * @param img           The image of the background.
     * @param repeatingType Indicates how the background repeats. See the final constants above.
     */
    public Background(int x, int y, Image img, int repeatingType)
    {
        this.x = x;
        this.y = y;
        this.img = img;
        rType = repeatingType;
    }
    
    /**
     * Updates the position and repeats the background correctly.
     * 
     * @param dX    How much the background needs to move in the x-direction.
     * @param dY    How much the background needs to move in the y-direction.
     */
    public void update(double dX, double dY)
    {
        if(rType == REPEAT_HORIZ)
        {
            this.x += dX;
            if(x <= -img.getWidth(null))
                x += img.getWidth(null);
            else if(x >= img.getWidth(null))
                x -= img.getWidth(null);
        }
        else if(rType == REPEAT_VERTICAL)
        {
            this.y += dY;
            if(y <= -img.getHeight(null)+180)
                y += img.getHeight(null);
            else if(y >= img.getHeight(null))
                y -= img.getHeight(null);
        }
        else if(rType == REPEAT_ALL)
        {
            this.x += dX;
            this.y += dY;
            if(x <= -img.getWidth(null))
                x += img.getWidth(null);
            else if(x >= img.getWidth(null))
                x -= img.getWidth(null);
            if(y <= -img.getHeight(null)+180)
                y += img.getHeight(null);
            else if(y >= img.getHeight(null))
                y -= img.getHeight(null);
        }
    }
    
    /**
     * Draws the background.
     * 
     * @param g The object on which to draw the graphics.
     */
    public void drawBackground(Graphics g)
    {
        g.drawImage(img, (int)Math.round(x)-Game.cameraX, (int)Math.round(y)-Game.cameraY, null);
        if(rType == REPEAT_HORIZ)
        {
            g.drawImage(img, (int)Math.round(x-img.getWidth(null))+1-Game.cameraX, (int)Math.round(y)-Game.cameraY, null);
            g.drawImage(img, (int)Math.round(x+img.getWidth(null))-1-Game.cameraX, (int)Math.round(y)-Game.cameraY, null);
        }
        else if(rType == REPEAT_VERTICAL)
        {
            g.drawImage(img, (int)Math.round(x)-Game.cameraX, (int)Math.round(y-img.getHeight(null))+1-Game.cameraY, null);
            g.drawImage(img, (int)Math.round(x)-Game.cameraX, (int)Math.round(y+img.getHeight(null))-1-Game.cameraY, null);
        }
        else if(rType == REPEAT_ALL)
        {
            g.drawImage(img, (int)Math.round(x-img.getWidth(null))+1-Game.cameraX, (int)Math.round(y)-Game.cameraY, null);
            g.drawImage(img, (int)Math.round(x+img.getWidth(null))-1-Game.cameraX, (int)Math.round(y)-Game.cameraY, null);
            g.drawImage(img, (int)Math.round(x)-Game.cameraX, (int)Math.round(y-img.getHeight(null))+1-Game.cameraY, null);
            g.drawImage(img, (int)Math.round(x)-Game.cameraX, (int)Math.round(y+img.getHeight(null))-1-Game.cameraY, null);
            g.drawImage(img, (int)Math.round(x-img.getWidth(null))+1-Game.cameraX, (int)Math.round(y-img.getHeight(null))+1-Game.cameraY, null);
            g.drawImage(img, (int)Math.round(x+img.getWidth(null))-1-Game.cameraX, (int)Math.round(y-img.getHeight(null))+1-Game.cameraY, null);
            g.drawImage(img, (int)Math.round(x-img.getWidth(null))+1-Game.cameraX, (int)Math.round(y+img.getHeight(null))-1-Game.cameraY, null);
            g.drawImage(img, (int)Math.round(x+img.getWidth(null))-1-Game.cameraX, (int)Math.round(y+img.getHeight(null))-1-Game.cameraY, null);
        }
        g.setColor(new Color(255,255,255,170));
        g.fillRect(-Game.cameraX, -Game.cameraY, 700, 700);
    }
    
    /**
     * Fetches the Image of the background.
     * 
     * @return The Image of the background.
     */
    public Image getImage()
    {
        return img;
    }
    
    /**
     * Fetches the repeat type of the background.
     * 
     * @return The repeat type of the background.
     */
    public int getRepeatType()
    {
        return rType;
    }
}
