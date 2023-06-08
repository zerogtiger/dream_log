import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;

public abstract class Level {

    int levelNum;
    int[][] grid;
    Map<Pair, int[]> update;
    long timeElapsedms;

    public Level(int levelNum) {
        this.levelNum = levelNum;
        update = new HashMap<>();
        timeElapsedms = 0;
    }

    public int getLevelNum() {
        return levelNum;
    }

    public abstract void update();

}
