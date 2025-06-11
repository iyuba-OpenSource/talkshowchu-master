package com.iyuba.talkshow.ui.base;

import android.app.Activity;
import android.content.Context;
import androidx.fragment.app.Fragment;
import android.widget.Toast;

import com.iyuba.talkshow.injection.component.ActivityComponent;
import com.iyuba.talkshow.injection.component.FragmentComponent;

public class BaseFragment extends Fragment {
   public Activity mActivity;
   public Context mContext;

    private FragmentComponent mFragmentComponent;

    public FragmentComponent fragmentComponent() {
        if (mFragmentComponent == null) {
            ActivityComponent component = ((BaseActivity) getActivity()).activityComponent();
            if (component != null) {
                mFragmentComponent = component.fragmentComponent();
            }
        }
        return mFragmentComponent;
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        mActivity = (Activity) context;
        mContext = context;
    }

    public void showToastShort(int resId) {
        Toast.makeText(mContext, resId, Toast.LENGTH_SHORT).show();
    }

    public void showToastShort(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_SHORT).show();

    }

    public void showToastLong(int resId) {
        Toast.makeText(mContext, resId, Toast.LENGTH_LONG).show();
    }

    public void showToastLong(String message) {
        Toast.makeText(mContext, message, Toast.LENGTH_LONG).show();
    }
}
