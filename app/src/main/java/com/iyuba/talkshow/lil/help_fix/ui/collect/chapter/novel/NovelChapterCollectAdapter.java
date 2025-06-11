package com.iyuba.talkshow.lil.help_fix.ui.collect.chapter.novel;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.databinding.ItemChapterNovelBinding;
import com.iyuba.talkshow.lil.help_fix.data.bean.BookChapterBean;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.ChapterCollectEntity;
import com.iyuba.talkshow.lil.help_fix.util.FixUtil;

import java.util.List;

/**
 * @title:
 * @date: 2023/7/18 15:41
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class NovelChapterCollectAdapter extends RecyclerView.Adapter<NovelChapterCollectAdapter.NovelCollectHolder> {

    private Context context;
    private List<ChapterCollectEntity> list;

    public NovelChapterCollectAdapter(Context context, List<ChapterCollectEntity> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public NovelCollectHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemChapterNovelBinding binding = ItemChapterNovelBinding.inflate(LayoutInflater.from(context),parent,false);
        return new NovelCollectHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull NovelCollectHolder holder, int position) {
        if (holder==null){
            return;
        }

        ChapterCollectEntity bean = list.get(position);
        String types = FixUtil.transBookTypeToStr(bean.types);
        holder.indexView.setText(types);
        String chapterName = bean.title;

        //这里因为level3的数据存在问题，多了一个\r\n(数据存在问题真是服气了)
        holder.titleView.setText(chapterName.replace("\r\n","").trim());
        holder.titleCnView.setText(bean.desc.replace("\r\n","").trim());

        holder.itemView.setOnClickListener(v->{
            if (singleClickListener!=null){
                singleClickListener.onClick(bean);
            }
        });
        holder.itemView.setOnLongClickListener(v->{
            if (longClickListener!=null){
                longClickListener.onClick(bean);
            }
            return true;
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class NovelCollectHolder extends RecyclerView.ViewHolder{

        private TextView indexView;
        private TextView titleView;
        private TextView titleCnView;

        public NovelCollectHolder(ItemChapterNovelBinding binding){
            super(binding.getRoot());

            indexView = binding.index;
            titleView = binding.title;
            titleCnView = binding.titleCn;
        }
    }

    //刷新数据
    public void refreshData(List<ChapterCollectEntity> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //点击回调
    public OnSimpleClickListener<ChapterCollectEntity> singleClickListener;

    public void setSingleClickListener(OnSimpleClickListener<ChapterCollectEntity> singleClickListener) {
        this.singleClickListener = singleClickListener;
    }

    //长按回调
    public OnSimpleClickListener<ChapterCollectEntity> longClickListener;

    public void setLongClickListener(OnSimpleClickListener<ChapterCollectEntity> longClickListener) {
        this.longClickListener = longClickListener;
    }
}
