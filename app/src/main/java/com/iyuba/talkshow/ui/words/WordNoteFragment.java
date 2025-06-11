package com.iyuba.talkshow.ui.words;

import android.app.Activity;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iyuba.play.Player;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.databinding.FragmentWordnoteBinding;
import com.iyuba.talkshow.injection.PerFragment;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.widget.recycler.EndlessListRecyclerView;
import com.iyuba.wordtest.db.WordOp;
import com.iyuba.wordtest.entity.WordEntity;
import com.iyuba.wordtest.event.WordFavorEvent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

/**
 * Created by carl shen on 2022/2/23
 * New Primary English, new study experience.
 */
@PerFragment
public class WordNoteFragment extends BaseFragment implements WordNoteMvpView {
    private static final int PAGE_COUNT = 30;
    public static final String TAG = "WordNoteFragment";
    FragmentWordnoteBinding binding;
    private Player mPlayer;
    WordsAdapter mAdapter;
    @Inject
    WordNotePresenter mPresenter;

    @Inject
    DataManager dataManager ;
    @Inject
    ConfigManager configManager ;
    private final Handler handler = new Handler();
    private LoadingDialog mLoadingDialog;
    private boolean mLoadingFlag = false;
    private int mCurrentPage = 1;
    List<Word> selectedWords;
    private WordOp wordOp;

    public static WordNoteFragment build() {
        WordNoteFragment fragment  = new WordNoteFragment();
        Bundle bundle  = new Bundle( );
        fragment.setArguments(bundle);
        return fragment ;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
        mPresenter.attachView(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWordnoteBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);

        mPlayer = new Player();
        mAdapter = new WordsAdapter();
        binding.recycler.setAdapter(mAdapter);
        binding.recycler.setOnEndlessListener(mEndlessListener);

        wordOp = new WordOp(mContext);
        initData(true);
        initClick();
    }

    @Override
    public void onDestroyView() {
        mPlayer.stopAndRelease();
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    private void initClick() {
        binding.syncWord.setOnClickListener(v -> syncYunWord());
        binding.selectMode.setOnClickListener(v -> setWordMode());
    }

    public void syncYunWord() {
        if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            showMessage("请开启网络连接后同步生词本!");
            return;
        }

        if (!UserInfoManager.getInstance().isLogin()){
            showMessage("请登录后同步生词本");
            return;
        }

        showLoadingDialog();
        if (!mLoadingFlag) {
            int userId = UserInfoManager.getInstance().getUserId();
            mPresenter.getLatestData(userId, PAGE_COUNT);
        }
    }

    public void setWordMode() {
        ((Activity) mContext).startActionMode(mEditActionModeCallback);
        mAdapter.setDeleteMode(true);
    }
    public void onClickReload() {
        initData(false);
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void WordFavorChange(WordFavorEvent event) {
        Log.e("WordNoteActivity", "WordFavorEvent event.id " + event.id);
        initData(false);
    }

    public void showLoadingDialog() {
        if ((mLoadingDialog == null) || !mLoadingDialog.isShowing()) {
            mLoadingDialog = new LoadingDialog(mContext);
            mLoadingDialog.show();
        }
    }

    public void dismissLoadingDialog() {
        if (!binding.syncWord.isClickable()) {
            binding.syncWord.setClickable(true);
        }
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    private void initData(Boolean flag) {
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
            binding.textHint.setText(wordList.size() + "个");
        } else {
//            if (flag) {
//                ToastUtil.show(mContext,"暂无本地数据，请开启网络连接后同步生词本!");
//            }
            mAdapter.setList(new ArrayList<>());
            binding.textHint.setText("0个");

//            ToastUtil.show(this,"暂无本地数据，请开启网络连接后同步生词本!");
            if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                showMessage("请开启网络连接后同步生词本!");
                return;
            }

            if (!UserInfoManager.getInstance().isLogin()){
                showNoData(true,"请登录后同步生词本");
                return;
            }

            showLoadingDialog();
            if (!mLoadingFlag) {
                int userId = UserInfoManager.getInstance().getUserId();
                mPresenter.getLatestData(userId, PAGE_COUNT);
            }
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
            ToastUtil.show(getActivity(), message);
        }else {
            showNoData(true,message);
        }
    }

    @Override
    public void showMessage(int resId) {
        ToastUtil.show(mContext, getResources().getString(resId));
    }

    @Override
    public void onLatestDataLoaded(List<Word> words, int total, boolean instantRefresh) {
//        mCurrentPage = 1;
//        mAdapter.setList(words, instantRefresh);
//        binding.textTinyHint.setText(total + "个");
        dismissLoadingDialog();
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
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            initData(false);
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
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            mode.finish();
                            setRecyclerEndless(true);
                            initData(false);
                        }
                    });
                }
            });
        }
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
                    handler.post(new Runnable() {
                        @Override
                        public void run() {
                            setRecyclerEndless(true);
                            initData(false);
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

    private final ActionMode.Callback mEditActionModeCallback = new ActionMode.Callback() {

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

    };


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
