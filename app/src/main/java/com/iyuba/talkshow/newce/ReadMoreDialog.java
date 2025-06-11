package com.iyuba.talkshow.newce;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.TextView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.util.DensityUtil;
import com.xujiaji.happybubble.BubbleDialog;
import com.xujiaji.happybubble.BubbleLayout;


/**
 * Created by carl shen on 2020/7/29
 * New Primary English, new study experience.
 */
public class ReadMoreDialog {
    private final Context mContext;
    private BubbleDialog bubbleDialog;
    private TextView tvPDF, tvSpeed,tvShare;

    public ReadMoreDialog(Context mContext) {
        this.mContext = mContext;
        initDialog();
    }

    private void initDialog() {
        View dialogView = LayoutInflater.from(mContext).inflate(R.layout.read_more, null, false);
        BubbleLayout bubbleLayout = new BubbleLayout(mContext);
        bubbleLayout.setLookWidth(DensityUtil.dp2px(mContext, 10));
        bubbleLayout.setLookLength(DensityUtil.dp2px(mContext, 10));
        bubbleLayout.setBubbleRadius(6);
        bubbleDialog = new BubbleDialog(mContext).addContentView(dialogView).setPosition(BubbleDialog.Position.BOTTOM).calBar(true).setBubbleLayout(bubbleLayout);

        tvPDF = dialogView.findViewById(R.id.tv_pdf);
        tvSpeed = dialogView.findViewById(R.id.tv_speed);
        tvShare = dialogView.findViewById(R.id.tv_share);

    }

    public void setOnClickListener(View.OnClickListener onClickListener) {
        tvPDF.setOnClickListener(onClickListener);
        tvSpeed.setOnClickListener(onClickListener);
        tvShare.setOnClickListener(onClickListener);
    }


    public void show(View view) {
        bubbleDialog.setClickedView(view);
        bubbleDialog.show();
    }

    public void dismiss() {

        bubbleDialog.dismiss();
    }


}
