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

    private static String encrypt(String data, String key) {
        Key k = toKey(Base64.decodeBase64(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = null;
        byte[] bytes = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(1, secretKeySpec);
            bytes = cipher.doFinal(data.getBytes("UTF-8"));
        } catch (NoSuchAlgorithmException e) {
            ExceptionHandleUtils.handling(e);
        } catch (NoSuchPaddingException e) {
            ExceptionHandleUtils.handling(e);
        } catch (BadPaddingException e) {
            ExceptionHandleUtils.handling(e);
        } catch (UnsupportedEncodingException e) {
            ExceptionHandleUtils.handling(e);
        } catch (IllegalBlockSizeException e) {
            ExceptionHandleUtils.handling(e);
        } catch (InvalidKeyException e) {
            ExceptionHandleUtils.handling(e);
        }
        return Base64.encodeBase64String(bytes);
    }

    private static String decrypt(String data, String key) {
        Key k = toKey(Base64.decodeBase64(key));
        byte[] raw = k.getEncoded();
        SecretKeySpec secretKeySpec = new SecretKeySpec(raw, ALGORITHM);
        Cipher cipher = null;
        byte[] bytes = null;
        try {
            cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(2, secretKeySpec);
            bytes = cipher.doFinal(Base64.decodeBase64(data));
        } catch (NoSuchAlgorithmException e) {
            ExceptionHandleUtils.handling(e);
        } catch (NoSuchPaddingException e) {
            ExceptionHandleUtils.handling(e);
        } catch (BadPaddingException e) {
            ExceptionHandleUtils.handling(e);
        } catch (IllegalBlockSizeException e) {
            ExceptionHandleUtils.handling(e);
        } catch (InvalidKeyException e) {
            ExceptionHandleUtils.handling(e);
        }
        String r = null;
        try {
            r = new String(bytes, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            ExceptionHandleUtils.handling(e);
        }
        return r;
    }

    private static String getSecrtKey(String encrypted) {
        byte[] bytes = new byte[0];
        try {
            bytes = encrypted.getBytes("ISO8859-1");
            bytes = Arrays.copyOf(bytes, 16);
        } catch (UnsupportedEncodingException e) {
            ExceptionHandleUtils.handling(e);
        }
        return Base64.encodeBase64String(bytes);
    }

    public static String d(String data, String key) {
        return  decrypt(data, getSecrtKey(key));
    }

    public static String e(String data, String key) {
        return encrypt(data, getSecrtKey(key));
    }

    public static String toEncryptWithUserPwd(String data){
        return e(data, Auth.getInstance().getPwd());
    }

    public static String toDencryptWithUserPwd(String data){
        return d(data, Auth.getInstance().getPwd());
    }

    public static void main(String[] args) {
        String data = "Abl20171026";
        String encryptData = e(data, "2");
        System.out.println("加密后数据" + encryptData);
        System.out.println("解密后数据" + d(encryptData, "1"));
    }

}
