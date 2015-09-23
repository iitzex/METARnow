package com.atc.qn.metarnow;

import android.content.Context;
import android.support.v7.widget.RecyclerView;
import android.text.Layout;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
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

        holder.mICAO.setText(mInfo.getICAO());
        holder.mName.setText(mInfo.getmName());
        holder.mTime.setText(mInfo.getmTime());
        holder.mWind.setText(mInfo.getmWind());
        holder.mVis.setText(mInfo.getmVis());
        holder.mCeil.setText(mInfo.getmCeil());
        holder.mTemp.setText(mInfo.getmTemp());
        holder.mDew.setText(mInfo.getmDew());
        holder.mQNH.setText(mInfo.getmQNH());
        holder.mWX.setText(mInfo.getmWX());

        if(mInfo.isLoading())
            holder.mBar.setVisibility(View.VISIBLE);

        if(mInfo.isExpand()) {
            holder.mImage.setImageResource(R.drawable.expand);
            if (mInfo.getSetting().Decoded | mInfo.getSetting().showAll) {
                holder.mDecoder.setVisibility(View.VISIBLE);
            }else
                holder.mDecoder.setVisibility(View.GONE);

            if (mInfo.getSetting().METAR | mInfo.getSetting().showAll) {
                holder.mMETAR.setVisibility(View.VISIBLE);
                holder.mMETAR.setText(mInfo.getMETAR());
            }else
                holder.mMETAR.setVisibility(View.GONE);

            if (mInfo.getSetting().TAF | mInfo.getSetting().showAll) {
                holder.mTAF.setVisibility(View.VISIBLE);
                holder.mTAF.setText(mInfo.getTAF());
            }else
                holder.mTAF.setVisibility(View.GONE);

            if (mInfo.getSetting().Last6Hr | mInfo.getSetting().showAll) {
                holder.mLAST6HR.setVisibility(View.VISIBLE);
                holder.mLAST6HR.setText(mInfo.getLast6HrMETAR());
            }else
                holder.mLAST6HR.setVisibility(View.GONE);

            if (mInfo.getSetting().NOTAM | mInfo.getSetting().showAll) {
                holder.mNOTAM.setVisibility(View.VISIBLE);
                holder.mNOTAM.setText(mInfo.getNOTAM());
            }else
                holder.mNOTAM.setVisibility(View.GONE);
        }else {
            holder.mImage.setImageResource(R.drawable.collapse);
        }
    }

    @Override
    public int getItemCount() {
        return mInfoList == null ? 0 : mInfoList.size();
    }

    @Override
    public void onItemClick(int position) {
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
        LinearLayout mDecoder;
        TextView mICAO, mMETAR, mTAF, mLAST6HR, mNOTAM;
        TextView mName, mTime, mWind, mVis, mCeil, mTemp, mDew, mQNH, mWX;
        ProgressBar mBar;
        ImageView mImage;
        List<Info> mInfoList;
        OnItemTouchListener mContext;

        public ItemViewHolder(View v, List<Info> mInfoList, OnItemTouchListener mContext) {
            super(v);
            mDecoder = (LinearLayout) v.findViewById(R.id.layout_decoded);
            mICAO = (TextView) v.findViewById(R.id.text_airport);
            mName = (TextView) v.findViewById(R.id.text_decoded_name);
            mTime = (TextView) v.findViewById(R.id.text_decoded_time);
            mWind = (TextView) v.findViewById(R.id.text_decoded_wind);
            mVis = (TextView) v.findViewById(R.id.text_decoded_vis);
            mCeil = (TextView) v.findViewById(R.id.text_decoded_ceil);
            mTemp = (TextView) v.findViewById(R.id.text_decoded_temp);
            mDew = (TextView) v.findViewById(R.id.text_decoded_dew);
            mQNH = (TextView) v.findViewById(R.id.text_decoded_QNH);
            mWX = (TextView) v.findViewById(R.id.text_decoded_WX);

            mMETAR = (TextView) v.findViewById(R.id.text_metar);
            mTAF = (TextView) v.findViewById(R.id.text_taf);
            mLAST6HR = (TextView) v.findViewById(R.id.text_latest6hr);
            mNOTAM = (TextView) v.findViewById(R.id.text_notam);

            mBar = (ProgressBar) v.findViewById(R.id.progressbar);
            mImage = (ImageView) v.findViewById(R.id.image_expand);

            this.mInfoList = mInfoList;
            this.mContext = mContext;

            mImage.setOnClickListener(this);
            v.setOnClickListener(this);
        }

        @Override
        public void onClick(View v) {
            LogD.print(this.getAdapterPosition());

            if (v instanceof ImageView) {
                LogD.print("imageview");

                Info mInfo = mInfoList.get(this.getAdapterPosition());
                mInfo.setExpand(!mInfo.isExpand());
                mContext.onSync();
            }else if (v instanceof View) {
                LogD.print("view");
//
//                Info mInfo = mInfoList.get(this.getAdapterPosition());
//                mInfo.getSetting().showAll = !mInfo.getSetting().showAll;
//
//                mContext.onItemClick(this.getAdapterPosition());
            }
        }
    }
}
