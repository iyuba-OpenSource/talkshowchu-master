package com.iyuba.talkshow.newce.study.word;

import android.content.Context;
import android.graphics.drawable.AnimationDrawable;
import android.media.MediaPlayer;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.VoaWordItemBinding;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newdata.ResourceUtil;
import com.iyuba.talkshow.newdata.RetrofitUtils;
import com.iyuba.talkshow.newview.WordApi;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.ui.detail.WordDetailActivity;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.entity.WordEntity;
import com.iyuba.wordtest.utils.StorageUtil;
import com.iyuba.wordtest.utils.TextAttr;

import java.io.File;
import java.io.IOException;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by carl shen on 2021/8/28
 * New Primary English, new study experience.
 */
public class VoaWordAdapter extends RecyclerView.Adapter<VoaWordAdapter.WordViewHolder> {
    private final String TAG = "VoaWordAdapter";
    private final Voa mVoa;
    private final int mUnit;
    private final Context mContext;
    private final List<TalkShowWords> words;
    private final MediaPlayer player;
    private final WordApi wordApi;
    private final WordDataBase db;
    private TalkShowWords talkshowWord;
    private AnimationDrawable animationSpeaker;

    public VoaWordAdapter(Context context, Voa wordVoa, int unit) {
        mContext = context;
        mVoa = wordVoa;
        db = WordDataBase.getInstance(mContext);
        Log.e("VoaWordAdapter", "getUnitByVoa book " + mVoa.series());
        Log.e("VoaWordAdapter", "getUnitWords mUnit " + unit);
        mUnit = unit;
        if (unit > 0) {
            words = db.getTalkShowWordsDao().getUnitWords(mVoa.series(), unit);
        } else {
            words = db.getTalkShowWordsDao().getUnitByVoa(mVoa.series(), mVoa.voaId());
        }
        wordApi = RetrofitUtils.getInstance().getApiService(Constant.Web.WordBASEURL, WordApi.class);

        player = new MediaPlayer();
        player.setOnPreparedListener(iMediaPlayer -> iMediaPlayer.start());
    }

