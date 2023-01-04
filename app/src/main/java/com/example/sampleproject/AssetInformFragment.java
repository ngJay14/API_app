package com.example.sampleproject;

import android.annotation.SuppressLint;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.Spinner;
import android.widget.TextView;

import com.example.sampleproject.Model.Asset;
import com.google.android.material.navigation.NavigationView;
import com.jjoe64.graphview.GraphView;

import org.osmdroid.util.GarbageCollector;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class AssetInformFragment extends Fragment implements AdapterView.OnItemSelectedListener {

    private APIInterface apiInterface;
    private String asset_id;
    private String asset_name;

    private ArrayList<String> attributesName = new ArrayList<String>();
    private ArrayList<String> parameters = new ArrayList<String>();
    private ArrayList<String> statusUpdate = new ArrayList<String>();

    private boolean spinnerItemChanged = false;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        //Set status checked for nav item
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_asset);

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_asset_inform, container, false);

        //Set up spinner
        Spinner spinnerAssets = (Spinner) view.findViewById(R.id.spinner_assets);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.assets_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssets.setAdapter(adapter);

        spinnerAssets.setOnItemSelectedListener(this);

        //Format date
        @SuppressLint("SimpleDateFormat")
        SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd-MM-yyy");

        //Get asset id from Map Fragment
        if(getArguments() != null)
        {
            asset_id = getArguments().getString("asset_id");
            asset_name = getArguments().getString("asset_name");
        }
        else
        {
            asset_id = "6H4PeKLRMea1L0WsRXXWp9";
            asset_name = "Weather Asset";
        }

        //Set spinner selection
        spinnerAssets.setSelection(adapter.getPosition(asset_name));
        spinnerItemChanged = true;

        apiInterface = APIClient.getClient().create(APIInterface.class);

        Call<Asset> callAsset = apiInterface.getAsset(asset_id);

        callAsset.enqueue(new Callback<Asset>() {
            @Override
            public void onResponse(Call<Asset> call, Response<Asset> response) {
                Log.d("API CALL", response.code() + "");

                Asset asset = response.body();

                //Asset name
                TextView textViewNameAsset = view.findViewById(R.id.textViewNameAsset);
                textViewNameAsset.setTextColor(Color.BLACK);
                textViewNameAsset.setText(asset.name);

                //Infor view
                //Asset id
                TextView textViewId = view.findViewById(R.id.textViewId);
                textViewId.setText(asset.id);

                //Asset version
                TextView textViewVersion = view.findViewById(R.id.textViewVersion);
                textViewVersion.setText(asset.version);

                //Asset parentId
                TextView textViewParentId = view.findViewById(R.id.textViewParentId);
                textViewParentId.setText(asset.parentId);

                //Asset realm
                TextView textViewRealm = view.findViewById(R.id.textViewRealm);
                textViewRealm.setText(asset.realm);

                //Asset type
                TextView textViewType = view.findViewById(R.id.textViewType);
                textViewType.setText(asset.type);

                //Asset created on
                Calendar calendar = Calendar.getInstance();
                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                calendar.setTimeInMillis(Long.parseLong(asset.createdOn));

                int mYear = calendar.get(Calendar.YEAR);
                int mMonth = calendar.get(Calendar.MONTH) + 1;
                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                TextView textViewCreatedOn = view.findViewById(R.id.textViewCreatedOn);
                textViewCreatedOn.setText(mDay + "-" + mMonth + "-" + mYear);

//                // Setting header
//                TextView textView = new TextView(getActivity());
//                textView.setTypeface(Typeface.DEFAULT_BOLD);
//                textView.setTextSize(20);
//                textView.setTextColor(Color.BLACK);
//                textView.setText("ATTRIBUTES");

                //Set header for atribute item
                attributesName.add("Humidity (%)");
                attributesName.add("Temperature (℃)");
                attributesName.add("Feels like (℃)");
                attributesName.add("Wind speed (km/h)");
                attributesName.add("Sunset");
                attributesName.add("Sunrise");

                //Set body for atribute item
                parameters.add(asset.attributes.getAsJsonObject("humidity").get("value").getAsString());
                parameters.add(asset.attributes.getAsJsonObject("temperature").get("value").getAsString());
                parameters.add(asset.attributes.getAsJsonObject("weatherData").getAsJsonObject("value").getAsJsonObject("main").get("feels_like").getAsString());
                parameters.add(asset.attributes.getAsJsonObject("windSpeed").get("value").getAsString());
                parameters.add(sdf.format(new Date((long) asset.attributes.getAsJsonObject("weatherData").getAsJsonObject("value").getAsJsonObject("sys").get("sunset").getAsLong() * 1000)));
                parameters.add(sdf.format(new Date((long) asset.attributes.getAsJsonObject("weatherData").getAsJsonObject("value").getAsJsonObject("sys").get("sunrise").getAsLong() * 1000)));

                //Set footer for attribute item
                statusUpdate.add("Updated: " + sdf.format(new Date((long) asset.attributes.getAsJsonObject("humidity").get("timestamp").getAsLong())));
                statusUpdate.add("Updated: " + sdf.format(new Date((long) asset.attributes.getAsJsonObject("temperature").get("timestamp").getAsLong())));
                statusUpdate.add("Updated: " + sdf.format(new Date((long) asset.attributes.getAsJsonObject("temperature").get("timestamp").getAsLong())));
                statusUpdate.add("Updated: " + sdf.format(new Date((long) asset.attributes.getAsJsonObject("windSpeed").get("timestamp").getAsLong())));
                statusUpdate.add("Updated: " + sdf.format(new Date((long) asset.attributes.getAsJsonObject("windSpeed").get("timestamp").getAsLong())));
                statusUpdate.add("Updated: " + sdf.format(new Date((long) asset.attributes.getAsJsonObject("windSpeed").get("timestamp").getAsLong())));


                ListView listViewAttributes= (ListView) view.findViewById(android.R.id.list);

                // For populating list data
                AttributesList attributesList = new AttributesList(getActivity(), attributesName, parameters,statusUpdate);
                listViewAttributes.setAdapter(attributesList);

                listViewAttributes.setOnItemClickListener(new AdapterView.OnItemClickListener() {
                    @Override
                    public void onItemClick(AdapterView<?> adapterView, View view, int position, long l) {
                        String selectedFromList = (String) (listViewAttributes.getItemAtPosition(position));

                        if (selectedFromList.equals("Temperature (℃)") || selectedFromList.equals("Humidity (%)") || selectedFromList.equals("Wind speed (km/h)"))
                        {
                            //Create a bundle to send asset name and asset data for Graph Fragment
                            Bundle bundle = new Bundle();

                            if (selectedFromList.equals("Temperature (℃)"))
                            {
                                bundle.putString("asset_name", asset_name);
                                bundle.putString("asset_data", "Temperature");
                            }
                            else if (selectedFromList.equals("Humidity (%)"))
                            {
                                bundle.putString("asset_name", asset_name);
                                bundle.putString("asset_data", "Humidity");
                            }
                            else if (selectedFromList.equals("Wind speed (km/h)"))
                            {
                                bundle.putString("asset_name", asset_name);
                                bundle.putString("asset_data", "Windspeed");
                            }

                            // set Fragmentclass Arguments
                            GraphFragment graphFragment = new GraphFragment();
                            graphFragment.setArguments(bundle);

                            //Go to Graph Fragment
                            FragmentManager fragmentManager;
                            fragmentManager = requireActivity().getSupportFragmentManager();
                            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                            fragmentTransaction.replace(R.id.fragment_container, graphFragment);
                            fragmentTransaction.addToBackStack(null);
                            fragmentTransaction.commit();
                        }
                    }
                });

                Log.d("API CALL", "id: " + asset.id);
                Log.d("API CALL", "version: " + asset.version);
                Log.d("API CALL", "createdOn: " + asset.createdOn);
                Log.d("API CALL", "name: " + asset.name);
                Log.d("API CALL", "accessPublicRead: " + asset.accessPublicRead);
                Log.d("API CALL", "parentID: " + asset.parentId);
                Log.d("API CALL", "realm: " + asset.realm);
                Log.d("API CALL", "type: " + asset.type);
                Log.d("API CALL", "path: " + asset.path);
            }

            @Override
            public void onFailure(Call<Asset> callAssets, Throwable t) {
                Log.d("API CALL", t.getMessage());
            }
        });

        return view;
    }

    @Override
    public void onItemSelected(AdapterView<?> parent, View view, int position, long id)
    {
        ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);
        String selectedItem = parent.getItemAtPosition(position).toString();

        Log.d("TEST", asset_name + " - " + selectedItem);

        if (!spinnerItemChanged && !selectedItem.equals(asset_name))
        {
            //Create a bundle to send asset id for AssetInform Fragment
            Bundle bundle = new Bundle();

            if(selectedItem.equals("Weather Asset"))
            {
                bundle.putString("asset_id", "6H4PeKLRMea1L0WsRXXWp9");
                bundle.putString("asset_name", selectedItem);
            }
            else if(selectedItem.equals("Weather Asset 2"))
            {
                bundle.putString("asset_id", "2UZPM2Mvu11Xyq5jCWNMX1");
                bundle.putString("asset_name", selectedItem);
            }
            else
            {
                bundle.putString("asset_id", "4cdWlxEvmDRBBDEc2HRsaF");
                bundle.putString("asset_name", selectedItem);
            }

            // set Fragmentclass Arguments
            AssetInformFragment assetInformFragment = new AssetInformFragment();
            assetInformFragment.setArguments(bundle);

            //Go to Asset Infor Fragment again
            FragmentManager fragmentManager;
            fragmentManager = requireActivity().getSupportFragmentManager();
            FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
            fragmentTransaction.replace(R.id.fragment_container, assetInformFragment);
            fragmentTransaction.addToBackStack(null);
            fragmentTransaction.commit();
        }
        else
            spinnerItemChanged = false;

    }

    @Override
    public void onNothingSelected(AdapterView<?> parent) {
    }
}