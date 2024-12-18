package cc.unknown.util.socket;

import lombok.SneakyThrows;
import lombok.experimental.UtilityClass;

@UtilityClass
public class EncryptUtil {
	
	// code war crimes

	@SneakyThrows
    public String encrypt(String data) {
		javax.crypto.SecretKey secretKey = getSecretKey();
        byte[] iv = generateIV(secretKey);
        javax.crypto.spec.IvParameterSpec ivSpec = new javax.crypto.spec.IvParameterSpec(iv);
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(javax.crypto.Cipher.ENCRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedData = cipher.doFinal(data.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        return java.util.Base64.getEncoder().encodeToString(encryptedData);
    }

    @SneakyThrows
    public String decrypt(String encryptedData) {
    	javax.crypto.SecretKey secretKey = getSecretKey();
        byte[] iv = generateIV(secretKey);
        javax.crypto.spec.IvParameterSpec ivSpec = new javax.crypto.spec.IvParameterSpec(iv);
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedBytes = java.util.Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(encryptedBytes);
        return new String(decryptedData, java.nio.charset.StandardCharsets.UTF_8);
    }

    @SneakyThrows
    public String decrypt2(String encryptedData) {
    	javax.crypto.SecretKey secretKey = getSecretKey2();
        byte[] iv = generateIV(secretKey);
        javax.crypto.spec.IvParameterSpec ivSpec = new javax.crypto.spec.IvParameterSpec(iv);
        javax.crypto.Cipher cipher = javax.crypto.Cipher.getInstance("AES/CBC/PKCS5Padding");
        cipher.init(javax.crypto.Cipher.DECRYPT_MODE, secretKey, ivSpec);
        byte[] encryptedBytes = java.util.Base64.getDecoder().decode(encryptedData);
        byte[] decryptedData = cipher.doFinal(encryptedBytes);
        return new String(decryptedData, java.nio.charset.StandardCharsets.UTF_8);
    }

    @SneakyThrows
    private byte[] generateIV(Object secretKey) {
        Class<?> c = Class.forName("java.security.MessageDigest");
        java.lang.reflect.Method x = c.getMethod("getInstance", String.class);
        Object z = x.invoke(null, "SHA-256");
        java.lang.reflect.Method v = c.getMethod("digest", byte[].class);
        byte[] b = (byte[]) v.invoke(z, secretKey.getClass().getMethod("getEncoded").invoke(secretKey));
        byte[] n = new byte[16];
        System.arraycopy(b, 0, n, 0, Math.min(b.length, 16));
        return n;
    }

    @SneakyThrows
    private javax.crypto.SecretKey getSecretKey() {
        Class<?> c = Class.forName("java.security.MessageDigest");
        java.lang.reflect.Method x = c.getMethod("getInstance", String.class);
        Object b = x.invoke(null, "SHA-256");
        java.lang.reflect.Method v = c.getMethod("digest", byte[].class);
        byte[] n = (byte[]) v.invoke(b, SocketUtil.key.getBytes(java.nio.charset.StandardCharsets.UTF_8));
        Class<?> m = Class.forName("javax.crypto.spec.SecretKeySpec");
        return (javax.crypto.SecretKey) m.getConstructor(byte[].class, String.class).newInstance(n, "AES");
    }

    @SneakyThrows
    private javax.crypto.SecretKey getSecretKey2() {
        Class<?> q = Class.forName("java.security.MessageDigest");
        java.lang.reflect.Method w = q.getMethod("getInstance", String.class);
        Object h = w.invoke(null, "SHA-256");
        java.lang.reflect.Method e = q.getMethod("digest", byte[].class);
        byte[] r = (byte[]) e.invoke(h, "5q7inZ34T0LiadFjaZWexQryC0G8Fr".getBytes(java.nio.charset.StandardCharsets.UTF_8));
        Class<?> t = Class.forName("javax.crypto.spec.SecretKeySpec");
        return (javax.crypto.SecretKey) t.getConstructor(byte[].class, String.class).newInstance(r, "AES");
    }

}