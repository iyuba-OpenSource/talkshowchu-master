package com.iyuba.talkshow.ui.user.edit.dialog;

import android.app.Application;
import android.content.Context;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.University;
import com.iyuba.talkshow.databinding.DialogSchoolBinding;
import com.iyuba.talkshow.ui.base.BaseDialog;

import java.util.List;

import javax.inject.Inject;


/**
 * Created by Administrator on 2016/12/24/024.
 */

public class SchoolDialog extends BaseDialog implements SchoolMvpView {
    private OnTextChangeListener mOnTextChangeListener;
    private OnBtnClickListener mOnBtnClickListener;
    private OnSelectData onSelectData;
    @Inject
    SchoolAdapter mAdapter;
    @Inject
    SchoolPresenter mPresenter;
    @Inject
    Application mApplication;

    DialogSchoolBinding binding ;

    public SchoolDialog(Context context, int themeResId) {
        super(context, themeResId);
        binding = DialogSchoolBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        dialogComponent().inject(this);
        mPresenter.attachView(this);
        binding.schoolList.setAdapter(mAdapter);
        LinearLayoutManager layoutManager = new LinearLayoutManager(mApplication);
        layoutManager.setOrientation(LinearLayoutManager.VERTICAL);
        binding.schoolList.setLayoutManager(layoutManager);
        binding.schoolList.setHasFixedSize(true);// 如果Item够简单，高度是确定的，打开FixSize将提高性能。
        binding.schoolList.setItemAnimator(new DefaultItemAnimator());// 设置Item默认动画，加也行，不加也行。
        mPresenter.getallschools();
        mAdapter.setOnschoolItemSelectListener(new SchoolAdapter.OnSchoolItemSelectListener() {
            @Override
            public void OnSchoolItemClick(View view, int position, String data) {
                if(onSelectData!=null){
                    onSelectData.onSelectData(data);
                    dismiss();
                }
            }

        });
    }

    public void setOnSelectData(OnSelectData onSelectData) {
        this.onSelectData = onSelectData;
    }

    @Override
    public void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        mPresenter.detachView();
    }

//    @OnTextChanged(R.id.search_et)
    public void onSearchTextChange(CharSequence arg0) {
        if(mOnTextChangeListener!=null)
        mOnTextChangeListener.onTextChange(String.valueOf(arg0));
        if(!"".equals(String.valueOf(arg0)))
        mPresenter.search(String.valueOf(arg0));
    }

//    @OnClick(R.id.clear_btn)
    public void onClearClick() {
        if(mOnBtnClickListener!=null)
        mOnBtnClickListener.onClearClick();
        binding.searchEt.setText("");
        mPresenter.getallschools();
    }

//    @OnClick(R.id.search_btn)
    public void onSearchBtnClick() {
        if(mOnBtnClickListener!=null)
        mOnBtnClickListener.onSearchClick();
        mPresenter.search(binding.searchEt.getText().toString());
    }

//    @OnClick(R.id.cancel_btn)
    public void onCancelBtnClick() {
        binding.searchEt.setText("");
        dismiss();
    }

    @Override
    public void showUniversities(List<University> list) {
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(getContext(), resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showAllUniversities(List<University> list) {
        mAdapter.setData(list);
        mAdapter.notifyDataSetChanged();
    }

    public void setOnTextChangeListener(OnTextChangeListener mOnTextChangeListener) {
        this.mOnTextChangeListener = mOnTextChangeListener;
    }

    public void setOnBtnClickListener(OnBtnClickListener mOnBtnClickListener) {
        this.mOnBtnClickListener = mOnBtnClickListener;
    }

    public interface OnTextChangeListener {
        void onTextChange(String text);
    }
    public interface OnSelectData{
        void onSelectData(String data);
    }
    public interface OnBtnClickListener {
        void onClearClick();

        void onSearchClick();
    }
}
