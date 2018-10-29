package com.zxsoft.crawler.p.test;

import com.jfinal.kit.StrKit;
import name.iaceob.kit.disgest.Base64;
import name.iaceob.kit.disgest.Disgest;
import org.inh3rit.httphelper.entity.HttpEntity;
import org.inh3rit.httphelper.http.HttpStatus;
import org.inh3rit.httphelper.kit.HttpKit;
import org.junit.Test;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.nio.charset.Charset;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cox on 2016/4/26.
 */
public class testQQ {

    private static final Logger log = LoggerFactory.getLogger(testQQ.class);

    private String appid = "636014201";
    private String action = "2-0-1456213685600";
    private String urlRaw = "http://ui.ptlogin2.qq.com/cgi-bin/login";
    private String urlCheck = "http://check.ptlogin2.qq.com/check";
    private String urlLogin = "http://ptlogin2.qq.com/login";
    private String urlSuccess = "http://www.qq.com/qq2012/loginSuccess.htm";

    final protected static char[] hexArray = "0123456789abcdef".toCharArray();

    private String parseCookie(String ck) {
        if (StrKit.isBlank(ck))
            throw new RuntimeException("Cookie can not be null");
        return ck.split(";")[0].split("=")[1];
    }

    private List<String> extraSalt(String text) {
        Pattern p = Pattern.compile("'(.*?)'");
        Matcher m = p.matcher(text);
        List<String> res = new ArrayList<>();
        while (m.find()) {
            res.add(m.group(1));
        }
        return res;
    }
    /**
     * 获取盐种子
     * @return
     */
    private String getSign() {
        Map<String, String> paras = new HashMap<>();
        paras.put("no_verifyimg", "1");
        paras.put("appid", this.appid);
        paras.put("s_url", this.urlSuccess);

        HttpEntity he = HttpKit.get(this.urlRaw, paras, Charset.forName("UTF-8"));
        if (he.getResponseCode()!= HttpStatus.SC_OK)
            throw new RuntimeException("get sign fail");
        List<String> cks = he.getHeaders("Set-Cookie");
        for (String val : cks) {
            // if (!"pt_login_sig".equals(key)) continue;
            if (!val.contains("pt_login_sig")) continue;
            return this.parseCookie(val);
        }
        throw new RuntimeException("can not get sign");
    }


    /**
     * 检查登录 获取加密盐
     * @param uin
     * @param sign
     * @return
     */
    private List<String> checkLogin(String uin, String sign) {
        Map<String, String> paras = new HashMap<>();
        paras.put("uin", uin);
        paras.put("appid", this.appid);
        paras.put("pt_tea", "1");
        paras.put("pt_vcode", "1");
        paras.put("js_ver", "10151");
        paras.put("js_type", "1");
        paras.put("login_sig", sign);
        paras.put("u1", this.urlSuccess);
        HttpEntity he = HttpKit.get(this.urlCheck, paras, Charset.forName("UTF-8"));
        if (he.getResponseCode() != HttpStatus.SC_OK)
            throw new RuntimeException("check login fail");
        return this.extraSalt(he.getHtml());
    }


