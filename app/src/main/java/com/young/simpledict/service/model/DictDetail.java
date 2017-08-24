package com.young.simpledict.service.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: landerlyoung
 * Date:   2014-10-21
 * Time:   15:20
 * Life with passion. Code with creativity!
 */
public class DictDetail {
    public String word;
    public String usphontic;
    public String usspeech;
    public String ukphontic;
    public String ukspeech;
    public String pinyin;

    public List<String> trs = new ArrayList<String>();
    public List<WF> wfs = new ArrayList<WF>();

    public static class WF {
        public WF(String name, String value) {
            this.name = name;
            this.value = value;
        }

        public String name;
        public String value;
    }
}
