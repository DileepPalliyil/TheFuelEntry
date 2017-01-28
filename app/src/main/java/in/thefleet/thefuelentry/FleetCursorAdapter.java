package in.thefleet.thefuelentry;

import android.content.Context;
import android.database.Cursor;
import android.support.v4.content.CursorLoader;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.CursorAdapter;
import android.widget.Filter;
import android.widget.Filterable;
import android.widget.TextView;

public class FleetCursorAdapter extends CursorAdapter  {


    public FleetCursorAdapter(Context context, Cursor c, int flags) {
        super(context, c, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup parent) {
        return LayoutInflater.from(context).inflate(R.layout.fleet_auto_row, parent, false);

    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView tvFleet = (TextView) view.findViewById(R.id.tvAutoFleet);
        String fleet = cursor.getString(cursor.getColumnIndexOrThrow(FleetDbOpenHelper.FLEETS_REGNO));
        tvFleet.setText(fleet);
    }


}
