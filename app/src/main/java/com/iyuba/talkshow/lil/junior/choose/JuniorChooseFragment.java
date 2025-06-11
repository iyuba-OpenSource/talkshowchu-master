package com.iyuba.talkshow.lil.junior.choose;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.manager.AbilityControlManager;
import com.iyuba.talkshow.data.model.LessonNewResponse;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.databinding.FragmentCoursechooseBinding;
import com.iyuba.talkshow.event.RefreshBookEvent;
import com.iyuba.talkshow.event.SelectBookEvent;
import com.iyuba.talkshow.injection.PerFragment;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.courses.coursechoose.ChooseCourseMVPView;
import com.iyuba.talkshow.ui.courses.coursechoose.ChooseCoursePresenter;
import com.iyuba.talkshow.ui.courses.coursechoose.CourseTitleAdapter;
import com.iyuba.talkshow.ui.courses.coursechoose.CourseTypeAdapter;
import com.iyuba.talkshow.ui.courses.coursechoose.TypeHelper;
import com.iyuba.talkshow.ui.courses.coursechoose.TypeHolder;
import com.iyuba.talkshow.util.NetStateUtil;
import com.iyuba.talkshow.util.ToastUtil;
import com.iyuba.wordtest.db.BookLevelDao;
import com.iyuba.wordtest.db.CategorySeriesDao;
import com.iyuba.wordtest.db.NewBookLevelDao;
import com.iyuba.wordtest.db.WordDataBase;
import com.iyuba.wordtest.entity.BookLevels;
import com.iyuba.wordtest.entity.CategorySeries;
import com.iyuba.wordtest.entity.NewBookLevels;
import com.iyuba.wordtest.manager.WordManager;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;


@PerFragment
public class JuniorChooseFragment extends BaseFragment implements ChooseCourseMVPView {

    String catId ;
    JuniorChooseAdapter adapter  ;
    CourseTypeAdapter courseTypeAdapter;
    CourseTitleAdapter courseTitleAdapter;
    List<TypeHolder> listTitle = new ArrayList<>();
    List<TypeHolder> list = new ArrayList<>();
    public static final String CATID = "CATID";
    List<SeriesData> dataBeans  = new ArrayList<>();
    @Inject
    ChooseCoursePresenter mPresenter ;
    HashMap<String, List<TypeHolder>> hashXiao = new HashMap();
    WordDataBase db;
    CategorySeriesDao categoryDao;

    //布局样式
    private FragmentCoursechooseBinding binding ;

