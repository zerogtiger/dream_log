import javax.swing.*;
import javax.sound.sampled.*;
import java.awt.image.*;
import javax.imageio.ImageIO;
import java.util.*;
import java.io.*;
import java.awt.*;

public abstract class Level {

    // Number associated with level; number of cells in x, y direction, respectively
    private int levelNum, xLength, yLength;
    // Grid stores map data; One cell is 8 x 8 pixels
    private int[][] grid;
    // Map with teleportation info
    // Value of length 1: perform a specific function
    // Value of length 2: implicit teleportation in the same level (initial x, initial y, target x, target y)
    // Value of length 3: explicit teleportation in the same level (initial x, initial y, required rotation, target x, target y)
    // Value of length 4: explicit teleportation to different level (initial x, initial y, required rotation, target x, target y, target level)
    private Map<Pair, int[]> update;
    // Used to keep track of position from which player entered the level
    protected Pair exit;
    private BufferedImage image;

    private final Pair[] portalCheck = {
        new Pair(0, 1), 
        new Pair(1, 0),
        new Pair(0, -1),
        new Pair(-1, 0)
    };

    public Level(int levelNum, String gridFile, String updateFile, String levelOverlay, int shiftx, int shifty) {
        this.levelNum = levelNum;
        update = new HashMap<>();

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
                if (line.charAt(0) == '/')
                    continue;
                tmpUpdateEntry = line.split(" ");
                exit = new Pair(Integer.parseInt(tmpUpdateEntry[0]), Integer.parseInt(tmpUpdateEntry[1]));
                // System.out.println(Arrays.toString(tmpUpdateEntry));
                if (tmpUpdateEntry.length == 3) {
                    update.put(exit, new int[]{Integer.parseInt(tmpUpdateEntry[2])});
                }
                else if (tmpUpdateEntry.length == 4) {
                    update.put(exit, new int[]{Integer.parseInt(tmpUpdateEntry[2]), Integer.parseInt(tmpUpdateEntry[3])});
                }
                else if (tmpUpdateEntry.length == 5) {
                    update.put(exit, new int[]{Integer.parseInt(tmpUpdateEntry[2]), Integer.parseInt(tmpUpdateEntry[3]), Integer.parseInt(tmpUpdateEntry[4])});
                }
                else if (tmpUpdateEntry.length == 6) {
                    update.put(exit, new int[]{Integer.parseInt(tmpUpdateEntry[2]), Integer.parseInt(tmpUpdateEntry[3]), Integer.parseInt(tmpUpdateEntry[4]), Integer.parseInt(tmpUpdateEntry[5])});
                }
            }
            br.close();

            // Image
            image = ImageIO.read(new File(levelOverlay));
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
    
    // Setters
    public void setOrigin(int x, int y) {
        if (update.get(exit) != null) {
            update.get(exit)[1] = x;
            update.get(exit)[2] = y;
        }
    }

    public void render(Graphics g) {
        // g.drawImage(image, Game.SCREEN_WIDTH/2-(int) (Game.player.getX()*8), yLength*4-(int) (Game.player.getY()*8), xLength*8, -yLength*8, null);
        g.drawImage(image, Game.SCREEN_WIDTH/2-(int) (Game.player.getX()*8), Game.SCREEN_HEIGHT/2 + yLength*8 - (int) (Game.player.getY()*8), xLength*8, -yLength*8, null);
    }


