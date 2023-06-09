import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;

public class Player {
    // Current player coords (from center); facing: degrees from north (+y)
    private int facing;
    private double x, y;
    // 
    private final int turnSpeed;
    private final double speed;

    // Noise texture to fill the shadow
    private Color[][] noise = new Color[Game.SCREEN_WIDTH][Game.SCREEN_HEIGHT];
    // Helper constants for placement of red dot on player depending on rotation
    private final int[][] CORE_FACE = {
        {-1,  0},
        { 0,  0},
        { 0, -1},
        {-1, -1}
    };
    private final int[][] PERIPHERY_FACE = {
        {-2, +1}, {-1, +1}, {+0, +1}, {+1, +1}, 
        {+1, +1}, {+1, +0}, {+1, -1}, {+1, -2},
        {+1, -2}, {+0, -2}, {-1, -2}, {-2, -2}, 
        {-2, -2}, {-2, -1}, {-2, +0}, {-2, +1}
    };
    // Helper constants for the filling of shadows behind player
    private final int[][] FILL_COORDINATES = {
        {0, 0}, 
        {Game.SCREEN_WIDTH, 0}, 
        {Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT}, 
        {0, Game.SCREEN_HEIGHT}
    };
    private final int[][] FILL_ORDER = {
        {1, 0, 3}, {0, 3, 2}, {3, 2, 1}, {2, 1, 0}
    };
    // Helper values for object shadow tracing
    private final int[][] vertexDelta = {
        {0, 0}, 
        {0, 1}, 
        {1, 1}, 
        {1, 0}
    };
    private final HashSet<Integer> xEdgeValues = new HashSet<>(Arrays.asList(-Game.SCREEN_WIDTH/2, Game.SCREEN_WIDTH/2));
    private final HashSet<Integer> yEdgeValues = new HashSet<>(Arrays.asList(-Game.SCREEN_HEIGHT/2, Game.SCREEN_HEIGHT/2));

    // Helper mask for the noise
    private BufferedImage mask;

    // Constructor
    public Player(int x, int y, int facing) {
        this.x = x;
        this.y = y;
        this.facing = facing;

        speed = 0.125;
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
            x -= speed;
        else if (Game.right)
            x += speed;
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

        renderPlayerShadow(g);
        renderPlayer(g);

        // For all objects in range; calculate the shadow
        // Pair xRange = new Pair(
        //     Math.max(
        //         0,
        //         (int) Math.floor(x-Game.SCREEN_WIDTH/8/2)
        //     ),
        //     Math.min(
        //         Game.level[Game.currentLevel].getXLength(),
        //         (int) Math.ceil(x+Game.SCREEN_WIDTH/8/2)
        //     )
        // );
        // Pair yRange = new Pair(
        //     Math.max(
        //         0,
        //         (int) Math.floor(y-Game.SCREEN_HEIGHT/8/2)
        //     ), 
        //     Math.min(
        //         Game.level[Game.currentLevel].getYLength(),
        //         (int) Math.ceil(y+Game.SCREEN_HEIGHT/8/2)
        //     )
        // );
        // for (int i = xRange.f; i < xRange.s; i++) {
        //     for (int j = yRange.f; j < yRange.s; j++) {
        //         renderObjectShadow(g, i, j);
        //     }
        // }
        // For all objects in range; calculate the shadow
        Pair xRange = new Pair(
            Math.max(
                0,
                (int) Game.camera.getLeftX()-1
            ),
            Math.min(
                Game.level[Game.currentLevel].getXLength(),
                (int) Game.camera.getRightX()+1
            )
        );
        Pair yRange = new Pair(
            Math.max(
                0,
                (int) Game.camera.getBottomY()
            ), 
            Math.min(
                Game.level[Game.currentLevel].getYLength(),
                (int) Game.camera.getTopY()
            )
        );
        for (int i = xRange.f; i < xRange.s; i++) {
            for (int j = yRange.f; j < yRange.s; j++) {
                renderObjectShadow(g, i, j);
            }
        }



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
//                 tmpx++k;
//                 g.fillRect(Game.SCREEN_WIDTH/2 + tmpx, Game.SCREEN_HEIGHT/2 + tmpy, 1, 1);
//             }
//             tmpy++;
//             g.fillRect(Game.SCREEN_WIDTH/2 + tmpx, Game.SCREEN_HEIGHT/2 + tmpy, 1, 1);
//         }

    }

