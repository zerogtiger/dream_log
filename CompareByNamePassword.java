import java.util.*;

public class CompareByNamePassword implements Comparator<User> {
    public int compare(User u1, User u2) {
        int ret = u1.getName().compareToIgnoreCase(u2.getName());
        if (ret == 0)
            ret = u1.getPassword().compareTo(u2.getPassword());
        return ret;
    }
}
