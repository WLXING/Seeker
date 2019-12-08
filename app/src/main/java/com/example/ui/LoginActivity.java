package com.example.ui;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.preference.PreferenceManager;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bean.User;
import com.example.seeker.MainActivity;
import com.example.seeker.R;
import com.example.utils.L;
import com.umeng.message.PushAgent;

import org.litepal.LitePal;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;
import java.util.List;

import cn.smssdk.EventHandler;
import cn.smssdk.SMSSDK;
import cn.smssdk.gui.RegisterPage;

public class LoginActivity extends AppCompatActivity {
    private static String TAG = "LoginActivity";
    private EditText usernameEdit;
    private EditText passwordEdit;
    private Button loginbtn;
    private CheckBox remPwd;
    private int loginCheckFlag;
    private String username;
    private String password;
    private TextView newUser;
    private static final int REGISTER_CODE = 10;
    private SharedPreferences sharepr;
    private SharedPreferences.Editor shEditor;
    private static int REFRESH_LOGIN = 12;
    private static int MAIN_REQUEST = 11;
    public static String dbName = "seeker.db";//数据库的名字
    private static String DATABASE_PATH = "/data/data/com.example.seeker/databases/";//数据库在手机里的路径

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        init();
        //判断数据库是否存在
        boolean dbExist = checkDataBase();
        if (dbExist) {

        } else {//不存在就把raw里的数据库写入手机
            try {
                copyDataBase();
            } catch (IOException e) {
                throw new Error("Error copying database");
            }
        }
        PushAgent.getInstance(this).onAppStart();//友盟消息推送的
        //正在运行的android程序，按home键之后退回到桌面，再次点击桌面图标避免再次重新启动程序的终极解决办法
        if ((getIntent().getFlags() & Intent.FLAG_ACTIVITY_BROUGHT_TO_FRONT) != 0) {
            finish();
            return;
        }
        setContentView(R.layout.activity_login);
        loginCheckFlag = sharepr.getInt("checkFlag", 0);//是否记住密码
        if (loginCheckFlag == 1) {
            String username = sharepr.getString("username", "");
            String password = sharepr.getString("password", "");
            usernameEdit.setText(username);
            passwordEdit.setText(password);
            remPwd.setChecked(true);
        } else {
            //usernameEdit.setText("");//只清除密码
            passwordEdit.setText("");
        }

        loginbtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                username = usernameEdit.getText().toString();
                password = passwordEdit.getText().toString();
                List<User> users = LitePal.where("username=? and password=?", username, password).find(User.class);

