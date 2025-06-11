//package com.iyuba.talkshow.newce.search.newSearch.list;
//
//import android.content.Context;
//import android.content.Intent;
//import android.os.Bundle;
//import android.view.View;
//
//import androidx.annotation.Nullable;
//import androidx.recyclerview.widget.DividerItemDecoration;
//import androidx.recyclerview.widget.LinearLayoutManager;
//
//import com.iyuba.play.IJKPlayer;
//import com.iyuba.talkshow.data.model.Voa;
//import com.iyuba.talkshow.data.model.VoaText;
//import com.iyuba.talkshow.databinding.ActivitySearchListBinding;
//import com.iyuba.talkshow.newce.search.oldSearch.SearchSentenceAdapter;
//import com.iyuba.talkshow.newce.search.adapter.SearchVoaAdapter;
//import com.iyuba.talkshow.newce.search.adapter.SearchWordAdapter;
//import com.iyuba.talkshow.newce.search.oldSearch.SearchMvpView;
//import com.iyuba.talkshow.newce.search.oldSearch.SearchPresenter;
//import com.iyuba.talkshow.ui.base.BaseViewBindingActivity;
//import com.iyuba.wordtest.entity.TalkShowWords;
//
//import java.util.List;
//
//import javax.inject.Inject;
//
///**
// * 查询列表界面
// */
//public class SearchListActivity extends BaseViewBindingActivity<ActivitySearchListBinding> implements SearchMvpView {
//
//    private static final String SEARCH_TYPE = "search_type";
//    private static final String KEY_WORD = "key_word";
//
//    //搜索类型
//    public static final int TAG_WORD = 1;
//    public static final int TAG_SENTENCE = 2;
//    public static final int TAG_VOA = 3;
//
//    @Inject
//    public SearchPresenter presenter;
//
//    //单词适配器
//    private SearchWordAdapter wordAdapter;
//    //句子适配器
//    private SearchSentenceAdapter sentenceAdapter;
//    //文章适配器
//    private SearchVoaAdapter voaAdapter;
//
//    //音频播放器
//    private IJKPlayer ijkPlayer;
//
//    //跳转
//    public static void start(Context context,int searchType,String keyWord){
//        Intent intent = new Intent();
//        intent.setClass(context,SearchListActivity.class);
//        intent.putExtra(SEARCH_TYPE,searchType);
//        intent.putExtra(KEY_WORD,keyWord);
//        context.startActivity(intent);
//    }
//
//    @Override
//    protected void onCreate(@Nullable Bundle savedInstanceState) {
//        super.onCreate(savedInstanceState);
//
//        activityComponent().inject(this);
//        presenter.attachView(this);
//    }
//
//    @Override
//    protected void onPostCreate(@Nullable Bundle savedInstanceState) {
//        super.onPostCreate(savedInstanceState);
//
//        initToolbar();
//        showData();
//    }
//
//    private void initToolbar(){
//        binding.toolbar.imgTopRight.setVisibility(View.INVISIBLE);
//        binding.toolbar.imgTopLeft.setOnClickListener(v->{
//            finish();
//
//            if (wordAdapter!=null){
//                wordAdapter.stopAudio();
//            }
//
//            if (sentenceAdapter!=null){
//                sentenceAdapter.stopAudio();
//            }
//        });
//    }
//
//    private void showData(){
//        String keyWord = getIntent().getStringExtra(KEY_WORD);
//        int type = getIntent().getIntExtra(SEARCH_TYPE,0);
//
//        switch (type){
//            case TAG_WORD:
//                //单词
//                binding.toolbar.tvTopCenter.setText(keyWord+" 相关单词");
//
//                presenter.searchWord(keyWord);
//                break;
//            case TAG_SENTENCE:
//                //句子
//                binding.toolbar.tvTopCenter.setText(keyWord+" 相关句子");
//
//                presenter.searchSentence(keyWord);
//                break;
//            case TAG_VOA:
//                //文章
//                binding.toolbar.tvTopCenter.setText(keyWord+" 相关课文");
//
//                presenter.searchVoa(keyWord);
//                break;
//        }
//    }
//
//    @Override
//    public void showWord(List<TalkShowWords> wordsList) {
//        if (wordsList!=null&&wordsList.size()>0){
//            LinearLayoutManager manager = new LinearLayoutManager(this);
//            wordAdapter = new SearchWordAdapter(this,wordsList,presenter);
//            binding.recyclerView.setLayoutManager(manager);
//            binding.recyclerView.setAdapter(wordAdapter);
//            binding.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
//            binding.recyclerView.setItemViewCacheSize(wordsList.size());
//        }
//    }
//
//    @Override
//    public void showSentence(List<VoaText> evalList) {
//        if (evalList!=null&&evalList.size()>0){
//            LinearLayoutManager manager = new LinearLayoutManager(this);
//            sentenceAdapter = new SearchSentenceAdapter(this,evalList,presenter);
//            binding.recyclerView.setLayoutManager(manager);
//            binding.recyclerView.setAdapter(sentenceAdapter);
//            binding.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
//            binding.recyclerView.setItemViewCacheSize(evalList.size());
//        }
//    }
//
//    @Override
//    public void showVoa(List<Voa> voaList) {
//        if (voaList!=null&&voaList.size()>0){
//            LinearLayoutManager manager = new LinearLayoutManager(this);
//            voaAdapter = new SearchVoaAdapter(this,voaList);
//            binding.recyclerView.setLayoutManager(manager);
//            binding.recyclerView.setAdapter(voaAdapter);
//            binding.recyclerView.addItemDecoration(new DividerItemDecoration(this,DividerItemDecoration.VERTICAL));
//            binding.recyclerView.setItemViewCacheSize(voaList.size());
//        }
//    }
//}
