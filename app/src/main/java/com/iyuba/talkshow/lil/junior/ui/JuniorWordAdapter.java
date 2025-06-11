package com.iyuba.talkshow.lil.junior.ui;


import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.db.TalkShowTestsDao;
import com.iyuba.wordtest.db.TalkShowWordsDao;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.TalkShowTests;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.ui.TalkshowWordListActivity;
import com.iyuba.wordtest.wxapi.WordAppData;

import java.util.ArrayList;
import java.util.List;

public class JuniorWordAdapter extends BaseAdapter {

    private int step = 0;
    private int size = 0;
    private int book_id ;
    Context context;
    List<Integer> unitList = new ArrayList<>();
    private TalkShowWordsDao talkShowDao;
    private List<TalkShowWords> talkShowWords;
    private TalkShowTestsDao talkTestDao;
    private List<TalkShowTests> talkShowTests;
    private MobCallback mobCallback;
    private JuniorWordFragment wordstepFragment;

    public JuniorWordAdapter(int book_id , TalkShowWordsDao words){
        this.book_id = book_id;
        talkShowDao = words;
    }

    public JuniorWordAdapter(int book_id , int step, List<Integer> units, TalkShowWordsDao words){
        this.book_id = book_id;
        this.step = step;
        if (units == null) {
            this.unitList = new ArrayList<>();
            this.size = 0;
        } else {
            this.unitList = units;
            this.size = units.size();
        }
        talkShowDao = words;
    }

    public void setStep(int step1) {
        step = step1;
        notifyDataSetChanged();
    }

    @Override
    public int getCount() {
        return size;
    }

    @Override
    public Object getItem(int position) {
        return null;
    }

    @Override
    public long getItemId(int position) {
        return position;
    }

    public interface MobCallback {
        void onMobCheck();
    }
    public void setMobCallback(MobCallback voaCallback) {
        mobCallback = voaCallback;
    }
    public void setStepFragment(JuniorWordFragment stepFragment) {
        wordstepFragment = stepFragment;
    }

    @SuppressLint({"ResourceAsColor", "DefaultLocale"})
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        context = parent.getContext();
        if (convertView == null){
            convertView = LayoutInflater.from(context).inflate(R.layout.item_words_new,parent,false);
        }
        ViewHolder holder = new ViewHolder(convertView);

        if ((450 <= book_id) && (book_id <= 457)) {
            holder.tvUnit.setText(String.format("Lesson %d", unitList.get(position)));
        } else {
            holder.tvUnit.setText(String.format("Unit %d", unitList.get(position)));
        }

        int allCount = getUnitWordsDataCount(position);
        int rightCount = getRightWordsDataCount(position);
        if (rightCount>allCount){
            rightCount = allCount;
        }
        String progress = rightCount+"/"+allCount;
        holder.tvProgress.setText(progress);

        if (position<step){
            holder.llWords.setSelected(true);
        }else if (position == step){
            if (rightCount*100/allCount<80){
                holder.llWords.setSelected(false);
            }else {
                holder.llWords.setSelected(true);
            }
        }else{
            holder.llWords.setSelected(false);
        }

        holder.llWords.setOnClickListener(v->{
            if ((mobCallback != null) && (wordstepFragment != null) && !UserInfoManager.getInstance().isLogin()) {
                mobCallback.onMobCheck();
                return;
            }
            if ((WordManager.getInstance().vip == 0) && (position>0)){
                ToastUtil.showToast(context, "非VIP会员只能免费解锁前一关，如需解锁更多关卡，请开通VIP后重试");
                return;
            }

            //保存部分必要数据
            WordAppData.getInstance(context).putAppId(App.APP_ID);
            WordAppData.getInstance(context).putAppNameEn(App.APP_NAME_EN);
            WordAppData.getInstance(context).putAppNameCn(context.getResources().getString(R.string.app_name));
            WordAppData.getInstance(context).putAppLogoUrl(App.Url.APP_ICON_URL);
            WordAppData.getInstance(context).putAppShareUrl(App.Url.SHARE_APP_URL);
            WordAppData.getInstance(context).putBookName(wordstepFragment.configManager.getWordTitle());

            if (position <= step){
                TalkshowWordListActivity.start(context, book_id , unitList.get(position), position,true);
            }else {
                TalkshowWordListActivity.start(context, book_id , unitList.get(position), position,false);
            }
        });
        return convertView;
    }

    static class ViewHolder{
        TextView tvUnit;
        TextView tvProgress;
        LinearLayout llWords;

        ViewHolder(View view){
            tvUnit = view.findViewById(R.id.tv_unit);
            tvProgress = view.findViewById(R.id.tv_progress);
            llWords = view.findViewById(R.id.ll_words_new);
        }
    }

    //获取当前单元的单词数量
    private int getUnitWordsDataCount(int position){
        int count = 0;

        if (talkShowDao!=null){
            talkShowWords = talkShowDao.getUnitWords(book_id,unitList.get(position));
            if ((talkShowWords!=null) && talkShowWords.size()>0){
                count = talkShowWords.size();
            }
        }

        return count;
    }

    //获取当前单元的正确单词数据
    private int getRightWordsDataCount(int position){
        int sum = 0;
        if (WordManager.WordDataVersion == 2){
            //test形式
            if (talkTestDao==null){
                talkTestDao = WordDataBase.getInstance(TalkShowApplication.getInstance()).getTalkShowTestsDao();
            }
            talkShowTests = talkTestDao.getUnitWords(book_id,unitList.get(position),WordManager.getInstance().userid);
            if ((talkShowTests!=null)&&talkShowTests.size()>0){
                for (TalkShowTests tests:talkShowTests){
                    if (tests.wrong == 1){
                        sum++;
                    }
                }
            }
        }else {
            if (talkShowDao!=null){
                talkShowWords = talkShowDao.getUnitWords(book_id,unitList.get(position));
                if ((talkShowWords!=null)&&talkShowWords.size()>0){
                    for (TalkShowWords words:talkShowWords){
                        if (words.wrong == 1){
                            sum++;
                        }
                    }
                }
            }
        }

        return sum;
    }
}
