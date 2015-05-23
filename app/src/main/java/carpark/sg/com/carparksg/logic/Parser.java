package carpark.sg.com.carparksg.logic;

import android.net.Uri;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import carpark.sg.com.model.Carpark;
import carpark.sg.com.model.CarparkList;
import carpark.sg.com.model.Coordinate;

/**
 * Created by joseph on 4/5/2015.
 */
public class Parser {

    private static final int INDEX_JSON_STATUS_CODE = 0;
    private static final int INDEX_JSON_DATA = 1;
    private static final int INDEX_JSON_DATA_NUMBER_OF_CARPARK = 0;
    private static final int INDEX_JSON_DATA_LIST_OF_CARPARK = 1;

    private static final String EMPTY_VALUE = "";
    private static final String NO_INFORMATION = "No Information";

    public Parser(){}

    /**
     *  Get coordinate (latitude, longitude) value from JSON which is from Google API
     * **/
    public static Coordinate parseCoordinateFromJson(String value) throws JSONException{
        try{
            Coordinate mCoord;
            JSONObject obj = new JSONObject(value);
            String lat = obj.getString("lat");
            String lng = obj.getString("long");

            mCoord = new Coordinate(lat, lng);
            return mCoord;
        }catch(Exception e){
            throw new JSONException("Unable to parse coordinate from the given json string");
        }
    }

    /**
     *  Get list of carparks from JSON which is from HDB API
     * **/
    public static CarparkList parseCarparkListFromJson(String value) throws JSONException {

        CarparkList mCarparkList = CarparkList.getInstance(); // get the carparklist instance
        mCarparkList.removeAll(); // clear all last search

        JSONObject parentObj = new JSONObject(value);
        JSONObject data =  parentObj.getJSONObject("data");
        JSONArray dataListOfCarpark = data.getJSONArray("listOfCarpark");

        for(int i=0; i < dataListOfCarpark.length(); i++){
            JSONObject obj = dataListOfCarpark.getJSONObject(i);
            String address = obj.getString("address");
            //String neighbor = obj.getString("neighborhood");
            String lat = obj.getString("lat");
            String lng = obj.getString("long");
            String cpkNum = obj.getString("cpkNum");
            String cpkColor = obj.getString("cpkFlag");
            String cpkAvailableString = obj.getString("cpkAvailable");

            int cpkAvailableNum = -1;

            // if available carpark is NA, it will be -1
            // else will be >= 0
            if(isValueNumberic(cpkAvailableString)){
                cpkAvailableNum = convertStringToInteger(cpkAvailableString);
            }

            Carpark cp = new Carpark(cpkNum, lng, lat);
            cp.setAvailableLot(cpkAvailableNum);
            cp.setColor(cpkColor);
            cp.setAddress(address);
            //cp.setNeighborhood(neighbor);
            mCarparkList.addNewCarpark(cp);
        }

        //System.out.println("mCarparkList.getSize() - " + mCarparkList.getSize());
        //for(Carpark c : mCarparkList.getList()){
        //    System.out.println(c.getID() + " - " + c.getLongitude() + " - " + c.getLatitude());
        //}

        return mCarparkList;

    }

    public static Carpark parseCarparkFromJSON(String value, String findID) throws JSONException {
        JSONObject parentObj = new JSONObject(value);
        JSONObject data =  parentObj.getJSONObject("data");
        JSONArray dataListOfCarpark = data.getJSONArray("listOfCarpark");

        for(int i=0; i < dataListOfCarpark.length(); i++) {
            JSONObject obj = dataListOfCarpark.getJSONObject(i);

            String address = obj.getString("address");
            String lat = obj.getString("lat");
            String lng = obj.getString("long");
            String cpkNum = obj.getString("cpkNum");
            String cpkColor = obj.getString("cpkFlag");
            String cpkAvailableString = obj.getString("cpkAvailable");
            int cpkAvailableNum = -1;

            if(cpkNum.equalsIgnoreCase(findID)){ //the carpark which is wanted to find is found
                // if available carpark is NA, it will be -1
                // else will be >= 0
                if(isValueNumberic(cpkAvailableString)){
                    cpkAvailableNum = convertStringToInteger(cpkAvailableString);
                }

                Carpark cp = new Carpark(cpkNum, lng, lat);
                cp.setAvailableLot(cpkAvailableNum);
                cp.setColor(cpkColor);
                cp.setAddress(address);

                return cp;
            }

        }

        return null;
    }


