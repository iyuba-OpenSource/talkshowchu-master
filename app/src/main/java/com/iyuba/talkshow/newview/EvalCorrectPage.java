package com.iyuba.talkshow.newview;

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

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iyuba.imooclib.ui.mobclass.MobClassActivity;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.lil.help_fix.view.dialog.LoadingDialog;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.newdata.EvaluateBean;
import com.iyuba.talkshow.newdata.RetrofitUtils;
import com.iyuba.talkshow.util.TextAttr;
import com.iyuba.talkshow.util.iseutil.ResultParse;
import com.iyuba.wordtest.bean.SendEvaluateResponse;
import com.iyuba.wordtest.entity.WordAiEntity;
import com.iyuba.wordtest.network.EvaluateApi;
import com.iyuba.wordtest.network.HttpManager;
import com.iyuba.wordtest.utils.FileUtils;
import com.iyuba.wordtest.utils.RecordManager;
import com.iyuba.wordtest.utils.ToastUtil;

import org.jetbrains.annotations.NotNull;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

/**
 * Created by carl shen on 2022/1/18
 * New Primary English, new study experience.
 */
public class EvalCorrectPage extends DialogFragment implements View.OnClickListener {
    public static final String TAG = "EvalCorrectPage";
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
    private EvaluateBean evaluateBean;
    private VoaText voaText;
    private String titleWord;
    private Double wordScore;
    private String wordPron;
    private String userPron;
    private int uid = 0;
    private int wordId = 0;
    private WordPlayer player;
    private WordApi wordApi;
    private WordAiEntity wordEntity;
    private Disposable mEvalSub;
    private File file;
    private RecordManager recordManager;
    private boolean isEvaluating = false;
    private boolean isRecording;
    private final TreeMap<Integer, String> wordMap = new TreeMap();

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view = LayoutInflater.from(getActivity()).inflate(R.layout.fragment_eval_correct, null);
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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
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
        /*if ((wordMap != null) && (wordMap.size() > 1)) {
            ClickableSpan[] clickSpan = new ClickableSpan[wordMap.size()];
            Log.e(TAG, "onStart setSpan evaluateBean.getSentence() " + evaluateBean.getSentence());
            SpannableStringBuilder spanBuilder = new SpannableStringBuilder(evaluateBean.getSentence());
            int spIndex = 0;
            for (Integer index: wordMap.keySet()) {
                Log.e(TAG, "onStart setSpan for word " + wordMap.get(index));
                clickSpan[spIndex] = new ClickableSpan() {
                    @Override
                    public void onClick(@NotNull View widget) {
                        Log.e(TAG, "onStart setSpan onClick index " + index);
                        for (EvaluateBean.WordsBean word: evaluateBean.getWords()) {
                            if ((word != null) && (word.getIndex() == index)) {
                                if (wordId != word.getIndex()) {
                                    wordId = word.getIndex();
                                    wordPron = word.pron2;
                                    userPron = word.user_pron2;
                                    wordScore = word.getScore();
                                    titleWord = wordMap.get(index);
                                    setUIData();
                                    layScore.setVisibility(View.INVISIBLE);
                                    break;
                                }
                            }
                        }
                    }

                    @Override
                    public void updateDrawState(TextPaint ds) {
                        ds.setColor(getResources().getColor(R.color.RED));
                    }
                };
                int indOf = evaluateBean.getSentence().indexOf(wordMap.get(index));
                Log.e(TAG, "onStart setSpan indOf " + indOf);
                if (indOf < 0) {
                    if (wordMap.get(index).contains("’")) {
                        indOf = evaluateBean.getSentence().indexOf(wordMap.get(index).replace("’", "'"));
                    } else if (wordMap.get(index).contains("'")) {
                        indOf = evaluateBean.getSentence().indexOf(wordMap.get(index).replace("'", "’"));
                    } else {
                        indOf = 0;
                    }
                    Log.e(TAG, "onStart setSpan indOf " + indOf);
                }
                String[] wordList = evaluateBean.getSentence().split(" ");
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
        } else if ((wordMap != null) && (wordMap.size() == 1)) {
            evalSentence.setText(ResultParse.getSenResultEvaluate(evaluateBean.getWords(), evaluateBean.getSentence()));
        } else {
            Log.e(TAG, "onStart evalSentence may not need correct.");
        }*/

        //将原来只显示错误单词修改为显示正确和错误单词
        int clickCount = 0;
        for (int i = 0; i < evaluateBean.getWords().size(); i++) {
            EvaluateBean.WordsBean tempBean = evaluateBean.getWords().get(i);
            if (tempBean != null
                    && !TextUtils.isEmpty(tempBean.getContent())
                    && !("-".equals(tempBean.getContent()))
                    && (tempBean.getScore() < 2.5 || tempBean.getScore() > 4))
                clickCount++;
        }

