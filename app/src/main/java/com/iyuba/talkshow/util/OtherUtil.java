package com.iyuba.talkshow.util;

import android.os.Build;

public class OtherUtil {

    /*******************手机品牌判断*****************/
    //是否是oppo旗下的手机
    public static boolean isBelongToOppoPhone(){
        String brand = Build.BRAND.toLowerCase();
        switch (brand){
            case "oppo"://oppo
            case "oneplus"://一加
                return true;
        }
        return false;
    }
}
