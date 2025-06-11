package com.iyuba.talkshow.ui.courses.coursechoose;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.databinding.CourseChooseTitleBinding;
import com.iyuba.talkshow.databinding.CourseChooseTypeBinding;

import java.util.List;

public class CourseTitleAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private TitleCallback callback;
    Context context;


    public CourseTitleAdapter(List<TypeHolder> types) {
        this.types = types;
    }

    private List<TypeHolder> generateTypes(List<TypeHolder> types) {
        for (int i = 0; i <types.size() ; i++) {
            if (types.get(i).getId()==0&& i!= 0){
                if (i%5==0) continue;
                for (int j = 0; j < 5-i%5 ; j++){
                    types.add(i,new TypeHolder(1,""));
                }
            }
        }
        return types ;
    }
    public void SetTitleList(List<TypeHolder> typeList) {
        if (typeList == null) {
            return;
        }
        this.types = typeList;
        if (mActiviteHolder >= this.types.size()) {
            mActiviteHolder = this.types.size() - 1;
        }
        callback.onTitleChoose(mActiviteHolder, this.types.get(mActiviteHolder).getId());
    }

    private int mActiviteHolder = 1;
    public void putActiveTitle(int courseId){
        mActiviteHolder = courseId;
        notifyDataSetChanged();
    }

    private List<TypeHolder> types ;

    private enum ITEM_TYPE {
        TITLE, COURSE_TYPE
    }


    @NonNull
    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        context =parent.getContext();
        if (viewType == ITEM_TYPE.TITLE.ordinal()) {
            CourseChooseTitleBinding bindingTitle = CourseChooseTitleBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new CourseTitleAdapter.TitleViewHolder(bindingTitle);
        } else if (viewType == ITEM_TYPE.COURSE_TYPE.ordinal()) {
            CourseChooseTypeBinding bindingType = CourseChooseTypeBinding.inflate(LayoutInflater.from(parent.getContext()),parent,false);
            return new CourseTitleAdapter.TypeViewHolder(bindingType);
        }
        return null ;
    }

    @Override
    public void onBindViewHolder(@NonNull RecyclerView.ViewHolder viewHolder, int i) {
        if (viewHolder instanceof TypeViewHolder){
            TypeViewHolder holder = (TypeViewHolder) viewHolder;
            holder.setItem(types.get(i),i);
            holder.setClick();
        }else {
            TitleViewHolder holder = (TitleViewHolder) viewHolder;
            holder.setItem(types.get(i));
            holder.setClick();
        }
    }

    @Override
    public int getItemCount() {
        return types.size();
    }

    @Override
    public int getItemViewType(int position) {
        int type;
        if ((App.APP_ID < 259) && (types.get(position).getId()== 0)) {
            type = ITEM_TYPE.TITLE.ordinal();
        } else  {
            type = ITEM_TYPE.COURSE_TYPE.ordinal();
        }
        return type;
    }

    public interface TitleCallback {
        void onTitleChoose(int pos, int id);
    }

    public void setCallback(TitleCallback titleCallback) {
        this.callback = titleCallback;
    }

    public class TypeViewHolder extends RecyclerView.ViewHolder {

        int pos ;
        private final TextView type;
        private final RelativeLayout item;
        private final ImageView under;

        public TypeViewHolder(@NonNull CourseChooseTypeBinding itemView) {
            super(itemView.getRoot());
            type = itemView.type;
            item = itemView.item;
            under = itemView.underLine;
        }

        public void setItem(TypeHolder typeHolder,int pos) {
            type.setText(typeHolder.getValue());
            this.pos = pos ;
            if (mActiviteHolder == pos){
                type.setTextColor(context.getResources().getColor(R.color.colorPrimary));
                type.setBackgroundResource(R.drawable.transparent_corner);
                type.setTextSize(17);
                under.setVisibility(View.VISIBLE);
            }else {
                type.setTextColor(context.getResources().getColor(R.color.BLACK));
                type.setBackgroundResource(R.drawable.transparent_corner);
                type.setTextSize(14);
                under.setVisibility(View.INVISIBLE);
            }
        }

        public void clickType(){
            if (pos >= types.size()) {
                pos = types.size() - 1;
            }
            mActiviteHolder = pos ;
            notifyDataSetChanged();
            callback.onTitleChoose(mActiviteHolder, types.get(pos).getId());
        }

        public void setClick() {
            item.setOnClickListener(v -> clickType());
        }
    }

    public class TitleViewHolder extends RecyclerView.ViewHolder {

        private final TextView title;

        public TitleViewHolder(@NonNull CourseChooseTitleBinding itemView) {
            super(itemView.getRoot());
            title = itemView.title;

        }
        public void setItem(TypeHolder typeHolder) {
            title.setText(typeHolder.getValue());
        }

        public void setClick() {

        }

    }

}
