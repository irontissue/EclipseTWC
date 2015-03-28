package moving;

import java.io.*;
import javax.sound.sampled.*;

/**
 * Plays the background music.
 * 
 * @author EclipseTWC
 */
public class Music
{
    public static Clip currClip;
    
    /**
     * Plays the audio. Stops any other music that is playing.
     *
     * @param f the music file to be played.
     */
    public static void playAudio(File f)
    {
        if(currClip != null)
        {
            currClip.stop();
        }
        try
        {
            if (f.exists())
            {
                Clip clip;
                AudioInputStream audioIn = AudioSystem.getAudioInputStream(f);
                clip = AudioSystem.getClip();
                try
                {
                    clip.open(audioIn);
                    clip.start();
                    clip.loop(Clip.LOOP_CONTINUOUSLY);
                    currClip = clip;
                }
                catch(Exception e)
                {
                    clip.close();
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Used to stop/close the audio without having it play another file.
     */
    public static void stopAudio()
    {
        currClip.stop();
        currClip.close();
    }
}
