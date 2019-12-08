package com.example.ui;

import android.content.Intent;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.Toast;

import com.alibaba.fastjson.JSON;
import com.example.adapter.NewsItemsAdatper;
import com.example.adapter.NewsTitleAdapter;
import com.example.bean.FilmNews;
import com.example.bean.FinanceNews;
import com.example.bean.MilitrayNews;
import com.example.bean.MusicNews;
import com.example.bean.News;
import com.example.bean.NewsTitle;
import com.example.bean.PeNews;
import com.example.bean.StarNews;
import com.example.bean.TvNews;
import com.example.seeker.R;
import com.example.utils.L;
import com.example.utils.MyHttpUtil;
import com.fasterxml.jackson.databind.JavaType;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.Gson;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import okhttp3.Call;
import okhttp3.Callback;
import okhttp3.Response;

/**
 * 这个新闻API每种新闻都要分别建立一个类，解析起来就需要判断，它不像其他接口一次返回多少数据，要你自己设置
 */
public class NewsActivity extends AppCompatActivity {
    private static final String TAG = "NewsActivity";
    private ImageView back;
    private RecyclerView newsItems;
    private ListView newsTitles;
    private List<String> newsItemsList = new ArrayList<String>();
    private List<NewsTitle> newsTitleList = new ArrayList<NewsTitle>();
    private static final String TV = "电视";
    private static final String FILM = "电影";
    private static final String STAR = "明星";
    private static final String MUSIC = "音乐";
    private static final String PE = "体育";
    private static final String FINANCE = "财经";
    private static final String MILITARY = "军事";
    private String itemNamesRes[] = {TV, FILM, STAR, MUSIC, PE, FINANCE, MILITARY};
    private static final String TV_Url = "BA10TA81wangning";
    private static final String FILM_Url = "BD2A9LEIwangning";
    private static final String STAR_Url = "BD2AB5L9wangning";
    private static final String MUSIC_Url = "BD2AC4LMwangning";
    private static final String PE_Url = "BA8E6OEOwangning";
    private static final String FINANCE_Url = "BA8EE5GMwangning";
    private static final String MILITARY_Url = "BAI67OGGwangning";
    private String currentUrl = TV_Url;
    private int count = 0;//每次刷新增加15
    private NewsTitleAdapter newsTitleAdapter;
    private SwipeRefreshLayout swipeRefresh;