    private void login(String pwd, String salt, String verifyCode) {
        try {
            this.encodePwd(pwd, salt, verifyCode);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }


    private void encodePwd(String pwd, String salt, String verifyCode) throws Exception {
        salt = salt.replaceAll("\\\\x", "");
        salt = "0000000009d161cd";
        pwd = "wjallen1991...";
        verifyCode = "!PWL";
        byte[] e = this.hexStringToByteArray(salt);

        String md5Pwd = Disgest.encodeMD5(pwd).toUpperCase();

        byte[] r = this.md5Digest(pwd.getBytes());
        String p = this.bytesToHex(this.md5Digest(this.mergeByte(r, e))).toUpperCase();
        String pubKey = "169973496567562136053738556643262051129147221930730171567546808318669800630777271562082277302029412563050680222092830740030881574030505620421311453125665387837661916476634209138865888121927094060213236093982956747502778584174304176532350540445380400801515356006921100844017729664961212377046804242107487483079";
        // RSACryptoServiceProvider rsaToEncrypt = new RSACryptoServiceProvider();
        log.debug(RSACoder.getPublicKey());
        String a = this.bytesToHex(RSAUtil.encryptRSA(RSAUtil.getPublicKey(pubKey, "3"), r));
        String s = Integer.toHexString(a.length()/2);
        s = this.zfill(s, 4);
        String l = this.bytesToHex(verifyCode.getBytes());
        String c = Integer.toHexString(l.length()/2);
        c = this.zfill(c, 4);
        String newPwd = s + a + salt + c + l;
        Tea.encry(this.hexStringToByteArray(newPwd), this.hexStringToByteArray(p));

        log.debug(p);
    }

    private String zfill(String val, Integer length) {
        if (StrKit.isBlank(val))
            throw new IllegalArgumentException("val can not be null");
        if (val.length()>length)
            return val.substring(val.length()-length, val.length());
        Integer interval = length-val.length();
        StringBuilder sb = new StringBuilder();
        for (Integer i=interval; i-->0;)
            sb.append(0);
        sb.append(val);
        return sb.toString();
    }

    private byte[] mergeByte(byte[] a, byte[] b) {
        byte[] r = new byte[a.length + b.length];
        for (int i=a.length; i-->0;)
            r[i] = a[i];
        Integer index = a.length;
        for (byte aB : b) {
            r[index] = aB;
            index += 1;
        }
        return r;
    }

    public String bytesToHex(byte[] bytes) {
        char[] hexChars = new char[bytes.length * 2];
        for ( int j = 0; j < bytes.length; j++ ) {
            int v = bytes[j] & 0xFF;
            hexChars[j * 2] = hexArray[v >>> 4];
            hexChars[j * 2 + 1] = hexArray[v & 0x0F];
        }
        return new String(hexChars);
    }

    private byte[] md5Digest(byte[] val) {
        try {
            MessageDigest m = MessageDigest.getInstance("MD5");
            m.update(val);
            return m.digest();
        } catch (NoSuchAlgorithmException e) {
            throw new RuntimeException(e);
        }
    }

    public byte[] hexStringToByteArray(String s) {
        int len = s.length();
        byte[] data = new byte[len / 2];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4)
                    + Character.digit(s.charAt(i+1), 16));
        }
        return data;
    }

    @Test
    public void test() {
        String uin = "3115565062";
        String passwd = "passwd";
        String sign = this.getSign();
        // log.debug(sign);
        List<String> salts = this.checkLogin(uin, sign);
        String checkCode = salts.get(0),
                verifyCode = salts.get(1),
                salt = salts.get(2),
                ptVerifysessionV1 = salts.get(3);
        if (!"0".equals(checkCode))
            throw new RuntimeException("salt fail");

        this.login(passwd, salt, verifyCode);
    }

    @Test
    public void test2() throws Exception {
        String pubkey1 = "F20CE00BAE5361F8FA3AE9CEFA495362FF7DA1BA628F64A347F0A8C012BF0B254A30CD92ABFFE7A6EE0DC424CB6166F8819EFA5BCCB20EDFB4AD02E412CCF579B1CA711D55B8B0B3AEB60153D5E0693A2A86F3167D7847A0CB8B00004716A9095D9BADC977CBB804DBDCBA6029A9710869A453F27DFDDF83C016D928B3CBF4C7";
        String pubkey2 = "169973496567562136053738556643262051129147221930730171567546808318669800630777271562082277302029412563050680222092830740030881574030505620421311453125665387837661916476634209138865888121927094060213236093982956747502778584174304176532350540445380400801515356006921100844017729664961212377046804242107487483079";
        String res = RSACoder.bytesToHexStr(pubkey1.getBytes());
        log.debug(res);
        log.debug(this.bytesToHex(RSACoder.hexStrToBytes(pubkey2)));


        RSAUtil.getPublicKey(pubkey2, "3");

    }

}
