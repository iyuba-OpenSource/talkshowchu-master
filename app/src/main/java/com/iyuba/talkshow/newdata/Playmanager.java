package com.iyuba.talkshow.newdata;

import com.iyuba.talkshow.data.model.Voa;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.ConcurrentHashMap;

/**
 * Created by carl shen on 2020/8/3
 * New Primary English, new study experience.
 */
public class Playmanager {
    private final List<Integer> list;
    private final ConcurrentHashMap<Integer, Voa> voaList;
    private static Playmanager myPlayer;
    public static Playmanager getInstance() {
        if (myPlayer == null) {
            myPlayer = new Playmanager();
        }
        return myPlayer;
    }

    private Playmanager() {
        list = new ArrayList<>();
        voaList = new ConcurrentHashMap<Integer, Voa>();
    }

    public Voa getVoaFromList(int voaid) {
        if (voaList != null) {
            return voaList.get(voaid);
        }
        return null;
    }

    public void setPlayList(List<Voa> listVoa) {
        list.clear();
        voaList.clear();
        if (listVoa == null || listVoa.size()<1) {
            return;
        }
        for (int i = 0; i < listVoa.size(); i++) {
            Voa voa = listVoa.get(i);
            boolean isAmerican = SPconfig.Instance().loadBoolean(Config.ISAMEICAN, true);
            if (isAmerican) {
                list.add(voa.voaId());
                voaList.put(voa.voaId(), voa);
            } else {
                if (voa.voaId() < 2000 && voa.voaId() % 2 == 0) {
                } else {
                    list.add(voa.voaId());
                    voaList.put(voa.voaId(), voa);
                }
            }
        }
    }

    public int setFormerVoaId() {
        int playMode = SPconfig.Instance().loadInt(Config.playMode);
        int formerId = 0;

        switch (playMode) {
            case 0:
                //顺序播放
                int currCourseId = SPconfig.Instance().loadInt(Config.currVoaId);
                for (int i = 0; i < list.size(); i++) {
                    if (currCourseId == list.get(i)) {
                        if (i == 0) {
                            formerId = list.get(list.size() - 1);
                        } else {
                            formerId = list.get(i - 1);
                        }
                    }
                }
                break;
            case 1:
                formerId = SPconfig.Instance().loadInt(Config.currVoaId);
                break;
            case 2:
                //随机播放
                formerId = list.get((int) (1 + Math.random() * list.size()));
                break;
        }

        return formerId;
    }

    public int setNextVoaId() {
        int nextId = 0;
        int playMode = SPconfig.Instance().loadInt(Config.playMode);
        if ((list == null) || list.size() < 1) {
            return nextId;
        }

        switch (playMode) {
            case 0:
                //顺序播放
                int currCourseId = SPconfig.Instance().loadInt(Config.currVoaId);
                for (int i = 0; i < list.size(); i++) {
                    if (currCourseId == list.get(i)) {
                        if (i == list.size() - 1) {
                            nextId = list.get(0);
                        } else {
                            nextId = list.get(i + 1);
                        }
                    }
                }
                break;
            case 1:
                //单曲循环
                nextId = SPconfig.Instance().loadInt(Config.currVoaId);
                break;
            case 2:
                int index = (int) (1 + Math.random() * list.size());
                if (index ==list.size()){
                    index -- ;
                }
                //随机播放
                nextId = list.get(index);
                break;
        }

        return nextId;
    }

}
