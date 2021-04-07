package com.cs619.cs619iotweatherapp;

import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.provider.Settings;

import androidx.core.app.NotificationCompat;
import androidx.core.app.NotificationManagerCompat;

public class NotificationHelper {
    public static boolean isTemperatureLowerNotificationShown = false;
    public static boolean isTemperatureNormalNotificationShown = true;
    public static boolean isTemperatureUpperNotificationShown = false;

    public static boolean isHumidityLowerNotificationShown = false;
    public static boolean isHumidityNormalNotificationShown = true;
    public static boolean isHumidityUpperNotificationShown = false;

    public static boolean isRainLowerNotificationShown = false;
    public static boolean isRainNormalNotificationShown = true;
    public static boolean isRainUpperNotificationShown = false;

    public static boolean wasRaining = false;

    private Context context;
    private DatabaseHelper dbHelper;

    public NotificationHelper(Context context) {
        this.context = context;
        this.dbHelper = new DatabaseHelper(context);
    }

    public void finalize() {
        this.close();
    }

    public void close() {
        this.dbHelper.close();
    }

    public void reset() {
        isTemperatureLowerNotificationShown = false;
        isTemperatureNormalNotificationShown = true;
        isTemperatureUpperNotificationShown = false;

        isHumidityLowerNotificationShown = false;
        isHumidityNormalNotificationShown = true;
        isHumidityUpperNotificationShown = false;

        isRainLowerNotificationShown = false;
        isRainNormalNotificationShown = true;
        isRainUpperNotificationShown = false;

        wasRaining = false;
    }

    public void handleNotifications() {
        handleTemperatureNotification();
        handleHumidityNotification();
        handleRainNotification();
    }

    public void handleTemperatureNotification() {
        Double temperatureValue = dbHelper.getTemperatureValue();
        Double temperatureLowLimitInput = dbHelper.getTemperatureLowLimitInput();
        Double temperatureUpperLimitInput = dbHelper.getTemperatureUpperLimitInput();
        Integer shouldNotifyTemperature = dbHelper.getShouldNotifyTemperature();

        String temperatureValueStr = String.format("%s", temperatureValue == null ? "--" : temperatureValue + " °C");

        boolean shouldShowTemperatureLowerNotification =
                temperatureValue != null && shouldNotifyTemperature == 1 &&
                        !isTemperatureLowerNotificationShown && temperatureValue <= temperatureLowLimitInput;
        boolean shouldShowTemperatureNormalNotification =
                temperatureValue != null && shouldNotifyTemperature == 1 &&
                        !isTemperatureNormalNotificationShown && temperatureValue > temperatureLowLimitInput &&
                        temperatureValue < temperatureUpperLimitInput;
        boolean shouldShowTemperatureUpperNotification =
                temperatureValue != null && shouldNotifyTemperature == 1 &&
                        !isTemperatureUpperNotificationShown && temperatureValue >= temperatureUpperLimitInput;

        if (shouldShowTemperatureLowerNotification) {
            isTemperatureLowerNotificationShown = true;
            isTemperatureNormalNotificationShown = false;
            isTemperatureUpperNotificationShown = false;
            showNotification(1, R.drawable.temperature, "Temperature is Low (" + temperatureValueStr + ")", "Temperature is below Lower limit.");
        } else if (shouldShowTemperatureNormalNotification) {
            isTemperatureLowerNotificationShown = false;
            isTemperatureNormalNotificationShown = true;
            isTemperatureUpperNotificationShown = false;
            showNotification(1, R.drawable.temperature, "Temperature is Normal (" + temperatureValueStr + ")", "Temperature is within Normal range.");
        } else if (shouldShowTemperatureUpperNotification) {
            isTemperatureLowerNotificationShown = false;
            isTemperatureNormalNotificationShown = false;
            isTemperatureUpperNotificationShown = true;
            showNotification(1, R.drawable.temperature, "Temperature is High (" + temperatureValueStr + ")", "Temperature is above Upper limit.");
        }
    }

