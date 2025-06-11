package com.iyuba.talkshow.lil.help_fix.manager;

import android.text.TextUtils;

import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_fix.manager.dataManager.CommonDataManager;
import com.iyuba.talkshow.lil.help_fix.model.local.entity.SettingEntity;
import com.iyuba.talkshow.lil.user.UserInfoManager;

/**
 * @title: 学习管理-专用类型数据
 * @date: 2023/5/23 10:38
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 * @description: 保存在数据库中，从数据库中根据用户id获取配置
 */
public class StudyUserManager {
    private static final String TAG = "StudyUserManager";

    private static StudyUserManager instance;

    public static StudyUserManager getInstance(){
        if (instance==null){
            synchronized (StudyUserManager.class){
                if (instance==null){
                    instance = new StudyUserManager();
                }
            }
        }
        return instance;
    }

    //类型
    private static final String PLAY_SPEED = TypeLibrary.SettingType.STUDY_SPEED;//播放倍速

    public float getPlaySpeed(){
        SettingEntity entity = CommonDataManager.getSettingDataFromDB(String.valueOf(UserInfoManager.getInstance().getUserId()), PLAY_SPEED);
        float speed = 1.0f;
        if (entity!=null&&!TextUtils.isEmpty(entity.data)){
            return Float.parseFloat(entity.data);
        }
        return speed;
    }

    public void savePlaySpeed(float speed){
        SettingEntity entity = new SettingEntity(String.valueOf(UserInfoManager.getInstance().getUserId()),PLAY_SPEED,String.valueOf(speed));
        CommonDataManager.saveSettingDataToDB(entity);
    }
}
