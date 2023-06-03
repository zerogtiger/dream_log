import java.awt.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

    public void keyPressed(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            Game.forward = true;
            Game.backward = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_S) {
            Game.backward = true;
            Game.forward = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_D) {
            Game.right = true;
            Game.left = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_A) {
            Game.left = true;
            Game.right = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            Game.turnRight = true;
            Game.turnLeft = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            Game.turnLeft = true;
            Game.turnRight = false;
            // System.out.println("turnLeft: " + turnLeft);
        }
    }

    public void keyReleased(KeyEvent e) {
        if (e.getKeyCode() == KeyEvent.VK_W) {
            Game.forward = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_S) {
            Game.backward = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_D) {
            Game.right = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_A) {
            Game.left = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_RIGHT) {
            Game.turnRight = false;
        }
        else if (e.getKeyCode() == KeyEvent.VK_LEFT) {
            Game.turnLeft = false;
        }
    }
}
