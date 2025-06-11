package com.iyuba.talkshow.newview;

import android.content.Context;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.method.LinkMovementMethod;
import android.text.style.ForegroundColorSpan;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.data.model.VoaSoundNew;
import com.iyuba.talkshow.data.model.VoaText;
import com.iyuba.talkshow.databinding.FragmentReadStudyBinding;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
import com.iyuba.talkshow.lil.help_mvp.view.NoScrollLinearLayoutManager;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newce.study.StudyActivity;
import com.iyuba.talkshow.newce.study.read.newRead.ui.NewReadListenEvent;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.util.BaseStorageUtil;
import com.iyuba.talkshow.util.DensityUtil;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.db.WordOp;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.entity.WordEntity;

import org.greenrobot.eventbus.EventBus;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Objects;

import javax.inject.Inject;

import cn.sharesdk.framework.Platform;
import cn.sharesdk.framework.PlatformActionListener;
import cn.sharesdk.framework.ShareSDK;
import cn.sharesdk.onekeyshare.OnekeyShare;
import cn.sharesdk.wechat.friends.Wechat;
import cn.sharesdk.wechat.moments.WechatMoments;

/**
 * Created by carl shen on 2021/8/28
 * New Primary English, new study experience.
 */
public class StudyReportPage extends BaseFragment {
    public static final String TAG = "StudyReportPage";
    @Inject
    ConfigManager configManager;
    @Inject
    public DataManager mManager;
    private FragmentReadStudyBinding binding;
    private List<WordEntity>  wordEntities;
    private List<TalkShowWords> mWordList;
    private WordOp wordOp;

    private String signPath;
    private StudyReadAdapter wordUnitAdapter;
    private StudyReadAdapter wordLikeAdapter;
    private List<VoaText> mEvalGoodList;
    private List<VoaText> mEvalBadList;
    private StudyEvalAdapter evalGoodAdapter;
    private StudyEvalAdapter evalBadAdapter;

    private Voa mVoa;
    private int position = 0;
    private int unitId = 0;
    private String reward = "";
    private String pageType = "";

    //计算高度
    private int tagHeight = 0;

    public static StudyReportPage newInstance(Voa voa, int pos, int unit) {
        StudyReportPage readFragment = new StudyReportPage();
        Bundle args = new Bundle();
        args.putParcelable(StudyActivity.VOA, voa);
        args.putInt(StudyActivity.POS, pos);
        args.putInt(StudyActivity.UNIT, unit);
        readFragment.setArguments(args);
        return readFragment;
    }

