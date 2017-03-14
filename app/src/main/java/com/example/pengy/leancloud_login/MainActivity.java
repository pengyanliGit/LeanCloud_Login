package com.example.pengy.leancloud_login;

import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
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
    @Bind(R.id.activity_main)
    LinearLayout activityMain;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

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

}
