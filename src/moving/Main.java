package moving;

import java.awt.Color;
import java.awt.Cursor;
import java.awt.Font;
import java.awt.Graphics;
import java.awt.event.WindowEvent;
import java.io.File;
import java.io.FileWriter;
import java.util.Scanner;
import java.util.logging.FileHandler;
import java.util.logging.Level;
import java.util.logging.Logger;
import java.util.logging.SimpleFormatter;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * This class is the Main manager. It initializes the game and/or map editor.
 * 
 * @author EclipseTWC
 */
public class Main extends JFrame
{
    public static Game game;
    public MenuScreen m;
    
    public static final int FRAME_WIDTH = 600, FRAME_HEIGHT = 650;
    
    private static int defaultCloseOperation = JFrame.HIDE_ON_CLOSE;
    private static final int SPECIAL_EXIT_ON_CLOSE = 4; //used for overriding java's window close event.
    
    public static Logger logger;
    
    /**
     * Default constructor. Initializes frame properties.
     */
    public Main()
    {
        //System.out.println(Grid.CELL_SIZE);
        setBackground(Color.BLACK);
        setSize(FRAME_WIDTH, FRAME_HEIGHT);
        setCursor(Cursor.getPredefinedCursor(1));
        setResizable(false);
        setLocationRelativeTo(null);
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setTitle("Eclipse - The Waxing Crescent");
        setName("Eclipse - The Waxing Crescent");
        m = new MenuScreen(this);
        add(m);
        m.requestFocusInWindow();
        setVisible(true);
    }
    
    /**
     * The main method. Will load all data necessary for the game, then initiate
     * the logging file, and initiate the game.
     * 
     * @param args the command line arguments
     */
    public static void main(String[] args)
    {
        try
        {
            AssetManager.loadAllMedia();
            AssetManager.loadItemData();
            updateSaveFileDeletion();
            duplicateMaps();
            int choice = JOptionPane.showConfirmDialog(null, "Do you want to use the map editor?", " ", JOptionPane.YES_NO_OPTION);
            if(choice == 0)
            {
                new ELC();
            }
            else
            {
                Music.playAudio(AssetManager.getMusic("title"));
                logger = Logger.getLogger(Main.class.getName());
                FileHandler fh;
                fh = new FileHandler("resources/log.txt", true);
                SimpleFormatter sf = new SimpleFormatter();
                fh.setFormatter(sf);
                logger.addHandler(fh);
                new Main();
                updateSaveFileDeletion();
            }
        }
        catch (Exception e)
        {
            e.printStackTrace();
            logger.log(Level.SEVERE, "Error in main() method in Main.class");
        }
    }
    
    /**
     * Paints the screen. We made this a loading screen.
     * 
     * @param g The object on which the graphics will be drawn.
     */
    @Override
    public void paint(Graphics g)
    {
        AssetManager.imageInit(g);
        g.setColor(Color.WHITE);
        g.setFont(new Font(Game.DEFAULT_FONT, Font.PLAIN, 13));
        g.drawString("Loading...", FRAME_WIDTH/2-25, FRAME_HEIGHT/2);
    }
    
