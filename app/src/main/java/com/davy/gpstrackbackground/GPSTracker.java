package com.davy.gpstrackbackground;

import android.content.Intent;
import android.os.IBinder;
import android.app.Service;

/**
 * Created by User on 15-06-2016.
 */
public class GPSTracker extends Service {
    @Override
    public void onCreate() {
        super.onCreate();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        return super.onStartCommand(intent, flags, startId);
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
