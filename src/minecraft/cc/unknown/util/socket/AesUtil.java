package cc.unknown.util.socket;

import java.lang.reflect.Method;
import java.nio.charset.StandardCharsets;
import java.util.Base64;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class AesUtil {

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
    private byte[] generateIV(Object secretKey) {
        Class<?> c = Class.forName("java.security.MessageDigest");
        Method x = c.getMethod("getInstance", String.class);
        Object z = x.invoke(null, "SHA-256");
        Method v = c.getMethod("digest", byte[].class);
        byte[] b = (byte[]) v.invoke(z, secretKey.getClass().getMethod("getEncoded").invoke(secretKey));
        byte[] n = new byte[16];
        System.arraycopy(b, 0, n, 0, Math.min(b.length, 16));
        return n;
    }

    @SneakyThrows
    private SecretKey getSecretKey() {
        Class<?> c = Class.forName("java.security.MessageDigest");
        Method x = c.getMethod("getInstance", String.class);
        Object b = x.invoke(null, "SHA-256");
        Method v = c.getMethod("digest", byte[].class);
        byte[] n = (byte[]) v.invoke(b, SocketUtil.key.getBytes(StandardCharsets.UTF_8));
        Class<?> m = Class.forName("javax.crypto.spec.SecretKeySpec");
        return (SecretKey) m.getConstructor(byte[].class, String.class).newInstance(n, "AES");
    }

    @SneakyThrows
    private SecretKey getSecretKey2() {
        Class<?> q = Class.forName("java.security.MessageDigest");
        Method w = q.getMethod("getInstance", String.class);
        Object h = w.invoke(null, "SHA-256");
        Method e = q.getMethod("digest", byte[].class);
        byte[] r = (byte[]) e.invoke(h, "5q7inZ34T0LiadFjaZWexQryC0G8Fr".getBytes(StandardCharsets.UTF_8));
        Class<?> t = Class.forName("javax.crypto.spec.SecretKeySpec");
        return (SecretKey) t.getConstructor(byte[].class, String.class).newInstance(r, "AES");
    }

}