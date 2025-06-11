package com.iyuba.talkshow.ui.deletlesson;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.databinding.ItemLessonDeleteBinding;

import java.util.ArrayList;
import java.util.List;


class LessonDeleteAdapter  extends RecyclerView.Adapter<LessonDeleteAdapter.ViewHolder> {

    CourseCallback courseCallback ;
    private final List<SeriesData> bookList = new ArrayList<>();

    private final List<String> checkList = new ArrayList<>();

    public void setBookList(List<SeriesData> books){
        bookList.clear();
        if (null!= books){
            bookList.addAll(books);
        }
        notifyDataSetChanged();
    }

    @NonNull
    @Override
    public ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemLessonDeleteBinding binding  = ItemLessonDeleteBinding.inflate(LayoutInflater.from(parent.getContext()),
                parent,false);
        return new ViewHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull ViewHolder holder, int position) {
        holder.setItem(bookList.get(position));
    }

    @Override
    public int getItemCount() {
        return bookList.size();
    }

    public class ViewHolder extends RecyclerView.ViewHolder {

        ImageView ivCheck ;
        TextView bookName ;
        ImageView bookImage ;
        public ViewHolder(@NonNull ItemLessonDeleteBinding binding) {
            super(binding.getRoot());
            ivCheck = binding.check;
            bookName = binding.bookname;
            bookImage = binding.image;
        }

        public void setItem(SeriesData data) {
            if (data == null) {
                Log.e("LessonDeletePresenter", "setItem SeriesData is null.");
                return;
            }
            bookName.setText(data.getSeriesName());
            if (checkList.contains(data.getId())){
                ivCheck.setImageResource(R.drawable.checkbox_checked);
            }else{
                ivCheck.setImageResource(0);
            }
            Glide.with(ivCheck.getContext()).load(data.getPic()).centerCrop().placeholder(R.drawable.default_pic_v_new).into(bookImage);
            itemView.setOnClickListener(v -> setClick(data));
            itemView.setOnLongClickListener(v -> {
                return setLongClick(data);
            });
        }

        public void setClick(SeriesData data){
            Log.e("LessonDeletePresenter", "setClick bookId " + data.getId());
            if (courseCallback != null) {
                courseCallback.onCourseClicked(data);
            }
        }
        public boolean setLongClick(SeriesData data){
            if (checkList.contains(data.getId())){
                checkList.remove(data.getId());
            }else{
                checkList.add(data.getId());
            }
            notifyDataSetChanged();
            return true;
        }
    }

    public List<String> getCheckList(){
        return checkList ;
    }

    interface CourseCallback {
        void onCourseClicked(SeriesData data);
    }

    public void setCourseCallback(CourseCallback mVoaCallback) {
        this.courseCallback = mVoaCallback;
    }
}
