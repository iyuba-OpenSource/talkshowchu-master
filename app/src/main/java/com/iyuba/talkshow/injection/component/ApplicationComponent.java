package com.iyuba.talkshow.injection.component;

import android.app.Application;

import com.iyuba.talkshow.data.DataManager;
import com.iyuba.talkshow.data.SyncService;
import com.iyuba.talkshow.data.local.DatabaseHelper;
import com.iyuba.talkshow.data.local.PreferencesHelper;
import com.iyuba.talkshow.data.manager.AdManager;
import com.iyuba.talkshow.data.manager.ConfigManager;
import com.iyuba.talkshow.data.remote.CommentService;
import com.iyuba.talkshow.data.remote.EvalServiece;
import com.iyuba.talkshow.data.remote.FeedbackService;
import com.iyuba.talkshow.data.remote.LocationService;
import com.iyuba.talkshow.data.remote.LoopService;
import com.iyuba.talkshow.data.remote.OtherService;
import com.iyuba.talkshow.data.remote.PayService;
import com.iyuba.talkshow.data.remote.ThumbsService;
import com.iyuba.talkshow.data.remote.UserService;
import com.iyuba.talkshow.data.remote.VerifyCodeService;
import com.iyuba.talkshow.data.remote.VersionService;
import com.iyuba.talkshow.data.remote.VipService;
import com.iyuba.talkshow.data.remote.VoaService;
import com.iyuba.talkshow.injection.module.ApplicationModule;
import com.iyuba.talkshow.util.GetLocation;
import com.iyuba.talkshow.util.RxEventBus;
import com.iyuba.talkshow.util.VerifyCodeSmsObserver;

import javax.inject.Singleton;

import dagger.Component;

/**
 * Created by Administrator on 2016/11/11 0011.
 */

@Singleton
@Component(modules = ApplicationModule.class) // 这个类可以向外提供实例
public interface ApplicationComponent {
    void inject(SyncService syncService);
    Application application();
    VoaService voaService();
    LoopService lunboService();
    ThumbsService rankingService();
    FeedbackService feedBackService();
    CommentService commentService();
    UserService userService();
    PayService payService();
    VersionService versionService();
    VerifyCodeService verifyCodeService();
    VipService vipService();
    OtherService otherService();
    LocationService locationService();
    PreferencesHelper preferencesHelper();
    EvalServiece evalService();
    DatabaseHelper databaseHelper();
    DataManager dataManager();
//    AccountManager accountManager();
    ConfigManager configManager();
    AdManager adManager();
    RxEventBus eventBus();
    GetLocation getLocation();
    VerifyCodeSmsObserver verifyCodeSmsObserver();
//    ConvertManager convertManager();
}
