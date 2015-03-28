package moving;

import java.awt.Graphics;
import java.awt.Point;
import java.util.ArrayList;

/**
 * The Grid which is the map on which objects will interact.
 * 
 * @author EclipseTWC
 */
public class Grid
{
    public Cell[][] cells;
    
    public static final int CELL_SIZE = 15;
    
    /**
     * Creates a 2d array of Cells, making one layer of ground and leaving the
     * rest empty. This happens usually when the game is started and the map to
     * be loaded is not found, so the grid will default to a randomly
     * generated map in which there is nothing to do.
     * 
     * @param cols  Number of columns in the grid
     * @param rows  Number of rows in the grid.
     */
    public Grid(int cols, int rows)
    {
        //System.out.println(Grid.CELL_SIZE);
        cells = new Cell[cols][rows];
        for(int x = 0; x < cols; x++)
        {
            for(int y = 0; y < rows; y++)
            {
                if(x <=1 || y <= 1 || x >= cols-2 || y >= rows-2)
                {
                    cells[x][y] = new Cell(Cell.VILLAGE_STONE, x, y, 10, 0);
                }
                else
                {
                    cells[x][y] = new Cell(Cell.NULL, x, y, 10, 0);
                }
            }
        }
        for(int y = 0; y < rows; y+=5)
        {
            int col = (int)(Math.random()*cols/3);
            int col2 = (int)(Math.random()*cols/3+cols/3.0);
            int col3 = (int)(Math.random()*cols/3+1.5*cols/3.0);
            int randomLength = (int)(Math.random()*23)+5;
            for(int p = 0; p < randomLength; p++)
            {
                cells[col+p][y].setType(Cell.VILLAGE_STONE);
                cells[col2+p][y].setType(Cell.VILLAGE_STONE);
                cells[col3+p][y].setType(Cell.VILLAGE_STONE);
            }
        }
    }
    
    /**
     * Draws the grid.
     * 
     * @param g Graphics object that each cell is drawn on.
     */
    public void drawGrid(Graphics g)
    {
        for(int x = (-Game.cameraX-CELL_SIZE)/CELL_SIZE; x <= (-Game.cameraX+Main.FRAME_WIDTH)/CELL_SIZE; x++)
        {
            for(int y = (-Game.cameraY-CELL_SIZE)/CELL_SIZE; y <= (-Game.cameraY+Main.FRAME_HEIGHT)/CELL_SIZE; y++)
            {
                try
                {
                    cells[x][y].drawCell(g);
                    cells[x][y].discovered = true;
                }
                catch(Exception ex){}
            }
        }
    }
    
    /**
     * Updates the grid. Note that the water positions's y coordinates are
     * searched backwards (bottom to top), since I want the water to all be
     * updated at the same time.
     */
    public void updateGrid()
    {
        for(int y = cells[0].length-1; y >= 0; y--) //backwards y sort for water
        {
            for(int x = 0; x < cells.length; x+=1)
            {
                ArrayList<Point> waterMoveLocs = new ArrayList();
                if(cells[x][y].getType() == Cell.WATER)
                {
                    if(x+1 != cells.length && cells[x+1][y].getType() == Cell.NULL)
                    {
                        waterMoveLocs.add(new Point(x+1,y));
                    }
                    if(x-1 != -1 && cells[x-1][y].getType() == Cell.NULL)
                    {
                        waterMoveLocs.add(new Point(x-1,y));
                    }
                    waterMoveLocs.add(new Point(x,y));
                    int random = (int) (Math.random()*waterMoveLocs.size());
                    if(!waterMoveLocs.isEmpty())
                    {
                        cells[x][y].setType(Cell.NULL);
                        cells[waterMoveLocs.get(random).x][waterMoveLocs.get(random).y].setType(Cell.WATER);
                        if(y+1 != cells[0].length && cells[waterMoveLocs.get(random).x][y+1].getType() == Cell.NULL)
                        {
                            cells[waterMoveLocs.get(random).x][y+1].setType(Cell.WATER);
                            cells[waterMoveLocs.get(random).x][y].setType(Cell.NULL);
                        }
                    }
                    else
                    {
                        if(y+1 != cells[0].length && cells[x][y+1].getType() == Cell.NULL)
                        {
                            cells[x][y+1].setType(Cell.WATER);
                            cells[x][y].setType(Cell.NULL);
                        }
                    }
                }
            }
        }
    }
}
