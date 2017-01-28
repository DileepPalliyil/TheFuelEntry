package in.thefleet.thefuelentry;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.DatePickerDialog;
import android.app.Dialog;
import android.app.LoaderManager;
import android.content.ContentValues;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.Loader;
import android.content.SharedPreferences;
import android.database.Cursor;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.icu.text.SimpleDateFormat;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.preference.PreferenceManager;
import android.provider.MediaStore;
import android.support.annotation.Nullable;
import android.support.v4.app.DialogFragment;
import android.support.v4.app.Fragment;
import android.support.v4.content.CursorLoader;
import android.text.Editable;
import android.text.InputType;
import android.text.SpannableStringBuilder;
import android.text.TextWatcher;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.Spinner;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.theartofdev.edmodo.cropper.CropImageView;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.text.DateFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.Iterator;
import java.util.Locale;

import static android.R.attr.bitmap;
import static android.R.attr.fingerprintAuthDrawable;
import static android.app.Activity.RESULT_CANCELED;
import static android.app.Activity.RESULT_OK;
import static android.content.Context.INPUT_METHOD_SERVICE;
import static android.content.Context.MODE_PRIVATE;

public class CreateTab extends Fragment implements android.support.v4.app.LoaderManager.LoaderCallbacks<Cursor>
        {

    FleetCursorAdapter adapter;
    private int LOADER_ID1 = 1;
    private int LOADER_ID2 = 2;
    final static String DATE_FORMAT = "dd/MM/yyyy";
    AutoCompleteTextView fleetAuto;
    private SharedPreferences prefs;
    EditText fleetCreate;
    EditText fleetInvoice,invAmount,dateOffill,ckm;
    private  final int EDITOR_REQUEST_CODE = 1 ;
    private CropImageView mCropImageView;
    private Uri mCropImageUri = null;
    ImageView invoiceView;
    Button start,saveFilling;
    ArrayList<String> stationArray;
    Spinner stationSpn;
    int fleetId = 0;
    String fuelTyp;
    TextView regularFprice,premiumFprice,invQty;
    Double regularFprice2,premiumFprice2;
    RadioGroup fuelRg;
    int selectedRId = 1;
    int maxCkm=0;
    int ckmOkm=0;
    int okm=0;


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.create_tab, container, false);
        fleetCreate = (EditText)view.findViewById(R.id.fleetCreate);
        fleetInvoice = (EditText)view.findViewById(R.id.fleetInvoice);
        start = (Button) view.findViewById(R.id.btn_start);
        saveFilling = (Button) view.findViewById(R.id.btn_saveFilling);
        invoiceView = (ImageView) view.findViewById(R.id.invoiceView);
        stationSpn = (Spinner) view.findViewById(R.id.spinStation);
        regularFprice = (TextView) view.findViewById(R.id.fRegularPrice);
        premiumFprice = (TextView) view.findViewById(R.id.fPremiumPrice);
        fuelRg = (RadioGroup) view.findViewById(R.id.radiogfprice);
        invAmount = (EditText) view.findViewById(R.id.invAmount);
        invQty = (TextView) view.findViewById(R.id.invQty);
        ckm = (EditText) view.findViewById(R.id.ckm);
        dateOffill = (EditText) view.findViewById(R.id.fillDate);
        dateOffill.setText(getTodaysDate());
       dateOffill.setInputType(InputType.TYPE_NULL);

        Globals g = Globals.getInstance();
        g.setLatSelected((double)0.0);
        g.setLonSelected((double)0.0);


        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                openCropEditor(view);
            }
        });

        //save last fleet selected inpref
        final SharedPreferences prefs = PreferenceManager
                .getDefaultSharedPreferences(getContext());
        fleetCreate.setText(prefs.getString("autoSave", ""));

        adapter = new FleetCursorAdapter(getContext(),null,0);
        final ListView fleetList = (ListView) view.findViewById(R.id.fleetList);
        fleetList.setAdapter(adapter);

        fleetCreate.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                String name= null;
                if(stationSpn.getSelectedItem() ==null ) {
                    String regNo = fleetCreate.getText().toString();
                    populateStationSpiner(regNo);
                } else  {
                    //Do nothing
                }


            }
        });

        invAmount.setOnFocusChangeListener(new View.OnFocusChangeListener() {
            @Override
            public void onFocusChange(View v, boolean hasFocus) {

                if(stationSpn.getSelectedItem() ==null ) {
                    String regNo = fleetCreate.getText().toString();
                    populateStationSpiner(regNo);
                } else  {
                    //Do nothing
                }


            }
        });


            //listener called inside runnable to avoid firing during oriantation changes
        fleetList.post(new Runnable() {
            @Override
            public void run() {

                fleetCreate.addTextChangedListener(new TextWatcher() {
                    @Override
                    public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                    }

                    @Override
                    public void onTextChanged(CharSequence s, int start, int before, int count) {
                        Bundle search_bundle = new Bundle();
                        search_bundle.putCharSequence("search_string",s.toString());
                        getLoaderManager().restartLoader(LOADER_ID2, search_bundle, CreateTab.this);
                        fleetList.setAdapter(adapter);
                    }

                    @Override
                    public void afterTextChanged(Editable s) {

                            prefs.edit().putString("autoSave", s.toString()).commit();

                    }
                });

            }
        });


        fleetList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> arg0, View view, int position, long arg3) {
                String fleetSelected=((TextView)view.findViewById(R.id.tvAutoFleet)).getText().toString();
                fleetCreate.setText(fleetSelected);
                //Clear list after selecting the fleet
                fleetList.setAdapter(null);
                //Show cursor at end of the selected fleet
                fleetCreate.setSelection(fleetCreate.getText().length());
                hideKeyboard(getActivity());
            }
        });

        //Save filling
        saveFilling.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
          Boolean invNullFlag = isEmptyEdit(fleetInvoice);
          Boolean fleetNullFlag = isEmptyEdit(fleetCreate);
          Boolean qtyNullFlag = isEmptyText(invQty);
          Boolean ckmNullFlag = isEmptyEdit(ckm);
          Boolean dateValidFlag = isDateValid(dateOffill.getText().toString());


        if(invNullFlag ==false && fleetNullFlag==false && qtyNullFlag==false && ckmNullFlag==false && dateValidFlag==true ) {
            Boolean fleetValidFlag = false;
            //Check the regno is valid
            Cursor cursor_regno = getActivity().getContentResolver().query(FleetDataSource.CONTENT_URI,
            FleetDbOpenHelper.ALL_FLEETS, null, null,null);
            for (cursor_regno.moveToFirst(); !cursor_regno.isAfterLast(); cursor_regno.moveToNext()) {

                if (cursor_regno.getString(cursor_regno.getColumnIndex(FleetDbOpenHelper.FLEETS_REGNO))
                        .equals(fleetCreate.getText().toString())) {

                    fleetValidFlag =true;
                }
            }//End of fleet valid check
            cursor_regno.close();
            if (fleetValidFlag==false){
                Toast.makeText(getActivity(), "Not a valid fleet", Toast.LENGTH_LONG).show();
                fleetCreate.requestFocus();
                return;
            }

            String filling_filter =FillingDbOpenHelper.FILLING_REGNO + " = " +"'"+ fleetCreate.getText().toString()+"'";
            //Get okm (ckm from filling) fkm from existing filling,if any
            Cursor cursor_okm = getActivity().getContentResolver().query(FillingDataSource.CONTENT_URI,
                    FillingDbOpenHelper.FILLING, filling_filter, null, null);
            if (cursor_okm.getCount()>0) {

                for (cursor_okm.moveToFirst(); !cursor_okm.isAfterLast(); cursor_okm.moveToNext())
                    if (cursor_okm.getInt(cursor_okm.getColumnIndex(FillingDbOpenHelper.FILLING_CKM)) > maxCkm) {
                        maxCkm = cursor_okm.getInt(cursor_okm.getColumnIndex(FillingDbOpenHelper.FILLING_CKM));
                    }
            }
            cursor_okm.close();

            //If okm not exist ckm get it from fleet db (maxckm is okm for next filling)
            String fleet_filter =FleetDbOpenHelper.FLEETS_REGNO + " = " + "'"+fleetCreate.getText().toString()+"'";
            Cursor cursor_okmavg = getActivity().getContentResolver().query(FleetDataSource.CONTENT_URI,
                    FleetDbOpenHelper.FLEETS_OKMAVG, fleet_filter, null, null);
            for (cursor_okmavg.moveToFirst(); !cursor_okmavg.isAfterLast(); cursor_okmavg.moveToNext()) {
                double lqty = 0.0;
                lqty = cursor_okmavg.getInt(cursor_okmavg.getColumnIndex(FleetDbOpenHelper.FLEETS_LQTY));
                if (lqty > 0.0) {
                    if (maxCkm >0){
                        ckmOkm = Integer.valueOf(ckm.getText().toString())- maxCkm;
                        okm=maxCkm;
                    }else {
                        ckmOkm = Integer.valueOf(ckm.getText().toString())
                                - cursor_okmavg.getInt(cursor_okmavg.getColumnIndex(FleetDbOpenHelper.FLEETS_OKM));
                        okm= cursor_okmavg.getInt(cursor_okmavg.getColumnIndex(FleetDbOpenHelper.FLEETS_OKM));
                    }
                    double avg = cursor_okmavg.getInt(cursor_okmavg.getColumnIndex(FleetDbOpenHelper.FLEETS_AVG));
                    double upperavg = avg + (avg * .7);
                    double loweravg = avg - (avg * .7);
                    double recavg = ckmOkm / lqty;

                    if ((ckmOkm > 0) && (recavg > loweravg && recavg < upperavg)) {

                    } else {
                        Toast.makeText(getActivity(), "Incorrect CKM"+"OKM:"+okm+"Lqty:"+lqty+"Avg:"+avg, Toast.LENGTH_LONG).show();
                        ckm.requestFocus();
                        return;
                    }

                } else {
                    Toast.makeText(getActivity(), "Invalid value for last filled quantity" + ":" + lqty, Toast.LENGTH_LONG).show();
                }

            }//End of checking ckm

              String fleetRegNo = fleetCreate.getText().toString().trim();
              String fleetInv = fleetInvoice.getText().toString().trim();
                if (mCropImageUri != null) {
                    String fleetSaved = saveFillingToDb();
                    if (fleetSaved != null) {
                        createDirectoryAndSaveFile(mCropImageUri, fleetRegNo, fleetSaved);
                        Toast.makeText(getActivity(), "Saved successfully", Toast.LENGTH_LONG).show();
                    } else {
                        Toast.makeText(getActivity(), "Error in saving the details", Toast.LENGTH_LONG).show();
                    }
                } else {
                       AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                       builder.setMessage("Invoice image missing.Do you want to save filling?")
                          .setCancelable(false)
                          .setPositiveButton("Save", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                             String fleetSaved=saveFillingToDb();
                              if (fleetSaved !=null) {

                                 Toast.makeText(getActivity(), "Saved successfully", Toast.LENGTH_LONG).show();
                              } else {
                                 Toast.makeText(getActivity(), "Error in saving the details", Toast.LENGTH_LONG).show();
                                  }
                                    dialog.cancel();
                               }
                                })
                                .setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                                    public void onClick(DialogInterface dialog, int id) {
                                        dialog.cancel();
                                    }
                                });
                        AlertDialog alert = builder.create();
                        alert.show();

                    }

            }else {
              Toast.makeText(getActivity(), "Enter all values and try again", Toast.LENGTH_LONG).show();
          }

            }
        });

        //Get stations for the fleet entered
        fleetCreate.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {


            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void afterTextChanged(Editable s)
            {
                //Uncommand for populating station details on text change
                if (s!=null) {
                    populateStationSpiner(s.toString());
                }
            }
        });


        invAmount.addTextChangedListener(new TextWatcher() {
            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            //Calculate the fule qty from invoice amount
            @Override
            public void afterTextChanged(Editable s) {

                  if (selectedRId == 1) {
                      if (regularFprice2 !=null && s.length()>0 && regularFprice2 >0 ) {
                          Double a = round(Double.parseDouble(invAmount.getText().toString()) / regularFprice2, 1);
                          invQty.setText(a.toString());
                      }

                  } else if (selectedRId == 2) {
                      if (premiumFprice2 !=null && s.length()>0 && premiumFprice2 >0) {
                          Double a = round(Double.parseDouble(invAmount.getText().toString()) / premiumFprice2, 1);
                          invQty.setText(a.toString());
                      }
                  }

                if (s.length()==0){
                    invQty.setText("");
                }
              }
        });

        //Populate fuel proce after selecting station

        stationSpn.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                String selectedItem = parent.getItemAtPosition(position).toString();
                String sid = stationSpn.getSelectedItem().toString().substring(0,
                        stationSpn.getSelectedItem().toString().indexOf('-'));

                String ftype = fuelTyp.substring(0,1);

                //Display fuel prices for selected stations
                String priceFilter = StationDbOpenHelper.STATION_KEY + "=" + Integer.valueOf(sid.trim());

                Cursor cursor_fpice = getActivity().getContentResolver().query(StationDataSource.CONTENT_URI2,
                        StationDbOpenHelper.ALL_COLUMNS, priceFilter, null,null);

                if (cursor_fpice.getCount() >0) {
                    //  Log.d(TAG,"Inside if for get count gr than 0" + "ftype :"+ftype);
                    cursor_fpice.moveToFirst();
                    if (ftype.equals("D")) {
                        regularFprice2 = cursor_fpice.getDouble(cursor_fpice.getColumnIndex(StationDbOpenHelper.STATION_RDPRICE));
                        premiumFprice2 = cursor_fpice.getDouble(cursor_fpice.getColumnIndex(StationDbOpenHelper.STATION_PDPRICE));
                        regularFprice.setText("NormalDiesel:"+cursor_fpice.getString(cursor_fpice.getColumnIndex(StationDbOpenHelper.STATION_RDPRICE)));
                        premiumFprice.setText("PremiumDiesel:"+cursor_fpice.getString(cursor_fpice.getColumnIndex(StationDbOpenHelper.STATION_PDPRICE)));

                    }else if (ftype.equals("P")) {
                        regularFprice2 = cursor_fpice.getDouble(cursor_fpice.getColumnIndex(StationDbOpenHelper.STATION_RPPRICE));
                        premiumFprice2 = cursor_fpice.getDouble(cursor_fpice.getColumnIndex(StationDbOpenHelper.STATION_PPPRICE));
                        regularFprice.setText("NormalPetrol:"+cursor_fpice.getString(cursor_fpice.getColumnIndex(StationDbOpenHelper.STATION_RPPRICE)));
                        premiumFprice.setText("PremiumPetrol:"+cursor_fpice.getString(cursor_fpice.getColumnIndex(StationDbOpenHelper.STATION_PPPRICE)));

                    }
                    cursor_fpice.close();

                }else {
                 //Do nothing

                }
                if (selectedItem != parent.getItemAtPosition(0).toString()) {

                    AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
                    builder.setMessage("Station selected is not nearest!Still want to continue or reset to nearest?")
                            .setCancelable(false)
                            .setPositiveButton("Continue", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    dialog.cancel();
                                }
                            })
                            .setNegativeButton("Reset", new DialogInterface.OnClickListener() {
                                public void onClick(DialogInterface dialog, int id) {
                                    stationSpn.setSelection(0);
                                }
                            });
                    AlertDialog alert = builder.create();
                    alert.show();
                }

            } // to close the onItemSelected

            public void onNothingSelected(AdapterView<?> parent) {

            }
        }); //End of selecting fuel price


        fuelRg.setOnCheckedChangeListener(new RadioGroup.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(RadioGroup group, int checkedId) {
                switch(checkedId) {
                    case R.id.radioReular:
                       selectedRId=1;
                        break;
                    case R.id.radioPremium:
                        selectedRId=2;
                        break;

                }
            }
        });

        dateOffill.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                hideKeyboard(getActivity());
                selectDate();

            }
        });


        return view;
    }

       private String saveFillingToDb() {
           Double fp = 0.0;
           Double qty = 0.0;
           String sdateOfFill;
           Globals g = Globals.getInstance();
           DateFormat sdf = new java.text.SimpleDateFormat(DATE_FORMAT);
           Date dateOfFill;
           String insertId=null;

           if (selectedRId == 1) {
               fp = regularFprice2;
               qty = Double.parseDouble(invQty.getText().toString());
           } else if (selectedRId == 2) {
               fp = premiumFprice2;
               qty = Double.parseDouble(invQty.getText().toString());
           }

           try {
               dateOfFill = sdf.parse(dateOffill.getText().toString());
               DateFormat dateFormatISO8601 = new java.text.SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
               sdateOfFill = dateFormatISO8601.format(dateOfFill);

               if (fp > 0.0 && qty > 0.0 && okm >0) {
                   ContentValues values = new ContentValues();
                   values.put(FillingDbOpenHelper.FILLING_REGNO, fleetCreate.getText().toString().trim());
                   values.put(FillingDbOpenHelper.FILLING_CKM, Integer.parseInt(ckm.getText().toString().trim()));
                   values.put(FillingDbOpenHelper.FILLING_PRICE, fp);
                   values.put(FillingDbOpenHelper.FILLING_QTY, qty);
                   values.put(FillingDbOpenHelper.FILLING_OKM, okm);
                   values.put(FillingDbOpenHelper.FILLING_DATE,  sdateOfFill);
                   values.put(FillingDbOpenHelper.FILLING_IEMI,  g.getImei());
                   values.put(FillingDbOpenHelper.FILLING_CREATED,  getTodaysDate());
                   Uri insertUri=getActivity().getContentResolver().insert(FillingDataSource.CONTENT_URI, values);
                   insertId = insertUri.getLastPathSegment();

               } else {
                   Toast.makeText(getActivity(), "Select fuelprice,quantity filled", Toast.LENGTH_LONG).show();
               }
           } catch (ParseException e) {
               e.printStackTrace();
           }
          return insertId;

       }


        public void populateStationSpiner(String regno) {
        stationArray = new ArrayList<String>();
        Globals g = Globals.getInstance();

        //Get the fleet id of the regno selected
        String fleetFilter = FleetDbOpenHelper.FLEETS_REGNO + "=" + "'" + regno + "'";
        Cursor cursor_fleetId = getActivity().getContentResolver().query(FleetDataSource.CONTENT_URI,
                FleetDbOpenHelper.FLEETS_IDFUEL, fleetFilter, null, null);
        for (cursor_fleetId.moveToFirst(); !cursor_fleetId.isAfterLast(); cursor_fleetId.moveToNext()) {
            fleetId = cursor_fleetId.getInt(cursor_fleetId.getColumnIndex(FleetDbOpenHelper.FLEETS_KEY));
            fuelTyp = cursor_fleetId.getString(cursor_fleetId.getColumnIndex(FleetDbOpenHelper.FLEETS_FTYPE));

        }
        cursor_fleetId.close();


        String stationFilter = StationMapingDBOpenHelper.STATION_FKEY + "=" + fleetId;
        Cursor cursor_stndd = getActivity().getContentResolver().query(StationMapingDataSource.CONTENT_URI3,
                StationMapingDBOpenHelper.ALL_COLUMNS, stationFilter, null, null);

        if (cursor_stndd.getCount()>0){

          if ((Double)g.getLatSelected() != 0.0 || (Double)g.getLonSelected() != 0.0) {
            double distance = 0;
            double prevDistance = 0;
            String prevName = null;

            for (cursor_stndd.moveToFirst(); !cursor_stndd.isAfterLast(); cursor_stndd.moveToNext()) {
                String stationNameFilter = StationDbOpenHelper.STATION_KEY + "=" +
                        cursor_stndd.getString(cursor_stndd.getColumnIndex(StationMapingDBOpenHelper.STATION_KEY));
                Cursor cursor_stnnm = getActivity().getContentResolver().query(StationDataSource.CONTENT_URI2,
                        StationDbOpenHelper.ALL_COLUMNS, stationNameFilter, null, null);
                cursor_stnnm.moveToFirst();

                distance = geoCoordToMeter(round((Double) g.getLatSelected(), 4),
                        round((Double) g.getLonSelected(), 4),
                        cursor_stnnm.getDouble(cursor_stnnm.getColumnIndex(StationDbOpenHelper.STATION_LAT)),
                        cursor_stnnm.getDouble(cursor_stnnm.getColumnIndex(StationDbOpenHelper.STATION_LON)));
                String name = cursor_stnnm.getString(cursor_stnnm.getColumnIndex(StationDbOpenHelper.STATION_KEY)) + "-" +
                        cursor_stnnm.getString(cursor_stnnm.getColumnIndex(StationDbOpenHelper.STATION_NAME)) + "-" +
                        cursor_stnnm.getString(cursor_stnnm.getColumnIndex(StationDbOpenHelper.STATION_LOCN)) + "-" +
                        distance;

                int prevChkZero = Double.compare(prevDistance, 0.0);
                int disChkPrev = Double.compare(distance, prevDistance);
                if (prevChkZero != 0 && disChkPrev > 0) {
                    stationArray.add(name);
                } else if (prevChkZero != 0 && disChkPrev < 0) {
                    stationArray.add(prevName);
                    //prevDistance = distance;
                    prevName = name;
                    prevDistance = distance;
                } else {
                    prevDistance = distance;
                    prevName = name;
                }
                cursor_stnnm.close();
            }

            stationArray.add(prevName);
            Collections.reverse(stationArray);

        } else {
            //Show all statins
            Toast.makeText(getActivity(), "Please wait and retry", Toast.LENGTH_LONG).show();

              for (cursor_stndd.moveToFirst(); !cursor_stndd.isAfterLast(); cursor_stndd.moveToNext()) {
                  String stationNameFilter = StationDbOpenHelper.STATION_KEY + "=" +
                          cursor_stndd.getString(cursor_stndd.getColumnIndex(StationMapingDBOpenHelper.STATION_KEY));
                  Cursor cursor_stnnm = getActivity().getContentResolver().query(StationDataSource.CONTENT_URI2,
                          StationDbOpenHelper.ALL_COLUMNS, stationNameFilter, null, null);
                  cursor_stnnm.moveToFirst();

                  String name = cursor_stnnm.getString(cursor_stnnm.getColumnIndex(StationDbOpenHelper.STATION_KEY)) + "-" +
                          cursor_stnnm.getString(cursor_stnnm.getColumnIndex(StationDbOpenHelper.STATION_NAME)) + "-" +
                          cursor_stnnm.getString(cursor_stnnm.getColumnIndex(StationDbOpenHelper.STATION_LOCN));

                  stationArray.add(name);

                  cursor_stnnm.close();
              }
        }


        if (stationArray.contains(null)||stationArray.contains("")) {

          }else{
            ArrayAdapter<String> stationArrayAdapter = new ArrayAdapter<String>(
                    getContext(), R.layout.station_spinner, R.id.textSpin, stationArray);
            // Log.d("CreateTab","Array adapter count:"+stationArray.toString());

            stationSpn.setAdapter(stationArrayAdapter);
        }
      }

        cursor_stndd.close();
    }


    private double geoCoordToMeter(double latA, double lonA, double latB, double lonB) {
        double earthRadius = 6378.137d; // km
        double dLat = (latB - latA) * Math.PI / 180d;
        double dLon = (lonB - lonA) * Math.PI / 180d;
        double a = Math.sin(dLat / 2d) * Math.sin(dLat / 2d)
                + Math.cos(latA * Math.PI / 180d)
                * Math.cos(latB * Math.PI / 180d)
                * Math.sin(dLon / 2d) * Math.sin(dLon / 2);
        double c = 2d * Math.atan2(Math.sqrt(a), Math.sqrt(1d - a));
        double d = earthRadius * c;
        return round((d * 1000d)/1000,2);
    }

    public static double round(double value, int places) {
        if (places < 0) throw new IllegalArgumentException();
        BigDecimal bd = new BigDecimal(value);
        bd = bd.setScale(places, RoundingMode.HALF_UP);
        return bd.doubleValue();
    }

    private boolean isEmptyEdit(EditText etText) {
        if (etText.getText().toString().trim().length() > 0)
            return false;

        return true;
    }
    private boolean isEmptyText(TextView etText) {
        if (etText.getText().toString().trim().length() > 0)
           return false;

          return true;
    }


    private void createDirectoryAndSaveFile(Uri imageToSave, String regNo,String fleetSaved) {
        String fileName = "InvFill" + regNo + "-"+fleetSaved+ "-"+System.currentTimeMillis() + ".jpg";
        File direct = new File(Environment.getExternalStorageDirectory() + "/TheFleet/Invoice");
        Bitmap btmap;
        try {
            btmap = MediaStore.Images.Media.getBitmap(getActivity().getContentResolver(), imageToSave);


            if (!direct.exists()) {
                File invoiceDirectory = new File("/sdcard//TheFleet/Invoice/");
                invoiceDirectory.mkdirs();
            }

            File file = new File(new File("/sdcard/TheFleet/Invoice/"), fileName);
            if (file.exists()) {
                file.delete();
            }

            FileOutputStream out = new FileOutputStream(file);
            btmap.compress(Bitmap.CompressFormat.JPEG, 55, out);
            out.flush();
            out.close();
            Log.d("CreateTab","Invoice saved in directory");
        } catch (IOException e) {
            e.printStackTrace();
        }

    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        getLoaderManager().initLoader(LOADER_ID1, null, this);
    }

    @Override
    public android.support.v4.content.Loader<Cursor> onCreateLoader(int id, Bundle args) {
        if (id == 1) {
            return null;
        } else  {
            String fFilter = FleetDbOpenHelper.FLEETS_REGNO + " like " + "'" + "%" + args.get("search_string") + "%" + "'";

            return new CursorLoader(getContext(), FleetDataSource.CONTENT_URI, null, fFilter, null, null);
        }
    }

    @Override
    public void onLoadFinished(android.support.v4.content.Loader<Cursor> loader, Cursor data) {
        adapter.swapCursor(data);
    }

    @Override
    public void onLoaderReset(android.support.v4.content.Loader<Cursor> loader) {
        adapter.swapCursor(null);
    }

     public void openCropEditor(View view) {
        Intent intent = new Intent(getActivity(), CropViewActivity.class);
        startActivityForResult(intent, EDITOR_REQUEST_CODE);
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_OK) {
            mCropImageUri = Uri.parse(data.getStringExtra("imageUri"));
            invoiceView.setImageURI(mCropImageUri);
        }else if ((requestCode == EDITOR_REQUEST_CODE && resultCode == RESULT_CANCELED)){
            invoiceView.setImageURI(null);
        }
    }

    public static void hideKeyboard(Activity activity) {
        InputMethodManager imm = (InputMethodManager) activity.getSystemService(Activity.INPUT_METHOD_SERVICE);
        //Find the currently focused view, so we can grab the correct window token from it.
        View view = activity.getCurrentFocus();
        //If no view currently has focus, create a new one, just so we can grab a window token from it
        if (view == null) {
            view = new View(activity);
        }
        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
    }

    public String getTodaysDate() {
       DateFormat dateFormat = new java.text.SimpleDateFormat("dd/MM/yyyy");
       Calendar cal = Calendar.getInstance();
       return dateFormat.format(cal.getTime());
     }

    public static boolean isDateValid(String date)
         {
            try {
               DateFormat df = new java.text.SimpleDateFormat(DATE_FORMAT);
               df.setLenient(false);
               df.parse(date);
                 return true;
            } catch (ParseException e) {
                return false;
            }
         }



            public static class SelectDateFragment extends DialogFragment implements DatePickerDialog.OnDateSetListener {

                public SelectDateFragment() {this.mEditText = mEditText;}

                // Thanks to -> http://javapapers.com/android/android-datepicker/
                EditText mEditText;
                public SelectDateFragment(EditText mEditText){
                    this.mEditText = mEditText;
                }

                @Override
                public Dialog onCreateDialog(Bundle savedInstanceState) {
                    final Calendar calendar = Calendar.getInstance();
                    int yy = calendar.get(Calendar.YEAR);
                    int mm = calendar.get(Calendar.MONTH);
                    int dd = calendar.get(Calendar.DAY_OF_MONTH);
                    return new DatePickerDialog(getActivity(), this, yy, mm, dd);
                }

                public void onDateSet(DatePicker view, int yy, int mm, int dd) {
                    populateSetDate(mEditText, yy, mm+1, dd);
                }
            }

            public void selectDate() {
                DialogFragment newFragment = new SelectDateFragment(dateOffill);
                newFragment.show(getFragmentManager(), "DatePicker");
            }

            public static void populateSetDate(EditText mEditText, int year, int month, int day) {
                mEditText.setText(day+"/"+month+"/"+year);
            }




}


