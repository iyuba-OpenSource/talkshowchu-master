package com.iyuba.wordtest.ui.test;

import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MenuItem;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.appcompat.app.AppCompatActivity;
import androidx.recyclerview.widget.GridLayoutManager;

import com.iyuba.module.toolbox.MD5;
import com.iyuba.wordtest.adapter.WordClearAdapter;
import com.iyuba.wordtest.bean.UploadExamResult;
import com.iyuba.wordtest.bean.UploadTestBean;
import com.iyuba.wordtest.bean.WordClearBean;
import com.iyuba.wordtest.databinding.ActivityWordClearBinding;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.BookLevels;
import com.iyuba.wordtest.entity.NewBookLevels;
import com.iyuba.wordtest.entity.TalkShowTests;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.event.WordTestEvent;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.network.HttpManager;
import com.iyuba.wordtest.ui.TalkshowWordListActivity;
import com.iyuba.wordtest.utils.ToastUtil;
import com.iyuba.wordtest.widget.LoadingDialog;

import org.greenrobot.eventbus.EventBus;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.RequestBody;

/**
 * 单词消消乐
 *
 * 1.只要进行评测，就可以保存进度，实时更新
 * 2.只有全部完成之后才能提交进度和刷新通过率
 * 3.只要完成之后，如果覆盖进度，则会把之前的进度替换掉，但是通过率不会替换
 * 4.每次进入如果存在进度，则会提示根据进度处理
 */
public class WordClearActivity extends AppCompatActivity {

    //标识
    public static String BOOKID = "BOOKID";
    public static String UNIT = "UNIT";
    public static String STEP = "STEP";

    private static final int COL_COUNT = 4;//列表的列数
    private static final int ROW_COUNT = 4;//列表的行数

    private static final int SHOW_COUNT = 8;//每页的展示数量

    //布局
    private ActivityWordClearBinding binding;

    //数据
    private int bookId;
    private int unitId;
    private String uid;
    private int step;

    private Map<Integer, List<WordClearBean>> groupMap;//总的分组数据
    private Map<String,WordClearBean> saveMap;//保存已经完成的数据
    private WordClearAdapter clearAdapter;//适配器
    private WordDataBase db;//数据库
    private int curSelectPage = 1;//当前的页码
    private List<TalkShowWords> curTotalList;//当前的所有数据

    private int selectPosition = -1;//选中的位置
    private WordClearBean selectBean = null;//选中的数据

    private LoadingDialog loadingDialog;//加载弹窗

    public static void start(Context context, int bookId, int unitId, int pos) {
        Intent starter = new Intent(context, WordClearActivity.class);
        starter.putExtra(TalkshowWordListActivity.UNIT, unitId);
        starter.putExtra(TalkshowWordListActivity.BOOKID, bookId);
        starter.putExtra(TalkshowWordListActivity.STEP, pos);
        context.startActivity(starter);
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        binding = ActivityWordClearBinding.inflate(getLayoutInflater());
        setContentView(binding.getRoot());

        bookId = getIntent().getIntExtra(BOOKID,0);
        unitId = getIntent().getIntExtra(UNIT,0);
        uid = WordManager.getInstance().userid;
        step = getIntent().getIntExtra(STEP,0);

        db = WordDataBase.getInstance(getApplicationContext());
        curTotalList = new ArrayList<>();
        groupMap = new HashMap<>();
        saveMap = new HashMap<>();

        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(this);
            loadingDialog.setMessage("提交进度中...");
        }

        initView();
        initList();

