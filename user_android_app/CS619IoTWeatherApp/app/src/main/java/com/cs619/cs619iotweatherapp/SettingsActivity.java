package com.cs619.cs619iotweatherapp;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

public class SettingsActivity extends AppCompatActivity {
    DatabaseHelper dbHelper;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settings);

        dbHelper = new DatabaseHelper(this);

        updateUI();

        findViewById(R.id.serverUrlSaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    String serverUrl = String.valueOf(((EditText) findViewById(R.id.serverUrlEditText)).getText());
                    if (serverUrl.equals("")) {
                        Toast.makeText(SettingsActivity.this, "Server URL can't be empty.", Toast.LENGTH_LONG).show();
                        updateUI();
                        return;
                    }
                    dbHelper.updateServerUrl(serverUrl);
                } catch (Exception e) {
                    Toast.makeText(SettingsActivity.this, "Invalid value.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                updateUI();
            }
        });

        findViewById(R.id.fetchDelaySaveBtn).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    int fetchDelay = Integer.parseInt(String.valueOf(((EditText) findViewById(R.id.fetchDelayEditText)).getText()));
                    if (fetchDelay == 0) {
                        Toast.makeText(SettingsActivity.this, "Fetch Delay can't be 0.", Toast.LENGTH_LONG).show();
                        updateUI();
                        return;
                    }
                    dbHelper.updateFetchDelay(fetchDelay*1000);
                } catch (Exception e) {
                    Toast.makeText(SettingsActivity.this, "Invalid value.", Toast.LENGTH_LONG).show();
                    e.printStackTrace();
                }
                updateUI();
            }
        });
    }

    public void updateUI() {
        String serverUrl = dbHelper.getServerUrl();
        String serverUrlTextView = "SERVER URL:\n".concat(serverUrl);

        String fetchDelay = Integer.toString(dbHelper.getFetchDelay()/1000);
        String fetchDelayTextView = "FETCH INTERVAL IN SECONDS: ".concat(fetchDelay);

        ((TextView) findViewById(R.id.serverUrlTextView)).setText(serverUrlTextView);
        ((EditText) findViewById(R.id.serverUrlEditText)).setText(serverUrl);

        ((TextView) findViewById(R.id.fetchDelayTextView)).setText(fetchDelayTextView);
        ((EditText) findViewById(R.id.fetchDelayEditText)).setText(fetchDelay);
    }
}
