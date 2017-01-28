package in.thefleet.thefuelentry;

import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.os.RecoverySystem;
import android.preference.PreferenceManager;
import android.app.LoaderManager;
import android.content.Loader;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CursorAdapter;
import android.widget.ImageButton;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.NetworkError;
import com.android.volley.NoConnectionError;
import com.android.volley.ParseError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.RetryPolicy;
import com.android.volley.ServerError;
import com.android.volley.TimeoutError;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;


import java.io.IOException;
import java.net.SocketTimeoutException;
import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.List;

import in.thefleet.thefuelentry.Model.Fleet;
import in.thefleet.thefuelentry.Model.Station;
import in.thefleet.thefuelentry.Model.StationMaping;
import in.thefleet.thefuelentry.Online.isOnline;
import in.thefleet.thefuelentry.Parser.FleetJSONParser;
import in.thefleet.thefuelentry.Parser.StationJSONParser;
import in.thefleet.thefuelentry.Parser.StationMapingJSONParser;
import in.thefleet.thefuelentry.Phone.TelephonyInfo;


public class RefreshDb extends AppCompatActivity {
    ImageButton rButton;
    public static final String FLEETS_BASE_URL =
            "https://thefleet.in/fleetmasterservice.svc/getFleetDetails/";
    public static final String STATIONS_BASE_URL =
            "https://thefleet.in/Fleetmasterservice.svc/getStation/";
    public static final String STATIONSMAPING_BASE_URL =
            "https://thefleet.in/Fleetmasterservice.svc/getStationMaping/";
    public String fleetUrl = null;
    public String stationsUrl = null;
    public String stationMapingUrl = null;
    public static final String TAG = "RefreshDb";
    List<Fleet> fleetList;
    List<Station> stationList;
    List<StationMaping> stationMapingList;
    private ProgressBar progressBar;
    private final String FLEET_TAG = "FLEET";
    private final String STATION_TAG = "STATION";
    private final String LINK_TAG = "LINK";
    private TextView fleetRefreshPg,stationRefreshPg,mapingRefreshPg;
    ProgressBar fleetProgressPg,stationProgressPg,linkProgressPg;
    public static String imsiSIM;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_refresh_db);
        fleetRefreshPg = (TextView) findViewById(R.id.tvFleetRefresh);
        stationRefreshPg = (TextView) findViewById(R.id.tvStationRefresh);
        fleetProgressPg = (ProgressBar) findViewById(R.id.fleetprogressBar);
        stationProgressPg = (ProgressBar) findViewById(R.id.stationprogressBar);
        linkProgressPg = (ProgressBar) findViewById(R.id.linkprogressBar);
        mapingRefreshPg = (TextView) findViewById(R.id.tvMapingRefresh);
        rButton = (ImageButton) findViewById(R.id.refreshButton);
        rButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                doRefreshTasks();

            }
        });


    }

    private void doRefreshTasks() {

        if (isOnline.isNetworkConnected(getBaseContext())) {

            TelephonyInfo telephonyInfo = TelephonyInfo.getInstance(getBaseContext());
            //Commented to test in avd. Uncomment for production
            boolean isSIMReady = telephonyInfo.isSIM1Ready();
            if (isSIMReady) {
                fleetRefreshPg.setText("Fleet Refresh in progress.");
                stationRefreshPg.setText("Station Refresh in progress.");
                mapingRefreshPg.setText("Link Refresh in Progress");

                SharedPreferences sv = PreferenceManager.getDefaultSharedPreferences(getBaseContext());
                String simValue = sv.getString("simValue", "1");

                if (simValue.equals("1")) {
                    Globals g = Globals.getInstance();
                    g.setImei(imsiSIM);
                    fleetUrl = FLEETS_BASE_URL + telephonyInfo.getImsiSIM1();
                      requestFleetData(fleetUrl);
                    stationsUrl = STATIONS_BASE_URL + telephonyInfo.getImsiSIM1();
                      requestStations(stationsUrl);
                    stationMapingUrl = STATIONSMAPING_BASE_URL + telephonyInfo.getImsiSIM1();
                      requestStationMaping(stationMapingUrl);
                } else if (simValue.equals("2")) {
                    Globals g = Globals.getInstance();
                    g.setImei(imsiSIM);
                    fleetUrl = FLEETS_BASE_URL + telephonyInfo.getImsiSIM2();
                     requestFleetData(fleetUrl);
                    stationsUrl = STATIONS_BASE_URL + telephonyInfo.getImsiSIM2();
                     requestStations(stationsUrl);
                    stationMapingUrl = STATIONSMAPING_BASE_URL + telephonyInfo.getImsiSIM2();
                     requestStationMaping(stationMapingUrl);
                } else {
                    imsiSIM = "357327070825555";
                    Globals g = Globals.getInstance();
                    g.setImei(imsiSIM);
                    fleetUrl = FLEETS_BASE_URL + "357327070825555";
                     requestFleetData(fleetUrl);
                    stationsUrl = STATIONS_BASE_URL + "357327070825555";
                     requestStations(stationsUrl);
                    stationMapingUrl = STATIONSMAPING_BASE_URL + "357327070825555";
                     requestStationMaping(stationMapingUrl);
                }
            } else {
                Toast.makeText(getBaseContext(), "Sim is not ready or no access.Try with different sim", Toast.LENGTH_LONG).show();
            }
        } else {
            Toast.makeText(getBaseContext(), "Network isn't available", Toast.LENGTH_LONG).show();

        }
    } //End of refresh task


    private void requestFleetData(String fleetUrl) {
        Log.d(TAG,"Fleet url :"+fleetUrl);
        StringRequest request1 = new StringRequest(fleetUrl,
                new Response.Listener<String>() {
                    @Override
                    public void onResponse(String response) {
                        if (response.length()>0) {
                            fleetList = FleetJSONParser.parseFeed(response);
                            insertFleetDataToDb(fleetList);
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                        fleetRefreshPg.setVisibility(View.VISIBLE);
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            fleetRefreshPg.setText("Network time out error in fleet request data");
                        } else if (error instanceof AuthFailureError) {
                            fleetRefreshPg.setText("Authentication failure in fleet request data");
                        } else if (error instanceof ServerError) {
                            fleetRefreshPg.setText("Server error in fleet request data");
                        } else if (error instanceof NetworkError) {
                            fleetRefreshPg.setText("Network error in fleet request data");
                        } else if (error instanceof ParseError) {
                            fleetRefreshPg.setText("Parse error in fleet request data");
                        }
                        fleetProgressPg.setVisibility(View.INVISIBLE);
                    }
                });

        int socketTimeout = 60000;// seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
        request1.setRetryPolicy(policy);
        ServerRequestQueue.getInstance(this).addToQueue(request1,FLEET_TAG);
        fleetProgressPg.setVisibility(View.VISIBLE);
    }


    private void insertFleetDataToDb(List<Fleet> fleetList) {
        if (fleetList.size()>0) {
            getContentResolver().delete(
                    FleetDataSource.CONTENT_URI,null, null);
            ContentValues[] fvaluesArr = new ContentValues[fleetList.size()];
            int i = 0;
            for (Fleet fleet : fleetList) {
                ContentValues fvalues = new ContentValues();
                fvalues.put(FleetDbOpenHelper.FLEETS_KEY, fleet.getFleet_ID());
                fvalues.put(FleetDbOpenHelper.FLEETS_REGNO, fleet.getRegNo());
                fvalues.put(FleetDbOpenHelper.FLEETS_TYPE, fleet.getTypName());
                fvalues.put(FleetDbOpenHelper.FLEETS_AVG, fleet.getFleet_Avg_Milage());
                fvalues.put(FleetDbOpenHelper.FLEETS_LQTY, fleet.getLastQty());
                fvalues.put(FleetDbOpenHelper.FLEETS_OKM, fleet.getOpening_KM());
                fvalues.put(FleetDbOpenHelper.FLEETS_FTYPE, fleet.getFuel_Type());
                fvalues.put(FleetDbOpenHelper.FLEETS_CREATED, getTodaysDate());
                fvaluesArr[i++] = fvalues;
            }
            getContentResolver().bulkInsert(FleetDataSource.CONTENT_URI, fvaluesArr);
            fleetProgressPg.setVisibility(View.INVISIBLE);
            fleetRefreshPg.setText("Fleet Data Refresh Completed.");

         }
      }


    public String getTodaysDate() {
        DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd");
        Calendar cal = Calendar.getInstance();
        return dateFormat.format(cal.getTime());
    }


    //Volley request stations
    private void requestStations(String suri) {
        Log.d(TAG,"station url :"+suri);
        StringRequest request2 = new StringRequest(suri,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (response.length()>0) {
                            stationList = StationJSONParser.parseFeed(response);
                            insertStationDataToDb();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {
                            stationRefreshPg.setVisibility(View.VISIBLE);
                            if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                                stationRefreshPg.setText("Network time out error in station request data");
                            } else if (error instanceof AuthFailureError) {
                                stationRefreshPg.setText("Authentication failure in station request data");
                            } else if (error instanceof ServerError) {
                                stationRefreshPg.setText("Server error in station request data");
                            } else if (error instanceof NetworkError) {
                                stationRefreshPg.setText("Network error in station request data");
                            } else if (error instanceof ParseError) {
                                stationRefreshPg.setText("Parse error in station request data");
                            }
                        stationProgressPg.setVisibility(View.INVISIBLE);
                        }
                });
        int socketTimeout = 60000;// seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
        request2.setRetryPolicy(policy);
        ServerRequestQueue.getInstance(this).addToQueue(request2,STATION_TAG);
        stationProgressPg.setVisibility(View.VISIBLE);
    }


    private void insertStationDataToDb() {
        getContentResolver().delete(
                StationDataSource.CONTENT_URI2, null, null) ;

        if (stationList != null) {
            // Log.d(TAG,"Deleting stations");

            ContentValues[] svaluesArr = new ContentValues[stationList.size()];
            int i = 0;
            for (Station station : stationList) {

                ContentValues svalues = new ContentValues();
                svalues.put(StationDbOpenHelper.STATION_KEY, station.getStation_ID());
                svalues.put(StationDbOpenHelper.STATION_NAME, station.getStation_Name());
                svalues.put(StationDbOpenHelper.STATION_LAT, station.getLatitude());
                svalues.put(StationDbOpenHelper.STATION_LON, station.getLongitude());
                svalues.put(StationDbOpenHelper.STATION_LOCN, station.getLocation_Name());
                svalues.put(StationDbOpenHelper.STATION_RPPRICE, station.getRegularPetrol());
                svalues.put(StationDbOpenHelper.STATION_PPPRICE, station.getPremiumPetrol());
                svalues.put(StationDbOpenHelper.STATION_RDPRICE, station.getRegularDiesel());
                svalues.put(StationDbOpenHelper.STATION_PDPRICE, station.getPremiumDiesel());
                svalues.put(StationDbOpenHelper.STATION_CREATED, getTodaysDate());

                svaluesArr[i++] = svalues;
            }
            getContentResolver().bulkInsert(StationDataSource.CONTENT_URI2, svaluesArr);
            stationProgressPg.setVisibility(View.INVISIBLE);
            stationRefreshPg.setText("Station Data Refresh Completed.");
        }
    }


    //Volley request stations
    private void requestStationMaping(String muri) {
        Log.d(TAG,"station maping url :"+muri);
        StringRequest request3 = new StringRequest(muri,

                new Response.Listener<String>() {

                    @Override
                    public void onResponse(String response) {
                        if (response.length()>0) {
                            stationMapingList = StationMapingJSONParser.parseFeed(response);
                            insertStationMapingDataToDb();
                        }
                    }
                },

                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                       mapingRefreshPg.setVisibility(View.VISIBLE);
                        if (error instanceof TimeoutError || error instanceof NoConnectionError) {
                            mapingRefreshPg.setText("Network time out error in link request data");
                        } else if (error instanceof AuthFailureError) {
                            mapingRefreshPg.setText("Authentication failure in link request data");
                        } else if (error instanceof ServerError) {
                            mapingRefreshPg.setText("Server error in station link data");
                        } else if (error instanceof NetworkError) {
                            mapingRefreshPg.setText("Network error in station link data");
                        } else if (error instanceof ParseError) {
                            mapingRefreshPg.setText("Parse error in station link data");
                        }
                        linkProgressPg.setVisibility(View.INVISIBLE);
                    }
                });
        int socketTimeout = 60000;// seconds - change to what you want
        RetryPolicy policy = new DefaultRetryPolicy(socketTimeout, 2, 2);
        request3.setRetryPolicy(policy);
        ServerRequestQueue.getInstance(this).addToQueue(request3,LINK_TAG);
        linkProgressPg.setVisibility(View.VISIBLE);
    }


    private void insertStationMapingDataToDb() {
        getContentResolver().delete(
                StationMapingDataSource.CONTENT_URI3, null, null) ;

        if (stationMapingList != null) {
            // Log.d(TAG,"Deleting stations");
            ContentValues[] mvaluesArr = new ContentValues[stationMapingList.size()];
            int i = 0;
            for (StationMaping stationmaping : stationMapingList) {

                ContentValues mvalues = new ContentValues();
                mvalues.put(StationMapingDBOpenHelper.STATION_FKEY, stationmaping.getFleet_ID());
                mvalues.put(StationMapingDBOpenHelper.STATION_KEY, stationmaping.getStation_ID());
                mvalues.put(StationMapingDBOpenHelper.STATION_CREATED, getTodaysDate());
                mvaluesArr[i++] = mvalues;
            }
            getContentResolver().bulkInsert(StationMapingDataSource.CONTENT_URI3, mvaluesArr);
            linkProgressPg.setVisibility(View.INVISIBLE);
            mapingRefreshPg.setText("Link Data Refresh Completed.");

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        ServerRequestQueue.getInstance(this).cancelAll(FLEET_TAG);
        ServerRequestQueue.getInstance(this).cancelAll(STATION_TAG);
        ServerRequestQueue.getInstance(this).cancelAll(LINK_TAG);
    }




}
