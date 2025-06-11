package com.iyuba.talkshow.newview;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.content.Context;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.TranslateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import com.iyuba.module.user.IyuUserManager;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.event.PlayListEvent;
import com.iyuba.talkshow.newdata.ResourceUtil;
import com.iyuba.talkshow.newdata.RetrofitUtils;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.db.WordOp;
import com.iyuba.wordtest.entity.WordEntity;
import com.iyuba.wordtest.event.WordFavorEvent;

import org.greenrobot.eventbus.EventBus;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;


/**
 * 单词卡
 *
 * @author
 */
public class WordCard extends LinearLayout {

    private final Context mContext;
    private String selectText;

    ProgressBar progressBar;
    TextView tv_word;
    TextView tv_pron;
    TextView tv_word_def;
    ImageView speaker;
    ImageView img_close;
    TextView tv_no_data;
    ImageView imgFavor;

    private final WordApi wordApi;
    private WordEntity wordEntity;
    private final View view;
    private final WordPlayer player;
    private final WordOp wordOp;
    public int voa = 0;
    public int book = 0;
    public int unit = 0;

    public WordCard(Context context) {
        super(context);
        mContext = context;
        view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.word_card, this);
        progressBar = view.findViewById(R.id.progressBar);
        tv_word = view.findViewById(R.id.tv_word);
        tv_pron = view.findViewById(R.id.en_pron);
        tv_word_def = view.findViewById(R.id.word_def);
        speaker = view.findViewById(R.id.en_speaker);
        img_close = view.findViewById(R.id.img_close);
        tv_no_data = view.findViewById(R.id.tv_no_data);
        imgFavor = view.findViewById(R.id.img_favor);
//        ButterKnife.bind(this, view);
        wordApi = RetrofitUtils.getInstance().getApiService(Constant.Web.WordBASEURL, WordApi.class);
        wordOp = new WordOp(mContext);
        initGetWordMenu();
        player = new WordPlayer();

    }

    public WordCard(Context context, AttributeSet attributeSet) {
        super(context, attributeSet);
        mContext = context;
        view = ((Activity) mContext).getLayoutInflater().inflate(R.layout.word_card, this);
        progressBar = view.findViewById(R.id.progressBar);
        tv_word = view.findViewById(R.id.tv_word);
        tv_pron = view.findViewById(R.id.en_pron);
        tv_word_def = view.findViewById(R.id.word_def);
        speaker = view.findViewById(R.id.en_speaker);
        img_close = view.findViewById(R.id.img_close);
        tv_no_data = view.findViewById(R.id.tv_no_data);
        imgFavor = view.findViewById(R.id.img_favor);
//        ButterKnife.bind(this, view);
        wordOp = new WordOp(mContext);
        wordApi = RetrofitUtils.getInstance().getApiService(Constant.Web.WordBASEURL, "xml", WordApi.class);
        initGetWordMenu();
        player = new WordPlayer();
    }

    private void initGetWordMenu() {
        img_close.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
        speaker.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
        imgFavor.setColorFilter(mContext.getResources().getColor(R.color.colorPrimaryDark));
        
        img_close.setOnClickListener(arg0 -> {
            WordCard.this.setVisibility(View.GONE);
            TranslateAnimation mShowAction = new TranslateAnimation(Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, 0.0f, Animation.RELATIVE_TO_SELF, -1.0f);
            mShowAction.setDuration(500);
            view.startAnimation(mShowAction);
        });


        view.setOnClickListener(v -> {

        });
    }

    /**
     * 获取单词释义
     */
    private void getNetworkInterpretation() {
        progressBar.setVisibility(VISIBLE);
        wordApi.getWordApi(selectText).enqueue(new Callback<WordEntity>() {
            @Override
            public void onResponse(Call<WordEntity> call, Response<WordEntity> response) {
                progressBar.setVisibility(GONE);
                try {
                    wordEntity = response.body();
                    handler.sendEmptyMessage(1);
                } catch (Exception e) {
                    e.printStackTrace();

                }
                if (wordEntity != null) {
                    if (!wordEntity.key.contains(voa + "")) {
                        wordEntity.key = wordEntity.key + voa;
                    }
                    wordEntity.voa = voa;
                    wordEntity.book = 0;
                    wordEntity.unit = unit;
                    if (!wordOp.isExsitsWord(wordEntity.key, voa, IyuUserManager.getInstance().getUserId())) {
                        long result = wordOp.insertWord(wordEntity, IyuUserManager.getInstance().getUserId());
                        Log.e("WordCard", "getNetworkInterpretation result " + result);
                    }
                }
            }

            @Override
            public void onFailure(Call<WordEntity> call, Throwable t) {
                progressBar.setVisibility(GONE);
                ToastUtil.showToast(mContext, ResourceUtil.getString(mContext, R.string.please_check_network));
                tv_no_data.setVisibility(View.VISIBLE);

            }
        });
    }

    private void showWordDefInfo() {
        if ((wordEntity == null) || (wordEntity.result == 0)) {
            tv_no_data.setVisibility(View.VISIBLE);
            return;
        }

        tv_no_data.setVisibility(View.GONE);
        speaker.setVisibility(View.VISIBLE);
        if (wordEntity.key.contains(voa + "")) {
            wordEntity.key = wordEntity.key.replace("" + voa, "");
        }
        tv_word.setText(wordEntity.key);
        tv_word_def.setText(wordEntity.def);
        if (!TextUtils.isEmpty(wordEntity.pron) && !wordEntity.pron.equals("null")) {
            StringBuffer sb = new StringBuffer();
            sb.append('[').append(wordEntity.pron).append(']');
            tv_pron.setText(sb.toString());
        }

        if (IyuUserManager.getInstance().getUserId() != 0 && wordOp.isFavorWord(wordEntity.key, voa, IyuUserManager.getInstance().getUserId())) {
            imgFavor.setImageResource(R.mipmap.ic_favor);
        } else {
            imgFavor.setImageResource(R.mipmap.ic_favor_not);
        }

        imgFavor.setOnClickListener(v -> {

            if (IyuUserManager.getInstance().getUserId() == 0) {
                ToastUtil.showToast(mContext, "登录账号后可以收藏单词");
                return;

            }
            if (wordEntity.key.contains(voa + "")) {
                wordEntity.key = wordEntity.key.replace("" + voa, "");
            }
            if (wordOp.isFavorWord(wordEntity.key, voa, IyuUserManager.getInstance().getUserId())) {
                wordApi.updateWord(IyuUserManager.getInstance().getUserId() + "", "delete", "Iyuba", wordEntity.key).enqueue(new Callback<WordEntity>() {
                    @Override
                    public void onResponse(Call<WordEntity> call, Response<WordEntity> response) {
                        wordOp.deleteWord(wordEntity.key, IyuUserManager.getInstance().getUserId());
                        imgFavor.setImageResource(R.mipmap.ic_favor_not);
                        EventBus.getDefault().post(new WordFavorEvent(0));
                    }

                    @Override
                    public void onFailure(Call<WordEntity> call, Throwable t) {

                    }
                });


            } else {


                wordApi.updateWord(IyuUserManager.getInstance().getUserId() + "", "insert", "Iyuba", wordEntity.key).enqueue(new Callback<WordEntity>() {
                    @Override
                    public void onResponse(Call<WordEntity> call, Response<WordEntity> response) {
                        wordEntity.voa = voa;
                        wordEntity.book = book;
                        wordEntity.unit = unit;
                        wordOp.insertWord(wordEntity, IyuUserManager.getInstance().getUserId());
                        imgFavor.setImageResource(R.mipmap.ic_favor);
                        ToastUtil.showToast(mContext, "单词收藏成功！");
                        EventBus.getDefault().post(new WordFavorEvent(1));
                    }

                    @Override
                    public void onFailure(Call<WordEntity> call, Throwable t) {

                    }
                });

            }
        });

        speaker.setOnClickListener(arg0 -> {

            if (TextUtils.isEmpty(wordEntity.audio)) return;
            String url = wordEntity.audio;
            player.initMediaplayer();
            player.playMusic(url);
            EventBus.getDefault().post(new PlayListEvent(0));
        });
    }

    public void searchWord(String word) {
        clearCardContent();
        selectText = word;
        getNetworkInterpretation();
    }

    private void clearCardContent() {
        tv_word.setText("");
        tv_pron.setText("");
        tv_word_def.setText("");
        speaker.setVisibility(View.GONE);
        tv_no_data.setVisibility(View.GONE);
    }

    @SuppressLint("HandlerLeak")
    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            switch (msg.what) {
                case 1:
                    showWordDefInfo();
                    break;

            }
        }
    };
}
