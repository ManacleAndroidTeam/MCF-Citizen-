package com.citizen.fmc.activity;

import android.annotation.SuppressLint;
import android.content.ActivityNotFoundException;
import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.res.Configuration;
import android.content.res.Resources;
import android.graphics.Bitmap;
import android.net.Uri;
import android.net.http.SslError;
import android.os.Bundle;
import androidx.annotation.Nullable;
import androidx.browser.customtabs.CustomTabsClient;
import androidx.browser.customtabs.CustomTabsIntent;
import androidx.browser.customtabs.CustomTabsServiceConnection;
import androidx.browser.customtabs.CustomTabsSession;
import androidx.core.content.ContextCompat;
import androidx.swiperefreshlayout.widget.SwipeRefreshLayout;
import androidx.appcompat.app.AppCompatActivity;

import android.text.TextUtils;
import android.util.DisplayMetrics;
import android.util.Log;
import android.webkit.CookieManager;
import android.webkit.SslErrorHandler;
import android.webkit.WebChromeClient;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.citizen.fmc.R;
import com.citizen.fmc.utils.Constants;

import java.util.Locale;

import dmax.dialog.SpotsDialog;

public class WebViewActivity extends AppCompatActivity {
    private WebView webView;
    private SwipeRefreshLayout swipeRefreshLayout;
    private SpotsDialog spotsDialog;
    private String toolBarExtra;
    private String webURL;

