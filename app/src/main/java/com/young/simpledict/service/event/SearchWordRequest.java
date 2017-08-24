package com.young.simpledict.service.event;

/**
 * Author: landerlyoung
 * Date:   2014-10-21
 * Time:   15:20
 * Life with passion. Code with creativity!
 */
public class SearchWordRequest extends BaseEvent {
   public String word;
   /**
    * use which dict, line {@link com.young.simpledict.dict.DictAdapter#DICT_YOUDAO_BRIEF}
    */
   public int useDict;
}
