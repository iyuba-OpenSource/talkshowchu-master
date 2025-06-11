package com.iyuba.talkshow.data.model;

import java.util.Arrays;
import java.util.List;

/**
 * Created by Administrator on 2016/11/16 0016.
 */

public class Level extends BasicNameValue {

    private Level(String name, String value) {
        super(name, value);
    }

    private final static List<Level> levels = Arrays.asList(
            new Level(Name.ALL, Value.ALL),
            new Level(Name.EASY, Value.EASY),
            new Level(Name.NORMAL, Value.NORMAL),
            new Level(Name.DIFFICULT, Value.DIFFICULT)
    );

    public static List<Level> getLevels() {
        return levels;
    }

    public static String[] getIdArray(String id) {
        return id != null ? id.split(Value.SEP) : null;
    }

    public interface Value {
        String SEP = ",";
        String ALL = "1" + SEP + "2" + SEP + "3" + SEP + "4" + SEP + "5";
        String EASY = "1" + SEP + "2";
        String NORMAL = "3" + SEP + "4";
        String DIFFICULT = "5";
    }

    interface Name {
        String ALL = "全部";
        String EASY = "容易";
        String NORMAL = "普通";
        String DIFFICULT = "困难";
    }
}