    public static JuniorChooseFragment build(String catId) {
        JuniorChooseFragment fragment  = new JuniorChooseFragment() ;
        Bundle bundle  = new Bundle( );
        bundle.putString(CATID, catId);
        fragment.setArguments(bundle);
        return fragment ;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        fragmentComponent().inject(this);
        mPresenter.attachView(this);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        binding = FragmentCoursechooseBinding.inflate(getLayoutInflater(),container,false);
        return binding.getRoot();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        mPresenter.detachView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        db = WordDataBase.getInstance(TalkShowApplication.getContext());
        categoryDao = db.getCategorySeriesDao();
        List<CategorySeries> newTitle = categoryDao.getUidCategories(UserInfoManager.getInstance().getUserId());

        //控制人教版显示(这里实现两次处理，下边处理下有网络的情况下使用)
        if (AbilityControlManager.getInstance().isLimitPep()){
            List<CategorySeries> temps = new ArrayList<>();
            for (int i = 0; i < newTitle.size(); i++) {
                if (!newTitle.get(i).SeriesName.contains("人教版")){
                    temps.add(newTitle.get(i));
                }
            }
            newTitle = temps;
        }

        if ((newTitle == null) || (newTitle.size() < 3)) {
            listTitle = TypeHelper.getDefaultBigTypeList();
            list = TypeHelper.getDefaultSmallTypeList(mPresenter.getWordClass());
        } else {
            listTitle.clear();
            list.clear();
            if (newTitle.size() < 3) {
                for (int i = 0; i < newTitle.size(); i++) {
                    if (App.APP_TENCENT_MOOC) {
                        listTitle.add(new TypeHolder(newTitle.get(i).Category, newTitle.get(i).SeriesName));
                    } else {
                        if (!"新概念".equals(newTitle.get(i).SeriesName)) {
                            listTitle.add(new TypeHolder(newTitle.get(i).Category, newTitle.get(i).SeriesName));
                        }
                    }
                }
            } else {
                int middle = newTitle.size()/2;
                for (int i = 0; i < middle; i++) {
                    if (App.APP_TENCENT_MOOC) {
                        listTitle.add(new TypeHolder(newTitle.get(i).Category, newTitle.get(i).SeriesName));
                    } else {
                        if (!"新概念".equals(newTitle.get(i).SeriesName)) {
                            listTitle.add(new TypeHolder(newTitle.get(i).Category, newTitle.get(i).SeriesName));
                        }
                    }
                }
                for (int i = middle; i < newTitle.size(); i++) {
                    if (App.APP_TENCENT_MOOC) {
                        list.add(new TypeHolder(newTitle.get(i).Category, newTitle.get(i).SeriesName));
                    } else {
                        if (!"新概念".equals(newTitle.get(i).SeriesName)) {
                            list.add(new TypeHolder(newTitle.get(i).Category, newTitle.get(i).SeriesName));
                        }
                    }
                }
            }
        }
        Log.e("ChooseCoursePresenter", "onViewCreated listTitle " + listTitle.size());
        Log.e("ChooseCoursePresenter", "onViewCreated list " + list.size());
        catId  = getArguments().getString(CATID);
        binding.recycler.setLayoutManager(new GridLayoutManager(mActivity,3));
        adapter = new JuniorChooseAdapter(dataBeans);
        adapter.setVoaCallback(callback);
        binding.recycler.setAdapter(adapter);
        courseTypeAdapter = new CourseTypeAdapter(list);
        courseTypeAdapter.setCallback(cousreCallback);
        binding.recyclertype.setLayoutManager(new GridLayoutManager(mActivity,5));
        binding.recyclertype.setAdapter(courseTypeAdapter);
        if (list == null || list.size() < 1) {
            binding.recyclertype.setVisibility(View.GONE);
        }
        courseTitleAdapter = new CourseTitleAdapter(listTitle);
        courseTitleAdapter.setCallback(titleCallback);
        binding.recyclertitle.setLayoutManager(new GridLayoutManager(mActivity,5));
        binding.recyclertitle.setAdapter(courseTitleAdapter);

        if (App.APP_ID == 259) {
//            binding.recyclertitle.setVisibility(View.GONE);
//            binding.recyclertype.setVisibility(View.GONE);
            //全合并数据
            courseTitleAdapter.putActiveTitle(mPresenter.getWordClass());
            courseTypeAdapter.putActiveType(mPresenter.getWordType());
            courseTitleAdapter.putActiveTitle(mPresenter.getCourseClass());
            courseTypeAdapter.putActiveType(mPresenter.getCourseType());
            setTextArea(catId);
        } else {
            //全合并数据
            courseTitleAdapter.putActiveTitle(mPresenter.getWordClass());
            courseTypeAdapter.putActiveType(mPresenter.getWordType());
            courseTitleAdapter.putActiveTitle(mPresenter.getCourseClass());
            courseTypeAdapter.putActiveType(mPresenter.getCourseType());
        }
//        if (TypeHelper.TYPE_PRIMARY_RENJIAO == TypeHelper.DEFAULT_TYPE) {
//            binding.recyclertitle.setVisibility(View.GONE);
//        }
        Log.e("ChooseCoursePresenter", "onViewCreated catId " + catId);
        if (NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            mPresenter.getBookDataByRemote(catId);
            mPresenter.getTypeDataByRemote();
        } else {
            ToastUtil.showToast(mContext, "选择课程需要打开数据网络。");
            mPresenter.getBookDataByDb(catId);
        }
    }

    private void setTextArea(String catId) {
        Log.e("ChooseCoursePresenter", "setTextArea catId " + catId);
        binding.tvArea.setVisibility(View.VISIBLE);
        switch (catId) {
            case "316":
                binding.tvArea.setText(R.string.area_renjiao);
                break;
            case "330":
                binding.tvArea.setText(R.string.area_waiyan);
                break;
            case "331":
                binding.tvArea.setText(R.string.area_beishi);
                break;
            case "332":
                binding.tvArea.setText(R.string.area_renai);
                break;
            case "333":
                binding.tvArea.setText(R.string.area_jiban);
                break;
            case "334":
                binding.tvArea.setText(R.string.area_yilin);
                break;
            case "335":
                binding.tvArea.setText(R.string.area_lujiao);
                break;
            default:
                binding.tvArea.setVisibility(View.GONE);
                break;
        }
    }

