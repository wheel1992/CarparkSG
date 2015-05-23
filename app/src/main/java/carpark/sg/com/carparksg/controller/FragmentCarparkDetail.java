package carpark.sg.com.carparksg.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.CardView;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.TextView;

import com.google.android.gms.maps.StreetViewPanorama;
import com.google.android.gms.maps.StreetViewPanoramaOptions;
import com.google.android.gms.maps.StreetViewPanoramaView;
import com.google.android.gms.maps.model.LatLng;
import com.squareup.picasso.Picasso;

import org.json.JSONException;

import carpark.sg.com.carparksg.R;
import carpark.sg.com.carparksg.logic.AsyncHttpConnection;
import carpark.sg.com.carparksg.logic.Parser;
import carpark.sg.com.model.Carpark;
import carpark.sg.com.model.CarparkList;
import carpark.sg.com.model.Constant;
import carpark.sg.com.model.Favourite;
import carpark.sg.com.model.FavouriteList;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentCarparkDetail.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentCarparkDetail#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentCarparkDetail extends Fragment {

    private static final String ARG_CARPARK = "carpark";
    private static final String ARG_LATITUDE = "latitude";
    private static final String ARG_LONGITUDE = "longitude";
    private static final String ARG_CARPARK_NUMBER = "id";
    private static final String ARG_CARPARK_ADDRESS = "address";
    private static final String ARG_CARPARK_NEIGHBORHOOD = "neighborhood";
    private static final String ARG_CARPARK_AVAILABLE_LOT = "availableLot";
    private static final String ARG_CURRENT_USER_LONGITUDE = "currentUserLongitude";
    private static final String ARG_CURRENT_USER_LATITUDE = "currentUserLatitude";
    private static final String ARG_IS_FAVOURITE = "isFavourite";

    private static FragmentCarparkDetail fragmentCarparkDetail = new FragmentCarparkDetail();

    private Carpark mParamCarpark;
    private String mParamLatitude;
    private String mParamLongitude;
    private String mParamCarparkNumber;
    private String mParamCarparkAddress;
    private String mParamCarparkNeighbourhood;
    private double mParamCurrentUserLongitude;
    private double mParamCurrentUserLatitude;
    private int mParamCarparkAvailableLot;
    private boolean mParamIsFavourite;

    private OnFragmentInteractionListener mListener;

    private StreetViewPanoramaView mStreetView;
    private StreetViewPanorama mPanorama;
    private StreetViewPanoramaOptions mPanoramaOption;
    private Bundle streetViewBundle;

    private MainActivity mActivity;
    private CardView cvTop;
    private CardView cvAvailableLot;
    private TextView txtTitle;
    private TextView txtSubTitle;
    private TextView txtAvailableLot;
    private ImageView imgStreetView;
    private Button btnNavigateTo;
    private Button btnFavourite;
    private Button btnRefresh;
    private ProgressDialog prog;

    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mRecyclerLayoutManager;

    private LatLng currentUserLocation;
    private CarparkList mCarparkList;
    private FavouriteList mFavouriteList;
    private httpConnectionAsyncTask httpAsyncTask;
    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param currentPosition current user positon in LatLng.
     * @param position marker positon in LatLng.
     * @param cp Carpark object.
     * @return A new instance of fragment FragmentCarparkDetail.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentCarparkDetail newInstance(LatLng currentPosition, LatLng position, Carpark cp) {
        if(fragmentCarparkDetail == null){
            fragmentCarparkDetail = new FragmentCarparkDetail();
        }

        Bundle args = new Bundle();
        args.putSerializable(ARG_CARPARK, cp);
        args.putString(ARG_LATITUDE, Parser.convertDoubleToString(position.latitude));
        args.putString(ARG_LONGITUDE, Parser.convertDoubleToString(position.longitude));
        args.putString(ARG_CARPARK_NUMBER, cp.getID());
        args.putString(ARG_CARPARK_ADDRESS, Parser.toTitleCase(cp.getAddress()));
        args.putString(ARG_CARPARK_NEIGHBORHOOD, cp.getNeighborhood());
        args.putInt(ARG_CARPARK_AVAILABLE_LOT, cp.getAvailableLot());
        args.putBoolean(ARG_IS_FAVOURITE, cp.getIsFavourite());

        if(currentPosition != null){
            args.putDouble(ARG_CURRENT_USER_LONGITUDE, currentPosition.longitude);
            args.putDouble(ARG_CURRENT_USER_LATITUDE, currentPosition.latitude);
        }else{
            args.putDouble(ARG_CURRENT_USER_LONGITUDE, Constant.NO_LOCATION_LONGITUDE);
            args.putDouble(ARG_CURRENT_USER_LATITUDE, Constant.NO_LOCATION_LATITUDE);
        }

        //removeArgument();
        fragmentCarparkDetail.setArguments(args);
        return fragmentCarparkDetail;
    }

    private static void removeArgument(){
        Bundle b = fragmentCarparkDetail.getArguments();
        if(b != null){
            fragmentCarparkDetail.removeArgument();
        }
    }

    public FragmentCarparkDetail() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mParamCarpark = (Carpark) getArguments().getSerializable(ARG_CARPARK);
            this.mParamLatitude = getArguments().getString(ARG_LATITUDE);
            this.mParamLongitude = getArguments().getString(ARG_LONGITUDE);
            this.mParamCarparkNumber = getArguments().getString(ARG_CARPARK_NUMBER);
            this.mParamCarparkAddress = getArguments().getString(ARG_CARPARK_ADDRESS);
            this.mParamCarparkNeighbourhood = getArguments().getString(ARG_CARPARK_NEIGHBORHOOD);
            this.mParamCurrentUserLongitude = getArguments().getDouble(ARG_CURRENT_USER_LONGITUDE);
            this.mParamCurrentUserLatitude = getArguments().getDouble(ARG_CURRENT_USER_LATITUDE);
            this.mParamCarparkAvailableLot = getArguments().getInt(ARG_CARPARK_AVAILABLE_LOT);
            this.mParamIsFavourite = getArguments().getBoolean(ARG_IS_FAVOURITE);
        }

        this.mCarparkList = CarparkList.getInstance();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_carpark_detail, container, false);

        this.initFavouriteList();
        //this.initStreetViewPanorama(rootView);
        //this.setStreetViewPanorama(this.mParamLatitude, this.mParamLongitude);

        this.initViews(rootView);
        this.setImageStreetView(this.mParamLatitude, this.mParamLongitude);
        this.setCardViewTextTitle(mParamCarparkNeighbourhood);
        this.setCardViewTextSubtitle(mParamCarparkAddress);
        this.setCardViewTextAvailableLot(mParamCarparkAvailableLot);
        this.setCardViewAvailableLot(mParamCarparkAvailableLot);

        initAsyncTask(Constant.SEARCH_HDB_SPECIFIC_CARPARK_DETAIL);
        // check carpark has detail
        if(!this.mParamCarpark.getHasDetail()){
            // get detail of the carpark
            httpAsyncTask.execute(String.valueOf(Constant.SEARCH_HDB_SPECIFIC_CARPARK_DETAIL),
                                    this.mParamLatitude,
                                    this.mParamLongitude,
                                    this.mParamCarparkNumber);
        }else{
            // show carpark detail
            this.setRecyclerDetailView(this.mParamCarpark);
        }

        //check whether this carpark is inside favourite or not
        //update the favourite status as well
        this.saveFavouriteStatus(this.isMarkAsFavourite());


        if(this.mParamIsFavourite){ //is favourite
            //set button to unfavourite
            this.setButtonFavouriteText(getMainActivity().getResources().getString(R.string.button_text_unfavourite));
        }else{ //is not favourite
            //set button to favourite
            this.setButtonFavouriteText(getMainActivity().getResources().getString(R.string.button_text_favourite));
        }

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

    @Override
    public void onResume(){
        super.onResume();
        if(this.mStreetView != null){
            this.mStreetView.onResume();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(this.mStreetView != null){
            this.mStreetView.onDestroy();
        }
    }

    @Override
    public void onPause(){
        super.onDestroy();
        if(this.mStreetView != null){
            this.mStreetView.onPause();
        }
    }

    private void initMainActivity(MainActivity activity){
        this.mActivity = activity;
    }

    private MainActivity getMainActivity(){
        return this.mActivity;
    }

    private void initFavouriteList(){
        if(this.mFavouriteList == null){
            this.mFavouriteList = FavouriteList.getInstance();
        }
    }

    private void initStreetViewPanorama(View v){
        //this.mStreetView = (StreetViewPanoramaView) v.findViewById(R.id.street_view_panorama);
        //this.mStreetView.onCreate(streetViewBundle);
    }

    private void setStreetViewPanorama(String lat, String lng){
        LatLng mPosition = new LatLng(Parser.convertStringToDouble(lat), Parser.convertStringToDouble(lng));
        /*
        this.mPanoramaOption = new StreetViewPanoramaOptions();
        this.mPanoramaOption.streetNamesEnabled(true)
                .panningGesturesEnabled(true)
                .streetNamesEnabled(true)
                .userNavigationEnabled(true)
                .zoomGesturesEnabled(true)
                .position(mPosition);

        this.mPanorama = this.mStreetView.getStreetViewPanorama();
        this.mPanorama.setStreetNamesEnabled(true);
        this.mPanorama.setPanningGesturesEnabled(true);
        this.mPanorama.setUserNavigationEnabled(true);
        this.mPanorama.setZoomGesturesEnabled(true);
        this.mPanorama.setPosition(mPosition);
        */
    }



    private void initViews(View v){
        this.cvTop = (CardView) v.findViewById(R.id.card_view_top);
        this.cvAvailableLot = (CardView) v.findViewById(R.id.card_view_lot);
        this.txtTitle = (TextView) v.findViewById(R.id.text_title);
        this.txtSubTitle = (TextView) v.findViewById(R.id.text_subtitle);
        this.txtAvailableLot = (TextView) v.findViewById(R.id.text_available_lot);
        this.imgStreetView = (ImageView) v.findViewById(R.id.image_street_view);
        this.mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_detail);
        this.btnNavigateTo = (Button) v.findViewById(R.id.btnNavigateTo);
        this.btnFavourite = (Button) v.findViewById(R.id.btnFavourite);
        this.btnRefresh = (Button) v.findViewById(R.id.btnRefresh);
        this.cvTop.setPreventCornerOverlap(false);

        this.setButtonOnClick(btnNavigateTo);
        this.setButtonOnClick(btnFavourite);
        this.setButtonOnClick(btnRefresh);
        this.setImageOnClick(imgStreetView);
    }

    private void setCardViewAvailableLot(int value){
        if(value == Constant.INDEX_LOT_ERROR_LOADING){ //error
            this.cvAvailableLot.setCardBackgroundColor(getMainActivity().getResources().getColor(R.color.card_view_grey));
        }else if(value == -1){ //not available
            this.cvAvailableLot.setCardBackgroundColor(getMainActivity().getResources().getColor(R.color.card_view_grey));
        }else if (value == 0){
            this.cvAvailableLot.setCardBackgroundColor(getMainActivity().getResources().getColor(R.color.card_view_red));
        }else{
            this.cvAvailableLot.setCardBackgroundColor(getMainActivity().getResources().getColor(R.color.card_view_green));
        }
    }

    private void setImageStreetView(String lat, String lng){
        String url = Constant.URL_GOOGLE_STREET_VIEW_QUERY.replace(Constant.DUMMY_LATITUDE, lat).replace(Constant.DUMMY_LONGITUDE, lng);
        Picasso.with(getMainActivity()).load(url).error(R.drawable.ic_image_not_available).into(this.imgStreetView);
    }

    private void setCardViewTextTitle(String value){
        this.txtTitle.setText(value);
    }

    private void setCardViewTextSubtitle(String value){
        this.txtSubTitle.setText(value);
    }

    private void setCardViewTextAvailableLot(int value) {
        if(value == Constant.INDEX_LOT_ERROR_LOADING) {
            this.txtAvailableLot.setText(Constant.LOT_ERROR_LOADING);
        }if(value == Constant.INDEX_LOT_LOADING) {
            this.txtAvailableLot.setText(Constant.LOT_LOADING);
        }else if(value == -1){
            this.txtAvailableLot.setText(Constant.LOT_NOT_AVAILABLE);
        }else if (value == 0){
            this.txtAvailableLot.setText(Constant.LOT_ZERO_AVAILABLE);
        }else{
            this.txtAvailableLot.setText(value + Constant.LOT_AVAILABLE);
        }

    }

    private void setRecyclerDetailView(Carpark cp){
        //mRecyclerView.setHasFixedSize(true);
        mRecyclerLayoutManager = new LinearLayoutManager(getMainActivity());
        mRecyclerView.setLayoutManager(mRecyclerLayoutManager);

        mRecyclerAdapter = new RecyclerViewCarparkDetailAdapter(cp);
        mRecyclerView.setAdapter(mRecyclerAdapter);
        mRecyclerView.addItemDecoration(new DividerItemDecoration(getMainActivity(), DividerItemDecoration.VERTICAL_LIST));
    }

    private void setButtonFavouriteText(String value){
        this.btnFavourite.setText(value);
    }

    private void setButtonOnClick(View mView){
        if(mView instanceof Button){
            ((Button)mView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {
                    switch (v.getId()){
                        case R.id.btnNavigateTo:
                            navigateTo();
                            break;

                        case R.id.btnFavourite:
                            boolean isMarked = isMarkAsFavourite();
                            if(isMarked){
                                //unfavourite it
                                boolean checkMark = markAsUnfavourite();
                                if(checkMark){
                                    setButtonFavouriteText(getMainActivity().getResources().getString(R.string.button_text_favourite));
                                }

                            }else{
                                //mark as favourite
                                boolean checkMark = markAsFavourite();
                                System.out.println("checkMark - " + checkMark);
                                if(checkMark) {
                                    setButtonFavouriteText(getMainActivity().getResources().getString(R.string.button_text_unfavourite));
                                }
                            }
                            break;

                        case R.id.btnRefresh:
                            refreshAvailableLot();
                            break;

                    }
                }
            });
        }
    }

    private void setImageOnClick(View mView){
        if(mView instanceof ImageView){
            ((ImageView) mView).setOnClickListener(new View.OnClickListener() {
                @Override
                public void onClick(View v) {

                    switch(v.getId()){
                        case R.id.image_street_view:
                            String url = Constant.URL_GOOGLE_MAP_STREET_VIEW_QUERY;
                            url = url.replace(Constant.DUMMY_LATITUDE, mParamLatitude);
                            url = url.replace(Constant.DUMMY_LONGITUDE, mParamLongitude);

                            openGoogleMapActivity(url);

                            break;
                    }
                }
            });
        }
    }

    private void navigateTo(){
        String url = Constant.URL_GOOGLE_MAP_DIRECTION_QUERY;

        if(mParamCurrentUserLatitude == Constant.NO_LOCATION_LATITUDE || mParamCurrentUserLongitude == Constant.NO_LOCATION_LONGITUDE){
            url = url.replace("saddr=[srcLat],[srcLong]","");
        }else{
            url = url.replace("[srcLat]",Parser.convertDoubleToString(mParamCurrentUserLatitude));
            url = url.replace("[srcLong]",Parser.convertDoubleToString(mParamCurrentUserLongitude));
        }

        url = url.replace("[destLat]",mParamLatitude);
        url = url.replace("[destLong]",mParamLongitude);

        openGoogleMapActivity(url);
    }

    private void openGoogleMapActivity(String url){
        Uri gmmIntentUri = Uri.parse(url);
        Intent mapIntent = new Intent(Intent.ACTION_VIEW, gmmIntentUri);
        mapIntent.setPackage(Constant.URI_GOOGLE_MAP);
        startActivity(mapIntent);
    }

    private boolean isMarkAsFavourite(){
        return this.getMainActivity().isFavouriteExist(this.mParamCarparkNumber, this.mParamCarparkAddress, this.mParamLatitude, this.mParamLongitude);
    }

    private boolean markAsFavourite(){
        //Log.d()
        System.out.println("adding this carpark to favourite");
        this.getMainActivity().addNewFavourite(this.mParamCarparkNumber, this.mParamCarparkAddress, this.mParamLatitude, this.mParamLongitude);
        return this.getMainActivity().saveFavouriteList();
    }

    private boolean markAsUnfavourite(){
        //Log.d()
        this.getMainActivity().removeFavourite(this.mParamCarparkNumber, this.mParamCarparkAddress, this.mParamLatitude, this.mParamLongitude);
        return this.getMainActivity().saveFavouriteList();
    }


    private void refreshAvailableLot(){
        initAsyncTask(Constant.SEARCH_HDB_NEARBY_CARPARK_USING_COORDINATE);
        // get carpark availability lot
        httpAsyncTask.execute(String.valueOf(Constant.SEARCH_HDB_NEARBY_CARPARK_USING_COORDINATE), mParamLatitude, mParamLongitude);

    }

    private void initAsyncTask(int type){
        this.httpAsyncTask = new httpConnectionAsyncTask(type);
    }

    private class httpConnectionAsyncTask extends AsyncHttpConnection {
        private int queryType;

        public httpConnectionAsyncTask(int type){
            this.queryType = type;
        }

        @Override
        protected void onPreExecute() {
            if(this.queryType == Constant.SEARCH_HDB_SPECIFIC_CARPARK_DETAIL){
                prog = new ProgressDialog(getMainActivity());
                prog.setMessage("Getting detail(s)...");
                prog.show();

            }else if(this.queryType == Constant.SEARCH_HDB_NEARBY_CARPARK_USING_COORDINATE){
                setCardViewTextAvailableLot(-2);
            }

        }//end onPreExecute

        @Override
        protected void onPostExecute(final String result) {
            if (prog != null && prog.isShowing()) {
                prog.dismiss();
            }//end if

            getMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    // result in json object
                    if(result.equals("") || result.isEmpty()){
                        //error message
                    }else{
                        try{
                            if(queryType == Constant.SEARCH_HDB_SPECIFIC_CARPARK_DETAIL){

                                mParamCarpark = Parser.parseCarparkDetailFromJSON(mParamCarpark, result);
                                mParamCarpark.setHasDetail(true);
                                mCarparkList.updateCarpark(mParamCarpark);
                                setRecyclerDetailView(mParamCarpark);

                                //set neighborhood argument
                                saveNeighborhoodValue(mParamCarpark.getNeighborhood());
                                //update UI title neighborhood
                                setCardViewTextTitle(mParamCarpark.getNeighborhood());

                            }else if(queryType == Constant.SEARCH_HDB_NEARBY_CARPARK_USING_COORDINATE){
                                Carpark searchedCarpark = Parser.parseCarparkFromJSON(result, mParamCarparkNumber);
                                System.out.println(result);

                                if(searchedCarpark != null){
                                    setCardViewAvailableLot(searchedCarpark.getAvailableLot());
                                    setCardViewTextAvailableLot(searchedCarpark.getAvailableLot());
                                    //update the CarparkList instance
                                    mCarparkList.updateCarparkAvailableLot(searchedCarpark);
                                }else{
                                    setCardViewAvailableLot(Constant.INDEX_LOT_ERROR_LOADING);
                                    setCardViewTextAvailableLot(Constant.INDEX_LOT_ERROR_LOADING);
                                }

                            }

                        }catch(JSONException e){
                            //print error
                            e.printStackTrace();
                        }

                    }
                }//end run
            });

        }//end onPostExecute
    }//end httpConnectionAsyncTask


    private void saveFavouriteStatus(boolean stat){
        this.mParamIsFavourite = stat;
        fragmentCarparkDetail.getArguments().putBoolean(ARG_IS_FAVOURITE, stat);
    }

    private void saveNeighborhoodValue(String val){
        this.mParamCarparkNeighbourhood = val;
        fragmentCarparkDetail.getArguments().putString(ARG_CARPARK_NEIGHBORHOOD, val);
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
        public void onFragmentCarparkDetailInteraction(Uri uri);
    }

    // TODO: Rename method, update argument and hook method into UI event
    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentCarparkDetailInteraction(uri);
        }
    }

}
