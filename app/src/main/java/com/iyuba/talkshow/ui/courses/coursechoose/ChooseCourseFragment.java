package com.iyuba.talkshow.ui.courses.coursechoose;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.recyclerview.widget.GridLayoutManager;

import com.iyuba.sdk.other.NetworkUtil;
import com.iyuba.talkshow.R;
import com.iyuba.talkshow.TalkShowApplication;
import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.data.manager.AbilityControlManager;
import com.iyuba.talkshow.data.model.LessonNewResponse;
import com.iyuba.talkshow.data.model.SeriesData;
import com.iyuba.talkshow.data.model.Voa;
import com.iyuba.talkshow.databinding.FragmentCoursechooseBinding;
import com.iyuba.talkshow.event.RefreshBookEvent;
import com.iyuba.talkshow.event.SelectBookEvent;
import com.iyuba.talkshow.injection.PerFragment;
import com.iyuba.talkshow.lil.help_fix.view.dialog.LoadingDialog;
import com.iyuba.talkshow.lil.help_mvp.util.DateUtil;
import com.iyuba.talkshow.lil.help_mvp.view.NoScrollGridLayoutManager;
import com.iyuba.talkshow.lil.user.UserInfoManager;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlayEvent;
import com.iyuba.talkshow.newce.study.read.newRead.service.PrimaryBgPlaySession;
import com.iyuba.talkshow.ui.base.BaseViewBindingFragmet;
import com.iyuba.talkshow.ui.courses.coursedetail.CourseDetailActivity;
import com.iyuba.talkshow.util.LogUtil;
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
import com.scwang.smartrefresh.layout.api.RefreshLayout;
import com.scwang.smartrefresh.layout.header.ClassicsHeader;
import com.scwang.smartrefresh.layout.listener.OnRefreshListener;

import org.greenrobot.eventbus.EventBus;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;

import javax.inject.Inject;


/**
 * 书本选择界面
 *
 * 这里后期尽量修改成按照id进行查询的，因为位置查询可能存在问题，导致部分数据灭失时显示不正确
 */
@PerFragment
public class ChooseCourseFragment extends BaseViewBindingFragmet<FragmentCoursechooseBinding> implements ChooseCourseMVPView {

    String catId;
    private int flag;

    //展示
    CourseTitleAdapter bigTypeAdapter;
    CourseTypeAdapter smallTypeAdapter;
    CourseChooseAdapter courseAdapter;

    List<TypeHolder> bigTypeList = new ArrayList<>();
    List<TypeHolder> smallTypeList = new ArrayList<>();
    List<SeriesData> courseList = new ArrayList<>();

    //参数
    public static final String CATID = "CATID";
    public static final String FLAG = "FLAG";
    public static final String TYPE = "TYPE";

    @Inject
    ChooseCoursePresenter mPresenter;
    HashMap<String, List<TypeHolder>> hashXiao = new HashMap();
    WordDataBase db;
    CategorySeriesDao categoryDao;

    //布局样式
    FragmentCoursechooseBinding binding;

