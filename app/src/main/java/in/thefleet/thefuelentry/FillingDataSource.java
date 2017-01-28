package in.thefleet.thefuelentry;

import android.content.ContentProvider;
import android.content.ContentValues;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.net.Uri;
import android.support.annotation.Nullable;
import android.util.Log;

public class FillingDataSource extends ContentProvider {

    private static final String TAG = "FillingDataSource";

    private static final String AUTHORITY = "in.thefleet.thefuelentry.fillingdatasource";
    private static final String BASE_PATH = "filling";
    public static final Uri CONTENT_URI = Uri.parse("content://" + AUTHORITY + "/" + BASE_PATH );

    // Constant to identify the requested operation
    private static final int FILLING = 1;
    private static final int FILLING_ID = 2;

    private static final UriMatcher uriMatcher =
            new UriMatcher(UriMatcher.NO_MATCH);

    static  {
        uriMatcher.addURI(AUTHORITY,BASE_PATH,FILLING);
        uriMatcher.addURI(AUTHORITY,BASE_PATH + "/#",FILLING_ID);

    }
    private SQLiteDatabase database;
    FillingDbOpenHelper dbhelper;


    @Override
    public boolean onCreate() {
        dbhelper = new FillingDbOpenHelper(getContext());
        database = dbhelper.getWritableDatabase();
        Log.i(TAG, "Database opened");
        return true;
    }

    @Nullable
    @Override
    public Cursor query(Uri uri, String[] projection, String selection, String[] selectionArgs, String sortOrder) {
        if (uriMatcher.match(uri) == FILLING_ID) {
            selection = FillingDbOpenHelper.FILLING_ID + "=" + uri.getLastPathSegment();
        }
        // Log.d(TAG,"Selection: "+selection);
        return database.query(FillingDbOpenHelper.TABLE_FILLING,FillingDbOpenHelper.ALL_COLUMNS,selection,null,null,null,
                FillingDbOpenHelper.FILLING_REGNO + " DESC");
    }

    @Nullable
    @Override
    public String getType(Uri uri) {
        return null;
    }

    @Nullable
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        long id = database.insert(FillingDbOpenHelper.TABLE_FILLING,null,values);
        return Uri.parse(BASE_PATH + "/" + id);
    }

    @Override
    public int delete(Uri uri, String selection, String[] selectionArgs) {
        return database.delete(FillingDbOpenHelper.TABLE_FILLING,selection,selectionArgs);
    }

    @Override
    public int update(Uri uri, ContentValues values, String selection, String[]selectionArgs) {
        return database.update(FillingDbOpenHelper.TABLE_FILLING,values,selection,selectionArgs);
    }
}


