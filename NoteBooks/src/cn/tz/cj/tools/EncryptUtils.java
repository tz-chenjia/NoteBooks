package cn.tz.cj.tools;

import cn.tz.cj.bo.Auth;
import org.apache.commons.codec.binary.Base64;

import javax.crypto.*;
import javax.crypto.spec.SecretKeySpec;
import java.io.UnsupportedEncodingException;
import java.security.InvalidKeyException;
import java.security.Key;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

public class EncryptUtils {

    private static final String ALGORITHM = "AES";

    private static Key toKey(byte[] key) {
        SecretKey secretKey = new SecretKeySpec(key, ALGORITHM);
        return secretKey;
    }

    private static String encrypt(String data, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, UnsupportedEncodingException, BadPaddingException, IllegalBlockSizeException {
        Key k = toKey(Base64.decodeBase64(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = null;
        byte[] bytes = null;
        cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(1, secretKeySpec);
        bytes = cipher.doFinal(data.getBytes("UTF-8"));
        return Base64.encodeBase64String(bytes);
    }

    private static String decrypt(String data, String key) throws NoSuchPaddingException, NoSuchAlgorithmException, InvalidKeyException, BadPaddingException, IllegalBlockSizeException, UnsupportedEncodingException {
        Key k = toKey(Base64.decodeBase64(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = null;
        byte[] bytes = null;
        cipher = Cipher.getInstance(ALGORITHM);
        cipher.init(2, secretKeySpec);
        bytes = cipher.doFinal(Base64.decodeBase64(data));
        String r = null;
        r = new String(bytes, "UTF-8");
        return r;
    }

    private static String getSecrtKey(String encrypted) throws UnsupportedEncodingException {
        byte[] bytes = new byte[0];
        bytes = encrypted.getBytes("ISO8859-1");
        bytes = Arrays.copyOf(bytes, 16);
        return Base64.encodeBase64String(bytes);
    }

    public static String d(String data, String key) {
        String decrypt = null;
        try{
            decrypt = decrypt(data, getSecrtKey(key));
        }catch (Throwable e){
            GlobalExceptionHandling.exceptionHanding(e);
        }
        return decrypt;
    }

    public static String e(String data, String key) {
        String encrypt = null;
        try{
            encrypt = encrypt(data, getSecrtKey(key));
        }catch (Throwable e){
            GlobalExceptionHandling.exceptionHanding(e);
        }
        return encrypt;
    }

    public static String toEncryptWithUserPwd(String data) {
        String encrypt = null;
        try{
            encrypt = e(data, Auth.getInstance().getPwd());
        }catch (Throwable e){
            GlobalExceptionHandling.exceptionHanding(e);
        }
        return encrypt;
    }

    public static String toDencryptWithUserPwd(String data) {
        String decrypt = null;
        try{
            decrypt = d(data, Auth.getInstance().getPwd());
        }catch (Throwable e){
            GlobalExceptionHandling.exceptionHanding(e);
        }
        return decrypt;
    }

}
