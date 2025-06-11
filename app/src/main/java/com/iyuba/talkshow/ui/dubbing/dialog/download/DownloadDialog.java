package com.iyuba.talkshow.ui.dubbing.dialog.download;

import android.content.Context;
import android.os.Bundle;
import android.view.Display;
import android.view.Window;
import android.view.WindowManager;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.DialogDownloadBinding;
import com.iyuba.talkshow.ui.base.BaseDialog;


public class DownloadDialog  extends BaseDialog {

    DialogDownloadBinding binding ;
    private OnDownloadListener mOnDownloadListener;

    public DownloadDialog(Context context) {
        super(context, R.style.DialogTheme);
    }

    public DownloadDialog(Context context, int themeResId) {
        super(context, themeResId);
    }

    public void setmOnDownloadListener(OnDownloadListener listener) {
        this.mOnDownloadListener = listener;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = DialogDownloadBinding .inflate(getLayoutInflater());
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

//    @OnClick(R.id.continue_loading_tv)
    void onContinueLoadingClick() {
        if(mOnDownloadListener != null) {
            mOnDownloadListener.onContinue();
        }
    }

//    @OnClick(R.id.cancel_loading_tv)
    void onCancelLoadingClick() {
        if (mOnDownloadListener != null) {
            mOnDownloadListener.onCancel();
        }
    }

    public interface OnDownloadListener {
        void onContinue();

        void onCancel();
    }
}
