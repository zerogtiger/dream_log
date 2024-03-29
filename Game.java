
// Class description: Hosts the game, responsible for sound, popup menus, and keeps track of all user info

import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;

public class Game extends Canvas implements Runnable {

    // Variable declarations
    private static final long serialVersionUID = 1L;
    private Thread thread;
    private boolean running;
    private BufferedImage image;

    private final JFrame frame;
    
    // Screen variabels
    public static final int WIDTH = 1200;
    public static final int HEIGHT = WIDTH*9/16;
    public static final int SCREEN_WIDTH = 320;
    public static final int SCREEN_HEIGHT = 180;

    // Wether the player should be performing these moves
    public static boolean forward, backward, left, right, turnLeft, turnRight;
    // Whether time should be sped up
    public static boolean warptime;

    // Game mechancis
    public static Player player;
    public static User user;
    public static LinkedList<User> allUsers = new LinkedList<>();
    public static TreeSet<User> completedUsers = new TreeSet<>(new SortByTime());
    public static Camera camera;
    public static Level[] level;
    // Current level player is on. 0 for main lobby
    public static int currentLevel;

    // Sounds
    public static Clip menuMusic, inGameMusic, walkingSound, springSound, summerSound, autumeSound, winterSound, thunderSound;

    // Option dialogue components
    private static JPanel loginPanel, newAccountPanel, leaderboardPanel, tutorialPanel;
    private static JTextField loginField, passwordField, newAccountField, newPassword1Field, newPassword2Field;
    private static JLabel loginLabel, newAccountLabel, tutorialTitleLabel, tutorialTopLabel, tutorialTopRightLabel, tutorialCenterLabel, tutorialBottomLabel, tutorialImageLabel, leaderboardLabel;
    private static final String[] loginOptions = {"New Account", "Cancel", "Log In"}, newAccountOptions = {"Cancel", "Sign Up"};
    private static JTable leaderboard; 
    private static Object[] header, ranking[];

