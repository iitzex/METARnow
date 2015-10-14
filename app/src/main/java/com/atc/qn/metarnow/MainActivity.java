package com.atc.qn.metarnow;
import com.atc.qn.metarnow.AddDialog.nameInputListener;
import com.atc.qn.metarnow.InfoAsyncTask.OnTaskCompletedListener;

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
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.PopupWindow;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

public class MainActivity extends AppCompatActivity
        implements nameInputListener, OnTaskCompletedListener, OnItemTouchListener {
    private List<Info> mInfoList = new ArrayList<>();
    private SwipeRefreshLayout mSwipe;
    private RecyclerView mRecyclerView;
    private InfoAdapter mAdapter = new InfoAdapter(mInfoList, this);
    private SharedPreferences mPrefs;
    private Config setting = new Config(true, true, false, false, false);

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
        savePref();
    }

    private void savePref() {
        String InfoListJSONString = null;
        try {
            InfoListJSONString = JSONEncode();
        } catch (JSONException e) {
            e.printStackTrace();
        }

        SharedPreferences.Editor Pref = mPrefs.edit();
        Pref.putBoolean("SETTING_DECODED", setting.Decoded);
        Pref.putBoolean("SETTING_METAR", setting.METAR);
        Pref.putBoolean("SETTING_TAF", setting.TAF);
        Pref.putBoolean("SETTING_NOTAM", setting.NOTAM);
        Pref.putBoolean("SETTING_LAST6HR", setting.Last6Hr);
        Pref.putString("INFOLIST", InfoListJSONString);
        Pref.apply();
    }

    private void recoveryData(Bundle savedInstanceState) {
        mPrefs = getSharedPreferences("PREFERENCES", MODE_PRIVATE);

        setting.Decoded = mPrefs.getBoolean("SETTING_DECODED", true);
        setting.METAR = mPrefs.getBoolean("SETTING_METAR", true);
        setting.TAF = mPrefs.getBoolean("SETTING_TAF", false);
        setting.NOTAM = mPrefs.getBoolean("SETTING_NOTAM", false);
        setting.Last6Hr = mPrefs.getBoolean("SETTING_LAST6HR", false);

        try {
            String InfoListJSONString = mPrefs.getString("INFOLIST", "EMPTY!");
            JSONDecode(InfoListJSONString);
            LogD.out(InfoListJSONString);
        } catch (JSONException e) {
            e.printStackTrace();
        }
    }

    private String JSONEncode() throws JSONException {
        JSONArray jsonArray = new JSONArray();

        for (Info mInfo:mInfoList) {
            JSONObject jsonObject = new JSONObject();
            jsonObject.put("mName", mInfo.getICAO());
            jsonObject.put("mFullName", mInfo.getmName());

            jsonArray.put(jsonObject);
        }

        return jsonArray.toString();
    }

    private void JSONDecode(String InfoListJSONString) throws JSONException {
        JSONArray jsonArray = new JSONArray(InfoListJSONString);

        for (int i = 0; i < jsonArray.length(); i++) {
            JSONObject jsonObject = jsonArray.getJSONObject(i);
            String name = jsonObject.getString("mName");
            String fullname = jsonObject.getString("mFullName");

            Info mInfo = new Info(name, fullname, setting);
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
    public void onPopHistory(String ICAO) {
        HistoryDialog dialog = new HistoryDialog();

        Bundle args = new Bundle();
        args.putString("ICAO", ICAO);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "HistoryDialog");
    }

    @Override
    public void onPopNOTAM(String ICAO) {
        NotamDialog dialog = new NotamDialog();

        Bundle args = new Bundle();
        args.putString("ICAO", ICAO);
        dialog.setArguments(args);
        dialog.show(getFragmentManager(), "NotamDialog");
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_main, menu);

        if(setting.Decoded){
            MenuItem mItem = menu.findItem(R.id.action_allDecoded);
            mItem.setChecked(true);
        }
        if(setting.METAR){
            MenuItem mItem = menu.findItem(R.id.action_allMETAR);
            mItem.setChecked(true);
        }
        if(setting.TAF){
            MenuItem mItem = menu.findItem(R.id.action_allTAF);
            mItem.setChecked(true);
        }
        if(setting.NOTAM){
            MenuItem mItem = menu.findItem(R.id.action_allNOTAM);
            mItem.setChecked(true);
        }
        if(setting.Last6Hr){
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

        if (id == R.id.action_add) {
            AddDialog dialog = new AddDialog();
            dialog.show(getFragmentManager(), "AddDialog");
            return true;
        }else if (id == R.id.action_allDecoded) {
            setting.Decoded = !setting.Decoded;
        }else if (id == R.id.action_allMETAR) {
            setting.METAR = !setting.METAR;
        }else if (id == R.id.action_allTAF) {
            setting.TAF = !setting.TAF;
        }else if (id == R.id.action_allNOTAM) {
            setting.NOTAM = !setting.NOTAM;
        }else if (id == R.id.action_allLatest6Hr) {
            setting.Last6Hr = !setting.Last6Hr;
        }else if (id == R.id.action_sort) {
            sortInfoList();
        }

        for (Info mInfo: mInfoList) {
            mInfo.setSetting(setting);
        }

        syncData();
        savePref();
        return super.onOptionsItemSelected(item);
    }

    private void sortInfoList() {
        Collections.sort(mInfoList, new Comparator() {
            @Override
            public int compare(Object lhs, Object rhs) {
                int result = ((Info) lhs).getICAO().compareTo(((Info) rhs).getICAO());
                return result;
            }
        });
    }

    @Override
    public void onAirportInputComplete(String ICAO, String name, String msg)
    {
        //String[] item = airportName.split(",");
        Snackbar.make(mRecyclerView, ICAO + " "+ msg, Snackbar.LENGTH_SHORT).show();
        addInfoList(ICAO, name);
        syncData();
    }

    private void addInfoList(String ICAO, String name) {
        mInfoList.add(new Info(ICAO, name, setting));
    }

    @Override
    public void onTaskCompleted() {
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
    }

    @Override
    public void onItemClick(int position) {
        LogD.out(position);
        Info mInfo = mInfoList.get(position);
        new InfoAsyncTask(this).execute(mInfo, null, null);
        mRecyclerView.setAdapter(mAdapter);
    }

    @Override
    public void onItemDismiss(int position) {
        final Info mInfo = mInfoList.get(position);

        Snackbar.make(mRecyclerView, mInfo.getICAO() + " Deleted", Snackbar.LENGTH_SHORT)
                .setAction("UNDO", new View.OnClickListener() {
                    @Override
                    public void onClick(View v) {
                        onAirportInputComplete(mInfo.getICAO(), mInfo.getmName(), "Recovered");
                    }
                })
                .show();
    }

    @Override
    public void onSync() {
        onTaskCompleted();
    }

}


