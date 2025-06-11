package com.iyuba.talkshow.newview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.databinding.ItemStudyEvalBinding;

import java.util.ArrayList;
import java.util.List;

public class StudyEvalAdapter extends RecyclerView.Adapter<StudyEvalAdapter.ViewHolder> {
    private Context context ;
    private List<VoaText> mTextList;

    public StudyEvalAdapter() {
    }

    public void setTextList(List<VoaText> dataBeans) {
        if (dataBeans == null) {
            mTextList = new ArrayList<>();
        } else {
            mTextList = dataBeans;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemStudyEvalBinding binding = ItemStudyEvalBinding.inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);
        this.context = viewGroup.getContext();
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        viewHolder.setItem(mTextList.get(i));
    }

    @Override
    public int getItemCount() {
        if (mTextList == null) {
            return 0;
        } else {
            return mTextList.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView eval_sentence;
        private final TextView eval_score;
        private final ImageView eval_image;

        public ViewHolder(@NonNull ItemStudyEvalBinding binding) {
            super(binding.getRoot());
            eval_sentence = binding.evalSentence ;
            eval_score = binding.evalScore ;
            eval_image = binding.evalImage;
        }

        public void setItem(VoaText dataBean) {
            eval_sentence.setText(dataBean.sentence());
            eval_score.setText(dataBean.readScore + "");
            if (dataBean.readScore > 90) {
                eval_score.setTextColor(context.getResources().getColor(R.color.color_green));
                eval_image.setImageResource(R.drawable.study_text_green);
            } else {
                eval_score.setTextColor(context.getResources().getColor(R.color.color_yellow));
                eval_image.setImageResource(R.drawable.study_text_yellow);
            }
        }
    }
}
