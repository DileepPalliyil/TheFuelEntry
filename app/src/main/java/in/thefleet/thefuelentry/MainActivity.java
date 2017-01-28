package in.thefleet.thefuelentry;

import android.Manifest;
import android.app.Activity;
import android.app.SearchManager;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.location.Criteria;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Build;
import android.provider.Settings;
import android.support.annotation.IntDef;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.design.widget.TabLayout;
import android.support.multidex.MultiDex;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentTransaction;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.Loader;
import android.support.v4.view.ViewPager;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.SearchView;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.Filter;
import android.widget.ListView;
import android.widget.Toast;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.util.ArrayList;
import java.util.Locale;

import in.thefleet.thefuelentry.Model.Fleet;

import static android.net.NetworkInfo.DetailedState.BLOCKED;

public class MainActivity extends AppCompatActivity implements TabLayout.OnTabSelectedListener,
        GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener,
        com.google.android.gms.location.LocationListener {

    private TabLayout tabLayout;
    private ViewPager viewPager;
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private long UPDATE_INTERVAL = 60 * 1000;  /* 1 min */
    private long FASTEST_INTERVAL = 2000; /* 2 sec */

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Adding toolbar to the activity
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        //Initializing the tablayout
        tabLayout = (TabLayout) findViewById(R.id.tabLayout);

        //Adding the tabs using addTab() method
        tabLayout.addTab(tabLayout.newTab()
                .setText("ENTER"));
        tabLayout.addTab(tabLayout.newTab()
                .setText("FLEET"));
        tabLayout.addTab(tabLayout.newTab()
                .setText("STATN"));
        tabLayout.addTab(tabLayout.newTab()
                .setText("LINK"));
        tabLayout.addTab(tabLayout.newTab()
                .setText("SAVED"));
        tabLayout.setTabGravity(TabLayout.GRAVITY_FILL);


        //Creating our pager adapter
        Pager adapter = new Pager(getSupportFragmentManager(), tabLayout.getTabCount());
        //Initializing viewPager
        viewPager = (ViewPager) findViewById(R.id.pager);
        int limit = (adapter.getCount() > 1 ? adapter.getCount() - 1 : 1);

        //Adding adapter to pager
        viewPager.setAdapter(adapter);
        viewPager.setOffscreenPageLimit(limit);

        //Adding onTabSelectedListener to swipe views
        //tabLayout.setOnTabSelectedListener(this);
        tabLayout.addOnTabSelectedListener(this);

        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
                viewPager.setCurrentItem(position, false);
                tabLayout.getTabAt(position).select();
            }

            @Override
            public void onPageScrollStateChanged(int state) {
            }
        });
        permissionChk.requestPermissions(MainActivity.this);


        mGoogleApiClient = new GoogleApiClient.Builder(this)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this).build();


    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();

        switch (item.getItemId()) {
            case R.id.action_refresh:
                Intent i = new Intent(this, RefreshDb.class);
                startActivity(i);
                return true;
            default:
                return super.onOptionsItemSelected(item);

            case R.id.action_settings:
                Intent j = new Intent(this, FleetPreferenceActivity.class);
                startActivity(j);
                return true;
        }
    }


    @Override
    public void onTabSelected(TabLayout.Tab tab) {
        viewPager.setCurrentItem(tab.getPosition());


    }

    @Override
    public void onTabUnselected(TabLayout.Tab tab) {

    }

    @Override
    public void onTabReselected(TabLayout.Tab tab) {

    }


    @Override
    protected void onDestroy() {

        super.onDestroy();
    }


    public void onLocationChanged(Location location) {
        // New location has now been determined
        /*String msg = "Updated Location: " +
                Double.toString(location.getLatitude()) + "," +
                Double.toString(location.getLongitude());
        Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();*/
        Globals g = Globals.getInstance();
        g.setLatSelected(location.getLatitude());
        g.setLonSelected(location.getLongitude());

        // create a LatLng Object for use with maps
       // LatLng latLng = new LatLng(location.getLatitude(), location.getLongitude());
    }

    @Override
    public void onConnectionSuspended(int i) {
        if (i == CAUSE_SERVICE_DISCONNECTED) {
            Toast.makeText(this, "Disconnected. Please re-connect.", Toast.LENGTH_SHORT).show();
        } else if (i == CAUSE_NETWORK_LOST) {
            Toast.makeText(this, "Network lost. Please re-connect.", Toast.LENGTH_SHORT).show();
        }
    }


    // Trigger new location updates at interval
    protected void startLocationUpdates() {
        // Create the location request
        mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_BALANCED_POWER_ACCURACY)
                .setInterval(UPDATE_INTERVAL)
                .setFastestInterval(FASTEST_INTERVAL);
        // Request location updates
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {

            return;
        }
        LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient,
                mLocationRequest, this);
    }


    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        Globals g = Globals.getInstance();
        g.setLatSelected((double)0);
        g.setLonSelected((double)0);

    }

    protected void onStart() {
        super.onStart();
        // Connect the client.
        mGoogleApiClient.connect();
    }

    protected void onStop() {
        // only stop if it's connected, otherwise we crash
        if (mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }

        super.onStop();
    }

    public void onConnected(Bundle dataBundle) {
        // Get last known recent location.
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this,
                Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        Location mCurrentLocation = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        // Note that this can be NULL if last location isn't already known.
        if (mCurrentLocation != null) {
            Globals g = Globals.getInstance();
            g.setLatSelected(mCurrentLocation.getLatitude());
            g.setLonSelected(mCurrentLocation.getLongitude());

           /* String msg = "Current Location: " +
                    Double.toString(mCurrentLocation.getLatitude()) + "," +
                    Double.toString(mCurrentLocation.getLongitude());
            Toast.makeText(this, msg, Toast.LENGTH_SHORT).show();*/
           // LatLng latLng = new LatLng(mCurrentLocation.getLatitude(), mCurrentLocation.getLongitude());
        }
        // Begin polling for new location updates.
        startLocationUpdates();
    }





}
