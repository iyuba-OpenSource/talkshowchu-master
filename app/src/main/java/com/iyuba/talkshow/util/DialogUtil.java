package com.iyuba.talkshow.util;

import android.app.Activity;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.net.Uri;
import android.os.Build;
import android.text.SpannableString;
import android.text.Spanned;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.text.style.ForegroundColorSpan;
import android.util.DisplayMetrics;
import android.view.Gravity;
import android.view.View;
import android.view.Window;
import android.view.WindowManager;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AlertDialog;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.lil.help_fix.util.AppStoreUtil;
import com.iyuba.talkshow.lil.help_fix.view.dialog.SingleButtonDialog;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.PreviewBookActivity;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.tencent.vasdolly.helper.ChannelReaderUtil;

import java.util.List;

import personal.iyuba.personalhomelibrary.ui.message.ChattingActivity;
import personal.iyuba.personalhomelibrary.ui.message.MessageActivity;

/**
 * @desction: 弹窗功能
 * @date: 2023/2/17 13:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * <p>
 * <p>
 * 这里为部分通用弹窗的功能集合
 */
public class DialogUtil {
    public static final String DIALOG = "dialog";
    public static final String FRAGMENT = "fragment";


    //显示送书弹窗
    public static void showSendBookDialog(Context context, String type) {
        AlertDialog dialog = new AlertDialog.Builder(context).create();
        dialog.setCancelable(false);
        dialog.show();

        Window window = dialog.getWindow();
        if (window != null) {
            window.setContentView(R.layout.dialog_sendbook);
            window.setGravity(Gravity.CENTER);
            window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

            DisplayMetrics dm = context.getResources().getDisplayMetrics();
            WindowManager.LayoutParams lp = window.getAttributes();
            lp.width = dm.widthPixels * 4 / 5;
            lp.flags = WindowManager.LayoutParams.FLAG_DIM_BEHIND;
            window.setAttributes(lp);

            TextView msgText = window.findViewById(R.id.msg);
            TextView evalBtn = window.findViewById(R.id.evaluate);
            evalBtn.setOnClickListener(v -> {
                try {
                    //去评价后标记，再也不弹出好评送书弹框
//                    configManager.setSendBook(true);
                    if (type.equals(DIALOG)) {
                        saveSendBookData(context, true);
                    }

                    //跳转到应用市场
                    AppStoreUtil.toAppStoreDetailByType(context);
//                    MarketUtil.getTools().startMarket(context,context.getPackageName());

                    dialog.dismiss();
                } catch (Exception e) {
                    android.app.AlertDialog newDialog = new android.app.AlertDialog.Builder(context).create();
                    newDialog.setIcon(android.R.drawable.ic_dialog_alert);
                    newDialog.setTitle(context.getResources().getString(R.string.alert_title));
                    newDialog.setMessage(context.getResources().getString(R.string.about_market_error));
                    newDialog.setButton(android.app.AlertDialog.BUTTON_NEUTRAL, context.getString(R.string.alert_btn_ok),
                            new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int which) {
                                }
                            });
                    newDialog.show();
                }
            });
            TextView nextBtn = window.findViewById(R.id.next);
            nextBtn.setOnClickListener(v -> {
                dialog.dismiss();
            });

            // TODO: 2023/11/9 李老师明确表示只有3天的本应用会员
            String showText = "\u3000\u3000如果您感到本app对您有帮助，为鼓励程序猿小哥哥的辛苦工作，在应用商店对本应用好评后，把用户名和好评截图发给在线客服可免费领取本应用3天试用会员和由爱语吧名师团队编写的电子书哦。" +
                    "\n\u3000\u3000赠送的电子书类型如下，您可以任意选择获取：考试和日语类书籍、原版英文名著" +
                    "\n\u3000\u3000机会难得，不容错过，小伙伴们赶快行动吧!";
            SpannableString showSpan = new SpannableString(showText);

            String needText1 = "用户名";
            String needText2 = "好评截图";
            String helpText = "在线客服";
            String giftText1 = "本应用3天试用会员";
            String giftText2 = "由爱语吧名师团队编写的电子书";
            String pdfText1 = "考试和日语类书籍";
            String pdfText2 = "原版英文名著";

