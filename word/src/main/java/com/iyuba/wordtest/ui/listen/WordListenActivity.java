package com.iyuba.wordtest.ui.listen;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.fragment.app.FragmentTransaction;

import com.iyuba.wordtest.R;
import com.iyuba.wordtest.databinding.ActivityContainorBinding;
import com.iyuba.wordtest.ui.listen.empty.WordEmptyFragment;
import com.iyuba.wordtest.ui.listen.singleListen.WordSingleListenFragment;
import com.iyuba.wordtest.ui.listen.singleWrite.WordSingleWriteFragment;
import com.jaeger.library.StatusBarUtil;

/**
 * @desction: 单词练习容器界面
 * @date: 2023/2/7 10:18
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class WordListenActivity extends AppCompatActivity{

    //参数名称
    public static final String TAG_BOOKID = "bookId";
    public static final String TAG_UNITID = "unitId";
    public static final String TAG_LISTEN_TYPE = "listen_type";

    //布局
    private ActivityContainorBinding binding;

    public static void start(Context context, String listenType,int bookId, int unitId) {
        Intent intent = new Intent();
        intent.setClass(context, WordListenActivity.class);
        intent.putExtra(TAG_LISTEN_TYPE,listenType);
        intent.putExtra(TAG_BOOKID, bookId);
        intent.putExtra(TAG_UNITID, unitId);
        context.startActivity(intent);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityContainorBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtil.setColor(this, getResources().getColor(R.color.colorPrimary));

        initToolbar();
        showView();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return super.onOptionsItemSelected(item);
    }

    private void initToolbar() {
        setSupportActionBar(binding.toolbar);
        //根据类型显示
        String listenType = getIntent().getStringExtra(TAG_LISTEN_TYPE);
        if (listenType.equals(WordLibrary.WordListenType.Listen_word)){
            //单词听写
            getSupportActionBar().setTitle("单词拼写");
        }else if (listenType.equals(WordLibrary.WordListenType.Listen_audio)){
            //音频听写
            getSupportActionBar().setTitle("音频听写");
        }else if (listenType.equals(WordLibrary.WordListenType.Write_word)){
            //单词手写
            getSupportActionBar().setTitle("单词手写");
        }else {
            //没有界面
            getSupportActionBar().setTitle("未知界面");
        }
    }

    //显示界面
    private void showView(){
        String listenType = getIntent().getStringExtra(TAG_LISTEN_TYPE);
        int bookId = getIntent().getIntExtra(TAG_BOOKID,0);
        int unitId = getIntent().getIntExtra(TAG_UNITID,0);

        FragmentTransaction transaction = getSupportFragmentManager().beginTransaction();

        switch (listenType){
            case WordLibrary.WordListenType.Listen_word:
                //单词听写
                WordSingleListenFragment singleFragment = WordSingleListenFragment.getInstance(bookId,unitId);
                transaction.add(R.id.container,singleFragment).show(singleFragment).commitNowAllowingStateLoss();
                break;
//            case WordLibrary.WordListenType.Listen_audio:
//                //音频听写
//                WordAudioListenFragment audioFragment = WordAudioListenFragment.getInstance(bookId,unitId);
//                transaction.add(R.id.container,audioFragment).show(audioFragment).commitNowAllowingStateLoss();
//                break;
            case WordLibrary.WordListenType.Write_word:
                //单词手写
                WordSingleWriteFragment writeFragment = WordSingleWriteFragment.getInstance(bookId,unitId);
                transaction.add(R.id.container,writeFragment).show(writeFragment).commitNowAllowingStateLoss();
                break;
            default:
                //显示默认界面
                WordEmptyFragment emptyFragment = WordEmptyFragment.getInstance(listenType);
                transaction.add(R.id.container,emptyFragment).show(emptyFragment).commitNowAllowingStateLoss();
                break;
        }
    }
}
