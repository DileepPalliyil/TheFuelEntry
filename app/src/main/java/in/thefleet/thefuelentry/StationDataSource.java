package in.thefleet.thefuelentry;


import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class StationDataSource extends ContentProvider {

    private static final String TAG = "StationDataSource";

    private static final String AUTHORITY2 = "in.thefleet.thefuelentry.stationdatasource";
    private static final String BASE_PATH2 = "station";
    public static final Uri CONTENT_URI2 = Uri.parse("content://" + AUTHORITY2 + "/" + BASE_PATH2 );

    // Constant to identify the requested operation
    private static final int STATIONS = 1;
    private static final int STATIONS_ID = 2;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    public static final String CONTENT_ITEM_TYPE = "Stations";

    static  {
        uriMatcher.addURI(AUTHORITY2,BASE_PATH2,STATIONS);
        uriMatcher.addURI(AUTHORITY2,BASE_PATH2 + "/#",STATIONS_ID);

    }
    private SQLiteDatabase database;
    StationDbOpenHelper dbhelper;


    @Override
    public boolean onCreate() {
        dbhelper = new StationDbOpenHelper(getContext());
        database = dbhelper.getWritableDatabase();
        Log.i(TAG, "Stations Database opened");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == STATIONS_ID) {
            selection = StationDbOpenHelper.STATION_ID + "=" + uri.getLastPathSegment();
        }

        return database.query(StationDbOpenHelper.TABLE_STATION,StationDbOpenHelper.ALL_COLUMNS,selection,null,null,null,
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
        long id = database.insert(StationDbOpenHelper.TABLE_STATION,null,values);
        return Uri.parse(BASE_PATH2 + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(StationDbOpenHelper.TABLE_STATION,selection,selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[]selectionArgs) {
        return database.update(StationDbOpenHelper.TABLE_STATION,values,selection,selectionArgs);
    }
}
