package com.young.common

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   15:58
 * Life with passion. Code with creativity!
 */
abstract class Singleton<T> {

    @Volatile
    private var mInstance: T? = null

    abstract fun init(): T

    fun get(): T {
        val checkedInstance = mInstance
        if (checkedInstance != null) {
            return checkedInstance
        }
        return synchronized(this) {
            val checkInstanceAgain = mInstance
            if (checkInstanceAgain != null) {
                checkInstanceAgain
            } else {
                val created = init()
                mInstance = created
                created
            }
        }
    }
}
