package in.thefleet.thefuelentry;


import android.Manifest;
import android.app.Activity;
import android.content.Context;
import android.content.DialogInterface;
import android.content.pm.PackageManager;
import android.os.Build;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AlertDialog;
import android.widget.Toast;

import java.util.ArrayList;
import java.util.List;

public class permissionChk {
    private Context context;
    public permissionChk(Context context){
        this.context=context;
    }


    public static final int REQUEST_PERMISSIONS = 1;

    public static boolean requestPermissions(Activity activity) {
        int writePermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.WRITE_EXTERNAL_STORAGE);
        int readPermission = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_EXTERNAL_STORAGE);
        int readPhone = ActivityCompat.checkSelfPermission(activity, Manifest.permission.READ_PHONE_STATE);
        int accessNetwork = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_NETWORK_STATE);
        int accessfLocn = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION);
        int accesscLocn = ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION);
        int receiveBoot = ActivityCompat.checkSelfPermission(activity, Manifest.permission.RECEIVE_BOOT_COMPLETED);
        int internet = ActivityCompat.checkSelfPermission(activity, Manifest.permission.INTERNET);

        List<String> listPermissionsNeeded = new ArrayList<>();


        if (writePermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.WRITE_EXTERNAL_STORAGE);
        }
        if (readPermission != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_EXTERNAL_STORAGE);
        }
        if (readPhone != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.READ_PHONE_STATE);

        }
        if (accessfLocn != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.ACCESS_FINE_LOCATION);
        }
        if (accesscLocn != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(Manifest.permission.ACCESS_COARSE_LOCATION);
        }
        if (receiveBoot != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.RECEIVE_BOOT_COMPLETED);
        }
        if (internet != PackageManager.PERMISSION_GRANTED) {
            listPermissionsNeeded.add(android.Manifest.permission.INTERNET);
        }

        if (!listPermissionsNeeded.isEmpty())
        {
            ActivityCompat.requestPermissions(activity,listPermissionsNeeded.toArray
                    (new String[listPermissionsNeeded.size()]),REQUEST_PERMISSIONS);
            return false;
        }
        return true;

    }
}





