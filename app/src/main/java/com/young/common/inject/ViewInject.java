package com.young.common.inject;

import android.app.Activity;

import java.lang.reflect.Field;

/**
 * Author: landerlyoung
 * Date:   2014-10-21
 * Time:   10:37
 * Life with passion. Code with creativity!
 */
public class ViewInject {
    public static void doInject(Activity o) {
        Field[] fields = o.getClass().getDeclaredFields();
        for (Field f : fields) {
            Inject iv = f.getAnnotation(Inject.class);
            if (iv != null) {
                try {
                    f.setAccessible(true);
                    f.set(o, o.findViewById(iv.value()));
                } catch (IllegalAccessException | IllegalArgumentException e) {
                    throw new IllegalArgumentException(String.format("Cannot doInject. nested exception is %s.", e.getMessage()), e);
                }
            }
        }
    }
}
