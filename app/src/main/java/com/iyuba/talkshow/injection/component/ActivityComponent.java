package com.iyuba.talkshow.injection.component;

import com.iyuba.talkshow.injection.PerActivity;
import com.iyuba.talkshow.injection.module.ActivityModule;
import com.iyuba.talkshow.lil.help_fix.ui.me_wallet.WalletListActivity;
import com.iyuba.talkshow.lil.help_fix.ui.preSaveData.PreSaveDataActivity;
import com.iyuba.talkshow.lil.junior.choose.JuniorChooseActivity;
import com.iyuba.talkshow.newce.ContainActivity;
import com.iyuba.talkshow.newce.ContianerActivity;
import com.iyuba.talkshow.newce.comment.CommentActivity;
import com.iyuba.talkshow.newce.search.newSearch.NewSearchActivity;
import com.iyuba.talkshow.newce.study.StudyActivity;
import com.iyuba.talkshow.newce.study.dubbingNew.DubbingAboutActivity;
import com.iyuba.talkshow.ui.PreviewBookActivity;
import com.iyuba.talkshow.ui.about.AboutActivity;
import com.iyuba.talkshow.ui.courses.coursechoose.CourseChooseActivity;
import com.iyuba.talkshow.ui.courses.coursechoose.CourseKouActivity;
import com.iyuba.talkshow.ui.courses.coursedetail.CourseDetailActivity;
import com.iyuba.talkshow.ui.deletlesson.LessonDeleteActivity;
import com.iyuba.talkshow.ui.detail.DetailActivity;
import com.iyuba.talkshow.ui.detail.ranking.watch.WatchDubbingActivity;
import com.iyuba.talkshow.ui.dubbing.DubbingActivity;
import com.iyuba.talkshow.ui.feedback.FeedbackActivity;
import com.iyuba.talkshow.ui.preview.PreviewActivity;
import com.iyuba.talkshow.ui.rank.RankActivity;
import com.iyuba.talkshow.ui.rank.dubbing.DubbingListActivity;
import com.iyuba.talkshow.ui.sign.SignActivity;
import com.iyuba.talkshow.ui.user.detail.ShowUserInfoActivity;
import com.iyuba.talkshow.ui.user.download.DownloadActivity;
import com.iyuba.talkshow.ui.user.edit.EditUserInfoActivity;
import com.iyuba.talkshow.ui.user.edit.ImproveUserActivity;
import com.iyuba.talkshow.ui.user.image.UploadImageActivity;
import com.iyuba.talkshow.ui.user.login.changeName.ChangeNameActivity;
import com.iyuba.talkshow.ui.user.me.CalendarActivity;
import com.iyuba.talkshow.ui.user.me.ClockInfoActivity;
import com.iyuba.talkshow.ui.user.me.LocalActivity;
import com.iyuba.talkshow.ui.user.me.MeActivity;
import com.iyuba.talkshow.ui.user.me.SyncActivity;
import com.iyuba.talkshow.ui.user.register.email.RegisterActivity;
import com.iyuba.talkshow.ui.user.register.phone.RegisterByPhoneActivity;
import com.iyuba.talkshow.ui.user.register.submit.RegisterSubmitActivity;
import com.iyuba.talkshow.ui.vip.buyiyubi.BuyIyubiActivity;
import com.iyuba.talkshow.ui.vip.buyvip.NewVipCenterActivity;
import com.iyuba.talkshow.ui.vip.payorder.PayOrderActivity;
import com.iyuba.talkshow.ui.web.OfficialActivity;
import com.iyuba.talkshow.ui.welcome.WelcomeActivity;
import com.iyuba.talkshow.ui.words.WordNoteActivity;

import dagger.Subcomponent;

/**
 * This component inject dependencies to all Activities across the application
 */
@PerActivity
@Subcomponent(modules = ActivityModule.class)
public interface ActivityComponent {

    void inject(ContianerActivity containerActivity);
    void inject(ContainActivity containActivity);
    void inject(StudyActivity studyActivity);
    void inject(CommentActivity commentActivity);

    void inject(DubbingListActivity dubbingListActivity);

    void inject(RankActivity rankActivity);

    void inject(DetailActivity detailActivity);

    void inject(DubbingActivity dubbingActivity);

    void inject(PreviewActivity previewActivity);

    void inject(WatchDubbingActivity watchDubbingActivity);

//    void inject(LoginActivity loginActivity);
    void inject(ChangeNameActivity userNameActivity);

    void inject(MeActivity meActivity);
    void inject(LocalActivity localActivity);
    void inject(SyncActivity syncActivity);
    void inject(ClockInfoActivity daInfoActivity);
    void inject(CalendarActivity calendarActivity);

    void inject(WordNoteActivity wordNoteActivity);

    void inject(RegisterActivity registerActivity);

    void inject(RegisterByPhoneActivity registerByPhoneActivity);

    void inject(RegisterSubmitActivity registerSubmitActivity);

    void inject(PayOrderActivity payOrderActivity);

    void inject(WelcomeActivity welcomeActivity);

    void inject(AboutActivity aboutActivity);

    void inject(CourseDetailActivity aboutActivity);

    void inject(FeedbackActivity feedbackActivity);

    void inject(ShowUserInfoActivity showUserInfoActivity);

    void inject(EditUserInfoActivity editUserInfoActivity);
    void inject(ImproveUserActivity improveUserActivity);

    void inject(UploadImageActivity uploadImageActivity);

//    void inject(CollectionActivity collectionActivity);
    void inject(OfficialActivity officialActivity);

    void inject(DownloadActivity downloadActivity);

    void inject(BuyIyubiActivity buyIyubiActivity);

    void inject(SignActivity signActivity);

    void inject(CourseChooseActivity courseChooseActivity);
    void inject(CourseKouActivity courseKouActivity);

//    void inject(WXPayEntryActivity wxPayEntryActivity);

    void inject(NewVipCenterActivity newVipCenterActivity);

    void inject(LessonDeleteActivity mainActivity);


    FragmentComponent fragmentComponent();

    DialogComponent dialogComponent();
    //配音-关于
    void inject(DubbingAboutActivity aboutActivity);
    //图书预览
    void inject(PreviewBookActivity bookActivity);
    //混合登录界面(秒验、小程序和账号登录)
//    void inject(FixLoginActivity fixLoginActivity);
    //钱包历史记录列表
    void inject(WalletListActivity walletListActivity);
    //数据预存界面
    void inject(PreSaveDataActivity preSaveDataActivity);
    //新的搜索界面
    void inject(NewSearchActivity newSearchActivity);

    /**********************中小学******************/
    //选书界面
    void inject(JuniorChooseActivity chooseActivity);
}
