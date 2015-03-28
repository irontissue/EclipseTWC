package moving;

import java.awt.Color;
import java.awt.Font;
import java.awt.FontMetrics;
import java.awt.Graphics;
import java.awt.Graphics2D;
import java.awt.Image;
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
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Scanner;
import javax.swing.JOptionPane;
import javax.swing.JPanel;
import javax.swing.Timer;

/**
 * The Worker class for the Eclipse Level Creator. Takes care of all updating
 * and actually draws the level creator.
 * 
 * @author EclipseTWC
 */
public class ELCWorker extends JPanel
{
    private int cameraX = 15, cameraY = 15;
    private int typeSetter = 0;
    private int spawnX = 100, spawnY = 100;
    private int backgroundRepeatType = 0;
    public int mouseX = 0, mouseY = 0;
    
    private String music = "city";
    
    private boolean fake = false;
    
    public static ELCCell[][] grid;
    
    private ArrayList<Door> doors = new ArrayList();
    private ArrayList<TreasureChest> chests = new ArrayList();
    private ArrayList<NPC> npcs = new ArrayList();
    private ArrayList<Enemy> enemies = new ArrayList();
    
    private Timer t;
    
    private Image backgroundImg = AssetManager.getImage("forest");
    
    /**
     * Default constructor.
     * 
     * @param gridWidth     The grid's (map's) width, in cells.
     * @param gridHeight    The grid's (map's) height, in cells.
     */
    public ELCWorker(int gridWidth, int gridHeight)
    {
        setFocusable(true);
        requestFocusInWindow();
        initGrid(gridWidth, gridHeight);
        t = new Timer(16, new ActionListener()
        {
            @Override
            public void actionPerformed(ActionEvent e)
            {
                if(mouseX < 15 && -cameraX > -15)
                {
                    cameraX += 3;
                }
                else if(mouseX > ELCWorker.this.getWidth()-15 && -cameraX+ELCWorker.this.getWidth() < grid[grid.length-1][0].getX()+ELCCell.CELL_SIZE+15)
                {
                    cameraX -= 3;
                }
                if(mouseY < 15 && -cameraY > -15)
                {
                    cameraY += 3;
                }
                else if(mouseY > ELCWorker.this.getHeight()-15 && -cameraY+ELCWorker.this.getHeight() < grid[0][grid[0].length-1].getY()+ELCCell.CELL_SIZE+15)
                {
                    cameraY -= 3;
                }
                repaint();
            }
        });
        t.start();
        
        addMouseListener(new MouseListener()
        {

            @Override
            public void mouseClicked(MouseEvent e)
            {
                
            }

            @Override
            public void mousePressed(MouseEvent e)
            {
                int cellX = (e.getX()-cameraX)/ELCCell.CELL_SIZE;
                int cellY = (e.getY()-cameraY)/ELCCell.CELL_SIZE;
                if(cellX > -1 && cellX < grid.length && cellY > -1 && cellY < grid[0].length)
                {
                    grid[cellX][cellY].setType(ELCCell.types[typeSetter]);
                    if(fake)
                    {
                        grid[cellX][cellY].setReal(ELCCell.FAKE);
                    }
                    else
                    {
                        grid[cellX][cellY].setReal(ELCCell.REAL);
                    }
                }
            }

            @Override
            public void mouseReleased(MouseEvent e)
            {
                
            }

            @Override
            public void mouseEntered(MouseEvent e)
            {
                
            }

            @Override
            public void mouseExited(MouseEvent e)
            {
                
            }
        });
        
        addMouseMotionListener(new MouseMotionListener()
        {

            @Override
            public void mouseDragged(MouseEvent e)
            {
                mouseX = e.getX();
                mouseY = e.getY();
                int cellX = (e.getX()-cameraX)/ELCCell.CELL_SIZE;
                int cellY = (e.getY()-cameraY)/ELCCell.CELL_SIZE;
                if(cellX > -1 && cellX < grid.length && cellY > -1 && cellY < grid[0].length)
                {
                    grid[cellX][cellY].setType(ELCCell.types[typeSetter]);
                    if(fake)
                    {
                        grid[cellX][cellY].setReal(ELCCell.FAKE);
                    }
                    else
                    {
                        grid[cellX][cellY].setReal(ELCCell.REAL);
                    }
                }
            }

            @Override
            public void mouseMoved(MouseEvent e)
            {
                mouseX = e.getX();
                mouseY = e.getY();
            }
        });
        
        addMouseWheelListener(new MouseWheelListener()
        {
            @Override
            public void mouseWheelMoved(MouseWheelEvent e)
            {
                typeSetter -= e.getWheelRotation();
                if(typeSetter > ELCCell.types.length-1)
                {
                    typeSetter = 0;
                }
                else if(typeSetter < 0)
                {
                    typeSetter = ELCCell.types.length-1;
                }
            }
        });
        
        addKeyListener(new KeyListener()
        {
            @Override
            public void keyTyped(KeyEvent e)
            {
                
            }

            @Override
            public void keyPressed(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_E)
                {
                    String s = JOptionPane.showInputDialog(null, "Give a name to your map.\nDo not include the .map extension.", "Export Map", JOptionPane.QUESTION_MESSAGE);
                    File f = new File("resources/maps/"+s+".map");
                    if(f.exists())
                    {
                        int choice = JOptionPane.showConfirmDialog(null, "There is already a map named " + s + ".map.\nAre you sure you want to replace it?", "Export Map", JOptionPane.YES_NO_OPTION);
                        if(choice == 0)
                        {
                            f.delete();
                            try
                            {
                                f.createNewFile();
                            }
                            catch(Exception ex)
                            {
                                ex.printStackTrace();
                            }
                            export(f);
                        }
                    }
                    else
                    {
                        export(f);
                    }
                }
                else if(e.getKeyCode() == KeyEvent.VK_M)
                {
                    music = JOptionPane.showInputDialog(null, "Enter the name of the music for this map.\nDo not include the extension.", "Set Music", JOptionPane.QUESTION_MESSAGE);
                }
                else if(e.getKeyCode() == KeyEvent.VK_I)
                {
                    String s = JOptionPane.showInputDialog(null, "Enter the name of the map to import.\nDo not include the .map extension.", "Import Map", JOptionPane.QUESTION_MESSAGE);
                    File f = new File("resources/maps/"+s+".map");
                    readMap(f);
                }
                else if(e.getKeyCode() == KeyEvent.VK_D)
                {
                    t.stop();
                    if(typeSetter == 0)
                    {
                        for(int i = 0; i < doors.size(); i++)
                        {
                            Door d = doors.get(i);
                            if(mouseX-cameraX > d.x && mouseX-cameraX < d.x+d.img.getWidth(null) &&
                                    mouseY-cameraY > d.y && mouseY-cameraY < d.y+d.img.getHeight(null))
                            {
                                doors.remove(d);
                                i--;
                            }
                        }
                    }
                    else
                    {
                        String s = JOptionPane.showInputDialog(null, "Enter the name of the door's image.\nDo not include the extension.", "Create Door", JOptionPane.QUESTION_MESSAGE);
                        String s2 = JOptionPane.showInputDialog(null, "Enter the name of the map the door will\nlink to. Do not include the .map extension.", "Create Door", JOptionPane.QUESTION_MESSAGE);
                        if(!(s == null || s2 == null || s.equals("") || s2.equals("")) && AssetManager.getImage(s) != null)
                        {
                            doors.add(new Door(mouseX-cameraX, mouseY-cameraY, s2, AssetManager.getImage(s)));
                        }
                    }
                    t.start();
                }
                else if(e.getKeyCode() == KeyEvent.VK_N)
                {
                    t.stop();
                    if(typeSetter == 0)
                    {
                        for(int i = 0; i < npcs.size(); i++)
                        {
                            NPC d = npcs.get(i);
                            if(mouseX-cameraX > d.x && mouseX-cameraX < d.x+d.img.getWidth(null) &&
                                    mouseY-cameraY > d.y && mouseY-cameraY < d.y+d.img.getHeight(null))
                            {
                                npcs.remove(d);
                                i--;
                            }
                        }
                    }
                    else
                    {
                        try
                        {
                            String imgName = JOptionPane.showInputDialog(null, "Enter the name of the NPC's image.\nDo not include the extension.", "Create NPC", JOptionPane.QUESTION_MESSAGE);
                            String firstMessage = JOptionPane.showInputDialog(null, "Enter the first message the NPC will say.\nThis message will not be recycled.", "Create NPC", JOptionPane.QUESTION_MESSAGE);
                            String[] msgs = JOptionPane.showInputDialog(null, "Enter all of the locked messages the NPC\nwill say. If there will be no locked messages,\nleave this blank.\nSeparate messages with this special code:\nP64R6T1", "Create NPC", JOptionPane.QUESTION_MESSAGE).split("P64R6T1");
                            String[] umsgs = JOptionPane.showInputDialog(null, "Enter all of the unlocked messages the NPC\nwill say. If there will be no unlocked messages,\nleave this blank.\nSeparate messages with this special code:\nP64R6T1", "Create NPC", JOptionPane.QUESTION_MESSAGE).split("P64R6T1");
                            int recycle = Integer.parseInt(JOptionPane.showInputDialog(null, "How will the NPC recycle his messages (Enter a number)\n0 = Do not recycle\n1 = recycle all messages\n2 = recycle unlocked only\n3 = recycle locked only", "Create NPC", JOptionPane.QUESTION_MESSAGE));
                            String[] itms = JOptionPane.showInputDialog(null, "Enter all of the items the NPC can drop. If there will be no items to drop,\nleave this blank.\nSeparate items with this special code:\nP64R6T1", "Create NPC", JOptionPane.QUESTION_MESSAGE).split("P64R6T1");
                            Item[] items = null;
                            if(itms != null && itms.length != 0)
                            {
                                items = new Item[itms.length];
                                for(int i = 0; i < itms.length; i++)
                                {
                                    items[i] = AssetManager.getItem(itms[i]);
                                }
                            }
                            if(!(imgName == null || firstMessage == null || imgName.equals("") || firstMessage.equals("")) && AssetManager.getImage(imgName) != null)
                            {
                                npcs.add(new NPC(mouseX-cameraX, mouseY-cameraY, firstMessage, msgs, umsgs, recycle, AssetManager.getImage(imgName), items));
                            }
                        }
                        catch(Exception ex)
                        {
                            JOptionPane.showMessageDialog(null, "You have entered an invalid input.\nPlease try again.", "Create NPC - Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    t.start();
                }
                else if(e.getKeyCode() == KeyEvent.VK_T)
                {
                    t.stop();
                    if(typeSetter == 0)
                    {
                        for(int i = 0; i < chests.size(); i++)
                        {
                            TreasureChest d = chests.get(i);
                            if(mouseX-cameraX > d.xOnMap && mouseX-cameraX < d.xOnMap+d.img.getWidth(null) &&
                                    mouseY-cameraY > d.yOnMap && mouseY-cameraY < d.yOnMap+d.img.getHeight(null))
                            {
                                chests.remove(d);
                                i--;
                            }
                        }
                    }
                    else
                    {
                        try
                        {
                            String imgName = JOptionPane.showInputDialog(null, "Enter the name of the chest's image.\nDo not include \"Opened\" or \"Closed\" at the\nend, that will be automatically done.\nDo not include the extension for the image.", "Create Treasure Chest", JOptionPane.QUESTION_MESSAGE)+"closed";
                            String[] itms = JOptionPane.showInputDialog(null, "Enter all of the items the chest can drop.\nLeave this blank if you want to purposefully\nmake this treasure chest contain no items.\nSeparate items with commas.", "Create Treasure Chest", JOptionPane.QUESTION_MESSAGE).split(",");
                            Item[] items = null;
                            if(itms != null && itms.length != 0)
                            {
                                items = new Item[itms.length];
                                for(int i = 0; i < itms.length; i++)
                                {
                                    items[i] = AssetManager.getItem(itms[i]);
                                    System.out.println(itms[i] + "/" + items[i]);
                                }
                            }
                            if(!(imgName == null || imgName.equals("")) && AssetManager.getImage(imgName) != null)
                            {
                                chests.add(new TreasureChest(mouseX-cameraX, mouseY-cameraY, AssetManager.getImage(imgName), items));
                            }
                        }
                        catch(Exception ex)
                        {
                            JOptionPane.showMessageDialog(null, "You have entered an invalid input.\nPlease try again.", "Create Treasure Chest - Error", JOptionPane.ERROR_MESSAGE);
                        }
                    }
                    t.start();
                }
                else if(e.getKeyCode() == KeyEvent.VK_ENTER)
                {
                    t.stop();
                    int choice = JOptionPane.showConfirmDialog(null, "Are you sure you want to create a new map?\nMake sure you save your current map.", "New Map?", JOptionPane.YES_NO_OPTION);
                    if(choice == 0)
                    {
                        boolean valid = false;
                        while(!valid)
                        {
                            try
                            {
                                String i = JOptionPane.showInputDialog(null, "Enter width and height, separated by a comma.", "Grid size?", JOptionPane.QUESTION_MESSAGE);
                                if(i == null || i.equals(""))
                                    System.exit(0);
                                String[] gridSize = i.split(",");
                                int w = Integer.parseInt(gridSize[0].trim());
                                int h = Integer.parseInt(gridSize[1].trim());
                                grid = null;
                                enemies.clear();
                                doors.clear();
                                npcs.clear();
                                chests.clear();
                                initGrid(w,h);
                                valid = true;
                            }catch(Exception ex){}
                        }
                    }
                    t.start();
                }
                else if(e.getKeyCode() == KeyEvent.VK_P)
                {
                    spawnX = mouseX-cameraX;
                    spawnY = mouseY-cameraY;
                }
                else if(e.getKeyCode() == KeyEvent.VK_B)
                {
                    try
                    {
                        String s = JOptionPane.showInputDialog(null, "Enter the name of the background for this map.\nDo not include the extension.", "Background Set", JOptionPane.QUESTION_MESSAGE);
                        backgroundRepeatType = Integer.parseInt(JOptionPane.showInputDialog(null, "How will the background repeat?\n0 = does not repeat\n1 = repeats horizontally only\n2 = repeats vertically only\n3 = repeats in all directions", "Background set", JOptionPane.QUESTION_MESSAGE));
                        backgroundImg = AssetManager.getImage(s);
                        if(backgroundImg == null)
                        {
                            JOptionPane.showMessageDialog(null, "Background has been set to NULL.\n(The background was not found. Please try again.)", "Background set", JOptionPane.ERROR_MESSAGE);
                        }
                        else
                        {
                            JOptionPane.showMessageDialog(null, "Background has been set to: " + AssetManager.getImageSource(backgroundImg), "Background set", JOptionPane.INFORMATION_MESSAGE);
                        }
                    }
                    catch(Exception ex)
                    {
                        JOptionPane.showMessageDialog(null, "Error while setting background. You have\nentered something incorrect. Please try again.", "Background set - Error", JOptionPane.ERROR_MESSAGE);
                    }
                }
                else if(e.getKeyCode() == KeyEvent.VK_F)
                {
                    fake = true;
                }
                else if(e.getKeyCode() == KeyEvent.VK_Y)
                {
                    if(typeSetter == 0)
                    {
                        for(int i = 0; i < enemies.size(); i++)
                        {
                            Enemy d = enemies.get(i);
                            if(mouseX-cameraX > d.x && mouseX-cameraX < d.x+d.img.getWidth(null) &&
                                    mouseY-cameraY > d.y && mouseY-cameraY < d.y+d.img.getHeight(null))
                            {
                                enemies.remove(d);
                                i--;
                            }
                        }
                    }
                    else
                    {
                        String s = JOptionPane.showInputDialog(null, "Enter the image/name of the enemy.\nDo not include the extension.", "Enemy create", JOptionPane.QUESTION_MESSAGE);
                        String[] ss = JOptionPane.showInputDialog(null, "Enter the items that will drop from this enemy, a comma, then a double\nthat indicates the percent chance of dropping.\nMultiple items should be separated by semicolons. Example:\nbasic gun,20.0;legend's sword,0.1", "Enemy create", JOptionPane.QUESTION_MESSAGE).split(";");
                        boolean done = false;
                        double d = 1.0;
                        while(!done)
                        {
                            try
                            {
                                d = Double.parseDouble(JOptionPane.showInputDialog(null, "Input the difficulty of the enemy.\nThis is a value from 1.0-5.0", "Enemy Create", JOptionPane.QUESTION_MESSAGE));
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
                        HashMap<Item, Double> h = new HashMap();
                        for(int i = 0; i < ss.length; i++)
                        {
                            String[] split = ss[i].split(",");
                            try
                            {
                                Item it = AssetManager.getItem(split[0]);
                                if(it != null)
                                    h.put(it, Double.parseDouble(split[1]));
                            }catch(Exception ex){}
                        }
                        addEnemy(s, mouseX, mouseY, h, d);
                    }
                }
            }

            @Override
            public void keyReleased(KeyEvent e)
            {
                if(e.getKeyCode() == KeyEvent.VK_F)
                {
                    fake = false;
                }
            }
        });
    }
    
    /**
     * Paints/Draws all components of the level creator.
     * 
     * @param g The object on which the graphics is drawn.
     */
    @Override
    public void paintComponent(Graphics g)
    {
        Graphics2D g2d = (Graphics2D) g;
        g2d.setRenderingHint(RenderingHints.KEY_TEXT_ANTIALIASING, RenderingHints.VALUE_TEXT_ANTIALIAS_GASP);
        g.clearRect(0,0,getWidth(),getHeight());
        g.translate(cameraX, cameraY);
        g.setFont(new Font("Arial", Font.PLAIN, 14));
        for(int x = -cameraX/ELCCell.CELL_SIZE; x < (-cameraX+getWidth())/ELCCell.CELL_SIZE+1; x++)
        {
            for(int y = -cameraY/ELCCell.CELL_SIZE; y < (-cameraY+getHeight())/ELCCell.CELL_SIZE+1; y++)
            {
                if(x > -1 && x < grid.length && y > -1 && y < grid[0].length)
                    grid[x][y].drawCell(g);
            }
        }
        for(int i = 0; i < doors.size(); i++)
        {
            Door d = doors.get(i);
            g.drawImage(d.img, d.x, d.y, null);
        }
        for(int i = 0; i < npcs.size(); i++)
        {
            NPC n = npcs.get(i);
            n.drawNPC(g);
        }
        for(int i = 0; i < chests.size(); i++)
        {
            TreasureChest t = chests.get(i);
            g.drawImage(t.img, (int)t.xOnMap, (int)t.yOnMap, null);
        }
        for(int i = 0; i < enemies.size(); i++)
        {
            Enemy e = enemies.get(i);
            e.drawEnemy(g);
        }
        g.drawImage(AssetManager.getImage("tmp-5"), spawnX, spawnY, null);
        g.setColor(Color.BLACK);
        String ss = grid.length + "x" + grid[0].length;
        FontMetrics fm = g.getFontMetrics();
        g.drawString(ss, getWidth()-fm.stringWidth(ss)-cameraX-5, 40-cameraY);
        String sss = ELCCell.types[typeSetter];
        g.drawString(sss, getWidth()-fm.stringWidth(sss)-cameraX-5, 20-cameraY);
    }
    
    /**
     * Exports/Saves the map. Writes the map to the given File f.
     * 
     * @param f The file to save the map to.
     */
    public void export(File f)
    {
        FileWriter fw = null;
        try
        {
            fw = new FileWriter(f);
            fw.write(grid.length+","+grid[0].length+","+AssetManager.getImageSource(backgroundImg)+","+ backgroundRepeatType + ","+spawnX+","+spawnY+","+music+"\n");
            for(int x = 0; x < grid.length; x++)
            {
                for(int y = 0; y < grid[0].length; y++)
                {
                    int rand = (int)(Math.random()*9);
                    int rand2 = (int)(Math.random()*9);
                    int rand3 = (int)(Math.random()*9);
                    int index = 0;
                    for(int i = 0; i < ELCCell.types.length; i++)
                    {
                        if(ELCCell.types[i].equalsIgnoreCase(grid[x][y].getType()))
                        {
                            index = i;
                            i = ELCCell.types.length;
                        }
                    }
                    fw.write(x + "-" + rand + "" + y + "-" + rand2 + "" + index + "-" + rand3 + "" + grid[x][y].isReal() + "-9");
                    //System.out.println(x + "-" + rand + "" + y + "-" + rand2 + "" + index + "-" + rand3 + "" + grid[x][y].isReal() + "-9");
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
            for(int i = 0; i < chests.size(); i++)
            {
                fw.write(chests.get(i).exportTC() + ";");
            }
            fw.write("\n");
            for(int i = 0; i < enemies.size(); i++)
            {
                fw.write(enemies.get(i).name + ";" + (int)enemies.get(i).x + ";" + (int)enemies.get(i).y + ";" + AssetManager.getImageSource(enemies.get(i).img) + ";" + enemies.get(i).difficulty + ";");
                for(int j = 0; j < enemies.get(i).drops.size(); j++)
                {
                    Item it = (Item) enemies.get(i).drops.keySet().toArray()[j];
                    double d = (double)enemies.get(i).drops.values().toArray()[j];
                    if(it != null && it.name != null)
                    {
                        fw.write(it.name + "," + d);
                        if(j != enemies.get(i).drops.size()-1)
                        {
                            fw.write(";");
                        }
                    }
                }
                fw.write(":");
            }
            fw.close();
            JOptionPane.showMessageDialog(null, "Succesfully exported map!", "Export Map", JOptionPane.INFORMATION_MESSAGE);
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
    
    /**
     * Reads/Imports the map from the given file.
     * 
     * @param file The file from which to import the map.
     */
    public void readMap(File file)
    {
        try
        {
            if(file.exists())
            {
                Scanner scan = new Scanner(file);
                String[] grS = scan.nextLine().split(",");
                int gridW = Integer.parseInt(grS[0]);
                int gridH = Integer.parseInt(grS[1]);
                backgroundImg = AssetManager.getImage(grS[2]);
                backgroundRepeatType = Integer.parseInt(grS[3]);
                spawnX = Integer.parseInt(grS[4]);
                spawnY = Integer.parseInt(grS[5]);
                grid = null;
                chests.clear();
                enemies.clear();
                doors.clear();
                npcs.clear();
                grid = new ELCCell[gridW][gridH];
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
                    grid[r[0]][r[1]] = new ELCCell(r[0]*ELCCell.CELL_SIZE, r[1]*ELCCell.CELL_SIZE, ELCCell.types[r[2]], r[3]);
                    grid[r[0]][r[1]].setType(ELCCell.types[r[2]]);
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
                    chests.add(new TreasureChest(Integer.parseInt(tc[0]), Integer.parseInt(tc[1]), AssetManager.getImage(tc[2]), items));
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
                    enemies.add(new Enemy(en[0], Integer.parseInt(en[1]), Integer.parseInt(en[2]), drops, AssetManager.getImage(en[3]), Double.parseDouble(en[4])));
                }
                scan.close();
            }
            else
            {
                JOptionPane.showMessageDialog(null, "This map does not exist!", "Error", JOptionPane.OK_OPTION);
            }
        }
        catch(Exception e)
        {
            e.printStackTrace();
        }
    }
    
    /**
     * Initializes the grid. Makes the sides and bottom solid, as an initial
     * "shell" of the grid.
     * 
     * @param gridWidth     The grid's width, in cells..
     * @param gridHeight    The grid's height, in cells.
     */
    public void initGrid(int gridWidth, int gridHeight)
    {
        grid = new ELCCell[gridWidth][gridHeight];
        for(int x = 1; x < grid.length-1; x++)
        {
            for(int y = 0; y < grid[0].length-1; y++)
            {
                grid[x][y] = new ELCCell(ELCCell.CELL_SIZE*x, ELCCell.CELL_SIZE*y, "null", ELCCell.REAL);
            }
        }
        for(int x = 0; x < grid.length; x++)
        {
            grid[x][grid[0].length-1] = new ELCCell(ELCCell.CELL_SIZE*x, ELCCell.CELL_SIZE*(grid[0].length-1), "foreststone", ELCCell.REAL);
        }
        for(int y = 0; y < grid[0].length; y++)
        {
            grid[grid.length-1][y] = new ELCCell(ELCCell.CELL_SIZE*(grid.length-1), ELCCell.CELL_SIZE*y, "foreststone", ELCCell.REAL);
            grid[0][y] = new ELCCell(0, ELCCell.CELL_SIZE*y, "foreststone", ELCCell.REAL);
        }
    }
    
    /**
     * Adds an enemy to the list on enemies on the map.
     * 
     * @param s         The name of the enemy. Used to put the correct enemy in the map.
     * @param mouseX    The x-location to put the enemy.
     * @param mouseY    The y-location to put the enemy.
     * @param drops     A HashMap representing the drops of this enemy (see the Enemy class for more info).
     */
    public void addEnemy(String s, int mouseX, int mouseY, HashMap<Item, Double> drops, double difficulty)
    {
        if(s.equalsIgnoreCase("gavin"))
        {
            enemies.add(new Gavin("gavin", mouseX-cameraX, mouseY-cameraY, drops, AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("enemy"))
        {
            enemies.add(new Enemy("enemy", mouseX-cameraX, mouseY-cameraY, drops, AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("ufo"))
        {
            enemies.add(new UFO("ufo", mouseX-cameraX, mouseY-cameraY, drops, AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("chaser"))
        {
            enemies.add(new Chaser("chaser", mouseX-cameraX, mouseY-cameraY, drops, AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("superchaser"))
        {
            enemies.add(new SuperChaser("superchaser", mouseX-cameraX, mouseY-cameraY, drops, AssetManager.getImage("chaser"), difficulty));
        }
        else if(s.equalsIgnoreCase("hyperchaser"))
        {
            enemies.add(new HyperChaser("hyperchaser", mouseX-cameraX, mouseY-cameraY, drops, AssetManager.getImage("chaser"), difficulty));
        }
        else if(s.equalsIgnoreCase("icicledropper"))
        {
            enemies.add(new IcicleDropper("icicledropper", mouseX-cameraX, mouseY-cameraY, drops, AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("testEnemy"))
        {
            enemies.add(new TestEnemy("testEnemy", mouseX-cameraX, mouseY-cameraY, drops, AssetManager.getImage(s), difficulty));
        }
        else if(s.equalsIgnoreCase("boss1"))
        {
            enemies.add(new Boss1("boss1", mouseX-cameraX, mouseY-cameraY, drops, AssetManager.getImage(s), difficulty));
        }
    }
}
