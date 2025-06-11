package com.iyuba.talkshow.ui.widget;

import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.view.View;
import android.view.ViewGroup.LayoutParams;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.DialogCustomBinding;
import com.iyuba.talkshow.ui.base.BaseDialog;


public class CustomDialog extends BaseDialog {


	private Context mContext;
	private String mTitle;
	private String message;
	private String mPositiveBtnText;
	private String mNegativeBtnText;
	private final View mCustomerView;

	private DialogInterface.OnClickListener mPositiveBtnClickListener;
	private DialogInterface.OnClickListener mNegativeBtClickListener;

	public CustomDialog(Context context, int theme) {
		super(context, theme);
		this.mCustomerView = onCreateCustomerView(context);
	}

	public void onPositiveBtnClick() {
		if (mPositiveBtnClickListener != null) {
			mPositiveBtnClickListener.onClick(this, DialogInterface.BUTTON_POSITIVE);
		}
	}

	public void onNegativeBtnClick() {
		if (mNegativeBtClickListener != null) {
			mNegativeBtClickListener.onClick(this, DialogInterface.BUTTON_POSITIVE);
		}
	}

	DialogCustomBinding binding ;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		binding = DialogCustomBinding.inflate(getLayoutInflater());
		setContentView(binding.getRoot());

		if (mTitle != null && mTitle.length() != 0) {
			binding.title.setText(mTitle);
		} else {
			binding.title.setVisibility(View.GONE);
		}

		if (mPositiveBtnText != null) {
			binding.positiveButton.setText(mPositiveBtnText);
		} else {
			binding.positiveButton.setVisibility(View.GONE);
		}

		if (mNegativeBtnText != null) {
			binding.negativeButton.setText(mNegativeBtnText);
		} else {
			binding.negativeButton.setVisibility(View.GONE);
		}

		if (message != null) {
			binding.message.setText(message);
		} else if (mCustomerView != null) {
			binding.content.removeAllViews();
			onBindView(getContext());
			binding.content.addView(mCustomerView,
					new LayoutParams(LayoutParams.WRAP_CONTENT, LayoutParams.WRAP_CONTENT));
		}
		setClick();
	}

	private void setClick() {
		binding.positiveButton.setOnClickListener(v -> onPositiveBtnClick());
		binding.negativeButton.setOnClickListener(v -> onNegativeBtnClick());
	}


	public CustomDialog setMessage(String message) {
		this.message = message;
		return this;
	}

	public CustomDialog setMessage(int resId) {
		this.message = (String) mContext.getText(resId);
		return this;
	}

	public CustomDialog setTitle(String mTitle) {
		this.mTitle = mTitle;
		return this;
	}


	public View onCreateCustomerView(Context context) {
		return null;
	}

	public void onBindView(Context context) {}

	public CustomDialog setPositiveButton(int resId, DialogInterface.OnClickListener listener) {
		this.mPositiveBtnText = (String) mContext.getText(resId);
		this.mPositiveBtnClickListener = listener;
		return this;
	}

	public CustomDialog setPositiveButton(String text, DialogInterface.OnClickListener listener) {
		this.mPositiveBtnText = text;
		this.mPositiveBtnClickListener = listener;
		return this;
	}

	public CustomDialog setNegativeButton(int resId, DialogInterface.OnClickListener listener) {
		this.mNegativeBtnText = (String) mContext.getText(resId);
		this.mNegativeBtClickListener = listener;
		return this;
	}

	public CustomDialog setNegativeButton(String text, DialogInterface.OnClickListener listener) {
		this.mNegativeBtnText = text;
		this.mNegativeBtClickListener = listener;
		return this;
	}

}
