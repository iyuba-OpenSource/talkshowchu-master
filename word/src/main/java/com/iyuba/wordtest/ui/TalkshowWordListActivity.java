package com.iyuba.wordtest.ui;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.res.ColorStateList;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.LinearLayoutManager;

import com.iyuba.wordtest.BuildConfig;
import com.iyuba.wordtest.R;
import com.iyuba.wordtest.adapter.SimpleTalkshowAdapter;
import com.iyuba.wordtest.databinding.ActivityWordListBinding;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.BookLevels;
import com.iyuba.wordtest.entity.NewBookLevels;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.event.ToDetailEvent;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.ui.detail.WordDetailActivity;
import com.iyuba.wordtest.ui.listen.WordLibrary;
import com.iyuba.wordtest.ui.listen.WordListenActivity;
import com.iyuba.wordtest.ui.test.WordClearActivity;
import com.iyuba.wordtest.ui.test.WordTestActivity;
import com.iyuba.wordtest.utils.MediaUtils;
import com.iyuba.wordtest.utils.StorageUtil;
import com.iyuba.wordtest.utils.ToastUtil;
import com.iyuba.wordtest.widget.LoadingDialog;
import com.jaeger.library.StatusBarUtil;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


public class TalkshowWordListActivity extends AppCompatActivity implements WordListMvpView {

    public static String BOOKID = "BOOKID";
    public static String UNIT = "UNIT";
    public static String STEP = "STEP";//当前位置
    public static final String IS_UNLOCK = "isUnLock";

    int bookId;
    int unit;
    int step;
    boolean isUnLock = false;

    private Context context;
    private WordDataBase db;
    SimpleTalkshowAdapter adapter;
    WordListPresenter presenter;

    //增加加载弹窗
    private LoadingDialog loadingDialog;
    //播放器
    private MediaPlayer player;

    private final List<TalkShowWords> list = new ArrayList<>();
    ActivityWordListBinding binding;

