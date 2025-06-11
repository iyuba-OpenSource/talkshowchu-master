package com.iyuba.wordtest.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.DividerItemDecoration;
import androidx.recyclerview.widget.LinearLayoutManager;

import android.view.MenuItem;
import android.view.View;

//import com.iyuba.wordtest.adapter.SimpleWordListAdapter;
import com.iyuba.wordtest.R;
import com.iyuba.wordtest.ui.detail.WordDetailActivity;
import com.iyuba.wordtest.databinding.ActivityWordListBinding;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.CetRootWord;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.manager.WordConfigManager;
import com.iyuba.wordtest.ui.test.WordTestActivity;
import com.jaeger.library.StatusBarUtil;


import java.util.List;


public class WordListActivity extends AppCompatActivity {
    private boolean showSideBar;
    ActivityWordListBinding binding ;

    public static void startIntnent(Context mContext, int stage ,boolean showSideBar) {
        Intent intent = new Intent(mContext, WordListActivity.class);
        intent.putExtra("stage", stage);
        intent.putExtra("showSideBar", showSideBar);
        mContext.startActivity(intent);
    }



    WordDataBase db;
    Context context;
//    SimpleWordListAdapter adapter;
    List<CetRootWord> list;
    List<TalkShowWords> talkShowWords;
    int stage;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordListBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        StatusBarUtil.setColor(this,getResources().getColor(R.color.colorPrimary) , 0);
        context = this;
        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        stage = getIntent().getExtras().getInt("stage");
        showSideBar = getIntent().getExtras().getBoolean("showSideBar");
        db = WordDataBase.getInstance(this);

        binding.recyclerView.setLayoutManager(new LinearLayoutManager(this));
        if (stage == -1) {
            list = db.getCetRootWordDao().getWordsCollect();
        } else {
            list = db.getCetRootWordDao().getWordsByStage(stage);
        }
//        adapter = new SimpleWordListAdapter(list, false);
//        adapter.setShowOrder(true);
//        recyclerView.setAdapter(adapter);
        binding.recyclerView.addItemDecoration(new DividerItemDecoration(this, DividerItemDecoration.VERTICAL));

        binding.recyclerView.setItemAnimator(new DefaultItemAnimator());
    }

    public void onViewClicked(View view) {
        int id = view.getId();
        if (id == R.id.study) {
            Intent intent = new Intent(context, WordDetailActivity.class);
//            intent.putExtra("stage", WordConfigManager.Instance(context).loadInt("stage", 1));
            startActivity(intent);
        } else if (id == R.id.test) {
            WordTestActivity.start(context, WordConfigManager.Instance(context).loadInt("stage", 1));
        }
    }

    @Override
    protected void onResume() {

        super.onResume();
        if (stage == -1) {
            list = db.getCetRootWordDao().getWordsCollect();
            binding.test.setVisibility(View.GONE);
            binding.study.setVisibility(View.GONE);
//            adapter = new SimpleWordListAdapter(list, false);
        } else if (stage == 0) {
            list = db.getCetRootWordDao().getAllRootWord();
            binding.test.setVisibility(View.GONE);
            binding.study.setVisibility(View.GONE);
//            adapter = new SimpleWordListAdapter(list, true);
//            adapter.setShowOrder(true);
        } else {
            list = db.getCetRootWordDao().getWordsByStage(stage);
//            adapter = new SimpleWordListAdapter(list, true);
        }
//        recyclerView.setAdapter(adapter);
        binding.sidebar.setSelectedSideBarColor(R.color.app_color);
        binding.sidebar.setRecyclerView(binding.recyclerView);
        if (!showSideBar){
            binding.sidebar.setVisibility(View.GONE);
        }
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            onBackPressed();
        }
        return super.onOptionsItemSelected(item);
    }
}