    // Constructor
    // Descripton: initializes game variables
    // Parameters: none
    // Return: none
    public Game() {

        warptime = false;

        // Game component related initialization
        player = new Player(1.5, 1.5, 1);
        camera = new Camera(20);
        level = new Level[5];
        level[0] = new Lobby();
        level[1] = new Level1();
        level[2] = new Level2();
        level[3] = new Level3();
        level[4] = new Level4();
        currentLevel = 0;

        // Window related initializations
        setPreferredSize(new Dimension(WIDTH, HEIGHT));

        frame = new JFrame("dream_log");
        frame.setResizable(false);
        frame.setDefaultCloseOperation(JFrame.EXIT_ON_CLOSE);
        frame.setBackground(Color.black);

        thread = new Thread(this);
        image = new BufferedImage(SCREEN_WIDTH, SCREEN_HEIGHT, BufferedImage.TYPE_INT_RGB);

        frame.add(this);
        frame.setFocusable(true);
        frame.requestFocus();
        this.addKeyListener(new KeyInput());
        this.setFocusable(true);
        this.requestFocusInWindow();
        frame.pack();
        frame.setLocationRelativeTo(null);
        frame.setVisible(true);

        // Option dialogue related initializationz
        loginField = new JTextField(15);
        passwordField = new JTextField(15);
        newAccountField = new JTextField(15);
        newPassword1Field = new JTextField(15);
        newPassword2Field = new JTextField(15);

        loginLabel = new JLabel("test");
        loginLabel.setVisible(false);
        newAccountLabel = new JLabel("test2");
        newAccountLabel.setVisible(false);

        // Load in sounds
        try {
            //Menu background music
            AudioInputStream sound = AudioSystem.getAudioInputStream(new File("level_data/soundtrack/menuMusic.wav"));
            menuMusic = AudioSystem.getClip();
            menuMusic.open(sound);
            menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
            menuMusic.stop();

            sound = AudioSystem.getAudioInputStream(new File("level_data/soundtrack/inGameMusic.wav"));
            inGameMusic = AudioSystem.getClip();
            inGameMusic.open(sound);
            inGameMusic.loop(Clip.LOOP_CONTINUOUSLY);
            inGameMusic.stop();

            sound = AudioSystem.getAudioInputStream(new File("level_data/soundtrack/walking.wav"));
            walkingSound = AudioSystem.getClip();
            walkingSound.open(sound);
            walkingSound.loop(Clip.LOOP_CONTINUOUSLY);
            walkingSound.stop();

            sound = AudioSystem.getAudioInputStream(new File("level_data/soundtrack/spring.wav"));
            springSound = AudioSystem.getClip();
            springSound.open(sound);
            springSound.loop(Clip.LOOP_CONTINUOUSLY);
            springSound.stop();

            sound = AudioSystem.getAudioInputStream(new File("level_data/soundtrack/summer.wav"));
            summerSound = AudioSystem.getClip();
            summerSound.open(sound);
            summerSound.loop(Clip.LOOP_CONTINUOUSLY);
            summerSound.stop();

            sound = AudioSystem.getAudioInputStream(new File("level_data/soundtrack/autume.wav"));
            autumeSound = AudioSystem.getClip();
            autumeSound.open(sound);
            autumeSound.loop(Clip.LOOP_CONTINUOUSLY);
            autumeSound.stop();
            
            sound = AudioSystem.getAudioInputStream(new File("level_data/soundtrack/winter.wav"));
            winterSound = AudioSystem.getClip();
            winterSound.open(sound);
            winterSound.loop(Clip.LOOP_CONTINUOUSLY);
            winterSound.stop();
            
            sound = AudioSystem.getAudioInputStream(new File("level_data/soundtrack/thunder.wav"));
            thunderSound = AudioSystem.getClip();
            thunderSound.open(sound);
            thunderSound.loop(Clip.LOOP_CONTINUOUSLY);
            thunderSound.stop();
        } 
        catch (UnsupportedAudioFileException e) {
            System.out.println(e);
        }
        catch (LineUnavailableException e) {
            System.out.println(e);
        }
        catch (IOException e) {
            System.out.println(e);
        }
        
        // Log in panel
        loginPanel = new JPanel(new GridBagLayout());
        GridBagConstraints c = new GridBagConstraints();
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        loginPanel.add(new JLabel("Username: "), c);
        c.gridx = 1;
        c.gridwidth = 3;
        loginPanel.add(loginField, c);
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        loginPanel.add(new JLabel("Password: "), c);
        c.gridwidth = 3;
        c.gridx = 1;
        c.gridy = 1;
        loginPanel.add(passwordField, c);
        c.gridwidth = 2;
        c.gridy = 2;
        c.gridx = 1;
        loginPanel.add(loginLabel, c);

        // New account panel
        newAccountPanel = new JPanel(new GridBagLayout());
        c.weightx = 1.0;
        c.weighty = 1.0;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        newAccountPanel.add(new JLabel("Username: "), c);
        c.gridx = 1;
        c.gridwidth = 3;
        newAccountPanel.add(newAccountField, c);
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 1;
        newAccountPanel.add(new JLabel("Password: "), c);
        c.gridwidth = 3;
        c.gridx = 1;
        c.gridy = 1;
        newAccountPanel.add(newPassword1Field, c);
        c.gridwidth = 1;
        c.gridx = 0;
        c.gridy = 2;
        newAccountPanel.add(new JLabel("Confirm Password: "), c);
        c.gridwidth = 3;
        c.gridx = 1;
        c.gridy = 2;
        newAccountPanel.add(newPassword2Field, c);
        c.gridwidth = 2;
        c.gridx = 1;
        c.gridy = 3;
        newAccountPanel.add(newAccountLabel, c);

        // Tutorial and instructions page
        tutorialTitleLabel = new JLabel("dream_log");
        tutorialTitleLabel.setFont(new Font("consolas", Font.BOLD, 80));
        tutorialTopLabel = new JLabel("<HTML> This is your dream <br> &emsp &emsp --a log of your dream <br> &emsp &emsp &emsp &emsp __a dream_log</HTML>");
        tutorialTopLabel.setFont(new Font("consolas", Font.PLAIN, 18));
        tutorialTopRightLabel = new JLabel("A Game by zerogtiger (aka. Tiger Ding) for ICS4U");
        tutorialTopRightLabel.setFont(new Font("consolas", Font.PLAIN, 12));
        tutorialCenterLabel = new JLabel("Instructions");
        tutorialCenterLabel.setFont(new Font("consolas", Font.BOLD, 24));
        tutorialBottomLabel = new JLabel("<HTML>You may press [ESC] at any time to quit back to the lobby.<br> However, you are only allowed to enter another level once you finish the current one.<br> To continue a level, enter the \"continue\" portal. <br> You are forbidden to re-enter a level you have completed. <br><br>Complete all dream levels <br> &emsp __in the shortest possible time<br><br> Quit the game from the lobby's portal to avoid losing your progress <br><br> Don't lose yourself. </HTML>");
        tutorialBottomLabel.setFont(new Font("consolas", Font.PLAIN, 18));

        tutorialImageLabel = new JLabel(new ImageIcon("level_data/instructions.png"));
        tutorialPanel = new JPanel();
        tutorialPanel = new JPanel(new GridBagLayout());
        c.anchor = GridBagConstraints.WEST;
        c.fill = GridBagConstraints.BOTH;
        c.weightx = 1;
        c.weighty = 1;
        c.gridx = 0;
        c.gridy = 0;
        c.gridwidth = 1;
        tutorialPanel.add(tutorialTitleLabel, c);
        tutorialPanel.add(tutorialTopLabel, c);
        c.gridy = 1;
        tutorialPanel.add(new JLabel("   "), c);
        c.anchor = GridBagConstraints.EAST;
        c.gridy = 2;
        tutorialPanel.add(tutorialTopRightLabel, c);
        c.anchor = GridBagConstraints.WEST;
        JSeparator sep = new JSeparator(JSeparator.HORIZONTAL);
        c.gridy = 3;
        tutorialPanel.add(new JLabel("   "), c);
        c.gridy = 4;
        tutorialPanel.add(sep, c);
        c.gridy = 5;
        tutorialPanel.add(new JLabel("   "), c);
        c.gridy = 6;
        tutorialPanel.add(tutorialCenterLabel, c);
        c.gridy = 7;
        tutorialPanel.add(tutorialImageLabel, c);
        c.gridy = 8;
        tutorialPanel.add(new JLabel("   "), c);
        c.gridy = 9;
        tutorialPanel.add(tutorialBottomLabel, c);
        
        // Leaderboard screen
        header = new Object[]{"R_ank", "Use__rna_me", "T__ime (hh:mm:ss)"};
        ranking = new Object[][]{
            {"1", "-------", "-----"},
            {"2", "-------", "-----"}, 
            {"3", "-------", "-----"}, 
            {"4", "-------", "-----"}, 
            {"5", "-------", "-----"}, 
            {"6", "-------", "-----"}, 
            {"7", "-------", "-----"}, 
            {"8", "-------", "-----"}, 
            {"9", "-------", "-----"}, 
            {"10", "-------", "-----"}, 
            {"11", "-------", "-----"}, 
            {"12", "-------", "-----"}, 
            {"13", "-------", "-----"}, 
            {"14", "-------", "-----"}, 
            {"15", "-------", "-----"}, 
            {"16", "-------", "-----"}, 
            {"17", "-------", "-----"}, 
            {"18", "-------", "-----"}, 
            {"19", "-------", "-----"}, 
            {"20", "-------", "-----"}
        };

        // Leaderboard
        leaderboard = new JTable();
        leaderboard = new JTable(ranking, header) {
            public boolean isCellEditable(int row, int column) {
                return false;
            }
        };
		// Table appearance settings
        leaderboard.setFont(new Font("Consolas", Font.PLAIN, 14));
        leaderboard.setRowHeight(20);
        // leaderboard.getColumnModel().getColumn(0).setPreferredWidth(30);
        leaderboard.getColumnModel().getColumn(0).setMinWidth(45);
        leaderboard.getColumnModel().getColumn(0).setMaxWidth(45);
        leaderboard.getColumnModel().getColumn(1).setPreferredWidth(150);
        leaderboard.getColumnModel().getColumn(1).setMinWidth(60);
        // leaderboard.getColumnModel().getColumn(2).setPreferredWidth(40);
        leaderboard.getColumnModel().getColumn(2).setMinWidth(110);
        leaderboard.getColumnModel().getColumn(2).setMaxWidth(110);

        leaderboardLabel = new JLabel("lea_de_rboar__d");
        leaderboardLabel.setFont(new Font("consolas", Font.BOLD, 28));

        leaderboardPanel = new JPanel(new BorderLayout());

        leaderboardPanel.add(leaderboardLabel, BorderLayout.NORTH);
        leaderboardPanel.add(new JScrollPane(leaderboard), BorderLayout.CENTER);

        start();

        retriveAllUserInfo();

        getUserInfo();

    }

