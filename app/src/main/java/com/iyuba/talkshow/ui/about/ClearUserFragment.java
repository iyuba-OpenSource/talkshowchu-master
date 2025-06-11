package com.iyuba.talkshow.ui.about;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.Window;
import android.view.WindowManager;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iyuba.module.toolbox.DensityUtil;
import com.iyuba.talkshow.R;

/**
 * Created by carl shen on 2021/3/8
 * New Primary English, new study experience.
 */
public class ClearUserFragment extends DialogFragment implements View.OnClickListener {
    private EditText mPassword;
    private Button toConfirm;
    private Button toCancel;
    private IDialogResultListener resultListener;

    public void setOnResult(IDialogResultListener listener) {
        resultListener = listener;
    }

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        View view= LayoutInflater.from(getActivity()).inflate(R.layout.dialog_clear_user, null);
        mPassword = view.findViewById(R.id.pass_word);
        toCancel = view.findViewById(R.id.clear_cancel);
        toConfirm = view.findViewById(R.id.clear_confirm);
        toCancel.setOnClickListener(this);
        toConfirm.setOnClickListener(this);

        builder.setView(view);
        return builder.create();
    }

    @Override
    public void onStart() {
        super.onStart();
        Window window = getDialog().getWindow();
        WindowManager.LayoutParams params = window.getAttributes();
        params.gravity = Gravity.CENTER;
//        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
//        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = DensityUtil.dp2px(getActivity(),260);
        params.height = DensityUtil.dp2px(getActivity(),200);
        window.setAttributes(params);
        window.setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()){
            case R.id.clear_cancel:
                dismiss();
                break;
            case R.id.clear_confirm:
                if (TextUtils.isEmpty(mPassword.getText().toString())) {
                    if (resultListener != null) {
                        resultListener.onDataResult(null);
                    }
                    return;
                }
                if (mPassword.getText().toString().length() < 6 || mPassword.getText().toString().length() > 20) {
                    if (resultListener != null) {
                        resultListener.onDataResult(mPassword.getText().toString());
                    }
                    return;
                }
                if (resultListener != null) {
                    resultListener.onDataResult(mPassword.getText().toString());
                }
                dismiss();
                break;
        }
    }

}
