package com.young.common.inject

import android.app.Activity

/**
 * Author: landerlyang
 * Date:   2014-10-21
 * Time:   10:37
 * Life with passion. Code with creativity!
 */
object ViewInject {
    fun doInject(o: Activity) {
        val fields = o.javaClass.declaredFields
        for (f in fields) {
            val iv = f.getAnnotation(Inject::class.java)
            if (iv != null) {
                try {
                    f.isAccessible = true
                    f.set(o, o.findViewById(iv.value))
                } catch (e: IllegalAccessException) {
                    throw IllegalArgumentException(
                        "Cannot doInject. nested exception is ${e.message}", e
                    )
                } catch (e: IllegalArgumentException) {
                    throw IllegalArgumentException(
                        "Cannot doInject. nested exception is ${e.message}", e
                    )
                }
            }
        }
    }
}
