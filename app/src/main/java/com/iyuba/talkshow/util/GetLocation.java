package com.iyuba.talkshow.util;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.pm.PackageManager;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import androidx.core.app.ActivityCompat;
import androidx.core.util.Pair;

import com.iyuba.talkshow.TalkShowApplication;


public class GetLocation {

    private final Application mApplication;

    private GetLocation(Application application) {
        this.mApplication = application;
    }

    private static class GetLocationHolder {
        public static GetLocation INSTANCE = new GetLocation(TalkShowApplication.getInstance());
    }

    public static GetLocation getInstance() {
        return GetLocationHolder.INSTANCE;
    }

    public Location getLocation() {
        LocationManager manager = (LocationManager) mApplication.getSystemService(Context.LOCATION_SERVICE);
        Location location = null;
        if (ActivityCompat.checkSelfPermission(mApplication, Manifest.permission.ACCESS_FINE_LOCATION) == PackageManager.PERMISSION_GRANTED
                && ActivityCompat.checkSelfPermission(mApplication, Manifest.permission.ACCESS_COARSE_LOCATION) == PackageManager.PERMISSION_GRANTED) {
            if (manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
                location = manager.getLastKnownLocation(LocationManager.GPS_PROVIDER);
            }
            if (location == null && manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)) {
                manager.requestLocationUpdates(LocationManager.NETWORK_PROVIDER, 1000, 0, mListener);
                location = manager.getLastKnownLocation(LocationManager.NETWORK_PROVIDER);
            }
            if (location != null) {
                manager.removeUpdates(mListener);
            }
        }
        return location;
    }

    public Pair<String, String> getStrLocation() {
        Location location = getLocation();
        return new Pair<>(
                String.valueOf(location.getLatitude()),
                String.valueOf(location.getLongitude()));
    }

    // 监听状态
    private final LocationListener mListener = new LocationListener() {
        // Provider的状态在可用、暂时不可用和无服务三个状态直接切换时触发此函数
        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {
        }

        // Provider被enable时触发此函数，比如GPS被打开
        @Override
        public void onProviderEnabled(String provider) {
        }

        // Provider被disable时触发此函数，比如GPS被关闭
        @Override
        public void onProviderDisabled(String provider) {
        }

        // 当坐标改变时触发此函数，如果Provider传进相同的坐标，它就不会被触发
        @Override
        public void onLocationChanged(Location location) {
        }
    };
}
