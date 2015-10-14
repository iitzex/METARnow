package com.atc.qn.metarnow;

public interface OnItemTouchListener {
    void onItemMove(int fromPosition, int toPosition);
    void onItemDismiss(int position);
    void onItemClick(int position);
    void onSync();
    void onPopHistory(String ICAO);
    void onPopNOTAM(String ICAO);
}