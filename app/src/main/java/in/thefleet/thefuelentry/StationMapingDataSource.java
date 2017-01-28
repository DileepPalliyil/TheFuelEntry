package in.thefleet.thefuelentry;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class StationMapingDataSource extends ContentProvider {

    private static final String TAG = "SMapingDataSource";

    private static final String AUTHORITY3 = "in.thefleet.thefuelentry.stationmapingdatasource";
    private static final String BASE_PATH3 = "stationmaping";
    public static final Uri CONTENT_URI3 = Uri.parse("content://" + AUTHORITY3 + "/" + BASE_PATH3 );

    // Constant to identify the requested operation
    private static final int STATIONS = 1;
    private static final int STATIONS_ID = 2;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "StationMapings";

    static  {
        uriMatcher.addURI(AUTHORITY3,BASE_PATH3,STATIONS);
        uriMatcher.addURI(AUTHORITY3,BASE_PATH3 + "/#",STATIONS_ID);

    }
    private SQLiteDatabase database;
    StationMapingDBOpenHelper dbhelper;


    @Override
    public boolean onCreate() {
        dbhelper = new StationMapingDBOpenHelper(getContext());
        database = dbhelper.getWritableDatabase();
        Log.i(TAG, "Stations Maping Database opened");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == STATIONS_ID) {
            selection = StationMapingDBOpenHelper.STATION_ID + "=" + uri.getLastPathSegment();
        }
        // Log.d(TAG,"Selection: "+selection);
        return database.query(StationMapingDBOpenHelper.TABLE_STATION_MAPING,StationMapingDBOpenHelper.ALL_COLUMNS,selection,null,null,null,
                null);
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(StationMapingDBOpenHelper.TABLE_STATION_MAPING,null,values);
        return Uri.parse(BASE_PATH3 + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(StationMapingDBOpenHelper.TABLE_STATION_MAPING,selection,selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[]selectionArgs) {
        return database.update(StationMapingDBOpenHelper.TABLE_STATION_MAPING,values,selection,selectionArgs);
    }
}
