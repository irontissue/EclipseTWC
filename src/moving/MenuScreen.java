package moving;

import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.ActionEvent;
import java.awt.event.ActionListener;
import javax.swing.JPanel;
import javax.swing.Timer;
import java.awt.Color;
import java.awt.Graphics2D;
import java.awt.Image;
import java.awt.RenderingHints;
import java.awt.event.MouseEvent;
import java.awt.event.MouseListener;
import java.io.File;
import java.util.ArrayList;
import java.util.Scanner;
import javax.swing.JOptionPane;

/**
 * Handles all updating and drawing for the initial title/menu screen.
 * 
 * @author EclipseTWC
 */
public class MenuScreen extends JPanel
{
    private boolean drawCredits = false;
    
    private String charName = "";
    private String f1Name = "<Empty>";
    private String f2Name = "<Empty>";
    private String f3Name = "<Empty>";
    
    private Message msg;
    
    public Graphics g;
    
    private int fileChosen = 0, fIndex = 0;
    private int menuAnimIndex = 0, menuAnimTimer = 0;
    private int order = 0; //0 is nothing (at menu screen still), 1 is new game, 2 is load game
    
    private Main m;
    
    private ArrayList<Button> buttons = new ArrayList();
    
    public Timer t;
    
    private File f;
    
    private Image[] menuAnim = new Image[12];
    private Image logo = AssetManager.getImage("eclipseLogo");
    private Image credits = AssetManager.getImage("credits");
    
