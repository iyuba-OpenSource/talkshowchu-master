package com.iyuba.talkshow.util;

import android.content.ContentResolver;
import android.database.ContentObserver;
import android.database.Cursor;
import android.net.Uri;

import com.iyuba.talkshow.constant.App;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class VerifyCodeSmsObserver extends ContentObserver {
    private static final String TAG = VerifyCodeSmsObserver.class.getSimpleName();

    private static final String SMS_URI_INBOX = "content://sms/inbox";

    private ContentResolver mContentResolver;
    private OnReceiveVerifyCodeSMSListener mListener;

    private static class VerifyCodeSmsObserverHolder {
        static final VerifyCodeSmsObserver INSTANCE = new VerifyCodeSmsObserver();
    }

    public static VerifyCodeSmsObserver getInstance(ContentResolver contentResolver) {
        VerifyCodeSmsObserver observer =  VerifyCodeSmsObserverHolder.INSTANCE;
        observer.mContentResolver = contentResolver;
        return observer;
    }

    private VerifyCodeSmsObserver() {
        super(null);
    }

    public void setOnReceiveVerifyCodeSMSListener(OnReceiveVerifyCodeSMSListener l) {
        mListener = l;
    }

    @Override
    public void onChange(boolean selfChange) {
        super.onChange(selfChange);
        Cursor cursor = mContentResolver.query(Uri.parse(SMS_URI_INBOX),
                new String[]{"_id", "address", "body", "read"}, "read=?",
                new String[]{"0"}, "date desc");
        if (cursor != null && cursor.moveToFirst()) {
            String smsbody = cursor.getString(cursor.getColumnIndex("body"));
            if (smsbody.contains(App.APP_NAME_CH)) {
                String regEx = "[^0-9]";
                Pattern p = Pattern.compile(regEx);
                Matcher m = p.matcher(smsbody);
                if (mListener != null)
                    mListener.onReceive(m.replaceAll("").trim());
            }
            cursor.close();
        }
    }

    public interface OnReceiveVerifyCodeSMSListener {
        void onReceive(String vcodeContent);
    }
}