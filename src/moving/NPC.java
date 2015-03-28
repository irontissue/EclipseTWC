package moving;

import java.awt.Graphics;
import java.awt.Image;

/**
 * An NPC is a character that talks to the player but cannot be played by the
 * player. NPC's can be customized to talk to you and/or give items.
 * 
 * @author EclipseTWC
 */
public class NPC //Non-Playable Character
{
    private boolean firstMsg = true; //first message is NEVER recycled. If you want the first message to be recycled, make it a part of the Message[] array, also
    private boolean loopedAllOnce = false;
    
    public int recycle;
    public static final int DONT_RECYCLE = 0, RECYCLE_ALL = 1, RECYCLE_UNLOCKED_ONLY = 2, RECYCLE_LOCKED_ONLY = 3;
    
    public Message firstMessage;
    public Message[] messages;
    
    public double x, y;
    public double xSpeed;
    public double grav;
    
    private int moveTimer;
    private int msgIndex = -1;
    
    public Image img;
    
    public Item[] items;
    
    public String[] msgs, umsgs;
    
    /**
     * Default constructor.
     * 
     * @param x                 The x-position of the NPC.
     * @param y                 The y-position of the NPC.
     * @param firstMessage      The first message that the NPC will say. This message will be seen exactly once by the player, and that's at the beginning of the NPC's text.
     * @param messages          An array of Strings that represent the LOCKED messages that the NPC will say. See the UnlockedMessage class to see more about locked vs. unlocked messges.
     * @param unlockedMessages  An array of Strings that represent the UNLOCKED messages that the NPC will say. See the UnlockedMessage class to see more about locked vs. unlocked messges.
     * @param recycle           How the NPC "recycles" his messages; i.e. repeats all messages from the beginning, or just stays at the last message, etc. See final constants above.
     * @param img               The NPC Image.
     * @param items             An array of items that the NPC will drop. The NPC drops these items when you talk to him and he says his second-to-last message.
     */
    public NPC(int x, int y, String firstMessage, String[] messages, String[] unlockedMessages, int recycle, Image img, Item[] items)
    //An NPC is programmed to loop once through all of its messages, and when it reaches its SECOND TO LAST message (which is a trigger message),
    //it will give the player an item or a hint or something significant. After it finished looping once (the last message usually is a "your welcome"
    //message), the NPC will loop accordingly, but might not give that item/hint ever again.
    {
        this.items = items;
        this.recycle = recycle;
        this.img = img;
        this.msgs = messages;
        this.umsgs = unlockedMessages;
        this.firstMessage = new Message(firstMessage, false, img);
        if((messages == null || messages[0].equals("")) && (unlockedMessages == null || unlockedMessages[0].equals("")))
        {
            this.messages = new Message[0];
        }
        else if(messages == null || messages[0].equals(""))
        {
            this.messages = new Message[unlockedMessages.length];
        }
        else if(unlockedMessages == null || unlockedMessages[0].equals(""))
        {
            this.messages = new Message[messages.length];
        }
        else
        {
            this.messages = new Message[unlockedMessages.length+messages.length];
        }
        if(messages != null && !messages[0].trim().equals(""))
            for(int i = 0; i < messages.length; i++)
            {
                this.messages[i] = new Message(messages[i], false, img);
            }
        if(unlockedMessages != null && !unlockedMessages[0].trim().equals(""))
            for(int i = 0; i < unlockedMessages.length; i++)
            {
                this.messages[i+messages.length] = new UnlockedMessage(unlockedMessages[i], false, img);
            }
        this.x = x;
        this.y = y;
    }
    
    /**
     * Draws the NPC.
     * 
     * @param g The object on which the graphics is drawn.
     */
    public void drawNPC(Graphics g)
    {
        g.drawImage(img, (int)Math.round(x), (int)Math.round(y), null);
    }
    
