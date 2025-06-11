//package com.iyuba.talkshow.newce.wordstep;
//
//import android.annotation.SuppressLint;
//import android.content.Context;
//import android.os.Build;
//import android.text.TextUtils;
//import android.view.LayoutInflater;
//import android.view.View;
//import android.view.ViewGroup;
//import android.widget.BaseAdapter;
//import android.widget.ImageView;
//import android.widget.LinearLayout;
//import android.widget.TextView;
//
//import com.iyuba.talkshow.Constant;
//import com.iyuba.talkshow.R;
//import com.iyuba.talkshow.TalkShowApplication;
//import com.iyuba.talkshow.lil.help_mvp.util.glide3.Glide3Util;
//import com.iyuba.talkshow.lil.user.UserInfoManager;
//import com.iyuba.talkshow.util.ToastUtil;
//import com.iyuba.wordtest.BuildConfig;
//import com.iyuba.wordtest.db.TalkShowTestsDao;
//import com.iyuba.wordtest.db.TalkShowWordsDao;
//import com.iyuba.wordtest.db.WordDataBase;
//import com.iyuba.wordtest.entity.TalkShowTests;
//import com.iyuba.wordtest.entity.TalkShowWords;
//import com.iyuba.wordtest.manager.WordManager;
//import com.iyuba.wordtest.ui.TalkshowWordListActivity;
//
//import java.util.ArrayList;
//import java.util.List;
//
//import de.hdodenhof.circleimageview.CircleImageView;
//
///**
// * Created by carl shen on 2020/7/31
// * New Primary English, new study experience.
// */
//public class WordstepAdapter extends BaseAdapter {
//    private int step = 0;
//    private int size = 0;
//    private final int book_id ;
//    Context context;
//    List<Integer> unitList = new ArrayList<>();
//    private final TalkShowWordsDao talkShowDao;
//    private List<TalkShowWords> talkShowWords;
//    private TalkShowTestsDao talkTestDao;
//    private List<TalkShowTests> talkShowTests;
//    private MobCallback mobCallback;
//    private WordstepFragment wordstepFragment;
//
//    public WordstepAdapter(int book_id , TalkShowWordsDao words) {
//        this.book_id = book_id;
//        talkShowDao = words;
//    }
//    public WordstepAdapter(int book_id , int step, List<Integer> units, TalkShowWordsDao words) {
//        this.book_id = book_id;
//        this.step = step;
//        if (units == null) {
//            this.unitList = new ArrayList<>();
//            this.size = 0;
//        } else {
//            this.unitList = units;
//            this.size = units.size();
//        }
//        talkShowDao = words;
//    }
//
//    public void setStep(int step1) {
//        step = step1;
//        notifyDataSetChanged();
//    }
//
//    @Override
//    public int getCount() {
//        return size;
//    }
//
//    @Override
//    public Object getItem(int position) {
//        return null;
//    }
//
//    @Override
//    public long getItemId(int position) {
//        return position;
//    }
//
//    public interface MobCallback {
//        void onMobCheck();
//    }
//    public void setMobCallback(MobCallback voaCallback) {
//        mobCallback = voaCallback;
//    }
//    public void setStepFragment(WordstepFragment stepFragment) {
//        wordstepFragment = stepFragment;
//    }
//
//    @SuppressLint({"ResourceAsColor", "DefaultLocale"})
//    @Override
//    public View getView(final int position, View convertView, ViewGroup parent) {
//        context = parent.getContext();
//        if (convertView == null) {
//            convertView = LayoutInflater.from(parent.getContext()).inflate(R.layout.fragment_wordstep_item, parent, false);
//        }
//        ViewHolder holder = new ViewHolder(convertView);
//        TextView tv = holder.tv;
//        CircleImageView iv = holder.img;
//        ImageView iv_avtor = holder.img_avator;
//        View vView = holder.lineVt;
//        iv.setVisibility(View.GONE);
//        iv_avtor.setVisibility(View.GONE);
//        if ((450 <= book_id) && (book_id <= 457)) {
//            tv.setText(String.format("Lesson %d", unitList.get(position)));
//        } else {
//            tv.setText(String.format("Unit %d", unitList.get(position)));
//        }
////        if (388 == book_id) {
////            tv.setText(String.format("Unit %d", position));
////        } else if (398 == book_id) {
////            tv.setText(String.format("Unit %d", position + 5));
////        } else if (400 == book_id) {
////            tv.setText(String.format("Unit %d", position + 5));
////        } else if (402 == book_id) {
////            tv.setText(String.format("Unit %d", position + 5));
////        } else {
////            tv.setText(String.format("Unit %d", position + 1));
////        }
//
//        tv.setOnClickListener(new View.OnClickListener() {
//            @Override
//            public void onClick(View v) {
//                if ((mobCallback != null) && (wordstepFragment != null) && !UserInfoManager.getInstance().isLogin()) {
//                    mobCallback.onMobCheck();
//                    return;
//                }
//                if ((WordManager.getInstance().vip == 0) && (position>0)){
//                    ToastUtil.showToast(context, "非VIP会员只能免费解锁前一关，如需解锁更多关卡，请开通VIP后重试");
//                    return;
//                }
//                if (position <= step || BuildConfig.DEBUG) {
//                    TalkshowWordListActivity.startIntnent(context, book_id , unitList.get(position), position, false);
////                    if (388 == book_id) {
////                        TalkshowWordListActivity.startIntnent(context, book_id , position , false);
////                    } else if (398 == book_id) {
////                        TalkshowWordListActivity.startIntnent(context, book_id , position + 5 , false);
////                    } else if (400 == book_id) {
////                        TalkshowWordListActivity.startIntnent(context, book_id , position + 5 , false);
////                    } else if (402 == book_id) {
////                        TalkshowWordListActivity.startIntnent(context, book_id , position + 5 , false);
////                    } else {
////                        TalkshowWordListActivity.startIntnent(context, book_id , position + 1 , false);
////                    }
//                }else {
//                    ToastUtil.showToast(context, context.getResources().getString(R.string.alert_step_no_finish));
//                }
//
//            }
//        });
//
//        if (step <= position) {
//            tv.setBackground(context.getResources().getDrawable(R.drawable.word_step_bg_lock));
////            tv.setText("未解锁");
//        }else {
//            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP) {
//                tv.getBackground().setTint(context.getResources().getColor(R.color.colorPrimary));
//            }
//            if (talkShowDao != null) {
//                talkShowWords = talkShowDao.getUnitWords(book_id, unitList.get(position));
//                if ((talkShowWords != null) && talkShowWords.size() > 0) {
//                    int sum = 0;
//                    if (WordManager.WordDataVersion == 2) {
//                        if (talkTestDao == null) {
//                            talkTestDao = WordDataBase.getInstance(TalkShowApplication.getContext()).getTalkShowTestsDao();
//                        }
//                        talkShowTests = talkTestDao.getUnitWords(book_id, unitList.get(position), WordManager.getInstance().userid);
//                        if ((talkShowTests != null) && talkShowTests.size() > 0) {
//                            for (TalkShowTests words : talkShowTests) {
//                                if (1 == words.wrong) {
//                                    sum++;
//                                }
//                            }
//                        }
//                    } else
//                    for (TalkShowWords words : talkShowWords) {
//                        if (1 == words.wrong) {
//                            sum++;
//                        }
//                    }
//                    if (sum > talkShowWords.size()) {
//                        sum = talkShowWords.size();
//                    }
//                    holder.correctTips.setVisibility(View.VISIBLE);
//                    holder.correctRight.setText("" + sum);
//                    holder.correctAll.setText("/" + talkShowWords.size());
//                }
//            }
//        }
//        vView.setVisibility(View.INVISIBLE);
//
//        if (step == position) {
//            iv.setVisibility(View.VISIBLE);
//            if ((wordstepFragment != null)) {
//                if (UserInfoManager.getInstance().isLogin()) {
//                    loadUserIcon( holder.img);
//                }
//            }
//            iv_avtor.setVisibility(View.VISIBLE);
//        }
//        return convertView;
//    }
//
//    private void loadUserIcon( ImageView imageView) {
//
//        String userIconUrl = "http://api." + Constant.Web.WEB2_SUFFIX + "v2/api.iyuba?protocol=10005&uid="
//                + WordManager.getInstance().userid + "&size=big";
//        if ((wordstepFragment != null) && !TextUtils.isEmpty(wordstepFragment.getUserImageUrl())) {
//            userIconUrl = wordstepFragment.getUserImageUrl();
//        }
//        /*Glide.with(context)
//                .load(userIconUrl)
//                .asBitmap()
//                .signature(new StringSignature(System.currentTimeMillis() + ""))
//                .placeholder(R.drawable.noavatar_small)
//                .into(imageView);*/
//        Glide3Util.loadImg(context,userIconUrl,R.drawable.noavatar_small,imageView);
//    }
//
//    static class ViewHolder {
//        TextView tv;
//        LinearLayout correctTips;
//        TextView correctRight;
//        TextView correctAll;
//        View lineVt;
//        CircleImageView img;
//        ImageView img_avator;
//
//
//        ViewHolder(View view) {
//            tv = view.findViewById(R.id.tv);
//            correctTips = view.findViewById(R.id.correct_tips);
//            correctRight = view.findViewById(R.id.correct_right);
//            correctAll = view.findViewById(R.id.correct_all);
//            lineVt = view.findViewById(R.id.line_vt);
//            img = view.findViewById(R.id.img);
//            img_avator = view.findViewById(R.id.img_indicator);
//        }
//    }
//}
