package com.iyuba.talkshow.util;

import android.text.TextUtils;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.net.URLEncoder;
import java.util.HashMap;
import java.util.Iterator;

/**
 * Created by carl shen on 2020/12/2.
 */
public class ParameterUrl {
    public static String setRequestParameter(String url, HashMap<String, Object> para) {
        StringBuffer requestURLTemp = new StringBuffer(url);
        Iterator iterator = para.entrySet().iterator();
        HashMap.Entry entry;
        Object key, value;
        requestURLTemp.append("?");
        while (iterator.hasNext()) {
            entry = (HashMap.Entry) iterator.next();
            key = entry.getKey();
            value = entry.getValue();
            requestURLTemp.append(key).append("=").append(value).append("&");
        }
        return requestURLTemp.substring(0, requestURLTemp.length() - 1);
    }

    public static String setRequestParameter(String url, Object key, Object value) {
        StringBuffer requestURLTemp = new StringBuffer(url);
        requestURLTemp.append("?").append(key).append("=").append(value);
        return requestURLTemp.toString();
    }

    public static String encode(String content) {
        try {
            if (TextUtils.isEmpty(content)) {
                return content;
            } else {
                return URLEncoder.encode(content, "UTF-8");
            }
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }

    public static String decode(String content) {
        try {
            return URLDecoder.decode(content, "UTF-8");
        } catch (UnsupportedEncodingException e) {
            e.printStackTrace();
        }
        return null;
    }
}
