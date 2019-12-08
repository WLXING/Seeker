package com.example.ui;

import android.content.Context;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bean.User;
import com.example.seeker.R;
import com.example.utils.L;
import com.umeng.message.PushAgent;

import org.litepal.LitePal;

import java.util.HashMap;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class RegisterActivity extends AppCompatActivity {
    private static final String TAG = "RegisterActivity";
    private TextView usernameText;
    private EditText passwordEdit;
    private EditText passwordConfirm;
    private Button registerOk;
    private String username;
    private String  password;
    private ImageView back;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);
        init();
        PushAgent.getInstance(this).onAppStart();  //友盟消息推送的
        String phone = getIntent().getStringExtra("phone");
        usernameText.setText(phone);
        List<User> user = LitePal.where("username=?", phone).find(User.class);
        if (!user.isEmpty()) {
            Toast.makeText(RegisterActivity.this,"该用户已存在",Toast.LENGTH_SHORT).show();
            Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
            setResult(0,intent );
            finish();
        }
        registerOk.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameText.getText().toString();
                password = passwordEdit.getText().toString();
                if ((passwordEdit.getText().toString().equals(passwordConfirm.getText().toString())) &&!"".equals(password)) {
                                password = passwordEdit.getText().toString();
                                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                                intent.putExtra("username", usernameText.getText().toString());
                                intent.putExtra("password", passwordConfirm.getText().toString());
                                User user = new User();
                                user.setUsername(username);
                                user.setPassword(password);
                                user.save();
                                setResult(0, intent);
                                finish();
                    }
                 else {
                    Toast.makeText(RegisterActivity.this, "密码不一致", Toast.LENGTH_SHORT).show();
                    passwordEdit.setText("");
                    passwordConfirm.setText("");
                }
            }
        });
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
                setResult(0,intent );
                finish();
            }
        });
    }

    private void init() {
        usernameText = (TextView) findViewById(R.id.registerActivity_username);
        passwordEdit = (EditText) findViewById(R.id.registerActivity_password);
        passwordConfirm = (EditText) findViewById(R.id.registerActivity_confirm);
        registerOk = (Button) findViewById(R.id.registerActivity_ok);
        back = (ImageView) findViewById(R.id.registerActivity_back);
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(RegisterActivity.this, LoginActivity.class);
        setResult(0,intent );
        finish();
        super.onBackPressed();
    }
}
