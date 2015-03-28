package moving;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

/**
 * Displays and updates the in-game messages.
 * 
 * @author EclipseTWC
 */
public class Message
{
    public String text;
    public String count = "";
    public String[] indivWords;
    
    private Image img;
    
    public ArrayList<String> linesToShow = new ArrayList();
    public ArrayList<String> lines = new ArrayList();
    
    public boolean textOnRight;
    public boolean remove = false;
    public boolean update = false;
    public boolean waitingForConfirm = false;
    
    public int backgroundAlpha;
    
    public int i = 0;
    public int addCharTimer = 0;
    
    public static final int MAX_CHAR_PER_LINE = 20;
    
    /**
     * Default constructor.
     * 
     * @param text          The text that the message will display.
     * @param textOnRight   Whether the text is on the right or the left. (If text is on right, the picture will be on the left, and vice-versa)
     * @param img           The picture that will be displayed as the person or thing that is saying the message.
     */
    public Message(String text, boolean textOnRight, Image img)
    {
        this.text = text;
        this.img = img;
        backgroundAlpha = 255;
        this.indivWords = text.split(" ");
        for(int i = 0; i < indivWords.length; i++)
        {
            if(indivWords[i].contains("playername"))
            {
                if(Game.player != null && Game.player.name != null)
                {
                    indivWords[i] = indivWords[i].replaceFirst("playername", Game.player.name);
                }
            }
        }
        this.textOnRight = textOnRight;
    }
    
    /**
     * Updates the message and what exactly it should display. Gets the graphics
     * from the Object that is passed in (it will either be a MenuScreen or
     * a Game).
     * 
     * @param ref The Object to receive Graphics data from.
     */
    public void updateMessage(Object ref)
    {
        boolean stillAddingChars = false;
        int k;
        if(addCharTimer > 0)
        {
            if(!waitingForConfirm)
            {
                for(k = 0; k < linesToShow.size(); k++)
                {
                    if(!linesToShow.get(k).equals(lines.get(k)))
                    {
                        linesToShow.set(k, lines.get(k).substring(0, linesToShow.get(k).length()+1));
                        stillAddingChars = true;
                    }
                    else
                    {
                        if(k == 2)
                        {
                            update = false;
                            waitingForConfirm = true;
                        }
                    }
                }
                if(i == indivWords.length && !stillAddingChars)
                {
                    waitingForConfirm = true;
                    update = false;
                    removeBasedOnType();
                }
            }
            else
            {
                
            }
            if(update && !stillAddingChars)
            {
                if(linesToShow.size() == 3)
                {
                    /*lines.set(0, lines.get(1));
                    lines.set(1, lines.get(2));
                    lines.remove(2);
                    linesToShow.set(0, linesToShow.get(1));
                    linesToShow.set(1, linesToShow.get(2));
                    linesToShow.remove(2);*/
                    lines.clear();
                    linesToShow.clear();
                    waitingForConfirm = false;
                }
                else if(linesToShow.size() < 3)
                {
                    if(i < indivWords.length)
                    {
                        FontMetrics fm;
                        try
                        {
                            Game gg = (Game) ref;
                            fm = gg.g.getFontMetrics(new Font(Game.DEFAULT_FONT, Font.PLAIN, 30));
                        }
                        catch(Exception ex)
                        {
                            MenuScreen mm = (MenuScreen) ref;
                            fm = mm.g.getFontMetrics(new Font(Game.DEFAULT_FONT, Font.PLAIN, 30));
                        }
                        if(fm.stringWidth(count+indivWords[i]) >= 350)
                        {
                            linesToShow.add(count.substring(0, 1));
                            lines.add(count);
                            count = "";
                        }
                        else
                        {
                            count += indivWords[i]+" ";
                            i++;
                            if(i == indivWords.length)
                            {
                                linesToShow.add(count.substring(0, 1));
                                lines.add(count);
                                count = "";
                            }
                        }
                    }
                    else
                    {
                        remove = true;
                    }
                }
            }
            addCharTimer = 0;
        }
        else
        {
            addCharTimer++;
        }
    }
    
    /**
     * Draws the message.
     * 
     * @param g The object on which the graphics is drawn.
     */
    public void drawMessage(Graphics g)
    {
        g.setFont(new Font(Game.DEFAULT_FONT, Font.PLAIN, 30));
        g.setColor(new Color(255,255,255,backgroundAlpha));
        g.fillRect(-Game.cameraX+(Game.WIDTH-550)/2, -Game.cameraY+Game.HEIGHT-175, 550, 150);
        g.setColor(Color.BLACK);
        if(textOnRight)
            g.drawImage(img, -Game.cameraX+(Game.WIDTH-550)/2+10, -Game.cameraY+Game.HEIGHT-165, 130, 130, null);
        else
            g.drawImage(img, -Game.cameraX-(Game.WIDTH-550)/2-140+Game.WIDTH, -Game.cameraY+Game.HEIGHT-165, 130, 130, null);
        for(int k = 0; k < linesToShow.size(); k++)
        {
            if(textOnRight)
                g.drawString(linesToShow.get(k), -Game.cameraX+(Game.WIDTH-550)/2+170, -Game.cameraY+Game.HEIGHT-132+k*40);
            else
                g.drawString(linesToShow.get(k), -Game.cameraX+(Game.WIDTH-550)/2+20, -Game.cameraY+Game.HEIGHT-132+k*40);
        }
    }
    
    /**
     * A special method that is created for the UnlockedMessage class to
     * override. Details are in the UnlockedMessage class.
     */
    public void removeBasedOnType()
    {
        remove = true;
    }
}
