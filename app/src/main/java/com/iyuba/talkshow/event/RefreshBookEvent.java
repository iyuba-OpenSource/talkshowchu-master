package com.iyuba.talkshow.event;

public class RefreshBookEvent {
    public int  bookId;
    public int  version;
    public boolean sync;

    public RefreshBookEvent(int bookId , int version) {
        this.bookId = bookId;
        this.version = version;
        sync = false;
    }
    public RefreshBookEvent(int bookId , int version, boolean sy) {
        this.bookId = bookId;
        this.version = version;
        sync = sy;
    }
}
