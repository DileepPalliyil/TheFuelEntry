package in.thefleet.thefuelentry;

import android.app.Activity;
import android.content.Context;
import android.database.Cursor;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;


public class StationAdapter extends RecyclerView.Adapter<StationAdapter.ViewHolder>    {
    Cursor dataCursor;
    Context context;

    //added view types
    private static final int TYPE_HEADER = 2;
    private static final int TYPE_ITEM = 1;


    public StationAdapter(Activity mContext, Cursor cursor) {
        this.dataCursor = cursor;
        this.context = mContext;
    }

    @Override
    public ViewHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        View cardview = LayoutInflater.from(parent.getContext())
                .inflate(R.layout.station_row, parent, false);
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

        String stationName = dataCursor.getString(dataCursor.getColumnIndex(StationDbOpenHelper.STATION_NAME));
        String locationName = dataCursor.getString(dataCursor.getColumnIndex(StationDbOpenHelper.STATION_LOCN));
        Double latitude = dataCursor.getDouble(dataCursor.getColumnIndex(StationDbOpenHelper.STATION_LAT));
        Double longitude = dataCursor.getDouble(dataCursor.getColumnIndex(StationDbOpenHelper.STATION_LON));
        Double regularPetrol = dataCursor.getDouble(dataCursor.getColumnIndex(StationDbOpenHelper.STATION_RPPRICE));
        Double premiumPetrol = dataCursor.getDouble(dataCursor.getColumnIndex(StationDbOpenHelper.STATION_PPPRICE));
        Double regularDiesel = dataCursor.getDouble(dataCursor.getColumnIndex(StationDbOpenHelper.STATION_RDPRICE));
        Double premiumDiesel = dataCursor.getDouble(dataCursor.getColumnIndex(StationDbOpenHelper.STATION_PDPRICE));

        holder.tv_stationName.setText(stationName+"-"+locationName);
        holder.tv_latitude.setText("Latitude:"+latitude.toString());
        holder.tv_longitude.setText("Longitude:"+longitude.toString());
        holder.tv_regularPetrol.setText("Regular Petrol"+regularPetrol.toString());
        holder.tv_regularDiesel.setText("Regular Diesel"+regularDiesel.toString());
        holder.tv_premiumPetrol.setText("Premium Petrol"+premiumPetrol.toString());
        holder.tv_premiumDiesel.setText("Premium Diesel"+premiumDiesel.toString());
    }

    @Override
    public int getItemCount() {
        return (dataCursor == null) ? 0 : dataCursor.getCount();
    }


    public class ViewHolder extends RecyclerView.ViewHolder{
        private TextView tv_stationName,tv_locationName,tv_latitude,
                 tv_longitude,tv_regularPetrol,tv_premiumPetrol, tv_regularDiesel,tv_premiumDiesel;
        public ViewHolder(View view) {
            super(view);

            tv_stationName = (TextView)view.findViewById(R.id.tv_StationName);
            tv_locationName = (TextView)view.findViewById(R.id.tv_LocationName);
            tv_latitude = (TextView)view.findViewById(R.id.tv_latitude);
            tv_longitude = (TextView)view.findViewById(R.id.tv_longitude);
            tv_regularPetrol = (TextView)view.findViewById(R.id.tv_regularPetrol);
            tv_premiumPetrol = (TextView)view.findViewById(R.id.tv_premiumPetrol);
            tv_regularDiesel = (TextView)view.findViewById(R.id.tv_regularDiesel);
            tv_premiumDiesel = (TextView)view.findViewById(R.id.tv_premiumDiesel);

        }
    }
}

