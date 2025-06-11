package com.iyuba.wordtest.ui.test;

import android.animation.Animator;
import android.animation.AnimatorSet;
import android.animation.ObjectAnimator;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.os.Handler;
import android.os.HandlerThread;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.CycleInterpolator;
import android.view.animation.TranslateAnimation;
import android.widget.CompoundButton;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;

import com.iyuba.module.toolbox.MD5;
import com.iyuba.wordtest.R;
import com.iyuba.wordtest.bean.UploadExamResult;
import com.iyuba.wordtest.bean.UploadTestBean;
import com.iyuba.wordtest.databinding.ActivityWordTestBinding;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.db.WordOp;
import com.iyuba.wordtest.entity.BookLevels;
import com.iyuba.wordtest.entity.NewBookLevels;
import com.iyuba.wordtest.entity.TalkShowTests;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.entity.WordEntity;
import com.iyuba.wordtest.event.WordTestEvent;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.network.HttpManager;
import com.iyuba.wordtest.ui.TalkshowWordListActivity;
import com.iyuba.wordtest.utils.TextAttr;
import com.iyuba.wordtest.utils.ToastUtil;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;
import java.util.ListIterator;
import java.util.Objects;
import java.util.Random;

import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

/**
 * 单词闯关功能
 */
public class WordTestActivity extends AppCompatActivity {

    private final String playurl = "http://dict.youdao.com/dictvoice?audio=";

    MediaPlayer player;
    private int step;
    private int bookid;
    private int unit;
    private final SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd");
    private String date;
    private HandlerThread mHandlerThread;
    private Handler mSubHandler;
    private WordOp wordOp;

    public static void start(Context context, int level) {
        Intent starter = new Intent(context, WordTestActivity.class);
        starter.putExtra("level", level);
        context.startActivity(starter);
    }
    public static void start(Context context, int bookId , int unitId, int pos) {
        Intent starter = new Intent(context, WordTestActivity.class);
        starter.putExtra(TalkshowWordListActivity.UNIT, unitId);
        starter.putExtra(TalkshowWordListActivity.BOOKID, bookId);
        starter.putExtra(TalkshowWordListActivity.STEP, pos);
        context.startActivity(starter);
    }


    TextView right;
    private int unitTotal = 0;
    private List<TalkShowWords> words = new ArrayList<>();
    private List<String> wronganswers = new ArrayList<>();
    TalkShowWords talkShowWords;
    private  WordDataBase db;
    private TextView[] tvs;
    private int wrong = 0;
    private final List<UploadTestBean.TestListBean> testLists  = new ArrayList<>();