    @NonNull
    @Override
    public WordViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        VoaWordItemBinding binding = VoaWordItemBinding.inflate(LayoutInflater.from(viewGroup.getContext()));
        return new WordViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WordViewHolder viewHolder, int pos) {
        viewHolder.setItem(words.get(pos), pos);
    }

    @Override
    public int getItemCount() {
        if (words == null) {
            return 0;
        }
        return words.size();
    }

    public class WordViewHolder extends RecyclerView.ViewHolder {
        private final TextView key;
        private final TextView pron;
        private final TextView def;
        private final ImageView speaker;
        private final AnimationDrawable animation;

        private RelativeLayout rlWord;

        public WordViewHolder(VoaWordItemBinding itemView) {
            super(itemView.getRoot());
            key = itemView.getRoot().findViewById(R.id.word_key);
            pron = itemView.getRoot().findViewById(R.id.word_pron);
            def = itemView.getRoot().findViewById(R.id.word_def);
            speaker = itemView.getRoot().findViewById(R.id.word_speaker);
            animation = (AnimationDrawable) speaker.getDrawable();

            rlWord = itemView.getRoot().findViewById(R.id.rl_word);
        }

        public void setItem(TalkShowWords rootWord, int position) {
            key.setText(rootWord.word);
            /*key.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (mIsLogin){
                        WordDetailActivity.start(mContext, words, position, mVoa.series(), mUnit, true);
                    }else {
                        ToastUtil.showToast(mContext,"请登录后查看");
                    }
                }
            });*/
            rlWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (!UserInfoManager.getInstance().isLogin()){
                        NewLoginUtil.startToLogin(mContext);
                        return;
                    }

                    WordDetailActivity.start(mContext, words, position, mVoa.series(), mUnit);
                }
            });
            if (TextUtils.isEmpty(rootWord.pron)) {
                pron.setText("");
            } else {
                if (rootWord.pron.startsWith("[")){
                    pron.setText(String.format("%s", TextAttr.decode(rootWord.pron)));
                } else {
                    pron.setText(String.format("[%s]", TextAttr.decode(rootWord.pron)));
                }
            }
            def.setText(rootWord.def);
            speaker.setTag(position);
            speaker.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if (TextUtils.isEmpty(rootWord.audio)) {
                        Log.e(TAG, "onClick playWord is null, need get from net " + rootWord.word);
                        talkshowWord = rootWord;
                        getNetworkInterpretation(rootWord.word);
                        return;
                    }
                    if (player == null) {
                        Log.d("VoaWordAdapter", "speaker onClick is null.");
                        return;
                    }
                    if (player.isPlaying()) {
                        player.pause();
                        if (animation != null) {
                            animation.stop();
                        }
                        if (animationSpeaker != null) {
                            animationSpeaker.stop();
                        }
                    }
                    try {
                        player.reset();
                        boolean result = playWord(rootWord);
                        if (result) {
                            if (animation != null) {
                                animation.start();
                                animationSpeaker = animation;
                            }
                        }
                        player.prepareAsync();
                        player.setOnCompletionListener(iMediaPlayer -> {
                            if (animation != null) {
                                animation.stop();
                            }
                        });
                    } catch (Exception e) {
                        Log.d(TAG, "playWord IOException: " + e.getMessage());
                    }
                }
            });
        }
    }

    private boolean playWord(TalkShowWords talkWord) throws IOException {
        if (TextUtils.isEmpty(talkWord.audio)) {
            Log.e(TAG, "playWord is null, need get from net.");
            talkshowWord = talkWord;
            getNetworkInterpretation(talkWord.word);
            return false;
        }
        String dir = StorageUtil.getWordDir(TalkShowApplication.getContext()).getAbsolutePath();
        File file = new File(dir, StorageUtil.getWordName(talkWord.audio));
        String worDir = StorageUtil.getWordDir(TalkShowApplication.getContext()).getAbsolutePath() + "/primary_audio/" + StorageUtil.getWordName(talkWord.audio);
        File wordFile = new File(worDir);
        if (file.exists() && file.isFile()) {
            Log.e(TAG, "playWord file.getAbsolutePath(): " + file.getAbsolutePath());
            player.setDataSource(file.getAbsolutePath());
        } else if (wordFile.exists() && wordFile.isFile()) {
            Log.e(TAG, "playWord worDir.getAbsolutePath(): " + worDir);
            player.setDataSource(worDir);
        } else {
            Log.e(TAG, "playWord talkshowWord.audio: " + talkWord.audio);
            if (NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                player.setDataSource(talkWord.audio);
            } else {
                ToastUtil.showToast(mContext, "暂时没有这个单词的音频，请打开数据网络播放。");
                return false;
            }
        }
        return true;
    }
    /**
     * 获取单词释义
     */
    private void getNetworkInterpretation(String selectText) {
        if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            ToastUtil.showToast(mContext, "请打开数据网络播放这个单词的音频。");
            return;
        }
        wordApi.getWordApi(selectText).enqueue(new Callback<WordEntity>() {
            @Override
            public void onResponse(Call<WordEntity> call, Response<WordEntity> response) {
                if (response != null && response.body() != null) {
                    WordEntity wordEntity = response.body();
                    showSearchResult(wordEntity);
                } else {
                    Log.e(TAG, "getWordApi onResponse is null?");
                }
            }

            @Override
            public void onFailure(Call<WordEntity> call, Throwable t) {
                ToastUtil.showToast(mContext, ResourceUtil.getString(mContext, R.string.please_check_network));
                if (t != null) {
                    Log.e(TAG, "getWordApi onFailure " + t.getMessage());
                }
            }
        });
    }
    public void showSearchResult(WordEntity newWord) {
        if (newWord != null && !TextUtils.isEmpty(newWord.audio)) {
            Log.e(TAG, "showSearchResult newWord.audio: " + newWord.audio);
            talkshowWord.audio = newWord.audio;
            try {
                player.reset();
                playWord(talkshowWord);
                player.prepareAsync();
            } catch (IOException e) {
                Log.d(TAG, "playWord IOException: " + e.getMessage());
            }
            TalkShowApplication.getSubHandler().post(new Runnable() {
                @Override
                public void run() {
                    int result = db.getTalkShowWordsDao().updateSingleWord(talkshowWord);
                    Log.e(TAG, "updateSingleWord result " + result);
                }
            });
        } else {
            ToastUtil.showToast(mContext, "此单词暂无音频");
        }
    }
}
