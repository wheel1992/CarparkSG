package carpark.sg.com.carparksg.logic;


import android.content.Context;
import android.location.Address;
import android.location.Geocoder;

import java.io.IOException;
import java.util.List;


/**
 * Created by joseph on 4/5/2015.
 */
public class GoogleLocation {

    private static Geocoder mGeocoder;
    private static final int MAX_NUMBER_OF_ADDRESS_RESULT = 10;
    private static final String COUNTRY_NAME = "singapore";
    private static final String COUNTRY_CODE = "sg";

    public static List<Address> getListOfAddresses(Context context, String address) throws IOException{
        List<Address> mList = null;
        mGeocoder = new Geocoder(context);
        mList = mGeocoder.getFromLocationName(address, MAX_NUMBER_OF_ADDRESS_RESULT);

        if(mList.size() <= 0){
            throw new IOException("No result found");
        }

        for(Address a : mList){ // remove addresses that are not within singapore
            if(!a.getCountryCode().equalsIgnoreCase(COUNTRY_CODE) && !a.getCountryName().equalsIgnoreCase(COUNTRY_NAME)){
                mList.remove(a);
            }
        }

        return mList;
    }

    public static String getLatitudeFromAddress(Address address){
        double lat = address.getLatitude();
        return convertDoubleToString(lat);
    }

    public static String getLongitudeFromAddress(Address address){
        double lng = address.getLongitude();
        return convertDoubleToString(lng);
    }

    public static String getFullStreetFromAddress(Address address){
        int numOfAddress = address.getMaxAddressLineIndex();
        String add = "";

        for(int i = 0; i< numOfAddress; i++){
            add += "" + address.getAddressLine(i);
        }

        return add;
    }



    private static String convertDoubleToString(double d){
        return Double.toString(d);
    }
}
