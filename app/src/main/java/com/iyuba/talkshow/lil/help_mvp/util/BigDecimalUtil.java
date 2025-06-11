package com.iyuba.talkshow.lil.help_mvp.util;

import java.math.BigDecimal;
import java.text.DecimalFormat;

/**
 * @desction:
 * @date: 2023/3/2 15:48
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class BigDecimalUtil {

    public static double trans2Double(String doubleStr){
        BigDecimal bigDecimal = new BigDecimal(doubleStr);
        bigDecimal = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);
        return bigDecimal.doubleValue();
    }

    public static double trans2Double(double doubleStr){
        BigDecimal bigDecimal = new BigDecimal(doubleStr);
        bigDecimal = bigDecimal.setScale(2,BigDecimal.ROUND_HALF_UP);
        return bigDecimal.doubleValue();
    }

    public static double trans2Double(int scale,double doubleStr){
        BigDecimal bigDecimal = new BigDecimal(doubleStr);
        bigDecimal = bigDecimal.setScale(scale,BigDecimal.ROUND_DOWN);
        return bigDecimal.doubleValue();
    }
}
