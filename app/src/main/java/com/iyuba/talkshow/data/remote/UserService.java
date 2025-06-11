package com.iyuba.talkshow.data.remote;

import static me.jessyan.retrofiturlmanager.RetrofitUrlManager.DOMAIN_NAME_HEADER;

import com.google.gson.Gson;
import com.google.gson.GsonBuilder;
import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.data.HttpUtil;
import com.iyuba.talkshow.data.model.UserData;
import com.iyuba.talkshow.data.model.result.ChangeNameResponse;
import com.iyuba.talkshow.data.model.result.EditUserBasicInfoResponse;
import com.iyuba.talkshow.data.model.result.GetUserBasicInfoResponse;
import com.iyuba.talkshow.data.model.result.GetUserResponse;
import com.iyuba.talkshow.data.model.result.LoginResponse;
import com.iyuba.talkshow.data.model.result.RegisterMobResponse;
import com.iyuba.talkshow.data.model.result.RegisterResponse;
import com.iyuba.talkshow.data.model.result.UploadImageResponse;
import com.iyuba.talkshow.ui.user.edit.ImproveUserResponse;
import com.iyuba.talkshow.ui.user.login.TokenBean;
import com.iyuba.talkshow.ui.user.login.UidBean;
import com.iyuba.talkshow.util.MyGsonTypeAdapterFactory;

import java.util.Map;

import okhttp3.RequestBody;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.adapter.rxjava.RxJavaCallAdapterFactory;
import retrofit2.converter.gson.GsonConverterFactory;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.Multipart;
import retrofit2.http.POST;
import retrofit2.http.Part;
import retrofit2.http.Query;
import retrofit2.http.QueryMap;
import rx.Observable;

/**
 * Created by Administrator on 2016/12/14/014.
 */

public interface UserService {
    String ENDPOINT = "http://api.iyuba.com.cn/v2/";

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @GET("api.iyuba?")
    Observable<LoginResponse> login(@QueryMap Map<String, String> params);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @GET("api.iyuba?protocol=11001&username=diane258&password=f45aa9d63e00a043ac8ca061cf199a77&x=0.0&y=0.0&appId=201&sign=b39505be908d0b10696e4744c451f0a3&format=json")
    Observable<LoginResponse> login();

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @GET("api.iyuba?")
    Observable<ClearUserResponse> clearUser(@Query("protocol") int protocol ,
                                        @Query("username") String username,
                                        @Query("password") String password,
                                        @Query("sign") String sign,
                                        @Query("format") String format);
//	965eb72c92a549dd
//    http://api.iyuba.com.cn/v2/api.iyuba?protocol=11005&username=iyuba11&password=92a7f6dfc64&format=json&sign=d68b92a7f6dfc6495604e8dc7c8021ca

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @GET("api.iyuba?")
    Observable<GetUserResponse> getUser(@Query("protocol") int protocol,
                                        @Query("username") String username,
                                        @Query("appid") int appId);
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @GET("api.iyuba?")
    Observable<UserData> userInfoApi(@QueryMap Map<String, String> params);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @GET("api.iyuba?")
    Observable<RegisterResponse> registerByEmail(@QueryMap Map<String, String> params);

    //http://api.iyuba.com.cn/v2/api.iyuba?platform=android&app=meiyu&protocol=11002
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @GET("api.iyuba?")
    Observable<RegisterResponse> registerByPhone(@QueryMap Map<String, String> params);
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @POST("api.iyuba?")
    Observable<RegisterMobResponse> registerByMob(@QueryMap Map<String, String> params);
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @POST("api.iyuba?")
    Observable<ChangeNameResponse> ChangeUserName(@QueryMap Map<String, String> params);

    //http://api.iyuba.com.cn/v2/avatar?uid=;
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @Multipart
    @POST("avatar?")
    Observable<UploadImageResponse> uploadImage(@Query("uid") int uid,
                                                @Part("file\"; filename=\"photo.jpg") RequestBody body);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @GET("api.iyuba?")
    Observable<GetUserBasicInfoResponse> getUserBasicInfo(@QueryMap Map<String, String> params);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @POST("api.iyuba?")
    Observable<EditUserBasicInfoResponse> editUserBasicInfo(@QueryMap Map<String, String> params);

    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @POST("api.iyuba?")
    Observable<ImproveUserResponse> improveUserInfo(@QueryMap Map<String, String> params);


    //获取token
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @GET("api.iyuba")
    Observable<TokenBean> getToken(@Query("platform") String platform,
                                   @Query("format") String json,
                                   @Query("protocol") int protocol,
                                   @Query("appid") int appid,
                                   @Query("sign") String sign);

    //获取uid
    @Headers({DOMAIN_NAME_HEADER + Constant.DOMAIN_LONG_API})
    @GET("api.iyuba")
    Observable<UidBean> getUid(@Query("platform") String platform,
                               @Query("format") String format,
                               @Query("protocol") int protocol,
                               @Query("token") String token,
                               @Query("appid") int appid,
                               @Query("sign") String sign);

    interface Login {
        interface Param {
            interface Key {
                String PROTOCOL = "protocol";
                String USERNAME = "username";
                String PASSWORD = "password";
                String LONGITUDE = "x";
                String LATITUDE = "y";
                String APP_ID = "appId";
                //登陆时这里是小写
                String APP_ID2 = "appid";
                String SIGN = "sign";
                String FORMAT = "format";
            }

            interface Value {
                int PROTOCOL = 11001;
                String FORMAT = "json";
            }
        }

        interface Result {
            interface Code {
                int SUCCESS = 101;
                int USERNAME_NOT_EXIST = 102;
                int NOT_MATCHING = 103;
                int VIP = 1;
                int YEAR_OF_LIFELONG = 2099;
            }

