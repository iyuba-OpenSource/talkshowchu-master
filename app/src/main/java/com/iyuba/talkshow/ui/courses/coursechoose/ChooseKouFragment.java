package com.iyuba.talkshow.ui.courses.coursechoose;

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
import com.iyuba.talkshow.data.model.LessonNewResponse;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.FragmentCoursechooseBinding;
import com.iyuba.talkshow.event.KouBookEvent;
import com.iyuba.talkshow.injection.PerFragment;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.ui.base.BaseFragment;
import com.iyuba.talkshow.ui.courses.coursedetail.CourseDetailActivity;
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
public class ChooseKouFragment extends BaseFragment implements ChooseCourseMVPView {

    String catId ;
    CourseChooseAdapter adapter  ;
    CourseTypeAdapter courseTypeAdapter;
    CourseTitleAdapter courseTitleAdapter;
    List<TypeHolder> listTitle = new ArrayList<>();
    List<TypeHolder> list = new ArrayList<>();
    public static final String CATID = "CATID";
    public static final String FLAG = "FLAG";
    List<SeriesData> dataBeans  = new ArrayList<>();
    @Inject
    ChooseKouPresenter mPresenter ;
    private int flag;
    HashMap<String, List<TypeHolder>> hashXiao = new HashMap();
    WordDataBase db;
    CategorySeriesDao categoryDao;

    FragmentCoursechooseBinding binding ;

