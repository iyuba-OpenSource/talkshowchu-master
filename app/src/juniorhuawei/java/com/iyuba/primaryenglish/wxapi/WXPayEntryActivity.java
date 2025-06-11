package com.iyuba.primaryenglish.wxapi;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.manager.AccountManager;
import com.iyuba.talkshow.data.model.User;
import com.iyuba.talkshow.event.WXPayResultEvent;
import com.tencent.mm.opensdk.constants.ConstantsAPI;
import com.tencent.mm.opensdk.modelbase.BaseReq;
import com.tencent.mm.opensdk.modelbase.BaseResp;
import com.tencent.mm.opensdk.openapi.IWXAPI;
import com.tencent.mm.opensdk.openapi.IWXAPIEventHandler;
import com.tencent.mm.opensdk.openapi.WXAPIFactory;

import org.greenrobot.eventbus.EventBus;

import javax.inject.Inject;

public class WXPayEntryActivity extends Activity implements IWXAPIEventHandler {

    private static final String TAG = "WXPayEntryActivity";

    private IWXAPI api;

    @Inject
    AccountManager mAccountManager;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.pay_result);
        api = WXAPIFactory.createWXAPI(this, App.WX_KEY);
        api.handleIntent(getIntent(), this);
    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        setIntent(intent);
        api.handleIntent(intent, this);
    }

    @Override
    public void onReq(BaseReq req) {
    }

    @Override
    public void onResp(BaseResp resp) {
        if (resp.getType() == ConstantsAPI.COMMAND_PAY_BY_WX) {
//            if (resp.errCode == 0) {
//                User user = mAccountManager.getUser();
//                mAccountManager.login(user.getUsername(),
//                        user.getPassword(), 0, 0, null);
//                Toast.makeText(this, "支付成功", Toast.LENGTH_SHORT).show();
//            } else {
//                Log.d("com.iyuba.talkshow", resp.errCode+":"+resp.errStr);
//                Toast.makeText(this, "支付失败", Toast.LENGTH_SHORT).show();
//            }
            EventBus.getDefault().post(new WXPayResultEvent(resp.errCode));
            finish();
        }
    }
}