package com.iyuba.talkshow.newce;

import android.Manifest;
import android.content.Context;
import android.text.TextUtils;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.iyuba.dlex.bizs.DLManager;
import com.iyuba.dlex.bizs.DLTaskInfo;
import com.iyuba.dlex.interfaces.SimpleDListener;
import com.iyuba.imooclib.data.local.IMoocDBManager;
import com.iyuba.imooclib.data.model.StudyProgress;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.model.AdNativeResponse;
import com.iyuba.talkshow.data.model.ArticleRecord;
import com.iyuba.talkshow.data.model.CategoryFooter;
import com.iyuba.talkshow.data.model.Header;
import com.iyuba.talkshow.data.model.LoopItem;
import com.iyuba.talkshow.data.model.RecyclerItem;
import com.iyuba.talkshow.data.model.TitleSeries;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.databinding.FragmentMainDetailBinding;
import com.iyuba.talkshow.event.WordStepEvent;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlayEvent;
import com.iyuba.talkshow.newview.MyRing;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.StorageUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.TalkShowTests;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.utils.PermissionDialogUtil;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.concurrent.ConcurrentHashMap;

import javax.inject.Inject;


/**
 * Created by carl shen on 2020/7/30
 * New Primary English, new study experience.
 */
public class MainAdapter extends RecyclerView.Adapter<RecyclerView.ViewHolder> {

    private static final int BANNER_POSITION = 0;
    private final List<RecyclerItem> mItemList;
    private List<Voa> mVoaList;
    private List<LoopItem> mLoopItemList;
    private final List<String> mImageUrls;
    private final List<String> mNames;

    private VoaCallback mVoaCallback;
    private LoopCallback mLoopCallback;
    private DataChangeCallback mDataChangeCallback;
    private final WordDataBase db;
    @Inject
    MainFragPresenter mPresenter;
    private DLManager mDLManager;
    Context context;
    SimpleDateFormat SDF = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss", Locale.CHINA);

    @Inject
    public MainAdapter() {
        this.mItemList = new ArrayList<>();
        this.mImageUrls = new ArrayList<>();
        this.mNames = new ArrayList<>();
        db = WordDataBase.getInstance(TalkShowApplication.getInstance());
    }

