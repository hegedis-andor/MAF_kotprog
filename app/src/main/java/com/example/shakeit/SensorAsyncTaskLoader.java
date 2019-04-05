package com.example.shakeit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

public class SensorAsyncTaskLoader extends AsyncTaskLoader<Void> {

    private SensorDataUtil sensorDataUtil;

    public SensorAsyncTaskLoader(@NonNull Context context) {
        super(context);
        sensorDataUtil = new SensorDataUtil(context);
    }

    @Override
    public void onStartLoading() {
        super.onStartLoading();
        forceLoad();
    }


    @Nullable
    @Override
    public Void loadInBackground() {
        sensorDataUtil.startAccelerometerListening();

        return null;
    }
}
