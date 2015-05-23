package carpark.sg.com.carparksg.controller;

import android.support.v7.internal.widget.AdapterViewCompat;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.List;

import carpark.sg.com.carparksg.R;
import carpark.sg.com.model.History;

/**
 * Created by joseph on 12/5/2015.
 */
public class RecyclerViewRecentAdapter extends RecyclerView.Adapter<RecyclerViewRecentAdapter.RecentHolder>  {
    private OnItemClickListener mItemClickListener;
    private MainActivity mMainActivity;
    private FragmentRecent mFragmentRecent;
    private List<History> mRecentArray;



    public class RecentHolder extends RecyclerView.ViewHolder implements View.OnClickListener{
        private ImageView imgIcon;
        private ImageView imgDelete;
        private TextView txtPrimary;

        public RecentHolder(View v){
            super(v);
            v.setOnClickListener(this);
            this.imgIcon = (ImageView) v.findViewById(R.id.image_recent_icon);
            this.imgDelete = (ImageView) v.findViewById(R.id.image_recent_delete);
            this.txtPrimary = (TextView) v.findViewById(R.id.text_recent_value);
        }

        public ImageView getImageDelete(){
            return this.imgDelete;
        }

        public void setImageView(int id){
            switch(id){
                case R.drawable.ic_list_recent_2:
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
    public RecyclerViewRecentAdapter(MainActivity activity, FragmentRecent frag, List<History> array) {
        this.mMainActivity = activity;
        this.mFragmentRecent = frag;
        this.mRecentArray = new ArrayList<History>(array);
    }

    @Override
    public RecyclerViewRecentAdapter.RecentHolder onCreateViewHolder(ViewGroup parent,
                                                                           int viewType) {
        // create a new view
        View v = LayoutInflater.from(parent.getContext()).inflate(R.layout.recycler_recent_list_row_layout, parent, false);
        // set the view's size, margins, paddings and layout parameters
        RecentHolder vh = new RecentHolder(v);
        return vh;
    }

    @Override
    public void onBindViewHolder(RecentHolder holder, int position){
        if(this.mRecentArray != null){
            final String name = this.mRecentArray.get(position).getName();
            final String lat = this.mRecentArray.get(position).getLatitude();
            final String lng = this.mRecentArray.get(position).getLongitude();

            holder.setTextViewPrimary(name);
            holder.setImageView(R.drawable.ic_list_recent_2);
            holder.setImageView(R.drawable.ic_close_grey);
            holder.getImageDelete().setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    mMainActivity.removeHistory(name, lat, lng);
                    mMainActivity.saveHistoryList();
                    mMainActivity.clearHistoryList();
                    mMainActivity.populateHistoryList();
                    mMainActivity.refreshSearchAdapter();
                    mFragmentRecent.refreshRecentAdapter();
                    mRecentArray = mFragmentRecent.getHistoryList().getList();
                }
            });

        }
    }

    @Override
    public int getItemCount() {
        return this.mRecentArray.size();
    }


    public interface OnItemClickListener {
        public void onItemClick(View view, int position);
    }

    public void setOnItemClickListener(final OnItemClickListener listener){
        this.mItemClickListener = listener;
    }


}