    @Override
    public RecyclerView.ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        context = parent.getContext();
        FragmentMainDetailBinding bindVoa = FragmentMainDetailBinding.inflate(LayoutInflater.from(parent.getContext()), parent, false);
        return new ViewHolder(bindVoa);
    }

    @Override
    public void onBindViewHolder(RecyclerView.ViewHolder holder, int position) {
        if (holder==null){
            return;
        }

        Voa voa = (Voa) mItemList.get(position);
        ((ViewHolder) holder).setItem(voa, position);
        ((ViewHolder) holder).setClick(position);
    }

    @Override
    public int getItemCount() {
        return mItemList.size();
    }

    public void setVoasByCategory(final List<Voa> voas, final CategoryFooter category) {
        int pos = category.getPos() - 4;
        mItemList.remove(pos);
        mItemList.remove(pos);
        mItemList.remove(pos);
        mItemList.remove(pos);
        mItemList.add(pos, voas.get(0));
        mItemList.add(pos, voas.get(1));
        mItemList.add(pos, voas.get(2));
        mItemList.add(pos, voas.get(3));
        notifyItemRangeChanged(pos, 4);
    }

    public void setAd(AdNativeResponse nativeResponse) {
        int pos = Header.startIndex;
        Iterator<RecyclerItem> iterator = mItemList.iterator();
        while (iterator.hasNext()) {
            if (iterator.next() instanceof AdNativeResponse) {
                iterator.remove();
            }
        }
        mItemList.add(pos, nativeResponse);
        notifyDataSetChanged();
    }

    public void setVoas(List<Voa> mVoaList) {
        this.mVoaList = mVoaList;
        try {
            setRecyclerItemList();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void setRecyclerItemList() {
        mItemList.clear();
        mItemList.addAll(mVoaList);
    }

    public void setBanner(List<LoopItem> loopItemList) {
        this.mLoopItemList = loopItemList;
        if (loopItemList != null && loopItemList.size() > 0) {
            mImageUrls.clear();
            mNames.clear();
            for (LoopItem item : mLoopItemList) {
                mImageUrls.add(item.pic());
                mNames.add(item.name());
            }
            notifyItemChanged(BANNER_POSITION);
        }
    }

    public class ViewHolder extends RecyclerView.ViewHolder {
        private Voa voa;
        private final TextView title;
        private final TextView desc;
        private final ImageView image;
        private final ImageView lock;
        private final MyRing ringEval;
        private final MyRing ringWord;
        private final MyRing ringHear;
        private final TextView tvEval;
        private final TextView tvWord;
        private final TextView tvHear;
        private final MyRing ringDown;
        private final TextView tvDown;
        private final LinearLayout ringLinear;
        private final LinearLayout moocLinear;
        private final MyRing ringMooc;
        private final TextView tvMooc;
        @Nullable
        private DLTaskInfo task = null; // the voa and task has 1 on 1 relationship.

        public ViewHolder(@NonNull FragmentMainDetailBinding binding) {
            super(binding.getRoot());
            title = binding.title;
            ringEval = binding.ringEval;
            ringWord = binding.ringWord;
            ringHear = binding.ringHear;
            tvEval = binding.tvEval;
            tvWord = binding.tvWord;
            tvHear = binding.tvHear;
            ringDown = binding.ringDownload;
            tvDown = binding.tvDownload;
            desc = binding.desc;
            image = binding.image;
            lock = binding.lock;
            ringLinear = binding.ringLinear;
            moocLinear = binding.linearMooc;
            ringMooc = binding.ringMooc;
            tvMooc = binding.tvMooc;
        }

        //正常数据类型
        public void setItem(Voa dataBean, int positionInList) {
            this.voa = dataBean;
            ringLinear.setVisibility(View.VISIBLE);
            if (dataBean.titleCn().contains(curTitle)) {
                title.setText(dataBean.titleCn().replace(curTitle, "").trim());
            } else {
                title.setText(dataBean.titleCn());
            }
            if ((450 <= bookId) && (bookId <= 457)) {
                int index = dataBean.voaId() % 1000;
                title.setText("Lesson " + index + "  " + dataBean.titleCn());
            }

            //评测显示
            initEvalData(voa, ringEval, tvEval);
            //原文显示
            initArticleData(voa, ringHear, tvHear);
            //单词显示
            initWordData(voa, ringWord, tvWord);

            //图片和下载
            desc.setText(dataBean.title());
            Glide.with(image.getContext()).load(dataBean.pic()).placeholder(R.drawable.beishi_grade7).into(image);
            File audioFile = StorageUtil.getAudioFile(TalkShowApplication.getContext(), voa.voaId());
            if (audioFile.exists()) {
                ringDown.setCurrProgress(360, R.drawable.ic_cloud_success);
                ringDown.setClickable(false);
                tvDown.setText("100%");
            } else {
                ringDown.setCurrProgress(0, R.drawable.ic_download);
                ringDown.setClickable(true);
                tvDown.setText("0%");
            }
            ringDown.setVisibility(View.VISIBLE);

            //文章进度
            ringHear.setOnClickListener(v->{
                if ((mVoaCallback != null) && (voa != null)) {
                    mVoaCallback.onVoaClick(voa, positionInList, positionInList);
                }
            });
            //评测进度
            ringEval.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    if ((mVoaCallback != null) && (voa != null)) {
                        mVoaCallback.onEvalClick(voa, positionInList, positionInList);
                    }
                }
            });
            //单词进度
            ringWord.setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    //暂停播放音频
                    EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_audio_pause));
                    EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_pause));

                    EventBus.getDefault().post(new WordStepEvent());
                }
            });

            //微课展示
            if ((microId > 0 || 313 <= mPresenter.getCourseCategory()) && (mPresenter.getCourseCategory() <= 316) && !TextUtils.isEmpty(voa.classId()) && !"0".equals(voa.classId())) {
                moocLinear.setVisibility(View.VISIBLE);
                int percent = 0;
                if (TextUtils.isEmpty(voa.classId()) || "0".equals(voa.classId())) {
                    Log.e("MainFragment", " voa.classId() should not be null? ");
                } else if (UserInfoManager.getInstance().getUserId() < 1) {
                    Log.e("MainFragment", " voa.classId() should be 0 for uid = 0.");
                } else {
                    String[] classIds = voa.classId().split(",");
                    if ((classIds == null) || (classIds.length < 1)) {
                        Log.e("MainFragment", " classIds should not be null? ");
                    } else if (classIds.length == 1) {
                        try {
                            StudyProgress studyProgress = IMoocDBManager.getInstance().findStudyProgress(UserInfoManager.getInstance().getUserId(), Integer.parseInt(classIds[0]));
                            if (studyProgress != null) {
                                long section = SDF.parse(studyProgress.endTime).getTime() - SDF.parse(studyProgress.startTime).getTime();
                                if (voa.totalTime() > 0) {
                                    percent = (int) (section / 10 / voa.totalTime());
                                } else {
                                    Log.e("MainFragment", "findStudyProgress voa totalTime is 0? ");
                                }
                            } else {
                                Log.e("MainFragment", " findStudyProgress is null? ");
                            }
                        } catch (Exception var2) {
                            Log.e("MainFragment", " findStudyProgress Exception " + var2.getMessage());
                        }
                    } else {
                        try {
                            int small = Integer.parseInt(classIds[0]);
                            int large = Integer.parseInt(classIds[0]);
                            for (int i = 1; i < classIds.length; i++) {
                                if (small > Integer.parseInt(classIds[i])) {
                                    small = Integer.parseInt(classIds[i]);
                                }
                                if (large < Integer.parseInt(classIds[i])) {
                                    large = Integer.parseInt(classIds[i]);
                                }
                            }
                            List<StudyProgress> studyProgresses = IMoocDBManager.getInstance().findStudyProgressByRange(UserInfoManager.getInstance().getUserId(), String.valueOf(voa.voaId()), small, large);
                            if ((studyProgresses != null) && (studyProgresses.size() > 0)) {
                                long totalSum = 0;
                                for (StudyProgress sp : studyProgresses) {
                                    long section = SDF.parse(sp.endTime).getTime() - SDF.parse(sp.startTime).getTime();
                                    totalSum += (section / 1000);
                                }
                                if (voa.totalTime() > 0) {
                                    percent = (int) (totalSum * 100 / voa.totalTime());
                                } else {
                                    Log.e("MainFragment", " findStudyProgressByRange totalTime is 0? ");
                                }
                            } else {
                                Log.e("MainFragment", " findStudyProgressByRange is null? ");
                            }
                        } catch (Exception var2) {
                            Log.e("MainFragment", " findStudyProgressByRange Exception " + var2.getMessage());
                        }
                    }
                }
                if (percent > 100) {
                    percent = 100;
                }
                Log.e("MainFragment", " mPresenter.percent " + percent);
                if (percent > 0) {
                    ringMooc.setCurrProgress(360 * percent / 100, R.drawable.ic_imooc_1);
                    tvMooc.setText(percent + "%");
                } else {
                    ringMooc.setCurrProgress(0, R.drawable.ic_imooc);
                    tvMooc.setText("0%");
                }
                moocLinear.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        if ((mVoaCallback != null) && (voa != null)) {
                            mVoaCallback.onMoocClick(voa);
                        }
                    }
                });
            } else {
                moocLinear.setVisibility(View.GONE);
            }
        }

        public void setClick(int positionInList) {
            itemView.setOnClickListener(v->{
                if (mVoaCallback!=null){
                    mVoaCallback.onItemClick((Voa) mItemList.get(positionInList),positionInList,positionInList);
                }
            });
            ringDown.setOnClickListener(v -> {
                //增加权限弹窗
                showDownloadDialog(positionInList);
            });
        }

        /**
         * 下载弹窗显示
         * 弹窗之后需要调用 onDownClicked 功能显示
         */
        private void showDownloadDialog(int positionInList) {
            List<Pair<String, Pair<String, String>>> pairList = new ArrayList<>();
            pairList.add(new Pair<>(Manifest.permission.WRITE_EXTERNAL_STORAGE, new Pair<>("存储权限", "存放下载后的文件，用于辅助原音音频播放")));

            PermissionDialogUtil.getInstance().showMsgDialog(context, pairList, new PermissionDialogUtil.OnPermissionResultListener() {
                @Override
                public void onGranted(boolean isSuccess) {
                    if (isSuccess) {
                        onDownClicked(positionInList);
                    }
                }
            });
        }

        public void onDownClicked(int positionInList) {
            if (!NetStateUtil.isConnected(TalkShowApplication.getContext())) {
                ToastUtil.showToast(context, "下载需要开启数据网络！");
                return;
            }
            File audioFile = StorageUtil.getAudioFile(TalkShowApplication.getContext(), voa.voaId());
            if (audioFile.exists()) {
                ToastUtil.showToast(context, "已下载");
                return;
            }

            if (UserInfoManager.getInstance().getUserId() < 1) {
                Voa curVoa = (Voa) mItemList.get(positionInList);
                if (mVoaCallback!=null){
                    mVoaCallback.onDownloadClick(curVoa);
                }

                return;
            }

            if (!UserInfoManager.getInstance().isVip() && (positionInList > 2)) {
                ToastUtil.showToast(context, "非VIP用户只能下载前3篇文章资源。");
                return;
            }
            Log.e("MainFragment", "voa download audioFile " + audioFile.getAbsolutePath());
            if (mDLManager == null) {
                mDLManager = DLManager.getInstance();
            }
            if (task == null) {
                task = new DLTaskInfo();
                task.tag = audioFile.getAbsolutePath();
                task.filePath = StorageUtil.getMediaDir(TalkShowApplication.getContext(), voa.voaId()).getAbsolutePath();
                task.fileName = StorageUtil.getAudioFilename(voa.voaId());
                String audioUrl = Constant.getSoundMp3Url(voa.sound(), voa.voaId());
                Log.e("MainFragment", "voa download audioUrl " + audioUrl);
                task.initalizeUrl(audioUrl);
                task.setDListener(new DListener(voa));
                mDLManager.addDownloadTask(task);
            } else {
                switch (task.state) {
                    case DLTaskInfo.TaskState.INIT:
                        ToastUtil.showToast(context, "正在初始化");
                        break;
                    case DLTaskInfo.TaskState.WAITING:
                        ToastUtil.showToast(context, "下载任务正在等待");
                        break;
                    case DLTaskInfo.TaskState.PREPARING:
                    case DLTaskInfo.TaskState.DOWNLOADING:
                        mDLManager.stopTask(task);
                        break;
                    case DLTaskInfo.TaskState.ERROR:
                    case DLTaskInfo.TaskState.PAUSING:
                        task.setDListener(new DListener(task.totalBytes, voa));
                        mDLManager.resumeTask(task);
                        break;
                    default:
                        break;
                }
            }
        }

        private class DListener extends SimpleDListener {
            private int total;
            private final Voa mHeadVoa;

            DListener(Voa headlines) {
                mHeadVoa = headlines;
            }

            DListener(int total, Voa headlines) {
                this.total = total;
                mHeadVoa = headlines;
            }

            @Override
            public void onStart(String fileName, String realUrl, int fileLength) {
                total = fileLength;
                ringDown.setCurrProgress(0, R.drawable.ic_cloud_success);
            }

            @Override
            public void onProgress(int progress) {
                int percentage = getCurrentPercentage(progress);
                ringDown.setCurrProgress(percentage * 360 / 100, R.drawable.ic_cloud_success);
                tvDown.setText(percentage + "%");
            }

            @Override
            public void onStop(int progress) {
                int percentage = getCurrentPercentage(progress);
                ringDown.setCurrProgress(percentage * 360 / 100, R.drawable.ic_cloud_success);
                tvDown.setText(percentage + "%");
            }

            @Override
            public void onFinish(File file) {
                ringDown.setCurrProgress(360, R.drawable.ic_cloud_success);
                tvDown.setText("100%");
                task = null;
            }

            @Override
            public void onError(int status, String error) {
                Log.e("MainFragment", "DListener onError status " + status);
                Log.e("MainFragment", "DListener onError error " + error);
            }

            private int getCurrentPercentage(int progress) {
                int result = 0;
                if (total >= 10000) {
                    result = progress / (total / 100);
                } else if (total > 0) {
                    result = (progress * 100) / total;
                }
                return result;
            }
        }
    }

    public void setTitleSeries(List<TitleSeries> mList) {
        if ((mList == null) || mList.size() < 1) {
            return;
        }
        for (TitleSeries title : mList) {
            if ((title != null) && (title.Id > 0)) {
                mVoaTextNum.put(title.Id, title);
            }
        }
    }

    private final ConcurrentHashMap<Integer, TitleSeries> mVoaTextNum = new ConcurrentHashMap<>();
    private List<VoaSoundNew> mSoundList = new ArrayList<>();

    private void initEvalData(Voa curVoa, MyRing ringEval, TextView tvEval) {
        if ((curVoa == null) || (mVoaTextNum == null)) {
            ringEval.setCurrProgress(0, R.drawable.ic_home_eval_0);
            tvEval.setText("0/0");
            return;
        }
        int vTextNum = 0;
        if (mVoaTextNum.get(curVoa.voaId()) != null) {
            vTextNum = mVoaTextNum.get(curVoa.voaId()).Texts;
        }
        mSoundList = mPresenter.getVoaSoundVoaId(curVoa.voaId());
        if ((mSoundList == null) || mSoundList.size() == 0) {
            ringEval.setCurrProgress(0, R.drawable.ic_home_eval_0);
            tvEval.setText("0/" + vTextNum);
        } else {
            tvEval.setText(mSoundList.size() + "/" + vTextNum);
            if (vTextNum > 0) {
                ringEval.setCurrProgress(360 * mSoundList.size() / vTextNum, R.drawable.ic_home_eval_1);
            } else {
                ringEval.setCurrProgress(0, R.drawable.ic_home_eval_0);
            }
        }
    }

    private List<ArticleRecord> mArticleList = new ArrayList<>();

    private void initArticleData(Voa curVoa, MyRing ringArticle, TextView tvArticle) {
        if (curVoa == null) {
            ringArticle.setCurrProgress(0, R.drawable.ic_home_hear_0);
            tvArticle.setText("0%");
            return;
        }
        mArticleList = mPresenter.getArticleByVoaId(curVoa.voaId());
        if (mArticleList == null || mArticleList.size() < 1) {
            tvArticle.setText("0%");
            ringArticle.setCurrProgress(0, R.drawable.ic_home_hear_0);
        } else {
            ArticleRecord bean = mArticleList.get(0);
            if (bean.is_finish() == 1) {
                tvArticle.setText("100%");
                ringArticle.setCurrProgress(360, R.drawable.ic_home_hear_1);
            } else {
                if (bean.total_time() == 0) {
                    int progress = 360 * bean.percent() / 100;
                    tvArticle.setText(String.format("%d%%", bean.percent()));
                    ringArticle.setCurrProgress(progress, R.drawable.ic_home_hear_1);
                } else {
                    int percent = 100 * bean.curr_time() / bean.total_time();
                    percent = percent > bean.percent() ? percent : bean.percent();
                    int progress = 360 * percent / 100;
                    tvArticle.setText(String.format("%d%%", percent));
                    ringArticle.setCurrProgress(progress, R.drawable.ic_home_hear_1);
                }
            }
        }
    }

    public void setBookTitle(String id) {
        curTitle = id;
    }

    public void setBookId(int id) {
        bookId = id;
    }

    public void setMicroId(int id) {
        microId = id;
    }

    private String curTitle = App.getBookDefaultShowData().getBookName();
    int bookId = App.getBookDefaultShowData().getBookId();
    int unitId = 1;
    int microId = 0;
    private List<TalkShowWords> mWordList = new ArrayList<>();
    private List<TalkShowTests> mTestList = new ArrayList<>();

    private void initWordData(Voa curVoa, MyRing ringWord, TextView tvWord) {
        if ((curVoa == null) || (TextUtils.isEmpty(curVoa.titleCn()))) {
            ringWord.setCurrProgress(0, R.drawable.ic_home_word_0);
            tvWord.setText("0/0");
            curVoa.UnitId = -1;
            return;
        }
        unitId = mPresenter.getUnitId4Voa(curVoa);
        if (unitId >= 0) {
            curVoa.UnitId = unitId;
            mWordList = db.getTalkShowWordsDao().getUnitWords(bookId, unitId);
            if ((mWordList != null) && (mWordList.size() > 0)) {
                int sum = mWordList.size();
                int right = 0;
                if (WordManager.WordDataVersion == 2) {
                    mTestList = db.getTalkShowTestsDao().getUnitWords(bookId, unitId, String.valueOf(UserInfoManager.getInstance().getUserId()));
                    if ((mTestList != null) && mTestList.size() > 0) {
                        for (TalkShowTests word : mTestList) {
                            if ("1".equals(word.answer) && (word.wrong == 1)) {
                                right++;
                            }
                        }
                    }
                } else {
                    for (TalkShowWords word : mWordList) {
                        if ("1".equals(word.answer) && (word.wrong == 1)) {
                            right++;
                        }
                    }
                }
                if (right > sum) {
                    right = sum;
                }
                if (0 < right) {
                    ringWord.setCurrProgress(360 * right / sum, R.drawable.ic_home_word_1);
                } else {
                    ringWord.setCurrProgress(0, R.drawable.ic_home_word_0);
                }
                tvWord.setText(right + "/" + sum);
                return;
            }
        }

        //如果上边没有查出单元数据来，则查询当前课程的数据
        if (mWordList.size()<=0){
            mWordList = db.getTalkShowWordsDao().getUnitByVoa(bookId, curVoa.voaId());
        }

        int sum = mWordList.size();
        int right = 0;
        if (sum > 0) {
            if (WordManager.WordDataVersion == 2) {
                mTestList = db.getTalkShowTestsDao().getUnitByVoa(bookId, curVoa.voaId(), String.valueOf(UserInfoManager.getInstance().getUserId()));
                if ((mTestList != null) && mTestList.size() > 0) {
                    right = mTestList.size();
                }
            } else
                for (TalkShowWords word : mWordList) {
                    if ("1".equals(word.answer) && (word.wrong == 1)) {
                        right++;
                    }
                }
            if (right > sum) {
                right = sum;
            }
            if (0 < right) {
                ringWord.setCurrProgress(360 * right / sum, R.drawable.ic_home_word_1);
            } else {
                ringWord.setCurrProgress(0, R.drawable.ic_home_word_0);
            }
            tvWord.setText(right + "/" + sum);
            curVoa.UnitId = 0;
        } else {
            ringWord.setCurrProgress(0, R.drawable.ic_home_word_0);
            tvWord.setText("0/0");
            curVoa.UnitId = -1;
        }
    }

    public interface VoaCallback {
        //item跳转
        void onItemClick(Voa voa, int pos, int positionInList);

        //原文跳转-根据当前的位置处理是否需要会员或者登录显示
        void onVoaClick(Voa voa, int pos, int positionInList);

        //下载点击
        void onDownloadClick(Voa voa);

        ////评测跳转-根据当前的位置处理是否需要会员或者登录显示
        void onEvalClick(Voa voa, int pos, int positionInList);

        //微课跳转
        void onMoocClick(Voa voa);
    }

    public interface LoopCallback {
        void onLoopClick(int voaId);
    }

    public interface DataChangeCallback {
        void onClick(View v, CategoryFooter category, int limit, String ids);
    }

    public void setVoaCallback(VoaCallback voaCallback) {
        this.mVoaCallback = voaCallback;
    }

    public void setLoopCallback(LoopCallback loopCallback) {
        this.mLoopCallback = loopCallback;
    }

    public void setDataChangeCallback(DataChangeCallback mDataChangeCallback) {
        this.mDataChangeCallback = mDataChangeCallback;
    }
}