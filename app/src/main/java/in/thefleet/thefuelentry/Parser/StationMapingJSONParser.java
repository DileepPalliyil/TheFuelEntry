package in.thefleet.thefuelentry.Parser;


import android.util.Log;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

import in.thefleet.thefuelentry.Model.StationMaping;

public class StationMapingJSONParser {

    public static List<StationMaping> parseFeed(String content) {
        //Proceed only if url retrieves data for not getting null pointer exception
        if (content != null) {

            try {
                JSONObject jobj = new JSONObject(content);

                List<StationMaping> stationMappingList = new ArrayList<>();
                JSONArray ar = jobj.optJSONArray("getStationMapingResult");
                for (int i = 0; i < ar.length(); i++) {

                    JSONObject obj = ar.getJSONObject(i);
                    StationMaping stationmaping = new StationMaping();
                    stationmaping.setStation_ID(obj.getInt("Station_ID"));
                    stationmaping.setFleet_ID(obj.getInt("Fleet_ID"));

                    stationMappingList.add(stationmaping);
                }

                return stationMappingList;

            }catch(JSONException e){

                Log.d("SMappingJSONParser", "In parser exception", e);
                return null;
            }

        } else {
            Log.d("SMappingJSONParser", "Null String return");
            return null;
        }

    }
}
