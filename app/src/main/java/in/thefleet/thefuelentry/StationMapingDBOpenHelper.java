package in.thefleet.thefuelentry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class StationMapingDBOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "stationmaping.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "SMapingDbAdapter";

    //Constants for identifying table and columns
    public static final String TABLE_STATION_MAPING = "stationmaping";
    public static final String STATION_ID = "_id";
    public static final String STATION_KEY = "stationID";
    public static final String STATION_FKEY = "stationFkey";
    public static final String STATION_CREATED = "stationCreated";

    public static final String[] ALL_COLUMNS =
            {STATION_ID,STATION_KEY,STATION_FKEY,STATION_CREATED};

    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_STATION_MAPING + " (" +
                    STATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    STATION_KEY + " INTEGER, " +
                    STATION_FKEY + " INTEGER, " +
                    STATION_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";

    public StationMapingDBOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(TAG, TABLE_CREATE);
        db.execSQL(TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_STATION_MAPING);
        onCreate(db);
    }
}
