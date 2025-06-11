package com.iyuba.talkshow.data.manager;

import com.iyuba.talkshow.data.local.PreferencesHelper;
import com.iyuba.talkshow.data.model.result.GetAdData;
import com.iyuba.talkshow.data.model.result.GetAdData1;

import java.io.IOException;

import javax.inject.Inject;

public class AdManager {
    private static final String OPEN_AD = "open_id";
    private final PreferencesHelper mPreferencesHelper;

    @Inject
    public AdManager(PreferencesHelper preferencesHelper) {
        this.mPreferencesHelper = preferencesHelper;
    }

    public GetAdData1 getAdData1() {
        GetAdData1 adData = null;
        try {
            adData = (GetAdData1) mPreferencesHelper.loadObject(OPEN_AD);
        } catch (IOException | ClassNotFoundException e) {
            e.printStackTrace();
        }
        return adData;
    }

    public void saveAdData1(GetAdData1 adData) {
        try {
            mPreferencesHelper.putObject(OPEN_AD, adData);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
