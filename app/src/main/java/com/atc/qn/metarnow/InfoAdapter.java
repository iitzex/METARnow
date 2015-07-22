package com.atc.qn.metarnow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.Collections;
import java.util.List;

public class InfoAdapter extends RecyclerView.Adapter<InfoAdapter.ItemViewHolder>
        implements OnItemTouchListener
{
    private List<Info> mInfoList;
    private OnItemTouchListener mContext;

    public InfoAdapter(List<Info> mInfoList, Context mContext) {
        this.mInfoList = mInfoList;
        this.mContext = (OnItemTouchListener) mContext;
    }

    @Override
    public ItemViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.cardview, parent, false);
        return new ItemViewHolder(v, mInfoList, mContext);
    }

    @Override
    public void onBindViewHolder(ItemViewHolder holder, int position) {
        Info mInfo = mInfoList.get(position);

        holder.mName.setText(mInfo.getName());

        if(mInfo.isLoading())
            holder.mBar.setVisibility(View.VISIBLE);

        holder.mMETAR.setVisibility(View.GONE);
        holder.mTAF.setVisibility(View.GONE);
        holder.mLATEST6HR.setVisibility(View.GONE);
        holder.mNOTAM.setVisibility(View.GONE);

        if(mInfo.isExpand()) {
            holder.mImage.setImageResource(R.drawable.expand);
            if (mInfo.isShowMETAR()) {
                holder.mMETAR.setVisibility(View.VISIBLE);
                holder.mMETAR.setText(mInfo.getMETAR());
            }
            if (mInfo.isShowTAF()) {
                holder.mTAF.setVisibility(View.VISIBLE);
                holder.mTAF.setText(mInfo.getTAF());
            }
            if (mInfo.isShowLatest6Hr()) {
                holder.mLATEST6HR.setVisibility(View.VISIBLE);
                holder.mLATEST6HR.setText(mInfo.getLatest6HrMETAR());
            }
            if (mInfo.isShowNOTAM()) {
                holder.mNOTAM.setVisibility(View.VISIBLE);
                holder.mNOTAM.setText(mInfo.getNOTAM());
            }
        }else {
            holder.mImage.setImageResource(R.drawable.collapse);
        }
    }

    @Override
    public int getItemCount() {
        return mInfoList == null ? 0 : mInfoList.size();
    }

    @Override
    public void onItemDismiss(int position) {
        notifyItemRemoved(position);
        mContext.onItemDismiss(position);
        mInfoList.remove(position);
    }

    @Override
    public void onItemMove(int fromPosition, int toPosition) {
        notifyItemMoved(fromPosition, toPosition);
        Collections.swap(mInfoList, fromPosition, toPosition);
    }
    @Override
    public void onSync(){
        mContext.onSync();
    }

    public static class ItemViewHolder extends RecyclerView.ViewHolder
        implements View.OnClickListener
    {
        TextView mName, mMETAR, mTAF, mLATEST6HR, mNOTAM;
        ProgressBar mBar;
        ImageView mImage;
        List<Info> mInfoList;
        OnItemTouchListener mContext;

        public ItemViewHolder(View v, List<Info> mInfoList, OnItemTouchListener mContext) {
            super(v);
            mName = (TextView) v.findViewById(R.id.text_airport);
            mMETAR = (TextView) v.findViewById(R.id.text_metar);
            mTAF = (TextView) v.findViewById(R.id.text_taf);
            mLATEST6HR = (TextView) v.findViewById(R.id.text_latest6hr);
            mNOTAM = (TextView) v.findViewById(R.id.text_notam);

            mBar = (ProgressBar) v.findViewById(R.id.progressbar);
            mImage = (ImageView) v.findViewById(R.id.image_expand);

            this.mInfoList = mInfoList;
            this.mContext = mContext;

            mImage.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            new LogD(String.valueOf(this.getAdapterPosition()));
            Info mInfo = mInfoList.get(this.getAdapterPosition());

            mInfo.setExpand(!mInfo.isExpand());
            mContext.onSync();
        }
    }
}
