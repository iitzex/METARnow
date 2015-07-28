package com.atc.qn.metarnow;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AlertDialog;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Toast;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.util.ArrayList;

public class AddDialog extends DialogFragment {
    public interface nameInputListener {
        void onAirportInputComplete(String ICAO, String name, String msg);
    }
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        String[] airports = new String[]{};
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        View view = inflater.inflate(R.layout.add_dialog, null);
        // Inflate and set the layout for the dialog
        final AutoCompleteTextView mAirportName = (AutoCompleteTextView)
                view.findViewById(R.id.id_name_textview);
        mAirportName.setHint("ICAO/IATA code or Airport Name");
        airports = createAirportLists();
        ArrayAdapter<String> autocompleteAdapter = new ArrayAdapter<>(
                getActivity(), R.layout.icao_item, R.id.item, airports);

        mAirportName.setAdapter(autocompleteAdapter);
        mAirportName.setThreshold(1);

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
            // Add action buttons
            .setPositiveButton("Add",
                    new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialog, int id) {
                            nameInputListener listener = (nameInputListener) getActivity();
                            String[] item = mAirportName.getText().toString().split(",");
                            if (item.length > 2)
                                listener.onAirportInputComplete(item[0], item[2], "Added");
                            else {
                                Toast.makeText(getActivity(), "Error!! Please select from list", Toast.LENGTH_SHORT).show();
                            }
                        }
                    })
            .setNegativeButton("Cancel", null);
        return builder.create();
    }

    private String[] createAirportLists(){
        ArrayList<String> airportArrayList = new ArrayList<>();

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

        return airportArrayList.toArray(new String[airportArrayList.size()]);
    }
}
