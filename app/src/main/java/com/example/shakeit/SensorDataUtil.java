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

public class SensorDataUtil implements SensorEventListener {

    private Context context;
    private SensorManager sensorManager;
    private Sensor sensorAccelerometer;
    private static int counter = 0;
    private CountDownTimer countDownTimer;
    private long lastUpdate = 0;
    private float last_x, last_y, last_z;
    private boolean isNotified;

    SensorDataUtil(@NonNull Context context) {
        this.context = context;
        this.sensorManager = (SensorManager) context.getSystemService(Context.SENSOR_SERVICE);
        this.sensorAccelerometer = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
        this.sensorManager.registerListener(this, sensorAccelerometer , SensorManager.SENSOR_DELAY_NORMAL);
        this.countDownTimer = new CountDownTimer(1200, 300) {
            @Override
            public void onTick(long millisUntilFinished) { }

            @Override
            public void onFinish() {
                counter = counter > 20000 ? counter -20000 : 0;
                if (counter == 0) {
                    cancelCountdownTimer();
                    sendStateChange("0"); //happy state
                } else {
                    restartCountdownTimer();
                }
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



//------------    Logic of state change, notification and sound
                //The bigger the speed the bigger the step of the sick counter
                if (speed > 4500) {
                    this.countDownTimer.cancel();
                    this.countDownTimer.start();
                    counter = counter >= 100000 ?  100000 : counter +4000;

                } else if (speed > 3000) {
                    this.countDownTimer.cancel();
                    this.countDownTimer.start();
                    counter = counter >= 100000 ?  100000 : counter +2000;

                } else if (speed > 1500) {
                    this.countDownTimer.cancel();
                    this.countDownTimer.start();
                    counter = counter >= 100000 ?  100000 : counter +500;

                }
//----------------------------------------------

                if (counter >= 100000) {
                    //state -> 50 sick background, second sound
                    sendStateChange("50");
                }
                else if (counter >= 80000) {
                    //state 40 -> sick background, notification
                    if (!this.isNotified) {
                        sendStateChange("40"); //notification
                        this.isNotified = true;
                    }
                }
                else if(counter >= 60000) {
                    //start sound if not playing currently
                    if (speed > 1500)
                        sendStateChange("31");
                    if (this.isNotified) this.isNotified = false;
                    //state 30 -> sick background, first sound
                    sendStateChange("30");
                }
                else if (counter >= 32000)  {
                    //state -> 20 confused background
                    sendStateChange("20");
                }
                else if (counter >= 10000)  {
                    //state 10 -> surprised background
                    sendStateChange("10");
                }
                //state 0 -> happy background ( in countdown timer)
//----------------------------------------------
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

    void startAccelerometerAndCountDown() {
        this.sensorManager.registerListener(this, sensorAccelerometer, SensorManager.SENSOR_DELAY_NORMAL);
        restartCountdownTimer();

    }

    void pauseAccelerometerAndCountDown() {
        this.sensorManager.unregisterListener(this);
        cancelCountdownTimer();
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
