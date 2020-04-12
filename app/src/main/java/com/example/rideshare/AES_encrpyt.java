package com.example.rideshare;
import android.os.Build;
import android.support.annotation.RequiresApi;

import java.security.Key;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;


public class AES_encrpyt {
    private static final String ALGO="AES"; // Default uses ECB PKCS5Padding
    public static String message;

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encrypt(String Data, String secret) throws Exception {
        Key key = generateKey(secret);
        Cipher c = Cipher.getInstance(ALGO);
        c.init(Cipher.ENCRYPT_MODE, key);
        byte[] encVal = c.doFinal(Data.getBytes());
        String encryptedValue = Base64.getEncoder().encodeToString(encVal);
        return encryptedValue;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decrypt(String strToDecrypt, String secret) {
        try {
            Key key = generateKey(secret);
            Cipher cipher = Cipher.getInstance(ALGO);
            cipher.init(Cipher.DECRYPT_MODE, key);
            return new String(cipher.doFinal(Base64.getDecoder().decode(strToDecrypt)));
        } catch (Exception e) {

        }
        return null;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    private static Key generateKey(String secret) throws Exception {
        byte[] decoded = Base64.getDecoder().decode(secret.getBytes());
        Key key = new SecretKeySpec(decoded, ALGO);
        return key;
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String decodeKey(String str) {
        byte[] decoded = Base64.getDecoder().decode(str.getBytes());
        return new String(decoded);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public static String encodeKey(String str) {
        byte[] encoded = Base64.getEncoder().encode(str.getBytes());
        return new String(encoded);
    }

    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getEncryptedData(String msg) throws Exception {
        message=msg;
        String secretKey ="UwW@WarHawks1868";
        String encodedBase64Key = encodeKey(secretKey);
        System.out.println("EncodedBase64Key = " + encodedBase64Key);
        String encrStr = AES_encrpyt.encrypt(message, encodedBase64Key);
        return encrStr;
    }
    @RequiresApi(api = Build.VERSION_CODES.O)
    public String getDecryptedData(String msg) throws Exception {
        message=msg;
        String secretKey ="UwW@WarHawks1868";
        String encodedBase64Key = encodeKey(secretKey);
        String decrStr = AES_encrpyt.decrypt(message, encodedBase64Key);
        return decrStr;
    }


}
