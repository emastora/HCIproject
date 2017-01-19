package com.example.hciproject;

import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.CookieSyncManager;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

/**
 * Created by Schoox on 1/19/2017.
 */

public class AuthActivity extends AppCompatActivity {
    String secret="K3j/0biTBg1QLkXGMrs68COfWsQaBewIVcec3d0BTMEwsCzQLAWdzpsjrtg4e4hpUDunnHLjlpBLDMM1QEsw==";
    String redirectionUrl="www.manos.mastorakis.gr";
    String responceType="code";
    String scope="favorites";
    String clientId="vZrtR94kaCqLtlzGrRwGFg==";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.auth);
        WebView webView= (WebView) this.findViewById(R.id.webview);
        webView.setWebViewClient(new MyWebChromeClient());
        clearCookies(this);
        String url="https://www.skroutz.gr/oauth2/authorizations/new";
        url= url + "?client_id=" + clientId;
        url= url + "&redirect_uri=" + redirectionUrl;
        url= url + "&response_type=" + responceType;
        url= url + "&scope=" + scope;
        webView.loadUrl(url);
    }

    class MyWebChromeClient extends WebViewClient{
        public boolean shouldOverrideUrlLoading(WebView view, String url){
            // do your handling codes here, which url is the requested url
            // probably you need to open that url rather than redirect:
            Log.d("debug","url to open:"+url);
            if(url.contains("?code=")){
                String[] codepiece = url.split("code=");
                String code=codepiece[1];
                Log.d("debug","code is :"+code+":");
            }else {
                view.loadUrl(url);
            }
            return false; // then it is not handled by default action
        }
    }

    @SuppressWarnings("deprecation")
    public static void clearCookies(Context context)
    {

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.LOLLIPOP_MR1) {
            Log.d("debug", "Using clearCookies code for API >=" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieManager.getInstance().removeAllCookies(null);
            CookieManager.getInstance().flush();
        } else
        {
            Log.d("debug", "Using clearCookies code for API <" + String.valueOf(Build.VERSION_CODES.LOLLIPOP_MR1));
            CookieSyncManager cookieSyncMngr=CookieSyncManager.createInstance(context);
            cookieSyncMngr.startSync();
            CookieManager cookieManager=CookieManager.getInstance();
            cookieManager.removeAllCookie();
            cookieManager.removeSessionCookie();
            cookieSyncMngr.stopSync();
            cookieSyncMngr.sync();
        }
    }
}
