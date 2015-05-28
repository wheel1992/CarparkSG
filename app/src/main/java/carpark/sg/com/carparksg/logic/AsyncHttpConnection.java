package carpark.sg.com.carparksg.logic;

import android.os.AsyncTask;

import org.json.JSONException;

import java.io.IOException;
import java.io.InputStream;

import carpark.sg.com.model.Constant;
import carpark.sg.com.model.Coordinate;

/**
 * Created by joseph on 4/5/2015.
 */
public class AsyncHttpConnection extends AsyncTask<String, Void, String> {

    InputStream urlStream = null;
    String urlAddress = "";

    @Override
    final protected String doInBackground(String... params) {

        String result = "";
        int typeOfQuery = Integer.parseInt(params[0]);

        try{
            /*
            * params[0] - type of query
            * type
            * 1 - search carpark availability using address (params[1])
            * params[1] - address
            * params[2] - radius
            *
            * 2 - search carpark availability using coordinate (params[1])
            * params[1] - latitude
            * params[2] - longitude
            * params[3] - radius
            *
            * 3 - search carpark detail (params[1])
            * params[1] - latitude
            * params[2] - longitude
            * params[3] - carpark number e.g. BJ39
            * */
            System.out.println("typeOfQuery - " + typeOfQuery);

            switch (typeOfQuery){
                case 1:
                        result = searchCarparkAvailabilityByAddress(params[1]);
                        Coordinate mCoordinate = Parser.parseCoordinateFromJson(result);
                        result = searchCarparkAvailabilityByCoordinate(mCoordinate.getLatitude(), mCoordinate.getLongitude(), params[2]);
                    break;

                case 2:
                    result = searchCarparkAvailabilityByCoordinate(params[1], params[2], params[3]);
                    break;

                case 3:
                    result = searchCarparkDetailByNumber(params[1], params[2], params[3]);
                    break;

            }//end switch

            return result;

        }catch(IOException e){
            //print error
            e.printStackTrace();

        }catch(JSONException e){
            //print error
            e.printStackTrace();
        }

        return "";

    }

    private String searchCarparkAvailabilityByAddress(String address) throws IOException{
        String dummyAddress = Constant.DUMMY_ADDRESS;
        urlAddress = Constant.URL_GEOCODE_HTTP_ADDRESS_QUERY.replace(dummyAddress, Parser.encodeURLString(address));
        urlStream = WebConnector.downloadUrl(urlAddress); //get coordinate from geocode
        return  WebConnector.convertStreamToString(urlStream);
    }

    private String searchCarparkAvailabilityByCoordinate(String lat, String lng, String radius) throws IOException{
        String dummyLatitude = Constant.DUMMY_LATITUDE;
        String dummyLongitude = Constant.DUMMY_LONGITUDE;
        String dummyRadius = Constant.DUMMY_RADIUS;
        urlAddress = Constant.URL_HDB_HTTP_CARPARK_AVAILABILITY_QUERY.replace(dummyLatitude, Parser.encodeURLString(lat))
                                                                    .replace(dummyLongitude, Parser.encodeURLString(lng))
                                                                    .replace(dummyRadius, Parser.encodeURLString(radius));

        System.out.println("AsyncHttpConnection - URL -> " + urlAddress);

        urlStream = WebConnector.downloadUrl(urlAddress); //get list of carparks
        return  WebConnector.convertStreamToString(urlStream);
    }

    private String searchCarparkDetailByNumber(String lat, String lng, String cpkNum) throws IOException{
        String dummyLatitude = Constant.DUMMY_LATITUDE;
        String dummyLongitude = Constant.DUMMY_LONGITUDE;
        String dummyNumber = Constant.DUMMY_NUMBER;
        urlAddress = Constant.URL_HDB_HTTP_CARPARK_DETAIL_QUERY.replace(dummyLatitude, Parser.encodeURLString(lat))
                                                                        .replace(dummyLongitude, Parser.encodeURLString(lng))
                                                                        .replace(dummyNumber, cpkNum);
        urlStream = WebConnector.downloadUrl(urlAddress); //get carpark details
        return  WebConnector.convertStreamToString(urlStream);
    }
}
