package carpark.sg.com.carparksg.controller;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.graphics.Color;
import android.graphics.Rect;
import android.net.Uri;
import android.os.Handler;
import android.os.SystemClock;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.content.Context;
import android.os.Bundle;
import android.support.v4.app.FragmentTransaction;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.view.KeyEvent;
import android.view.LayoutInflater;
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

import com.google.android.gms.maps.model.LatLng;
import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.listeners.ActionClickListener;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import carpark.sg.com.carparksg.R;
import carpark.sg.com.carparksg.logic.Parser;
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
    private ActionBarDrawerToggle mNavigationDrawerToggle;
    private LinearLayout searchContainer;
    private AutoCompleteTextView toolbarSearchText;
    private ImageView toolbarSearchClear;
    private ImageView toolbarLogo;
    private TextView toolbarTitle;
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
    private boolean mHasRadiusChanged = false;

    private boolean exit = false;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        System.out.println("MainActivity - onCreate");


        //When call this, it will also call navigation drawer fragment onCreate
        setContentView(R.layout.activity_main);

        // Set up Toolbar
        Toolbar mToolBar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(mToolBar);
        //this.toggleDisplayToolbarTitle(false);
        //mToolBar.setTitle("testing toolbar");
        //this.setToolbarTitle("testing");
        this.initToolbarLogo();
        this.initToolbarTitle();

        this.toggleDisplayToolbarTitle(false);
        this.toggleDisplayToolbarLogo(true);
        this.adjustToolbarLogo();

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

        this.mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        this.mTitle = getTitle();

        // Set up the drawer.
        this.mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        this.mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                this.mDrawerLayout);

        this.initNavigationDrawerToggle();
        // Toolbar search container
        this.initSearchContainer();

        // Set up fragment manager
        this.initFragmentManager();
    }

    @Override
    public void onStart(){
        super.onStart();
        System.out.println("MainActivity - onStart");
    }

    @Override
    public void onResume(){
        System.out.println("MainActivity - onResume");
        this.displayFragmentSearch(Constant.SEARCH_HDB_NEARBY_CARPARK_USING_CURRENT_LOCATION, "",
                Constant.LOCATION_LATITUDE_EXAMPLE,
                Constant.LOCATION_LONGITUDE_EXAMPLE,
                Parser.convertIntegerToString(this.getSettingRadius()));
        super.onResume();
    }

    @Override
    public void onPause(){
        System.out.println("MainActivity - onPause");
        this.clearHistoryList();
        super.onPause();
    }

    @Override
    public void onStop(){
        System.out.println("MainActivity - onStop");
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
            case 0: // favourite
                openFragment = FragmentFavourite.newInstance("", "");
                fragName = Constant.FRAGMENT_FAVOURITE_NAME;
                break;
            case 1: // recent
                openFragment = FragmentRecent.newInstance("", "");
                fragName = Constant.FRAGMENT_RECENT_NAME;
                break;
            case 2: // setting
                openFragment = FragmentSetting.newInstance(this.mSetting);
                fragName = Constant.FRAGMENT_SETTING_NAME;
                break;
            case 3: // about
                // About will have a pop-up dialog instead of fragment
                this.showDialogAbout(this);
                break;
        }

        // show fragment for other position except 3 (about)
        if(position != 3){
            displayFragment(openFragment, fragName);
        }


    }

    public void initNavigationDrawerToggle(){
        if(mNavigationDrawerFragment != null){
            this.mNavigationDrawerToggle = this.mNavigationDrawerFragment.getDrawerToggle();
        }
    }

    public void initFragmentManager(){

        this.fragmentManager = getSupportFragmentManager();
        this.fragmentManager.addOnBackStackChangedListener(new FragmentManager.OnBackStackChangedListener() {
            @Override
            public void onBackStackChanged() {
                if (fragmentManager.getBackStackEntryCount() > 0) {
                    mNavigationDrawerToggle.setDrawerIndicatorEnabled(false);
                    //mNavigationDrawerToggle.setToolbarNavigationClickListener(new View.OnClickListener(){
                    //    @Override
                    //    public void onClick(View v) {
                    //        System.out.println("MainActivity - toolbar navigation click");
                    //    }
                    //});
                } else {
                    mNavigationDrawerToggle.setDrawerIndicatorEnabled(true);
                    mNavigationDrawerToggle.setToolbarNavigationClickListener(mNavigationDrawerFragment.getOriginalToolbarNavigationClickListener());
                }
            }
        });
    }

    /**
     * This method will open the fragment search with map
     * **/
    public void displayFragmentSearch(int type, String address, String lat, String lng, String radius){
        String fragName = Constant.FRAGMENT_SEARCH_NAME;
        this.fragmentSearch = FragmentSearch.newInstance(type, address, lat, lng, radius);
        this.replaceFragment(this.fragmentSearch, fragName);
    }

    public void displayFragmentCarparkDetail(LatLng currentPosition, LatLng cpPosition, Carpark cp){
        this.fragmentCarparkDetail = FragmentCarparkDetail.newInstance(currentPosition, cpPosition, cp);
        this.displayFragment(this.fragmentCarparkDetail, Constant.FRAGMENT_CARPARK_DETAIL_NAME);
    }

    public void displayFragment(Fragment newFrag, String name){
        //System.out.println("MainActivity - displaying a fragment..." + name);
        if(newFrag != null){
            if(fragmentManager == null){
                this.initFragmentManager();
            }

            this.fragmentTransaction = fragmentManager.beginTransaction();

            // remove the fragment from manager first if found
            Fragment searchFrag = fragmentManager.findFragmentByTag(name);
            if(searchFrag != null){
                //System.out.println("MainActivity - removing similar fragment..." + searchFrag.getTag());
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
        //System.out.println("MainActivity - Replace without adding to backstack fragment..." + name);
        if(newFrag != null){
            this.fragmentTransaction = fragmentManager.beginTransaction();
            this.fragmentTransaction
                    .replace(R.id.container, newFrag, name)
                    .commit();
        }
    }

    public void removeFragmentFromBackStack(){
        this.fragmentManager.popBackStack();
        this.fragmentManager.executePendingTransactions();
    }

    public boolean hasReachedFirstPage(){
        //System.out.println("MainActivity - hasReachedFirstPage -> this.fragmentManager.getBackStackEntryCount() -> " + this.fragmentManager.getBackStackEntryCount());
        if(this.fragmentManager.getBackStackEntryCount() == 0){
            return true;
        }else{
            return false;
        }
    }

    public void setToolbarTitleBasedOnPopBackStackResult(){
        FragmentManager.BackStackEntry backEntry = this.fragmentManager.getBackStackEntryAt(this.fragmentManager.getBackStackEntryCount() - 1);
        String lastFragName = backEntry.getName();
        switch(lastFragName){
            case Constant.FRAGMENT_FAVOURITE_NAME:
                this.setToolbarTitle(Constant.FRAGMENT_FAVOURITE_TITLE);
                break;
            case Constant.FRAGMENT_RECENT_NAME:
                this.setToolbarTitle(Constant.FRAGMENT_RECENT_TITLE);
                break;
            case Constant.FRAGMENT_SETTING_NAME:
                this.setToolbarTitle(Constant.FRAGMENT_SETTING_TITLE);
                break;
            case Constant.FRAGMENT_CARPARK_DETAIL_NAME:
                this.setToolbarTitle(Constant.FRAGMENT_CARPARK_DETAIL_TITLE);
                break;
        }
    }

    /*
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
*/

    public void restoreActionBar() {
        //this.toggleDisplayToolbarTitle(false);
        getSupportActionBar().setDisplayShowHomeEnabled(true);
        getSupportActionBar().setHomeButtonEnabled(true);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
    }

    public void initToolbarLogo(){
        this.toolbarLogo = (ImageView) findViewById(R.id.toolbar_logo_img);
    }

    public void adjustToolbarLogo(){
        this.toolbarLogo.setAdjustViewBounds(true);
    }

    public void toggleDisplayToolbarLogo(boolean visible){
        if(visible){
            this.toolbarLogo.setVisibility(View.VISIBLE);
        }else{
            this.toolbarLogo.clearAnimation();
            this.toolbarLogo.setVisibility(View.GONE);
        }
    }

    public void initToolbarTitle(){
        this.toolbarTitle = (TextView) findViewById(R.id.toolbar_text_title);
    }

    public void toggleDisplayToolbarTitle(boolean visible){
        //getSupportActionBar().setDisplayShowTitleEnabled(visible);
        if(visible){
            this.toolbarTitle.setVisibility(View.VISIBLE);
        }else{
            this.toolbarTitle.setVisibility(View.GONE);
        }
    }

    public void setToolbarTitle(String title){
        //toggleDisplayToolbarTitle(true);
        //getSupportActionBar().setTitle(title);
        //this.setTitle(title);
        this.toolbarTitle.setText(title);

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {

        if (!mNavigationDrawerFragment.isDrawerOpen()) {
            // Only show items in the action bar relevant to this screen
            // if the drawer is not showing. Otherwise, let the drawer
            // decide what to show in the action bar.
            getMenuInflater().inflate(R.menu.main, menu);
            restoreActionBar();
            toggleDisplaySearchView(false, menu.findItem(R.id.action_search));

            if(this.hasReachedFirstPage()){
                this.toggleDisplayToolbarLogo(true); //show logo
                this.toggleDisplayToolbarTitle(false); //hide title
            }else{
                this.toggleDisplayToolbarLogo(false); //hide logo
                this.toggleDisplayToolbarTitle(true); //show title
                this.setToolbarTitleBasedOnPopBackStackResult(); //display title based on last fragment
            }

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
            this.closeKeyboard(null); //close keyboard
            this.hideSearchView(); //hide the autocomplete search text

            Fragment openFragment = FragmentSetting.newInstance(this.mSetting);
            String fragName = Constant.FRAGMENT_SETTING_NAME;
            this.displayFragment(openFragment, fragName);

            return true;

        }else if (id == R.id.action_search){
            toggleDisplaySearchView(true, item);
            if(this.hasReachedFirstPage()){
                toggleDisplayToolbarLogo(false);
            }else{
                toggleDisplayToolbarTitle(false);
            }


        }else if(id == android.R.id.home){ //it is the back arrow
            //System.out.println("MainActivity - activity onOptionsItemSelected");
            if(this.mMenu != null){
                this.hideSearchView();
            }

            //toggleDisplayToolbarLogo(true);
            closeKeyboard(this.toolbarSearchText);

        }

        return super.onOptionsItemSelected(item);
    }


    @Override
    public void onBackPressed() {
        //Log.d(this.getClass().getName(), "onBackPressed - back button pressed");
        System.out.println("MainActivity - back button pressed");
        if(this.fragmentManager.getBackStackEntryCount() > 0){
            this.removeFragmentFromBackStack();

            if(this.hasReachedFirstPage()){
                this.toggleDisplayToolbarLogo(true); //show logo
                this.toggleDisplayToolbarTitle(false); //hide title
                if(this.hasRadiusChanged()){
                    this.showSnackBar();
                }

            }else{
                this.toggleDisplayToolbarLogo(false); //hide logo
                this.toggleDisplayToolbarTitle(true); //show title
                this.setToolbarTitleBasedOnPopBackStackResult(); //display title based on last fragment
            }

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

    public void setSettingRadius(int value){
        this.mSetting.setRadius(value);
    }

    public int getSettingRadius(){
        return this.mSetting.getRadius();
    }

    public boolean isFavouriteExist(String id, String address, String lat, String lng){
        return this.mFavouriteList.isFavouriteExist(id, address, lat, lng);
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
                        "", "", Parser.convertIntegerToString(getSettingRadius()));

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
                                    lat, lng, Parser.convertIntegerToString(getSettingRadius()));
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

                        }


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

    private void displaySearchContainer(boolean visible){
        if(visible){
            this.searchContainer.setVisibility(View.VISIBLE);
        }else{
            this.searchContainer.setVisibility(View.GONE);
        }
    }

    public void hideSearchView(){
        this.toggleDisplaySearchView(false, this.mMenu.findItem(R.id.action_search));
    }

    private void toggleDisplaySearchView(boolean visible, final MenuItem searchMenuItem) {
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

            if(this.hasReachedFirstPage()){
                this.toggleDisplayToolbarLogo(true);
            }else{
                this.toggleDisplayToolbarTitle(true);
                this.setToolbarTitleBasedOnPopBackStackResult();
            }

            // Turn the home button back into a drawer icon
            //toggleActionBarIcon(ActionDrawableState.BURGER, mDrawerToggle, true);
        }
    }

    private void initKeyboard(){
        imm = (InputMethodManager) this.getSystemService(Context.INPUT_METHOD_SERVICE);
    }

    public void closeKeyboard(View v){
        //imm.hideSoftInputFromWindow(v.getWindowToken(), 0);
        imm.hideSoftInputFromWindow(this.toolbarSearchText.getWindowToken(), 0);
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

    public boolean hasRadiusChanged(){
        return this.mHasRadiusChanged;
    }

    public void setHasRadiusChanged(boolean val){
        this.mHasRadiusChanged = val;
    }

    private void setToolbarSearchText(String value){
        this.toolbarSearchText.setText(value);
    }

    private void clearToolbarSearchText(){
        this.toolbarSearchText.setText("");
    }

    public void hideSearchTextAndKeyboard(){
        this.closeKeyboard(null);
        this.hideSearchView();
    }

    public void showSnackBar(){
        Snackbar mSnackBar = Snackbar.with(this)
                .text(Constant.SNACK_BAR_RADIUS_CHANGED)
                .duration(Snackbar.SnackbarDuration.LENGTH_LONG)
                .actionLabel(Constant.SNACK_BAR_ACTION_REFRESH_MAP)
                .actionColor(Color.WHITE)
                .actionListener(new ActionClickListener() {
                    @Override
                    public void onActionClicked(Snackbar snackbar) {
                        fragmentSearch.run();
                        mHasRadiusChanged = false;
                    }
                });

        SnackbarManager.show(mSnackBar, this);

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
    public void onFragmentSettingInteraction(boolean isRadiusChanged) {
        this.setHasRadiusChanged(isRadiusChanged);
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


    /**
     * About dialog
     * **/
    private void showDialogAbout(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_about, null);

        this.initDialogAboutTextView(dialogView);

        builder.setView(dialogView)
                .setPositiveButton(R.string.dialog_ok, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();
    }

    private void initDialogAboutTextView(View v){
        TextView txtTitle = (TextView) v.findViewById(R.id.text_about_title);
        TextView txtVersion = (TextView) v.findViewById(R.id.text_about_version);
        TextView txtDetail = (TextView) v.findViewById(R.id.text_about_detail);

        txtTitle.setText(getString(R.string.about_title));
        txtVersion.setText(getString(R.string.about_version));
        txtDetail.setText(getString(R.string.about_detail));

    }



}
