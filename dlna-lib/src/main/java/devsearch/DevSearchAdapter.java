package devsearch;

import androidx.recyclerview.widget.RecyclerView;

import android.annotation.SuppressLint;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;


import org.cybergarage.android.R;
import org.cybergarage.upnp.Device;

import java.util.List;

public  class DevSearchAdapter extends RecyclerView.Adapter<DevSearchAdapter.Holder> {

    public void setmDeviceList(List<Device> mDeviceList) {
        this.mDeviceList = mDeviceList;
    }

    private List<Device> mDeviceList ;

    private OnItemClickListener mListener;

    private Device mActiveDevice  ;

    @Override
    public Holder onCreateViewHolder(ViewGroup parent, int viewType) {
        return new Holder(LayoutInflater.from(parent.getContext()).inflate(R.layout.list_item_device, parent, false));
    }

    @Override
    public void onBindViewHolder(final Holder holder, @SuppressLint("RecyclerView") final int position) {
        Device device = mDeviceList.get(position);
        holder.deviceName.setText(device.getFriendlyName());
        holder.contentView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    if (mDeviceList.get(position) == mActiveDevice){
                        holder.img.setVisibility(View.VISIBLE);
                        mListener.onItemClick(v, position);
                    }else {
                        mActiveDevice = mDeviceList.get(position);
                        notifyDataSetChanged();
                    }

                }
            }
        });
        holder.retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (mListener != null) {
                    mListener.onRetryClick();
                }
            }
        });
        if (mDeviceList.get(position)  == mActiveDevice){
            holder.img.setVisibility(View.VISIBLE);
        }else {
            holder.img.setVisibility(View.INVISIBLE);
        }

        if (position == mDeviceList.size()-1){
            holder.retry.setVisibility(View.VISIBLE);
        }else {
            holder.retry.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public int getItemCount() {
        return mDeviceList==null? 0 :mDeviceList.size();
    }

    public void setOnItemClickListener(OnItemClickListener mListener) {
        this.mListener = mListener;
    }

    class Holder extends RecyclerView.ViewHolder {
        TextView deviceName;
        View contentView;
        ImageView img;
        TextView retry;

        Holder(View itemView) {
            super(itemView);
            contentView = itemView;
            deviceName = itemView.findViewById(R.id.tv_name_item);
            img = itemView.findViewById(R.id.choose);
            retry = itemView.findViewById(R.id.retry);
        }
    }

    public interface OnItemClickListener{
        void onItemClick(View v, int pos);
        void onRetryClick();
    }
}