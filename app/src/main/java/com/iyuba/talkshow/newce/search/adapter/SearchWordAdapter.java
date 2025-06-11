package com.iyuba.talkshow.newce.search.adapter;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.recyclerview.widget.RecyclerView;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.databinding.ItemSearchWordBinding;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newce.search.view.ExercisePlayer;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.entity.TalkShowWords;
import com.iyuba.wordtest.manager.WordManager;
import com.iyuba.wordtest.ui.detail.WordDetailActivity;
import com.iyuba.wordtest.utils.StorageUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * 查询-单词适配器
 */
public class SearchWordAdapter extends RecyclerView.Adapter<SearchWordAdapter.WordHolder> {

    private Context context;
    private List<TalkShowWords> list;

    //建议修改成exoplayer，然后放在外面处理
    private ExercisePlayer exercisePlayer;

    public SearchWordAdapter(Context context, List<TalkShowWords> list) {
        this.context = context;
        this.list = list;
    }

    @NonNull
    @Override
    public WordHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        ItemSearchWordBinding binding = ItemSearchWordBinding.inflate(LayoutInflater.from(context),parent,false);
        return new WordHolder(binding);
    }

    @Override
    public void onBindViewHolder(@NonNull WordHolder holder, int position) {
        if (holder==null){
            return;
        }

        TalkShowWords bean = list.get(position);
        //单词
        holder.word.setText(bean.word);
        //播放
        if (TextUtils.isEmpty(bean.audio)){
            holder.play.setVisibility(View.GONE);
        }else {
            holder.play.setVisibility(View.VISIBLE);
            holder.play.setOnClickListener(v->{
                if (exercisePlayer!=null&&exercisePlayer.isPlaying()){
                    pausePlay();
                }else {
                    startPlay(bean.audio);
                }
            });
        }
        //音标
        if (TextUtils.isEmpty(bean.pron)){
            holder.pron.setVisibility(View.GONE);
        }else {
            holder.pron.setVisibility(View.VISIBLE);
            holder.pron.setText("/"+bean.pron+"/");
        }
        //释义
        if (TextUtils.isEmpty(bean.def)){
            holder.explain.setVisibility(View.GONE);
        }else {
            holder.explain.setVisibility(View.VISIBLE);
            holder.explain.setText(bean.def);
        }

        holder.itemView.setOnClickListener(v->{
            jumpToWord(context,bean);
        });
    }

    @Override
    public int getItemCount() {
        return list==null?0:list.size();
    }

    class WordHolder extends RecyclerView.ViewHolder{

        private TextView word;
        private ImageView play;
        private TextView explain;
        private TextView pron;

        public WordHolder(ItemSearchWordBinding binding){
            super(binding.getRoot());

            word = binding.word;
            play = binding.play;
            explain = binding.explain;
            pron = binding.pron;
        }
    }

    //刷新数据
    public void refreshData(List<TalkShowWords> refreshList){
        this.list = refreshList;
        notifyDataSetChanged();
    }

    //播放音频
    private void startPlay(String audioUrl){
        if (!NetStateUtil.isConnected(context)){
            ToastUtil.showToast(context,"请链接网络后进行播放");
            return;
        }

        try {
            String dir = StorageUtil.getWordDir(context).getAbsolutePath();
            File file = new File(dir, StorageUtil.getWordName(audioUrl));
            String worDir = StorageUtil.getWordDir(context).getAbsolutePath() + "/primary_audio/" + StorageUtil.getWordName(audioUrl);
            File wordFile = new File(worDir);

            String playUri = audioUrl;
            if (file.exists()&&file.isFile()){
                playUri = file.getAbsolutePath();
            }else if (wordFile.exists()&&wordFile.isFile()){
                playUri = wordFile.getAbsolutePath();
            }

            exercisePlayer = ExercisePlayer.getInstance(context);
            exercisePlayer.setResource(playUri);
            exercisePlayer.setOnAudioPlayerListener(new ExercisePlayer.OnAudioPlayerListener() {
                @Override
                public void onPrepared() {
                    exercisePlayer.start();
                }

                @Override
                public void onCompletion() {

                }
            });
        }catch (Exception e){
            e.printStackTrace();
        }
    }

    //暂停播放
    private void pausePlay(){
        if (exercisePlayer!=null&&exercisePlayer.isPlaying()){
            exercisePlayer.pause();
        }
    }

    //释放音频
    public void stopAudio(){
        pausePlay();
    }

    //跳转单词界面
    private void jumpToWord(Context context, TalkShowWords talkShowWords){
        WordManager.getInstance().init(UserInfoManager.getInstance().getUserName(),
                String.valueOf(UserInfoManager.getInstance().getUserId()),
                App.APP_ID, Constant.EVAL_TYPE, UserInfoManager.getInstance().isVip() ? 1 : 0, App.APP_NAME_EN);
        WordManager.getInstance().migrateData(TalkShowApplication.getContext());

        List<TalkShowWords> tempList = new ArrayList<>();
        tempList.add(talkShowWords);
        WordDetailActivity.start(context,tempList,0,talkShowWords.book_id,talkShowWords.unit_id);
    }
}
