package com.blankshrimp.xjtimetablu;

import android.app.ProgressDialog;
import android.content.Intent;
import android.graphics.Bitmap;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.MenuItem;
import android.webkit.JavascriptInterface;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class WebviewActivity extends AppCompatActivity {

    private WebView webView;
    private String count;
    private Handler handler;
    private int countc = 0;
    private ProgressDialog progressDialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        countc = 0;
        setContentView(R.layout.activity_webview);
        Toolbar toolbar = (Toolbar) findViewById(R.id.webToolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        webView = (WebView) findViewById(R.id.webView);
        handler = new Handler();
        webView.getSettings().setJavaScriptEnabled(true);
        webView.addJavascriptInterface(new InJavaScriptLocalObj(), "local_obj");
        webView.setWebViewClient(new MyWebViewClient());
        webView.loadUrl("https://ebridge.xjtlu.edu.cn/urd/sits.urd/run/siw_lgn");
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            //重写ToolBar返回按钮的行为，防止重新打开父Activity重走生命周期方法
            case android.R.id.home:
                finish();
                return true;
        }
        return super.onOptionsItemSelected(item);
    }

    final class InJavaScriptLocalObj {
        @JavascriptInterface
        public void showSource(final String html) {
            new Thread() {
                public void run() {
                    count = html;
                    handler.post(runnableui);
                }
            }.start();
        }

        Runnable runnableui = new Runnable() {
            @Override
            public void run() {
                if (countc == 0 && count.contains("<title>e:Vision Portal</title>")) {
                    progressDialog = new ProgressDialog(WebviewActivity.this);
                    progressDialog.setTitle(WebviewActivity.this.getString(R.string.progressTitle));
                    progressDialog.setMessage(WebviewActivity.this.getString(R.string.progressContent));
                    progressDialog.setCancelable(false);
                    progressDialog.show();
                    Document document = Jsoup.parse(count);
                    String dis = document.select("a[id=smTIMETABLE]").get(0).attr("href");
                    try {
                    } catch (Exception e) {
                    }
                    webView.loadUrl("https://ebridge.xjtlu.edu.cn/urd/sits.urd/run/" + dis);
                    countc++;

                } else if (countc == 1 && count.contains("sv-list-group-item sv-list-group-item-overflow")) {
                    Document document = Jsoup.parse(count);
                    String dis = document.select("a[class=sv-list-group-item sv-list-group-item-overflow]").get(0).attr("href");
                    try {
                    } catch (Exception e) {
                    }
                    webView.loadUrl("https://ebridge.xjtlu.edu.cn/urd/sits.urd/run/" + dis);
                    countc++;

                } else if (countc == 2 && count.contains("<title>My Class Timetable</title>")) {
                    progressDialog.dismiss();
                    Intent intent = new Intent(WebviewActivity.this, WebviewRegister.class);
                    Bundle bundle = new Bundle();
                    bundle.putString("html", count);
                    intent.putExtras(bundle);
                    startActivity(intent);
                    finish();
                }
            }
        };
    }

    final class MyWebViewClient extends WebViewClient {
        public boolean shouldOverrideUrlLoading(WebView view, String url) {
            view.loadUrl(url);
            return true;
        }

        public void onPageStarted(WebView view, String url, Bitmap favicon) {
            Log.d("WebView", "onPageStarted");
            super.onPageStarted(view, url, favicon);
        }

        public void onPageFinished(WebView view, String url) {
            super.onPageFinished(view, url);
            if (webView.getProgress() == 100) {
                view.loadUrl("javascript:window.local_obj.showSource('<head>'+" +
                        "document.getElementsByTagName('html')[0].innerHTML+'</head>');");

            }
        }
    }
}
