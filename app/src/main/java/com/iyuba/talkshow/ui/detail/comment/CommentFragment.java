package com.iyuba.talkshow.ui.detail.comment;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.media.MediaPlayer;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.core.content.ContextCompat;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;

import com.devbrackets.android.exomedia.listener.OnPreparedListener;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.model.Comment;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.event.AudioEvent;
import com.iyuba.talkshow.event.CommentEvent;
import com.iyuba.talkshow.event.StopEvent;
import com.iyuba.talkshow.lil.help_fix.ui.dubbing.DubbingNewActivity;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.dubbing.DubbingActivity;
import com.iyuba.talkshow.ui.main.drawer.Share;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.Recorder;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.TimeUtil;
import com.permissionx.guolindev.PermissionX;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.io.File;
import java.io.IOException;
import java.util.List;

import javax.inject.Inject;


public class CommentFragment extends BaseFragment implements CommentMvpView {


    private static final String TAG = CommentFragment.class.getSimpleName();

    private static final String VOA = "voa";
    private static final String RANKING_ID = "ranking_id";
    private static final String RANKING_NAME = "ranking_name";
    private static final String RANKING_URL= "ranking_url";
    public static final int PAGE_NUM = 1;
    public static final int PAGE_SIZE = 20;

    private static final int REQUEST_RECORD_PERMISSION = 1;

    private Voa mVoa;
    private int mRankingId;
    private String mRankingURL;
    private String mRankingName;
    private int pageNum = 1;

    @Inject
    CommentPresenter mPresenter;
    @Inject
    CommentAdapter mAdapter;

    MediaPlayer mMediaPlayer;
    Recorder mRecorder;
    private String mRecorderFilePath;

    boolean mIsShowInputMethod;
    private LoadingDialog mLoadingDialog;

    com.iyuba.talkshow.databinding.FragmentCommentBinding binding ;

    CommentAdapter.CommentCallback callback = new CommentAdapter.CommentCallback() {
        @Override
        public void onPhotoClick(Comment comment) {
            //TODO
        }

        @Override
        public void onAgreeClick(int id) {
            mPresenter.doAgree(id);
        }

        @Override
        public void onAgainstClick(int id) {
            mPresenter.doAgainst(id);
        }
    };

    OnPreparedListener mOnPreparedListener = new OnPreparedListener() {
        @Override
        public void onPrepared() {
            EventBus.getDefault().post(new AudioEvent(AudioEvent.State.STOP_INTERRUPTED));
        }
    };

    public CommentFragment() {

    }

    public static CommentFragment newInstance(Voa voa, int rankingId, String rankName,String rankUrl) {
        CommentFragment fragment = new CommentFragment();
        Bundle args = new Bundle();
        args.putParcelable(VOA, voa);
        args.putInt(RANKING_ID, rankingId);
        args.putString(RANKING_NAME, rankName);
        args.putString(RANKING_URL, rankUrl);
        fragment.setArguments(args);
        return fragment;
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        binding = com.iyuba.talkshow.databinding.FragmentCommentBinding.inflate(getLayoutInflater(), container, false);
        mPresenter.attachView(this);

        init();
        initClick();
        initRecyclerView();
        initCommentInputView();
        if (getArguments() != null) {
            mVoa = getArguments().getParcelable(VOA);
            mRankingId = getArguments().getInt(RANKING_ID);
            mRankingName = getArguments().getString(RANKING_NAME);
            mRankingURL = getArguments().getString(RANKING_URL);
            //这里出于对评论内容的考虑，暂时封禁
//            showCommentLoadingLayout();
//            mPresenter.loadComment(mVoa.voaId(), mRankingId, PAGE_NUM, PAGE_SIZE);
            binding.commentTitleTv.setVisibility(View.INVISIBLE);
        }
        return binding.getRoot();
    }

    private void initClick() {
        binding.dubbingView.shareTv.setOnClickListener(v -> onClickShare());
        binding.dubbingView.dubbingTv.setOnClickListener(v -> onClickDubbing());

        //关闭提交评论
        binding.dubbingView.commentTv.setVisibility(View.INVISIBLE);
        binding.dubbingView.commentTv.setOnClickListener(v -> onClickComment());
        binding.commentTitleTv.setOnClickListener(v -> onClickOthers());
        binding.emptyLayout.getRoot().setOnClickListener(v -> onClickOthers());
        if (ConfigData.openShare) {
            binding.dubbingView.shareTv.setVisibility(View.VISIBLE);
        }else {
            binding.dubbingView.shareTv.setVisibility(View.GONE);
        }
    }

    private void init() {
        mRecorder = new Recorder();
        mMediaPlayer = new MediaPlayer();
        mLoadingDialog = new LoadingDialog(getContext());
        binding.rootLayout.setOnResizeListener(new MyRelativeLayout.OnResizeListener() {
            @Override
            public void OnResize(int w, int h, int oldw, int oldh) {
                mIsShowInputMethod = h < oldh;
            }
        });
        int shareSize = (int) getResources().getDimension(R.dimen.comment_share_image_size);
        setTextDrawable(binding.dubbingView.shareTv, R.drawable.share, shareSize, shareSize);
    }

