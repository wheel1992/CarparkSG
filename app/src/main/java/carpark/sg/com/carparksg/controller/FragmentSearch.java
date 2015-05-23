package carpark.sg.com.carparksg.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentSender;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.location.Location;
import android.location.LocationManager;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.v4.app.Fragment;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapView;
import com.google.android.gms.maps.MapsInitializer;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import org.json.JSONException;

import carpark.sg.com.carparksg.R;
import carpark.sg.com.carparksg.logic.AsyncHttpConnection;
import carpark.sg.com.carparksg.logic.Parser;
import carpark.sg.com.model.Carpark;
import carpark.sg.com.model.CarparkList;
import carpark.sg.com.model.Constant;
import carpark.sg.com.model.Coordinate;
import carpark.sg.com.model.MarkerCarparkMap;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSearch.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSearch#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSearch extends Fragment implements GoogleApiClient.ConnectionCallbacks,
                                                GoogleApiClient.OnConnectionFailedListener,
                                                LocationListener{
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM_TYPE_OF_SEARCH = "type";
    private static final String ARG_PARAM_ADDRESS = "address";
    private static final String ARG_PARAM_LATITUDE = "latitude";
    private static final String ARG_PARAM_LONGITUDE = "longitude";

    private static final int ALERT_DIALOG_TYPE_GPS = 1;
    private static final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    // TODO: Rename and change types of parameters
    private int mParamType;
    private String mParamAddress;
    private String mParamLatitude;
    private String mParamLongitude;

    private OnFragmentInteractionListener mListener;

    private ViewGroup fragmentContainer;
    private ProgressDialog prog;
    private AlertDialog.Builder mAlertDialog;

    // Google map
    private MapView mMapView;
    private GoogleMap mMap;
    private Bundle mBundleMap;
    private LatLng currentLocation;

    // Marker
    private MarkerCarparkMap markerMap;


    //Location
    private GoogleApiClient mGoogleApiClient;
    private LocationRequest mLocationRequest;

    private MainActivity mActivity;
    private static FragmentSearch mFragmentSearch = new FragmentSearch();
    private httpConnectionAsyncTask httpAsyncTask;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param paramAddress Parameter 1.
     * @return A new instance of fragment FragmentSearch.
     */
    public static FragmentSearch newInstance(int type, String paramAddress, String paramLat, String paramLong) {
        //if(mFragmentSearch == null){
            mFragmentSearch = new FragmentSearch();
        //}
        Bundle args = new Bundle();
        args.putInt(ARG_PARAM_TYPE_OF_SEARCH, type);
        args.putString(ARG_PARAM_ADDRESS, paramAddress);
        args.putString(ARG_PARAM_LATITUDE, paramLat);
        args.putString(ARG_PARAM_LONGITUDE, paramLong);
        mFragmentSearch.setArguments(args);
        return mFragmentSearch;
    }

    public FragmentSearch() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            mParamType = getArguments().getInt(ARG_PARAM_TYPE_OF_SEARCH);
            mParamAddress = getArguments().getString(ARG_PARAM_ADDRESS);
            mParamLatitude = getArguments().getString(ARG_PARAM_LATITUDE);
            mParamLongitude = getArguments().getString(ARG_PARAM_LONGITUDE);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);

        Log.i(this.getMainActivity().getClass().getSimpleName(), "Fragment onCreateView");

        // Inflate the layout for this fragment
        View rootView = inflater.inflate(R.layout.fragment_search, container, false);
        this.fragmentContainer = container;

        MapsInitializer.initialize(this.getMainActivity());
        mMapView = (MapView) rootView.findViewById(R.id.map);
        mMapView.onCreate(mBundleMap);
        initGoogleMap(rootView);
        initMarkerMap();

        // execute asynctask
        httpAsyncTask = new httpConnectionAsyncTask(mParamType);
        switch(mParamType){
            case Constant.SEARCH_HDB_NEARBY_CARPARK_USING_ADDRESS: //search by address, followed by coordinate
                httpAsyncTask.execute(String.valueOf(mParamType), mParamAddress);
                break;
            case Constant.SEARCH_HDB_NEARBY_CARPARK_USING_COORDINATE: //search by coordinate
                httpAsyncTask.execute(String.valueOf(mParamType), mParamLatitude, mParamLongitude);
                break;
            case Constant.SEARCH_HDB_SPECIFIC_CARPARK_DETAIL: //search carpark detail
                break;

        }

        mGoogleApiClient = new GoogleApiClient.Builder(this.getMainActivity())
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(LocationServices.API)
                                .build();

        mLocationRequest = LocationRequest.create()
                                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                .setInterval(10 * 1000)
                                .setFastestInterval(1 * 1000);

        return rootView;
    }

    @Override
    public void onConnected(Bundle bundle){

        Log.i(this.getMainActivity().getClass().getSimpleName(), "Location services connected.");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(this.isLocationServiceOn()){ // check if location service is on
            if(location == null){
                LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }else{
                handleLocation(location);
            }
        }

    }

    @Override
    public void onConnectionSuspended(int i){
        Log.i(this.getMainActivity().getClass().getSimpleName(), "Location services suspended. Please reconnect.");
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){
            try{
                connectionResult.startResolutionForResult(this.getMainActivity(), CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }catch(IntentSender.SendIntentException e){
                e.printStackTrace();
            }
        }else{
            Log.i(this.getMainActivity().getClass().getSimpleName(), "Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onLocationChanged(Location location) {
        handleLocation(location);
    }



    @Override
    public void onResume(){
        super.onResume();
        if(mMapView != null){
            mMapView.onResume();
        }

        if(!isLocationServiceOn()){
            initAlertDialog();
            showAlertDialog(this.ALERT_DIALOG_TYPE_GPS);
        }

        mGoogleApiClient.connect();
    }

    @Override
    public void onPause(){
        super.onPause();
        if(mGoogleApiClient.isConnected()){
            LocationServices.FusedLocationApi.removeLocationUpdates(mGoogleApiClient, this);
            mGoogleApiClient.disconnect();
        }
    }

    @Override
    public void onDestroy(){
        super.onDestroy();
        if(mMapView != null){
            mMapView.onDestroy();
        }
    }

    @Override
    public void onLowMemory(){
        super.onLowMemory();
        if(mMapView != null){
            mMapView.onLowMemory();
        }
    }


    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        this.initMainActivity((MainActivity) activity);

        Log.i(this.mActivity.getClass().getSimpleName(), "Fragment onAttach");

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

    private void initGoogleMap(View v){
        if(this.mMap == null){
            System.out.println("mMap is null");
            this.mMap = this.mMapView.getMap();
            if(this.mMap != null){
                System.out.println("mMap is not null, configure map");
                configureGoogleMap();
            }
        }
    }

    private void configureGoogleMap(){
        this.mMap.setMapType(GoogleMap.MAP_TYPE_NORMAL);
        this.mMap.getUiSettings().setZoomControlsEnabled(true);
        this.mMap.getUiSettings().setCompassEnabled(true);
        this.mMap.setMyLocationEnabled(true);
        this.mMap.getUiSettings().setMyLocationButtonEnabled(true);
        this.mMap.getUiSettings().setRotateGesturesEnabled(true);
        this.mMap.getUiSettings().setMapToolbarEnabled(true);
        this.mMap.setOnMyLocationButtonClickListener(new GoogleMap.OnMyLocationButtonClickListener() {
            @Override
            public boolean onMyLocationButtonClick() {
                Location currentLocation = mMap.getMyLocation();
                if (currentLocation == null) {
                    showAlertDialog(ALERT_DIALOG_TYPE_GPS);
                }
                return true;
            }
        });
        this.mMap.setOnMarkerClickListener(new GoogleMap.OnMarkerClickListener() {
            @Override
            public boolean onMarkerClick(Marker marker) {
                if (marker != null) {
                    mMap.animateCamera(CameraUpdateFactory.newLatLng(marker.getPosition()));
                    marker.showInfoWindow();
                }
                return true;
            }
        });
        this.mMap.setOnInfoWindowClickListener(new GoogleMap.OnInfoWindowClickListener() {
            @Override
            public void onInfoWindowClick(Marker marker) {
                Carpark cp = markerMap.searchCarpark(marker);
                FragmentCarparkDetail openFrag = FragmentCarparkDetail.newInstance(currentLocation, marker.getPosition(), cp);
                getMainActivity().displayFragment(openFrag, Constant.FRAGMENT_CARPARK_DETAIL_NAME);
            }
        });
    }

    private void setCarparkMarkerInGoogleMap(CarparkList mList){
        String lat = "";
        String lng = "";
        LatLng coordinate;
        MarkerOptions markerOption;
        Marker mMarker;

        initMarkerMap();
        for(Carpark cp : mList.getList()){
            lat = cp.getLatitude();
            lng = cp.getLongitude();

            //coordinate = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
            //marker = new MarkerOptions().position(coordinate);
            markerOption = this.setMarkerOption(cp.getAvailableLot(), lat, lng);
            markerOption.title(cp.getAddress());
            mMarker = this.mMap.addMarker(markerOption);
            //mMarker = getMap().addMarker(markerOption);
            mMarker.setAnchor(0.5f, 1);

            addElementToMarkerMap(mMarker, cp);

        }

        addInfoWindowToGoogleMap(markerMap);

    }

    private void setSearchInputCoordinateMarkerInGoogleMap(Coordinate searchCoordinate){
        String lat = searchCoordinate.getLatitude();
        String lng = searchCoordinate.getLongitude();
        LatLng coordinate = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        MarkerOptions marker = new MarkerOptions().position(coordinate);

        this.mMap.addMarker(marker);
        this.zoomCameraToCoordinateInGoogleMap(coordinate);
    }

    private void setCurrentLocationMarkerInGoogleMap(Location location){
        String lat = Parser.convertDoubleToString(location.getLatitude());
        String lng = Parser.convertDoubleToString(location.getLongitude());
        LatLng coordinate = new LatLng(Double.parseDouble(lat), Double.parseDouble(lng));
        MarkerOptions marker = new MarkerOptions().position(coordinate);
        this.mMap.addMarker(marker);
    }

    private void zoomCameraToCoordinateInGoogleMap(LatLng coordinate){
        this.mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(coordinate, 15)); //move camera
        this.mMap.animateCamera(CameraUpdateFactory.zoomTo(16), 2000, null); //zoom camera
    }

    private void addInfoWindowToGoogleMap(MarkerCarparkMap m){
        this.mMap.setInfoWindowAdapter(new MarkerInfoWindowAdapter(this.fragmentContainer, m));
    }

    private void handleLocation(Location location){
        // this will create marker based on the updated location that is changing from GoogleApiClient
        if(location != null){
            currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }

        //this.setCurrentLocationMarkerInGoogleMap(location);
    }

    private boolean isLocationServiceOn(){
        LocationManager manager = (LocationManager) this.getMainActivity().getSystemService(Context.LOCATION_SERVICE);
        if(!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            System.out.println("Location service is off");
            return false;
        }
        System.out.println("Location service is on");
        return true;
    }

    private void initMarkerMap(){
        if(this.markerMap == null){
            this.markerMap = new MarkerCarparkMap();
        }
    }

    private void addElementToMarkerMap(Marker m, Carpark cp){
        this.markerMap.add(m, cp);
    }

    private MarkerOptions setMarkerOption(int value, String lat, String lng){
        MarkerOptions option = new MarkerOptions();
        LatLng coordinate = new LatLng(Parser.convertStringToDouble(lat), Parser.convertStringToDouble(lng));

        option.position(coordinate);
        option.icon(BitmapDescriptorFactory.fromBitmap(this.configureMarkerBitmap(value)));

        return option;
    }

    private Bitmap configureMarkerBitmap(int value){
        String textValue = Parser.convertIntegerToString(value);
        Bitmap.Config conf = Bitmap.Config.ARGB_8888;
        Bitmap bmp;

        if (value == -1) { // N.A.
            bmp = BitmapFactory.decodeResource(this.getMainActivity().getResources(), R.drawable.marker_grey).copy(conf, true);
        }else if (value == 0) { // no lot
            bmp = BitmapFactory.decodeResource(this.getMainActivity().getResources(), R.drawable.marker_red).copy(conf, true);
        }else{ // available lot
            bmp = BitmapFactory.decodeResource(this.getMainActivity().getResources(), R.drawable.marker_green).copy(conf, true);
        }

        Canvas canvas1 = new Canvas(bmp);

        Paint mPaint = new Paint();
        mPaint.setTextSize(getResources().getDimensionPixelSize(R.dimen.text_primary));
        mPaint.setColor(this.getMainActivity().getResources().getColor(R.color.white));
        mPaint.setTextAlign(Paint.Align.CENTER);

        Rect boundsText = new Rect();
        mPaint.getTextBounds(textValue,
                0, textValue.length(),
                boundsText);

        int x = (canvas1.getWidth()) / 2; // - boundsText.width()
        int y = (int) Math.floor((canvas1.getHeight() - boundsText.height()) / 1.5);

        if(value >= 0){
            canvas1.drawText(textValue, x, y, mPaint);
        }

        return bmp;

    }


    private void initAlertDialog(){
        this.mAlertDialog = new AlertDialog.Builder(this.getMainActivity());
    }

    /**
     * @param type number to indicate the type of message.\n 1. location service
     * **/
    private void showAlertDialog(int type){
        if(this.mAlertDialog == null){
            initAlertDialog();
        }

        switch(type){
            case 1:
                //this.mAlertDialog.setTitle(title);
                this.mAlertDialog.setMessage(Constant.ALERT_DIALOG_GPS_DSIABLED_MESSAGE);

                this.mAlertDialog.setPositiveButton("Ok", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        openLocationSetting();
                    }
                }).setNegativeButton("Cancel", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int arg1) {
                        dialog.dismiss();
                    }
                });
                break;

        }

        this.mAlertDialog.create().show();

    }

    private void openLocationSetting(){
        Intent locationSettingIntent = new Intent(Settings.ACTION_LOCATION_SOURCE_SETTINGS);
        startActivity(locationSettingIntent);
    }

    private void updateHistory(String address, Coordinate point){
        System.out.println("Updating History");
        getMainActivity().addNewHistory(address, point.getLatitude(), point.getLongitude());
        getMainActivity().saveHistoryList();
        getMainActivity().refreshSearchAdapter();
    }


    private class httpConnectionAsyncTask extends AsyncHttpConnection{
        private int queryType;

        public httpConnectionAsyncTask(int type){
            this.queryType = type;
        }

        @Override
        protected void onPreExecute() {
            prog = new ProgressDialog(getMainActivity());
            prog.setMessage("Getting all nearby carpark(s)...");
            prog.show();
        }//end onPreExecute

        @Override
        protected void onPostExecute(final String result) {
            if (prog.isShowing()) {
                prog.dismiss();
            }//end if

            getMainActivity().runOnUiThread(new Runnable() {
                @Override
                public void run() {
                    if(result.equals("") || result.isEmpty()){
                        //error message
                    }else{
                        //tv.setText(result);
                        // result in json object
                        try{
                            CarparkList mCarparkList = Parser.parseCarparkListFromJson(result);
                            Coordinate searchCoordinate = Parser.parseSearchInputFromJSON(result);
                            setCarparkMarkerInGoogleMap(mCarparkList); //set marker for each carpark
                            setSearchInputCoordinateMarkerInGoogleMap(searchCoordinate); //set marker for search coordinate

                            // update history from main activity
                            updateHistory(mParamAddress, searchCoordinate);

                        }catch(JSONException e){
                            //print error
                            e.printStackTrace();
                        }

                    }
                }//end run
            });

        }//end onPostExecute
    }//end httpConnectionAsyncTask


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
        public void onFragmentSearchInteraction(Uri uri);
    }

    public boolean onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentSearchInteraction(uri);
            return true;
        }
        return false;
    }



}
