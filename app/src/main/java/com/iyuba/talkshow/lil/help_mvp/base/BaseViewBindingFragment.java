package com.iyuba.talkshow.lil.help_mvp.base;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.viewbinding.ViewBinding;

import com.iyuba.talkshow.lil.help_mvp.mvp.BaseView;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @desction: 基础的fragment(ViewBinding类型)
 * @date: 2023/3/16 00:37
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class BaseViewBindingFragment<VB extends ViewBinding> extends Fragment implements BaseView {

    protected VB binding;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        try {
            Type type = this.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType){
                Class clz = (Class<VB>) ((ParameterizedType)type).getActualTypeArguments()[0];
                Method method = clz.getMethod("inflate",LayoutInflater.class);
                binding = (VB) method.invoke(null,this.getLayoutInflater());
            }
        }catch (Exception e){
            e.printStackTrace();
        }
        return binding==null?null:binding.getRoot();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        binding = null;
    }
}
