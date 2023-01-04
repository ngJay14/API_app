package com.example.sampleproject;

import android.os.Bundle;

import androidx.fragment.app.Fragment;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sampleproject.Model.User;

import java.util.Calendar;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class UserInformFragment extends Fragment {

    APIInterface apiInterface;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<User> userCall = apiInterface.getUser();


        userCall.enqueue(new Callback<User>() {
            @Override
            public void onResponse(Call<User> call, Response<User> response) {
                Log.d("API CALL", response.code() + "");

                User user = response.body();

                //Username
                TextView usernameText = (TextView) getView().findViewById(R.id.username);
                usernameText.setText(user.username);

                //ID
                TextView useridText = (TextView) getView().findViewById(R.id.uesrid);
                useridText.setText(user.id);

                //Realm
                TextView realmText = (TextView) getView().findViewById(R.id.realm);
                realmText.setText(user.realm);

                //RealmId
                TextView realmIdText = (TextView) getView().findViewById(R.id.realmId);
                realmIdText.setText(user.realmId);

                //Enabled
                TextView enabledText = (TextView) getView().findViewById(R.id.enabled);
                enabledText.setText(user.enabled.toString());

                //Service account
                TextView serviceAccText = (TextView) getView().findViewById(R.id.serviceAcc);
                serviceAccText.setText("" + user.serviceAccount);

                //Created On
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeInMillis(Long.parseLong(user.createdOn));

                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH) + 1;
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                TextView createdOnText = (TextView) getView().findViewById(R.id.createdOn);
                createdOnText.setText(mDay + "-" + mMonth + "-" + mYear);

                //Show log
                Log.d("API CALL", "Realm: " + user.realm);
                Log.d("API CALL", "Realm ID: " + user.realmId);
                Log.d("API CALL", "ID: " + user.id);
                Log.d("API CALL", "Enabled: " + user.enabled);
                Log.d("API CALL", "Created on: " + user.createdOn);
                Log.d("API CALL", "Service Account: " + user.serviceAccount);
                Log.d("API CALL", "Username: " + user.username);


            }

            @Override
            public void onFailure(Call<User> callAssets, Throwable t) {
                Log.d("API CALL", t.getMessage());
            }
        });
        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_user_inform, container, false);
    }
}