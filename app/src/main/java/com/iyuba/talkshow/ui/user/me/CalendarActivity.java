package com.iyuba.talkshow.ui.user.me;

import android.os.Bundle;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.data.model.result.ShareInfoRecord;
import com.iyuba.talkshow.databinding.ActivityCalendarBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.NetStateUtil;
import com.othershe.calendarview.bean.DateBean;
import com.othershe.calendarview.listener.OnPagerChangeListener;
import com.othershe.calendarview.listener.OnSingleChooseListener;
import com.othershe.calendarview.utils.CalendarUtil;
import com.othershe.calendarview.weiget.CalendarView;
import com.umeng.analytics.MobclickAgent;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;

import javax.inject.Inject;

/**
 * Created by carl shen on 2021/4/23
 * New Primary English, new study experience.
 */
public class CalendarActivity extends BaseActivity implements CalendarMvpView {
    private static final String TAG = CalendarActivity.class.getSimpleName();
    @Inject
    CalendarPresenter mPresenter;
    ActivityCalendarBinding binding;
    private LoadingDialog mLoadingDialog;
    private CalendarView calendarView;
    private TextView title;
    private TextView chooseDate;
    private final int[] cDate = CalendarUtil.getCurrentDate();
    private String curTime = "";
    private int flag = 0;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityCalendarBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        activityComponent().inject(this);
        setSupportActionBar(binding.clockToolbar);
        mPresenter.attachView(this);
    }

    @Override
    protected void onPostCreate(Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        calendarView = findViewById(R.id.calendar);
        calendarView
//                .setSpecifyMap(map)
                .setStartEndDate("2016.1", "2028.12")
                .setDisableStartEndDate("2016.10.10", "2028.10.10")
                .setInitDate(cDate[0] + "." + cDate[1])
                .setSingleDate(cDate[0] + "." + cDate[1] + "." + cDate[2])
                .init();
        title = findViewById(R.id.title);
        title.setText(cDate[0] + "年" + cDate[1] + "月");
        chooseDate = findViewById(R.id.choose_date);
        chooseDate.setText("今天的日期：" + cDate[0] + "年" + cDate[1] + "月" + cDate[2] + "日");

        calendarView.setOnPagerChangeListener(new OnPagerChangeListener() {
            @Override
            public void onPagerChanged(int[] date) {
                if (date == null) {
                    Log.e(TAG, "onPagerChanged data is null? ");
                    return;
                }
                title.setText(date[0] + "年" + date[1] + "月");
                Log.e(TAG, "onPagerChanged flag " + flag);
            }
        });

        calendarView.setOnSingleChooseListener(new OnSingleChooseListener() {
            @Override
            public void onSingleChoose(View view, DateBean date) {
                title.setText(date.getSolar()[0] + "年" + date.getSolar()[1] + "月");
                if (date.getType() == 1) {
                    chooseDate.setText("今天的日期：" + date.getSolar()[0] + "年" + date.getSolar()[1] + "月" + date.getSolar()[2] + "日");
                }
            }
        });
        if(NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            getCalendar(cDate);
        } else {
            showToastShort(R.string.please_check_network);
        }
    }

    private void getCalendar(int[] curDate) {
        if (curDate[1] < 10) {
            curTime = curDate[0] + "0" + curDate[1];
        } else {
            curTime = curDate[0] + "" + curDate[1];
        }
        Log.e(TAG, "getCalendar curTime " + curTime);
        mPresenter.getCalendar(curTime);
    }
    public void lastMonth(View view) {
//        calendarView.lastMonth();
        if(NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            flag = -1;
            showLoadingLayout();
            if (cDate[1] > 0) {
                cDate[1]--;
            } else {
                cDate[0]--;
                cDate[1]=12;
            }
            getCalendar(cDate);
        } else {
            showToastShort(R.string.please_check_network);
        }
    }

    public void nextMonth(View view) {
//        calendarView.nextMonth();
        if(NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            flag = 1;
            showLoadingLayout();
            if (cDate[1] < 12) {
                cDate[1]++;
            } else {
                cDate[0]++;
                cDate[1]=1;
            }
            getCalendar(cDate);
        } else {
            showToastShort(R.string.please_check_network);
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }
    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        MobclickAgent.onResume(this);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    public void showCalendar(List<ShareInfoRecord> ranking) {
        HashMap<String, String> map = new HashMap<>();
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);
        for (ShareInfoRecord record: ranking) {
            Log.e(TAG, "showCalendar record.createtime: " + record.createtime);
            try {
                Date date = sdf.parse(record.createtime);
//                Log.e(TAG, "showCalendar date: " + date);
//                Log.e(TAG, "showCalendar getYear: " + date.getYear());
                String item = (date.getYear()+1900) + "." + (date.getMonth() + 1) + "." + date.getDate();
                Log.e(TAG, "showCalendar item: " + item);
                if (record.scan > 1) {
                    map.put(item, "" + record.scan);
                } else {
                    map.put(item, "true");
                }
            } catch (ParseException e) {
                e.printStackTrace();
            }
        }
        calendarView.setClockInStatus(map);
        if (flag > 0) {
            calendarView.nextMonth();
        } else if (flag < 0) {
            calendarView.lastMonth();
        } else {
            calendarView
                    .setStartEndDate("2016.1", "2028.12")
                    .setDisableStartEndDate("2016.10.10", "2028.10.10")
                    .setInitDate(cDate[0] + "." + cDate[1])
                    .setSingleDate(cDate[0] + "." + cDate[1] + "." + cDate[2])
                    .init();
        }
    }

    @Override
    public void showLoadingLayout() {
        if (mLoadingDialog == null) {
            mLoadingDialog = new LoadingDialog(mContext);
        }
        mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingLayout() {
        if (mLoadingDialog != null && mLoadingDialog.isShowing()) {
            mLoadingDialog.dismiss();
            mLoadingDialog = null;
        }
    }

}
