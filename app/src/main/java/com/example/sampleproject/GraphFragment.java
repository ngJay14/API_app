package com.example.sampleproject;

import android.annotation.SuppressLint;
import android.database.Cursor;
import android.graphics.Color;
import android.os.Bundle;

import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.fragment.app.FragmentTransaction;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.RelativeLayout;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.example.sampleproject.Model.DbAssets;
import com.google.android.material.navigation.NavigationView;
import com.jjoe64.graphview.DefaultLabelFormatter;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.LegendRenderer;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.DataPointInterface;
import com.jjoe64.graphview.series.LineGraphSeries;
import com.jjoe64.graphview.series.OnDataPointTapListener;
import com.jjoe64.graphview.series.Series;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Objects;

public class GraphFragment extends Fragment  {

    private DbAssets dbAssets;
    private Cursor cursor;
    private String asset_name;
    private String asset_data;

    private List<String> assetsHumidity;
    private List<String> assetsTemp;
    private List<String> assetWindspeed;
    private List<String> assetsTimestamp;

    private boolean spinnerAssetItemChanged = false;
    private boolean spinnerAssetAtrItemChanged = false;


//    private GraphView linegraph;

    LineGraphSeries<DataPoint> temperatureSeries = new LineGraphSeries<>(new DataPoint[0]);
    LineGraphSeries<DataPoint> humiditySeries = new LineGraphSeries<>(new DataPoint[0]);
    LineGraphSeries<DataPoint> windspeedSeries = new LineGraphSeries<>(new DataPoint[0]);

    @SuppressLint("SimpleDateFormat")
//    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a yyyy-MM-dd");
    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a");

