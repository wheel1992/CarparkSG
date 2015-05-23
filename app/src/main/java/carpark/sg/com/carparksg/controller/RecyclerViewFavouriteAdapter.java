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
import carpark.sg.com.model.Favourite;

/**
 * Created by joseph on 12/5/2015.
 */
public class RecyclerViewFavouriteAdapter extends RecyclerView.Adapter<RecyclerViewFavouriteAdapter.FavouriteHolder>  {
    private OnItemClickListener mItemClickListener;
    private MainActivity mMainActivity;
    private FragmentFavourite mFragmentFavourite;
    private HashMap<String, Favourite> mFavouriteMap;
    private ArrayList<Favourite> mFavouriteArray;

    public class FavouriteHolder extends RecyclerView.ViewHolder implements View.OnClickListener {

        private ImageView imgIcon;
        private ImageView imgDelete;
        private TextView txtPrimary;

        public FavouriteHolder(View v){
            super(v);
            v.setOnClickListener(this);
            this.imgIcon = (ImageView) v.findViewById(R.id.image_favourite_icon);
            this.imgDelete = (ImageView) v.findViewById(R.id.image_favourite_delete);
            this.txtPrimary = (TextView) v.findViewById(R.id.text_favourite_value);
        }

        public ImageView getImageDelete(){
            return this.imgDelete;
        }

        public void setImageView(int id){
            switch(id){
                case R.drawable.ic_list_favourite_2:
                    this.imgIcon.setImageResource(id);
                    break;

                case R.drawable.ic_close_grey:
                    this.imgDelete.setImageResource(id);
                    break;
            }
        }

        public void setTextViewPrimary(String value){
            this.txtPrimary.setText(value);
        }

        @Override
        public void onClick(View v) {
            if(mItemClickListener != null){
                mItemClickListener.onItemClick(v, getAdapterPosition());
            }
        }
    }

    //default constructor
    public RecyclerViewFavouriteAdapter(MainActivity activity, FragmentFavourite frag, HashMap<String, Favourite> mMap) {
        if(mMap == null){
            this.mFavouriteMap = new HashMap<String, Favourite>();
        }else{
            this.mFavouriteMap = new HashMap<String, Favourite>(mMap);
        }

        this.mFavouriteArray = new ArrayList<Favourite>();
        this.hashMapToArrayList();
        this.mMainActivity = activity;
        this.mFragmentFavourite = frag;
    }

    @Override
    public RecyclerViewFavouriteAdapter.FavouriteHolder onCreateViewHolder(ViewGroup parent,
                                                                             int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_favourite_list_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        FavouriteHolder vh = new FavouriteHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(FavouriteHolder holder, int position){
        if(this.mFavouriteArray != null){
            final String id = this.mFavouriteArray.get(position).getID();
            final String name = this.mFavouriteArray.get(position).getName();
            final String lat = this.mFavouriteArray.get(position).getLatitude();
            final String lng = this.mFavouriteArray.get(position).getLongitude();

            holder.setTextViewPrimary(name);
            holder.setImageView(R.drawable.ic_list_favourite_2);
            holder.setImageView(R.drawable.ic_close_grey);
            holder.getImageDelete().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View view) {
                    mMainActivity.removeFavourite(id, name, lat, lng);
                    mMainActivity.saveFavouriteList();
                    mMainActivity.clearFavouriteList();
                    mMainActivity.populateFavouriteList();
                    mFragmentFavourite.refreshFavouriteAdapter();
                    mFavouriteArray = mFragmentFavourite.getFavouriteList().getList();
                }
            });

        }else{

        }
    }

    @Override
    public int getItemCount() {
        return this.mFavouriteArray.size();
    }


    private void hashMapToArrayList(){
        Iterator it = this.mFavouriteMap.entrySet().iterator();
        while (it.hasNext()) {
            Map.Entry pair = (Map.Entry) it.next();

            mFavouriteArray.add((Favourite)pair.getValue());
        }
    }

    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener listener){
        this.mItemClickListener = listener;
    }



}
