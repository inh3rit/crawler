package com.zxsoft.crawler.p.test;

import name.iaceob.kit.disgest.Base64;

import java.security.*;
import java.security.interfaces.RSAPrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;

import java.util.HashMap;
import java.util.Map;

import javax.crypto.Cipher;

/**
 * RSA安全编码组件
 *
 * @author 梁栋
 * @version 1.0
 * @since 1.0
 */
public class RSACoder {

    private static final String RSA_PUBLICE =
            "MIGfMA0GCSqGSIb3DQEBAQUAA4GNADCBiQKBgQDSipVLWBnTtIaLpSkvtmQTJNFl\n" +
                    "INLKNmLufvgp9JIpGgFOYU6f1231QFDhbK2dEykNLwaDmCOVHLpR5fm0b/qhZeJ0\n" +
                    "zWGgQ7hOyTISiwUM5PGqwAXlfJRS/ZQXe72l7/+ijBEdTZ3U/5okDQA5QTAVWo9Q\n" +
                    "JGqKj1IVL0Hs9JwK8wIDAQAB";
    private static final String ALGORITHM = "RSA";

    /**
     * 得到公钥
     *
     * @param algorithm
     * @param bysKey
     * @return
     */
    private static PublicKey getPublicKeyFromX509(String algorithm,
                                                  String bysKey) throws Exception {
        byte[] decodedKey = Base64.decode(bysKey);
        X509EncodedKeySpec x509 = new X509EncodedKeySpec(decodedKey);
        KeyFactory keyFactory = KeyFactory.getInstance(algorithm);
        return keyFactory.generatePublic(x509);
    }

    public static PublicKey getKey() throws Exception {
        return getPublicKeyFromX509(ALGORITHM, RSA_PUBLICE);
    }

    public static String getPublicKey() {
        try {
            PublicKey pubkey = getPublicKeyFromX509(ALGORITHM, RSA_PUBLICE);
            java.security.interfaces.RSAPublicKey pk = (java.security.interfaces.RSAPublicKey) pubkey;
            return pk.getModulus().toString(16);
        } catch (Exception e) {
            return null;
        }
    }

    private static final char[] bcdLookup = {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', 'a', 'b', 'c', 'd', 'e', 'f'};

    /**
     * 将字节数组转换为16进制字符串的形式.
     */
    public static String bytesToHexStr(byte[] bcd) {
        StringBuffer s = new StringBuffer(bcd.length * 2);
        for (int i = 0; i < bcd.length; i++) {
            s.append(bcdLookup[(bcd[i] >>> 4) & 0x0f]);
            s.append(bcdLookup[bcd[i] & 0x0f]);
        }
        return s.toString();
    }

    /**
     * 将16进制字符串还原为字节数组.
     */
    public static final byte[] hexStrToBytes(String s) {
        byte[] bytes;
        bytes = new byte[s.length() / 2];
        for (int i = 0; i < bytes.length; i++) {
            bytes[i] = (byte) Integer.parseInt(s.substring(2 * i, 2 * i + 2), 16);
        }
        return bytes;
    }

}