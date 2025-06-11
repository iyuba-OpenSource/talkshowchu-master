package com.iyuba.talkshow.ui.base;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.widget.Toast;

import com.iyuba.talkshow.injection.component.DialogComponent;

public class BaseDialog extends Dialog {

    private final DialogComponent mDialogComponent;
    private final Context mContext;

    public BaseDialog(Context context, int themeResId) {
        super(context, themeResId);
        mContext = context;
        setOwnerActivity((Activity) context);
        mDialogComponent = ((BaseActivity) getOwnerActivity())
                .activityComponent().dialogComponent();
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    public DialogComponent dialogComponent() {
        return mDialogComponent;
    }

    public void showToastShort(int resId) {
        Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
    }

    public void showToastShort(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

    }

    public void showToastLong(int resId) {
        Toast.makeText(mContext, resId, Toast.LENGTH_LONG).show();
    }

    public void showToastLong(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
