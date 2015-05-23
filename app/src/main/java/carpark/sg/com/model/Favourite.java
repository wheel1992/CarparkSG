package carpark.sg.com.model;

/**
 * Created by joseph on 11/5/2015.
 */
public class Favourite extends History {

    private String _id; //e.g. BJ39

    public Favourite(String id, String name, String lat, String lng){
        super(name, lat, lng);
        this._id = id;
    }

    public String getID(){
        return this._id;
    }
}
