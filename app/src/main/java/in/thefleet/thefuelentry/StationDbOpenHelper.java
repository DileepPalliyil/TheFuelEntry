package in.thefleet.thefuelentry;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;


public class StationDbOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "station.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "StationDbHelper";

    //Constants for identifying table and columns
    public static final String TABLE_STATION = "station";
    public static final String STATION_ID = "_id";
    public static final String STATION_KEY = "stationID";
    public static final String STATION_NAME = "stationName";
    public static final String STATION_LAT = "stationLat";
    public static final String STATION_LON = "stationLon";
    public static final String STATION_LOCN = "stationLocn";
    public static final String STATION_STATE = "stationState";
    public static final String STATION_FKEY = "stationFkey";
    public static final String STATION_DIS = "stationDistance";
    public static final String STATION_RPPRICE = "regularPetrol";
    public static final String STATION_PPPRICE = "premiumPetrol";
    public static final String STATION_RDPRICE = "regularDiesel";
    public static final String STATION_PDPRICE = "premiumDiesel";
    public static final String STATION_CREATED = "stationCreated";

    public static final String[] ALL_COLUMNS =
            {STATION_ID,STATION_KEY,STATION_NAME,STATION_LAT,STATION_LON,
                    STATION_LOCN,STATION_STATE,STATION_FKEY,STATION_DIS,STATION_RPPRICE,
                    STATION_PPPRICE,STATION_RDPRICE,STATION_PDPRICE,STATION_CREATED};


    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_STATION + " (" +
                    STATION_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    STATION_KEY + " INTEGER, " +
                    STATION_NAME + " TEXT, " +
                    STATION_LAT + " DOUBLE, " +
                    STATION_LON + " DOUBLE, " +
                    STATION_LOCN + " TEXT, " +
                    STATION_STATE + " TEXT, " +
                    STATION_FKEY + " INTEGER, " +
                    STATION_DIS + " DOUBLE, " +
                    STATION_RPPRICE + " DOUBLE, " +
                    STATION_PPPRICE + " DOUBLE, " +
                    STATION_RDPRICE + " DOUBLE, " +
                    STATION_PDPRICE + " DOUBLE, " +
                    STATION_CREATED + " TEXT default CURRENT_TIMESTAMP" +
                    ")";

    public StationDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(TAG, TABLE_CREATE);
        db.execSQL(TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_STATION);
        onCreate(db);
    }
}

