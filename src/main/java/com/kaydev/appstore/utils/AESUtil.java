package com.kaydev.appstore.utils;

import javax.crypto.Cipher;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
import java.security.SecureRandom;

public class AESUtil {
    public static String encryptData(String data, String secretKey) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
        byte[] iv = new byte[16];
        new SecureRandom().nextBytes(iv); // Generate random IV
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.ENCRYPT_MODE, key, ivParameterSpec);
        byte[] encryptedBytes = cipher.doFinal(data.getBytes());
        String encryptedData = Base64.getEncoder().encodeToString(encryptedBytes);
        return Base64.getEncoder().encodeToString(iv) + ":" + encryptedData;
    }

    public static String decryptData(String encryptedDataWithIV, String secretKey) throws Exception {
        String[] parts = encryptedDataWithIV.split(":");
        byte[] iv = Base64.getDecoder().decode(parts[0]);
        String encryptedData = parts[1];
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        SecretKeySpec key = new SecretKeySpec(secretKey.getBytes(), "AES");
        IvParameterSpec ivParameterSpec = new IvParameterSpec(iv);
        cipher.init(Cipher.DECRYPT_MODE, key, ivParameterSpec);
        byte[] decryptedBytes = cipher.doFinal(Base64.getDecoder().decode(encryptedData));
        return new String(decryptedBytes);
    }

}