        //查询数据并显示方式
        showData();
    }

    private void initView(){
        binding.textTinyHint.setText("单词消消乐");
        binding.loadLast.setText("保存进度");

        setSupportActionBar(binding.toolbar);
        getSupportActionBar().setDisplayShowTitleEnabled(false);

        //保存进度
        binding.loadLast.setOnClickListener(v->{
            if (saveMap.keySet().size()==0){
                ToastUtil.showToast(this,"请至少练习1题后进行进度保存");
                return;
            }

            //判断是否删除数据
            checkExistDataAndDelete();

            List<TalkShowWords> wordList = db.getTalkShowWordsDao().getUnitWords(bookId,unitId);
            List<TalkShowTests> testList = db.getTalkShowTestsDao().getUnitWords(bookId,unitId,uid);
            int allWords = wordList.size();
            int testWords = testList.size();
            int saveWords = saveMap.keySet().size();

            if ((allWords == saveWords)||(testWords+saveWords == allWords)){
                ToastUtil.showToast(this,"习题已全部完成，请提交进度");
                return;
            }

            for (String key:saveMap.keySet()){
                saveTestData(saveMap.get(key).getData(),true);
            }

            //刷新数据
            initData(true);
            ToastUtil.showToast(this,"当前进度已保存，进度已重置");
            //刷新进度
            EventBus.getDefault().post(new WordTestEvent(bookId));
        });
        //提交进度
        binding.submitProgress.setOnClickListener(v->{
            if (saveMap.keySet().size()==0){
                ToastUtil.showToast(this,"未进行评测，无需提交进度");
                return;
            }

            checkExistDataAndDelete();

            //获取全部的数据进行插入
            List<TalkShowWords> allWordsList = db.getTalkShowWordsDao().getUnitWords(bookId,unitId);
            for (int i = 0; i < allWordsList.size(); i++) {
                saveTestData(allWordsList.get(i),true);
            }

            //保存进度
            saveWordLevel();
            //保存网络数据
            uploadResultData();
            //刷新进度
            EventBus.getDefault().post(new WordTestEvent(bookId));
        });
    }

    private void initList(){
        clearAdapter = new WordClearAdapter(this,new ArrayList<>());
        GridLayoutManager manager = new GridLayoutManager(this,COL_COUNT);
        binding.lineUpView.setLayoutManager(manager);
        binding.lineUpView.setAdapter(clearAdapter);
        clearAdapter.setOnItemClickListener(new WordClearAdapter.OnItemClickListener() {
            @Override
            public void onClick(int position, WordClearBean bean) {
                //这里判断是否被选中并且需要隐藏
                List<WordClearBean> tempList = groupMap.get(curSelectPage);
                WordClearBean tempBean = tempList.get(position);
                if (!tempBean.isVisible()){
                    return;
                }

                if (selectPosition==-1&&selectBean==null){
                    //第一个数据
                    selectPosition = position;
                    selectBean = bean;
                    clearAdapter.setOncePosition(position);
                    return;
                }

                //判断是否是同一个
                if (selectPosition==position&&selectBean == bean){
                    selectPosition = -1;
                    selectBean = null;
                    clearAdapter.setOncePosition(-1);
                    return;
                }

                String itemId = selectBean.getLineId();
                String curId = bean.getLineId();
                if (itemId.equals(curId)){
                    clearAdapter.setSecondPosition(position);

                    //设置数据隐藏
                    List<WordClearBean> itemList = groupMap.get(curSelectPage);
                    itemList.get(selectPosition).setVisible(false);
                    itemList.get(position).setVisible(false);
                    groupMap.put(curSelectPage,itemList);
                    //刷新数据
                    clearAdapter.refreshData(itemList,false);
                    //重置
                    selectPosition = -1;
                    selectBean = null;
                    //保存数据
                    saveMap.put(curId,bean);
                    binding.progress.setText("完成度："+saveMap.keySet().size()+"/"+curTotalList.size());
                    //最后一个则刷新数据
                    int curProgress = saveMap.keySet().size();
                    int totalCount = curTotalList.size();
                    int curTotalProgress = curSelectPage*SHOW_COUNT;

                    if (curProgress==curTotalProgress||curProgress==totalCount){
                        new Handler().postDelayed(() -> {
                            runOnUiThread(() -> {
                                //判断是显示结果还是下一个
                                if (curProgress == totalCount){
                                    //显示结果
                                    binding.loadLast.setVisibility(View.INVISIBLE);
                                    showResult();
                                }else {
                                    //显示下一个
                                    showNextData();
                                }
                            });
                        },500);
                    }
                }else {
                    ToastUtil.showToast(WordClearActivity.this,"当前内容与选中的内容不匹配，请重新选择");
                }
            }
        });

        //刷新布局高度
        binding.lineUpView.post(new Runnable() {
            @Override
            public void run() {
                int height = binding.lineUpView.getHeight();
                int itemHeight = (int) (height / ROW_COUNT);
                clearAdapter.refreshHeight(itemHeight);
            }
        });
    }

    private void initData(boolean isContinue){
        curSelectPage = 1;
        groupMap.clear();
        curTotalList.clear();

        if (isContinue){
            groupMap = getLastData();
        }else {
            curTotalList = db.getTalkShowWordsDao().getUnitWords(bookId,unitId);
            Collections.shuffle(curTotalList);
            //总的页数
            int pageCount = curTotalList.size()/SHOW_COUNT;
            if (curTotalList.size()%SHOW_COUNT>0){
                pageCount+=1;
            }
            //当前数量
            int curCount = 0;
            for (int i = 0; i < pageCount; i++) {

                List<WordClearBean> itemList = new ArrayList<>();
                //每个item的数量
                int itemCount = SHOW_COUNT;
                if (curTotalList.size()-curCount<SHOW_COUNT){
                    itemCount = curTotalList.size()-curCount;
                }

                item:for (int j = curCount; j < curTotalList.size(); j++) {

                    int tagCount = groupMap.keySet().size()*SHOW_COUNT+itemCount;
                    if (tagCount>=curCount){
                        curCount++;

                        TalkShowWords words = curTotalList.get(j);
                        String id = words.book_id+"_"+words.unit_id+"_"+words.position;
                        itemList.add(new WordClearBean(
                                WordClearBean.TAG_SHOW_PORN,
                                id,
                                true,
                                words
                        ));
                        itemList.add(new WordClearBean(
                                WordClearBean.TAG_SHOW_WORD,
                                id,
                                true,
                                words
                        ));

                        //如果被整出，则可以保存并且跳出
                        int tag = curCount/tagCount;
                        if (tag>=1){
                            //这里计算数组位置
                            int position = curCount/SHOW_COUNT;
                            if (curCount%SHOW_COUNT>0){
                                position+=1;
                            }
                            groupMap.put(position,itemList);
                            break item;
                        }
                    }
                }
            }
        }

        List<WordClearBean> showList = groupMap.get(curSelectPage);
        Collections.shuffle(showList);
        clearAdapter.refreshData(showList,true);
        //重置数据
        saveMap.clear();
        selectPosition = -1;
        selectBean = null;
        //显示页码
        showPageIndex();
    }

    private void showData(){
        List<TalkShowTests> saveList = checkExistDataAndReset();
        List<TalkShowWords> allList = db.getTalkShowWordsDao().getUnitWords(bookId,unitId);

        if (saveList!=null&&saveList.size()>0&&saveList.size()<allList.size()){
            new AlertDialog.Builder(this)
                    .setTitle("评测进度")
                    .setMessage(String.format("当前检测到上次做题的进度(%s)，是否继续做题?",saveList.size()+"/"+allList.size()))
                    .setCancelable(false)
                    .setNegativeButton("重新做题", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            //删除之前的数据
                            db.getTalkShowTestsDao().deleteWordTest(Integer.parseInt(uid),bookId,unitId);
                            //重置数据
                            initData(false);
                            //刷新进度
                            EventBus.getDefault().post(new WordTestEvent(bookId));

                            dialog.dismiss();
                        }
                    }).setPositiveButton("继续做题", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            initData(true);

                            dialog.dismiss();
                        }
                    }).show();
        }else {
            //重新展示
            initData(false);
        }
    }

    //处理剩余的数据
    private Map<Integer,List<WordClearBean>> getLastData(){
        Map<Integer,List<WordClearBean>> tempMap = new HashMap<>();

        List<TalkShowTests> existList = db.getTalkShowTestsDao().getUnitWords(bookId,unitId,uid);
        List<TalkShowWords> totalList = db.getTalkShowWordsDao().getUnitWords(bookId,unitId);

        Map<String,TalkShowWords> allMap = new HashMap<>();
        for (int i = 0; i < totalList.size(); i++) {
            String key = totalList.get(i).book_id+"_"+totalList.get(i).unit_id+"_"+totalList.get(i).position;
            allMap.put(key,totalList.get(i));
        }

        Map<String,TalkShowTests> existMap = new HashMap<>();
        for (int i = 0; i < existList.size(); i++) {
            String key = existList.get(i).book_id+"_"+existList.get(i).unit_id+"_"+existList.get(i).position;
            existMap.put(key,existList.get(i));
        }

        //获取所有的剩余数据
        for (String key:allMap.keySet()){
            if (existMap.get(key)==null){
                curTotalList.add(allMap.get(key));
            }
        }

        Collections.shuffle(curTotalList);

        //获取剩余的页数
        int pageCount = curTotalList.size()/SHOW_COUNT;
        if (curTotalList.size()%SHOW_COUNT>0){
            pageCount+=1;
        }
        //进行分组
        int curCount = 0;
        for (int i = 0; i < pageCount; i++) {

            List<WordClearBean> itemList = new ArrayList<>();
            //需要保存的item数量
            int itemCount = SHOW_COUNT;
            if (curTotalList.size()-curCount<SHOW_COUNT){
                itemCount = curTotalList.size()-curCount;
            }

            item:for (int j = curCount; i < curTotalList.size(); j++) {

                int tagCount = tempMap.keySet().size()*SHOW_COUNT+itemCount;
                if (tagCount>=curCount){
                    curCount++;

                    TalkShowWords words = curTotalList.get(j);
                    String id = words.book_id+"_"+words.unit_id+"_"+words.position;
                    itemList.add(new WordClearBean(
                            WordClearBean.TAG_SHOW_PORN,
                            id,
                            true,
                            words
                    ));

                    itemList.add(new WordClearBean(
                            WordClearBean.TAG_SHOW_WORD,
                            id,
                            true,
                            words
                    ));

                    //如果可以被整除，则跳出
                    int tag = curCount/tagCount;
                    if (tag>=1){
                        //这里计算数组位置
                        int position = curCount/SHOW_COUNT;
                        if (curCount%SHOW_COUNT>0){
                            position+=1;
                        }
                        tempMap.put(position,itemList);
                        break item;
                    }
                }
            }
        }

        return tempMap;
    }

    //插入或者更新测试数据
    private void saveTestData(TalkShowWords words,boolean isToRight){
        TalkShowTests temp = db.getTalkShowTestsDao().getUnitWord(bookId,unitId,words.position,uid);
        //将数据设置为已经回答和正确
        words.answer = "1";//已经回答
        if (isToRight){
            words.wrong = 1;//正确
        }else {
            words.wrong = 0;//错误
        }
        if (temp==null){
            db.getTalkShowTestsDao().insertWord(WordManager.getInstance().Words2Tests(words));
        }else {
            db.getTalkShowTestsDao().updateSingleWord(WordManager.getInstance().Words2Tests(words));
        }
    }

    //显示页码
    private void showPageIndex(){
        binding.sort.setText("页码："+curSelectPage+"/"+groupMap.keySet().size());
        binding.progress.setText("完成度："+saveMap.keySet().size()+"/"+curTotalList.size());
    }

    //显示下一个数据
    private void showNextData(){
        curSelectPage+=1;
        binding.sort.setText("页码："+curSelectPage+"/"+groupMap.keySet().size());
        List<WordClearBean> nextData = groupMap.get(curSelectPage);
        Collections.shuffle(nextData);
        clearAdapter.refreshData(nextData,false);
    }

    //显示结果
    private void showResult(){
        binding.dataView.setVisibility(View.GONE);
        binding.resultLayout.setVisibility(View.VISIBLE);
    }

    //根据当前情况处理是否显示保存进度
    private void showProgressSave(){
        List<TalkShowWords> allList = db.getTalkShowWordsDao().getUnitWords(bookId,unitId);
        List<TalkShowTests> saveList = checkExistDataAndReset();
        int progressCount = saveMap.keySet().size();

        if (progressCount<=0){
            finish();
        }else {

            boolean isAllFinish = (saveList.size()+progressCount==allList.size());
            String showTitle = "";
            String showMsg = "";
            if (isAllFinish){
                showTitle = "保存进度";
                showMsg = "当前习题评测已经完成，如未提交则会清空当前的评测进度，是否保存进度后再退出？";
            }else {
                showTitle = "提交进度";
                showMsg = "退出后会清空当前的评测进度，是否保存进度后再退出？";
            }

            //显示保存
            new AlertDialog.Builder(this)
                    .setCancelable(false)
                    .setTitle(showTitle)
                    .setMessage(showMsg)
                    .setNegativeButton("继续退出", new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int which) {
                            saveMap.clear();
                            selectPosition = -1;
                            selectBean = null;

                            finish();
                        }
                    }).setPositiveButton(showTitle, new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    checkExistDataAndDelete();

                    if (isAllFinish){
                        //获取全部的数据进行插入
                        List<TalkShowWords> allWordsList = db.getTalkShowWordsDao().getUnitWords(bookId,unitId);
                        for (int i = 0; i < allWordsList.size(); i++) {
                            saveTestData(allWordsList.get(i),true);
                        }
                    }else {
                        //插入部分新增的数据
                        for (String key:saveMap.keySet()){
                            saveTestData(saveMap.get(key).getData(),true);
                        }
                    }

                    saveMap.clear();
                    selectPosition = -1;
                    selectBean = null;

                    if (isAllFinish){
                        //提交当前的评测数据
                        uploadResultData();
                    }

                    //刷新进度
                    EventBus.getDefault().post(new WordTestEvent(bookId));
                }
            }).show();
        }
    }

    // 更新单词关卡
    private void saveWordLevel() {
        BookLevels levels =  db.getBookLevelDao().getBookLevel(bookId) ;
        if (WordManager.WordDataVersion == 2) {
            new Handler().post(new Runnable() {
                @Override
                public void run() {
                    NewBookLevels newBookLevel = db.getNewBookLevelDao().getBookLevel(bookId, WordManager.getInstance().userid);
                    if (newBookLevel == null) {
                        newBookLevel = new NewBookLevels(bookId, 1 ,0 ,0, WordManager.getInstance().userid);
                        db.getNewBookLevelDao().saveBookLevel(newBookLevel);
                    } else {
                        if (newBookLevel.level > step) {
                            Log.e("WordTestActivity", "newBookLevel = " + newBookLevel.level +" is larger than step " + step);
                        } else {
                            newBookLevel.level++;
                            db.getNewBookLevelDao().updateBookLevel(newBookLevel);
                        }
                    }
                    EventBus.getDefault().post(new WordTestEvent(bookId));
                }
            });
        } else if (levels != null) {
            levels.level++;
            Log.e("WordTestActivity", "saveWordLevel levels.level = " + levels.level);
            db.getBookLevelDao().updateBookLevel(levels);
        }
    }

    //上传结果数据
    private void uploadResultData(){
        if (loadingDialog!=null&&!loadingDialog.isShowing()){
            loadingDialog.show();
        }

        //转换成需要的数据
        String date = new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        List<UploadTestBean.TestListBean> upList = new ArrayList<>();
        List<TalkShowTests> testList = db.getTalkShowTestsDao().getUnitWords(bookId,unitId,uid);
        for (int i = 0; i < testList.size(); i++) {
            UploadTestBean.TestListBean tempBean = new UploadTestBean.TestListBean();
            tempBean.AnswerResut = 1;
            tempBean.BeginTime = date;
            tempBean.TestTime = date;
            tempBean.Category = "单词闯关";
            tempBean.RightAnswer = testList.get(i).word;
            tempBean.TestId = testList.get(i).position ;
            tempBean.LessonId = String.valueOf(testList.get(i).unit_id);
            tempBean.TestMode = "W";
            tempBean.UserAnswer = testList.get(i).word;

            upList.add(tempBean);
        }

        //转换成最终数据
        UploadTestBean testBean = new UploadTestBean();
        testBean.testList = upList;
        testBean.sign = createSign();
        testBean.appId = WordManager.getInstance().appid;
        try {
            //去掉获取mac地址操作
//            GetDeviceInfo getDeviceInfo = new GetDeviceInfo(getApplicationContext());
//            testBean.DeviceId = getDeviceInfo.getLocalMACAddress();
            testBean.DeviceId = "";

        } catch (Exception e) {
            e.printStackTrace();
            testBean.DeviceId = "";
        }
        testBean.format = "json";
        testBean.lesson = String.valueOf(bookId);
        testBean.mode = 2;
        testBean.uid= WordManager.getInstance().userid;
        testBean.scoreList = new ArrayList<>() ;

        //提交数据
        RequestBody requestBody = HttpManager.getBody(testBean);
        HttpManager.getUploadExamApi().uploadWordExamNew(requestBody)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<UploadExamResult>() {
                    @Override
                    public void onSubscribe(@NonNull Disposable d) {

                    }

                    @Override
                    public void onNext(@NonNull UploadExamResult result) {
                        if (result != null) {
                            ToastUtil.showToast(WordClearActivity.this, "提交进度成功");
                        }

                        if (loadingDialog!=null){
                            loadingDialog.dismiss();
                        }

                        finish();
                    }

                    @Override
                    public void onError(@NonNull Throwable e) {
                        if (loadingDialog!=null){
                            loadingDialog.dismiss();
                        }

                        finish();
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取提交数据的sign
    private String createSign(){
        /* 用户id+你的应用id+统计类别+iyubaExam+当前时间戳*/
        String sign = WordManager.getInstance().userid + WordManager.getInstance().appid +
                bookId + "iyubaExam" + new SimpleDateFormat("yyyy-MM-dd").format(new Date());
        return MD5.getMD5ofStr(sign).toLowerCase();
    }

    //检查数据是否相同并且进行删除操作
    private void checkExistDataAndDelete(){
        List<TalkShowTests> existList = db.getTalkShowTestsDao().getUnitWords(bookId,unitId,uid);
        List<TalkShowWords> allList = db.getTalkShowWordsDao().getUnitWords(bookId,unitId);
        if (existList.size() == allList.size()){
            db.getTalkShowTestsDao().deleteWordTest(Integer.parseInt(uid),bookId,unitId);
        }
    }

    //检查数据是否相同并且重置
    private List<TalkShowTests> checkExistDataAndReset(){
        List<TalkShowTests> existList = db.getTalkShowTestsDao().getUnitWords(bookId,unitId,uid);
        List<TalkShowWords> allList = db.getTalkShowWordsDao().getUnitWords(bookId,unitId);
        if (existList.size() == allList.size()){
            existList = new ArrayList<>();
        }
        return existList;
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if (item.getItemId() == android.R.id.home) {
            //是否需要保存进度
            showProgressSave();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onBackPressed() {
        //根据情况显示弹窗
        showProgressSave();
    }
}
