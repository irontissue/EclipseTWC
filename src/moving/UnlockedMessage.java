package moving;

import java.awt.Image;

/**
 * An unlocked message is almost the same thing as a normal Message, which we
 * refer to as a "locked" message, but the unlocked message does not restrict
 * player actions. Thus, unlocked messages must have a timer that allows them
 * to automatically move on at a set rate.
 * 
 * @author EclipseTWC
 */
public class UnlockedMessage extends Message
{
    public int timer = 0;
    
    /**
     * Default constructor.
     * 
     * @param text          The text that the message will display.
     * @param textOnRight   Whether the text is on the right or the left. (If text is on right, the picture will be on the left, and vice-versa)
     * @param img           The picture that will be displayed as the person or thing that is saying the message.
     */
    public UnlockedMessage(String text, boolean textOnRight, Image img)
    {
        super(text, textOnRight, img);
        backgroundAlpha = 120;
    }
    
    /**
     * A special remove method made just for this unlocked message. Necessary
     * due to timing of removal.
     */
    @Override
    public void removeBasedOnType()
    {
        if(lines.isEmpty())
        {
            remove = true;
        }
    }
}
