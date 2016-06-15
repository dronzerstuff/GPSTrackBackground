package com.davy.gpstrackbackground;

import android.app.ActivityManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.SharedPreferences;
import android.content.pm.PackageManager;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.Set;

public class MainActivity extends AppCompatActivity {

    TextView lat_lang;
    public static final int REQUEST_LOCATION = 8;
    SharedPreferences preferences;
    SharedPreferences.Editor editor;

    Button button, btn_log;
    ListView listView;
    ArrayAdapter<String> adapter;
    ArrayList<String> arrayList = new ArrayList<>();

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        preferences = getSharedPreferences("latLang", Context.MODE_PRIVATE);
        editor = preferences.edit();

        lat_lang = (TextView) findViewById(R.id.textView);
        listView = (ListView)findViewById(R.id.listView);

        adapter = new ArrayAdapter<String>(this, android.R.layout.simple_list_item_1, arrayList);
        listView.setAdapter(adapter);

        button = (Button) findViewById(R.id.button);
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                if (isMyServiceRunning(GPSTracker.class)) {
                    Intent intent = new Intent(MainActivity.this, GPSTracker.class);
                    stopService(intent);
                    button.setText("Start Update");
                    editor.clear();
                    editor.apply();
                } else {
                    Intent intent = new Intent(MainActivity.this, GPSTracker.class);
                    startService(intent);
                    button.setText("Stop Update");
                }
            }
        });

        btn_log = (Button) findViewById(R.id.button2);
        btn_log.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Set<String> set = preferences.getStringSet("location", null);
                if (set != null) {
                    arrayList.clear();
                    arrayList = new ArrayList<>(set);
                    adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                    listView.setAdapter(adapter);
                }
            }
        });

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            if (!ActivityCompat.shouldShowRequestPermissionRationale(this, android.Manifest.permission.ACCESS_FINE_LOCATION)) {
                Toast.makeText(this, "Allow Location Access Permission.", Toast.LENGTH_LONG).show();
            }
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_LOCATION);

        } else {

            if (!isMyServiceRunning(GPSTracker.class)) {
                Intent intent = new Intent(MainActivity.this, GPSTracker.class);
                startService(intent);
            }

        }
    }

    @Override
    protected void onResume() {
        super.onResume();
        registerReceiver(receiver, new IntentFilter(GPSTracker.LOCATION_TRACKING));
    }

    @Override
    protected void onPause() {
        super.onPause();
        unregisterReceiver(receiver);
    }

    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String[] permissions, @NonNull int[] grantResults) {
        super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        if (requestCode == REQUEST_LOCATION) {
            if (grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                if (!isMyServiceRunning(GPSTracker.class)) {
                    Intent intent = new Intent(MainActivity.this, GPSTracker.class);
                    startService(intent);
                }
            } else {
                Toast.makeText(this, "Allow Location Access Permission.", Toast.LENGTH_LONG).show();
            }
        }
    }

    private BroadcastReceiver receiver = new BroadcastReceiver() {

        @Override
        public void onReceive(Context context, Intent intent) {
            Bundle bundle = intent.getExtras();
            if (bundle != null) {
                lat_lang.setText("Last Lat &  Lang : " + bundle.getDouble("Latitude", 0) + ", " + bundle.getDouble("Longitude", 0));
                Set<String> set = preferences.getStringSet("location", null);
                if (set != null) {
                    arrayList.clear();
                    arrayList = new ArrayList<>(set);
                    adapter = new ArrayAdapter<String>(MainActivity.this, android.R.layout.simple_list_item_1, arrayList);
                    listView.setAdapter(adapter);
                }
            }
        }
    };

    private boolean isMyServiceRunning(Class<?> serviceClass) {
        ActivityManager manager = (ActivityManager) getSystemService(Context.ACTIVITY_SERVICE);
        for (ActivityManager.RunningServiceInfo service : manager.getRunningServices(Integer.MAX_VALUE)) {
            if (serviceClass.getName().equals(service.service.getClassName())) {
                return true;
            }
        }
        return false;
    }

}
