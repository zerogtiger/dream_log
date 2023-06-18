// Class description: Level 1 of the game, subclass of Level

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;
import javax.sound.sampled.*;

public class Level1 extends Level {

    // Constructor
    // Description: initializes variables related to level 1
    // Parameters: none
    // Return: none
    public Level1() {
        super(1, "level_data/level1/level1.txt", "level_data/level1/level1_data.txt", "level_data/level1/level_1.png", 0, 0);

        // super(0, "level_data/test.txt", "level_data/lobby_data.txt", 0, 0);
    }

    public void refresh() {
        // Play sounds depending on user position
        if (Game.player.getX() >=1 && Game.player.getX() < 21 && Game.user.getEnvironmentSounds()) {
            Game.springSound.start();
            Game.springSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
        else {
            Game.springSound.stop();
        }
        if (Game.player.getX() >=22 && Game.player.getX() < 42 && Game.user.getEnvironmentSounds()) {
            Game.summerSound.start();
            Game.summerSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
        else {
            Game.summerSound.stop();
        }
        if (Game.player.getX() >=43 && Game.player.getX() < 63 && Game.user.getEnvironmentSounds()) {
            Game.autumeSound.start();
            Game.autumeSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
        else {
            Game.autumeSound.stop();
        }
        if (Game.player.getX() >=64 && Game.player.getX() < 84 && Game.user.getEnvironmentSounds()) {
            Game.winterSound.start();
            Game.winterSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
        else {
            Game.winterSound.stop();
        }
    }
}
