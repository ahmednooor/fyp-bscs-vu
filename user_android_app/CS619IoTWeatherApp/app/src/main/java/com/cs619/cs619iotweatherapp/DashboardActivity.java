package com.cs619.cs619iotweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.Switch;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.ParseError;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class DashboardActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;
    NotificationHelper notificationHelper;

    public static final Handler handler = new Handler();
    int fetchDelay = DatabaseHelper.DEFAULT_FETCH_DELAY_MS; //milliseconds
    Runnable handlerRunnable;

    ImageButton logOutImgBtn_Btn;
    TextView showOnMapLink_Tv;

    TextView deviceName_Tv;
    TextView deviceId_Tv;
    TextView timestamp_Tv;
    TextView lastChecked_Tv;

    ImageView temperatureNotifDot_Img;
    TextView temperatureValue_Tv;
    EditText temperatureLowLimitInput_Et;
    EditText temperatureUpperLimitInput_Et;
    CheckBox shouldNotifyTemperature_Cb;

    ImageView humidityNotifDot_Img;
    TextView humidityValue_Tv;
    EditText humidityLowLimitInput_Et;
    EditText humidityUpperLimitInput_Et;
    CheckBox shouldNotifyHumidity_Cb;

    ImageView rainNotifDot_Img;
    TextView rainValue_Tv;
    EditText rainLowLimitInput_Et;
    EditText rainUpperLimitInput_Et;
    CheckBox shouldNotifyRain_Cb;

    Switch shouldKeepFetchingData_Sw;

    String gps_lat = null;
    String gps_lng = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_dashboard);

        dbHelper = new DatabaseHelper(this);
        notificationHelper = new NotificationHelper(this);

        initViewsVariables();

        updateUI();
        bindEventListeners();

        fetchDelay = dbHelper.getFetchDelay();
        bindHandlerRunnable();
        handler.removeCallbacksAndMessages(null);
        handler.post(handlerRunnable);
    }

    @Override
    protected void onResume() {
        super.onResume();
        updateUI();
    }

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        handler.removeCallbacksAndMessages(null);
        dbHelper.close();
        notificationHelper.close();
    }

    public void initViewsVariables() {
        logOutImgBtn_Btn = findViewById(R.id.logOutImgBtn);
        showOnMapLink_Tv = findViewById(R.id.showOnMapLink);

        deviceName_Tv = findViewById(R.id.deviceName);
        deviceId_Tv = findViewById(R.id.deviceId);
        timestamp_Tv = findViewById(R.id.timestamp);
        lastChecked_Tv = findViewById(R.id.lastCheckedTextView);

        temperatureNotifDot_Img = findViewById(R.id.temperatureNotificationDot);
        temperatureValue_Tv = findViewById(R.id.temperatureValue);
        temperatureLowLimitInput_Et = findViewById(R.id.temperatureLowLimitInput);
        temperatureUpperLimitInput_Et = findViewById(R.id.temperatureUpperLimitInput);
        shouldNotifyTemperature_Cb = findViewById(R.id.shouldNotifyTemperature);

        humidityNotifDot_Img = findViewById(R.id.humidityNotificationDot);
        humidityValue_Tv = findViewById(R.id.humidityValue);
        humidityLowLimitInput_Et = findViewById(R.id.humidityLowLimitInput);
        humidityUpperLimitInput_Et = findViewById(R.id.humidityUpperLimitInput);
        shouldNotifyHumidity_Cb = findViewById(R.id.shouldNotifyHumidity);

        rainNotifDot_Img = findViewById(R.id.rainNotificationDot);
        rainValue_Tv = findViewById(R.id.rainValue);
        rainLowLimitInput_Et = findViewById(R.id.rainLowLimitInput);
        rainUpperLimitInput_Et = findViewById(R.id.rainUpperLimitInput);
        shouldNotifyRain_Cb = findViewById(R.id.shouldNotifyRain);

        shouldKeepFetchingData_Sw = findViewById(R.id.shouldKeepFetchingData);
    }

    private String getDateTime() {
//        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss", Locale.US);
        DateFormat dateFormat = new SimpleDateFormat("EEE, dd MMM yyyy HH:mm:ss", Locale.US);
        Date date = new Date();
        return dateFormat.format(date);
    }

    public void bindHandlerRunnable() {
        handlerRunnable = new Runnable() {
//            Runnable _this = this;
            public void run() {
                fetchDelay = dbHelper.getFetchDelay();

                if (dbHelper.getShouldFetchDataPeriodically() != 1) {
                    handler.postDelayed(handlerRunnable, fetchDelay);
                    return;
                }

                findViewById(R.id.fetchingLoader).setVisibility(View.VISIBLE);

                if (dbHelper.isDeviceIdOrPassEmpty()) {
                    handler.removeCallbacksAndMessages(null);
                    startActivity(new Intent(DashboardActivity.this, LoginActivity.class));
                }

                final String deviceId = dbHelper.getDeviceId();
                final String userPassword = dbHelper.getUserPassword();

                NetworkHelper networkHelper = new NetworkHelper();
                networkHelper.getDeviceData(
                        DashboardActivity.this,
                        deviceId,
                        userPassword,
                        new NetworkHelper.GetDeviceDataCallbacks() {
                            @Override
                            public void onSuccess(Context context, JSONObject response) {
                                findViewById(R.id.fetchingLoader).setVisibility(View.INVISIBLE);

                                try {
                                    dbHelper.insertDeviceIdPassName(
                                            deviceId,
                                            userPassword,
                                            response.isNull("name") ? null : response.getString("name")
                                    );
                                    dbHelper.insertDeviceData(
                                            response.isNull("timestamp") ? null : response.getString("timestamp"),
                                            response.isNull("temperature_c") ? null : response.getDouble("temperature_c"),
                                            response.isNull("humidity") ? null : response.getDouble("humidity"),
                                            response.isNull("rain") ? null : response.getDouble("rain"),
                                            response.isNull("gps_lat") ? null : response.getString("gps_lat"),
                                            response.isNull("gps_lng") ? null : response.getString("gps_lng")
                                    );

                                    Log.d("VOLLEY_OK", response.toString());

                                    hideErrorBar();

                                    if (context.getClass() != DashboardActivity.class) {
                                        context.startActivity(new Intent(context, DashboardActivity.class));
                                    }
                                } catch (JSONException e) {
                                    e.printStackTrace();
                                    showErrorBar(e.getMessage() != null ? e.getMessage() : e.toString());
                                }

                                updateUI();
                                notificationHelper.handleNotifications();
                                handler.postDelayed(handlerRunnable, fetchDelay);
                            }

                            @Override
                            public void onError(Context context, VolleyError error) {
                                findViewById(R.id.fetchingLoader).setVisibility(View.INVISIBLE);

                                Log.d("VOLLEY_ERR", error.toString());
                                error.printStackTrace();

                                String message = "";

                                if (error.networkResponse != null && error.networkResponse.statusCode >= 400) {

                                    try {
                                        String errResponse = new String(error.networkResponse.data,"UTF-8");
                                        JSONObject errJsonObj = new JSONObject(errResponse);
                                        message = errJsonObj.getString("message");
                                    } catch (JSONException | UnsupportedEncodingException e) {
                                        e.printStackTrace();
                                        message = e.toString();
                                    }

                                    if (error.networkResponse.statusCode == 400
                                            || error.networkResponse.statusCode == 401
                                            || error.networkResponse.statusCode == 402
                                            || error.networkResponse.statusCode == 403
                                            || error.networkResponse.statusCode == 404
                                    ) {
                                        dbHelper.insertDeviceIdPassName("", "", "");

                                        if (context.getClass() != LoginActivity.class) {
                                            handler.removeCallbacksAndMessages(null);
                                            Intent loginIntent = new Intent(DashboardActivity.this, LoginActivity.class);
                                            loginIntent.putExtra("errMessage", message);
                                            DashboardActivity.this.startActivity(loginIntent);
                                            return;
                                        }
                                    }

                                } else if (error instanceof NetworkError) {
                                    message = "Cannot connect to Internet...Please check your connection!";
                                } else if (error instanceof ServerError) {
                                    message = "The server could not be found. Please try again after some time!";
                                } else if (error instanceof AuthFailureError) {
                                    message = "Cannot connect to Internet...Please check your connection!";
                                } else if (error instanceof ParseError) {
                                    message = "Parsing error! Please try again after some time!!";
                                } else if (error instanceof TimeoutError) {
                                    message = "Connection TimeOut! Please check your internet connection.";
                                } else {
                                    message = "Something went wrong. Please try again after some time!";
                                }

//                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                                showErrorBar(message);
                                updateUI();
                                handler.postDelayed(handlerRunnable, fetchDelay);
                            }
                        }
                );

            }
        };
    }

    public void updateUI() {
        if (dbHelper.isDeviceIdOrPassEmpty()) {
            startActivity(new Intent(this, LoginActivity.class));
        }

        String deviceName = dbHelper.getDeviceName();
        String deviceId = dbHelper.getDeviceId();
        String timestamp = dbHelper.getTimestamp();

        Double temperatureValue = dbHelper.getTemperatureValue();
        Double temperatureLowLimitInput = dbHelper.getTemperatureLowLimitInput();
        Double temperatureUpperLimitInput = dbHelper.getTemperatureUpperLimitInput();
        Integer shouldNotifyTemperature = dbHelper.getShouldNotifyTemperature();

        Double humidityValue = dbHelper.getHumidityValue();
        Double humidityLowLimitInput = dbHelper.getHumidityLowLimitInput();
        Double humidityUpperLimitInput = dbHelper.getHumidityUpperLimitInput();
        Integer shouldNotifyHumidity = dbHelper.getShouldNotifyHumidity();

        Double rainValue = dbHelper.getRainValue();
        Double rainLowLimitInput = dbHelper.getRainLowLimitInput();
        Double rainUpperLimitInput = dbHelper.getRainUpperLimitInput();
        Integer shouldNotifyRain = dbHelper.getShouldNotifyRain();

        Integer shouldFetchDataPeriodically = dbHelper.getShouldFetchDataPeriodically();

        gps_lat = dbHelper.getGpsLat();
        gps_lng = dbHelper.getGpsLng();


        deviceName_Tv.setText(deviceName == null ? "--" : deviceName);
        deviceId_Tv.setText(String.format("ID: %s", deviceId == null ? "--" : deviceId));
        timestamp_Tv.setText(String.format("Last Updated: %s", timestamp == null ? "--" : timestamp));
        lastChecked_Tv.setText(String.format("Last Checked: %s", getDateTime()));


        temperatureValue_Tv.setText(String.format("%s", temperatureValue == null ? "--" : temperatureValue + " °C"));
        if (!temperatureLowLimitInput_Et.isFocused()) {
            temperatureLowLimitInput_Et.setText(String.format("%s", temperatureLowLimitInput));
        }
        if (!temperatureUpperLimitInput_Et.isFocused()) {
            temperatureUpperLimitInput_Et.setText(String.format("%s", temperatureUpperLimitInput));
        }
        shouldNotifyTemperature_Cb.setChecked(shouldNotifyTemperature == 1);


        humidityValue_Tv.setText(String.format("%s", humidityValue == null ? "--" : humidityValue + " %"));
        if (!humidityLowLimitInput_Et.isFocused()) {
            humidityLowLimitInput_Et.setText(String.format("%s", humidityLowLimitInput));
        }
        if (!humidityUpperLimitInput_Et.isFocused()) {
            humidityUpperLimitInput_Et.setText(String.format("%s", humidityUpperLimitInput));
        }
        shouldNotifyHumidity_Cb.setChecked(shouldNotifyHumidity == 1);


        rainValue_Tv.setText(String.format("%s", rainValue == null ? "--" : rainValue + " mm"));
        if (!rainLowLimitInput_Et.isFocused()) {
            rainLowLimitInput_Et.setText(String.format("%s", rainLowLimitInput));
        }
        if (!rainUpperLimitInput_Et.isFocused()) {
            rainUpperLimitInput_Et.setText(String.format("%s", rainUpperLimitInput));
        }
        shouldNotifyRain_Cb.setChecked(shouldNotifyRain == 1);


        shouldKeepFetchingData_Sw.setChecked(shouldFetchDataPeriodically == 1);


        if (temperatureValue != null && (temperatureValue < temperatureLowLimitInput || temperatureValue > temperatureUpperLimitInput)) {
            temperatureValue_Tv.setTextColor(0xFF6200EE);
            temperatureNotifDot_Img.setVisibility(View.VISIBLE);
        } else {
            temperatureValue_Tv.setTextColor(0xFF212121);
            temperatureNotifDot_Img.setVisibility(View.GONE);
        }

        if (humidityValue != null && (humidityValue < humidityLowLimitInput || humidityValue > humidityUpperLimitInput)) {
            humidityNotifDot_Img.setVisibility(View.VISIBLE);
            humidityValue_Tv.setTextColor(0xFF6200EE);
        } else {
            humidityValue_Tv.setTextColor(0xFF212121);
            humidityNotifDot_Img.setVisibility(View.GONE);
        }

        if (rainValue != null && (rainValue < rainLowLimitInput || rainValue > rainUpperLimitInput)) {
            rainValue_Tv.setTextColor(0xFF6200EE);
            rainNotifDot_Img.setVisibility(View.VISIBLE);
        } else {
            rainValue_Tv.setTextColor(0xFF212121);
            rainNotifDot_Img.setVisibility(View.GONE);
        }

    }

    public void bindEventListeners() {
        logOutImgBtn_Btn.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logOutOfDevice();
            }
        });

        showOnMapLink_Tv.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (gps_lat == null || gps_lat.equals("")) gps_lat = "0.0";
                if (gps_lng == null || gps_lng.equals("")) gps_lng = "0.0";

                Intent intent = new Intent(DashboardActivity.this, LocationActivity.class);
                intent.putExtra("gps_lat", Double.parseDouble(gps_lat));
                intent.putExtra("gps_lng", Double.parseDouble(gps_lng));
                startActivity(intent);
            }
        });

        bindLowerUpperTextEditListeners(
                R.id.temperatureLowLimitInput, R.id.temperatureUpperLimitInput,
                DatabaseHelper.TEMPERATURE_LOWER_HARD_LIMIT, DatabaseHelper.TEMPERATURE_UPPER_HARD_LIMIT,
                DatabaseHelper.COL_TEMPERATURE_LOW_LIMIT, DatabaseHelper.COL_TEMPERATURE_HIGH_LIMIT
        );
        bindLowerUpperTextEditListeners(
                R.id.humidityLowLimitInput, R.id.humidityUpperLimitInput,
                DatabaseHelper.HUMIDITY_LOWER_HARD_LIMIT, DatabaseHelper.HUMIDITY_UPPER_HARD_LIMIT,
                DatabaseHelper.COL_HUMIDITY_LOW_LIMIT, DatabaseHelper.COL_HUMIDITY_HIGH_LIMIT
        );
        bindLowerUpperTextEditListeners(
                R.id.rainLowLimitInput, R.id.rainUpperLimitInput,
                DatabaseHelper.RAIN_LOWER_HARD_LIMIT, DatabaseHelper.RAIN_UPPER_HARD_LIMIT,
                DatabaseHelper.COL_RAIN_LOW_LIMIT, DatabaseHelper.COL_RAIN_HIGH_LIMIT
        );

        bindShouldNotifyCheckBoxListeners(R.id.shouldNotifyTemperature, DatabaseHelper.COL_SHOULD_NOTIFY_TEMPERATURE);
        bindShouldNotifyCheckBoxListeners(R.id.shouldNotifyHumidity, DatabaseHelper.COL_SHOULD_NOTIFY_HUMIDITY);
        bindShouldNotifyCheckBoxListeners(R.id.shouldNotifyRain, DatabaseHelper.COL_SHOULD_NOTIFY_RAIN);

        bindShouldFetchDataSwitchListeners(R.id.shouldKeepFetchingData, DatabaseHelper.COL_SHOULD_FETCH_DATA_PERIODICALLY);
    }

    public void bindLowerUpperTextEditListeners(
            final int lowerLimitEditTextId, final int upperLimitEditTextId,
            final double lowerHardLimit, final double upperHardLimit,
            final String lowLimitColumnDb, final String upperLimitColumnDb
    ) {
        ((EditText) findViewById(lowerLimitEditTextId)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) return;
                try {
                    double inputVal = Double.parseDouble(String.valueOf(((EditText) v).getText()));
                    double upperInput = Double.parseDouble(String.valueOf(((EditText) findViewById(upperLimitEditTextId)).getText()));
                    if (inputVal >= upperInput) {
                        Toast.makeText(DashboardActivity.this, "Lower limit should be less than upper limit.", Toast.LENGTH_LONG).show();
                        updateUI();
                        return;
                    }
                    if (inputVal > upperHardLimit) {
                        Toast.makeText(DashboardActivity.this, "Lower limit should be less than " + upperHardLimit, Toast.LENGTH_LONG).show();
                        updateUI();
                        return;
                    }
                    if (inputVal < lowerHardLimit) {
                        Toast.makeText(DashboardActivity.this, "Lower limit should be greater than " + lowerHardLimit, Toast.LENGTH_LONG).show();
                        updateUI();
                        return;
                    }
                    dbHelper.updateLimitColumn(lowLimitColumnDb, inputVal);
                } catch (Exception e) {
                    Toast.makeText(DashboardActivity.this, "Invalid Value.", Toast.LENGTH_LONG).show();
                }
                updateUI();
            }
        });

        ((EditText) findViewById(upperLimitEditTextId)).setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) return;
                try {
                    double inputVal = Double.parseDouble(String.valueOf(((EditText) v).getText()));
                    double lowerInput = Double.parseDouble(String.valueOf(((EditText) findViewById(lowerLimitEditTextId)).getText()));
                    if (inputVal <= lowerInput) {
                        Toast.makeText(DashboardActivity.this, "Upper limit should be greater than lower limit.", Toast.LENGTH_LONG).show();
                        updateUI();
                        return;
                    }
                    if (inputVal > upperHardLimit) {
                        Toast.makeText(DashboardActivity.this, "Upper limit should be less than " + upperHardLimit, Toast.LENGTH_LONG).show();
                        updateUI();
                        return;
                    }
                    if (inputVal < lowerHardLimit) {
                        Toast.makeText(DashboardActivity.this, "Upper limit should be greater than " + lowerHardLimit, Toast.LENGTH_LONG).show();
                        updateUI();
                        return;
                    }
                    dbHelper.updateLimitColumn(upperLimitColumnDb, inputVal);
                } catch (Exception e) {
                    Toast.makeText(DashboardActivity.this, "Invalid Value.", Toast.LENGTH_LONG).show();
                }
                updateUI();
            }
        });
    }

    public void bindShouldNotifyCheckBoxListeners(final int checkBoxId, final String shouldNotifyColumnDb) {
        ((CheckBox) findViewById(checkBoxId)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dbHelper.updateShouldColumn(shouldNotifyColumnDb, 1);
                } else {
                    dbHelper.updateShouldColumn(shouldNotifyColumnDb, 0);
                }
            }
        });
    }

    public void bindShouldFetchDataSwitchListeners(final int switchId, final String shouldFetchColumnDb) {
        ((Switch) findViewById(switchId)).setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                if (isChecked) {
                    dbHelper.updateShouldColumn(shouldFetchColumnDb, 1);
                } else {
                    dbHelper.updateShouldColumn(shouldFetchColumnDb, 0);
                }
                handler.removeCallbacksAndMessages(null);
                handler.post(handlerRunnable);
            }
        });
    }

    public void showErrorBar(String message) {
        TextView errBar_Tv = findViewById(R.id.errTextView);
        errBar_Tv.setText(message);
        errBar_Tv.setVisibility(View.VISIBLE);
    }
    public void hideErrorBar() {
        TextView errBar_Tv = findViewById(R.id.errTextView);
        errBar_Tv.setText("");
        errBar_Tv.setVisibility(View.GONE);
    }

    public void logOutOfDevice() {
        handler.removeCallbacksAndMessages(null);
        dbHelper.insertDeviceIdPassName("", "", "");
        notificationHelper.reset();
        startActivity(new Intent(DashboardActivity.this, MainActivity.class));
    }

    //    this clears focus from edit text views when tapped outside of the edit text
    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (event.getAction() == MotionEvent.ACTION_UP) {
            View v = getCurrentFocus();
            if ( v instanceof EditText) {
                Rect outRect = new Rect();
                v.getGlobalVisibleRect(outRect);
                if (!outRect.contains((int)event.getRawX(), (int)event.getRawY())) {
                    v.clearFocus();
                    InputMethodManager imm = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    if (imm != null) {
                        imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
                    }
                }
            }
        }
        return super.dispatchTouchEvent( event );
    }
}
