package com.atc.qn.metarnow;
import com.atc.qn.metarnow.AddDialog.nameInputListener;
import com.atc.qn.metarnow.InfoAsyncTask.OnTaskCompletedListner;

import android.content.SharedPreferences;
import android.os.Bundle;
import android.support.design.widget.Snackbar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.DefaultItemAnimator;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v4.widget.SwipeRefreshLayout;
import android.support.v4.widget.SwipeRefreshLayout.OnRefreshListener;
import android.support.v7.widget.helper.ItemTouchHelper;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements nameInputListener, OnTaskCompletedListner, OnItemTouchListener {
    private List<Info> mInfoList = new ArrayList<>();
    private ArrayList<String> mHistory = new ArrayList<>();
    private SwipeRefreshLayout mSwipe;
    private RecyclerView mRecyclerView;
    private InfoAdapter mAdapter = new InfoAdapter(mInfoList, this);
    private SharedPreferences mPrefs;
    private boolean setting_METAR = true;
    private boolean setting_TAF = false;
    private boolean setting_NOTAM = false;
    private boolean setting_Latest6Hr = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.activity_main);
        setSwipeLayout();
        setRecycleView();

        recoveryData(savedInstanceState);
    }

    @Override
    public void onResume() {
        super.onResume();  // Always call the superclass method first
        syncData();
    }

    @Override
    protected void onStop() {
        super.onStop();

        String InfoListJSONString = null;
        try {
            InfoListJSONString = JSONEncode();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor Pref = mPrefs.edit();
        Pref.putBoolean("ATIS_METAR", setting_METAR);
        Pref.putBoolean("ATIS_TAF", setting_TAF);
        Pref.putBoolean("ATIS_NOTAM", setting_NOTAM);
        Pref.putBoolean("ATIS_LATEST6HR", setting_Latest6Hr);
        Pref.putString("ATIS_INFOLIST", InfoListJSONString);
        Pref.apply();
        new LogD(InfoListJSONString);
    }

    @Override
    protected void onSaveInstanceState(Bundle outState) {
        new LogD(String.valueOf(mInfoList));
        new LogD(String.valueOf(outState));

    }

    private void recoveryData(Bundle savedInstanceState) {
        mPrefs = getSharedPreferences("ATISTW_PREFERENCES", MODE_PRIVATE);

        setting_METAR = mPrefs.getBoolean("ATIS_METAR", true);
        setting_TAF = mPrefs.getBoolean("ATIS_TAF", false);
        setting_NOTAM = mPrefs.getBoolean("ATIS_NOTAM", false);
        setting_Latest6Hr = mPrefs.getBoolean("ATIS_LATEST6HR", false);

        try {
            String InfoListJSONString = mPrefs.getString("ATIS_INFOLIST", "EMPTY!");
            JSONDecode(InfoListJSONString);
            new LogD(InfoListJSONString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String JSONEncode() throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (Info mInfo:mInfoList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mName", mInfo.getName());

            jsonArray.put(jsonObject);
        }

        return jsonArray.toString();
    }
    private void JSONDecode(String InfoListJSONString) throws JSONException {
        JSONArray jsonArray = new JSONArray(InfoListJSONString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("mName");

            Info mInfo = new Info(name, setting_METAR, setting_TAF, setting_NOTAM, setting_Latest6Hr);
            mInfoList.add(mInfo);
        }
    }

    private void setRecycleView() {
        mRecyclerView = (RecyclerView) findViewById(R.id.recycleview);
        mRecyclerView.setLayoutManager(new LinearLayoutManager(this));
        mRecyclerView.setItemAnimator(new DefaultItemAnimator());
        mRecyclerView.setHasFixedSize(true);
        mRecyclerView.setAdapter(mAdapter);

        ItemTouchHelper touchHelper = new ItemTouchHelper(new ItemCallback(mAdapter, mSwipe));
        touchHelper.attachToRecyclerView(mRecyclerView);
    }

    private void setSwipeLayout() {
        mSwipe = (SwipeRefreshLayout) findViewById(R.id.swipeContainer);
        // Setup refresh listener which triggers new data loading
        mSwipe.setOnRefreshListener(new OnRefreshListener() {
            @Override
            public void onRefresh() {
                syncData();
                mSwipe.setRefreshing(false);
            }
        });
        mSwipe.setColorSchemeResources(
            R.color.refresh_progress_1, R.color.refresh_progress_2, R.color.refresh_progress_3);
    }

    private void syncData() {
        for (Info mInfo: mInfoList) {
            new InfoAsyncTask(this).execute(mInfo, null, null);
            mRecyclerView.setAdapter(mAdapter);
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if(setting_METAR){
            MenuItem mItem = menu.findItem(R.id.action_allMETAR);
            mItem.setChecked(true);
        }
        if(setting_TAF){
            MenuItem mItem = menu.findItem(R.id.action_allTAF);
            mItem.setChecked(true);
        }
        if(setting_NOTAM){
            MenuItem mItem = menu.findItem(R.id.action_allNOTAM);
            mItem.setChecked(true);
        }
        if(setting_Latest6Hr){
            MenuItem mItem = menu.findItem(R.id.action_allLatest6Hr);
            mItem.setChecked(true);
        }
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if(item.isCheckable()) {
            item.setChecked(!item.isChecked());
        }

        if (id == R.id.action_settings) {
            return true;
        }else if (id == R.id.action_add) {
            AddDialog dialog = new AddDialog();
//            Bundle args = new Bundle();
//            args.putStringArray("HISTORY", mHistory.toArray(new String[mHistory.size()]));
//            dialog.setArguments(args);
            dialog.show(getFragmentManager(), "AddDialog");

            return true;
        }else if (id == R.id.action_allMETAR) {
            setting_METAR = !setting_METAR;

            for (Info mInfo: mInfoList) {
                mInfo.setShowMETAR(setting_METAR);
            }
        }else if (id == R.id.action_allTAF) {
            setting_TAF = !setting_TAF;

            for (Info mInfo: mInfoList) {
                mInfo.setShowTAF(setting_TAF);
            }
        }else if (id == R.id.action_allNOTAM) {
            setting_NOTAM = !setting_NOTAM;
            for (Info mInfo: mInfoList) {
                mInfo.setShowNOTAM(setting_NOTAM);
            }
        }else if (id == R.id.action_allLatest6Hr) {
            setting_Latest6Hr = !setting_Latest6Hr;
            for (Info mInfo: mInfoList) {
                mInfo.setShowLatest6Hr(setting_Latest6Hr);
            }
        }
        syncData();
        return super.onOptionsItemSelected(item);
    }

    @Override
    public void onAirportInputComplete(String airporName, String msg)
    {
        airporName = airporName.toUpperCase();
        //Toast.makeText(this, airporName + " " + msg, Toast.LENGTH_SHORT).show();
        Snackbar.make(mRecyclerView, airporName + " "+ msg, Snackbar.LENGTH_SHORT).show();
        addInfoList(airporName);
        syncData();
    }

    private void addInfoList(String name) {
        new LogD("Add " + name);

        mInfoList.add(new Info(name, setting_METAR, setting_TAF, setting_NOTAM, setting_Latest6Hr));
    }

    @Override
    public void onTaskCompleted() {
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
    }

    @Override
    public void onItemDismiss(int position) {
        final Info mInfo = mInfoList.get(position);
        if(mHistory.size() < 4) {
            mHistory.add(mInfo.getName());
        }else{
            mHistory.remove(0);
            mHistory.add(mInfo.getName());
        }
        new LogD(mHistory.toString());

        Snackbar.make(mRecyclerView, mInfo.getName() + " Deleted", Snackbar.LENGTH_SHORT)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAirportInputComplete(mInfo.getName(), "Recovered");
                    }
                })
                .show();
    }

    @Override
    public void onSync() {
        onTaskCompleted();
    }
}


