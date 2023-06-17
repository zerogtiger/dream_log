
import javax.print.attribute.standard.JobMessageFromOperator;
import javax.sound.sampled.*;
import javax.swing.*;
import java.util.*;
import java.io.*;
import java.awt.image.*;
import java.awt.*;
import java.awt.event.*;

public class Game extends Canvas implements Runnable {

    private static final long serialVersionUID = 1L;
    private Thread thread;
    private boolean running;
    private BufferedImage image;

    private final JFrame frame;
    
    public static final int WIDTH = 1200;
    public static final int HEIGHT = WIDTH*9/16;
    public static final int SCREEN_WIDTH = 320;
    public static final int SCREEN_HEIGHT = 180;

    // Wether the player should be performing these moves
    public static boolean forward, backward, left, right, turnLeft, turnRight;
    // Whether time should be sped up
    public static boolean warptime;

    public static Player player;
    public static User user;
    public static LinkedList<User> allUsers = new LinkedList<>();
    public static TreeSet<User> completedUsers = new TreeSet<>(new SortByTime());
    public static Camera camera;
    public static Level[] level;
    // Current level player is on. 0 for main lobby
    public static int currentLevel;

    // Option dialogue components
    private static JPanel loginPanel, newAccountPanel, leaderboardPanel, tutorialPanel;
    private static JTextField loginField, passwordField, newAccountField, newPassword1Field, newPassword2Field;
    private static JLabel loginLabel, newAccountLabel, tutorialTopLabel, tutorialTopRightLabel, tutorialCenterLabel, tutorialBottomLabel, tutorialImageLabel, leaderboardLabel;
    private static final String[] loginOptions = {"New Account", "Cancel", "Log In"}, newAccountOptions = {"Cancel", "Sign Up"};
    private static JTable leaderboard; 
    private static Object[] header, ranking[];

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
        start();

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

        tutorialTopLabel = new JLabel("<HTML> This is your dream <br> &emsp &emsp --a log of your dream <br> &emsp &emsp &emsp &emsp __a dream_log</HTML>");
        tutorialTopLabel.setFont(new Font("consolas", Font.PLAIN, 18));
        tutorialTopRightLabel = new JLabel("A Game by zerogtiger (aka. Tiger Ding) for ICS4U");
        tutorialTopRightLabel.setFont(new Font("consolas", Font.PLAIN, 12));
        tutorialCenterLabel = new JLabel("Instructions");
        tutorialCenterLabel.setFont(new Font("consolas", Font.BOLD, 28));
        tutorialBottomLabel = new JLabel("<HTML>Complete all dream levels <br> &emsp __in the shortest possible time </HTML>");
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

        retriveAllUserInfo();

        getUserInfo();
    }

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


    private void getUserInfo() {
        int result;
        // While the current user has not been detectedx
        while (user == null) {
            // Repeatedly ask user for input if user closes the dialogue box
            passwordField.setText("");
            do {
                result = JOptionPane.showOptionDialog(null, loginPanel, "Log in", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.PLAIN_MESSAGE, null, loginOptions, null);
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
                    result = JOptionPane.showOptionDialog(null, newAccountPanel, "Log in", JOptionPane.YES_NO_OPTION, JOptionPane.PLAIN_MESSAGE, null, newAccountOptions, null);

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
    }

    public static void showLeaderboard() {
        int counter = 0;
        for (User u : completedUsers) {
            ranking[counter][1] = u.getName();
            ranking[counter][2] = secondsToTime(u.getTimeElapsedSeconds());
            counter++;
        }
        JOptionPane.showOptionDialog(null, leaderboardPanel, "lea_de_rboar__d", JOptionPane.YES_OPTION, JOptionPane.PLAIN_MESSAGE, null, new Object[]{"Close"}, null);
    }

    public static String secondsToTime(Long timeElapsedSeconds) {
        long hour = timeElapsedSeconds/3600;
        int minute = (int) (timeElapsedSeconds%3600/60);
        int second = (int) (timeElapsedSeconds%60);
        return String.format("%02d:%02d:%02d", hour, minute, second);
    }

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
    private synchronized void start() {
        running = true;
        thread.start();
    }

    public synchronized void stop() {
        running = false;
        try {
            thread.join();
        } catch(InterruptedException e) {
            e.printStackTrace();
        }
    }

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
        // gi.setColor(Color.gray);
        // gi.fillRect(SCREEN_WIDTH/2-2, SCREEN_HEIGHT/2-2, 8, 8);
        player.render(gi);
        // gi.setColor(Color.RED);
        // gi.fillPolygon(new int[]{50, 20, 30}, new int[]{40, 60, 10}, 3);
        // gi.fillRect(162, 92, 3, 3);

        // Draw the image such that 0, 0 is at the bottom left corner. Makes handling rotation easier
        g.drawImage(image, 0, 0+HEIGHT, WIDTH, -HEIGHT, null);
        g.dispose();
        bs.show();
    }

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
