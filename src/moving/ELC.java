package moving;

import java.awt.Cursor;
import java.awt.Graphics;
import javax.swing.JFrame;
import javax.swing.JOptionPane;

/**
 * ELC stands for Eclipse Level Creator.
 * Thic class initializes the entire level creator system.
 * 
 * @author EclipseTWC
 */
public class ELC extends JFrame
{
    private boolean init = true;
    
    /**
     * Default constructor. Prompts user for preferred width/height of the grid,
     * then creates the grid.
     */
    public ELC()
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
                ELCWorker ew = new ELCWorker(w,h);
                add(ew);
                ew.requestFocusInWindow();
                valid = true;
            }catch(Exception ex){}
        }
        setCursor(Cursor.getPredefinedCursor(Cursor.CROSSHAIR_CURSOR));
        setSize(800,800);
        setLocationRelativeTo(null);
        setTitle("Eclipse Level Creator");
        setName("Eclipse Level Creator");
        setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        setResizable(true);
        setVisible(true);
    }
    
    /**
     * Paints the frame. Initializes the images by calling the imageInit() method
     * in the AssetManager class.
     * 
     * @param g The object on which the graphics is drawn.
     */
    @Override
    public void paint(Graphics g)
    {
        if(init)
        {
            init = false;
            AssetManager.imageInit(g);
        }
    }
}
