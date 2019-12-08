package com.example.fragment;

import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.support.v7.app.AlertDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.TextView;
import android.widget.Toast;

import com.example.adapter.WordListAdapter;
import com.example.bean.MyWordList;
import com.example.bean.TransResultBean;
import com.example.bean.TranslateResult;
import com.example.bean.WordList;
import com.example.seeker.MyApplication;
import com.example.seeker.R;
import com.example.seeker.WordListRecyclerViewListener;
import com.example.ui.WordActivity;
import com.example.ui.WordEditActivity;
import com.example.utils.GetUrlWithQueryString;
import com.example.utils.L;
import com.example.utils.MD5;
import com.example.utils.MyHttpUtil;
import com.google.gson.Gson;

import org.litepal.LitePal;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * Created by ${WLX} on 2019/7/23.
 */

public class SearchWordFragment extends Fragment {
    private static final String TAG = "SearchWordFragment";
    private RadioGroup searchRadioGroup;
    private SearchView searchView;
    private RecyclerView recyclerView;
    private List<WordList> wordLists = new ArrayList<>();
    private WordListAdapter wordListAdapter;
    private int searchType = 0;//搜索类型，0为本地搜索，1为在线搜索
    private static final int SEARCH_FRAGMENT_EDIT = 300;
    private static final int WORD_EDIT_OK = 301;
    private String searchContent;//搜索的内容
    private boolean isSearching = false;
    private static final String MY_NOTEBOOK = "1009";//我的词库对应索引
    private String currentUsername;
    private Button searchCommit;
    private TextView pressText;
    private EditText onlineSearchEdit;
    private Handler handler = new Handler();

    //在百度翻译申请的appid、securityKey
    private String appid = "自己去百度翻译API相关说明";
    private String securityKey = "自己去百度翻译API相关说明";

