package com.hrbu.crypto;


import cn.hutool.core.collection.CollUtil;
import cn.hutool.core.io.file.FileWriter;
import cn.hutool.core.text.csv.CsvUtil;
import cn.hutool.core.text.csv.CsvWriter;
import cn.hutool.core.util.CharsetUtil;
import cn.hutool.core.util.StrUtil;
import cn.hutool.crypto.SecureUtil;
import cn.hutool.crypto.SmUtil;
import cn.hutool.crypto.asymmetric.KeyType;
import cn.hutool.crypto.asymmetric.SM2;
import cn.hutool.crypto.symmetric.SymmetricAlgorithm;
import cn.hutool.crypto.symmetric.SymmetricCrypto;
import org.junit.Test;

import java.io.File;
import java.security.KeyPair;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.List;

/**
 * 1.对称加密
 * 2.非对称加密
 * 3.摘要加密
 * 4.消息验证码算法
 * 5.签名和验证
 * 6.国密算法
 */
public class 加密解密 {

    /**
     * 非对称加密和签名：SM2
     */
    @Test
    public void Main () {

        String text = "https://doc.xugaoyi.com/";

        SM2 sm2 = SmUtil.sm2();
        // 公钥加密，私钥解密
        String encryptStr = sm2.encryptBcd(text, KeyType.PublicKey);
        String decryptStr = StrUtil.utf8Str(sm2.decryptFromBcd(encryptStr, KeyType.PrivateKey));
        System.out.println(StrUtil.format("加密后的字符串为{}\n解密后的字符串为{}", encryptStr, decryptStr));
    }

    /**
     * 非对称加密和签名：SM2
     * 自定义密钥对加密或解密
     */
    @Test
    public void Main2 () {

        String text = "https://doc.xugaoyi.com/";

        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        byte[] privateKey = pair.getPrivate().getEncoded();
        byte[] publicKey = pair.getPublic().getEncoded();

        SM2 sm2 = SmUtil.sm2(privateKey, publicKey);
        // 公钥加密，私钥解密
        String encryptStr = sm2.encryptBcd(text, KeyType.PublicKey);
        String decryptStr = StrUtil.utf8Str(sm2.decryptFromBcd(encryptStr, KeyType.PrivateKey));
        System.out.println(StrUtil.format("加密后的字符串为{}\n解密后的字符串为{}", encryptStr, decryptStr));

        /*CsvWriter writer = CsvUtil.getWriter(new File("PublicKey.csv"), CharsetUtil.CHARSET_UTF_8);
        writer.write(Collections.singleton(publicKey));*/

        /*FileWriter writer = new FileWriter("PublicKey.pub");
        writer.write(String.valueOf(publicKey));*/
    }

    /**
     * SM2签名和验签
     */
    @Test
    public void Main3 () {
        String content = "我是Hanley.";
        KeyPair pair = SecureUtil.generateKeyPair("SM2");
        final SM2 sm2 = new SM2(pair.getPrivate(), pair.getPublic());

        byte[] sign = sm2.sign(content.getBytes());

        // true
        boolean verify = sm2.verify(content.getBytes(), sign);

        System.out.println(StrUtil.format("结果为: {}", verify));
    }

    /**
     * 测试闭包参数...
     */
    @Test
    public void dispalyParams() {
        System.out.println(getParams("hello", "world", "123", "888"));
    }
    public String getParams(String...values) {
        List list = new ArrayList<String>();
        list.addAll(Arrays.asList(values));
        return CollUtil.join(list, " ");
    }

    @Test
    public void Test() {
        String content = "1T/VfITZXpxzlvtB68eKdv1EbyZTbV0ozqSk6WaU9Zw=";  // oqA3YKecxMsBSPIX+ulDOg==  8TccGZU+eDw/4duT4jIVkw==
        String 职能职责 = "vw_pm_funcresp";
        String 专项项目目录 = "vw_pm_specialdirectory";

        //随机生成密钥
        byte[] key = "bosssoft20220527".getBytes()/*SecureUtil.generateKey(SymmetricAlgorithm.AES.getValue()).getEncoded()*/;

        //构建
        SymmetricCrypto aes = new SymmetricCrypto(SymmetricAlgorithm.AES, key);

        //加密
        // byte[] encrypt = aes.encrypt(content);
        //解密
        // byte[] decrypt = aes.decrypt(encrypt);

        //加密为16进制表示
        // String encryptHex = aes.encryptHex(content);
        //解密为字符串
        String decryptStr = aes.decryptStr(content, CharsetUtil.CHARSET_UTF_8);
        System.out.println(decryptStr);

        System.out.println(aes.encryptBase64(职能职责, CharsetUtil.CHARSET_UTF_8));
        System.out.println(aes.encryptBase64(专项项目目录, CharsetUtil.CHARSET_UTF_8));
    }

    /*@Test
    public void Test2() {
        BCryptPasswordEncoder bCryptPasswordEncoder = new BCryptPasswordEncoder();
        *//*String hashPass = bCryptPasswordEncoder.encode("1");
        System.out.println(hashPass);*//*

        // $2a$10$aCJVHVsN1f2PZcjy3s7fReZW7APziBhiq62HPoB4JrsqOu/J2zFi.
        boolean f = bCryptPasswordEncoder.matches("1","$2a$10$2iTnGTOC1.fMUiVE.QJEW.d66E.YAi3FIs7JZ76mBPw0Zv4GOxDt.");
        System.out.println(f);
    }*/

}
