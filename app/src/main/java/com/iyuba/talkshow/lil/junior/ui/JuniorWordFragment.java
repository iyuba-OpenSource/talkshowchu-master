package com.iyuba.talkshow.lil.junior.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.manager.AbilityControlManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.databinding.FragmentJuniorWordBinding;
import com.iyuba.talkshow.event.LoginEvent;
import com.iyuba.talkshow.event.LoginOutEvent;
import com.iyuba.talkshow.event.RefreshBookEvent;
import com.iyuba.talkshow.event.RefreshWordEvent;
import com.iyuba.talkshow.injection.PerFragment;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newce.study.word.WordMvpView;
import com.iyuba.talkshow.newce.wordstep.WordStepPresenter;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.courses.coursechoose.CourseChooseActivity;
import com.iyuba.talkshow.ui.courses.coursechoose.OpenFlag;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.db.BookLevelDao;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.NewBookLevels;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.event.WordTestEvent;
import com.iyuba.wordtest.manager.WordConfigManager;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.ui.WordStepActivity;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.List;

import javax.inject.Inject;

/**
 * 中小学-单词界面
 * Created by carl shen on 2020/7/31
 * New Primary English, new study experience.
 */
@PerFragment
public class JuniorWordFragment extends BaseFragment implements WordMvpView {
    public static final String TAG = "WordstepFragment";
    private static final String BOOKID = "BOOKID";
    private static final String BOOKNAME = "BOOKNAME";
//    WordstepAdapter adapter;
    JuniorWordAdapter adapter;
    WordDataBase db;
    int step;
    private List<Integer> getUnits;
    private Handler handler = new Handler();

    private int bookId ;
    private BookLevelDao bookLevelDao;
    private String bookName;
    //布局样式
    FragmentJuniorWordBinding binding;
    @Inject
    ConfigManager configManager;
    @Inject
    WordStepPresenter mPresenter;
    private LoadingDialog mLoadingDialog;
    private int defaultUi = 1;
    private long starttime;

    public static JuniorWordFragment getInstance(){
        JuniorWordFragment fragment  = new JuniorWordFragment();
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
        binding = FragmentJuniorWordBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
        getIntents();
        initWord();
        initDb(true);
        initData();
        refreshData();
    }

    @Override
    public void onDestroyView() {
        mPresenter.detachView();
        EventBus.getDefault().unregister(this);
        super.onDestroyView();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginEvent event) {
        initWord();
        initDb(false);
        Log.e("WordstepFragment", "LoginEvent init word and db... ");
//        mPresenter.syncExamWord(bookId, false);
        refreshData();
    }


    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(LoginOutEvent event) {
        Log.e("WordstepFragment", "LoginOutEvent init word and db... ");
        //人教版审核限制
        int bookId = App.getBookDefaultShowData().getBookId();
        bookName = App.getBookDefaultShowData().getBookName();

        if (configManager != null) {
            bookId = configManager.getWordId()==0?bookId:configManager.getWordId();
            bookName = configManager.getWordTitle()==null?bookName:configManager.getWordTitle();

            configManager.putWordId(bookId);
            configManager.putWordTitle(bookName);
        }

        initWord();
        refreshData();
    }
    private void initWord() {
        WordManager.getInstance().init(UserInfoManager.getInstance().getUserName(), String.valueOf(UserInfoManager.getInstance().getUserId()),
                App.APP_ID, Constant.EVAL_TYPE, UserInfoManager.getInstance().isVip() ? 1 : 0, App.APP_NAME_EN);
    }

    private void initDb(Boolean flag) {
        db = WordDataBase.getInstance(TalkShowApplication.getInstance());
        bookLevelDao  = db.getBookLevelDao();
        int wordLoad = WordConfigManager.Instance(mContext).loadInt(WordConfigManager.WORD_DB_NEW_LOADED, 0);
        if (wordLoad == 1) {
            int uidLoad = WordConfigManager.Instance(mContext).loadInt(WordConfigManager.WORD_DB_NEW_LOADED + UserInfoManager.getInstance().getUserId(), 0);
            if (uidLoad == 0) {
                TalkShowApplication.getSubHandler().post(new Runnable() {
                    @Override
                    public void run() {
                        WordManager.getInstance().migrateData(TalkShowApplication.getContext());
                        Log.e("WordManager", "migrateData bookId " + bookId);
                        EventBus.getDefault().post(new RefreshWordEvent(bookId, 0));
                    }
                });
            }
        }
        List<String> words = db.getTalkShowWordsDao().getWords4Book(bookId);
        if ((words == null) || (words.size() < Constant.PRODUCT_WORDS)) {
            if (NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                if (flag) {
                    showLoadingDialog();
                }
                mPresenter.getWordsById(bookId);
            } else {
                if (flag) {
                    showToastShort("首次加载需要连接数据网络获取单词数据。");
                }
            }
        }
    }