        if (clickCount > 1) {
            ClickableSpan[] clickableSpans = new ClickableSpan[clickCount];
            SpannableStringBuilder span = new SpannableStringBuilder(evaluateBean.getSentence());
            int spIndex = 0;
            for (int i = 0; i < evaluateBean.getWords().size(); i++) {
                EvaluateBean.WordsBean tempWord = evaluateBean.getWords().get(i);
                int curIndex = i;
                if (tempWord != null
                        && !TextUtils.isEmpty(tempWord.getContent())
                        && !("-".equals(tempWord.getContent()))
                        && (tempWord.getScore() < 2.5 || tempWord.getScore() > 4)) {

                    tempWord.setContent(tempWord.getContent().replaceAll("[?:!.,;\"]*([a-zA-Z]+)[?:!.,;\"]*", "$1"));

                    clickableSpans[spIndex] = new ClickableSpan() {
                        @Override
                        public void onClick(@NonNull View widget) {
                            if(wordId == tempWord.getIndex()){
                                return;
                            }

                            //根据点击中断显示
                            if (isRecording) {
                                if (recordManager != null) {
                                    recordManager.stopRecord();
                                }
                                isRecording = false;
                                readHint.setText("点击开始");
                            }

                            if (isEvaluating) {
                                handler.sendEmptyMessage(103);
                                isEvaluating = false;
                            }

                            if ((tempWord != null) && (tempWord.getIndex() == curIndex)) {
                                if (wordId != tempWord.getIndex()) {
                                    wordId = tempWord.getIndex();
                                    wordPron = tempWord.pron2;
                                    userPron = tempWord.user_pron2;
                                    wordScore = tempWord.getScore();
                                    titleWord = tempWord.getContent();
                                    setUIData();
                                    layScore.setVisibility(View.INVISIBLE);
                                }
                            }
                        }

                        @Override
                        public void updateDrawState(@NonNull TextPaint ds) {
                            if (tempWord.getScore() > 4) {
                                ds.setColor(getResources().getColor(R.color.colorPrimary));
                            } else if (tempWord.getScore() < 2.5) {
                                ds.setColor(getResources().getColor(R.color.RED));
                            }
                        }
                    };

                    int index = evaluateBean.getSentence().indexOf(tempWord.getContent());
                    if (index < 0) {
                        if (tempWord.getContent().contains("’")) {
                            index = evaluateBean.getSentence().indexOf(tempWord.getContent().replace("’", "'"));
                        } else if (tempWord.getContent().contains("'")) {
                            index = evaluateBean.getSentence().indexOf(tempWord.getContent().replace("'", "’"));
                        } else {
                            index = 0;
                        }
                    }
                    String[] wordList = evaluateBean.getSentence().split(" ");
                    int start = 0;
                    for (String word : wordList) {
                        if (TextUtils.isEmpty(word)) {
                            continue;
                        }
                        String wordReal = word.replaceAll("[?:!.,;\"]*([a-zA-Z]+)[?:!.,;\"]*", "$1");
                        if (word.equalsIgnoreCase(tempWord.getContent())) {
                            index = start;
                            break;
                        } else if (wordReal.equalsIgnoreCase(tempWord.getContent())) {
                            index = start + word.indexOf(wordReal);
                            break;
                        }
                        start += word.length();
                        ++start;
                    }
                    span.setSpan(clickableSpans[spIndex], index, index + tempWord.getContent().length(), Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
                    ++spIndex;
                }
                evalSentence.setText(span);
                evalSentence.setMovementMethod(LinkMovementMethod.getInstance());
            }
        } else if (clickCount == 1) {
            evalSentence.setText(ResultParse.getSenResultEvaluate(evaluateBean.getWords(), evaluateBean.getSentence()));
        }

        player = new WordPlayer();
        player.initMediaplayer();
        wordApi = RetrofitUtils.getInstance().getApiService(Constant.Web.WordBASEURL, WordApi.class);
        setUIData();
    }

