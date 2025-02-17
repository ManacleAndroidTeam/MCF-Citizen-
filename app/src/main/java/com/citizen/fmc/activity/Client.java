package com.citizen.fmc.activity;

import android.graphics.Bitmap;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;

public class Client extends WebViewClient
{

    @Override
    public void onPageStarted(WebView view,String url,Bitmap favicon){
        super.onPageStarted(view,url,favicon);

    }
    @Override
    public boolean shouldOverrideUrlLoading(WebView view,String Url)
    {
        view.loadUrl(Url);
        return true;

    }
    @Override
    public void onPageFinished(WebView view, String url)
    {
        super.onPageFinished(view,url);

    }
}
class GoogleClient extends WebChromeClient
{
    @Override
    public void onProgressChanged(WebView view,int newProgress)
    {
        super.onProgressChanged(view,newProgress);

    }
}
