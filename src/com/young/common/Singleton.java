package com.young.common;

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   15:58
 * Life with passion. Code with creativity!
 */
public abstract class Singleton<T> {
    private T mInstance;

    public abstract T create();

    public T get() {
        if(mInstance == null) {
            synchronized (this) {
                if (mInstance == null) {
                    mInstance = create();
                }
            }
        }
        return mInstance;
    }
}
