package com.iyuba.talkshow.lil.junior.choose;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.databinding.ItemCourseChosseBinding;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;

import java.util.List;

public class JuniorChooseAdapter extends RecyclerView.Adapter<JuniorChooseAdapter.ViewHolder> {

    private Context context ;
    private List<SeriesData> dataBeans;

    CourseCallback courseCallback ;


    public JuniorChooseAdapter(List<SeriesData> dataBeans) {
        this.dataBeans = dataBeans;
    }

    public List<SeriesData> getDataBeans() {
        return dataBeans;
    }

    public void setDataBeans(List<SeriesData> dataBeans) {
        this.dataBeans.clear();
        this.dataBeans.addAll(dataBeans);
        notifyDataSetChanged();
    }


    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int i) {
        ItemCourseChosseBinding binding = ItemCourseChosseBinding.inflate(LayoutInflater.from(viewGroup.getContext()),viewGroup,false);
        this.context = viewGroup.getContext();
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder viewHolder, int position) {
        viewHolder.setItem(dataBeans.get(position),position);
    }

    @Override
    public int getItemCount() {
        return dataBeans.size() ;
    }


    public class ViewHolder extends RecyclerView.ViewHolder {


        private TextView title;
        private TextView views;
        private ImageView image;

        public ViewHolder(@NonNull ItemCourseChosseBinding binding) {
            super(binding.getRoot());
            title = binding.title ;
            views = binding.views;
            image = binding.image;
        }


        public void setItem(SeriesData dataBean,int position) {
            title.setText(dataBean.getSeriesName().replace("(人教版)", ""));
            views.setText(dataBean.getDescCn());
            String updateTime = String.valueOf(System.currentTimeMillis());
//             Glide.with(context).load(dataBean.getPic()).signature(new StringSignature(updateTime)).placeholder(R.drawable.default_pic_v_new).into(image);
            Glide3Util.loadImg(context,dataBean.getPic(),R.drawable.default_pic_v_new,image);
             setClick(position);
        }

        public void setClick(int position){
            image.setOnClickListener(v -> onViewClicked(position));
            image.setOnLongClickListener(v->onViewLongClicked(position));
        }


//        @OnClick(R.id.image)
        public void onViewClicked(int position) {
            courseCallback.onCourseClicked(Integer.parseInt(dataBeans.get(position).getId()), dataBeans.get(position).getSeriesCount(),
                    Integer.parseInt(dataBeans.get(position).getCategory()), dataBeans.get(position).getSeriesName());
            ((Activity)context).finish();
        }

//        @OnLongClick(R.id.image)
        public boolean onViewLongClicked(int position) {
            courseCallback.onCourseLongClicked(Integer.parseInt(dataBeans.get(position).getId()));
            return true ;
        }
    }
    public interface CourseCallback {
        void onCourseClicked(int series, int count, int category, String title);
        void onCourseLongClicked(int series);
    }

    public void setVoaCallback(CourseCallback mVoaCallback) {
        this.courseCallback = mVoaCallback;
    }
}
