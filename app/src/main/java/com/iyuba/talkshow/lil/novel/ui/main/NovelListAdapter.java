package com.iyuba.talkshow.lil.novel.ui.main;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.databinding.ItemChapterNovelBinding;
import com.iyuba.talkshow.lil.help_fix.data.bean.BookChapterBean;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;

import java.util.List;

/**
 * @title:
 * @date: 2023/7/3 17:34
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NovelListAdapter extends RecyclerView.Adapter<NovelListAdapter.LessonHolder> {

    private Context context;
    private List<BookChapterBean> list;

    public NovelListAdapter(Context context, List<BookChapterBean> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public LessonHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChapterNovelBinding binding = ItemChapterNovelBinding.inflate(LayoutInflater.from(context),parent,false);
        return new LessonHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull LessonHolder holder, int position) {
        if (holder==null){
            return;
        }

        BookChapterBean bean = list.get(position);
        //这里处理下，因为有的内容开头是chatper的数据，则从标题中直接获取数据，不要自己定义数据了
        //可以显示的标题
        String showTitle = bean.getTitleEn().replace("\r\n","").trim();
        //需要处理的标题
        String checkTitle = showTitle.toLowerCase();
        /**
         * 三个方法
         * 1.Chapter 1这种样式或者chapter 1这种样式
         * 2.Chapter1或者chapter1这种样式
         * 3.没有chapter的数据
         */

        //tag标题
        String chapterIndex = "";
        //需要处理的标题数组
        String[] chapterTitleArray = checkTitle.split(" ");

        //实际上的tag标题
        String oldChapterIndex = "";
        //实际上需要处理的标题数据
        String[] oldChapterTitleArray = showTitle.split(" ");

        if (checkTitle.startsWith("chapter")){

            if (chapterTitleArray.length>=2){
                //判断第二个数据是什么
                String secondStr = chapterTitleArray[1];
                String showIndex = "";
                try {
                    //第二个是数字
                    int tempIndex = Integer.parseInt(secondStr);
                    showIndex = "Chapter "+tempIndex;
                    oldChapterIndex = oldChapterTitleArray[0]+" "+oldChapterTitleArray[1];
                }catch (Exception e){
                    //第二个不是数字
                    String tempShowStr = chapterTitleArray[0];
                    String tempIndex = tempShowStr.toLowerCase().replace("chapter","");
                    showIndex = "Chapter "+tempIndex;
                    oldChapterIndex = oldChapterTitleArray[0];
                }
                chapterIndex = showIndex;
            }else {
                String tempIndex = checkTitle.replace("chapter","");
                chapterIndex = "Chapter "+tempIndex;
                //这里只有一个的暂时不处理了
//                oldChapterIndex = showTitle;
            }
        }else {
            chapterIndex = "Chapter "+(position+1);
        }
        holder.indexView.setText(chapterIndex);

        //出现标题显示
        String chapterName = bean.getTitleEn();
        if (chapterName.startsWith(oldChapterIndex)){
            chapterName = chapterName.replace(oldChapterIndex,"");
        }

        //这里因为level3的数据存在问题，多了一个\r\n(数据存在问题真是服气了)
        holder.titleView.setText(chapterName.replace("\r\n","").trim());
        holder.titleCnView.setText(bean.getTitleCn().replace("\r\n","").trim());

        holder.itemView.setOnClickListener(v->{
            if (listener!=null){
                listener.onClick(bean);
            }
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class LessonHolder extends RecyclerView.ViewHolder{

        private TextView indexView;
        private TextView titleView;
        private TextView titleCnView;

        public LessonHolder(ItemChapterNovelBinding binding){
            super(binding.getRoot());

            indexView = binding.index;
            titleView = binding.title;
            titleCnView = binding.titleCn;
        }
    }

    //回调
    private OnSimpleClickListener<BookChapterBean> listener;

    public void setListener(OnSimpleClickListener<BookChapterBean> listener) {
        this.listener = listener;
    }

    //刷新数据
    public void refreshData(List<BookChapterBean> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }
}
