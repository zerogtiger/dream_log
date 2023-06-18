// class description: records all user account info, such as their last positions, time elapsed,
// and settings

import java.util.*;
import java.io.*;
import java.math.BigInteger;
import java.security.*;
import java.nio.charset.*;

public class User implements Comparable<User> {
    // username and password (sha-256 encoded)
    private String name, password;
    // last level[A
    private int lastLevel;
    // last positions
    private double lastLobbyX, lastLobbyY, lastLevelX, lastLevelY;
    private int lastFacing;
    // Levels cleared
    private HashSet<Integer> levelsCleared;
    // Total time taken so far
    private long timeElapsedSeconds;
    // Last toggled setting state
    private boolean menuMusic, inGameMusic, environmentSounds;

    // Counts number of game ticks for timer
    private int counter = 0;

    // Descripton: initializes with existing account
    // Parameters: user name, password (sha-256 hashed), and dedicated file storing other info
    // Return: none
    public User(String name, String password, String fileName) {
        this.name = name;
        this.password = password;
        String line;
        try {
            // Get input from file
            BufferedReader br = new BufferedReader(new FileReader(fileName));
            // Last lobby positions
            lastFacing = Integer.parseInt(br.readLine());
            line = br.readLine();
            lastLobbyX = Double.parseDouble(line.split(" ")[0]);
            lastLobbyY = Double.parseDouble(line.split(" ")[1]);
            // Last level info
            lastLevel = Integer.parseInt(br.readLine());
            line = br.readLine();
            lastLevelX = Double.parseDouble(line.split(" ")[0]);
            lastLevelY = Double.parseDouble(line.split(" ")[1]);
            // Levels cleared
            levelsCleared = new HashSet<>();
            String[] tmpLine = br.readLine().split(" ");
            for (String s : tmpLine) {
                if (s.equals("")) 
                    continue;
                levelsCleared.add(Integer.parseInt(s));
            }
            timeElapsedSeconds = Long.parseLong(br.readLine());
            menuMusic = br.readLine().trim().equals("1");
            inGameMusic = br.readLine().trim().equals("1");
            environmentSounds = br.readLine().trim().equals("1");
            br.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(e);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    // Descripton: initializes new account
    // Parameters: user name, password (sha-256 hashed)
    // Return: none
    public User(String name, String password) {
        this.name = name;
        this.password = password;
        // Last lobby positions
        lastLevel = 0;
        lastLobbyX = 2;
        lastLobbyY = 2;
        levelsCleared = new HashSet<>();
        timeElapsedSeconds = 0;
        menuMusic = true;
        inGameMusic = true;
        environmentSounds = true;
    }

    // Description: updates the timer for the user
    // Parameters: none
    // Return: none
    public void update() {
        if (levelsCleared.size() == 4)
            return;
        counter++;
        if (counter == 60) {
            timeElapsedSeconds++;
            counter=0;
        }
    }

    // Description: compareto function implementing the comparable interface
    // Parameters: user to be compared to
    // Return: string comparson by name
    public int compareTo(User u) {
        return name.compareToIgnoreCase(u.name);
    }

    // Description: hashes the string with sha-256
    // Parameters: the string to be hashed
    // Return: the hashed string
    public static String sha256hash(String password) { 
        try{
            // Hash with SHA-256
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest(password.getBytes(StandardCharsets.UTF_8));
            String stringHash = new BigInteger(1,encodedhash).toString(16);
            return stringHash;
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println(e);
            return null;
        }
    }
    
    // Setters
    public void setLastLevel(int level) {
        lastLevel = level;
    }

    public void setLastLobbyPosition(double x, double y) {
        lastLobbyX = x;
        lastLobbyY = y;
    }

    public void addLevelCleared(int level) {
        levelsCleared.add(level);
    }

    public void setLastLevelPosition(double x, double y) {
        lastLevelX = x;
        lastLevelY = y;
    }

    public void setLastfacing(int facing) {
        this.lastFacing = facing;
    }

    public void setMenuMusic(boolean menuMusic) {
        this.menuMusic = menuMusic;
    }

    public void setInGameMusic(boolean inGameMusic) {
        this.inGameMusic = inGameMusic;
    }

    public void setEnvironmentSounds(boolean environmentSounds) {
        this.environmentSounds = environmentSounds;
    }

    // Getters
    public int getLastFacing() {
        return lastFacing;
    }

    public int getLastLevel() {
        return lastLevel;
    }

    public double getLastLevelX() {
        return lastLevelX;
    }

    public double getLastLevelY() {
        return lastLevelY;
    }

    public double getLastLobbyX() {
        return lastLobbyX;
    }

    public double getLastLobbyY() {
        return lastLobbyY;
    }

    public HashSet<Integer> getLevelsCleared() {
        return levelsCleared;
    }

    public long getTimeElapsedSeconds() {
        return timeElapsedSeconds;
    }

    public boolean getMenuMusic() {
        return menuMusic;
    }

    public boolean getInGameMusic() {
        return inGameMusic;
    }

    public boolean getEnvironmentSounds() {
        return environmentSounds;
    }

    public String getName() {
        return name;
    }
    public String getPassword() {
        return password;
    }
}
