package org.peenyaindustries.piaconnect.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.os.Handler;
import android.support.v7.app.AppCompatActivity;
import android.view.animation.AlphaAnimation;
import android.view.animation.Animation;
import android.widget.TextView;

import org.peenyaindustries.piaconnect.R;
import org.peenyaindustries.piaconnect.helper.ConnectionDetector;

public class SplashActivity extends AppCompatActivity {

    private static final int SPALSH_TIME = 3000;// 3 Seconds

    private AlertDialog.Builder builder;
    private AlertDialog alert;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_splash);

        builder = new AlertDialog.Builder(SplashActivity.this);
        isConnected();
    }

    public void isConnected() {

        ConnectionDetector connection = new ConnectionDetector(getApplicationContext());
        Boolean isInternetActive = connection.isConnectedToInternet();
        if (isInternetActive) {
            TextView loadingText = (TextView) findViewById(R.id.loadingText);

            //Blink Animation Effect
            Animation blink = new AlphaAnimation(0.0f, 1.0f);
            blink.setDuration(500);
            blink.setStartOffset(20);
            blink.setRepeatMode(Animation.REVERSE);
            blink.setRepeatCount(Animation.INFINITE);
            loadingText.startAnimation(blink);

            //Start Register Activity
            new Handler().postDelayed(new Runnable() {

                @Override
                public void run() {
                    Intent intent = new Intent(SplashActivity.this,
                            RegisterActivity.class);
                    startActivity(intent);
                    SplashActivity.this.finish();
                }
            }, SPALSH_TIME);
        } else {
            builder.setTitle("No Internet Connection Detected");
            builder.setMessage("Internet Connection is required to proceed. \nPlease try again after establishing an Internet Connection.");
            builder.setPositiveButton("RETRY", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    isConnected();
                }
            });
            builder.setNegativeButton("EXIT", new DialogInterface.OnClickListener() {
                @Override
                public void onClick(DialogInterface dialog, int which) {
                    dialog.dismiss();
                    finish();
                }
            });
            alert = builder.create();
            alert.show();
        }

    }
}
