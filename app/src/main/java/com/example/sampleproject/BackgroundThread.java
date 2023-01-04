package com.example.sampleproject;

import android.app.Service;
import android.content.Intent;
import android.database.Cursor;
import android.os.Handler;
import android.os.IBinder;
import android.util.Log;
import android.widget.Toast;

import androidx.annotation.Nullable;

import com.example.sampleproject.Model.Asset;
import com.example.sampleproject.Model.DbAssets;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class BackgroundThread extends Service {
    private static final String TAG = "Call Asset";
    private int mInterval = 3600000; // interval time is 1 hour
//    private int mInterval = 10000; // interval time is 10 seconds

    private Handler mHandler;

    private DbAssets dbAssets;
    private Cursor cursor;
    private List<String> assets;

    Runnable mCallAsset = new Runnable() {
        @Override
        public void run() {
            try {
                APIInterface apiInterface;
                apiInterface = APIClient.getClient().create(APIInterface.class);

                List<String> clone_assets = new ArrayList<String>();
                clone_assets.add("6H4PeKLRMea1L0WsRXXWp9");
                clone_assets.add("2UZPM2Mvu11Xyq5jCWNMX1");
                clone_assets.add("4cdWlxEvmDRBBDEc2HRsaF");

                dbAssets = new DbAssets(BackgroundThread.this);
                dbAssets.open();
                //dbAssets.deleteAllAssets();

                for(int i = 0; i >= 0; i++)
                {
                    if (i == clone_assets.size())
                        break;
                    else
                    {
                        //Get 1 clone asset
                        Call<Asset> callCloneAsset = apiInterface.getAsset(clone_assets.get(i));

                        callCloneAsset.enqueue(new Callback<Asset>() {
                            @Override
                            public void onResponse(Call<Asset> call, Response<Asset> response) {
                                Log.d("API CALL", response.code() + "");

                                Asset asset = response.body();

                                Calendar calendar = Calendar.getInstance();
                                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                                calendar.setTimeInMillis(asset.attributes.getAsJsonObject("temperature").get("timestamp").getAsLong());

                                int mYear = calendar.get(Calendar.YEAR);
                                int mMonth = calendar.get(Calendar.MONTH) + 1;
                                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                                int mHour = calendar.get(Calendar.HOUR_OF_DAY) + 7;
                                int mMin = calendar.get(Calendar.MINUTE);
                                int mSecond = calendar.get(Calendar.SECOND);


                                String humidity = asset.attributes.getAsJsonObject("humidity").get("value").getAsString();
                                String temperature = asset.attributes.getAsJsonObject("temperature").get("value").getAsString();
                                String windSpeed = asset.attributes.getAsJsonObject("windSpeed").get("value").getAsString();
                                String timestamp = asset.attributes.getAsJsonObject("weatherData").get("timestamp").getAsString();

                                //Add data into database
                                dbAssets.createAsset(asset.name, humidity, temperature, windSpeed, timestamp);

                                Log.d("API CAll", "Asset name: " + asset.name);
                                Log.d("API CALL", "Timestamp: " + timestamp);
                                Log.d("API CALL", "Timestamp: " + mHour + "h" + mMin + "m" + mSecond + "s | " + mDay + "-" + mMonth + "-" + mYear);
                                //Humidity
                                Log.d("API Call: ", "Humidity: " + humidity);
                                //Temperature
                                Log.d("API Call: ", "Temperature: " + temperature);
                                //Windspeed
                                Log.d("API Call: ", "Windspeed: " + windSpeed);
                                //Check call
                                Log.d("API CALL", "id: " + asset.id);
                                Log.d("API CALL", "version: " + asset.version);
                                Log.d("API CALL", "createdOn: " + asset.createdOn);
                                Log.d("API CALL", "*********End call********");

                            }
                            @Override
                            public void onFailure(Call<Asset> callAssets, Throwable t) {
                                Log.d("API CALL", t.getMessage());
                            }
                        });
                    }
                }

            }
            finally {
                mHandler.postDelayed(mCallAsset, mInterval);
            }
        }
    };

    private void startRepeatingCallAsset() {
        mCallAsset.run();
    }

    private void stopRepeatingCallAsset() {
        mHandler.removeCallbacks(mCallAsset);
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        Toast.makeText(this, "Call asset service created", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onCreate");

    }

    @Override
    public void onDestroy() {
        Toast.makeText(this, "Call asset service stopped", Toast.LENGTH_LONG).show();
        //stop thread
        stopRepeatingCallAsset();
    }

    @Override
    public void onStart(Intent intent, int startid) {
        Toast.makeText(this, "Call asset service started", Toast.LENGTH_LONG).show();
        Log.d(TAG, "onStart");
        //start thread
        mHandler = new Handler();
        startRepeatingCallAsset();
    }


}
