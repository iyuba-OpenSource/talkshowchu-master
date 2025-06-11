package com.iyuba.talkshow.ui.vip.buyvip;

public class PriceUtils {
    public static double getSpend(int month) {
        double result = 199;
        switch (month) {
            case 1:
                result = 50;
                break;
            case 3:
                result = 88;
                break;
            case 6:
                result = 198;
                break;
            case 12:
                result = 298;
                break;
            case 36:
                result = 588;
                break;
        }
        return result;
    }
    public static double getSpendVip(int month) {
        double result = 98;
        switch (month) {
            case 1:
                result = 98;
                break;
            case 3:
                result = 288;
                break;
            case 6:
                result = 518;
                break;
            case 12:
                result = 998;
                break;
        }
        return result;
    }

    public static double getSpendBenyingyong(int month) {
        switch (month){
            case 1:
                return 30 ;
            case 6:
                return 69 ;
            case 12:
                return 99 ;
            case 36:
                return 199 ;
            case 60:
                return 299 ;
            default:
                break;
        }
        return 100;
    }
}
