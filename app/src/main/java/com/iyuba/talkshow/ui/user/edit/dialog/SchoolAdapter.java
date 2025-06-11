package com.iyuba.talkshow.ui.user.edit.dialog;

import androidx.recyclerview.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.University;
import com.iyuba.talkshow.databinding.ItemSchoolBinding;
import com.iyuba.talkshow.ui.user.me.dubbing.MyDubbingActivity;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;


public class SchoolAdapter extends RecyclerView.Adapter<SchoolAdapter.SchoolViewHolder> {
    private List<University> mList;
    private OnSchoolItemSelectListener OnschoolItemSelectListener;
    ItemSchoolBinding binding ;
    @Inject
    public SchoolAdapter() {
        mList = new ArrayList<>();
    }

    @Override
    public SchoolViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        binding = ItemSchoolBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
        return new SchoolViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(SchoolViewHolder holder, final int position) {
        final University university = mList.get(position);
        binding.schoolname.setText(university.province());
        binding.province.setText(university.uniName().trim());
        if(OnschoolItemSelectListener!=null){
            holder.itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    OnschoolItemSelectListener.OnSchoolItemClick(v,position,university.uniName());
                }
            });

        }
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
        return mList.size();
    }

    public void setOnschoolItemSelectListener(OnSchoolItemSelectListener onschoolItemSelectListener) {
        OnschoolItemSelectListener = onschoolItemSelectListener;
    }

    public void setData(List<University> list) {
        mList = list;
    }

    class SchoolViewHolder extends RecyclerView.ViewHolder {

        public SchoolViewHolder(View itemView) {
            super(itemView);
        }
    }

    public interface OnSchoolItemSelectListener{
        void OnSchoolItemClick(View view,int position,String data);

    }
}
