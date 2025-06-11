package com.iyuba.talkshow.event;

public class SelectBookEvent {
    public int  bookId;
    public int  version;
    public boolean sync;

    public SelectBookEvent(int bookId , int version) {
        this.bookId = bookId;
        this.version = version;
        sync = false;
    }
    public SelectBookEvent(int bookId , int version, boolean sy) {
        this.bookId = bookId;
        this.version = version;
        sync = sy;
    }
}
