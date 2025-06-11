//package com.iyuba.talkshow.data;
//
//import com.iyuba.talkshow.data.model.Voa;
//
///**
// * 后台的会话
// */
//public class BackgroundSession {
//
//    private static BackgroundSession instance;
//
//    public static BackgroundSession getInstance(){
//        if (instance==null){
//            synchronized (BackgroundSession.class){
//                if (instance==null){
//                    instance = new BackgroundSession();
//                }
//            }
//        }
//        return instance;
//    }
//
//    //保存的数据
//    private Voa mVoa;
//    private int position;
//    private int unitId;
//    private boolean backToMain;
//    private boolean isAuto;
//
//    public void setData(Voa voa,int position,int unitId,boolean back,boolean isAuto){
//        this.mVoa = voa;
//        this.position = position;
//        this.unitId = unitId;
//        this.backToMain = back;
//        this.isAuto = isAuto;
//    }
//
//    public Voa getVoa(){
//        return mVoa;
//    }
//
//    public int getPosition(){
//        return position;
//    }
//
//    public int getUnitId(){
//        return unitId;
//    }
//
//    public boolean isBack(){
//        return backToMain;
//    }
//
//    public boolean isAuto(){
//        return isAuto;
//    }
//}
