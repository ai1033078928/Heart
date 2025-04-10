package com.hrbu.crypto;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;
import java.util.Base64;
public class AESTest {
    public static void main(String[] args) {
        try {
            // 明文和秘钥
            String plainText = "10303877";
            String key = "qnbyzzwmdgghmcnm";

            // 转换秘钥
            SecretKeySpec secretKey = new SecretKeySpec(key.getBytes(), "AES");

            // 创建AES密码器
            Cipher cipher = Cipher.getInstance("AES");  // AES/ECB/PKCS7Padding

            // 初始化密码器为加密模式
            cipher.init(Cipher.ENCRYPT_MODE, secretKey);

            // 加密
            byte[] encryptedBytes = cipher.doFinal(plainText.getBytes());

            // 将密文进行Base64编码
            String encryptedText = Base64.getEncoder().encodeToString(encryptedBytes);

            // 打印密文
            System.out.println("Encrypted Text: " + encryptedText);

        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