    public void handleHumidityNotification() {
        Double humidityValue = dbHelper.getHumidityValue();
        Double humidityLowLimitInput = dbHelper.getHumidityLowLimitInput();
        Double humidityUpperLimitInput = dbHelper.getHumidityUpperLimitInput();
        Integer shouldNotifyHumidity = dbHelper.getShouldNotifyHumidity();

        String humidityValueStr = String.format("%s", humidityValue == null ? "--" : humidityValue + " %");

        boolean shouldShowHumidityLowerNotification =
                humidityValue != null && shouldNotifyHumidity == 1 &&
                        !isHumidityLowerNotificationShown && humidityValue <= humidityLowLimitInput;
        boolean shouldShowHumidityNormalNotification =
                humidityValue != null && shouldNotifyHumidity == 1 &&
                        !isHumidityNormalNotificationShown && humidityValue > humidityLowLimitInput &&
                        humidityValue < humidityUpperLimitInput;
        boolean shouldShowHumidityUpperNotification =
                humidityValue != null && shouldNotifyHumidity == 1 &&
                        !isHumidityUpperNotificationShown && humidityValue >= humidityUpperLimitInput;

        if (shouldShowHumidityLowerNotification) {
            isHumidityLowerNotificationShown = true;
            isHumidityNormalNotificationShown = false;
            isHumidityUpperNotificationShown = false;
            showNotification(2, R.drawable.humidity, "Humidity is Low (" + humidityValueStr + ")", "Humidity is below Lower limit.");
        } else if (shouldShowHumidityNormalNotification) {
            isHumidityLowerNotificationShown = false;
            isHumidityNormalNotificationShown = true;
            isHumidityUpperNotificationShown = false;
            showNotification(2, R.drawable.humidity, "Humidity is Normal (" + humidityValueStr + ")", "Humidity is within Normal range.");
        } else if (shouldShowHumidityUpperNotification) {
            isHumidityLowerNotificationShown = false;
            isHumidityNormalNotificationShown = false;
            isHumidityUpperNotificationShown = true;
            showNotification(2, R.drawable.humidity, "Humidity is High (" + humidityValueStr + ")", "Humidity is above Upper limit.");
        }
    }

    public void handleRainNotification() {
        Double rainValue = dbHelper.getRainValue();
        Double rainLowLimitInput = dbHelper.getRainLowLimitInput();
        Double rainUpperLimitInput = dbHelper.getRainUpperLimitInput();
        Integer shouldNotifyRain = dbHelper.getShouldNotifyRain();

        String rainValueStr = String.format("%s", rainValue == null ? "--" : rainValue + " mm");

        if (!wasRaining && shouldNotifyRain == 1 && rainValue != null && rainValue > 0.0) {
            wasRaining = true;
            showNotification(3, R.drawable.rain, "Rain has Started", "Rain has Started.");
        }
        if (wasRaining && shouldNotifyRain == 1 && rainValue != null && rainValue == 0.0) {
            wasRaining = false;
            showNotification(3, R.drawable.rain, "Rain has Stopped", "Rain has Stopped.");
        }

        boolean shouldShowRainLowerNotification =
                rainValue != null && shouldNotifyRain == 1 &&
                        !isRainLowerNotificationShown && rainValue <= rainLowLimitInput &&
                        rainValue != 0.0;
        boolean shouldShowRainNormalNotification =
                rainValue != null && shouldNotifyRain == 1 &&
                        !isRainNormalNotificationShown && rainValue > rainLowLimitInput &&
                        rainValue < rainUpperLimitInput &&
                        rainValue != 0.0;
        boolean shouldShowRainUpperNotification =
                rainValue != null && shouldNotifyRain == 1 &&
                        !isRainUpperNotificationShown && rainValue >= rainUpperLimitInput &&
                        rainValue != 0.0;

        if (shouldShowRainLowerNotification) {
            isRainLowerNotificationShown = true;
            isRainNormalNotificationShown = false;
            isRainUpperNotificationShown = false;
            showNotification(4, R.drawable.rain, "Rain is Low (" + rainValueStr + ")", "Rain is below Lower limit.");
        } else if (shouldShowRainNormalNotification) {
            isRainLowerNotificationShown = false;
            isRainNormalNotificationShown = true;
            isRainUpperNotificationShown = false;
            showNotification(4, R.drawable.rain, "Rain is Normal (" + rainValueStr + ")", "Rain is within Normal range.");
        } else if (shouldShowRainUpperNotification) {
            isRainLowerNotificationShown = false;
            isRainNormalNotificationShown = false;
            isRainUpperNotificationShown = true;
            showNotification(4, R.drawable.rain, "Rain is High (" + rainValueStr + ")", "Rain is above Upper limit.");
        }
    }

    public void showNotification(int notificationId, int iconResourceId, String title, String text) {
        Intent intent = new Intent(this.context, DashboardActivity.class);
        PendingIntent pendingIntent = PendingIntent.getActivity(this.context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);

        NotificationCompat.Builder builder = new NotificationCompat.Builder(this.context, App.IOT_WEATHER_NOTIFICATION_CHANNEL)
                .setSmallIcon(iconResourceId)
                .setContentTitle(title)
                .setContentText(text)
                .setAutoCancel(true)
                .setPriority(NotificationCompat.PRIORITY_MAX)
                .setVibrate(new long[] {1000, 1000})
                .setSound(Settings.System.DEFAULT_NOTIFICATION_URI)
                .setContentIntent(pendingIntent);

//        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this);
        NotificationManagerCompat notificationManager = NotificationManagerCompat.from(this.context);

        // notificationId is a unique int for each notification that you must define
        notificationManager.notify(notificationId, builder.build());
    }
}