    public void setTextDrawable(TextView textView, int resId, int width, int height) {
        Drawable drawable = ContextCompat.getDrawable(getActivity(), resId);
        drawable.setBounds(0, 0, width, height);
        textView.setCompoundDrawables(drawable, null, null, null);
    }

    @Override
    public void onViewCreated(View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        EventBus.getDefault().register(this);
    }

    @Override
    public void onResume() {
        super.onResume();
        MobclickAgent.onPageStart(TAG);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPageEnd(TAG);
        if ((mMediaPlayer != null) && mMediaPlayer.isPlaying()) {
            mMediaPlayer.pause();
        }
    }

    public void initCommentInputView() {
        binding.commentInputView.setOnCommentSendListener(mSendListener);
        binding.commentInputView.setInputMethodCallback(new CommentInputView.InputMethodCallback() {

            @Override
            public void show() {
                toggleInputMethod(true);
            }

            @Override
            public void hide() {
                toggleInputMethod(false);
            }
        });
        try {
            mRecorderFilePath = StorageUtil.getCommentVoicePath(TalkShowApplication.getInstance());
        } catch (IOException e) {
            e.printStackTrace();
        }
        binding.commentInputView.setRecordFilePath(mRecorderFilePath);
        binding.commentInputView.setRecorderListener(mRecorderListener);
        binding.commentInputView.setRequestPermissionCallback(mPermissionCB);
        binding.commentInputView.setMediaPlayer(mMediaPlayer);
        binding.commentInputView.setRecorder(mRecorder);
    }

    public void initRecyclerView() {
        binding.refreshLayout.setOnRefreshListener(mOnRefreshListener);
        mAdapter.setCallback(callback);
        mAdapter.setMediaPlayer(mMediaPlayer);
        mAdapter.setOnReplyListener(mOnReplyListener);
        mAdapter.setOnItemClickListener(new CommentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick() {

            }
        });
        binding.recyclerView.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(getContext());
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
//        mRecyclerView.addItemDecoration(new LinearItemDivider(getActivity(), LinearItemDivider.VERTICAL_LIST));
        binding.recyclerView.setLayoutManager(layoutManager);
        binding.recyclerView.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        binding.recyclerView.addOnScrollListener(mOnScrollListener);
    }

    private final CommentAdapter.OnReplyListener mOnReplyListener = new CommentAdapter.OnReplyListener() {
        @Override
        public void onReply(String targetUsername) {
            binding.dubbingView.getRoot().setVisibility(View.GONE);
            binding.commentInputView.getRootView().setVisibility(View.VISIBLE);
            binding.commentInputView.replyToSomeone(targetUsername);
        }
    };

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        EventBus.getDefault().unregister(this);
        mPresenter.detachView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
        if (mMediaPlayer != null) mMediaPlayer.release();
        if (mRecorder != null) mRecorder.release();
