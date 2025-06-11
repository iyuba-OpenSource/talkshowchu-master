package com.iyuba.talkshow.newce.study.word;

import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.util.Pair;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AlertDialog;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.data.listener.OnSimpleClickListener;
import com.iyuba.talkshow.lil.help_fix.ui.study.word.WordShowBottomAdapter;
import com.iyuba.talkshow.lil.help_fix.ui.study.word.wordTrain.WordTrainActivity;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.lil.user.util.NewLoginUtil;
import com.iyuba.talkshow.newce.study.StudyActivity;
import com.iyuba.talkshow.newdata.Config;
import com.iyuba.talkshow.newdata.Playmanager;
import com.iyuba.talkshow.newdata.RefreshEvent;
import com.iyuba.talkshow.newdata.SPconfig;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.umeng.analytics.MobclickAgent;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by carl shen on 2021/8/28
 * New Primary English, new study experience.
 */
public class VoaWordFragment extends BaseFragment {
    public static String TAG = VoaWordFragment.class.getSimpleName();
    private RecyclerView wordsListView = null;
    private VoaWordAdapter wordsAdapter;
    private View rootView;
    private Voa mVoa;
    private int unitId = 0;

    //当前章节位置
    private int positionInList = -1;
    //类型
    private String types = TypeLibrary.BookType.junior_middle;
    //底部控件
    private RecyclerView bottomView;

    //增加登录判断处理
    public static VoaWordFragment newInstance(Voa voa, int unit,int position) {
        VoaWordFragment fragment = new VoaWordFragment();
        Bundle args = new Bundle();
        args.putParcelable(StudyActivity.VOA, voa);
        args.putInt(StudyActivity.UNIT, unit);
        //当前章节在列表中所在的位置
        args.putInt(StrLibrary.position,position);
        fragment.setArguments(args);
        return fragment;
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (rootView == null) {
            rootView = inflater.inflate(R.layout.voa_word_list, container, false);
        }
        return rootView;
    }


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        //注册eventbus
        EventBus.getDefault().register(this);
        fragmentComponent().inject(this);
        wordsListView = rootView.findViewById(R.id.word_list);
        bottomView = rootView.findViewById(R.id.bottomView);
        initVoaWords();
    }

    // 初始化单词生词
    private void initVoaWords() {
        mVoa = getArguments().getParcelable(StudyActivity.VOA);
        unitId = getArguments().getInt(StudyActivity.UNIT, 0);
        positionInList = getArguments().getInt(StrLibrary.position,-1);
        wordsAdapter = new VoaWordAdapter(mContext, mVoa, unitId);
        wordsListView.setAdapter(wordsAdapter);
        wordsListView.setLayoutManager(new LinearLayoutManager(mContext));

        List<Pair<String, Pair<Integer,String>>> pairList = new ArrayList<>();
        pairList.add(new Pair<>(TypeLibrary.WordTrainType.Train_enToCn,new Pair<>(R.drawable.vector_en2cn,"英汉训练")));
        pairList.add(new Pair<>(TypeLibrary.WordTrainType.Train_cnToEn,new Pair<>(R.drawable.vector_cn2en,"汉英训练")));
        pairList.add(new Pair<>(TypeLibrary.WordTrainType.Word_spell,new Pair<>(R.drawable.vector_spelling,"单词拼写")));
        pairList.add(new Pair<>(TypeLibrary.WordTrainType.Train_listen,new Pair<>(R.drawable.vector_listen,"听力训练")));
        WordShowBottomAdapter bottomAdapter = new WordShowBottomAdapter(getActivity(),pairList);
        GridLayoutManager bottomManager = new GridLayoutManager(getActivity(),pairList.size());
        bottomView.setLayoutManager(bottomManager);
        bottomView.setAdapter(bottomAdapter);
        bottomAdapter.setListener(new OnSimpleClickListener<String>() {
            @Override
            public void onClick(String showType) {
                //这里需要判断当前章节的位置，前三个章节免费，后面的需要开通会员收费
                if (positionInList<3){
                    WordTrainActivity.start(getActivity(),showType,types,String.valueOf(mVoa.series()),unitId,mVoa.voaId());
                }else {
                    if (!UserInfoManager.getInstance().isLogin()){
                        new AlertDialog.Builder(getActivity())
                                .setTitle("使用说明")
                                .setMessage("课程前三课程为免费课程，后续课程需要登录您的账号，是否继续使用？")
                                .setPositiveButton("继续使用", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        NewLoginUtil.startToLogin(getActivity());
                                    }
                                }).setNegativeButton("暂不使用",null)
                                .setCancelable(false)
                                .show();
                        return;
                    }

                    if (!UserInfoManager.getInstance().isVip()){
                        new AlertDialog.Builder(getActivity())
                                .setTitle("使用说明")
                                .setMessage("课程前三课程为免费课程，后续课程需要开通会员后使用，是否继续使用？")
                                .setPositiveButton("继续使用", new DialogInterface.OnClickListener() {
                                    @Override
                                    public void onClick(DialogInterface dialog, int which) {
                                        dialog.dismiss();
                                        NewVipCenterActivity.start(getActivity(),NewVipCenterActivity.BENYINGYONG);
                                    }
                                }).setNegativeButton("暂不使用",null)
                                .setCancelable(false)
                                .show();
                        return;
                    }

                    WordTrainActivity.start(getActivity(),showType,types,String.valueOf(mVoa.series()),unitId,mVoa.voaId());
                }
            }
        });

        //这里确认下，如果位置是-1，则表示是从其他界面出来的，无法判断位置，则不显示训练功能
        if (positionInList<0){
            bottomView.setVisibility(View.GONE);
        }
    }

    public void onResume() {
        super.onResume();
        MobclickAgent.onResume(mContext);
    }

    @Override
    public void onPause() {
        super.onPause();
        MobclickAgent.onPause(mContext);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        //解除绑定
        EventBus.getDefault().unregister(this);
    }

    //刷新数据
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void event(RefreshEvent event){
        int voaId = SPconfig.Instance().loadInt(Config.currVoaId);
        if (Playmanager.getInstance().getVoaFromList(voaId) != null) {
            mVoa = Playmanager.getInstance().getVoaFromList(voaId);
            unitId = mVoa.UnitId;
            wordsAdapter = new VoaWordAdapter(mContext,mVoa,unitId);
            wordsListView.setAdapter(wordsAdapter);
        }
    }
}