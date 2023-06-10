import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import java.awt.image.*;

public class Player {
    // Current player coords (from center); facing: degrees from north (+y)
    private int facing;
    private double x, y;
    private boolean blockLeft, blockRight, blockFront, blockBack;

    // Rate of travel and rate of rotation
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
    private final Pair[] initialPoints = {
        new Pair(1, 1), 
        new Pair(-1, 1), 
        new Pair(-1, -1), 
        new Pair(1, -1)
    };
    private final HashSet<Integer> xEdgeValues = new HashSet<>(Arrays.asList(-Game.SCREEN_WIDTH/2, Game.SCREEN_WIDTH/2));
    private final HashSet<Integer> yEdgeValues = new HashSet<>(Arrays.asList(-Game.SCREEN_HEIGHT/2, Game.SCREEN_HEIGHT/2));

    // Helper mask for the noise
    private BufferedImage mask;

    // Constructor
    public Player(double x, double y, int facing) {
        // Initialize player location / orientation variables
        this.x = x;
        this.y = y;
        this.facing = facing;
        this.blockLeft = false;
        this.blockRight = false;
        this.blockFront = false;
        this.blockBack = false;

        // Rate of travel and rate of rotation
        speed = 0.125;
        turnSpeed = 10;

        // Initialize noise texture
        for (int i = 0; i < Game.SCREEN_WIDTH; i++) {
            for (int j = 0; j < Game.SCREEN_HEIGHT; j++) {
                noise[i][j] = new Color((int) (Math.random()*70)+60, (int) (Math.random()*70)+60, (int) (Math.random()*70)+60);
            }
        }

        // Initialize mask
        mask = new BufferedImage(Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);
    }

