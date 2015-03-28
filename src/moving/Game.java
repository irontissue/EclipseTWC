package moving;

import java.awt.AlphaComposite;
import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.Point;
import java.awt.RenderingHints;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import java.awt.event.KeyEvent;
import java.awt.event.KeyListener;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.awt.event.MouseMotionListener;
import java.awt.event.MouseWheelEvent;
import java.awt.event.MouseWheelListener;
import java.io.File;
import java.io.FileWriter;
import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Scanner;
import java.util.logging.Level;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.SwingUtilities;
import javax.swing.Timer;

/**
 * The game. Takes care of all updates and all components of the game, and draws
 * the game. Essentially a manager for the game, even though the Main class is
 * the one that initializes the program.
 * 
 * @author EclipseTWC
 */
public class Game extends JPanel
{
    public static Grid grid = new Grid(200,200);
    
    public static String DEFAULT_FONT;
    
    private int gridUpdateTimer = 0, updateAt = 2;
    private int typeSetter = 0;
    private int qtimer = 0;
    private int x = Player.PLAYER_X, y = Player.PLAYER_Y;
    public int mouseX = 0, mouseY = 0;
    public int totalXMove = 0, totalYMove = 0;
    private int miniMapZoom = 7;
    private int addEnemy = 0;
    private int energRegen = 0, healthRegen = 0;
    private int fIndex;
    private int inventoryY, inventoryX;
    public static int cameraX = -Main.FRAME_WIDTH/2, cameraY = -Main.FRAME_HEIGHT+12;
    private static int fps, fpsToDisplay;
    public static int WIDTH, HEIGHT;
    
    private double shootCounter = 0;
    
    private long fpsDispUpdate = 0;
    private long oldMillis = 0;
    
    private Background b;
    
    private boolean addShot = false;
    private boolean up, left, down, right, leftMouse, rightMouse;
    private boolean firstJump = false;
    private boolean upReleased = true;
    private boolean dead = false;
    private boolean menu = false;
    private boolean mousePressed = false;
    public boolean fade = false;
    private boolean init = true;
    private boolean trash = false;
    private boolean initDie = true;
    public boolean loadSavedGame = false;
    public static boolean soundPlayed = false;
    
    public static Player player;
    
    public ArrayList<Shot> shots = new ArrayList();
    public ArrayList<Enemy> enemies = new ArrayList();
    private ArrayList<Button> buttons = new ArrayList();
    public ArrayList<Item> itemsOnMap = new ArrayList();
    public ArrayList<TreasureChest> treasureChests = new ArrayList();
    public ArrayList<Door> doors = new ArrayList();
    public ArrayList<NPC> npcs = new ArrayList();
    
    public Message message = null;
    
    public Door currDoor;
    
    public Timer t;
    
    private Main m;
    
    private File saveFile;
    
    private String currMap;
    
    public static Graphics g;
    
    private Point highlightedInvCell = new Point(0,-1); //the first value is the arraylist in which the user has clicked on a cell,
                                                        //the second value is the index clicked in the arraylist.
                                                        //0 means inventory, 1 means weapons, 2 means abilities, 3 means armors.
    
    /**
     * Default constructor.
     * 
     * @param m             A copy of the main class.
     * @param saveFile      The File to which player save data will be saved.
     * @param fIndex        Represents the file chosen as an int (1, 2, or 3).
     * @param playerName    The player's name.
     */
    public Game(Main m, File saveFile, int fIndex, String playerName)
    {
        //System.out.println(Grid.CELL_SIZE);
        //ArrayList<Font> allFonts = (ArrayList) Arrays.asList(GraphicsEnvironment.getLocalGraphicsEnvironment().getAllFonts());
        this.m = m;
        this.saveFile = saveFile;
        this.fIndex = fIndex;
        player = new Player(playerName, Game.this);
        t = new Timer(16, new ActionListener()
        {

            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(init)
                {
                    //b = new Background(-cameraX, -cameraY+148, AssetManager.getImage("forest"), Background.STATIC);
                    if(Font.getFont("Tahoma") == null)
                    {
                        DEFAULT_FONT = "Tahoma";
                    }
                    else
                    {
                        DEFAULT_FONT = "Tahoma";
                    }
                    setFocusable(true);
                    if(loadSavedGame)
                    {
                        loadSaveGame();
                    }
                    else
                    {
                        readMap("tutorial1.map");
                    }
                    buttons.add(new Button(512, 18, 60, 30, "Menu", new Color(100,100,255), "Menu"));
                }
                soundPlayed = false;
                if(!fade)
                {
                    if(message != null)
                    {
                        if(message.getClass().equals(UnlockedMessage.class))
                        {
                            if(message.waitingForConfirm)
                            {
                                ((UnlockedMessage) message).timer ++;
                                if(((UnlockedMessage) message).timer >= 60)
                                {
                                    ((UnlockedMessage) message).timer = 0;
                                    message.update = true;
                                }
                            }
                            if(message.remove)
                            {
                                message = null;
                            }
                            if(!dead)
                            {
                                updateGame();
                            }
                            else if(Fade.reversing())
                            {
                                t.stop();
                                try{Thread.sleep(5000);}catch(Exception ex){ex.printStackTrace();}
                                Game.this.m.backToMenu();
                            }
                        }
                        if(message != null)
                        {
                            message.updateMessage(Game.this);
                        }
                    }
                    else
                    {
                        if(menu)
                        {
                            updateMenu();
                        }
                        else
                        {
                            if(!dead)
                            {
                                updateGame();
                            }
                            else if(Fade.reversing())
                            {
                                t.stop();
                                try{Thread.sleep(5000);}catch(Exception ex){ex.printStackTrace();}
                                Game.this.m.backToMenu();
                            }
                        }
                    }
                }
                player.updateStats();
                repaint();
                Sounds.run();
                if(init)
                {
                    init = false;
                    if(!loadSavedGame)
                    {
                        message = new Message("Welcome to Eclipse - The Waxing Crescent (Press SPACEBAR to continue). Press \"A\" to move left and \"D\" to move right. Make your way over to the person standing to the right, and press \"S\" to learn more information from him.", false, AssetManager.getImage("blank"));
                        message.update = true;
                    }
                    else
                    {
                        loadSavedGame = false;
                    }
                }
            }
            
        });
        t.start();
        addKeyListener(new KeyListener()
        {

            @Override
            public void keyTyped(KeyEvent e)
            {
                
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_0)
                {
                    //typeSetter = Cell.NULL;
                }
                /*if(e.getKeyCode() == KeyEvent.VK_1)
                {
                    typeSetter = Cell.VILLAGE_WOOD;
                }
                if(e.getKeyCode() == KeyEvent.VK_2)                 //THIS IS ALL DEVELOPER MODE STUFF
                {                                                   //LETS YOU DRAW ON THE MAP AND/OR SPAWN ENEMIES AND SUCH
                    typeSetter = Cell.WATER;
                }
                if(e.getKeyCode() == KeyEvent.VK_3)
                {
                    typeSetter = Cell.VILLAGE_STONE;
                }
                if(e.getKeyCode() == KeyEvent.VK_4)
                {
                    typeSetter = 1000;
                }
                if(e.getKeyCode() == KeyEvent.VK_5)
                {
                    typeSetter = 1001;
                }
                */
                if(e.getKeyCode() == KeyEvent.VK_T)
                {
                    trash = true;
                }
                else if(e.getKeyCode() == KeyEvent.VK_G) //G for Give!
                {
                    t.stop();
                    try
                    {
                        String s = JOptionPane.showInputDialog(null,"Enter the name of the item you want to add to the map.", "Item add", JOptionPane.QUESTION_MESSAGE);
                        Item it = AssetManager.getItem(s);
                        if(it != null)
                        {
                            it.isOnMap = true;
                            it.xOnMap = mouseX-cameraX;
                            it.yOnMap = mouseY-cameraY;
                            itemsOnMap.add(it);
                        }
                    }
                    catch (Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    t.start();
                }
                else if(e.getKeyCode() == KeyEvent.VK_6)
                {
                    t.stop();
                    try
                    {
                        String[] s = JOptionPane.showInputDialog(null, "Input name of enemy to add, then\na comma, then the number of that enemy.", "Enemy add", JOptionPane.QUESTION_MESSAGE).split(",");
                        boolean done = false;
                        double d = 1.0;
                        while(!done)
                        {
                            try
                            {
                                d = Double.parseDouble(JOptionPane.showInputDialog(null, "Input the difficulty of the enemy. This is a\nvalue from 1.0-5.0.", "Enemy add", JOptionPane.QUESTION_MESSAGE));
                                if(d < 1.0)
                                {
                                    d = 1.0;
                                }
                                else if(d > 5.0)
                                {
                                    d = 5.0;
                                }
                                done = true;
                            }
                            catch(Exception ex){}
                        }
                        for(int i = 0; i < Integer.parseInt(s[1].trim()); i++)
                        {
                            addEnemy(s[0], d);
                        }
                    }
                    catch(Exception ex)
                    {
                        ex.printStackTrace();
                    }
                    t.start();
                }
                else if(e.getKeyCode() == KeyEvent.VK_SPACE)
                {
                    if(message != null)
                    {
                        if(message.getClass().equals(Message.class))
                        {
                            if(message.remove)
                            {
                                message = null;
                            }
                            else
                            {
                                if(!message.update)
                                {
                                    message.update = true;
                                }
                                else
                                {
                                    while(message.update)
                                    {
                                        message.updateMessage(Game.this);
                                    }
                                }
                            }
                        }
                        else
                        {
                            if(message.remove)
                            {
                                message = null;
                            }
                            if(upReleased)
                            {
                                up = true;
                            }
                        }
                    }
                    else
                    {
                        if(upReleased)
                        {
                            up = true;
                        }
                    }
                }
                else if(e.getKeyCode() == KeyEvent.VK_A)
                {
                    if(message == null || message.getClass().equals(UnlockedMessage.class))
                    {
                        left = true;
                    }
                }
                else if(e.getKeyCode() == KeyEvent.VK_D)
                {
                    if(message == null || message.getClass().equals(UnlockedMessage.class))
                    {
                        right = true;
                    }
                }
                else if(e.getKeyCode() == KeyEvent.VK_S && message == null && !menu) //W is the action button. Can be used to pick up stuff, talk to people, open treasure chests, etc.
                {
                    boolean inter = false;
                    down = true;
                    for(Door d : doors)
                    {
                        if(player.hits(d.x+d.img.getWidth(null)/2, d.y+d.img.getHeight(null)/2))
                        {
                            currDoor = d;
                            fade = true;
                        }
                    }
                    for(NPC n : npcs)
                    {
                        if(player.hits((int)Math.round(n.x)+n.img.getWidth(null)/2, (int)Math.round(n.y)+n.img.getHeight(null)/2))
                        {
                            Sounds.addAudio(AssetManager.getSoundEffect("talk"));
                            message = n.getMessage(Game.this);
                            message.update = true;
                        }
                    }
                    for(int i = 0; i < treasureChests.size(); i++)
                    {
                        TreasureChest tc = treasureChests.get(i);
                        if(!tc.opened && player.hits((int)tc.xOnMap+tc.img.getWidth(null)/2, (int)tc.yOnMap+tc.img.getHeight(null)/2))
                        {
                            if(tc.items != null)
                            for(Item it: tc.items)
                            {
                                if(it!=null)
                                {
                                    it.xOnMap = tc.xOnMap;
                                    it.yOnMap = tc.yOnMap-it.img.getHeight(null)+tc.img.getHeight(null)-5;
                                    itemsOnMap.add(it);
                                }
                            }
                            tc.makeOpened();
                            inter = true;
                        }
                    }
                    for(int i = 0; i < itemsOnMap.size(); i++)
                    {
                        itemsOnMap.get(i).updatePosition(Game.this);
                        Item it = itemsOnMap.get(i);
                        if(!inter && player.hits((int)it.xOnMap+it.img.getWidth(null)/2, (int)it.yOnMap+it.img.getHeight(null)/2))
                        {
                            for(int j = 0; j < player.allItems[Player.INVENTORY].length; j++)
                            {
                                if(player.allItems[Player.INVENTORY][j] == null)
                                {
                                    player.allItems[Player.INVENTORY][j] = it;
                                    itemsOnMap.remove(i);
                                    i--;
                                    j = player.allItems[Player.INVENTORY].length;
                                }
                            }
                        }
                    }
                }
                else if(e.getKeyCode() == KeyEvent.VK_ESCAPE)
                {
                    player.xp = player.xpneeded;
                }
                else if(e.getKeyCode() == KeyEvent.VK_Q)
                {
                    /*if(qtimer < 0) //IMPLEMENTED QQ RAGE QUIT. Disabled because it is annoying.
                    {
                        qtimer = 7;
                    }
                    else
                    {
                        System.exit(0);
                    }*/
                    if(player.allItems[Player.ABILITIES][0] != null)
                    {
                        Ability ab = (Ability) player.allItems[Player.ABILITIES][0];
                        ab.activate(Game.this);
                    }
                }
                else if(e.getKeyCode() == KeyEvent.VK_W)
                {
                    if(player.allItems[Player.ABILITIES][1] != null)
                    {
                        Ability ab = (Ability) player.allItems[Player.ABILITIES][1];
                        ab.activate(Game.this);
                    }
                }
                else if(e.getKeyCode() == KeyEvent.VK_E)
                {
                    if(player.allItems[Player.ABILITIES][2] != null)
                    {
                        Ability ab = (Ability) player.allItems[Player.ABILITIES][2];
                        ab.activate(Game.this);
                    }
                }
                else if(e.getKeyCode() == KeyEvent.VK_I)
                {
                    readMap();
                }
                else if(e.getKeyCode() == KeyEvent.VK_1)
                {
                    if(!menu && player.allItems[Player.POTIONS][0] != null)
                    {
                        PotionJar p = (PotionJar) player.allItems[Player.POTIONS][0];
                        potionGet(p);
                    }
                }
                else if(!menu && e.getKeyCode() == KeyEvent.VK_2)
                {
                    if(player.allItems[Player.POTIONS][1] != null)
                    {
                        PotionJar p = (PotionJar) player.allItems[Player.POTIONS][1];
                        potionGet(p);
                    }
                }
                else if(!menu && e.getKeyCode() == KeyEvent.VK_3)
                {
                    if(player.allItems[Player.POTIONS][2] != null)
                    {
                        PotionJar p = (PotionJar) player.allItems[Player.POTIONS][2];
                        potionGet(p);
                    }
                }
                else if(e.getKeyCode() == KeyEvent.VK_SHIFT)
                {
                    if(message == null)
                    {
                        menuSwitch();
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_A)
                {
                    left = false;
                }
                else if(e.getKeyCode() == KeyEvent.VK_D)
                {
                    right = false;
                }
                else if(e.getKeyCode() == KeyEvent.VK_SPACE)
                {
                    up = false;
                    upReleased = true;
                }
                else if(e.getKeyCode() == KeyEvent.VK_S)
                {
                    down = false;
                }
                else if(e.getKeyCode() == KeyEvent.VK_T)
                {
                    trash = false;
                }
            }
            
        });
        addMouseListener(new MouseListener()
        {

            public void mouseClicked(MouseEvent e)
            {
                
            }

            public void mousePressed(MouseEvent e)
            {
                if(e.getY() > 147)
                {
                    if(SwingUtilities.isLeftMouseButton(e))
                    {
                        leftMouse = true;
                    }
                    else if(SwingUtilities.isRightMouseButton(e))
                    {
                        rightMouse = true;
                    }
                    /*if(typeSetter<1000)  //MODIFIED DUE TO TAKE OUT DEVELOPER MODE.
                    {
                        if((e.getX()-cameraX)/Grid.CELL_SIZE >= 0 && (e.getX()-cameraX)/Grid.CELL_SIZE < grid.cells.length &&
                                (e.getY()-cameraY)/Grid.CELL_SIZE >= 0 && (e.getY()-cameraY)/Grid.CELL_SIZE < grid.cells[0].length)
                        {
                            int gX = (int)(e.getX()-cameraX)/Grid.CELL_SIZE;
                            int gY = (int)(e.getY()-cameraY)/Grid.CELL_SIZE;
                            if(gX >= 0 && gY >= 0 && gX < grid.cells.length && gY < grid.cells[0].length)
                            {
                                grid.cells[gX][gY].setType(typeSetter);
                            }
                        }
                    }
                    else if(typeSetter == 1000)
                    {
                        addShot = true;
                    }
                    else if(typeSetter == 1001)
                    {
                        poop(e.getX()-cameraX, e.getY()-cameraY);
                    }*/
                }
                else
                {
                    addShot = false;
                }
                if(!buttons.isEmpty())
                {
                    for(int i = 0; i < buttons.size(); i++)
                    {
                        if(buttons.get(i).hits(e.getX()-cameraX, e.getY()-cameraY))
                        {
                            buttons.get(i).pressed = true;
                        }
                    }
                }
                mousePressed = true;
            }

            public void mouseReleased(MouseEvent e)
            {
                if(SwingUtilities.isLeftMouseButton(e))
                {
                    leftMouse = false;
                }
                else if(SwingUtilities.isRightMouseButton(e))
                {
                    rightMouse = false;
                }
                if(!buttons.isEmpty())
                {
                    for(int i = 0; i < buttons.size(); i++)
                    {
                        if(buttons.get(i).hits(e.getX()-cameraX, e.getY()-cameraY) && buttons.get(i).pressed)
                        {
                            buttons.get(i).pressed = false;
                            if((buttons.get(i).getID().equals("Close") || buttons.get(i).getID().equals("Menu")) && message == null)
                            {
                                menuSwitch();
                                i--;
                            }
                            else if(buttons.get(i).getID().equals("Main Menu"))
                            {
                                int ch = JOptionPane.showConfirmDialog(null, "Are you sure you want to go back to the main\nmenu? Be sure to save your game first.", "", JOptionPane.OK_CANCEL_OPTION);
                                if(ch == 0)
                                {
                                    Fade.fade();
                                }
                            }
                            else if(buttons.get(i).getID().equals("Save Game"))
                            {
                                saveGame();
                                message = new Message("Game saved successfully!", false, AssetManager.getImage("blank"));
                                message.update = true;
                            }
                            else if(player.levelUpPoints > 0)
                            {
                                if(buttons.get(i).getID().equals("staplus"))
                                {
                                    player.stamina++;
                                    if(player.stamina > 50)
                                    {
                                        player.stamina = 50;
                                    }
                                    else
                                        player.levelUpPoints--;
                                }
                                else if(buttons.get(i).getID().equals("potplus"))
                                {
                                    player.potential++;
                                    if(player.potential > 50)
                                    {
                                        player.potential = 50;
                                    }
                                    else
                                        player.levelUpPoints--;
                                }
                                else if(buttons.get(i).getID().equals("defplus"))
                                {
                                    player.defense++;
                                    if(player.defense > 50)
                                    {
                                        player.defense = 50;
                                    }
                                    else
                                        player.levelUpPoints--;
                                }
                                else if(buttons.get(i).getID().equals("strplus"))
                                {
                                    player.strength++;
                                    if(player.strength > 50)
                                    {
                                        player.strength = 50;
                                    }
                                    else
                                        player.levelUpPoints--;
                                }
                                else if(buttons.get(i).getID().equals("spdplus"))
                                {
                                    player.speed++;
                                    if(player.speed > 50)
                                    {
                                        player.speed = 50;
                                    }
                                    else
                                        player.levelUpPoints--;
                                }
                                else if(buttons.get(i).getID().equals("intplus"))
                                {
                                    player.intelligence++;
                                    if(player.intelligence > 50)
                                    {
                                        player.intelligence = 50;
                                    }
                                    else
                                        player.levelUpPoints--;
                                }
                                else if(buttons.get(i).getID().equals("lckplus"))
                                {
                                    player.luck++;
                                    if(player.luck > 50)
                                    {
                                        player.luck = 50;
                                    }
                                    else
                                        player.levelUpPoints--;
                                }
                            }
                        }
                    }
                }
            }

            public void mouseEntered(MouseEvent e)
            {
                
            }

            public void mouseExited(MouseEvent e)
            {
                
            }
            
        });
        addMouseMotionListener(new MouseMotionListener()
        {

            public void mouseDragged(MouseEvent e)
            {
                if(e.getY() > 180)
                {
                    if(SwingUtilities.isLeftMouseButton(e))
                    {
                        leftMouse = true;
                    }
                    else if(SwingUtilities.isRightMouseButton(e))
                    {
                        rightMouse = true;
                    }
                    /*if(typeSetter<1000)
                    {
                        if((e.getX()-cameraX)/Grid.CELL_SIZE >= 0 && (e.getX()-cameraX)/Grid.CELL_SIZE < grid.cells.length &&
                                (e.getY()-cameraY)/Grid.CELL_SIZE >= 0 && (e.getY()-cameraY)/Grid.CELL_SIZE < grid.cells[0].length)
                        {
                            int gX = (int)(e.getX()-cameraX)/Grid.CELL_SIZE;
                            int gY = (int)(e.getY()-cameraY)/Grid.CELL_SIZE;
                            if(gX >= 0 && gY >= 0 && gX < grid.cells.length && gY < grid.cells[0].length)
                            {
                                grid.cells[gX][gY].setType(typeSetter);
                            }
                        }
                    }
                    else if(typeSetter == 1000)
                    {
                        addShot = true;
                    }
                    else if(typeSetter == 1001)
                    {
                        poop(e.getX()-cameraX, e.getY()-cameraY);
                    }*/
                }
                else
                {
                    addShot = false;
                }
                mouseX = e.getX();
                mouseY = e.getY();
            }

            public void mouseMoved(MouseEvent e)
            {
                mouseX = e.getX();
                mouseY = e.getY();
            }
            
        });
        addMouseWheelListener(new MouseWheelListener()
        {

            public void mouseWheelMoved(MouseWheelEvent e)
            {
                /*player.changeWeaponIndex(Math.abs(e.getWheelRotation())/-e.getWheelRotation());
                miniMapZoom+=Math.abs(e.getWheelRotation())/e.getWheelRotation();
                if(miniMapZoom <= 3)
                {
                    miniMapZoom = 4;
                }
                else if(miniMapZoom > 30)
                {
                    miniMapZoom = 30;
                }*/
                int change = Math.abs(e.getWheelRotation())/-e.getWheelRotation();
                player.changeWeaponIndex(change);
                if(change > 0)
                {
                    for(int i = player.currWeaponIndex; i < player.allItems[Player.WEAPONS].length; i++)
                    {
                        if(player.allItems[Player.WEAPONS][i] != null)
                        {
                            player.currWeaponIndex = i;
                            i = player.allItems[Player.WEAPONS].length;
                        }
                    }
                    if(player.allItems[Player.WEAPONS][player.currWeaponIndex] == null)
                    {
                        for(int i = 0; i < player.currWeaponIndex; i++)
                        {
                            if(player.allItems[Player.WEAPONS][i] != null)
                            {
                                player.currWeaponIndex = i;
                                i = player.allItems[Player.WEAPONS].length;
                            }
                        }
                    }
                    if(player.allItems[Player.WEAPONS][player.currWeaponIndex] == null)
                    {
                        player.currWeaponIndex = 0;
                    }
                }
                else
                {
                    for(int i = player.currWeaponIndex; i >= 0; i--)
                    {
                        if(player.allItems[Player.WEAPONS][i] != null)
                        {
                            player.currWeaponIndex = i;
                            i = -1;
                        }
                    }
                    if(player.allItems[Player.WEAPONS][player.currWeaponIndex] == null)
                    {
                        for(int i = player.allItems[Player.WEAPONS].length-1; i > player.currWeaponIndex; i--)
                        {
                            if(player.allItems[Player.WEAPONS][i] != null)
                            {
                                player.currWeaponIndex = i;
                                i = -1;
                            }
                        }
                    }
                    if(player.allItems[Player.WEAPONS][player.currWeaponIndex] == null)
                    {
                        player.currWeaponIndex = 0;
                    }
                }
                shootCounter = 0;
            }
            
        });
    }
    
