package com.atc.qn.metarnow;
import com.atc.qn.metarnow.LogD;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AddDialog extends DialogFragment {
    private ArrayList<String> airportArrayList = new ArrayList<>();
    private String[] airports = new String[]{};
    private String[] mHistory = new String[]{};

    public interface nameInputListener {
        void onAirportInputComplete(String airportName, String msg);
    }

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
//        mHistory = savedInstanceState.getStringArray("HISTORY");
//        if(mHistory != null)
//            Log.d("ATIS_HISTORY", mHistory.toString());

        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_dialog, null);
        // Inflate and set the layout for the dialog
        final AutoCompleteTextView mAirportName = (AutoCompleteTextView)
                view.findViewById(R.id.id_name_textview);
        mAirportName.setHint("ICAO/IATA code or Airport name");

        getAirports();
        //airports = new Airports().list;

        ArrayAdapter<String> autocompleteAdapter = new ArrayAdapter<>(
                getActivity(), R.layout.icao_item, R.id.item, airports);

        mAirportName.setAdapter(autocompleteAdapter);
        mAirportName.setThreshold(1);
        mAirportName.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                new LogD(String.valueOf(mAirportName.getText()));
                String[] item = mAirportName.getText().toString().split(" ");
                mAirportName.setText(item[0]);
            }
        });

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
            // Add action buttons
            .setPositiveButton("Add",
                new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        nameInputListener listener = (nameInputListener) getActivity();
                        listener.onAirportInputComplete(
                                mAirportName.getText().toString(), "Added");
                    }
                })
            .setNegativeButton("Cancel", null);
        return builder.create();
    }

    public void getAirports(){
        BufferedReader reader = null;
        try {
            reader = new BufferedReader(new InputStreamReader
                    (getActivity().getAssets().open("airports.list"), "UTF-8"));

            // do reading, usually loop until end of file reading
            String mLine = reader.readLine();
            while (mLine != null) {
                airportArrayList.add(mLine);
                //Log.i("ATIS_PORTS", mLine);
                mLine = reader.readLine();
            }
        } catch (IOException e) {
            //log the exception
        } finally {
            if (reader != null) {
                try {
                    reader.close();
                } catch (IOException e) {
                    //log the exception
                }
            }
        }
        airports = airportArrayList.toArray(new String[airportArrayList.size()]);
    }
}
