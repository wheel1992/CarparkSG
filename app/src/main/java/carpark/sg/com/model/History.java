package carpark.sg.com.model;

/**
 * Created by joseph on 9/5/2015.
 */
public class History {

    private String _name;
    private String _lat;
    private String _lng;

    public History() {}
    public History(String name, String lat, String lng){
        this._name = name;
        this._lat = lat;
        this._lng = lng;
    }

    public String getName(){
        return this._name;
    }

    public String getLatitude(){
        return this._lat;
    }

    public String getLongitude(){
        return this._lng;
    }

}
