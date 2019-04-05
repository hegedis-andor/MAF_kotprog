package com.example.shakeit;

import android.content.Context;
import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
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


    public SensorDataUtil(@NonNull Context context) {
        this.context = context;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        sensorManager.registerListener(this, sensorAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);

        this.countDownTimer = new CountDownTimer(1800, 300) {
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
                    sickCounter = sickCounter >= 60000 ?  60000 : sickCounter+3000;

                } else if (speed > 3000) {
                    this.countDownTimer.cancel();
                    this.countDownTimer.start();
                    sickCounter = sickCounter >= 60000 ?  60000 : sickCounter+1800;

                } else if (speed > 1500) {
                    this.countDownTimer.cancel();
                    this.countDownTimer.start();
                    sickCounter = sickCounter >= 60000 ?  60000 : sickCounter+1000;

                }

                if(sickCounter > 59999) {
                    sendStateChange("3");
                }
                else if (sickCounter  > 39999)  {
                    sendStateChange("2");
                }
                else if (sickCounter > 19999)  {
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

    private void restartCountdownTimer() {
        this.countDownTimer.start();
    }

    private void cancelCountdownTimer() {
        this.countDownTimer.cancel();
    }

    public void resumeAccelerometerListening() {
        this.sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
    }

    public void pauseAccelerometerListening() {
        this.sensorManager.unregisterListener(this);
    }

    @Override
    public void onAccuracyChanged(Sensor sensor, int accuracy) {

    }
}
