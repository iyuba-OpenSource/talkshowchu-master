package com.iyuba.talkshow.lil.help_fix.model.remote.base;

/**
 * @desction:
 * @date: 2023/2/27 17:51
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class BaseBean_voatext<T>{

    private String total;
    private String Images;
    private T voatext;

    public String getTotal() {
        return total;
    }

    public String getImages() {
        return Images;
    }

    public T getVoatext() {
        return voatext;
    }
}
