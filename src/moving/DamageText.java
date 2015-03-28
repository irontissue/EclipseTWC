package moving;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;

/**
 * The text that displays above the enemy/player when an enemy or the player
 * gets hurt or healed.
 * 
 * @author EclipseTWC
 */
public class DamageText
{
    private String text;
    
    private Enemy e;
    private Player p;
    
    private Color c;
    
    private double x, y;
    private double yAdjust = 0;
    private int alpha = 255;
    private int xRange, myXRand;
    
    private boolean remove = false;
    
    private Font font1 = new Font(Game.DEFAULT_FONT, Font.BOLD, 19);
    private Font font2 = new Font(Game.DEFAULT_FONT, Font.BOLD, 17);
    
    private FontMetrics fm;
    
    /**
     * Default constructor.
     * 
     * @param g     The object on which the graphics will be drawn.
     * @param o     The object which this damagetext is locked to (i.e. will follow this object).
     * @param text  The text that will display.
     * @param c     The Color of the DamageText.
     */
    public DamageText(Graphics g, Object o, String text, Color c)
    {
        this.text = text;
        this.xRange = xRange;
        if(o.getClass().equals(Player.class))
        {
            this.p = (Player) o;
        }
        else
        {
            this.e = (Enemy) o;
        }
        g.setFont(font1);
        fm = g.getFontMetrics();
        int txWidth = fm.stringWidth(text);
        if(p == null)
        {
            if(e.img.getWidth(null) < txWidth)
            {
                myXRand = (e.img.getWidth(null)-txWidth)/2;
            }
            else
            {
                myXRand = (int)(Math.random()*(e.img.getWidth(null)-txWidth+1));
            }
            this.x = e.getCoord().x+myXRand;
            this.y = e.getCoord().y-7;
        }
        else
        {
            if(p.currImage.getWidth(null) < txWidth)
            {
                myXRand = (p.currImage.getWidth(null)-txWidth)/2;
            }
            else
            {
                myXRand = (int)(Math.random()*(p.currImage.getWidth(null)-txWidth+1));
            }
            this.x = p.x-Player.PLAYER_WIDTH+myXRand;
            this.y = p.y-20;
        }
        this.c = c;
    }
    
    /**
     * Updates the position and fading of the text.
     * 
     * @return True if this DamageText is done fading and should be removed, false if not.
     */
    public boolean updateText()
    {
        yAdjust-=0.4;
        if(p == null)
        {
            this.x = e.getCoord().x+myXRand;
            this.y = e.getCoord().y-7;
        }
        else
        {
            this.x = p.x-p.currImage.getWidth(null)/2+myXRand;
            this.y = p.y-20;
        }
        if(alpha >= 60)
        {
            alpha-=alpha/40;
        }
        else if(alpha > 0)
        {
            alpha-=2;
        }
        if(alpha <= 0)
        {
            remove = true;
        }
        return remove;
    }
    
    /**
     * Draws the damage text.
     * 
     * @param g The object on which to draw the graphics.
     */
    public void drawText(Graphics g)
    {
        if(alpha < 0)
        {
            alpha = 0;
        }
        g.setFont(font1);
        fm = g.getFontMetrics();
        int tx1 = fm.stringWidth(text);
        g.setColor(new Color(0,0,0,alpha));
        g.drawString(text, (int)x, (int)(y+yAdjust+1));
        g.setFont(font2);
        fm = g.getFontMetrics();
        int tx2 = fm.stringWidth(text);
        double xx = (tx1-tx2)/2.0;
        g.setColor(new Color(c.getRed(), c.getGreen(), c.getBlue(), alpha));
        g.drawString(text, (int)Math.round(x+xx), (int)(y+yAdjust));
    }
}
