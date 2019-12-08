package com.example.fragment;

import android.content.ContentValues;
import android.opengl.Visibility;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.text.Editable;
import android.text.TextWatcher;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.bean.AllReciteWord;
import com.example.bean.MyWordList;
import com.example.bean.Process;
import com.example.bean.WordList;
import com.example.seeker.R;
import com.example.ui.WordActivity;
import com.example.utils.L;

import org.litepal.LitePal;
import org.litepal.crud.LitePalSupport;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.Random;

/**
 * Created by ${WLX} on 2019/7/23.
 */

public class ReciteWordFragment extends Fragment implements View.OnClickListener {
    private static final String TAG = "ReciteWordFragment";
    private RadioGroup radioGroup;
    private RadioButton reciteRadioBtn;
    private RadioButton testRdioBtn;
    private Spinner chooseWarehouse;
    private EditText reciteHeadWord;
    private EditText reciteQhonetic;
    private EditText recitePhonetic;
    private EditText answerHeadWord;
    private EditText answerQhonetic;
    private EditText answerPhonetic;
    private Button lastOne;
    private Button nextOne;
    private Button answer;
    private Button jumpOk;
    private TextView nowLocation;
    private TextView total;
    private EditText jumpTo;
    private int mode = 0;//模式：0背诵模式，1测试模式，在radiobutton中切换改变
    List<String> spinnerMenu = null;
    private int currentRecite = 0;//当前背诵进度,第一个
    private int currentTest = 0;//当前测试进度，第一个
    private String currentGuid = "1001";//当前词库，默认是大学英语四级
    private String oldGuid = "1001";//用于记录上一个词库，当词库改变时能及时保存数据
    private int currentTotal;//当前词库所含总数
    //今天时间
    int m_year;
    int m_month;
    int m_day;
    private List<WordList> wordLists = null;
    private Calendar calendar;
    private int answerClickTime;//答案按键次数
    private int commitClickTime;//提交按键次数
    private String reciteHeadContent;//背诵单词内容
    private String reciteQhoneticContent;//背诵单词翻译
    private int reciteFlag;//0测试单词，1测试翻译
    private int wordListId;
    private String currentUsername;
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.recite_word_fragment, container, false);
        radioGroup = (RadioGroup) view.findViewById(R.id.reciteFragment_radioGroup);
        reciteRadioBtn = (RadioButton) view.findViewById(R.id.reciteFragment_reciteRadiobutton);
        testRdioBtn = (RadioButton) view.findViewById(R.id.reciteFragment_testRadiobutton);
        chooseWarehouse = (Spinner) view.findViewById(R.id.reciteFragment_spinner);
        reciteHeadWord = (EditText) view.findViewById(R.id.reciteFragment_headword);
        recitePhonetic = (EditText) view.findViewById(R.id.reciteFragment_phonetic);
        reciteQhonetic = (EditText) view.findViewById(R.id.reciteFragment_quickdefinition);
        answerHeadWord = (EditText) view.findViewById(R.id.reciteFragment_answerHeadword);
        answerQhonetic = (EditText) view.findViewById(R.id.reciteFragment_answerQuickdefinition);
        answerPhonetic = (EditText) view.findViewById(R.id.reciteFragment_answerPhonetic);
        lastOne = (Button) view.findViewById(R.id.reciteFragment_lastOne);
        nextOne = (Button) view.findViewById(R.id.reciteFragment_nextOne);
        answer = (Button) view.findViewById(R.id.reciteFragment_answer);
        jumpOk = (Button) view.findViewById(R.id.reciteFragment_jumpOk);
        nowLocation = (TextView) view.findViewById(R.id.reciteFragment_nowLocation);
        total = (TextView) view.findViewById(R.id.reciteFragment_total);
        jumpTo = (EditText) view.findViewById(R.id.reciteFragment_jumpToLocation);
        lastOne.setOnClickListener(this);
        nextOne.setOnClickListener(this);
        jumpOk.setOnClickListener(this);
        answer.setOnClickListener(this);
        //今天时间
        calendar = Calendar.getInstance();
        m_year = calendar.get(Calendar.YEAR);
        m_month = calendar.get(Calendar.MONTH);
        m_day = calendar.get(Calendar.DAY_OF_MONTH);
        //设置下拉列表菜单
        ArrayAdapter<String> adapter = new ArrayAdapter<String>(getActivity(), R.layout.support_simple_spinner_dropdown_item, getData());
        chooseWarehouse.setAdapter(adapter);
        reciteRadioBtn.setChecked(true);//默认是背单词
        answer.setVisibility(View.INVISIBLE);//背单词模式下答案按钮默认不显示
        answer.setEnabled(false);//不可按
        //背诵状态下不可编辑
        reciteHeadWord.setFocusableInTouchMode(false);
        reciteHeadWord.setFocusable(false);
        recitePhonetic.setFocusableInTouchMode(false);
        recitePhonetic.setFocusable(false);
        reciteQhonetic.setFocusableInTouchMode(false);
        reciteQhonetic.setFocusable(false);
        //每次换库都要根据当前的表刷新下方的第多少个那里
        List<Process> processes = LitePal.where("guid=? ", currentGuid).find(Process.class);
        if (!processes.isEmpty()) {
            Process process = processes.get(0);
            currentRecite = process.getCurrentReciteId();
            currentTest = process.getCurrentTestId();
            //currentTotal = processes.size()+1;不能这么算。我们应该算guid对应的词库的单词
        }
        CaculateCurrentTotal();
        Display();
