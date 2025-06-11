package com.iyuba.talkshow.ui.web;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.view.View;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.util.FileUtils;
import com.iyuba.talkshow.util.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import javax.inject.Inject;

/**
 * Created by carl shen on 2020/10/14
 * New Junior English, new study experience.
 */
public class SendBookActivity extends AppCompatActivity {

    @Inject
    ConfigManager configManager;
    Toolbar button_back;
    TextView text;
    TextView commit;
    TextView cancel;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_sendbook);
        StatusBarUtil.setColor(this, getResources().getColor(com.iyuba.wordtest.R.color.colorPrimary), 0);
        button_back = findViewById(R.id.button_back);
        text = findViewById(R.id.text);
        commit = findViewById(R.id.commit);
        cancel = findViewById(R.id.cancel);
        SpannableString spanText=new SpannableString("\u3000\u3000好评免费领奥数视频课程\n\u3000\u3000小学1-6年级，初中7-9年级的奥数视频课程，每年级有50-100节名师奥数视频课，在应用商店对本app好评后截图发给QQ2111356785，可免费领任意两年级的课程。\n\u3000\u3000视频课文件较大，都是通过【百度网盘链接】发送，如果你不是百度网盘会员，可能会超出文件数量和大小限制，请用电脑分批转存即可解决此问题。");
        spanText.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);       //设置文件颜色
                ds.setUnderlineText(true);      //设置下划线
            }

            @Override
            public void onClick(View view) {
                if(FileUtils.isQQClientAvailable(SendBookActivity.this)) {
                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=";
                    startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url + "3099007489")));
                } else {
                    ToastUtil.showToast(SendBookActivity.this,"未安装qq客户端");
                }
            }
        }, 39, 49, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
        text.setText(spanText);
        text.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //去评价后标记，再也不弹出好评送书弹框
                    configManager.setSendBook(true);
                    Uri uri = Uri.parse("market://details?id=" + getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    startActivity(intent);
                } catch (Exception e) {
                    AlertDialog dialog = new AlertDialog.Builder(SendBookActivity.this).create();
                    dialog.setIcon(android.R.drawable.ic_dialog_alert);
                    dialog.setTitle(getResources().getString(R.string.alert_title));
                    dialog.setMessage(getResources().getString(R.string.about_market_error));
                    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, getString(R.string.alert_btn_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    dialog.show();
                }
            }
        });

        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
             finish();
            }
        });

        button_back.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

}
