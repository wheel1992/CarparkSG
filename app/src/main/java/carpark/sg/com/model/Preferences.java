package carpark.sg.com.model;

import android.content.Context;
import android.content.SharedPreferences;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;

/**
 * Created by joseph on 9/5/2015.
 */
public class Preferences {

    private static Preferences mPreferences;
    private static Context mContext;

    public static Preferences getInstance(Context context){
        mContext = context;
        initPreferences();
        return mPreferences;
    }

    private static void initPreferences(){
        if(mPreferences == null){
            mPreferences = new Preferences();
        }
    }

    // ========= Get methods =============
    public String getHistory() throws FileNotFoundException, IOException{
        //Read text from file
        String value = this.getStringFromFile(Constant.HISTORY_FILE_NAME);
        return value;
    }

    public String getFavourite() throws FileNotFoundException, IOException{
        String value = this.getStringFromFile(Constant.FAVOURITE_FILE_NAME);
        return value;
    }

    public String getSetting() throws FileNotFoundException, IOException{
        String value = "";
        value = this.getStringFromFile(Constant.SETTING_FILE_NAME);
        return value;
    }

    // ========= Set methods =============
    public void setHistory(String history) throws FileNotFoundException, IOException{
        this.setStringToFile(Constant.HISTORY_FILE_NAME, history);
    }

    public void setFavourite(String favourite) throws FileNotFoundException, IOException{
        this.setStringToFile(Constant.FAVOURITE_FILE_NAME, favourite);
    }

    public void setSetting(String setting) throws FileNotFoundException, IOException{
        this.setStringToFile(Constant.SETTING_FILE_NAME, setting);
    }


    private String getStringFromFile(String fileName) throws FileNotFoundException, IOException{
        StringBuilder sb = new StringBuilder();
        String line;
        FileInputStream fis = mContext.openFileInput(fileName);
        BufferedReader br = new BufferedReader(new InputStreamReader(fis));
        while ((line = br.readLine()) != null) {
            sb.append(line);
        }
        br.close();
        return sb.toString();
    }

    private void setStringToFile(String fileName, String value) throws FileNotFoundException, IOException{
        FileOutputStream fos = null;
        File mFile = new File(fileName);
        fos = mContext.openFileOutput(fileName, Context.MODE_PRIVATE);
        fos.write(value.getBytes());
        fos.flush();
        fos.close();

    }




}
