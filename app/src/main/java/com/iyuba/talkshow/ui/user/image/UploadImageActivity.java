package com.iyuba.talkshow.ui.user.image;

import android.Manifest;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.os.Build;
import android.os.Bundle;
import android.os.Handler;
import android.provider.MediaStore;
import android.view.MenuItem;
import android.widget.Toast;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.core.content.FileProvider;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.ActivityUploadImageBinding;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.SelectPicUtils;
import com.iyuba.talkshow.util.StorageUtil;
import com.permissionx.guolindev.PermissionX;
import com.umeng.analytics.MobclickAgent;

import java.io.File;

import javax.inject.Inject;

import butterknife.OnClick;


public class UploadImageActivity extends BaseActivity implements UploadImageMvp {

    private static final String UID = "uid";

    // 拍照
    public static final int TAKE_PHOTO = 1;
    // 缩放
    public static final int PHOTO_ZOOM = 2;
    // 结果
    public static final int PHOTO_RESULT = 3;
    private final int REQUECT_CODE_CAMERA = 4;

    @Inject
    UploadImagePresenter mPresenter;

    LoadingDialog mLoadingDialog;
    ActivityUploadImageBinding binding;

    public static Intent getIntent(Context context, int uid) {
        Intent intent = new Intent(context, UploadImageActivity.class);
        intent.putExtra(UID, uid);
        return intent;
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityUploadImageBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.uploadimageToolbar.listToolbar);
        activityComponent().inject(this);
        mPresenter.attachView(this);
        mLoadingDialog = new LoadingDialog(this);
        setClick();
    }

    private void setClick() {
        binding.uploadimageSubmitBtn.setOnClickListener(v -> clickSubmit());
        binding.uploadimageLocalBtn.setOnClickListener(v -> clickLocal());
        binding.uploadimageSkipBtn.setOnClickListener(v -> clickSkip());
        binding.uploadimageCameraBtn.setOnClickListener(v -> clickCamera());
    }

    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);
        /*Glide.with(this)
                .load(mPresenter.getImageUrl())
                .transform(new CircleTransform(this))
                .placeholder(R.drawable.default_avatar)
                .into(binding.uploadimageUserIv);*/
        Glide3Util.loadCircleImg(this,mPresenter.getImageUrl(),R.drawable.default_avatar,binding.uploadimageUserIv);
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
    protected void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (resultCode == RESULT_CANCELED) {
            return;
        }
        final File picture = StorageUtil.getAvatarFile(this, Constant.User.AVATAR_FILENAME);
        // 拍照
        if (requestCode == TAKE_PHOTO) {
            Uri imageUri;
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
                imageUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", picture);
            } else {
                imageUri = Uri.fromFile(picture);
            }
//            Uri imageUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", picture);
            SelectPicUtils.beginCrop(mContext, imageUri, picture, PHOTO_RESULT);

        } else if (requestCode == PHOTO_ZOOM && data != null) {

            Uri imageUri = data.getData();
            SelectPicUtils.beginCrop(mContext, imageUri, picture, PHOTO_RESULT);

        } else if (requestCode == PHOTO_RESULT && data != null) {
            /*Glide.with(mContext).load(R.drawable.default_avatar)
                    .transform(new CircleTransform(mContext))
                    .into(binding.uploadimageUserIv);

            new Handler().postDelayed(new Runnable() {
                @Override
                public void run() {
                    Glide.with(mContext).load(picture)
                            .transform(new CircleTransform(mContext))
                            .diskCacheStrategy(DiskCacheStrategy.NONE)
                            .skipMemoryCache(true)
                            .into(binding.uploadimageUserIv);
                }
            }, 400);*/

            Glide3Util.loadCircleImg(mContext,picture.getPath(),R.drawable.default_avatar,binding.uploadimageUserIv);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @OnClick(R.id.uploadimage_camera_btn)
    void clickCamera() {
        askForPermisions();
    }

    @OnClick(R.id.uploadimage_local_btn)
    void clickLocal() {
        SelectPicUtils.selectPicture(this, PHOTO_ZOOM);
    }

    @OnClick(R.id.uploadimage_skip_btn)
    void clickSkip() {
        Intent intent = new Intent(this, NewVipCenterActivity.class);
        startActivity(intent);
        finish();
    }

    @OnClick(R.id.uploadimage_submit_btn)
    void clickSubmit() {
        File tempHead = StorageUtil.getAvatarFile(this, Constant.User.AVATAR_FILENAME);
        if (!tempHead.exists()) {
            finish();
            return;
        }
        mLoadingDialog.show();
        showToast(R.string.upload_image_uploading);
        mPresenter.uploadImage(tempHead);
    }

    @Override
    public void dismissWaitingDialog() {
        mLoadingDialog.dismiss();
    }

    @Override
    public void setSubmitBtnClickable(boolean clickable) {
        binding.uploadimageSubmitBtn.setClickable(clickable);
    }

    @Override
    public void showToast(int resId) {
        Toast.makeText(this, getString(resId), Toast.LENGTH_SHORT).show();
    }

    @Override
    public void startVipCenterActivity(boolean isRegister) {
        Intent intent = new Intent(this, NewVipCenterActivity.class);
        startActivity(intent);
    }

    @Override
    public void finishCurActivity() {
        finish();
    }

    @Override
    public void showAlertDialog(String title, int msgResId) {
        new AlertDialog.Builder(UploadImageActivity.this)
                .setTitle(title)
                .setMessage(getString(msgResId)).show();
    }


    public void requestCameraSuccess() {
        File file = StorageUtil.getAvatarFile(this, Constant.User.AVATAR_FILENAME);
        if (!file.getParentFile().exists()) {
            file.getParentFile().mkdirs();
        }
        //通过FileProvider创建一个content类型的Uri
        Uri imageUri;
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.N) {
            imageUri = FileProvider.getUriForFile(mContext, mContext.getPackageName() + ".fileprovider", file);
        } else {
            imageUri = Uri.fromFile(file);
        }

        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
        //将拍取的照片保存到指定URI
        intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
        startActivityForResult(intent, TAKE_PHOTO);

//        Intent intent = new Intent(MediaStore.ACTION_IMAGE_CAPTURE);
//        intent.putExtra(MediaStore.EXTRA_OUTPUT,
//                Uri.fromFile());
//        startActivityForResult(intent, TAKE_PHOTO);
    }

    public void requestCameraFailed() {
        Toast.makeText(this, "请授予必要的权限", Toast.LENGTH_LONG).show();
    }

    private void askForPermisions() {
        PermissionX.init(this).permissions(Manifest.permission.CAMERA)
                .request((granted, strings, strings2) -> {
                    if (granted) requestCameraSuccess();
                    else  requestCameraFailed();
                });
    }



}
