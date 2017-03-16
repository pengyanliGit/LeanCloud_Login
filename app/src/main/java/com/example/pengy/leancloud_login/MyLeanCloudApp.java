package com.example.pengy.leancloud_login;

import android.app.Application;
import android.support.annotation.NonNull;

import com.avos.avoscloud.AVOSCloud;
import com.baidu.mapapi.SDKInitializer;

import java.util.ArrayList;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;
import java.util.Set;


/**
 * Created by pengy on 2017/3/13.
 */

public class MyLeanCloudApp extends Application{
    private static MyLeanCloudApp myLeanCloudApp ;
    private Set<String> data ;
    public Set<String> getData() {
        return data;
    }

    public void setData(Set<String> data) {
        this.data = data;
    }

    public static  MyLeanCloudApp getMyLeanCloudApp(){ return myLeanCloudApp;}

    @Override
    public void onCreate() {
        super.onCreate();
        SDKInitializer.initialize(getApplicationContext());
        AVOSCloud.initialize(this,"1mCDXRfFFWgMbwrmespwUOQv-gzGzoHsz","McaVNW1eGduVWPlzNMCIEn1h");
        AVOSCloud.setDebugLogEnabled(true);

        myLeanCloudApp = this;

    }
}
