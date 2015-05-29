package carpark.sg.com.carparksg.logic;

import android.util.Log;

/**
 * Created by joseph on 28/5/2015.
 */
public class CustomLogging {

    private static CustomLogging mLog = new CustomLogging();

    public static CustomLogging getInstance(){
        return mLog;
    }

    private CustomLogging(){}

    public void debug(String tag, String msg){
        Log.d(tag, msg);
    }

    public void info(String tag, String msg){
        Log.i(tag, msg);
    }

    public void error(String tag, String msg){
        Log.e(tag, msg);
    }
}
