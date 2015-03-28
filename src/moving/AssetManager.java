package moving;

import java.awt.Color;
import java.awt.Graphics;
import java.awt.Image;
import java.awt.Toolkit;
import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;

/**
 * This class loads and stores ALL IMAGES in the entire game.
 * It makes it very easy to manage all the images and pull images from a compact location.
 * Also, solves image "reload" problems.
 * Since the methods in this class are all static, this class does not need to be
 * constructed and thus we did not include the default constructor.
 * 
 * @author EclipseTWC
 */
public class AssetManager
{
    private static HashMap<String, Image> images = new HashMap();
    private static HashMap<String, RangedWeapon> ranWeps = new HashMap();
    private static HashMap<String, MeleeWeapon> melWeps = new HashMap();
    private static HashMap<String, PotionJar> potJars = new HashMap();
    private static HashMap<String, File> soundEffects = new HashMap();
    private static HashMap<String, File> music = new HashMap();
    
    /**
     * Loads all media used in the game (music/sounds).
     */
    public static void loadAllMedia()
    {
        recurseFileSearch(new File("resources/images").listFiles(), images);
        recurseFileSearchAudio(new File("resources/audio/effects").listFiles(), soundEffects);
        recurseFileSearchAudio(new File("resources/audio/music").listFiles(), music);
    }
    
    /**
     * Loads all of the item data. Redirects to the loadItemDatas() method.
     */
    public static void loadItemData()
    {
        loadItemDatas();
    }
    
    /**
     * Initializes all images in the game. This removes image lag.
     * 
     * @param g The object on which the graphics will be drawn.
     */
    public static void imageInit(Graphics g)
    {
        g.setColor(Color.WHITE);
        for(String s : images.keySet())
        {
            g.drawImage(images.get(s), -10000, -10000, 50, 50, null);
        }
    }
    
    /**
     * Recursively searches for files and stores them in the target hashmap.
     * This method is specifically used for loading images.
     * 
     * @param files     The files to search and load data from.
     * @param target    The HashMap in which to store the data.
     */
    private static void recurseFileSearch(File[] files, HashMap target)
    {
        for(File f : files)
        {
            if(f.listFiles() == null)
            {
                String s = f.getName().replace('.', ',');
                String[] split = s.split(",");
                if(split[0] != null && !split[0].equals(""))
                {
                    target.put(split[0].toLowerCase(), Toolkit.getDefaultToolkit().getImage(f.getAbsolutePath()));
                }
            }
            else
            {
                recurseFileSearch(f.listFiles(), target);
            }
        }
    }
    
    /**
     * Recursively searches for files and stores them in the target hashmap.
     * This method is specifically used for loading audio.
     * 
     * @param files     The files to search and load data from.
     * @param target    The HashMap in which to store the data.
     */
    private static void recurseFileSearchAudio(File[] files, HashMap target)
    {
        if(files != null)
        for(File f : files)
        {
            if(f.listFiles() == null)
            {
                String s = f.getName().replace('.', ',');
                String[] split = s.split(",");
                if(split[0] != null && !split[0].equals(""))
                {
                    target.put(split[0].toLowerCase(), f);
                }
            }
            else
            {
                recurseFileSearch(f.listFiles(), target);
            }
        }
    }
    
