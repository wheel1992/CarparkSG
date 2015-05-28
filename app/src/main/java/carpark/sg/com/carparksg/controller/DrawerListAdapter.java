package carpark.sg.com.carparksg.controller;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;

import carpark.sg.com.carparksg.R;

/**
 * Created by joseph on 11/5/2015.
 */
public class DrawerListAdapter  extends ArrayAdapter<String>{

    private ArrayList<String> mListName;
    private Context mainContext;

    public DrawerListAdapter(Context context, int textViewResourceId, ArrayList<String> obj){
        super(context, textViewResourceId, obj);
        this.mainContext = context;
        this.mListName = new ArrayList<String>(obj);
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View v = convertView;

        if(v == null){
            LayoutInflater inflater = (LayoutInflater) getContext().getSystemService(Context.LAYOUT_INFLATER_SERVICE);
            v = inflater.inflate(R.layout.drawer_list_row_layout, null); // inflate custom list view row layout
        }

        ViewHolder holder = new ViewHolder(v);
        String name = this.mListName.get(position);

        holder.setListTextName(name);
        switch (position){
            case 0:
                holder.setListIcon(R.drawable.ic_list_favourite);
                break;

            case 1:
                holder.setListIcon(R.drawable.ic_list_recent);
                break;

            case 2:
                holder.setListIcon(R.drawable.ic_list_setting);
                break;

            case 3:
                holder.setListIcon(R.drawable.ic_list_info);
                break;
        }

        return v;
    }

    @Override
    public int getCount(){
        return this.mListName.size();
    }

    @Override
    public String getItem(int position) {
        if(this.mListName != null){
            return this.mListName.get(position);
        }

        return "";

    }

    private class ViewHolder{
        private ImageView _listIcon;
        private TextView _listTextName;

        public ViewHolder(View v){
            this._listIcon = (ImageView) v.findViewById(R.id.drawer_list_icon);
            this._listTextName = (TextView) v.findViewById(R.id.drawer_list_text);
        }

        public ImageView getImageDelete(){
            return this._listIcon;
        }

        public void setListTextName(String value){
            this._listTextName.setText(value);
        }

        public void setListIcon(int id){
            this._listIcon.setImageResource(id);
        }



    }//end ViewHolder


}
