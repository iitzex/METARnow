package com.atc.qn.metarnow;

import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AlertDialog;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class HistoryDialog extends DialogFragment {
    private View view;
    private String history = "";

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState)
    {
        final AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        LayoutInflater inflater = getActivity().getLayoutInflater();
        view = inflater.inflate(R.layout.history, null);
        String ICAO = getArguments().getString("ICAO");
        LogD.out("ICAO " + ICAO);
        new HistoryAsyncTask().execute(ICAO, null, history);

        // Pass null as the parent view because its going in the dialog layout
        builder.setView(view)
                // Add action buttons
                .setPositiveButton("OK",
                        new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int id) {
                            }
                        });
        return builder.create();
    }

    private void done(){
        ProgressBar bar = (ProgressBar)view.findViewById(R.id.history_progressbar);
        bar.setVisibility(View.GONE);
        TextView content = (TextView)view.findViewById(R.id.history_content);
        content.setText(history);
    }

    public class HistoryAsyncTask extends AsyncTask<String, Integer, String> {
        String ICAO;

        @Override
        protected String doInBackground(String... params) {
            ICAO = params[0];
//            mInfo.setLoading(true);
            try {
                String addr = "http://aoaws.caa.gov.tw/cgi-bin/wmds/aoaws_metars?metar_ids=" +
                        ICAO + "&NHOURS=6&std_trans=standard";

                String text = downloadData(addr);
                history = parse(text, "UTC.*?" + ICAO + " (.*?=)");
                LogD.out(history);
            } catch (MalformedURLException e) {
                e.printStackTrace();
                return "";
            } catch (IOException e) {
                e.printStackTrace();
                return "";
            }
            return history;
        }

        private String downloadData(String HttpAddr) throws IOException {
            URL textUrl = new URL(HttpAddr);
            BufferedReader bufferReader
                    = new BufferedReader(new InputStreamReader(textUrl.openStream()));

            String StringBuffer;
            String stringText = "";
            while ((StringBuffer = bufferReader.readLine()) != null) {
                stringText += StringBuffer;
            }

            bufferReader.close();
            return stringText;
        }

        private String parse(String rawData, String expression){
            if(rawData == null){
                return "";
            }

            Matcher m =  Pattern.compile(expression).matcher(rawData);
            String text = "";
            while (m.find()) {
//                text += m.group(1) + "\n\n";
                text = text + m.group(1);
                text = text + "\n\n";
            }

            if (text.equals(""))
                return "FAILED!!";
            else
                return text;
        }

        @Override
        protected void onPostExecute(String result) {
            done();
        }
    }
}