    public static void start(Context context, int book_id, int unit, int pos, boolean isUnLock) {
        Intent intent = new Intent();
        intent.setClass(context, TalkshowWordListActivity.class);
        intent.putExtra(BOOKID, book_id);
        intent.putExtra(UNIT, unit);
        intent.putExtra(STEP, pos);
        intent.putExtra(IS_UNLOCK, isUnLock);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary), 0);
        context = this;
        presenter = new WordListPresenter();
        presenter.attachView(this);
        EventBus.getDefault().register(this);
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);

        loadingDialog = new LoadingDialog(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        db = WordDataBase.getInstance(this);
        initWords();
        adapter = new SimpleTalkshowAdapter(list);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        binding.recyclerView.setAdapter(adapter);
        binding.recyclerView.setItemViewCacheSize(list.size());
        adapter.setOnWordItemClickListener(new SimpleTalkshowAdapter.OnWordItemClickListener() {
            @Override
            public void onPlayAudio(String audioUrl) {
                startPlay(audioUrl);
            }
        });

        binding.sidebar.setVisibility(View.GONE);
        setClick();

        initPlayer();
    }

    private void setClick() {
        binding.syncWord.setOnClickListener(v -> onViewClicked(v));
        binding.study.setOnClickListener(v -> onViewClicked(v));
        binding.test.setOnClickListener(v -> onViewClicked(v));
        binding.listen.setOnClickListener(v -> onViewClicked(v));
    }

    /***********************音频****************/
    private void initPlayer() {
        player = new MediaPlayer();
        player.setOnPreparedListener(new MediaPlayer.OnPreparedListener() {
            @Override
            public void onPrepared(MediaPlayer mp) {
                mp.start();
            }
        });
        player.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
            @Override
            public void onCompletion(MediaPlayer mp) {
                mp.stop();
            }
        });
        player.setOnErrorListener(new MediaPlayer.OnErrorListener() {
            @Override
            public boolean onError(MediaPlayer mp, int what, int extra) {
                ToastUtil.showToast(TalkshowWordListActivity.this, "初始化音频失败，请重试");
                return true;
            }
        });
    }

    //播放音频
    private void startPlay(String audioUrl) {
        if (TextUtils.isEmpty(audioUrl)) {
            ToastUtil.showToast(this, "暂无该单词音频");
            return;
        }

        try {
            pausePlay();
            player.reset();

            String dir = StorageUtil.getWordDir(this).getAbsolutePath();
            File file = new File(dir, StorageUtil.getWordName(audioUrl));
            String worDir = StorageUtil.getWordDir(this).getAbsolutePath() + "/primary_audio/" + StorageUtil.getWordName(audioUrl);
            File wordFile = new File(worDir);
            if (file.exists() && file.isFile()) {
                this.player.setDataSource(file.getAbsolutePath());
            } else if (wordFile.exists() && wordFile.isFile()) {
                this.player.setDataSource(worDir);
            } else {
                if (MediaUtils.isConnected(getApplicationContext())) {
                    this.player.setDataSource(audioUrl);
                } else {
                    showText("暂时没有这个单词的音频，请打开数据网络播放。");
                    return;
                }
            }

            player.prepare();
        } catch (Exception e) {
            ToastUtil.showToast(this, "音频播放失败，请重试");
        }
    }

    //暂停音频
    private void pausePlay() {
        if (player != null && player.isPlaying()) {
            player.pause();
        }
    }

    private void initWords() {
        bookId = getIntent().getIntExtra(BOOKID, 217);
        unit = getIntent().getIntExtra(UNIT, 1);
        step = getIntent().getIntExtra(STEP, 0);

        isUnLock = getIntent().getBooleanExtra(IS_UNLOCK, true);

        ColorStateList stateList = ColorStateList.valueOf(getResources().getColor(R.color.colorPrimary));
        if (!isUnLock) {
            stateList = ColorStateList.valueOf(getResources().getColor(android.R.color.darker_gray));
        }
        binding.test.setBackgroundTintList(stateList);

        //根据bookId显示名称
        String title = "Unit " + unit + " 单词";
        if ((450 <= bookId) && (bookId <= 457)) {
            title = "Lesson " + unit + " 单词";
        }
        binding.textTopTitle.setText(title);

        list.clear();
        list.addAll(db.getTalkShowWordsDao().getUnitWords(bookId, unit));
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.study) {
            WordDetailActivity.start(context, list, 0, bookId, unit);
        } else if (id == R.id.sync_word) {
            if (!MediaUtils.isConnected(getApplicationContext())) {
                ToastUtil.showToast(context, "请检查数据网络是否可用！");
                return;
            }

            //显示弹窗
            if (loadingDialog != null && !loadingDialog.isShowing()) {
                loadingDialog.show();
            }

            if (WordManager.WordDataVersion == 2) {
                NewBookLevels newLevels = db.getNewBookLevelDao().getBookLevel(bookId, WordManager.getInstance().userid);
                if (newLevels == null) {
                    presenter.refreshWords(getApplicationContext(), bookId, 0);
                } else {
                    presenter.refreshWords(getApplicationContext(), bookId, newLevels.version);
                }
            } else {
                BookLevels bookLevel = db.getBookLevelDao().getBookLevel(bookId);
                presenter.refreshWords(getApplicationContext(), bookId, bookLevel.version);
            }
        } else if (id == R.id.test) {
            //闯关功能
            if (!isUnLock) {
                ToastUtil.showToast(this, "通关前面的单元后解锁此单元的闯关内容");
                return;
            }

            showTopicType();
        } else if (id == R.id.listen) {
            //弹窗显示选项
            showTrainType();
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onResume() {
        super.onResume();
//        initWords();
//        adapter.notifyDataSetChanged();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(ToDetailEvent event) {
        Log.e("TalkshowWordActivity", "ToDetailEvent bookId " + event.bookid);
        refreshWords();
    }

    @Override
    public void showText(String text) {
        if (loadingDialog != null) {
            loadingDialog.dismiss();
        }
        ToastUtil.showToast(context, text);
    }

    @Override
    public void refreshWords() {
        list.clear();
        list.addAll(db.getTalkShowWordsDao().getUnitWords(bookId, unit));
        adapter.notifyDataSetChanged();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        presenter.detachView();
        EventBus.getDefault().unregister(this);
    }

    //训练题型显示
    private void showTrainType() {
        String[] listenArray = new String[]{"单词拼写", "单词手写"};//音频听写
        new AlertDialog.Builder(this)
                .setTitle("听写类型")
                .setItems(listenArray, (dialog, index) -> {
                    dialog.dismiss();

                    String listenType = "";
                    switch (index) {
                        case 0:
                            //单词拼写
                            listenType = WordLibrary.WordListenType.Listen_word;
                            break;
//                        case 1:
//                            //音频听写
//                            listenType = WordLibrary.WordListenType.Listen_audio;
//                            break;
                        case 1:
                            //单词手写
                            listenType = WordLibrary.WordListenType.Write_word;
                            break;
                        default:
                            listenType = WordLibrary.WordListenType.Listen_word;
                            break;
                    }

                    WordListenActivity.start(context, listenType, bookId, unit);
                }).create().show();
    }

    //闯关题型显示
    private AlertDialog topicDialog;

    private void showTopicType() {
        if (topicDialog == null) {
            String[] topicTypeStr = new String[]{"单词闯关", "单词消消乐"};
            topicDialog = new AlertDialog.Builder(this)
                    .setTitle("选择题型")
                    .setItems(topicTypeStr, (dialog, which) -> {
                        String topicType = topicTypeStr[which];

                        if (topicType.equals("单词闯关")) {
                            WordTestActivity.start(context, bookId, unit, step);
                        } else if (topicType.equals("单词消消乐")) {
                            WordClearActivity.start(context, bookId, unit, step);
                        } else if (topicType.equals("单词连线")) {

                        }

                        finish();
                        dialog.dismiss();
                    }).create();
        }

        topicDialog.show();
    }
}