    private class SortBySeries implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            SeriesData s1 = (SeriesData) o1;
            SeriesData s2 = (SeriesData) o2;
            if (Integer.parseInt(s1.getId()) > Integer.parseInt(s2.getId())) //小的在前面
                return 1;
            return -1;
        }
    }
    @Override
    public void setMoreCourse(List<SeriesData> beans) {
        if (beans == null || beans.size() < 1) {
            adapter.setDataBeans(beans);
            return;
        }

        Collections.sort(beans, new SortBySeries());
        adapter.setDataBeans(beans);
    }
    @Override
    public void setCoures(List<SeriesData> beans) {
        if (beans == null || beans.size() < 1) {
            mPresenter.getBookDataByDb(catId);
            Log.e("ChooseCoursePresenter", "setCoures beans is null for catId " + catId);
            return;
        }
        Log.e("ChooseCoursePresenter", "setCoures beans size " + beans.size());
        Collections.sort(beans, new SortBySeries());
        adapter.setDataBeans(beans);
    }

    @Override
    public void setCourseFail(String showMsg) {

    }

    @Override
    public void setLesson(List<LessonNewResponse.Series> series) {
        if (series == null || series.size() < 1) {
            return;
        }
        setChuLesson(series);
    }

    @Override
    public void setLessonFail(String showMsg) {

    }

    private void setChuLesson(List<LessonNewResponse.Series> series) {
        Log.e("ChooseCoursePresenter", "setLesson series.size() " + series.size());
        categoryDao.deleteCategory(UserInfoManager.getInstance().getUserId());
        List<TypeHolder> newTitle = new ArrayList<>();
        for (int i = 0; i < series.size(); i++) {
            LessonNewResponse.Series ser = series.get(i);
            if (ser == null) {
                continue;
            }
            for (LessonNewResponse.SeriesDatas seriesData: ser.SeriesData) {
                if (seriesData == null) {
                    continue;
                }
                if (App.APP_TENCENT_MOOC) {
                    if (TypeHelper.TYPE_OPPO_RENJIAO) {
                        newTitle.add(new TypeHolder(seriesData.Category,seriesData.SeriesName));
                    } else {
                        if (!"人教版".equals(seriesData.SeriesName)) {
                            newTitle.add(new TypeHolder(seriesData.Category,seriesData.SeriesName));
                        }
                    }
                } else {
                    if (!"新概念".equals(seriesData.SeriesName)) {
                        newTitle.add(new TypeHolder(seriesData.Category,seriesData.SeriesName));
                    }
                }
                CategorySeries categorySer = categoryDao.getUidCategory(seriesData.Category, UserInfoManager.getInstance().getUserId());
                if (categorySer == null) {
                    categoryDao.saveCategory(new CategorySeries(seriesData.Category, seriesData.SeriesName, seriesData.lessonName, ser.SourceType, seriesData.isVideo, UserInfoManager.getInstance().getUserId()));
                } else {
                    categoryDao.updateCategory(new CategorySeries(seriesData.Category, seriesData.SeriesName, seriesData.lessonName, ser.SourceType, seriesData.isVideo, UserInfoManager.getInstance().getUserId()));
                }
            }
        }
        Log.e("ChooseCoursePresenter", "setLesson newTitle " + newTitle.size());
        if (newTitle.size() < 3) {
            listTitle.clear();
            listTitle.addAll(newTitle);
            courseTitleAdapter.SetTitleList(listTitle);
            courseTitleAdapter.notifyDataSetChanged();
            binding.recyclertype.setVisibility(View.GONE);
            return;
        }
        binding.recyclertype.setVisibility(View.VISIBLE);
        int middle = newTitle.size()/2;
        listTitle.clear();
        for (int i = 0; i < middle; i++) {
            listTitle.add(newTitle.get(i));
        }
        courseTitleAdapter.SetTitleList(listTitle);
        courseTitleAdapter.notifyDataSetChanged();
        Log.e("ChooseCoursePresenter", "setLesson listTitle " + listTitle.size());
        list.clear();
        for (int i = middle; i < newTitle.size(); i++) {
            list.add(newTitle.get(i));
        }
        courseTypeAdapter.SetCourseList(list);
        courseTypeAdapter.notifyDataSetChanged();
        Log.e("ChooseCoursePresenter", "setLesson newList " + list.size());
    }

    @Override
    public void showToastShort(int resId) {
        ToastUtil.showToast(mActivity, getResources().getString(resId));
    }

    @Override
    public void showToastShort(String message) {
        ToastUtil.showToast(mActivity, message);
    }

    @Override
    public void showToastLong(int resId) {
        ToastUtil.showToast(mActivity, getResources().getString(resId));
    }

    @Override
    public void showToastLong(String message) {
        ToastUtil.showToast(mActivity, message);
    }

    JuniorChooseAdapter.CourseCallback callback = new JuniorChooseAdapter.CourseCallback() {
        @Override
        public void onCourseClicked(int bookId, int count, int category, String courseTitle) {
            //全合并数据
            mPresenter.putWordId(bookId, courseTitle);
            mPresenter.putWordCategory(category);
            mPresenter.putCourseId(bookId, courseTitle);
            mPresenter.putCourseCategory(category);

            Log.e("ChooseCourseFragment", "onCourseClicked bookId " + bookId);
            BookLevelDao bookLevelDao = WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao();
            int version = 0;
            if (WordManager.WordDataVersion == 2) {
                NewBookLevelDao newLevelDao = WordDataBase.getInstance(TalkShowApplication.getInstance()).getNewBookLevelDao();
                if (bookLevelDao != null) {
                    NewBookLevels newLevel = newLevelDao.getBookLevel(bookId, String.valueOf(UserInfoManager.getInstance().getUserId()));
                    if (newLevel == null) {
                        TalkShowApplication.getSubHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                NewBookLevels levels = new NewBookLevels(bookId, 0, 0, 0, String.valueOf(UserInfoManager.getInstance().getUserId()));
                                newLevelDao.saveBookLevel(levels);
                            }
                        });
                    } else {
                        version = newLevel.version;
                    }
                }
            } else{
                if (bookLevelDao != null) {
                    BookLevels bookLevels = bookLevelDao.getBookLevel(bookId);
                    if (bookLevels == null) {
                        TalkShowApplication.getSubHandler().post(new Runnable() {
                            @Override
                            public void run() {
                                BookLevels levels = new BookLevels(bookId, 0, 0, 0);
                                bookLevelDao.saveBookLevel(levels);
                            }
                        });
                    } else {
                        version = bookLevels.version;
                    }
                }
            }
            //课程
            EventBus.getDefault().post(new SelectBookEvent(bookId,version, true));
            //单词
            EventBus.getDefault().post(new RefreshBookEvent(bookId,version,true));
            mActivity.finish();
        }

        @Override
        public void onCourseLongClicked(int series) {
            AlertDialog.Builder builder = new AlertDialog.Builder(mActivity);
            builder.setMessage("是否删除此书下的视频？");
            builder.setPositiveButton("是的", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    mPresenter.deletCourses(series);
                }
            });
            builder.setCancelable(true);
            builder.show();
        }
    };

    CourseTypeAdapter.Callback cousreCallback = new CourseTypeAdapter.Callback() {
        @Override
        public void onTypeChoose(int pos, int id) {
            Log.e("ChooseCoursePresenter", "onTypeChoose pos = " + pos);
            Log.e("ChooseCoursePresenter", "onTypeChoose id = " + id);
            //全合并数据
            mPresenter.putWordClass(-1);
            mPresenter.putWordType(pos);
            mPresenter.putWordCategory(id);

            mPresenter.putCourseClass(-1);
            mPresenter.putCourseType(pos);
            mPresenter.putCourseCategory(id);

            courseTitleAdapter.putActiveTitle(-1);
            courseTypeAdapter.putActiveType(pos);

            catId = "" + id;
            mPresenter.getBookDataByRemote(catId);
            setTextArea(catId);
        }
    };

    CourseTitleAdapter.TitleCallback titleCallback = new CourseTitleAdapter.TitleCallback() {
        @Override
        public void onTitleChoose(int pos, int id) {
            Log.e("ChooseCoursePresenter", "onTitleChoose pos = " + pos);
            Log.e("ChooseCoursePresenter", "onTitleChoose id = " + id);
            //全合并数据
            mPresenter.putWordCategory(id);
            mPresenter.putWordClass(pos);
            mPresenter.putWordType(-1);
            courseTitleAdapter.putActiveTitle(pos);
            courseTypeAdapter.putActiveType(-1);

            mPresenter.putCourseCategory(id);
            mPresenter.putCourseClass(pos);
            mPresenter.putCourseType(-1);
            courseTitleAdapter.putActiveTitle(pos);
            courseTypeAdapter.putActiveType(-1);

            catId = "" + id;
            mPresenter.getBookDataByRemote(catId);
            setTextArea(catId);
        }
    };
}
