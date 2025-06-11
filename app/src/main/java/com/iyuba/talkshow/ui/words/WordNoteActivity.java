package com.iyuba.talkshow.ui.words;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.View;

import androidx.annotation.Nullable;

import com.iyuba.play.Player;
import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.databinding.ActivityWordNoteBinding;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.widget.recycler.EndlessListRecyclerView;
import com.iyuba.wordtest.db.WordOp;
import com.iyuba.wordtest.entity.WordEntity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


/**
 * 我的单词
 */
public class WordNoteActivity extends BaseActivity implements WordNoteMvpView {
    private static final int PAGE_COUNT = 30;

    //布局样式
    private ActivityWordNoteBinding binding;

    public static void start(Context context){
        Intent intent = new Intent();
        intent.setClass(context,WordNoteActivity.class);
        context.startActivity(intent);
    }

    private Player mPlayer;

    private WordsAdapter mAdapter;

    @Inject
    WordNotePresenter mPresenter;

    @Inject
    DataManager dataManager ;

    @Inject
    ConfigManager configManager ;
    private LoadingDialog mLoadingDialog;
    private boolean mLoadingFlag = false;
    private int mCurrentPage = 1;
    List<Word> selectedWords;
    private WordOp wordOp;
    Context context ;

    //当前编辑按钮状态
    private static final String Status_editing = "editing";//编辑状态
    private static final String Status_deleting = "deleting";//删除状态
    private static final String Status_normal = "normal";//正常状态
    private String editStatus = Status_normal;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityWordNoteBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());


        //绑定activity
        activityComponent().inject(this);
        mPresenter.attachView(this);

        context =this ;
        mPlayer = new Player();
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        mAdapter = new WordsAdapter();
        binding.recycler.setAdapter(mAdapter);
        binding.recycler.setOnEndlessListener(mEndlessListener);
        mAdapter.setOnWordSelectListener(new WordsAdapter.OnWordSelectListener() {
            @Override
            public void onWordSelect(int selectCount) {
                if (selectCount>0){
                    editStatus = Status_deleting;
                    binding.toolbar.imgTopRight.setImageResource(R.drawable.imooc_ic_delete);
                }else {
                    editStatus = Status_editing;
                    binding.toolbar.imgTopRight.setImageResource(R.drawable.button_edit_white);
                }
            }
        });

        wordOp = new WordOp(mContext);

        initToolbar();
        getData();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        mPlayer.stopAndRelease();
    }

    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_word_note, menu);
        return super.onCreateOptionsMenu(menu);
    }*/

    /*@Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
            case R.id.wordmenu_sync:
                if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                    showMessage("请开启网络连接后同步生词本!");
                    break;
                }
                showLoadingDialog();
                if (!mLoadingFlag) {
                    int userId = UserInfoManager.getInstance().getUserId();
                    mPresenter.getLatestData(userId, PAGE_COUNT);
                }
                break;
            case R.id.wordmenu_edit:
                startActionMode(mEditActionModeCallback);
                mAdapter.setDeleteMode(true);
                break;
//                mQueryDialog.show();
//                int category = mCategoryDataHelper.getDefaultCategoryCode();
//                Intent intent = SearchActivity.buildIntent(this, category);
//                startActivity(intent);
//                break;
        }
        return super.onOptionsItemSelected(item);
    }*/

    public void showLoadingDialog() {
        if ((mLoadingDialog == null) || !mLoadingDialog.isShowing()) {
            mLoadingDialog = new LoadingDialog(mContext);
            mLoadingDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    private void initToolbar(){
        binding.toolbar.imgTopLeft.setImageResource(R.drawable.back);
        binding.toolbar.reTopLeft.setOnClickListener(v->{
            finish();
        });
        binding.toolbar.tvTopCenter.setText("我的生词");

        //编辑操作
        binding.toolbar.reTopRight.setVisibility(View.VISIBLE);
        binding.toolbar.imgTopRight.setVisibility(View.VISIBLE);
        binding.toolbar.imgTopRight.setImageResource(R.drawable.button_edit_white);
        binding.toolbar.reTopRight.setOnClickListener(v->{
            //先检查是否存在单词
            if (mAdapter.getList()==null||mAdapter.getList().size()<=0){
                ToastUtil.showToast(this,"暂无收藏的单词，请点击同步按钮同步单词数据");
                return;
            }

            //然后根据状态处理
            if (editStatus.equals(Status_normal)){
                //切换为编辑状态
                editStatus = Status_editing;
                mAdapter.setDeleteMode(true);
            }else if (editStatus.equals(Status_editing)){
                //切换为正常状态
                editStatus = Status_normal;
                binding.toolbar.imgTopRight.setImageResource(R.drawable.button_edit_white);

                mAdapter.setDeleteMode(false);
            }else if (editStatus.startsWith(Status_deleting)){
                //删除数据，并且切换为正常状态
                //这里使用网络接口处理，然后会自动处理本地删除操作
                selectedWords = mAdapter.getSelectedWords();
                if ((selectedWords != null) && selectedWords.size() > 0) {
                    final int uid = UserInfoManager.getInstance().getUserId();
                    List<String> strs = new ArrayList<>();
                    for (Word word : selectedWords) {
                        strs.add(word.key);
                    }

                    mPresenter.deleteWordsNew(uid, strs);
                }else {
                    //无数据则直接切换为正常状态
                    editStatus = Status_normal;
                    binding.toolbar.imgTopRight.setImageResource(R.drawable.button_edit_white);
                }
            }
        });
        //同步单词
        binding.toolbar.reTopRight2.setVisibility(View.VISIBLE);
        binding.toolbar.imgTopRight2.setVisibility(View.VISIBLE);
        binding.toolbar.imgTopRight2.setImageResource(R.drawable.background2_press);
        binding.toolbar.reTopRight2.setOnClickListener(v->{
            if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                showMessage("请开启网络连接后同步生词本!");
                return;
            }
            showLoadingDialog();
            if (!mLoadingFlag) {
                int userId = UserInfoManager.getInstance().getUserId();
                mPresenter.getLatestData(userId, PAGE_COUNT);
            }
        });
    }

    private void initData() {
        List<WordEntity> myWords = wordOp.findWordByUser(UserInfoManager.getInstance().getUserId());
        if ((myWords != null) && (myWords.size()>0)) {
            Log.e("WordNoteActivity", "initData myWords.size " + myWords.size());
            showNoData(false,null);

            List<Word> wordList = new ArrayList<>();
            for (WordEntity entity: myWords) {
                if ((entity != null) && !TextUtils.isEmpty(entity.key)) {
                    if (entity.voa > 0) {
                        if (!(entity.key.contains(entity.voa + ""))) {
                            wordList.add(Entity2Word(entity));
                        }
                    } else {
                        wordList.add(Entity2Word(entity));
                    }
                }
            }
            mCurrentPage = 1;
            mAdapter.setList(wordList, true);
//            binding.textTinyHint.setText(wordList.size() + "个");
        } else {
            mAdapter.setList(new ArrayList<>());
//            binding.textTinyHint.setText("");

//            ToastUtil.show(this,"暂无本地数据，请开启网络连接后同步生词本!");
            if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                showNoData(true,"请开启网络连接后同步生词本!");
                return;
            }

            showLoadingDialog();
            if (!mLoadingFlag) {
                int userId = UserInfoManager.getInstance().getUserId();
                mPresenter.getLatestData(userId, PAGE_COUNT);
            }
        }
    }

    private void getData(){
        //这里根据当前情况优化下，如果存在网络，则使用
        if (NetworkUtil.isConnected(this)){
            showLoadingDialog();
            if (!mLoadingFlag) {
                int userId = UserInfoManager.getInstance().getUserId();
                mPresenter.getLatestData(userId, PAGE_COUNT);
            }
        }else {
            initData();
        }
    }

    private Word Entity2Word(WordEntity entity) {
        if (entity == null) {
            return null;
        }
        Word word = new Word();
        word.key = entity.key;
        word.audioUrl = entity.audio;
        word.def = entity.def;
        word.pron = entity.pron;
        word.lang = entity.lang;
        word.userid = String.valueOf(UserInfoManager.getInstance().getUserId());
        return word;
    }

    private WordEntity Word2Entity(Word word) {
        if (word == null) {
            return null;
        }
        WordEntity entity = new WordEntity();
        entity.key = word.key;
        entity.audio = word.audioUrl;
        entity.def = word.def;
        entity.pron = word.pron;
        entity.lang = word.lang;
        entity.voa = 0;
        entity.book = 0;
        entity.unit = 0;
        return entity;
    }

    @Override
    public void setLoading(boolean isLoading) {
        mLoadingFlag = isLoading;
        if (isLoading) {
        } else {
            dismissLoadingDialog();
        }
    }

    @Override
    public void setRecyclerEndless(boolean isEndless) {
        binding.recycler.setEndless(isEndless);
    }

    @Override
    public void showMessage(String message) {
        if (binding.recycler.getChildCount()>0){
            ToastUtil.show(this, message);
        }else {
            showNoData(true,message);
        }
    }

    @Override
    public void showMessage(int resId) {
        ToastUtil.show(this, getResources().getString(resId));
    }

    @Override
    public void onLatestDataLoaded(List<Word> words, int total, boolean instantRefresh) {
//        mCurrentPage = 1;
//        mAdapter.setList(words, instantRefresh);
//        binding.textTinyHint.setText(total + "个");
        if ((words != null) && (words.size()>0)) {
            Log.e("WordNoteActivity", "onLatestDataLoaded need sync word " + words.size());
            showMessage("同步数据成功!");
            TalkShowApplication.getSubHandler().post(new Runnable() {
                @Override
                public void run() {
                    for (Word word: words) {
                        if ((word != null) && !TextUtils.isEmpty(word.key) && !wordOp.isExsitsWord(word.key, UserInfoManager.getInstance().getUserId())) {
                            long result = wordOp.insertWord(Word2Entity(word), UserInfoManager.getInstance().getUserId());
                            Log.e("WordNoteActivity", "onLatestDataLoaded insertWord result " + result);                       }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            initData();
                        }
                    });
                }
            });
        }
    }

    @Override
    public void onMoreDataLoaded(List<Word> words, int page) {
        mCurrentPage = page;
        mAdapter.addList(words);
    }

    @Override
    public void onDeleteAccomplish(int userId, ActionMode mode) {
//        mPresenter.getLatestInActionMode(userId, PAGE_COUNT, binding.recycler.getEndless(), mode);
        /*if ((selectedWords != null) && selectedWords.size() > 0) {
            showMessage("删除数据成功!");
            TalkShowApplication.getSubHandler().post(new Runnable() {
                @Override
                public void run() {
                    for (Word word: selectedWords) {
                        if ((word != null) && !TextUtils.isEmpty(word.key) && wordOp.isExsitsWord(word.key, UserInfoManager.getInstance().getUserId())) {
                            wordOp.deleteWord(word.key, UserInfoManager.getInstance().getUserId());
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            mode.finish();
                            //设置为正常状态
                            editStatus = Status_normal;
                            binding.toolbar.imgTopRight.setImageResource(R.drawable.button_edit_white);
                            //设置适配器为正常状态
                            mAdapter.clearSelection();
                            mAdapter.setDeleteMode(false);
                            //刷新数据
                            setRecyclerEndless(true);
                            initData();
                        }
                    });
                }
            });
        }*/
    }

    @Override
    public void onDeleteAccomplish(int userId) {
        if ((selectedWords != null) && selectedWords.size() > 0) {
            showMessage("删除数据成功!");
            TalkShowApplication.getSubHandler().post(new Runnable() {
                @Override
                public void run() {
                    for (Word word: selectedWords) {
                        if ((word != null) && !TextUtils.isEmpty(word.key) && wordOp.isExsitsWord(word.key, UserInfoManager.getInstance().getUserId())) {
                            wordOp.deleteWord(word.key, UserInfoManager.getInstance().getUserId());
                        }
                    }
                    runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            //设置为正常状态
                            editStatus = Status_normal;
                            binding.toolbar.imgTopRight.setImageResource(R.drawable.button_edit_white);
                            //设置适配器为正常状态
                            mAdapter.clearSelection();
                            mAdapter.setDeleteMode(false);
                            //刷新数据
                            setRecyclerEndless(true);
                            initData();
                        }
                    });
                }
            });
        }
    }

    private final EndlessListRecyclerView.OnEndlessListener mEndlessListener = new EndlessListRecyclerView.OnEndlessListener() {
        @Override
        public void onEndless() {
            if (!mLoadingFlag) {
                int userId = UserInfoManager.getInstance().getUserId();
                mPresenter.loadMore(userId, mCurrentPage + 1, PAGE_COUNT);
            }
        }
    };

    /*private final ActionMode.Callback mEditActionModeCallback = new ActionMode.Callback() {

        @Override
        public boolean onCreateActionMode(ActionMode mode, Menu menu) {
            menu.add("删除")
                    .setIcon(android.R.drawable.ic_menu_delete)
                    .setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);
            return true;
        }

        @Override
        public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
            return false;
        }

        @Override
        public boolean onActionItemClicked(final ActionMode mode, MenuItem item) {
            selectedWords = mAdapter.getSelectedWords();
            if ((selectedWords != null) && selectedWords.size() > 0) {
                final int uid = UserInfoManager.getInstance().getUserId();
                mPresenter.deleteWords(uid, buildStr(selectedWords), mode);
            } else {
                mode.finish();
            }
            return true;
        }

        @Override
        public void onDestroyActionMode(ActionMode mode) {
            mAdapter.clearSelection();
            mAdapter.setDeleteMode(false);
        }

        private List<String> buildStr(List<Word> words) {
            List<String> strs = new ArrayList<>();
            for (Word word : words) {
                strs.add(word.key);
            }
            return strs;
        }

    };*/

    @Override
    public void showToastShort(int resId) {

    }

    @Override
    public void showToastShort(String message) {

    }

    @Override
    public void showToastLong(int resId) {

    }

    @Override
    public void showToastLong(String message) {

    }

    private void showNoData(boolean show,String msg){
        if (show){
            binding.recycler.setVisibility(View.GONE);
            binding.llNoData.setVisibility(View.VISIBLE);
            binding.tvMsg.setText(msg);
        }else {
            binding.recycler.setVisibility(View.VISIBLE);
            binding.llNoData.setVisibility(View.GONE);
        }
    }
}
