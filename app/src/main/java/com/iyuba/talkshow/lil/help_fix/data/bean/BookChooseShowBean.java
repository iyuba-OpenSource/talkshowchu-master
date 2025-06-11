package com.iyuba.talkshow.lil.help_fix.data.bean;

/**
 * 书籍默认显示的bean
 */
public class BookChooseShowBean {
    private int smallTypeId;//小类型id
    private int bookId;//书籍id
    private String bookName;//书籍名称

    public BookChooseShowBean(int smallTypeId, int bookId, String bookName) {
        this.smallTypeId = smallTypeId;
        this.bookId = bookId;
        this.bookName = bookName;
    }

    public int getSmallTypeId() {
        return smallTypeId;
    }

    public int getBookId() {
        return bookId;
    }

    public String getBookName() {
        return bookName;
    }
}
