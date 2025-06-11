package com.iyuba.talkshow.ui.user.me.dubbing;

import java.util.List;

/**
 * Created by Administrator on 2017/1/17/017.
 */

public interface Editable {
    int getMode();

    void setMode(int mode);

    void deleteCollection();

    int getDataSize();

    void addAllSelection();
}
