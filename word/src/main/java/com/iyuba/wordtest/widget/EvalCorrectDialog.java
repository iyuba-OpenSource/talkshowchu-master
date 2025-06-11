package com.iyuba.wordtest.widget;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextPaint;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ClickableSpan;
import android.util.Log;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.iyuba.wordtest.R;
import com.iyuba.wordtest.bean.SendEvaluateResponse;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.entity.WordAiEntity;
import com.iyuba.wordtest.event.GetSoundEvent;
import com.iyuba.wordtest.manager.WordConfigManager;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.network.EvaluateApi;
import com.iyuba.wordtest.network.HttpManager;
import com.iyuba.wordtest.network.WordApi;
import com.iyuba.wordtest.utils.FileUtils;
import com.iyuba.wordtest.utils.MediaUtils;
import com.iyuba.wordtest.utils.RecordManager;
import com.iyuba.wordtest.utils.TextAttr;
import com.iyuba.wordtest.utils.ToastUtil;
import com.iyuba.wordtest.utils.WordPlayer;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

/**
 * Created by carl shen on 2022/1/18
 * New Primary English, new study experience.
 */
public class EvalCorrectDialog extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "EvalCorrectDialog";
    private TextView evalSentence;
    private TextView evalTitle;
    private TextView evalCorrect;
    private TextView evalSelf;
    private TextView evalDefine;
    private TextView evalScore;
    private LinearLayout layScore;
    private LinearLayout layReader;
    private TextView readAgain;
    private TextView readHint;
    private ImageView imgSpeaker;
    private ImageView wordSpeaker;
    private ImageView wordUser;
    private ImageView evalAgain;
    private ImageView imgClose;
    private ImageView toLisa;
    private RelativeLayout lisaLayout;
    private List<SendEvaluateResponse.DataBean.WordsBean> evaluateBean;
    private TalkShowWords showWord;
    private boolean isSentence = true;
    private String titleWord;
    private Double wordScore;
    private String wordPron;
    private String userPron;
    private int wordId = 0;
    private WordPlayer player;
    private WordApi wordApi;
    private WordAiEntity wordEntity;
    private Disposable mEvalSub;
    private File file;
    private RecordManager recordManager;
    private boolean isEvaluating = false;
    private boolean isRecording;
    private ProgressDialog dialog;
    private final TreeMap<Integer, String> wordMap = new TreeMap();

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.fragment_eval_correct, null);
        evalSentence = view.findViewById(R.id.correct_sentence);
        evalTitle = view.findViewById(R.id.correct_title);
        evalCorrect = view.findViewById(R.id.correct_oral);
        evalSelf = view.findViewById(R.id.self_oral);
        evalDefine = view.findViewById(R.id.word_define);
        evalScore = view.findViewById(R.id.word_score);
        readHint = view.findViewById(R.id.eval_hint);
        layScore = view.findViewById(R.id.ll_score);
        layScore.setVisibility(View.INVISIBLE);
        readAgain = view.findViewById(R.id.word_read);
        readAgain.setOnClickListener(this);
        layReader = view.findViewById(R.id.ll_reader);
        imgSpeaker = view.findViewById(R.id.img_speaker);
        imgSpeaker.setOnClickListener(this);
        wordSpeaker = view.findViewById(R.id.read_speaker);
        wordSpeaker.setOnClickListener(this);
        wordUser = view.findViewById(R.id.user_speaker);
        wordUser.setOnClickListener(this);
        evalAgain = view.findViewById(R.id.eval_speaker);
        evalAgain.setOnClickListener(this);
        toLisa = view.findViewById(R.id.img_lisa);
        toLisa.setOnClickListener(this);
        lisaLayout = view.findViewById(R.id.lisa_layout);
        lisaLayout.setOnClickListener(this);
        imgClose = view.findViewById(R.id.correct_close);
        imgClose.setOnClickListener(this);

        builder.setView(view);
        return builder.create();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onViewCreated(View view, Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
//        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        // set the UI data
        if ((wordMap != null) && (wordMap.size() > 1)) {
            ClickableSpan[] clickSpan = new ClickableSpan[wordMap.size()];
            Log.e(TAG, "onStart setSpan evaluateBean.getSentence() " + showWord.Sentence);
            SpannableStringBuilder spanBuilder = new SpannableStringBuilder(showWord.Sentence);
            int spIndex = 0;
            for (Integer index: wordMap.keySet()) {
                Log.e(TAG, "onStart setSpan for word " + wordMap.get(index));
                clickSpan[spIndex] = new ClickableSpan() {
                    @Override
                    public void onClick(View widget) {
                        Log.e(TAG, "onStart setSpan onClick index " + index);
                        for (SendEvaluateResponse.DataBean.WordsBean word: evaluateBean) {
                            if ((word != null) && (word.getIndex() == index)) {
                                if (wordId != word.getIndex()) {
                                    wordId = word.getIndex();
                                    wordPron = word.pron2;
                                    userPron = word.user_pron2;
                                    wordScore = word.getScore();
                                    titleWord = wordMap.get(index);
                                    layScore.setVisibility(View.INVISIBLE);
                                    setUIData();
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(getResources().getColor(R.color.red));
                    }
                };
                int indOf = showWord.Sentence.indexOf(wordMap.get(index));
                Log.e(TAG, "onStart setSpan indOf " + indOf);
                String[] wordList = showWord.Sentence.split(" ");
                int start = 0;
                for (String word: wordList) {
                    if (TextUtils.isEmpty(word)) {
                        continue;
                    }
                    String wordReal = word.replaceAll("[?:!.,;\"]*([a-zA-Z]+)[?:!.,;\"]*", "$1");
                    if (word.equalsIgnoreCase(wordMap.get(index))) {
                        indOf = start;
                        Log.e(TAG, "onStart setSpan indOf " + indOf);
                        break;
                    } else if (wordReal.equalsIgnoreCase(wordMap.get(index))) {
                        indOf = start + word.indexOf(wordReal);
                        Log.e(TAG, "onStart setSpan indexOf " + indOf);
                        break;
                    }
                    start += word.length();
                    ++start;
                }
                spanBuilder.setSpan(clickSpan[spIndex], indOf, indOf + wordMap.get(index).length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                ++spIndex;
            }
            evalSentence.setText(spanBuilder);
            evalSentence.setMovementMethod(LinkMovementMethod.getInstance());
        } else if (!isSentence && (wordMap != null) && (wordMap.size() == 1)) {
            Log.e(TAG, "onStart setSpan titleWord " + titleWord);
            SpannableStringBuilder spanBuilder = new SpannableStringBuilder(titleWord);
            ClickableSpan clickSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Log.e(TAG, "onStart setSpan onClick ");
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(getResources().getColor(R.color.red));
                }
            };
            spanBuilder.setSpan(clickSpan, 0, titleWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            evalSentence.setText(spanBuilder);
            evalSentence.setMovementMethod(LinkMovementMethod.getInstance());
        } else if (isSentence && (wordMap != null) && (wordMap.size() == 1)) {
            Log.e(TAG, "onStart setSpan titleWord " + titleWord);
            Log.e(TAG, "onStart setSpan evaluateBean.Sentence " + showWord.Sentence);
            SpannableStringBuilder spanBuilder = new SpannableStringBuilder(showWord.Sentence);
            ClickableSpan clickSpan = new ClickableSpan() {
                @Override
                public void onClick(View widget) {
                    Log.e(TAG, "onStart setSpan onClick ");
                }

                @Override
                public void updateDrawState(TextPaint ds) {
                    ds.setColor(getResources().getColor(R.color.red));
                }
            };
            int indOf = showWord.Sentence.indexOf(titleWord);
            Log.e(TAG, "onStart setSpan indOf " + indOf);
            String[] wordList = showWord.Sentence.split(" ");
            int start = 0;
            for (String word: wordList) {
                if (TextUtils.isEmpty(word)) {
                    continue;
                }
                String wordReal = word.replaceAll("[?:!.,;\"]*([a-zA-Z]+)[?:!.,;\"]*", "$1");
                if (word.equalsIgnoreCase(titleWord)) {
                    indOf = start;
                    Log.e(TAG, "onStart setSpan indOf " + indOf);
                    break;
                } else if (wordReal.equalsIgnoreCase(titleWord)) {
                    indOf = start + word.indexOf(wordReal);
                    Log.e(TAG, "onStart setSpan indexOf " + indOf);
                    break;
                }
                start += word.length();
                ++start;
            }
            spanBuilder.setSpan(clickSpan, indOf, indOf + titleWord.length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            evalSentence.setText(spanBuilder);
            evalSentence.setMovementMethod(LinkMovementMethod.getInstance());
        } else {
            Log.e(TAG, "onStart evalSentence may not need correct.");
        }
        player = new WordPlayer();
        player.initMediaplayer();
        wordApi = HttpManager.getWordAi();
        setUIData();
    }

    private void setUIData() {
        evalTitle.setText(titleWord);
        evalCorrect.setText(getActivity().getString(R.string.eval_ok_sound) + titleWord + " [" + wordPron + "]");
        evalSelf.setText(getActivity().getString(R.string.eval_self_sound) + titleWord + " [" + userPron + "]");
        evalScore.setText("" + (int) (wordScore * 20));
        if (isSentence) {
            if (!TextUtils.isEmpty(titleWord)) {
                if (titleWord.equals(showWord.word)) {
                    evalDefine.setText(getActivity().getString(R.string.eval_word_dict) + showWord.def);
                } else {
                    lookWordAi(titleWord);
                }
            }
        } else {
            layScore.setVisibility(View.VISIBLE);
            evalDefine.setText(getActivity().getString(R.string.eval_word_dict) + showWord.def);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopProgressDialog();
        handler.removeCallbacksAndMessages(null);
        if (player != null) {
            player.ReleasePlayer();
        }
        if (mEvalSub != null) {
            mEvalSub.dispose();
        }
    }

    @Override
    public void onClick(View v) {
        int id = v.getId();
        if (id == R.id.correct_close) {
            dismiss();
        } else if (id == R.id.img_speaker || id == R.id.read_speaker) {
            if (isRecording || isEvaluating) {
                ToastUtil.showToast(getActivity(), "评测中...");
                return;
            }
            if (!TextUtils.isEmpty(titleWord) && titleWord.equals(showWord.word) && !TextUtils.isEmpty(showWord.audio)) {
                player.initMediaplayer();
                player.playMusic(showWord.audio);
            } else if ((wordEntity != null) && !TextUtils.isEmpty(wordEntity.audio)) {
                player.stopPlayer();
                player.playMusic(wordEntity.audio);
            } else {
                ToastUtil.showToast(getActivity(), "暂时没有查询到这个单词的正确发音");
            }
        } else if (id == R.id.user_speaker) {
            if (player != null) {
                player.stopPlayer();
            }
            if (file != null) {
                player.playMusic(file.getAbsolutePath());
            } else {
                ToastUtil.showToast(getActivity(), "您还没有评测单词呢");
            }
        } else if (id == R.id.eval_speaker) {
            if (player != null) {
                player.stopPlayer();
            }
            if (isRecording) {
                handler.removeMessages(101);
                stopRecord();
                sendEvaluate();
            } else {
                startRecord(titleWord);
            }
        } else if (id == R.id.img_lisa || id == R.id.lisa_layout) {
            EventBus.getDefault().post(new GetSoundEvent(showWord.voa_id, showWord.book_id, showWord.position));
        }
    }

    Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            super.handleMessage(msg);
            if ((getActivity() == null) || isRemoving() || isDetached()) {
                Log.e(TAG, "handleMessage getActivity is null? ");
                return;
            }
            switch (msg.what) {
                case 101:
                    if (isRecording) {
                        stopRecord();
                        sendEvaluate();
                    }
                    break;
                case 100:
//                    layReader.setVisibility(View.INVISIBLE);
//                    imgSpeaker.setVisibility(View.INVISIBLE);
                    break;
                case 102:
                    if (wordEntity != null) {
                        evalDefine.setText(getActivity().getString(R.string.eval_word_dict) + wordEntity.def);
                        if (TextUtils.isEmpty(wordPron) && !TextUtils.isEmpty(wordEntity.pron)) {
                            wordPron = wordEntity.pron;
                            evalCorrect.setText(getActivity().getString(R.string.eval_ok_sound) + titleWord + " [" + wordPron + "]");
                        }
                    }
                    if ((wordEntity != null) && !TextUtils.isEmpty(wordEntity.audio)) {
                        layReader.setVisibility(View.VISIBLE);
                        imgSpeaker.setVisibility(View.VISIBLE);
                    } else {
//                        layReader.setVisibility(View.INVISIBLE);
//                        imgSpeaker.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 103:
                    isEvaluating = false;
                    stopProgressDialog();
                    if (MediaUtils.isConnected(getActivity())) {
                        Toast.makeText(getActivity(), "评测失败，请稍后再试", Toast.LENGTH_SHORT).show();
                    } else {
                        Toast.makeText(getActivity(), "评测失败，请确认开启了数据网络", Toast.LENGTH_SHORT).show();
                    }
                    break;
                case 104:
                    isEvaluating = false;
                    stopProgressDialog();
                    ToastUtil.showToast(getActivity(), "评测服务暂时有问题，请稍后再试");
                    break;
                case 105:
                    isEvaluating = false;
                    stopProgressDialog();
                    ToastUtil.showToast(getActivity(), "评测成功");
                    if (msg.obj != null) {
                        SendEvaluateResponse.DataBean dataBean = (SendEvaluateResponse.DataBean) msg.obj;
                        if (dataBean != null) {
                            try {
                                layScore.setVisibility(View.VISIBLE);
                                wordScore = Double.parseDouble(dataBean.getTotal_score());
                                evalScore.setText("" + (int) (wordScore * 20));
                                if ((dataBean.getWords() != null) && (dataBean.getWords().size() > 0)) {
                                    userPron = dataBean.getWords().get(0).user_pron2;
                                    if (TextUtils.isEmpty(userPron) || "null".equalsIgnoreCase(userPron)) {
                                        userPron = "";
                                    }
                                    evalSelf.setText(getActivity().getString(R.string.eval_self_sound) + titleWord + " [" + userPron + "]");
                                }
                            } catch (Exception var) { }
                        }
                    }
                    break;
            }
        }
    };

    public void startRecord(String word) {
        file = FileUtils.getNewFile(getActivity().getApplicationContext(), word);
        readHint.setText("点击停止");
        Log.e(TAG, "startRecord file " + file.getAbsolutePath());
        recordManager = new RecordManager(file);
        recordManager.startRecord();
        isRecording = true;
        handler.sendEmptyMessageDelayed(101, 2000);
    }

    private void stopRecord() {
        if (recordManager != null) {
            recordManager.stopRecord();
        }
        showProgressDialog();
        isRecording = false;
        readHint.setText("点击开始");
    }

    private void showProgressDialog() {
        if (dialog == null) {
            dialog = new ProgressDialog(getActivity());
            dialog.setProgressStyle(ProgressDialog.STYLE_SPINNER);
        }
        dialog.show();
    }

    private void stopProgressDialog() {
        if ((dialog != null) && dialog.isShowing()) {
            dialog.dismiss();
        }
    }

    private void lookWordAi(String word) {
        Log.e(TAG, "getWordAi for word " + word);
        wordApi.getWordAi(word, TextAttr.encode(userPron), TextAttr.encode(wordPron), WordConfigManager.APP_ID, WordManager.getInstance().userid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<WordAiEntity>() {
                    @Override
                    public void accept(WordAiEntity response) {
                        if ((response != null)) {
                            wordEntity = response;
                            Log.e(TAG, "getWordAi onResponse word audio " + wordEntity.audio);
                            Message msg = Message.obtain();
                            msg.what = 102;
                            msg.obj = wordEntity;
                            handler.sendMessage(msg);
                            return;
                        }
                        Log.e(TAG, "getWordAi onResponse is null ");
                        handler.sendEmptyMessage(100);
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable)  {
                        if (throwable != null) {
                            Log.e(TAG, "getWordAi onFailure " + throwable.getMessage());
                        }
                        handler.sendEmptyMessage(100);
                    }
                });
    }

    private void sendEvaluate() {
        isEvaluating = true;
        Map<String, RequestBody> map = new HashMap<>(9);
        map.put(EvaluateApi.GetVoa.Param.Key.SENTENCE, HttpManager.fromString(titleWord));
        map.put(EvaluateApi.GetVoa.Param.Key.IDINDEX, HttpManager.fromString(showWord.position + ""));
        map.put(EvaluateApi.GetVoa.Param.Key.PARAID, HttpManager.fromString(showWord.book_id + ""));
        map.put(EvaluateApi.GetVoa.Param.Key.NEWSID, HttpManager.fromString("0"));
        map.put("wordId", HttpManager.fromString(wordId + ""));
        map.put("flg", HttpManager.fromString("2"));
        map.put("appId", HttpManager.fromString(WordConfigManager.APP_ID));
        map.put(EvaluateApi.GetVoa.Param.Key.TYPE, HttpManager.fromString(WordConfigManager.EVAL_TYPE));
        map.put(EvaluateApi.GetVoa.Param.Key.USERID, HttpManager.fromString(WordManager.getInstance().userid));

        //修改成单词评测的接口
        HttpManager.getEvaluateApi().evalAi10(map, HttpManager.fromFile(file))
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<SendEvaluateResponse>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        mEvalSub = d;
                    }

                    @Override
                    public void onNext(SendEvaluateResponse evalResponse) {
                        if ((evalResponse != null) && (evalResponse.getData() != null)) {
                            Log.e(TAG, "onNext getResult  " + evalResponse.getResult());
                            Message msg = Message.obtain();
                            msg.what = 105;
                            msg.obj = evalResponse.getData();
                            if (handler.hasMessages(105)) {
                                handler.removeMessages(105);
                            }
                            handler.sendMessage(msg);
                            return;
                        }
                        Log.e(TAG, "onNext evalResponse is null? ");
                        handler.sendEmptyMessage(104);
                    }

                    @Override
                    public void onError(Throwable e) {
                        if (e != null) {
                            Log.e(TAG, "onError " + e.getMessage());
                        }
                        handler.sendEmptyMessage(103);
                    }

                    @Override
                    public void onComplete() {
                    }
                });
    }

    public void setDataBean(List<SendEvaluateResponse.DataBean.WordsBean> detail) {
        evaluateBean = detail;
        if ((evaluateBean != null) && (evaluateBean.size() > 0)) {
            boolean flag = false;
            for (int index = 0; index < evaluateBean.size(); index++) {
                SendEvaluateResponse.DataBean.WordsBean word = evaluateBean.get(index);
                if ((word != null) && !TextUtils.isEmpty(word.getContent()) && !("-".equals(word.getContent())) && (word.getScore() < 2.5)) {
                    if ("Mr.".equals(word.getContent()) || "Mrs.".equals(word.getContent()) || "Ms.".equals(word.getContent())) {
                        wordMap.put(word.getIndex(), word.getContent());
                    } else {
                        wordMap.put(word.getIndex(), word.getContent().replaceAll("[?:!.,;\"]*([a-zA-Z]+)[?:!.,;\"]*", "$1"));
                    }
                    if (!flag) {
                        wordId = word.getIndex();
                        titleWord = wordMap.get(word.getIndex());
                        wordPron = word.pron2;
                        if (TextUtils.isEmpty(wordPron) || "null".equalsIgnoreCase(wordPron)) {
                            wordPron = "";
                        }
                        userPron = word.user_pron2;
                        if (TextUtils.isEmpty(userPron) || "null".equalsIgnoreCase(userPron)) {
                            userPron = "";
                        }
                        wordScore = word.getScore();
                        flag = true;
                    }
                }
            }
        } else {
            Log.e(TAG, "setDataBean WordsBean is null ");
        }
    }

    public void setVoaText(TalkShowWords detail) {
        showWord = detail;
    }

    public void setSentence(boolean detail) {
        isSentence = detail;
    }
}