    ObjectAnimator animator;
    ObjectAnimator animator1;
    AnimatorSet animatorSet = new AnimatorSet();
    int position;
    boolean isCheckable;
    ActivityWordTestBinding binding ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
//        setContentView(R.layout.activity_word_test);
//        ButterKnife.bind(this);
        binding=ActivityWordTestBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        initActionBar();
    }
    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        wordOp = new WordOp(getApplicationContext());
        getDatas();
        date= format.format(new Date());
        initPlayer();
        initAnswerTv();
        initAnimator();
        initClick();
        initData(0, true);
        binding.cb.setOnCheckedChangeListener(listener);
        mHandlerThread = new HandlerThread("WordTest");
        mHandlerThread.start();
        mSubHandler = new Handler(mHandlerThread.getLooper());

        //隐藏继续答题按钮
        binding.loadLast.setVisibility(View.INVISIBLE);
        //根据数量判断
        showTestResult();
    }

    private void getDatas() {
        db = WordDataBase.getInstance(getApplicationContext());
//        level = getIntent().getIntExtra("level", 1);
        bookid  = getIntent().getIntExtra(TalkshowWordListActivity.BOOKID,0);
        unit  = getIntent().getIntExtra(TalkshowWordListActivity.UNIT,1);
        step = getIntent().getIntExtra(TalkshowWordListActivity.STEP,0);
        words = db.getTalkShowWordsDao().getUnitWords( bookid ,unit);
        Collections.shuffle(words);
        wronganswers = db.getTalkShowWordsDao().getWords();
//        Log.e("WordTestActivity", "onCreate bookid " + bookid);
//        Log.e("WordTestActivity", "onCreate unit " + unit);
        if ((words != null)) {
            binding.textTinyHint.setText((position + 1) + "/" + words.size());
            unitTotal = words.size();
        }
    }

    CompoundButton.OnCheckedChangeListener listener = new CompoundButton.OnCheckedChangeListener() {
        @Override
        public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
            if (isChecked) {
//                talkShowWords.flag = 1;
                if (wordOp.isFavorWord(talkShowWords.word, talkShowWords.voa_id, Integer.parseInt(WordManager.getInstance().userid))) {
                    return;
                }
                mSubHandler.post(new Runnable() {
                    @Override
                    public void run() {
                        WordEntity wordEntity = new WordEntity();
                        wordEntity.key = talkShowWords.word;
                        wordEntity.audio = talkShowWords.audio;
                        wordEntity.pron = talkShowWords.pron;
                        wordEntity.def = talkShowWords.def;
                        wordEntity.unit = talkShowWords.unit_id;
                        wordEntity.book = talkShowWords.book_id;
                        wordEntity.voa = talkShowWords.voa_id;
                        wordOp.insertWord(wordEntity, Integer.parseInt(WordManager.getInstance().userid));
                    }
                });
                addNetwordWord(talkShowWords.word);
            } else {
//                talkShowWords.flag = 0;
                if (!wordOp.isFavorWord(talkShowWords.word, talkShowWords.voa_id, Integer.parseInt(WordManager.getInstance().userid))) {
                    return;
                }
                deleteNetWord(talkShowWords.word);
            }
        }
    };

    private void initActionBar() {
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }

    private void initAnswerTv() {
        tvs = new TextView[]{binding.answera, binding.answerb, binding.answerc, binding.answerd};
    }

    private void initAnimator() {
        animator = ObjectAnimator.ofFloat(binding.ll, "translationY", -200, 0);
        animator1 = ObjectAnimator.ofFloat(binding.ll, "alpha", 0.2f, 1f);
        animatorSet.playTogether(animator, animator1);
        animatorSet.setDuration(600);
    }

    private void initPlayer() {
        player = new MediaPlayer();
        player.setOnPreparedListener(onPreparedListener);
    }

    MediaPlayer.OnPreparedListener onPreparedListener = new MediaPlayer.OnPreparedListener() {
        @Override
        public void onPrepared(MediaPlayer mp) {
//            player.start();
        }
    };

    private void initData(int pos, boolean reset) {
        position = pos;
        try {
            talkShowWords = words.get(pos);
            binding.textTinyHint.setText((position + 1) + "/" + words.size());
            setData(talkShowWords);
            if (reset) {
                wrong = 0;
            }
        } catch (Exception e) {
            finish();
            e.printStackTrace();
        }

    }

    private void setData(TalkShowWords words) {
        isCheckable = true;
        animatorSet.start();
        binding.word.setText(words.def);
        for (TextView textView : tvs) {
            textView.setBackground(getResources().getDrawable(R.drawable.wordtest_rect_default1));
            textView.setTextColor(Color.parseColor("#333333"));
        }
        int random = new Random().nextInt(100) % 4;
//        Log.d("com.iyuba.talkshow", random + "");
        fillinAnswers(tvs[random],words.word);
        binding.jiexi.setVisibility(View.GONE);
        binding.nextButton.setVisibility(View.GONE);
        playWord(words.word);
    }

    private void playWord(String word) {
        try {
            player.reset();
            player.setDataSource(playurl + word);
            player.prepareAsync();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }


    private void fillinAnswers(TextView textView, String word) {
        textView.setText(word);
        right = textView;
        Log.e("WordTestActivity", "fillinAnswers word " + word);
        for (TextView tv : tvs) {
            if (tv != textView) {
                int random = new Random().nextInt(wronganswers.size() - 1);
                Log.e("WordTestActivity", "fillinAnswers wronganswers.get(random) " + wronganswers.get(random));
                tv.setText(wronganswers.get(random));
                if (wronganswers.get(random).equals(word)) {
                    fillinAnswers(textView, word);
                }
            }
        }
    }

    private void initClick() {
        binding.loadLast.setOnClickListener(v -> onViewClicked(v));
        binding.jiexi.setOnClickListener(v -> onViewClicked(v));
        binding.jiexiClose.setOnClickListener(v -> binding.jiexiRoot.setVisibility(View.GONE));
        binding.nextButton.setOnClickListener(v -> onViewClicked(v));
        binding.answera.setOnClickListener(v -> onViewClicked(v));
        binding.answerb.setOnClickListener(v -> onViewClicked(v));
        binding.answerc.setOnClickListener(v -> onViewClicked(v));
        binding.answerd.setOnClickListener(v -> onViewClicked(v));
        binding.ll.setOnClickListener(v -> onViewClicked(v));

        binding.retryTest.setOnClickListener(v->{
            //重新闯关

            //刷新显示
            updateUI(false);
            //重置数据操作
            retryWordTest();
        });
    }

    @SuppressLint("CheckResult")
//    @OnClick({R2.id.jiexi, R2.id.next_button, R2.id.answera, R2.id.answerb, R2.id.answerc, R2.id.answerd, R2.id.ll})
    public void onViewClicked(View view) {
//        if (binding.jiexiRoot.getVisibility() == View.VISIBLE) {
//            binding.jiexiRoot.setVisibility(View.GONE);
//            if (view.getId() == R.id.next_button)
//                initData(++position, false);
//            return;
//        }
        int id = view.getId();
        if (id == R.id.load_last) {
            Log.e("WordTestActivity", "getUnitWords unitTotal " + unitTotal);
            List<TalkShowTests> answerWords = db.getTalkShowTestsDao().getAnswerWords(bookid, unit, "1", WordManager.getInstance().userid);
            if ((answerWords != null) && (answerWords.size() > 0)) {
                Log.e("WordTestActivity", "getUnitWords answerWords " + answerWords.size());
                if (answerWords.size() >= unitTotal) {
                    int sumWrong = 0;
                    List<TalkShowTests> wrongWords = db.getTalkShowTestsDao().getWrongWords(bookid, unit, 0, WordManager.getInstance().userid,"1");
                    if (wrongWords != null) {
                        sumWrong = wrongWords.size() * 100 / unitTotal;
                    }
                    new AlertDialog.Builder(this)
                            .setMessage(String.format("您已经回答完了本关%s道题,错误率%s,请选择退出还是重新闯关.", unitTotal, sumWrong + "%"))
                            .setPositiveButton("退出", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    finish();
                                }
                            })
                            .setNegativeButton("重新闯关", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    dialog.dismiss();
                                    retryWordTest();
                                }
                            })
                            .setCancelable(false)
                            .create().show();
                } else {
                    ToastUtil.showToast(WordTestActivity.this, "您已经回答了" + answerWords.size() + "道题，只需要回答剩下的" + (unitTotal-answerWords.size()) + "道题即可。");
                    ListIterator<TalkShowWords> iter = words.listIterator();
                    TalkShowWords tempWord;
                    while (iter.hasNext()) {
                        tempWord = iter.next();
                        for (TalkShowTests tests: answerWords) {
                            if ((tempWord != null) && (tests != null) && (tempWord.book_id == tests.book_id)
                                    && (tempWord.unit_id == tests.unit_id)  && (tempWord.position == tests.position)) {
                                iter.remove();
                                break;
                            }
                        }
                    }
                    initData(0, true);
                    Log.e("WordTestActivity", "getUnitWords for left " + words.size());
                }
            } else {
                Log.e("WordTestActivity", "getUnitWords for left is null? ");
                ToastUtil.showToast(WordTestActivity.this, "您暂时没有答题记录，继续答题吧。");
            }
        } else if (id == R.id.jiexi) {
            showJiexiView();
        } else if (id == R.id.next_button) {
            if (binding.jiexiRoot.getVisibility() == View.VISIBLE) {
                binding.jiexiRoot.setVisibility(View.GONE);
            }
            if (position == (words.size() - 1)) {
                List<TalkShowTests> wrongWords = db.getTalkShowTestsDao().getWrongWords(bookid, unit, 0, WordManager.getInstance().userid,"1");
                if (wrongWords != null) {
                    wrong = wrongWords.size();
                }
                if (wrong * 100 / unitTotal > 20) {
                    showFailDialog();
                } else {
                    showSuccessDialog();
                }
            } else {
                initData(++position, false);
                if (words != null) {
                    binding.textTinyHint.setText((position + 1) + "/" + words.size());
                }
            }
        } else if (id == R.id.answera || id == R.id.answerb || id == R.id.answerc || id == R.id.answerd) {
            if (!isCheckable) return;
            isCheckable = false;
            binding.jiexi.setVisibility(View.VISIBLE);
            if (position < (words.size() - 1)) {
                binding.nextButton.setText(R.string.word_pass_next);
            } else {
                binding.nextButton.setText(R.string.word_pass_ok);
            }
            binding.nextButton.setVisibility(View.VISIBLE);
            if (view == right) {
                view.setBackground(getResources().getDrawable(R.drawable.answer_right));
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                talkShowWords.wrong = 1;
                buildBean(1);
//                if (position == (words.size() - 1)) {
//                    if (wrong * 100 / words.size() > 20) {
//                        showFailDialog();
//                    } else {
//                        showSuccessDialog();
//                    }
//                }

            } else {
                view.setBackground(getResources().getDrawable(R.drawable.answer_wrong));
                ((TextView) view).setTextColor(getResources().getColor(R.color.white));
                view.startAnimation(shakeAnimation(1));
                buildBean(0);
                talkShowWords.wrong = 0;
                wrong++;
//                if (position == words.size() - 1) {
//                    if (wrong * 100 / words.size() > 20) {
//                        showFailDialog();
//                    } else {
//                        showSuccessDialog();
//                    }
//                }
            }
            talkShowWords.answer = "1";
            mSubHandler.post(new Runnable() {
                @Override
                public void run() {
                    int result = db.getTalkShowWordsDao().updateSingleWord(talkShowWords);
//                    Log.e("WordTestActivity", "updateSingleWord result " + result);
                    TalkShowTests talkTest = db.getTalkShowTestsDao().getUnitWord(talkShowWords.book_id, talkShowWords.unit_id, talkShowWords.position, WordManager.getInstance().userid);
                    if ((talkTest == null)) {
                        long[] update = db.getTalkShowTestsDao().insertWord(WordManager.getInstance().Words2Tests(talkShowWords));
                        Log.e("WordTestActivity", "updateTestWord update " + update[0]);
                    } else {
                        int update = db.getTalkShowTestsDao().updateSingleWord(WordManager.getInstance().Words2Tests(talkShowWords));
                        Log.e("WordTestActivity", "syncExamWord updateSingleWord update " + update);
                    }
                }
            });
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (mSubHandler != null) {
            mSubHandler.removeCallbacks(null);
            mSubHandler = null;
        }
        if (mHandlerThread != null) {
            mHandlerThread.quit();
            mHandlerThread = null;
        }
    }

    private void buildBean(int type) {
        UploadTestBean.TestListBean bean = new UploadTestBean.TestListBean();
        bean.AnswerResut = type ;
        bean.BeginTime = date;
        bean.Category = "单词闯关";
        bean.LessonId = "" + talkShowWords.unit_id ;
        bean.RightAnswer = right.getText().toString();
        bean.TestId = talkShowWords.position ;
        bean.TestMode = "W";
        bean.TestTime = date;
        bean.UserAnswer = right.getText().toString();
        testLists.add(bean);
    }

    private void showJiexiView() {
        if (android.os.Build.VERSION.SDK_INT >= android.os.Build.VERSION_CODES.LOLLIPOP) {
            binding.jiexiRoot.setVisibility(View.VISIBLE);
            binding.jiexiDef.setText(talkShowWords.def);
            if (!TextUtils.isEmpty(talkShowWords.pron)) {
                if (!talkShowWords.pron.startsWith("[")){
                    binding.jiexiPron.setText(String.format("[%s]", TextAttr.decode(talkShowWords.pron)));
                }else {
                    binding.jiexiPron.setText(String.format("%s", TextAttr.decode(talkShowWords.pron)));
                }
            } else {
                binding.jiexiPron.setText("");
            }
            binding.jiexiWord.setText(talkShowWords.word);
            binding.cb.setChecked((wordOp != null) && wordOp.isFavorWord(talkShowWords.word, talkShowWords.voa_id, Integer.parseInt(WordManager.getInstance().userid)));
            Animator animator = ObjectAnimator.ofFloat(binding.jiexiRoot, "scaleX", 0.1f, 1f);
            animator.setDuration(600);
            animator.start();
        } else {
            binding.jiexiRoot.setVisibility(View.VISIBLE);
            binding.jiexiDef.setText(talkShowWords.def);
            if (!talkShowWords.pron.startsWith("[")){
                binding.jiexiPron.setText(String.format("[%s]", TextAttr.decode(talkShowWords.pron)));
            }else {
                binding.jiexiPron.setText(String.format("%s", TextAttr.decode(talkShowWords.pron)));
            }
            binding.jiexiWord.setText(talkShowWords.word);
        }
    }

    private void retryWordTest() {
        //将测试数据重置为0
        words = db.getTalkShowWordsDao().getUnitWords(bookid ,unit);
        if (words!=null&&words.size()>0){
            for (int i = 0; i < words.size(); i++) {
                TalkShowWords tempWord = words.get(i);
                tempWord.answer = "0";
                tempWord.wrong = 0;

                TalkShowTests talkTest = db.getTalkShowTestsDao().getUnitWord(tempWord.book_id, tempWord.unit_id, tempWord.position, WordManager.getInstance().userid);
                if ((talkTest == null)) {
                    long[] update = db.getTalkShowTestsDao().insertWord(WordManager.getInstance().Words2Tests(tempWord));
                } else {
                    int update = db.getTalkShowTestsDao().updateSingleWord(WordManager.getInstance().Words2Tests(tempWord));
                }
            }
        }

        words = db.getTalkShowWordsDao().getUnitWords( bookid ,unit);
        for (TalkShowWords word: words) {
            if (word != null) {
                word.answer = "";
                word.wrong = 0;
            }
        }
        Collections.shuffle(words);
        initData(0, true);

        //刷新数据
        EventBus.getDefault().post(new WordTestEvent(bookid));
    }

    private void showFailDialog() {
        uploadWordResult();
        String c = String.format("闯关失败,回答%s题,答错%s题,错误率%s,是否重新闯关?", unitTotal, wrong, wrong * 100 / unitTotal + "%");
        new AlertDialog.Builder(this).setMessage(c)
                .setPositiveButton("重新闯关", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        retryWordTest();
                    }
                })
                .setNegativeButton("再学一会儿", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        dialog.dismiss();
                        finish();

                        EventBus.getDefault().post(new WordTestEvent(bookid));
                    }
                })
                .setCancelable(false)
                .create()
                .show();
    }

    private void showSuccessDialog() {
        mSubHandler.post(new Runnable() {
            @Override
            public void run() {
                saveWordLevel();
            }
        });
        uploadWordResult();
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("恭喜您成功完成单词闯关!");
        builder.setPositiveButton("好的", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                dialog.dismiss();
                finish();

                EventBus.getDefault().post(new WordTestEvent(bookid));
            }
        });
        builder.setCancelable(false);
        builder.show();
    }

    @SuppressLint("CheckResult")
    private void uploadWordResult() {
        UploadTestBean bean = getUploadTestBean();
        RequestBody  multipart = HttpManager.getBody( bean);
        HttpManager.getUploadExamApi().uploadWordExamNew(multipart)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<UploadExamResult>() {
                    @Override
                    public void accept(UploadExamResult uploadExamResult) {
                        if (uploadExamResult == null) {
                            Log.e("WordTestActivity", "uploadWordResult result is null.");
                        } else {
                            ToastUtil.showToast(WordTestActivity.this, getString(R.string.alert_upload_exam_success));
                            Log.e("WordTestActivity", "uploadWordResult result " + uploadExamResult.getResult());
                        }
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable)  {
                        if (throwable != null) {
                            Log.e("WordTestActivity", "uploadWordResult throwable " + throwable.getMessage());
                        }
                    }
                });
    }

    private UploadTestBean getUploadTestBean() {

        UploadTestBean bean = new UploadTestBean() ;
        bean.testList = testLists ;
        bean.sign = buildSign();
        bean.appId = WordManager.getInstance().appid;
        try {
            //去掉获取mac地址操作
//            GetDeviceInfo getDeviceInfo = new GetDeviceInfo(getApplicationContext());
//            bean.DeviceId = getDeviceInfo.getLocalMACAddress();
            bean.DeviceId = "";

        } catch (Exception e) {
            e.printStackTrace();
            bean.DeviceId = "";
        }
        bean.format = "json";
        bean.lesson = "" + bookid;
        bean.mode = 2;
        bean.uid= WordManager.getInstance().userid;
        bean.scoreList = new ArrayList<>() ;
        return bean ;
    }

    private String buildSign() {
       /* 用户id+你的应用id+统计类别+iyubaExam+当前时间戳*/
        String sign = WordManager.getInstance().userid + WordManager.getInstance().appid +
                bookid + "iyubaExam" + format.format(System.currentTimeMillis());
        Log.e("WordTestActivity", "uploadWordResult sign text: " + sign);
        return MD5.getMD5ofStr(sign).toLowerCase();
    }

    // 更新单词关卡
    private void saveWordLevel() {
        BookLevels levels =  db.getBookLevelDao().getBookLevel(bookid) ;
        if (WordManager.WordDataVersion == 2) {
            mSubHandler.post(new Runnable() {
                @Override
                public void run() {
                    NewBookLevels newBookLevel = db.getNewBookLevelDao().getBookLevel(bookid, WordManager.getInstance().userid);
                    if (newBookLevel == null) {
                        newBookLevel = new NewBookLevels(bookid, 1 ,0 ,0, WordManager.getInstance().userid);
                        Log.e("WordTestActivity", "saveWordLevel newBookLevel = " + newBookLevel.level);
                        db.getNewBookLevelDao().saveBookLevel(newBookLevel);
                    } else {
                        if (newBookLevel.level > step) {
                            Log.e("WordTestActivity", "newBookLevel = " + newBookLevel.level +" is larger than step " + step);
                        } else {
                            newBookLevel.level++;
                            Log.e("WordTestActivity", "updateBookLevel newBookLevel = " + newBookLevel.level);
                            db.getNewBookLevelDao().updateBookLevel(newBookLevel);
                        }
                    }
                    EventBus.getDefault().post(new WordTestEvent(bookid));
                }
            });
        } else
        if (levels != null) {
            levels.level++;
            Log.e("WordTestActivity", "saveWordLevel levels.level = " + levels.level);
            db.getBookLevelDao().updateBookLevel(levels);
        }
    }

    public Animation shakeAnimation(int counts) {
        Animation translate = new TranslateAnimation(0, 18, 0, 0);
        translate.setInterpolator(new CycleInterpolator(counts));
        translate.setRepeatCount(1);
        translate.setDuration(100);
        return translate;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        if (binding.jiexiRoot.getVisibility() == View.VISIBLE) {
            binding.jiexiRoot.setVisibility(View.GONE);
        } else {
            new AlertDialog.Builder(this).setMessage("您正在进行闯关,确定要退出吗?").setPositiveButton("退出", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {

                    //刷新显示
                    EventBus.getDefault().post(new WordTestEvent(bookid));
                    //关闭弹窗
                    dialog.dismiss();
                    finish();
                }
            }).setNegativeButton("取消", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                }
            }).create().show();
        }
    }

    private void deleteNetWord(String word) {
        HttpManager.getWordApi().operateWord(word,"delete",
                "Iyuba", WordManager.getInstance().userid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                        wordOp.deleteWord(word, Integer.parseInt(WordManager.getInstance().userid));
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable)  {
                        if (throwable != null) {
                            Log.e("WordTestActivity", "addNetWord throwable " + throwable.getMessage());
                        }
                    }
                });
    }

    private void addNetwordWord(String wordTemp) {
        HttpManager.getWordApi().operateWord(wordTemp,"insert",
                "Iyuba", WordManager.getInstance().userid)
                .observeOn(AndroidSchedulers.mainThread())
                .subscribeOn(Schedulers.io())
                .subscribe(new Consumer<String>() {
                    @Override
                    public void accept(String s) {
                    }
                }, new Consumer<Throwable>() {
                    @Override
                    public void accept(Throwable throwable)  {
                        if (throwable != null) {
                            Log.e("WordTestActivity", "addNetWord throwable " + throwable.getMessage());
                        }
                        wordOp.deleteWord(wordTemp, Integer.parseInt(WordManager.getInstance().userid));
                    }
                });
    }

    /*******************************新增继续答题逻辑**************************/
    //优化显示继续答题逻辑
    private void showNextQuestion(){
        //查询数据并使用弹窗显示
        List<TalkShowTests> answerWords = db.getTalkShowTestsDao().getAnswerWords(bookid, unit, "1", WordManager.getInstance().userid);
        if (answerWords!=null&&answerWords.size()>0){
            //剩余的题目数量
            int lastQuestionCount = words.size() - answerWords.size();
            if (lastQuestionCount>0){
                List<TalkShowTests> wrongWords = db.getTalkShowTestsDao().getWrongWords(bookid, unit, 0, WordManager.getInstance().userid,"1");

                //错误率
                int sumWrong = 0;
                //每个的数量
                int errorNum = wrongWords.size();
                int curNum = answerWords!=null?answerWords.size():0;
                int rightNum = curNum - errorNum;
//                if (wrongWords != null) {
//                    sumWrong = wrongWords.size() * 100 / unitTotal;
//                }
                //这里认为用当前的错误数量/当前完成数量好一些
                if (errorNum != 0) {
                    sumWrong = errorNum * 100 / curNum;
                }

                new AlertDialog.Builder(this)
                        .setMessage(String.format("您已经回答完了本关%s道题\n正确题数：%s\n错误题数：%s\n当前错误率%s\n请选择继续闯关还是重新闯关", curNum,rightNum+"个",errorNum+"个", sumWrong + "%"))
                        .setPositiveButton("继续闯关", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                ListIterator<TalkShowWords> iter = words.listIterator();
                                TalkShowWords tempWord;
                                while (iter.hasNext()) {
                                    tempWord = iter.next();
                                    for (TalkShowTests tests: answerWords) {
                                        if ((tempWord != null) && (tests != null) && (tempWord.book_id == tests.book_id)
                                                && (tempWord.unit_id == tests.unit_id)  && (tempWord.position == tests.position)) {
                                            iter.remove();
                                            break;
                                        }
                                    }
                                }
                                initData(0, true);

                                dialog.dismiss();
                            }
                        })
                        .setNegativeButton("重新闯关", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                                retryWordTest();
                            }
                        })
                        .setCancelable(false)
                        .create().show();
            }
        }
    }

    //优化显示结果逻辑
    private void showTestResult(){
        //闯关的数据
        List<TalkShowTests> answerWords = db.getTalkShowTestsDao().getAnswerWords(bookid, unit, "1", WordManager.getInstance().userid);
        int curTestCount = answerWords!=null?answerWords.size():0;
        //单词数量
        int allWordCount = words.size();
        //计算正确和错误数量
        List<TalkShowTests> wrongWords = db.getTalkShowTestsDao().getWrongWords(bookid, unit, 0, WordManager.getInstance().userid,"1");
        int wrongTestCount = wrongWords!=null?wrongWords.size():0;
        int rightTestCount = curTestCount-wrongTestCount;

        if (curTestCount>=allWordCount){
            updateUI(true);
            binding.textTinyHint.setText("单词闯关");

            //错误率
            int wrongRate = wrongTestCount*100/curTestCount;
            binding.rightCount.setText("正确数量："+rightTestCount);
            binding.wrongCount.setText("错误数量："+wrongTestCount);
            binding.wrongRate.setText("错误率："+wrongRate+"%");
        }else {
            updateUI(false);

            showNextQuestion();
        }
    }

    //刷新ui显示
    private void updateUI(boolean isFinish){
        if (isFinish){
            binding.resultLayout.setVisibility(View.VISIBLE);
        }else {
            binding.resultLayout.setVisibility(View.GONE);
        }
    }
}
