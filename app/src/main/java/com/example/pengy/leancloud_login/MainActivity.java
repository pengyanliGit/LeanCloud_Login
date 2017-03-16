package com.example.pengy.leancloud_login;

import android.Manifest;
import android.app.Activity;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.baidu.location.BDLocation;
import com.baidu.location.BDLocationListener;
import com.baidu.location.LocationClient;
import com.baidu.mapapi.SDKInitializer;
import com.baidu.mapapi.map.BitmapDescriptor;
import com.baidu.mapapi.map.BitmapDescriptorFactory;
import com.baidu.mapapi.map.MapStatus;
import com.baidu.mapapi.map.MapStatusUpdate;
import com.baidu.mapapi.map.MapStatusUpdateFactory;
import com.baidu.mapapi.map.MarkerOptions;
import com.baidu.mapapi.map.OverlayOptions;
import com.baidu.mapapi.model.LatLng;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

public class MainActivity extends AppCompatActivity {

    @Bind(R.id.user_id)
    EditText userId;
    @Bind(R.id.user_pwd)
    EditText userPwd;
    @Bind(R.id.btn_login)
    Button btnLogin;
    @Bind(R.id.btn_register)
    Button btnRegister;

    private LocationClient locationClient;
    private BDLocationListener myListener = new MyLocationListener();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        locationClient = new LocationClient(getApplicationContext());
        locationClient.registerLocationListener(myListener);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        getPermision();

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, RegisterActivity.class);
                startActivity(intent);
            }
        });
        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入的用户名和密码
                String ueserName = userId.getText().toString().trim();
                String userPass = userPwd.getText().toString().trim();
                login(ueserName, userPass);
            }
        });
    }

    //获取权限
    private void getPermision() {
        List<String > permisstionList = new ArrayList<>();
        if(ContextCompat.checkSelfPermission(MainActivity.this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED ){
            permisstionList.add(Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.READ_PHONE_STATE)
                != PackageManager.PERMISSION_GRANTED){
            permisstionList.add(Manifest.permission.READ_PHONE_STATE);
        }
        if (ContextCompat.checkSelfPermission(MainActivity.this,Manifest.permission.WRITE_EXTERNAL_STORAGE)
                != PackageManager.PERMISSION_GRANTED){
            permisstionList.add(Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (!permisstionList.isEmpty()){
            String [] permissions = permisstionList.toArray(new String[permisstionList.size()]);
            ActivityCompat.requestPermissions(MainActivity.this,permissions,1);
        }else {
            locationClient.start();
        }
    }
    //是否允许所有的权限
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        switch (requestCode){
            case 1:
                if (grantResults.length > 0){
                    for (int result : grantResults){
                        if (result != PackageManager.PERMISSION_GRANTED){
                            Toast.makeText(this,"必须同意全部的权限",Toast.LENGTH_LONG).show();
                            finish();
                            return;
                        }
                    }
                    locationClient.start();
                }else {
                    Toast.makeText(this,"未知错误",Toast.LENGTH_LONG).show();
                    finish();
                }
                break;
        }
    }

    //登录
    private void login(String userName, String userPass) {
        final String pwd = userPwd.getText().toString();
        //通过输入的用户名从数据库中相应的密码进行匹配
        AVQuery<AVObject> query = new AVQuery<>("TestObject");
        query.whereStartsWith("name", userName);
        query.findInBackground(new FindCallback<AVObject>() {
            @Override
            public void done(List<AVObject> list, AVException e) {
                if (e == null) {
                    if (list.toString() != "[]") {
                        //通过遍历的方式来对查询返回的数据进行取数
                        for (AVObject avObject : list) {
                            String name = (String) avObject.get("name");
                            String password = (String) avObject.get("password");
                            Log.d("Main", "用户名" + name);
                            Log.d("Main", "密码" + password);

                            if (pwd.equals(password)) {
                                Log.d("Main", "登陆成功");
                                Toast.makeText(MainActivity.this, "登陆成功", Toast.LENGTH_SHORT).show();

                                //保存
                                Set<String> values = avObject.keySet();
                                Log.d("Main", "自定义属性" + values);

                                MyLeanCloudApp app = MyLeanCloudApp.getMyLeanCloudApp();
                                app.setData(values);
                                Log.d("Main", "自定义属保存成功" + app.getData());
                                Intent intent = new Intent(MainActivity.this, LoginSuccess.class);
                                startActivity(intent);

                            } else {
                                Log.d("Main", "密码错误");
                                Toast.makeText(MainActivity.this, "密码错误", Toast.LENGTH_SHORT).show();
                            }
                        }
                    } else {
                        Log.d("Main", "请先注册");
                        Toast.makeText(MainActivity.this, "请先注册", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
    }

    //定位
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

            sb.append("\nerror code : ");
            sb.append(bdLocation.getLocType());    //获取类型类型

            sb.append("\nlatitude 纬度: ");
            sb.append(bdLocation.getLatitude());    //获取纬度信息

            sb.append("\nlontitude 经度: ");
            sb.append(bdLocation.getLongitude());    //获取经度信息

            sb.append("\nradius : ");
            sb.append(bdLocation.getCity());    //获取定位精准度

        }

        @Override
        public void onConnectHotSpotMessage(String s, int i) {

        }
    }

}
