package com.iyuba.wordtest.adapter;

import android.content.Context;
import android.graphics.Color;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.wordtest.R;
import com.iyuba.wordtest.ui.detail.WordDetailActivity;
import com.iyuba.wordtest.databinding.WordtestSimpleWordItemBinding;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.utils.TextAttr;

import java.util.List;


public class SimpleTalkshowAdapter extends RecyclerView.Adapter<SimpleTalkshowAdapter.ViewHolder> {

    private Context context;

    public SimpleTalkshowAdapter(List<TalkShowWords> words) {
        this.words = words;
    }

    private final List<TalkShowWords> words;

    @NonNull
    @Override
    public SimpleTalkshowAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        context = viewGroup.getContext();
//        View view ;
        WordtestSimpleWordItemBinding binding = WordtestSimpleWordItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
//        view = LayoutInflater.from(viewGroup.getContext()).inflate(R.layout.wordtest_simple_word_item, viewGroup, false);
        return new SimpleTalkshowAdapter.ViewHolder(binding);
    }


    @Override
    public void onBindViewHolder(@NonNull SimpleTalkshowAdapter.ViewHolder viewHolder, int pos) {
        TalkShowWords rootWord = words.get(pos);
        viewHolder.setItem(rootWord, pos);
    }

    @Override
    public int getItemCount() {
        return words.size();
    }



    public class ViewHolder extends RecyclerView.ViewHolder {


        private final View talkshowinfoImg;
        private final View parent;
        TalkShowWords talkShowWord;

        boolean isChecked = false;

        private final ImageView talkshowPlay;
        private final TextView talkshowword;
        private final TextView talkshowpron;
        private final TextView talkshowdef;
        private final LinearLayout ll1;
        private final LinearLayout ll2;


        public ViewHolder(WordtestSimpleWordItemBinding itemView) {
            super(itemView.getRoot());
            talkshowinfoImg = itemView.talkshowinfoImg;
            parent = itemView.parent;
            talkshowPlay = itemView.talkshowPlay;
            talkshowword = itemView.talkshowword;
            talkshowpron = itemView.talkshowpron;
            talkshowdef = itemView.talkshowdef;
            ll1 = itemView.ll1;
            ll2 = itemView.ll2;

        }


        public void setItem(TalkShowWords rootWord, int position) {
            talkshowPlay.setOnClickListener(v->{
                if (onWordItemClickListener!=null){
                    onWordItemClickListener.onPlayAudio(rootWord.audio);
                }
            });
            talkshowinfoImg.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    WordDetailActivity.start(context, words, position, words.get(position).book_id, words.get(position).unit_id);
                }
            });
            parent.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    isChecked = !isChecked;
                    setItemVisible();
                }
            });
            talkshowword.setText(rootWord.word);
            if (!TextUtils.isEmpty(rootWord.pron)) {
                if (!rootWord.pron.startsWith("[")){
                    talkshowpron.setText(String.format("[%s]", TextAttr.decode(rootWord.pron)));
                }else {
                    talkshowpron.setText(String.format("%s", TextAttr.decode(rootWord.pron)));
                }
            }else {
                talkshowpron.setText("");
            }

            //这里去掉颜色显示
            /*if (rootWord.wrong == 2){
                talkshowword.setTextColor(Color.parseColor("#FFBD1616"));
            }else if (rootWord.wrong == 1){
                talkshowword.setTextColor(Color.parseColor("#FF8BC34A"));
            }*/
            talkshowword.setTextColor(context.getResources().getColor(R.color.black));

            talkshowdef.setText(rootWord.def);
            setItemVisible();
            itemView.setTag(position);
            talkShowWord = rootWord;
        }

        private void setItemVisible() {
            if (isChecked) {
                ll1.setVisibility(View.GONE);
                ll2.setVisibility(View.VISIBLE);
            } else {
                ll1.setVisibility(View.VISIBLE);
                ll2.setVisibility(View.GONE);
            }
        }
    }

    //设置回调
    private OnWordItemClickListener onWordItemClickListener;

    public interface OnWordItemClickListener{
        //播放音频
        void onPlayAudio(String audioUrl);
    }

    public void setOnWordItemClickListener(OnWordItemClickListener onWordItemClickListener) {
        this.onWordItemClickListener = onWordItemClickListener;
    }
}
