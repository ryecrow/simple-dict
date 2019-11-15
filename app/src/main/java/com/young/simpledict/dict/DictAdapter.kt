package com.young.simpledict.dict

import com.young.simpledict.dict.model.DictDetail

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   19:00
 * Life with passion. Code with creativity!
 */
interface DictAdapter {

    fun getSearchUri(mWordToSearch: String): String

    fun parseDict(response: String): DictDetail

    companion object {
        const val DICT_YOUDAO_BRIEF = 0
        const val DICT_YOUDAO_DETAIL = 1
    }
}
