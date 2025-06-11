package com.iyuba.talkshow.util;

import java.text.DecimalFormat;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by Administrator on 2016/12/7 0007.
 */

public class NumberUtil {
    public static String keepOneDecimal(float decemal) {
        DecimalFormat decimalFormat = new DecimalFormat("##0.0");//构造方法的字符格式这里如果小数不足1位,会以0补足.
        return decimalFormat.format(decemal);//format 返回的是字符串
    }

    public static boolean isNumeric(String str){
        Pattern pattern = Pattern.compile("[0-9]*");
        Matcher isNum = pattern.matcher(str);
        return isNum.matches();
    }

}