    // Descripton: Retreives all stored user info
    // Parameters: none
    // Return: none
    private void retriveAllUserInfo() {
        try {
            BufferedReader br = new BufferedReader(new FileReader("user_data/login_info.txt"));
            String line, tmpArray[];
            User tmp;
            while ((line = br.readLine()) != null) {
                tmpArray = line.split(" ");
                tmp = new User(tmpArray[0], tmpArray[1], "user_data/user_info/" + tmpArray[0] + ".txt");
                allUsers.add(tmp);
                if (tmp.getLevelsCleared().size() == 4) {
                    completedUsers.add(tmp);
                }
            }
            Collections.sort(allUsers);
            br.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(e);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }
    

    // Descripton: Stops all environment sounds
    // Parameters: none
    // Return: none
    public static void stopEnvironmentSound() {
        springSound.stop();
        summerSound.stop();
        autumeSound.stop();
        winterSound.stop();
        thunderSound.stop();
    }

    // Descripton: Gets info from current user
    // Parameters: none
    // Return: none
    private void getUserInfo() {
        int result;
        // While the current user has not been detectedx
        while (user == null) {
            // Repeatedly ask user for input if user closes the dialogue box
            passwordField.setText("");
            do {
                result = JOptionPane.showOptionDialog(null, loginPanel, "User Log In", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, loginOptions, null);
            } while (result == JOptionPane.CLOSED_OPTION);
            // If user wish to create a new account
            if (result == JOptionPane.YES_OPTION) {
                // Reset the textfields
                newAccountField.setText("");
                newPassword1Field.setText("");
                newPassword2Field.setText("");
                newAccountLabel.setVisible(false);
                boolean creatable = true;
                do {
                    // Get user input from dialogue box
                    result = JOptionPane.showOptionDialog(null, newAccountPanel, "Create Account", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, newAccountOptions, null);

                    // If selected create new account
                    if (result == JOptionPane.NO_OPTION) {
                        // Whether passwords match
                        creatable = newPassword1Field.getText().equals(newPassword2Field.getText());
                        // Whether there already exists such a username
                        creatable &= Collections.binarySearch(allUsers, new User(newAccountField.getText(), "")) < 0;
                        // If username already exists, show message
                        if (Collections.binarySearch(allUsers, new User(newAccountField.getText(), "")) >= 0) {
                            newAccountLabel.setForeground(Color.RED);
                            newAccountLabel.setText("<HTML>Duplicate username.<br> Please select an alternate username.</HTML>");
                            newAccountLabel.setVisible(true);
                        }
                        // If passwords don't match, show message
                        else if (!newPassword1Field.getText().equals(newPassword2Field.getText())) {
                            newAccountLabel.setForeground(Color.RED);
                            newAccountLabel.setText("<HTML>Password mismatch.<br> Please check both passwords match identically.</HTML>"); 
                            newAccountLabel.setVisible(true);
                        }
                        // Otherwise, the account is creatable
                        else {
                            newAccountLabel.setVisible(false);
                            // Create the new user
                            user = new User(newAccountField.getText(), User.sha256hash(newPassword1Field.getText()));
                            // Add to total list of users
                            allUsers.add(user);
                            // Append current user credentials to file
                            try {
                                // Append current user credentials
                                PrintWriter pr = new PrintWriter(new FileWriter("user_data/login_info.txt", true));
                                pr.println(user.getName() + " " + user.getPassword());
                                pr.close();
                            }
                            catch (IOException e) {
                                System.out.println(e);
                            }
                        }
                    }
                    // If selected cancel, revert back to previous menu
                    else if (result == JOptionPane.YES_OPTION) {
                        creatable = true;
                    }
                } while (result == JOptionPane.CLOSED_OPTION || !creatable);
            }
            // If selected cancel
            else if (result == JOptionPane.NO_OPTION) {
                stop();
                System.exit(0);
            }
            // Otherwise, if the user selected to log in with credentials
            else if (result == JOptionPane.CANCEL_OPTION) {
                // Get username and password (sha-256 encoded)
                String username = loginField.getText(), password = User.sha256hash(passwordField.getText());
                int idx;
                if ((idx = Collections.binarySearch(allUsers, new User(username, password), new SortByNamePassword())) >= 0) {
                    user = allUsers.get(idx);
                    loginLabel.setVisible(false);
                }
                else {
                    loginLabel.setForeground(Color.RED);
                    loginLabel.setText("<HTML>Unrecorded username or password, please try again.<br> If you do not have an account, press the \'New Account\' button.</HTML>");
                    loginLabel.setVisible(true);
                    passwordField.setText("");
                }
            }

        }
        // Set player lobby position
        player.setX(user.getLastLobbyX() + 0.5);
        player.setY(user.getLastLobbyY() + 0.5);
        player.setFacing(user.getLastFacing());
        // Show tutorial screen
        result = JOptionPane.showOptionDialog(null, Game.getTutorialPanel(), "About & Instructions", JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Close"}, null);
    }

    // Descripton: shows the leaderboard popup menu
    // Parameters: none
    // Return: none
    public static void showLeaderboard() {
        int counter = 0;
        for (User u : completedUsers) {
            ranking[counter][1] = u.getName();
            ranking[counter][2] = secondsToTime(u.getTimeElapsedSeconds());
            counter++;
        }
        JOptionPane.showOptionDialog(null, leaderboardPanel, "lea_de_rboar__d", JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Close"}, null);
    }

    // Descripton: Converts serconds to time in form of (hh:mm:ss)
    // Parameters: number of secomds
    // Return: formatted string displaying time in the form (hh:mm:ss)
    public static String secondsToTime(Long timeElapsedSeconds) {
        long hour = timeElapsedSeconds/3600;
        int minute = (int) (timeElapsedSeconds%3600/60);
        int second = (int) (timeElapsedSeconds%60);
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

    // Descripton: records all info of current user in a file before shutdown
    // Parameters: none
    // Return: none
    public static void recordUserInfo() {
        // User account record sequence
        try {
            // Create user file
            PrintWriter pr = new PrintWriter(new FileWriter("user_data/user_info/" + user.getName() + ".txt"));
            pr.println(player.getFacing());
            pr.println(Game.player.getPrevX() + " " + Game.player.getPrevY());
            pr.println(user.getLastLevel());
            pr.println(user.getLastLevelX() + " " + user.getLastLevelY());
            for (Integer i : user.getLevelsCleared()) {
                pr.print(" " + i);
            }
            pr.println(" ");
            pr.println(user.getTimeElapsedSeconds());
            pr.println(user.getMenuMusic()? 1 : 0);
            pr.println(user.getInGameMusic()? 1 : 0);
            pr.println(user.getEnvironmentSounds()? 1 : 0);
            pr.close();
        }
        catch (FileNotFoundException e) {
            System.out.println(e);
        }
        catch (IOException e) {
            System.out.println(e);
        }
    }

    // Descripton: Starts the thread
    // Parameters: none
    // Return: none
    private synchronized void start() {
        running = true;
        thread.start();
    }

    // Descripton: stops the thread
    // Parameters: none
    // Return: none
    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

    // Descripton: renders the stuff to the screen
    // Parameters: none
    // Return: none
    public void render() {
        BufferStrategy bs = this.getBufferStrategy();
        if(bs == null) {
            this.createBufferStrategy(3);
            return;
        }
        Graphics g = bs.getDrawGraphics();
        Graphics gi = image.createGraphics();
        gi.clearRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        gi.setColor(Color.WHITE);
        gi.fillRect(0, 0, SCREEN_WIDTH, SCREEN_HEIGHT);
        gi.fillRect(0, 0, 1, 1);
        gi.setColor(Color.RED);
        gi.fillRect(SCREEN_WIDTH-1, SCREEN_HEIGHT-1, 1, 1);
        // render level
        level[currentLevel].render(gi);
        // render player
        player.render(gi);

        // Draw the image such that 0, 0 is at the bottom left corner. Makes handling rotation easier
        g.drawImage(image, 0, 0+HEIGHT, WIDTH, -HEIGHT, null);
        g.setFont(new Font("Consolas", Font.PLAIN, 15));
        g.setColor(Color.WHITE);
        g.fillRect(20, HEIGHT-50, 88, 30);
        if (user != null) {
            g.setColor(Color.BLACK);
            g.drawString(secondsToTime(user.getTimeElapsedSeconds()) + "", 30, HEIGHT-30);
        }
        g.dispose();
        bs.show();
    }

    // Descripton: Main game loop
    // Parameters: none
    // Return: none
    public void run() {
        long lastTime = System.nanoTime();
        final double ns = 1000000000.0 / 60.0;//60 times per second
        double delta = 0;
        requestFocus();
        while(running) {
            long now = System.nanoTime();
            delta = delta + ((now-lastTime) / ns);
            lastTime = now;
            while (delta >= 1)//Make sure update is only happening 60 times a second
            {
                //handles all of the logic restricted time
                // screen.update(camera, pixels);
                level[currentLevel].update();
                camera.update();
                player.update();
                if (user != null) {
                    user.update();
                    if (user.getMenuMusic() && currentLevel == 0 && !menuMusic.isActive()) {
                        menuMusic.setFramePosition(0);
                        menuMusic.start();
                        menuMusic.loop(Clip.LOOP_CONTINUOUSLY);
                    }
                    else if (!user.getMenuMusic()) 
                        menuMusic.stop();
                    if (currentLevel == 0) {
                        stopEnvironmentSound();
                    }
                }
                delta--;
            }
            render();//displays to the screen unrestricted time
        }
    }

    // Getters
    public int getWidth() {
        return WIDTH;
    }

    public int getHeight() {
        return HEIGHT;
    }

    public static JPanel getTutorialPanel() {
        return tutorialPanel;
    }

    public static void main(String [] args) {
        Game game = new Game();
    }
}
