package carpark.sg.com.carparksg.controller;

import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

import carpark.sg.com.carparksg.R;
import carpark.sg.com.carparksg.logic.Parser;
import carpark.sg.com.model.EnumSetting;
import carpark.sg.com.model.Favourite;

/**
 * Created by joseph on 12/5/2015.
 */
public class RecyclerViewSettingAdapter extends RecyclerView.Adapter<RecyclerViewSettingAdapter.SettingHolder>  {
    private OnItemClickListener mItemClickListener;
    private MainActivity mMainActivity;
    private HashMap<EnumSetting, String> mSettingTitleValueMap;
    private ArrayList<EnumSetting> arrayTitle;
    private ArrayList<String> arraySubtitle;

    public class SettingHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private TextView txtTitle;
        private TextView txtSubtitle;

        public SettingHolder(View v){
            super(v);
            v.setOnClickListener(this);
            this.txtTitle = (TextView) v.findViewById(R.id.text_setting_list_title);
            this.txtSubtitle = (TextView) v.findViewById(R.id.text_setting_list_subtitle);
        }

        public void setTextViewTitle(String value){
            this.txtTitle.setText(value);
        }

        public void setTextViewSubTitle(String value){
            this.txtSubtitle.setText(value);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener != null){
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    //default constructor
    public RecyclerViewSettingAdapter(MainActivity activity, HashMap<EnumSetting, String> mMap) {
        if(mMap == null){
            this.mSettingTitleValueMap = new HashMap<EnumSetting, String>();
        }else{
            this.mSettingTitleValueMap = new HashMap<EnumSetting, String>(mMap);
        }

        this.mMainActivity = activity;
        this.arrayTitle = this.hashMapToArrayListTitle();
        this.arraySubtitle = this.hashMapToArrayListSubtitle();
    }

    @Override
    public SettingHolder onCreateViewHolder(ViewGroup parent, int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_setting_list_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        SettingHolder vh = new SettingHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(SettingHolder holder, int position){
        if(this.mSettingTitleValueMap != null){

            final String title = this.getTitleFromEnumSetting(this.arrayTitle.get(position));
            final String subtitle = Parser.parseDistanceToString(Parser.convertStringToInteger(this.arraySubtitle.get(position)));

            holder.setTextViewTitle(title);
            holder.setTextViewSubTitle(subtitle);
        }
    }

    @Override
    public int getItemCount() {
        return this.mSettingTitleValueMap.size();
    }


    private ArrayList<EnumSetting> hashMapToArrayListTitle(){
        ArrayList<EnumSetting> arr = new ArrayList<EnumSetting>();
        Iterator it = this.mSettingTitleValueMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            arr.add((EnumSetting)pair.getKey());
        }
        return arr;
    }

    private ArrayList<String> hashMapToArrayListSubtitle(){
        ArrayList<String> arr = new ArrayList<String>();
        Iterator it = this.mSettingTitleValueMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            arr.add(pair.getValue().toString());
        }
        return arr;
    }

    private String getTitleFromEnumSetting(EnumSetting e){
        String title = "";
        switch (e){
            case SETTING_RADIUS:
                title = "Search for carparks within";
                break;
        }
        return title;
    }


    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener listener){
        this.mItemClickListener = listener;
    }




}
