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

    private final Pair[] portalCheck = {
        new Pair(0, 1), 
        new Pair(1, 0),
        new Pair(0, -1),
        new Pair(-1, 0)
    };

    public Level(int levelNum, String gridFile, String updateFile, int shiftx, int shifty, double startX, double startY) {
        this.levelNum = levelNum;
        update = new HashMap<>();
        timeElapsedms = 0;

        Game.player.setX(startX);
        Game.player.setY(startY);

        // Read in grid info such that the grid coordinate system has (0, 0) at the bottom left corner
        try {
            BufferedReader br = new BufferedReader(new FileReader(gridFile));
            yLength = 1;
            xLength = br.readLine().length();
            String line;
            while ((line = br.readLine()) != null && !line.equals(""))
                yLength++;
            grid = new int[xLength+0][yLength+0];
            br.close();
            br = new BufferedReader(new FileReader(gridFile));
            for (int y = yLength-1; y >= 0; y--) {
                line = br.readLine();
                for (int x = 0; x < xLength; x++) {
                    grid[x][y] = Integer.parseInt(line.charAt(x) + "");
                }
            }
            br.close();
            
            // Teleportation info
            String[] tmpUpdateEntry;
            br = new BufferedReader(new FileReader(updateFile));
            while ((line = br.readLine()) != null && !line.equals("")) {
                tmpUpdateEntry = line.split(" ");
                // System.out.println(Arrays.toString(tmpUpdateEntry));
                if (tmpUpdateEntry.length == 4) {
                    update.put(new Pair(Integer.parseInt(tmpUpdateEntry[0]), Integer.parseInt(tmpUpdateEntry[1])), new int[]{Integer.parseInt(tmpUpdateEntry[2]), Integer.parseInt(tmpUpdateEntry[3])});
                }
                else if (tmpUpdateEntry.length == 5) {
                    update.put(new Pair(Integer.parseInt(tmpUpdateEntry[0]), Integer.parseInt(tmpUpdateEntry[1])), new int[]{Integer.parseInt(tmpUpdateEntry[2]), Integer.parseInt(tmpUpdateEntry[3]), Integer.parseInt(tmpUpdateEntry[4])});
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

    public void update() {
        double playerx = Game.player.getX(), playery = Game.player.getY();
        if (update.containsKey(new Pair((int) playerx, (int) playery))) {
            int[] value = update.get(new Pair((int) playerx, (int) playery));
            int length = value.length;
            if (length == 2) {
                Game.player.setX(playerx - (int) playerx + value[0]);
                Game.player.setY(playery - (int) playery + value[1]);
            }
            int quadrant = value[0]/90;
            boolean flag = portalCheck[quadrant].f * (playerx - (int) playerx - 0.5) >= 0 && portalCheck[quadrant].s * (playery - (int) playery - 0.5) >= 0;
            // System.out.println(flag);
            // System.out.println(playerx);
            // System.out.println(portalCheck[quadrant].f * (playerx - (int) playerx - 0.5));
            if (length == 3 && Math.abs((Game.player.getFacing() + 45)%360 - (value[0] + 45)%360) <= 25 && flag) {
                Game.player.setX(playerx - (int) playerx + value[1]);
                Game.player.setY(playery - (int) playery + value[2]);
            }
        }
    }
}
