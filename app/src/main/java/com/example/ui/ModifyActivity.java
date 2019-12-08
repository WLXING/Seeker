package com.example.ui;

import android.content.Intent;
import android.graphics.Color;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;
import android.widget.Toolbar;

import com.example.seeker.MainActivity;
import com.example.seeker.R;
import com.umeng.message.PushAgent;

public class ModifyActivity extends AppCompatActivity {
    private static String TAG = "ModifyActivity";
    private Toolbar toolbar;
    private TextView toolbarText;
    private EditText modifyEdit;
    private static int MODIFY_OK=100;
    private static final String MODIFY_TYPE_PUBLIC="noSecret";
    private static final String MODIFY_TYPE_SECRET="Secret";
    private String modifyType;
    private String content;
    private String itemTitle;
    private int oldPasswordOk=0;//旧密码输入正确
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_modify);
        init();
        PushAgent.getInstance(this).onAppStart(); //友盟消息推送的
        content = getIntent().getStringExtra("content");
        modifyType = getIntent().getStringExtra("modifyType");
        itemTitle = getIntent().getStringExtra("itemTitle");
        toolbarText.setText(itemTitle);
        if (MODIFY_TYPE_PUBLIC.equals(modifyType)) {
            modifyEdit.setText(content);
        } else if (MODIFY_TYPE_SECRET.equals(modifyType)) {
            modifyEdit.setHint("请输入您的旧密码");
        }
    }

    private void init() {
        modifyEdit = (EditText) findViewById(R.id.modifyActivity_edittext);
        toolbarText = (TextView) findViewById(R.id.modifyActivity__toolbar_text);
        toolbarText.setTextColor(Color.BLACK);
        //将toolbar设为标题，记得将application的label去掉，并在activity里设置label=""
        android.support.v7.widget.Toolbar toolbar = (android.support.v7.widget.Toolbar) findViewById(R.id.modifyActivity_toolbar);
        setSupportActionBar(toolbar);//如不需用到ActionBar的某些功能，则省略这句
        ActionBar actionBar = getSupportActionBar();
        if (actionBar != null) {
            actionBar.setDisplayHomeAsUpEnabled(true);
            actionBar.setHomeAsUpIndicator(R.drawable.seeker_icon_back);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.modifyactivity_toolbar_menu,menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
               finish();
                break;
            case R.id.modifyActivity_toolbar_ok:
                if (oldPasswordOk == 1) {
                    Intent intent = new Intent(ModifyActivity.this, MainActivity.class);
                    intent.putExtra("content", modifyEdit.getText().toString());
                    Toast.makeText(this, "密码修改成功", Toast.LENGTH_SHORT).show();
                    setResult(MODIFY_OK, intent);
                    finish();
                }
                else if (MODIFY_TYPE_SECRET.equals(modifyType)) {
                    if (modifyEdit.getText().toString().equals(content)) {
                        modifyEdit.setText("");
                        modifyEdit.setHint("请输入您的新密码");
                        oldPasswordOk = 1;
                    } else {
                        modifyEdit.setHint("请输入您的旧密码");
                        Toast.makeText(this, "密码有误,请重新输入", Toast.LENGTH_SHORT).show();
                    }
                } else {
                    Intent intent = new Intent(ModifyActivity.this, MainActivity.class);
                    intent.putExtra("content", modifyEdit.getText().toString());
                    Toast.makeText(this, "修改成功", Toast.LENGTH_SHORT).show();
                    setResult(MODIFY_OK, intent);
                    finish();
                }
        }
        return true;
    }
}
