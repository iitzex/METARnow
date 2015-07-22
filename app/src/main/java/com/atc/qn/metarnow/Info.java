package com.atc.qn.metarnow;
import android.os.Parcel;
import android.os.Parcelable;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class Info implements Parcelable{
    private String mName;
    private String METAR;
    private String TAF;
    private String NOTAM;
    private String latest6HrMETAR;
    private boolean showMETAR = true;
    private boolean showTAF = false;
    private boolean showNOTAM = false;
    private boolean showLatest6Hr = false;
    private boolean expand = true;
    private boolean loading = false;

    public Info(String mName, boolean ShowMETAR,
                boolean showTAF, boolean showNOTAM, boolean showLatest6Hr){
        this.mName = mName;
        this.showMETAR = showMETAR;
        this.showTAF = showTAF;
        this.showNOTAM = showNOTAM;
        this.showLatest6Hr = showLatest6Hr;
    }

    public Info(String mName){
        this.mName = mName;
    }

    public void setShowMETAR(boolean showMETAR) {
        this.showMETAR = showMETAR;
    }

    public void setShowTAF(boolean showTAF) {
        this.showTAF = showTAF;
    }

    public void setShowNOTAM(boolean showNOTAM) {
        this.showNOTAM = showNOTAM;
    }

    public void setShowLatest6Hr(boolean showLatest6Hr) {
        this.showLatest6Hr = showLatest6Hr;
    }

    public void setExpand(boolean expand) {
        this.expand = expand;
    }

    public void setLoading(Boolean loading) {
        this.loading = loading;
    }

    public boolean isShowMETAR() {
        return showMETAR;
    }

    public boolean isShowTAF() {
        return showTAF;
    }

    public boolean isShowNOTAM() {
        return showNOTAM;
    }

    public boolean isShowLatest6Hr() {
        return showLatest6Hr;
    }

    public boolean isExpand() {
        return expand;
    }

    public void setMETAR(String METAR) {
        this.METAR = "METAR\n";
        this.METAR += parse(METAR, "<PRE>.*" + mName + " (.*)</PRE>");
    }

    public void setTAF(String TAF) {
        this.TAF = "TAF\n";
        this.TAF += parse(TAF, "<PRE>.*" + mName + " (.*)</PRE>")
                .replaceAll("TEMPO", "\nTEMPO")
                .replaceAll("BECMG", "\nBECMG");
    }

    public void setNOTAM(String NOTAM) {
        this.NOTAM = "NOTAM\n";
        this.NOTAM += parse(NOTAM, "notamRight.*?<PRE>(.*?)</PRE>");
    }

    public void setLatest6HrMETAR(String latest6HrMETAR) {
        this.latest6HrMETAR = "Latest 6Hr METAR\n";
        this.latest6HrMETAR += parse(latest6HrMETAR, "UTC.*?" + mName + " (.*?=)");
    }

    public String getName() {
        return mName;
    }

    public String getMETAR() {
        return METAR != null ? METAR.trim() : null;
    }

    public String getTAF() {
        return TAF != null ? TAF.trim() : null;
    }

    public String getNOTAM() {
        return NOTAM != null ? NOTAM.trim() : null;
    }

    public String getLatest6HrMETAR() {
        return  latest6HrMETAR != null ? latest6HrMETAR.trim() : null;
    }

    public Boolean isLoading() {
        return loading;
    }


    private String parse(String rawData, String expression){
        if(rawData == null){
            return "";
        }

        Matcher m =  Pattern.compile(expression).matcher(rawData);
        String result = "";
        int i = 0;
        while (m.find()) {
            new LogD(String.valueOf(i++) + "_" + m.group(1));
            result += m.group(1) + "\n\n";
        }
        if (result.equals(""))
            return "EMPTY!!";
        else
            return  result;
    }

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        dest.writeString(mName);
    }

    public static final Parcelable.Creator<Info> CREATOR
            = new Parcelable.Creator<Info>()
    {
        public Info createFromParcel(Parcel in) {
            return new Info(in);
        }

        public Info[] newArray(int size) {
            return new Info[size];
        }
    };
    private Info(Parcel in) {
        mName = in.readString();
    }
}

