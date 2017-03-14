package com.example.pengy.leancloud_login;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.widget.TextView;

import com.avos.avoscloud.AVObject;

import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by pengy on 2017/3/13.
 */

public class LoginSuccess extends AppCompatActivity {

    @Bind(R.id.view)
    TextView view;



    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login_success);
        ButterKnife.bind(this);

        MyLeanCloudApp app = MyLeanCloudApp.getMyLeanCloudApp();
        Set<String> data = app.getData();
        Log.d("LoginSuccess", "自定义属保存成功"+ data);
        view.setText(data.toString());
    }


}
