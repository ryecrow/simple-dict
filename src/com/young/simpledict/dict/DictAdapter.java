package com.young.simpledict.dict;

import com.young.simpledict.service.model.DictDetail;

/**
 * Author: taylorcyang
 * Date:   2014-10-21
 * Time:   19:00
 * Life with passion. Code with creativity!
 */
public interface DictAdapter {
    public String getSearchUri(String mWordToSearch);
    public DictDetail parseDict(String response);
}
