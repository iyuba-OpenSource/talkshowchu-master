package com.iyuba.talkshow.ui.user.edit;

import android.app.DatePickerDialog;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.PersistableBundle;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.widget.DatePicker;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.util.Pair;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.result.GetUserBasicInfoResponse;
import com.iyuba.talkshow.data.remote.UserService;
import com.iyuba.talkshow.databinding.ActivityEditUserInfoBinding;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.user.edit.dialog.SchoolAdapter;
import com.iyuba.talkshow.ui.user.edit.dialog.SchoolDialog;
import com.iyuba.talkshow.ui.user.image.UploadImageActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.JudgeZodicaAndConstellation;
import com.iyuba.talkshow.util.TimeUtil;
import com.umeng.analytics.MobclickAgent;

import java.text.MessageFormat;
import java.util.Calendar;
import java.util.GregorianCalendar;

import javax.inject.Inject;

import timber.log.Timber;

public class EditUserInfoActivity extends BaseActivity implements EditUserInfoMvpView {
    private static final String USER_INFO_RESPONSE = "user_info_response";

    private GetUserBasicInfoResponse mResponse;

    @Inject
    EditUserInfoPresenter mPresenter;
    @Inject
    SchoolAdapter mSchoolAdapter;

    SchoolDialog dialog;
    private LoadingDialog mLoadingDialog;

    //布局样式
    private ActivityEditUserInfoBinding binding;

    public static Intent buildIntent(Context context, GetUserBasicInfoResponse response) {
        Intent intent = new Intent(context, EditUserInfoActivity.class);
        intent.putExtra(USER_INFO_RESPONSE, response);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        binding = ActivityEditUserInfoBinding.inflate(LayoutInflater.from(this));
        setContentView(binding.getRoot());

        setSupportActionBar(binding.edituserinfoToolbar.listToolbar);
        activityComponent().inject(this);
        mPresenter.attachView(this);
        mLoadingDialog = new LoadingDialog(this);
        mResponse = getIntent().getParcelableExtra(USER_INFO_RESPONSE);
        mPresenter.getLocation();
        initView();
    }

    public void initView() {
        binding.edituserinfoGenderTv.setText(UserService.GetUserBasicInfo.Result.getMessageByCode(mResponse.gender()));
        binding.edituserinfoBirthdayTv.setText(mResponse.birthday());
        binding.edituserinfoZodiacTv.setText(mResponse.zodiac());
        binding.edituserinfoConstellationTv.setText(mResponse.constellation());
        binding.edituserinfoLocationEdt.setText(mResponse.resideLocation());
        binding.edituserinfoCollegeTv.setText(mResponse.graduateSchool());
        /*Glide.with(this)
                .load(mPresenter.getUserImageUrl())
                .centerCrop()
                .transform(new CircleTransform(this))
                .placeholder(R.drawable.default_avatar)
                .into(binding.edituserinfoPortraitIv);*/
        Glide3Util.loadCircleImg(this,mPresenter.getUserImageUrl(),R.drawable.default_avatar,binding.edituserinfoPortraitIv);
    }

    @Override
    protected void onPause() {
        super.onPause();
        MobclickAgent.onPause(this);
    }

