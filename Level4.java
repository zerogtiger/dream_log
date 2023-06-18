
// Class description: Level 4 of the game, subclass of Level

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;

public class Level4 extends Level {

    // Constructor
    // Description: initializes variables related to level 4
    // Parameters: none
    // Return: none
    public Level4() {
        super(4, "level_data/level4/level4_unlabelled.txt", "level_data/level4/level4_data.txt", "level_data/level4/level_4.png", 0, 0);

        // super(0, "level_data/test.txt", "level_data/lobby_data.txt", 0, 0);
    }

    public void refresh() {
        return;
    }
}
