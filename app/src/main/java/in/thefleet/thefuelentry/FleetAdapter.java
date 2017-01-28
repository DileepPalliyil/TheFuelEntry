package in.thefleet.thefuelentry;


import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;

import in.thefleet.thefuelentry.Model.Fleet;

public class FleetAdapter extends RecyclerView.Adapter<FleetAdapter.ViewHolder>    {
    Cursor dataCursor;
    Context context;

    //added view types
     private static final int TYPE_HEADER = 2;
     private static final int TYPE_ITEM = 1;


    public FleetAdapter(Activity mContext,Cursor cursor) {
        this.dataCursor = cursor;
        this.context = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.fleet_row, parent, false);
        return new ViewHolder(cardview);
    }


    public Cursor swapCursor(Cursor cursor) {
        if (dataCursor == cursor) {
            return null;
        }
        Cursor oldCursor = dataCursor;
        this.dataCursor = cursor;
        if (cursor != null) {
            this.notifyDataSetChanged();
        }
        return oldCursor;
    }

    @Override
    public void onBindViewHolder(ViewHolder holder, int position) {
        dataCursor.moveToPosition(position);

        String regno = dataCursor.getString(dataCursor.getColumnIndex(FleetDbOpenHelper.FLEETS_REGNO));
        String typ = dataCursor.getString(dataCursor.getColumnIndex(FleetDbOpenHelper.FLEETS_TYPE));
        String ftype = dataCursor.getString(dataCursor.getColumnIndex(FleetDbOpenHelper.FLEETS_FTYPE));

        holder.tv_regno.setText(regno);
        holder.tv_fueltype.setText(ftype);
        holder.tv_typename.setText(typ);
    }

    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_regno,tv_fueltype,tv_typename;
        public ViewHolder(View view) {
            super(view);

            tv_regno = (TextView)view.findViewById(R.id.tv_regno);
            tv_fueltype = (TextView)view.findViewById(R.id.tv_fueltype);
            tv_typename = (TextView)view.findViewById(R.id.tv_typename);

        }
    }
}
