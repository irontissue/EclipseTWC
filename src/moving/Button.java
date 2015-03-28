package moving;


import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Point;

/**
 * A clickable button. We created our own button class so that we could customize
 * how it looked.
 * 
 * @author EclipseTWC
 */
public class Button
{
    public boolean pressed = false;
    
    private String btnText;
    private String ID;
    
    private int xPos, yPos, buttonWidth, buttonHeight;
    private int baseX, baseY;
    
    private Color c;
    
    /**
     * Default constructor.
     * 
     * @param xPos          The x-position of the button.
     * @param yPos          The x-position of the button.
     * @param buttonWidth   The button's width
     * @param buttonHeight  The button's height
     * @param btnText       The text that will display on the button.
     * @param c             The Color of the button
     * @param ID            The ID of the button, which usually is the same as the name. This will allow you to distinguish between two buttons that have the same name, but do different things (such as the level up points buttons).
     */
    public Button(int xPos, int yPos, int buttonWidth, int buttonHeight, String btnText, Color c, String ID)
    {
        this.c = c;
        this.xPos = xPos;
        this.yPos = yPos;
        this.ID = ID;
        baseX = xPos;
        baseY = yPos;
        this.buttonWidth = buttonWidth;
        this.buttonHeight = buttonHeight;
        this.btnText = btnText;
    }
    
    /**
     * Updates the button's position.
     */
    public void updateButton()
    {
        xPos = -Game.cameraX+baseX;
        yPos = -Game.cameraY+baseY;
    }
    
    /**
     * Draws the button.
     * 
     * @param gg The object on which to draw the graphics.
     */
    public void drawButton(Graphics gg)
    {
        Color dark = c.darker();
        int xSideBar = 3;
        int ySideBar = 3;
        if(pressed)
        {
            gg.setColor(Color.BLACK);
        }
        else
        {
            gg.setColor(c);
        }
        gg.fillRect(xPos, yPos, buttonWidth, ySideBar);
        gg.fillRect(xPos, yPos, xSideBar, buttonHeight);
        gg.setColor(dark);
        gg.fillRect(xPos+xSideBar, yPos+ySideBar, buttonWidth - xSideBar*2, buttonHeight - ySideBar*2);
        if (pressed)
        {
            gg.setColor(c);
        }
        else
        {
            gg.setColor(Color.BLACK);
        }
        gg.fillRect(xPos+xSideBar, yPos+buttonHeight-ySideBar, buttonWidth - xSideBar, ySideBar);
        gg.fillRect(xPos+buttonWidth-xSideBar, yPos+ySideBar, xSideBar, buttonHeight - ySideBar);
        gg.drawLine(xPos+buttonWidth-1, yPos, xPos+buttonWidth-1, yPos+4);
        gg.drawLine(xPos+buttonWidth-2, yPos+1, xPos+buttonWidth-2, yPos+4);
        gg.drawLine(xPos+buttonWidth-3, yPos+2, xPos+buttonWidth-3, yPos+4);
        gg.drawLine(xPos, yPos+buttonHeight-1, xPos+4, yPos+buttonHeight-1);
        gg.drawLine(xPos+1, yPos+buttonHeight-2, xPos+4, yPos+buttonHeight-2);
        gg.drawLine(xPos+2, yPos+buttonHeight-3, xPos+4, yPos+buttonHeight-3);
        gg.setColor(Color.BLACK);
        gg.setFont(new Font(Game.DEFAULT_FONT, Font.PLAIN, 12));
        FontMetrics fm = gg.getFontMetrics();
        int txWidth = fm.stringWidth(btnText);
        gg.drawString(btnText, (xPos+(buttonWidth-txWidth)/2), yPos+(buttonHeight/2)+4);
    }
    
    /**
     * Checks to see if the given location hits this button.
     * 
     * @param x The x value of the test location.
     * @param y The y value of the test location.
     * @return  Whether the test location hits this button.
     */
    public boolean hits(int x, int y)
    {
        if(x > this.xPos && x < this.xPos+buttonWidth && y > this.yPos && y < this.yPos+buttonHeight)return true;
        else return false;
    }
    
    /**
     * Fetches the location of this button.
     * 
     * @return A point representing the position of the button.
     */
    public Point getCoord()
    {
        return new Point(xPos,yPos);
    }
    
    /**
     * Sets the button's location to the new given values.
     * 
     * @param newX The new x coordinate
     * @param newY The new y coordinate
     */
    public void setCoord(int newX, int newY)
    {
        this.xPos = newX;
        this.yPos = newY;
    }
    
    /**
     * Fetches the ID of the button.
     * 
     * @return The ID of the button.
     */
    public String getID()
    {
        return ID;
    }
}
