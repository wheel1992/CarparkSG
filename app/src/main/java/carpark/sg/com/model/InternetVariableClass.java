package carpark.sg.com.model;

/**
 * Created by joseph on 28/5/2015.
 */
public class InternetVariableClass {
    private boolean wifi;
    private boolean data;

    public InternetVariableClass(){
    }

    public InternetVariableClass(boolean hasWifi, boolean hasData){
        this.wifi = hasWifi;
        this.data = hasData;
    }

    public boolean getHasWifi(){
        return this.wifi;
    }

    public boolean getHasData(){
        return this.data;
    }

    public void setHasWifi(boolean hasWifi){
        this.wifi = hasWifi;
    }

    public void setHasData(boolean hasdata){
        this.data = hasdata;
    }

}
