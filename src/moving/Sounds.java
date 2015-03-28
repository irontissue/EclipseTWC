package moving;

import java.io.File;
import java.io.FileInputStream;
import java.util.ArrayList;
import javax.sound.sampled.Clip;
import sun.audio.AudioPlayer;
import sun.audio.AudioStream;

/**
 * Plays sound effects.
 * 
 * @author EclipseTWC
 */
public class Sounds
{    
    private static ArrayList<File> sounds = new ArrayList();
    
    private static Clip clip;
    
    /**
     * Adds the audio to the list of audios to play.
     *
     * @param f the sound file to be played
     */
    public static void addAudio(File f)
    {
        try
        {
            if(f != null && f.exists())
            {
                sounds.add(f);
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
    
    public static void run()
    {
        try
        {
            if(!sounds.isEmpty())
            {
                /*ArrayList<byte[]> byteArrays = new ArrayList();
                long size = 0;
                int pos = 0;
                for(int i = 0; i < sounds.size(); i++)
                {
                    AudioInputStream temp = AudioSystem.getAudioInputStream(sounds.get(i));
                    ByteArrayOutputStream byteStream = new ByteArrayOutputStream();
                    byte[] buffer = new byte[1024];
                    int numRead;
                    while((numRead = temp.read(buffer)) != -1)
                    {
                        byteStream.write(buffer, 0, numRead);
                    }
                    byte[] tot = byteStream.toByteArray();
                    byteArrays.add(tot);
                    if(size < temp.getFrameLength()){
                        size = temp.getFrameLength();
                        pos = i;
                    }
                }
                byte[] compiledStream = new byte[byteArrays.get(pos).length];
                for(int i = 0; i < compiledStream.length; i++)
                {
                    int byteSum = 0;
                    for(int j = 0; j < byteArrays.size(); j++)
                    {
                        try
                        {
                            byteSum += byteArrays.get(j)[i];
                        }
                        catch(Exception e)
                        {
                            byteArrays.remove(j);
                        }
                    }
                    compiledStream[i] = (byte) (byteSum / byteArrays.size());
                }
                InputStream ais = new AudioInputStream(new ByteArrayInputStream(compiledStream), AudioSystem.getAudioInputStream(sounds.get(pos)).getFormat(), AudioSystem.getAudioInputStream(sounds.get(pos)).getFrameLength());
                *///Game.soundPlayed = true;
                /*clip = AudioSystem.getClip();
                clip.open(ais);
                clip.start();*/
                //AudioPlayer.player.start(astr);
                for(int i = 0; i < sounds.size(); i++)
                {
                    AudioStream as = new AudioStream(new FileInputStream(sounds.get(i)));
                    AudioPlayer.player.start(as);
                }
                sounds.clear();
            }
            /*if(clip != null && !clip.isRunning())
            {
                clip.close();
            }*/
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }
    }
}
