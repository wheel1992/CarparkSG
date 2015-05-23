package carpark.sg.com.model;

/**
 * Created by joseph on 4/5/2015.
 */
public class Coordinate {
    private String _lat;
    private String _lng;

    public Coordinate(String lat, String lng){
        this._lat = lat;
        this._lng = lng;
    }

    public String getLatitude(){
        return this._lat;
    }

    public String getLongitude(){
        return this._lng;
    }

}
