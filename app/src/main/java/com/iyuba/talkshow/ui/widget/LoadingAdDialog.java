package com.iyuba.talkshow.ui.widget;

import android.app.Dialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.AnimationDrawable;
import android.graphics.drawable.ColorDrawable;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iyuba.ad.adblocker.AdBlocker;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.DialogLoadingWithAdBinding;
import com.iyuba.talkshow.util.AdInfoFlowUtil;
import com.youdao.sdk.nativeads.NativeResponse;

import java.util.List;

/**
 * 自定义加载进度对话框
 * Created by Administrator on 2016-10-28.
 */

public class LoadingAdDialog extends Dialog {

    AdInfoFlowUtil adInfoFlowUtil;
    NativeResponse nativeResponse;
    DialogLoadingWithAdBinding binding ;
    AnimationDrawable animationDrawable;

    public LoadingAdDialog(final Context context) {
        super(context, R.style.DialogTheme);
        binding = DialogLoadingWithAdBinding.inflate(getLayoutInflater());
        /**设置对话框背景透明*/
        getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        setContentView(binding.getRoot());
        setCanceledOnTouchOutside(false);
        binding.tvTitle.setText(context.getString(R.string.loading));

        //屏蔽广告
        if (!AdBlocker.getInstance().shouldBlockAd()){
            adInfoFlowUtil = new AdInfoFlowUtil(context, false, new AdInfoFlowUtil.Callback() {
                @Override
                public void onADLoad(List ads) {
                    if (ads != null && ads.size() > 0) {
                        nativeResponse = (NativeResponse) ads.get(0);
                    }
                }
            }).setAdRequestSize(1);
            adInfoFlowUtil.refreshAd();
        }


        binding.ivClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                dismiss();
            }
        });


        // 把动画资源设置为imageView的背景,也可直接在XML里面设置
        binding.ivAnimLoading.setBackgroundResource(R.drawable.publish_loading_anim);
        animationDrawable = (AnimationDrawable) binding.ivAnimLoading.getBackground();
    }


    public void show(boolean vip) {
        if (!vip) {
            if (nativeResponse != null) {
                showAd();
            }
        }
        super.show();

        animationDrawable.start();
    }

    private void showAd() {
        binding.layoutAd.setVisibility(View.VISIBLE);
        binding.layoutAd.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                nativeResponse.handleClick(binding.layoutAd);
            }
        });
        nativeResponse.recordImpression(binding.layoutAd);
        Glide.with(getContext()).load(nativeResponse.getMainImageUrl()).placeholder(R.drawable.default_pic).into(binding.ivAd);
        binding.tvTitle.setText(nativeResponse.getTitle());
    }

    @Override
    public void dismiss() {
        if (adInfoFlowUtil!=null){
            adInfoFlowUtil.destroy();
        }
        super.dismiss();
    }


    public void setRetryOnClick(View.OnClickListener listener) {
        binding.tvRetry.setOnClickListener(listener);
    }

    CharSequence title;

    public LoadingAdDialog setMessageText(CharSequence message) {
        binding.tvMessage.setText(message);
        return this;
    }

    public void setTitleText(CharSequence message) {
        title = message;
        binding.tvTitle.setText(message);
    }

    public void showSuccess(CharSequence message) {
        animationDrawable.stop();
        binding.ivAnimLoading.setVisibility(View.GONE);

        binding.ivHint.setVisibility(View.VISIBLE);
        binding.ivHint.setImageResource(R.drawable.iv_publish_success);
        setTitleText(message);
    }

    public void showFailure(String message) {
        animationDrawable.stop();
        binding.ivAnimLoading.setVisibility(View.GONE);

        binding.ivHint.setVisibility(View.VISIBLE);
        binding.ivHint.setImageResource(R.drawable.iv_publish_failure);
//        setTitleText(message);
    }

    public void showRetryButton() {
        binding.tvRetry.setVisibility(View.VISIBLE);
        binding.tvTitle.setVisibility(View.GONE);
    }

    public void retry() {
        animationDrawable.start();
        binding.ivAnimLoading.setVisibility(View.VISIBLE);

        binding.ivHint.setVisibility(View.GONE);
        binding.tvTitle.setVisibility(View.VISIBLE);
        binding.tvTitle.setText(title);
        binding.tvRetry.setVisibility(View.GONE);
    }
}
