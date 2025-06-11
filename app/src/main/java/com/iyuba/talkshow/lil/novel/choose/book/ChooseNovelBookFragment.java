package com.iyuba.talkshow.lil.novel.choose.book;

import android.os.Bundle;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.databinding.LayoutRefreshTitleBinding;
import com.iyuba.talkshow.lil.help_fix.data.event.RefreshDataEvent;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.NovelBookChooseManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Novel_book;
import com.iyuba.talkshow.lil.help_fix.ui.choose.ChooseActivity;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingFragment;
import com.iyuba.talkshow.lil.help_mvp.util.StackUtil;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.List;

/**
 * @title: 选书-小说-内容界面
 * @date: 2023/4/27 13:58
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class ChooseNovelBookFragment extends BaseViewBindingFragment<LayoutRefreshTitleBinding> implements ChooseNovelBookView{

    //是否已经加载
    private boolean hasLoaded = false;

    private ChooseNovelBookPresenter bookPresenter;
    private ChooseNovelBookAdapter bookAdapter;

    public static ChooseNovelBookFragment getInstance(String bookType,int bookLevel){
        ChooseNovelBookFragment fragment = new ChooseNovelBookFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.type,bookType);
        bundle.putInt(StrLibrary.level,bookLevel);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        bookPresenter = new ChooseNovelBookPresenter();
        bookPresenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initList();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        bookPresenter.detachView();
    }

    @Override
    public void onResume() {
        super.onResume();

//        String bookType = getArguments().getString(StrLibrary.type);
//        int level = getArguments().getInt(StrLibrary.level);
//        List<BookEntity_novel> list = DataManager.getNovelBookByLevelFromDB(bookType,String.valueOf(level));
//        if (list!=null&&list.size()>0){
//            showBookData(list);
//        }else {
//            binding.refreshLayout.autoRefresh();
//        }

        if (!hasLoaded){
            hasLoaded = true;

            //刷新数据
            binding.refreshLayout.autoRefresh();
        }
    }

    private void initList(){
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setOnRefreshListener(refreshLayout -> {
            if (!NetworkUtil.isConnected(getActivity())){
                binding.refreshLayout.finishRefresh(false);
                ToastUtil.showToast(getActivity(),"请链接网络后下拉刷新重试~");
                return;
            }

            String bookType = getArguments().getString(StrLibrary.type);
            int level = getArguments().getInt(StrLibrary.level);

            bookPresenter.loadNovelBookData(level,bookType);
        });

        bookAdapter = new ChooseNovelBookAdapter(getActivity(),new ArrayList<>());
        GridLayoutManager manager = new GridLayoutManager(getActivity(),3);
        binding.recyclerView.setLayoutManager(manager);
        binding.recyclerView.setAdapter(bookAdapter);
        bookAdapter.setListener(novel_book -> {
            //设置选中的内容
            NovelBookChooseManager.getInstance().setBookType(getArguments().getString(StrLibrary.type));
            NovelBookChooseManager.getInstance().setBookLevel(Integer.parseInt(novel_book.getLevel()));
            NovelBookChooseManager.getInstance().setBookId(novel_book.getOrderNumber());
            NovelBookChooseManager.getInstance().setBookName(novel_book.getBookname_cn());
            EventBus.getDefault().post(new RefreshDataEvent(TypeLibrary.RefreshDataType.novel));

            StackUtil.getInstance().finish(ChooseActivity.class);
        });
    }

    @Override
    public void showBookData(List<Novel_book> list) {
        if (list!=null){
            binding.refreshLayout.finishRefresh(true);
            if (list.size()>0){
                bookAdapter.refreshData(list);
            }else {
                ToastUtil.showToast(getActivity(),"暂无该类型的书籍数据");
            }
        }else {
            binding.refreshLayout.finishRefresh(false);
            ToastUtil.showToast(getActivity(),"加载该类型的书籍数据失败，请重试～");
        }
    }
}
