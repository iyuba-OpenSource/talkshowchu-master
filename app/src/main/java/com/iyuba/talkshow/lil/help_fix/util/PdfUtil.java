package com.iyuba.talkshow.lil.help_fix.util;

import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.text.TextUtils;

import androidx.appcompat.app.AlertDialog;

import com.iyuba.talkshow.Constant;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.NovelDataManager;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Integral_deduct;
import com.iyuba.talkshow.lil.help_fix.model.remote.bean.Pdf_url;
import com.iyuba.talkshow.lil.help_mvp.util.ToastUtil;
import com.iyuba.talkshow.lil.help_mvp.util.rxjava2.RxUtil;
import com.iyuba.talkshow.lil.user.UserInfoManager;

import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

/**
 * @title: pdf操作类
 * @date: 2023/7/4 14:33
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description:
 */
public class PdfUtil {

    private static PdfUtil instance;

    public static PdfUtil getInstance(){
        if (instance==null){
            synchronized (PdfUtil.class){
                if (instance==null){
                    instance = new PdfUtil();
                }
            }
        }
        return instance;
    }

    //判断下一步操作
    public void checkNext(Context context,String types, String title,String imageUrl,String voaId){
        //选择类型->扣除积分->获取链接并展示(vip不用扣除积分)
        showFileTypeDialog(context,types, title,imageUrl,voaId);
    }

