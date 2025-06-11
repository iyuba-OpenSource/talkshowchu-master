package com.iyuba.wordtest.adapter;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;
import android.view.View;
import android.view.ViewGroup;

public class TalkShowStepAdapter extends RecyclerView.Adapter<TalkShowStepAdapter.ViewHolder> {

    public String step ;
    public int size ;

    @NonNull
    @Override
    public TalkShowStepAdapter.ViewHolder onCreateViewHolder(@NonNull ViewGroup viewGroup, int posion) {
        return null;
    }

    @Override
    public void onBindViewHolder(@NonNull TalkShowStepAdapter.ViewHolder viewHolder, int position) {

    }

    @Override
    public int getItemCount() {
        return size;
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        public ViewHolder(@NonNull View itemView) {
            super(itemView);
        }
    }
}
