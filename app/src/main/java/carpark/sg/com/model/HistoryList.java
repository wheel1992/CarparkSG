package carpark.sg.com.model;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import carpark.sg.com.carparksg.logic.Parser;

/**
 * Created by joseph on 9/5/2015.
 */
public class HistoryList {

    private static HistoryList mHistoryList = new HistoryList();
    private List<History> mList;
    private String mHistoryJSONString;

    // keep carpark name, lat, long, id

    public static HistoryList getInstance(){
        initHistoryList();
        mHistoryList.initList();
        return mHistoryList;
    }

    private static void initHistoryList(){
        if(mHistoryList == null){
            mHistoryList = new HistoryList();
        }
    }

    /**
     * This method will parse the history string value into list of carpark
     * The string will be in json
     * **/
    public void parseStringToList(String historyValue) throws JSONException{
        //{{"id":"xxx", "name":"xxx", "lat","...", "long":"..."}, {...}, {...}}
        System.out.println("parseStringToList");
        if(!Parser.isValueCorruptedOrEmpty(historyValue)){
            JSONArray array = new JSONArray(historyValue);
            for(int i=0; i < array.length(); i++) {
                JSONObject obj = array.getJSONObject(i);
                String name = obj.getString("name");
                String lat = obj.getString("lat");
                String lng = obj.getString("long");

                History element = new History(name, lat, lng);
                mList.add(element);

            }
        }
        System.out.println("HistoryList mList size - " + mList.size());
    }

    public String parseListToString() throws JSONException{
        String value = "";
        JSONArray array = new JSONArray();

        for(History element : this.mList){
            JSONObject obj = new JSONObject();
            obj.put("name", element.getName());
            obj.put("lat", element.getLatitude());
            obj.put("long", element.getLongitude());

            array.put(obj);
        }

        return array.toString();
    }

    public void addHistory(History element){
        if(!isExistInList(element)){
            this.mList.add(element);
        }
    }

    public void removeHistory(History element){
        Iterator<History> iter = this.mList.iterator();
        while (iter.hasNext()) {
            History e = iter.next();
            if(e.getName().toLowerCase().equals(element.getName().toLowerCase()) && e.getLatitude().equals(element.getLatitude()) && e.getLongitude().equals(element.getLongitude())){
                iter.remove();
            }
        }
    }

    public void removeAllHistory(){
        this.mList = new ArrayList<History>();
    }

    public List<History> getList(){
        return this.mList;
    }

    public int getSize(){
        return this.mList.size();
    }

    private boolean isExistInList(History element){
        for(History e : this.mList){
            if(e.getName().toLowerCase().equals(element.getName().toLowerCase())){
                return true;
            }
        }
        return false;
    }



    private void initList(){
        if(mList == null){
            mList = new ArrayList<History>();
        }
    }





}
