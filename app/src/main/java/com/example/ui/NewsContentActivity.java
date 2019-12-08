package com.example.ui;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.widget.ImageView;
import android.widget.Toast;

import com.example.seeker.R;

public class NewsContentActivity extends AppCompatActivity {
    private ImageView newsContentBack;
    private WebView webView;
    private String contentUrl;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_news_content);
        newsContentBack = (ImageView) findViewById(R.id.newsContentActivity_back);
        webView = (WebView) findViewById(R.id.newsContentActivity_webView);
        contentUrl = getIntent().getStringExtra("newsContentUrl");
        webView.getSettings().setJavaScriptEnabled(true);
        webView.setWebChromeClient(new WebChromeClient());
        if ("".equals(contentUrl)) {
            Toast.makeText(NewsContentActivity.this, "抱歉，新闻链接不存在", Toast.LENGTH_SHORT).show();
        } else {
            webView.loadUrl(contentUrl);
        }
        newsContentBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                finish();
            }
        });
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
