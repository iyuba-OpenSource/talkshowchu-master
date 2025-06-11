package com.iyuba.talkshow.newdata;

import android.os.Handler;
import android.os.Message;
import android.util.Log;

import com.iyuba.talkshow.lil.help_fix.data.library.StrLibrary;
import com.iyuba.talkshow.lil.help_fix.model.remote.RemoteManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.base.BaseBean_data;
import com.iyuba.talkshow.lil.help_fix.model.remote.newService.OldService;
import com.iyuba.talkshow.lil.help_mvp.util.gson.GsonUtil;
import com.iyuba.talkshow.util.RxUtil;

import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.Map;
import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;
import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Headers;
import okhttp3.MediaType;
import okhttp3.MultipartBody;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.RequestBody;
import okhttp3.Response;

/**
 * Created by carl shen on 2020/7/28
 * New Primary English, new study experience.
 */
public class EvaluateRequset {
    /*public void post(String actionUrl, Map<String, String> params, String filePath, final Handler handler) throws Exception {

        //POST参数构造MultipartBody.Builder，表单提交
        final OkHttpClient okHttpClient = new OkHttpClient().newBuilder().
                connectTimeout(7, TimeUnit.SECONDS).
                readTimeout(15, TimeUnit.SECONDS).
                writeTimeout(15, TimeUnit.SECONDS)
                .build();
        //一：文本类的
        MultipartBody.Builder urlBuilder = new MultipartBody.Builder().setType(MultipartBody.FORM);
        if (params != null) {
            for (String key : params.keySet()) {
                if (params.get(key) != null) {
                    urlBuilder.addFormDataPart(key, params.get(key));
                }
            }
        }
        //二种：文件请求体
        MediaType type = MediaType.parse("application/octet-stream");//"text/xml;charset=utf-8"
        File file1 = new File(filePath);
        RequestBody fileBody = RequestBody.create(type, file1);
        urlBuilder.addPart(Headers.of("Content-Disposition", "form-data; name=\"file\"; filename=\"" + filePath + "\""), fileBody);

        // 构造Request->call->执行
        final Request request = new Request.Builder().headers(new Headers.Builder().build())//extraHeaders 是用户添加头
                .url(actionUrl).post(urlBuilder.build())//参数放在body体里
                .build();
        okHttpClient.newCall(request).enqueue(new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
                if (e != null) {
                    Log.e("sendRank", "onFailure " + e.getMessage());
                } else {
                    Log.e("sendRank", "onFailure!!");
                }
                handler.sendEmptyMessage(14);
            }

            @Override
            public void onResponse(Call call, Response response) {
                if (response == null) {
                    Log.e("sendRank", "response null. " );
                    handler.sendEmptyMessage(14);
                    return;
                }
                if (response.isSuccessful()) {
                    JSONObject jsonObject = null;
                    try {
                        jsonObject = new JSONObject(response.body().string());
                        String result = jsonObject.optString("result");
                        Log.e("sendRank", jsonObject.toString());
                        if ("0".equals(result)) {
                            handler.sendEmptyMessage(14);

                        } else {
                            JSONObject data = jsonObject.getJSONObject("data");

                            if (data.toString() != null) {
                                Message message = new Message();
                                message.what = 15;
                                message.obj = data.toString();
                                handler.sendMessage(message);
                            } else {
                                handler.sendEmptyMessage(14);
                            }
                        }

                    } catch (Exception e) {
                        e.printStackTrace();
                        handler.sendEmptyMessage(14);
                    }

                } else {
                    Log.e("sendRank", "onResponse = " + response);
                    handler.sendEmptyMessage(14);
                }

            }
        });
    }*/

    private static EvaluateRequset instance;
    private Disposable evalSubmitDis;

    public static EvaluateRequset getInstance(){
        if (instance==null){
            synchronized (EvaluateRequset.class){
                if (instance==null){
                    instance = new EvaluateRequset();
                }
            }
        }
        return instance;
    }

    public void post(String actionUrl, Map<String, String> params, String filePath, final Handler handler) throws Exception{
        stopEval();
        evalSentence(actionUrl,params,filePath)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<BaseBean_data<EvaluateBean>>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        evalSubmitDis = d;
                    }

                    @Override
                    public void onNext(BaseBean_data<EvaluateBean> bean) {
                        if (bean==null){
                            handler.sendEmptyMessage(14);
                            return;
                        }

                        if (!bean.getResult().equals("1")){
                            handler.sendEmptyMessage(14);
                            return;
                        }

                        if (bean.getData()==null){
                            handler.sendEmptyMessage(14);
                            return;
                        }

                        Message message = new Message();
                        message.what = 15;
                        message.obj = GsonUtil.toJson(bean.getData());
                        handler.sendMessage(message);
                    }

                    @Override
                    public void onError(Throwable e) {
                        handler.sendEmptyMessage(14);
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //停止评测
    public void stopEval(){
        RxUtil.unsubscribe(evalSubmitDis);
    }

    /**********************************************功能接口*******************************/
    //评测功能接口
    private Observable<BaseBean_data<EvaluateBean>> evalSentence(String url,Map<String,String> map,String filePath){
        //设置数据头部显示
        File file = new File(filePath);
        RequestBody fileBody = MultipartBody.create(MediaType.parse("application/octet-stream"),file);
        MultipartBody.Builder builder = new MultipartBody.Builder()
                .setType(MultipartBody.FORM);

        //设置数据显示
        for (String key:map.keySet()){
            builder.addFormDataPart(key,map.get(key));
        }
        builder.addFormDataPart("file",file.getName(),fileBody);
        MultipartBody body = builder.build();

        //请求数据
        OldService oldService = RemoteManager.getInstance().createJson(OldService.class);
        return oldService.evalSentence(url,body);
    }
}
