
import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;
    private Thread thread;
    private boolean running;
    private BufferedImage image;

    private final JFrame frame;
    
    public static final int WIDTH = 1200;
    public static final int HEIGHT = WIDTH*9/16;
    public static final int SCREEN_WIDTH = 320;
    public static final int SCREEN_HEIGHT = 180;

    // Wether the player should be performing these moves
    public static boolean forward, backward, left, right, turnLeft, turnRight;
    // Whether time should be sped up
    public static boolean warptime;

    public static Player player;
    public static Camera camera;
    public static Level[] level;
    // Current level player is on. 0 for main lobby
    public static int currentLevel;

    public Game() {

        warptime = false;

        // Game component related initialization
        player = new Player(1.5, 1.5, 1);
        camera = new Camera(20);
        level = new Level[2];
        level[0] = new Lobby();
        currentLevel = 0;

        // Window related initializations
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        frame = new JFrame("dream_log");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.black);

        thread = new Thread(this);
        image = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);

        frame.add(this);
        frame.setFocusable(true);
        frame.requestFocus();
        this.addKeyListener(new KeyInput());
        this.setFocusable(true);
        this.requestFocusInWindow();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);
        start();
    }

    private synchronized void start() {
        running = true;
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        Graphics gi = image.createGraphics();
        gi.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        gi.setColor(Color.WHITE);
        gi.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        gi.fillRect(0, 0, 1, 1);
        gi.setColor(Color.RED);
        gi.fillRect(SCREEN_WIDTH-1, SCREEN_HEIGHT-1, 1, 1);
        // gi.setColor(Color.gray);
        // gi.fillRect(SCREEN_WIDTH/2-2, SCREEN_HEIGHT/2-2, 8, 8);
        player.render(gi);
        // gi.setColor(Color.RED);
        // gi.fillPolygon(new int[]{50, 20, 30}, new int[]{40, 60, 10}, 3);
        // gi.fillRect(162, 92, 3, 3);

        // Draw the image such that 0, 0 is at the bottom left corner. Makes handling rotation easier
        g.drawImage(image, 0, 0+HEIGHT, WIDTH, -HEIGHT, null);
        g.dispose();
        bs.show();
    }

    public void run() {
        long lastTime = System.nanoTime();
        final double ns = 1000000000.0 / 60.0;//60 times per second
        double delta = 0;
        requestFocus();
        while(running) {
            long now = System.nanoTime();
            delta = delta + ((now-lastTime) / ns);
            lastTime = now;
            while (delta >= 1)//Make sure update is only happening 60 times a second
            {
                //handles all of the logic restricted time
                // screen.update(camera, pixels);
                camera.update();
                player.update();
                level[currentLevel].update();
                delta--;
            }
            render();//displays to the screen unrestricted time
        }
    }

    // Getters
    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public static Player getPlayer() {
        return player;
    }

    public static void main(String [] args) {
        Game game = new Game();
    }
}