//        mCommentInput.deleteRecordFile();
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onStopEvent(StopEvent stopEvent) {
        switch (stopEvent.source) {
            case StopEvent.SOURCE.VOICE:
                if (mMediaPlayer.isPlaying()) {
                    mMediaPlayer.pause();
                }
                break;
            default:
                break;
        }
    }

    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onCommentEvent(CommentEvent event) {
        switch (event.status) {
            case CommentEvent.Status.GONE:
                onClickOthers();
                break;
            default:
                break;
        }
    }

    private void toggleInputMethod(boolean show) {
        InputMethodManager inputMethodManager =
                (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        if (mIsShowInputMethod) {
            if (!show) inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        } else {
            if (show) inputMethodManager.toggleSoftInput(0, InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 刷新监听
     */
    private final SwipeRefreshLayout.OnRefreshListener mOnRefreshListener = new SwipeRefreshLayout.OnRefreshListener() {
        @Override
        public void onRefresh() {
            binding.refreshLayout.setRefreshing(false);
            binding.recyclerView.post(new Runnable() {
                @Override
                public void run() {
                    //暂时封禁，注意联动开启
//                    mPresenter.loadComment(mVoa.voaId(), mRankingId, PAGE_NUM, PAGE_SIZE);
                }
            });
        }
    };

    /**
     * 加载更多
     */
    private final RecyclerView.OnScrollListener mOnScrollListener = new RecyclerView.OnScrollListener() {
        @Override
        public void onScrolled(RecyclerView recyclerView, int dx, int dy) {
            if (!recyclerView.canScrollVertically(1)) {// 手指不能向上滑动了
                // TODO 这里有个注意的地方，如果你刚进来时没有数据，但是设置了适配器，
                // TODO 这个时候就会触发加载更多，需要开发者判断下是否有数据，如果有数据才去加载更多。
                //暂时封禁，注意联动开启
//                pageNum++;
//                mPresenter.loadComment(mVoa.voaId(), mRankingId, pageNum, PAGE_SIZE);
            }
        }
    };

//    @OnClick(R.id.dubbing_tv)
    public void onClickDubbing() {
        EventBus.getDefault().post(new StopEvent(StopEvent.SOURCE.VIDEO));
        EventBus.getDefault().post(new StopEvent(StopEvent.SOURCE.VOICE));
        mPresenter.getVoa(mVoa.voaId());
    }

//    @OnClick(R.id.share_tv)
    public void onClickShare() {
        Share.prepareDubbingMessage(getContext(), mVoa, mRankingId, mRankingName,
                mPresenter.getIntegralService(), UserInfoManager.getInstance().getUserId());
    }

//    @OnClick(R.id.comment_tv)
    public void onClickComment() {
        if (UserInfoManager.getInstance().isLogin()) {
            binding.dubbingView.getRoot().setVisibility(View.GONE);
            binding.commentInputView.setVisibility(View.VISIBLE);
            binding.commentInputView.initState();
        } else {
            NewLoginUtil.startToLogin(getActivity());
        }
    }

//    @OnClick({R.id.comment_title_tv, R.id.empty_layout})
    public void onClickOthers() {
        if (mIsShowInputMethod) {
            toggleInputMethod(false);
        } else {
            if (binding.dubbingView.getRoot().getVisibility() == View.GONE) {
                binding.dubbingView.getRoot().setVisibility(View.VISIBLE);
                binding.commentInputView.setVisibility(View.GONE);
            }
        }
    }


    @Override
    public void showComments(List<Comment> commentList) {
        binding.emptyView.setVisibility(View.GONE);
        binding.recyclerView.setVisibility(View.VISIBLE);
        mAdapter.setList(commentList);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyComment() {
        binding.emptyLayout.emptyText.setText(getString(R.string.has_no_comment));
        binding.emptyLayout.getRoot().setVisibility(View.VISIBLE);
        binding.recyclerView.setVisibility(View.GONE);
    }

    @Override
    public void showToast(int id) {
        Toast.makeText(getContext(), id, Toast.LENGTH_LONG).show();
    }

    @Override
    public void clearInputText() {
        binding.commentInputView.clearInputText();
    }

    @Override
    public void setCommentNum(int num) {
//        mCommentTitleTv.setText(MessageFormat.format(getString(R.string.comment_num), num));
    }

    @Override
    public void startDubbingActivity(Voa voa) {
//        long timestamp = TimeUtil.getTimeStamp();
//        Intent intent = DubbingActivity.buildIntent(getActivity(), voa, timestamp);
//        startActivity(intent);

        //跳转到新的配音界面中
        DubbingNewActivity.start(getActivity(),voa);
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void showCommentLoadingLayout() {

        binding.loadingLayout.getRoot().setVisibility(View.VISIBLE);
    }

    @Override
    public void dismissCommentLoadingLayout() {
        binding.loadingLayout.getRoot().setVisibility(View.GONE);
    }

    @Override
    public void dismissRefreshingView() {
        binding.refreshLayout.setRefreshing(false);
    }

    private final CommentInputView.RequestPermissionCallback mPermissionCB = new CommentInputView.RequestPermissionCallback() {
        @Override
        public void requestRecordPermission() {
            PermissionX.init(getActivity())
                    .permissions(Manifest.permission.RECORD_AUDIO)
                    .request((aBoolean, strings, strings2) -> {
                        if (aBoolean) onRecordPermissionGranted();
                        else {
                            onRecordPermissionDenied();
                        }
                    });
        }
    };

    void onRecordPermissionGranted() {
        binding.commentInputView.onRecordPermissionGranted();
    }

    void onRecordPermissionDenied() {
        Toast.makeText(getContext(), "Record Permission Denied! Can't make audio comment", Toast
                .LENGTH_SHORT).show();
    }

    private final CommentInputView.RecorderListener mRecorderListener = new CommentInputView.RecorderListener() {
        @Override
        public void onBegin() {
            binding.micVolumeView.setVisibility(View.VISIBLE);
        }

        @Override
        public void onVolumeChanged(int db) {
            binding.micVolumeView.setVolume(db);
        }

        @Override
        public void onEnd() {
            binding.micVolumeView.setVisibility(View.GONE);
        }

        @Override
        public void onError() {
            binding.micVolumeView.setVisibility(View.GONE);
        }
    };

    private final CommentInputView.OnCommentSendListener mSendListener = new CommentInputView.OnCommentSendListener() {

        @Override
        public void onTextSend(String comment) {
            showLoadingDialog();
            mPresenter.senTextComment(mVoa.voaId(), mRankingId, comment);
        }

        @Override
        public void onVoiceSend(File record) {
            showLoadingDialog();
            mPresenter.sendVoiceComment(mVoa.voaId(), mRankingId, record);
        }
    };
}
