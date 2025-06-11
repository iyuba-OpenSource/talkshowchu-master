package com.iyuba.talkshow.ui.words;

import android.graphics.Typeface;
import android.os.Build;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.play.Player;
import com.iyuba.talkshow.databinding.ItemWordnoteBinding;

import java.util.ArrayList;
import java.util.List;

public class WordsAdapter extends RecyclerView.Adapter<WordsAdapter.WordViewHolder> {

    private List<Word> mList;
    private final List<Word> mSelectedWords;

    private final Player mPlayer;


    public boolean modeDelete = false;

    public WordsAdapter() {
        mPlayer = new Player();
        mList = new ArrayList<>();
        mSelectedWords = new ArrayList<>();
    }

    public void setList(@NonNull List<Word> list) {
        setList(list, true);
    }

    public void setList(@NonNull List<Word> list, boolean instantRefresh) {
        mList = list;
        if (instantRefresh) notifyDataSetChanged();
    }

    public List<Word> getList(){
        return mList;
    }

    public void addList(@NonNull List<Word> list) {
        mList.addAll(list);
        notifyDataSetChanged();
    }

    public void setDeleteMode(boolean value) {
        if (modeDelete != value) {
            modeDelete = value;
            notifyDataSetChanged();
        }
    }

    public boolean isInDeleteMode() {
        return modeDelete;
    }

    public void clearSelection() {
        mSelectedWords.clear();
    }

    public List<Word> getSelectedWords() {
        return mSelectedWords;
    }

    public Word getItem(int position) {
        return mList.get(position);
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemWordnoteBinding binding = ItemWordnoteBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new WordViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder holder, int position) {
        holder.setItem(mList.get(position));
        holder.setClick();
    }

    class WordViewHolder extends RecyclerView.ViewHolder {


        CheckBox checkBox;
        ImageView wordSpeaker;
        TextView wordDef;
        TextView wordKey;
        TextView wordPron;
        Word word;
        View root ;

        public WordViewHolder(ItemWordnoteBinding itemBinding) {
            super(itemBinding.getRoot());
            wordPron = itemBinding.wordPron;
            wordDef = itemBinding.wordDef;
            wordKey = itemBinding.wordKey;
            wordSpeaker = itemBinding.wordSpeaker;
            checkBox = itemBinding.checkBox;
            root = itemBinding.getRoot();
            customizeSomeStyle();
        }

        private void customizeSomeStyle() {
            if (Build.BRAND.equalsIgnoreCase("meizu")) {
                wordPron.setTypeface(Typeface.DEFAULT);
            }
        }

        public void setItem(Word word) {
            this.word = word;
            wordKey.setText(word.key);
            wordDef.setText(word.hasDefinition() ? word.def : "");
            if (word.hasPronunciation()) {
                wordPron.setText("[" + word.pron + "]");
            } else {
                wordPron.setText("");
            }
            if (word.hasAudioUrl()) {
                wordSpeaker.setVisibility(View.VISIBLE);
                wordSpeaker.setClickable(true);
            } else {
                wordSpeaker.setVisibility(View.INVISIBLE);
                wordSpeaker.setClickable(false);
            }
            if (modeDelete) {
                checkBox.setVisibility(View.VISIBLE);
                checkBox.setChecked(mSelectedWords.contains(word));
            } else {
                checkBox.setVisibility(View.GONE);
            }
        }

        void clickItem() {
            if (modeDelete) {
                if (mSelectedWords.contains(word)) {
                    mSelectedWords.remove(word);
                    checkBox.setChecked(false);
                } else {
                    mSelectedWords.add(word);
                    checkBox.setChecked(true);
                }

                //回调数据接口
                if (onWordSelectListener!=null){
                    onWordSelectListener.onWordSelect(mSelectedWords.size());
                }
            }
        }

        void clickSpeaker() {
            if (!mPlayer.isInPlayingBackState()) mPlayer.startToPlay(word.audioUrl);
        }

        public void setClick() {
            wordSpeaker.setOnClickListener(v -> clickSpeaker());
            root.setOnClickListener(v -> clickItem());
        }
    }

    /*****************************设置接口回调**************************/
    private OnWordSelectListener onWordSelectListener;

    public interface OnWordSelectListener{
        //回调单词显示
        void onWordSelect(int selectCount);
    }

    public void setOnWordSelectListener(OnWordSelectListener onWordSelectListener) {
        this.onWordSelectListener = onWordSelectListener;
    }
}
