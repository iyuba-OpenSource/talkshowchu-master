package com.iyuba.talkshow.newce.search.util;

import com.iyuba.module.commonvar.CommonVars;

public class SearchUtil {

    //拼接图片
    public static String fixUrl(String suffix){
        String header = "http://static2." + CommonVars.domain + "/images/words";

        if (suffix.endsWith("/")){
            return header+suffix;
        }

        return header+"/"+suffix;
    }
}
