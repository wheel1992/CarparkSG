package carpark.sg.com.model;

import android.support.annotation.NonNull;

import com.google.android.gms.maps.model.Marker;

import java.util.HashMap;
import java.util.Map;


/**
 * Created by joseph on 6/5/2015.
 */
public class MarkerCarparkMap {

    private Map<Marker, Carpark> mMap;
    private Marker _marker;
    private Carpark _carpark;

    public MarkerCarparkMap() {}

    public MarkerCarparkMap(Marker m, Carpark cp){
        this.add(m, cp);
    }

    public void add(Marker m, Carpark cp){
        if(mMap == null){
            mMap = new HashMap<Marker, Carpark>();
        }
        mMap.put(m, cp);
    }

    public Carpark searchCarpark(Marker m){
        return mMap.get(m);
    }

}