    public static StudyReportPage newInstance(Voa voa, int pos, int unit,String reward,String pageType) {
        StudyReportPage readFragment = new StudyReportPage();
        Bundle args = new Bundle();
        args.putParcelable(StudyActivity.VOA, voa);
        args.putInt(StudyActivity.POS, pos);
        args.putInt(StudyActivity.UNIT, unit);
        args.putString(StudyActivity.REWARD,reward);
        args.putString(StrLibrary.pageTag,pageType);
        readFragment.setArguments(args);
        return readFragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
        mVoa = getArguments().getParcelable(StudyActivity.VOA);
        position = getArguments().getInt(StudyActivity.POS, 0);
        unitId = getArguments().getInt(StudyActivity.UNIT, 0);
        reward = getArguments().getString(StudyActivity.REWARD,"");
        pageType = getArguments().getString(StrLibrary.pageTag, TypeLibrary.RefreshDataType.study_other);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentReadStudyBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        binding.imageClose.setOnClickListener((v) -> {
//            EventBus.getDefault().post(new StudyReportEvent(0,pageType));
            EventBus.getDefault().post(new NewReadListenEvent(NewReadListenEvent.type_closeReport));
        });
        binding.studyShare.setOnClickListener((v) -> {
            binding.imageClose.setVisibility(View.INVISIBLE);
            binding.studyShare.setVisibility(View.INVISIBLE);
            Log.e(TAG, "writeBitmapToFile == " + System.currentTimeMillis());
            writeBitmapToFile();
            Log.e(TAG, "after writeBitmapToFile == " + System.currentTimeMillis());
            showShareOnMoment(mContext, UserInfoManager.getInstance().getUserId(), App.APP_ID);
            TalkShowApplication.getSubHandler().post(() -> {
//                EventBus.getDefault().post(new StudyReportEvent(0,pageType));

                EventBus.getDefault().post(new NewReadListenEvent(NewReadListenEvent.type_closeReport));
            });
        });
        binding.userName.setText(UserInfoManager.getInstance().getUserName());
        if (UserInfoManager.getInstance().isLogin()) {
            /*Glide.with(mContext)
                    .load(Constant.Url.getMiddleUserImageUrl(UserInfoManager.getInstance().getUserId(), configManager.getPhotoTimestamp()))
                    .asBitmap()
                    .signature(new StringSignature(System.currentTimeMillis()+""))
                    .transform(new CircleTransform(mContext))
                    .placeholder(R.drawable.default_avatar)
                    .into(binding.userImage);*/
            Glide3Util.loadCircleImg(mContext,Constant.Url.getMiddleUserImageUrl(UserInfoManager.getInstance().getUserId(), configManager.getPhotoTimestamp()),R.drawable.default_avatar,binding.userImage);
        }
        if (position == 1) {
            binding.studyReadScroll.setVisibility(View.VISIBLE);
            binding.studyEval.setVisibility(View.GONE);
            binding.studyTitle.setText("恭喜您完成了本课听力学习！");
            if (unitId > 0) {
                mWordList = WordDataBase.getInstance(TalkShowApplication.getContext()).getTalkShowWordsDao().getUnitWords(configManager.getCourseId(), unitId);
            } else {
                mWordList = WordDataBase.getInstance(TalkShowApplication.getContext()).getTalkShowWordsDao().getUnitByVoa(configManager.getCourseId(), mVoa.voaId());
            }
            StringBuilder summery = new StringBuilder(32);
            summery.append("本次共学习单词");
            String span1 = "0";
            if ((mWordList != null) && mWordList.size() > 0) {
                span1 = mWordList.size() + "";
                binding.studyUnitList.setVisibility(View.VISIBLE);
                binding.studyUnitBack.setVisibility(View.GONE);
            } else {
                binding.studyUnitList.setVisibility(View.GONE);
                binding.studyUnitBack.setVisibility(View.VISIBLE);
            }
            summery.append(span1);
            summery.append("个，生词");
            String span2 = "0";
            wordOp = new WordOp(TalkShowApplication.getContext());
            wordEntities = wordOp.findWordByUser(UserInfoManager.getInstance().getUserId(), mVoa.voaId(), unitId);
            if ((wordEntities != null) && wordEntities.size() > 0) {
                span2 = wordEntities.size() + "";
                binding.studyWordList.setVisibility(View.VISIBLE);
                binding.studyWordText.setVisibility(View.GONE);
            } else {
                binding.studyWordList.setVisibility(View.GONE);
                binding.studyWordText.setVisibility(View.VISIBLE);
            }
            summery.append(span2);
            summery.append("个");
            ForegroundColorSpan spanColor1 = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
            ForegroundColorSpan spanColor = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
            SpannableStringBuilder summerySpanner = new SpannableStringBuilder(summery);
            summerySpanner.setSpan(spanColor1, summery.indexOf("单词") + 2, summery.indexOf("单词") + 2 + span1.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            summerySpanner.setSpan(spanColor, summery.indexOf("生词") + 2, summery.indexOf("生词") + 2 + span2.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            binding.studySummery.setText(summerySpanner);
            binding.studySummery.setMovementMethod(LinkMovementMethod.getInstance());

            //这里直接写成奖励信息
            if (!TextUtils.isEmpty(reward)){
                String formatStr = "本次学习获得%1$s元红包奖励";
                String showMsg = String.format(formatStr,reward);

                SpannableStringBuilder ssb = new SpannableStringBuilder();
                ssb.append(showMsg);
                ForegroundColorSpan priceSpan = new ForegroundColorSpan(getResources().getColor(R.color.exercise_error));
                ssb.setSpan(priceSpan,showMsg.indexOf(reward),showMsg.indexOf(reward)+reward.length(),Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
                binding.studySummery.setText(ssb);
                binding.studySummery.setMovementMethod(LinkMovementMethod.getInstance());
            }

            wordUnitAdapter = new StudyReadAdapter(1);
            wordUnitAdapter.setWordList(mWordList);
            binding.studyUnitList.setAdapter(wordUnitAdapter);
            binding.studyUnitList.setLayoutManager(new NoScrollLinearLayoutManager(mContext,false));

            //这里根据高度大小处理
            binding.studyUnitList.post(new Runnable() {
                @Override
                public void run() {
                    Objects.requireNonNull(getActivity()).runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int height = binding.studyUnitList.getHeight();
                            tagHeight+=height;
                        }
                    });
                }
            });

            wordLikeAdapter = new StudyReadAdapter(0);
            wordLikeAdapter.setWordEntity(wordEntities);
            binding.studyWordList.setAdapter(wordLikeAdapter);
            binding.studyWordList.setLayoutManager(new NoScrollLinearLayoutManager(mContext,false));

            //这里根据高度大小处理
            binding.studyWordList.post(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int height = binding.studyWordList.getHeight();
                            if (DensityUtil.getRealDisplay(getActivity()).getHeight()<1920){
                                binding.imageStudy.setVisibility(View.GONE);
                            }else {
                                if (wordEntities.size()>0&&height==0){
                                    binding.imageStudy.setVisibility(View.GONE);
                                }

                                if (mWordList.size()>0&&tagHeight==0){
                                    binding.imageStudy.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            });
        } else {
            binding.studyReadScroll.setVisibility(View.GONE);
            binding.studyEval.setVisibility(View.VISIBLE);
            binding.studyTitle.setText("恭喜您完成了口语评测！");

            int totalScore = 0;
            int sentenceSize = 0;
            mEvalGoodList = new ArrayList<>();
            mEvalBadList = new ArrayList<>();
            List<VoaText> mVoaList = mManager.getVoaTextbyVoaId(mVoa.voaId());
            List<VoaSoundNew> localArrayList = mManager.getVoaSoundVoaId(mVoa.voaId());
            for (VoaSoundNew voaSound: localArrayList) {
                if ((voaSound != null) && !TextUtils.isEmpty(voaSound.sound_url())) {
                    Log.e(TAG, "localArrayList voaRecord.audio() = " + voaSound.sound_url());
                    totalScore += voaSound.totalscore();
                    sentenceSize++;
                    for (VoaText voaText: mVoaList) {
                        if ((voaText != null) && (voaText.getVoaId() == voaSound.voa_id())) {
                            long itemId = Long.parseLong(voaText.getVoaId() +""+ voaText.paraId()+""+voaText.idIndex());
                            if ((itemId == voaSound.itemid()) && voaSound.totalscore() > 90) {
                                voaText.readScore = voaSound.totalscore();
                                mEvalGoodList.add(voaText);
                            } else if ((itemId == voaSound.itemid()) && voaSound.totalscore() <= 90) {
                                voaText.readScore = voaSound.totalscore();
                                mEvalBadList.add(voaText);
                            }
                        }
                    }
                }
            }
            Log.e(TAG, "init sentenceSize == " + sentenceSize);
            String sentences = "0";
            String averages = "0";
            if (sentenceSize > 0) {
                sentences = "" + sentenceSize;
                averages = "" + (totalScore/sentenceSize);
            }
            StringBuilder summery = new StringBuilder(32);
            summery.append("本次共录制");
            summery.append(sentences);
            summery.append("句，平均分");
            summery.append(averages);
            summery.append("分");
            ForegroundColorSpan spanColor1 = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
            ForegroundColorSpan spanColor = new ForegroundColorSpan(getResources().getColor(R.color.colorPrimary));
            SpannableStringBuilder summerySpanner = new SpannableStringBuilder(summery);
            summerySpanner.setSpan(spanColor1, summery.indexOf("录制") + 2, summery.indexOf("录制") + 2 + sentences.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            summerySpanner.setSpan(spanColor, summery.indexOf("平均分") + 3, summery.indexOf("平均分") + 3 + averages.length(), Spannable.SPAN_INCLUSIVE_EXCLUSIVE);
            binding.studySummery.setText(summerySpanner);
            binding.studySummery.setMovementMethod(LinkMovementMethod.getInstance());

            evalGoodAdapter = new StudyEvalAdapter();
            evalGoodAdapter.setTextList(mEvalGoodList);
            binding.evalGoodList.setAdapter(evalGoodAdapter);
            binding.evalGoodList.setLayoutManager(new NoScrollLinearLayoutManager(mContext,false));

            //这里根据高度大小处理
            binding.evalGoodList.post(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int height = binding.evalGoodList.getMeasuredHeight();
                            tagHeight+=height;
                        }
                    });
                }
            });

            evalBadAdapter = new StudyEvalAdapter();
            evalBadAdapter.setTextList(mEvalBadList);
            binding.evalBadList.setAdapter(evalBadAdapter);
            binding.evalBadList.setLayoutManager(new NoScrollLinearLayoutManager(mContext,false));

            //这里根据高度大小处理
            binding.evalBadList.post(new Runnable() {
                @Override
                public void run() {
                    getActivity().runOnUiThread(new Runnable() {
                        @Override
                        public void run() {
                            int height = binding.evalBadList.getHeight();
                            if (DensityUtil.getRealDisplay(getActivity()).getHeight()<1920){
                                binding.imageStudy.setVisibility(View.GONE);
                            }else {
                                if (mEvalBadList.size()>0&&height==0){
                                    binding.imageStudy.setVisibility(View.GONE);
                                }

                                if (mEvalGoodList.size()>0&&tagHeight==0){
                                    binding.imageStudy.setVisibility(View.GONE);
                                }
                            }
                        }
                    });
                }
            });
        }
        if (ConfigData.openShare) {
            binding.studyShare.setVisibility(View.VISIBLE);
        }else {
            binding.studyShare.setVisibility(View.INVISIBLE);
        }
    }

    public void writeBitmapToFile() {
        if (mActivity == null) {
            Log.e(TAG, "writeBitmapToFile mActivity == null ?");
            return;
        }
        View view = mActivity.getWindow().getDecorView();
        view.setDrawingCacheEnabled(true);
        view.buildDrawingCache();
        Bitmap bitmap = view.getDrawingCache();
        if (bitmap == null) {
            return;
        }
        bitmap.setHasAlpha(false);
        bitmap.prepareToDraw();
        File newpngfile = BaseStorageUtil.getExternalFile(TalkShowApplication.getContext(), "sign",  System.currentTimeMillis() + ".png");
        signPath = newpngfile.getAbsolutePath();
        Log.e(TAG, "writeBitmapToFile " + signPath);
        if (newpngfile.exists()) {
            newpngfile.delete();
        }
        try {
            FileOutputStream out = new FileOutputStream(newpngfile);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
            out.flush();
            out.close();
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    public void showShareOnMoment(Context context, int userID, int AppId) {
        OnekeyShare oks = new OnekeyShare();
        //关闭sso授权
        oks.disableSSOWhenAuthorize();
        oks.setPlatform(WechatMoments.NAME);
        oks.setImagePath(signPath);
        ShareSDK.getPlatform(Wechat.NAME);
        oks.setSilent(true);
        oks.setCallback(new PlatformActionListener() {
            @Override
            public void onComplete(Platform platform, int i, HashMap<String, Object> hashMap) {
                Log.e(TAG, "--分享成功===");
            }

            @Override
            public void onError(Platform platform, int i, Throwable throwable) {
                Log.e(TAG, "--分享失败===");
                if (throwable != null) {
                    Log.e(TAG, "--throwable=== " + throwable.getMessage());
                }
            }

            @Override
            public void onCancel(Platform platform, int i) {
                Log.e(TAG, "--分享取消===");
            }
        });
        oks.show(context);
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

}
