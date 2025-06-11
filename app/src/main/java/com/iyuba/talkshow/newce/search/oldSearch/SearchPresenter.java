//package com.iyuba.talkshow.newce.search.oldSearch;
//
//import android.content.Context;
//import android.util.Log;
//
//import com.iyuba.talkshow.Constant;
//import com.iyuba.talkshow.TalkShowApplication;
//import com.iyuba.talkshow.constant.App;
//import com.iyuba.talkshow.data.DataManager;
//import com.iyuba.talkshow.data.model.Voa;
//import com.iyuba.talkshow.data.model.VoaSoundNew;
//import com.iyuba.talkshow.data.model.VoaText;
//import com.iyuba.talkshow.lil.user.UserInfoManager;
//import com.iyuba.talkshow.newce.study.exercise.util.ExerciseUtil;
//import com.iyuba.talkshow.ui.base.BasePresenter;
//import com.iyuba.talkshow.util.RxUtil;
//import com.iyuba.wordtest.entity.TalkShowWords;
//import com.iyuba.wordtest.manager.WordManager;
//import com.iyuba.wordtest.ui.detail.WordDetailActivity;
//
//import java.io.File;
//import java.util.ArrayList;
//import java.util.HashMap;
//import java.util.List;
//import java.util.Map;
//
//import javax.inject.Inject;
//
//import okhttp3.ResponseBody;
//import rx.Subscriber;
//import rx.Subscription;
//import rx.android.schedulers.AndroidSchedulers;
//import rx.schedulers.Schedulers;
//
//@Deprecated
//public class SearchPresenter extends BasePresenter<SearchMvpView> {
//
//    private final DataManager mDataManager;
//
//    //保存评测结果
//    private Subscription mSaveEvalResult;
//
//    @Inject
//    public SearchPresenter(DataManager mDataManager) {
//        this.mDataManager = mDataManager;
//    }
//
//    @Override
//    public void detachView() {
//        super.detachView();
//
//        RxUtil.unsubscribe(mSaveEvalResult);
//    }
//
//    //获取uid
//    public int getUid(){
//        return UserInfoManager.getInstance().getUserId();
//    }
//
//    //查询单词
//    public void searchWord(String keyWord){
//        List<TalkShowWords> wordsList = mDataManager.searchWords(keyWord);
//        List<TalkShowWords> showList = new ArrayList<>();
//
//        if (wordsList!=null&&wordsList.size()>0){
//            //去重
//            Map<String,TalkShowWords> map = new HashMap<>();
//
//            for (int i = 0; i < wordsList.size(); i++) {
//                TalkShowWords temp = wordsList.get(i);
//
//                if (map.get(temp.word)==null){
//                    map.put(temp.word,temp);
//                    showList.add(temp);
//                }
//            }
//        }
//
//        if (getMvpView()!=null){
//            getMvpView().showWord(showList);
//        }
//    }
//
//    //联网查询单词
//    public void searchWordByNet(String keyWord){
//
//    }
//
//    //跳转单词界面
//    public void startWord(Context context, TalkShowWords talkShowWords){
//        WordManager.getInstance().init(UserInfoManager.getInstance().getUserName(),
//                String.valueOf(UserInfoManager.getInstance().getUserId()),
//                App.APP_ID, Constant.EVAL_TYPE, UserInfoManager.getInstance().isVip() ? 1 : 0, App.APP_NAME_EN);
//        WordManager.getInstance().migrateData(TalkShowApplication.getContext());
//
//        List<TalkShowWords> tempList = new ArrayList<>();
//        tempList.add(talkShowWords);
//        WordDetailActivity.start(context,tempList,0,talkShowWords.book_id,talkShowWords.unit_id);
//    }
//
//    //查询例句
//    public void searchSentence(String keyWord){
//        List<VoaText> sentenceList = mDataManager.searchSentence(keyWord);
//        List<VoaText> showList = new ArrayList<>();
//
//        if (sentenceList!=null&&sentenceList.size()>0){
//            //去重
//            Map<String,VoaText> map = new HashMap<>();
//
//            for (int i = 0; i < sentenceList.size(); i++) {
//                VoaText temp = sentenceList.get(i);
//
//                if (map.get(temp.sentence())==null){
//                    map.put(temp.sentence(),temp);
//                    showList.add(temp);
//                }
//            }
//        }
//
//        if (getMvpView()!=null){
//            getMvpView().showSentence(showList);
//        }
//    }
//
//    //查询课文
//    public void searchVoa(String keyWord){
//        List<Voa> voaList = mDataManager.searchVoa(keyWord);
//        if (getMvpView()!=null){
//            getMvpView().showVoa(voaList);
//        }
//    }
//
//    //获取课程（voaId）
//    public Voa getVoa(int voaId){
//        return mDataManager.searchVoa(voaId);
//    }
//
//    //获取录音
//    public VoaSoundNew getVoaSound(int voaId, int paraId,int idIndex){
//        long itemId = Long.parseLong(voaId+""+paraId+""+idIndex);
//
//        List<VoaSoundNew> soundNewList = mDataManager.getVoaSoundItemUid(UserInfoManager.getInstance().getUserId(), itemId);
//        if (soundNewList.size()>0){
//            return soundNewList.get(0);
//        }
//        return null;
//    }
//
//    //保存评测结果
//    public void saveVoaSoundResult(VoaSoundNew voaSoundNew){
//        RxUtil.unsubscribe(mSaveEvalResult);
//        mSaveEvalResult = mDataManager.saveVoaSound(voaSoundNew)
//                .subscribeOn(Schedulers.io())
//                .observeOn(Schedulers.io())
//                .subscribe(new Subscriber<Boolean>() {
//                    @Override
//                    public void onCompleted() {
//
//                    }
//
//                    @Override
//                    public void onError(Throwable e) {
//
//                    }
//
//                    @Override
//                    public void onNext(Boolean aBoolean) {
//                        RxUtil.unsubscribe(mSaveEvalResult);
//                    }
//                });
//    }
//
//    /****文件操作****/
//    //循环下载音频文件
//    public int downloadCount = 0;
//    public void recycleDownloadAudio(List<String> audioList,boolean isMustDown){
//        if (audioList==null||audioList.size()==0){
//            return;
//        }
//
//        if (downloadCount>=audioList.size()){
//            downloadCount = 0;
//            return;
//        }
//
//        String audioUrl = audioList.get(downloadCount);
//
//        //设置总的路径
//        int index = audioUrl.lastIndexOf("/");
//        if (index==-1){
//            return;
//        }
//
//        String audioName = audioUrl.substring(index+1);
//        //创建文件夹
//        String allAudioPath = ExerciseUtil.getExerciseAudioPath(audioName,"/search/audio/");
//        if (allAudioPath==null){
//            return;
//        }
//
//        //创建
//        try {
//            File audioFile = new File(allAudioPath);
//            if (audioFile.exists()&&!isMustDown){
//                downloadCount++;
//                recycleDownloadAudio(audioList,isMustDown);
//                return;
//            }
//
//            if (!audioFile.getParentFile().exists()){
//                audioFile.getParentFile().mkdirs();
//            }
//            audioFile.createNewFile();
//
//            //下载
//            mDataManager.downloadFile(audioUrl)
//                    .subscribeOn(Schedulers.io())
//                    .observeOn(AndroidSchedulers.mainThread())
//                    .subscribe(new Subscriber<ResponseBody>() {
//                        @Override
//                        public void onCompleted() {
//
//                        }
//
//                        @Override
//                        public void onError(Throwable e) {
//                            downloadCount++;
//                            recycleDownloadAudio(audioList,isMustDown);
//
//                            Log.d("下载数据", "失败");
//                        }
//
//                        @Override
//                        public void onNext(ResponseBody responseBody) {
//                            boolean saveFile = ExerciseUtil.saveFile(responseBody.byteStream(),allAudioPath);
//                            downloadCount++;
//                            recycleDownloadAudio(audioList,isMustDown);
//
//                            Log.d("下载数据", "onNext: --"+allAudioPath);
//                        }
//                    });
//        }catch (Exception e){
//            e.printStackTrace();
//            downloadCount++;
//            recycleDownloadAudio(audioList,isMustDown);
//        }
//    }
//}
