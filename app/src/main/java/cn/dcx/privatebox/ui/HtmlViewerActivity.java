package cn.dcx.privatebox.ui;

import android.app.Activity;
import android.os.Bundle;
import com.tencent.smtt.sdk.WebSettings;
import com.tencent.smtt.sdk.WebView;
import com.tencent.smtt.sdk.WebViewClient;

import cn.dcx.privatebox.R;

public class HtmlViewerActivity extends Activity {
    private static final String TAG = "HtmlViewerActivity";
    WebView wv;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_html_viewer);
        wv=(WebView)this.findViewById(R.id.file_htmlViewer);
        //启用支持javascript
        WebSettings settings = wv.getSettings();
        settings.setJavaScriptEnabled(true);
        settings.setSupportZoom(true);
        settings.setCacheMode(WebSettings.LOAD_NO_CACHE);
        settings.setDefaultTextEncodingName("utf-8");
        wv.setWebViewClient(new WebViewClient(){
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url){
                return false;
            }
        });
        wv.setWebContentsDebuggingEnabled(true);
        Bundle bundle = this.getIntent().getExtras();
        String url = bundle.getString("url");
        if(url!=null&&url.isEmpty()==false){
            wv.loadUrl(url);
        }

    }


}
