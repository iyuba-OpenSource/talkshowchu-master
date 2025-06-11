package com.iyuba.talkshow.ui.widget;


import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.talkshow.R;

/**
 * @Description:gridview的Adapter
 */
public class MyGridAdapter extends BaseAdapter {
    private final Context mContext;
    public String[] img_text = {"无广告", "尊贵标识", "调节语速", "高速无限下载", "查看解析",
            "智慧化测评", "PDF导出", "全部应用", "换话费"};
    public int[] imgs = {R.drawable.tequan1, R.drawable.tequan2,
            R.drawable.tequan3,
            R.drawable.tequan4,
            R.drawable.tequan5, R.drawable.tequan6,
            R.drawable.tequan7, R.drawable.tequan8,
            R.drawable.tequan9};
    public String[] imgHint = {
            "去除(开屏外)所有烦人的广告",
            "尊享V标识，社区您为贵",
            "选择自由调节语速",
            "享受VIP高速通道, 无限下载",
            "查看考试类所有试题答案解析",
            "享受智能化无限语音评测",
            "文章pdf无限导出",
            "适用于app.iyuba.cn旗下所有APP",
            "积分商城换取不同价值手机充值卡"};

    private ClickCallback callback ;

    public void setCallback(ClickCallback callback) {
        this.callback = callback;
    }

    public MyGridAdapter(Context mContext) {
        super();
        this.mContext = mContext;
    }

    public void setImageText(String[] imageText) {
        img_text = imageText.clone();
    }

    public void setImgs(int[] imgs) {
        this.imgs = imgs.clone();
    }

    public void setHint(String[] imgHint) {
        this.imgHint = imgHint.clone();
    }

    public String getHint(int i) {
        return this.imgHint[i];
    }

    @Override
    public int getCount() {
        // TODO Auto-generated method stub
        return img_text.length;
    }

    @Override
    public Object getItem(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public long getItemId(int position) {
        // TODO Auto-generated method stub
        return position;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        if (convertView == null) {
            convertView = LayoutInflater.from(mContext).inflate(
                    R.layout.grid_item, parent, false);
        }
        convertView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                callback.onClick(getHint(position),position);
            }
        });
        TextView tv = BaseViewHolder.get(convertView, R.id.tv_item);
        ImageView iv = BaseViewHolder.get(convertView, R.id.iv_item);
        iv.setBackgroundResource(imgs[position]);

        tv.setText(img_text[position]);
        return convertView;
    }

    public interface ClickCallback{
        void onClick(String hint,int position );
    }
}
