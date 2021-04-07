package com.cs619.cs619iotweatherapp;

import android.app.Application;
import android.app.Notification;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.os.Build;
import android.widget.Toast;

public class App extends Application {
    public static final String IOT_WEATHER_NOTIFICATION_CHANNEL = "cs619iotWeatherNotificationChannel";

    @Override
    public void onCreate() {
        super.onCreate();

        createNotificationChannels();
    }

//    for registering notification channel in order to show notifications.
//    this method should be run before showing notification and App is registered
//    to be run as the first thing when the app starts so this method is run on app start.
    private void createNotificationChannels() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            NotificationChannel iotWeatherNotificationChannel = new NotificationChannel(
                    IOT_WEATHER_NOTIFICATION_CHANNEL,
                    "CS619 IOT Weather Notifications",
                    NotificationManager.IMPORTANCE_HIGH
            );
            iotWeatherNotificationChannel.setDescription("CS619 IOT Weather Notifications");
            iotWeatherNotificationChannel.setLockscreenVisibility(Notification.VISIBILITY_PUBLIC);
            iotWeatherNotificationChannel.setImportance(NotificationManager.IMPORTANCE_HIGH);

            NotificationManager manager = getSystemService(NotificationManager.class);
            if (manager != null) {
                manager.createNotificationChannel(iotWeatherNotificationChannel);
            } else {
                Toast.makeText(this, "Could not create notification channel. [manager is null]", Toast.LENGTH_LONG).show();
            }
        }
    }
}