    /**
     * Default constructor.
     * 
     * @param m A copy of the Main class.
     */
    public MenuScreen(Main m)
    {
        this.m = m;
        setFocusable(true);
        requestFocusInWindow();
        for(int i = 0; i < menuAnim.length; i++)
        {
            menuAnim[i] = AssetManager.getImage("menu"+(i+1)+"");
        }
        buttons.add(new Button((Main.FRAME_WIDTH-100)/2, 250, 100, 40, "New Game", Color.GREEN, "New Game"));
        buttons.add(new Button((Main.FRAME_WIDTH-100)/2, 310, 100, 40, "Load Game", Color.GREEN, "Load Game"));
        buttons.add(new Button((Main.FRAME_WIDTH-100)/2, 370, 100, 40, "Credits", new Color(100,100,255), "Credits"));
        buttons.add(new Button((Main.FRAME_WIDTH-100)/2, 430, 100, 40, "Exit", Color.LIGHT_GRAY, "Exit"));
        try
        {
            File f1 = new File("saveData1.dat");
            File f2 = new File("saveData2.dat");
            File f3 = new File("saveData3.dat");
            if(f1.exists())
            {
                Scanner sc = new Scanner(f1);
                f1Name = sc.nextLine();
                sc.close();
            }
            if(f2.exists())
            {
                Scanner sc = new Scanner(f2);
                f2Name = sc.nextLine();
                sc.close();
            }
            if(f3.exists())
            {
                Scanner sc = new Scanner(f3);
                f3Name = sc.nextLine();
                sc.close();
            }
        }
        catch(Exception e)
        {
            
        }
        t = new Timer(16, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent ae)
            {
                try
                {
                    menuAnimTimer++;
                    if(menuAnimTimer >= 30)
                    {
                        menuAnimTimer=0;
                        menuAnimIndex++;
                        if(menuAnimIndex > menuAnim.length-1)
                        {
                            menuAnimIndex = 0;
                        }
                    }
                    if(msg!=null)
                    {
                        if(msg.getClass().equals(UnlockedMessage.class))
                        {
                            if(msg.waitingForConfirm)
                            {
                                ((UnlockedMessage) msg).timer ++;
                                if(((UnlockedMessage) msg).timer >= 180)
                                {
                                    ((UnlockedMessage) msg).timer = 0;
                                    msg.update = true;
                                }
                            }
                            if(msg.remove)
                            {
                                msg = null;
                            }
                        }
                        if(msg != null)
                        {
                            msg.updateMessage(MenuScreen.this);
                        }
                    }
                    if(Fade.getAlpha() == 0)
                    {
                        if(getOrder() == 1)
                        {
                            if(getFileChosen() == 1)
                            {
                                f = new File("saveData1.dat");
                            }
                            else if(getFileChosen() == 2)
                            {
                                f = new File("saveData2.dat");
                            }
                            else if(getFileChosen() == 3)
                            {
                                f = new File("saveData3.dat");
                            }
                            if(f != null)
                            {
                                if(f.exists())
                                {
                                    int i = JOptionPane.showConfirmDialog(null, "Are you sure you want to create a new\nfile in \"" + f.getName() + "\"?\nThis will delete the data existing\nin that file.", "Confirm", JOptionPane.YES_NO_OPTION);
                                    if(i == 0)
                                    {
                                        boolean done = false;
                                        while(!done)
                                        {
                                            charName = JOptionPane.showInputDialog(null, "Give a name to your character:", "Name?", JOptionPane.QUESTION_MESSAGE);
                                            if(charName != null)
                                            {
                                                charName = charName.trim();
                                                if(!charName.equals("") && !charName.equals("<Empty>") && charName.length() < 15)
                                                {
                                                    done = true;
                                                }
                                            }
                                        }
                                        f.delete();
                                        f.createNewFile();
                                        Fade.fade();
                                        fileChosen = 0;
                                    }
                                    else
                                    {
                                        fileChosen = 0;
                                        f = null;
                                    }
                                }
                                else
                                {
                                    boolean done = false;
                                    while(!done)
                                    {
                                        charName = JOptionPane.showInputDialog(null, "Give a name to your character:", "Name?", JOptionPane.QUESTION_MESSAGE);
                                        if(charName != null)
                                        {
                                            charName = charName.trim();
                                            if(!charName.equals("") && !charName.equals("<Empty>") && charName.length() < 15)
                                            {
                                                done = true;
                                            }
                                        }
                                    }
                                    f.delete();
                                    f.createNewFile();
                                    Fade.fade();
                                    fileChosen = 0;
                                }
                            }
                        }
                        else if(getOrder() == 2)
                        {
                            if(getFileChosen() == 1)
                            {
                                f = new File("saveData1.dat");
                            }
                            else if(getFileChosen() == 2)
                            {
                                f = new File("saveData2.dat");
                            }
                            else if(getFileChosen() == 3)
                            {
                                f = new File("saveData3.dat");
                            }
                            if(f != null && f.exists())
                            {
                                fileChosen = 0;
                                Fade.fade();
                            }
                            else if(f != null)
                            {
                                if(msg == null || !msg.text.equals("That file does not exist. Please make a new game in that file."))
                                {
                                    msg = new UnlockedMessage("That file does not exist. Please make a new game in that file.", false, AssetManager.getImage("blank"));
                                    msg.update = true;
                                }
                            }
                        }
                    }
                    else if(Fade.reversing())
                    {
                        MenuScreen.this.m.startGame(f, fIndex, charName);
                    }
                    repaint();
                }
                catch (Exception e)
                {
                    //Main.logger.log(Level.SEVERE, "Error in MenuScreen actionPerformed method");
                    e.printStackTrace();
                }
            }
        });
        t.start();
        
        addMouseListener(new MouseListener()
        {

            public void mouseClicked(MouseEvent me)
            {
                
            }

            public void mousePressed(MouseEvent me)
            {
                if(Fade.getAlpha() == 0)
                {
                    for(Button b : buttons)
                    {
                        if(b.hits(me.getX(), me.getY()))
                        {
                            b.pressed = true;
                        }
                    }
                }
            }

            public void mouseReleased(MouseEvent me)
            {
                for(int i = 0; i < buttons.size(); i++)
                {
                    Button b = buttons.get(i);
                    if(b.hits(me.getX(), me.getY()) && b.pressed)
                    {
                        if(b.getID().equalsIgnoreCase("New Game"))
                        {
                            i = 9999;
                            order = 1;
                            buttons.clear();
                            Main.updateSaveFileDeletion();
                            buttons.add(new Button((Main.FRAME_WIDTH)/2-200, 400, 100, 40, f1Name, Color.LIGHT_GRAY, "File 1"));
                            buttons.add(new Button((Main.FRAME_WIDTH)/2-50, 400, 100, 40, f2Name, Color.LIGHT_GRAY, "File 2"));
                            buttons.add(new Button((Main.FRAME_WIDTH)/2+100, 400, 100, 40, f3Name, Color.LIGHT_GRAY, "File 3"));
                            buttons.add(new Button(10, 10, 100, 40, "Back", Color.LIGHT_GRAY, "Back"));
                            msg = new UnlockedMessage("Select a file in which you want to make a new game:", false, AssetManager.getImage("blank"));
                            msg.update = true;
                        }
                        else if(b.getID().equalsIgnoreCase("Load Game"))
                        {
                            Main.updateSaveFileDeletion();
                            buttons.clear();
                            order = 2;
                            buttons.add(new Button((Main.FRAME_WIDTH)/2-200, 400, 100, 40, f1Name, Color.LIGHT_GRAY, "File 1"));
                            buttons.add(new Button((Main.FRAME_WIDTH)/2-50, 400, 100, 40, f2Name, Color.LIGHT_GRAY, "File 2"));
                            buttons.add(new Button((Main.FRAME_WIDTH)/2+100, 400, 100, 40, f3Name, Color.LIGHT_GRAY, "File 3"));
                            buttons.add(new Button(10, 10, 100, 40, "Back", Color.LIGHT_GRAY, "Back"));
                            msg = new UnlockedMessage("Select a file to load:", false, AssetManager.getImage("blank"));
                            msg.update = true;
                        }
                        else if(b.getID().equalsIgnoreCase("Credits"))
                        {
                            //msg = new UnlockedMessage("Credits: Eclipse - The Waxing Crescent: Team Leader/Lead Programmer: Ashok Sankaran Graphics: Gavin Love and Richard Wu Concept, Management, and Music: Kareem Abouleish", false, AssetManager.getImage("blank"));
                            //msg.update = true;
                            buttons.clear();
                            buttons.add(new Button(10,10,100,40,"Back",Color.LIGHT_GRAY,"Back"));
                            msg = null;
                            drawCredits = true;
                        }
                        else if(b.getID().equalsIgnoreCase("File 1"))
                        {
                            fileChosen = 1;
                            fIndex = 1;
                        }
                        else if(b.getID().equalsIgnoreCase("File 2"))
                        {
                            fileChosen = 2;
                            fIndex = 2;
                        }
                        else if(b.getID().equalsIgnoreCase("File 3"))
                        {
                            fileChosen = 3;
                            fIndex = 3;
                        }
                        else if(b.getID().equalsIgnoreCase("Back"))
                        {
                            i = 9999;
                            order = 0;
                            fileChosen = 0;
                            f = null;
                            buttons.clear();
                            buttons.add(new Button((Main.FRAME_WIDTH-100)/2, 250, 100, 40, "New Game", Color.GREEN, "New Game"));
                            buttons.add(new Button((Main.FRAME_WIDTH-100)/2, 300, 100, 40, "Load Game", Color.GREEN, "Load Game"));
                            buttons.add(new Button((Main.FRAME_WIDTH-100)/2, 350, 100, 40, "Credits", new Color(100,100,255), "Credits"));
                            buttons.add(new Button((Main.FRAME_WIDTH-100)/2, 400, 100, 40, "Exit", Color.LIGHT_GRAY, "Exit"));
                            drawCredits = false;
                            msg = null;
                        }
                        else if(b.getID().equalsIgnoreCase("exit"))
                        {
                            Main.updateSaveFileDeletion();
                            System.exit(0);
                        }
                    }
                    if(b != null)
                    {
                        b.pressed = false;
                    }
                }
            }

            public void mouseEntered(MouseEvent me)
            {
                
            }

            public void mouseExited(MouseEvent me)
            {
                
            }
            
        });
    }
    
    /**
     * Paints the menu screen.
     * 
     * @param g The object on which the graphics is drawn.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        this.g = g;
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.clearRect(0,0,Main.FRAME_WIDTH, Main.FRAME_HEIGHT);
        g.drawImage(menuAnim[menuAnimIndex], 0, 0, null);
        g.setColor(new Color(100,100,100,140));
        g.fillRect(0,0,getWidth(),getHeight());
        g.drawImage(logo, 50, 20, null);
        g.setFont(new Font(Game.DEFAULT_FONT, Font.PLAIN, 18));
        g.setColor(Color.RED);
        if(msg!=null)
        {
            msg.drawMessage(g);
        }
        for(Button b : buttons)
        {
            b.drawButton(g);
        }
        if(drawCredits)
        {
            g.drawImage(credits, (Main.FRAME_WIDTH-credits.getWidth(null))/2, (Main.FRAME_HEIGHT-credits.getHeight(null))/2+100, this);
        }
        else if(order != 0)
        {
            try
            {
                if(!f1Name.equals("<Empty>"))
                {
                    Scanner sc = new Scanner(new File("saveData1.dat"));
                    sc.nextLine();
                    sc.nextLine();
                    String[] splitt = sc.nextLine().split(",");
                    sc.nextLine();
                    sc.nextLine();
                    String[] splitty = sc.nextLine().split(":");
                    sc.close();
                    g.drawString("Level: " + splitt[0], Main.FRAME_WIDTH/2-200, 378);
                    g.drawString("Current Map: " + splitty[0], Main.FRAME_WIDTH/2-200, 395);
                }
                if(!f2Name.equals("<Empty>"))
                {
                    Scanner sc = new Scanner(new File("saveData2.dat"));
                    sc.nextLine();
                    sc.nextLine();
                    String[] splitt = sc.nextLine().split(",");
                    sc.nextLine();
                    sc.nextLine();
                    String[] splitty = sc.nextLine().split(":");
                    sc.close();
                    g.drawString("Level: " + splitt[0], Main.FRAME_WIDTH/2-200, 378);
                    g.drawString("Current Map: " + splitty[0], Main.FRAME_WIDTH/2-200, 395);
                }
                if(!f3Name.equals("<Empty>"))
                {
                    Scanner sc = new Scanner(new File("saveData3.dat"));
                    sc.nextLine();
                    sc.nextLine();
                    String[] splitt = sc.nextLine().split(",");
                    sc.nextLine();
                    sc.nextLine();
                    String[] splitty = sc.nextLine().split(":");
                    sc.close();
                    g.drawString("Level: " + splitt[0], Main.FRAME_WIDTH/2-200, 378);
                    g.drawString("Current Map: " + splitty[0], Main.FRAME_WIDTH/2-200, 395);
                }
            }
            catch(Exception ex)
            {
                ex.printStackTrace();
            }
        }
        g.setColor(new Color(0,0,0,Fade.getAlpha()));
        g.fillRect(0, 0, getWidth(), getHeight());
    }
    
    /**
     * Fetches the file chosen by the player (1,2, or 3). A value of 0 means a
     * file hasn't been chosen.
     * 
     * @return an int representing the file chosen.
     */
    public int getFileChosen()
    {
        return fileChosen;
    }
    
    /**
     * Fetches whether the player is creating a new game or loading a saved
     * game. 1 means new game, 2 means load game, 0 means the user is still
     * at the first screen.
     * 
     * @return an int representing the choice chosen at the title screen (new game or load game).
     */
    public int getOrder()
    {
        return order;
    }
}