    //Chrome Custom Tabs Warm Up Setting Views:-
    CustomTabsClient mCustomTabsClient;
    CustomTabsSession mCustomTabsSession;
    CustomTabsServiceConnection mCustomTabsServiceConnection;
    final String CUSTOM_TAB_PACKAGE_NAME = "com.android.chrome";
    String language_code = "";
    String m_id = "";

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.fragment_web_view);

        webView = findViewById(R.id.web_view);
        swipeRefreshLayout = findViewById(R.id.swipe_refresh_layout);
        language_code = Constants.getPrefrence(WebViewActivity.this, "locale");
        if (!TextUtils.isEmpty(language_code)) {
            setLocale(language_code);
        }
        if (getIntent() != null) {
            toolBarExtra = getIntent().getStringExtra(Constants.KEY_TOOL_BAR_TITLE);
            webURL = getIntent().getStringExtra(Constants.KEY_WEB_VIEW_URL);
        }
        //   warmUpChromeCustomTab();

        spotsDialog = new SpotsDialog(this, getResources().getString(R.string.loading_web_page), R.style.CustomSpotsDialogStyle);
        getSupportActionBar().setTitle(toolBarExtra);
        getSupportActionBar().setDisplayHomeAsUpEnabled(false);
        startWebView(webURL);

        swipeRefreshLayout.setColorSchemeColors(getResources().getIntArray(R.array.swipe_refresh_color_scheme));

        swipeRefreshLayout.setOnRefreshListener(new SwipeRefreshLayout.OnRefreshListener() {
            @Override
            public void onRefresh() {
                webView.loadUrl("about:blank");
                startWebView(webURL);
            }
        });

    }

    @SuppressLint("SetJavaScriptEnabled")
    private void startWebView(String url) {
        try {
            //Create new web view Client to show progress dialog
            //When opening a url or click on link
//            CookieManager.getInstance().setAcceptCookie(true);
//            Log.d("MYWEBVIEWCLIENT", "This url couldn't not be handled by the webclient : " + Uri.parse(url).getHost());
            webView.setWebViewClient(new WebViewClient() {

                //If you will not use this method url links are open in new browser not in web view
                public boolean shouldOverrideUrlLoading(WebView view, String url1) {
                    if (url1.contains("//ewbilling")) {
                        pdfDownload(url1);
                    }
                    else
                    {
                        view.loadUrl(url1);
                    }
                    return true;

                }// This method will be triggered when the Page Started Loading

                public void onPageStarted(WebView view, String url, Bitmap favicon) {
                    if (!swipeRefreshLayout.isRefreshing()) {
                        spotsDialog.show();
                    }
                    super.onPageStarted(view, url, favicon);
                }

                //Show loader on url load
                public void onLoadResource(WebView view, String url) {
                }

                public void onPageFinished(WebView view, String url) {
                    try {
                        if (swipeRefreshLayout.isRefreshing()) {
                            swipeRefreshLayout.setRefreshing(false);
                        }
                        spotsDialog.dismiss();
                    } catch (Exception exception) {
                        exception.printStackTrace();
                    }
                    super.onPageFinished(view, url);
                }

                public void onReceivedError(WebView view, int errorCode,
                                            String description, String failingUrl) {
                    if (spotsDialog != null) {
                        if (spotsDialog.isShowing()) {
                            spotsDialog.dismiss();
                        }
                    }
                    if (swipeRefreshLayout.isRefreshing()) {
                        swipeRefreshLayout.setRefreshing(false);
                    }
                    super.onReceivedError(view, errorCode, description, failingUrl);
                }

                @SuppressLint("WebViewClientOnReceivedSslError")
                @Override
                public void onReceivedSslError(WebView view, SslErrorHandler handler, SslError error) {
                    String message = "";
                    switch (error.getPrimaryError()) {
                        case SslError.SSL_EXPIRED:
                            message = "The certificate has expired.";
                            break;
                        case SslError.SSL_IDMISMATCH:
                            message = "The certificate Hostname mismatch.";
                            break;
                        case SslError.SSL_NOTYETVALID:
                            message = "The certificate is not yet valid.";
                            break;
                        case SslError.SSL_DATE_INVALID:
                            message = "The certificate Date is invalid";
                            break;
                        case SslError.SSL_UNTRUSTED:
                            message = "The certificate authority is not trusted.";
                            break;
                    }
                    Constants.customToast(WebViewActivity.this,message,1);
                    handler.proceed(); // Ignore SSL certificate errors
                    Log.e("WebViewError", "SSL Error: " + error.toString());
                }
            });
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setLoadWithOverviewMode(true);
            webView.getSettings().setUseWideViewPort(true);
            webView.setScrollbarFadingEnabled(true);
            webView.setHorizontalScrollBarEnabled(true);
            webView.getSettings().setDisplayZoomControls(false);
            webView.getSettings().setBuiltInZoomControls(true);
            webView.getSettings().setDomStorageEnabled(true);
            webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
            Log.d("UArtyuj", webView.getSettings().getUserAgentString());
            //Load url in web view
            if (url.contains(".pdf")) {
                //  String url1 = "http://docs.google.com/gview?embedded=true&url=" + url;
                webView.loadUrl(url);
            } else if (url.endsWith(".aspx") || url.endsWith("gov.in")) {
               /* Intent intent = new Intent(Intent.ACTION_VIEW, Uri.parse(url));
                intent.setData(Uri.parse(url));
                browserChooser = Intent.createChooser(intent , getResources().getString(R.string.brower_chooser));
                startActivity(browserChooser);
                finish();*/
//                chromeCustomTabOpen(url);
                webView.loadUrl(url);
            } else {
                webView.loadUrl(url);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void pdfDownload(final String urls) {
        startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urls)));
//        webView.setDownloadListener(new DownloadListener() {
//            @Override
//            public void onDownloadStart(String url, String userAgent, String contentDisposition, String mimetype, long contentLength) {
//                startActivity(new Intent(Intent.ACTION_VIEW, Uri.parse(urls)));
//
//            }
//        });
      /*  webView.loadUrl(url);
        webView.setWebViewClient(new Client());
        webView.setWebChromeClient(new GoogleClient());
        WebSettings webSettings = webView.getSettings();
        webSettings.setJavaScriptEnabled(true);
        webView.getSettings().setJavaScriptCanOpenWindowsAutomatically(true);
        webView.clearCache(true);
        webView.clearHistory();
        webView.setDownloadListener(new DownloadListener() {
            @Override
            public void onDownloadStart(String url, String userAgent,
                                        String contentDisposition, String mimeType,
                                        long contentLength) {

                DownloadManager.Request request = new DownloadManager.Request(Uri.parse(url));

                request.setMimeType(mimeType);
                String cookies = CookieManager.getInstance().getCookie(url);
                request.addRequestHeader("cookie", cookies);
                request.addRequestHeader("User-Agent", userAgent);
                request.setDescription("Downloading file...");
                request.setTitle(URLUtil.guessFileName(url, contentDisposition, mimeType));
                request.allowScanningByMediaScanner();

                request.setNotificationVisibility(DownloadManager.Request.VISIBILITY_VISIBLE_NOTIFY_COMPLETED);
                request.setDestinationInExternalFilesDir(WebViewActivity.this,Environment.DIRECTORY_DOCUMENTS+"/NDMC/", "pdf");
                DownloadManager dm = (DownloadManager) getSystemService(DOWNLOAD_SERVICE);
                registerReceiver(onCompleted, new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE));
                dm.enqueue(request);
                Constants.customToast(getApplicationContext(), "Downloading File", 0);
            }
        });*/


        /*webView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                webView.loadUrl(url);
            }
        });*/
    }

    public void chromeCustomTabOpen(String url) {
        try {
            CustomTabsIntent.Builder builder = new CustomTabsIntent.Builder();
            builder.setToolbarColor(ContextCompat.getColor(WebViewActivity.this, R.color.colorLightBlue));
            builder.addDefaultShareMenuItem();
            builder.setShowTitle(false);
            builder.enableUrlBarHiding();
            builder.setStartAnimations(this, R.anim.animation_enter_from_right, R.anim.animation_enter_from_right);
            builder.setExitAnimations(this, R.anim.animation_leave_out_to_left, R.anim.animation_leave_out_to_left);
            CustomTabsIntent customTabsIntent = builder.build();
            customTabsIntent.launchUrl(this, Uri.parse(url));
            finish();

        } catch (ActivityNotFoundException e) {
            e.printStackTrace();
        }
    }

    public void warmUpChromeCustomTab() {
        mCustomTabsServiceConnection = new CustomTabsServiceConnection() {
            @Override
            public void onCustomTabsServiceConnected(ComponentName name, CustomTabsClient client) {
                mCustomTabsClient = client;
                mCustomTabsClient.warmup(0L);
                mCustomTabsSession = mCustomTabsClient.newSession(null);
            }

            @Override
            public void onServiceDisconnected(ComponentName componentName) {
                mCustomTabsClient = null;
            }
        };
        CustomTabsClient.bindCustomTabsService(this, CUSTOM_TAB_PACKAGE_NAME, mCustomTabsServiceConnection);

    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        spotsDialog.dismiss();
    }

    @Override
    protected void onResume() {
        super.onResume();
    }

    public void setLocale(String language_code) {
//        Resources res =  this.getResources();
        // Change locale settings in the app.
//        DisplayMetrics dm = res.getDisplayMetrics();
//        android.content.res.Configuration conf = res.getConfiguration();
//        conf.setLocale(new Locale(language_code.toLowerCase())); // API 17+ only.
// Use conf.locale = new Locale(...) if targeting lower versions
//        res.updateConfiguration(conf, dm);
        Locale myLocale = new Locale(language_code);
        Resources res = getResources();
        DisplayMetrics dm = res.getDisplayMetrics();
        Configuration conf = res.getConfiguration();
        conf.locale = myLocale;
        res.updateConfiguration(conf, dm);
//        Intent refresh = new Intent(getApplicationContext(),getClass());
////        finish();
//        startActivity(refresh);

    }
    BroadcastReceiver onCompleted = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            Toast.makeText(context.getApplicationContext(), "Download Finish", Toast.LENGTH_SHORT).show();

        }
    };

}

