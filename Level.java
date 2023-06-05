import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.*;

public abstract class Level {

    int levelNum;
    int[][] grid;
    Map<

    public Level(int levelNum, int[][] grid) {
        this.levelNum = levelNum;
        this.grid = grid;
    }

    public int getLevelNum() {
        return levelNum;
    }

}
