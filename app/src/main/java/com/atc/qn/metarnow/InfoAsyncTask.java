package com.atc.qn.metarnow;

import android.os.AsyncTask;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.MalformedURLException;
import java.net.URL;

public class InfoAsyncTask extends AsyncTask<Info, Integer, Integer> {
    Info mInfo;
    public interface OnTaskCompletedListner{
        void onTaskCompleted();
    }
    private OnTaskCompletedListner listener = null;

    public InfoAsyncTask(OnTaskCompletedListner listener){
        this.listener = listener;
    }

    @Override
    protected Integer doInBackground(Info... params) {
        mInfo = params[0];
        mInfo.setLoading(true);
//        listener.onTaskCompleted();
        try {
            if(mInfo.isShowMETAR()) {
                String addr = "http://aoaws.caa.gov.tw/cgi-bin/wmds/aoaws_metars?metar_ids=" +
                        mInfo.getName() + "&NHOURS=Lastest&std_trans=standard";

                mInfo.setMETAR(downloadData(addr));
            }
            if(mInfo.isShowTAF()) {
                String addr = "http://aoaws.caa.gov.tw/cgi-bin/wmds/aoaws_tafs?taf_ids=" +
                        mInfo.getName() + "&NHOURS=Lastest&std_trans=standard";

                mInfo.setTAF(downloadData(addr));
            }
            if(mInfo.isShowNOTAM()) {
                String addr = "https://pilotweb.nas.faa.gov/PilotWeb/" +
                    "notamRetrievalByICAOAction.do?method=displayByICAOs&formatType=ICAO&" +
                    "retrieveLocId=" + mInfo.getName() +
                    "&reportType=RAW&actionType=notamRetrievalByICAOs";
                mInfo.setNOTAM(downloadData(addr));
            }
            if(mInfo.isShowLatest6Hr()) {
                String addr = "http://aoaws.caa.gov.tw/cgi-bin/wmds/aoaws_metars?metar_ids=" +
                        mInfo.getName() + "&NHOURS=6&std_trans=standard";

                mInfo.setLatest6HrMETAR(downloadData(addr));
            }

        } catch (MalformedURLException e) {
            e.printStackTrace();
            return 0;
        } catch (IOException e) {
            e.printStackTrace();
            return 0;
        }
        return 1;
    }

    private String downloadData (String HttpAddr) throws IOException {
        URL textUrl = new URL(HttpAddr);
        BufferedReader bufferReader
                = new BufferedReader(new InputStreamReader(textUrl.openStream()));

        String StringBuffer;
        String stringText = "";
        while ((StringBuffer = bufferReader.readLine()) != null) {
            stringText += StringBuffer;
            //Log.d("ATIS_NOTAM", StringBuffer);
        }

        bufferReader.close();
        return stringText;
    }

    @Override
    protected void onPostExecute(Integer result) {
        if(result == 1) {
            listener.onTaskCompleted();
            mInfo.setLoading(false);
        }
    }
}

