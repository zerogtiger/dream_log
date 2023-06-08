import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;

public class Player {
    // Current player coords; facing: degrees from north (+y)
    private int x, y, facing;
    // 
    private final int speed, turnSpeed;
    // Noise texture to fill the shadow
    private Color[][] noise = new Color[Game.SCREEN_WIDTH][Game.SCREEN_HEIGHT];
    // Helper constants for placement of red dot on player depending on rotation
    private final int[][] CORE_FACE = {{-1, 0}, {0, 0}, {0, -1}, {-1, -1}};
    private final int[][] PERIPHERY_FACE = {{-2, +1}, {-1, +1}, {+0, +1}, {+1, +1}, 
                                            {+1, +1}, {+1, +0}, {+1, -1}, {+1, -2},
                                            {+1, -2}, {+0, -2}, {-1, -2}, {-2, -2}, 
                                            {-2, -2}, {-2, -1}, {-2, +0}, {-2, +1}};
    // Helper constants for the filling of shadows behind player
    private final int[][] FILL_COORDINATES = {{0, 0}, {Game.SCREEN_WIDTH, 0}, {Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT}, {0, Game.SCREEN_HEIGHT}};
    private final int[][] FILL_ORDER = {{1, 0, 3}, {0, 3, 2}, {3, 2, 1}, {2, 1, 0}};

    // Helper mask for the noise
    private BufferedImage mask;