    private void setUIData() {
        evalTitle.setText(titleWord);
        evalCorrect.setText(getActivity().getString(R.string.eval_ok_sound) + titleWord + " [" + wordPron + "]");
        evalSelf.setText(getActivity().getString(R.string.eval_self_sound) + titleWord + " [" + userPron + "]");
        evalScore.setText("" + (int) (wordScore * 20));
        if (!TextUtils.isEmpty(titleWord)) {
            lookWordAi(titleWord);
        }
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        stopLoading();
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
        switch (v.getId()) {
            case R.id.correct_close:
                dismiss();
                break;
            case R.id.img_speaker:
            case R.id.read_speaker:
                if (isRecording || isEvaluating) {
                    ToastUtil.showToast(getActivity(), "评测中...");
                    return;
                }
                if ((wordEntity != null) && !TextUtils.isEmpty(wordEntity.audio)) {
                    player.stopPlayer();
                    player.playMusic(wordEntity.audio);
                } else {
                    ToastUtil.showToast(getActivity(), "暂时没有查询到这个单词的正确发音");
                }
                break;
            case R.id.user_speaker:
                if (player != null) {
                    player.stopPlayer();
                }
                if (file != null) {
                    player.playMusic(file.getAbsolutePath());
                } else {
                    ToastUtil.showToast(getActivity(), "您还没有评测单词呢");
                }
                break;
            case R.id.eval_speaker:
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
                break;
            case R.id.img_lisa:
            case R.id.lisa_layout:
                ArrayList<Integer> typeIdFilter = new ArrayList<>();
                typeIdFilter.add(3);
                startActivity(MobClassActivity.buildIntent(getActivity(), 3, true, typeIdFilter));
//                dismiss();
                break;
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
                    layReader.setVisibility(View.INVISIBLE);
                    imgSpeaker.setVisibility(View.INVISIBLE);
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
                        layReader.setVisibility(View.INVISIBLE);
                        imgSpeaker.setVisibility(View.INVISIBLE);
                    }
                    break;
                case 103:
                    isEvaluating = false;
                    stopLoading();
                    ToastUtil.showToast(getActivity(), "评测服务暂时有问题，请稍后再试");
                    break;
                case 104:
                    isEvaluating = false;
                    stopLoading();
                    ToastUtil.showToast(getActivity(), "评测失败了，可以再试试");
                    break;
                case 105:
                    isEvaluating = false;
                    stopLoading();
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
                            } catch (Exception var) {
                            }
                        }
                    }
                    break;
            }
        }
    };

    public void startRecord(String word) {
        file = FileUtils.getNewFile(TalkShowApplication.getContext(), word);
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
        startLoading("正在评测中~");
        isRecording = false;
        readHint.setText("点击开始");
    }

    //加载弹窗
    private LoadingDialog loadingDialog;

    private void startLoading(String showMsg) {
        if (loadingDialog == null) {
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMsg(showMsg);
        loadingDialog.show();
    }

    private void stopLoading() {
        if (loadingDialog != null && loadingDialog.isShowing()) {
            loadingDialog.dismiss();
        }
    }

    private void lookWordAi(String word) {
        Log.e(TAG, "getWordAi for word " + word);
        wordApi.getWordAi(word, TextAttr.encode(userPron), TextAttr.encode(wordPron), App.APP_ID, uid).enqueue(new Callback<WordAiEntity>() {
            @Override
            public void onResponse(Call<WordAiEntity> call, Response<WordAiEntity> response) {
                if ((response != null) && (response.body() != null)) {
                    wordEntity = response.body();
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

            @Override
            public void onFailure(Call<WordAiEntity> call, Throwable t) {
                if (t != null) {
                    Log.e(TAG, "getWordAi onFailure " + t.getMessage());
                }
                handler.sendEmptyMessage(100);
            }
        });
    }

    private void sendEvaluate() {
        isEvaluating = true;
        Map<String, RequestBody> map = new HashMap<>();
        map.put(EvaluateApi.GetVoa.Param.Key.SENTENCE, HttpManager.fromString(titleWord));
        map.put(EvaluateApi.GetVoa.Param.Key.IDINDEX, HttpManager.fromString(voaText.idIndex() + ""));
        map.put(EvaluateApi.GetVoa.Param.Key.NEWSID, HttpManager.fromString(voaText.getVoaId() + ""));
        map.put(EvaluateApi.GetVoa.Param.Key.PARAID, HttpManager.fromString(voaText.paraId() + ""));
        map.put(EvaluateApi.GetVoa.Param.Key.TYPE, HttpManager.fromString(Constant.EVAL_TYPE));
        map.put(EvaluateApi.GetVoa.Param.Key.USERID, HttpManager.fromString(uid + ""));
        map.put("appId", HttpManager.fromString(App.APP_ID + ""));
        map.put("wordId", HttpManager.fromString(wordId + ""));
        map.put("flg", HttpManager.fromString("2"));

        //改成单词评测接口
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

    public void setUid(int ud) {
        uid = ud;
    }

    public void setDataBean(EvaluateBean detail) {
        evaluateBean = detail;
        if ((evaluateBean != null) && (evaluateBean.getWords() != null)) {
            boolean flag = false;
            List<EvaluateBean.WordsBean> wordBeans = evaluateBean.getWords();
            for (int index = 0; index < wordBeans.size(); index++) {
                EvaluateBean.WordsBean word = wordBeans.get(index);
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
            Log.e(TAG, "setDataBean evaluateBean is null ");
        }
    }

    public void setVoaText(VoaText detail) {
        voaText = detail;
    }
}
