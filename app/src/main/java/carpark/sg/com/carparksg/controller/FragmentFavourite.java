package carpark.sg.com.carparksg.controller;

import android.app.Activity;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import java.util.HashMap;

import carpark.sg.com.carparksg.R;
import carpark.sg.com.model.Constant;
import carpark.sg.com.model.Favourite;
import carpark.sg.com.model.FavouriteList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentFavourite.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentFavourite#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentFavourite extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String LOG_TAG = "Fragment Favourite";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    private MainActivity mActivity;
    private TextView txtError;

    private FavouriteList mFavouriteList;
    private HashMap<String, Favourite> mFavouriteMap;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mRecyclerLayoutManager;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentFavourite.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentFavourite newInstance(String param1, String param2) {
        FragmentFavourite fragment = new FragmentFavourite();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentFavourite() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_favourite, container, false);

        this.initFavouriteList();
        this.initRecyclerView(rootView);
        this.initTextError(rootView);
        int size = this.getFavouriteListSize();

        if(size > 0){
            Log.d(LOG_TAG, "history(s) are available.");
            this.mFavouriteMap = this.getFavouriteMap();
            this.setRecyclerFavouriteView(this.mFavouriteMap);
            this.showRecyclerFavouriteView();
            this.hideTextError();
        }else{
            Log.d(LOG_TAG, "history(s) are not available.");
            this.hideRecyclerFavouriteView();
            this.showTextError(Constant.ERROR_NO_FAVOURITE);
        }

        // Activity has already gotten favourite from the favourite file in internal storage

        return rootView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.initMainActivity((MainActivity) activity);

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException(activity.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        mListener = null;
    }

    private void initMainActivity(MainActivity activity){
        this.mActivity = activity;
    }

    private MainActivity getMainActivity(){
        return this.mActivity;
    }

    private void initFavouriteList(){
        this.mFavouriteList = FavouriteList.getInstance();
    }

    private HashMap<String, Favourite> getFavouriteMap(){
        if(this.mFavouriteList == null){
            this.initFavouriteList();
        }
        return this.mFavouriteList.getFavouriteMap();
    }

    private int getFavouriteListSize(){
        return this.mFavouriteList.getSize();
    }

    private void initRecyclerAdapter(HashMap<String, Favourite> mMap){
        mRecyclerAdapter = new RecyclerViewFavouriteAdapter(getMainActivity(), this, mMap);
        ((RecyclerViewFavouriteAdapter)mRecyclerAdapter).setOnItemClickListener(new RecyclerViewFavouriteAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String address = getFavouriteList().getList().get(position).getName();
                String lat = getFavouriteList().getList().get(position).getLatitude();
                String lng = getFavouriteList().getList().get(position).getLongitude();

                //getMainActivity().searchCarpark(Constant.SEARCH_HDB_NEARBY_CARPARK_USING_COORDINATE, address, lat, lng);
            }
        });
    }

    private void initRecyclerView(View v){
        this.mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_favourite);
    }

    private void setRecyclerFavouriteView(HashMap<String, Favourite> mMap){
        this.mRecyclerLayoutManager = new LinearLayoutManager(getMainActivity());
        this.mRecyclerView.setLayoutManager(this.mRecyclerLayoutManager);

        this.initRecyclerAdapter(mMap);
        this.mRecyclerView.setAdapter(this.mRecyclerAdapter);
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(getMainActivity(), DividerItemDecoration.VERTICAL_LIST));
    }

    private void showRecyclerFavouriteView(){
        this.mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideRecyclerFavouriteView(){
        this.mRecyclerView.setVisibility(View.GONE);
    }

    private void initTextError(View v){
        this.txtError = (TextView) v.findViewById(R.id.text_favourite_error);
    }

    private void showTextError(String error){
        this.txtError.setVisibility(View.VISIBLE);
        this.txtError.setText(error);
    }

    private void hideTextError(){
        this.txtError.setVisibility(View.GONE);
        this.txtError.setText("");
    }

    public void refreshFavouriteAdapter(){
        this.initRecyclerAdapter(this.mFavouriteMap);
        this.mRecyclerView.setAdapter(null);
        this.mRecyclerView.setAdapter(this.mRecyclerAdapter);
    }

    public FavouriteList getFavouriteList(){
        return this.mFavouriteList;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p/>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentFavouriteInteraction(Uri uri);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentFavouriteInteraction(uri);
        }
    }


}