    public void update() {

        // System.out.println(x + " | " + y);
        
        // Checking whether the player is blocked in any direction
        // Each is adding a slight delta as the center of the player may be in one cell, but its bounding box may come into contact with an object in another cell; adding 0.125 here ----| to avoid false detection
        blockLeft = (Game.level[Game.currentLevel].getGrid((int) (x-1), (int) (y-0.25)) > 0 && (x - (int) x <= 0.25)) || (Game.level[Game.currentLevel].getGrid((int) (x-1), (int) (y+0.125)) > 0 && (x - (int) x <= 0.25));
        blockRight = (Game.level[Game.currentLevel].getGrid((int) x+1, (int) (y-0.25)) > 0 && (x - (int) x >= 0.75)) || (Game.level[Game.currentLevel].getGrid((int) x+1, (int) (y+0.125)) > 0 && (x - (int) x >= 0.75));
        blockFront = (Game.level[Game.currentLevel].getGrid((int) (x-0.25), (int) y+1) > 0 && (y - (int) y >= 0.75)) || (Game.level[Game.currentLevel].getGrid((int) (x+0.125), (int) y+1) > 0 && (y - (int) y >= 0.75));
        blockBack = (Game.level[Game.currentLevel].getGrid((int) (x-0.25), (int) y-1) > 0 && (y - (int) y <= 0.25)) || (Game.level[Game.currentLevel].getGrid((int) (x+0.125), (int) y-1) > 0 && (y - (int) y <= 0.25));

        // Perform the needed actions if requested
        if (Game.forward) {
            if (blockFront) 
                y = (int) y + 0.75;
            else 
                y += speed;
        }
        else if (Game.backward) {
            if (blockBack) 
                y = (int) y + 0.25;
            else 
                y -= speed;
        }
        if (Game.left) {
            if (blockLeft) 
                x = (int) x + 0.25;
            else 
                x -= speed;
        }
        else if (Game.right) {
            if (blockRight) 
                x = (int) x + 0.75;
            else 
                x += speed;
        }
        // if (Game.forward && !blockFront)
        //     y += speed;
        // else if (blockFront && y - (int) y > 0.75)
        //     y = (int) y + 0.75;
        // else if (Game.backward && !blockBack)
        //     y -= speed;
        // if (Game.left && !blockLeft)
        //     x -= speed;
        // else if (Game.right && !blockRight)
        //     x += speed;
        if (Game.turnRight)
            facing += turnSpeed;
        else if (Game.turnLeft)
            facing -= turnSpeed;

        // System.out.println(x + " | " + y + " | " + blockBack + " | " + blockLeft);
        // Update facing value such that it is always in the range [0, 360)
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
        // Get and clear graphics object for image mask
        Graphics gi = mask.createGraphics();
        gi.clearRect(0, 0, Game.SCREEN_WIDTH, Game.SCREEN_HEIGHT);

        // Draw shadow casted by player
        renderPlayerShadow(gi);

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
                renderObjectShadow(gi, i, j);
            }
        }
        // Use mask to draw the noise onto the image
        for (int i = 0; i < Game.SCREEN_WIDTH; i++) {
            for (int j = 0; j < Game.SCREEN_HEIGHT; j++) {
                int red = (mask.getRGB(i, j) & 0x00ff0000) >> 16;
                int green = (mask.getRGB(i, j) & 0x0000ff00) >> 8;
                int blue = (mask.getRGB(i, j) & 0x000000ff);
                // System.out.println(red);
                // System.out.println(green);
                // System.out.println(blue);
                if (red == 1 && green == 1 && blue == 1) {
                    g.setColor(noise[i][j]);
                    g.fillRect(i, j, 1, 1);
                }
            }
        }
        // Draw player spite
        renderPlayer(g);
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
            // Based on the value of sine and cosine, determine which quadrant it is to be drawn
            int quadrant = Arrays.asList(initialPoints).indexOf(new Pair((int) (cos[i]/Math.abs(cos[i])), (int) (sin[i]/Math.abs(sin[i]))));
            // System.out.println(quadrant);
            // Start point
            endpoints[i][0] = initialPoints[quadrant].f;
            endpoints[i][1] = initialPoints[quadrant].s;
            // Continue drawing the line so long as it's within boundaries of the mask
            while (endpoints[i][0] + Game.SCREEN_WIDTH/2 > 0 && endpoints[i][0] + Game.SCREEN_WIDTH/2 < Game.SCREEN_WIDTH
                    && endpoints[i][1] + Game.SCREEN_HEIGHT/2 > 0 && endpoints[i][1] + Game.SCREEN_HEIGHT/2 < Game.SCREEN_HEIGHT) {
                // This is based on the fact that tan(θ) = slope
                // Move in the x-direction to fit to the slope if larger than target value
                if (Math.abs(1.0*endpoints[i][1]/endpoints[i][0]) > Math.abs(sin[i]/cos[i])) {
                    endpoints[i][0] += initialPoints[quadrant].f;
                }
                // Otherwise, increment in the y-direction
                else {
                    endpoints[i][1] += initialPoints[quadrant].s;
                }
                gi.fillRect(Game.SCREEN_WIDTH/2 + endpoints[i][0] - initialPoints[quadrant].s, Game.SCREEN_HEIGHT/2 + endpoints[i][1] - initialPoints[quadrant].f, 1, 1);
            }
            // Turn local coords to coords based on the screen
            endpoints[i][0] += Game.SCREEN_WIDTH/2;
            endpoints[i][1] += Game.SCREEN_HEIGHT/2;
        }
        // Used to get 
        int dirIndex = facing/90;
        // Set to the special color for the mask
        gi.setColor(new Color(1, 1, 1));
        // Draw the polygon defining the shadow cast by player
        gi.drawPolygon(new int[]{Game.SCREEN_WIDTH/2, endpoints[0][0], 
                                FILL_COORDINATES[FILL_ORDER[dirIndex][0]][0],
                                FILL_COORDINATES[FILL_ORDER[dirIndex][1]][0],
                                FILL_COORDINATES[FILL_ORDER[dirIndex][2]][0],
                                endpoints[1][0]}, 
                      new int[]{Game.SCREEN_HEIGHT/2, endpoints[0][1], 
                                FILL_COORDINATES[FILL_ORDER[dirIndex][0]][1],
                                FILL_COORDINATES[FILL_ORDER[dirIndex][1]][1],
                                FILL_COORDINATES[FILL_ORDER[dirIndex][2]][1],
                                endpoints[1][1]}, 6);
    }

    private void renderObjectShadow(Graphics g, int cellX, int cellY) {
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
                    // Get Quadrant
                    // Based on the the change in x and y, determine which quadrant it is to be drawn
                    int quadrant = Arrays.asList(initialPoints).indexOf(new Pair((int) ((cellX + vertexDelta[v][0] - x)/Math.abs(cellX + vertexDelta[v][0] - x)),
                                                                            (int) ((cellY + vertexDelta[v][1] - y)/Math.abs(cellY + vertexDelta[v][1] - y))));
                    // System.out.println(quadrant);
                    // Start point
                    endpoints[v][0] = initialPoints[quadrant].f;
                    endpoints[v][1] = initialPoints[quadrant].s;
                    while (endpoints[v][0] + Game.SCREEN_WIDTH/2 > 0 && endpoints[v][0] + Game.SCREEN_WIDTH/2 < Game.SCREEN_WIDTH 
                            && endpoints[v][1] + Game.SCREEN_HEIGHT/2 > 0 && endpoints[v][1] + Game.SCREEN_HEIGHT/2 < Game.SCREEN_HEIGHT) {
                        // This is based on the fact that tan(θ) = slope
                        // Move in the x-direction to fit to the slope if larger than target value
                        if (Math.abs(1.0 * endpoints[v][1] / endpoints[v][0]) > Math.abs(slope)) {
                            endpoints[v][0] += initialPoints[quadrant].f;
                        }
                        // Otherwise, increment in the y-direction
                        else {
                            endpoints[v][1] += initialPoints[quadrant].s;
                        }
                    }
                }
            }
            g.setColor(new Color(1, 1, 1));
            for (int v = 0; v < 4; v++) {
                int[] xPoints, yPoints;
                if ((endpoints[v%4][0] == endpoints[(v+1)%4][0] && xEdgeValues.contains(endpoints[v%4][0] - Game.SCREEN_WIDTH/2)) || (endpoints[v%4][1] == endpoints[(v+1)%4][1] && yEdgeValues.contains(endpoints[v%4][1] - Game.SCREEN_HEIGHT/2))) {
                    xPoints = new int[]{
                        (int) (-x*8) + Game.SCREEN_WIDTH/2 + (cellX + vertexDelta[v][0])*8,
                        (int) Game.SCREEN_WIDTH/2 + endpoints[v][0],
                        (int) Game.SCREEN_WIDTH/2 + endpoints[(v+1)%4][0],
                        (int) (-x*8) + Game.SCREEN_WIDTH/2 + (cellX + vertexDelta[(v+1)%4][0])*8
                    }; 
                    yPoints = new int[]{
                        (int) (-y*8) + Game.SCREEN_HEIGHT/2 + (cellY + vertexDelta[v][1])*8,
                        (int) Game.SCREEN_HEIGHT/2 + endpoints[v][1],
                        (int) Game.SCREEN_HEIGHT/2 + endpoints[(v+1)%4][1],
                        (int) (-y*8) + Game.SCREEN_HEIGHT/2 + (cellY + vertexDelta[(v+1)%4][1])*8
                    };
                }
                else if ((xEdgeValues.contains(endpoints[v%4][0]) && xEdgeValues.contains(endpoints[(v+1)%4][0]) && endpoints[v%4][0] != endpoints[(v+1)%4][0]) || (yEdgeValues.contains(endpoints[v%4][1]) && yEdgeValues.contains(endpoints[(v+1)%4][1]) && endpoints[v%4][1] != endpoints[(v+1)%4][1])) {
                    xPoints = new int[]{
                        (int) (-x*8) + Game.SCREEN_WIDTH/2 + (cellX + vertexDelta[v][0])*8,
                        (int) Game.SCREEN_WIDTH/2 + endpoints[v][0],
                        (int) Game.SCREEN_WIDTH/2 + (xEdgeValues.contains(endpoints[v%4][0])? endpoints[v%4][0] : (endpoints[v%4][0] < 0? -Game.SCREEN_WIDTH/2 : Game.SCREEN_WIDTH/2)),
                        (int) Game.SCREEN_WIDTH/2 + (xEdgeValues.contains(endpoints[(v+1)%4][0])? endpoints[(v+1)%4][0] : (endpoints[(v+1)%4][0] < 0? -Game.SCREEN_WIDTH/2 : Game.SCREEN_WIDTH/2)),
                        (int) Game.SCREEN_WIDTH/2 + endpoints[(v+1)%4][0],
                        (int) (-x*8) + Game.SCREEN_WIDTH/2 + (cellX + vertexDelta[(v+1)%4][0])*8
                    }; 
                    yPoints = new int[]{
                        (int) (-y*8) + Game.SCREEN_HEIGHT/2 + (cellY + vertexDelta[v][1])*8,
                        (int) Game.SCREEN_HEIGHT/2 + endpoints[v][1],
                        (int) Game.SCREEN_HEIGHT/2 + (yEdgeValues.contains(endpoints[v%4][1])? endpoints[v%4][1] : (endpoints[v%4][1] < 0? -Game.SCREEN_HEIGHT/2 : Game.SCREEN_HEIGHT/2)),
                        (int) Game.SCREEN_HEIGHT/2 + (yEdgeValues.contains(endpoints[(v+1)%4][1])? endpoints[(v+1)%4][1] : (endpoints[(v+1)%4][1] < 0? -Game.SCREEN_HEIGHT/2 : Game.SCREEN_HEIGHT/2)),
                        (int) Game.SCREEN_HEIGHT/2 + endpoints[(v+1)%4][1],
                        (int) (-y*8) + Game.SCREEN_HEIGHT/2 + (cellY + vertexDelta[(v+1)%4][1])*8
                    };
                }
                else {
                    xPoints = new int[]{
                        (int) (-x*8) + Game.SCREEN_WIDTH/2 + (cellX + vertexDelta[v][0])*8,
                        (int) Game.SCREEN_WIDTH/2 + endpoints[v][0],
                        (int) Game.SCREEN_WIDTH/2 + (xEdgeValues.contains(endpoints[v][0])? endpoints[v][0] : endpoints[(v+1)%4][0]),
                        (int) Game.SCREEN_WIDTH/2 + endpoints[(v+1)%4][0],
                        (int) (-x*8) + Game.SCREEN_WIDTH/2 + (cellX + vertexDelta[(v+1)%4][0])*8
                    };
                    yPoints = new int[]{
                        (int) (-y*8) + Game.SCREEN_HEIGHT/2 + (cellY + vertexDelta[v][1])*8,
                        (int) Game.SCREEN_HEIGHT/2 + endpoints[v][1],
                        (int) Game.SCREEN_HEIGHT/2 + (yEdgeValues.contains(endpoints[v][1])? endpoints[v][1] : endpoints[(v+1)%4][1]),
                        (int) Game.SCREEN_HEIGHT/2 + endpoints[(v+1)%4][1],
                        (int) (-y*8) + Game.SCREEN_HEIGHT/2 + (cellY + vertexDelta[(v+1)%4][1])*8
                    };
                }
                g.drawPolygon(xPoints, yPoints, xPoints.length);
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

    public int getFacing() {
        return facing;
    }

    // Setters
    public void setX(double x) {
        this.x = x;
    }

    public void setY(double y) {
        this.y = y;
    }




}
