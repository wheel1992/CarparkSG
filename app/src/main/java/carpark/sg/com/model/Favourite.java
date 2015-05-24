package carpark.sg.com.model;

/**
 * Created by joseph on 11/5/2015.
 */
public class Favourite extends History {

    private String _id; //e.g. BJ39
    private String _neighbor;
    private int _availableLot;
    private boolean _isFavourite;

    public Favourite(String id, String name, String lat, String lng, String neighbour, int availableLot, boolean isFavourite){
        super(name, lat, lng);
        this._id = id;
        this._neighbor = neighbour;
        this._availableLot = availableLot;
        this._isFavourite = isFavourite;
    }

    public String getID(){
        return this._id;
    }

    public String getNeighborhood(){
        return this._neighbor;
    }

    public int getAvailableLot(){
        return this._availableLot;
    }

    public boolean getIsFavourite(){
        return this._isFavourite;
    }
}
