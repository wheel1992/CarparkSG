package carpark.sg.com.carparksg.controller;

import android.app.AlertDialog;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.maps.model.LatLng;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Set;

import carpark.sg.com.carparksg.R;
import carpark.sg.com.model.Carpark;
import carpark.sg.com.model.Constant;
import carpark.sg.com.model.Favourite;
import carpark.sg.com.model.FavouriteList;
import carpark.sg.com.model.History;
import carpark.sg.com.model.HistoryList;
import carpark.sg.com.model.Preferences;
import carpark.sg.com.model.Setting;


public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        FragmentFavourite.OnFragmentInteractionListener,
        FragmentRecent.OnFragmentInteractionListener,
        FragmentSearch.OnFragmentInteractionListener,
        FragmentCarparkDetail.OnFragmentInteractionListener,
        FragmentSetting.OnFragmentInteractionListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private LinearLayout searchContainer;
    private AutoCompleteTextView toolbarSearchText;
    private ImageView toolbarSearchClear;
    private AlertDialog.Builder mAlertDialog;

    private HistoryList mHistoryList;
    private FavouriteList mFavouriteList;
    private Preferences mPreferences;
    private Setting mSetting;
    private SearchDropDownAdapter mSearchAdapter;
    private boolean searchDropDownListShown;

    private Menu mMenu;
    private InputMethodManager imm;

    private final String TAG_GEOCODE_IO_EXCEPTION = "Geocoder IO Exception";
    private final int ALERT_DIALOG_TYPE_GPS = 1;
    private final int CONNECTION_FAILURE_RESOLUTION_REQUEST = 9000;

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private FragmentSearch fragmentSearch;
    private FragmentCarparkDetail fragmentCarparkDetail;

    //Location
    //private GoogleApiClient mGoogleApiClient;
   // private LocationRequest mLocationRequest;
    private LatLng currentLocation;
   // private boolean mRequestingLocationUpdates = false;
    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MainActivity - onCreate");
        // Set up fragment manager
        this.initFragmentManager();

        //When call this, it will also call navigation drawer fragment onCreate
        setContentView(R.layout.activity_main);

        // Set up Toolbar
        Toolbar mToolBar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(mToolBar);
        this.adjustToolbarLogo();
        this.displayToolbarLogo(true);
        this.restoreActionBar();

        // Custom preference
        this.initPreferences();

        // History list
        this.initHistoryList();
        this.populateHistoryList();

        // Favourite list
        this.initFavouriteList();
        this.populateFavouriteList();

        //Setting
        this.initSetting();
        this.populateSetting();

        // Keyboard
        this.initKeyboard();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                mDrawerLayout);

        // Toolbar search container
        this.initSearchContainer();

        // Google API Client and Location
        //this.initGoogleApiClient();
       // this.initLocationRequest();

    }

    @Override
    public void onStart(){
        super.onStart();
        System.out.println("MainActivity - onStart");
        /*
        if(!this.isLocationServiceOn()){
            System.out.println("MainActivity - Location is not on");
            this.initAlertDialog();
            this.showAlertDialog(this.ALERT_DIALOG_TYPE_GPS);
            this.setCurrentLocation(Constant.LOCATION_LATITUDE_EXAMPLE, Constant.LOCATION_LONGITUDE_EXAMPLE);

        }
        */

    }

    @Override
    public void onResume(){
        System.out.println("MainActivity - onResume");
        //if(mGoogleApiClient.isConnected() && !mRequestingLocationUpdates){
       // if(!mGoogleApiClient.isConnected()){
        //    this.mGoogleApiClient.connect();
        //}

        //this.startLocationUpdates();
       // }

        //if(!isLocationServiceOn()){
       //     this.setCurrentLocation(Constant.LOCATION_LATITUDE_EXAMPLE, Constant.LOCATION_LONGITUDE_EXAMPLE);
        //}

        // this step will call fragment search to display the google map with current location
        //this.initFragmentSearch(Constant.SEARCH_HDB_NEARBY_CARPARK_USING_COORDINATE, "",
       //         Parser.convertDoubleToString(currentLocation.latitude),
        //        Parser.convertDoubleToString(currentLocation.longitude));
        this.displayFragmentSearch(Constant.SEARCH_HDB_NEARBY_CARPARK_USING_CURRENT_LOCATION, "",
                Constant.LOCATION_LATITUDE_EXAMPLE,
                Constant.LOCATION_LONGITUDE_EXAMPLE);

        super.onResume();
    }

    @Override
    public void onPause(){
        System.out.println("MainActivity - onPause");
        this.clearHistoryList();
       // if(this.mGoogleApiClient.isConnected() && mRequestingLocationUpdates){
       //     System.out.println("MainActivity - Google api client disconnecting");
       //     this.stopLocationUpdates();
       //     this.mGoogleApiClient.disconnect();
        //}
        super.onPause();
    }

    @Override
    public void onStop(){
        System.out.println("MainActivity - onStop");
        //if(mRequestingLocationUpdates){
        //    this.stopLocationUpdates();
        //}
        super.onStop();
    }

    @Override
    public void onDestroy() {
        System.out.println("MainActivity - onDestroy");
        super.onDestroy();
    }

    @Override
    public void onNavigationDrawerItemSelected(int position) {
        // update the main content by replacing fragments
        Fragment openFragment = null;
        String fragName = "";
        System.out.println("position - " + position);
        switch(position){
            case 0:
                openFragment = FragmentFavourite.newInstance("", "");
                fragName = Constant.FRAGMENT_FAVOURITE;
                break;
            case 1:
                openFragment = FragmentRecent.newInstance("", "");
                fragName = Constant.FRAGMENT_RECENT;
                break;
            case 2:
                openFragment = FragmentSetting.newInstance(this.mSetting);
                fragName = Constant.FRAGMENT_SETTING;
                break;
        }

        displayFragment(openFragment, fragName);

    }

    /*
    public void initGoogleApiClient(){
        this.mGoogleApiClient = new GoogleApiClient.Builder(this)
                                .addConnectionCallbacks(this)
                                .addOnConnectionFailedListener(this)
                                .addApi(LocationServices.API)
                                .build();
    }

    public void initLocationRequest(){
        this.mLocationRequest = LocationRequest.create()
                .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                .setInterval(10 * 1000)
                .setFastestInterval(1 * 1000);
    }

    */

    public void initFragmentManager(){
        this.fragmentManager = getSupportFragmentManager();
    }

    /**
     * This method will open the fragment search with map
     * **/
    public void displayFragmentSearch(int type, String address, String lat, String lng){
        String fragName = Constant.FRAGMENT_SEARCH_NAME;
        this.fragmentSearch = FragmentSearch.newInstance(type, address, lat, lng);
        //displayFragment(this.fragmentSearch, fragName);
        replaceFragment(this.fragmentSearch, fragName);
    }

    public void displayFragmentCarparkDetail(LatLng currentPosition, LatLng cpPosition, Carpark cp){
        this.fragmentCarparkDetail = FragmentCarparkDetail.newInstance(currentPosition, cpPosition, cp);
        this.displayFragment(this.fragmentCarparkDetail, Constant.FRAGMENT_CARPARK_DETAIL_NAME);
    }

    public void displayFragment(Fragment newFrag, String name){
        System.out.println("MainActivity - displaying a fragment..." + name);
        if(newFrag != null){
            if(fragmentManager == null){
                this.initFragmentManager();
            }

            this.fragmentTransaction = fragmentManager.beginTransaction();

            // remove the fragment from manager first if found
            Fragment searchFrag = fragmentManager.findFragmentByTag(name);
            if(searchFrag != null){
                System.out.println("MainActivity - removing similar fragment..." + searchFrag.getTag());
                this.removeFragment(searchFrag);
            }

            // add new fragment
            this.fragmentTransaction
                    .add(R.id.container, newFrag, name)
                    .addToBackStack(name)
                    .commit();
        }
    }

    public void removeFragment(Fragment frag){
        if(frag != null){
            this.fragmentTransaction
                    .remove(frag);
        }
    }

    public void replaceFragment(Fragment newFrag, String name){
        System.out.println("MainActivity - Replace without adding to backstack fragment..." + name);
        if(newFrag != null){
            this.fragmentTransaction = fragmentManager.beginTransaction();
            this.fragmentTransaction
                    .replace(R.id.container, newFrag, name)
                    .commit();
            //.addToBackStack(name)
        }
    }

    public void onSectionAttached(int number) {
        switch (number) {
            case 1:
                mTitle = getString(R.string.title_fragment_favorite);
                break;
            case 2:
                mTitle = getString(R.string.title_fragment_recent);
                break;
            case 3:
                mTitle = getString(R.string.title_fragment_setting);
                break;
        }
    }

    public void restoreActionBar() {
        getSupportActionBar().setDisplayShowTitleEnabled(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void adjustToolbarLogo(){
        ImageView iv = getToolbarLogo();
        iv.setAdjustViewBounds(true);
    }

    public void displayToolbarLogo(boolean visible){
        ImageView iv = getToolbarLogo();
        if(visible){
            iv.setVisibility(View.VISIBLE);
        }else{
            iv.setVisibility(View.GONE);
        }
    }

    private ImageView getToolbarLogo(){
        ImageView iv = (ImageView) findViewById(R.id.toolbar_logo_img);
        return iv;
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            displayToolbarLogo(true);
            displaySearchView(false, menu.findItem(R.id.action_search));

            this.mMenu = menu;
            return true;
        }
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        if (id == R.id.action_settings) {
            return true;

        }else if (id == R.id.action_search){
            displaySearchView(true, item);
            displayToolbarLogo(false);

        }else if(id == android.R.id.home){
            if(this.mMenu != null){
                displaySearchView(false, this.mMenu.findItem(R.id.action_search));
            }

            displayToolbarLogo(true);
            closeKeyboard(this.toolbarSearchText);
        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        //Log.d(this.getClass().getName(), "onBackPressed - back button pressed");
        System.out.println("MainActivity - back button pressed");
        /*
        //closeKeyboard(toolbarSearchText);
        if(fragmentManager != null){
            //by default, the last fragment to be shown is favourite.
            //hence, the least number of backstack is 1
            //cos favourite is inside the backstack

            if(fragmentManager.getBackStackEntryCount() > 1){
                System.out.println("MainActivity - ==== OLD STACK ====");
                for(int i = 0; i<fragmentManager.getBackStackEntryCount(); i++){
                    System.out.println(i + ". -" + fragmentManager.getBackStackEntryAt(i));
                }
                fragmentManager.popBackStack();

                System.out.println("MainActivity - ==== NEW STACK ====");
                for(int i = 0; i<fragmentManager.getBackStackEntryCount(); i++){
                    System.out.println(i + ". -" + fragmentManager.getBackStackEntryAt(i));
                }

            }else{
                //will execute exit program
                //with a delay
            }
        }else{
            System.out.println("MainActivity - fragmentManager is null, back pressed");
            super.onBackPressed();
        }

        System.out.println("MainActivity - ==== OLD STACK ====");
        for(int i = 0; i<fragmentManager.getBackStackEntryCount(); i++){
            System.out.println(i + ". -" + fragmentManager.getBackStackEntryAt(i));
        }
*/
        if(this.fragmentManager.getBackStackEntryCount() > 0){
            this.fragmentManager.popBackStack();
            this.fragmentManager.executePendingTransactions();
        }else{
            if(this.exit){
                this.finish();
            }else{
                Toast.makeText(this, "Press back again to exit program.",
                        Toast.LENGTH_SHORT).show();
                this.exit = true;
                new Handler().postDelayed(new Runnable() {
                    @Override
                    public void run() {
                        exit = false;
                    }//end run
                }, 1 * 1000);
            }

        }

    }

    public void initPreferences(){
        if(this.mPreferences == null){
            this.mPreferences = Preferences.getInstance(this);
        }
    }

    public void initHistoryList(){
        if(this.mHistoryList == null){
            this.mHistoryList = HistoryList.getInstance();
        }
    }

    // Initialize favourite list
    public void initFavouriteList(){
        this.mFavouriteList = FavouriteList.getInstance();
    }

    // Initalize setting
    public void initSetting(){
        this.mSetting = Setting.getInstance();
    }

    public void clearHistoryList(){
        if(this.mHistoryList != null){
            this.mHistoryList.removeAllHistory();
        }
    }

    public void clearFavouriteList(){
        this.mFavouriteList.removeAllFavourite();
    }

    public HistoryList getHistoryList(){
        return this.mHistoryList;
    }

    public FavouriteList getFavouriteList(){
        return this.mFavouriteList;
    }

    public void populateHistoryList(){
        // Get history json string from preferences
        // get history list from json string
        try{
            String historyJson = this.mPreferences.getHistory();
            System.out.println("MainActivity - JSON string from Preferences - " + historyJson);
            this.mHistoryList.parseStringToList(historyJson);

        }catch(JSONException e){
            //print error
            e.printStackTrace();

        }catch (FileNotFoundException e){
            //print error
            e.printStackTrace();
        }catch (IOException e){
            //print error
            e.printStackTrace();
        }
    }

    public void populateFavouriteList(){
        try{
            String favouriteValue = this.mPreferences.getFavourite();
            System.out.println("MainActivity - JSON string from Favourite - " + favouriteValue);
            this.mFavouriteList.parseStringToMap(favouriteValue);
        }catch(Exception e){
            //print error
            e.printStackTrace();
        }
    }

    public void populateSetting(){
        String settingValue = "";
        try{
            settingValue = this.mPreferences.getSetting();
            System.out.println("MainActivity - JSON string from setting - " + settingValue);
        }catch(Exception e){
            //print error
            e.printStackTrace();
        }finally {
            try{
                Setting.parseJsonToSetting(settingValue);
            }catch(JSONException e){
                e.printStackTrace();
            }
        }

    }

    public void addNewHistory(String name, String lat, String lng){
        History element = new History(name, lat, lng);
        this.mHistoryList.addHistory(element);
    }

    public void addNewFavourite(String id, String name, String lat, String lng, String neighbor, int lot, boolean isFav){
        Favourite element = new Favourite(id, name, lat, lng, neighbor, lot, isFav);
        this.mFavouriteList.addFavourite(element);
    }

    public void removeHistory(String name, String lat, String lng){
        History element = new History(name, lat, lng);
        this.mHistoryList.removeHistory(element);
    }

    public void removeFavourite(String id, String name, String lat, String lng, String neighbor, int lot, boolean isFav){
        Favourite element = new Favourite(id, name, lat, lng, neighbor, lot, isFav);
        this.mFavouriteList.removeFavourite(element);
    }

    public void saveHistoryList(){
        try{
            String value = mHistoryList.parseListToString();
            mPreferences.setHistory(value);
        }catch(JSONException e){
            //print error
            e.printStackTrace();
        }catch (FileNotFoundException e){
            //print error
            e.printStackTrace();
        }catch (IOException e){
            //print error
            e.printStackTrace();
        }

    }

    public boolean saveFavouriteList(){
        try{
            String value = mFavouriteList.parseMapToString();
            mPreferences.setFavourite(value);
            return true;
        }catch(JSONException e){
            //print error
            e.printStackTrace();
            return false;
        }catch (FileNotFoundException e){
            //print error
            e.printStackTrace();
            return false;
        }catch (IOException e){
            //print error
            e.printStackTrace();
            return false;
        }
    }

    public boolean saveSetting(){
        try{
            String value = Setting.parseSettingToJson();
            mPreferences.setSetting(value);
            return true;
        }catch(JSONException e){
            //print error
            e.printStackTrace();
            return false;
        }catch (FileNotFoundException e){
            //print error
            e.printStackTrace();
            return false;
        }catch (IOException e){
            //print error
            e.printStackTrace();
            return false;
        }
    }

    public boolean isFavouriteExist(String id, String address, String lat, String lng){
        return this.mFavouriteList.isFavouriteExist(id, address, lat, lng);
    }


    public void refreshHistoryList(){
        this.saveHistoryList();
        this.populateHistoryList();
    }

    private void initSearchContainer(){
        searchContainer = (LinearLayout) findViewById(R.id.search_container);
        toolbarSearchText = (AutoCompleteTextView) findViewById(R.id.search_auto_complete_text);
        toolbarSearchClear = (ImageView) findViewById(R.id.search_clear);

        this.initSearchAdapter();
        this.toolbarSearchText.setAdapter(mSearchAdapter);
        this.toolbarSearchText.setDropDownAnchor(R.id.custom_toolbar);
        this.displaySearchContainer(false); // set gone first

        try {
            // Set cursor colour to white
            // http://stackoverflow.com/a/26544231/1692770
            // https://github.com/android/platform_frameworks_base/blob/kitkat-release/core/java/android/widget/TextView.java#L562-564
            Field f = TextView.class.getDeclaredField("mCursorDrawableRes");
            f.setAccessible(true);
            f.set(toolbarSearchText, R.drawable.cursor);
        } catch (Exception ignored) {
        }

        toolbarSearchClear.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                clearToolbarSearchText();
            }
        });

        toolbarSearchText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                //System.out.println("onEditorAction is triggered!");
                //System.out.println(v.getText());
                closeKeyboard(toolbarSearchText);
                initSearchAdapter();

                displayFragmentSearch(Constant.SEARCH_HDB_NEARBY_CARPARK_USING_ADDRESS, v.getText().toString(),
                        "", "");

                //searchCarpark(Constant.SEARCH_HDB_NEARBY_CARPARK_USING_ADDRESS, v.getText().toString(), "", "");
                return true;
            }
        });

        toolbarSearchText.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                //System.out.println("position " + position + " is clicked");
                closeKeyboard(toolbarSearchText);
                initSearchAdapter();

                History element = (History) parent.getAdapter().getItem(position);
                String address = element.getName();
                String lat = element.getLatitude();
                String lng = element.getLongitude();

                setToolbarSearchText(address);
                displayFragmentSearch(Constant.SEARCH_HDB_NEARBY_CARPARK_USING_COORDINATE, address,
                                    lat, lng);
                //searchCarpark(Constant.SEARCH_HDB_NEARBY_CARPARK_USING_COORDINATE, address, lat, lng);

            }
        });

        toolbarSearchText.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                //System.out.println("setOnTouchListener - DropDown showing");
                toolbarSearchText.showDropDown();
                return false;
            }
        });


        toolbarSearchText.getViewTreeObserver().addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                boolean isShow = isKeyboardShown(toolbarSearchText.getRootView());
                /*
                if(!isShow){
                    System.out.println("isVisibleSearchContainer() - " + isVisibleSearchContainer());
                    if(isVisibleSearchContainer()){
                        if(mMenu != null){
                            displaySearchView(false, mMenu.findItem(R.id.action_search));
                        }

                        displayToolbarLogo(true);
                        closeKeyboard(toolbarSearchText);
                    }
                }
                */
            }
        });

    }

    public void initSearchAdapter(){
        mSearchAdapter = new SearchDropDownAdapter(this, R.layout.dropdown_list_row_layout, mHistoryList.getList(), this);
    }

    public void refreshSearchAdapter(){
        this.initSearchAdapter();
        this.toolbarSearchText.setAdapter(null);
        this.toolbarSearchText.setAdapter(this.mSearchAdapter);
    }

    /*
    public void searchCarpark(int type, String address, String lat, String lng){
        if(fragmentSearch != null) {
            this.removeFragment(fragmentSearch);
        }

        fragmentSearch = FragmentSearch.newInstance(type, address, lat, lng);
        displayFragment(fragmentSearch, Constant.FRAGMENT_SEARCH_NAME);
    }
    */

    private void displaySearchContainer(boolean visible){
        if(visible){
            this.searchContainer.setVisibility(View.VISIBLE);
        }else{
            this.searchContainer.setVisibility(View.GONE);
        }
    }

    private void displaySearchView(boolean visible, final MenuItem searchMenuItem) {
        if (visible) {
            // Stops user from being able to open drawer while searching
            //mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_LOCKED_CLOSED);

            // Hide search button, display EditText
            searchMenuItem.setVisible(false);
            displaySearchContainer(true);

            // Animate the home icon to the back arrow
            //toggleActionBarIcon(ActionDrawableState.ARROW, mDrawerToggle, true);

            // Shift focus to the search EditText
            toolbarSearchText.requestFocus();
            // Pop up the soft keyboard
            new Handler().postDelayed(new Runnable() {
                public void run() {
                    toolbarSearchText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_DOWN, 0, 0, 0));
                    toolbarSearchText.dispatchTouchEvent(MotionEvent.obtain(SystemClock.uptimeMillis(), SystemClock.uptimeMillis(), MotionEvent.ACTION_UP, 0, 0, 0));
                }
            }, 100);
        } else {
            // Allows user to open drawer again
            //mDrawerLayout.setDrawerLockMode(DrawerLayout.LOCK_MODE_UNLOCKED);

            // Hide the EditText and put the search button back on the Toolbar.
            // This sometimes fails when it isn't postDelayed(), don't know why.
            toolbarSearchText.postDelayed(new Runnable() {
                @Override
                public void run() {
                    clearToolbarSearchText();
                    toolbarSearchText.clearFocus();
                    //closeKeyboard(toolbarSearchText);
                    displaySearchContainer(false);
                    searchMenuItem.setVisible(true);
                }
            }, 100);

            // Turn the home button back into a drawer icon
            //toggleActionBarIcon(ActionDrawableState.BURGER, mDrawerToggle, true);

        }
    }

    private void sortArray(ArrayList<String> array){
        Collections.sort(array);
    }

    private void initKeyboard(){
        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    private void closeKeyboard(View v){
       imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
    }

    private boolean isKeyboardShown(View rootView) {
    /* 128dp = 32dp * 4, minimum button height 32dp and generic 4 rows soft keyboard */
        final int SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD = 128;

        Rect r = new Rect();
        rootView.getWindowVisibleDisplayFrame(r);
        DisplayMetrics dm = rootView.getResources().getDisplayMetrics();
        /* heightDiff = rootView height - status bar height (r.top) - visible frame height (r.bottom - r.top) */
        int heightDiff = rootView.getBottom() - r.bottom;
        /* Threshold size: dp to pixels, multiply with display density */
        boolean isKeyboardShown = heightDiff > SOFT_KEYBOARD_HEIGHT_DP_THRESHOLD * dm.density;

        //Log.d("isKeyboardShown", "isKeyboardShown ? " + isKeyboardShown + ", heightDiff:" + heightDiff + ", density:" + dm.density
        //        + "root view height:" + rootView.getHeight() + ", rect:" + r);

        return isKeyboardShown;
    }



    private void setToolbarSearchText(String value){
        this.toolbarSearchText.setText(value);
    }

    private void clearToolbarSearchText(){
        this.toolbarSearchText.setText("");
    }

    // Interaction between activity and fragment search
    public void onFragmentSearchInteraction(Uri uri){

    }

    @Override
    public void onFragmentRecentInteraction(Uri uri) {

    }

    @Override
    public void onFragmentFavouriteInteraction(Uri uri) {

    }

    @Override
    public void onFragmentCarparkDetailInteraction(Uri uri) {

    }

    @Override
    public void onFragmentSettingInteraction(Uri uri) {

    }

    /**
     * For Google API Client - Location stuffs
     * **/
    public void setCurrentLocation(LatLng location){
        this.currentLocation = location;
    }

    public LatLng getCurrentLocation(){
        return this.currentLocation;
    }





/*
    @Override
    public void onConnected(Bundle bundle) {
        //Log.i(this.getClass().getSimpleName(), "Location services connected.");
        System.out.println("MainActivity - Location service connected");
        Location location = LocationServices.FusedLocationApi.getLastLocation(mGoogleApiClient);
        if(this.isLocationServiceOn()){ // check if location service is on
            if(location == null){
                this.startLocationUpdates();
                //LocationServices.FusedLocationApi.requestLocationUpdates(mGoogleApiClient, mLocationRequest, this);
            }else{
                handleLocation(location);
            }
        }
    }

    @Override
    public void onConnectionFailed(ConnectionResult connectionResult) {
        if(connectionResult.hasResolution()){
            try{
                connectionResult.startResolutionForResult(this, CONNECTION_FAILURE_RESOLUTION_REQUEST);
            }catch(IntentSender.SendIntentException e){
                e.printStackTrace();
            }
        }else{
            //Log.i(this.getClass().getSimpleName(), "Location services connection failed with code " + connectionResult.getErrorCode());
            System.out.println("MainActivity - Location services connection failed with code " + connectionResult.getErrorCode());
        }
    }

    @Override
    public void onConnectionSuspended(int i) {
        //Log.i(this.getClass().getSimpleName(), "Location services suspended. Please reconnect.");
        System.out.println("MainActivity - Location services suspended. Please reconnect.");
    }

    @Override
    public void onLocationChanged(Location location) {
        handleLocation(location);
    }

    private void startLocationUpdates() {
        System.out.println("MainActivity - Start location updates");
        LocationServices.FusedLocationApi.requestLocationUpdates(
                mGoogleApiClient, mLocationRequest, this);

        this.mRequestingLocationUpdates = true;
    }

    private void stopLocationUpdates() {
        System.out.println("MainActivity - Stop location updates");
        LocationServices.FusedLocationApi.removeLocationUpdates(
                mGoogleApiClient, this);
        this.mRequestingLocationUpdates = false;
    }

    private boolean isLocationServiceOn(){
        LocationManager manager = (LocationManager) this.getSystemService(Context.LOCATION_SERVICE);
        if(!manager.isProviderEnabled(LocationManager.NETWORK_PROVIDER)){
            System.out.println("MainActivity - Location service is off");
            return false;
        }
        System.out.println("MainActivity - Location service is on");
        return true;
    }

    private void handleLocation(Location location){
        // this will create marker based on the updated location that is changing from GoogleApiClient
        if(location != null){
            System.out.println("MainActivity - handle location, get current latitude and longitude");
            this.currentLocation = new LatLng(location.getLatitude(), location.getLongitude());
        }
        //this.setCurrentLocationMarkerInGoogleMap(location);
    }

    private void setCurrentLocation(String lat, String lng){
        this.currentLocation = new LatLng(Parser.convertStringToDouble(lat),
                                        Parser.convertStringToDouble(lng));
    }

    /**
     * Alert Dialogs
     * **/
    /*
    private void initAlertDialog(){
        this.mAlertDialog = new AlertDialog.Builder(this);
    }
*/
    /**
     * @param type number to indicate the type of message.\n 1. location service
     * **/
  /*
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

    */

}
