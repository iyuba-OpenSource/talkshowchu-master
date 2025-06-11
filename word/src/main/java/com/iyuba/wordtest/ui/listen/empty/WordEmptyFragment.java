package com.iyuba.wordtest.ui.listen.empty;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.iyuba.wordtest.databinding.FragmentWordEmptyBinding;

/**
 * 空界面
 */
public class WordEmptyFragment extends Fragment {

    //展示类型
    public static final String TAG_TYPE = "type";
    private String showType;

    //布局样式
    private FragmentWordEmptyBinding binding;

    public static WordEmptyFragment getInstance(String type){
        WordEmptyFragment emptyFragment = new WordEmptyFragment();
        Bundle bundle = new Bundle();
        bundle.putString(TAG_TYPE,type);
        emptyFragment.setArguments(bundle);
        return emptyFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        showType = getArguments().getString(TAG_TYPE);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentWordEmptyBinding.inflate(LayoutInflater.from(getActivity()),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.emptyTypeView.setText("未设置该类型界面("+showType+")");
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }
}
