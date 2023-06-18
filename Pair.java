// Class description: helper class which stores an ordered pair of integers
import java.util.*;

public class Pair implements Comparable<Pair> {
    public int f, s;
    
    // Constructor
    // Description: initializes the first and second value of this pair
    // Parameters: first and second integer value
    // Return: none
    public Pair(int f, int s) {
        this.f = f;
        this.s = s;
    }

    // Description: compareTo function implementing the comparable interface, compares two pairs by its first, and if equal, second value
    // Parameters: the pair to be comapred to
    // Return: the difference in the first value, and if equal, the difference in the second value
    public int compareTo(Pair p) {
        if (f - p.f == 0) 
            return s - p.s;
        return f - p.f;
    }

    // Description: deterines if two pairs are equal by comparing both values
    // Parameters: the pair object to be compared to
    // Return: whether both values stored in each pair are exactly identical
    public boolean equals(Object o) {
        Pair p = (Pair) o;
        return f == p.f && s == p.s;
    }

    // Description: provides the hashcode for the pair via (lshift-16 f) bitwise xor (s)
    // Parameters: none
    // Return: the hashcode for the current pair
    public int hashCode() {
        return (f << 16) ^ (s);
    }
}
