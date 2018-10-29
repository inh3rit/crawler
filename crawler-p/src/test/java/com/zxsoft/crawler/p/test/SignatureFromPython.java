package com.zxsoft.crawler.p.test;

import java.math.BigInteger;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by cox on 2016/4/26.
 */
public class SignatureFromPython {

    private static final Pattern PAT = Pattern.compile("\\[(\\d+)\\]");

    private static byte[] i2osp(final BigInteger i, final int bitSize) {
        if (i == null || i.signum() == -1) {
            throw new IllegalArgumentException(
                    "input parameter should not be null or negative");
        }

        if (bitSize < Byte.SIZE) {
            throw new IllegalArgumentException(
                    "bitSize parameter should not be negative and a multiple of 8");
        }

        final int byteSize = (bitSize - 1) / Byte.SIZE + 1;
        final byte[] signedBigEndian = i.toByteArray();
        final int signedBigEndianLength = signedBigEndian.length;
        if (signedBigEndianLength == byteSize) {
            return signedBigEndian;
        }

        final byte[] leftPadded = new byte[byteSize];

        if (signedBigEndianLength == byteSize + 1) {
            System.arraycopy(signedBigEndian, 1, leftPadded, 0, byteSize);
        } else if (signedBigEndianLength < byteSize) {
            System.arraycopy(signedBigEndian, 0, leftPadded, byteSize
                    - signedBigEndianLength, signedBigEndianLength);
        } else {
            throw new IllegalArgumentException(
                    "Integer i is too large to fit into " + bitSize + " bits");
        }
        return leftPadded;
    }

    public static String toHex(final byte[] data) {
        final StringBuilder hex = new StringBuilder(data.length * 2);
        for (int i = 0; i < data.length; i++) {
            hex.append(String.format("%02X", data[i]));
        }
        return hex.toString();
    }

    public static byte[] getPubkey() {
        String sigString = "[169973496567562136053738556643262051129147221930730171567546808318669800630777271562082277302029412563050680222092830740030881574030505620421311453125665387837661916476634209138865888121927094060213236093982956747502778584174304176532350540445380400801515356006921100844017729664961212377046804242107487483079]";
        Matcher sigMatcher = PAT.matcher(sigString);
        if (!sigMatcher.matches()) {
            throw new IllegalArgumentException("Whatever");
        }
        BigInteger sigBI = new BigInteger(sigMatcher.group(1));
        // requires bouncy castle libraries
        return i2osp(sigBI, 1024);
    }
}
