package com.iyuba.talkshow.util;

import android.content.Context;

import com.bun.miitmdid.core.InfoCode;
import com.bun.miitmdid.core.MdidSdkHelper;
import com.bun.miitmdid.interfaces.IIdentifierListener;
import com.bun.miitmdid.interfaces.IdSupplier;
import com.iyuba.talkshow.BuildConfig;
import com.iyuba.talkshow.constant.App;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;

/**
 * 新的id数据获取
 *
 * 使用oaid2.0.0版本
 */
public class OAID2Helper {

    //证书文件名称
    private static final String PEM_NAME = App.oaid_pem;
    //回调接口
    public OnIdCallListener onIdCallListener;
    //是否初始化过证书
    private boolean isInitCert = false;
    //是否显示日志
    private boolean isSDKLog = BuildConfig.DEBUG;

    private static OAID2Helper instance;

    public static OAID2Helper getInstance(OnIdCallListener onIdCallListener){
        if (instance==null){
            synchronized (OAID2Helper.class){
                if (instance==null){
                    instance = new OAID2Helper(onIdCallListener);
                }
            }
        }
        return instance;
    }


    //构造器
    public OAID2Helper(OnIdCallListener onIdCallListener){
        this.onIdCallListener = onIdCallListener;
    }

    //初始化sdk
    public static void initSdk(){
        System.loadLibrary("msaoaidsec");
    }

    //获取id
    public void getIds(Context context){
        if (!isInitCert){
            try {
                isInitCert = MdidSdkHelper.InitCert(context,loadPemFromAssetFile(context,PEM_NAME));
            }catch (Exception e){
                e.printStackTrace();
            }
        }

        try {
            MdidSdkHelper.setGlobalTimeout(5000);
        }catch (Exception e){
            e.printStackTrace();
        }

        int code = 0;
        try {
            code = MdidSdkHelper.InitSdk(context,isSDKLog,true,true,true,iIdentifierListener);
        }catch (Exception e){
            e.printStackTrace();
        }

        if (code!= InfoCode.INIT_INFO_RESULT_DELAY&&code!=InfoCode.INIT_INFO_RESULT_OK){
            onIdCallListener.onFail(code,showError(code));
        }
    }

    //从assets中加载证书
    private String loadPemFromAssetFile(Context context, String assetFileName){
        try {
            InputStream is = context.getAssets().open(assetFileName);
            BufferedReader in = new BufferedReader(new InputStreamReader(is));
            StringBuilder builder = new StringBuilder();
            String line;
            while ((line = in.readLine()) != null){
                builder.append(line);
                builder.append('\n');
            }
            return builder.toString();
        } catch (IOException e) {
            return "";
        }
    }

    //数据信息回调（正确数据直接回调，不用处理）
    private IIdentifierListener iIdentifierListener = new IIdentifierListener() {
        @Override
        public void onSupport(IdSupplier idSupplier) {
            if (idSupplier==null||onIdCallListener==null){
                onIdCallListener.onFail(-1,showError(-1));
                return;
            }

            boolean isSupport = idSupplier.isSupported();
            boolean isLimit = idSupplier.isLimited();
            String oaID = idSupplier.getOAID();
            String vaId = idSupplier.getVAID();
            String aaId = idSupplier.getAAID();

            if (isSupport&&!isLimit){
                onIdCallListener.OnSuccess(oaID,vaId,aaId);
            }else {
                onIdCallListener.onFail(-1,showError(-1));
            }
        }
    };

    //回调接口
    public interface OnIdCallListener{
        //成功
        void OnSuccess(String oaId,String vaId,String aaId);

        //失败
        void onFail(int code,String msg);
    }


    //获取错误信息显示
    private String showError(int code){
        switch (code){
            case InfoCode.INIT_ERROR_CERT_ERROR:
                return "证书未初始化或证书无效";
            case InfoCode.INIT_ERROR_DEVICE_NOSUPPORT:
                return "不支持的设备";
            case InfoCode.INIT_ERROR_LOAD_CONFIGFILE:
                return "加载配置文件出错";
            case InfoCode.INIT_ERROR_MANUFACTURER_NOSUPPORT:
                return "不支持的设备厂商";
            case InfoCode.INIT_ERROR_SDK_CALL_ERROR:
                return "sdk调用出错";
        }
        return "未知错误";
    }
}
