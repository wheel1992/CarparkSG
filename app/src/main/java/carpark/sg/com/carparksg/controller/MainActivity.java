package carpark.sg.com.carparksg.controller;

import android.app.Activity;
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
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;
import android.util.DisplayMetrics;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.support.v4.widget.DrawerLayout;
import android.view.ViewTreeObserver;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.AutoCompleteTextView;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.TextView;

import org.json.JSONException;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.lang.reflect.Field;
import java.util.ArrayList;
import java.util.Collections;

import carpark.sg.com.carparksg.R;
import carpark.sg.com.model.CarparkList;
import carpark.sg.com.model.Constant;
import carpark.sg.com.model.Favourite;
import carpark.sg.com.model.FavouriteList;
import carpark.sg.com.model.History;
import carpark.sg.com.model.HistoryList;
import carpark.sg.com.model.Preferences;


public class MainActivity extends AppCompatActivity implements NavigationDrawerFragment.NavigationDrawerCallbacks,
        FragmentFavourite.OnFragmentInteractionListener,
        FragmentRecent.OnFragmentInteractionListener,
        FragmentSearch.OnFragmentInteractionListener,
        FragmentCarparkDetail.OnFragmentInteractionListener{

    /**
     * Fragment managing the behaviors, interactions and presentation of the navigation drawer.
     */
    private DrawerLayout mDrawerLayout;
    private NavigationDrawerFragment mNavigationDrawerFragment;
    private LinearLayout searchContainer;
    private AutoCompleteTextView toolbarSearchText;
    private ImageView toolbarSearchClear;

    private HistoryList mHistoryList;
    private FavouriteList mFavouriteList;
    private Preferences mPreferences;
    private SearchDropDownAdapter mSearchAdapter;
    private boolean searchDropDownListShown;

    private Menu mMenu;
    private InputMethodManager imm;

    private final String TAG_GEOCODE_IO_EXCEPTION = "Geocoder IO Exception";

    private FragmentManager fragmentManager;
    private FragmentTransaction fragmentTransaction;
    private FragmentSearch fragmentSearch;
    private FragmentCarparkDetail fragmentCarparkDetail;


    /**
     * Used to store the last screen title. For use in {@link #restoreActionBar()}.
     */
    private CharSequence mTitle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        // Set up fragment manager
        this.initFragmentManager();

        //When call this, it will also call navigation drawer fragment onCreate
        setContentView(R.layout.activity_main);

        // Set up Toolbar
        Toolbar mToolBar = (Toolbar) findViewById(R.id.custom_toolbar);
        setSupportActionBar(mToolBar);
        adjustToolbarLogo();
        displayToolbarLogo(true);
        restoreActionBar();

        // Custom preference
        initPreferences();
        // History list
        initHistoryList();
        populateHistoryList();
        // Favourite list
        initFavouriteList();
        populateFavouriteList();

        // Keyboard
        initKeyboard();

        mNavigationDrawerFragment = (NavigationDrawerFragment)
                getSupportFragmentManager().findFragmentById(R.id.navigation_drawer);
        mTitle = getTitle();

        // Set up the drawer.
        mDrawerLayout = (DrawerLayout) findViewById(R.id.drawer_layout);
        mNavigationDrawerFragment.setUp(
                R.id.navigation_drawer,
                mDrawerLayout);

        searchContainer = (LinearLayout) findViewById(R.id.search_container);
        toolbarSearchText = (AutoCompleteTextView) findViewById(R.id.search_auto_complete_text);
        toolbarSearchClear = (ImageView) findViewById(R.id.search_clear);
        initSearchContainer();

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
                //openFragment = FragmentSetting.newInstance(param1, param2);
                break;
        }

        displayFragment(openFragment, fragName);

    }

    public void initFragmentManager(){
        this.fragmentManager = getSupportFragmentManager();
    }

    public void displayFragment(Fragment newFrag, String name){
        System.out.println("displaying a fragment..." + name);
        if(newFrag != null){
            if(fragmentManager == null){
                this.initFragmentManager();
            }

            this.fragmentTransaction = fragmentManager.beginTransaction();

            // remove the fragment from manager first if found
            Fragment searchFrag = fragmentManager.findFragmentByTag(name);
            if(searchFrag != null){
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

        //noinspection SimplifiableIfStatement

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
        Log.d(this.getClass().getName(), "onBackPressed - back button pressed");

        //closeKeyboard(toolbarSearchText);
        if(fragmentManager != null){
            //by default, the last fragment to be shown is favourite.
            //hence, the least number of backstack is 1
            //cos favourite is inside the backstack

            if(fragmentManager.getBackStackEntryCount() > 1){
                for(int i = 0; i<fragmentManager.getBackStackEntryCount(); i++){
                    System.out.println(i + ". -" + fragmentManager.getBackStackEntryAt(i));
                }
                fragmentManager.popBackStackImmediate();

            }else{
                //will execute exit program
                //with a delay

                //    System.out.println("super.onBackPressed");
                //     super.onBackPressed();
            }
        }else{
            System.out.println("super.onBackPressed");
            super.onBackPressed();
        }


    }

    @Override
    public void onPause(){
        super.onPause();
        this.clearHistoryList();
    }

    @Override
    public void onDestroy()
    {
        super.onDestroy();
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
            System.out.println("JSON string from Preferences - " + historyJson);
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
            String favouriteValue = mPreferences.getFavourite();
            System.out.println("JSON string from Favourite - " + favouriteValue);
            this.mFavouriteList.parseStringToMap(favouriteValue);
        }catch(Exception e){
            //print error
            e.printStackTrace();
        }
    }

    public void addNewHistory(String name, String lat, String lng){
        History element = new History(name, lat, lng);
        this.mHistoryList.addHistory(element);
    }

    public void addNewFavourite(String id, String name, String lat, String lng){
        Favourite element = new Favourite(id, name, lat, lng);
        this.mFavouriteList.addFavourite(element);
    }

    public void removeHistory(String name, String lat, String lng){
        History element = new History(name, lat, lng);
        this.mHistoryList.removeHistory(element);
    }

    public void removeFavourite(String id, String name, String lat, String lng){
        Favourite element = new Favourite(id, name, lat, lng);
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

    public boolean isFavouriteExist(String id, String address, String lat, String lng){
        return this.mFavouriteList.isFavouriteExist(id, address, lat, lng);
    }


    public void refreshHistoryList(){
        this.saveHistoryList();
        this.populateHistoryList();
    }


    private void initSearchContainer(){

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
                searchCarpark(Constant.SEARCH_HDB_NEARBY_CARPARK_USING_ADDRESS, v.getText().toString(), "", "");
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
                searchCarpark(Constant.SEARCH_HDB_NEARBY_CARPARK_USING_COORDINATE, address, lat, lng);

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

    private void initSearchAdapter(){
        mSearchAdapter = new SearchDropDownAdapter(this, R.layout.dropdown_list_row_layout, mHistoryList.getList(), this);
    }

    public void refreshSearchAdapter(){
        this.initSearchAdapter();
        this.toolbarSearchText.setAdapter(null);
        this.toolbarSearchText.setAdapter(this.mSearchAdapter);
    }

    public void searchCarpark(int type, String address, String lat, String lng){
        if(fragmentSearch != null) {
            this.removeFragment(fragmentSearch);
        }

        fragmentSearch = FragmentSearch.newInstance(type, address, lat, lng);
        displayFragment(fragmentSearch, Constant.FRAGMENT_SEARCH_NAME);
    }

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



    private String getToolbarSearchText(){
        return this.toolbarSearchText.getText().toString();
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




    /**
     * A placeholder fragment containing a simple view.
     */

    public static class PlaceholderFragment extends Fragment {
        /**
         * The fragment argument representing the section number for this
         * fragment.
         */
        private static final String ARG_SECTION_NUMBER = "section_number";
        private RecyclerView mRecyclerView;
        private RecyclerView.Adapter mAdapter;
        private RecyclerView.LayoutManager mLayoutManager;
        private CarparkList mCarparkList;

        /**
         * Returns a new instance of this fragment for the given section
         * number.
         */
        public static PlaceholderFragment newInstance(int sectionNumber) {
            PlaceholderFragment fragment = new PlaceholderFragment();
            Bundle args = new Bundle();
            args.putInt(ARG_SECTION_NUMBER, sectionNumber);
            fragment.setArguments(args);
            return fragment;
        }

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                                 Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);

            mRecyclerView = (RecyclerView) rootView.findViewById(R.id.recycler_view_favourite);
            mRecyclerView.setHasFixedSize(true);
            mLayoutManager = new LinearLayoutManager(getActivity());
            mRecyclerView.setLayoutManager(mLayoutManager);


            mAdapter = new RecyclerViewAdapter();
            mRecyclerView.setAdapter(mAdapter);
            mRecyclerView.addItemDecoration(new DividerItemDecoration(getActivity(), DividerItemDecoration.VERTICAL_LIST));

            return rootView;
        }

        @Override
        public void onAttach(Activity activity) {
            super.onAttach(activity);
            ((MainActivity) activity).onSectionAttached(
                    getArguments().getInt(ARG_SECTION_NUMBER));
        }


    }

}