    /**
     * Starts the game, removes the menu. If the player starts a new game,
     * then this method will delete all existing maps for that save file
     * and re-create them.
     * 
     * @param f             The savefile in which player save data will be stored.
     * @param playerName    The name the player chose or is loaded.
     * @param fIndex        The index (1, 2, or 3) of the file chosen.
     */
    public void startGame(File f, int fIndex, String playerName)
    {
        try
        {
            if(m.getOrder() == 1)
            {
                remove(m);
                m.t.stop();
                m = null;
                File[] files = new File("resources/maps").listFiles();
                for(int i = 0; i < files.length; i++)
                {
                    char[] charray = files[i].getName().replaceFirst(".map", "").toCharArray();
                    if(!(charray[0] == '.' && charray[1] == '_') && (charray[charray.length-2] == '-' && Integer.parseInt(Character.toString(charray[charray.length-1])) == fIndex))
                    {
                        files[i].delete();
                    }
                }
                duplicateMaps();
                game = new Game(this, f, fIndex, playerName);
                add(game);
                game.requestFocusInWindow();
                setVisible(true);
            }
            else if(m.getOrder() == 2)
            {
                remove(m);
                m.t.stop();
                m = null;
                game = new Game(this, f, fIndex, playerName);
                game.loadSavedGame = true;
                add(game);
                game.requestFocusInWindow();
                setVisible(true);
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error in startGame() method in Main.class");
            e.printStackTrace();
        }
    }
    
    /**
     * Goes back to the menuscreen. Removes the game and adds the menuscreen.
     */
    public void backToMenu()
    {
        try
        {
            updateSaveFileDeletion();
            Game.cameraX = 0;
            Game.cameraY = 0;
            try{remove(game);}catch(Exception ex){}
            game.t.stop();
            game = null;
            m = new MenuScreen(this);
            add(m);
            m.requestFocusInWindow();
            setVisible(true);
            Music.playAudio(AssetManager.getMusic("title"));
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error in backToMenu() method in Main.class");
            e.printStackTrace();
        }
    }
    
    /**
     * Updates the save files; if the save files are empty but exist, then it
     * deletes the file.
     */
    public static void updateSaveFileDeletion()
    {
        try
        {
            File f1 = new File("saveData1.dat");
            File f2 = new File("saveData2.dat");
            File f3 = new File("saveData3.dat");
            if(f1.exists())
            {
                Scanner s = new Scanner(f1);
                try
                {
                    String ss = s.nextLine();
                    s.nextLine();
                    s.nextLine();
                    s.close();
                }
                catch (Exception e)
                {
                    s.close();
                    f1.delete();
                }
            }
            if(f2.exists())
            {
                Scanner s = new Scanner(f2);
                try
                {
                    String ss = s.nextLine();
                    s.nextLine();
                    s.nextLine();
                    s.close();
                }
                catch (Exception e)
                {
                    s.close();
                    f2.delete();
                }
            }
            if(f3.exists())
            {
                Scanner s = new Scanner(f3);
                try
                {
                    String ss = s.nextLine();
                    s.nextLine();
                    s.nextLine();
                    s.close();
                }
                catch (Exception e)
                {
                    s.close();
                    f3.delete();
                }
            }
        }
        catch (Exception e)
        {
            logger.log(Level.SEVERE, "Error in updateSaveFileDeletion() method in Main.class");
            e.printStackTrace();
        }
    }
    
    /**
     * Duplicates maps for each save file, if needed.
     */
    public static void duplicateMaps()
    {
        try
        {
            File[] mapsToDupe = new File("resources/maps").listFiles();
            for(int i = 0; i < mapsToDupe.length; i++)
            {
                char[] charray = mapsToDupe[i].getName().replaceFirst(".map", "").toCharArray();
                if(!(charray[0] == '.' && charray[1] == '_') && charray[charray.length-2] != '-')
                {
                    File f1 = new File("resources/maps/"+mapsToDupe[i].getName().replaceFirst(".map", "")+"-1.map");
                    File f2 = new File("resources/maps/"+mapsToDupe[i].getName().replaceFirst(".map", "")+"-2.map");
                    File f3 = new File("resources/maps/"+mapsToDupe[i].getName().replaceFirst(".map", "")+"-3.map");
                    if(!f1.exists())
                    {
                        f1.createNewFile();
                        FileWriter fw = new FileWriter(f1);
                        Scanner scan = new Scanner(mapsToDupe[i]);
                        while(scan.hasNext())
                        {
                            fw.write(scan.nextLine() + "\n");
                        }
                        scan.close();
                        fw.close();
                    }
                    else
                    {
                        Scanner sc = new Scanner(f1);
                        String ss = sc.nextLine().trim();
                        sc.close();
                        if(ss.equals(""))
                        {
                            FileWriter fw = new FileWriter(f1);
                            Scanner scan = new Scanner(mapsToDupe[i]);
                            while(scan.hasNext())
                            {
                                fw.write(scan.nextLine() + "\n");
                            }
                            scan.close();
                            fw.close();
                        }
                    }
                    if(!f2.exists())
                    {
                        f2.createNewFile();
                        FileWriter fw = new FileWriter(f2);
                        Scanner scan = new Scanner(mapsToDupe[i]);
                        while(scan.hasNext())
                        {
                            fw.write(scan.nextLine() + "\n");
                        }
                        scan.close();
                        fw.close();
                    }
                    else
                    {
                        Scanner sc = new Scanner(f2);
                        String ss = sc.nextLine().trim();
                        sc.close();
                        if(ss.equals(""))
                        {
                            FileWriter fw = new FileWriter(f2);
                            Scanner scan = new Scanner(mapsToDupe[i]);
                            while(scan.hasNext())
                            {
                                fw.write(scan.nextLine() + "\n");
                            }
                            scan.close();
                            fw.close();
                        }
                    }
                    if(!f3.exists())
                    {
                        f3.createNewFile();
                        FileWriter fw = new FileWriter(f3);
                        Scanner scan = new Scanner(mapsToDupe[i]);
                        while(scan.hasNext())
                        {
                            fw.write(scan.nextLine() + "\n");
                        }
                        scan.close();
                        fw.close();
                    }
                    else
                    {
                        Scanner sc = new Scanner(f3);
                        String ss = sc.nextLine().trim();
                        sc.close();
                        if(ss.equals(""))
                        {
                            FileWriter fw = new FileWriter(f3);
                            Scanner scan = new Scanner(mapsToDupe[i]);
                            while(scan.hasNext())
                            {
                                fw.write(scan.nextLine() + "\n");
                            }
                            scan.close();
                            fw.close();
                        }
                    }
                }
            }
        }
        catch (Exception ex)
        {
            ex.printStackTrace();
        }
    }
    
    /**
     * Overrides the JFrame's DefaultCloseOperation method, so that whenever
     * the player exits the game, the updateSaveFileDeletion() method can be
     * called.
     * 
     * @param operation The default close operation.
     */
    @Override
    public void setDefaultCloseOperation(int operation) {
	if (operation != DO_NOTHING_ON_CLOSE &&
	    operation != HIDE_ON_CLOSE &&
	    operation != DISPOSE_ON_CLOSE &&
	    operation != EXIT_ON_CLOSE &&
            operation != SPECIAL_EXIT_ON_CLOSE) {
            throw new IllegalArgumentException("defaultCloseOperation must be one of: DO_NOTHING_ON_CLOSE, HIDE_ON_CLOSE, DISPOSE_ON_CLOSE, SPECIAL_EXIT_ON_CLOSE, or EXIT_ON_CLOSE");
	}
        if (defaultCloseOperation != operation) {
            if (operation == EXIT_ON_CLOSE || operation == SPECIAL_EXIT_ON_CLOSE) {
                SecurityManager security = System.getSecurityManager();
                if (security != null) {
                    security.checkExit(0);
                }
            }
            int oldValue = defaultCloseOperation;
            defaultCloseOperation = operation;
            firePropertyChange("defaultCloseOperation", oldValue, operation);
	}
    }
    
    /**
     * Refer to JFrame's processWindowEvent method for more info. This method
     * needed to be overridden because we added the SPECIAL_EXIT_ON_CLOSE parameter,
     * which will invoke the exitProgram() method (also made by us), which will
     * exit the program after managing the logger files.
     * 
     * @param e The action the window has received. The program will respond
     *          based on the defaultCloseOperation.
     */
    @Override
    public void processWindowEvent(WindowEvent e)
    {
        super.processWindowEvent(e);
        if (e.getID() == WindowEvent.WINDOW_CLOSING)
        {
            switch (defaultCloseOperation)
            {
                case HIDE_ON_CLOSE:
                    setVisible(false);
                    break;
                case DISPOSE_ON_CLOSE:
                    dispose();
                    break;
                case DO_NOTHING_ON_CLOSE:
                default:                        
                    break;
                case EXIT_ON_CLOSE:
                    // This needs to match the checkExit call in
                    // setDefaultCloseOperation
                    System.exit(0);
                    break;
                case SPECIAL_EXIT_ON_CLOSE:
                    updateSaveFileDeletion();
                    System.exit(0);
                    break;
            }
        }
    }
}
