package com.iyuba.talkshow.lil.novel.choose;

import android.os.Bundle;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import com.google.android.material.tabs.TabLayout;
import com.google.android.material.tabs.TabLayoutMediator;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.LayoutViewpager2TabTitleBinding;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;
import com.iyuba.talkshow.lil.help_fix.manager.NovelBookChooseManager;
import com.iyuba.talkshow.lil.help_fix.util.FixUtil;
import com.iyuba.talkshow.lil.help_fix.view.adapter.ViewPager2FragmentAdapter;
import com.iyuba.talkshow.lil.help_fix.view.dialog.BookTypeDialog;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingFragment;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.StackUtil;
import com.iyuba.talkshow.lil.novel.choose.book.ChooseNovelBookFragment;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 小说选书界面
 * @date: 2023/7/3 17:33
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NovelChooseFragment extends BaseViewBindingFragment<LayoutViewpager2TabTitleBinding> {

    //书本类型弹窗
    private BookTypeDialog novelBookTypeDialog;
    //当前展示的书籍类型
    private String curBookType;
    //适配器
    private ViewPager2FragmentAdapter novelAdapter;

    public static NovelChooseFragment getInstance(){
        NovelChooseFragment fragment = new NovelChooseFragment();
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initToolbar();
        initView();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    /************初始化****************/
    private void initToolbar(){
        binding.toolbar.tvTopCenter.setText(FixUtil.transBookTypeToStr(NovelBookChooseManager.getInstance().getBookType()));
        binding.toolbar.imgTopLeft.setVisibility(View.VISIBLE);
        binding.toolbar.imgTopLeft.setImageResource(R.mipmap.img_back);
        binding.toolbar.imgTopLeft.setOnClickListener(v->{
            StackUtil.getInstance().finishCur();
        });
        binding.toolbar.imgTopRight.setVisibility(View.VISIBLE);
        binding.toolbar.imgTopRight.setImageResource(R.drawable.ic_book_more);
        binding.toolbar.imgTopRight.setOnClickListener(v->{
            showBookTypeDialog();
        });
    }

    private void initView(){
        curBookType = NovelBookChooseManager.getInstance().getBookType();
        int bookLevel = NovelBookChooseManager.getInstance().getBookLevel();
        refreshView(curBookType,bookLevel);
    }

    /**********************刷新数据********************/
    //刷新视图
    private void refreshView(String bookType,int bookLevel){
        String[] tabArray = getTabArray(bookType);

        List<Fragment> list = new ArrayList<>();
        for (int i = 0; i < tabArray.length; i++) {
            list.add(ChooseNovelBookFragment.getInstance(bookType,i));
        }

        //重置vp样式
        novelAdapter = new ViewPager2FragmentAdapter(getActivity());
        binding.viewPager2.setAdapter(novelAdapter);
        //刷新vp数据
        novelAdapter.refreshList(list);
        binding.viewPager2.setOffscreenPageLimit(list.size());
        binding.viewPager2.setCurrentItem(bookLevel,false);
        //绑定tab
        binding.tabLayout.setTabMode(TabLayout.MODE_SCROLLABLE);
        new TabLayoutMediator(binding.tabLayout, binding.viewPager2, new TabLayoutMediator.TabConfigurationStrategy() {
            @Override
            public void onConfigureTab(@NonNull TabLayout.Tab tab, int position) {
                tab.setText(tabArray[position]);
            }
        }).attach();
    }

    /***************辅助功能*******************/
    //显示书籍弹窗
    private void showBookTypeDialog(){
        List<Pair<String,String>> list = new ArrayList<>();
        list.add(new Pair<>(TypeLibrary.BookType.newCamstoryColor,FixUtil.transBookTypeToStr(TypeLibrary.BookType.newCamstoryColor)));
        list.add(new Pair<>(TypeLibrary.BookType.newCamstory,FixUtil.transBookTypeToStr(TypeLibrary.BookType.newCamstory)));
        list.add(new Pair<>(TypeLibrary.BookType.bookworm,FixUtil.transBookTypeToStr(TypeLibrary.BookType.bookworm)));

        if (novelBookTypeDialog ==null){
            novelBookTypeDialog = new BookTypeDialog(getActivity());
            novelBookTypeDialog.create();
        }
        novelBookTypeDialog.setTitle("选择书籍类型");
        novelBookTypeDialog.setData(curBookType,list);
        novelBookTypeDialog.setListener(new OnSimpleClickListener<Pair<String,String>>() {
            @Override
            public void onClick(Pair<String,String> pair) {
                if (curBookType.equals(pair.first)){
                    return;
                }

                curBookType = pair.first;
                int bookLevel = NovelBookChooseManager.getInstance().getBookLevel();
                String[] tabArray = getTabArray(pair.first);
                if (tabArray.length<bookLevel){
                    bookLevel = 0;
                }

                binding.toolbar.tvTopCenter.setText(pair.second);
                refreshView(pair.first,bookLevel);
            }
        });
        novelBookTypeDialog.show();
    }

    //获取tab的数据
    private String[] getTabArray(String bookType){
        String[] levelArray = new String[]{};
        if (bookType.equals(TypeLibrary.BookType.bookworm)){
            levelArray = ResUtil.getInstance().getStrArray(R.array.BookwormLevel);
        }else if (bookType.equals(TypeLibrary.BookType.newCamstory)){
            levelArray = ResUtil.getInstance().getStrArray(R.array.NewCamstory);
        }else if (bookType.equals(TypeLibrary.BookType.newCamstoryColor)){
            levelArray = ResUtil.getInstance().getStrArray(R.array.NewCamstoryColor);
        }
        return levelArray;
    }
}
