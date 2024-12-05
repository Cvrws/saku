package cc.unknown.util.security.socket;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AesUtil {

    private final String PRIVATE_SECRET_KEY = "5q7inZ34T0LiadFjaZWexQryC0G8Fr";

	@SneakyThrows
    public String encrypt(String data) {
        SecretKey secretKey = getSecretKey();
        byte[] iv = generateIV(secretKey);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(encryptedData);
    }

    @SneakyThrows
    public String decrypt(String encryptedData) {
        SecretKey secretKey = getSecretKey();
        byte[] iv = generateIV(secretKey);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(encryptedBytes);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public String decrypt2(String encryptedData) {
        SecretKey secretKey = getSecretKey2();
        byte[] iv = generateIV(secretKey);
        IvParameterSpec ivSpec = new IvParameterSpec(iv);
        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedBytes = Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(encryptedBytes);
        return new String(decryptedData, StandardCharsets.UTF_8);
    }

    @SneakyThrows
    private byte[] generateIV(SecretKey secretKey) {
        byte[] iv = new byte[16]; 
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] keyBytes = sha.digest(secretKey.getEncoded());
        System.arraycopy(keyBytes, 0, iv, 0, iv.length);
        return iv;
    }

    @SneakyThrows
    private SecretKey getSecretKey() {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(SocketUtil.key.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, "AES");
    }

    @SneakyThrows
    private SecretKey getSecretKey2() {
        MessageDigest sha = MessageDigest.getInstance("SHA-256");
        byte[] key = sha.digest(PRIVATE_SECRET_KEY.getBytes(StandardCharsets.UTF_8));
        return new SecretKeySpec(key, "AES");
    }

}