    @Override
    protected void onResume() {
        super.onResume();
        if(binding.edituserinfoPortraitIv!=null){
            /*Glide.with(this)
                    .load(mPresenter.getUserImageUrl())
                    .centerCrop()
                    .transform(new CircleTransform(this))
                    .placeholder(R.drawable.default_avatar)
                    .into(binding.edituserinfoPortraitIv);*/
            Glide3Util.loadCircleImg(this,mPresenter.getUserImageUrl(),R.drawable.default_avatar,binding.edituserinfoPortraitIv);
        }
        MobclickAgent.onResume(this);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                break;
        }
        return true;
    }

    void clickImageLayout() {
        Intent intent = new Intent(this, UploadImageActivity.class);
        startActivity(intent);
    }

    void clickGender() {
        showGenderDialog();
    }

    void clickBirthDay() {
        showDateDialog();
    }

    void clickSave() {
        showLoadingDialog();
        setSaveBtnClickable(false);
        String gender = binding.edituserinfoGenderTv.getText().toString();
        String birthday = binding.edituserinfoBirthdayTv.getText().toString();
        String constellation = binding.edituserinfoConstellationTv.getText().toString();
        String zodiac = binding.edituserinfoZodiacTv.getText().toString();
        String collage = binding.edituserinfoCollegeTv.getText().toString();
        String city = binding.edituserinfoLocationEdt.getText().toString().trim();
        Pair<String, String> pair = mPresenter.getEditKeyValue(gender, birthday,
                constellation, zodiac, collage, city);
        Timber.e("&&&" + pair.first);
        Timber.e("&&&" + pair.second);
        mPresenter.edit(pair.first, pair.second);
    }

    void clickCollege() {
        showSchoolDialog();
    }

    @Override
    public void setLocationTv(String text) {
        binding.edituserinfoLocationEdt.setText(text);
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, resId, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void showLoadingDialog() {
        mLoadingDialog.show();
    }

    @Override
    public void dismissLoadingDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void setSaveBtnClickable(boolean clickable) {
        binding.edituserinfoSaveBtn.setClickable(clickable);
    }

    private void showGenderDialog() {
        Dialog dialog = new AlertDialog.Builder(this)
                .setTitle(R.string.person_info_gender)
                .setSingleChoiceItems(R.array.gender, 0,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                binding.edituserinfoGenderTv.setText(
                                        UserService.GetUserBasicInfo.Result
                                                .getMessageByCode(String.valueOf(which)));
                                dialog.dismiss();
                            }
                        })
                .setNegativeButton(R.string.alert_btn_cancel,
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                dialog.dismiss();
                            }
                        })
                .create();
        dialog.show();
    }

    private void showDateDialog() {
        Calendar calendar = Calendar.getInstance();
        Dialog dialog = new DatePickerDialog(this,
                new DatePickerDialog.OnDateSetListener() {
                    public void onDateSet(DatePicker dp, int year,
                                          int month, int day) {
                        if(TimeUtil.checkAfterCur(year, month, day)) {
                            showToast(R.string.birthday_after_cur);
                        } else {
                            setBirthDayAndOthers(year, month+1, day);
                        }
                    }
                }, calendar.get(Calendar.YEAR), // 传入年份
                calendar.get(Calendar.MONTH), // 传入月份
                calendar.get(Calendar.DAY_OF_MONTH) // 传入天数
        );
        dialog.setTitle(R.string.person_info_birth);
        dialog.show();
    }

    public void setBirthDayAndOthers(int year, int month, int day) {
        Calendar cal = new GregorianCalendar(year, month, day);
        binding.edituserinfoZodiacTv.setText(JudgeZodicaAndConstellation.date2Zodica(cal));
        binding.edituserinfoConstellationTv.setText(JudgeZodicaAndConstellation.date2Constellation(cal));
        binding.edituserinfoBirthdayTv.setText(MessageFormat.format(getString(R.string.birth_day_format),
                String.valueOf(year), String.valueOf(month), String.valueOf(day)));
    }

    private void showSchoolDialog() {
        dialog = new SchoolDialog(EditUserInfoActivity.this, R.style.DialogTheme);
        dialog.setOnTextChangeListener(new SchoolDialog.OnTextChangeListener() {
            @Override
            public void onTextChange(String text) {

            }
        });
        dialog.setOnBtnClickListener(new SchoolDialog.OnBtnClickListener() {
            @Override
            public void onClearClick() {

            }

            @Override
            public void onSearchClick() {

            }
        });
        dialog.setOnSelectData(new SchoolDialog.OnSelectData() {
            @Override
            public void onSelectData(String data) {
                binding.edituserinfoCollegeTv.setText(data);
            }
        });
        dialog.show();

    }


    @Override
    public void onPostCreate(@Nullable Bundle savedInstanceState, @Nullable PersistableBundle persistentState) {
        super.onPostCreate(savedInstanceState, persistentState);
        binding.edituserinfoGenderTv.setOnClickListener(v -> clickGender());
        binding.edituserinfoPortraitLl.setOnClickListener(v -> clickImageLayout());
        binding.edituserinfoBirthdayTv.setOnClickListener(v -> clickBirthDay());
        binding.edituserinfoSaveBtn.setOnClickListener(v -> clickSave());
        binding.edituserinfoCollegeTv.setOnClickListener(v -> clickCollege());
        binding.edituserinfoGenderTv.setOnClickListener(v -> clickGender());
        binding.edituserinfoGenderTv.setOnClickListener(v -> clickGender());
    }
}