    @Override

    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news);
        init();
        swipeRefresh.setColorSchemeResources(R.color.colorBlue);
        //swipeRefresh.setProgressViewOffset(true,);设置距顶端偏移位置
        swipeRefresh.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                count = count + 15;
                refreshNews(currentUrl, count);
            }
        });
        for (int i = 0; i < itemNamesRes.length; i++) {
            newsItemsList.add(itemNamesRes[i]);
        }
        //配置RecyclerView
        final NewsItemsAdatper newsItemsAdatper = new NewsItemsAdatper(newsItemsList);
        LinearLayoutManager layoutManager = new LinearLayoutManager(this);
        layoutManager.setOrientation(LinearLayoutManager.HORIZONTAL);
        newsItems.setLayoutManager(layoutManager);
        newsItems.setAdapter(newsItemsAdatper);
        newsItemsAdatper.setOnItemClickListener(new NewsItemsAdatper.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String itemName = itemNamesRes[position];
                switch (itemName) {
                    case TV:
                        currentUrl = TV_Url;
                        refreshNews(currentUrl, count);
                        break;
                    case FILM:
                        currentUrl = FILM_Url;
                        refreshNews(currentUrl, count);
                        break;
                    case STAR:
                        currentUrl = STAR_Url;
                        refreshNews(currentUrl, count);
                        break;
                    case MUSIC:
                        currentUrl = MUSIC_Url;
                        refreshNews(currentUrl, count);
                        break;
                    case PE:
                        currentUrl = PE_Url;
                        refreshNews(currentUrl, count);
                        break;
                    case FINANCE:
                        currentUrl = FINANCE_Url;
                        refreshNews(currentUrl, count);
                        break;
                    case MILITARY:
                        currentUrl = MILITARY_Url;
                        refreshNews(currentUrl, count);
                        break;
                }
                Toast.makeText(NewsActivity.this, itemNamesRes[position], Toast.LENGTH_SHORT).show();
            }
        });
        //配置ListView
        newsTitleAdapter = new NewsTitleAdapter(NewsActivity.this, R.layout.news_title_item, newsTitleList);
        newsTitles.setAdapter(newsTitleAdapter);
        newsTitles.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                Intent intent = new Intent(NewsActivity.this, NewsContentActivity.class);
                intent.putExtra("newsContentUrl", newsTitleList.get(position).getNewsUrl());
                startActivity(intent);
            }
        });
        refreshNews(TV_Url, count);//默认显示电视类节目的前十五条新闻
        back.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    private void init() {
        back = (ImageView) findViewById(R.id.newsActivity_back);
        newsItems = (RecyclerView) findViewById(R.id.newsActivity_items);
        newsTitles = (ListView) findViewById(R.id.newsActivity_title);
        swipeRefresh = (SwipeRefreshLayout) findViewById(R.id.newsActivity_swipRefresh);
    }

    private void refreshNews(final String currentUrl, int count) {
        //返回currentUrl类新闻的第count~count+15 条消息，这是个野生的接口，有点难用
        MyHttpUtil.sendOkHttpRequest("https://3g.163.com/touch/reconstruct/article/list/" + currentUrl +
                "/" + count + "-" + 15 + ".html", new Callback() {
            @Override
            public void onFailure(Call call, IOException e) {
            }

            @Override
            public void onResponse(Call call, Response response) throws IOException {
                String str = response.body().string();
                String newStr = str.substring(9, str.length() - 1);//因为得到的数据有不规则的地方artlist()，所以要截取,野生API有点难用
                L.e(TAG, newStr);
                //用JSONArray解析电视新闻，解析成功
//                try {
//                    JSONObject object = new JSONObject(newStr);//先把最外层看做一个对象，这个对象里包含很多新闻对象
//                    String  tvNews=object.getString("BA10TA81wangning");//取得这个对象
//                    JSONArray array = new JSONArray(tvNews);//创建数组开始解析新闻
//                    for (int i = 0; i < array.length(); i++) {
//                        JSONObject jsonObject = array.getJSONObject(i);
//                        L.e(TAG,"----------->"+jsonObject);
//                        String title = jsonObject.getString("title");
//                        String ptime = jsonObject.optString("ptime");
//                        String imgsrc = jsonObject.optString("imgsrc");
//                        String url = jsonObject.optString("url");
//                        String source = jsonObject.optString("source");
//                        NewsTitle newsTitle = new NewsTitle();
//                        newsTitle.setText(title);
//                        newsTitle.setImageurl(imgsrc);
//                        newsTitle.setTime(ptime);
//                        newsTitle.setNewsUrl(url);
//                        newsTitle.setSrc(source);
//                        newsTitleList.add(newsTitle);
//                    }
//                    runOnUiThread(new Runnable() {
//                        @Override
//                        public void run() {
//                            newsTitleAdapter.notifyDataSetChanged();
//                            swipeRefresh.setRefreshing(false);//加载数据的时候关闭刷新状态，不然一直在屏幕上面转
//                        }
//                    });
//                } catch (JSONException e) {
//                    e.printStackTrace();
//                }

                //用Fastjson解析新闻，可以按需解析，也就是说bean只写需要解析的属性就行
                List<News> news = null;
                switch (currentUrl) {
                    case TV_Url:
                        TvNews tvNews = JSON.parseObject(newStr, TvNews.class);
                        news = tvNews.getBA10TA81wangning();
                        break;
                    case FILM_Url:
                        FilmNews filmNews = JSON.parseObject(newStr, FilmNews.class);
                        news = filmNews.getBD2A9LEIwangning();
                        break;
                    case STAR_Url:
                        StarNews starNews = JSON.parseObject(newStr, StarNews.class);
                        news = starNews.getBD2AB5L9wangning();
                        break;
                    case MUSIC_Url:
                        MusicNews musicNews = JSON.parseObject(newStr, MusicNews.class);
                        news = musicNews.getBD2AC4LMwangning();
                        break;
                    case PE_Url:
                        PeNews peNews = JSON.parseObject(newStr, PeNews.class);
                        news = peNews.getBA8E6OEOwangning();
                        break;
                    case FINANCE_Url:
                        FinanceNews financeNews = JSON.parseObject(newStr, FinanceNews.class);
                        news = financeNews.getBA8EE5GMwangning();
                        break;
                    case MILITARY_Url:
                        MilitrayNews militrayNews = JSON.parseObject(newStr, MilitrayNews.class);
                        news = militrayNews.getBAI67OGGwangning();
                        break;
                }
                newsTitleList.clear();
                for (News news1 : news) {
                    NewsTitle newsTitle = new NewsTitle();
                    newsTitle.setText(news1.getTitle());
                    newsTitle.setImageurl(news1.getImgsrc());
                    newsTitle.setTime(news1.getPtime());
                    newsTitle.setNewsUrl(news1.getUrl());
                    L.e(TAG,news1.getUrl());
                    newsTitle.setSrc(news1.getSource());
                    newsTitleList.add(newsTitle);
                }
                runOnUiThread(new Runnable() {
                    @Override
                    public void run() {
                        newsTitleAdapter.notifyDataSetChanged();
                        swipeRefresh.setRefreshing(false);//加载数据的时候关闭刷新状态，不然一直在屏幕上面转

                    }
                });

                //使用Jackson解析电视新闻，没成功，，，，，待解决
//                ObjectMapper objectMapper = new ObjectMapper();
//                L.e(TAG, "使用Jackson解析电视新闻前");
//                TvNews tvNews = objectMapper.readValue(newStr, TvNews.class);
//                List<News> newsList=tvNews.getBA10TA81wangning();
//                newsTitleList.clear();
//                for (News news1 : newsList) {
//                    NewsTitle newsTitle = new NewsTitle();
//                    newsTitle.setText(news1.getTitle());
//                    newsTitle.setImageurl(news1.getImgsrc());
//                    newsTitle.setTime(news1.getPtime());
//                    newsTitle.setNewsUrl(news1.getUrl());
//                    newsTitle.setSrc(news1.getSource());
//                    newsTitleList.add(newsTitle);
//                }
//                L.e(TAG, "使用Jackson解析电视新闻后");
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        newsTitleAdapter.notifyDataSetChanged();
//                        swipeRefresh.setRefreshing(false);//加载数据的时候关闭刷新状态，不然一直在屏幕上面转
//
//                    }
//                });


                //用GSON解析所有新闻，可以按需解析，也就是说bean只写需要解析的属性就行，解析成功
//                List<News> news = null;
//                L.e(TAG, newStr);
//                Gson gson = new Gson();
//                switch (currentUrl) {
//                    case TV_Url:
//                        TvNews tvNews = gson.fromJson(newStr, TvNews.class);
//                        news = tvNews.getBA10TA81wangning();
//                        break;
//                    case FILM_Url:
//                        FilmNews filmNews = gson.fromJson(newStr, FilmNews.class);
//                        news = filmNews.getBD2A9LEIwangning();
//                        break;
//                    case STAR_Url:
//                        StarNews starNews = gson.fromJson(newStr, StarNews.class);
//                        news = starNews.getBD2AB5L9wangning();
//                        break;
//                    case MUSIC_Url:
//                        MusicNews musicNews = gson.fromJson(newStr, MusicNews.class);
//                        news = musicNews.getBD2AC4LMwangning();
//                        break;
//                    case PE_Url:
//                        PeNews peNews = gson.fromJson(newStr, PeNews.class);
//                        news = peNews.getBA8E6OEOwangning();
//                        break;
//                    case FINANCE_Url:
//                        FinanceNews financeNews = gson.fromJson(newStr, FinanceNews.class);
//                        news = financeNews.getBA8EE5GMwangning();
//                        break;
//                    case MILITARY_Url:
//                        MilitrayNews militrayNews = gson.fromJson(newStr, MilitrayNews.class);
//                        news = militrayNews.getBAI67OGGwangning();
//                        break;
//                }
//                newsTitleList.clear();
//                for (News news1 : news) {
//                    NewsTitle newsTitle = new NewsTitle();
//                    newsTitle.setText(news1.getTitle());
//                    newsTitle.setImageurl(news1.getImgsrc());
//                    newsTitle.setTime(news1.getPtime());
//                    newsTitle.setNewsUrl(news1.getNews_url());
//                    newsTitle.setSrc(news1.getSource());
//                    newsTitleList.add(newsTitle);
//                }
//                runOnUiThread(new Runnable() {
//                    @Override
//                    public void run() {
//                        newsTitleAdapter.notifyDataSetChanged();
//                        swipeRefresh.setRefreshing(false);//加载数据的时候关闭刷新状态，不然一直在屏幕上面转
//
//                    }
//                });
            }
        });
    }
}
