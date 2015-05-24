package carpark.sg.com.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import carpark.sg.com.carparksg.logic.Parser;

/**
 * Created by joseph on 11/5/2015.
 */
public class FavouriteList {

    private static FavouriteList mFavouriteList = new FavouriteList();
    // String - address
    private HashMap<String, Favourite> mFavouriteMap;

    private FavouriteList() {}

    public static FavouriteList getInstance(){
        initFavouriteList();
        mFavouriteList.initFavouriteMap();
        return mFavouriteList;
    }

    public HashMap<String, Favourite> getFavouriteMap(){
        return this.mFavouriteMap;
    }

    public void parseStringToMap(String favouriteValue) throws JSONException {
        //{{"id":"xxx", "name":"xxx", "lat","...", "long":"..."}, {...}, {...}}
        System.out.println("parseStringToMap");
        if(!Parser.isValueCorruptedOrEmpty(favouriteValue)){
            JSONArray array = new JSONArray(favouriteValue);
            for(int i=0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String id = obj.getString("id");
                String name = obj.getString("name");
                String neighbor = obj.getString("neighbor");
                String lat = obj.getString("lat");
                String lng = obj.getString("long");
                int lot = obj.getInt("lot");
                boolean isFav = obj.getBoolean("isFavourite");

                Favourite element = new Favourite(id, name, lat, lng, neighbor, lot, isFav);
                mFavouriteMap.put(name, element);
            }
        }

    }

    public String parseMapToString() throws JSONException{
        String value = "";
        JSONArray array = new JSONArray();

        Iterator it = this.mFavouriteMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry)it.next();

            Favourite element = (Favourite)pair.getValue();

            JSONObject obj = new JSONObject();
            obj.put("id", element.getID());
            obj.put("name", element.getName());
            obj.put("lat", element.getLatitude());
            obj.put("long", element.getLongitude());
            obj.put("neighbor", element.getNeighborhood());
            obj.put("lot", element.getAvailableLot());
            obj.put("isFavourite", element.getIsFavourite());

            array.put(obj);
        }

        return array.toString();
    }

    public void addFavourite(Favourite fav){
        if(this.mFavouriteMap.get(fav.getName()) == null){ //no favourite value exist
            this.mFavouriteMap.put(fav.getName(), fav);
        }
    }

    public void removeFavourite(Favourite fav){
        if(this.mFavouriteMap.get(fav.getName()) != null) { //favourite value exist
            this.mFavouriteMap.remove(fav.getName());
        }
    }

    public void removeFavourite(String address){
        if(this.mFavouriteMap.get(address) != null){ //favourite value exist
            this.mFavouriteMap.remove(address);
        }
    }

    public void removeAllFavourite(){
        this.initFavouriteMap();
    }

    public boolean isFavouriteExist(String id, String address, String lat, String lng){
        if(this.mFavouriteMap.get(address) != null &&
                this.mFavouriteMap.get(address).getID().equals(id) &&
                this.mFavouriteMap.get(address).getLatitude().equals(lat) &&
                this.mFavouriteMap.get(address).getLongitude().equals(lng)) { //favourite value exist
            return true;
        }
        return false;
    }

    public int getSize(){
        return this.mFavouriteMap.size();
    }

    public ArrayList<Favourite> getList(){
        return new ArrayList<Favourite>(this.mFavouriteMap.values());
    }


    private static void initFavouriteList(){
        if(mFavouriteList == null){
            mFavouriteList = new FavouriteList();
        }
    }

    private void initFavouriteMap(){
        if(this.mFavouriteMap == null){
            this.mFavouriteMap = new HashMap<String, Favourite>();
        }
    }


}
