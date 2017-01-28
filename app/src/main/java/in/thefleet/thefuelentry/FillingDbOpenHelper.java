package in.thefleet.thefuelentry;


import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

public class FillingDbOpenHelper extends SQLiteOpenHelper {

    //Constants for db name and version
    private static final String DATABASE_NAME = "filling.db";
    private static final int DATABASE_VERSION = 1;
    private static final String TAG = "FillingDbHelper";

    //Constants for identifying table and columns
    public static final String TABLE_FILLING = "filling";
    public static final String FILLING_ID = "_id";
    public static final String FILLING_KEY = "fillingId";
    public static final String FILLING_REGNO = "fillingRegNo";
    public static final String FILLING_CKM = "fillingCKM";
    public static final String FILLING_OKM = "fillingOKM";
    public static final String FILLING_PRICE = "fillingPrice";
    public static final String FILLING_DATE = "fillingDate";
    public static final String FILLING_QTY = "fillingQty";
    public static final String FILLING_IEMI = "fillingIEMI";
    public static final String FILLING_CREATED = "fillingCreated";

    public static final String[] ALL_COLUMNS =
            {FILLING_ID, FILLING_KEY, FILLING_REGNO, FILLING_CKM, FILLING_OKM,
                    FILLING_PRICE, FILLING_DATE,FILLING_QTY , FILLING_IEMI, FILLING_CREATED};
    public static final String[] FILLING = {FILLING_KEY, FILLING_REGNO,FILLING_CKM};

    //SQL to create table
    private static final String TABLE_CREATE =
            "CREATE TABLE " + TABLE_FILLING + " (" +
                    FILLING_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    FILLING_KEY + " INTEGER, " +
                    FILLING_REGNO + " TEXT, " +
                    FILLING_CKM + " INTEGER, " +
                    FILLING_OKM + " INTEGER, " +
                    FILLING_PRICE + " DOUBLE, " +
                    FILLING_DATE + " DATETIME, " +
                    FILLING_QTY + " DOUBLE, " +
                    FILLING_IEMI + " TEXT, " +
                    FILLING_CREATED + " DATETIME DEFAULT CURRENT_TIMESTAMP" +
                    ")";


    public FillingDbOpenHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        Log.w(TAG, TABLE_CREATE);
        db.execSQL(TABLE_CREATE);

    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_FILLING);
        onCreate(db);
    }
}
