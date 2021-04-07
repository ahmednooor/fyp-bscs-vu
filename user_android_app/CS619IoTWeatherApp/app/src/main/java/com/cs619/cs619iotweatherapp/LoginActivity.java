package com.cs619.cs619iotweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.content.Context;
import android.content.Intent;
import android.graphics.Rect;
import android.os.Bundle;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
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

public class LoginActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);

        dbHelper = new DatabaseHelper(LoginActivity.this);

        DashboardActivity.handler.removeCallbacksAndMessages(null);
        showErrorBarIfIntentHasErrMessage();

        findViewById(R.id.settingsImageBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginActivity.this.startActivity(new Intent(LoginActivity.this, SettingsActivity.class));
            }
        });

        findViewById(R.id.loginButton).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                final String deviceId = ((EditText) findViewById(R.id.deviceIdEditText)).getText().toString();
                final String userPassword = ((EditText) findViewById(R.id.userPassEditText)).getText().toString();

                hideErrorBar();
                showLoginLoader();

                NetworkHelper networkHelper = new NetworkHelper();
                networkHelper.getDeviceData(
                    LoginActivity.this,
                    deviceId,
                    userPassword,
                    new NetworkHelper.GetDeviceDataCallbacks() {
                        @Override
                        public void onSuccess(Context context, JSONObject response) {
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
//                                Toast.makeText(context, e.toString(), Toast.LENGTH_LONG).show();
                                e.printStackTrace();
                                showErrorBar(e.getMessage() != null ? e.getMessage() : e.toString());
                            }

                            hideLoginLoader();
                        }

                        @Override
                        public void onError(Context context, VolleyError error) {
                            Log.d("VOLLEY_ERR", error.toString());
                            error.printStackTrace();
                            String message = "";

                            if (error.networkResponse != null && error.networkResponse.statusCode >= 400) {

                                if (error.networkResponse.statusCode == 400
                                        || error.networkResponse.statusCode == 401
                                        || error.networkResponse.statusCode == 402
                                        || error.networkResponse.statusCode == 403
                                        || error.networkResponse.statusCode == 404
                                ) {
                                    dbHelper.insertDeviceIdPassName("", "", "");

                                    if (context.getClass() != LoginActivity.class) {
                                        context.startActivity(new Intent(context, LoginActivity.class));
                                    }
                                }

                                try {
                                    String errResponse = new String(error.networkResponse.data,"UTF-8");
                                    JSONObject errJsonObj = new JSONObject(errResponse);
                                    message = errJsonObj.getString("message");
                                } catch (JSONException | UnsupportedEncodingException e) {
                                    e.printStackTrace();
                                    message = e.toString();
                                }

                            } else if (error instanceof NetworkError) {
                                message = "Cannot connect to Internet or URL is invalid...Please check your connection and URL!";
                            } else if (error instanceof ServerError) {
                                message = "The server could not be found. Please try again after some time!";
                            } else if (error instanceof AuthFailureError) {
                                message = "Authentication Error! Please try again after some time!";
                            } else if (error instanceof ParseError) {
                                message = "Parsing error! Please try again after some time!";
                            } else if (error instanceof TimeoutError) {
                                message = "Connection TimeOut! Please check your internet connection.";
                            } else {
                                message = "Something went wrong. Please try again after some time!";
                            }

//                            Toast.makeText(context, message, Toast.LENGTH_LONG).show();
                            showErrorBar(message);
                            hideLoginLoader();
                        }
                    }
                );
            }
        });
    }

    @Override
    protected void onResume() {
        super.onResume();
        DashboardActivity.handler.removeCallbacksAndMessages(null);
        showErrorBarIfIntentHasErrMessage();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
        dbHelper.close();
    }

    public void showLoginLoader() {
        findViewById(R.id.loginButton).setVisibility(View.GONE);
        findViewById(R.id.loginLoader).setVisibility(View.VISIBLE);
    }
    public void hideLoginLoader() {
        findViewById(R.id.loginButton).setVisibility(View.VISIBLE);
        findViewById(R.id.loginLoader).setVisibility(View.GONE);
    }

    public void showErrorBarIfIntentHasErrMessage() {
        Intent intent = getIntent();

        // Get the extras (if there are any)
        Bundle extras = intent.getExtras();
        if (extras != null) {
            if (extras.containsKey("errMessage")) {
                String errMessage = extras.getString("errMessage", "Something went Wrong. Please try again.");
                showErrorBar(errMessage);
            }
        }
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

    @Override
    public void onBackPressed() {
        moveTaskToBack(true);
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
