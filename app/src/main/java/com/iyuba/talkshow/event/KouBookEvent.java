package com.iyuba.talkshow.event;

public class KouBookEvent {
    public int  bookId;
    public int  version;
    public boolean sync;

    public KouBookEvent(int bookId , int version) {
        this.bookId = bookId;
        this.version = version;
        sync = false;
    }
    public KouBookEvent(int bookId , int version, boolean sy) {
        this.bookId = bookId;
        this.version = version;
        sync = sy;
    }
}
