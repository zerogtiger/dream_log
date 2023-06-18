// Class description: records keyinputs from the user and performs according functions

import java.awt.*;
import javax.sound.sampled.*;
import java.awt.event.KeyAdapter;
import java.awt.event.KeyEvent;

public class KeyInput extends KeyAdapter {

    // Description: performs events when a key is pressed
    // Parameters: key that user pressed
    // Return: none
    public void keyPressed(KeyEvent e) {
        // Do not recognize keys if no account has been attached
        if (Game.user == null)
            return;
        // Movement / turning related
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
        }
        // Escape to main lobby
        else if (e.getKeyCode() == KeyEvent.VK_ESCAPE) {
            if (Game.currentLevel != 0) {
                Game.user.setLastLevel(Game.currentLevel);
                Game.user.setLastLevelPosition(Game.player.getX(), Game.player.getY());
                Game.currentLevel = 0;
                Game.player.setX(Game.user.getLastLobbyX());
                Game.player.setY(Game.user.getLastLobbyY());
                Game.inGameMusic.stop();
                Game.stopEnvironmentSound();
                // Start music
                if (Game.user.getMenuMusic()) {
                    Game.menuMusic.setFramePosition(0);
                    Game.menuMusic.start();
                    Game.menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
                }
            }
        }
    }

    // Description: performs events when a key is released
    // Parameters: key that user released
    // Return: none
    public void keyReleased(KeyEvent e) {
        // Movement / turning related keypresses
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
