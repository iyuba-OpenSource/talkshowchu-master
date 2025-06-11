package com.iyuba.talkshow.lil.help_fix.model.local.entity;

import androidx.annotation.NonNull;
import androidx.room.Entity;
import androidx.room.Ignore;

/**
 * @title: 书籍表-小说
 * @date: 2023/7/4 09:25
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
@Entity(primaryKeys = {"types","orderNumber","level"})
public class BookEntity_novel {

    @NonNull
    public String types;
    @NonNull
    public String orderNumber;
    @NonNull
    public String level;
    public String bookname_en;
    public String author;
    public String about_book;
    public String bookname_cn;
    public String about_interpreter;
    public String wordcounts;
    public String interpreter;
    public String pic;
    public String about_author;

    public BookEntity_novel() {
    }

    @Ignore
    public BookEntity_novel(String types, String orderNumber, String level, String bookname_en, String author, String about_book, String bookname_cn, String about_interpreter, String wordcounts, String interpreter, String pic, String about_author) {
        this.types = types;
        this.orderNumber = orderNumber;
        this.level = level;
        this.bookname_en = bookname_en;
        this.author = author;
        this.about_book = about_book;
        this.bookname_cn = bookname_cn;
        this.about_interpreter = about_interpreter;
        this.wordcounts = wordcounts;
        this.interpreter = interpreter;
        this.pic = pic;
        this.about_author = about_author;
    }
}
