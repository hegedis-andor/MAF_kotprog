package com.example.shakeit;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.graphics.BitmapFactory;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.Build;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.NonNull;
import android.support.v4.app.NotificationCompat;
import android.support.v4.app.NotificationManagerCompat;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

public class SensorDataUtil implements SensorEventListener {

    private Context context;
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private static int sickCounter = 0;
    private CountDownTimer countDownTimer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private MediaPlayer nononoMP;
    private MediaPlayer plsNoMP;
    private boolean isNotified;

    public SensorDataUtil(@NonNull Context context) {
        this.context = context;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sensorManager.registerListener(this, sensorAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        this.nononoMP = nononoMP.create(context, R.raw.nononofx);
        this.plsNoMP = plsNoMP.create(context, R.raw.pleasenofx);
        this.countDownTimer = new CountDownTimer(2200, 300) {
            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                sickCounter = sickCounter > 20000 ? sickCounter-20000 : 0;
                if (sickCounter == 0) {
                    cancelCountdownTimer();
                    sendStateChange("0");
                } else {
                    restartCountdownTimer();
                }
                Log.e("Counter: " , String.valueOf(sickCounter));
            }
        }.start();
        this.isNotified = false;
    }

    @Override
    public void onSensorChanged(SensorEvent sensorEvent) {
        Sensor mySensor = sensorEvent.sensor;

        if (mySensor.getType() == Sensor.TYPE_ACCELEROMETER) {
            float x = sensorEvent.values[0];
            float y = sensorEvent.values[1];
            float z = sensorEvent.values[2];


            long currentTime = SystemClock.elapsedRealtime();

            // Updates the sensor data every 100 milli sec
            if ((currentTime - lastUpdate) > 100) {
                long diffTime = (currentTime - lastUpdate);
                lastUpdate = currentTime;

                // speed of movement
                float speed = Math.abs(x + y + z - last_x - last_y - last_z)/ diffTime * 10000;

                //The bigger the speed the bigger the step of the sick counter
                if (speed > 4500) {
                    this.countDownTimer.cancel();
                    this.countDownTimer.start();
                    sickCounter = sickCounter >= 100000 ?  100000 : sickCounter+4000;

                } else if (speed > 3000) {
                    this.countDownTimer.cancel();
                    this.countDownTimer.start();
                    sickCounter = sickCounter >= 100000 ?  100000 : sickCounter+2000;

                } else if (speed > 1500) {
                    this.countDownTimer.cancel();
                    this.countDownTimer.start();
                    sickCounter = sickCounter >= 100000 ?  100000 : sickCounter+500;

                }

                // Logic of state change, notification and sound
                if (sickCounter >= 100000) {
                    if (!this.plsNoMP.isPlaying()) this.plsNoMP.start();
                }
                else if (sickCounter >= 80000) {
                    if (!this.isNotified) {
                        noNoNotify();
                        this.isNotified = true;
                    }
                }
                else if(sickCounter >= 60000) {
                    if (!this.nononoMP.isPlaying() && speed > 1500) this.nononoMP.start();
                    if (this.isNotified) this.isNotified = false;
                    sendStateChange("3");
                }
                else if (sickCounter  >= 32000)  {
                    sendStateChange("2");
                }
                else if (sickCounter >= 10000)  {
                    if (this.nononoMP.isPlaying()) this.nononoMP.pause();
                    if (this.plsNoMP.isPlaying()) this.plsNoMP.pause();
                    sendStateChange("1");
                }

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    // localbroadcasting the sickness level based state, which determines the background.
    private void sendStateChange(String state) {
        Intent intent = new Intent("backgroundStateChange");
        intent.putExtra("state", state);
        LocalBroadcastManager.getInstance(context).sendBroadcast(intent);
    }



    public void startAccelerometerListening() {
        this.sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        this.countDownTimer.start();

    }

    public void pauseAccelerometerListening() {
        this.sensorManager.unregisterListener(this);
        this.countDownTimer.cancel();
        if (this.nononoMP.isPlaying()) this.nononoMP.pause();
        if (this.plsNoMP.isPlaying()) this.plsNoMP.pause();
    }

    public void noNoNotify() {
        CharSequence channelName = null;

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            channelName = "shakeNotificationChan";
            String description = "Intense shake notification.";
            int importance = NotificationManager.IMPORTANCE_DEFAULT;

            NotificationChannel channel = new NotificationChannel(channelName.toString(), channelName, importance);
            channel.setDescription(description);

            NotificationManager notificationManager = context.getSystemService(NotificationManager.class);

            notificationManager.createNotificationChannel(channel);
        }

        PendingIntent pendingIntent =
                PendingIntent.getActivity(this.context, 0, new Intent(), 0);


        NotificationCompat.Builder builder = new NotificationCompat.Builder(
                context, channelName != null ? channelName.toString() : null)
                        .setContentTitle("Stop, Stop, Stop!")
                        .setContentText("Please Stoooooppppppppppp!!!")
                        .setSmallIcon(R.drawable.sick)
                        .setPriority(NotificationCompat.PRIORITY_HIGH)
                        .setAutoCancel(true)
                        .setContentIntent(pendingIntent)
                        .setLargeIcon(BitmapFactory.decodeResource(context.getResources(),
                            R.drawable.sick));


        NotificationManagerCompat notifcationManager = NotificationManagerCompat.from(context);
        notifcationManager.notify(0, builder.build());
    }




    private void restartCountdownTimer() {
        this.countDownTimer.start();
    }

    private void cancelCountdownTimer() {
        this.countDownTimer.cancel();
    }


    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
