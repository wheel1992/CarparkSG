package carpark.sg.com.carparksg.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import carpark.sg.com.carparksg.R;
import carpark.sg.com.model.Carpark;
import carpark.sg.com.model.CarparkList;

/**
 * Created by joseph on 3/5/2015.
 */
public class RecyclerViewAdapter extends RecyclerView.Adapter<RecyclerViewAdapter.CarparkHolder> {
    private CarparkList mCarparkList;

    private static final String TEXTVIEW_EMPTY_STRING = "";
    private static final String TEXTVIEW_AVAILABLE_LOT_STRING = " lots left";


    public class CarparkHolder extends RecyclerView.ViewHolder {

        private TextView txtPrimary;
        private TextView txtSecondary;

        public CarparkHolder(View v){
            super(v);

            this.txtPrimary = (TextView) v.findViewById(R.id.first_line);
            this.txtSecondary = (TextView) v.findViewById(R.id.second_line);
        }

        public TextView getTextViewPrimary(){
            return this.txtPrimary;
        }
        public TextView getTextViewSecondary(){
            return this.txtSecondary;
        }

        public void setTextViewPrimary(String value){
            this.txtPrimary.setText(value);
        }
        public void setTextViewSecondary(String value){
            this.txtSecondary.setText(value);
        }

    }//end CarparkHolder

    public void add(int position, Carpark cp) {
        mCarparkList.addNewCarpark(cp);
        notifyItemInserted(position);
    }

    public void remove(String item) {
        //int position = mDataset.indexOf(item);
        //mDataset.remove(position);
        //notifyItemRemoved(position);
    }

    //default constructor
    public RecyclerViewAdapter() {
        this.mCarparkList = CarparkList.getInstance();
    }

    // Create new views
    @Override
    public RecyclerViewAdapter.CarparkHolder onCreateViewHolder(ViewGroup parent,
                                                   int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_list_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        CarparkHolder vh = new CarparkHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(CarparkHolder holder, int position){
        Carpark cp = this.mCarparkList.getCarpark(position);
        if(cp == null){
            holder.setTextViewPrimary(TEXTVIEW_EMPTY_STRING);
            holder.setTextViewSecondary(TEXTVIEW_EMPTY_STRING);
        }else{
            String address = cp.getAddress();
            int availableLot = cp.getAvailableLot();

            holder.setTextViewPrimary(address);
            holder.setTextViewSecondary(availableLot + TEXTVIEW_AVAILABLE_LOT_STRING);

        }
    }

    @Override
    public int getItemCount() {
        return mCarparkList.getSize();
    }




}