    /**
     * Draws all elements of the game. Although most calculations are carried
     * out in the updateGame() and updateMenu() methods, some calculations
     * must be carried out in this method, too.
     * 
     * @param g The object on which the graphics is drawn.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        try
        {
            WIDTH = getWidth();
            HEIGHT = getHeight();
            this.g = g;
            Graphics2D g2d = (Graphics2D) g;
            g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
            g.clearRect(0, 0, getWidth(), getHeight());
            g.setFont(new Font(DEFAULT_FONT, Font.PLAIN, 13));
            g.translate(cameraX, cameraY);
            b.drawBackground(g);
            for(int i = 0; i < shots.size(); i++)
            {
                shots.get(i).drawShot(g);
            }
            for(int i = 0; i < npcs.size(); i++)
            {
                npcs.get(i).drawNPC(g);
            }
            for(int i = 0; i < treasureChests.size(); i++)
            {
                TreasureChest tc = treasureChests.get(i);
                g.drawImage(tc.img, (int)tc.xOnMap, (int)tc.yOnMap, null);
            }
            for(int i = 0; i < enemies.size(); i++)
            {
                enemies.get(i).drawEnemy(g);
            }
            player.drawPlayer(g);
            grid.drawGrid(g);
            for(int i = 0; i < doors.size(); i++)
            {
                Door d = doors.get(i);
                g.drawImage(d.img, d.x, d.y, null);
            }
            for(int i = 0; i < itemsOnMap.size(); i++)
            {
                Item it = itemsOnMap.get(i);
                if(it.removeTimer > it.removeAt)
                {
                    itemsOnMap.remove(i);
                    i--;
                }
                else
                {
                    float alphadraw = (float)(10.0*(it.removeAt-it.removeTimer)/(it.removeAt));
                    if(alphadraw > 1)
                    {
                        alphadraw = 1;
                    }
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, alphadraw));
                    if(it instanceof PotionJar)
                    {
                        PotionJar p = (PotionJar) it;
                        if(p.type == PotionJar.HEALTH)
                        {
                            g.setColor(Color.RED);
                        }
                        else
                        {
                            g.setColor(Color.GREEN);
                        }
                        g.fillRect((int)p.xOnMap, (int)(p.yOnMap+(p.img.getHeight(null)-p.img.getHeight(null)*p.getRatio())), p.img.getWidth(null), (int)(p.img.getHeight(null)*p.getRatio()));
                    }
                    g2d.drawImage(it.img, (int)it.xOnMap, (int)it.yOnMap, null);
                    g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
                    if(!menu && mouseX-cameraX > it.xOnMap && mouseX-cameraX < it.xOnMap+it.img.getWidth(null) &&
                            mouseY-cameraY > it.yOnMap && mouseY-cameraY < it.yOnMap+it.img.getHeight(null))
                    {
                        it.drawItemDescription(g, mouseX-cameraX, mouseY-cameraY);
                    }
                }
            }
            g2d.setComposite(AlphaComposite.getInstance(AlphaComposite.SRC_OVER, 1f));
            g.setFont(new Font(DEFAULT_FONT, Font.PLAIN, 13));
            g.setFont(new Font(DEFAULT_FONT, Font.PLAIN, 13));
            g.setColor(Color.BLACK);
            g.fillRect(-cameraX, -cameraY, getWidth(), 148);
            g.setColor(new Color(150,150,150));
            g.fillRect(-cameraX+10, -cameraY+10, getWidth()-20, 128);
            g.setColor(Color.BLUE);
            g.drawString("Level: " + player.level, -cameraX+22, -cameraY+28);
            g.setColor(Color.BLACK);
            g.drawString("Experience:", -cameraX+22, -cameraY+52);
            g.fillRect(-cameraX+22, -cameraY+56, 180, 20);
            g.drawString("Health:", -cameraX+207, -cameraY+52);
            g.fillRect(-cameraX+207, -cameraY+56, 180, 20);
            g.drawString("Energy:", -cameraX+392, -cameraY+52);
            g.fillRect(-cameraX+392, -cameraY+56, 180, 20);
            g.setColor(Color.BLUE.brighter().brighter());
            g.fillRect(-cameraX+22, -cameraY+56, (int)(180*player.getExperienceRatio()), 20);
            g.setColor(Color.RED);
            g.fillRect(-cameraX+207, -cameraY+56, (int)(180*player.getHealthRatio()), 20);
            g.setColor(Color.GREEN);
            g.fillRect(-cameraX+392, -cameraY+56, (int)(180*player.getEnergyRatio()), 20);
            g.setColor(Color.BLACK);
            /*DecimalFormat df = new DecimalFormat();
            df.applyPattern("0.0#");
            g.drawString("Moving speed: " + df.format(player.xSpeed), -cameraX+390, -cameraY+30);
            g.drawString("Shooting speed: " + df.format(player.shootSpeed), -cameraX+390, -cameraY+45);
            g.drawString("Health regen rate: " + df.format(player.healthRegen), -cameraX+390, -cameraY+60);
            g.drawString("Energy regen rate: " + df.format(player.energyRegen), -cameraX+390, -cameraY+75);*/
            g.drawString("FPS: " + fpsToDisplay, -cameraX+380, -cameraY+140);
            g.setColor(Color.WHITE);
            String xp = player.xp+"/"+player.xpneeded;
            String health = (int) (player.health)+"/"+(int)player.maxHealth;
            String energy = (int) (player.energy)+"/"+(int)player.maxEnergy;
            //if(mouseX >= 180 && mouseX <= 360)
            //{
                //if(mouseY >= 56 && mouseY <= 76)
                //{
                    g.drawString(xp, -cameraX+109-(int)Math.round(xp.length()*3.3), -cameraY+71);
                //}
                //else if(mouseY >= 100 && mouseY <= 120)
                //{
                    g.drawString(health, -cameraX+295-(int)Math.round(health.length()*3.3), -cameraY+71);
                //}
                //else if(mouseY >= 140 && mouseY <= 160)
                //{
                    g.drawString(energy, -cameraX+480-(int)Math.round(energy.length()*3.3), -cameraY+71);
                //}
            //}
            //drawMiniMap(g, -cameraX+20, -cameraY+20, 140);
            int mouseOnCellX = -100000;
            int mouseOnCellY = -100000;
            Point pressedIndex = new Point(0, -100000);
            Point indexOfMouse = new Point(0, -100000);
            g.setColor(new Color(0,0,0,50));
            if(mouseX >= 22 && mouseX < 22+Player.INVENTORY_CELL_SIZE*6 &&
                        mouseY >= 96 && mouseY < 96+Player.INVENTORY_CELL_SIZE)
            { //weapons
                mouseOnCellX = (mouseX+Player.INVENTORY_CELL_SIZE-22)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-22);
                mouseOnCellY = (mouseY+Player.INVENTORY_CELL_SIZE-96)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-96);
                g.fillRect(mouseOnCellX-cameraX, mouseOnCellY-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                indexOfMouse = new Point(1, (mouseOnCellX-22)/Player.INVENTORY_CELL_SIZE);
                if(mousePressed)
                {
                    pressedIndex = indexOfMouse;
                }
            }
            else if(mouseX >= Player.INVENTORY_CELL_SIZE*6+52 && mouseX < 52+Player.INVENTORY_CELL_SIZE*9 &&
                    mouseY >= 96 && mouseY < 96+Player.INVENTORY_CELL_SIZE)
            { //abilities
                mouseOnCellX = (mouseX+Player.INVENTORY_CELL_SIZE-52)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-52);
                mouseOnCellY = (mouseY+Player.INVENTORY_CELL_SIZE-96)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-96);
                g.fillRect(mouseOnCellX-cameraX, mouseOnCellY-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                indexOfMouse = new Point(2, (mouseOnCellX-Player.INVENTORY_CELL_SIZE*6-52)/Player.INVENTORY_CELL_SIZE);
                if(mousePressed)
                {
                    pressedIndex = indexOfMouse;
                }
            }
            else if(mouseX >= Player.INVENTORY_CELL_SIZE*9+82 && mouseX < 82+Player.INVENTORY_CELL_SIZE*10 &&
                    mouseY >= 96 && mouseY < 96+Player.INVENTORY_CELL_SIZE)
            { //head slot
                mouseOnCellX = (mouseX+Player.INVENTORY_CELL_SIZE-82)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-82);
                mouseOnCellY = (mouseY+Player.INVENTORY_CELL_SIZE-96)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-96);
                g.fillRect(mouseOnCellX-cameraX, mouseOnCellY-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                indexOfMouse = new Point(3, (mouseOnCellX-Player.INVENTORY_CELL_SIZE*9-82)/Player.INVENTORY_CELL_SIZE);
                if(mousePressed)
                {
                    pressedIndex = indexOfMouse;
                }
            }
            else if(mouseX >= Player.INVENTORY_CELL_SIZE*10+86 && mouseX < 86+Player.INVENTORY_CELL_SIZE*11 &&
                    mouseY >= 96 && mouseY < 96+Player.INVENTORY_CELL_SIZE)
            { //body slot
                mouseOnCellX = (mouseX+Player.INVENTORY_CELL_SIZE-86)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-86);
                mouseOnCellY = (mouseY+Player.INVENTORY_CELL_SIZE-96)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-96);
                g.fillRect(mouseOnCellX-cameraX, mouseOnCellY-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                indexOfMouse = new Point(4, (mouseOnCellX-Player.INVENTORY_CELL_SIZE*10-86)/Player.INVENTORY_CELL_SIZE);
                if(mousePressed)
                {
                    pressedIndex = indexOfMouse;
                }
            }
            else if(mouseX >= Player.INVENTORY_CELL_SIZE*11+90 && mouseX < 90+Player.INVENTORY_CELL_SIZE*12 &&
                    mouseY >= 96 && mouseY < 96+Player.INVENTORY_CELL_SIZE)
            { //foot slot
                mouseOnCellX = (mouseX+Player.INVENTORY_CELL_SIZE-90)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-90);
                mouseOnCellY = (mouseY+Player.INVENTORY_CELL_SIZE-96)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-96);
                g.fillRect(mouseOnCellX-cameraX, mouseOnCellY-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                indexOfMouse = new Point(5, (mouseOnCellX-Player.INVENTORY_CELL_SIZE*11-90)/Player.INVENTORY_CELL_SIZE);
                if(mousePressed)
                {
                    pressedIndex = indexOfMouse;
                }
            }
            else if(mouseX >= Player.INVENTORY_CELL_SIZE*12+121 && mouseX < 121+Player.INVENTORY_CELL_SIZE*15 &&
                    mouseY >= 96 && mouseY < 96+Player.INVENTORY_CELL_SIZE)
            { //potions
                mouseOnCellX = (mouseX+Player.INVENTORY_CELL_SIZE-121)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-121);
                mouseOnCellY = (mouseY+Player.INVENTORY_CELL_SIZE-96)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-96);
                g.fillRect(mouseOnCellX-cameraX, mouseOnCellY-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                indexOfMouse = new Point(6, (mouseOnCellX-Player.INVENTORY_CELL_SIZE*12-121)/Player.INVENTORY_CELL_SIZE);
                if(mousePressed)
                {
                    pressedIndex = indexOfMouse;
                }
            }
            if(menu)
            {
                g.setColor(new Color(150,150,150,225));
                g.fillRect(-cameraX, -cameraY+148, getWidth(), getHeight()-148);
                g.setColor(Color.BLACK);
                g.drawString("Stat Points Remaining: " + player.levelUpPoints, 370-cameraX, 335-cameraY);
                if(player.stamina > 49)
                {
                    g.setColor(Color.YELLOW);
                }
                g.drawString("Stamina: " + (int)player.stamina, 370-cameraX, 360-cameraY);
                g.setColor(Color.BLACK);
                if(player.potential > 49)
                {
                    g.setColor(Color.YELLOW);
                }
                g.drawString("Potential: " + (int)player.potential, 370-cameraX, 385-cameraY);
                g.setColor(Color.BLACK);
                if(player.defense > 49)
                {
                    g.setColor(Color.YELLOW);
                }
                g.drawString("Defense: " + (int)player.defense, 370-cameraX, 410-cameraY);
                g.setColor(Color.BLACK);
                if(player.strength > 49)
                {
                    g.setColor(Color.YELLOW);
                }
                g.drawString("Strength: " + (int)player.strength, 370-cameraX, 435-cameraY);
                g.setColor(Color.BLACK);
                if(player.speed > 49)
                {
                    g.setColor(Color.YELLOW);
                }
                g.drawString("Speed: " + (int)player.speed, 485-cameraX, 360-cameraY);
                g.setColor(Color.BLACK);
                if(player.intelligence > 49)
                {
                    g.setColor(Color.YELLOW);
                }
                g.drawString("Intelligence: " + (int)player.intelligence, 485-cameraX, 385-cameraY);
                g.setColor(Color.BLACK);
                if(player.luck > 49)
                {
                    g.setColor(Color.YELLOW);
                }
                g.drawString("Luck: " + (int)player.luck, 485-cameraX, 410-cameraY);
                //draw the inventory grid:
                inventoryY = -cameraY+200;
                inventoryX = -cameraX+20;
                for(int i = 0; i < player.allItems[Player.INVENTORY].length; i++)
                {
                    g.setColor(Color.BLACK);
                    g.drawRect(inventoryX, inventoryY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                    Item it = player.allItems[Player.INVENTORY][i];
                    if(it != null && it.img != null)
                    {
                        try
                        {
                            PotionJar p = (PotionJar) it;
                            if(p.img.getWidth(null) > Player.INVENTORY_CELL_SIZE)
                            {
                                int adjustedHeight = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/p.img.getWidth(null))*p.img.getHeight(null));
                                if(p.type == PotionJar.HEALTH)
                                {
                                    g.setColor(Color.RED);
                                }
                                else
                                {
                                    g.setColor(Color.GREEN);
                                }
                                g.fillRect(inventoryX, inventoryY+(Player.INVENTORY_CELL_SIZE-adjustedHeight)/2+(int)Math.round(adjustedHeight*(1-p.getRatio())), Player.INVENTORY_CELL_SIZE, (int) Math.round(adjustedHeight*p.getRatio()));
                                g.drawImage(p.img, inventoryX, inventoryY+(Player.INVENTORY_CELL_SIZE-adjustedHeight)/2, Player.INVENTORY_CELL_SIZE, adjustedHeight, null);
                            }
                            else if(p.img.getHeight(null) > Player.INVENTORY_CELL_SIZE)
                            {
                                int adjustedWidth = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/p.img.getHeight(null))*p.img.getWidth(null));
                                if(p.type == PotionJar.HEALTH)
                                {
                                    g.setColor(Color.RED);
                                }
                                else
                                {
                                    g.setColor(Color.GREEN);
                                }
                                g.fillRect(inventoryX+(Player.INVENTORY_CELL_SIZE-adjustedWidth)/2, inventoryY+(int)Math.round(Player.INVENTORY_CELL_SIZE*(1-p.getRatio())), adjustedWidth, (int) Math.round(Player.INVENTORY_CELL_SIZE*p.getRatio()));
                                g.drawImage(p.img, inventoryX+(Player.INVENTORY_CELL_SIZE-adjustedWidth)/2, inventoryY, adjustedWidth, Player.INVENTORY_CELL_SIZE, null);
                            }
                            else
                            {
                                if(p.type == PotionJar.HEALTH)
                                {
                                    g.setColor(Color.RED);
                                }
                                else
                                {
                                    g.setColor(Color.GREEN);
                                }
                                g.fillRect(inventoryX+(Player.INVENTORY_CELL_SIZE-p.img.getWidth(null))/2, inventoryY+(Player.INVENTORY_CELL_SIZE-p.img.getHeight(null))/2+(int)Math.round(p.img.getHeight(null)*(1-p.getRatio())), p.img.getWidth(null), (int)Math.round(p.img.getHeight(null)*p.getRatio()));
                                g.drawImage(p.img, inventoryX+(Player.INVENTORY_CELL_SIZE-p.img.getWidth(null))/2, inventoryY+(Player.INVENTORY_CELL_SIZE-p.img.getHeight(null))/2, null);
                            }
                        }
                        catch (Exception e)
                        {
                            if(player.allItems[Player.INVENTORY][i].img.getWidth(null) > Player.INVENTORY_CELL_SIZE)
                            {
                                int adjustedHeight = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/player.allItems[Player.INVENTORY][i].img.getWidth(null))*player.allItems[Player.INVENTORY][i].img.getHeight(null));
                                g.drawImage(player.allItems[Player.INVENTORY][i].img, inventoryX, inventoryY+(Player.INVENTORY_CELL_SIZE-adjustedHeight)/2, Player.INVENTORY_CELL_SIZE, adjustedHeight, null);
                            }
                            else if(player.allItems[Player.INVENTORY][i].img.getHeight(null) > Player.INVENTORY_CELL_SIZE)
                            {
                                int adjustedWidth = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/player.allItems[Player.INVENTORY][i].img.getHeight(null))*player.allItems[Player.INVENTORY][i].img.getWidth(null));
                                g.drawImage(player.allItems[Player.INVENTORY][i].img, inventoryX+(Player.INVENTORY_CELL_SIZE-adjustedWidth)/2, inventoryY, adjustedWidth, Player.INVENTORY_CELL_SIZE, null);
                            }
                            else
                            {
                                g.drawImage(player.allItems[Player.INVENTORY][i].img, inventoryX+(Player.INVENTORY_CELL_SIZE-player.allItems[Player.INVENTORY][i].img.getWidth(null))/2, inventoryY+(Player.INVENTORY_CELL_SIZE-player.allItems[Player.INVENTORY][i].img.getHeight(null))/2, null);
                            }
                        }
                    }
                    inventoryX+=Player.INVENTORY_CELL_SIZE;
                    if((i+1)%10 == 0)
                    {
                        inventoryX = -cameraX+20;
                        inventoryY+=Player.INVENTORY_CELL_SIZE;
                    }
                }
                g.setColor(new Color(0,0,0,50));
                if(mouseX >= 20 && mouseX < Player.INVENTORY_CELL_SIZE*10+20 && mouseY >= 200 && mouseY < Player.INVENTORY_CELL_SIZE*10+200)
                { //inventory
                    mouseOnCellX = (mouseX+Player.INVENTORY_CELL_SIZE-20)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-20);
                    mouseOnCellY = (mouseY+Player.INVENTORY_CELL_SIZE-20)/Player.INVENTORY_CELL_SIZE*Player.INVENTORY_CELL_SIZE-(Player.INVENTORY_CELL_SIZE-20);
                    g.fillRect(mouseOnCellX-cameraX, mouseOnCellY-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                    indexOfMouse = new Point(0, (mouseOnCellY-200)/Player.INVENTORY_CELL_SIZE*10+mouseOnCellX/Player.INVENTORY_CELL_SIZE);
                    if(mousePressed)
                    {
                        pressedIndex = indexOfMouse;
                    }
                }
                if(pressedIndex.y >= 0)
                {
                    if(trash)
                    {
                        Item it = player.allItems[pressedIndex.x][pressedIndex.y];
                        if(it != null)
                        {
                            int xRand = (int)(Math.random()*21)-10;
                            int yRand = (int)(Math.random()*21)-10;
                            it.xOnMap = player.x-it.img.getWidth(null)/2+xRand;
                            it.yOnMap = player.y-Player.PLAYER_HEIGHT-it.img.getHeight(null)/2+yRand;
                            it.grav = -(Math.random()*3)-3;
                            it.xSpeed = (Math.random()*7)-3;
                            it.isOnMap = true;
                            itemsOnMap.add(it);
                            player.allItems[pressedIndex.x][pressedIndex.y] = null;
                        }
                    }
                    else
                    {
                        if(highlightedInvCell.equals(pressedIndex))
                        {
                            highlightedInvCell.y = -1;
                        }
                        else
                        {
                            if(highlightedInvCell.y > -1)
                            {
                                if(player.allItems[highlightedInvCell.x][highlightedInvCell.y] == null && player.allItems[pressedIndex.x][pressedIndex.y] == null)
                                {
                                    highlightedInvCell = pressedIndex;
                                }
                                else if(player.allItems[highlightedInvCell.x][highlightedInvCell.y] == null)
                                {

                                    Item i = player.allItems[highlightedInvCell.x][highlightedInvCell.y];
                                    Item j = player.allItems[pressedIndex.x][pressedIndex.y];
                                    try
                                    {
                                        player.allItems[highlightedInvCell.x][highlightedInvCell.y] = j;
                                        player.allItems[pressedIndex.x][pressedIndex.y] = i;
                                    }
                                    catch (Exception e)
                                    {
                                        player.allItems[highlightedInvCell.x][highlightedInvCell.y] = i;
                                        player.allItems[pressedIndex.x][pressedIndex.y] = j;
                                    }
                                    highlightedInvCell.y = -1;
                                }
                                else if(player.allItems[pressedIndex.x][pressedIndex.y] == null)
                                {

                                    Item i = player.allItems[highlightedInvCell.x][highlightedInvCell.y];
                                    Item j = player.allItems[pressedIndex.x][pressedIndex.y];
                                    try
                                    {
                                        player.allItems[highlightedInvCell.x][highlightedInvCell.y] = j;
                                        player.allItems[pressedIndex.x][pressedIndex.y] = i;
                                    }
                                    catch (Exception e)
                                    {
                                        player.allItems[highlightedInvCell.x][highlightedInvCell.y] = i;
                                        player.allItems[pressedIndex.x][pressedIndex.y] = j;
                                    }
                                    highlightedInvCell.y = -1;
                                }
                                else 
                                {
                                    Item i = player.allItems[highlightedInvCell.x][highlightedInvCell.y];
                                    Item j = player.allItems[pressedIndex.x][pressedIndex.y];
                                    try
                                    {
                                        player.allItems[highlightedInvCell.x][highlightedInvCell.y] = j;
                                        player.allItems[pressedIndex.x][pressedIndex.y] = i;
                                    }
                                    catch (Exception e)
                                    {
                                        player.allItems[highlightedInvCell.x][highlightedInvCell.y] = i;
                                        player.allItems[pressedIndex.x][pressedIndex.y] = j;
                                    }
                                    highlightedInvCell.y = -1;
                                }
                            }
                            else
                            {
                                highlightedInvCell = pressedIndex;
                            }
                        }
                    }
                }
            }
            else
            {

            }
            g.setFont(new Font(DEFAULT_FONT, Font.PLAIN, 13));
            //draw the weapons grid:
            inventoryX = -cameraX+22;
            inventoryY = -cameraY+96;
            g.setColor(Color.BLACK);
            g.drawString("Weapons:", -cameraX+22, -cameraY+92);
            for(int i = 0; i < player.allItems[Player.WEAPONS].length; i++)
            {
                g.drawRect(inventoryX, inventoryY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                if(player.allItems[Player.WEAPONS][i] != null && player.allItems[Player.WEAPONS][i].img != null)
                {
                    if(player.allItems[Player.WEAPONS][i].img.getWidth(null) > Player.INVENTORY_CELL_SIZE)
                    {
                        int adjustedHeight = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/player.allItems[Player.WEAPONS][i].img.getWidth(null))*player.allItems[Player.WEAPONS][i].img.getHeight(null));
                        g.drawImage(player.allItems[Player.WEAPONS][i].img, inventoryX, inventoryY+(Player.INVENTORY_CELL_SIZE-adjustedHeight)/2, Player.INVENTORY_CELL_SIZE, adjustedHeight, null);
                    }
                    else if(player.allItems[Player.WEAPONS][i].img.getHeight(null) > Player.INVENTORY_CELL_SIZE)
                    {
                        int adjustedWidth = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/player.allItems[Player.WEAPONS][i].img.getHeight(null))*player.allItems[Player.WEAPONS][i].img.getWidth(null));
                        g.drawImage(player.allItems[Player.WEAPONS][i].img, inventoryX+(Player.INVENTORY_CELL_SIZE-adjustedWidth)/2, inventoryY, adjustedWidth, Player.INVENTORY_CELL_SIZE, null);
                    }
                    else
                    {
                        g.drawImage(player.allItems[Player.WEAPONS][i].img, inventoryX+(Player.INVENTORY_CELL_SIZE-player.allItems[Player.WEAPONS][i].img.getWidth(null))/2, inventoryY+(Player.INVENTORY_CELL_SIZE-player.allItems[Player.WEAPONS][i].img.getHeight(null))/2, null);
                    }
                }
                inventoryX+=Player.INVENTORY_CELL_SIZE;
            }
            //draw the ability grid:
            inventoryX = -cameraX+52+Player.INVENTORY_CELL_SIZE*6;
            inventoryY = -cameraY+96;
            g.drawString("Abilities:", inventoryX, -cameraY+92);
            for(int i = 0; i < player.allItems[Player.ABILITIES].length; i++)
            {
                g.drawRect(inventoryX, inventoryY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                Ability a = (Ability) player.allItems[Player.ABILITIES][i];
                if(a != null && a.img != null)
                {
                    if(a.img.getWidth(null) > Player.INVENTORY_CELL_SIZE)
                    {
                        int adjustedHeight = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/a.img.getWidth(null))*a.img.getHeight(null));
                        g.drawImage(a.img, inventoryX, inventoryY+(Player.INVENTORY_CELL_SIZE-adjustedHeight)/2, Player.INVENTORY_CELL_SIZE, adjustedHeight, null);
                    }
                    else if(player.allItems[Player.ABILITIES][i].img.getHeight(null) > Player.INVENTORY_CELL_SIZE)
                    {
                        int adjustedWidth = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/a.img.getHeight(null))*a.img.getWidth(null));
                        g.drawImage(a.img, inventoryX+(Player.INVENTORY_CELL_SIZE-adjustedWidth)/2, inventoryY, adjustedWidth, Player.INVENTORY_CELL_SIZE, null);
                    }
                    else
                    {
                        g.drawImage(a.img, inventoryX+(Player.INVENTORY_CELL_SIZE-a.img.getWidth(null))/2, inventoryY+(Player.INVENTORY_CELL_SIZE-a.img.getHeight(null))/2, null);
                    }
                    DecimalFormat df = new DecimalFormat("0.0");
                    if(a.currCooldown != 0)
                    {
                        g.setColor(new Color(0,0,255,180));
                        g.fillRect(inventoryX, inventoryY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                        g.setColor(Color.BLACK);
                        g.setFont(new Font(DEFAULT_FONT, Font.BOLD, 15));
                        g.drawString(df.format(a.currCooldown), inventoryX+2, inventoryY+21);
                    }
                }
                inventoryX+=Player.INVENTORY_CELL_SIZE;
            }
            //draw the head slot:
            inventoryX = -cameraX+82+Player.INVENTORY_CELL_SIZE*9;
            inventoryY = -cameraY+96;
            g.setFont(new Font(DEFAULT_FONT, Font.PLAIN, 13));
            g.drawString("Armor:", inventoryX, -cameraY+92);
            g.drawRect(inventoryX, inventoryY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
            if(player.allItems[Player.HEAD][0] != null && player.allItems[Player.HEAD][0].img != null)
            {
                if(player.allItems[Player.HEAD][0].img.getWidth(null) > Player.INVENTORY_CELL_SIZE)
                {
                    int adjustedHeight = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/player.allItems[Player.HEAD][0].img.getWidth(null))*player.allItems[Player.HEAD][0].img.getHeight(null));
                    g.drawImage(player.allItems[Player.HEAD][0].img, inventoryX, inventoryY+(Player.INVENTORY_CELL_SIZE-adjustedHeight)/2, Player.INVENTORY_CELL_SIZE, adjustedHeight, null);
                }
                else if(player.allItems[Player.HEAD][0].img.getHeight(null) > Player.INVENTORY_CELL_SIZE)
                {
                    int adjustedWidth = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/player.allItems[Player.HEAD][0].img.getHeight(null))*player.allItems[Player.HEAD][0].img.getWidth(null));
                    g.drawImage(player.allItems[Player.HEAD][0].img, inventoryX+(Player.INVENTORY_CELL_SIZE-adjustedWidth)/2, inventoryY, adjustedWidth, Player.INVENTORY_CELL_SIZE, null);
                }
                else
                {
                    g.drawImage(player.allItems[Player.HEAD][0].img, inventoryX+(Player.INVENTORY_CELL_SIZE-player.allItems[Player.HEAD][0].img.getWidth(null))/2, inventoryY+(Player.INVENTORY_CELL_SIZE-player.allItems[Player.HEAD][0].img.getHeight(null))/2, null);
                }
            }
            //draw the body slot:
            inventoryX = -cameraX+86+Player.INVENTORY_CELL_SIZE*10;
            inventoryY = -cameraY+96;
            g.drawRect(inventoryX, inventoryY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
            if(player.allItems[Player.BODY][0] != null && player.allItems[Player.BODY][0].img != null)
            {
                if(player.allItems[Player.BODY][0].img.getWidth(null) > Player.INVENTORY_CELL_SIZE)
                {
                    int adjustedHeight = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/player.allItems[Player.BODY][0].img.getWidth(null))*player.allItems[Player.BODY][0].img.getHeight(null));
                    g.drawImage(player.allItems[Player.BODY][0].img, inventoryX, inventoryY+(Player.INVENTORY_CELL_SIZE-adjustedHeight)/2, Player.INVENTORY_CELL_SIZE, adjustedHeight, null);
                }
                else if(player.allItems[Player.BODY][0].img.getHeight(null) > Player.INVENTORY_CELL_SIZE)
                {
                    int adjustedWidth = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/player.allItems[Player.BODY][0].img.getHeight(null))*player.allItems[Player.BODY][0].img.getWidth(null));
                    g.drawImage(player.allItems[Player.BODY][0].img, inventoryX+(Player.INVENTORY_CELL_SIZE-adjustedWidth)/2, inventoryY, adjustedWidth, Player.INVENTORY_CELL_SIZE, null);
                }
                else
                {
                    g.drawImage(player.allItems[Player.BODY][0].img, inventoryX+(Player.INVENTORY_CELL_SIZE-player.allItems[Player.BODY][0].img.getWidth(null))/2, inventoryY+(Player.INVENTORY_CELL_SIZE-player.allItems[Player.BODY][0].img.getHeight(null))/2, null);
                }
            }
            //draw the foot slot:
            inventoryX = -cameraX+90+Player.INVENTORY_CELL_SIZE*11;
            inventoryY = -cameraY+96;
            g.drawRect(inventoryX, inventoryY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
            if(player.allItems[Player.FOOT][0] != null && player.allItems[Player.FOOT][0].img != null)
            {
                if(player.allItems[Player.FOOT][0].img.getWidth(null) > Player.INVENTORY_CELL_SIZE)
                {
                    int adjustedHeight = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/player.allItems[Player.FOOT][0].img.getWidth(null))*player.allItems[Player.FOOT][0].img.getHeight(null));
                    g.drawImage(player.allItems[Player.FOOT][0].img, inventoryX, inventoryY+(Player.INVENTORY_CELL_SIZE-adjustedHeight)/2, Player.INVENTORY_CELL_SIZE, adjustedHeight, null);
                }
                else if(player.allItems[Player.FOOT][0].img.getHeight(null) > Player.INVENTORY_CELL_SIZE)
                {
                    int adjustedWidth = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/player.allItems[Player.FOOT][0].img.getHeight(null))*player.allItems[Player.FOOT][0].img.getWidth(null));
                    g.drawImage(player.allItems[Player.FOOT][0].img, inventoryX+(Player.INVENTORY_CELL_SIZE-adjustedWidth)/2, inventoryY, adjustedWidth, Player.INVENTORY_CELL_SIZE, null);
                }
                else
                {
                    g.drawImage(player.allItems[Player.FOOT][0].img, inventoryX+(Player.INVENTORY_CELL_SIZE-player.allItems[Player.FOOT][0].img.getWidth(null))/2, inventoryY+(Player.INVENTORY_CELL_SIZE-player.allItems[Player.FOOT][0].img.getHeight(null))/2, null);
                }
            }
            //draw the potion jars grid:
            inventoryX = -cameraX+121+Player.INVENTORY_CELL_SIZE*12;
            inventoryY = -cameraY+96;
            g.drawString("Potions:", inventoryX, -cameraY+92);
            for(int i = 0; i < player.allItems[Player.POTIONS].length; i++)
            {
                g.setColor(Color.BLACK);
                g.drawRect(inventoryX, inventoryY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                PotionJar p = (PotionJar) player.allItems[Player.POTIONS][i];
                if(p != null && p.img != null)
                {
                    if(p.img.getWidth(null) > Player.INVENTORY_CELL_SIZE)
                    {
                        int adjustedHeight = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/p.img.getWidth(null))*p.img.getHeight(null));
                        if(p.type == PotionJar.HEALTH)
                        {
                            g.setColor(Color.RED);
                        }
                        else
                        {
                            g.setColor(Color.GREEN);
                        }
                        g.fillRect(inventoryX, inventoryY+(Player.INVENTORY_CELL_SIZE-adjustedHeight)/2+(int)Math.round(adjustedHeight*(1-p.getRatio())), Player.INVENTORY_CELL_SIZE, (int) Math.round(adjustedHeight*p.getRatio()));
                        g.drawImage(p.img, inventoryX, inventoryY+(Player.INVENTORY_CELL_SIZE-adjustedHeight)/2, Player.INVENTORY_CELL_SIZE, adjustedHeight, null);
                    }
                    else if(p.img.getHeight(null) > Player.INVENTORY_CELL_SIZE)
                    {
                        int adjustedWidth = (int)Math.round((Player.INVENTORY_CELL_SIZE*1.0/p.img.getHeight(null))*p.img.getWidth(null));
                        if(p.type == PotionJar.HEALTH)
                        {
                            g.setColor(Color.RED);
                        }
                        else
                        {
                            g.setColor(Color.GREEN);
                        }
                        g.fillRect(inventoryX+(Player.INVENTORY_CELL_SIZE-adjustedWidth)/2, inventoryY+(int)Math.round(Player.INVENTORY_CELL_SIZE*(1-p.getRatio())), adjustedWidth, (int) Math.round(Player.INVENTORY_CELL_SIZE*p.getRatio()));
                        g.drawImage(p.img, inventoryX+(Player.INVENTORY_CELL_SIZE-adjustedWidth)/2, inventoryY, adjustedWidth, Player.INVENTORY_CELL_SIZE, null);
                    }
                    else
                    {
                        if(p.type == PotionJar.HEALTH)
                        {
                            g.setColor(Color.RED);
                        }
                        else
                        {
                            g.setColor(Color.GREEN);
                        }
                        g.fillRect(inventoryX+(Player.INVENTORY_CELL_SIZE-p.img.getWidth(null))/2, inventoryY+(Player.INVENTORY_CELL_SIZE-p.img.getHeight(null))/2+(int)Math.round(p.img.getHeight(null)*(1-p.getRatio())), p.img.getWidth(null), (int)Math.round(p.img.getHeight(null)*p.getRatio()));
                        g.drawImage(p.img, inventoryX+(Player.INVENTORY_CELL_SIZE-p.img.getWidth(null))/2, inventoryY+(Player.INVENTORY_CELL_SIZE-p.img.getHeight(null))/2, null);
                    }
                    DecimalFormat df = new DecimalFormat("0.0");
                    if(p.currCooldown > 0)
                    {
                        g.setColor(new Color(0,0,255,180));
                        g.fillRect(inventoryX, inventoryY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                        g.setColor(Color.BLACK);
                        g.setFont(new Font(DEFAULT_FONT, Font.BOLD, 15));
                        g.drawString(df.format(p.currCooldown), inventoryX+2, inventoryY+21);
                    }
                }
                inventoryX+=Player.INVENTORY_CELL_SIZE;
            }
            if(!menu)
            {
                g.setColor(Color.RED);
                g.drawRect(-cameraX+22+Player.INVENTORY_CELL_SIZE*player.currWeaponIndex, -cameraY+96, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                g.drawRect(-cameraX+21+Player.INVENTORY_CELL_SIZE*player.currWeaponIndex, -cameraY+95, Player.INVENTORY_CELL_SIZE+2, Player.INVENTORY_CELL_SIZE+2);
                g.setColor(new Color(250,0,0,160));
                g.fillRect(-cameraX+23+Player.INVENTORY_CELL_SIZE*player.currWeaponIndex, -cameraY+97, Player.INVENTORY_CELL_SIZE-1, Player.INVENTORY_CELL_SIZE-1);
            }
            for(Button b: buttons)
            {
                b.drawButton(g);
            }
            if(indexOfMouse.y >= 0 && player.allItems[indexOfMouse.x][indexOfMouse.y] != null)
            {
                player.allItems[indexOfMouse.x][indexOfMouse.y].drawItemDescription(g, mouseX-cameraX, mouseY-cameraY);
                if(trash)
                {
                    Image x = AssetManager.getImage("trashicon");
                    g.drawImage(x, mouseX-cameraX-x.getWidth(null)/2, mouseY-cameraY-x.getHeight(null)/2, null);
                }
            }
            g.setColor(Color.YELLOW);
            if(highlightedInvCell.y != -1)
            {
                if(highlightedInvCell.x == 0) //in the inventory
                {
                    g.drawRect(highlightedInvCell.y%10*Player.INVENTORY_CELL_SIZE+20-cameraX, highlightedInvCell.y/10*Player.INVENTORY_CELL_SIZE+200-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                }
                else if(highlightedInvCell.x == 1) //in the weapons
                {
                    g.drawRect(highlightedInvCell.y%6*Player.INVENTORY_CELL_SIZE+22-cameraX, highlightedInvCell.y/10*Player.INVENTORY_CELL_SIZE+96-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                }
                else if(highlightedInvCell.x == 2) //in the abilities
                {
                    g.drawRect(highlightedInvCell.y%3*Player.INVENTORY_CELL_SIZE+52-cameraX+Player.INVENTORY_CELL_SIZE*6, highlightedInvCell.y/10*Player.INVENTORY_CELL_SIZE+96-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                }
                else if(highlightedInvCell.x == 3) //in the head cell
                {
                    g.drawRect(highlightedInvCell.y%1*Player.INVENTORY_CELL_SIZE+82-cameraX+Player.INVENTORY_CELL_SIZE*9, highlightedInvCell.y/10*Player.INVENTORY_CELL_SIZE+96-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                }
                else if(highlightedInvCell.x == 4) //in the body cell
                {
                    g.drawRect(highlightedInvCell.y%1*Player.INVENTORY_CELL_SIZE+86-cameraX+Player.INVENTORY_CELL_SIZE*10, highlightedInvCell.y/10*Player.INVENTORY_CELL_SIZE+96-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                }
                else if(highlightedInvCell.x == 5) //in the foot cell
                {
                    g.drawRect(highlightedInvCell.y%1*Player.INVENTORY_CELL_SIZE+90-cameraX+Player.INVENTORY_CELL_SIZE*11, highlightedInvCell.y/10*Player.INVENTORY_CELL_SIZE+96-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                }
                else if(highlightedInvCell.x == 6) //in the potions
                {
                    g.drawRect(highlightedInvCell.y%3*Player.INVENTORY_CELL_SIZE+121-cameraX+Player.INVENTORY_CELL_SIZE*12, highlightedInvCell.y/10*Player.INVENTORY_CELL_SIZE+96-cameraY, Player.INVENTORY_CELL_SIZE, Player.INVENTORY_CELL_SIZE);
                }
            }
            if(message != null)
                message.drawMessage(g);
            if(mousePressed)
            {
                mousePressed = false;
            }
            if(fade)
            {
                Fade.fade();
                if(Fade.reversing())
                {
                    fade = false;
                    String dd = currMap;
                    readMap(currDoor.mapLink+".map");
                    if(!loadSavedGame)
                    for(Door d : doors)
                    {
                        String check = d.mapLink+".map";
                        if(check.equals(dd))
                        {
                            player.x = d.x+Player.PLAYER_WIDTH/2;
                            player.y = d.y+Player.PLAYER_HEIGHT/2;
                        }
                    }
                }
            }
            else
            {
                if(Fade.reversing())
                {
                    m.backToMenu();
                }
            }
            g.setColor(new Color(0,0,0,Fade.getAlpha()));
            g.fillRect(-cameraX, -cameraY, getWidth(), getHeight());
            if(dead)
            {
                g.setFont(new Font(DEFAULT_FONT, Font.BOLD, 60));
                g.setColor(Color.DARK_GRAY);
                FontMetrics fm = g.getFontMetrics();
                g.drawString("YOU HAVE DIED.", -cameraX+(WIDTH-fm.stringWidth("YOU HAVE DIED."))/2, -cameraY+getHeight()/2+30);
                Fade.fade();
            }
        }
        catch(Exception e)
        {
            
        }
    }
    
    /**
     * Draws the mini-map for the game. NOTE: THIS METHOD IS NOT USED IN OUR
     * GAME. We initially were going to have a mini-map in the top right corner,
     * which would show were you were in a "big picture" with respect to the
     * rest of the map, but this didn't fit with our game style.
     * We decided to leave the method in, just for information.
     * 
     * @param g         The object on which the graphics is drawn.
     * @param mapX      The x location where the map will be drawn.
     * @param mapY      The y location where the map will be drawn.
     * @param edgeSize  The edge size of the map; i.e. the scale to which the map will be drawn. (e.g. 100 would mean a 100x100 square)
     */
    public void drawMiniMap(Graphics g, int mapX, int mapY, int edgeSize)
    {
        g.setColor(Color.BLACK);
        g.fillRect(mapX, mapY, edgeSize, edgeSize);
        for(int i = 0; i < grid.cells.length; i++)
        {
            for(int j = 0; j < grid.cells[0].length; j++)
            {
               Image img = grid.cells[i][j].getImage();
               if(!(grid.cells[i][j].getDrawCoord().x/miniMapZoom + cameraX/miniMapZoom+3 + 28 < 0 ||
                        grid.cells[i][j].getDrawCoord().x/miniMapZoom + cameraX/miniMapZoom-3 + 28 > edgeSize-3 ||
                        grid.cells[i][j].getDrawCoord().y/miniMapZoom + cameraY/miniMapZoom+3 + 2 < 0 ||
                        grid.cells[i][j].getDrawCoord().y/miniMapZoom + cameraY/miniMapZoom-3 + 2 > edgeSize-3))
                {
                    if(grid.cells[i][j].discovered)
                    {
                        if(grid.cells[i][j].getType() == Cell.NULL)
                        {
                            g.drawImage(AssetManager.getImage("none"), grid.cells[i][j].getDrawCoord().x/miniMapZoom + mapX + cameraX/miniMapZoom + 28, grid.cells[i][j].getDrawCoord().y/miniMapZoom + mapY + cameraY/miniMapZoom + 4, Grid.CELL_SIZE/miniMapZoom+1, Grid.CELL_SIZE/miniMapZoom+1, null);
                        }
                        else
                        {
                            g.drawImage(img, grid.cells[i][j].getDrawCoord().x/miniMapZoom + mapX + cameraX/miniMapZoom + 28, grid.cells[i][j].getDrawCoord().y/miniMapZoom + mapY + cameraY/miniMapZoom + 4, Grid.CELL_SIZE/miniMapZoom+1, Grid.CELL_SIZE/miniMapZoom+1, null);
                        }
                    }
                }
            }
        }
        g.fillRect((int)(player.x+cameraX-Player.PLAYER_X)/miniMapZoom+mapX+69, (int)(player.y+cameraY-Player.PLAYER_Y)/miniMapZoom+mapY+56, 3, 5);
        g.setColor(Color.RED);
        for(int i = 0; i < enemies.size(); i++)
        {
            int fillX = (int)(enemies.get(i).getCoord().x+cameraX-Player.PLAYER_X)/miniMapZoom+mapX+69;
            int fillY = (int)(enemies.get(i).getCoord().y+cameraY-Player.PLAYER_Y)/miniMapZoom+mapY+62;
            if(fillX > mapX+edgeSize)
            {
                fillX = mapX+edgeSize-2;
            }
            else if(fillX < mapX)
            {
                fillX = mapX+2;
            }
            if(fillY > mapY+edgeSize)
            {
                fillY = mapY+edgeSize-2;
            }
            else if(fillY < mapY)
            {
                fillY = mapY+2;
            }
            g.fillRect(fillX-1, fillY-1, 3,3);
        }
        for(int i = 5; i >= 0; i--)
        {
            g.setColor(Color.DARK_GRAY);
            g.drawRect(mapX-i, mapY-i, edgeSize+i*2, edgeSize+i*2);
        }
    }
    
    /**
     * Updates the game and all of its components.
     */
    public void updateGame()
    {
        try
        {
            if(oldMillis == 0)
            {
                oldMillis = System.currentTimeMillis()-16;
                fpsDispUpdate = System.currentTimeMillis();
                fpsToDisplay = 62;
            }
            if(System.currentTimeMillis() != oldMillis)
                fps = (int) (1000/(System.currentTimeMillis()-oldMillis));
            oldMillis = System.currentTimeMillis();
            if(System.currentTimeMillis()-fpsDispUpdate >= 500)
            {
                fpsToDisplay = fps;
                fpsDispUpdate = System.currentTimeMillis();
            }
            gridUpdateTimer++;
            if(gridUpdateTimer == updateAt)
            {
                gridUpdateTimer = 0;
                grid.updateGrid();
            }
            //addEnemy+=1;
            if(leftMouse)
            {
                addShot = true;
            }
            else
            {
                addShot = false;
            }
            /*if(rightMouse)
            {
                Ability a;
                if(player.allItems[Player.ABILITIES][player.currAbilityIndex] != null)
                {
                    a = (Ability) player.allItems[Player.ABILITIES][player.currAbilityIndex];
                    a.activate(this);
                }
            }*/
            for(int i = 0; i < treasureChests.size(); i++)
            {
                treasureChests.get(i).updatePosition(this);
            }
            for(int i = 0; i < itemsOnMap.size(); i++)
            {
                if(itemsOnMap.get(i).removeTimer > itemsOnMap.get(i).removeAt)
                {
                    itemsOnMap.remove(i);
                    i--;
                }
                else
                {
                    itemsOnMap.get(i).updatePosition(this);
                }
            }
            for(int i = 0; i < npcs.size(); i++)
            {
                npcs.get(i).updatePosition(this);
            }
            if(player.allItems[Player.WEAPONS][player.currWeaponIndex] != null)
            {
                if(addShot)
                {
                    Object weaponn = player.allItems[Player.WEAPONS][player.currWeaponIndex];
                    try
                    {
                        RangedWeapon weapon = (RangedWeapon) weaponn;
                        shootCounter+=(player.shootSpeed*(1+weapon.attSpeed))/62.5;
                        if(shootCounter < 0)
                        {
                            shootCounter = 0;
                        }
                        if(shootCounter > 1/*48-player.shootSpeed-weapon.attSpeed*/)
                        {
                            //SoundEffect.playAudio(new File("resources/audio/effects/shoot.wav"));
                            double shotAngle = Math.atan((double)(mouseY-player.y-cameraY)/(mouseX-player.x-cameraX));
                            List<Shot> l = Arrays.asList(weapon.getShotsToAdd((int)Math.round(player.x), (int)Math.round(player.y), shotAngle, this));
                            for(Shot s : l)
                            {
                                s.damage *= (0.75+(player.strength*0.015));
                                double rand = Math.random()*100;
                                if(player.critChance >= rand)
                                {
                                    s.damage *= player.critDamage/100.0;
                                    s.crit = true;
                                }
                                if(s.damage == 0)
                                {
                                    s.damage = 1;
                                }
                                shots.add(s);
                            }
                            Sounds.addAudio(AssetManager.getSoundEffect("shoot"));
                            shootCounter = shootCounter-1;
                        }
                    }
                    catch (Exception e)
                    {
                        MeleeWeapon weapon = (MeleeWeapon) weaponn;
                        shootCounter+=(player.shootSpeed*(1+weapon.attSpeed))/62.5;
                        if(shootCounter < 0)
                        {
                            shootCounter = 0;
                        }
                        if(shootCounter > 1/*51-player.shootSpeed-weapon.attSpeed*/)
                        {
                            double shotAngle = Math.atan((double)(mouseY-player.y-cameraY+10)/(mouseX-player.x-cameraX));
                            if(mouseX >= cameraX+player.x)
                            {
                                List<Shot> l = Arrays.asList(weapon.getShotsToAdd((int) (Math.round(player.x)-(Math.cos(shotAngle)*40)), (int) (Math.round(player.y)-10-(Math.sin(shotAngle)*40)), shotAngle, this));
                                for(Shot s: l)
                                {
                                    s.damage *= (0.75+(player.strength*0.015));
                                    double rand = Math.random()*100;
                                    if(player.critChance >= rand)
                                    {
                                        s.damage *= player.critDamage/100.0;
                                        s.crit = true;
                                    }
                                    if(s.damage == 0)
                                    {
                                        s.damage = 1;
                                    }
                                    shots.add(s);
                                }
                            }
                            else
                            {
                                List<Shot> l = Arrays.asList(weapon.getShotsToAdd((int) (Math.round(player.x)+(Math.cos(shotAngle)*40)), (int) (Math.round(player.y)-10-(Math.sin(shotAngle)*40)), shotAngle, this));
                                for(Shot s : l)
                                {
                                    s.damage *= (0.75+(player.strength*0.015));
                                    double rand = Math.random()*100;
                                    if(player.critChance >= rand)
                                    {
                                        s.damage *= player.critDamage/100.0;
                                        s.crit = true;
                                    }
                                    if(s.damage == 0)
                                    {
                                        s.damage = 1;
                                    }
                                    s.xspeed = -s.xspeed;
                                    s.yspeed = -s.yspeed; 
                                    shots.add(s);
                                }
                            }
                            Sounds.addAudio(AssetManager.getSoundEffect("sword"));
                            shootCounter = shootCounter-1;
                        }
                    }
                }
                else
                {
                    Object o = player.allItems[Player.WEAPONS][player.currWeaponIndex];
                    try
                    {
                        RangedWeapon weapon = (RangedWeapon) o;
                        shootCounter+=(player.shootSpeed*(1+weapon.attSpeed))/62.5;
                    }
                    catch (Exception e)
                    {
                        MeleeWeapon weapon = (MeleeWeapon) o;
                        shootCounter+=(player.shootSpeed*(1+weapon.attSpeed))/62.5;
                    }
                    if(shootCounter > 1)
                    {
                        shootCounter = 1;
                    }
                }
            }
            else
            {
                for(int i = player.currWeaponIndex; i < player.allItems[Player.WEAPONS].length; i++)
                {
                    if(player.allItems[Player.WEAPONS][i] != null)
                    {
                        player.currWeaponIndex = i;
                        i = player.allItems[Player.WEAPONS].length;
                    }
                }
                if(player.allItems[Player.WEAPONS][player.currWeaponIndex] == null)
                {
                    for(int i = 0; i < player.currWeaponIndex; i++)
                    {
                        if(player.allItems[Player.WEAPONS][i] != null)
                        {
                            player.currWeaponIndex = i;
                            i = player.allItems[Player.WEAPONS].length;
                        }
                    }
                }
                if(player.allItems[Player.WEAPONS][player.currWeaponIndex] == null)
                {
                    player.currWeaponIndex = 0;
                }
            }
            boolean addGravToPlayerY = false;
            int gx = (int)(player.x)/Grid.CELL_SIZE;
            int gxright = (int)(player.x+14)/Grid.CELL_SIZE; //for some reason, right side collision is stupid. So this variable is needed.
            int gy = (int)(player.y+23)/Grid.CELL_SIZE;
            /*
             * This is the guy in cell coordinates:
             * (0,0)
             *  _ _
             * |_|_|
             * |_|_|
             * |_|_|            At (1,3) is gx, gy.
             *      (2,3)
             */
            if(gx-1 >= 0 &&
                    gx+1 < grid.cells.length &&
                    gy >= 0 &&
                    gy < grid.cells[0].length)
            {
                try
                {
                    if((grid.cells[gx-1][gy].isSolid && grid.cells[gx-1][gy].isReal==0) ||
                            (grid.cells[gx][gy].isSolid && grid.cells[gx][gy].isReal==0) ||
                            (grid.cells[gxright][gy].isSolid && grid.cells[gxright][gy].isReal==0))
                    {
                        if(player.currentGrav > -player.jumpHeight)
                        {
                            player.y = grid.cells[gx][gy].getDrawCoord().y-22;
                            player.currentGrav = 0;
                            if(up && upReleased)
                            {
                                firstJump = true;
                                player.currentGrav = -player.jumpHeight;
                                upReleased = false;
                                addGravToPlayerY = true;
                            }
                            else if(down)
                            {
                                //player.currentGrav = -0.001;
                                //player.y+=player.gravConstant/2;
                            }
                        }
                        else
                        {
                            if(left || player.xSpeed < 0)
                            if(grid.cells[gx-2][gy].isSolid && player.x-17 <= grid.cells[gx-1][gy].getDrawCoord().x
                                    || grid.cells[gx-1][gy].isSolid)
                            {
                                player.x = grid.cells[gx][gy-2].getDrawCoord().x+1;
                                player.xSpeed = 0;
                            }
                            if(right || player.xSpeed > 0)
                            if(grid.cells[gx+1][gy].isSolid)
                            {
                                player.x = grid.cells[gx][gy-2].getDrawCoord().x;
                                player.xSpeed = 0;
                            }
                            player.currentGrav+=player.gravConstant;
                            if(player.currentGrav > player.maxGrav)
                            {
                                player.currentGrav = player.maxGrav;
                            }
                            addGravToPlayerY = true;
                        }
                    }
                    else
                    {
                        if(left || player.xSpeed < 0)
                        if(grid.cells[gx-2][gy].isSolid && grid.cells[gx-2][gy].isReal == 0 && player.x-17 <= grid.cells[gx-1][gy].getDrawCoord().x
                                || (grid.cells[gx-1][gy].isSolid && grid.cells[gx-1][gy].isReal == 0))
                        {
                            player.x = grid.cells[gx][gy-2].getDrawCoord().x+1;
                            player.xSpeed = 0;
                        }
                        if(right || player.xSpeed > 0)
                        if(grid.cells[gx+1][gy].isSolid && grid.cells[gx+1][gy].isReal == 0)
                        {
                            player.x = grid.cells[gx][gy-2].getDrawCoord().x;
                            player.xSpeed = 0;
                        }
                        player.currentGrav+=player.gravConstant;
                        if(player.currentGrav > player.maxGrav)
                        {
                            player.currentGrav = player.maxGrav;
                        }
                        addGravToPlayerY = true;
                    }
                    if(firstJump)
                    {
                        if(up && player.currentGrav <= -3.2)
                        {
                            player.currentGrav -= 0.55;
                        }
                        else
                        {
                            player.currentGrav = -3.21;
                            firstJump = false;
                        }
                    }
                    if(left || player.xSpeed < 0)
                    {
                        if((grid.cells[gx-2][gy-1].isSolid && grid.cells[gx-2][gy-1].isReal == 0 && player.x-18 <= grid.cells[gx-1][gy-1].getDrawCoord().x) ||
                                (grid.cells[gx-2][gy-2].isSolid && grid.cells[gx-2][gy-2].isReal == 0 && player.x-18 <= grid.cells[gx-1][gy-2].getDrawCoord().x) ||
                                (grid.cells[gx-2][gy-3].isSolid && grid.cells[gx-2][gy-3].isReal == 0 && player.x-18 <= grid.cells[gx-1][gy-3].getDrawCoord().x) /*||
                                (player.currentGrav > 0 && grid.cells[gx-2][gy].isSolid && player.x-19 <= grid.cells[gx-1][gy].getDrawCoord().x)*/)
                        {
                            player.x = grid.cells[gx][gy-2].getDrawCoord().x;
                            player.xSpeed = 0;
                        }
                        else if(left)
                        {
                            player.xSpeed-=player.xAccel;
                        }
                    }
                    if(right || player.xSpeed > 0)
                    {
                        if((grid.cells[gx+1][gy-1].isSolid && grid.cells[gx+1][gy-1].isReal == 0) ||
                                (grid.cells[gx+1][gy-2].isSolid && grid.cells[gx+1][gy-2].isReal == 0) ||
                                (grid.cells[gx+1][gy-3].isSolid && grid.cells[gx+1][gy-3].isReal == 0) /*||
                                (player.currentGrav > 0 && grid.cells[gx+1][gy].isSolid)*/)
                        {
                            player.x = grid.cells[gx][gy-2].getDrawCoord().x;
                            player.xSpeed = 0;
                        }
                        else if(right)
                        {
                            player.xSpeed+=player.xAccel;
                        }
                    }
                    if((left && right) || (!left && !right))
                    {
                        if(player.xSpeed > 0)
                        {
                            player.xSpeed-=player.xAccel;
                        }
                        else if(player.xSpeed < 0)
                        {
                            player.xSpeed+=player.xAccel;
                        }
                    }
                    if(Math.abs(player.xSpeed) < player.xAccel)
                    {
                        player.xSpeed = 0;
                    }
                    if(Math.abs(player.xSpeed) > player.maxSpeed)
                    {
                        player.xSpeed = Math.abs(player.xSpeed)/player.xSpeed*player.maxSpeed;
                    }
                    if(player.currentGrav <= 0 && ((grid.cells[gx-1][gy-4].isSolid && grid.cells[gx-1][gy-4].isReal == 0 && player.y-29 < grid.cells[gx-1][gy-3].getDrawCoord().y) ||
                            (grid.cells[gx][gy-4].isSolid && grid.cells[gx][gy-4].isReal == 0 && player.y-29 < grid.cells[gx][gy-3].getDrawCoord().y) ||
                            (grid.cells[gxright][gy-4].isSolid && grid.cells[gxright][gy-4].isReal == 0 && player.y-29 < grid.cells[gxright][gy-3].getDrawCoord().y)))
                    {
                        player.currentGrav = 0;
                        player.y+=0;
                    }
                }
                catch(Exception ex)
                {
                    
                }
            }
            else
            {
                player.currentGrav+=player.gravConstant;
                player.y+=player.currentGrav;
                if(left)
                {
                    player.xSpeed-=player.xAccel;
                }
                if(right)
                {
                    player.xSpeed+=player.xAccel;
                }
                if(gy > grid.cells[0].length)
                {
                    dead = true;
                    player.health = 0;
                    Sounds.addAudio(AssetManager.getSoundEffect("death"));
                    Music.stopAudio();
                    Thread.sleep(1000);
                }
            }
            if(addGravToPlayerY)
            {
                player.y+=player.currentGrav;
            }
            player.x+=player.xSpeed;
            cameraX = Player.PLAYER_X-(int)player.x;
            cameraY = Player.PLAYER_Y-(int)player.y;
            double bdX=0, bdY=0;
            if(getWidth() > grid.cells.length*Grid.CELL_SIZE)
            {
                cameraX = (getWidth()-grid.cells.length*Grid.CELL_SIZE)/2;
            }
            else
            {
                if(cameraX > 0)
                {
                    cameraX = 0;
                }
                else if(cameraX - getWidth() < -grid.cells[grid.cells.length-1][0].getDrawCoord().x - Grid.CELL_SIZE)
                {
                    cameraX = -grid.cells[grid.cells.length-1][0].getDrawCoord().x+getWidth()-Grid.CELL_SIZE;
                }
                else
                {
                    bdX=player.xSpeed*0.2;
                }
            }
            if(getHeight()-148 > grid.cells[0].length*Grid.CELL_SIZE)
            {
                cameraY = (getHeight()-148-grid.cells[0].length*Grid.CELL_SIZE)/2+148;
            }
            else
            {
                if(cameraY-148 > 0)
                {
                    cameraY = 148;
                }
                else if(cameraY - getHeight() < -grid.cells[0][grid.cells[0].length-1].getDrawCoord().y - Grid.CELL_SIZE)
                {
                    cameraY = -grid.cells[0][grid.cells[0].length-1].getDrawCoord().y+getHeight()-Grid.CELL_SIZE;
                }
                else
                {
                    bdY=0.2*player.currentGrav;
                }
            }
            if(b != null)
            {
                b.update(-bdX, -bdY);
            }
            else
            {
                b = new Background(0, 148, AssetManager.getImage("forest"), Background.REPEAT_HORIZ);
            }
            for(int i = 0; i < shots.size(); i++)
            {
                for(int q = 0; q < shots.get(i).shotLocs.size(); q++)
                {
                    int shotGridX = (shots.get(i).shotLocs.get(q).x+shots.get(i).getCoord().x)/Grid.CELL_SIZE;
                    int shotGridY = (shots.get(i).shotLocs.get(q).y+shots.get(i).getCoord().y)/Grid.CELL_SIZE;
                    if(!(shotGridX < 0 || shotGridX >= grid.cells.length || shotGridY < 0
                            || shotGridY >= grid.cells[0].length))
                    {
                        if(grid.cells[shotGridX][shotGridY].isReal == 0 && grid.cells[shotGridX][shotGridY].isSolid)
                        {
                            if(!grid.cells[shotGridX][shotGridY].isIndestructible())
                            {
                                if(!shots.get(i).cellsHit.contains(new Point(shotGridX, shotGridY)))
                                {
                                    Sounds.addAudio(AssetManager.getSoundEffect("wallHit"));
                                    if(shots.get(i).crit)
                                    {
                                        Sounds.addAudio(AssetManager.getSoundEffect("crit"));
                                    }
                                    grid.cells[shotGridX][shotGridY].health -= shots.get(i).getDamage();
                                    shots.get(i).cellsHit.add(new Point(shotGridX, shotGridY));
                                    if(grid.cells[shotGridX][shotGridY].health <= 0)
                                    {
                                        grid.cells[shotGridX][shotGridY].health = 10;
                                        grid.cells[shotGridX][shotGridY].setType(Cell.NULL);
                                    }
                                }
                            }
                            if(!shots.get(i).piercesWalls)
                            {
                                shots.get(i).setRemove(true);
                            }
                        }
                        if(shots.get(i).friendly)
                        {
                            for(int k = 0; k < enemies.size(); k++)
                            {
                                if(!shots.get(i).enemiesHit.contains(enemies.get(k)) && enemies.get(k).hits(new Point(shots.get(i).shotLocs.get(q).x+shots.get(i).getCoord().x, shots.get(i).shotLocs.get(q).y+shots.get(i).getCoord().y)))
                                {
                                    enemies.get(k).setHealth(enemies.get(k).getHealth()-shots.get(i).getDamage());
                                    enemies.get(k).damages.add(new DamageText(g, enemies.get(k),"-"+shots.get(i).getDamage(), Color.RED));
                                    if(shots.get(i).crit)
                                    {
                                        Sounds.addAudio(AssetManager.getSoundEffect("crit"));
                                    }
                                    if(enemies.get(k).getHealth() < 0)
                                    {
                                        enemies.get(k).setHealth(0);
                                    }
                                    else
                                    {
                                        Sounds.addAudio(AssetManager.getSoundEffect("hit"));
                                    }
                                    if(!shots.get(i).piercesEnemies)
                                    {
                                        shots.get(i).setRemove(true);
                                    }
                                    shots.get(i).enemiesHit.add(enemies.get(k));
                                }
                            }
                        }
                        else
                        {
                            if(!shots.get(i).playerHit && player.hits(shots.get(i).shotLocs.get(q).x+shots.get(i).getCoord().x, shots.get(i).shotLocs.get(q).y+shots.get(i).getCoord().y))
                            {
                                player.health-=shots.get(i).getDamage()*(1-player.defense*0.008);
                                player.damages.add(new DamageText(g, player, "-"+(int)Math.round(shots.get(i).getDamage()*(1-player.defense*0.008)), Color.RED));
                                if(player.health <= 0)
                                {
                                    dead = true;
                                    player.health = 0;
                                    Sounds.addAudio(AssetManager.getSoundEffect("death"));
                                    Music.stopAudio();
                                    Thread.sleep(1000);
                                }
                                else
                                {
                                    Sounds.addAudio(AssetManager.getSoundEffect("hurt"));
                                }
                                if(!shots.get(i).piercesEnemies)
                                {
                                    shots.get(i).setRemove(true);
                                }
                                shots.get(i).playerHit = true;
                            }
                        }
                    }
                }
                if(shots.get(i).needsToBeRemoved())
                {
                    shots.remove(i);
                }
                else
                {
                    shots.get(i).updateShot();
                }
            }
            for(int i = 0; i < enemies.size(); i++)
            {
                boolean enemyRemove = enemies.get(i).updateEnemy(this);
                if(enemyRemove)
                {
                    player.xp += enemies.get(i).getXP();
                    for(Item it : enemies.get(i).drops.keySet())
                    {
                        if(it != null)
                        {
                            it.xOnMap = enemies.get(i).x;
                            it.yOnMap = enemies.get(i).y-it.img.getHeight(null);
                            double rand = Math.random();
                            if(rand <= enemies.get(i).drops.get(it)/100.0)
                            {
                                itemsOnMap.add(it);
                            }
                        }
                    }
                    enemies.remove(i);
                    Sounds.addAudio(AssetManager.getSoundEffect("enemyDead"));
                }
            }
            player.updatePlayer();
            if(addEnemy > 280)
            {
                addEnemy = 0;
            }
            qtimer--;
            for(int j = 0; j < player.allItems[Player.ABILITIES].length; j++)
            {
                Item i = player.allItems[Player.ABILITIES][j];
                Item k = player.allItems[Player.POTIONS][j];
                if(i != null)
                {
                    Ability a = (Ability) i;
                    a.update();
                }
                if(k != null)
                {
                    PotionJar p = (PotionJar) k;
                    p.update();
                }
            }
            for(Button b: buttons)
            {
                b.updateButton();
                if(!b.hits(mouseX-cameraX, mouseY-cameraY))
                {
                    b.pressed = false;
                }
            }
        }
        catch(Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * Updates the menu, if the player is in the menu.
     */
    public void updateMenu()
    {
        for(Button b: buttons)
        {
            b.updateButton();
            if(!b.hits(mouseX-cameraX, mouseY-cameraY))
            {
                b.pressed = false;
            }
        }
    }
    
    /**
     * Switches from game to menu or menu to game. Handles which buttons to
     * put on the screen.
     */
    public void menuSwitch()
    {
        menu = !menu;
        if(menu)
        {
            buttons.clear();
            buttons.add(new Button(512, 18, 60, 30, "Close", Color.GREEN, "Close"));
            buttons.add(new Button(497, 540, 80, 30, "Save Game", new Color(255,150,150), "Save Game"));
            buttons.add(new Button(497, 580, 80, 30, "Main Menu", new Color(150,255,150), "Main Menu"));
            if(player.levelUpPoints > 0)
            {
                buttons.add(new Button(345, 350, 20, 20, "+", Color.LIGHT_GRAY, "staplus"));
                buttons.add(new Button(345, 375, 20, 20, "+", Color.LIGHT_GRAY, "potplus"));
                buttons.add(new Button(345, 400, 20, 20, "+", Color.LIGHT_GRAY, "defplus"));
                buttons.add(new Button(345, 425, 20, 20, "+", Color.LIGHT_GRAY, "strplus"));
                buttons.add(new Button(460, 350, 20, 20, "+", Color.LIGHT_GRAY, "spdplus"));
                buttons.add(new Button(460, 375, 20, 20, "+", Color.LIGHT_GRAY, "intplus"));
                buttons.add(new Button(460, 400, 20, 20, "+", Color.LIGHT_GRAY, "lckplus"));
            }
        }
        else
        {
            buttons.clear();
            buttons.add(new Button(512, 18, 60, 30, "Menu", new Color(100,100,255), "Menu"));
            highlightedInvCell.y = -1;
        }
        for(Button b: buttons)
        {
            b.updateButton();
        }
    }
    
    /**
     * This method is used for custom input for map reading. In "admin mode",
     * you could press 'I' to call this message and import any map you wanted.
     * In the actual game, this is disabled, to prevent players from teleporting
     * anywhere they wanted.
     */
    public void readMap()
    {
        try
        {
            String sss = JOptionPane.showInputDialog(null, "Enter the name of the map file you want to import\n(Include the \".map\" extension!)", "");
            readMap(sss);
        }
        catch (Exception ex)
        {
            Main.logger.log(Level.SEVERE, "Error in readMap() method in Game.class");
        }
    }
    
    /**
     * Exports the map. Although we don't edit most of the map file, we must
     * edit the Treasure Chests because once a player opens a treasure chest
     * and receives the item it had, we don't want the player to be able to
     * reload the map and acquire an infinite amount of items. Thus, we edit the
     * treasure chests and make them permanently opened.
     * 
     * @param f The file to export the map to.
     */
    public void export(File f)
    {
        FileWriter fw = null;
        if(f.exists())
        {
            try
            {
                Scanner sc = new Scanner(f);
                String[] sss = sc.nextLine().split(",");
                int spawnX = Integer.parseInt(sss[4]);
                int spawnY = Integer.parseInt(sss[5]);
                String musicc = sss[6];
                sc.nextLine();
                sc.nextLine();
                sc.nextLine();
                sc.nextLine();
                String enms = "";
                try
                {
                    enms = sc.nextLine();
                } catch(Exception ex){};
                sc.close();
                fw = new FileWriter(f);
                fw.write(grid.cells.length+","+grid.cells[0].length+","+AssetManager.getImageSource(b.getImage())+","+ b.getRepeatType() + ","+spawnX+","+spawnY+","+musicc+"\n");
                for(int x = 0; x < grid.cells.length; x++)
                {
                    for(int y = 0; y < grid.cells[0].length; y++)
                    {
                        int rand = (int)(Math.random()*9);
                        int rand2 = (int)(Math.random()*9);
                        int rand3 = (int)(Math.random()*9);
                        int index = 0;
                        for(int i = 0; i < ELCCell.types.length; i++)
                        {
                            if(i == grid.cells[x][y].getType())
                            {
                                index = i;
                                i = ELCCell.types.length;
                            }
                        }
                        fw.write(x + "-" + rand + "" + y + "-" + rand2 + "" + index + "-" + rand3 + "" + grid.cells[x][y].isReal + "-9");
                    }
                }
                fw.write("\n");
                for(int i = 0; i < doors.size(); i++)
                {
                    Door d = doors.get(i);
                    fw.write(d.x + "," + d.y + "," + d.mapLink + "," + AssetManager.getImageSource(d.img) + ";");
                }
                fw.write("\n");
                for(int i = 0; i < npcs.size(); i++)
                {
                    NPC n = npcs.get(i);
                    fw.write((int)n.x + "P64R6T2" + (int)n.y + "P64R6T2" + n.firstMessage.text + "P64R6T2");
                    if(n.msgs != null && !n.msgs[0].trim().equals(""))
                    {
                        for(int j = 0; j < n.msgs.length; j++)
                        {
                            fw.write(n.msgs[j]);
                            if(j != n.msgs.length-1)
                            {
                                fw.write("P64R6T1");
                            }
                        }
                    }
                    else
                    {
                        fw.write(" ");
                    }
                    fw.write("P64R6T2");
                    if(n.umsgs != null && !n.umsgs[0].trim().equals(""))
                    {
                        for(int j = 0; j < n.umsgs.length; j++)
                        {
                            fw.write(n.umsgs[j]);
                            if(j != n.umsgs.length-1)
                            {
                                fw.write("P64R6T1");
                            }
                        }
                    }
                    else
                    {
                        fw.write(" ");
                    }
                    fw.write("P64R6T2" + n.recycle + "P64R6T2" + AssetManager.getImageSource(n.img) + "P64R6T2");
                    boolean aa = false;
                    if(n.items != null)
                    {
                        for(int j = 0; j < n.items.length; j++)
                        {
                            if(n.items[j] != null && n.items[j].name != null)
                            {
                                fw.write(n.items[j].name);
                                if(j != n.items.length-1)
                                {
                                    fw.write("P64R6T1");
                                }
                            }
                            else
                            {
                                aa = true;
                            }
                        }
                    }
                    else
                    {
                        aa = true;
                    }
                    if(aa)
                    {
                        fw.write(" ");
                    }
                    fw.write("P64R6T3");
                }
                fw.write("\n");
                for(int i = 0; i < treasureChests.size(); i++)
                {
                    fw.write(treasureChests.get(i).exportTC() + ";");
                }
                fw.write("\n");
                fw.write(enms);
                fw.close();
                System.out.println("Succesfully exported map!");
            }
            catch(Exception e)
            {
                e.printStackTrace();
                JOptionPane.showMessageDialog(null, "Error while exporting file.", "Export error", JOptionPane.ERROR_MESSAGE);
                if(fw != null)
                {
                    try{
                    fw.close();}catch(Exception exx){}
                }
            }
        }
        else
        {
            System.out.println("OH NO");
        }
    }
    
    /**
     * Loads a map from the indicated map file.
     * 
     * @param map A String that is the name of the map file.
     */
    public void readMap(String map)
    {
        try
        {
            t.stop();
            currMap = map;
            File file = new File("resources/maps/" + map.replaceFirst(".map","")+"-"+fIndex+".map");
            if(file.exists())
            {
                Scanner scan = new Scanner(file);
                String[] grS = scan.nextLine().split(",");
                int gridW = Integer.parseInt(grS[0]);
                int gridH = Integer.parseInt(grS[1]);
                if(!loadSavedGame)
                {
                    player.x = Integer.parseInt(grS[4]);
                    player.y = Integer.parseInt(grS[5]);
                }
                Music.stopAudio();
                Music.playAudio(AssetManager.getMusic(grS[6]));
                grid.cells = null;
                enemies.clear();
                itemsOnMap.clear();
                treasureChests.clear();
                shots.clear();
                doors.clear();
                npcs.clear();
                grid.cells = new Cell[gridW][gridH];
                String[] orig = scan.nextLine().split("-9");
                int counter = 0;
                while(counter < orig.length)
                {
                    String[] refined = orig[counter].split("-");
                    refined[1] = refined[1].substring(1);
                    refined[2] = refined[2].substring(1);
                    refined[3] = refined[3].substring(1);
                    int[] r = {Integer.parseInt(refined[0]), Integer.parseInt(refined[1]), Integer.parseInt(refined[2]), Integer.parseInt(refined[3])};
                    //System.out.println(r[0] + "," + r[1] + "/" + r[2] + "/" + r[3]);
                    grid.cells[r[0]][r[1]] = new Cell(r[2], r[0], r[1], 10, r[3]);
                    grid.cells[r[0]][r[1]].setType(r[2]);
                    counter++;
                }
                String[] doordat = scan.nextLine().split(";");
                for(int i = 0; i < doordat.length; i++)
                {
                    String[] d = doordat[i].split(",");
                    doors.add(new Door(Integer.parseInt(d[0]), Integer.parseInt(d[1]), d[2], AssetManager.getImage(d[3])));
                }
                String[] npcdat = scan.nextLine().split("P64R6T3");
                for(int i = 0; i < npcdat.length; i++)
                {
                    String[] n = npcdat[i].split("P64R6T2");
                    String[] msgs = n[3].split("P64R6T1");
                    if(msgs[0].equals(" "))
                    {
                        msgs = null;
                    }
                    String[] umsgs = n[4].split("P64R6T1");
                    if(umsgs[0].equals(" "))
                    {
                        umsgs = null;
                    }
                    String[] itms = n[7].split("P64R6T1");
                    Item[] items = new Item[itms.length];
                    for(int j = 0; j < items.length; j++)
                    {
                        items[j] = AssetManager.getItem(itms[j]);
                        if(items[j] != null)
                        {
                            try
                            {
                                MeleeWeapon m = (MeleeWeapon) items[j];
                                m.setReferencePoint(player);
                                items[j] = m;
                            }catch(Exception e){}
                        }
                    }
                    npcs.add(new NPC(Integer.parseInt(n[0]), Integer.parseInt(n[1]), n[2], msgs, umsgs, Integer.parseInt(n[5]), AssetManager.getImage(n[6]), items));
                }
                String[] tcdat = scan.nextLine().split(";");
                for(int i = 0; i < tcdat.length; i++)
                {
                    String[] tc = tcdat[i].split(",");
                    Item[] items = null;
                    if(tc.length-3 > 0 && tc[3] != null && !tc[3].equals("") && !tc[3].equals(" "))
                    {
                        items = new Item[tc.length-3];
                        for(int j = 3; j < tc.length; j++)
                        {
                            items[j-3] = AssetManager.getItem(tc[j]);
                        }
                    }
                    treasureChests.add(new TreasureChest(Integer.parseInt(tc[0]), Integer.parseInt(tc[1]), AssetManager.getImage(tc[2]), items));
                }
                String[] endat = scan.nextLine().split(":");
                for(int i = 0; i < endat.length; i++)
                {
                    String[] en = endat[i].split(";");
                    HashMap<Item, Double> drops = new HashMap();
                    for(int j = 5; j < en.length; j++)
                    {
                        drops.put(AssetManager.getItem(en[j].split(",")[0]), Double.parseDouble(en[j].split(",")[1]));
                    }
                    addEnemy(en[0], Integer.parseInt(en[1]), Integer.parseInt(en[2]), drops, AssetManager.getImage(en[3]), Double.parseDouble(en[4]));
                }
                b = new Background(0, 148, AssetManager.getImage(grS[2]), Integer.parseInt(grS[3]));
                //updateGame();
                //b = new Background(0, 148, AssetManager.getImage(grS[2]), Integer.parseInt(grS[3]));
                scan.close();
            }
            else
            {
                JOptionPane.showMessageDialog(null, "The map " + "resources/maps/" + map.replaceFirst(".map","")+"-"+fIndex+".map" + " does not exist!", "Error", JOptionPane.OK_OPTION);
            }
            t.start();
        }
        catch (Exception ex)
        {
            //Main.logger.log(Level.SEVERE, "Error in readMap() method in Game.class");
            //ex.printStackTrace();
            t.start();
        }
    }
    
    /**
     * Heals the player based on the given potion jar.
     * 
     * @param p The potion jar to get the health/energy from.
     */
    public void potionGet(PotionJar p)
    {
        if(p.type == PotionJar.HEALTH)
        {
            int heal = p.getHeal((int)Math.round(player.maxHealth-player.health));
            player.health += heal;
            if(heal != 0)
            {
                player.damages.add(new DamageText(g, player, "+"+heal, Color.GREEN));
            }
        }
        else
        {
            int heal = p.getHeal((int)Math.round(player.maxEnergy-player.energy));
            player.energy += heal;
            if(heal != 0)
            {
                player.damages.add(new DamageText(g, player, "+"+heal, Color.GREEN));
            }
        }
    }
    
    /**
     * Loads the player's save game.
     */
    private void loadSaveGame()
    {
        try
        {
            Scanner s = new Scanner(saveFile);
            String pName = s.nextLine();
            player.name = pName;
            String[] line1 = s.nextLine().split(",");
            player.x = Double.parseDouble(line1[0]);
            player.y = Double.parseDouble(line1[1]);
            player.xSpeed = Double.parseDouble(line1[2]);
            player.currentGrav = Double.parseDouble(line1[3]);
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            String[] line2 = s.nextLine().split(",");
            player.level = Integer.parseInt(line2[0]);
            player.xpneeded = Integer.parseInt(line2[2]);
            player.xp = Integer.parseInt(line2[1]);
            player.health = Double.parseDouble(line2[3]);
            player.energy = Double.parseDouble(line2[4]);
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            String[] stats = s.nextLine().split(",");
            player.stamina = Integer.parseInt(stats[0]);
            player.potential = Integer.parseInt(stats[1]);
            player.defense = Integer.parseInt(stats[2]);
            player.strength = Integer.parseInt(stats[3]);
            player.speed = Integer.parseInt(stats[4]);
            player.intelligence = Integer.parseInt(stats[5]);
            player.luck = Integer.parseInt(stats[6]);
            player.levelUpPoints = Integer.parseInt(stats[7]);
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            String[] line3 = s.nextLine().split(";");
            String[] inventory = line3[Player.INVENTORY].split(",");
            for(int i = 0; i < inventory.length; i++)
            {
                if(inventory[i].toLowerCase().equals("ability"))
                {
                    int[] dmgRange = {10,20};
                    player.allItems[Player.INVENTORY][i] = new Ability("Fire Spray", "A spray of fire that radiates out from target.", 100, 200, false, dmgRange, AssetManager.getImage("ability1"), AssetManager.getImage("shot2"), 20, 1.5);
                }
                else if(inventory[i].toLowerCase().equals("ability2"))
                {
                    int[] dmgRange = {20,30};
                    player.allItems[Player.INVENTORY][i] = new Ability("Fiery Explosion", "Deadly fire spews out from the target location.", 100, 200, false, dmgRange, AssetManager.getImage("ability1"), AssetManager.getImage("bigShot"), 20, 1.5);
                }
                else if(inventory[i].toLowerCase().equals("ability3"))
                {
                    int[] dmgRange = {30,45};
                    player.allItems[Player.INVENTORY][i] = new Ability("Blazing Nova", "Incredibly powerful bursts of fire emanate from the target location.", 100, 200, false, dmgRange, AssetManager.getImage("ability1"), AssetManager.getImage("hugeShot"), 20, 1.5);
                }
                else
                {
                    player.allItems[Player.INVENTORY][i] = AssetManager.getItem(inventory[i]);
                }
            }
            String[] weapons = line3[Player.WEAPONS].split(",");
            for(int i = 0; i < weapons.length; i++)
            {
                player.allItems[Player.WEAPONS][i] = AssetManager.getItem(weapons[i]);
            }
            String[] abilities = line3[Player.ABILITIES].split(",");
            for(int i = 0; i < abilities.length; i++)
            {
                if(abilities[i].toLowerCase().equals("ability"))
                {
                    int[] dmgRange = {10,20};
                    player.allItems[Player.ABILITIES][i] = new Ability("Fire Spray", "A spray of fire that radiates out from target.", 100, 200, false, dmgRange, AssetManager.getImage("ability1"), AssetManager.getImage("shot2"), 20, 1.5);
                }
                else if(abilities[i].toLowerCase().equals("ability2"))
                {
                    int[] dmgRange = {20,30};
                    player.allItems[Player.ABILITIES][i] = new Ability("Fiery Explosion", "Deadly fire spews out from the target location.", 100, 200, false, dmgRange, AssetManager.getImage("ability1"), AssetManager.getImage("bigShot"), 20, 1.5);
                }
                else if(abilities[i].toLowerCase().equals("ability3"))
                {
                    int[] dmgRange = {30,45};
                    player.allItems[Player.ABILITIES][i] = new Ability("Blazing Nova", "Incredibly powerful bursts of fire emanate from the target location.", 100, 200, false, dmgRange, AssetManager.getImage("ability1"), AssetManager.getImage("hugeShot"), 20, 1.5);
                }
                else
                {
                    player.allItems[Player.ABILITIES][i] = AssetManager.getItem(abilities[i]);
                }
            }
            player.allItems[Player.HEAD][0] = AssetManager.getItem(line3[Player.HEAD]);
            player.allItems[Player.BODY][0] = AssetManager.getItem(line3[Player.BODY]);
            player.allItems[Player.FOOT][0] = AssetManager.getItem(line3[Player.FOOT]);
            String[] potions = line3[Player.POTIONS].split(",");
            for(int i = 0; i < potions.length; i++)
            {
                player.allItems[Player.POTIONS][i] = AssetManager.getItem(potions[i]);
            }
            //////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////////
            String[] line4 = s.nextLine().split(":");
            currMap = line4[0];
            System.out.println(currMap+".map");
            readMap(currMap+".map");
            if(line4.length > 1)
            {
                /*if(line4[1] != null && !line4[1].equals(" "))
                {
                    String[] tcs = line4[1].split(";");
                    for(int i = 0; i < tcs.length; i++)
                    {
                        String[] tc = tcs[i].split(",");
                        if(tc.length > 3)
                        {
                            Item[] tcitems = new Item[tc.length-3];
                            for(int j = 3; j < tc.length; j++)
                            {
                                tcitems[j-3] = AssetManager.getItem(tc[j]);
                            }
                            treasureChests.add(new TreasureChest(Integer.parseInt(tc[0]), Integer.parseInt(tc[1]), AssetManager.getImage(tc[2]), tcitems));
                        }
                    }   
                }*/
                String[] itsOnMap = line4[1].split(";");
                for(int i = 0; i < itsOnMap.length; i++)
                {
                    String[] itm = itsOnMap[i].split(",");
                    Item it = AssetManager.getItem(itm[0]);
                    it.isOnMap = true;
                    it.xOnMap = Double.parseDouble(itm[1]);
                    it.yOnMap = Double.parseDouble(itm[2]);
                    itemsOnMap.add(it);
                }
            }
            String[] potsOnMap = s.nextLine().split(";");
            for(int i = 0; i < potsOnMap.length; i++)
            {
                String[] pot = potsOnMap[i].split(",");
                PotionJar pt = (PotionJar) AssetManager.getItem(pot[0]);
                pt.isOnMap = true;
                pt.xOnMap = Double.parseDouble(pot[1]);
                pt.yOnMap = Double.parseDouble(pot[2]);
                pt.checkWidth.remove(pt.currVolume + "/" + pt.maxHealAmount);
                pt.currVolume = Integer.parseInt(pot[3]);
                pt.checkWidth.put((pt.currVolume + "/" + pt.maxHealAmount), Color.RED);
                itemsOnMap.add(pt);
            }
            s.close();
        }
        catch (Exception ex)
        {
            //Main.logger.log(Level.SEVERE, "Error in method loadSaveFile() in Game.class");
            ex.printStackTrace();
        }
    }
    
    /**
     * Saves the game.
     */
    public void saveGame()
            //Line 1: playerx, playery, xSpeed, maxSpeed, currentGrav
            //Line 2: player level, xp, xpneeded, health, maxHealth, energy, maxEnergy, shootSpeed, healthRegen, energyRegen
            //Line 3: player inventory, weapons, abilities, armors, potions (each item is separated by semicolon, each category is separated by colon)
            //Line 4: current map file name, all treasureChests, all itemsOnmap
    {
        try
        {
            FileWriter fw = new FileWriter(saveFile);
            fw.write(player.name + "\n");
            fw.write((int)player.x + "," + (int)player.y + "," + (int)player.xSpeed + "," + (int)player.currentGrav + "\n");
            fw.write(player.level + "," + player.xp + "," + player.xpneeded + "," + (int)player.health + "," + (int)player.energy + "\n");
            fw.write(player.stamina + "," + player.potential + "," + player.defense + "," + player.strength + "," + player.speed + "," + player.intelligence + "," + player.luck + "," + player.levelUpPoints + "\n");
            for(int i = 0; i < player.allItems.length; i++)
            {
                for(int j = 0; j < player.allItems[i].length; j++)
                {
                    if(player.allItems[i][j] == null)
                    {
                        fw.write("null");
                    }
                    else
                    {
                        fw.write(player.allItems[i][j].name);
                    }
                    if(j != player.allItems[i].length-1)
                    {
                        fw.write(",");
                    }
                }
                fw.write(";"); //Since each item is separated by comma, a semicolon is used to signify the start of a new set of items.
            }
            fw.write("\n");
            String mapp = currMap.substring(0,currMap.length()-4);
            fw.write(mapp + ":"); //Yes, colon.
            /*if(treasureChests != null && !treasureChests.isEmpty())
                for(int i = 0; i < treasureChests.size(); i++)
                {
                    fw.write(treasureChests.get(i).exportTC());
                    if(i != treasureChests.size()-1)
                    {
                        fw.write(";");
                    }
                }
            else
                fw.write(" ");
            fw.write(":");*/
            for(int i = 0; i < itemsOnMap.size(); i++)
            {
                if(!(itemsOnMap.get(i) instanceof PotionJar))
                {
                    fw.write(itemsOnMap.get(i).export());
                    if(i != itemsOnMap.size()-1)
                    {
                        fw.write(";"); //In this loop, each item is separated by a semicolon.
                    }
                }
            }
            fw.write("\n");
            for(int i = 0; i < itemsOnMap.size(); i++)
            {
                if(itemsOnMap.get(i) instanceof PotionJar)
                {
                    fw.write(itemsOnMap.get(i).export());
                    if(i != itemsOnMap.size()-1)
                    {
                        fw.write(";"); //In this loop, each item is separated by a semicolon.
                    }
                }
            }
            fw.close();
            String cc = currMap.replaceFirst(".map", "");
            export(new File("resources/maps/"+cc+"-"+fIndex+".map"));
        }
        catch (Exception e)
        {
            Main.logger.log(Level.SEVERE, "Error in method saveGame() in Game.class");
            e.printStackTrace();
        }
    }
    
    /**
     * Adds an enemy to the map at the mouse's position. This was intended to be
     * used as a testing mechanism. In "admin mode" you could access this method
     * by pressing '6', but this is disabled in the actual game.
     * 
     * @param s The name of the enemy. Used to put the correct enemy in the map.
     */
    public void addEnemy(String s, double difficulty)
    {
        int randX = (int)(Math.random()*31)-15;
        int randY = (int)(Math.random()*31)-15;
        if(s.equalsIgnoreCase("gavin"))
        {
            HashMap<Item, Double> d = new HashMap();
            d.put(AssetManager.getItem("Great Health Potion Jar"), 40.0);
            d.put(AssetManager.getItem("Great Energy Potion Jar"), 40.0);
            d.put(AssetManager.getItem("Legend's Sword"), 1.0);
            d.put(AssetManager.getItem("Big Fabulous Sword"),2.0);
            d.put(AssetManager.getItem("Ultimate Weapon"), 1.0);
            enemies.add(new Gavin("gavin", mouseX-cameraX+randX, mouseY-cameraY+randY, d, AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("enemy"))
        {
            HashMap<Item, Double> drops = new HashMap();
            drops.put(AssetManager.getItem("Health Potion Jar"), 30.0);
            drops.put(AssetManager.getItem("Energy Potion Jar"), 30.0);
            drops.put(AssetManager.getItem("Moonblade"), 1.0);
            drops.put(AssetManager.getItem("Fire Gun"), 2.0);
            drops.put(AssetManager.getItem("Piercer"), 1.5);
            drops.put(AssetManager.getItem("Heavy Energy Bolter"), 2.0);
            enemies.add(new Enemy("enemy", mouseX-cameraX+randX, mouseY-cameraY+randY, drops, AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("ufo"))
        {
            HashMap<Item, Double> drops = new HashMap();
            drops.put(AssetManager.getItem("Super Health Potion Jar"), 20.0);
            drops.put(AssetManager.getItem("Super Energy Potion Jar"), 20.0);
            enemies.add(new UFO("ufo", mouseX-cameraX+randX, mouseY-cameraY+randY, drops, AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("chaser"))
        {
            HashMap<Item, Double> drops = new HashMap();
            drops.put(AssetManager.getItem("light sword"), 1.0);
            enemies.add(new Chaser("chaser", mouseX-cameraX+randX, mouseY-cameraY+randY, drops, AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("superchaser"))
        {
            HashMap<Item, Double> drops = new HashMap();
            drops.put(AssetManager.getItem("light sword"), 3.0);
            enemies.add(new SuperChaser("superchaser", mouseX-cameraX+randX, mouseY-cameraY+randY, drops, AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("hyperchaser"))
        {
            HashMap<Item, Double> drops = new HashMap();
            drops.put(AssetManager.getItem("light sword"), 5.0);
            drops.put(AssetManager.getItem("Great Health Potion Jar"), 15.0);
            drops.put(AssetManager.getItem("Great Health Potion Jar"), 15.0);
            enemies.add(new HyperChaser("hyperchaser", mouseX-cameraX+randX, mouseY-cameraY+randY, drops, AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("icicledropper"))
        {
            HashMap<Item, Double> drops = new HashMap();
            drops.put(AssetManager.getItem("popsicle sword"), 10.0);
            enemies.add(new IcicleDropper("icicledropper", mouseX-cameraX+randX, mouseY-cameraY+randY, drops, AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("testEnemy"))
        {
            enemies.add(new TestEnemy("testEnemy", mouseX-cameraX+randX, mouseY-cameraY+randY, new HashMap(), AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("boss1"))
        {
            enemies.add(new Boss1("boss1", mouseX-cameraX+randX, mouseY-cameraY+randY, new HashMap(), AssetManager.getImage(s), difficulty));
        }
    }
    
    /**
     * This method adds an enemy to the game. This method is used when loading
     * a map, so the correct enemies can be loaded into the correct locations on
     * the map.
     * 
     * @param s         The name of the enemy.
     * @param x         The x-location to put the enemy.
     * @param y         The y-location to put the enemy.
     * @param drops     A HashMap representing the drops of this enemy (see the Enemy class for more info).
     * @param img       The enemy's image.
     */
    public void addEnemy(String s, int x, int y, HashMap<Item,Double> drops, Image img, double difficulty)
    {
        if(s.equalsIgnoreCase("gavin"))
        {
            enemies.add(new Gavin("gavin", x, y, drops, img, difficulty));
        }
        else if(s.equalsIgnoreCase("enemy"))
        {
            enemies.add(new Enemy("enemy", x, y, drops, img, difficulty));
        }
        else if(s.equalsIgnoreCase("ufo"))
        {
            enemies.add(new UFO("ufo", x, y, drops, img, difficulty));
        }
        else if(s.equalsIgnoreCase("chaser"))
        {
            enemies.add(new Chaser("chaser", x, y, drops, img, difficulty));
        }
        else if(s.equalsIgnoreCase("superchaser"))
        {
            enemies.add(new SuperChaser("superchaser", x, y, drops, img, difficulty));
        }
        else if(s.equalsIgnoreCase("hyperchaser"))
        {
            enemies.add(new HyperChaser("hyperchaser", x, y, drops, img, difficulty));
        }
        else if(s.equalsIgnoreCase("icicledropper"))
        {
            enemies.add(new IcicleDropper("icicledropper", x, y, drops, img, difficulty));
        }
        else if(s.equalsIgnoreCase("testEnemy"))
        {
            enemies.add(new TestEnemy("testEnemy", x, y, new HashMap(), img, difficulty));
        }
        else if(s.equalsIgnoreCase("boss1"))
        {
            enemies.add(new Boss1("boss1", x, y, new HashMap(), img, difficulty));
        }
    }
}