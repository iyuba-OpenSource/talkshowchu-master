//package com.iyuba.talkshow.newce.search.oldSearch;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.text.TextUtils;
//import android.view.KeyEvent;
//import android.view.View;
//import android.view.inputmethod.EditorInfo;
//import android.widget.TextView;
//
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.iyuba.talkshow.Constant;
//import com.iyuba.talkshow.data.model.Voa;
//import com.iyuba.talkshow.data.model.VoaText;
//import com.iyuba.talkshow.databinding.ActivitySearchBinding;
//import com.iyuba.talkshow.newce.search.newSearch.list.SearchListActivity;
//import com.iyuba.talkshow.newce.search.adapter.SearchVoaAdapter;
//import com.iyuba.talkshow.newce.search.adapter.SearchWordAdapter;
//import com.iyuba.talkshow.newce.search.newSearch.NewSearchActivity;
//import com.iyuba.talkshow.ui.base.BaseViewBindingActivity;
//import com.iyuba.wordtest.entity.TalkShowWords;
//
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.inject.Inject;
//
///**
// * 搜索界面
// *
// * 搜索本地单词、例句、课文等
// * 当前界面已经废弃，新的界面请查看{@link NewSearchActivity}
// */
//@Deprecated
//public class SearchActivity extends BaseViewBindingActivity<ActivitySearchBinding> implements SearchMvpView {
//
//    @Inject
//    SearchPresenter presenter;
//
//    //单词
//    private SearchWordAdapter wordAdapter;
//    //句子
//    private SearchSentenceAdapter sentenceAdapter;
//    //文章
//    private SearchVoaAdapter voaAdapter;
//
//    //标志
//    private static final String WORD = "word";
//    private static final String SENTENCE = "sentence";
//    private static final String VOA = "voa";
//
//    private static final String SUCCESS = "success";
//    private static final String FAIL = "fail";
//
//    //搜索的关键词
//    private String searchKeyWord;
//    //搜索记录保存
//    private Map<String,String> searchMarkMap = new HashMap<>();
//
//    //跳转
//    public static void start(Context context){
//        Intent intent = new Intent();
//        intent.setClass(context,SearchActivity.class);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//        activityComponent().inject(this);
//        presenter.attachView(this);
//    }
//
//    @Override
//    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        initClick();
//        initData();
//    }
//
//    private void initClick(){
//        binding.backToolbar.setOnClickListener(v->{
//            finish();
//        });
//        binding.deleteToolbar.setOnClickListener(v->{
//            binding.inputToolbar.setText("");
//        });
//        binding.inputToolbar.setOnEditorActionListener(new TextView.OnEditorActionListener() {
//            @Override
//            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
//                if (actionId == EditorInfo.IME_ACTION_SEARCH){
//                    //查询
//                    searchMarkMap.clear();
//
//                    //空格直接提示
//                    searchKeyWord = binding.inputToolbar.getText().toString().trim();
//                    if (TextUtils.isEmpty(searchKeyWord)){
//                        showToastShort("请输入需要查询的内容");
//                        return true;
//                    }
//
//
//                    updateUI(false);
//                    //查询单词
//                    presenter.searchWord(searchKeyWord);
//                    //查询句子
//                    presenter.searchSentence(searchKeyWord);
//                    //查询文章
//                    presenter.searchVoa(searchKeyWord);
//                    return true;
//                }
//                return false;
//            }
//        });
//    }
//
//    private void initData(){
//        updateUI(true);
//
//        //单词列表
//        wordAdapter = new SearchWordAdapter(this,new ArrayList<>(),presenter);
//        binding.wordShow.setLayoutManager(new LinearLayoutManager(this));
//        binding.wordShow.setAdapter(wordAdapter);
//        binding.wordShow.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
//        binding.wordMore.setOnClickListener(v->{
//            SearchListActivity.start(this,SearchListActivity.TAG_WORD,searchKeyWord);
//        });
//
//        //句子列表
//        sentenceAdapter = new SearchSentenceAdapter(this,new ArrayList<>(),presenter);
//        binding.sentenceShow.setLayoutManager(new LinearLayoutManager(this));
//        binding.sentenceShow.setAdapter(sentenceAdapter);
//        binding.sentenceShow.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
//        binding.sentenceMore.setOnClickListener(v->{
//            SearchListActivity.start(this,SearchListActivity.TAG_SENTENCE,searchKeyWord);
//        });
//
//        //文章列表
//        voaAdapter = new SearchVoaAdapter(this,new ArrayList<>());
//        binding.voaShow.setLayoutManager(new LinearLayoutManager(this));
//        binding.voaShow.setAdapter(voaAdapter);
//        binding.voaShow.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
//        binding.voaMore.setOnClickListener(v->{
//            SearchListActivity.start(this,SearchListActivity.TAG_VOA,searchKeyWord);
//        });
//    }
//
//    @Override
//    public void showWord(List<TalkShowWords> wordsList) {
//        if (wordsList!=null&&wordsList.size()>0){
//            binding.wordLayout.setVisibility(View.VISIBLE);
//
//            //这里只显示前三个
//            List<TalkShowWords> showList = new ArrayList<>();
//            if (wordsList.size()>3){
//                binding.wordMore.setVisibility(View.VISIBLE);
//
//                showList.add(wordsList.get(0));
//                showList.add(wordsList.get(1));
//                showList.add(wordsList.get(2));
//            }else {
//                binding.wordMore.setVisibility(View.GONE);
//
//                showList.addAll(wordsList);
//            }
//
//            searchMarkMap.put(WORD,SUCCESS);
//            wordAdapter.refreshData(showList);
//            binding.wordShow.setItemViewCacheSize(showList.size());
//        }else {
//            searchMarkMap.put(WORD,FAIL);
//            binding.wordLayout.setVisibility(View.GONE);
//        }
//
//        updateUI(false);
//    }
//
//    @Override
//    public void showSentence(List<VoaText> evalList) {
//        if (evalList!=null&&evalList.size()>0){
//            binding.sentenceLayout.setVisibility(View.VISIBLE);
//
//            //这里直接下载所有的音频
////            preDownloadAudio(evalList,false);
//
//            //这里只显示前三个
//            List<VoaText> showList = new ArrayList<>();
//            if (evalList.size()>3){
//                binding.sentenceMore.setVisibility(View.VISIBLE);
//
//                showList.add(evalList.get(0));
//                showList.add(evalList.get(1));
//                showList.add(evalList.get(2));
//            }else {
//                binding.sentenceMore.setVisibility(View.GONE);
//
//                showList.addAll(evalList);
//            }
//
//            searchMarkMap.put(SENTENCE,SUCCESS);
//            sentenceAdapter.refreshData(showList);
//            binding.sentenceShow.setItemViewCacheSize(showList.size());
//        }else {
//            searchMarkMap.put(SENTENCE,FAIL);
//            binding.sentenceLayout.setVisibility(View.GONE);
//        }
//
//        updateUI(false);
//    }
//
//    @Override
//    public void showVoa(List<Voa> voaList) {
//        if (voaList!=null&&voaList.size()>0){
//            binding.voaLayout.setVisibility(View.VISIBLE);
//
//            //这里只显示前三个
//            List<Voa> showList = new ArrayList<>();
//            if (voaList.size()>3){
//                binding.voaMore.setVisibility(View.VISIBLE);
//
//                showList.add(voaList.get(0));
//                showList.add(voaList.get(1));
//                showList.add(voaList.get(2));
//            }else {
//                binding.voaMore.setVisibility(View.GONE);
//
//                showList.addAll(voaList);
//            }
//
//            searchMarkMap.put(VOA,SUCCESS);
//            voaAdapter.refreshData(showList);
//            binding.voaShow.setItemViewCacheSize(showList.size());
//        }else {
//            searchMarkMap.put(VOA,FAIL);
//            binding.voaLayout.setVisibility(View.GONE);
//        }
//
//        updateUI(false);
//    }
//
//    //显示查询进度
//    private void updateUI(boolean isInit){
//        if (isInit){
//            binding.loadingLayout.setVisibility(View.VISIBLE);
//            binding.proLoading.setVisibility(View.GONE);
//            binding.msg.setText("请输入需要查询的内容");
//            return;
//        }
//
//        if (searchMarkMap.keySet().size()==3){
//            int failShowCount = 0;
//
//            for (String key:searchMarkMap.keySet()){
//                String status = searchMarkMap.get(key);
//                if (FAIL.equals(status)){
//                    failShowCount++;
//                }
//            }
//
//            if (failShowCount==3){
//                binding.loadingLayout.setVisibility(View.VISIBLE);
//                binding.proLoading.setVisibility(View.GONE);
//                binding.msg.setText("未查询到数据，请更换关键词重试");
//            }else {
//                binding.loadingLayout.setVisibility(View.GONE);
//            }
//
//            binding.inputToolbar.setEnabled(true);
//            binding.deleteToolbar.setEnabled(true);
//        }else {
//            binding.loadingLayout.setVisibility(View.VISIBLE);
//            binding.proLoading.setVisibility(View.VISIBLE);
//            binding.msg.setText("正在查询中...");
//
//            binding.inputToolbar.setEnabled(false);
//            binding.deleteToolbar.setEnabled(false);
//        }
//    }
//
//    //预下载音频文件
//    private void preDownloadAudio(List<VoaText> list, boolean isMustUpdate) {
//        //检查音频数据
//        Map<String,String> audioMap = new HashMap<>();
//        for (int i = 0; i < list.size(); i++) {
//            VoaText bean = list.get(i);
//
//            //查询对应的voa数据
//            Voa curVoa = presenter.getVoa(bean.getVoaId());
//            if (curVoa!=null&&!TextUtils.isEmpty(curVoa.sound())) {
//                String audioUrl = Constant.getSoundWavUrl(curVoa.sound(),curVoa.voaId());
//
//                //先判断是否存在，然后进行保存
//                if (audioMap.get(audioUrl)==null){
//                    audioMap.put(audioUrl,audioUrl);
//                }
//            }
//        }
//
//        //音频存在则下载
//        if (audioMap.keySet().size()>0){
//            //下载地址
//            List<String> audioList = new ArrayList<>();
//            for (String url:audioMap.keySet()){
//                audioList.add(url);
//            }
//
//            //下载
//            presenter.downloadCount = 0;
//            presenter.recycleDownloadAudio(audioList,isMustUpdate);
//        }
//    }
//}
