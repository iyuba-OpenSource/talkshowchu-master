package com.iyuba.talkshow.ui.dubbing.dialog;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.DialogDubbingBinding;
import com.iyuba.talkshow.ui.base.BaseDialog;
import com.iyuba.talkshow.ui.base.MvpView;


public class DubbingDialog extends BaseDialog implements MvpView {

    DialogDubbingBinding binding ;
    private OnSaveDraftListener mSaveListener;
    private OnContinueDubbingListener mContinueListener;
    private OnReturnDirectListener mReturnListener;

    public DubbingDialog(Context context) {
        super(context, R.style.DialogTheme);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogDubbingBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialogComponent().inject(this);

        WindowManager m = getOwnerActivity().getWindowManager();
        Display defaultDisplay = m.getDefaultDisplay();
        Window window = getWindow();
        window.setBackgroundDrawableResource(R.drawable.dialog_bkg);
        WindowManager.LayoutParams layoutParams = window.getAttributes();
        layoutParams.width = (int) (defaultDisplay.getWidth() * 0.78);
        window.setAttributes(layoutParams);
    }

//    @OnClick(R.id.tv_continue_dubbing)
    void onContinueDubbingClick() {
        dismiss();
        if (mContinueListener != null) {
            mContinueListener.onContinueDubbing();
        }
    }

//    @OnClick(R.id.tv_save_draft)
    void onSaveDraftClick() {
        if (mSaveListener != null) {
            mSaveListener.onSaveDraft();
        }
    }

//    @OnClick(R.id.tv_return_direct)
    void onReturnDirectClick() {
        if (mReturnListener != null) {
            mReturnListener.onReturnDirect();
        }
    }

    @Override
    public void show() {
        super.show();
    }

    @Override
    public void dismiss() {
        super.dismiss();
    }

    public DubbingDialog setOnSaveDraftListener(OnSaveDraftListener mSaveListener) {
        this.mSaveListener = mSaveListener;
        return this;
    }

    public DubbingDialog setOnContinueDubbingListener(OnContinueDubbingListener mContinueListener) {
        this.mContinueListener = mContinueListener;
        return this;
    }

    public DubbingDialog setOnReturnDirectListener(OnReturnDirectListener mReturnListener) {
        this.mReturnListener = mReturnListener;
        return this;
    }

    public interface OnSaveDraftListener {
        void onSaveDraft();
    }

    public interface OnReturnDirectListener {
        void onReturnDirect();
    }

    public interface OnContinueDubbingListener {
        void onContinueDubbing();
    }
}