//        //全部已背、昨天已背、今天已背的不算在已背单词里面
//        if (currentGuid.equals("1010") || currentGuid.equals("1011") || currentGuid.equals("1012")) {
//        } else {
//            SaveAllReciteWord();
//        }
        chooseWarehouse.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                CloseAnswer();
                answer.setText("答案");
                chooseWarehouse.setSelection(position);
                SaveProcess();//用户切换词库要保存下当前词库的进度，要先保存再换库
                switch (position) {
                    case 0:
                        currentGuid = "1001";
                        break;
                    case 1:
                        currentGuid = "1002";
                        break;
                    case 2:
                        currentGuid = "1003";
                        break;
                    case 3:
                        currentGuid = "1004";
                        break;
                    case 4:
                        currentGuid = "1005";
                        break;
                    case 5:
                        currentGuid = "1006";
                        break;
                    case 6:
                        currentGuid = "1007";
                        break;
                    case 7:
                        currentGuid = "1008";
                        break;
                    case 8:
                        currentGuid = "1010";
                        break;
                    case 9:
                        currentGuid = "1011";
                        break;
                    case 10:
                        currentGuid = "1012";
                        break;
                }
                L.e(TAG, "------------>" + currentGuid);
                List<Process> processes = null;
                processes = LitePal.where("guid=?", currentGuid).find(Process.class);
                Process process = processes.get(0);
                currentRecite = process.getCurrentReciteId();
                currentTest = process.getCurrentTestId();

                CaculateCurrentTotal();
                //改变下方的第多少个那里
                Display();//改变词库要刷新界面，在Display()里面要对currrentRecite和currentTest进行一些改变,如果是我的词库，取的方式有点不同
                //全部已背、昨天已背、今天已背的不算在已背单词里面
                if (currentGuid.equals("1010") || currentGuid.equals("1011") || currentGuid.equals("1012")) {
                } else {
                    SaveAllReciteWord();
                }
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {
                chooseWarehouse.setSelection(0);
            }
        });
        /**
         * 改变模式的单词不用放到全部已背中
         */
        radioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.reciteFragment_reciteRadiobutton) {
                    mode = 0;
                    nextOne.setText("下一个");
                    //背诵状态下不可编辑
                    reciteHeadWord.setFocusableInTouchMode(false);
                    reciteHeadWord.setFocusable(false);
                    recitePhonetic.setFocusableInTouchMode(false);
                    recitePhonetic.setFocusable(false);
                    reciteQhonetic.setFocusableInTouchMode(false);
                    reciteQhonetic.setFocusable(false);
                    SaveProcess();//用户切换模式也要保存下当前词库的进度,切换模式当前的guid没有变,主要是保存currentTest
                    Display();//切换要刷新界面，在Display()里面根据currrentRecite刷新
                    answer.setVisibility(View.INVISIBLE);//背单词模式下答案按钮默认不显示
                    answer.setEnabled(false);//不可按
                    CloseAnswer();//关闭答案
                    jumpTo.setText("");
                } else if (checkedId == R.id.reciteFragment_testRadiobutton) {
                    mode = 1;
                    nextOne.setText("提交");//切换到测试模式，下一个变成提交
                    SaveProcess();//用户切换模式也要保存下当前词库的进度，主要是保存currentRecite
                    Display();//切换要刷新界面，在Display()里面根据currrentTest刷新
                    answer.setVisibility(View.VISIBLE);//测试模式下答案按钮默认显示
                    answer.setEnabled(true);//可按
                    jumpTo.setText("");
                }
            }
        });

        return view;

    }


    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentUsername=((WordActivity) getActivity()).getUsername();
    }

    /**
     * 如果一张表比如Wordlist中都没有数据wordlist，就比如你新建了一张表，那么你get里面的东西是会出异常的，所以需要进行非空判断先
     */
    private void Display() {
        LocationDisplay();
        WordList wordList = ContentDisplay();
        if (wordList != null) {
            wordListId = wordList.getId();
            reciteHeadContent = wordList.getHeadword();
            String phonetic = wordList.getPhonetic();
            reciteQhoneticContent = wordList.getQuickdefinition();
            if (mode == 0) {
                reciteHeadWord.setText(reciteHeadContent);
                recitePhonetic.setText(phonetic);
                reciteQhonetic.setText(reciteQhoneticContent);
            } else if (mode == 1) {
                //测试模式下单词和解释出现任意不固定
                Random random = new Random();
                int i = random.nextInt(20);
                if (i % 2 == 0) {
                    reciteFlag = 1;
                    reciteHeadWord.setText(reciteHeadContent);
                    recitePhonetic.setText(phonetic);
                    reciteQhonetic.setText("");
                    reciteQhonetic.setHint("请写出单词翻译");
                    //背诵状态下Qhonetic可编辑
                    reciteHeadWord.setFocusableInTouchMode(false);
                    reciteHeadWord.setFocusable(false);
                    recitePhonetic.setFocusableInTouchMode(false);
                    recitePhonetic.setFocusable(false);
                    reciteQhonetic.setFocusableInTouchMode(true);
                    reciteQhonetic.setFocusable(true);
                } else if (i % 2 == 1) {
                    reciteFlag = 0;
                    reciteHeadWord.setText("");
                    reciteHeadWord.setHint("请写出单词");
                    recitePhonetic.setText(phonetic);
                    reciteQhonetic.setText(reciteQhoneticContent);
                    //背诵状态下Qhonetic可编辑
                    reciteHeadWord.setFocusableInTouchMode(true);
                    reciteHeadWord.setFocusable(true);
                    recitePhonetic.setFocusableInTouchMode(false);
                    recitePhonetic.setFocusable(false);
                    reciteQhonetic.setFocusableInTouchMode(false);
                    reciteQhonetic.setFocusable(false);
                }
            }
        } else {
            if (mode == 0) {
                reciteHeadWord.setText("");
                recitePhonetic.setText("");
                reciteQhonetic.setText("");
            } else if (mode == 1) {
                reciteHeadWord.setHint("");
                recitePhonetic.setHint("");
                reciteQhonetic.setHint("");
                reciteHeadWord.setText("");
                recitePhonetic.setText("");
                reciteQhonetic.setText("");
            }

        }
    }

    private WordList ContentDisplay() {
        WordList wordList = null;
        List<AllReciteWord> allReciteWords = LitePal.findAll(AllReciteWord.class);
        if (currentGuid.equals("1010")) {//如果词库选为"全部已背"
            if (!allReciteWords.isEmpty()) { L.e(TAG, "--------------<" + currentGuid + "非空");
                if (mode == 0) {
                    List<AllReciteWord> reciteWords = LitePal.findAll(AllReciteWord.class);
                    AllReciteWord reciteWord = reciteWords.get(currentRecite);
                    wordList = LitePal.find(WordList.class, reciteWord.getWordid());
                } else if (mode == 1) {
                    List<AllReciteWord> testWords = LitePal.findAll(AllReciteWord.class);
                    AllReciteWord testWord = testWords.get(currentTest);
                    wordList = LitePal.find(WordList.class, testWord.getWordid());
                }

            } else {
                Toast.makeText(getActivity(), "空空如也", Toast.LENGTH_SHORT).show();
                L.e(TAG, "--------------<" + currentGuid + "空");
            }
        } else if (currentGuid.equals("1011")) {//如果词库选为"昨天已背"
            if (!allReciteWords.isEmpty()) {
                List<AllReciteWord> yestodayWords = LitePal.where("month=? and day=?", Integer.toString(m_month), Integer.toString(m_day - 1)).find(AllReciteWord.class);
                if (!yestodayWords.isEmpty()) {
                    if (mode == 0) {
                        wordList = LitePal.find(WordList.class, yestodayWords.get(currentRecite).getWordid());
                    } else if (mode == 1) {
                        wordList = LitePal.find(WordList.class, yestodayWords.get(currentTest).getWordid());
                    }
                }
            }
        } else if (currentGuid.equals("1012")) {//如果词库选为"今天已背"
            if (!allReciteWords.isEmpty()) {
                List<AllReciteWord> todayWords = LitePal.where("month=? and day=?", Integer.toString(m_month), Integer.toString(m_day)).find(AllReciteWord.class);
                if (!todayWords.isEmpty()) {
                    if (mode == 0) {
                        wordList = LitePal.find(WordList.class, todayWords.get(currentRecite).getWordid());
                    } else if (mode == 1) {
                        wordList = LitePal.find(WordList.class, todayWords.get(currentTest).getWordid());
                    }
                }
            }
        } else {//如果是其它词库
            if (mode == 0) {
                List<WordList> wordList1 = LitePal.where("notebookguid=? ", currentGuid).find(WordList.class);
                wordList = wordList1.get(currentRecite);
            } else if (mode == 1) {
                List<WordList> wordList1 = LitePal.where("notebookguid=? ", currentGuid).find(WordList.class);
                wordList = wordList1.get(currentTest);
            }
        }
        return wordList;
    }

    private void LocationDisplay() {
        if (mode == 0) {
            nowLocation.setText(Integer.toString(currentRecite+1));//currentRecite从0开始的，currentTotal为100，那么currentRecite
        } else if (mode == 1) {
            nowLocation.setText(Integer.toString(currentTest+1));//currentTest从0开始的
        }
        total.setText(Integer.toString(currentTotal));
    }

    private void CaculateCurrentTotal() {
        if (currentGuid.equals("1010")) {//如果词库选为"全部已背"
            currentTotal = LitePal.findAll(AllReciteWord.class).size();
        } else if (currentGuid.equals("1011")) {//如果词库选为"昨天已背"
            List<AllReciteWord> yestodayWords = LitePal.where("month=? and day=?", Integer.toString(m_month), Integer.toString(m_day - 1)).find(AllReciteWord.class);
            if (!yestodayWords.isEmpty()) {
                currentTotal = yestodayWords.size();
            } else {
                currentTotal = 0;
                Toast.makeText(getActivity(), "昨天的空空如也", Toast.LENGTH_SHORT).show();
            }
        } else if (currentGuid.equals("1012")) {//如果词库选为"今天已背"
            List<AllReciteWord> todayWords = LitePal.where("month=? and day=?", Integer.toString(m_month), Integer.toString(m_day)).find(AllReciteWord.class);
            if (!todayWords.isEmpty()) {
                currentTotal = todayWords.size();
            } else {
                currentTotal = 0;
                Toast.makeText(getActivity(), "今天的空空如也", Toast.LENGTH_SHORT).show();
            }
        } else {//如果是其它词库
            currentTotal = LitePal.where("notebookguid=?", currentGuid).find(WordList.class).size();
        }
    }

    private void SaveProcess() {
        List<Process> processes = LitePal.where("guid=?", currentGuid).find(Process.class);
        Process process = processes.get(0);
        int processId = process.getId();
        ContentValues values = new ContentValues();
        values.put("currentReciteId", currentRecite);
        values.put("total", currentTotal);
        values.put("currentTestId", currentTest);
        LitePal.update(Process.class, values, processId);//在原来的基础上更新数据
    }


    public List<String> getData() {
        spinnerMenu = new ArrayList<String>();
        spinnerMenu.add("大学英语四级");//对应的Guid是1001
        spinnerMenu.add("大学英语六级");//1002
        spinnerMenu.add("考研词汇");//1003
        spinnerMenu.add("出国考试(G)");//1004
        spinnerMenu.add("出国考试(GM)");//1005
        spinnerMenu.add("国外生活词汇");//1006
        spinnerMenu.add("高考词汇");//1007
        spinnerMenu.add("出国考试(T)");//1008
        spinnerMenu.add("全部已背");//1010
        spinnerMenu.add("昨天已背");//1011
        spinnerMenu.add("今天已背");//1012
        return spinnerMenu;
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.reciteFragment_lastOne:
                //背诵模式下，每按一次上一个，currentrecite-1,测试模式下currenttest-1
                if (mode == 0) {
                    if ((currentRecite - 1) < 0) {
                        Toast.makeText(getActivity(), "已经是第一个", Toast.LENGTH_SHORT).show();
                    } else {
                        SaveProcess();//用户按上一个也要保存下当前词库的进度
                        currentRecite = currentRecite - 1;
                        SaveAllReciteWord();
                    }
                } else if (mode == 1) {
                    if ((currentTest - 1) < 0) {
                        Toast.makeText(getActivity(), "已经是第一个", Toast.LENGTH_SHORT).show();
                    } else {
                        SaveProcess();//用户按上一个也要保存下当前词库的进度
                        currentTest = currentTest - 1;
                    }
                }
                Display();//上一个要界面，在Display()里面要对currrentRecite和currentTest进行一些改变
                CloseAnswer();
                answer.setText("答案");
                break;
            case R.id.reciteFragment_nextOne:
                //背诵模式下，每按一次上一个，currentrecite+1,测试模式下currenttest+1
                if (mode == 0) {
                    if ((currentRecite + 1) > (currentTotal - 1)) {
                        Toast.makeText(getActivity(), "已经是最后一个", Toast.LENGTH_SHORT).show();
                    } else {
                        SaveProcess();//用户按下一个也要保存下当前词库的进度
                        currentRecite = currentRecite + 1;
                    }
                    Display();//下一个要界面，在Display()里面要对currrentRecite和currentTest进行一些改变
                    SaveAllReciteWord();
                } else if (mode == 1) {
                    if (commitClickTime % 2 == 0) {
                        judge();
                        nextOne.setText("下一个");
                        commitClickTime = commitClickTime + 1;
                    } else if (commitClickTime % 2 == 1) {
                        if ((currentTest + 1) > (currentTotal - 1)) {
                            Toast.makeText(getActivity(), "已经是最后一个", Toast.LENGTH_SHORT).show();
                        } else {
                            SaveProcess();//用户按下一个也要保存下当前词库的进度
                            currentTest = currentTest + 1;
                        }
                        nextOne.setText("提交");
                        commitClickTime = commitClickTime + 1;
                        Display();//下一个要界面，在Display()里面要对currrentRecite和currentTest进行一些改变
                    }
                }
                CloseAnswer();
                answer.setText("答案");
                break;
            case R.id.reciteFragment_answer:
                answerClickTime++;
                if (answerClickTime == 2) {
                    answerClickTime = 0;
                }
                if (answerClickTime % 2 == 1) {
                    OpenAnswer();
                    DisplayAnswer();
                    answer.setText("关闭");
                } else {
                    CloseAnswer();
                    answer.setText("答案");
                }
                break;
            case R.id.reciteFragment_jumpOk:
                CloseAnswer();
                answer.setText("答案");
                int jumpLocation = Integer.parseInt(jumpTo.getText().toString())-1;//Integet.getInteger用不了
                if (mode == 0) {
                    if (jumpLocation < 0 || jumpLocation > currentTotal - 1) {
                        Toast.makeText(getActivity(), "请输入1~" + (currentTotal ) + "的数字", Toast.LENGTH_SHORT).show();
                    } else {
                        currentRecite = jumpLocation;//当前背诵进度为跳转后进度
                        SaveProcess();//用户跳到下一个也要保存下当前词库的进度

                        SaveAllReciteWord();
                    }
                } else if (mode == 1) {
                    if (jumpLocation < 0 || jumpLocation > currentTotal - 1) {
                        Toast.makeText(getActivity(), "请输入1~" + (currentTotal ) + "的数字", Toast.LENGTH_SHORT).show();
                    } else {
                        SaveProcess();//用户跳到下一个也要保存下当前词库的进度
                        currentTest = jumpLocation;//当前背诵进度为跳转后进度
                    }
                }
                Display();//跳转要界面，在Display()里面要对currrentRecite和currentTest进行一些改变
                break;
        }
    }

    private void judge() {
        if (reciteFlag == 0) {
            if (reciteHeadContent.equals(reciteHeadWord.getText().toString())) {
                Toast.makeText(getActivity(), "太棒啦，完全正确", Toast.LENGTH_SHORT).show();

            } else {
                Toast.makeText(getActivity(), "还差一点，加油", Toast.LENGTH_SHORT).show();
            }
        } else {
            if (reciteQhoneticContent.equals(reciteQhonetic.getText().toString())) {
                Toast.makeText(getActivity(), "太棒啦，完全正确", Toast.LENGTH_SHORT).show();
            } else {
                Toast.makeText(getActivity(), "还差一点，加油", Toast.LENGTH_SHORT).show();
            }
        }

    }

    private void DisplayAnswer() {
        WordList wordList = ContentDisplay();
        answerHeadWord.setText(wordList.getHeadword());
        answerPhonetic.setText(wordList.getPhonetic());
        answerQhonetic.setText(wordList.getQuickdefinition());
    }

    private void CloseAnswer() {
        answerHeadWord.setVisibility(View.INVISIBLE);
        answerPhonetic.setVisibility(View.INVISIBLE);
        answerQhonetic.setVisibility(View.INVISIBLE);
    }

    private void OpenAnswer() {
        answerHeadWord.setVisibility(View.VISIBLE);
        answerPhonetic.setVisibility(View.VISIBLE);
        answerQhonetic.setVisibility(View.VISIBLE);
    }

    /**
     * 在刚进入背单词界面、上一个、下一个、跳至、切换词库进行已背单词的保存
     */
    private void SaveAllReciteWord() {
        if (wordListId != 0) {
            //今天时间
            m_year = calendar.get(Calendar.YEAR);
            m_month = calendar.get(Calendar.MONTH);
            m_day = calendar.get(Calendar.DAY_OF_MONTH);
            AllReciteWord allReciteWord = new AllReciteWord();
            allReciteWord.setWordid(wordListId);
            allReciteWord.setWordguid(currentGuid);
            allReciteWord.setYear(m_year);
            allReciteWord.setMonth(m_month);
            allReciteWord.setDay(m_day);
            allReciteWord.save();
        }
    }
}
