package com.iyuba.talkshow.newview;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.net.Uri;
import android.text.Spannable;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.WindowManager;
import android.widget.PopupWindow;
import android.widget.TextView;

import com.iyuba.module.user.IyuUserManager;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.util.FileUtils;
import com.iyuba.talkshow.util.ToastUtil;

import javax.inject.Inject;

import personal.iyuba.personalhomelibrary.ui.message.ChattingActivity;
/**
 * Created by carl shen on 2020/10/14
 * New Junior English, new study experience.
 */
public class SendBookPop extends PopupWindow {
    @Inject
    ConfigManager configManager;
    private final TextView text;
    private final Activity context;
    private final View conentView;

    public SendBookPop(final Activity context, View root) {
        this.context = context;
        LayoutInflater inflater = (LayoutInflater) context
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        conentView = inflater.inflate(R.layout.good_dlalog, null);

        int h = context.getWindowManager().getDefaultDisplay().getHeight() / 100 * 32;
        int w = context.getWindowManager().getDefaultDisplay().getWidth() / 100 * 85;
        // 设置SelectPicPopupWindow的View
        this.setContentView(conentView);
        // 设置SelectPicPopupWindow弹出窗体的宽
        this.setWidth(w);
//        // 设置SelectPicPopupWindow弹出窗体的高
        this.setHeight(h);
        // 设置SelectPicPopupWindow弹出窗体可点击
        this.setFocusable(true);
        this.setOutsideTouchable(true);
        // 刷新状态
        this.update();
        // 点back键和其他地方使其消失,设置了这个才能触发OnDismisslistener ，设置其他控件变化等操作
        this.setBackgroundDrawable(context.getResources().getDrawable(R.drawable.send_book_pop_bg));
        // mPopupWindow.setAnimationStyle(android.R.style.Animation_Dialog);
        // 设置SelectPicPopupWindow弹出窗体动画效果
//        this.setAnimationStyle(R.style.AnimationPreview);
        //产生背景变暗效果
        WindowManager.LayoutParams lp = context.getWindow().getAttributes();
        lp.alpha = 0.4f;
        context.getWindow().addFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//此行代码主要是解决在华为手机上半透明效果无效的bug

        context.getWindow().setAttributes(lp);

        this.showAtLocation(root, Gravity.CENTER, 0, 0);

        TextView commit = conentView.findViewById(R.id.commit);
        TextView cancel = conentView.findViewById(R.id.cancel);

        text = conentView.findViewById(R.id.text);
        this.setOnDismissListener(new OnDismissListener() {
            @Override
            public void onDismiss() {
                Log.e("调整屏幕", "透明度");
                WindowManager.LayoutParams lp = context.getWindow().getAttributes();
                lp.alpha = 1f;
                context.getWindow().setAttributes(lp);
                context.getWindow().clearFlags(WindowManager.LayoutParams.FLAG_DIM_BEHIND);//不移除该Flag的话,在有视频的页面上的视频会出现黑屏的bug 3099007489
            }
        });

        SpannableString spanText=new SpannableString("\u3000\u3000如果您感到本app对您有帮助，为鼓励程序猿小哥哥的辛苦工作，在应用商店对本应用好评后，把用户名和好评截图发给在线客服可免费领取本应用3天试用会员。\n\u3000\u3000试用会员通常每个工作日接近下班时一起送，请在app右下角[我][消息中心]领取，非常感谢！");
        ForegroundColorSpan colorSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.red));
        spanText.setSpan(colorSpan, 46, 49, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        ForegroundColorSpan colorSpan2 = new ForegroundColorSpan(context.getResources().getColor(R.color.red));
        spanText.setSpan(colorSpan2, 50, 54, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        ForegroundColorSpan colorSpan3 = new ForegroundColorSpan(context.getResources().getColor(R.color.red));
        spanText.setSpan(colorSpan3, 65, 78, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        ForegroundColorSpan colorSpan4 = new ForegroundColorSpan(context.getResources().getColor(R.color.red));
        spanText.setSpan(colorSpan4, 106, 115, Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
        spanText.setSpan(new ClickableSpan() {
            @Override
            public void updateDrawState(TextPaint ds) {
                super.updateDrawState(ds);
                ds.setColor(Color.BLUE);       //设置文件颜色
                ds.setUnderlineText(true);      //设置下划线
            }

            @Override
            public void onClick(View view) {
                Intent intent = ChattingActivity.buildIntent(context, 915340, 0, IyuUserManager.getInstance().getUsername(), "", "two");
                context.startActivity(intent);
//                if (FileUtils.isQQClientAvailable(context)) {
//                    String url = "mqqwpa://im/chat?chat_type=wpa&uin=";
//                    context.startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(url + "3099007489")));
//                } else {
//                    ToastUtil.showToast(context, "未安装qq客户端");
//                }
            }
        }, 56, 60, Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        text.setHighlightColor(Color.TRANSPARENT); //设置点击后的颜色为透明，否则会一直出现高亮
        text.setText(spanText);
        text.setMovementMethod(LinkMovementMethod.getInstance());//开始响应点击事件

        commit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    //去评价后标记，再也不弹出好评送书弹框
                    configManager.setSendBook(true);
                    Uri uri = Uri.parse("market://details?id=" + context.getPackageName());
                    Intent intent = new Intent(Intent.ACTION_VIEW, uri);
                    intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                    context.startActivity(intent);
                } catch (Exception e) {
                    AlertDialog dialog = new AlertDialog.Builder(context).create();
                    dialog.setIcon(android.R.drawable.ic_dialog_alert);
                    dialog.setTitle(context.getResources().getString(R.string.alert_title));
                    dialog.setMessage(context.getResources().getString(R.string.about_market_error));
                    dialog.setButton(AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.alert_btn_ok),
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
                dismiss();

            }
        });

    }

}
