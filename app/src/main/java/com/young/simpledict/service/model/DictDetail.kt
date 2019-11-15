package com.young.simpledict.service.model

import com.young.common.NameValuePair

/**
 * Author: landerlyang
 * Date:   2014-10-21
 * Time:   15:20
 * Life with passion. Code with creativity!
 */
data class DictDetail(
    var word: String,
    var usphontic: String,
    var usspeech: String,
    var ukphontic: String,
    var ukspeech: String,
    var pinyin: String,
    var trs: List<String>,
    var wfs: List<NameValuePair>
)
