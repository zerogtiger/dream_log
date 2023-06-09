import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;

public abstract class Level {

    // Number associated with level; number of cells in x, y direction, respectively
    private int levelNum, xLength, yLength;
    // Grid stores map data; One cell is 8 x 8 pixels
    private int[][] grid;
    private Map<Pair, int[]> update;
    private long timeElapsedms;

    public Level(int levelNum, String gridFile, int shiftx, int shifty) {
        this.levelNum = levelNum;
        update = new HashMap<>();
        timeElapsedms = 0;

        // Read in grid info
        try {
            BufferedReader br = new BufferedReader(new FileReader(gridFile));
            yLength = 1;
            xLength = br.readLine().length();
            while (br.readLine() != null)
            yLength++;
            grid = new int[xLength+0][yLength+0];
            br.close();
            br = new BufferedReader(new FileReader(gridFile));
            String line;
            for (int y = yLength-1; y >= 0; y--) {
                line = br.readLine();
                for (int x = 0; x < xLength; x++) {
                    grid[x][y] = Integer.parseInt(line.charAt(x) + "");
                }
            }
            br.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(e);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    // Getters
    public int getLevelNum() {
        return levelNum;
    }

    public int[][] getGrid() {
        return grid;
    }

    public int getGrid(int x, int y) {
        return grid[x][y];
    }

    public int getXLength() {
        return xLength;
    }

    public int getYLength() {
        return yLength;
    }

    public abstract void update();

}
