package org.peenyaindustries.piaconnect.activity.About;

import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.MenuItem;
import android.webkit.WebView;

import org.peenyaindustries.piaconnect.R;

import java.io.IOException;
import java.io.InputStream;

public class InviteeMembers extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_invitee_members);

        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle("Invitee Members");

        WebView webView = (WebView) findViewById(R.id.webView);

        int streamSize;

        try {
            InputStream html = this.getAssets().open("invitee_members.html");
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

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()) {
            case android.R.id.home:
                this.finish();
                return true;
            default:
                return super.onOptionsItemSelected(item);
        }
    }
}
