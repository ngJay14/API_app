package com.example.sampleproject;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.os.Bundle;

import androidx.appcompat.app.AlertDialog;
import androidx.drawerlayout.widget.DrawerLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.preference.PreferenceManager;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import com.example.sampleproject.Model.AllAsset;
import com.example.sampleproject.Model.Asset;
import com.example.sampleproject.Model.Map;
import com.google.gson.JsonArray;

import org.osmdroid.api.IMapController;
import org.osmdroid.config.Configuration;
import org.osmdroid.tileprovider.tilesource.TileSourceFactory;
import org.osmdroid.util.BoundingBox;
import org.osmdroid.util.GeoPoint;
import org.osmdroid.views.MapView;
import org.osmdroid.views.overlay.ItemizedIconOverlay;
import org.osmdroid.views.overlay.ItemizedOverlayWithFocus;
import org.osmdroid.views.overlay.OverlayItem;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.List;
import java.util.TimeZone;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class MapFragment extends Fragment {

    APIInterface apiInterface;
    MapView mapview;
    View view;
    DrawerLayout drawerLayout;
    IMapController mapController;

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        apiInterface = APIClient.getClient().create(APIInterface.class);

        //List marker
        ArrayList<OverlayItem> items = new ArrayList<>();

        //List information of asset
        List<String> assetsName = new ArrayList<String>();
        List<String> assetsId = new ArrayList<String>();
        List<String> assetsVer = new ArrayList<String>();
        List<String> assetsCreated = new ArrayList<String>();


        Call<Map> callMap = apiInterface.getMap();
        Call<List<AllAsset>> callAllAsset = apiInterface.getAllAsset();

        //Create dialog
        LayoutInflater li = LayoutInflater.from(getActivity());
        View customDialogView = li.inflate(R.layout.asset_dialog, null);

        AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(getActivity());
        alertDialogBuilder.setView(customDialogView);

        //View of dialog
        TextView assetName = (TextView) customDialogView.findViewById(R.id.asset_name);
        TextView assetId = (TextView) customDialogView.findViewById(R.id.asset_id);
        TextView assetVer = (TextView) customDialogView.findViewById(R.id.asset_ver);
        TextView assetCreatedOn = (TextView) customDialogView.findViewById(R.id.asset_createdOn);

        List<String> clone_assets = new ArrayList<String>();
        clone_assets.add("2UZPM2Mvu11Xyq5jCWNMX1");
        clone_assets.add("4cdWlxEvmDRBBDEc2HRsaF");
        clone_assets.add("6H4PeKLRMea1L0WsRXXWp9");

        //Call map
        callMap.enqueue(new Callback<Map>() {
            @SuppressLint("ResourceType")
            @Override
            public void onResponse(Call<Map> call, Response<Map> response) {

                Log.d("API CALL", response.code() + "");
                Map map = response.body();

                JsonArray location = map.options.getAsJsonObject("default").getAsJsonArray("center");
                JsonArray bound = map.options.getAsJsonObject("default").getAsJsonArray("bounds");

                //Get center location
                double latitude = Double.parseDouble(location.get(1).getAsString());
                double longtitude = Double.parseDouble(location.get(0).getAsString());

                //Get bounds
                double north = bound.get(3).getAsDouble();
                double east = bound.get(2).getAsDouble();
                double south = bound.get(1).getAsDouble();
                double west = bound.get(0).getAsDouble();

                Log.d("API CALL", String.valueOf(latitude));
                Log.d("API CALL", String.valueOf(longtitude));


                //Set up Open street map
                Context ctx = requireActivity().getApplicationContext();

                //important! set your user agent to prevent getting banned from the osm servers
                Configuration.getInstance().load(ctx, PreferenceManager.getDefaultSharedPreferences(ctx));

                mapview = (MapView) getView().findViewById(R.id.mapView);
                mapview.setTileSource(TileSourceFactory.MAPNIK);

                mapController = mapview.getController();
                mapController.setZoom(17.0);
                mapview.setMaxZoomLevel(22.0);
                mapview.setMinZoomLevel(16.0);

                GeoPoint startPoint = new GeoPoint(latitude, longtitude);
                mapController.setCenter(startPoint);

                mapview.addOnFirstLayoutListener(new MapView.OnFirstLayoutListener() {
                    @Override
                    public void onFirstLayout(View v, int left, int top, int right, int bottom) {
                        BoundingBox b = new BoundingBox(north, east, south, west);
                        mapview.zoomToBoundingBox(b,false,100);
                        mapview.invalidate();
                    }
                });
            }

            @Override
            public void onFailure(Call<Map> callMap, Throwable t) {
                Log.d("API CALL", t.getMessage());
            }
        });

        //Call asset and add marker into map
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


                        if(asset.attributes.getAsJsonObject("weatherData") != null)
                        {
                            JsonArray location = asset.attributes.getAsJsonObject("location").getAsJsonObject("value").getAsJsonArray("coordinates");

                            //Get location of asset which has lat and lon
                            double lat = location.get(1).getAsDouble();
                            double lon = location.get(0).getAsDouble();

                            Log.d("API CALL", "Asset name: " + asset.name);
                            Log.d("API CALL", "Lat: " + lat);
                            Log.d("API CALL", "Long: " + lon);

                            //Add information of asset into List inform
                            assetsName.add(asset.name);
                            assetsId.add(asset.id);
                            assetsVer.add(asset.version);
                            assetsCreated.add(asset.createdOn);

                            //Set marker
                            OverlayItem assetLoc = new OverlayItem(asset.name, "", new GeoPoint(lat, lon));
//                            Drawable m = assetLoc.getMarker(1);
                            @SuppressLint("UseCompatLoadingForDrawables")
                            Drawable m = getResources().getDrawable(R.drawable.marker);
                            assetLoc.setMarker(m);
                            items.add(assetLoc);

                            ItemizedOverlayWithFocus<OverlayItem> moverlay = new ItemizedOverlayWithFocus<OverlayItem>(requireActivity().getApplicationContext(),
                                    items, new ItemizedIconOverlay.OnItemGestureListener<OverlayItem>() {
                                @Override
                                public boolean onItemSingleTapUp(int index, OverlayItem item) {

                                    //Delete previous Dialog View
                                    if (customDialogView.getParent() != null)
                                        ((ViewGroup) customDialogView.getParent()).removeView(customDialogView);

                                    for (int i = 0; i >= 0; i++)
                                    {
                                        if (i == assetsName.size())
                                            break;
                                        else
                                        {
                                            if (assetsName.get(i) == item.getTitle())
                                            {
                                                Log.d("API CALL", "Asset name of marker: " + assetsName.get(i));

                                                //Asset name
                                                assetName.setText(assetsName.get(i));

                                                //Asset id
                                                assetId.setText(assetsId.get(i));

                                                //Asset version
                                                assetVer.setText(assetsVer.get(i));

                                                //Asset created on
                                                Calendar calendar = Calendar.getInstance();
                                                calendar.setTimeZone(TimeZone.getTimeZone("UTC"));
                                                calendar.setTimeInMillis(Long.parseLong(assetsCreated.get(i)));

                                                int mYear = calendar.get(Calendar.YEAR);
                                                int mMonth = calendar.get(Calendar.MONTH) + 1;
                                                int mDay = calendar.get(Calendar.DAY_OF_MONTH);

                                                assetCreatedOn.setText(mDay + "-" + mMonth + "-" + mYear);

                                                //Set button for alert dialog
                                                int indexOfAsset = i;
                                                alertDialogBuilder.setNegativeButton("Close", null);
                                                alertDialogBuilder.setPositiveButton("Details", new DialogInterface.OnClickListener() {

                                                    @Override
                                                    public void onClick(DialogInterface dialog, int id) {
                                                        //Create a bundle to send asset id for AssetInform Fragment
                                                        Bundle bundle = new Bundle();
                                                        bundle.putString("asset_id", assetsId.get(indexOfAsset));
                                                        bundle.putString("asset_name", assetsName.get(indexOfAsset));
                                                        // set Fragmentclass Arguments
                                                        AssetInformFragment assetInformFragment = new AssetInformFragment();
                                                        assetInformFragment.setArguments(bundle);

                                                        //Go to Asset Infor Fragment
                                                        FragmentManager fragmentManager;
                                                        fragmentManager = requireActivity().getSupportFragmentManager();
                                                        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                                                        fragmentTransaction.replace(R.id.fragment_container, assetInformFragment);
                                                        fragmentTransaction.addToBackStack(null);
                                                        fragmentTransaction.commit();
                                                    }
                                                });

                                                AlertDialog alertDialog = alertDialogBuilder.create();

                                                //Set color for button of alert dialog
                                                alertDialog.setOnShowListener(new DialogInterface.OnShowListener() {
                                                            @SuppressLint("ResourceAsColor")
                                                            @Override
                                                            public void onShow(DialogInterface arg0) {
                                                                alertDialog.getButton(AlertDialog.BUTTON_POSITIVE).setTextColor(Color.parseColor("#16AB94"));
                                                                alertDialog.getButton(AlertDialog.BUTTON_NEGATIVE).setTextColor(Color.GRAY);
                                                            }
                                                        });

                                                alertDialog.show();
                                            }
                                        }
                                    }

                                    return true;
                                }

                                @Override
                                public boolean onItemLongPress(int index, OverlayItem item) {
                                    return false;
                                }

                            });
                            moverlay.setFocusItemsOnTap(true);
                            mapview.getOverlays().add(moverlay);
                        }
                    }
                    @Override
                    public void onFailure(Call<Asset> callAssets, Throwable t) {
                        Log.d("API CALL", t.getMessage());
                    }
                });
            }
        }

        // Inflate the layout for this fragment
        return inflater.inflate(R.layout.fragment_map, container, false);
    }
}