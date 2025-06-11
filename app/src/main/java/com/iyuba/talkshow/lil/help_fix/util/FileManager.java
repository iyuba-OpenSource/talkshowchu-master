package com.iyuba.talkshow.lil.help_fix.util;

import android.os.Build;
import android.os.Environment;
import android.text.TextUtils;

import com.iyuba.talkshow.constant.App;
import com.iyuba.talkshow.lil.help_fix.data.library.TypeLibrary;
import com.iyuba.talkshow.lil.help_mvp.util.ResUtil;

import java.io.File;

/**
 * @desction: 文件管理
 * @date: 2023/3/2 19:36
 * @author: liang_mu
 * @email: liang.mu.cn@gmail.com
 */
public class FileManager {

    private static FileManager instance;

    public static FileManager getInstance(){
        if (instance==null){
            synchronized (FileManager.class){
                if (instance==null){
                    instance = new FileManager();
                }
            }
        }
        return instance;
    }

    private static final String DIR_COURSE = "course";//课程
    private static final String DIR_TALK = "talk";//口语秀
    private static final String DIR_WORD = "word";//单词

    private static final String DIR_EVAL = "eval";//评测

    //文件夹总路径
    private String dirPath(){
        if (Build.VERSION.SDK_INT>=Build.VERSION_CODES.N){
            return ResUtil.getInstance().getContext().getExternalFilesDir(null).getPath();
        }else {
            return Environment.getExternalStorageDirectory().getPath()+"/"+ App.APP_NAME_EN;
        }
    }

    /********课程*********/
    //课程音频：course/类型/voaId/voaId.mp3
    //课程视频：course/类型/voaId/voaId.mp4
    //课程评测：course/类型/voaId/eval/用户id/voaId_paraId_indexId.mp3
    public String getCourseAudioPath(String types,String voaId){
        String name = voaId+ TypeLibrary.FileType.MP3;
        return dirPath()+"/"+DIR_COURSE+"/"+types+"/"+voaId+"/"+name;
    }

    public String getCourseVideoPath(String types,String voaId){
        String name = voaId+ TypeLibrary.FileType.MP4;
        return dirPath()+"/"+DIR_COURSE+"/"+types+"/"+voaId+"/"+name;
    }

    public String getCourseEvalAudioPath(String types,String voaId,String paraId,String indexId,long userId){
        String name = voaId+"_"+paraId+"_"+indexId+ TypeLibrary.FileType.MP3;
        return dirPath()+"/"+DIR_COURSE+"/"+types+"/"+voaId+"/"+DIR_EVAL+"/"+userId+"/"+name;
    }


    /*********口语秀*******/
    //口语秀音频：talk/类型/voaId/voaId.mp3
    //口语秀视频：talk/类型/voaId/voaId.mp4
    //口语秀评测：talk/类型/voaId/eval/用户id/voaId_paraId_indexId.mp3
    public String getTalkAudioPath(String types,String voaId){
        String name = voaId+ TypeLibrary.FileType.MP3;
        return dirPath()+"/"+DIR_TALK+"/"+types+"/"+voaId+"/"+name;
    }

    public String getTalkVideoPath(String types,String voaId){
        String name = voaId+ TypeLibrary.FileType.MP4;
        return dirPath()+"/"+DIR_TALK+"/"+types+"/"+voaId+"/"+name;
    }

    public String getTalkEvalAudioPath(String types,String voaId,String paraId,String indexId,long userId){
        String name = voaId+"_"+paraId+"_"+indexId+ TypeLibrary.FileType.MP3;
        return dirPath()+"/"+DIR_TALK+"/"+types+"/"+voaId+"/"+DIR_EVAL+"/"+userId+"/"+name;
    }

    /************单词************/
    //单词音频：word/类型/voaId/单词名称.mp3
    //单词句子音频：word/类型/voaId/句子名称.mp3
    //单词句子视频：word/类型/voaId/句子名称.mp4
    //单词评测：word/类型/voaId/eval/用户id/eval_单词名称.mp3
    //单词句子评测：word/类型/voaId/eval/用户id/eval_句子名称.mp3
    public String getWordAudioPath(String types,String voaId,String word){
        String name = word+TypeLibrary.FileType.MP3;
        return dirPath()+"/"+DIR_WORD+"/"+types+"/"+voaId+"/"+name;
    }

    public String getWordSentenceAudioPath(String types,String voaId,String sentence){
        String name = sentence+TypeLibrary.FileType.MP3;
        return dirPath()+"/"+DIR_WORD+"/"+types+"/"+voaId+"/"+name;
    }

    public String getWordSentenceVideoPath(String types,String voaId,String sentence){
        String name = sentence+TypeLibrary.FileType.MP4;
        return dirPath()+"/"+DIR_WORD+"/"+types+"/"+voaId+"/"+name;
    }

    public String getWordEvalAudioPath(String types,String voaId,String word,long userId){
        String name = "eval_"+word+TypeLibrary.FileType.MP3;
        return dirPath()+"/"+DIR_WORD+"/"+types+"/"+voaId+"/"+DIR_EVAL+"/"+userId+"/"+name;
    }

    public String getWordSentenceEvalAudioPath(String types,String voaId,String sentence,long userId){
        String name = "eval_"+sentence+TypeLibrary.FileType.MP3;
        return dirPath()+"/"+DIR_WORD+"/"+types+"/"+voaId+"/"+DIR_EVAL+"/"+userId+"/"+name;
    }

    /*****************辅助功能****************/
    //创建空白文件
    public boolean createEmptyFile(String filePath){
        try {
            if (TextUtils.isEmpty(filePath)){
                return false;
            }

            File file = new File(filePath);
            if (file.exists()){
                file.delete();
            }

            if (!file.getParentFile().exists()){
                file.getParentFile().mkdirs();
            }

            return file.createNewFile();
        }catch (Exception e){
            return false;
        }
    }

    //判断文件是否存在
    public boolean isFileExist(String filePath){
        File file = new File(filePath);
        if (file.exists()){
            return true;
        }
        return false;
    }

    //删除文件
    public boolean deleteFile(String filePath){
        File file = new File(filePath);
        return file.delete();
    }
}
