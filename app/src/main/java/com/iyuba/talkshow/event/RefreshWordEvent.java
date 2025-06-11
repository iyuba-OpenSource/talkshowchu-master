package com.iyuba.talkshow.event;

public class RefreshWordEvent {
    public int  bookId;
    public int  version;

    public RefreshWordEvent(int bookId , int version) {
        this.bookId = bookId;
        this.version = version;
    }
}