    Date currentTime = Calendar.getInstance().getTime();

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

    }

    @SuppressLint("SetTextI18n")
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        // Inflate the layout for this fragment
        View view = inflater.inflate(R.layout.fragment_graph, container, false);

        //Set status checked for nav item
        NavigationView navigationView = getActivity().findViewById(R.id.nav_view);
        navigationView.setCheckedItem(R.id.nav_graph);

        if(getArguments() != null)
        {
            asset_name = getArguments().getString("asset_name");
            asset_data = getArguments().getString("asset_data");
        }
        else
        {
            asset_name = "Weather Asset";
            asset_data = "Temperature";
        }

        //Set up spinner asset name
        Spinner spinnerAssets = (Spinner) view.findViewById(R.id.spinner_asset_name);
        ArrayAdapter<CharSequence> adapter = ArrayAdapter.createFromResource(getActivity(), R.array.assets_name, android.R.layout.simple_spinner_item);
        adapter.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssets.setAdapter(adapter);
        spinnerAssets.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);

                String selectedItem = parent.getItemAtPosition(position).toString();

                if (!spinnerAssetItemChanged && !selectedItem.equals(asset_name))
                {
                    //Create a bundle to send asset id for Graph Fragment
                    Bundle bundle = new Bundle();

                    bundle.putString("asset_name", selectedItem);
                    bundle.putString("asset_data", asset_data);

                    // set Fragmentclass Arguments
                    GraphFragment graphFragment = new GraphFragment();
                    graphFragment.setArguments(bundle);

                    //Go to Asset Infor Fragment again
                    FragmentManager fragmentManager;
                    fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, graphFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                else
                    spinnerAssetItemChanged = false;

            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Set spinner selection
        spinnerAssets.setSelection(adapter.getPosition(asset_name));
        spinnerAssetItemChanged = true;

        //Set up spinner asset attribute
        Spinner spinnerAssetsAtr = (Spinner) view.findViewById(R.id.spinner_asset_atribute);
        ArrayAdapter<CharSequence> adapter1 = ArrayAdapter.createFromResource(getActivity(), R.array.assets_atribute, android.R.layout.simple_spinner_item);
        adapter1.setDropDownViewResource(android.R.layout.simple_spinner_dropdown_item);
        spinnerAssetsAtr.setAdapter(adapter1);
        spinnerAssetsAtr.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {

                ((TextView) parent.getChildAt(0)).setTextColor(Color.GRAY);

                String selectedItem = parent.getItemAtPosition(position).toString();
                if (!spinnerAssetItemChanged && !selectedItem.equals(asset_data))
                {
                    //Create a bundle to send asset id for Graph Fragment
                    Bundle bundle = new Bundle();

                    bundle.putString("asset_name", asset_name);
                    bundle.putString("asset_data", selectedItem);

                    // set Fragmentclass Arguments
                    GraphFragment graphFragment = new GraphFragment();
                    graphFragment.setArguments(bundle);

                    //Go to Asset Infor Fragment again
                    FragmentManager fragmentManager;
                    fragmentManager = requireActivity().getSupportFragmentManager();
                    FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
                    fragmentTransaction.replace(R.id.fragment_container, graphFragment);
                    fragmentTransaction.addToBackStack(null);
                    fragmentTransaction.commit();
                }
                else
                    spinnerAssetAtrItemChanged = false;
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });
        //Set spinner selection
        spinnerAssetsAtr.setSelection(adapter1.getPosition(asset_data));
        spinnerAssetAtrItemChanged = true;

        dbAssets = new DbAssets(getActivity());
        dbAssets.open();

        //Get data form database
        getAsset(asset_name);

        //Set current time TextView
        SimpleDateFormat sdf1 = new SimpleDateFormat("hh:mm a yyyy-MM-dd");
        TextView currentTime_view = (TextView) view.findViewById(R.id.current_time);
        currentTime_view.setText("Current time: " + sdf1.format(currentTime));

        //Create and set up a graphview
        GraphView linegraph = (GraphView) view.findViewById(R.id.graph);

        linegraph.getGridLabelRenderer().setLabelFormatter(new DefaultLabelFormatter(){
            @Override
            public String formatLabel(double value, boolean isValueX) {
                if(isValueX)
                    return sdf.format(new Date((long) value));
                else
                    return super.formatLabel(value, isValueX);
            }
        });

        linegraph.getViewport().setDrawBorder(true);
        linegraph.setTitle(asset_name);
        linegraph.setTitleColor(Color.RED);
        linegraph.setTitleTextSize(45f);

        linegraph.setContentDescription("sjhgdfshjdbbfksd");
        linegraph.getLegendRenderer().setVisible(true);
        linegraph.getLegendRenderer().setAlign(LegendRenderer.LegendAlign.TOP);

        linegraph.getGridLabelRenderer().setTextSize(20f);
        linegraph.getGridLabelRenderer().setPadding(25);
        linegraph.getGridLabelRenderer().setHighlightZeroLines(false);

        Date date = new Date();

        linegraph.getViewport().setMinX(1.0);
        linegraph.getViewport().setMaxX(date.getTime());
        linegraph.getViewport().setXAxisBoundsManual(true);

        linegraph.getGridLabelRenderer().setHumanRounding(false);

        linegraph.onDataChanged(false, false);
        linegraph.getGridLabelRenderer().setNumVerticalLabels(3);

        linegraph.getViewport().setScalable(true);
        linegraph.getViewport().setScalableY(true);
        linegraph.getViewport().setScrollable(true);
        linegraph.getViewport().setScrollableY(true);
        linegraph.getViewport().scrollToEnd();

        if (Objects.equals(asset_data, "Temperature"))
        {
            linegraph.getViewport().setYAxisBoundsManual(true);
            linegraph.getViewport().setMinY(0);
            linegraph.getViewport().setMaxY(45);

            //Set color for line date
            temperatureSeries.setColor(Color.RED);
            temperatureSeries.setDrawDataPoints(true);
            temperatureSeries.setTitle("Temperature");

            //Set data for line data
            temperatureSeries.resetData(graphTemperature());

            //Set on tap listener for datapoints
            temperatureSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd-MM-yyyy");

                    String msg = "Temperature: " + dataPoint.getY() + " ℃" + "\nUpdated on: " + sdf.format(new Date((long) dataPoint.getX()));
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                }
            });

            //Add line data into graph view
            linegraph.addSeries(temperatureSeries);
        }
        else if (Objects.equals(asset_data, "Humidity"))
        {
            linegraph.getViewport().setYAxisBoundsManual(true);
            linegraph.getViewport().setMinY(0);
            linegraph.getViewport().setMaxY(100);

            //Set color for line date
            humiditySeries.setColor(Color.BLUE);
            humiditySeries.setDrawDataPoints(true);
            humiditySeries.setTitle("Humidity");

            //Set data for line data
            humiditySeries.resetData(graphHumidity());

            //Set on tap listener for datapoints
            humiditySeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd-MM-yyyy");

                    String msg = "Humidity: " + dataPoint.getY() + " %" + "\nUpdated on: " + sdf.format(new Date((long) dataPoint.getX()));
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                }
            });

            //Add line data into graph view
            linegraph.addSeries(humiditySeries);
        }
        else
        {
            linegraph.getViewport().setYAxisBoundsManual(true);
            linegraph.getViewport().setMinY(0);
            linegraph.getViewport().setMaxY(10);

            //Set color for line date
            windspeedSeries.setColor(Color.GREEN);
            windspeedSeries.setDrawDataPoints(true);
            windspeedSeries.setTitle("Windspeed");

            //Set data for line data
            Log.d("DATABASE", "Windspeed: " + Arrays.toString(graphWindSpeed()));

            //Set data for line data
            windspeedSeries.resetData(graphWindSpeed());

            //Set on tap listener for datapoints
            windspeedSeries.setOnDataPointTapListener(new OnDataPointTapListener() {
                @Override
                public void onTap(Series series, DataPointInterface dataPoint) {

                    SimpleDateFormat sdf = new SimpleDateFormat("hh:mm a dd-MM-yyyy");

                    String msg = "Wind speed: " + dataPoint.getY() + " km/h" + "\nUpdated on: " + sdf.format(new Date((long) dataPoint.getX()));
                    Toast.makeText(getActivity(), msg, Toast.LENGTH_LONG).show();
                }
            });

            //Add line data into graph view
            linegraph.addSeries(windspeedSeries);
        }

        return view;
    }

    public DataPoint[] graphTemperature()
    {
        DataPoint[] dataPoints;
        Integer date = currentTime.getDate();
        Integer month = currentTime.getMonth() + 1;
        ArrayList <Integer> index = new ArrayList<>();

        for (int i = 0; i < assetsTemp.size(); i++)
        {
            Date timestampt = new Date(Long.parseLong(assetsTimestamp.get(i)));
            Integer dataDate = timestampt.getDate();
            Integer dataMonth = timestampt.getMonth() + 1;

            if (dataDate.equals(date) && dataMonth.equals(month))
            {
                index.add(i);
            }
        }

        dataPoints = new DataPoint[index.size()];

        Log.d("TEST", "Index: " + index);


        int k = 0;

        for (int i = index.get(0); i <= index.get(index.size() - 1); i++)
        {
            dataPoints[k] = new DataPoint(Long.parseLong(assetsTimestamp.get(i)), Double.parseDouble(assetsTemp.get(i)));
            k++;
        }

        return dataPoints;
    }

    public DataPoint[] graphHumidity()
    {
        DataPoint[] dataPoints;
        Integer date = currentTime.getDate();
        Integer month = currentTime.getMonth() + 1;
        ArrayList <Integer> index = new ArrayList<>();

        for (int i = 0; i < assetsHumidity.size(); i++)
        {
            Date timestampt = new Date(Long.parseLong(assetsTimestamp.get(i)));
            Integer dataDate = timestampt.getDate();
            Integer dataMonth = timestampt.getMonth() + 1;

            if (dataDate.equals(date) && dataMonth.equals(month))
            {
                index.add(i);
            }
        }

        dataPoints = new DataPoint[index.size()];

        Log.d("TEST", "Index: " + index);


        int k = 0;

        for (int i = index.get(0); i <= index.get(index.size() - 1); i++)
        {
            dataPoints[k] = new DataPoint(Long.parseLong(assetsTimestamp.get(i)), Double.parseDouble(assetsHumidity.get(i)));
            k++;
        }

        return dataPoints;
    }

    public DataPoint[] graphWindSpeed()
    {
        DataPoint[] dataPoints;
        Integer date = currentTime.getDate();
        Integer month = currentTime.getMonth() + 1;
        ArrayList <Integer> index = new ArrayList<>();

        for (int i = 0; i < assetWindspeed.size(); i++)
        {
            Date timestampt = new Date(Long.parseLong(assetsTimestamp.get(i)));
            Integer dataDate = timestampt.getDate();
            Integer dataMonth = timestampt.getMonth() + 1;

            if (dataDate.equals(date) && dataMonth.equals(month))
            {
                index.add(i);
            }
        }

        dataPoints = new DataPoint[index.size()];

        Log.d("TEST", "Index: " + index);


        int k = 0;

        for (int i = index.get(0); i <= index.get(index.size() - 1); i++)
        {
            dataPoints[k] = new DataPoint(Long.parseLong(assetsTimestamp.get(i)), Double.parseDouble(assetWindspeed.get(i)));
            k++;
        }

        return dataPoints;
    }

    @SuppressLint("Range")
    private void getAsset(String name) {

        assetsTemp = new ArrayList<>();
        assetsHumidity = new ArrayList<>();
        assetWindspeed = new ArrayList<>();
        assetsTimestamp = new ArrayList<>();
        cursor = dbAssets.getAsset(name);

        while (cursor.moveToNext()) {
            assetsHumidity.add(cursor.getString(cursor.getColumnIndex(DbAssets.KEY_HUMIDITY)));
            assetsTemp.add(cursor.getString(cursor.getColumnIndex(DbAssets.KEY_TEMPERATURE)));
            assetWindspeed.add(cursor.getString(cursor.getColumnIndex(DbAssets.KEY_WINDSPEED)));
            assetsTimestamp.add(cursor.getString(cursor.getColumnIndex(DbAssets.KEY_TIMESTAMP)));
        }
    }

}