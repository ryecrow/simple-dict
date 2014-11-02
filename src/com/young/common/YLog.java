package com.young.common;

import android.util.Log;

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   19:20
 * Life with passion. Code with creativity!
 */
public class YLog {
    public static void v(String tag, String msg) {
        Log.v(tag, msg);
    }

    public static void d(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static void i(String tag, String msg) {
        Log.i(tag, msg);
    }

    public static void w(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void e(String tag, String msg) {
        Log.w(tag, msg);
    }

    public static void wtf(String tag, String msg) {
        Log.wtf(tag, msg);
    }

    public static void v(String tag, String msg, Throwable t) {
        Log.v(tag, msg, t);
    }

    public static void d(String tag, String msg, Throwable t) {
        Log.d(tag, msg, t);
    }

    public static void i(String tag, String msg, Throwable t) {
        Log.e(tag, msg, t);
    }

    public static void w(String tag, String msg, Throwable t) {
        Log.w(tag, msg, t);
    }

    public static void e(String tag, String msg, Throwable t) {
        Log.e(tag, msg, t);
    }

    public static void wtf(String tag, String msg, Throwable t) {
        Log.wtf(tag, msg, t);
    }
}
