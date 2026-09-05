import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class SafeCrypto {

    public void useSHA256() {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }
}