    /**
     *  Get coordinate from JSON
     * **/
    public static Coordinate parseSearchInputFromJSON(String value) throws JSONException{
        JSONObject parentObj = new JSONObject(value);
        JSONObject data =  parentObj.getJSONObject("data");
        JSONObject searchInput = data.getJSONObject("searchInput");

        String lng = searchInput.getString("long");
        String lat = searchInput.getString("lat");
        Coordinate mCoordinate = new Coordinate(lat, lng);

        //System.out.println("searchInput - " + lng + ", " + lat);

        return mCoordinate;
    }

    public static Carpark parseCarparkDetailFromJSON(Carpark cp, String value) throws  JSONException {
        JSONObject parentObj = new JSONObject(value);
        JSONObject data =  parentObj.getJSONObject("data");

        if(isValueCorruptedOrEmpty(data.getString("cpkFreeParkingScheme"))){
            cp.setFreeParkingScheme(NO_INFORMATION);
        }else{
            cp.setFreeParkingScheme(data.getString("cpkFreeParkingScheme"));
        }

        if(isValueCorruptedOrEmpty(data.getString("cpkType"))){
            cp.setType(NO_INFORMATION);
        }else{
            cp.setType(data.getString("cpkType"));
        }

        if(isValueCorruptedOrEmpty(data.getString("cpkClearanceHeight"))){
            cp.setClearanceHeight(NO_INFORMATION);
        }else{
            cp.setClearanceHeight(data.getString("cpkClearanceHeight"));
        }

        if(isValueCorruptedOrEmpty(data.getString("cpkNightParkingScheme"))){
            cp.setNightParkingScheme(NO_INFORMATION);
        }else{
            cp.setNightParkingScheme(data.getString("cpkNightParkingScheme"));
        }

        if(isValueCorruptedOrEmpty(data.getString("cpkParkAndRide"))){
            cp.setParkAndRideScheme(NO_INFORMATION);
        }else{
            cp.setParkAndRideScheme(data.getString("cpkParkAndRide"));
        }

        if(isValueCorruptedOrEmpty(data.getString("cpkNumOfDeck")) || !Parser.isValueNumberic(data.getString("cpkNumOfDeck"))){
            cp.setNumofDeck(0);
        }else{
            cp.setNumofDeck(Parser.convertStringToInteger(data.getString("cpkNumOfDeck")));
        }

        if(isValueCorruptedOrEmpty(data.getString("neighborhood"))){
            cp.setNeighborhood(EMPTY_VALUE);
        }else{
            cp.setNeighborhood(data.getString("neighborhood"));
        }

        return cp;
    }


    public static String encodeURLString(String raw){
        return Uri.encode(raw);
    }


    private static boolean isValueNumberic(String value){
        try {
            int d = Integer.parseInt(value);
        } catch(NumberFormatException e) {
            return false;
        }
        return true;
    }

    public static boolean isValueCorruptedOrEmpty(String value){
        if(value.equals("[]") || value.equals("") || value.isEmpty()){
            return true;
        }
        return false;
    }

    public static String convertIntegerToString(int value){
        return String.valueOf(value);
    }

    public static int convertStringToInteger(String value){
        return Integer.parseInt(value);
    }

    public static String convertDoubleToString(Double d){
        return String.valueOf(d);
    }

    public static double convertStringToDouble(String value){
        return Double.valueOf(value);
    }

    public static String toTitleCase(final String init) {
        if (init==null)
            return null;

        final StringBuilder ret = new StringBuilder(init.length());

        for (final String word : init.split(" ")) {
            if (!word.isEmpty()) {
                ret.append(word.substring(0, 1).toUpperCase());
                ret.append(word.substring(1).toLowerCase());
            }
            if (!(ret.length()==init.length()))
                ret.append(" ");
        }

        return ret.toString();
    }


}
