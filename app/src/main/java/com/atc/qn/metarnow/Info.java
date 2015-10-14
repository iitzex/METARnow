package com.atc.qn.metarnow;
import android.os.Parcel;
import android.os.Parcelable;
import android.util.Log;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

class Config{
    boolean showAll = false;
    boolean METAR = true;
    boolean Decoded = false;
    boolean TAF = false;
    boolean NOTAM = true;
    boolean Last6Hr = true;

    public Config (boolean METAR, boolean Decoded, boolean TAF, boolean NOTAM, boolean Last6Hr) {
        this.METAR = METAR;
        this.Decoded = Decoded;
        this.TAF = TAF;
        this.NOTAM = NOTAM;
        this.Last6Hr = Last6Hr;
    }
}

public class Info implements Parcelable{
    private String ICAO;
    private String mName;
    private String mTime;
    private String mWind;
    private String mVis;
    private String mCeil;
    private String mTemp;
    private String mDew;
    private String mQNH;
    private String mWX;
    private String METAR;
    private String TAF;
    private String NOTAM;
    private String last6HrMETAR;
    private Config setting;
    private boolean expand = true;
    private boolean loading = false;

    public Info(String mICAO, String mName, Config setting){
        this.ICAO = mICAO;
        this.mName = mName.trim();
        this.setting = setting;
    }

    public void setmName(String mName) {
        this.mName = mName;
    }
    public Config getSetting() {
        return setting;
    }
    public void setSetting(Config setting) {
        this.setting = setting;
    }
    public void setExpand(boolean expand) {
        this.expand = expand;
    }
    public void setLoading(Boolean loading) {
        this.loading = loading;
    }
    public boolean isExpand() {
        return expand;
    }

    public void setDecoded(String decoded) {
        String info = parse(decoded, "<PRE>.*(" + ICAO + " .*)</PRE>");
        if (info.equals("EMPTY!!"))
            return;

        String name = info.substring(0, 4).trim();
        String time = info.substring(5, 9).trim();
        String degree = info.substring(10, 13).trim();
        String scale = info.substring(14, 17).trim();
        String gst = info.substring(18, 21).trim();
        String vis = info.substring(22, 26).trim();
        String weather = info.substring(31, 38).trim();
        String ceil = info.substring(39, 43).trim();
        String tmp = info.substring(44, 47).trim();
        String dew = info.substring(48, 51).trim();
        String QNH = info.substring(52, 56).trim();

        this.mTime = time + " UTC";
        this.mWind = degree + "° " + scale + " kt";
        this.mWind += gst.equals("") ? "" : ", gusting " + gst + "kt";
        this.mVis = vis.equals("") ? "" : translateVis(vis);
        this.mWX = weather.equals("") ? "" : translateWX(weather);
        this.mCeil = ceil.equals("") ? "" : ceil.replaceFirst("^0+(?!$)", "") + "00 ft";
        this.mTemp = tmp + " °C";
        this.mDew = dew + " °C";
        this.mQNH = QNH + " hPa";
    }
    private String translateVis(String vis){
        if (vis.equals("10k+")){
            return "10KM or more";
        }else
            return vis + " m";
    }
    private String translateWX(String wx){
                            //INTENSITY OR PROXIMITY 1
        String modifiedWX = wx.replace("-", "Light ")
                            .replace("+", "Heavy ")
                            .replace("VC", "In the vicinity, ")
                            //DESCRIPTOR 2
                            .replace("MI", "Shallow ")
                            .replace("PR", "Partial ")
                            .replace("BC", "Patches ")
                            .replace("DR", "Low Drifting ")
                            .replace("BL", "Blowing ")
                            .replace("SH", "Shower ")
                            .replace("TS", "Thunderstorm ")
                            .replace("FZ", "Freezing ")
                            //PRECIPITATION 3
                            .replace("DZ", "Drizzle, ")
                            .replace("RA", "Rain, ")
                            .replace("SN", "Snow, ")
                            .replace("SG", "Snow Grains, ")
                            .replace("IC", "Ice Crystals, ")
                            .replace("PL", "Ice Pellets, ")
                            .replace("GR", "Hail, ")
                            .replace("GS", "Small Hail and/or Snow Pellets, ")
                            .replace("UP", "Unknown Precipitation, ")
                            //OBSCURATION 4
                            .replace("BR", "Mist, ")
                            .replace("FG", "Fog, ")
                            .replace("FU", "Smoke, ")
                            .replace("VA", "Volcanic Ash, ")
                            .replace("DU", "Widespread Dust, ")
                            .replace("SA", "Sand, ")
                            .replace("HZ", "Haze, ")
                            .replace("PY", "Spray, ")
                            //OTHER 5
                            .replace("PO", "Well-Developed Dust/Sand Whirls, ")
                            .replace("SQ", "Squalls, ")
                            .replace("FC", "Funnel Cloud Tornado Waterspout, ")
                            .replace("SS", "Sandstorm, ")
                            .replace("DS", "Duststorm, ");
        return modifiedWX;
    }

    public void setMETAR(String METAR) {
        this.METAR = "METAR\n";
        this.METAR += parse(METAR, "<PRE>.*" + ICAO + " (.*)</PRE>");
    }

    public void setTAF(String TAF) {
        this.TAF = "TAF\n";
        this.TAF += parse(TAF, "<PRE>.*" + ICAO + " (.*)</PRE>")
                    .replaceAll("TEMPO", "\nTEMPO")
                    .replaceAll("BECMG", "\nBECMG");
    }

    public void setNOTAM(String NOTAM) {
        this.NOTAM = "NOTAM\n";
        this.NOTAM += parse(NOTAM, "notamRight.*?<PRE>(.*?)</PRE>");
    }

    public void setLast6HrMETAR(String last6HrMETAR) {
        this.last6HrMETAR = "Last 6Hr METAR\n";
        this.last6HrMETAR += parse(last6HrMETAR, "UTC.*?" + ICAO + " (.*?=)");
    }

    public String getICAO() {
        return ICAO;
    }
    public String getmName() {
        return mName;
    }
    public String getmTime() {
        return mTime;
    }
    public String getmWind() {
        return mWind;
    }
    public String getmVis() {
        return mVis;
    }
    public String getmCeil() {
        return mCeil;
    }
    public String getmTemp() {
        return mTemp;
    }
    public String getmDew() {
        return mDew;
    }
    public String getmQNH() {
        return mQNH;
    }
    public String getmWX() {
        return mWX;
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
    public String getLast6HrMETAR() {
        return  last6HrMETAR != null ? last6HrMETAR.trim() : null;
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
            //LogD.out(String.valueOf(i++) + "_" + m.group(1));
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
        dest.writeString(ICAO);
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
        ICAO = in.readString();
    }
}

