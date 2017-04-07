package org.peenyaindustries.piaconnect.activity;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.webkit.WebView;

import org.peenyaindustries.piaconnect.R;

import java.io.IOException;
import java.io.InputStream;

public class TermsConditionsActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_terms_conditions);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        WebView webView = (WebView) findViewById(R.id.termsView);

        int streamSize;

        try {
            InputStream html = this.getAssets().open("termsandconditions.html");
            streamSize = html.available();
            byte[] buffer = new byte[streamSize];
            html.read(buffer);
            html.close();
            String data = new String(buffer);
            webView.loadData(data, "text/html", "UTF-8");
            webView.setInitialScale(250);


        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
