// Class description: lobby of the game, subclass of Level

import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;

public class Lobby extends Level {

    // Constructor
    // Description: initializes variables related to the lobby
    // Parameters: none
    // Return: none
    public Lobby() {
        super(0, "level_data/lobby/lobby.txt", "level_data/lobby/lobby_data.txt", "level_data/lobby/lobby.png", 0, 0);
        // Set initial position
        Game.player.setX(2.5);
        Game.player.setY(2.5);
        // Dummy value to avoid errors
        exit.f = 0;
        exit.s = 0;
    }

    public void refresh() {
        return;
    }
}
