package com.example.pengy.leancloud_login;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.avos.avoscloud.AVException;
import com.avos.avoscloud.AVObject;
import com.avos.avoscloud.AVQuery;
import com.avos.avoscloud.FindCallback;
import com.avos.avoscloud.SaveCallback;

import java.util.List;
import java.util.Set;

import butterknife.Bind;
import butterknife.ButterKnife;

/**
 * Created by pengy on 2017/3/13.
 */

public class RegisterActivity extends AppCompatActivity {

    @Bind(R.id.register_id)
    EditText registerId;
    @Bind(R.id.register_pwd)
    EditText registerPwd;
    @Bind(R.id.register_pwd_ok)
    EditText registerPwdOk;
    @Bind(R.id.btn_register)
    Button btnRegister;

    public static Set<String> data = null;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        ButterKnife.bind(this);

        init();
    }

    public void init() {
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //获取输入的用户名，密码及确认密码
                final String name = registerId.getText().toString().trim();
                final String pwd = registerPwd.getText().toString().trim();
                final String pwdOk = registerPwdOk.getText().toString().trim();
                //查询数据库是否有该用户存在
                AVQuery<AVObject> query = new AVQuery<>("TestObject");
                query.whereStartsWith("name", name);
                query.findInBackground(new FindCallback<AVObject>() {
                    @Override
                    public void done(List<AVObject> list, AVException e) {

                        if (e == null){
                            //用户名不存在，可以注册
                            if (list.toString() == "[]") {
                                if (pwd.equals(pwdOk)) {
                                    AVObject testObject = new AVObject("TestObject");
                                    //两次输入密码一致，注册成功，信息存入数据库
                                    Log.d("RegisterActivity", "注册成功");
                                    testObject.put("name", name);
                                    testObject.put("password", pwd);

                                    testObject.saveInBackground(new SaveCallback() {
                                        @Override
                                        public void done(AVException e) {
                                            if (e == null) {
                                                Log.d("saved", "保存成功");
                                                Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
                                                startActivity(intent);
                                            }
                                        }
                                    });
                                }else {
                                    Log.d("RegisterActivity", "两次输入密码不一致，请重新输入");
                                    Toast.makeText(RegisterActivity.this, "两次输入密码不一致，请重新输入", Toast.LENGTH_SHORT).show();
                                }
                            }else {
                                Log.d("RegisterActivity", "用户名已存在，请直接登录");
                                Toast.makeText(RegisterActivity.this, "用户名已存在，请直接登录", Toast.LENGTH_SHORT).show();
                            }

                        }
                    }
                });
            }
        });
    }
}