    @Nullable
    @Override
    public View onCreateView(@NonNull final LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        if (savedInstanceState != null) {
            String searchText = savedInstanceState.getString("searchText");
            searchView.setQuery(searchText, true);
        }
        View view = inflater.inflate(R.layout.search_word_fragment, container, false);
        searchRadioGroup = (RadioGroup) view.findViewById(R.id.wordActivity_word_search_radioGroup);
        RadioButton localSearch = (RadioButton) view.findViewById(R.id.wordActivity_localSearch);
        localSearch.setChecked(true);
        searchView = (SearchView) view.findViewById(R.id.wordActivity_word_search);
        searchView.setIconifiedByDefault(false);
        recyclerView = (RecyclerView) view.findViewById(R.id.wordActivity_searchResult_recyclerview);
        searchCommit = (Button) view.findViewById(R.id.wordActivity_word_search_commit);
        pressText = (TextView) view.findViewById(R.id.wordActivity_word_search_pressText);
        onlineSearchEdit = (EditText) view.findViewById(R.id.word_onlineSearch_result);
        wordListAdapter = new WordListAdapter(new WordListRecyclerViewListener() {
            @Override
            public void onItemClick(View view, WordList wordList) {
                //跳转到另一个activity进行修改
                Intent intent = new Intent(getActivity(), WordEditActivity.class);
                Bundle bundle = new Bundle();
                bundle.putSerializable("wordList", wordList);
                bundle.putString("type", "编辑");
                bundle.putString("bean", "WordList");
                intent.putExtras(bundle);
                startActivityForResult(intent, SEARCH_FRAGMENT_EDIT);
            }

            @Override
            public void onItemLongClick(View view, final WordList wordList) {
                //如果已经在我的词库中的，弹出提示，否则长按添加
                //弹出一个dialog
                String notebookguid = wordList.getNotebookguid();
                if (notebookguid.equals(MY_NOTEBOOK)) {
                    Toast.makeText(getActivity(), "该单词已在我的词库中", Toast.LENGTH_SHORT).show();
                } else {
                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    AlertDialog alertDialog = builder.setTitle("系统提示：")
                            .setMessage("确定把这个单词加入我的词库吗？")
                            .setNegativeButton("取消", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                }
                            })
                            .setPositiveButton("确定", new DialogInterface.OnClickListener() {
                                @Override
                                public void onClick(DialogInterface dialogInterface, int i) {
                                    MyWordList myWordList = new MyWordList();
                                    myWordList.setHeadword(wordList.getHeadword());
                                    myWordList.setPhonetic(wordList.getPhonetic());
                                    myWordList.setNotebookguid(MY_NOTEBOOK);
                                    myWordList.setQuickdefinition(wordList.getQuickdefinition());
                                    myWordList.save();
                                    Toast.makeText(getActivity(), "添加成功，快去你的词库看看吧~", Toast.LENGTH_SHORT).show();
                                    L.e(TAG, "------------->" + LitePal.where("notebookguid=?", MY_NOTEBOOK));
                                }
                            }).create();
                    alertDialog.show();
                }
            }
        }, wordLists);

        LinearLayoutManager layoutManager = new LinearLayoutManager(MyApplication.getContext());
        recyclerView.setLayoutManager(layoutManager);
        recyclerView.setAdapter(wordListAdapter);

        searchView.setQueryHint("请输入关键字");
        searchRadioGroup.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                if (checkedId == R.id.wordActivity_localSearch) {
                    searchType = 0;
                    recyclerView.setVisibility(View.VISIBLE);
                    pressText.setVisibility(View.VISIBLE);
                    onlineSearchEdit.setVisibility(View.INVISIBLE);
                    onlineSearchEdit.setText("");
                    searchView.setQueryHint("请输入关键字");
                    searchView.setFocusable(true);//搜索框可编辑
                    searchView.setFocusableInTouchMode(true);
                } else if (checkedId == R.id.wordActivity_onlineSearch) {
                    searchType = 1;
                    recyclerView.setVisibility(View.INVISIBLE);
                    pressText.setVisibility(View.INVISIBLE);
                    onlineSearchEdit.setVisibility(View.VISIBLE);
                    searchView.setQueryHint("");
                    searchView.setFocusable(false);//搜索框不可编辑
                    searchView.clearFocus();
                }
            }
        });

        searchView.setOnQueryTextListener(new SearchView.OnQueryTextListener() {
            @Override
            public boolean onQueryTextSubmit(String query) {
                return false;
            }

            @Override
            public boolean onQueryTextChange(String newText) {
                searchContent = newText;
                isSearching = true;
                return false;
            }
        });

        searchView.setOnCloseListener(new SearchView.OnCloseListener() {
            @Override
            public boolean onClose() {
                //搜索完毕要重新刷新页面
                isSearching = false;
                return false;
            }
        });

        searchCommit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (searchType == 0) {
                    if (!"".equals(searchContent)) {
                        //本地搜索,这里不加个限制10条的话可能数据量太大，app会卡死
                        List<WordList> wordLists = LitePal.limit(10).where("headword like ? or quickdefinition like ?", "%" + searchContent + "%", "%" + searchContent + "%").find(WordList.class);
                        if (!wordLists.isEmpty()) {
                            wordListAdapter.refreshShow(wordLists);
                        }
                    } else {
                        Toast.makeText(getActivity(), "请输入搜索内容", Toast.LENGTH_SHORT).show();
                    }

                } else if (searchType == 1) {
                    //在线搜索
                    final String query = onlineSearchEdit.getText().toString();//要翻译的内容
//                    new Thread(new Runnable() {
//                        @Override
//                        public void run() {
                    //方法一：使用HttpURLConnection
                    // TransApi transApi = new TransApi(appid, securityKey);//使用HttpURLConnection,如果是okhttp转HttpURLConnection，将下面那个buildParams（）删除
                    //String resultJson = transApi.getTransResult(query, "auto", "auto");//使用HttpURLConnection
//                            L.e(TAG, "得到百度翻译返回的数据：" + resultJson);
//                            //采用GSON解析JSON数据
//                            Gson gson = new Gson();
//                            TranslateResult translateResult = gson.fromJson(resultJson, TranslateResult.class);
//                            final List<TransResultBean> transResultBeans = translateResult.getTrans_result();
//                            handler.post(new Runnable() {
//                                @Override
//                                public void run() {
//                                    String dst = "";
//                                    for (TransResultBean bean : transResultBeans) {
//                                        dst = dst + "\n" + bean.getDst();
//                                    }
//                                    onlineSearchEdit.append("\n\n" + dst);
//
//
//                        }
//                    }).start();
                    //方法二：下面使用Okhttp
                    if (!"".equals(query)) {
                        Map<String, String> params = buildParams(query, "auto", "auto");//配置好接口参数
                        String host = "http://api.fanyi.baidu.com/api/trans/vip/translate";
                        String sendUrl = GetUrlWithQueryString.getUrlWithQueryString(host, params);
                        MyHttpUtil.sendOkHttpRequest(sendUrl, new Callback() {
                            @Override
                            public void onFailure(Call call, IOException e) {
                                L.e(TAG, "得到百度翻译返回的数据，获取失败");
                            }

                            @Override
                            public void onResponse(Call call, Response response) throws IOException {
                                String resultJson = response.body().string();
                                L.e(TAG, "得到百度翻译返回的数据：" + resultJson);
                                //采用GSON解析JSON数据
                                Gson gson = new Gson();
                                TranslateResult translateResult = gson.fromJson(resultJson, TranslateResult.class);
                                final List<TransResultBean> transResultBeans = translateResult.getTrans_result();
                                handler.post(new Runnable() {
                                    @Override
                                    public void run() {
                                        String dst = "";
                                        for (TransResultBean bean : transResultBeans) {
                                            dst = dst + "\n" + bean.getDst();
                                        }
                                        onlineSearchEdit.append("\n" + dst);
                                    }
                                });
                            }
                        });
                    } else {
                        Toast.makeText(getActivity(), "请输入搜索内容", Toast.LENGTH_SHORT).show();
                    }
                }
            }
        });
        return view;
    }

    private Map<String, String> buildParams(String query, String from, String to) {
        Map<String, String> params = new HashMap<String, String>();
        params.put("q", query);
        params.put("from", from);
        params.put("to", to);

        params.put("appid", appid);

        // 随机数
        String salt = String.valueOf(System.currentTimeMillis());
        params.put("salt", salt);

        // 签名
        String src = appid + query + salt + securityKey; // 加密前的原文
        params.put("sign", MD5.md5(src));

        return params;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (requestCode == SEARCH_FRAGMENT_EDIT && resultCode == WORD_EDIT_OK) {
            //如果编辑成功，按搜索内容重新刷新一次页面，如果停止搜索，清空屏幕
            if (isSearching) {
                List<WordList> wordLists = LitePal.limit(10).where("headword like ? or quickdefinition like ?", "%" + searchContent + "%", "%" + searchContent + "%").find(WordList.class);
                if (!wordLists.isEmpty()) {
                    wordListAdapter.refreshShow(wordLists);
                }
            }
        }
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        currentUsername = ((WordActivity) getActivity()).getUsername();
    }

    //不加这个的话，每次切换页面之前的就不见了
    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        outState.putString("searchText", searchContent);
        super.onSaveInstanceState(outState);
    }
}
