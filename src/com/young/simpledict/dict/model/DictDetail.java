package com.young.simpledict.dict.model;

import java.util.ArrayList;

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   15:20
 * Life with passion. Code with creativity!
 */
public class DictDetail {
    public String word;
    public String pinyin;
    public String usphonetic;
    public String usspeech;
    public String ukphonetic;
    public String ukspeech;

    public Wiki wiki;

    public ArrayList<DictExplain> explains = new ArrayList<DictExplain>();
    public ArrayList<AudioSentence> audioSentences = new ArrayList<AudioSentence>();
    public ArrayList<TranslateSentence> translateSentences = new ArrayList<TranslateSentence>();

}
