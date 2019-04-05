package com.example.shakeit;

import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.content.AsyncTaskLoader;

public class SensorAsyncTaskLoader extends AsyncTaskLoader<Void> {

    public SensorAsyncTaskLoader(@NonNull Context context) {
        super(context);
    }

    @Nullable
    @Override
    public Void loadInBackground() {
        return null;
    }
}
