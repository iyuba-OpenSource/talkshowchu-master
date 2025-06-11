package com.iyuba.talkshow.ui.detail.comment;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.PartialMicVolumeBinding;

public class MicVolumeView extends RelativeLayout {

    PartialMicVolumeBinding binding ;

    public MicVolumeView(Context context) {
        this(context, null);
    }

    public MicVolumeView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public MicVolumeView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        binding = PartialMicVolumeBinding.inflate(LayoutInflater.from(context), this,true);
    }

    @Override
    protected void onFinishInflate() {
        super.onFinishInflate();
    }

    public void setVolume(int db) {
        switch (mapDbToLevel(db)) {
            case 1:
                binding.imageMicVolume.setBackgroundResource(R.drawable.amp1);
                break;
            case 2:
                binding.imageMicVolume.setBackgroundResource(R.drawable.amp2);
                break;
            case 3:
                binding.imageMicVolume.setBackgroundResource(R.drawable.amp3);
                break;
            case 4:
                binding.imageMicVolume.setBackgroundResource(R.drawable.amp4);
                break;
            case 5:
                binding.imageMicVolume.setBackgroundResource(R.drawable.amp5);
                break;
            case 6:
                binding.imageMicVolume.setBackgroundResource(R.drawable.amp6);
                break;
            case 7:
                binding.imageMicVolume.setBackgroundResource(R.drawable.amp7);
                break;
        }
    }

    public void setHint(String content) {
        binding.textMicHint.setText(content);
    }

    private int mapDbToLevel(int db) {
        if (db <= 10) {
            return 1;
        } else if (db <= 20) {
            return 2;
        } else if (db <= 30) {
            return 3;
        } else if (db <= 40) {
            return 4;
        } else if (db <= 50) {
            return 5;
        } else if (db <= 60) {
            return 6;
        } else {
            return 7;
        }
    }
}
