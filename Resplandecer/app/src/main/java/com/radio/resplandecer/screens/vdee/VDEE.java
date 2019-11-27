package com.radio.resplandecer.screens.vdee;

import android.annotation.SuppressLint;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.webkit.WebView;

import com.radio.resplandecer.R;
import com.radio.resplandecer.baseActivities.BaseDrawerActivity;
import com.radio.resplandecer.utils.ResplandecerWebViewClient;

public class VDEE extends BaseDrawerActivity implements ResplandecerWebViewClient.Listener {

    WebView vdeeWebView;
    ResplandecerWebViewClient resplandecerWebViewClient;

    @SuppressLint("SetJavaScriptEnabled")
    @Override
    public void onAttached() {
        LayoutInflater inflater = (LayoutInflater) this
                .getSystemService(Context.LAYOUT_INFLATER_SERVICE);
        View contentView = inflater.inflate(R.layout.activity_vdee, null, false);
        drawerLayout.addView(contentView, 0);

        resplandecerWebViewClient = new ResplandecerWebViewClient(this);
        vdeeWebView = findViewById(R.id.vdee_web_view);
        vdeeWebView.getSettings().setJavaScriptEnabled(true);

        vdeeWebView.setWebViewClient(resplandecerWebViewClient);
        vdeeWebView.loadUrl("http://www.evalverde.com/index.php/es/");
        showDialog();
    }

    @Override
    public void onPagedLoaded() {
        hideDialog();
    }
}
