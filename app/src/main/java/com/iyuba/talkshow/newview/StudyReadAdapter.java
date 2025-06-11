package com.iyuba.talkshow.newview;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.databinding.ItemStudyReadBinding;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.entity.WordEntity;

import java.util.ArrayList;
import java.util.List;

public class StudyReadAdapter extends RecyclerView.Adapter<StudyReadAdapter.ViewHolder> {

    private Context context ;
    private int targetFlag = 0;
    private List<WordEntity>  wordEntities;
    private List<TalkShowWords> mWordList;

    public StudyReadAdapter(int flag) {
        targetFlag = flag ;
    }

    public void setWordList(List<TalkShowWords> dataBeans) {
        if (dataBeans == null) {
            mWordList = new ArrayList<>();
        } else {
            mWordList = dataBeans;
        }
        notifyDataSetChanged();
    }

    public void setWordEntity(List<WordEntity> dataBeans) {
        if (dataBeans == null) {
            wordEntities = new ArrayList<>();
        } else {
            wordEntities = dataBeans;
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemStudyReadBinding binding = ItemStudyReadBinding.inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);
        this.context = viewGroup.getContext();
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int i) {
        if (targetFlag == 1) {
            viewHolder.setItem(mWordList.get(i));
        } else {
            viewHolder.setItem(wordEntities.get(i));
        }
    }

    @Override
    public int getItemCount() {
        if (targetFlag == 1) {
            return mWordList.size();
        } else {
            return wordEntities.size();
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private final TextView wordEnglish;
        private final TextView wordChn;

        public ViewHolder(@NonNull ItemStudyReadBinding binding) {
            super(binding.getRoot());
            wordEnglish = binding.wordEng ;
            wordChn = binding.wordCn;
        }

        public void setItem(TalkShowWords dataBean) {
            wordEnglish.setText(dataBean.word);
            wordChn.setText(dataBean.def);
        }

        public void setItem(WordEntity dataBean) {
            wordEnglish.setText(dataBean.key);
            wordChn.setText(dataBean.def);
        }

    }
}
