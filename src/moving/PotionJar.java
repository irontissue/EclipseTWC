package moving;

import java.awt.Color;
import java.awt.Image;

/**
 * Stores and updates all information for a Potion Jar.
 * 
 * @author EclipseTWC
 */
public class PotionJar extends Item
{
    public int maxHealAmount;
    public int currVolume;
    public int type;
    
    public double cooldown;
    public double currCooldown;
    
    public static final int HEALTH = 0, ENERGY = 1;
    
    /**
     * Default constructor.
     * 
     * @param name          The name of the item.
     * @param description   The item's description.
     * @param xOnMap        The x-location of the item on the map, if it is on the map.
     * @param yOnMap        The y-location of the item on the map, if it is on the map.
     * @param isOnMap       Whether the item is on the map or not.
     * @param img           The Image of the item.
     * @param type          The type of potion jar, i.e. health or energy.
     * @param maxHealAmount The maximum amount of potion this potion jar can hold.
     * @param cooldown      The cooldown (i.e. how long you must wait before using this jar again). This prevents spamming of potions.
     */
    public PotionJar(String name, String description, int xOnMap, int yOnMap, boolean isOnMap, Image img, int type, int maxHealAmount, double cooldown)
    {
        super(name, description, xOnMap, yOnMap, isOnMap, img);
        this.maxHealAmount = maxHealAmount;
        this.currVolume = maxHealAmount;
        this.type = type;
        this.cooldown = cooldown;
        this.currCooldown = 0;
        checkWidth.put(name, Color.BLACK);
        checkWidth.put(description, Color.BLUE);
        checkWidth.put("Cooldown: " + cooldown + " seconds", Color.RED);
        checkWidth.put("Amount remaining: " + (currVolume + "/" + maxHealAmount), Color.RED);
    }
    
    /**
     * Given an amount of health needed, gives as much health needed from this
     * jar. If the amount needed exceeds the amount left in the jar, this jar
     * gives back whatever it can, and empties itself. If the amount needed
     * is less than the amount left in the jar, the potion jar gives exactly
     * the amount needed, and keeps the remaining potion, and gets put on
     * a cooldown.
     * 
     * @param healthNeeded The amount of health needed by the player
     * @return The amount of health given back to the player.
     */
    public int getHeal(int healthNeeded)
    {
        if(currCooldown <= 0)
        {
            checkWidth.remove("Amount remaining: " + (currVolume + "/" + maxHealAmount));
            if(currVolume < healthNeeded)
            {
                int ass = currVolume;
                currVolume = 0;
                img = AssetManager.getImage("potionJar");
                if(currVolume != 0)
                {
                    currCooldown = cooldown;
                }
                checkWidth.put("Amount remaining: " + (currVolume + "/" + maxHealAmount), Color.RED);
                return ass;
            }
            else
            {
                currVolume -= healthNeeded;
                if(healthNeeded != 0)
                {
                    currCooldown = cooldown;
                }
                checkWidth.put("Amount remaining: " + (currVolume + "/" + maxHealAmount), Color.RED);
                return healthNeeded;
            }
        }
        else
        {
            return 0;
        }
    }
    
    /**
     * Updates the cooldown of the potion jar.
     */
    public void update()
    {
        currCooldown -= (1/62.5);
    }
    
    /**
     * Fetches the ratio of this jar's current amount remaining to the max
     * amount of potion it can hold.
     * 
     * @return Ratio of current volume to the maximum amount the jar can hold.
     */
    public double getRatio()
    {
        return (double)currVolume/maxHealAmount;
    }
    
    /**
     * Exports the PotionJar for use on exporting the map. It will also
     * export the potion jar's remaining volume. This is only used when the
     * jar is on the map.
     * 
     * @return A String that represents the Potion Jar's data to export.
     */
    @Override
    public String export()
    {
        return (name + "," + xOnMap + "," + yOnMap + "," + (int)currVolume);
    }
}
