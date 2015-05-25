package carpark.sg.com.model;

import org.json.JSONException;
import org.json.JSONObject;

import carpark.sg.com.carparksg.logic.Parser;

/**
 * Created by joseph on 25/5/2015.
 */
public class Setting {

    private static Setting mSetting = new Setting();
    private int _radius;

    private Setting(){}

    public static Setting getInstance(){
        return mSetting;
    }

    public static void parseJsonToSetting(String value) throws JSONException {
        //{"radius":"xxx", "yyy":"xxx", "yyy","...", "yyy":"..."}
        if(!Parser.isValueCorruptedOrEmpty(value)){
            JSONObject obj = new JSONObject(value);
            int radius = obj.getInt("radius");

            mSetting.setRadius(radius);

        }else{
            // means there is no value stored in preference
            mSetting.setRadius(Constant.DEFAULT_SETTING_RADIUS);

        }
    }

    public static String parseSettingToJson() throws JSONException {
        JSONObject obj = new JSONObject();

        //put every setting element into json object
        obj.put("radius", mSetting.getRadius());

        //return object string
        return obj.toString();
    }




    public int getRadius(){
        return this._radius;
    }

    public void setRadius(int val){
        this._radius = val;
    }



}
