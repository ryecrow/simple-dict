package com.young.simpledict.dict.model

import java.util.*

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   15:20
 * Life with passion. Code with creativity!
 */
data class DictDetail(
    var word: String,
    var pinyin: String,
    var usphonetic: String,
    var usspeech: String,
    var ukphonetic: String,
    var ukspeech: String,

    var wiki: Wiki,

    var explains: ArrayList<DictExplain>,
    var audioSentences: ArrayList<AudioSentence>,
    var translateSentences: ArrayList<TranslateSentence>
) {
    constructor() : this(
        "", "", "", "", "", "",
        Wiki("", "", Collections.emptyList()),
        ArrayList(), ArrayList(), ArrayList()
    )
}
