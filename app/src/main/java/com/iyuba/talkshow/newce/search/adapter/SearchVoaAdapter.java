package com.iyuba.talkshow.newce.search.adapter;

import android.content.Context;
import android.content.Intent;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.ItemSearchVoaBinding;
import com.iyuba.talkshow.event.MainPlayerEvent;
import com.iyuba.talkshow.newce.search.util.SearchFileHelper;
import com.iyuba.talkshow.newce.study.StudyActivity;

import org.greenrobot.eventbus.EventBus;

import java.util.List;

/**
 * 搜索-文章适配器
 */
public class SearchVoaAdapter extends RecyclerView.Adapter<SearchVoaAdapter.VoaHolder> {

    private Context context;
    private List<Voa> list;

    public SearchVoaAdapter(Context context, List<Voa> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public VoaHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchVoaBinding binding = ItemSearchVoaBinding.inflate(LayoutInflater.from(context),parent,false);
        return new VoaHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull VoaHolder holder, int position) {
        if (holder==null){
            return;
        }

        //数据
        Voa bean = list.get(position);
        //图片
        if (TextUtils.isEmpty(bean.pic())){
            holder.image.setVisibility(View.GONE);
        }else {
            holder.image.setVisibility(View.VISIBLE);
            SearchFileHelper.loadImg(context,bean.pic(), holder.image);
        }
        //标题
        if (TextUtils.isEmpty(bean.titleCn())){
            holder.title.setVisibility(View.GONE);
        }else {
            holder.title.setVisibility(View.VISIBLE);
            holder.title.setText(bean.titleCn());
        }
        //页码
        if (TextUtils.isEmpty(bean.title())){
            holder.titleCn.setVisibility(View.GONE);
        }else {
            holder.titleCn.setVisibility(View.VISIBLE);
            holder.titleCn.setText(bean.title());
        }

        holder.itemView.setOnClickListener(v->{
            if (onVoaListener!=null){
                onVoaListener.onStopPlay();
            }

            //跳转
            Intent intent = StudyActivity.buildIntent(context,bean,StudyActivity.title_default,0,false,-1);
            context.startActivity(intent);
            //设置主界面播放关闭
            EventBus.getDefault().post(new MainPlayerEvent());
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class VoaHolder extends RecyclerView.ViewHolder{

        private ImageView image;
        private TextView title;
        private TextView titleCn;

        public VoaHolder(ItemSearchVoaBinding binding){
            super(binding.getRoot());

            image = binding.image;
            title = binding.title;
            titleCn = binding.titleCn;
        }
    }

    //刷新数据
    public void refreshData(List<Voa> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    /**************************操作回调************************/
    //回调接口
    private OnVoaListener onVoaListener;

    public interface OnVoaListener{
        //停止音频
        void onStopPlay();
    }

    public void setOnVoaListener(OnVoaListener onVoaListener) {
        this.onVoaListener = onVoaListener;
    }
}
