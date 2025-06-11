package com.iyuba.talkshow.ui.courses.coursedetail;

import android.content.Context;
import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.PostItem;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.ItemCourseChosseBinding;
import com.iyuba.talkshow.databinding.ItemCourseDetailBinding;
import com.iyuba.talkshow.ui.detail.DetailActivity;
import com.iyuba.talkshow.ui.widget.FallingView;

import java.util.ArrayList;
import java.util.List;


public class CourseDetailAdapter extends RecyclerView.Adapter<CourseDetailAdapter.ViewHolder> {

    public List<Voa> getVoas() {
        return voas;
    }

    public void setVoas(List<Voa> voas) {

        this.voas.clear();
        this.voas.addAll(voas);
        notifyDataSetChanged();
    }

    private final List<Voa> voas = new ArrayList<>();
    private Context context;
    ItemCourseDetailBinding binding ;


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
         binding = ItemCourseDetailBinding.inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup, false);
        this.context = viewGroup.getContext();
        return new ViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setItem(voas.get(position));
        viewHolder.setClick(position);
    }

    @Override
    public int getItemCount() {
        return voas.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }

        public void setItem(Voa dataBean) {
            binding.title.setText(dataBean.titleCn());
            binding.views.setText("浏览量"+ dataBean.readCount());
            binding.desc.setText(dataBean.title());
            Glide.with(context).load(dataBean.pic()).into( binding.image);
        }


        public void onViewClicked(int position) {
            context.startActivity(DetailActivity.buildIntent(context, voas.get(position),true));
        }

        public void setClick(int position) {
            binding.voa.setOnClickListener(v -> onViewClicked(position));
        }
    }
}
