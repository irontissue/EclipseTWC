package moving;

import java.awt.Color;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.Image;
import java.util.ArrayList;

/**
 * Stores all data for the player and manages it. Draws the player.
 * 
 * Player stats and their definitions:
 * Stamina = how much health you have. Each point in stamina gives 19 health.                                                                           0 Points: 50 health                                  50 Points: 1000 health
 * Potential = how much energy you have. Each point in potential gives 12 energy.                                                                       0 Points: 30 energy                                  50 Points: 630 energy
 * Defense = Damage reduction. Each point in defense gives you 0.8% damage reduction.                                                                   0 Points: 0% damage reduction                        50 Points: 40% damage reduction
 * Speed = How fast you move and how fast you attack. Each point in speed gives you 0.05 moveSpeed and 0.001 acceleration, and 0.07 attack speed.       0 Points: 2 movespeed/0.16 accel, 1 attspeed         50 Points: 4.5 movespeed/0.21 accel, 4.5 attspeed
 * Strength = How potent your attacks are. Each point in strength increases damage done by your attacks by a factor of 0.015.                           0 Points: Attacks do 75% damage                      50 Points: Attacks do 150% damage
 * Intelligence = How fast you regenerate health/energy. Each point in intelligence gives 0.05% health regen and 0.032% energy regen.                   0 Points: 0.5% health regen, 0.4% energy regen       50 Points: 3% health regen, 2% energy regen
 * Luck = How often you get a critical hit, and how potent they are. Each point in luck gives 0.18% extra chance to crit, and 2% extra crit damage.     0 Points: 1% crit chance, 150% crit damage           50 Points: 10% crit chance, 250% crit damage
 * 
 * @author EclipseTWC
 */
public class Player
{
    public static final int INVENTORY_CELL_SIZE = 30;
    
    public String name;
    
    public int luck = 0, defense = 0, speed = 0, strength = 0, potential = 0, stamina = 0, intelligence = 0; //These are the player's BASE stats.
    public int tLck = 0, tDef = 0, tSpd = 0, tStr = 0, tPot = 0, tSta = 0, tInt = 0; //These are the "true" or "total" stats, with armor/weapon bonuses added on.
    
    public double x = 100, y = 100, xSpeed = 0, xAccel = 0.16+speed*0.001, maxSpeed = 2+speed*0.05;
    public double currentGrav = 0, gravConstant = 0.7, jumpHeight = 6, maxGrav = 8;
    public double health = 50+stamina*19, energy = 30+potential*12, maxHealth = health, maxEnergy = energy;
    public double shootSpeed = 1+speed*0.07; //shootSpeed is per second
    public double energyRegen = 0.4+intelligence*0.032, healthRegen = 0.5+intelligence*0.05; //percent per second
    public double critChance = 1+luck*0.18, critDamage = 150+luck*2; //percent chance/damage
    
    public int currWeaponIndex = 0, currAbilityIndex = 0, level = 1, xp = 0, xpneeded = 10;
    public static final int PLAYER_WIDTH = 30, PLAYER_HEIGHT = 46;
    private int levelUpTimer = -1;
    public static final int LEFT = 1, RIGHT = 2;
    public int direction;
    public int levelUpPoints = 0;
    
    public static final int PLAYER_X = Main.FRAME_WIDTH/2, PLAYER_Y = Main.FRAME_HEIGHT/2+50;
    
    public static final int INVENTORY = 0, WEAPONS = 1, ABILITIES = 2, HEAD = 3, BODY = 4, FOOT = 5, POTIONS = 6;
    
    public Item[][] allItems = new Item[7][]; //indices: 0 = inventory, 1 = weapons, 2 = abilities, 3 = head, 4 = body, 5 = foot, 6 = potions
    
    public ArrayList<DamageText> damages = new ArrayList();
    
    public Image standing;
    public Image jumpUp, jumpDown;
    public ArrayList<Image> runningFrames = new ArrayList();
    public Image currImage = standing;
    public int currImageIndex = 5;
    private double imageSwitchTimer = 0;
    
