package com.radio.resplandecer.utils;

import android.view.View;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.app.ProgressDialog;
import android.widget.ProgressBar;

public class ResplandecerWebViewClient extends WebViewClient {

    private Listener listener;

    public ResplandecerWebViewClient(Listener listener) {
        this.listener = listener;
    }

    @Override
    public boolean shouldOverrideUrlLoading(WebView view, String url) {
        view.loadUrl(url);
        return true;
    }

    @Override
    public void onPageFinished(WebView view, String url) {
        super.onPageFinished(view, url);
        listener.onPagedLoaded();
    }

    public interface Listener {
        void onPagedLoaded();
    }
}
