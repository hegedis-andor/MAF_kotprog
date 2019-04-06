package com.example.shakeit;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.MediaPlayer;
import android.os.CountDownTimer;
import android.os.SystemClock;
import android.support.annotation.NonNull;
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
    private MediaPlayer mediaPlayer;


    public SensorDataUtil(@NonNull Context context) {
        this.context = context;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sensorManager.registerListener(this, sensorAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        this.mediaPlayer = mediaPlayer.create(context, R.raw.nonosound);
        this.countDownTimer = new CountDownTimer(1200, 300) {
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

                //The more hardly it shakes, the counter is incremented with bigger steps
                if (speed > 4500) {
                    this.countDownTimer.cancel();
                    this.countDownTimer.start();
                    sickCounter = sickCounter >= 60000 ?  60000 : sickCounter+3500;

                } else if (speed > 3000) {
                    this.countDownTimer.cancel();
                    this.countDownTimer.start();
                    sickCounter = sickCounter >= 60000 ?  60000 : sickCounter+2000;

                } else if (speed > 1500) {
                    this.countDownTimer.cancel();
                    this.countDownTimer.start();
                    sickCounter = sickCounter >= 60000 ?  60000 : sickCounter+500;

                }

                if(sickCounter >= 60000) {
                    if (!this.mediaPlayer.isPlaying()) this.mediaPlayer.start();
                    sendStateChange("3");
                }
                else if (sickCounter  >= 32000)  {
                    sendStateChange("2");
                }
                else if (sickCounter >= 10000)  {
                    if (this.mediaPlayer.isPlaying()) this.mediaPlayer.pause();
                    sendStateChange("1");
                }
                Log.e("Counter: " , String.valueOf(sickCounter));

                last_x = x;
                last_y = y;
                last_z = z;
            }
        }
    }

    // localbroadcasting the sickness level based state, which determines the background.
    private void sendStateChange(String state) {
        Intent intent = new Intent("backgroundStateChange");
        // You can also include some extra data.
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
        if (this.mediaPlayer.isPlaying()) this.mediaPlayer.pause();
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
