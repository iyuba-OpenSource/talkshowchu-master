package com.iyuba.talkshow.injection.module;

import android.app.Application;
import android.content.Context;

import com.iyuba.talkshow.data.remote.AdService;
import com.iyuba.talkshow.data.remote.CmsService;
import com.iyuba.talkshow.data.remote.CommentService;
import com.iyuba.talkshow.data.remote.EvalServiece;
import com.iyuba.talkshow.data.remote.FeedbackService;
import com.iyuba.talkshow.data.remote.IntegralService;
import com.iyuba.talkshow.data.remote.LocationService;
import com.iyuba.talkshow.data.remote.LoopService;
import com.iyuba.talkshow.data.remote.MovieService;
import com.iyuba.talkshow.data.remote.OtherService;
import com.iyuba.talkshow.data.remote.PayService;
import com.iyuba.talkshow.data.remote.PdfService;
import com.iyuba.talkshow.data.remote.RankingService;
import com.iyuba.talkshow.data.remote.ThumbsService;
import com.iyuba.talkshow.data.remote.TitleSeriesService;
import com.iyuba.talkshow.data.remote.UploadStudyRecordService;
import com.iyuba.talkshow.data.remote.UserService;
import com.iyuba.talkshow.data.remote.VerifyCodeService;
import com.iyuba.talkshow.data.remote.VersionService;
import com.iyuba.talkshow.data.remote.VipService;
import com.iyuba.talkshow.data.remote.VoaService;
import com.iyuba.talkshow.data.remote.WordCollectService;
import com.iyuba.talkshow.data.remote.WordService;
import com.iyuba.talkshow.data.remote.WordTestService;
import com.iyuba.talkshow.injection.ApplicationContext;
import com.iyuba.talkshow.util.GetLocation;
import com.iyuba.talkshow.util.VerifyCodeSmsObserver;

import javax.inject.Singleton;

import dagger.Module;
import dagger.Provides;

@Module
public class ApplicationModule {
    protected final Application mApplication;

    public ApplicationModule(Application application) {
        mApplication = application;
    }

    @Provides
    Application provideApplication() {
        return mApplication;
    }

    @Provides
    @ApplicationContext
    Context provideContext() {
        return mApplication;
    }

    @Provides
    @Singleton
    VoaService provideVoaService() {
        return VoaService.Creator.newVoaService();
    }

    @Provides
    @Singleton
    WordTestService provideWordTestServcie() {
        return WordTestService.Creator.newWordTestService();
    }


    @Provides
    @Singleton
    WordService provideWordService() {
        return WordService.Creator.newWordService();
    }

    @Provides
    @Singleton
    PdfService providePdfService() {
        return PdfService.Creator.newPdfServcie();
    }

    @Provides
    @Singleton
    WordCollectService provideWordCollectService() {
        return WordCollectService.Creator.newWordCollectService();
    }

    @Provides
    @Singleton
    LoopService provideLoopService() {
        return LoopService.Creator.newLoopService();
    }

    @Provides
    @Singleton
    MovieService provideMovieService() {
        return MovieService.Creator.newMovieService();
    }

    @Provides
    @Singleton
    ThumbsService provideRankingService() {
        return ThumbsService.Creator.newThumbsService();
    }

    @Provides
    @Singleton
    FeedbackService provideFeedbackService() {
        return FeedbackService.Creator.newFeedbackService();
    }

    @Provides
    @Singleton
    IntegralService provideIntegralService() {
        return IntegralService.Creator.newIntegralService();
    }

    @Provides
    @Singleton
    CommentService provideCommentService() {
        return CommentService.Creator.newCommentService();
    }

    @Provides
    @Singleton
    CmsService provideCmsService() {
        return CmsService.Creator.newCmsService();
    }

    @Provides
    @Singleton
    TitleSeriesService provideTitleSeriesService() {
        return TitleSeriesService.Creator.newTitleService();
    }

    @Provides
    @Singleton
    UserService provideUserService() {
        return UserService.Creator.newUserService();
    }

    @Provides
    @Singleton
    PayService providePayService() {
        return PayService.Creator.newPayService();
    }

    @Provides
    @Singleton
    VersionService provideVersionService() {
        return VersionService.Creator.newVersionService();
    }

    @Provides
    @Singleton
    VerifyCodeService provideVerifyCodeService() {
        return VerifyCodeService.Creator.newVerifyCodeService();
    }

    @Provides
    @Singleton
    LocationService provideLocationService() {
        return LocationService.Creator.newLocationService();
    }

    @Provides
    @Singleton
    VipService provideVipService() {
        return VipService.Creator.newVipService();
    }

    @Provides
    @Singleton
    EvalServiece provideEvalService() {
        return EvalServiece.Creator.newCommentService();
    }

    @Provides
    @Singleton
    UploadStudyRecordService provideUploadStudyRecordService() {
        return UploadStudyRecordService.Creator.newUploadStudyRecordService();
    }

    @Provides
    @Singleton
    RankingService provideHomeRankingService() {
        return RankingService.Creator.newRankingService();
    }

    @Provides
    @Singleton
    AdService provideAdService() {
        return AdService.Creator.newAdService();
    }

    @Provides
    @Singleton
    OtherService provideOtherService() {
        return OtherService.Creator.newOtherService();
    }

    @Provides
    @Singleton
    GetLocation provideGetLocation() {
        return GetLocation.getInstance();
    }

    @Provides
    @Singleton
    VerifyCodeSmsObserver provideVerifyCodeSmsObserver() {
        return VerifyCodeSmsObserver.getInstance(mApplication.getContentResolver());
    }

//    @Provides
//    @Singleton
//    ConvertManager provideConvertManager() {
//        return ConvertManager.getInstance(mApplication);
//    }

}
