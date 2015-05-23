package carpark.sg.com.carparksg.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.HashMap;
import java.util.Map;

import carpark.sg.com.carparksg.R;
import carpark.sg.com.model.Carpark;
import carpark.sg.com.model.CarparkList;

/**
 * Created by joseph on 3/5/2015.
 */
public class RecyclerViewCarparkDetailAdapter extends RecyclerView.Adapter<RecyclerViewCarparkDetailAdapter.CarparkHolder> {
    private Map<Integer, String> mapListValue;

    private final int INDEX_CARPARK_COUPON_OR_ELECTRONIC = 0;
    private final int INDEX_CARPARK_TYPE = 1;
    private final int INDEX_CARPARK_CLEARANCE_HEIGHT = 2;
    private final int INDEX_CARPARK_NIGHT_PARKING = 3;
    private final int INDEX_CARPARK_PARK_RIDE = 4;

    public class CarparkHolder extends RecyclerView.ViewHolder {

        private ImageView imgIcon;
        private TextView txtPrimary;

        public CarparkHolder(View v){
            super(v);
            this.imgIcon = (ImageView) v.findViewById(R.id.image_list_icon);
            this.txtPrimary = (TextView) v.findViewById(R.id.text_list_value);
        }

        public void setImageView(int id){
            this.imgIcon.setImageResource(id);
        }

        public void setTextViewPrimary(String value){
            this.txtPrimary.setText(value);
        }

    }//end CarparkHolder


    //default constructor
    public RecyclerViewCarparkDetailAdapter(Carpark cp) {
        if(cp != null) {

            this.mapListValue = new HashMap<Integer, String>();

            this.mapListValue.put(this.INDEX_CARPARK_COUPON_OR_ELECTRONIC, cp.getFreeParkingScheme()); //Electronic Parking or Coupon Parking
            this.mapListValue.put(this.INDEX_CARPARK_TYPE, cp.getType()); //Multi-storey Car Park or Surface Car Park
            this.mapListValue.put(this.INDEX_CARPARK_CLEARANCE_HEIGHT, cp.getClearanceHeight()); // 2.15m
            this.mapListValue.put(this.INDEX_CARPARK_NIGHT_PARKING, cp.getNightParkingScheme()); //Yes or No
            this.mapListValue.put(this.INDEX_CARPARK_PARK_RIDE, cp.getParkAndRideScheme()); //Yes or No

        }
    }

    // Create new views
    @Override
    public RecyclerViewCarparkDetailAdapter.CarparkHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_carpark_detail_list_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        CarparkHolder vh = new CarparkHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CarparkHolder holder, int position){
        if(this.mapListValue != null){
            String value = this.mapListValue.get(position);
            System.out.println("value - " + value);

            holder.setTextViewPrimary(value);

            switch(position) {
                case 0: //Electronic Parking or Coupon Parking
                    if(value.toLowerCase().contains("electronic")){
                        holder.setImageView(R.drawable.ic_list_electronic);
                    }else{
                        holder.setImageView(R.drawable.ic_list_coupon);
                    }
                    holder.setTextViewPrimary(value);
                    break;
                case 1: //Multi-storey Car Park or Surface Car Park
                    if(value.toLowerCase().contains("surface")){
                        holder.setImageView(R.drawable.ic_list_carpark_surface);
                    }else{
                        holder.setImageView(R.drawable.ic_list_carpark_multistorey);
                    }
                    holder.setTextViewPrimary(value);
                    break;
                case 2: // 2.15m
                    holder.setImageView(R.drawable.ic_list_carpark_height);
                    holder.setTextViewPrimary(value);
                    break;
                case 3: //Yes or No
                    holder.setImageView(R.drawable.ic_list_carpark_night);
                    if(isYes(value)){
                        holder.setTextViewPrimary("Night Parking Available");
                    }else{
                        holder.setTextViewPrimary("Night Parking Not Available");
                    }

                    break;
                case 4: //Yes or No
                    holder.setImageView(R.drawable.ic_list_carpark_train);
                    if(isYes(value)){
                        holder.setTextViewPrimary("Park & Ride Available");
                    }else{
                        holder.setTextViewPrimary("Park & Ride Not Available");
                    }
                    break;
            }//end switch

        }
    }

    @Override
    public int getItemCount() {
        return this.mapListValue.size();
    }

    private boolean isYes(String val){
        if(val.equalsIgnoreCase("yes")){
            return true;
        }
        return false;
    }



}