    private void renderPlayerShadow(Graphics g) {
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
            // else if (sin[i] < 0 && cos[i] > 0) 
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
        for (int i = 0; i < Game.SCREEN_WIDTH; i++) {
            for (int j = 0; j < Game.SCREEN_HEIGHT; j++) {
                int intensity = (mask.getRGB(i, j) & 0x00ff0000) >> 16;
                if (intensity == 255) {
                    g.setColor(noise[i][j]);
                    g.fillRect(i, j, 1, 1);
                }
            }
        }
    }

    private void renderObjectShadow(Graphics g, int cellX, int cellY) {
        Graphics gi = mask.createGraphics();
        gi.clearRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);
        if (Game.level[Game.currentLevel].getGrid(cellX, cellY) > 0) {
            int endpoints[][] = new int[4][2];
            double slope;
            for (int v =0; v < 4; v++) {
                if ((int) 8*x == (cellX + vertexDelta[v][0])*8) {
                    endpoints[v][0] = (int) (cellX - x + vertexDelta[v][0])*8;
                    endpoints[v][1] = ((int) y*8 > cellY*8? 0 : Game.SCREEN_HEIGHT) - Game.SCREEN_HEIGHT/2;
                }
                else if (8*y == (cellY + vertexDelta[v][1])*8) {
                    endpoints[v][1] = (int) (cellY - y + vertexDelta[v][1])*8;
                    endpoints[v][0] = (x*8 > cellX*8? 0 : Game.SCREEN_WIDTH) - Game.SCREEN_WIDTH/2;
                }
                else {
                    slope = (cellY + vertexDelta[v][1] - y) / (cellX + vertexDelta[v][0] - x);
                    // Q1
                    if (cellY + vertexDelta[v][1] - y  > 0 && cellX + vertexDelta[v][0] - x > 0) {
                        endpoints[v][0] = 1;
                        endpoints[v][1] = 1;
                        while (endpoints[v][0] + Game.SCREEN_WIDTH/2 < Game.SCREEN_WIDTH && endpoints[v][1] + Game.SCREEN_HEIGHT/2 < Game.SCREEN_HEIGHT) {
                            if (Math.abs(1.0 * endpoints[v][1] / endpoints[v][0]) > Math.abs(slope)) {
                                endpoints[v][0] ++;
                            }
                            else {
                                endpoints[v][1] ++;
                            }
                        }
                    }
                    // Q2
                    else if (cellY + vertexDelta[v][1] - y > 0 && cellX + vertexDelta[v][0] - x < 0) {
                        endpoints[v][0] = -1;
                        endpoints[v][1] = 1;
                        while (endpoints[v][0] + Game.SCREEN_WIDTH/2 > 0 && endpoints[v][1] + Game.SCREEN_HEIGHT/2 < Game.SCREEN_HEIGHT) {
                            if (Math.abs(1.0 * endpoints[v][1] / endpoints[v][0]) > Math.abs(slope)) {
                                endpoints[v][0] --;
                            }
                            else {
                                endpoints[v][1] ++;
                            }
                        }
                    }
                    // Q3
                    else if (cellY + vertexDelta[v][1] - y < 0 && cellX + vertexDelta[v][0] - x < 0) {
                        endpoints[v][0] = -1;
                        endpoints[v][1] = -1;
                        while (endpoints[v][0] + Game.SCREEN_WIDTH/2 > 0 && endpoints[v][1] + Game.SCREEN_HEIGHT/2 > 0) {
                            if (Math.abs(1.0 * endpoints[v][1] / endpoints[v][0]) > Math.abs(slope)) {
                                endpoints[v][0] --;
                            }
                            else {
                                endpoints[v][1] --;
                            }
                        }
                    }
                    // Q4
                    else {
                        endpoints[v][0] = 1;
                        endpoints[v][1] = -1;
                        while (endpoints[v][0] + Game.SCREEN_WIDTH/2 < Game.SCREEN_WIDTH && endpoints[v][1] + Game.SCREEN_HEIGHT/2 > 0) {
                            if (Math.abs(1.0 * endpoints[v][1] / endpoints[v][0]) > Math.abs(slope)) {
                                endpoints[v][0] ++;
                            }
                            else {
                                endpoints[v][1] --;
                            }
                        }
                    }
                }
            }
            g.setColor(Color.BLACK);
            for (int v = 0; v < 4; v++) {
                if ((endpoints[v%4][0] == endpoints[(v+1)%4][0] && xEdgeValues.contains(endpoints[v%4][0] - Game.SCREEN_WIDTH/2)) || (endpoints[v%4][1] == endpoints[(v+1)%4][1] && yEdgeValues.contains(endpoints[v%4][1] - Game.SCREEN_HEIGHT/2))) {
                    g.drawPolygon(
                        new int[]{
                            (int) (-x*8) + Game.SCREEN_WIDTH/2 + (cellX + vertexDelta[v][0])*8,
                            (int) Game.SCREEN_WIDTH/2 + endpoints[v][0],
                            (int) Game.SCREEN_WIDTH/2 + endpoints[(v+1)%4][0],
                            (int) (-x*8) + Game.SCREEN_WIDTH/2 + (cellX + vertexDelta[(v+1)%4][0])*8
                        }, 
                        new int[]{
                            (int) (-y*8) + Game.SCREEN_HEIGHT/2 + (cellY + vertexDelta[v][1])*8,
                            (int) Game.SCREEN_HEIGHT/2 + endpoints[v][1],
                            (int) Game.SCREEN_HEIGHT/2 + endpoints[(v+1)%4][1],
                            (int) (-y*8) + Game.SCREEN_HEIGHT/2 + (cellY + vertexDelta[(v+1)%4][1])*8
                        },
                    4);
                }
                else if ((xEdgeValues.contains(endpoints[v%4][0]) && xEdgeValues.contains(endpoints[(v+1)%4][0]) && endpoints[v%4][0] != endpoints[(v+1)%4][0]) || (yEdgeValues.contains(endpoints[v%4][1]) && yEdgeValues.contains(endpoints[(v+1)%4][1]) && endpoints[v%4][1] != endpoints[(v+1)%4][1])) {
                    g.drawPolygon(
                        new int[]{
                            (int) (-x*8) + Game.SCREEN_WIDTH/2 + (cellX + vertexDelta[v][0])*8,
                            (int) Game.SCREEN_WIDTH/2 + endpoints[v][0],
                            (int) Game.SCREEN_WIDTH/2 + (xEdgeValues.contains(endpoints[v%4][0])? endpoints[v%4][0] : (endpoints[v%4][0] < 0? -Game.SCREEN_WIDTH/2 : Game.SCREEN_WIDTH/2)),
                            (int) Game.SCREEN_WIDTH/2 + (xEdgeValues.contains(endpoints[(v+1)%4][0])? endpoints[(v+1)%4][0] : (endpoints[(v+1)%4][0] < 0? -Game.SCREEN_WIDTH/2 : Game.SCREEN_WIDTH/2)),
                            (int) Game.SCREEN_WIDTH/2 + endpoints[(v+1)%4][0],
                            (int) (-x*8) + Game.SCREEN_WIDTH/2 + (cellX + vertexDelta[(v+1)%4][0])*8
                        }, 
                        new int[]{
                            (int) (-y*8) + Game.SCREEN_HEIGHT/2 + (cellY + vertexDelta[v][1])*8,
                            (int) Game.SCREEN_HEIGHT/2 + endpoints[v][1],
                            (int) Game.SCREEN_HEIGHT/2 + (yEdgeValues.contains(endpoints[v%4][1])? endpoints[v%4][1] : (endpoints[v%4][1] < 0? -Game.SCREEN_HEIGHT/2 : Game.SCREEN_HEIGHT/2)),
                            (int) Game.SCREEN_HEIGHT/2 + (yEdgeValues.contains(endpoints[(v+1)%4][1])? endpoints[(v+1)%4][1] : (endpoints[(v+1)%4][1] < 0? -Game.SCREEN_HEIGHT/2 : Game.SCREEN_HEIGHT/2)),
                            (int) Game.SCREEN_HEIGHT/2 + endpoints[(v+1)%4][1],
                            (int) (-y*8) + Game.SCREEN_HEIGHT/2 + (cellY + vertexDelta[(v+1)%4][1])*8
                        },
                    6);
                }
                else {
                    g.drawPolygon(
                        new int[]{
                            (int) (-x*8) + Game.SCREEN_WIDTH/2 + (cellX + vertexDelta[v][0])*8,
                            (int) Game.SCREEN_WIDTH/2 + endpoints[v][0],
                            (int) Game.SCREEN_WIDTH/2 + (xEdgeValues.contains(endpoints[v][0])? endpoints[v][0] : endpoints[(v+1)%4][0]),
                            (int) Game.SCREEN_WIDTH/2 + endpoints[(v+1)%4][0],
                            (int) (-x*8) + Game.SCREEN_WIDTH/2 + (cellX + vertexDelta[(v+1)%4][0])*8
                        }, 
                        new int[]{
                            (int) (-y*8) + Game.SCREEN_HEIGHT/2 + (cellY + vertexDelta[v][1])*8,
                            (int) Game.SCREEN_HEIGHT/2 + endpoints[v][1],
                            (int) Game.SCREEN_HEIGHT/2 + (yEdgeValues.contains(endpoints[v][1])? endpoints[v][1] : endpoints[(v+1)%4][1]),
                            (int) Game.SCREEN_HEIGHT/2 + endpoints[(v+1)%4][1],
                            (int) (-y*8) + Game.SCREEN_HEIGHT/2 + (cellY + vertexDelta[(v+1)%4][1])*8
                        },
                    5);
                }
            }
            // for (int v = 0; v < 4; v++) {
            //     if (endpoints[v%4][0] == endpoints[(v+1)%4][0] || endpoints[v%4][1] == endpoints[(v+1)%4][1]) {
            //         g.drawPolygon(
            //             new int[]{
            //                 (int) (Game.SCREEN_WIDTH + (-x*8) + (cellX + vertexDelta[v][0])*8),
            //                 (int) (Game.SCREEN_WIDTH + endpoints[v][0]),
            //                 (int) (Game.SCREEN_WIDTH + endpoints[(v+1)%4][0]),
            //                 (int) (Game.SCREEN_WIDTH + (-x*8) + (cellX + vertexDelta[(v+1)%4][0])*8)
            //             }, 
            //             new int[]{
            //                 (int) (Game.SCREEN_HEIGHT + (-y*8) + (cellY + vertexDelta[v][1])*8),
            //                 (int) (Game.SCREEN_HEIGHT + endpoints[v][1]),
            //                 (int) (Game.SCREEN_HEIGHT + endpoints[(v+1)%4][1]),
            //                 (int) (Game.SCREEN_HEIGHT + (-y*8) + (cellY + vertexDelta[(v+1)%4][1])*8)
            //             },
            //         4);
            //     }
            //     else {
            //         g.drawPolygon(
            //             new int[]{
            //                 (int) (Game.SCREEN_WIDTH + (-x*8) + (cellX + vertexDelta[v][0])*8),
            //                 (int) (Game.SCREEN_WIDTH + endpoints[v][0]),
            //                 (int) (Game.SCREEN_WIDTH + (xEdgeValues.contains(endpoints[v][0])? endpoints[v][0] : endpoints[(v+1)%4][0])),
            //                 (int) (Game.SCREEN_WIDTH + endpoints[(v+1)%4][0]),
            //                 (int) (Game.SCREEN_WIDTH + (-x*8) + (cellX + vertexDelta[(v+1)%4][0])*8)
            //             }, 
            //             new int[]{
            //                 (int) (Game.SCREEN_HEIGHT + (-y*8) + (cellY + vertexDelta[v][1])*8),
            //                 (int) (Game.SCREEN_HEIGHT + endpoints[v][1]),
            //                 (int) (Game.SCREEN_HEIGHT + (yEdgeValues.contains(endpoints[v][1])? endpoints[v][1] : endpoints[(v+1)%4][1])),
            //                 (int) (Game.SCREEN_HEIGHT + endpoints[(v+1)%4][1]),
            //                 (int) (Game.SCREEN_HEIGHT + (-y*8) + (cellY + vertexDelta[(v+1)%4][1])*8)
            //             },
            //         5);
            //     }
            // }
        }
        // Draw the noise onto the actual mask
        // for (int i = 0; i < Game.SCREEN_WIDTH; i++) {
        //     for (int j = 0; j < Game.SCREEN_HEIGHT; j++) {
        //         int intensity = (mask.getRGB(i, j) & 0x00ff0000) >> 16;
        //         if (intensity == 255) {
        //             g.setColor(noise[i][j]);
        //             g.fillRect(i, j, 1, 1);
        //         }
        //     }
        // }
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
    public double getX() {
        return x;
    }

    public double getY() {
        return y;
    }

    // Setters
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }




}
