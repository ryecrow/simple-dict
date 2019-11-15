package com.young.simpledict.dict.model

import com.young.common.NameValuePair

/**
 * Author: taylorcyang
 * Date:   2014-10-22
 * Time:   16:05
 * Life with passion. Code with creativity!
 */
data class DictExplain(
    var dictName: String,
    var trs: ArrayList<String>,
    var wfs: ArrayList<NameValuePair>
) {
    constructor(dictName: String) : this(dictName, ArrayList(), ArrayList())
}