import java.util.*;

public class SortByTime implements Comparator<User> {
    public int compare(User u1, User u2) {
        int ret = (int) (u1.getTimeElapsedSeconds() - u2.getTimeElapsedSeconds());
        if (ret == 0)
            ret = u1.getName().compareToIgnoreCase(u2.getName());
        return ret;
    }
}
