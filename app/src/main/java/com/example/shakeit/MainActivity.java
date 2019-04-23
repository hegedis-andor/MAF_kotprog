package com.example.shakeit;

import android.app.AlertDialog;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.ActivityInfo;
import android.graphics.BitmapFactory;
import android.media.MediaPlayer;
import android.os.Build;
import android.support.constraint.ConstraintLayout;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
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
    private MediaPlayer nononoMP;
    private MediaPlayer plsNoMP;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        setRequestedOrientation (ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        this.constraintLayout = findViewById(R.id.container);
        this.emojiImageView = findViewById(R.id.emojiImageView);

        changeBackground(0);

        this.nononoMP = MediaPlayer.create(this, R.raw.nononofx);
        this.plsNoMP = MediaPlayer.create(this, R.raw.pleasenofx);

        this.sensorDataUtil = new SensorDataUtil(getApplicationContext());

        LocalBroadcastManager.getInstance(this).registerReceiver(broadcastReceiver,
                new IntentFilter("backgroundStateChange"));

    }

    //UI background and emoji changes by the received state
    public void changeBackground(int state) {
        switch (state) {
            case 50:
                if (!this.plsNoMP.isPlaying()) this.plsNoMP.start();
                break;
            case 40:
                noNoNotify();
                break;
            case 31:
                if(!this.nononoMP.isPlaying()) this.nononoMP.start();
                break;
            case 30:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.HIGH_RED));
                emojiImageView.setBackgroundResource(R.drawable.confused);
                break;
            case 20:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.MID_ORANGE));
                emojiImageView.setBackgroundResource(R.drawable.confused);
                break;
            case 10:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.MID_YELLOW));
                emojiImageView.setBackgroundResource(R.drawable.surprised);
                if (this.nononoMP.isPlaying()) this.nononoMP.pause();
                if (this.plsNoMP.isPlaying()) this.plsNoMP.pause();
                break;
            case 0:
                constraintLayout.setBackgroundColor(ContextCompat.getColor(this, R.color.START_GREEN));
                emojiImageView.setBackgroundResource(R.drawable.happy);
                break;
        }
    }

    //receives state change by localbroadcast
    private BroadcastReceiver broadcastReceiver = new BroadcastReceiver() {
        @Override
        public void onReceive(Context context, Intent intent) {
            changeBackground(Integer.parseInt(intent.getStringExtra("state")));
        }
    };

    public void noNoNotify() {
        CharSequence channelName = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelName = "shakeNotificationChan";
            String description = "Intense shake notification.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelName.toString(), channelName, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = this.getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, new Intent(), 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                this, channelName != null ? channelName.toString() : null)
                .setContentTitle("Stop, Stop, Stop!")
                .setContentText("Please Stoooooppppppppppp!!!")
                .setSmallIcon(R.drawable.sick)
                .setPriority(NotificationCompat.PRIORITY_HIGH)
                .setAutoCancel(true)
                .setContentIntent(pendingIntent)
                .setLargeIcon(BitmapFactory.decodeResource(this.getResources(),
                        R.drawable.sick));


        NotificationManagerCompat notifcationManager = NotificationManagerCompat.from(this);
        notifcationManager.notify(0, builder.build());
    }

    @Override
    protected void onResume() {
        super.onResume();
        this.sensorDataUtil.startAccelerometerAndCountDown();
    }

    @Override
    protected void onPause() {
        super.onPause();
        this.sensorDataUtil.pauseAccelerometerAndCountDown();
        if (this.nononoMP.isPlaying()) this.nononoMP.pause();
        if (this.plsNoMP.isPlaying()) this.plsNoMP.pause();
    }

}
