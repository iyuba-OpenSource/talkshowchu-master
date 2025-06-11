package com.iyuba.talkshow.lil.help_fix.ui.main.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.viewbinding.ViewBinding;

import com.iyuba.talkshow.ui.base.BaseFragment;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @title: 使用旧版本创建
 * @date: 2023/7/13 16:51
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class BaseVBFragment<VB extends ViewBinding> extends BaseFragment {

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
