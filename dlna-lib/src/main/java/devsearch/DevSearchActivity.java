package devsearch;

import android.app.Activity;
import android.os.Bundle;
import com.google.android.material.appbar.AppBarLayout;
import com.google.android.material.appbar.CollapsingToolbarLayout;
import androidx.core.content.res.ResourcesCompat;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;
import androidx.appcompat.widget.Toolbar;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.android.dlna.manager.DLNADeviceManager;
import com.bumptech.glide.Glide;
import com.bumptech.glide.load.engine.DiskCacheStrategy;
import com.jaeger.library.StatusBarUtil;

import org.cybergarage.android.R;
import org.cybergarage.android.databinding.ActivityDevSearchBinding;
import org.cybergarage.upnp.Device;

import java.util.ArrayList;
import java.util.List;


import static com.android.dlna.manager.DLNADeviceManager.getInstance;

public class DevSearchActivity extends AppCompatActivity implements DevSearchMvpView {

    private DevSearchAdapter mAdapter;
    private List<Device> mDeviceList = new ArrayList<>();
    ActivityDevSearchBinding binding ;

    private void startRotate() {
        RotateAnimation animation = new RotateAnimation(0f, 360f, Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF, 0.5f);
        animation.setDuration(5000);
        animation.setRepeatMode(Animation.RESTART);
        animation.setRepeatCount(Animation.INFINITE);
        binding.imgSearching.startAnimation(animation);
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityDevSearchBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        setToolBarReplaceActionBar();
        StatusBarUtil.setColor(this, ResourcesCompat.getColor(getResources(), R.color.transparent, getTheme()), 0);

        setAndroidNativeLightStatusBar(this, true);
        setTitleToCollapsingToolbarLayout();
//        setSupportActionBar(toolbar);
        startDiscovery(false);
        startRotate();
        RecyclerView.ItemDecoration itemDecoration = new DividerItemDecoration(
                this, DividerItemDecoration.VERTICAL);
        ((DividerItemDecoration) itemDecoration).setDrawable(getResources().getDrawable(R.drawable.list_divider));
        binding.recyclerView.addItemDecoration(itemDecoration);
        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this, LinearLayoutManager.VERTICAL, false));
        mAdapter = new DevSearchAdapter();
        binding.recyclerView.setAdapter(mAdapter);
        binding.retry.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                onRetryClick();
            }
        });
        mAdapter.setOnItemClickListener(new DevSearchAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                getInstance().setCurrentDevice(mDeviceList.get(position));
                setResult(RESULT_OK);
                finish();
            }

            @Override
            public void onRetryClick() {
                startDiscovery(false);
            }
        });
        binding.back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        Glide.with(this).load(R.drawable.timg)
                .diskCacheStrategy(DiskCacheStrategy.ALL).into(binding.gif);

    }


    private void startDiscovery(boolean auto) {
        if (auto) {
//            mRefreshLayout.setRefreshing(true);
        }
        getInstance().startDiscovery(mListener);
        binding.searchingView.setVisibility(View.VISIBLE);
    }

    private final DLNADeviceManager.MediaRenderDeviceChangeListener mListener = new DLNADeviceManager.MediaRenderDeviceChangeListener() {
        @Override
        public void onStarted() {
            updateEmptyView(false);
        }

        @Override
        public void onDeviceListChanged(List<Device> list) {
            mDeviceList = list;
//            mRefreshLayout.setRefreshing(false);
            updateEmptyView(mDeviceList.isEmpty());
            mAdapter.setmDeviceList(mDeviceList);
            mAdapter.notifyDataSetChanged();
        }

        @Override
        public void onFinished(List<Device> list) {
            binding.searchingView.setVisibility(View.GONE);
            mDeviceList = list;
//            mRefreshLayout.setRefreshing(false);
            updateEmptyView(mDeviceList.isEmpty());
            if (mDeviceList.isEmpty()) {
                binding.retry.setVisibility(View.VISIBLE);
            }else {
                binding.retry.setVisibility(View.GONE);
            }
            mAdapter.setmDeviceList(mDeviceList);
            mAdapter.notifyDataSetChanged();
        }
    };

    private void updateEmptyView(boolean visible) {
//        if (visible && mEmptyView.getVisibility() != View.VISIBLE) {
//            mEmptyView.setVisibility(View.VISIBLE);
//        } else if (!visible && mEmptyView.getVisibility() == View.VISIBLE) {
//            mEmptyView.setVisibility(View.GONE);
//        }
    }


//    @OnClick(R2.id.back)
//    public void onViewClicked() {
//        finish();
//    }

    private static void setAndroidNativeLightStatusBar(Activity activity, boolean dark) {
        View decor = activity.getWindow().getDecorView();
        if (dark) {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LIGHT_STATUS_BAR);
        } else {
            decor.setSystemUiVisibility(View.SYSTEM_UI_FLAG_LAYOUT_FULLSCREEN | View.SYSTEM_UI_FLAG_LAYOUT_STABLE);
        }
    }

    private void setTitleToCollapsingToolbarLayout() {
        binding.top.addOnOffsetChangedListener(new AppBarLayout.OnOffsetChangedListener() {
            @Override
            public void onOffsetChanged(AppBarLayout appBarLayout, int verticalOffset) {
                if (verticalOffset <= -binding.headers.getHeight() / 2) {
//                    collapsingToolbarLayout.setTitle("选择设备");
//                    //使用下面两个CollapsingToolbarLayout的方法设置展开透明->折叠时你想要的颜色
//                    collapsingToolbarLayout.setExpandedTitleColor(getResources().getColor(android.R.color.transparent));
//                    collapsingToolbarLayout.setCollapsedTitleTextColor(Color.parseColor("#333333"));
//                    collapsingToolbarLayout.setLayoutMode(Color.parseColor("#333333"));
//                    collapsingToolbarLayout.setExpandedTitleGravity(Gravity.CENTER);
//                    collapsingToolbarLayout.setCollapsedTitleGravity(Gravity.CENTER);
                    binding.title.setText("选择设备");
//                    collapsingToolbarLayout.setCollapsedTitleTextColor(getResources().getColor(R.color.white));
                } else {
                    binding.title.setText("");
                    binding.collapsingToolbarLayout.setTitle("");
                }
            }
        });
    }

    private void setToolBarReplaceActionBar() {
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        binding.toolbar.setNavigationOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
            }
        });
    }

    public void onRetryClick() {
        startDiscovery(false);
        binding.retry.setVisibility(View.GONE);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        getInstance().stopDiscovery();
    }
}