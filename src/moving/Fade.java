package moving;

import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.Timer;

/**
 * Takes care of fading the screen. Doesn't actually draw the fade; that's
 * up to the other classes to do. This class simply has its own Timer so it
 * can fade without relying on the other class's Timers. This also enables for
 * very smooth transitions.
 * We did not include a default constructor since this class is meant to be
 * used in a static context.
 * 
 * @author EclipseTWC
 */
public class Fade
{
    private static int alpha = 0;
    private static int deltaAlpha = 5;
    
    private static Timer t = new Timer(16, new ActionListener()
    {
        @Override
        public void actionPerformed(ActionEvent ae)
        {
            run();
        }
    });
    
    /**
     * Starts the fade sequence. This method should be called by other
     * classes in order to start the fading.
     */
    public static void fade()
    {
        if(!t.isRunning())
        {
            t.start();
            alpha = 1;
        }
    }
    
    /**
     * This method happens ever tick of the Timer, which is at a rate of
     * approximately 60 frames per second.
     * 
     * Handles fading.
     */
    private static void run()
    {
        alpha += deltaAlpha;
        if(alpha >= 300)
        {
            deltaAlpha *= -1;
            alpha = 300;
        }
        else if(alpha <= 0)
        {
            alpha = 0;
            t.stop();
            deltaAlpha = Math.abs(deltaAlpha);
        }
    }
    
    /**
     * Used by other classes to determine if the Fade class is reversing the
     * fade. The concept behind this is that when the Fade is at a maximum,
     * loading will be done "behind the scenes" and then the fade will return
     * back to transparency.
     * 
     * @return Whether the Fade is reversing (at its maximum alpha) or not.
     */
    public static boolean reversing()
    {
        return (alpha == 300);
    }
    
    /**
     * Returns the current alpha of the Fade. This is used by other classes
     * to draw the big black box that will cover up the screen when fading.
     * 
     * @return The currnet alpha of the fade.
     */
    public static int getAlpha()
    {
        if(alpha > 255)
        {
            return 255;
        }
        return alpha;
    }
}
