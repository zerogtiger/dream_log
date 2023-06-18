import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;

public class Lobby extends Level {

    public Lobby() {
        super(0, "level_data/lobby/lobby.txt", "level_data/lobby/lobby_data.txt", "level_data/lobby/lobby.png", 0, 0);
        Game.player.setX(2.5);
        Game.player.setY(2.5);
        exit.f = 0;
        exit.s = 0;

        // super(0, "level_data/test.txt", "level_data/lobby_data.txt", 0, 0);
    }

    public void refresh() {
        return;
    }
}