    // Constructor
    public Player(int x, int y, int facing) {
        this.x = x;
        this.y = y;
        this.facing = facing;

        speed = 1;
        turnSpeed = 2;

        // Initialize noise texture
        for (int i = 0; i < Game.SCREEN_WIDTH; i++) {
            for (int j = 0; j < Game.SCREEN_HEIGHT; j++) {
                noise[i][j] = new Color((int) (Math.random()*70)+60, (int) (Math.random()*70)+60, (int) (Math.random()*70)+60);
            }
        }

        mask = new BufferedImage(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
    }

    public void update() {
        if (Game.forward)
            y += speed;
        else if (Game.backward)
            y -= speed;
        if (Game.left)
            x += speed;
        else if (Game.right)
            x -= speed;
        if (Game.turnRight)
            facing += turnSpeed;
        else if (Game.turnLeft)
            facing -= turnSpeed;

        facing = (facing%360 + 360)%360;

        // Randomly switch two horizontal columns and two vertical columns
        int h1 = (int) (Math.random()*Game.SCREEN_WIDTH), h2 = (int) (Math.random()*Game.SCREEN_WIDTH);
        int c1 = (int) (Math.random()*Game.SCREEN_HEIGHT), c2 = (int) (Math.random()*Game.SCREEN_HEIGHT);
        for (int i = 0; i < Game.SCREEN_HEIGHT; i++) {
            Color tmp = noise[h1][i];
            noise[h1][i] = noise[h2][i];
            noise[h2][i] = tmp;
        }
        for (int i = 0; i < Game.SCREEN_WIDTH; i++) {
            Color tmp = noise[i][c1];
            noise[i][c1] = noise[i][c2];
            noise[i][c2] = tmp;
        }
    }

    // Render methods
    public void render(Graphics g) {

        renderShadow(g);
        renderPlayer(g);




        // for (int i = 0; i < Game.SCREEN_WIDTH; i++) {
        //     for (int j = 0; j < Game.SCREEN_HEIGHT; j++) {
        //         g.setColor(noise[i][j]);
        //         g.fillRect(i, j, 1, 1);
        //     }
        // }
//
//         // If the is perfectly horizontal or perfectly vertical
//         if (Math.abs(facing%90) == 0) {
//             
//         }
//
//
//         System.out.println(Math.toRadians((90-(facing + 75))%90));
//         double slopeRight = Math.tan(Math.toRadians((90-(facing + 75))));
//         if (Double.isNaN(slopeRight)) return;
//         System.out.println(facing);
//         // int tmpx = Game.SCREEN_WIDTH/2, tmpy = Game.SCREEN_HEIGHT/2;
// // BUG IS HERE LAA
//         // if (facing  )
//         int tmpx = 1, tmpy = 0;
//         while (tmpx+Game.SCREEN_WIDTH/2 < Game.SCREEN_WIDTH && tmpy + Game.SCREEN_HEIGHT/2 < Game.SCREEN_HEIGHT) {
//             while (1.0*tmpy/tmpx > slopeRight) {
//                 tmpx++;
//                 g.fillRect(Game.SCREEN_WIDTH/2 + tmpx, Game.SCREEN_HEIGHT/2 + tmpy, 1, 1);
//             }
//             tmpy++;
//             g.fillRect(Game.SCREEN_WIDTH/2 + tmpx, Game.SCREEN_HEIGHT/2 + tmpy, 1, 1);
//         }

    }

    private void renderShadow(Graphics g) {
        Graphics gi = mask.createGraphics();
        gi.clearRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        gi.setColor(new Color(254, 0, 0));
        // Right and left boundary of shadow
        int angleRight = -facing - 106 + 90, angleLeft = -facing + 106 + 90;
        // System.out.println("angleRight: " + angleRight + " | " + "angleLeft: " + angleLeft);
        // Sine and cosine of these angles used to calculculate slope of line
        double sin[] = {Math.sin(Math.toRadians(angleRight)), Math.sin(Math.toRadians(angleLeft))};
        double cos[] = {Math.cos(Math.toRadians(angleRight)), Math.cos(Math.toRadians(angleLeft))};
        // System.out.println(Arrays.toString(sin) + " | " + Arrays.toString(cos));

        // Endpoint of where lines defining shadow contacts the boundary of the camera FOV
        int endpoints[][] = new int[2][2];
        // For each of the two shadow boundaries
        for (int i = 0; i < 2; i++) {
            // Q1
            int tmpx, tmpy;
            if (sin[i] > 0 && cos[i] > 0) {
                // Start point
                tmpx = 2; tmpy = 2;
                // Continue drawing the line so long as it's within boundaries of the mask
                while (tmpx+Game.SCREEN_WIDTH/2 < Game.SCREEN_WIDTH && tmpy + Game.SCREEN_HEIGHT/2 < Game.SCREEN_HEIGHT) {
                    // This is based on the fact that tan(θ) = slope
                    // Move in the x-direction to fit to the slope
                    while (tmpx+Game.SCREEN_WIDTH/2 < Game.SCREEN_WIDTH && 1.0*tmpy/tmpx > sin[i]/cos[i]) {
                        gi.fillRect(Game.SCREEN_WIDTH/2 + tmpx, Game.SCREEN_HEIGHT/2 + tmpy, 1, 1);
                        tmpx++;
                    }
                    // rhen increment in the y-direction
                    gi.fillRect(Game.SCREEN_WIDTH/2 + tmpx, Game.SCREEN_HEIGHT/2 + tmpy, 1, 1);
                    tmpy++;
                }
            }
            // Q2
            else if (sin[i] > 0 && cos[i] < 0) {
                tmpx = -2; tmpy = 2;
                while (tmpx+Game.SCREEN_WIDTH/2 > 0 && tmpy + Game.SCREEN_HEIGHT/2 < Game.SCREEN_HEIGHT) {
                    while (tmpx+Game.SCREEN_WIDTH/2 > 0 && Math.abs(1.0*tmpy/tmpx) > Math.abs(sin[i]/cos[i])) {
                        gi.fillRect(Game.SCREEN_WIDTH/2 + tmpx - 1, Game.SCREEN_HEIGHT/2 + tmpy, 1, 1);
                        tmpx--;
                    }
                    gi.fillRect(Game.SCREEN_WIDTH/2 + tmpx - 1, Game.SCREEN_HEIGHT/2 + tmpy, 1, 1);
                    tmpy++;
                }
            }
            // Q3
            else if (sin[i] < 0 && cos[i] < 0) {
                tmpx = -2; tmpy = -2;
                while (tmpx + Game.SCREEN_WIDTH/2 > 0 && tmpy + Game.SCREEN_HEIGHT/2 > 0) {
                    while (tmpx + Game.SCREEN_WIDTH/2 > 0 && Math.abs(1.0*tmpy/tmpx) > Math.abs(sin[i]/cos[i])) {
                        gi.fillRect(Game.SCREEN_WIDTH/2 + tmpx - 1, Game.SCREEN_HEIGHT/2 + tmpy - 1, 1, 1);
                        tmpx--;
                    }
                    gi.fillRect(Game.SCREEN_WIDTH/2 + tmpx -1, Game.SCREEN_HEIGHT/2 + tmpy - 1, 1, 1);
                    tmpy--;
                }
            }
            // Q4
            else {
            // else if (sin[i] < 0 && cos[i] > 0) {
                tmpx = +2; tmpy = -2;
                while (tmpx + Game.SCREEN_WIDTH/2 < Game.SCREEN_WIDTH && tmpy + Game.SCREEN_HEIGHT/2 > 0) {
                    while (tmpx + Game.SCREEN_WIDTH/2 < Game.SCREEN_WIDTH && Math.abs(1.0*tmpy/tmpx) > Math.abs(sin[i]/cos[i])) {
                        gi.fillRect(Game.SCREEN_WIDTH/2 + tmpx, Game.SCREEN_HEIGHT/2 + tmpy -1, 1, 1);
                        tmpx++;
                    }
                    gi.fillRect(Game.SCREEN_WIDTH/2 + tmpx, Game.SCREEN_HEIGHT/2 + tmpy - 1, 1, 1);
                    tmpy--;
                }
            }
            endpoints[i][0] = Game.SCREEN_WIDTH/2 + tmpx;
            endpoints[i][1] = Game.SCREEN_HEIGHT/2 + tmpy;
        }
        int dirIndex = facing/90;
        gi.setColor(new Color(255, 0, 0));
        gi.fillPolygon(new int[]{Game.SCREEN_WIDTH/2, endpoints[0][0], 
                                FILL_COORDINATES[FILL_ORDER[dirIndex][0]][0],
                                FILL_COORDINATES[FILL_ORDER[dirIndex][1]][0],
                                FILL_COORDINATES[FILL_ORDER[dirIndex][2]][0],
                                endpoints[1][0]}, 
                      new int[]{Game.SCREEN_HEIGHT/2, endpoints[0][1], 
                                FILL_COORDINATES[FILL_ORDER[dirIndex][0]][1],
                                FILL_COORDINATES[FILL_ORDER[dirIndex][1]][1],
                                FILL_COORDINATES[FILL_ORDER[dirIndex][2]][1],
                                endpoints[1][1]}, 6);
        // Draw the noise onto the actual mask
        for (int x = 0; x < Game.SCREEN_WIDTH; x++) {
            for (int y = 0; y < Game.SCREEN_HEIGHT; y++) {
                int intensity = (mask.getRGB(x, y) & 0x00ff0000) >> 16;
                if (intensity == 255) {
                    g.setColor(noise[x][y]);
                    g.fillRect(x, y, 1, 1);
                }
            }
        }
    }

    private void renderPlayer(Graphics g) {
        // Player
        // Center of the screen
        g.setColor(Color.BLACK);
        g.fillRect(Game.SCREEN_WIDTH/2-2, Game.SCREEN_HEIGHT/2-2, 4, 4);
        g.setColor(Color.WHITE);
        g.fillRect(Game.SCREEN_WIDTH/2-1, Game.SCREEN_HEIGHT/2-1, 2, 2);

        // Red dot to indicate facing of player
        g.setColor(Color.RED);
        g.fillRect(Game.SCREEN_WIDTH/2 + CORE_FACE[facing/90][0], Game.SCREEN_HEIGHT/2 + CORE_FACE[facing/90][1], 1, 1);
        g.fillRect(Game.SCREEN_WIDTH/2 + PERIPHERY_FACE[(int) (facing/22.5)][0], Game.SCREEN_HEIGHT/2 + PERIPHERY_FACE[(int) (facing/22.5)][1], 1, 1);
    }


    // Getters
    public int getX() {
        return x;
    }

    public int getY() {
        return y;
    }

    // Setters
    public void setX(int x) {
        this.x = x;
    }

    public void setY(int y) {
        this.y = y;
    }




}
