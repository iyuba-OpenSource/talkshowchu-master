package com.iyuba.talkshow.lil.help_fix.model.remote.bean;

import java.io.Serializable;

/**
 * @title: pdf下载链接
 * @date: 2023/7/4 15:41
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class Pdf_url implements Serializable {


    /**
     * exists : true
     * path : /pdf_eg/316023.pdf
     */

    private String exists;
    private String path;

    public String getExists() {
        return exists;
    }

    public void setExists(String exists) {
        this.exists = exists;
    }

    public String getPath() {
        return path;
    }

    public void setPath(String path) {
        this.path = path;
    }
}
