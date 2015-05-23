package carpark.sg.com.model;

import java.io.Serializable;

/**
 * Created by joseph on 3/5/2015.
 */
public class Carpark implements Serializable {

    private String _latitude;
    private String _longitude;
    private String _color;
    private String _address;
    private String _neighborhood;
    private String _id;
    private String _type;
    private String _clearanceHeight;
    private String _shortTermParkingScheme;
    private String _nightParkingScheme;
    private String _freeParkingScheme;
    private String _parkAndRideScheme;
    private String _parkingSystem;
    private int _availableLot;
    private int _numofDeck;
    private boolean _hasDetail;
    private boolean _isFavourite;

    /**
     * Default constructor
     * **/
    public Carpark(){
        this._hasDetail = false;
    }

    public Carpark(String id, String lng, String lat){
        this._id = id;
        this._longitude = lng;
        this._latitude = lat;
        this._hasDetail = false;
        this._isFavourite = false;
    }

    /**
     * Getter
     * **/
    public String getID(){
        return this._id;
    }

    public String getLatitude(){
        return this._latitude;
    }

    public String getLongitude(){
        return this._longitude;
    }

    public String getColor(){
        return this._color;
    }

    public String getAddress(){
        return this._address;
    }

    public String getNeighborhood() {   return this._neighborhood;   };

    public String getType(){
        return this._type;
    }

    public String getClearanceHeight(){
        return this._clearanceHeight;
    }

    public String get_shortTermParkingScheme(){
        return this._shortTermParkingScheme;
    }
    public String getNightParkingScheme(){
        return this._nightParkingScheme;
    }

    public String getFreeParkingScheme(){
        return this._freeParkingScheme;
    }

    public String getParkAndRideScheme(){
        return this._parkAndRideScheme;
    }

    public String getParkingSystem(){
        return this._parkingSystem;
    }

    public int getAvailableLot(){
        return this._availableLot;
    }

    public int getNumOfDeck(){
        return this._numofDeck;
    }

    public boolean getHasDetail(){
        return this._hasDetail;
    }

    public boolean getIsFavourite(){
        return this._isFavourite;
    }
    /**
     * Setter
     * **/
    public void setLatitude(String lat){
        this._latitude = lat;
    }

    public void setLongitude(String lng){
        this._longitude = lng;
    }

    public void setColor(String color){
        this._color = color;
    }

    public void setAddress(String address){
        this._address = address;
    }

    public void setNeighborhood(String neighbor) {  this._neighborhood = neighbor;  };

    public void setType(String type){
        this._type = type;
    }

    public void setClearanceHeight(String height){
        this._clearanceHeight = height;
    }

    public void setShortTermParkingScheme(String scheme){
        this._latitude = scheme;
    }

    public void setNightParkingScheme(String scheme){
        this._nightParkingScheme = scheme;
    }

    public void setFreeParkingScheme(String scheme){
        this._freeParkingScheme = scheme;
    }

    public void setParkAndRideScheme(String scheme){
        this._parkAndRideScheme = scheme;
    }

    public void setParkingSystem(String system){
        this._parkingSystem = system;
    }

    public void setAvailableLot(int num){
        this._availableLot = num;
    }

    public void setNumofDeck(int num){
        this._numofDeck = num;
    }

    public void setHasDetail(boolean val){
        this._hasDetail = val;
    }

    public void setIsFavourite(boolean val){
        this._isFavourite = val;
    }





}
