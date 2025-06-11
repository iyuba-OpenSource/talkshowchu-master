package com.iyuba.talkshow.lil.help_mvp.util.gson;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

/**
 * @desction: gson工具
 * @date: 2023/4/23 22:58
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class GsonUtil {
    public static Gson gson = new Gson();

    //格式化数据
    public static String formatJson(String json){
        JsonParser jsonParser = new JsonParser();
        JsonObject jsonObject = jsonParser.parse(json).getAsJsonObject();
        Gson gson = new GsonBuilder().setPrettyPrinting().create();
        return gson.toJson(jsonObject);
    }

    //将模型数据转为json形式
    public static <T> String toJson(T t){
        if (t==null){
            return null;
        }

        return gson.toJson(t);
    }

    //将json数据转成模型数据

    //将json数据转成集合模型数据
    public static <T> List<T> toList(String jsonStr,Class<T> clz){
        JsonArray jsonArray = JsonParser.parseString(jsonStr).getAsJsonArray();
        List<T> objList = new ArrayList<>();
        Iterator iterator = jsonArray.iterator();

        while (iterator.hasNext()){
            JsonElement element = (JsonElement) iterator.next();
            T obj = gson.fromJson(element,clz);
            objList.add(obj);
        }
        return objList;
    }
}
