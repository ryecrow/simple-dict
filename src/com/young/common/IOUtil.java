package com.young.common;

import java.io.Closeable;
import java.io.IOException;

/**
 * Author: taylorcyang
 * Date:   2014-10-29
 * Time:   9:59
 * Life with passion. Code with creativity!
 */
public class IOUtil {
    public static boolean closeSilently(Closeable c) {
        try {
            c.close();
            return true;
        } catch (IOException e) {
            return false;
        }
    }
}
