package com.iyuba.talkshow.event;

public class MyBookEvent {
    public int  bookId;
    public int  version;
    public boolean sync;

    public MyBookEvent(int bookId , int version) {
        this.bookId = bookId;
        this.version = version;
        sync = false;
    }
    public MyBookEvent(int bookId , int version, boolean sy) {
        this.bookId = bookId;
        this.version = version;
        sync = sy;
    }
}
