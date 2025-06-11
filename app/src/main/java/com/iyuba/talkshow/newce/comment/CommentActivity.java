package com.iyuba.talkshow.newce.comment;

import android.content.Context;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.RankWork;
import com.iyuba.talkshow.data.model.RankOralBean;
import com.iyuba.talkshow.newview.CustomDialog;
import com.iyuba.talkshow.newview.WaittingDialog;
import com.iyuba.talkshow.ui.base.BaseActivity;

import java.util.Comparator;
import java.util.List;

import javax.inject.Inject;

/**
 * 排行的详情界面
 * Created by carl shen on 2020/8/26
 * New Primary English, new study experience.
 */
public class CommentActivity extends BaseActivity implements CommentMvpView {
    public static final String VOA = "voa";
    private Context mContext;
    private ImageView backBtn;
    private TextView title;
    private RecyclerView listComment;
    private String userName, userPic;
    private CustomDialog waitDialog;
    private int uid, voaId;
    @Inject
    CommentPresenter mPresenter;
    @Inject
    public CommentListAdapter commentAdapter;

    public static class SortByScore implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            RankOralBean.DataBean s1 = (RankOralBean.DataBean) o1;
            RankOralBean.DataBean s2 = (RankOralBean.DataBean) o2;
            if (s1.getScores() == s2.getScores()) {
                if (s1.getRanking() > s2.getRanking()) {
                    return 1;
                } else {
                    return -1;
                }
            } else
            if (s1.getScores() < s2.getScores()) //小的在后面
                return 1;
            return -1;
        }
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_person_comment);
        activityComponent().inject(this);
        mPresenter.attachView(this);

        mContext = this;
        waitDialog = WaittingDialog.showDialog(mContext);

        backBtn = findViewById(R.id.button_back);
        title = findViewById(R.id.title);
        listComment = findViewById(R.id.voa_list);

        initRecyclerView();

        uid = getIntent().getIntExtra("uid", 0);
        voaId = getIntent().getIntExtra("voaId", 0);
        userName = getIntent().getStringExtra("userName");
        userPic = getIntent().getStringExtra("userPic");

        if (!TextUtils.isEmpty(userName)) {
            title.setText("\"" + userName + "\"" + "的评测");
        } else {
            title.setText("\"" + uid + "\"" + "的评测");
        }
        Log.e("CommentActivity", "voaId = " + voaId);

        backBtn.setOnClickListener(v -> finish());

        commentAdapter.setUserName(uid, userName);
        commentAdapter.setImgSrc(userPic);
        listComment.setAdapter(commentAdapter);
        waitDialog.show();
    }

    @Override
    public void onResume() {
        super.onResume();
        mPresenter.getWorkRanking(uid, voaId);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        if (commentAdapter != null) {
            commentAdapter.stopVoices();
        }
        mPresenter.detachView();
    }

    private void initRecyclerView() {
        listComment.setLayoutManager(new LinearLayoutManager(mContext));
        listComment.addItemDecoration(new DividerItemDecoration(mContext, 1));
    }

    @Override
    public void showRankings(List<RankWork> rankingList) {
        if (rankingList == null) {
            Log.e("CommentActivity", "Why showRankings null? ");
            return;
        }
        commentAdapter.setData(rankingList);
        commentAdapter.notifyDataSetChanged();
    }

    @Override
    public void showEmptyRankings() {
    }

    @Override
    public void showToast(int id) {
        Toast.makeText(this, id, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingLayout() {
        waitDialog.show();
    }

    @Override
    public void dismissLoadingLayout() {
        waitDialog.dismiss();
    }

    @Override
    public void refreshLayout() {
        mPresenter.getWorkRanking(uid, voaId);
    }
}
