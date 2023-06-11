import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;

public class Level1 extends Level {

    public Level1() {
        super(0, "level_data/level1/level1.txt", "level_data/level1/level1_data.txt", 0, 0);

        // super(0, "level_data/test.txt", "level_data/lobby_data.txt", 0, 0);
    }

    public void refresh() {
        return;
    }
}