    private void getIntents() {
        bookId = configManager.getWordId();
        bookName = configManager.getWordTitle();
        if (TextUtils.isEmpty(bookName)) {
            bookName = App.getBookDefaultShowData().getBookName();
        }
        if (bookId < 1){
            CourseChooseActivity.start(getActivity(), OpenFlag.FINISH, OpenFlag.TO_WORD);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void refreshBookWords(RefreshBookEvent event) {
        Log.e("WordstepFragment", "refreshBookWords currBookId " + event.bookId);
        bookId = event.bookId;
        bookName = configManager.getWordTitle();
        List<String> words = db.getTalkShowWordsDao().getWords4Book(bookId);
        if ((words == null) || (words.size() < Constant.PRODUCT_WORDS)) {
            if (NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                showLoadingDialog();
                mPresenter.getWordsById(bookId);
            } else {
                showToastShort("您选择的课程需要连接数据网络获取单词数据。");
            }
        } else {
            refreshData();
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void SetBookWords(RefreshWordEvent event) {
        bookId = event.bookId;
        bookName = configManager.getWordTitle();
        Log.e("WordstepFragment", "refreshBookWords bookName " + bookName);
        refreshData();
        List<TalkShowWords> talkShowWordsList = db.getTalkShowWordsDao().getBookWords(bookId);
        if ((talkShowWordsList == null) || talkShowWordsList.size() < 1) {
            showToastShort("暂时没有相应的单词资源");
        }
    }
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void WordStepTest(WordTestEvent event) {
        refreshData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
//            finish();
        }
        return true;
    }

    @Override
    public void showLoadingDialog() {
        if ((mLoadingDialog == null) || !mLoadingDialog.isShowing()) {
            mLoadingDialog = new LoadingDialog(mContext);
            mLoadingDialog.show();
        }
    }

    @Override
    public void dismissLoadingDialog() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

    public static Intent buildIntent(Context context , int bookId , String bookName){
        Intent intent = new Intent();
        intent.setClass(context, WordStepActivity.class);
        intent.putExtra(BOOKID , bookId);
        intent.putExtra(BOOKNAME , bookName);
        return intent;
    }

    public void initData(){
//        adapter = new WordstepAdapter(bookId, db.getTalkShowWordsDao());
        adapter = new JuniorWordAdapter(bookId,db.getTalkShowWordsDao());
        binding.gridview.setAdapter(adapter);
        adapter.setStepFragment(this);
        adapter.setMobCallback(() -> {
//            doLogin();
            startLogin();
        });
    }

    public void refreshData() {
        TalkShowApplication.getSubHandler().post(new Runnable() {
            @Override
            public void run() {
                step = loadStep();
                getUnits = db.getTalkShowWordsDao().getUnitsByBook(bookId);
                handler.post(new Runnable() {
                    @Override
                    public void run() {
//                        adapter = new WordstepAdapter(bookId, step, getUnits, db.getTalkShowWordsDao());
                        adapter = new JuniorWordAdapter(bookId, step, getUnits, db.getTalkShowWordsDao());
                        binding.gridview.setAdapter(adapter);
                        adapter.setStepFragment(JuniorWordFragment.this);
                        adapter.setMobCallback(() -> {
//                            doLogin();
                            startLogin();
                        });
                    }
                });
            }
        });
    }

    private int loadStep() {
        if (WordManager.WordDataVersion == 2) {
//            if (! checkLogin()) {
//                return 0;
//            }
            NewBookLevels newLevels = db.getNewBookLevelDao().getBookLevel(bookId, String.valueOf(UserInfoManager.getInstance().getUserId()));
            if (newLevels != null) {
                Log.e("WordstepFragment", "loadStep newLevels.level " + newLevels.level);
                return newLevels.level;
            }
        } else
        if (bookLevelDao != null && bookLevelDao.getBookLevel(bookId) != null) {
            return bookLevelDao.getBookLevel(bookId).level;
        }
        return 0;
    }

    public String getUserImageUrl() {
        return UserInfoManager.getInstance().isLogin() ?
                Constant.Url.getMiddleUserImageUrl(UserInfoManager.getInstance().getUserId(),
                        configManager.getPhotoTimestamp()) : null;
    }

    /*@Override
    public void goResultActivity(LoginResult data) {
        if (data == null) {
            NewLoginUtil.startToLogin(getActivity());
        } else if (!TextUtils.isEmpty(data.getPhone())) {
            String randNum = "" + System.currentTimeMillis();
            String user = "iyuba" + randNum.substring(randNum.length() - 4) + data.getPhone().substring(data.getPhone().length() - 4);
            String pass = data.getPhone().substring(data.getPhone().length() - 6);
            Log.e(TAG, "goResultActivity.user  " + user);
            Log.e(TAG, "goResultActivity.pass  " + pass);
            Intent intent = new Intent(mContext, RegisterSubmitActivity.class);
            intent.putExtra(RegisterSubmitActivity.PhoneNum, data.getPhone());
            intent.putExtra(RegisterSubmitActivity.UserName, user);
            intent.putExtra(RegisterSubmitActivity.PassWord, pass);
            intent.putExtra(RegisterSubmitActivity.RegisterMob, 1);
            startActivity(intent);
        } else {
            Log.e(TAG, "goResultActivity LoginResult is ok. ");
        }
        SecVerify.finishOAuthPage();
        CommonProgressDialog.dismissProgressDialog();
    }*/

    //跳转到登陆
    private void startLogin(){
        NewLoginUtil.startToLogin(getActivity());
    }

    /****************************辅助功能*******************************/
    //同步单词记录
    public void syncExamWord() {
        if (!NetStateUtil.isConnected(TalkShowApplication.getInstance())) {
            ToastUtil.showToast(mContext, mContext.getResources().getString(R.string.please_check_network));
            return;
        }
        if (!UserInfoManager.getInstance().isLogin()) {
            startLogin();
            return;
        }
        showLoadingDialog();
        mPresenter.syncExamWord(bookId, true);
    }
}
