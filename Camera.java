import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;

public class Camera {
    // Zoom of camera; defined as horizontal radius of the camera
    /*
    __             __
    |               |

    <------>X

    |__           __|

    */
    private double zoom;
    private double centerx, centery, topy, leftx, bottomy, rightx;
    private Player player;
    
    // Constructor
    // 
    public Camera(int zoom) {
        this.zoom = zoom;
        player = Game.getPlayer();
    }

    public void update() {
        // Update camera coordinates
        centerx = player.getX();
        centery = player.getY();

        // Camera FOV info
        topy = (int) (centery + Math.ceil(zoom*9/16));
        bottomy = (int) (centery - Math.ceil(zoom*9/16));

        leftx = (int) (centerx - Math.ceil(zoom));
        rightx = (int) (centerx + Math.ceil(zoom));
    }

    public void render(Graphics g) {
        
        
    }

    // Getters
    public double getZoom() {
        return zoom;
    }

    public double getCenterX() {
        return centerx;
    }

    public double getTopY() {
        return topy;
    }

    public double getLeftX() {
        return leftx;
    }

    public double getBottomY() {
        return bottomy;
    }

    public double getRightX() {
        return rightx;
    }

    public void setZoom(double zoom) {
        this.zoom = zoom;
    }

}
