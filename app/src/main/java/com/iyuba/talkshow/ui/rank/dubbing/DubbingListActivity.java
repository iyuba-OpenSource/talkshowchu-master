package com.iyuba.talkshow.ui.rank.dubbing;

import android.os.Bundle;
import android.view.View;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.databinding.ActivityDubbingListBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.user.me.dubbing.released.ReleasedFragment;

import javax.inject.Inject;


/**
 * DubbingListActivity
 *
 * @author wayne
 * @date 2018/2/6
 */
public class DubbingListActivity extends BaseActivity {


    @Inject
    DataManager mDataManager;
    int uid;
    ActivityDubbingListBinding binding ;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding  = ActivityDubbingListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityComponent().inject(this);
        binding.listToolbar.listToolbar.setTitle("录音");
        setSupportActionBar( binding.listToolbar.listToolbar);
        binding.listToolbar.listToolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                finish();
            }
        });

        uid = getIntent().getIntExtra("uid", 0);


        Fragment fragment = new ReleasedFragment();
        Bundle bundle = new Bundle();
        bundle.putString("uid", uid + "");
        fragment.setArguments(bundle);

        FragmentManager manager = getSupportFragmentManager();
        FragmentTransaction transaction = manager.beginTransaction();
        transaction.replace(R.id.container, fragment);
        transaction.commit();
    }
}
