package com.iyuba.talkshow.ui.web;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Bitmap;
import android.net.Uri;

import androidx.annotation.Nullable;

import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.widget.Toolbar;

import android.text.TextUtils;
import android.util.Log;
import android.view.KeyEvent;
import android.view.MenuItem;
import android.view.View;
import android.webkit.DownloadListener;
import android.webkit.WebChromeClient;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.TextView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.ActivityWebBinding;
import com.iyuba.talkshow.ui.base.BaseActivity;
import com.iyuba.talkshow.ui.widget.LoadingDialog;
import com.iyuba.talkshow.util.LogUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.umeng.analytics.MobclickAgent;

public class WebActivity extends BaseActivity {
    private static final String URL = "url";
    private static final String TITLE = "title";
    private static final String BUTTON = "button_right";
    private static final String BUTTON_URL = "button_right_url";

    ActivityWebBinding binding;

    public static Intent buildIntent(Context context, String url) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(URL, url);
        return intent;
    }

    public static Intent buildIntent(Context context, String url, String title) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(TITLE, title);
        return intent;
    }

    public static Intent buildIntent(Context context, String url, String right, String rightUrl) {
        Intent intent = new Intent(context, WebActivity.class);
        intent.putExtra(URL, url);
        intent.putExtra(BUTTON, right);
        intent.putExtra(BUTTON_URL, rightUrl);
        return intent;
    }


    LoadingDialog mLoadingDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWebBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        setSupportActionBar(binding.webToolbar);
        mLoadingDialog = new LoadingDialog(this);
    }


    @Override
    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
        super.onPostCreate(savedInstanceState);

        Intent intent = this.getIntent();
        String url = intent.getStringExtra(URL);
        String title = intent.getStringExtra(TITLE);
        if (!TextUtils.isEmpty(title)){
            title = title.replace("《","").replace("》","");
        }
        String right = intent.getStringExtra(BUTTON);
        String rightUrl = intent.getStringExtra(BUTTON_URL);

        if (!TextUtils.isEmpty(title)) {
            binding.webToolbar.setTitle(title);
        }

        if (!TextUtils.isEmpty(right)) {
            binding.tvRight.setVisibility(View.VISIBLE);
            binding.tvRight.setText(right);
            final String finalRightUrl = rightUrl;
            binding.tvRight.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    startActivity(WebActivity.buildIntent(mContext, finalRightUrl));
                }
            });
        }

//        mWebView.addJavascriptInterface(this, "Android");
        binding.webWebview.getSettings().setJavaScriptEnabled(true);
        if (!TextUtils.isEmpty(url)) {
            binding.webWebview.loadUrl(url);
        }
        binding.webWebview.requestFocus();
        binding.webWebview.getSettings().setBuiltInZoomControls(true);// 显示放大缩小
        binding.webWebview.getSettings().setSupportZoom(true);// 可放大
        binding.webWebview.getSettings().setRenderPriority(WebSettings.RenderPriority.HIGH);// 提高渲染,加快加载速度
        binding.webWebview.getSettings().setUseWideViewPort(true);
        binding.webWebview.getSettings().setLoadWithOverviewMode(true);
        binding.webWebview.getSettings().setDomStorageEnabled(true);
        binding.webWebview.getSettings().setDatabaseEnabled(true);
        binding.webWebview.getSettings().setGeolocationEnabled(true);

        binding.webWebview.setWebChromeClient(new WebChromeClient() {
            @Override
            public void onReceivedTitle(WebView view, String title) {
                super.onReceivedTitle(view, title);

                if (TextUtils.isEmpty(binding.webToolbar.getTitle().toString())){
                    binding.webToolbar.setTitle(title);
                }
            }

            @Override
            public void onProgressChanged(WebView view, int newProgress) {
                super.onProgressChanged(view, newProgress);

                LogUtil.d("web网页进度", "onProgressChanged: --"+newProgress);
                if (newProgress>=70
                        &&mLoadingDialog!=null
                        &&mLoadingDialog.isShowing()
                        &&!isDestroyed()){
                    mLoadingDialog.dismiss();
                }
            }
        });
        binding.webWebview.setWebViewClient(new WebViewClient() {
            @Override
            public void onPageStarted(WebView view, String url, Bitmap favicon) {
                if (!mLoadingDialog.isShowing() && !isDestroyed()) {
                    mLoadingDialog.show();
                }
            }

            @Override
            public void onReceivedError(WebView view, int errorCode,
                                        String description, String failingUrl) {
                super.onReceivedError(view, errorCode, description, failingUrl);
                if (mLoadingDialog!=null
                        &&mLoadingDialog.isShowing()
                        &&!isDestroyed()){
                    mLoadingDialog.dismiss();
                }
            }

            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
//                url = "https://wxpay.wxutil.com/mch/pay/h5jumppage.php";

                //这里需要处理其他类型开头的东西
                if (url.startsWith("http://")||url.startsWith("https://")){
                    view.loadUrl(url);
                }else {
                    new AlertDialog.Builder(WebActivity.this)
                            .setTitle("请注意")
                            .setMessage("当前网页操作需要打开其他app，是否允许？")
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialog, int which) {
                                    Intent jumpIntent = new Intent();
                                    jumpIntent.setAction(Intent.ACTION_VIEW);
                                    jumpIntent.setData(Uri.parse(url));
                                    startActivity(jumpIntent);
                                }
                            }).setNegativeButton("取消",null)
                            .setCancelable(false)
                            .show();
                }
                return true;
            }

            @Override
            public void onPageFinished(WebView view, String url) {
                if (mLoadingDialog!=null
                        &&mLoadingDialog.isShowing()
                        &&!isDestroyed()){
                    mLoadingDialog.dismiss();
                }
            }
        });
        binding.webWebview.setDownloadListener(new DownloadListener() {

            @Override
            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
                try {
                    Intent intent = new Intent();
                    intent.setAction(Intent.ACTION_VIEW);
                    intent.setData(Uri.parse(url));
                    startActivity(intent);
                }catch (Exception e){
                    ToastUtil.show(WebActivity.this,"未查询到可打开此文件的软件");
                }
            }
        });
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
    public boolean onKeyDown(int keyCoder, KeyEvent event) {
        if (binding.webWebview.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
            binding.webWebview.goBack();
            return true;
        } else if (!binding.webWebview.canGoBack() && keyCoder == KeyEvent.KEYCODE_BACK) {
            finish();
            return true;
        }
        return false;
    }

    @Override
    public void finish() {
        if (mLoadingDialog!=null
                &&mLoadingDialog.isShowing()
                &&!isDestroyed()){
            mLoadingDialog.dismiss();
        }
        super.finish();
    }

    @Override
    protected void onDestroy() {
        if (mLoadingDialog!=null
                &&mLoadingDialog.isShowing()
                &&!isDestroyed()){
            mLoadingDialog.dismiss();
        }
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                finish();
                return true;
            default:
                return false;
        }
    }

//    @JavascriptInterface
//    public void successOnAndroid() {
//        WebActivity.this.finish();
//    }


//    @Override
//    public void onBackPressed() {
//        super.onBackPressed();
//
//        if (binding.webWebview!=null){
//            if (binding.webWebview.canGoBack()){
//                binding.webWebview.goBack();
//            }else {
//                finish();
//            }
//        }
//    }
}
