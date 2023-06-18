// Class description: Level 2 of the game, subclass of Level

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;

public class Level3 extends Level {

    // Constructor
    // Description: initializes variables related to level 3
    // Parameters: none
    // Return: none
    public Level3() {
        super(3, "level_data/level3/level3.txt", "level_data/level3/level3_data.txt", "level_data/level3/level_3.png", 0, 0);

        // super(0, "level_data/test.txt", "level_data/lobby_data.txt", 0, 0);
    }

    public void refresh() {
        return;
    }
}