    public static ChooseKouFragment build(String catId, int flag) {
        ChooseKouFragment fragment  = new ChooseKouFragment() ;
        Bundle bundle  = new Bundle( );
        bundle.putString(CATID, catId);
        bundle.putInt(FLAG, flag);
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

    public ChooseKouFragment() {

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
        if (App.APP_ID == 260) {
            List<CategorySeries> newTitle = categoryDao.getVideoCategories(UserInfoManager.getInstance().getUserId(), "1");
            if ((newTitle == null) || (newTitle.size() < 1)) {
                listTitle = TypeHelper.getDefaultBigTypeList();
                list = TypeHelper.getDefaultSmallTypeList(mPresenter.getKouClass());
            } else {
                listTitle.clear();
                list.clear();
                hashXiao.clear();
                for (int i = 0; i < newTitle.size(); i++) {
                    CategorySeries category = newTitle.get(i);
                    if (hashXiao.containsKey(category.SourceType)) {
                        List<TypeHolder> result = hashXiao.get(category.SourceType);
                        result.add(new TypeHolder(newTitle.get(i).Category, newTitle.get(i).SeriesName));
                    } else {
                        List<TypeHolder> result = new ArrayList<>();
                        result.add(new TypeHolder(newTitle.get(i).Category, newTitle.get(i).SeriesName));
                        hashXiao.put(category.SourceType, result);
                    }
                }
                setXiaoTitle();
                setXiaoType();
            }
        } else {
            List<CategorySeries> newTitle = categoryDao.getVideoCategories(UserInfoManager.getInstance().getUserId(), "1");
            if ((newTitle == null) || (newTitle.size() < 1)) {
                listTitle = TypeHelper.getChuKouHolder();
            } else {
                listTitle.clear();
                list.clear();
                if (newTitle.size() < 3) {
                    for (int i = 0; i < newTitle.size(); i++) {
                        listTitle.add(new TypeHolder(newTitle.get(i).Category, newTitle.get(i).SeriesName));
                    }
                } else {
                    int middle = newTitle.size()/2;
                    for (int i = 0; i < middle; i++) {
                        listTitle.add(new TypeHolder(newTitle.get(i).Category, newTitle.get(i).SeriesName));
                    }
                    for (int i = middle; i < newTitle.size(); i++) {
                        list.add(new TypeHolder(newTitle.get(i).Category, newTitle.get(i).SeriesName));
                    }
                }
            }
        }
        Log.e("ChooseKouFragment", "onViewCreated newTitle " + listTitle.size());
        Log.e("ChooseKouFragment", "onViewCreated list " + list.size());
        catId  = getArguments().getString(CATID);
        flag  = getArguments().getInt(FLAG);
        binding.recycler.setLayoutManager(new GridLayoutManager(mActivity,3));
        adapter = new CourseChooseAdapter(dataBeans,flag);
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
            courseTitleAdapter.putActiveTitle(mPresenter.getKouClass());
            courseTypeAdapter.putActiveType(mPresenter.getKouType());
            setTextArea(catId);
        } else {
            courseTitleAdapter.putActiveTitle(mPresenter.getKouClass());
            courseTypeAdapter.putActiveType(mPresenter.getKouType());
        }
//        if (TypeHelper.TYPE_PRIMARY_RENJIAO == TypeHelper.DEFAULT_TYPE) {
//            binding.recyclertitle.setVisibility(View.GONE);
//        }
//        Log.e("ChooseKouPresenter", "onViewCreated catId " + catId);
        if (NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            mPresenter.getSeriesList(catId);
            mPresenter.chooseLessonNew();
        } else {
            ToastUtil.showToast(mContext, "选择课程需要打开数据网络。");
            mPresenter.chooseCourse(catId);
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
        List<SeriesData> dataList = new ArrayList<>();
        for (SeriesData series: beans) {
            if ("1".equals(series.getVideo())) {
                dataList.add(series);
            }
        }
        if (dataList == null || dataList.size() < 1) {
            showToastShort("暂时没有相应的口语秀资源，请稍后再试。");
        } else {
            Collections.sort(dataList, new SortBySeries());
        }
        adapter.setDataBeans(dataList);
    }
    @Override
    public void setCoures(List<SeriesData> beans) {
        if (beans == null || beans.size() < 1) {
            mPresenter.chooseCourse(catId);
            return;
        }
        List<SeriesData> dataList = new ArrayList<>();
        for (SeriesData series: beans) {
            if ("1".equals(series.getVideo())) {
                dataList.add(series);
            }
        }
        if (dataList == null || dataList.size() < 1) {
            showToastShort("暂时没有相应的口语秀资源，请稍后再试。");
        } else {
            Collections.sort(dataList, new SortBySeries());
        }
        adapter.setDataBeans(dataList);
    }

    @Override
    public void setCourseFail(String showMsg) {

    }

    @Override
    public void setLesson(List<LessonNewResponse.Series> series) {
        if (series == null || series.size() < 1) {
            return;
        }
        if (App.APP_ID == 260) {
            setXiaoLesson(series);
        } else {
            setChuLesson(series);
        }
    }

    @Override
    public void setLessonFail(String showMsg) {

    }

    private void setChuLesson(List<LessonNewResponse.Series> series) {
        Log.e("ChooseKouFragment", "setLesson series.size() " + series.size());
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
                if ("1".equals(seriesData.isVideo)) {
                    newTitle.add(new TypeHolder(seriesData.Category,seriesData.SeriesName));
                }
                CategorySeries categorySer = categoryDao.getUidCategory(seriesData.Category, UserInfoManager.getInstance().getUserId());
                if (categorySer == null) {
                    categoryDao.saveCategory(new CategorySeries(seriesData.Category, seriesData.SeriesName, seriesData.lessonName, ser.SourceType, seriesData.isVideo, UserInfoManager.getInstance().getUserId()));
                } else {
                    categoryDao.updateCategory(new CategorySeries(seriesData.Category, seriesData.SeriesName, seriesData.lessonName, ser.SourceType, seriesData.isVideo, UserInfoManager.getInstance().getUserId()));
                }
            }
        }
        Log.e("ChooseKouFragment", "setLesson newTitle " + newTitle.size());
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
        Log.e("ChooseKouFragment", "setLesson newTitle " + listTitle.size());
        list.clear();
        for (int i = middle; i < newTitle.size(); i++) {
            list.add(newTitle.get(i));
        }
        courseTypeAdapter.SetCourseList(list);
        courseTypeAdapter.notifyDataSetChanged();
        Log.e("ChooseKouFragment", "setLesson newList " + list.size());
    }

    private void setXiaoLesson(List<LessonNewResponse.Series> series) {
        Log.e("ChooseKouFragment", "setXiaoLesson series.size() " + series.size());
        categoryDao.deleteCategory(UserInfoManager.getInstance().getUserId());
        listTitle.clear();
        list.clear();
        hashXiao.clear();
        for (LessonNewResponse.Series ser: series) {
            if (ser == null) {
                continue;
            }
            for (LessonNewResponse.SeriesDatas seriesData: ser.SeriesData) {
                if (seriesData == null) {
                    continue;
                }
                if ("1".equals(seriesData.isVideo)) {
                    if (hashXiao.containsKey(ser.SourceType)) {
                        List<TypeHolder> result = hashXiao.get(ser.SourceType);
                        result.add(new TypeHolder(seriesData.Category, seriesData.SeriesName));
                    } else {
                        List<TypeHolder> result = new ArrayList<>();
                        result.add(new TypeHolder(seriesData.Category, seriesData.SeriesName));
                        hashXiao.put(ser.SourceType, result);
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
        setXiaoTitle();
        setXiaoType();
        courseTitleAdapter.SetTitleList(listTitle);
        courseTypeAdapter.SetCourseList(list);
        courseTitleAdapter.notifyDataSetChanged();
        courseTypeAdapter.notifyDataSetChanged();
        Log.e("ChooseKouFragment", "setLesson newTitle " + listTitle.size());
        Log.e("ChooseKouFragment", "setLesson newList " + list.size());
    }

    private void setXiaoTitle() {
        int index = 0;
        if (hashXiao == null || hashXiao.size() < 1) {
            return;
        }
        if (hashXiao.containsKey("人教版")) {
            listTitle.add(new TypeHolder(TypeHelper.TYPE_PRIMARY_RENJIAO, "人教版"));
            ++index;
        }
        if (hashXiao.containsKey("北师版")) {
            listTitle.add(new TypeHolder(TypeHelper.TYPE_PRIMARY_BEISHI, "北师版"));
            ++index;
        }
        for (String key: hashXiao.keySet()) {
            switch (key) {
                case "人教版":
                case "北师版":
                    break;
                case "新概念":
                    if (App.APP_TENCENT_MOOC) {
                        listTitle.add(new TypeHolder(index, key));
                        ++index;
                    }
                    break;
                default:
                    listTitle.add(new TypeHolder(index, key));
                    ++index;
                    break;
            }
        }
    }

    private void setXiaoType() {
        if (hashXiao == null || hashXiao.size() < 1) {
            list = TypeHelper.getDefaultSmallTypeList(mPresenter.getKouClass());
            return;
        }
        list.clear();
        int index = mPresenter.getKouClass();
        Log.e("ChooseKouFragment", "setXiaoType index " + index);
        for (String key: hashXiao.keySet()) {
            Log.e("ChooseKouFragment", "setXiaoType key " + key);
            for (TypeHolder typeHolder: listTitle) {
                if ((index == typeHolder.getId()) && key.equals(typeHolder.getValue())) {
                    list.addAll(hashXiao.get(key));
                    Log.e("ChooseCoursePresenter", "setXiaoType list " + list.size());
                    return;
                }
            }
        }
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

    CourseChooseAdapter.CourseCallback callback = new CourseChooseAdapter.CourseCallback() {
        @Override
        public void onCourseClicked(int bookId, int count, int category, String courseTitle) {
            mPresenter.putKouId(bookId, courseTitle);
            mPresenter.putKouCategory(category);
            Log.e("ChooseKouFragment", "onKouClicked bookId " + bookId);
            int version = 0;
            if (WordManager.WordDataVersion == 2) {
                NewBookLevelDao newLevelDao = WordDataBase.getInstance(TalkShowApplication.getInstance()).getNewBookLevelDao();
                if (newLevelDao != null) {
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
            } else {
                BookLevelDao bookLevelDao = WordDataBase.getInstance(TalkShowApplication.getInstance()).getBookLevelDao();
                if (bookLevelDao != null) {
                    BookLevels bookLevels = bookLevelDao.getBookLevel(bookId);
                    if (bookLevels != null) {
                        version = bookLevels.version;
                    }
                }
            }
            List<Voa> result = mPresenter.loadVoasByBookId(bookId);
            if (result == null || result.size() < count) {
                EventBus.getDefault().post(new KouBookEvent(bookId,version,true));
            } else {
                EventBus.getDefault().post(new KouBookEvent(bookId,version));
            }
            if (flag == OpenFlag.TO_DETAIL) {
                mActivity.startActivity(CourseDetailActivity.buildIntent(mActivity, bookId, mPresenter.getKouTitle()));
            }else if (flag == OpenFlag.TO_WORD){
//                startActivity(WordStepActivity.buildIntent(mContext,bookId,courseTitle));
                mActivity.finish();
            } else {
                mActivity.finish();
            }
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
            Log.e("ChooseKouPresenter", "onTypeChoose pos = " + pos);
            Log.e("ChooseKouPresenter", "onTypeChoose id = " + id);
            if (App.APP_ID == 259) {
                courseTitleAdapter.putActiveTitle(-1);
                courseTypeAdapter.putActiveType(pos);
                mPresenter.putKouClass(-1);
                mPresenter.putKouType(pos);
//                mPresenter.putKouCategory(id);
                catId = "" + id;
                mPresenter.getSeriesList(catId);
                setTextArea(catId);
                return;
            }
            mPresenter.putKouType(pos);
//            mPresenter.putKouCategory(id);
            catId = "" + id;
            mPresenter.getSeriesList(catId);
        }
    };

    CourseTitleAdapter.TitleCallback titleCallback = new CourseTitleAdapter.TitleCallback() {
        @Override
        public void onTitleChoose(int pos, int id) {
            Log.e("ChooseKouPresenter", "onTitleChoose pos = " + pos);
            Log.e("ChooseKouPresenter", "onTitleChoose id = " + id);
            if (App.APP_ID == 259) {
                courseTitleAdapter.putActiveTitle(pos);
                courseTypeAdapter.putActiveType(-1);
                mPresenter.putKouType(-1);
                mPresenter.putKouClass(pos);
//                mPresenter.putKouCategory(id);
                catId = "" + id;
                mPresenter.getSeriesList(catId);
                setTextArea(catId);
            } else {
                mPresenter.putKouClass(pos);
                setXiaoType();
//            Log.e("ChooseKouFragment", "list id = " + list.get(0).getValue());
                courseTypeAdapter.SetCourseList(list);
                courseTypeAdapter.notifyDataSetChanged();
            }
        }
    };
}