            ForegroundColorSpan needSpan1 = new ForegroundColorSpan(context.getResources().getColor(R.color.red));
            int needIndex1 = showText.indexOf(needText1);
            showSpan.setSpan(needSpan1, needIndex1, needIndex1 + needText1.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            ForegroundColorSpan needSpan2 = new ForegroundColorSpan(context.getResources().getColor(R.color.red));
            int needIndex2 = showText.indexOf(needText2);
            showSpan.setSpan(needSpan2, needIndex2, needIndex2 + needText2.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            ForegroundColorSpan helpSpan = new ForegroundColorSpan(context.getResources().getColor(R.color.blue));
            int helpIndex = showText.indexOf(helpText);
            showSpan.setSpan(helpSpan, helpIndex, helpIndex + helpText.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            showSpan.setSpan(new ClickableSpan() {

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setColor(context.getResources().getColor(R.color.blue));
                    ds.setUnderlineText(true);
                }

                @Override
                public void onClick(@NonNull View widget) {
//                    if(isQQClientAvailable(context)) {
//                        String url = "mqqwpa://im/chat?chat_type=wpa&uin=";
//                        context.startActivity(new Intent(Intent.ACTION_VIEW,
//                                Uri.parse(url + "2111356785")));
//                    }else{
//                        ToastUtil.showToast(context,"未安装qq客户端,请手动添加客服QQ:2111356785");
//                    }

                    // TODO: 2024/2/22 这里根据展姐要求，调整为跳转到消息中心sdk的功能，直接跳转到某个用户的聊天界面中
                    if (!UserInfoManager.getInstance().isLogin()) {
                        NewLoginUtil.startToLogin(context);
                        return;
                    }

                    //关闭界面
                    dialog.dismiss();

                    try {
                        context.startActivity(ChattingActivity.buildIntent(context, 915340, 0, "jesselee", "", "two"));
                    } catch (Exception e) {
                        SingleButtonDialog buttonDialog = new SingleButtonDialog(context);
                        buttonDialog.create();
                        buttonDialog.setTitle("查找错误");
                        buttonDialog.setMsg("未查找到客服信息,请手动添加客服QQ:2111356785");
                        buttonDialog.setButton("复制信息", new SingleButtonDialog.OnSingleClickListener() {
                            @Override
                            public void onClick() {
                                ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
                                ClipData clipData = ClipData.newPlainText("送书客服qq", "2111356785");
                                manager.setPrimaryClip(clipData);

                                ToastUtil.showToast(context, "复制成功");
                            }
                        });
                    }
                }
            }, helpIndex, helpIndex + helpText.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            ForegroundColorSpan giftSpan1 = new ForegroundColorSpan(context.getResources().getColor(R.color.red));
            int giftIndex1 = showText.indexOf(giftText1);
            showSpan.setSpan(giftSpan1, giftIndex1, giftIndex1 + giftText1.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            ForegroundColorSpan giftSpan2 = new ForegroundColorSpan(context.getResources().getColor(R.color.red));
            int giftIndex2 = showText.indexOf(giftText2);
            showSpan.setSpan(giftSpan2, giftIndex2, giftIndex2 + giftText2.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            ForegroundColorSpan pdfSpan1 = new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary));
            int pdfIndex1 = showText.indexOf(pdfText1);
            showSpan.setSpan(pdfSpan1, pdfIndex1, pdfIndex1 + pdfText1.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            showSpan.setSpan(new ClickableSpan() {

                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                }

                @Override
                public void onClick(@NonNull View widget) {
                    PreviewBookActivity.start(context, pdfText1, R.drawable.bg_send_book_image);
                }
            }, pdfIndex1, pdfIndex1 + pdfText1.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            ForegroundColorSpan pdfSpan2 = new ForegroundColorSpan(context.getResources().getColor(R.color.colorPrimary));
            int pdfIndex2 = showText.indexOf(pdfText2);
            showSpan.setSpan(pdfSpan2, pdfIndex2, pdfIndex2 + pdfText2.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);
            showSpan.setSpan(new ClickableSpan() {
                @Override
                public void updateDrawState(@NonNull TextPaint ds) {
                    super.updateDrawState(ds);
                    ds.setUnderlineText(true);
                }

                @Override
                public void onClick(@NonNull View widget) {
                    PreviewBookActivity.start(context, pdfText2, R.drawable.bg_send_book_image_novel);
                }
            }, pdfIndex2, pdfIndex2 + pdfText2.length(), Spanned.SPAN_EXCLUSIVE_INCLUSIVE);

            msgText.setHighlightColor(context.getResources().getColor(R.color.transparent));
            msgText.setText(showSpan);
            msgText.setMovementMethod(LinkMovementMethod.getInstance());
        }
    }

    //保存评价数据
    private static void saveSendBookData(Context context, boolean saveEval) {
        SharedPreferences preferences = context.getSharedPreferences("kouyu_show_file", Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = preferences.edit();
        editor.putBoolean(ConfigManager.Key.FIRST_SEND_BOOK, saveEval);
        editor.apply();
    }

    /**
     * 判断qq是否可用
     *
     * @param context
     * @return
     */
    private static boolean isQQClientAvailable(Context context) {
        final PackageManager packageManager = context.getPackageManager();
        List<PackageInfo> pinfo = packageManager.getInstalledPackages(0);
        if (pinfo != null) {
            for (int i = 0; i < pinfo.size(); i++) {
                String pn = pinfo.get(i).packageName;
                if (pn.equals("com.tencent.mobileqq")) {
                    return true;
                }
            }
        }
        return false;
    }

    /*******************************新的弹窗显示**********************************/
    //跳转vip界面弹窗
    public static void showVipDialog(Context context, String showMsg, int vipType) {
        new AlertDialog.Builder(context)
                .setTitle("温馨提示")
                .setMessage(showMsg)
                .setPositiveButton("开通会员", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        NewVipCenterActivity.start(context, vipType);
                    }
                }).setNegativeButton("暂不需要", null)
                .create().show();
    }
}
