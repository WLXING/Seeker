package com.example.ui;

import android.content.ContentValues;
import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bean.MyWordList;
import com.example.bean.WordList;
import com.example.fragment.wordWarehouseFragment;
import com.example.seeker.R;
import com.umeng.message.PushAgent;

import org.litepal.LitePal;

public class WordEditActivity extends AppCompatActivity {
    private static String TAG = "WordEditActivity";
    private TextView wordEditBack;
    private TextView wordEditSave;
    private EditText headwordEdit;
    private EditText phoneticEdit;
    private EditText quickdefEdit;
    private WordList wordList;
    private MyWordList myWordList;
    private int myWordListId;
    private int wordListId;
    private static final int WORD_EDIT_OK = 301;
    private String editType;//新建还是编辑
    private static final String MY_NOTEBOOK = "1009";//我的词库对应索引
    private String bean = null;//判断是wordlist还是mywordlist

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_word_edit);
        init();
        PushAgent.getInstance(this).onAppStart(); //友盟消息推送的
        editType = getIntent().getStringExtra("type");
        bean = getIntent().getStringExtra("bean");
        if (bean.equals("WordList")) {//如果不是我的词库的单词
            wordList = (WordList) getIntent().getSerializableExtra("wordList");
            if ("编辑".equals(editType)) {
                wordListId = wordList.getId();
                headwordEdit.setText(wordList.getHeadword());
                phoneticEdit.setText(wordList.getPhonetic());
                quickdefEdit.setText(wordList.getQuickdefinition());
            }
        } else if (bean.equals("MyWordList")) {//如果是我的词库的单词
            myWordList = (MyWordList) getIntent().getSerializableExtra("myWordList");
            if ("编辑".equals(editType)) {
                myWordListId = myWordList.getId();
                headwordEdit.setText(myWordList.getHeadword());
                phoneticEdit.setText(myWordList.getPhonetic());
                quickdefEdit.setText(myWordList.getQuickdefinition());
            } else if ("新建".equals(editType)) {
                headwordEdit.setHint("这里输入单词");
                phoneticEdit.setHint("这里输入音标");
                quickdefEdit.setHint("这里输入解释");
            }
        }

        wordEditSave.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (bean.equals("MyWordList")) {
                    if ("新建".equals(editType)) {
                        MyWordList myWordList = new MyWordList();
                        myWordList.setHeadword(headwordEdit.getText().toString());
                        myWordList.setPhonetic(phoneticEdit.getText().toString());
                        myWordList.setQuickdefinition(quickdefEdit.getText().toString());
                        myWordList.setNotebookguid(MY_NOTEBOOK);
                        //新建的内容不许为空
                        if (!"".equals(headwordEdit.getText().toString()) ||
                                !"".equals(phoneticEdit.getText().toString()) ||
                                !"".equals(quickdefEdit.getText().toString())) {
                            myWordList.save();
                            Toast.makeText(WordEditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                            setResult(WORD_EDIT_OK);
                            finish();
                        }
                    } else if ("编辑".equals(editType)) {
                        ContentValues contentValues = new ContentValues();
                        contentValues.put("headword", headwordEdit.getText().toString());
                        contentValues.put("phonetic", phoneticEdit.getText().toString());
                        contentValues.put("quickdefinition", quickdefEdit.getText().toString());
                        LitePal.update(MyWordList.class, contentValues, myWordListId);//在原来的基础上更新数据;
                        Toast.makeText(WordEditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                        setResult(WORD_EDIT_OK);
                        finish();
                    }
                } else if (bean.equals("WordList")) {
                    ContentValues contentValues = new ContentValues();
                    contentValues.put("headword", headwordEdit.getText().toString());
                    contentValues.put("phonetic", phoneticEdit.getText().toString());
                    contentValues.put("quickdefinition", quickdefEdit.getText().toString());
                    LitePal.update(WordList.class, contentValues, wordListId);//在原来的基础上更新数据;
                    Toast.makeText(WordEditActivity.this, "保存成功", Toast.LENGTH_SHORT).show();
                    setResult(WORD_EDIT_OK);
                    finish();
                }
            }
        });

        wordEditBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                setResult(WORD_EDIT_OK);
                finish();
            }
        });
    }

    private void init() {
        wordEditBack = (TextView) findViewById(R.id.wordEditActivity_back);
        wordEditSave = (TextView) findViewById(R.id.wordEditActivity_save);
        headwordEdit = (EditText) findViewById(R.id.wordEditActivity_headword);
        phoneticEdit = (EditText) findViewById(R.id.wordEditActivity_phonetic);
        quickdefEdit = (EditText) findViewById(R.id.wordEditActivity_quickdefinition);
    }
}
