package com.iyuba.talkshow.ui.user.edit;

import android.graphics.Color;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import androidx.appcompat.app.AppCompatActivity;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.result.GetUserBasicInfoResponse;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.umeng.analytics.MobclickAgent;

import javax.inject.Inject;

import cn.qqtheme.framework.entity.City;
import cn.qqtheme.framework.entity.County;
import cn.qqtheme.framework.entity.Province;
import cn.qqtheme.framework.picker.OptionPicker;
import cn.qqtheme.framework.widget.WheelView;

/**
 * Created by carl shen on 2020/12/2.
 */
public class ImproveUserActivity extends BaseActivity implements ImproveUserMvpView {
    protected AppCompatActivity context;
    private TextView tvGender, tvLocation, tvAge, tvTitle;
    private RelativeLayout rtCardSex, rtCardAge, rtCardArea, rtCardTitle;
    private TextView tvCommit;
    private ImageView imgClose;
    private String locProvince="", locCity="";
    private final String[] arrGender = new String[] {"男生", "女生"};
    private final String[] arrAge = new String[] {"70s","80s", "90s", "95s","00s", "05s", "10s"};
    private final String[] arrTitle = new String[] {"小学生", "初中生", "高中生","大学生", "研究生", "职员","其他"};
    @Inject
    ImproveUserPresenter mPresenter;
    LoadingDialog mLoadingDialog;
    private GetUserBasicInfoResponse mResponse;
    boolean register = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.userinfo_improve);
        activityComponent().inject(this);
        mPresenter.attachView(this);
        context = this;
        initWidget();
        setListener();
        register = getIntent().getBooleanExtra("register", false);
        if (register) {
            mPresenter.getIpInfo();
        }
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
        if (!register) {
            mPresenter.getUserBasicInfo();
        }
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    protected void initWidget() {
        imgClose = findViewById(R.id.user_close);
        tvCommit = findViewById(R.id.tv_commit);
        rtCardSex = findViewById(R.id.card_sex);
        rtCardAge = findViewById(R.id.card_age);
        rtCardArea = findViewById(R.id.card_area);
        rtCardTitle = findViewById(R.id.card_title);
        tvGender = findViewById(R.id.tv_gender);
        tvLocation = findViewById(R.id.tv_location);
        tvAge = findViewById(R.id.tv_birthday);
        tvTitle = findViewById(R.id.tv_company);
    }

    protected void setListener() {
        imgClose.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
        tvCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                changeUserByPara();
            }
        });
        rtCardSex.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionPicker picker = new OptionPicker(context, arrGender);
                picker.setDividerRatio(WheelView.DividerConfig.FILL);
                picker.setShadowColor(Color.RED, 40);
                if (!TextUtils.isEmpty(tvGender.getText().toString().trim())) {
                    int index = 0;
                    for (int i = 0; i < arrGender.length; i++) {
                        if (tvGender.getText().toString().trim().equalsIgnoreCase(arrGender[i])) {
                            index = i;
                            break;
                        }
                    }
                    picker.setSelectedIndex(index);
                } else {
                    picker.setSelectedIndex(1);
                }
                picker.setCycleDisable(true);
                picker.setOffset(2);
                picker.setTextSize(16);
                picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int index, String item) {
//                        showToast("index=" + index + ", item=" + item);
                        tvGender.setText(item);
                    }
                });
                picker.show();
            }
        });
        rtCardAge.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionPicker picker = new OptionPicker(context, arrAge);
                picker.setCanceledOnTouchOutside(false);
                picker.setDividerRatio(WheelView.DividerConfig.FILL);
                picker.setShadowColor(Color.RED, 40);
                if (!TextUtils.isEmpty(tvAge.getText().toString().trim())) {
                    int index = 0;
                    for (int i = 0; i < arrAge.length; i++) {
                        if (tvAge.getText().toString().trim().equalsIgnoreCase(arrAge[i])) {
                            index = i;
                            break;
                        }
                    }
                    picker.setSelectedIndex(index);
                } else {
                    picker.setSelectedIndex(3);
                }
                picker.setCycleDisable(true);
                picker.setOffset(3);
                picker.setTextSize(16);
                picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int index, String item) {
//                        showToast("index=" + index + ", item=" + item);
                        tvAge.setText(item);
                    }
                });
                picker.show();
            }
        });
        rtCardArea.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                AddressPickTask task = new AddressPickTask(context);
                task.setHideCounty(true);
                task.setCallback(new AddressPickTask.Callback() {
                    @Override
                    public void onAddressInitFailed() {
                        showToastShort("数据初始化失败");
                    }

                    @Override
                    public void onAddressPicked(Province province, City city, County county) {
                        locProvince = province.getAreaName();
                        locCity = city.getAreaName();
                        if (county == null) {
//                            showToast(province.getAreaName() + city.getAreaName());
                            tvLocation.setText(province.getAreaName() + city.getAreaName());
                        } else {
//                            showToast(province.getAreaName() + city.getAreaName() + county.getAreaName());
                            tvLocation.setText(province.getAreaName() + city.getAreaName() + county.getAreaName());
                        }
                    }
                });
                if (TextUtils.isEmpty(locProvince) || TextUtils.isEmpty(locCity)) {
                    task.execute("北京市", "通州区");
                } else {
                    task.execute(locProvince, locCity);
                }
            }
        });
        rtCardTitle.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                OptionPicker picker = new OptionPicker(context, arrTitle);
                picker.setCanceledOnTouchOutside(false);
                picker.setDividerRatio(WheelView.DividerConfig.FILL);
                picker.setShadowColor(Color.RED, 40);
                if (!TextUtils.isEmpty(tvTitle.getText().toString().trim())) {
                    int index = 0;
                    for (int i = 0; i < arrTitle.length; i++) {
                        if (tvTitle.getText().toString().trim().equalsIgnoreCase(arrTitle[i])) {
                            index = i;
                            break;
                        }
                    }
                    picker.setSelectedIndex(index);
                } else {
                    picker.setSelectedIndex(3);
                }
                picker.setCycleDisable(true);
                picker.setOffset(3);
                picker.setTextSize(16);
                picker.setOnOptionPickListener(new OptionPicker.OnOptionPickListener() {
                    @Override
                    public void onOptionPicked(int index, String item) {
//                        showToast("index=" + index + ", item=" + item);
                        tvTitle.setText(item);
                    }
                });
                picker.show();
            }
        });
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog = new LoadingDialog(this);
        mLoadingDialog.show();
    }

    @Override
    public void dismissWaitingDialog() {
        if (mLoadingDialog != null) {
            mLoadingDialog.dismiss();
        }
        tvCommit.setClickable(true);
    }

    @Override
    public void finishImprove() {
        finish();
    }

    @Override
    public void setIPInfo(GetIpResponse ipAddress) {
        if (null == ipAddress) {
            return;
        }
        Log.e("ImproveUserActivity", "province== " + ipAddress.province);
        Log.e("ImproveUserActivity", "city== " + ipAddress.city);
        locProvince = ipAddress.province;
        locCity = ipAddress.city;
        tvLocation.setText(ipAddress.province + " " + ipAddress.city);
    }

    @Override
    public void setUserInfo(GetUserBasicInfoResponse response) {
        if (null == response) {
            return;
        }
        mResponse = response;
        Log.e("ImproveUserActivity", "resideLocation== " + response.resideLocation());
        Log.e("ImproveUserActivity", "occupation== " + response.occupation());
        tvLocation.setText(response.resideLocation());
        String[] cities = response.resideLocation().split("\\s+");
        if (cities != null) {
            if (cities.length > 1) {
                locProvince = cities[0];
                locCity = cities[1];
            } else if (cities.length > 0) {
                locProvince = "";
                locCity = response.resideLocation();
            }
        }
        if ("1".equalsIgnoreCase(response.gender())) {
            tvGender.setText("男生");
        } else {
            tvGender.setText("女生");
        }
        tvAge.setText("" + response.age());
        tvTitle.setText(response.occupation());
    }

    protected void changeUserByPara() {
        if (TextUtils.isEmpty(tvGender.getText().toString())) {
            showToastShort("请选择性别");
            return;
        }
        if (TextUtils.isEmpty(tvAge.getText().toString())) {
            showToastShort("请选择年龄");
            return;
        }
        if (TextUtils.isEmpty(tvLocation.getText().toString())){
            showToastShort("请选择地区");
            return;
        }
        if (TextUtils.isEmpty(tvTitle.getText().toString())) {
            showToastShort("请选择身份");
            return;
        }
        tvCommit.setClickable(false);
        mPresenter.improveUserInfo(tvGender.getText().toString().trim(), tvAge.getText().toString().trim(),
                locProvince, locCity, tvTitle.getText().toString().trim());
    }

}
