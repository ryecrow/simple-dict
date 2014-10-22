package com.young.simpledict.dict.model;

import java.util.ArrayList;
import java.util.List;

/**
 * Author: taylorcyang
 * Date:   2014-10-22
 * Time:   16:05
 * Life with passion. Code with creativity!
 */
public class DictExplain {
    public String dictName;
    //词性
    public List<String> trs = new ArrayList<String>();
    //变换
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
