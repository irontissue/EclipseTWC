/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package moving;

import java.awt.Color;
import java.awt.Image;

/**
 * A default ability. We were planning to make more abilities by extending
 * this class, but we did not have time. So this is the only type of ability
 * we have for now.
 * 
 * @author EclipseTWC
 */
public class Ability extends Item
{
    public int energyUse;
    public int[] dmgRange;
    
    private Image shotImage;
    
    public double currCooldown = 0;
    public double cooldown;
    
    /**
     * Default constructor.
     * 
     * @param name          The name of the ability.
     * @param description   The description of the ability.
     * @param xOnMap        The x position on the map, if it is on the map.
     * @param yOnMap        The y position on the map, if it is on the map.
     * @param isOnMap       Whether the item is on the map or not.
     * @param dmgRange      The damage range of the ability, represented as an int array of length 2 [lower bound,upper bound]
     * @param img           The icon for the ability.
     * @param shotImage     The image of the shot(s) that the ability will shoot.
     * @param energyUse     How much energy the ability uses on use.
     * @param cooldown      The cooldown time for the ability (i.e. How long you must wait before using it again).
     */
    public Ability(String name, String description, int xOnMap, int yOnMap, boolean isOnMap, int[] dmgRange, Image img, Image shotImage, int energyUse, double cooldown)
    {
        super(name, description, xOnMap, yOnMap, isOnMap, img);
        this.energyUse = energyUse;
        this.cooldown = cooldown;
        this.dmgRange = dmgRange;
        this.shotImage = shotImage;
        checkWidth.put(name, Color.BLACK);
        checkWidth.put(description, Color.BLUE);
        checkWidth.put("Active", Color.WHITE);
        checkWidth.put("Damage: " + dmgRange[0] + "-" + dmgRange[1] + " per shot", Color.RED);
        checkWidth.put("500 pixel range", Color.RED);
    }
    
    /**
     * Activates the ability. In other words, does what the ability is supposed
     * to do.
     * 
     * @param myGame A copy of the game to write info to.
     */
    public void activate(Game myGame)
    {
        if(myGame.player.energy >= energyUse && currCooldown <= 0)
        {
            int xrandomize = (int) (Math.random()*51)-25;
            int yrandomize = (int) (Math.random()*51)-25;
            //SoundEffect.playAudio(new File("resources/audio/effects/special.wav"));
            for(double i = 0; i < Math.PI*2; i+= Math.PI/10.0)
            {
                myGame.shots.add(new Shot(myGame, myGame.mouseX-Game.cameraX+xrandomize, myGame.mouseY-Game.cameraY+yrandomize, i, 10, 500, 0, 0, false, true, dmgRange, true, false, null, shotImage));
            }
            Sounds.addAudio(AssetManager.getSoundEffect("special"));
            myGame.player.energy -= energyUse;
            currCooldown = cooldown;
        }
    }
    
    /**
     * Updates the cooldown of the ability.
     */
    public void update()
    {
        if(currCooldown < 1/62.5)
        {
            currCooldown = 0;
        }
        else if(currCooldown > 1/62.5)
        {
            currCooldown-=(1/62.5);
        }
    }
}
