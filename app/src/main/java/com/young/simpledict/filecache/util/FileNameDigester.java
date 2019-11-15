package com.young.simpledict.filecache.util;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Formatter;
import java.util.Locale;

/**
 * Author: taylorcyang
 * Date:   2014-10-28
 * Time:   20:36
 * Life with passion. Code with creativity!
 */
public class FileNameDigester {

    public static String digest(String url) {
        if (url != null) {

            final String MD5 = "MD5";
            try {
                // Create MD5 Hash
                MessageDigest digest = MessageDigest.getInstance(MD5);
                digest.update(url.getBytes());
                return toHex(digest.digest());
            } catch (NoSuchAlgorithmException e) {
                e.printStackTrace();
            }
        }
        return "";
    }

    public static String toHex(byte[] raw) {
        Formatter f = new Formatter(new StringBuilder(raw.length * 2), Locale.US);
        for (Byte b : raw) {
            f.format("%02x", b & 0xff);
        }
        String ret = f.toString();
        f.close();
        return ret;
    }
}
