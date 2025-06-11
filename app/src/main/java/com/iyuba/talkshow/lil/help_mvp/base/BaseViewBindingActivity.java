package com.iyuba.talkshow.lil.help_mvp.base;

import android.os.Bundle;
import android.view.LayoutInflater;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.viewbinding.ViewBinding;

import com.iyuba.talkshow.lil.help_mvp.mvp.BaseView;
import com.iyuba.talkshow.lil.help_mvp.util.StackUtil;

import java.lang.reflect.Method;
import java.lang.reflect.ParameterizedType;
import java.lang.reflect.Type;

/**
 * @desction: 基础activity(ViewBinding类型)
 * @date: 2023/3/15 17:55
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class BaseViewBindingActivity<VB extends ViewBinding> extends BaseStackActivity implements BaseView {

    protected VB binding;
    protected AppCompatActivity context;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        this.context = this;

        try {
            Type type = this.getClass().getGenericSuperclass();
            if (type instanceof ParameterizedType){
                Class clz = (Class<VB>) ((ParameterizedType)type).getActualTypeArguments()[0];
                Method method = clz.getMethod("inflate", LayoutInflater.class);
                binding = (VB) method.invoke(null,this.getLayoutInflater());
                setContentView(binding.getRoot());
            }
        }catch (Exception e){
            e.printStackTrace();
        }

        //加入堆栈
        StackUtil.getInstance().add(context);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();

        binding = null;
        //移除堆栈
        StackUtil.getInstance().finish(context);
    }
}
