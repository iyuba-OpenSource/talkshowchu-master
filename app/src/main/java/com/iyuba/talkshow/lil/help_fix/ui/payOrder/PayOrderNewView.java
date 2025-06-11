package com.iyuba.talkshow.lil.help_fix.ui.payOrder;

import com.iyuba.talkshow.lil.help_mvp.mvp.BaseView;

public interface PayOrderNewView extends BaseView {

    //显示支付链接状态
    void showPayLinkStatus(boolean isError,String showMsg);

    //显示支付功能状态
    void showPayFinishStatus(boolean isFinish,String payStatus);
}
