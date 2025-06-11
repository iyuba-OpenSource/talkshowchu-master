package com.iyuba.talkshow.data.model;

import java.util.List;

public class QQResponse {
    public int result;
    public List<QQData> data;

    public static class QQData {
        public String editor;
        public String technician;
        public String manager;
    }

}
