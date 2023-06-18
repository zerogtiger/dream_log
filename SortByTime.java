// Class description: helper class implementing a comparator interface to compare users by the time they took to complete the game
import java.util.*;

public class SortByTime implements Comparator<User> {
    // Description: compare function implementing the comparator interface
    // Parameters: the two users to be compared
    // Return: the difference between their times, and if equal, the string comparison of their names
    public int compare(User u1, User u2) {
        int ret = (int) (u1.getTimeElapsedSeconds() - u2.getTimeElapsedSeconds());
        if (ret == 0)
            ret = u1.getName().compareToIgnoreCase(u2.getName());
        return ret;
    }
}
