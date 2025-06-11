package com.iyuba.wordtest.ui.listen.singleWrite.model;

import com.google.android.gms.tasks.Task;
import com.google.mlkit.common.model.DownloadConditions;
import com.google.mlkit.common.model.RemoteModelManager;
import com.google.mlkit.vision.digitalink.DigitalInkRecognition;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModel;
import com.google.mlkit.vision.digitalink.DigitalInkRecognitionModelIdentifier;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizer;
import com.google.mlkit.vision.digitalink.DigitalInkRecognizerOptions;
import com.google.mlkit.vision.digitalink.Ink;
import com.google.mlkit.vision.digitalink.RecognitionContext;
import com.google.mlkit.vision.digitalink.RecognitionResult;

/**
 * 谷歌机器学习管理类
 *
 * 手写文字识别
 */
public class MLKitManager {
    private static MLKitManager instance;
    public static MLKitManager getInstance(){
        if (instance==null){
            synchronized (MLKitManager.class){
                if (instance==null){
                    instance = new MLKitManager();
                }
            }
        }
        return instance;
    }

    private RemoteModelManager modelManager;
    private DigitalInkRecognitionModel digitalModel;
    private DigitalInkRecognitionModelIdentifier digitalIdentifier;
    private DigitalInkRecognizer digitalRecognizer;

    public MLKitManager(){
        modelManager = RemoteModelManager.getInstance();
        try {
            digitalIdentifier = DigitalInkRecognitionModelIdentifier.fromLanguageTag(DigitalInkRecognitionModelIdentifier.EN.getLanguageTag());
        }catch (Exception e){
            e.printStackTrace();
        }
        digitalModel = DigitalInkRecognitionModel.builder(digitalIdentifier).build();
        digitalRecognizer = DigitalInkRecognition.getClient(DigitalInkRecognizerOptions.builder(digitalModel).build());
    }

    //检查是否存在模型
    public Task<Boolean> checkModelDownload(OnDigitalCallbackListener<String> onDigitalCallbackListener){
        return modelManager.isModelDownloaded(digitalModel)
                .addOnSuccessListener(result -> {
                    if (onDigitalCallbackListener!=null){
                        if (result){
                            onDigitalCallbackListener.onSuccess("模型已存在");
                        }else {
                            onDigitalCallbackListener.onFail("模型不存在，请下载模型后使用");
                        }
                    }
                }).addOnFailureListener(e -> {
                    if (onDigitalCallbackListener!=null){
                        onDigitalCallbackListener.onFail("模型状态异常("+e.getMessage()+")");
                    }
                });
    }

    //下载需要的模型
    public Task<Void> downloadModel(OnDigitalCallbackListener<String> onDigitalCallbackListener){
        return modelManager.download(digitalModel,new DownloadConditions.Builder().build())
                .addOnSuccessListener(unused -> {
                    if (onDigitalCallbackListener!=null){
                        onDigitalCallbackListener.onSuccess("模型下载完成");
                    }
                }).addOnFailureListener(e -> {
                    if (onDigitalCallbackListener!=null){
                        onDigitalCallbackListener.onFail("模型下载失败("+e.getMessage()+")");
                    }
                });
    }

    //识别手写文字
    public Task<RecognitionResult> recognize(Ink ink,OnDigitalCallbackListener<String> onDigitalCallbackListener){
        String checkText = "0123456789><";
        RecognitionContext recognitionContext = RecognitionContext.builder()
                .setPreContext(checkText)
                .build();
        return digitalRecognizer.recognize(ink,recognitionContext)
                .addOnSuccessListener(result -> {
                    String recognizeText = result.getCandidates().get(0).getText().toString();
                    if (onDigitalCallbackListener!=null){
                        onDigitalCallbackListener.onSuccess(recognizeText);
                    }
                }).addOnFailureListener(e -> {
                    if (onDigitalCallbackListener!=null){
                        onDigitalCallbackListener.onSuccess("识别手写内容失败("+e.getMessage()+")");
                    }
                });
    }

    //保存需要的手写数据
    public void saveStrokeData(Ink.Stroke.Builder builder,float x,float y,long time){
        builder.addPoint(Ink.Point.create(x,y,time));
    }

    //接口
    public interface OnDigitalCallbackListener<T>{
        void onSuccess(T showMsg);
        void onFail(String showMsg);
    }
}