    /**
     * Loads all the item data.
     */
    private static void loadItemDatas()
    {
        try
        {
            File rangedWeps = new File("resources/data/rangedWeapons.txt");
            if(rangedWeps.exists())
            {
                Scanner s = new Scanner(rangedWeps);
                while(s.hasNext())
                {
                    String tot = "";
                    String curr = "";
                    while(!curr.equals("-"))
                    {
                        try
                        {
                            String toAdd = s.nextLine().split(":")[1];
                            curr = toAdd;
                            if(!curr.equals("-"))
                            {
                                tot += curr+";";
                            }
                        }
                        catch (Exception e)
                        {
                            curr = "-";
                            tot = tot.substring(0, tot.length()-1);
                        }
                    }
                    String[] a = tot.split(";");
                    String[] dmgRange = a[3].split("-");
                    int[] dmgR = {Integer.parseInt(dmgRange[0]), Integer.parseInt(dmgRange[1])};
                    boolean prcsEnms;
                    boolean prcsWlls;
                    if(Integer.parseInt(a[12]) == 0)
                        prcsEnms = false;
                    else
                        prcsEnms = true;
                    if(Integer.parseInt(a[13]) == 0)
                        prcsWlls = false;
                    else
                        prcsWlls = true;
                    ranWeps.put(a[0].trim().toLowerCase(), new RangedWeapon(a[0].trim(),a[1].trim(),Item.NOT_ON_MAP_COORD,Item.NOT_ON_MAP_COORD,false,Double.parseDouble(a[2].trim()),dmgR,Integer.parseInt(a[4].trim()),Integer.parseInt(a[5].trim()),Integer.parseInt(a[6].trim()),getImage(a[7].trim()),getImage(a[8].trim()),Double.parseDouble(a[9].trim()),Double.parseDouble(a[10].trim()),Double.parseDouble(a[11].trim()),prcsEnms,prcsWlls));
                }
            }
            File meleeWeps = new File("resources/data/meleeWeapons.txt");
            if(meleeWeps.exists())
            {
                Scanner s = new Scanner(meleeWeps);
                while(s.hasNext())
                {
                    String tot = "";
                    String curr = "";
                    while(!curr.equals("-"))
                    {
                        try
                        {
                            String toAdd = s.nextLine().split(":")[1];
                            curr = toAdd;
                            if(!curr.equals("-"))
                            {
                                tot += curr+";";
                            }
                        }
                        catch (Exception e)
                        {
                            curr = "-";
                            tot = tot.substring(0, tot.length()-1);
                        }
                    }
                    String[] a = tot.split(";");
                    String[] dmgRange = a[3].split("-");
                    int[] dmgR = {Integer.parseInt(dmgRange[0].trim()), Integer.parseInt(dmgRange[1].trim())};
                    boolean prcsEnms;
                    boolean prcsWlls;
                    if(Integer.parseInt(a[6].trim()) == 0)
                        prcsEnms = false;
                    else
                        prcsEnms = true;
                    if(Integer.parseInt(a[7].trim()) == 0)
                        prcsWlls = false;
                    else
                        prcsWlls = true;
                    melWeps.put(a[0].trim().toLowerCase(), new MeleeWeapon(a[0].trim(), a[1].trim(), Item.NOT_ON_MAP_COORD, Item.NOT_ON_MAP_COORD, false, Double.parseDouble(a[2].trim()), dmgR, Integer.parseInt(a[4].trim()), Integer.parseInt(a[5].trim()), prcsEnms, prcsWlls, getImage(a[8].trim()), getImage(a[9].trim())));
                }
            }
            File potionJars = new File("resources/data/potionJars.txt");
            if(potionJars.exists())
            {
                Scanner s = new Scanner(potionJars);
                while(s.hasNext())
                {
                    String tot = "";
                    String curr = "";
                    while(!curr.equals("-"))
                    {
                        try
                        {
                            String toAdd = s.nextLine().split(":")[1];
                            curr = toAdd;
                            if(!curr.equals("-"))
                            {
                                tot += curr+";";
                            }
                        }
                        catch (Exception e)
                        {
                            curr = "-";
                            tot = tot.substring(0, tot.length()-1);
                        }
                    }
                    String[] a = tot.split(";");
                    potJars.put(a[0].trim().toLowerCase(), new PotionJar(a[0].trim(), a[1].trim(), Item.NOT_ON_MAP_COORD, Item.NOT_ON_MAP_COORD, false, AssetManager.getImage(a[2].trim()), Integer.parseInt(a[3].trim()), Integer.parseInt(a[4].trim()), Double.parseDouble(a[5].trim())));
                }
            }
            File abilities = new File("resources/data/abilities.txt");
            if(abilities.exists())
            {
                Scanner s = new Scanner(abilities);
                while(s.hasNext())
                {
                    String tot = "";
                    String curr = "";
                    while(!curr.equals("-"))
                    {
                        try
                        {
                            String toAdd = s.nextLine().split(":")[1];
                            curr = toAdd;
                            if(!curr.equals("-"))
                            {
                                tot += curr+",";
                            }
                        }
                        catch (Exception e)
                        {
                            curr = "-";
                            tot = tot.substring(0, tot.length()-1);
                        }
                    }
                    System.out.println(tot);
                }
            }
            File armors = new File("resources/data/armors.txt");
            if(armors.exists())
            {
                Scanner s = new Scanner(armors);
                while(s.hasNext())
                {
                    String tot = "";
                    String curr = "";
                    while(!curr.equals("-"))
                    {
                        try
                        {
                            String toAdd = s.nextLine().split(":")[1];
                            curr = toAdd;
                            if(!curr.equals("-"))
                            {
                                tot += curr+",";
                            }
                        }
                        catch (Exception e)
                        {
                            curr = "-";
                            tot = tot.substring(0, tot.length()-1);
                        }
                    }
                    System.out.println(tot);
                }
            }
            File misc = new File("resources/data/miscellaneous.txt");
            if(misc.exists())
            {
                Scanner s = new Scanner(misc);
                while(s.hasNext())
                {
                    String tot = "";
                    String curr = "";
                    while(!curr.equals("-"))
                    {
                        try
                        {
                            String toAdd = s.nextLine().split(":")[1];
                            curr = toAdd;
                            if(!curr.equals("-"))
                            {
                                tot += curr+",";
                            }
                        }
                        catch (Exception e)
                        {
                            curr = "-";
                            tot = tot.substring(0, tot.length()-1);
                        }
                    }
                    System.out.println(tot);
                }
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            Main.logger.log(Level.SEVERE, "Error in loadItemData() method in AssetManager.class");
        }
    }
    
    /**
     * Fetches an image from the images HashMap.
     * 
     * @param s The name of the image to get.
     * @return  The Image indicated by 
     */
    public static Image getImage(String s)
    {
        return images.get(s.toLowerCase());
    }
    
    /**
     * Given an image, returns the String name associated with it.
     * 
     * @param img   The image to get the name of
     * @return      A String that is the name of the image.
     */
    public static String getImageSource(Image img)
    {
        return (String)((List) Arrays.asList(images.keySet().toArray())).get(((List) Arrays.asList(images.values().toArray())).indexOf(img));
    }
    
    /**
     * Given a String, fetches the item associated with that String. If no item
     * is found, this returns null.
     * 
     * @param s The name of the item to get.
     * @return  The Item represented by the String.
     */
    public static Item getItem(String s)
    {
        Item i = null;
        if(ranWeps.get(s.toLowerCase()) != null)
        {
            RangedWeapon r = ranWeps.get(s.toLowerCase());
            i = new RangedWeapon(r.name,r.description,(int)r.xOnMap,(int)r.yOnMap,r.isOnMap,r.attSpeed,r.damage,r.spreadAngle,r.numShots,(int)Math.round(Math.toDegrees(r.shotAngleSep)),r.img,r.shotImage,r.speed,r.range,r.gravity,r.piercesEnemies,r.piercesWalls);
        }
        else if(melWeps.get(s.toLowerCase()) != null)
        {
            MeleeWeapon m = melWeps.get(s.toLowerCase());
            i = new MeleeWeapon(m.name,m.description,(int)m.xOnMap,(int)m.yOnMap,m.isOnMap,m.attSpeed,m.damage,m.arcLength,m.swingSpeed,m.piercesEnemies,m.piercesWalls,m.img,m.shotImage);
            
        }
        else if(potJars.get(s.toLowerCase()) != null)
        {
            PotionJar p = potJars.get(s.toLowerCase());
            i = new PotionJar(p.name,p.description,(int)p.xOnMap,(int)p.yOnMap,p.isOnMap,p.img,p.type,p.maxHealAmount,p.cooldown);
        }
        else if(s.toLowerCase().equals("ability") || s.toLowerCase().equals("fire spray"))
        {
            int[] dmgRange = {1,2};
            i = new Ability("Fire Spray", "A spray of fire that radiates out from target.", 100, 200, false, dmgRange, AssetManager.getImage("ability1"), AssetManager.getImage("shot2"), 20, 1.5);
        }
        else if(s.toLowerCase().equals("ability2") || s.toLowerCase().equals("Fiery explosion"))
        {
            int[] dmgRange = {2,4};
            i = new Ability("Fiery Explosion", "Deadly fire spews out from the target location.", 100, 200, false, dmgRange, AssetManager.getImage("ability1"), AssetManager.getImage("bigShot"), 20, 1.5);
        }
        else if(s.toLowerCase().equals("ability3") || s.toLowerCase().equals("Blazing Nova"))
        {
            int[] dmgRange = {5,8};
            i = new Ability("Blazing Nova", "Incredibly powerful bursts of fire emanate from the target location.", 100, 200, false, dmgRange, AssetManager.getImage("ability1"), AssetManager.getImage("hugeShot"), 20, 1.5);
        }
        return i;
    }
    
    /**
     * Given a String, fetches the sound file associated with that String. If no
     * sound file is found, this returns null.
     * 
     * @param s The name of the sound file to get.
     * @return  The sound file represented by the String.
     */
    public static File getSoundEffect(String s)
    {
        File f = null;
        if(soundEffects.get(s.toLowerCase()) != null)
        {
            f = soundEffects.get(s.toLowerCase());
        }
        return f;
    }
    
    /**
     * Given a String, fetches the music file associated with that String. If no
     * music file is found, this returns null.
     * 
     * @param s The name of the music file to get.
     * @return  The music file represented by the String.
     */
    public static File getMusic(String s)
    {
        File f = null;
        if(music.get(s.toLowerCase()) != null)
        {
            f = music.get(s.toLowerCase());
        }
        return f;
    }
}
