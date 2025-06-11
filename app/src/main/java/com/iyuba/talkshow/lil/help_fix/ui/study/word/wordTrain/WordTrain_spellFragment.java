package com.iyuba.talkshow.lil.help_fix.ui.study.word.wordTrain;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.os.Vibrator;
import android.text.TextUtils;
import android.util.Pair;
import android.view.View;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.databinding.FragmentWordTrainSpellBinding;
import com.iyuba.talkshow.lil.help_fix.data.bean.WordBean;
import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.view.dialog.MultiButtonDialog;
import com.iyuba.talkshow.lil.help_fix.view.dialog.SingleButtonDialog;
import com.iyuba.talkshow.lil.help_mvp.base.BaseViewBindingFragment;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;
import com.iyuba.talkshow.lil.help_mvp.util.StackUtil;

import java.util.List;

/**
 * @title: 拼写训练
 * @date: 2023/8/15 17:16
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class WordTrain_spellFragment extends BaseViewBindingFragment<FragmentWordTrainSpellBinding> implements WordTrainView {

    private String types;
    private String bookId;
    private int unitId;
    private int voaId;

    //当前单词的位置
    private int selectIndex = 0;
    //当前数据
    private Pair<WordBean, List<WordBean>> curBean;
    //当前需要练习的数据
    private List<Pair<WordBean,List<WordBean>>> pairList;

    private WordTrainPresenter presenter;

    //震动
    private Vibrator vibrator;

    //结果弹窗
    private SingleButtonDialog resultDialog;
    //进度弹窗
    private MultiButtonDialog progressDialog;

    //正确的单词数量
    private int rightCount = 0;
    //完成的单词数量
    private int progressCount = 0;

    /**
     *
     * @param types 数据类型-中小学、新概念全四册、新概念青少版
     * @param bookId 书籍id
     * @param unitId unitId
     * @param voaId voaId
     * @return
     */
    public static WordTrain_spellFragment getInstance(String types, String bookId, int unitId,int voaId){
        WordTrain_spellFragment fragment = new WordTrain_spellFragment();
        Bundle bundle = new Bundle();
        bundle.putString(StrLibrary.types,types);
        bundle.putString(StrLibrary.bookId,bookId);
        bundle.putInt(StrLibrary.id,unitId);
        bundle.putInt(StrLibrary.voaId,voaId);
        fragment.setArguments(bundle);
        return fragment;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        types = getArguments().getString(StrLibrary.types);
        bookId = getArguments().getString(StrLibrary.bookId);
        unitId = getArguments().getInt(StrLibrary.id);
        voaId = getArguments().getInt(StrLibrary.voaId);

        presenter = new WordTrainPresenter();
        presenter.attachView(this);
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initView();
        initClick();

        updateData();
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        closeProgressDialog();
        closeResultDialog();
        stopVibrate();
    }

    /**************************初始化**********************/
    private void initView(){
        binding.toolbar.getRoot().setVisibility(View.GONE);
        pairList = presenter.getRandomWordShowData(types,bookId,unitId,voaId);
    }

    private void initClick(){
        binding.next.setOnClickListener(v->{
            String showText = binding.next.getText().toString();
            if (showText.equals("检查拼写")){
                checkData();
            }else if (showText.equals("下一个")){
                selectIndex++;
                updateData();
            }else if (showText.equals("查看结果")){
                showResultDialog();
            }
        });
    }

    /****************************刷新数据********************/
    private void updateData(){
        curBean = pairList.get(selectIndex);

        binding.input.setEnabled(true);
        binding.input.setText("");
        binding.input.setTextColor(ResUtil.getInstance().getColor(R.color.black));
        binding.word.setText("");
        String pron = curBean.first.getPron();
        if (!TextUtils.isEmpty(pron)){
            pron = "["+pron+"]";
        }
        binding.def.setText(pron+"\t"+curBean.first.getDef());

        binding.progress.setMax(pairList.size());
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            binding.progress.setProgress(selectIndex+1,true);
        }else {
            binding.progress.setProgress(selectIndex+1);
        }
        binding.progressText.setText((selectIndex+1)+"/"+pairList.size());

        binding.next.setText("检查拼写");
    }

    private void checkData(){
        binding.input.setEnabled(false);
        binding.word.setText(curBean.first.getWord());
        String spellWord = binding.input.getText().toString();
        if (spellWord.equals(curBean.first.getWord())){
            binding.input.setTextColor(ResUtil.getInstance().getColor(R.color.exercise_right));
            //保存正确结果
            rightCount++;
        }else {
            binding.input.setTextColor(ResUtil.getInstance().getColor(R.color.exercise_error));
            showVibrate();
        }

        //写入进度
        progressCount++;

        if (selectIndex>=pairList.size()-1){
            binding.next.setText("查看结果");
        }else {
            binding.next.setText("下一个");
        }
    }

    /****************************辅助功能***********************/
    //显示震动
    private void showVibrate(){
        stopVibrate();
        if (vibrator==null){
            vibrator = (Vibrator) getActivity().getSystemService(Context.VIBRATOR_SERVICE);
        }
        vibrator.vibrate(300L);
    }

    //停止震动
    private void stopVibrate(){
        if (vibrator!=null){
            vibrator.cancel();
        }
    }

    //显示结果弹窗
    private void showResultDialog(){
        String msg = "正确数量："+rightCount+"\n正确率："+(rightCount*100/pairList.size())+"%"+"\n总数量："+pairList.size();

        if (resultDialog==null){
            resultDialog = new SingleButtonDialog(getActivity());
            resultDialog.create();
        }
        resultDialog.setTitle("训练结果");
        resultDialog.setMsg(msg);
        resultDialog.setButton("确定", new SingleButtonDialog.OnSingleClickListener() {
            @Override
            public void onClick() {
                StackUtil.getInstance().finishCur();
            }
        });
        resultDialog.show();
    }

    //关闭结果弹窗
    private void closeResultDialog(){
        if (resultDialog!=null){
            resultDialog.dismiss();
        }
    }

    //显示进度弹窗
    private void showProgressDialog(){
        if (progressDialog==null){
            progressDialog = new MultiButtonDialog(getActivity());
            progressDialog.create();
        }
        String msg = "当前已完成"+progressCount+"个单词，还有"+(pairList.size()-progressCount)+"个单词需要训练，是否退出当前训练？";
        progressDialog.setTitle("训练进度");
        progressDialog.setMsg(msg);
        progressDialog.setButton("继续训练", "立即退出", new MultiButtonDialog.OnMultiClickListener() {
            @Override
            public void onAgree() {
                StackUtil.getInstance().finishCur();
            }

            @Override
            public void onDisagree() {

            }
        });
        progressDialog.show();
    }

    //关闭进度弹窗
    private void closeProgressDialog(){
        if (progressDialog!=null){
            progressDialog.dismiss();
        }
    }

    //退出提示
    public boolean showExistDialog(){
        if (progressCount!=0&&progressCount<pairList.size()){
            showProgressDialog();
            return true;
        }

        closeResultDialog();
        return false;
    }
}
