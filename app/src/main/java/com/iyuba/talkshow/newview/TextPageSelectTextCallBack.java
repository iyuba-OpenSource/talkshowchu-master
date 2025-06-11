package com.iyuba.talkshow.newview;

/**
 * TextPage Select Text CallBack
 *
 * @author carl shen
 */
public interface TextPageSelectTextCallBack {
    void selectTextEvent(String selectText);

    void cancelWordCard();

    void selectParagraph(int paragraph);
}
