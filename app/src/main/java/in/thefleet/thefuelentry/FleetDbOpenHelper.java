package in.thefleet.thefuelentry;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;

public class FleetDbOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "fleet.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "FleetsDbAdapter";

    //Constants for identifying table and columns
    public static final String TABLE_FLEETS = "fleet";
    public static final String FLEETS_ID = "_id";
    public static final String FLEETS_KEY = "Fleet_ID";
    public static final String FLEETS_REGNO = "fleetsRegNo";
    public static final String FLEETS_TYPE = "fleetsType";
    public static final String FLEETS_MODEL = "fleetsModel";

    public static final String FLEETS_CKM = "fleetsCKM";
    public static final String FLEETS_OKM = "fleetsOKM";

    public static final String FLEETS_AVG = "fleetsAVG";
    public static final String FLEETS_RAVG = "fleetsRAVG";

    public static final String FLEETS_LQTY = "fleetsLQTY";
    public static final String FLEETS_FTYPE = "fleetsFType";

    public static final String FLEETS_CREATED = "fleetsCreated";

    public static final String[] ALL_COLUMNS =
            {FLEETS_ID,FLEETS_KEY,FLEETS_REGNO,FLEETS_TYPE,FLEETS_MODEL,
                    FLEETS_CKM,FLEETS_AVG,FLEETS_RAVG,FLEETS_LQTY,FLEETS_FTYPE,FLEETS_OKM,FLEETS_CREATED};
    public static final String[] FLEETS_IDFUEL = {FLEETS_KEY,FLEETS_FTYPE};
    public static final String[] ALL_FLEETS = {FLEETS_REGNO};
    public static final String[] FLEETS_OKMAVG = {FLEETS_OKM,FLEETS_AVG,FLEETS_LQTY};
    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_FLEETS + " (" +
                    FLEETS_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FLEETS_KEY + " INTEGER, " +
                    FLEETS_REGNO + " TEXT, " +
                    FLEETS_TYPE + " TEXT, " +
                    FLEETS_MODEL + " TEXT, " +
                    FLEETS_CKM + " INTEGER, " +
                    FLEETS_AVG + " DOUBLE, " +
                    FLEETS_RAVG + " DOUBLE, " +
                    FLEETS_LQTY + " DOUBLE, " +
                    FLEETS_FTYPE + " TEXT, " +
                    FLEETS_OKM + " INTEGER, " +
                    FLEETS_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";


    public FleetDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(TAG, TABLE_CREATE);
        db.execSQL(TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS "+TABLE_FLEETS);
        onCreate(db);
    }




}