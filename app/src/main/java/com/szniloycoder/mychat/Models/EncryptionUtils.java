package com.szniloycoder.mychat.Models;

import android.annotation.SuppressLint;
import android.util.Base64;

import com.bumptech.glide.load.Key;

import javax.crypto.Cipher;
import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import javax.crypto.spec.SecretKeySpec;

public class EncryptionUtils {

    private static final String ALGORITHM = "AES";
    private static final String TRANSFORMATION = "AES";

    // Generates a new AES key with 256-bit strength
    public static SecretKey generateKey() throws Exception {
        KeyGenerator keyGenerator = KeyGenerator.getInstance(ALGORITHM);
        keyGenerator.init(256);
        return keyGenerator.generateKey();
    }

    // Encrypts a string using the provided AES key
    public static String encrypt(String plaintext, SecretKey secretKey) throws Exception {
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.ENCRYPT_MODE, secretKey);
        byte[] encryptedBytes = cipher.doFinal(plaintext.getBytes(Key.STRING_CHARSET_NAME));
        return Base64.encodeToString(encryptedBytes, Base64.DEFAULT);
    }

    // Decrypts an encrypted string using the provided AES key
    public static String decrypt(String encryptedText, SecretKey secretKey) throws Exception {
        @SuppressLint("GetInstance") Cipher cipher = Cipher.getInstance(TRANSFORMATION);
        cipher.init(Cipher.DECRYPT_MODE, secretKey);
        byte[] decryptedBytes = cipher.doFinal(Base64.decode(encryptedText, Base64.DEFAULT));
        return new String(decryptedBytes, Key.STRING_CHARSET_NAME);
    }

    // Converts a base64-encoded string to a SecretKey
    public static SecretKey getKeyFromString(String keyString) throws Exception {
        byte[] decodedKey = Base64.decode(keyString, Base64.DEFAULT);
        return new SecretKeySpec(decodedKey, ALGORITHM);
    }

    // Converts a SecretKey to a base64-encoded string
    public static String keyToString(SecretKey secretKey) {
        return Base64.encodeToString(secretKey.getEncoded(), Base64.DEFAULT);
    }
}
