package com.iyuba.talkshow.lil.help_fix.manager;

import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.constant.ConfigData;
import com.iyuba.talkshow.lil.help_fix.model.remote.RemoteManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Login_account;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Mob_verify;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.User_info;
import com.iyuba.talkshow.lil.help_fix.model.remote.newService.UserInfoService;
import com.iyuba.talkshow.lil.help_mvp.util.EncodeUtil;

import io.reactivex.Observable;

/**
 * @title: 用户数据管理
 * @date: 2023/12/1 14:19
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class UserDataManager {

    //获取用户信息-20001
    public static Observable<User_info> getUserInfo(long userId){
        String url = "http://api."+ NetHostManager.getInstance().getDomainLong() +"/v2/api.iyuba";

        int protocol = 20001;
        int appId = App.APP_ID;
        String format = "json";
        String platform = "android";

        String sign = EncodeUtil.md5(protocol+""+userId+"iyubaV2");

        UserInfoService infoService = RemoteManager.getInstance().createJson(UserInfoService.class);
        return infoService.getUserInfo(url,protocol,appId,userId,userId,format,sign,platform);
    }

    //账号登录-11001
    public static Observable<Login_account> loginByAccount(String userName, String password){
        //http://api.iyuba.com.cn/v2/api.iyuba
        String url = "http://api."+NetHostManager.getInstance().getDomainLong()+"/v2/api.iyuba";

        int protocol = 11001;
        String longitude = "";
        String latitude = "";
        int appId = App.APP_ID;
        String format = "json";
        String sign = EncodeUtil.md5(protocol+userName+ EncodeUtil.md5(password)+"iyubaV2");

        userName = EncodeUtil.encode(userName);
        password = EncodeUtil.md5(password);

        UserInfoService infoService = RemoteManager.getInstance().createJson(UserInfoService.class);
        return infoService.loginByAccount(url,protocol,appId,longitude,latitude,format,userName,password,sign);
    }

    //秒验查询用户信息-10010
    public static Observable<Mob_verify> mobVerifyFromServer(String token, String opToken, String operator){
        String url = "http://api."+NetHostManager.getInstance().getDomainLong()+"/v2/api.iyuba";

        int protocol = 10010;
        int appId = App.APP_ID;
        String mobKey = ConfigData.mob_key;
        token = EncodeUtil.encode(token);

        UserInfoService infoService = RemoteManager.getInstance().createJson(UserInfoService.class);
        return infoService.loginByMob(url,protocol,token,opToken,operator,appId,mobKey);
    }
}
