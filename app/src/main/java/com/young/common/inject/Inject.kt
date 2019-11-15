package com.young.common.inject

/**
 * Author: landerlyang
 * Date:   2014-10-21
 * Time:   10:39
 * Life with passion. Code with creativity!
 */
@Retention(AnnotationRetention.RUNTIME)
@Target(AnnotationTarget.FIELD)
annotation class Inject(val value: Int)
