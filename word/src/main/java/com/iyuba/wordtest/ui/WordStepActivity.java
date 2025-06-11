package com.iyuba.wordtest.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.MenuItem;

import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;

import com.iyuba.wordtest.R;
import com.iyuba.wordtest.adapter.StepAdapter;
import com.iyuba.wordtest.databinding.ActivityWordStepBinding;
import com.iyuba.wordtest.db.BookLevelDao;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.BookLevels;
import com.iyuba.wordtest.entity.NewBookLevels;
import com.iyuba.wordtest.manager.WordManager;
import com.jaeger.library.StatusBarUtil;

import java.util.Objects;


public class WordStepActivity extends AppCompatActivity {
    private static final String BOOKID = "BOOKID";
    private static final String BOOKNAME = "BOOKNAME";
    StepAdapter adapter;
    WordDataBase db;
    int step;
    int unitSize ;

    private int bookId ;
    private BookLevelDao bookLevelDao;
    private String bookName;
    ActivityWordStepBinding binding ;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordStepBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());
        getIntents();
        initDb();
        initClick();
        StatusBarUtil.setColor(this,getResources().getColor(R.color.colorPrimary) , 0);
        initToolBar();
    }

    private void initClick() {
        binding.selectBook.setOnClickListener(v -> selectBook());
    }

    private void initDb() {
        bookLevelDao  = WordDataBase.getInstance(this).getBookLevelDao();
        saveFirst();
    }

    private void getIntents() {
        bookId = getIntent().getIntExtra(BOOKID,211);
        bookName = getIntent().getStringExtra(BOOKNAME).replace("(人教版)", "");
        if (bookId == 0){
//            EventBus.getDefault().post(new SelectBookEvent());
            finish();
        }
    }

    private void saveFirst() {
        if (WordManager.WordDataVersion == 2) {
            new Thread(new Runnable() {
                @Override
                public void run() {
                    if (db.getNewBookLevelDao().getBookLevel(bookId, WordManager.getInstance().userid) == null) {
                        NewBookLevels levels = new NewBookLevels(bookId, 0, 0, 0, WordManager.getInstance().userid);
                        db.getNewBookLevelDao().saveBookLevel(levels);
                    }
                }
            }).start();
        } else
        if (bookLevelDao.getBookLevel(bookId) == null){
            BookLevels levels = new BookLevels(bookId,1,0 ,0);
            bookLevelDao.saveBookLevel(levels);
        }
    }

    private void initToolBar() {
        setSupportActionBar(binding.toolbar);
        Objects.requireNonNull(getSupportActionBar()).setDisplayShowTitleEnabled(false);
    }


    @Override
    protected void onResume() {
        super.onResume();
        initData();
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            finish();
        }
        return true;
    }


    public void selectBook() {
//        EventBus.getDefault().post(new SelectBookEvent());
        finish();
    }

    public static Intent buildIntent(Context context , int bookId , String bookName){
        Intent intent = new Intent();
        intent.setClass(context,WordStepActivity.class);
        intent.putExtra(BOOKID , bookId);
        intent.putExtra(BOOKNAME , bookName);
        return intent;
    }

    private void initData(){
        db = WordDataBase.getInstance(this);
        unitSize = db.getTalkShowWordsDao().getUnitsByBook(bookId).size();
        step = loadStep() ;
        binding.allWords.setText(String.format("%s 总单词数: %s",  bookName,db.getTalkShowWordsDao().getBookWords(bookId).size()));
        adapter = new StepAdapter(bookId, step, unitSize);
        binding.gridview.setAdapter(adapter);
    }

    private int loadStep() {
        return db.getBookLevelDao().getBookLevel(bookId).level;
    }

}
