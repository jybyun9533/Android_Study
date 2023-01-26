package com.example.mywebview;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Activity;
import android.app.Dialog;
import android.content.Context;
import android.content.DialogInterface;
import android.os.Bundle;
import android.os.Message;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.webkit.JavascriptInterface;
import android.webkit.WebChromeClient;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    private WebView myWebView;
    private WebView myWebViewPop;


    private String myUrl = "file:///android_asset/testHTML.html";

    // webView로 넘길 count
    private int cnt = 0;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        // findViewById
        myWebView = (WebView) findViewById(R.id.webView);

        // 웹 URL 설정
        myWebView.loadUrl(myUrl);

        // 어플 내 웹 띄우기
        myWebView.setWebViewClient(new WebViewClient());
        // 디버깅 허용
        myWebView.setWebContentsDebuggingEnabled(true);

        // WebSettings
        WebSettings webSettings = myWebView.getSettings();
        webSettings.setJavaScriptEnabled(true); // javascript를 사용할 경우 다음과 같이 설정
        webSettings.setJavaScriptCanOpenWindowsAutomatically(true); // 자바스크립트가 window.open()을 사용가능
        webSettings.setSupportMultipleWindows(true); // 다중윈도우 허용

        myWebView.setWebChromeClient(new MyWebChromeClient(this));

        // JavascriptInterface 연결(bridge) :: Android
//        myWebView.addJavascriptInterface(new WebAppInterface(this), "Android");
    }

    // 뒤로가기 설정
    @Override
    public boolean onKeyDown(int keyCode, KeyEvent event) {
        //MyLog.toastMakeTextShow(view.getContext(), "TAG", "KEYCODE_BACK");
        Log.d("변준영", "onKeyDown");
        // Check if the key event was the Back button and if there's history
        if ((keyCode == KeyEvent.KEYCODE_BACK) && myWebView.canGoBack()) {
            Log.d("변준영", "onKeyDown : 뒤로가기");
            myWebView.goBack();
            return true;
        }
        Log.d("변준영", "onKeyDown : 뒤로가기 해당없음");
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

    private class MyWebChromeClient extends WebChromeClient {
        private Activity mActivity = null;

        public MyWebChromeClient(Activity activity) {
            this.mActivity = activity;
        }

        @Override
        public boolean onCreateWindow(WebView view, boolean isDialog, boolean isUserGesture, Message resultMsg) {
            myWebViewPop = new WebView(view.getContext());
            WebSettings webSettings = myWebViewPop.getSettings();
            webSettings.setJavaScriptEnabled(true);

            myWebViewPop.setWebViewClient(new WebViewClient());

            final Dialog dialog = new Dialog(view.getContext());
            dialog.setContentView(myWebViewPop);


            ViewGroup.LayoutParams params = dialog.getWindow().getAttributes();
            params.width = ViewGroup.LayoutParams.MATCH_PARENT;
            params.height = ViewGroup.LayoutParams.MATCH_PARENT;

            dialog.getWindow().setAttributes((WindowManager.LayoutParams) params);
            dialog.show();

            /*
            	 TODO :
                현재 팝업창에서 뒤로가기 버튼 클릭 시 뒤로가기가 두번씩 눌리는 현상 발생.
                해당 부분 확인하여 수정 예정
            */
            // 뒤로가기
            dialog.setOnKeyListener(new DialogInterface.OnKeyListener() {
                @Override
                public boolean onKey(DialogInterface dialog, int keyCode, KeyEvent event) {
                    Log.d("변준영", "onKey");
                    if (keyCode == KeyEvent.KEYCODE_BACK) {
                        Log.d("변준영", "Back");
                        if (myWebViewPop.canGoBack()) {
                            Log.d("변준영", "canGoBack");
                            myWebViewPop.goBack();
                        } else {
                            Log.d("변준영", "!canGoBack");
                            dialog.dismiss();
                            myWebViewPop.destroy();
                        }
                        return true;
                    } else {
                        Log.d("변준영", "else");
                        return false;
                    }
                }
            });


            myWebViewPop.setWebChromeClient(new WebChromeClient() {
                @Override
                public void onCloseWindow(WebView window) {
                    dialog.dismiss();
                    window.destroy();
                }
            });

            ((WebView.WebViewTransport) resultMsg.obj).setWebView(myWebViewPop);
            resultMsg.sendToTarget();
            return true;
        }

    }

}