                // if (user != null) 这里不可以这样写，判断不出来，没输入用户名和密码都能登录
                if (!(users.isEmpty())) {
                    if (loginCheckFlag == 1) {
                        shEditor.putString("username", username);
                        shEditor.putString("password", password);
                        shEditor.putInt("checkFlag", loginCheckFlag);
                    } else {
                        shEditor.clear();
                    }
                    shEditor.apply();

                    Intent intent = new Intent(LoginActivity.this, MainActivity.class);
                    intent.putExtra("username", username);
                    intent.putExtra("password", password);
                    intent.putExtra("loginCheckFlag", loginCheckFlag);
                    if (loginCheckFlag == 1) {

                    } else {//如果没有这句，退出登录的时候，如果你没记住密码，密码也会在上面
                        // usernameEdit.setText("");
                        passwordEdit.setText("");//只清除密码
                    }
                    //closeSoftKeyInput();
                    startActivityForResult(intent, MAIN_REQUEST);
                    Toast.makeText(LoginActivity.this, "登录成功", Toast.LENGTH_SHORT).show();
                } else {
                    Toast.makeText(LoginActivity.this, "账号或者密码不正确", Toast.LENGTH_SHORT).show();
                }

            }
        });
        remPwd.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    loginCheckFlag = 1;
                } else {
                    loginCheckFlag = 0;
                }
            }
        });
        newUser.setOnClickListener(new View.OnClickListener() {
            @SuppressLint("ResourceAsColor")
            @Override
            public void onClick(View v) {
                //用户点击注册新用户，发送短信验证码，成功的话就跳到注册界面，账号就是电话号码
                sendCode(LoginActivity.this);
            }
        });

    }

    private void init() {
        usernameEdit = (EditText) findViewById(R.id.loginActivity_username);
        passwordEdit = (EditText) findViewById(R.id.loginActivity_password);
        loginbtn = (Button) findViewById(R.id.loginActivity_loginButton);
        remPwd = (CheckBox) findViewById(R.id.loginActivity_rem_pwd);
        newUser = (TextView) findViewById(R.id.loginActivity_newUser);
        sharepr = PreferenceManager.getDefaultSharedPreferences(this);
        shEditor = sharepr.edit();
    }

    @SuppressLint("ResourceAsColor")
    @Override
    protected void onActivityResult(int requestCode, int resultCode, @Nullable Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == REGISTER_CODE) {
            if (!"".equals(data)) {
                username = data.getStringExtra("username");
                password = data.getStringExtra("password");
                usernameEdit.setText(username);
                passwordEdit.setText(password);
            }
        } else if (requestCode == MAIN_REQUEST && resultCode == REFRESH_LOGIN) {
            if (loginCheckFlag == 1) {//没有记住密码的话，什么都不做，记住密码的话刷新登录界面，保存数据
                usernameEdit.setText(data.getStringExtra("username"));
                passwordEdit.setText(data.getStringExtra("password"));
                shEditor.putString("username", data.getStringExtra("username"));
                shEditor.putString("password", data.getStringExtra("password"));
                shEditor.apply();
            } else {
                usernameEdit.setText(data.getStringExtra("username"));//如果不记住密码的话，只需改用户名就好
                shEditor.putString("username", data.getStringExtra("username"));
            }
        }
        newUser.setTextColor(R.color.white);
    }


    //关闭软键盘
    private void closeSoftKeyInput() {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        //boolean isOpen=imm.isActive();//isOpen若返回true，则表示输入法打开
        if (imm.isActive()) {
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(),
                    InputMethodManager.HIDE_NOT_ALWAYS);
        }
    }

    /**
     * 判断数据库是否存在
     */
    public boolean checkDataBase() {
        SQLiteDatabase checkDB = null;
        try {
            String databaseFilename = DATABASE_PATH + dbName;
            checkDB = SQLiteDatabase.openDatabase(databaseFilename, null,
                    SQLiteDatabase.OPEN_READONLY);
        } catch (SQLiteException e) {

        }
        if (checkDB != null) {
            checkDB.close();
        }
        return checkDB != null ? true : false;
    }

    /**
     * 复制数据库到手机指定文件夹下
     *
     * @throws IOException
     */
    public void copyDataBase() throws IOException {
        String databaseFilenames = DATABASE_PATH + dbName;
        File dir = new File(DATABASE_PATH);
        if (!dir.exists())//判断文件夹是否存在，不存在就新建一个
            dir.mkdir();
        FileOutputStream os = null;
        try {
            os = new FileOutputStream(databaseFilenames);//得到数据库文件的写入流
        } catch (FileNotFoundException e) {
            e.printStackTrace();
        }
        InputStream is = LoginActivity.this.getResources().openRawResource(R.raw.seeker);//得到数据库文件的数据流
        byte[] buffer = new byte[8192];
        int count = 0;
        try {

            while ((count = is.read(buffer)) > 0) {
                os.write(buffer, 0, count);
                os.flush();
            }
        } catch (IOException e) {

        }
        try {
            is.close();
            os.close();
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    /**
     * 发送短信验证
     */
    public void sendCode(Context context) {
        RegisterPage page = new RegisterPage();
        //如果使用我们的ui，没有申请模板编号的情况下需传null
        page.setTempCode(null);
        page.setRegisterCallback(new EventHandler() {
            public void afterEvent(int event, int result, Object data) {
                if (result == SMSSDK.RESULT_COMPLETE) {
                    // 处理成功的结果
                    HashMap<String, Object> phoneMap = (HashMap<String, Object>) data;
                    // 国家代码，如“86”
                    String country = (String) phoneMap.get("country");
                    // 手机号码，如“13800138000”
                    String phone = (String) phoneMap.get("phone");
                    // TODO 利用国家代码和手机号码进行后续的操作
                    L.e(TAG, "------------->" + country + "  " + phone);
                    //跳转到注册界面
                    Intent intent = new Intent(LoginActivity.this, RegisterActivity.class);
                    intent.putExtra("phone", phone);
                    startActivityForResult(intent, REGISTER_CODE);
                } else {
                    // TODO 处理错误的结果
                    L.e(TAG, "------------->" + "短信验证失败");
                    Toast.makeText(LoginActivity.this, "请填入正确号码", Toast.LENGTH_SHORT).show();
                }
            }
        });
        page.show(context);
    }
}
