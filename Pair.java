import java.util.*;

public class Pair implements Comparable<Pair> {
    public int f, s;
    
    public Pair(int f, int s) {
        this.f = f;
        this.s = s;
    }

    public int compareTo(Pair p) {
        if (f - p.f == 0) 
            return s - p.s;
        return f - p.f;
    }

    public boolean equals(Object o) {
        Pair p = (Pair) o;
        return f == p.f && s == p.s;
    }

    public int hashCode() {
        return (f << 16) ^ (s);
    }
}
