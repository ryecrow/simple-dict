package com.young.simpledict.dict;

import com.young.simpledict.dict.model.DictDetail;

/**
 * Author: landerlyoung
 * Date:   2014-10-21
 * Time:   19:00
 * Life with passion. Code with creativity!
 */
public interface DictAdapter {
    public static final int DICT_YOUDAO_BRIEF = 0;
    public static final int DICT_YOUDAO_DETAIL = 1;

    public String getSearchUri(String mWordToSearch);

    public DictDetail parseDict(String response);
}
