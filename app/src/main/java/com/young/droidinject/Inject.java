package com.young.droidinject;

import android.app.Activity;

import java.lang.reflect.Field;

/**
 * Author: landerlyoung
 * Date:   2014-10-21
 * Time:   10:37
 * Life with passion. Code with creativity!
 */
public class Inject {
    public static void inject(Activity o) {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field f : fields) {
            InjectView iv = f.getAnnotation(InjectView.class);
            if (iv != null) {
                try {
                    f.setAccessible(true);
                    f.set(o, o.findViewById(iv.value()));
                } catch (IllegalAccessException e) {
                    throw new RuntimeException("inject failed", e);
                } catch (IllegalArgumentException ex) {
                    throw new IllegalArgumentException("Type of View in xml and declared class"
                            + " is not compatible!");
                }
            }
        }
    }
}
