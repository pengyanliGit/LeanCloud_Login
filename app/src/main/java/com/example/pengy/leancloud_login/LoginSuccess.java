package com.example.pengy.leancloud_login;

import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.location.LocationClientOption;
import com.baidu.location.Poi;
import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BaiduMap;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MapView;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by pengy on 2017/3/13.
 */

public class LoginSuccess extends AppCompatActivity {
    @Bind(R.id.view)
    TextView view;
    @Bind(R.id.map_view)
    MapView mapView;

    public static BaiduMap myBaiduMap;
    private LocationClient locationClient = null;
    private Handler handler = null;
    private String content = null;
    private BDLocationListener myListener = new MyLocationListener();
    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        //声明LocationClient类
        locationClient = new LocationClient(getApplicationContext());
        //注册监听函数
        locationClient.registerLocationListener(myListener);
        setContentView(R.layout.activity_login_success);
        ButterKnife.bind(this);
        handler = new Handler();
        myBaiduMap = mapView.getMap();

        initLocation();
    }
    //更新Ui显示经纬度
    Runnable runnableUi = new Runnable() {
        @Override
        public void run() {
            view.setText("( "+content+" )");
        }
    };

    private void initLocation(){
        //可选，默认高精度，设置定位模式，高精度，低功耗，仅设备
        LocationClientOption option = new LocationClientOption();
        option.setLocationMode(LocationClientOption.LocationMode.Hight_Accuracy);

        //可选，默认false,设置是否使用gps
        option.setOpenGps(true);

        locationClient.setLocOption(option);
        locationClient.start();

        if (locationClient != null && locationClient.isStarted()) {
            Log.i("gps","发起定位");
            locationClient.requestLocation();

        } else {
            Log.i("gps","locClient is null or not started");
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        mapView.onResume();
    }

    @Override
    protected void onPause() {
        super.onPause();
        mapView.onPause();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        mapView.onDestroy();
        locationClient.stop();
        locationClient.registerLocationListener(myListener);
    }

    public class MyLocationListener implements BDLocationListener {

        @Override
        public void onReceiveLocation(final BDLocation bdLocation) {
            if (bdLocation == null){
                return;
            }

            //获取定位结果
            StringBuffer sb = new StringBuffer(256);

            sb.append("time : ");
            sb.append(bdLocation.getTime());    //获取定位时间

            sb.append("\nlontitude 经度: ");
            sb.append(bdLocation.getLongitude());    //获取经度信息

            //添加覆盖物
            LatLng point = new LatLng(bdLocation.getLatitude(),bdLocation.getLongitude());
            BitmapDescriptor bitmap = BitmapDescriptorFactory.fromResource(R.mipmap.icon_openmap_mark);
            OverlayOptions opt = new MarkerOptions().position(point).icon(bitmap).zIndex(9).draggable(true);
            myBaiduMap.addOverlay(opt);

            //设置当前定位地点为地图中心
            MapStatus mapStatus = new MapStatus.Builder().target(point).zoom(18).build();
            MapStatusUpdate newCenter= MapStatusUpdateFactory.newMapStatus(mapStatus);
            myBaiduMap.setMapStatus(newCenter);

             new Thread(){
                 public void run(){
                     content = bdLocation.getLatitude()+" , "+bdLocation.getLongitude();
                     handler.post(runnableUi);
                 }
             }.start();
        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

}
