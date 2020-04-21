package com.xinsite.common.uitls.codec;

import com.xinsite.common.uitls.lang.StringUtils;
import org.apache.commons.codec.binary.Base64;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.crypto.Cipher;
import java.io.*;
import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.util.*;

/**
 * RSA 不对称加密与解密
 *
 * @author www.xinsite.vip
 */
public class RSAUtils {
    private static final Logger log = LoggerFactory.getLogger(RSAUtils.class);
    private static Map<Integer, String> keyMap = new HashMap<Integer, String>();  //用于封装随机产生的公钥与私钥
    private static String secretKey = "sa21,4dss,fdes";
    private static List<Map<Integer, String>> listKey = new ArrayList<>();
    private static Random rand = new Random();

//    public static void main(String[] args) throws Exception {
//        //生成公钥和私钥
//        Map<Integer, String> keyMap = RSAUtils.genKeyPair();
//        //加密字符串
//        String message = "df723820";
//        System.out.println("随机生成的公钥为:" + keyMap.get(0));
//        System.out.println("随机生成的私钥为:" + keyMap.get(1));
//        String messageEn = encrypt(message, keyMap.get(0));
//        System.out.println(message + "\t加密后的字符串为:" + messageEn);
//        String messageDe = decrypt(messageEn, keyMap.get(1));
//        System.out.println("还原后的字符串为:" + messageDe);
//    }

    public static int getKeyPairCount() {
        return listKey.size();
    }

    /**
     * 随机获取一个密钥对
     */
    public static Map<Integer, String> randomKeyPair() {
        if (listKey.size() == 0) {
            listKey.add(generateKeyPair());
            return listKey.get(0);
        } else {
            return listKey.get(rand.nextInt(listKey.size()));
        }
    }

    /**
     * 加一个密钥对，最多20个
     */
    public static void addKeyPair() {
        if (listKey.size() < 21) {
            listKey.add(generateKeyPair());
        }
    }

    /**
     * 移除一个密钥对
     */
    public static void removeKeyPair() {
        if (listKey.size() > 10) {
            listKey.remove(rand.nextInt(listKey.size() - 10));
        }
    }

    /**
     * 获取生成密钥对
     *
     * @param new_key 是否要新生成密钥对
     */
    public static Map<Integer, String> genKeyPair(boolean new_key) {
        if (!new_key) {
            if (keyMap.size() == 0) keyMap = generateKeyPair();
            return keyMap;
        }
        return generateKeyPair();
    }

