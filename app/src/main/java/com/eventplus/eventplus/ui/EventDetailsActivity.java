package com.eventplus.eventplus.ui;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.webkit.WebResourceRequest;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;
import android.widget.Toast;

import com.eventplus.eventplus.R;

public class EventDetailsActivity extends AppCompatActivity {

    private WebView eventDetailsView;
    private static final String HTML_LINK = "HTML LINK";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_event_details);


        eventDetailsView = (WebView) findViewById(R.id.webView);

        Intent intent = getIntent();
        eventDetailsView.getSettings().setLoadsImagesAutomatically(true);
        eventDetailsView.loadDataWithBaseURL(null, intent.getStringExtra(HTML_LINK), "text/html", "UTF-8", null);
    }



}

