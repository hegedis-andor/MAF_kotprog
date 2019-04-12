package com.example.shakeit;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.support.constraint.ConstraintLayout;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.ImageView;

public class MainActivity extends AppCompatActivity  {

    private SensorDataUtil sensorDataUtil;
    private ConstraintLayout constraintLayout;
    private ImageView emojiImageView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.constraintLayout = findViewById(R.id.container);
        this.emojiImageView = findViewById(R.id.emojiImageView);

        changeBackground(0);


        this.sensorDataUtil = new SensorDataUtil(getApplicationContext());

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("backgroundStateChange"));

    }


    //receives state change by localbroadcast
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
             changeBackground(Integer.parseInt(intent.getStringExtra("state")));
        }
    };


    //UI background and emoji changes by the received state
    public void changeBackground(int state) {
        switch (state) {
            case 0:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.START_GREEN));
                emojiImageView.setBackgroundResource(R.drawable.happy);
                break;
            case 1:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.MID_YELLOW));
                emojiImageView.setBackgroundResource(R.drawable.surprised);
                break;
            case 2:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.MID_ORANGE));
                emojiImageView.setBackgroundResource(R.drawable.confused);
                break;
            case 3:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.HIGH_RED));
                emojiImageView.setBackgroundResource(R.drawable.confused);
                break;
        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.sensorDataUtil.startAccelerometerListening();
        Log.e("resume", "sensordata");
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.sensorDataUtil.pauseAccelerometerListening();
        Log.e("Pause", "sensordata");
    }

}
