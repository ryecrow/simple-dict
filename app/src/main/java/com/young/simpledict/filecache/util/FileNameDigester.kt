package com.young.simpledict.filecache.util

import java.security.MessageDigest
import java.security.NoSuchAlgorithmException
import java.util.*

/**
 * Author: landerlyang
 * Date:   2014-10-28
 * Time:   20:36
 * Life with passion. Code with creativity!
 */
object FileNameDigester {

    fun digest(url: String?): String {
        if (url != null) {
            try {
                // Create MD5 Hash
                val digest = MessageDigest.getInstance("MD5")
                digest.update(url.toByteArray())
                return toHex(digest.digest())
            } catch (e: NoSuchAlgorithmException) {
                e.printStackTrace()
            }
        }
        return ""
    }

    private fun toHex(raw: ByteArray): String {
        val f = Formatter(StringBuilder(raw.size * 2), Locale.US)
        for (b in raw) {
            f.format("%02x", (b.toInt() and 0xff).toByte())
        }
        val ret = f.toString()
        f.close()
        return ret
    }
}
