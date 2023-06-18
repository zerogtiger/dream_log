// Class description: helper class implementing a comparator interface to compare users by both name and password

import java.util.*;

public class SortByNamePassword implements Comparator<User> {
    // Description: compare function implementing the comparator interface
    // Parameters: the two users to be compared
    // Return: the string comparison of their name, and if identical, their hashed passwords
    public int compare(User u1, User u2) {
        int ret = u1.getName().compareToIgnoreCase(u2.getName());
        if (ret == 0)
            ret = u1.getPassword().compareTo(u2.getPassword());
        return ret;
    }
}