            interface Message {
                String USERNAME_NOT_EXIST = "该用户名不存在";
                String NOT_MATCHING = "密码错误";
                String LOGIN_FAILURE = "登录失败";
                String LOGIN_SERVER = "服务器异常，请稍后重试";
            }
        }
    }

    interface GetUser {
        interface Param {
            interface Value {
                int PROTOCOL = 10009;
            }
        }
    }

    interface EditUser {
        interface Result {
            int SUCCESS = 221;
        }
    }

    interface Register {
        interface Param {
            interface Key {
                String PROTOCOL = "protocol";
                String APP = "app";
                String EMAIL = "email";
                String MOBILE = "mobile";
                String USERNAME = "username";
                String PASSWORD = "password";
                String PLATFORM = "platform";
                String FORMAT = "format";
                String SIGN = "sign";
            }

            interface Value {
                int PROTOCOL = 11002;
                String FORMAT = "json";
            }
        }

        interface Result {
            interface Code {
                int SUCCESS = 111;
                int USERNAME_EXIST = 112;
                int EMAIL_EXIST = 113;
                int PHONE_REGISTER = 115;
            }

            interface Message {
                String USERNAME_EXIST = "用户名已存在,请重新填写!";
                String EMAIL_REGISTERED = "邮箱已经被注册";
                String PHONE_REGISTERED = "手机号已注册，请使用手机号登录";
                String REGISTER_FAIL = "注册失败...请重试";
            }
        }
    }

    class UploadImage {
        public static class Part {
            private static final String FILENAME_PREFIX = "file\"; filename=\"";

            public static String getFilePartKey() {
                return FILENAME_PREFIX + System.currentTimeMillis() + ".jpg";
            }
        }

        public interface Result {
            interface Code {
                int SUCCESS = 0;
            }
        }
    }

    interface GetUserBasicInfo {
        interface Param {
            interface Key {
                String PROTOCOL = "protocol";
                String PLATFORM = "platform";
                String FORMAT = "format";
                String ID = "id";
                String SIGN = "sign";
            }

            interface Value {
                String PROTOCOL = "20002";
                String FORMAT = "json";
            }
        }

        class Result {
            public interface Code {
                int SUCCESS = 211;

                String SECRET = "0";
                String MALE = "1";
                String FEMALE = "2";
            }

            public interface Message {
                String SECRET = "保密";
                String MALE = "男";
                String FEMALE = "女";
            }

            public static String getMessageByCode(String code) {
                String message;
                switch (code) {
                    case Code.MALE:
                        message = Message.MALE;
                        break;
                    case Code.FEMALE:
                        message = Message.FEMALE;
                        break;
                    default:
                        message = Message.SECRET;
                        break;
                }
                return message;
            }

            public static String getCodeByMessage(String message) {
                String code;
                switch (message) {
                    case Message.MALE:
                        code = Code.MALE;
                        break;
                    case Message.FEMALE:
                        code = Code.FEMALE;
                        break;
                    default:
                        code = Code.SECRET;
                        break;
                }
                return code;
            }
        }
    }

    interface EditUserBasicInfo {
        interface Param {
            String COMMA = ",";
            String SEP = "-";
            String SPACE = " ";
            int SIZE_THREE = 3;

            class Key {
                public static final String PALTFORM = "platform";
                public static final String FORMAT = "format";
                public static final String PROTOCOL = "protocol";
                public static final String ID = "id";
                public static final String SIGN = "sign";
                public static final String KEY = "key";
                public static final String VALUE = "value";
                private static final String GENDER = "gender";
                private static final String BIRTH_YEAR = "birthyear";
                private static final String BIRTH_MONTH = "birthmonth";
                private static final String BIRTH_DAY = "birthday";
                private static final String CONSTELLATION = "constellation";
                private static final String ZODIAC = "zodiac";
                private static final String GRADUATE_SCHOOL = "graduateschool";
                private static final String RESIDE_PROVICE = "resideprovince";
                private static final String RESIDE_CITY = "residecity";
                private static final String RESIDE_DIST = "residedist";

                //key = "mGenderTv,birthyear,birthmonth,mBirthdayTv,mConstellationTv,mZodiacTv,graduateschool,resideprovince,residecity,residedist";
                private static String getBaseKey() {
                    return GENDER + COMMA + BIRTH_YEAR + COMMA + BIRTH_MONTH + COMMA
                            + BIRTH_DAY + COMMA + CONSTELLATION + COMMA + ZODIAC + COMMA
                            + GRADUATE_SCHOOL;
                }

                public static String getLocationSizeOnekey() {
                    return getBaseKey() + COMMA + RESIDE_CITY;
                }

                public static String getLocationSizeTwoKey() {
                    return getBaseKey() + COMMA + RESIDE_PROVICE + COMMA + RESIDE_CITY;
                }

                public static String getLocationSizeThreeKey() {
                    return getLocationSizeTwoKey() + COMMA + RESIDE_DIST;
                }
            }

            interface Value {
                String FORMAT = "json";
                String PROTOCOL = "20003";
            }
        }
    }

    /******** Helper class that sets up a new services *******/
    class Creator {

        public static UserService newUserService() {
            Gson gson = new GsonBuilder()
                    .registerTypeAdapterFactory(MyGsonTypeAdapterFactory.create())
                    .setDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'")
                    .create();
            Retrofit retrofit = new Retrofit.Builder()
                    .addConverterFactory(GsonConverterFactory.create(gson))
                    .client(HttpUtil.getOkHttpClient())
                    .addCallAdapterFactory(RxJavaCallAdapterFactory.create())
                    .baseUrl(UserService.ENDPOINT)
                    .build();
            return retrofit.create(UserService.class);
        }
    }
}
