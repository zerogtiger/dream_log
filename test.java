import java.util.*;
import java.io.*;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.nio.charset.*;
import java.math.BigInteger;

public class test {
    public static void main(String args[]) {
        // Scanner s = new Scanner(System.in);
        // while (true) {
        //     int a = s.nextInt();
        //     int b = s.nextInt();
        //     System.out.println(b-1 + "," + (27-a));
        // }
        // try{
        //     MessageDigest messageDigest = MessageDigest.getInstance("SHA-256");
        //     messageDigest.update("test".getBytes());
        //     String stringHash = new BigInteger(1,byteArr).toString(16);
        //     System.out.println(stringHash);
        // }
        // catch (NoSuchAlgorithmException e) {
        //     System.out.println(e);
        //
        // }
        try{
            MessageDigest digest = MessageDigest.getInstance("SHA-256");
            byte[] encodedhash = digest.digest("test".getBytes(StandardCharsets.UTF_8));
            String stringHash = new BigInteger(1,encodedhash).toString(16);
            System.out.println(stringHash);
        }
        catch (NoSuchAlgorithmException e) {
            System.out.println(e);

        }

    }
}
