package com.iyuba.talkshow.newce.study;

import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentPagerAdapter;
import androidx.fragment.app.FragmentTransaction;

import java.util.List;

public class StudyAdapter extends FragmentPagerAdapter {

    private List<Fragment> list;
    private FragmentManager manager;

    public StudyAdapter(@NonNull FragmentManager fm,List<Fragment> list) {
        super(fm);

        this.manager = fm;
        this.list = list;
    }


    @NonNull
    @Override
    public Fragment getItem(int position) {
        return list.get(position);
    }

    @Override
    public int getCount() {
        return list==null?0:list.size();
    }

    //刷新数据
    public void refreshData(List<Fragment> refreshList){
//        if (list!=null){
//            FragmentTransaction transaction = manager.beginTransaction();
//            for (Fragment fragment:list){
//                transaction.remove(fragment);
//            }
//            transaction.commit();
////            transaction = null;
////            manager.executePendingTransactions();
//        }

        this.list = refreshList;
        notifyDataSetChanged();
    }

    /**
     * 因为该适配器涉及到工厂间切换，动态增删fragment，所以每次需要清空缓存
     * @param container
     * @param position
     * @return
     */
    @NonNull
    @Override
    public Object instantiateItem(@NonNull ViewGroup container, int position) {
        FragmentTransaction ft = manager.beginTransaction();
        for (int i = 0; i < getCount(); i++) {//通过遍历清除所有缓存
            final long itemId = getItemId(i);
            //得到缓存fragment的名字
            String name = makeFragmentName(container.getId(), itemId);
            //通过fragment名字找到该对象
            Fragment fragment = (Fragment) manager.findFragmentByTag(name);
            if (fragment != null) {
                //移除之前的fragment
                ft.remove(fragment);
            }
        }
        //重新添加新的fragment:最后记得commit
        ft.add(container.getId(), getItem(position)).attach(getItem(position)).commit();
        return getItem(position);
    }

    /**
     * 得到缓存fragment的名字
     * @param viewId
     * @param id
     * @return
     */
    private String makeFragmentName(int viewId, long id) {
        return "android:switcher:" + viewId + ":" + id;
    }
}