    //扣除积分
    private Disposable deductPointDis;
    private void deductPoint(Context context,String types,String title,String imageUrl,String downType,String voaId){
        RxUtil.unDisposable(deductPointDis);
        CommonDataManager.deductIntegralBeforePdf(UserInfoManager.getInstance().getUserId(), voaId)
                .subscribeOn(Schedulers.io())
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Observer<Integral_deduct>() {
                    @Override
                    public void onSubscribe(Disposable d) {
                        deductPointDis = d;
                    }

                    @Override
                    public void onNext(Integral_deduct bean) {
                        if (bean.getResult().equals("200")){
                            //扣除成功，获取下载链接
                            getPdfUrl(context,types,title,imageUrl,downType,voaId);
                        }else if (bean.getResult().equals("205")){
                            ToastUtil.showToast(context, "积分不足，无法下载pdf文件");
                        }else if (bean.getResult().equals("406")){
                            ToastUtil.showToast(context, "请登录后重试");
                        }
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtil.showToast(context, "扣除积分失败，请稍后再试");
                    }

                    @Override
                    public void onComplete() {

                    }
                });
    }

    //获取pdf下载链接
    private Disposable pdfUrlDis;
    private void getPdfUrl(Context context,String types,String title,String imageUrl,String downType,String voaId){
        RxUtil.unDisposable(pdfUrlDis);

        switch (types){
            case TypeLibrary.BookType.bookworm://书虫
            case TypeLibrary.BookType.newCamstory://剑桥小说馆
            case TypeLibrary.BookType.newCamstoryColor://剑桥小说馆彩绘
                NovelDataManager.getLessonPdfUrl(types, voaId, downType)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Pdf_url>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                pdfUrlDis = d;
                            }

                            @Override
                            public void onNext(Pdf_url bean) {
                                if (bean.getExists().equals("true")&&!TextUtils.isEmpty(bean.getPath())){
                                    //合成链接展示
                                    String pdfUrl = "http://apps."+ Constant.Web.WEB_SUFFIX +"book";
                                    if (!bean.getPath().startsWith("/")){
                                        pdfUrl = pdfUrl+"/"+bean.getPath();
                                    }else {
                                        pdfUrl = pdfUrl+bean.getPath();
                                    }

                                    showFileUrlDialog(context,title,imageUrl,pdfUrl,voaId);
                                }else {
                                    ToastUtil.showToast(context, "该文件不存在，请选择其他类型");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtil.showToast(context, "生成文件链接错误，请重试");
                            }

                            @Override
                            public void onComplete() {

                            }
                        });
                break;
            case TypeLibrary.BookType.conceptFour://新概念全四册
            case TypeLibrary.BookType.conceptFourUK://新概念全四册英音
            case TypeLibrary.BookType.conceptFourUS://新概念全四册美音
            case TypeLibrary.BookType.conceptJunior://新概念青少版
                break;
            case TypeLibrary.BookType.junior_primary://小学
            case TypeLibrary.BookType.junior_middle://初中
                /*JuniorDataManager.getLessonPdfUrl(types,downType,voaId)
                        .subscribeOn(Schedulers.io())
                        .observeOn(AndroidSchedulers.mainThread())
                        .subscribe(new Observer<Pdf_url>() {
                            @Override
                            public void onSubscribe(Disposable d) {
                                pdfUrlDis = d;
                            }

                            @Override
                            public void onNext(Pdf_url bean) {
                                if (bean.getExists().equals("true")&&!TextUtils.isEmpty(bean.getPath())){
                                    //合成链接展示
                                    String pdfUrl = "http://apps."+ Constant.Web.WEB_SUFFIX+"iyuba";
                                    if (!bean.getPath().startsWith("/")){
                                        pdfUrl = pdfUrl+"/"+bean.getPath();
                                    }else {
                                        pdfUrl = pdfUrl+bean.getPath();
                                    }

                                    showFileUrlDialog(context,title,imageUrl,pdfUrl,voaId);
                                }else {
                                    ToastUtil.showToast(context, "该文件不存在，请选择其他类型");
                                }
                            }

                            @Override
                            public void onError(Throwable e) {
                                ToastUtil.showToast(context, "生成文件链接错误，请重试");
                            }

                            @Override
                            public void onComplete() {

                            }
                        });*/
                break;
        }
    }

    //停止下载
    public void cancelDownload(){
        RxUtil.unDisposable(deductPointDis);
        RxUtil.unDisposable(pdfUrlDis);
    }

    /***********************辅助功能************************/
    //显示扣除积分弹窗
    private void showIntegralDeductDialog(Context context,String types,String title,String imageUrl,String downType,String voaId){
        new AlertDialog.Builder(context)
                .setTitle("扣除积分")
                .setMessage("生成PDF每篇文章将消耗20积分，是否生成？开通VIP后即可免积分下载。")
                .setCancelable(false)
                .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        deductPoint(context,types, title,imageUrl,downType, voaId);
                    }
                }).setNegativeButton("取消",null)
                .show();
    }

    //显示类型选择弹窗
    private void showFileTypeDialog(Context context,String types,String title,String imageUrl,String voaId){
        String[] typeArray = new String[]{"导出英文","导出中英双语"};

        new AlertDialog.Builder(context)
                .setTitle("选择类型")
                .setItems(typeArray, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        String downType = TypeLibrary.PdfFileType.ALL;
                        if (which == 0){
                            downType = TypeLibrary.PdfFileType.EN;
                        }

                        if (UserInfoManager.getInstance().isVip()){
                            getPdfUrl(context,types,title,imageUrl,downType,voaId);
                        }else {
                            showIntegralDeductDialog(context,types,title,imageUrl,downType,voaId);
                        }
                    }
                }).show();
    }

    //显示文件链接弹窗
    private void showFileUrlDialog(Context context,String title,String imageUrl,String fileUrl,String voaId){
        copyToClipBoard(context,fileUrl);

        new AlertDialog.Builder(context)
                .setTitle("文件已生成")
                .setMessage("文件链接："+fileUrl+"(链接已复制到剪贴板)")
                .setCancelable(false)
                .setPositiveButton("发送", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        sendToOther(context,title,imageUrl,fileUrl,voaId, UserInfoManager.getInstance().getUserId());
                    }
                }).setNegativeButton("下载", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                jumpBrowser(context,fileUrl);
            }
        }).show();
    }

    //跳转下载界面
    private void jumpBrowser(Context context,String url){
        try {
            Intent intent = new Intent();
            intent.setAction(Intent.ACTION_VIEW);
            intent.setData(Uri.parse(url));
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
            context.startActivity(intent);
        }catch (Exception e){
            ToastUtil.showToast(context, "未查找到可下载文件的软件，请重试或安装浏览器使用");
        }
    }

    //发送给其他好友
    private void sendToOther(Context context,String title,String imageUrl,String url,String voaId,int uid){
        String content = "即时导出中英课文PDF";
        if (TextUtils.isEmpty(imageUrl)){
            imageUrl = "http://app." + Constant.Web.WEB_SUFFIX + "android/images/newconcept/newconcept.png";
        }

        ShareUtil.getInstance().shareUrl(context,title,content,imageUrl,url,uid,voaId);
    }

    //复制到剪贴板
    private void copyToClipBoard(Context context,String dataText){
        ClipboardManager manager = (ClipboardManager) context.getSystemService(Context.CLIPBOARD_SERVICE);
        ClipData clipData = ClipData.newPlainText("文件下载链接",dataText);
        manager.setPrimaryClip(clipData);
    }
}
