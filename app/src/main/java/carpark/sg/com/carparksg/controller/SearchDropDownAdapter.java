package carpark.sg.com.carparksg.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Filter;

import java.util.ArrayList;
import java.util.List;


import carpark.sg.com.carparksg.R;
import carpark.sg.com.model.History;
import carpark.sg.com.model.HistoryList;

/**
 * Created by joseph on 9/5/2015.
 */
public class SearchDropDownAdapter extends ArrayAdapter<History> {

    private MainActivity mMainActivity;
    private List<History> mFullList;
    private List<History> mFilterList;

    public SearchDropDownAdapter(Context context, int textViewResourceId, List<History> obj, MainActivity activity){
        super(context, textViewResourceId, obj);
        this.mFullList = obj;
        this.mFilterList = new ArrayList<History>(mFullList);
        this.mMainActivity = activity;
    }

    public void updateList(List<History> list){
        mFullList = new ArrayList<History>(list);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.dropdown_list_row_layout, null); // inflate custom list view row layout
        }

        ViewHolder holder = new ViewHolder(v);
        final String name = mFullList.get(position).getName();
        final String lat = mFullList.get(position).getLatitude();
        final String lng = mFullList.get(position).getLongitude();

        holder.setTextName(name);
        holder.setImageDelete(R.drawable.ic_close_grey);
        holder.getImageDelete().setOnClickListener(new View.OnClickListener(){
            @Override
            public void onClick(View v) {
                mMainActivity.removeHistory(name, lat, lng);
                mMainActivity.saveHistoryList();
                mMainActivity.clearHistoryList();
                mMainActivity.populateHistoryList();
                mFullList = mMainActivity.getHistoryList().getList();
                mMainActivity.refreshSearchAdapter();
            }
        });

        return v;
    }

    @Override
    public int getCount(){
        return this.mFullList.size();
    }

    @Override
    public History getItem(int position) {
        if(this.mFullList != null){
            return this.mFullList.get(position);
        }else {
            if (this.mFilterList != null) {
                return this.mFilterList.get(position);
            }else{
                return new History();
            }
        }

    }

    @Override
    public Filter getFilter() {
        return new Filter() {
            @Override
            protected FilterResults performFiltering(CharSequence constraint) {

                FilterResults results = new FilterResults();

                if(mFilterList == null){
                    mFilterList = new ArrayList<History>(mFullList);
                }

                if(constraint == null || constraint.length() == 0){
                    results.values = mFullList;
                    results.count = mFullList.size();
                }else{
                    String constraintString = constraint.toString().toLowerCase();
                    List<History> values = mFilterList;
                    int count = values.size();

                    List<History> newValues = new ArrayList<History>(count);

                    for(History element : values){
                        if(element.getName().toLowerCase().contains(constraintString)){
                            newValues.add(element);
                        }
                    }

                    results.values = newValues;
                    results.count = newValues.size();

                }
                return results;
            }

            @Override
            protected void publishResults(CharSequence constraint,
                                          FilterResults results) {

                if(results.values != null){
                    mFullList = (ArrayList<History>) results.values;
                }else{
                    mFullList = new ArrayList<History>();
                }

                if (results.count > 0) {
                    notifyDataSetChanged();
                } else {
                    notifyDataSetInvalidated();
                }
            }

        };
    }

    private class ViewHolder{
        private ImageView _imageDelete;
        private TextView _textName;
        public ViewHolder(View v){
            this._imageDelete = (ImageView) v.findViewById(R.id.image_search_list_delete);
            this._textName = (TextView) v.findViewById(R.id.text_search_list_name);
        }

        public ImageView getImageDelete(){
            return this._imageDelete;
        }

        public void setTextName(String value){
            this._textName.setText(value);
        }

        public void setImageDelete(int id){
            this._imageDelete.setImageResource(id);
        }



    }//end ViewHolder


}
