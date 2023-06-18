// Class description: Level 2 of the game, subclass of Level
import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;

public class Level2 extends Level {

    // Constructor
    // Description: initializes variables related to level 2
    // Parameters: none
    // Return: none
    public Level2() {
        super(2, "level_data/level2/level2.txt", "level_data/level2/level2_data.txt","level_data/level2/level_2.png", 0, 0);

        // super(0, "level_data/test.txt", "level_data/lobby_data.txt", 0, 0);
    }

    public void refresh() {
        // Play sounds depending on user position
        if (Game.player.getX() >=36 && Game.player.getX() < 63 && Game.player.getY() >=37 && Game.player.getY() <47 && Game.user.getEnvironmentSounds()) {
            Game.springSound.start();
            Game.springSound.loop(Clip.LOOP_CONTINUOUSLY);
        }
        else {
            Game.springSound.stop();
        }
    }
}
