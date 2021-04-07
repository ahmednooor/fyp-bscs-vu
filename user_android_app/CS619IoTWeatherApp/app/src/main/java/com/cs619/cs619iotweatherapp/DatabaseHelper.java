package com.cs619.cs619iotweatherapp;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class DatabaseHelper extends SQLiteOpenHelper {
    public static final String DATABASE_NAME = "app.db";
    public static final String TABLE_DEVICES_SETTINGS = "DevicesSettings";
    public static final String TABLE_APP_STORAGE = "AppStorage";
    public static final String COL_ROW_ID = "row_id";
    public static final String COL_DEVICE_ID = "device_id";
    public static final String COL_USER_PASSWORD = "user_password";
    public static final String COL_DEVICE_NAME = "device_name";
    public static final String COL_TIMESTAMP = "timestamp";
    public static final String COL_TEMPERATURE = "temperature";
    public static final String COL_TEMPERATURE_LOW_LIMIT = "temperature_low_limit";
    public static final String COL_TEMPERATURE_HIGH_LIMIT = "temperature_high_limit";
    public static final String COL_SHOULD_NOTIFY_TEMPERATURE = "should_notify_temperature";
    public static final String COL_HUMIDITY = "humidity";
    public static final String COL_HUMIDITY_LOW_LIMIT = "humidity_low_limit";
    public static final String COL_HUMIDITY_HIGH_LIMIT = "humidity_high_limit";
    public static final String COL_SHOULD_NOTIFY_HUMIDITY = "should_notify_humidity";
    public static final String COL_RAIN = "rain";
    public static final String COL_RAIN_LOW_LIMIT = "rain_low_limit";
    public static final String COL_RAIN_HIGH_LIMIT = "rain_high_limit";
    public static final String COL_SHOULD_NOTIFY_RAIN = "should_notify_rain";
    public static final String COL_GPS_LAT = "gps_lat";
    public static final String COL_GPS_LNG = "gps_lng";
    public static final String COL_SHOULD_FETCH_DATA_PERIODICALLY = "should_fetch_data_periodically";
    public static final String COL_SERVER_URL = "server_url";
    public static final String COL_FETCH_DELAY_MS = "fetch_delay_ms";

    public static final double TEMPERATURE_LOWER_HARD_LIMIT = -40.0;
    public static final double TEMPERATURE_UPPER_HARD_LIMIT = 125.0;
    public static final double HUMIDITY_LOWER_HARD_LIMIT = 0.0;
    public static final double HUMIDITY_UPPER_HARD_LIMIT = 100.0;
    public static final double RAIN_LOWER_HARD_LIMIT = 0.0;
    public static final double RAIN_UPPER_HARD_LIMIT = 100.0;

    public static final String DEFAULT_SERVER_URL = "http://<your_username>.pythonanywhere.com:80/api/v1/user/device/data/get";
    public static final int DEFAULT_FETCH_DELAY_MS = 9000;

    private SQLiteDatabase db;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, 1);
        db = getWritableDatabase();
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(
                "CREATE TABLE " +
                        TABLE_APP_STORAGE +
                        "(" +
                        COL_ROW_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                        COL_DEVICE_ID + " TEXT, " +
                        COL_USER_PASSWORD + " TEXT, " +
                        COL_DEVICE_NAME + " TEXT, " +
                        COL_TIMESTAMP + " TEXT, " +
                        COL_TEMPERATURE + " REAL, " +
                        COL_TEMPERATURE_LOW_LIMIT + " REAL, " +
                        COL_TEMPERATURE_HIGH_LIMIT + " REAL, " +
                        COL_SHOULD_NOTIFY_TEMPERATURE + " INTEGER, " +
                        COL_HUMIDITY + " REAL, " +
                        COL_HUMIDITY_LOW_LIMIT + " REAL, " +
                        COL_HUMIDITY_HIGH_LIMIT + " REAL, " +
                        COL_SHOULD_NOTIFY_HUMIDITY + " INTEGER, " +
                        COL_RAIN + " REAL, " +
                        COL_RAIN_LOW_LIMIT + " REAL, " +
                        COL_RAIN_HIGH_LIMIT + " REAL, " +
                        COL_SHOULD_NOTIFY_RAIN + " INTEGER, " +
                        COL_GPS_LAT + " TEXT, " +
                        COL_GPS_LNG + " TEXT, " +
                        COL_SHOULD_FETCH_DATA_PERIODICALLY + " INTEGER, " +
                        COL_SERVER_URL + " TEXT, " +
                        COL_FETCH_DELAY_MS + " INTEGER " +
                        ")"
        );

        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_APP_STORAGE + " WHERE " + COL_ROW_ID + " = 1 ", null);
        Log.d("BEFORE_TABLE_POPULATED", Integer.toString(res.getCount()));
        if (res.getCount() > 0) {
            res.close();
            return;
        }

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DEVICE_ID, "");
        contentValues.put(COL_USER_PASSWORD, "");
        contentValues.put(COL_DEVICE_NAME, "");
        contentValues.put(COL_TIMESTAMP, "");
        contentValues.putNull(COL_TEMPERATURE);
        contentValues.put(COL_TEMPERATURE_LOW_LIMIT, TEMPERATURE_LOWER_HARD_LIMIT);
        contentValues.put(COL_TEMPERATURE_HIGH_LIMIT, TEMPERATURE_UPPER_HARD_LIMIT);
        contentValues.put(COL_SHOULD_NOTIFY_TEMPERATURE, 0);
        contentValues.putNull(COL_HUMIDITY);
        contentValues.put(COL_HUMIDITY_LOW_LIMIT, HUMIDITY_LOWER_HARD_LIMIT);
        contentValues.put(COL_HUMIDITY_HIGH_LIMIT, HUMIDITY_UPPER_HARD_LIMIT);
        contentValues.put(COL_SHOULD_NOTIFY_HUMIDITY, 0);
        contentValues.putNull(COL_RAIN);
        contentValues.put(COL_RAIN_LOW_LIMIT, RAIN_LOWER_HARD_LIMIT);
        contentValues.put(COL_RAIN_HIGH_LIMIT, RAIN_UPPER_HARD_LIMIT);
        contentValues.put(COL_SHOULD_NOTIFY_RAIN, 0);
        contentValues.putNull(COL_GPS_LAT);
        contentValues.putNull(COL_GPS_LNG);
        contentValues.put(COL_SHOULD_FETCH_DATA_PERIODICALLY, 0);
        contentValues.put(COL_SERVER_URL, DEFAULT_SERVER_URL);
        contentValues.put(COL_FETCH_DELAY_MS, DEFAULT_FETCH_DELAY_MS);

        db.insert(TABLE_APP_STORAGE, null, contentValues);

        res = db.rawQuery("SELECT * FROM " + TABLE_APP_STORAGE + " WHERE " + COL_ROW_ID + " = 1 ", null);
        Log.d("AFTER_TABLE_POPULATED", Integer.toString(res.getCount()));
        res.close();

        db.execSQL(
                "CREATE TABLE " +
                        TABLE_DEVICES_SETTINGS +
                        "(" +
                        COL_DEVICE_ID + " TEXT PRIMARY KEY, " +
                        COL_TEMPERATURE_LOW_LIMIT + " REAL, " +
                        COL_TEMPERATURE_HIGH_LIMIT + " REAL, " +
                        COL_SHOULD_NOTIFY_TEMPERATURE + " INTEGER, " +
                        COL_HUMIDITY_LOW_LIMIT + " REAL, " +
                        COL_HUMIDITY_HIGH_LIMIT + " REAL, " +
                        COL_SHOULD_NOTIFY_HUMIDITY + " INTEGER, " +
                        COL_RAIN_LOW_LIMIT + " REAL, " +
                        COL_RAIN_HIGH_LIMIT + " REAL, " +
                        COL_SHOULD_NOTIFY_RAIN + " INTEGER " +
                        ")"
        );
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_APP_STORAGE);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_DEVICES_SETTINGS);
        onCreate(db);
    }

    @Override
    protected void finalize() throws Throwable {
        super.finalize();
        this.close();
    }

    @Override
    public synchronized void close() {
        if (db != null && db.isOpen()) {
            db.close();
        }
        super.close();
    }

    public void loadDeviceSettings() {
        if (!isDeviceSettingsExistForCurrentDevice()) {
            return;
        }

        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_DEVICES_SETTINGS + " WHERE " + COL_DEVICE_ID + " = \"" + getDeviceId() + "\"", null);
        if (res.getCount() <= 0) {
            res.close();
            return;
        }

        res.moveToFirst();

        Double temperatureLowLimit = res.getDouble(res.getColumnIndex(COL_TEMPERATURE_LOW_LIMIT));
        Double temperatureHighLimit = res.getDouble(res.getColumnIndex(COL_TEMPERATURE_HIGH_LIMIT));
        Integer shouldNotifyTemperature = res.getInt(res.getColumnIndex(COL_SHOULD_NOTIFY_TEMPERATURE));

        Double humidityLowLimit = res.getDouble(res.getColumnIndex(COL_HUMIDITY_LOW_LIMIT));
        Double humidityHighLimit = res.getDouble(res.getColumnIndex(COL_HUMIDITY_HIGH_LIMIT));
        Integer shouldNotifyHumidity = res.getInt(res.getColumnIndex(COL_SHOULD_NOTIFY_HUMIDITY));

        Double rainLowLimit = res.getDouble(res.getColumnIndex(COL_RAIN_LOW_LIMIT));
        Double rainHighLimit = res.getDouble(res.getColumnIndex(COL_RAIN_HIGH_LIMIT));
        Integer shouldNotifyRain = res.getInt(res.getColumnIndex(COL_SHOULD_NOTIFY_RAIN));

        res.close();

        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_TEMPERATURE_LOW_LIMIT, temperatureLowLimit);
        contentValues.put(COL_TEMPERATURE_HIGH_LIMIT, temperatureHighLimit);
        contentValues.put(COL_SHOULD_NOTIFY_TEMPERATURE, shouldNotifyTemperature);

        contentValues.put(COL_HUMIDITY_LOW_LIMIT, humidityLowLimit);
        contentValues.put(COL_HUMIDITY_HIGH_LIMIT, humidityHighLimit);
        contentValues.put(COL_SHOULD_NOTIFY_HUMIDITY, shouldNotifyHumidity);

        contentValues.put(COL_RAIN_LOW_LIMIT, rainLowLimit);
        contentValues.put(COL_RAIN_HIGH_LIMIT, rainHighLimit);
        contentValues.put(COL_SHOULD_NOTIFY_RAIN, shouldNotifyRain);

        db.update(TABLE_APP_STORAGE, contentValues, COL_ROW_ID + " = ? ", new String[] { Integer.toString(1) });
    }

    public void saveDeviceSettings() {
        if (!isDeviceSettingsExistForCurrentDevice()) {
            return;
        }

        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_APP_STORAGE + " WHERE " + COL_ROW_ID + " = 1 ", null);
        if (res.getCount() < 1) {
            res.close();
            return;
        }

        res.moveToFirst();

        Double temperatureLowLimit = res.getDouble(res.getColumnIndex(COL_TEMPERATURE_LOW_LIMIT));
        Double temperatureHighLimit = res.getDouble(res.getColumnIndex(COL_TEMPERATURE_HIGH_LIMIT));
        Integer shouldNotifyTemperature = res.getInt(res.getColumnIndex(COL_SHOULD_NOTIFY_TEMPERATURE));

        Double humidityLowLimit = res.getDouble(res.getColumnIndex(COL_HUMIDITY_LOW_LIMIT));
        Double humidityHighLimit = res.getDouble(res.getColumnIndex(COL_HUMIDITY_HIGH_LIMIT));
        Integer shouldNotifyHumidity = res.getInt(res.getColumnIndex(COL_SHOULD_NOTIFY_HUMIDITY));

        Double rainLowLimit = res.getDouble(res.getColumnIndex(COL_RAIN_LOW_LIMIT));
        Double rainHighLimit = res.getDouble(res.getColumnIndex(COL_RAIN_HIGH_LIMIT));
        Integer shouldNotifyRain = res.getInt(res.getColumnIndex(COL_SHOULD_NOTIFY_RAIN));

        res.close();

        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_TEMPERATURE_LOW_LIMIT, temperatureLowLimit);
        contentValues.put(COL_TEMPERATURE_HIGH_LIMIT, temperatureHighLimit);
        contentValues.put(COL_SHOULD_NOTIFY_TEMPERATURE, shouldNotifyTemperature);

        contentValues.put(COL_HUMIDITY_LOW_LIMIT, humidityLowLimit);
        contentValues.put(COL_HUMIDITY_HIGH_LIMIT, humidityHighLimit);
        contentValues.put(COL_SHOULD_NOTIFY_HUMIDITY, shouldNotifyHumidity);

        contentValues.put(COL_RAIN_LOW_LIMIT, rainLowLimit);
        contentValues.put(COL_RAIN_HIGH_LIMIT, rainHighLimit);
        contentValues.put(COL_SHOULD_NOTIFY_RAIN, shouldNotifyRain);

        db.update(TABLE_DEVICES_SETTINGS, contentValues, COL_DEVICE_ID + " = ? ", new String[] { getDeviceId() });
    }

    public void addNewDeviceSettings() {
        ContentValues contentValues = new ContentValues();

        contentValues.put(COL_DEVICE_ID, getDeviceId());

        contentValues.put(COL_TEMPERATURE_LOW_LIMIT, TEMPERATURE_LOWER_HARD_LIMIT);
        contentValues.put(COL_TEMPERATURE_HIGH_LIMIT, TEMPERATURE_UPPER_HARD_LIMIT);
        contentValues.put(COL_SHOULD_NOTIFY_TEMPERATURE, 0);

        contentValues.put(COL_HUMIDITY_LOW_LIMIT, HUMIDITY_LOWER_HARD_LIMIT);
        contentValues.put(COL_HUMIDITY_HIGH_LIMIT, HUMIDITY_UPPER_HARD_LIMIT);
        contentValues.put(COL_SHOULD_NOTIFY_HUMIDITY, 0);

        contentValues.put(COL_RAIN_LOW_LIMIT, RAIN_LOWER_HARD_LIMIT);
        contentValues.put(COL_RAIN_HIGH_LIMIT, RAIN_UPPER_HARD_LIMIT);
        contentValues.put(COL_SHOULD_NOTIFY_RAIN, 0);

        db.insert(TABLE_DEVICES_SETTINGS, null, contentValues);
    }

    public boolean isDeviceSettingsExistForCurrentDevice() {
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_DEVICES_SETTINGS + " WHERE " + COL_DEVICE_ID + " = \"" + getDeviceId() + "\"", null);
        if (res.getCount() <= 0) {
            res.close();
            return false;
        }
        res.close();
        return true;
    }

    public boolean isDeviceIdOrPassEmpty() {
        boolean returnVal = false;

        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_APP_STORAGE + " WHERE " + COL_ROW_ID + " = 1 ", null);
        res.moveToFirst();

        if (res.getString(res.getColumnIndex(COL_DEVICE_ID)).equals("")
                || res.getString(res.getColumnIndex(COL_USER_PASSWORD)).equals("")) {
            res.close();
            returnVal = true;
        }

        res.close();
        return returnVal;
    }

    public Cursor getAllDataCursor() {
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_APP_STORAGE + " WHERE " + COL_ROW_ID + " = 1 ", null);
        res.moveToFirst();
        return res;
    }

    public String getDeviceId() {
        Cursor res = getAllDataCursor();
        String deviceId = res.getString(res.getColumnIndex(COL_DEVICE_ID));
        res.close();
        return deviceId;
    }
    public String getUserPassword() {
        Cursor res = getAllDataCursor();
        String userPassword = res.getString(res.getColumnIndex(COL_USER_PASSWORD));
        res.close();
        return userPassword;
    }
    public String getDeviceName() {
        Cursor res = getAllDataCursor();
        String deviceName;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_DEVICE_NAME))) {
            deviceName = null;
        } else {
            deviceName = res.getString(res.getColumnIndex(DatabaseHelper.COL_DEVICE_NAME));
        }
        res.close();
        return deviceName;
    }
    public String getTimestamp() {
        Cursor res = getAllDataCursor();
        String timestamp;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_TIMESTAMP))) {
            timestamp = null;
        } else {
            timestamp = res.getString(res.getColumnIndex(DatabaseHelper.COL_TIMESTAMP));
        }
        res.close();
        return timestamp;
    }
    public Double getTemperatureValue() {
        Cursor res = getAllDataCursor();
        Double temperatureValue;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_TEMPERATURE))) {
            temperatureValue = null;
        } else {
            temperatureValue = res.getDouble(res.getColumnIndex(DatabaseHelper.COL_TEMPERATURE));
        }
        res.close();
        return temperatureValue;
    }
    public Double getTemperatureLowLimitInput() {
        Cursor res = getAllDataCursor();
        Double temperatureLowLimitInput;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_TEMPERATURE_LOW_LIMIT))) {
            temperatureLowLimitInput = null;
        } else {
            temperatureLowLimitInput = res.getDouble(res.getColumnIndex(DatabaseHelper.COL_TEMPERATURE_LOW_LIMIT));
        }
        res.close();
        return temperatureLowLimitInput;
    }
    public Double getTemperatureUpperLimitInput() {
        Cursor res = getAllDataCursor();
        Double temperatureUpperLimitInput;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_TEMPERATURE_HIGH_LIMIT))) {
            temperatureUpperLimitInput = null;
        } else {
            temperatureUpperLimitInput = res.getDouble(res.getColumnIndex(DatabaseHelper.COL_TEMPERATURE_HIGH_LIMIT));
        }
        res.close();
        return temperatureUpperLimitInput;
    }
    public Integer getShouldNotifyTemperature() {
        Cursor res = getAllDataCursor();
        Integer shouldNotifyTemperature;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_SHOULD_NOTIFY_TEMPERATURE))) {
            shouldNotifyTemperature = null;
        } else {
            shouldNotifyTemperature = res.getInt(res.getColumnIndex(DatabaseHelper.COL_SHOULD_NOTIFY_TEMPERATURE));
        }
        res.close();
        return shouldNotifyTemperature;
    }
    public Double getHumidityValue() {
        Cursor res = getAllDataCursor();
        Double humidityValue;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_HUMIDITY))) {
            humidityValue = null;
        } else {
            humidityValue = res.getDouble(res.getColumnIndex(DatabaseHelper.COL_HUMIDITY));
        }
        res.close();
        return humidityValue;
    }
    public Double getHumidityLowLimitInput() {
        Cursor res = getAllDataCursor();
        Double humidityLowLimitInput;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_HUMIDITY_LOW_LIMIT))) {
            humidityLowLimitInput = null;
        } else {
            humidityLowLimitInput = res.getDouble(res.getColumnIndex(DatabaseHelper.COL_HUMIDITY_LOW_LIMIT));
        }
        res.close();
        return humidityLowLimitInput;
    }
    public Double getHumidityUpperLimitInput() {
        Cursor res = getAllDataCursor();
        Double humidityUpperLimitInput;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_HUMIDITY_HIGH_LIMIT))) {
            humidityUpperLimitInput = null;
        } else {
            humidityUpperLimitInput = res.getDouble(res.getColumnIndex(DatabaseHelper.COL_HUMIDITY_HIGH_LIMIT));
        }
        res.close();
        return humidityUpperLimitInput;
    }
    public Integer getShouldNotifyHumidity() {
        Cursor res = getAllDataCursor();
        Integer shouldNotifyHumidity;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_SHOULD_NOTIFY_HUMIDITY))) {
            shouldNotifyHumidity = null;
        } else {
            shouldNotifyHumidity = res.getInt(res.getColumnIndex(DatabaseHelper.COL_SHOULD_NOTIFY_HUMIDITY));
        }
        res.close();
        return shouldNotifyHumidity;
    }
    public Double getRainValue() {
        Cursor res = getAllDataCursor();
        Double rainValue;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_RAIN))) {
            rainValue = null;
        } else {
            rainValue = res.getDouble(res.getColumnIndex(DatabaseHelper.COL_RAIN));
        }
        res.close();
        return rainValue;
    }
    public Double getRainLowLimitInput() {
        Cursor res = getAllDataCursor();
        Double rainLowLimitInput;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_RAIN_LOW_LIMIT))) {
            rainLowLimitInput = null;
        } else {
            rainLowLimitInput = res.getDouble(res.getColumnIndex(DatabaseHelper.COL_RAIN_LOW_LIMIT));
        }
        res.close();
        return rainLowLimitInput;
    }
    public Double getRainUpperLimitInput() {
        Cursor res = getAllDataCursor();
        Double rainUpperLimitInput;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_RAIN_HIGH_LIMIT))) {
            rainUpperLimitInput = null;
        } else {
            rainUpperLimitInput = res.getDouble(res.getColumnIndex(DatabaseHelper.COL_RAIN_HIGH_LIMIT));
        }
        res.close();
        return rainUpperLimitInput;
    }
    public Integer getShouldNotifyRain() {
        Cursor res = getAllDataCursor();
        Integer shouldNotifyRain;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_SHOULD_NOTIFY_RAIN))) {
            shouldNotifyRain = null;
        } else {
            shouldNotifyRain = res.getInt(res.getColumnIndex(DatabaseHelper.COL_SHOULD_NOTIFY_RAIN));
        }
        res.close();
        return shouldNotifyRain;
    }
    public String getGpsLat() {
        Cursor res = getAllDataCursor();
        String gpsLat;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_GPS_LAT))) {
            gpsLat = null;
        } else {
            gpsLat = res.getString(res.getColumnIndex(DatabaseHelper.COL_GPS_LAT));
        }
        res.close();
        return gpsLat;
    }
    public String getGpsLng() {
        Cursor res = getAllDataCursor();
        String gpsLng;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_GPS_LNG))) {
            gpsLng = null;
        } else {
            gpsLng = res.getString(res.getColumnIndex(DatabaseHelper.COL_GPS_LNG));
        }
        res.close();
        return gpsLng;
    }
    public Integer getShouldFetchDataPeriodically() {
        Cursor res = getAllDataCursor();
        Integer shouldFetchDataPeriodically;
        if (res.isNull(res.getColumnIndex(DatabaseHelper.COL_SHOULD_FETCH_DATA_PERIODICALLY))) {
            shouldFetchDataPeriodically = null;
        } else {
            shouldFetchDataPeriodically = res.getInt(res.getColumnIndex(DatabaseHelper.COL_SHOULD_FETCH_DATA_PERIODICALLY));
        }
        res.close();
        return shouldFetchDataPeriodically;
    }
    public String getServerUrl() {
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_APP_STORAGE + " WHERE " + COL_ROW_ID + " = 1 ", null);
        res.moveToFirst();
        String serverUrl = res.getString(res.getColumnIndex(COL_SERVER_URL));
        res.close();
        return serverUrl;
    }
    public int getFetchDelay() {
        Cursor res = db.rawQuery("SELECT * FROM " + TABLE_APP_STORAGE + " WHERE " + COL_ROW_ID + " = 1 ", null);
        res.moveToFirst();
        int fetchDelay = res.getInt(res.getColumnIndex(COL_FETCH_DELAY_MS));
        res.close();
        return fetchDelay;
    }

    public boolean insertDeviceIdPassName(String device_id, String user_password, String device_name) {
        String oldDeviceId = getDeviceId();

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_DEVICE_ID, device_id);
        contentValues.put(COL_USER_PASSWORD, user_password);
        contentValues.put(COL_DEVICE_NAME, device_name);

        int numOfRowsEffected = db.update(TABLE_APP_STORAGE, contentValues, COL_ROW_ID + " = ? ", new String[] { Integer.toString(1) });

        if (!oldDeviceId.equals(device_id) && !isDeviceSettingsExistForCurrentDevice()) {
            addNewDeviceSettings();
        }
        loadDeviceSettings();
        return numOfRowsEffected > 0;
    }

    public boolean updateLimitColumn(String column_name, double limit) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column_name, limit);

        int numOfRowsEffected = db.update(TABLE_APP_STORAGE, contentValues, COL_ROW_ID + " = ? ", new String[] { Integer.toString(1) });
        saveDeviceSettings();
        return numOfRowsEffected > 0;
    }

    public boolean updateShouldColumn(String column_name, int state) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(column_name, state);

        int numOfRowsEffected = db.update(TABLE_APP_STORAGE, contentValues, COL_ROW_ID + " = ? ", new String[] { Integer.toString(1) });
        saveDeviceSettings();
        return numOfRowsEffected > 0;
    }

    public boolean insertDeviceData(
            String timestamp,
            Double temperature,
            Double humidity,
            Double rain,
            String gps_lat,
            String gps_lng
    ) {
        timestamp = timestamp != null ? timestamp.replace("GMT", "") : null;

        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_TIMESTAMP, timestamp);
        contentValues.put(COL_TEMPERATURE, temperature);
        contentValues.put(COL_HUMIDITY, humidity);
        contentValues.put(COL_RAIN, rain);
        contentValues.put(COL_GPS_LAT, gps_lat);
        contentValues.put(COL_GPS_LNG, gps_lng);

        int numOfRowsEffected = db.update(TABLE_APP_STORAGE, contentValues, COL_ROW_ID + " = ? ", new String[] { Integer.toString(1) });
        return numOfRowsEffected > 0;
    }

    public void updateServerUrl(String serverUrl) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_SERVER_URL, serverUrl);

        db.update(TABLE_APP_STORAGE, contentValues, COL_ROW_ID + " = ? ", new String[] { Integer.toString(1) });
    }
    public void updateFetchDelay(int fetchDelay) {
        ContentValues contentValues = new ContentValues();
        contentValues.put(COL_FETCH_DELAY_MS, fetchDelay);

        db.update(TABLE_APP_STORAGE, contentValues, COL_ROW_ID + " = ? ", new String[] { Integer.toString(1) });
    }
}
