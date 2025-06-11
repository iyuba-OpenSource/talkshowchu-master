package com.iyuba.talkshow.ui.vip.buyiyubi;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.ui.vip.payorder.PayOrderActivity;


public class BuyIyubiActivity extends Activity implements View.OnClickListener {
    private ImageView backButton;
    private TextView textView;
    private ImageView iv_buy1;
    private ImageView iv_buy2;
    private ImageView iv_buy3;
    private ImageView iv_buy4;
    private ImageView iv_buy5;

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,BuyIyubiActivity.class);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        // TODO Auto-generated method stub
        super.onCreate(savedInstanceState);
//		requestWindowFeature(Window.FEATURE_PROGRESS);
        setContentView(R.layout.activity_buy_iyubi);
        setProgressBarVisibility(true);
        backButton = findViewById(R.id.lib_button_back);
        textView = findViewById(R.id.web_buyiyubi_title);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                onBackPressed();
            }
        });
        initView();
    }

    private void initView() {
        iv_buy1 = findViewById(R.id.iv_buy1);
        iv_buy2 = findViewById(R.id.iv_buy2);
        iv_buy3 = findViewById(R.id.iv_buy3);
        iv_buy4 = findViewById(R.id.iv_buy4);
        iv_buy5 = findViewById(R.id.iv_buy5);

        iv_buy1.setOnClickListener(this);
        iv_buy2.setOnClickListener(this);
        iv_buy3.setOnClickListener(this);
        iv_buy4.setOnClickListener(this);
        iv_buy5.setOnClickListener(this);
    }

    @Override
    public void onClick(View view) {
        int amount = 0;
        String price = "";
        String body = "";
        if (view == iv_buy1) {
            price = "19.9";
            amount = 210;
            body = "购买210爱语币";
        } else if (view == iv_buy2) {
            price = "59.9";
            amount = 650;
            body = "购买650爱语币";
        } else if (view == iv_buy3) {
            price = "99.9";
            amount = 1100;
            body = "购买1100爱语币";
        } else if (view == iv_buy4) {
            price = "599";
            amount = 6600;
            body = "购买6600爱语币";
        } else if (view == iv_buy5) {
            price = "999";
            amount = 12000;
            body = "购买12000爱语币";
        }
        String subject = "爱语币";
        startActivity(PayOrderActivity.buildIntent(BuyIyubiActivity.this, body, price, subject, body, amount, 1,PayOrderActivity.Order_iyubi));
    }

}