    public void update() {
        double playerx = Game.player.getX(), playery = Game.player.getY();
        int prevx = Game.player.getPrevX(), prevy = Game.player.getPrevY();
        // flag indicates whether the player came from an adjacent cell
        boolean flag = ((prevx == (int) playerx-1 || prevx == (int) playerx + 1) && (prevy == (int) playery)) || ((prevy == (int) playery-1 || prevy == (int) playery + 1) && (prevx == (int) playerx));
        if (update.containsKey(new Pair((int) playerx, (int) playery)) && flag) {
            int[] value = update.get(new Pair((int) playerx, (int) playery));
            int length = value.length;
            if (length == 1) {
                specialFunctions(value[0]);
            }
            else if (length == 2) {
                Game.player.setX(playerx - (int) playerx + value[0]);
                Game.player.setY(playery - (int) playery + value[1]);
            }
            int quadrant = value[0]/90;
            flag = portalCheck[quadrant].f * (playerx - (int) playerx - 0.5) >= 0 && portalCheck[quadrant].s * (playery - (int) playery - 0.5) >= 0;
            // System.out.println(flag);
            // System.out.println(playerx);
            // System.out.println(portalCheck[quadrant].f * (playerx - (int) playerx - 0.5));
            if (length >= 3 && Math.abs((Game.player.getFacing() + 45)%360 - (value[0] + 45)%360) <= 25 && flag) {
                // Same level teleportation
                if (length == 3) {
                    Game.player.setX(playerx - (int) playerx + value[1]);
                    Game.player.setY(playery - (int) playery + value[2]);
                }
                // Inter-level teleportation
                if (length == 4) {
                    // If we are teleporting away
                    if (Game.currentLevel == 0 && !Game.user.getLevelsCleared().contains(value[3])) {
                        // Fresh start to a level
                        if (Game.user.getLastLevel() == 0) {
                            // Set the last level to be the target level
                            Game.user.setLastLevel(value[3]);
                            // Remember last lobby position
                            Game.user.setLastLobbyPosition(Game.player.getPrevX(), Game.player.getPrevY());
                            // Update current level
                            Game.currentLevel = value[3];
                            // Remember the original teleported position
                            Game.level[Game.currentLevel].setOrigin((int) playerx, (int) playery);
                            // Teleport player
                            Game.player.setX(playerx - (int) playerx + value[1]);
                            Game.player.setY(playery - (int) playery + value[2]);
                            Game.menuMusic.stop();
                            // Start music
                            if (Game.user.getInGameMusic()) {
                                Game.inGameMusic.setFramePosition(0);
                                Game.inGameMusic.start();
                                Game.inGameMusic.loop(Clip.LOOP_CONTINUOUSLY);
                            }
                        }
                        // Continue the same level
                        else if (Game.user.getLastLevel() == value[3]) {
                            // Remember last lobby position
                            Game.user.setLastLobbyPosition(Game.player.getPrevX(), Game.player.getPrevY());
                            // Update current level
                            Game.currentLevel = value[3];
                            // Remember the original teleported position
                            Game.level[Game.currentLevel].setOrigin((int) playerx, (int) playery);
                            // Teleport player
                            Game.player.setX(Game.user.getLastLevelX());
                            Game.player.setY(Game.user.getLastLevelY());
                            Game.menuMusic.stop();
                            // Start music
                            if (Game.user.getInGameMusic()) {
                                Game.inGameMusic.setFramePosition(0);
                                Game.inGameMusic.start();
                                Game.inGameMusic.loop(Clip.LOOP_CONTINUOUSLY);
                            }
                        }
                    }
                    // If we're teleporting back to the lobby
                    else if (Game.currentLevel != 0) {
                        // Add the current level to the list of completed levels
                        Game.user.addLevelCleared(Game.currentLevel);
                        // Completed level, 0 assigned to last level
                        Game.user.setLastLevel(0);
                        // If user has completed all levels, then add it to the list of users who've also done so
                        if (Game.user.getLevelsCleared().size() == 4) {
                            Game.completedUsers.add(Game.user);
                        }
                        // Update current level
                        Game.currentLevel = value[3];
                        // Teleport back to original position
                        Game.player.setX(playerx - (int) playerx + value[1]);
                        Game.player.setY(playery - (int) playery + value[2]);

                        Game.inGameMusic.stop();
                        Game.stopEnvironmentSound();
                        if (Game.user.getMenuMusic()) {
                            Game.menuMusic.setFramePosition(0);
                            Game.menuMusic.start();
                            Game.menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
                        }
                    }
                }
            }
        }
        refresh();
    }

    private void specialFunctions(int i) {
        boolean menuOption = false;
        // Continue game
        if (i == 1) {
            if (Game.user.getLastLevel() != 0 && !Game.user.getLevelsCleared().contains(Game.user.getLastLevel())) {
                Game.user.setLastLobbyPosition(Game.player.getPrevX(), Game.player.getPrevY());
                Game.currentLevel = Game.user.getLastLevel();
                Game.player.setX(Game.user.getLastLevelX());
                Game.player.setY(Game.user.getLastLevelY());
                Game.level[Game.currentLevel].setOrigin((int) Game.player.getX(), (int) Game.player.getY());
                Game.menuMusic.stop();
                // Start music
                if (Game.user.getInGameMusic()) {
                    Game.inGameMusic.setFramePosition(0);
                    Game.inGameMusic.start();
                    Game.inGameMusic.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
        }
        else if (i == 2) {
            
        }
        else if (i == 3) {
            Game.recordUserInfo();
            System.exit(0);
        }
        else if (i == 4) {
            int result = JOptionPane.showOptionDialog(null, Game.getTutorialPanel(), "About & Instructions", JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Close"}, null);
            menuOption = true;
        }
        else if (i == 5) {
            Game.showLeaderboard();
            menuOption = true;
        }
        else if (i == 6) {
            Game.user.setMenuMusic(!Game.user.getMenuMusic());
            menuOption = true;
        }
        else if (i == 7) {
            Game.user.setInGameMusic(!Game.user.getInGameMusic());
            menuOption = true;
        }
        else if (i == 8) {
            Game.user.setEnvironmentSounds(!Game.user.getEnvironmentSounds());
            menuOption = true;
        }
        // (Band-Aid solution) If toggled menu option, teleport the player out
        if (menuOption) {
            Game.left = false;
            Game.right = false;
            Game.forward = false;
            Game.backward = false;
            Game.player.setX(Game.player.getPrevX() + 0.5);
            Game.player.setY(Game.player.getPrevY() + 0.5);
        }
    }

    public abstract void refresh();

    
}
