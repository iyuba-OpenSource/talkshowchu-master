package com.iyuba.talkshow.util;

/**
 * Created by Administrator on 2016/11/21 0021.
 */

public class SqlUtil {
    public static String handleIn(String name, String[] values) {
        StringBuilder sb = new StringBuilder();
        for(int i = 0; i < values.length - 1; i++) {
            sb.append(name).append(" = ").append(values[i]).append(" OR ");
        }
        sb.append(name).append(" = ").append(values[values.length - 1]);
        return sb.toString();
    }
}