    /**
     * 随机生成密钥对
     */
    public static Map<Integer, String> generateKeyPair() {
        // 将公钥和私钥保存到Map
        Map<Integer, String> keyMap = new HashMap();
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = null;
        try {
            keyPairGen = KeyPairGenerator.getInstance("RSA");
            // 初始化密钥对生成器，密钥大小为512-1024位
            keyPairGen.initialize(1024, new SecureRandom());
            // 生成一个密钥对，保存在keyPair中
            KeyPair keyPair = keyPairGen.generateKeyPair();
            RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate();   // 得到私钥
            RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();       // 得到公钥
            String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));
            // 得到私钥字符串
            String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded())));
            keyMap.put(0, publicKeyString);  //0表示公钥
            keyMap.put(1, privateKeyString);  //1表示私钥
        } catch (NoSuchAlgorithmException e) {
            log.error("RSA生成密钥对异常", e);
            keyMap.put(0, secretKey);  //0表示公钥
            keyMap.put(1, secretKey);  //1表示私钥
        }
        return keyMap;
    }

    /**
     * RSA公钥加密
     *
     * @param str       加密字符串
     * @param publicKey 公钥
     * @return 密文
     * 加密失败返回DES加密密文
     */
    public static String encrypt(String str, String publicKey) {
        if (StringUtils.isEmpty(str)) return StringUtils.EMPTY;
        if (StringUtils.isEmpty(publicKey)) return StringUtils.EMPTY;
        byte[] decoded = Base64.decodeBase64(publicKey);  //base64编码的公钥
        RSAPublicKey pubKey = null;
        try {
            pubKey = (RSAPublicKey) KeyFactory.getInstance("RSA").generatePublic(new X509EncodedKeySpec(decoded));
            Cipher cipher = Cipher.getInstance("RSA"); //RSA加密
            cipher.init(Cipher.ENCRYPT_MODE, pubKey);
            return Base64.encodeBase64String(cipher.doFinal(str.getBytes("UTF-8")));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    /**
     * RSA私钥解密
     *
     * @param str        加密字符串
     * @param privateKey 私钥
     * @return 铭文
     * 解密失败返回DES解密明文
     */
    public static String decrypt(String str, String privateKey) {
        if (StringUtils.isEmpty(str)) return StringUtils.EMPTY;
        if (StringUtils.isEmpty(privateKey)) return StringUtils.EMPTY;

        //64位解码加密后的字符串
        byte[] inputByte = new byte[0];
        try {
            inputByte = Base64.decodeBase64(str.getBytes("UTF-8"));
            //base64编码的私钥
            byte[] decoded = Base64.decodeBase64(privateKey);
            RSAPrivateKey priKey = (RSAPrivateKey) KeyFactory.getInstance("RSA").generatePrivate(new PKCS8EncodedKeySpec(decoded));
            //RSA解密
            Cipher cipher = Cipher.getInstance("RSA");
            cipher.init(Cipher.DECRYPT_MODE, priKey);
            return new String(cipher.doFinal(inputByte));
        } catch (Exception e) {
            e.printStackTrace();
        }
        return StringUtils.EMPTY;
    }

    /**
     * 随机生成密钥对
     */
    public static void genKeyPair(String filePath) throws NoSuchAlgorithmException {
        // KeyPairGenerator类用于生成公钥和私钥对，基于RSA算法生成对象
        KeyPairGenerator keyPairGen = KeyPairGenerator.getInstance("RSA");
        // 初始化密钥对生成器，密钥大小为96-1024位
        keyPairGen.initialize(1024, new SecureRandom());
        // 生成一个密钥对，保存在keyPair中
        KeyPair keyPair = keyPairGen.generateKeyPair();
        RSAPrivateKey privateKey = (RSAPrivateKey) keyPair.getPrivate(); // 得到私钥
        RSAPublicKey publicKey = (RSAPublicKey) keyPair.getPublic();  // 得到公钥
        try {
            String publicKeyString = new String(Base64.encodeBase64(publicKey.getEncoded()));// 得到公钥字符串
            String privateKeyString = new String(Base64.encodeBase64((privateKey.getEncoded()))); // 得到私钥字符串
            // 将密钥对写入到文件
            FileWriter pubfw = new FileWriter(filePath + "/publicKey.keystore");
            FileWriter prifw = new FileWriter(filePath + "/privateKey.keystore");
            BufferedWriter pubbw = new BufferedWriter(pubfw);
            BufferedWriter pribw = new BufferedWriter(prifw);
            pubbw.write(publicKeyString);
            pribw.write(privateKeyString);
            pubbw.flush();
            pubbw.close();
            pubfw.close();
            pribw.flush();
            pribw.close();
            prifw.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 从文件中获取公钥/私钥
     */
    public static String loadPublicKeyByFile(String path, String fileName) throws Exception {
        try {
            BufferedReader br = new BufferedReader(new FileReader(path + fileName));
            String readLine = null;
            StringBuilder sb = new StringBuilder();
            while ((readLine = br.readLine()) != null) {
                sb.append(readLine);
            }
            br.close();
            return sb.toString();
        } catch (IOException e) {
            throw new Exception("数据流读取错误");
        } catch (NullPointerException e) {
            throw new Exception("输入流为空");
        }
    }

}
