package carpark.sg.com.carparksg.controller;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.app.AppCompatDialog;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;

import com.appyvet.rangebar.RangeBar;

import org.json.JSONException;

import java.io.IOException;
import java.util.HashMap;

import carpark.sg.com.carparksg.R;
import carpark.sg.com.carparksg.logic.Parser;
import carpark.sg.com.model.Constant;
import carpark.sg.com.model.EnumDialog;
import carpark.sg.com.model.EnumSetting;
import carpark.sg.com.model.Preferences;
import carpark.sg.com.model.Setting;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentSetting.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentSetting#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentSetting extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_RADIUS = "radius";
   // private static final String ARG_PARAM2 = "param2";

    private int mParamRadius;
    private int currentSelectedRadius = 0;
    private int oldRadius = 0;

    private boolean hasRadiusChange = false;

    private OnFragmentInteractionListener mListener;

    private MainActivity mActivity;
    private RangeBar mRangeBar;
    private Setting mSetting;
    private Preferences mPref;

    private LinearLayout mParentLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mRecyclerLayoutManager;

    // Store all the setting title and value
    private HashMap<EnumSetting, String> mSettingMap;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param setting Passed in Setting object.
     * @return A new instance of fragment FragmentSetting.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentSetting newInstance(Setting setting) {
        FragmentSetting fragment = new FragmentSetting();
        Bundle args = new Bundle();
        args.putInt(ARG_RADIUS, setting.getRadius());
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentSetting() {
        // Required empty public constructor
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        if (getArguments() != null) {
            this.mParamRadius = getArguments().getInt(ARG_RADIUS);
            this.oldRadius = this.mParamRadius;
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_setting, container, false);

        //this.initRangeBar(rootView);
        this.initSettingLayout(rootView);
        this.initPreference();
        this.initSettingMap();
        this.initSetting();
        this.addKeyValueToSettingMap(EnumSetting.SETTING_RADIUS, Parser.convertIntegerToString(this.mParamRadius));

        this.initRecyclerView(rootView);
        this.setRecyclerSettingView(this.mSettingMap);

        getMainActivity().toggleDisplayToolbarLogo(false);
        getMainActivity().toggleDisplayToolbarTitle(true);
        getMainActivity().setToolbarTitle(Constant.FRAGMENT_SETTING_TITLE);

        // Inflate the layout for this fragment
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

    private View.OnTouchListener mViewOnTouchListener = new View.OnTouchListener(){
        @Override
        public boolean onTouch(View v, MotionEvent event) {
            if(v.getId() != R.id.search_auto_complete_text){
                getMainActivity().hideSearchTextAndKeyboard();
                return true;
            }
            return false;
        }
    };

    private void initMainActivity(MainActivity activity){
        this.mActivity = activity;
    }

    private MainActivity getMainActivity(){
        return this.mActivity;
    }

    private void initSettingLayout(View v){
        this.mParentLayout = (LinearLayout) v.findViewById(R.id.layout_parent_setting);
        //this.mParentLayout.setOnClickListener(this.mViewOnClickListener);
        this.mParentLayout.setOnTouchListener(this.mViewOnTouchListener);
    }

    private void initPreference(){
        this.mPref = Preferences.getInstance(getMainActivity());
    }

    private void initSettingMap(){
        this.mSettingMap = new HashMap<EnumSetting, String>();
    }

    private void initSetting(){
        this.mSetting = Setting.getInstance();
    }

    private void addKeyValueToSettingMap(EnumSetting key, String value){
        this.mSettingMap.put(key, value);
    }

    private void saveSetting(){
        System.out.println("FragmentSetting - Save Setting");
        this.getMainActivity().setSettingRadius(mParamRadius);
        this.getMainActivity().saveSetting();
    }

    private void updateArgumentRadius(int value){
        this.getArguments().putInt(ARG_RADIUS, value);
    }

    private void initRecyclerAdapter(HashMap<EnumSetting, String> mMap){
        mRecyclerAdapter = new RecyclerViewSettingAdapter(getMainActivity(), mMap);
        ((RecyclerViewSettingAdapter)mRecyclerAdapter).setOnItemClickListener(new RecyclerViewSettingAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                System.out.println("FragmentSetting - Click position -> " + position);
                switch (position) {
                    case 0:
                        showDialog(EnumDialog.DIALOG_RADIUS);
                        getMainActivity().hideSearchTextAndKeyboard();
                        //setRangeBarValue(mParamRadius);
                        break;

                }
            }
        });
    }

    private void initRecyclerView(View v){
        this.mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_setting);
        //this.mRecyclerView.setOnClickListener(this.mViewOnClickListener);
        this.mRecyclerView.setOnTouchListener(this.mViewOnTouchListener);
    }

    private void setRecyclerSettingView(HashMap<EnumSetting, String> mMap){
        this.mRecyclerLayoutManager = new LinearLayoutManager(getMainActivity());
        this.mRecyclerView.setLayoutManager(this.mRecyclerLayoutManager);

        this.initRecyclerAdapter(mMap);
        this.mRecyclerView.setAdapter(this.mRecyclerAdapter);
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(getMainActivity(), DividerItemDecoration.VERTICAL_LIST));
    }

    public void refreshSettingAdapter(HashMap<EnumSetting, String> mMap){
        this.initRecyclerAdapter(mMap);
        this.mRecyclerView.setAdapter(null);
        this.mRecyclerView.setAdapter(this.mRecyclerAdapter);
        //this.mRecyclerAdapter.notifyDataSetChanged();
    }

    /**
     * Custom Dialogs
     * **/
    private void showDialog(EnumDialog dg){
        switch (dg){
            case DIALOG_RADIUS:
                showDialogRadius(this.getMainActivity());
                break;

        }
    }

    private void showDialogRadius(Context context){
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        LayoutInflater inflater = this.getMainActivity().getLayoutInflater();

        final View dialogView = inflater.inflate(R.layout.dialog_radius, null);

        this.initRangeBar(dialogView);
        this.setRangeBarValue(mParamRadius);

        builder.setView(dialogView)
                .setPositiveButton(R.string.dialog_set, new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int id) {
                        int newValue = getRangeBarValue();
                        System.out.println("FragmentSetting - range bar new value -> " + newValue);

                        if (newValue != oldRadius) {
                            mParamRadius = newValue;
                            oldRadius = newValue;
                            updateArgumentRadius(newValue);
                            addKeyValueToSettingMap(EnumSetting.SETTING_RADIUS, Parser.convertIntegerToString(newValue));
                            saveSetting();
                            refreshSettingAdapter(mSettingMap);

                            hasRadiusChange = true;

                        } else {
                            hasRadiusChange = false;
                        }

                        mListener.onFragmentSettingInteraction(hasRadiusChange);

                    }
                })
                .setNegativeButton(R.string.dialog_cancel, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.dismiss();
                    }
                });

        builder.create().show();

    }

    private void initRangeBar(View v){
        this.mRangeBar = (RangeBar) v.findViewById(R.id.rangebar_radius);
        this.mRangeBar.setOnRangeBarChangeListener(new RangeBar.OnRangeBarChangeListener() {
            @Override
            public void onRangeChangeListener(RangeBar rangeBar, int leftPinIndex,
                                              int rightPinIndex, String leftPinValue, String rightPinValue) {
                currentSelectedRadius = Parser.convertStringToInteger(rightPinValue);
            }
        });
    }

    private void setRangeBarValue(int value){
        float val = Parser.convertIntegerToFloat(value);
        this.mRangeBar.setSeekPinByValue(val);
    }

    private int getRangeBarValue(){
        return this.currentSelectedRadius;
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
        public void onFragmentSettingInteraction(boolean isRadiusChanged);
    }

    public void onButtonPressed(boolean val) {
        if (mListener != null) {
            mListener.onFragmentSettingInteraction(val);
        }
    }


}
