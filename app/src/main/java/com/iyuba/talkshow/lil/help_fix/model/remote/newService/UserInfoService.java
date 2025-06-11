package com.iyuba.talkshow.lil.help_fix.model.remote.newService;

import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Login_account;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Mob_verify;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.User_info;

import io.reactivex.Observable;
import retrofit2.http.GET;
import retrofit2.http.Query;
import retrofit2.http.Url;

/**
 * @title:
 * @date: 2023/11/3 13:51
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public interface UserInfoService {

    //获取用户信息
    //http://api.iyuba.com.cn/v2/api.iyuba?protocol=20001&myid=13865961&appid=260&format=json&sign=4603fde5e47c4c44628e30912817d8e5&id=13865961&platform=android
    @GET()
    Observable<User_info> getUserInfo(@Url String url,
                                      @Query("protocol") int protocol,
                                      @Query("appid") int appId,
                                      @Query("myid") long uid,
                                      @Query("id") long searchUserId,
                                      @Query("format") String format,
                                      @Query("sign") String sign,
                                      @Query("platform") String platform);

    //账号登录
    //http://api.iyuba.com.cn/v2/api.iyuba?protocol=11001&password=d993e6acf1d43e02f4dc71818dbf9adc&appid=260&x=0.0&sign=b57be113fac7e1535e33c156308b240d&format=json&y=0.0&username=aiyuba_lil
    @GET()
    Observable<Login_account> loginByAccount(@Url String url,
                                             @Query("protocol") int protocol,
                                             @Query("appid") int appId,
                                             @Query("x") String longitude,
                                             @Query("y") String latitude,
                                             @Query("format") String format,
                                             @Query("username") String userName,
                                             @Query("password") String password,
                                             @Query("sign") String sign);

    //mob登录
    @GET
    Observable<Mob_verify> loginByMob(@Url String url,
                                      @Query("protocol") int protocol,
                                      @Query("token") String token,
                                      @Query("opToken") String opToken,
                                      @Query("operator") String operator,
                                      @Query("appId") int appId,
                                      @Query("appkey") String mobKey);

}
