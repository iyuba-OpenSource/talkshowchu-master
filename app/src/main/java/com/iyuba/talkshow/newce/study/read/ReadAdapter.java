//package com.iyuba.talkshow.newce.study.read;
//
//import android.content.Context;
//import android.util.Log;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.TextView;
//
//import androidx.annotation.NonNull;
//import androidx.recyclerview.widget.RecyclerView;
//
//import com.iyuba.talkshow.R;
//import com.iyuba.talkshow.data.model.VoaText;
//import com.iyuba.talkshow.databinding.FragmentReadItemBinding;
//import com.iyuba.talkshow.newdata.Config;
//import com.iyuba.talkshow.newdata.SPconfig;
//import com.iyuba.talkshow.newview.TextPage;
//import com.iyuba.talkshow.newview.TextPageSelectTextCallBack;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import javax.inject.Inject;
//
///**
// * Created by carl shen on 2020/7/29
// * New Primary English, new study experience.
// */
//public class ReadAdapter extends RecyclerView.Adapter<ReadAdapter.MyViewHolder> {
//    private List<VoaText> list = new ArrayList<VoaText>();
//    private int lightPosition = 0;
//    private TextPageSelectTextCallBack tpstc;
//
//    @Inject
//    public ReadAdapter() {
//        list = new ArrayList<VoaText>();
//    }
//
//    public void SetTextPageCallback(TextPageSelectTextCallBack tpc) {
//        tpstc = tpc;
//    }
//
//    public void SetVoaList(List<VoaText> voalist) {
//        if (voalist != null) {
//            list.clear();
//            list.addAll(voalist);
//        }
//    }
//
//    @NonNull
//    @Override
//    public MyViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int i) {
//        FragmentReadItemBinding bindVoa = FragmentReadItemBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
//        return new MyViewHolder(bindVoa);
//    }
//
//    @Override
//    public void onBindViewHolder(@NonNull MyViewHolder viewHolder, final int i) {
//        VoaText detail = list.get(i);
//        viewHolder.sentence_cn.setText(detail.sentenceCn());
//        viewHolder.sentence_en.setText(detail.sentence());
//        viewHolder.sentence_en.setTextpageSelectTextCallBack(tpstc);
//
//        Context mContext = viewHolder.sentence_cn.getContext();
//        if (lightPosition == i) {
//            viewHolder.sentence_cn.setTextColor(mContext.getResources().getColor(R.color.colorText));
//            viewHolder.sentence_en.setTextColor(mContext.getResources().getColor(R.color.colorText));
//        } else {
//            viewHolder.sentence_cn.setTextColor(mContext.getResources().getColor(R.color.black));
//            viewHolder.sentence_en.setTextColor(mContext.getResources().getColor(R.color.black));
//        }
//
//        boolean isShowCn = SPconfig.Instance().loadBoolean(Config.ISSHOWCN);
//        if (isShowCn) {
//            viewHolder.sentence_cn.setVisibility(View.VISIBLE);
//        } else {
//            viewHolder.sentence_cn.setVisibility(View.GONE);
//        }
//
//        viewHolder.itemView.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                tpstc.cancelWordCard();
//            }
//        });
//    }
//
//    @Override
//    public int getItemCount() {
//        return list.size();
//    }
//
//    public VoaText getLightItem() {
//        return list.get(lightPosition);
//    }
//
//    public void setLightPosition(int position) {
//        this.lightPosition = position;
//        Log.e("ReadAdapter", "ppppp");
//        notifyDataSetChanged();
//    }
//
//    public void refreshAdpter() {
//        notifyDataSetChanged();
//    }
//
//    public class MyViewHolder extends RecyclerView.ViewHolder {
//        TextView sentence_cn;
//        TextPage sentence_en;
//
//        public MyViewHolder(@NonNull FragmentReadItemBinding itemView) {
//            super(itemView.getRoot());
//            sentence_cn = itemView.sentenceCn;
//            sentence_en = itemView.sentenceEn;
//        }
//    }
//}