    /**
     * Default constructor.
     * 
     * @param name      The player's name.
     * @param theGame   A copy of the current game.
     */
    public Player(String name, Game theGame)
    {
        //System.out.println(Grid.CELL_SIZE);
        this.name = name;
        allItems[INVENTORY] = new Item[100];
        allItems[WEAPONS] = new Weapon[6];
        allItems[ABILITIES] = new Ability[3];
        allItems[HEAD] = new Head[1];
        allItems[BODY] = new Body[1];
        allItems[FOOT] = new Foot[1];
        allItems[POTIONS] = new PotionJar[3];
        standing = AssetManager.getImage("standing");
        jumpUp = AssetManager.getImage("jumpup");
        jumpDown = AssetManager.getImage("jumpDown");
        runningFrames.add(AssetManager.getImage("tmp-0"));
        runningFrames.add(AssetManager.getImage("tmp-1"));
        runningFrames.add(AssetManager.getImage("tmp-2"));
        runningFrames.add(AssetManager.getImage("tmp-3"));
        runningFrames.add(AssetManager.getImage("tmp-4"));
        runningFrames.add(AssetManager.getImage("tmp-5"));
        runningFrames.add(AssetManager.getImage("tmp-6"));
        runningFrames.add(AssetManager.getImage("tmp-7"));
        runningFrames.add(AssetManager.getImage("tmp-8"));
        runningFrames.add(AssetManager.getImage("tmp-9"));
        runningFrames.add(AssetManager.getImage("tmp-10"));
        runningFrames.add(AssetManager.getImage("tmp-11"));
        try
        {
            allItems[WEAPONS][0] = AssetManager.getItem("Magic Bolter");
            allItems[POTIONS][0] = AssetManager.getItem("Health Potion Jar");
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * Draws the player.
     * 
     * @param g The object on which the graphics is drawn.
     */
    public void drawPlayer(Graphics g)
    {
        g.setColor(Color.RED);
        if(xSpeed > 0)
        {
            direction = RIGHT;
        }
        else if(xSpeed < 0)
        {
            direction = LEFT;
        }
        if(direction == LEFT)
        {
            g.drawImage(currImage, (int) Math.round(x)-15, (int)Math.round(y)-24, 30, 46, null);
        }
        else
        {
            g.drawImage(currImage, (int) Math.round(x)+15, (int) Math.round(y)-24, -30, 46, null);
        }        
        //g.fillRect((int) Math.round(x-16), (int) Math.round(y-24), playerWidth+1, playerHeight);
        if(levelUpTimer > 0)
        {
            g.setFont(new Font(Game.DEFAULT_FONT, Font.PLAIN, 20));
            g.setColor(Color.GREEN);
            g.drawString("Level Up!", (int)x-37, (int)y-30);
            levelUpTimer--;
        }
        for(int i = 0; i < damages.size(); i++)
        {
            damages.get(i).drawText(g);
        }
    }
    
    /**
     * Updates the player and checks for "level-up". Also updates
     * image management.
     */
    public void updatePlayer()
    {
        if(xp >= xpneeded)
        {
            if(level > 88)
            {
                xp = xpneeded-1;
            }
            else
            {
                level+=1;
                levelUpPoints+=4;
                xp = xp-xpneeded;
                xpneeded += level*12;
                levelUpTimer = 200;
            }
        }
        while(damages.size() > 10)
        {
            damages.remove(0);
        }
        for(int i = 0; i < damages.size(); i++)
        {
            damages.get(i).updateText();
        }
        if(currentGrav > 0)
        {
            currImage = jumpDown;
        }
        else if(currentGrav < 0)
        {
            currImage = jumpUp;
        }
        else
        {
            if(xSpeed != 0)
            {
                imageSwitchTimer += Math.abs(xSpeed)*1.1;
                if(imageSwitchTimer > 5)
                {
                    imageSwitchTimer -= 5;
                    currImageIndex++;
                    if(currImageIndex >= runningFrames.size())
                    {
                        currImageIndex = 0;
                    }

                }
                currImage = runningFrames.get(currImageIndex);
            }
            else
            {
                currImage = standing;
            }
        }
        if(energy < maxEnergy)
        {
            energy+=(energyRegen*0.01*maxEnergy)/62.5;
            if(energy > maxEnergy)
            {
                energy = maxEnergy;
            }
        }
        if(health < maxHealth)
        {
            health+=(healthRegen*0.01*maxHealth)/62.5;
            if(health > maxHealth)
            {
                health = maxHealth;
            }
        }
        
    }
    
    /**
     * Updates the player's stats. This method was also supposed to add up
     * the TOTAL stat levels based on armor and other stat bonuses, but we
     * did not have enough time to make any armor, thus the "total" stats (e.g.
     * tPot, tSta, etc.) are useless. We have left them in just to show
     * how the framework would work.
     */
    public void updateStats()
    {
        tPot = potential;
        tSta = stamina;
        tDef = defense;
        tStr = strength;
        tSpd = speed;
        tInt = intelligence;
        tLck = luck;
        xAccel = 0.16+tSpd*0.001;
        maxSpeed = 2+tSpd*0.05;
        maxHealth = 50+tSta*19;
        maxEnergy = 30+tPot*12;
        shootSpeed = 1+tSpd*0.07;
        energyRegen = 0.4+tInt*0.032;
        healthRegen = 0.5+tInt*0.05;
        critChance = 1+tLck*0.18;
        critDamage = 150+tLck*2;
    }
    
    /**
     * Changes the weapon index by amount dx. The player has six weapon slots,
     * thus this allows the player to rotate between slots.
     * 
     * @param dx 
     */
    public void changeWeaponIndex(int dx)
    {
        currWeaponIndex+=dx;
        if(currWeaponIndex >= allItems[WEAPONS].length)
        {
            currWeaponIndex = 0;
        }
        else if(currWeaponIndex < 0)
        {
            currWeaponIndex = allItems[WEAPONS].length-1;
        }
    }
    
    /**
     * Returns a ratio (value between 0 and 1) of the player's health to his
     * maximum health. Used to draw the health bar correctly.
     * 
     * @return A ratio of player's health to player's max health.
     */
    public double getHealthRatio()
    {
        return (double)health/maxHealth;
    }
    
     /**
     * Returns a ratio (value between 0 and 1) of the player's energy to his
     * maximum energy. Used to draw the energy bar correctly.
     * 
     * @return A ratio of player's energy to player's max energy.
     */
    public double getEnergyRatio()
    {
        return (double)energy/maxEnergy;
    }
    
     /**
     * Returns a ratio (value between 0 and 1) of the player's experience to his
     * maximum experience. Used to draw the experience bar correctly.
     * 
     * @return A ratio of player's experience to player's max experience.
     */
    public double getExperienceRatio()
    {
        return (double)xp/xpneeded;
    }
    
    /**
     * Checks to see if the test coordinate hits the player.
     * 
     * @param checkX The x-coordinate of the test point.
     * @param checkY The y-coordinate of the test point.
     * @return True if the player hits the point, false if not.
     */
    public boolean hits(int checkX, int checkY)
    {
        if(checkX > x-PLAYER_WIDTH/2 && checkX < x+PLAYER_WIDTH/2 && checkY > y-PLAYER_HEIGHT/2 && checkY < y+PLAYER_HEIGHT/2)
        {
            return true;
        }
        return false;
    }
}