    /**
     * Updates the NPC's position and checks for collision.
     * 
     * @param myGame A copy of the current Game.
     */
    public void updatePosition(Game myGame)
    {
        boolean isOnSolidGround = false;
        int xxMax = img.getWidth(null);
        if(xxMax <= Grid.CELL_SIZE)
        {
            xxMax = Grid.CELL_SIZE+1;
        }
        if(Math.abs(xSpeed) > 0.05)
        {
            if(xSpeed > 0)
            {
                xSpeed -= 0.1;
            }
            else if(xSpeed < 0)
            {
                xSpeed += 0.1;
            }
        }
        else
        {
            xSpeed = 0;
        }
        x+=xSpeed;
        for(int xx = (int)Math.round(x)/Grid.CELL_SIZE; xx < ((int)(Math.round(x)+xxMax)/Grid.CELL_SIZE); xx++)
        {
            Cell cell = myGame.grid.cells[xx][((int)Math.round(y)+img.getHeight(null))/Grid.CELL_SIZE];
            if(cell.isSolid && cell.isReal == 0)
            {
                isOnSolidGround = true;
                xx = ((int)Math.round(x)+img.getWidth(null)/Grid.CELL_SIZE);
            }
        }
        if(!isOnSolidGround)
        {
            grav+=0.2;
        }
        else
        {
            grav = -grav*0.4;
            if(Math.abs(grav) < 0.1)
            {
                grav = 0;
            }
            y = myGame.grid.cells[0][((int)Math.round(y+img.getHeight(null)))/Grid.CELL_SIZE].getDrawCoord().y - img.getHeight(null);
        }
        if(grav > 8)
        {
            grav = 8;
        }
        y+=grav;
    }
    
    /**
     * Fetches the correct message to display. Automatically keeps track of
     * which message the player will see next.
     * 
     * @param myGame    A copy of the current Game.
     * @return          The correct message to display.
     */
    public Message getMessage(Game myGame)
    {
        if(firstMsg)
        {
            firstMsg = false;
            return firstMessage;
        }
        else
        {
            msgIndex++;
            if(loopedAllOnce)
            {
                if(recycle == RECYCLE_LOCKED_ONLY)
                {
                    try
                    {
                        UnlockedMessage u = (UnlockedMessage) messages[msgIndex];
                        msgIndex = 0;
                    }
                    catch (Exception e)
                    {
                        
                    }
                }
                else if(msgIndex == messages.length)
                {
                    if(recycle == DONT_RECYCLE)
                    {
                        msgIndex = messages.length-1;
                    }
                    else if(recycle == RECYCLE_ALL)
                    {
                        msgIndex = 0;
                    }
                    else if(recycle == RECYCLE_UNLOCKED_ONLY)
                    {
                        int index = 0;
                        boolean done = false;
                        while(!done)
                        {
                            try
                            {
                                UnlockedMessage u = (UnlockedMessage) messages[index];
                                msgIndex = index;
                                done = true;
                            }
                            catch (Exception e)
                            {
                                index++;
                            }
                        }
                    }
                }
            }
            else
            {
                if(msgIndex == messages.length-2)
                {
                    for(Item it: items)
                    {
                        if(it!=null)
                        {
                            it.xOnMap = x;
                            it.yOnMap = y-45;
                            myGame.itemsOnMap.add(it);
                        }
                    }
                }
                if(msgIndex == messages.length)
                {
                    if(recycle == RECYCLE_LOCKED_ONLY)
                    {
                        try
                        {
                            UnlockedMessage u = (UnlockedMessage) messages[msgIndex];
                            msgIndex = 0;
                        }
                        catch (Exception e)
                        {
                            
                        }
                    }
                    else if(msgIndex == messages.length)
                    {
                        if(recycle == DONT_RECYCLE)
                        {
                            msgIndex = messages.length-1;
                        }
                        else if(recycle == RECYCLE_ALL)
                        {
                            msgIndex = 0;
                        }
                        else if(recycle == RECYCLE_UNLOCKED_ONLY)
                        {
                            int index = 0;
                            boolean done = false;
                            while(!done)
                            {
                                try
                                {
                                    UnlockedMessage u = (UnlockedMessage) messages[index];
                                    msgIndex = index;
                                    done = true;
                                }
                                catch (Exception e)
                                {
                                    index++;
                                }
                            }
                        }
                    }
                    loopedAllOnce = true;
                }
            }
            return new Message(messages[msgIndex].text, messages[msgIndex].textOnRight, img);
        }
    }
}
