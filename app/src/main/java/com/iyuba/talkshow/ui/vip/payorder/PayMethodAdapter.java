package com.iyuba.talkshow.ui.vip.payorder;

import android.app.Application;
import android.content.res.TypedArray;
import androidx.recyclerview.widget.RecyclerView;

import android.os.Binder;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.TextView;

import com.iyuba.talkshow.BuildConfig;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.ItemPayMethodBinding;

import javax.inject.Inject;


public class PayMethodAdapter extends RecyclerView.Adapter<PayMethodAdapter.PayMethodViewHolder> {

    private final String[] mNames;
    private final String[] mHints;
    private final int[] mImageIds;
    private final int[] mMethod;
    private int mSelect = 0;

    ItemPayMethodBinding binding ;


    @Inject
    public PayMethodAdapter(Application application) {
        TypedArray ta = application.getResources().obtainTypedArray(R.array.pay_methods_images);
        int length = ta.length();
        mImageIds = new int[length];
        for (int i = 0; i < length; i++) {
            mImageIds[i] = ta.getResourceId(i, 0);
        }
        ta.recycle();
        mNames = application.getResources().getStringArray(R.array.pay_method_names);
        mHints = application.getResources().getStringArray(R.array.pay_method_hints);
        mMethod = new int[] {
          PayMethod.ALIPAY, PayMethod.WEIXIN, PayMethod.BANKCARD
        };
    }

    @Override
    public PayMethodViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        LayoutInflater inflater =  LayoutInflater.from(parent.getContext());
        binding = ItemPayMethodBinding.inflate(inflater,parent,false);
        return new PayMethodViewHolder(binding.getRoot());
    }

    @Override
    public void onBindViewHolder(PayMethodViewHolder holder, int position) {
        holder.icon.setImageResource(mImageIds[position]);
        holder.text.setText(mNames[position]);
        holder.info.setText(mHints[position]);
        holder.checkBox.setChecked(position == mSelect);
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    @Override
    public int getItemCount() {
//        if (BuildConfig.APPLICATION_ID.contains("child")){
//            return 1 ;
//        }
        return mImageIds.length;
    }

    public int getSelectMethod() {
        return mSelect;
    }

    class PayMethodViewHolder extends RecyclerView.ViewHolder {

        private ImageView icon;
        private TextView text;
        private TextView info;
        private CheckBox checkBox;

        public PayMethodViewHolder(View itemView) {
            super(itemView);

            icon = binding.payMethodIcon;
            text = binding.payMethodText;
            info = binding.payMethodInfo;
            checkBox = binding.payMethodCheckbox;

            itemView.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    onClickItem();
                }
            });
        }

        public void onClickItem() {
            mSelect = getPosition();
            notifyDataSetChanged();
        }
    }

}
