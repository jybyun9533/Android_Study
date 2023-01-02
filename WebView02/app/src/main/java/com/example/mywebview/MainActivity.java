package com.example.mywebview;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.webkit.JavascriptInterface;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;
    private Button mBtn; // 버튼추가
    
    //    private String myUrl = "https://www.naver.com";
    private String myUrl = "file:///android_asset/testHTML.html";

    // webView로 넘길 count
    private int cnt = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // findViewById
        myWebView = (WebView) findViewById(R.id.webView);
        mBtn = (Button) findViewById(R.id.btn); // 버튼 추가

        // 웹 URL 설정
        myWebView.loadUrl(myUrl);

        // 어플 내 웹 띄우기
        myWebView.setWebViewClient(new WebViewClient());

        // 디버깅 허용
        myWebView.setWebContentsDebuggingEnabled(true);

        // WebSettings
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // javascript를 사용할 경우 다음과 같이 설정

        // JavascriptInterface 연결(bridge) :: Android
        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");

        // 버튼 클릭 구현
        mBtn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                myWebView.loadUrl("javascript:AndroidToSend('버튼 누른 횟수 = "+cnt+"회')");
                cnt++;
            }
        });
    }

    // 뒤로가기 설정
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            myWebView.goBack();
            return true;
        }
        // If it wasn't the Back key or there's no web page history, bubble up to the default
        // system behavior (probably exit the activity)
        return super.onKeyDown(keyCode, event);
    }

    public class WebAppInterface {
        Context mContext;

        /**
         * Instantiate the interface and set the context
         */
        WebAppInterface(Context c) {
            mContext = c;
        }

        /**
         * Show a toast from the web page
         */
        @JavascriptInterface
        public void showToast(String toast) {
            Toast.makeText(mContext, toast, Toast.LENGTH_SHORT).show();
        }
    }

}