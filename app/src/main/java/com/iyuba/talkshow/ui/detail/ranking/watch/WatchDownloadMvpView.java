package com.iyuba.talkshow.ui.detail.ranking.watch;

import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.ui.base.MvpView;

import java.util.List;

/**
 * WatchDownloadMvpView
 *
 * @author wayne
 * @date 2018/2/9
 */
public interface WatchDownloadMvpView extends MvpView {
    void showVoaTextLit(List<VoaText> voaTextList);
}
