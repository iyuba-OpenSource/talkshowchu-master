package com.iyuba.talkshow.data.manager;

import com.iyuba.talkshow.data.model.User;

/**
 * Created by Administrator on 2016/12/29/029.
 */

public interface OnLoginListener {
    void onLoginSuccess(User user);

    void onLoginFail(String errorMsg);
}
