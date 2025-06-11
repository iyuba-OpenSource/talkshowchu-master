package com.iyuba.talkshow.ui.detail.comment;

import com.iyuba.talkshow.data.model.Comment;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * Created by Administrator on 2016/11/28 0028.
 */

public interface CommentMvpView extends MvpView {
    void showComments(List<Comment> commentList);

    void showEmptyComment();

    void showToast(int id);

    void clearInputText();

    void setCommentNum(int num);

    void startDubbingActivity(Voa voa);

    void showLoadingDialog();

    void dismissLoadingDialog();

    void showCommentLoadingLayout();

    void dismissCommentLoadingLayout();

    void dismissRefreshingView();

}