    public static ChooseCourseFragment build(String catId, int flag, int type) {
        ChooseCourseFragment fragment = new ChooseCourseFragment();
        Bundle bundle = new Bundle();
        bundle.putString(CATID, catId);
        bundle.putInt(FLAG, flag);
        bundle.putInt(TYPE, type);
        fragment.setArguments(bundle);
        return fragment;
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
        binding = FragmentCoursechooseBinding.inflate(getLayoutInflater(), container, false);
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

        //获取类型数据
//        getLocalTypeData();

        catId = getArguments().getString(CATID);
        flag = getArguments().getInt(FLAG);

        //大类型的内容
        bigTypeAdapter = new CourseTitleAdapter(bigTypeList);
        bigTypeAdapter.setCallback(titleCallback);
        binding.recyclertitle.setLayoutManager(new GridLayoutManager(mActivity, 5));
        binding.recyclertitle.setAdapter(bigTypeAdapter);

        //小类型的内容
        smallTypeAdapter = new CourseTypeAdapter(smallTypeList);
        smallTypeAdapter.setCallback(cousreCallback);
        binding.recyclertype.setLayoutManager(new GridLayoutManager(mActivity, 5));
        binding.recyclertype.setAdapter(smallTypeAdapter);
        if (smallTypeList == null || smallTypeList.size() < 1) {
            binding.recyclertype.setVisibility(View.GONE);
        }

        //课程的内容
        binding.recycler.setLayoutManager(new NoScrollGridLayoutManager(mActivity, 3,false));
        courseAdapter = new CourseChooseAdapter(courseList, flag);
        courseAdapter.setVoaCallback(callback);
        binding.recycler.setAdapter(courseAdapter);

        //刷新数据操作
        binding.refreshLayout.setEnableRefresh(true);
        binding.refreshLayout.setEnableLoadMore(false);
        binding.refreshLayout.setRefreshHeader(new ClassicsHeader(getActivity()));
        binding.refreshLayout.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh(@NonNull RefreshLayout refreshLayout) {
                if (!NetworkUtil.isConnected(getActivity())){
                    ToastUtil.show(getActivity(),"请链接网络后获取数据");
                    return;
                }

                mPresenter.getSeriesList(catId);
            }
        });

        //判断网络处理
        if (NetStateUtil.isConnected(TalkShowApplication.getContext())) {
            startLoading("正在获取课程类型...");
            //获取类型数据
            mPresenter.chooseLessonNew();
            //获取课程数据
//            mPresenter.getSeriesList(catId);
        } else {
            ToastUtil.showToast(mContext, "选择课程需要打开数据网络。");
            mPresenter.chooseCourse(catId);
        }
    }

    //初始化数据处理(用于处理接口进行书籍增加或者减少时的显示)
    private void initFixDataShow(){
        // TODO: 2025/1/25 这里需要处理下，根据大类型的名称和小类型的id进行展示，不要使用位置处理
        //设置选中的位置显示
        String selectShowBigName = "";
        int selectShowSmallId = 0;
        int showSmallIndex = 0;
        int showBigIndex = 0;

        selectShowSmallId = mPresenter.getMargeCategory();

        bigType:for (String key:hashXiao.keySet()){
            List<TypeHolder> smallList = hashXiao.get(key);
            for (int i = 0; i < smallList.size(); i++) {
                int smallId = smallList.get(i).getId();
                if (smallId == selectShowSmallId){
                    selectShowBigName = key;

                    //设置小类型的位置
                    showSmallIndex = i;
                    break bigType;
                }
            }
        }
        //获取大类型的位置
        if (TextUtils.isEmpty(selectShowBigName)){
            showBigIndex = 0;
        }else {
            for (int i = 0; i < bigTypeList.size(); i++) {
                String bigTypeName = bigTypeList.get(i).getValue();
                if (bigTypeName.equals(selectShowBigName)){
                    showBigIndex = i;
                }
            }
        }

        LogUtil.d("测试大类型和小类型","大类型--"+showBigIndex+"，小类型："+showSmallIndex);

        //设置当前的位置，并且设置到列表上
        bigTypeAdapter.putActiveTitle(showBigIndex);
        smallTypeAdapter.putActiveType(showSmallIndex);
        //配置到数据中
        mPresenter.putMargeClass(showBigIndex);
        mPresenter.putMargeType(showSmallIndex);
    }

    //展示适用地区
    private void setTextArea(String catId) {
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

    //书籍排序操作
    private class SortBySeries implements Comparator {
        @Override
        public int compare(Object o1, Object o2) {
            SeriesData s1 = (SeriesData) o1;
            SeriesData s2 = (SeriesData) o2;

            //这里按照书籍的id进行排序，小的在前面，小的在后面
//            if (Integer.parseInt(s1.getId()) > Integer.parseInt(s2.getId())){
//                return  1;
//            }else {
//                return -1;
//            }

            // TODO: 2024/11/12 补充数据后重新处理下，李涛在中小学英语群里确定使用createTime进行处理，从小到大排序
            long firstTime = DateUtil.toDateLong(s1.getCreateTime(),DateUtil.YMDHMSS);
            long secondTime = DateUtil.toDateLong(s2.getCreateTime(),DateUtil.YMDHMSS);
            if (firstTime > secondTime){
                return 1;
            }else {
                return -1;
            }
        }
    }

    @Override
    public void setMoreCourse(List<SeriesData> beans) {
        if (beans == null || beans.size() < 1) {
            courseAdapter.setDataBeans(new ArrayList<>());
            return;
        }

        Collections.sort(beans, new SortBySeries());
        courseAdapter.setDataBeans(beans);

        binding.recycler.setVisibility(View.VISIBLE);
    }

    @Override
    public void setCoures(List<SeriesData> beans) {
        binding.refreshLayout.finishRefresh();

        if (beans == null || beans.size() < 1) {
            mPresenter.chooseCourse(catId);
            return;
        }

        Collections.sort(beans, new SortBySeries());
        courseAdapter.setDataBeans(beans);

        binding.recycler.setVisibility(View.VISIBLE);
    }

    @Override
    public void setCourseFail(String showMsg) {
        binding.refreshLayout.finishRefresh();
        ToastUtil.show(getActivity(),showMsg);
    }

    @Override
    public void setLesson(List<LessonNewResponse.Series> series) {
        stopLoading();
        if (series == null || series.size() < 1) {
            return;
        }

        setXiaoLesson(series);

        //获取当前的小类型id
        catId = getCurSmallTypeId();
        mPresenter.getSeriesList(catId);

        //设置适用地区
        setTextArea(catId);
    }

    @Override
    public void setLessonFail(String showMsg) {
        stopLoading();
        //刷新类型数据显示
        getLocalTypeData();
        //设置选中的位置信息
        initFixDataShow();
        //设置适用地区
        catId = getCurSmallTypeId();
        setTextArea(catId);

        if (smallTypeList!=null && smallTypeList.size()>0){
            smallTypeAdapter.SetCourseList(smallTypeList);
            smallTypeAdapter.notifyDataSetChanged();
            binding.recyclertype.setVisibility(View.VISIBLE);
        }
        if (bigTypeList!=null && bigTypeList.size()>0){
            bigTypeAdapter.SetTitleList(bigTypeList);
            bigTypeAdapter.notifyDataSetChanged();
            binding.recyclertitle.setVisibility(View.VISIBLE);
        }
    }

    private void setXiaoLesson(List<LessonNewResponse.Series> series) {
        categoryDao.deleteCategory(UserInfoManager.getInstance().getUserId());
        bigTypeList.clear();
        smallTypeList.clear();
        hashXiao.clear();

        for (LessonNewResponse.Series ser : series) {
            if (ser == null) {
                continue;
            }

            //人教版审核限制
            if (AbilityControlManager.getInstance().isLimitPep() && ser.SourceType.equals("人教版")) {
                continue;
            }

            for (LessonNewResponse.SeriesDatas seriesData : ser.SeriesData) {
                if (seriesData == null) {
                    continue;
                }

                if (hashXiao.containsKey(ser.SourceType)) {
                    List<TypeHolder> result = hashXiao.get(ser.SourceType);
                    result.add(new TypeHolder(seriesData.Category, seriesData.SeriesName));
                } else {
                    List<TypeHolder> result = new ArrayList<>();
                    result.add(new TypeHolder(seriesData.Category, seriesData.SeriesName));
                    hashXiao.put(ser.SourceType, result);
                }
                CategorySeries categorySer = categoryDao.getUidCategory(seriesData.Category, UserInfoManager.getInstance().getUserId());
                if (categorySer == null) {
                    categoryDao.saveCategory(new CategorySeries(seriesData.Category, seriesData.SeriesName, seriesData.lessonName, ser.SourceType, seriesData.isVideo, UserInfoManager.getInstance().getUserId()));
                } else {
                    categoryDao.updateCategory(new CategorySeries(seriesData.Category, seriesData.SeriesName, seriesData.lessonName, ser.SourceType, seriesData.isVideo, UserInfoManager.getInstance().getUserId()));
                }
            }
        }

        //筛选数据
        setXiaoTitle();
        setXiaoType();

        //增加初始化数据配置
        initFixDataShow();

        //刷新数据显示
        bigTypeAdapter.SetTitleList(bigTypeList);
        smallTypeAdapter.SetCourseList(smallTypeList);
        bigTypeAdapter.notifyDataSetChanged();
        smallTypeAdapter.notifyDataSetChanged();
        //显示界面
        binding.recyclertype.setVisibility(View.VISIBLE);
        binding.recyclertitle.setVisibility(View.VISIBLE);
    }

    private void setXiaoTitle() {
        int index = 0;
        if (hashXiao == null || hashXiao.size() < 1) {
            return;
        }

        //人教版审核限制
        if (AbilityControlManager.getInstance().isLimitPep()) {
            ++index;
        }

        if (hashXiao.containsKey("人教版")) {
            bigTypeList.add(new TypeHolder(TypeHelper.TYPE_PRIMARY_RENJIAO, "人教版"));
            ++index;
        }
        if (hashXiao.containsKey("北师版")) {
            bigTypeList.add(new TypeHolder(TypeHelper.TYPE_PRIMARY_BEISHI, "北师版"));
            ++index;
        }
        if (hashXiao.containsKey("冀教版")) {
            bigTypeList.add(new TypeHolder(TypeHelper.TYPE_PRIMARY_JIJIAO, "冀教版"));
            ++index;
        }
        if (hashXiao.containsKey("鲁教版")) {
            bigTypeList.add(new TypeHolder(TypeHelper.TYPE_PRIMARY_LUJIAO, "鲁教版"));
            ++index;
        }
        if (hashXiao.containsKey("新概念")) {
            bigTypeList.add(new TypeHolder(TypeHelper.TYPE_PRIMARY_CONCEPT, "新概念"));
            ++index;
        }
        if (hashXiao.containsKey("译林版")) {
            bigTypeList.add(new TypeHolder(TypeHelper.TYPE_PRIMARY_YILIN, "译林版"));
            ++index;
        }
        if (hashXiao.containsKey("仁爱版")) {
            bigTypeList.add(new TypeHolder(TypeHelper.TYPE_PRIMARY_RENAI, "仁爱版"));
            ++index;
        }

        /*for (String key : hashXiao.keySet()) {
            Log.e("ChooseCoursePresenter", "setXiaoTitle key " + key);
            switch (key) {
                case "人教版":
                case "北师版":
                case "北京版":
                    break;
                case "新概念":
                    if (App.APP_TENCENT_MOOC) {
                        bigTypeList.add(new TypeHolder(index, key));
                        ++index;
                    }
                    break;
                default:
                    bigTypeList.add(new TypeHolder(index, key));
                    ++index;
                    break;
            }
        }*/
    }

    private void setXiaoType() {
        if (hashXiao == null || hashXiao.size() < 1) {
            smallTypeList = TypeHelper.getDefaultSmallTypeList(mPresenter.getMargeClass());
            return;
        }

        smallTypeList.clear();
        int index = mPresenter.getMargeClass();

        //人教版审核限制
        if (AbilityControlManager.getInstance().isLimitPep() && hashXiao.keySet().size() == 3) {
            index += 1;
        }

        //如果id相同，则进行保存
//        for (String key : hashXiao.keySet()) {
//
//            //获取需要的小类型数据
//            for (TypeHolder typeHolder : bigTypeList) {
//                if ((index == typeHolder.getId()) && key.equals(typeHolder.getValue())) {
//                    smallTypeList.addAll(hashXiao.get(key));
//                    return;
//                }
//            }
//        }

        //根据位置进行处理
        if (index < bigTypeList.size() && index>=0){
            smallTypeList.addAll(hashXiao.get(bigTypeList.get(index).getValue()));
        }

        //如果没有数据，则默认显示第一个数据
        if (smallTypeList.size()<=0){
            smallTypeList.addAll(hashXiao.get(bigTypeList.get(0).getValue()));
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
            mPresenter.putMargeId(bookId, courseTitle);
            mPresenter.putMargeCategory(category);

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
            } else if (bookLevelDao != null) {
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

            //课程操作
            List<Voa> result = mPresenter.loadVoasByBookId(bookId);
            if (result == null || result.size() < count) {
                EventBus.getDefault().post(new SelectBookEvent(bookId, version, true));
            } else {
                EventBus.getDefault().post(new SelectBookEvent(bookId, version));
            }

            //判断选中的bookId和当前播放的bookId是否相同，不相同则关闭播放
            Voa tempVoa = PrimaryBgPlaySession.getInstance().getCurData();
            if (tempVoa!=null){
                int tempBookId = tempVoa.series();
                if (tempBookId != bookId){
                    EventBus.getDefault().post(new PrimaryBgPlayEvent(PrimaryBgPlayEvent.event_control_hide));
                }
            }
            //单词操作
            if (version == 0) {
                EventBus.getDefault().post(new RefreshBookEvent(bookId, version, true));
            } else {
                EventBus.getDefault().post(new RefreshBookEvent(bookId, version));
            }

            if (flag == OpenFlag.TO_DETAIL) {
                mActivity.startActivity(CourseDetailActivity.buildIntent(mActivity, bookId, mPresenter.getMargeTitle()));
            } else if (flag == OpenFlag.TO_WORD) {
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
            mPresenter.putMargeType(pos);

            catId = "" + id;
            mPresenter.getSeriesList(catId);
        }
    };

    CourseTitleAdapter.TitleCallback titleCallback = new CourseTitleAdapter.TitleCallback() {
        @Override
        public void onTitleChoose(int pos, int id) {
            mPresenter.putMargeClass(pos);

            setXiaoType();
            smallTypeAdapter.SetCourseList(smallTypeList);
            smallTypeAdapter.notifyDataSetChanged();

            //刷新适用地区
            setTextArea(catId);
        }
    };

    @Override
    public void init() {

    }

    //获取本地的类型数据
    private void getLocalTypeData(){
        List<CategorySeries> newTitle = categoryDao.getUidCategories(UserInfoManager.getInstance().getUserId());
        if ((newTitle == null) || (newTitle.size() < 1)) {
            bigTypeList = TypeHelper.getDefaultBigTypeList();
            smallTypeList = TypeHelper.getDefaultSmallTypeList(mPresenter.getMargeClass());
        } else {
            bigTypeList.clear();
            smallTypeList.clear();
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
    }

    //加载弹窗
    private LoadingDialog loadingDialog;

    private void startLoading(String showMsg){
        if (loadingDialog==null){
            loadingDialog = new LoadingDialog(getActivity());
            loadingDialog.create();
        }
        loadingDialog.setMsg(showMsg);
        loadingDialog.show();
    }

    private void stopLoading(){
        if (loadingDialog!=null && loadingDialog.isShowing()){
            loadingDialog.dismiss();
        }
    }

    //获取当前的小类型的id
    private String getCurSmallTypeId(){
        int showSmallTypeIndex = mPresenter.getMargeType();
        showSmallTypeIndex = Math.min(smallTypeList.size(),showSmallTypeIndex);
        return String.valueOf(smallTypeList.get(showSmallTypeIndex).getId());
    }
}