package carpark.sg.com.carparksg.controller;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.Marker;

import java.util.Map;

import carpark.sg.com.carparksg.R;
import carpark.sg.com.carparksg.logic.Parser;
import carpark.sg.com.model.Carpark;
import carpark.sg.com.model.MarkerCarparkMap;

/**
 * Created by joseph on 6/5/2015.
 */
public class MarkerInfoWindowAdapter implements GoogleMap.InfoWindowAdapter {

    private MarkerCarparkMap mMap;
    private ViewGroup parent;
    private ViewHolder mHolder;

    private final String NOT_AVAILABLE_STRING = "not available";

    public MarkerInfoWindowAdapter(ViewGroup vg, MarkerCarparkMap map) {
        this.parent = vg;
        this.mMap = map;
    }


    @Override
    public View getInfoWindow(Marker marker) {
        return null;
    }

    @Override
    public View getInfoContents(Marker marker) {
        View v  = LayoutInflater.from(parent.getContext()).inflate(R.layout.marker_infowindow, null);

        if(mMap == null){
            return v;
        }

        Carpark mCarpark = mMap.searchCarpark(marker);
        if(mCarpark == null){
            return v;
        }

        this.mHolder = new ViewHolder(v);
        this.mHolder.setTxtInfoWindowAddress(mCarpark.getAddress());
        if(mCarpark.getAvailableLot() == -1){
            this.mHolder.setTxtInfoWindowLot(NOT_AVAILABLE_STRING);
        }else{
            this.mHolder.setTxtInfoWindowLot(Parser.convertIntegerToString(mCarpark.getAvailableLot()));
        }

        return v;
    }


    private class ViewHolder{
        private static final String AVAILABLE_LOT_STRING = "Available Lot: ";
        private TextView txtInfoWindowAddress;
        private TextView txtInfoWindowLot;
        private ImageView imgInfoWindow;

        public ViewHolder(View v){
            this.imgInfoWindow = (ImageView) v.findViewById(R.id.infowindow_image);
            this.txtInfoWindowAddress = (TextView) v.findViewById(R.id.infowindow_text_address);
            this.txtInfoWindowLot = (TextView) v.findViewById(R.id.infowindow_text_lot);
        }

        public void setTxtInfoWindowAddress(String address){
            this.txtInfoWindowAddress.setText(address);
        }

        public void setTxtInfoWindowLot(String value){
            this.txtInfoWindowLot.setText(AVAILABLE_LOT_STRING + value);
        }

    }


}
