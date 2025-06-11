package com.iyuba.talkshow.ui.vip.payorder.alipay;

import android.content.Context;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;
import java.util.Map;
import java.util.Random;

/**
 * Created by Administrator on 2016/12/20/020.
 */

public class AliPayUtil {
    /**
     * 支付宝 get the out_trade_no for an order. 生成商户订单号，该值在商户端应保持唯一（可自定义格式规范）
     */
    public static String getOutTradeNo() {
        SimpleDateFormat format = new SimpleDateFormat("MMddHHmmss", Locale.getDefault());
        Date date = new Date();
        String key = format.format(date);

        Random r = new Random();
        key = key + Math.abs(r.nextInt());
        key = key.substring(0, 15);
        return key;
    }

    public static String getPayInfo(String orderInfo, String sign) {
        return orderInfo + "&sign=\"" + sign + "\"&" + "sign_type=\"RSA\"";
    }

}
