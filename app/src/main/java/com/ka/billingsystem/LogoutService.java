package com.ka.billingsystem;

import android.app.Service;


import android.content.Intent;
import android.content.SharedPreferences;
import android.os.IBinder;
import android.widget.Toast;


public class LogoutService extends Service {

    private static final String SHARED_PREFS = "shared_prefs";
    private static final String USER_KEY = "user_key";

    @Override
    public void onTaskRemoved(Intent rootIntent) {
        performLogout();
    }

    private void performLogout() {
        SharedPreferences sharedPreferences = getSharedPreferences(SHARED_PREFS, MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPreferences.edit();
        editor.remove(USER_KEY);
        editor.apply();

        Toast.makeText(this, "Logged out due to app termination", Toast.LENGTH_SHORT).show();
    }

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }
}
