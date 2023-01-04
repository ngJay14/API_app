package com.example.sampleproject;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.TextView;

import java.util.ArrayList;

public class AttributesList extends ArrayAdapter {
    private ArrayList<String> attributesName;
    private ArrayList<String> parameters;
    private Activity context;
    private ArrayList<String> statusUpdate;

    public AttributesList(Activity context, ArrayList<String> attributesName, ArrayList<String> parameters, ArrayList<String> statusUpdate) {
        super(context, R.layout.attributes_item, attributesName);
        this.context = context;
        this.attributesName = attributesName;
        this.parameters = parameters;
        this.statusUpdate = statusUpdate;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row=convertView;
        LayoutInflater inflater = context.getLayoutInflater();
        if(convertView==null)
            row = inflater.inflate(R.layout.attributes_item, null, true);
        TextView textViewAttributesName = (TextView) row.findViewById(R.id.textViewAttributesName);
        TextView textViewParameters = (TextView) row.findViewById(R.id.textViewParameters);
        TextView textViewStatusUpdate = (TextView) row.findViewById(R.id.textViewStatusUpdate);


        textViewAttributesName.setText(attributesName.get(position));
        textViewParameters.setText(parameters.get(position));
        textViewStatusUpdate.setText(statusUpdate.get(position));

        return row;
    }
}
