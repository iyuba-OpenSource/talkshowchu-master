package com.iyuba.talkshow.lil.help_fix.model.remote.base;

/**
 * @title:
 * @date: 2023/6/13 15:43
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class BaseBean_dubbing_rank<T> {


    /**
     * ResultCode : 511
     * Message : OK
     * PageNumber : 1
     * TotalPage : 1
     * FirstPage : 1
     * PrevPage : 1
     * NextPage : 1
     * LastPage : 1
     * AddScore : 0
     * Counts : 15
     */

    private String ResultCode;
    private String Message;
    private int PageNumber;
    private int TotalPage;
    private int FirstPage;
    private int PrevPage;
    private int NextPage;
    private int LastPage;
    private int AddScore;
    private int Counts;
    private T data;

    public String getResultCode() {
        return ResultCode;
    }

    public String getMessage() {
        return Message;
    }

    public int getPageNumber() {
        return PageNumber;
    }

    public int getTotalPage() {
        return TotalPage;
    }

    public int getFirstPage() {
        return FirstPage;
    }

    public int getPrevPage() {
        return PrevPage;
    }

    public int getNextPage() {
        return NextPage;
    }

    public int getLastPage() {
        return LastPage;
    }

    public int getAddScore() {
        return AddScore;
    }

    public int getCounts() {
        return Counts;
    }

    public T getData() {
        return data;
    }
}
