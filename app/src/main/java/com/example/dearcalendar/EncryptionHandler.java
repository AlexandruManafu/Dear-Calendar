package com.example.dearcalendar;

import java.math.BigInteger;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionHandler {
    public EncryptionHandler() {
    }

    private final String defaultPassword = "qwertyuiop123456";

    public String encrypt(String string,String password)
    {
        try {
            SecretKey keySpec = constructKey(password);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.ENCRYPT_MODE, keySpec);

            byte[] encrypted = cipher.doFinal(string.getBytes());
            return toHex(encrypted);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return string;
    }

    public String decrypt(String encrypted, String password)
    {
        try {
            byte[] enc = toByte(encrypted);
            SecretKey keySpec = constructKey(password);
            Cipher cipher = Cipher.getInstance("AES");
            cipher.init(Cipher.DECRYPT_MODE, keySpec);

            byte[] decrypted = cipher.doFinal(enc);
            return new String(decrypted);
        }catch (Exception e)
        {
            e.printStackTrace();
        }
        return encrypted;
    }

    private SecretKey constructKey(String password) {
        String keyValue;
        if (password.length() >= 32)
            keyValue = password.substring(0, 32);
        else if (password.length() >= 24)
            keyValue = password.substring(0, 24);
        else if (password.length() >= 16)
            keyValue = password.substring(0, 16);
        else if (password.length() >= 8)
        {
            keyValue = password + password;
            keyValue = keyValue.substring(0, 16);
        }
        else
            keyValue = defaultPassword;

        return new SecretKeySpec(keyValue.getBytes(), "AES");
    }

    public Boolean pwMatch(String hashed, String raw)
    {
        return hashed.equals(hash(raw));
    }

    public String hash(String string)
    {
        byte[] bytes = getBytes(string);
        if(bytes==null)
            return "";
        return toHex(bytes);
    }

    private byte[] getBytes(String string)
    {
        try {
            MessageDigest digest;
            digest = MessageDigest.getInstance("SHA-256");
            digest.update(string.getBytes(StandardCharsets.UTF_8), 0, string.length());
            return digest.digest();
        }catch (Exception e)
        {
            e.printStackTrace();
            return null;
        }
    }

    private String toHex(byte[] bytes)
    {
        BigInteger bi = new BigInteger(1, bytes);
        return String.format("%0" + (bytes.length << 1) + "x", bi);
    }

    private byte[] toByte(String hexString) {
        int len = hexString.length() / 2;
        byte[] result = new byte[len];
        for (int i = 0; i < len; i++)
            result[i] = Integer.valueOf(hexString.substring(2 * i, 2 * i + 2),
                    16).byteValue();
        return result;
    }
}
