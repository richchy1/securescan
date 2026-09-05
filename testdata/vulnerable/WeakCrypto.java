import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class WeakCrypto {

    public void useMD5() {
        try {
            MessageDigest md = MessageDigest.getInstance("MD5");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    public void useSHA1() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-1");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}