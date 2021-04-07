package com.cs619.cs619iotweatherapp;

import android.content.Context;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.util.Arrays;
import java.util.HashMap;


public class NetworkHelper {
    public interface GetDeviceDataCallbacks {
        void onSuccess(final Context context, JSONObject response);
        void onError(final Context context, VolleyError error);
    }

    public NetworkHelper() { }

    public void getDeviceData(
            final Context context,
            final String device_id,
            final String user_password,
            final GetDeviceDataCallbacks getDeviceDataCallbacks
    ) {
        // Instantiate the RequestQueue.
        RequestQueue queue = Volley.newRequestQueue(context);

        DatabaseHelper dbHelper = new DatabaseHelper(context);
        String url = dbHelper.getServerUrl();
        dbHelper.close();

        HashMap<String, String> jsonReqBody = new HashMap<String, String>();
        jsonReqBody.put("device_id", device_id);
        jsonReqBody.put("user_password", user_password);

        JsonObjectRequest jsonObjectRequest = new JsonObjectRequest(
                Request.Method.POST,
                url,
                new JSONObject(jsonReqBody),
                new Response.Listener<JSONObject>() {
                    @Override
                    public void onResponse(JSONObject response) {
                        getDeviceDataCallbacks.onSuccess(context, response);
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        getDeviceDataCallbacks.onError(context, error);
                    }
                });

        // Add the request to the RequestQueue.
        queue.add(jsonObjectRequest);
    }
}
