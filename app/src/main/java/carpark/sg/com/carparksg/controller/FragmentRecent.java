package carpark.sg.com.carparksg.controller;

import android.app.Activity;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.view.LayoutInflater;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.widget.LinearLayout;
import android.widget.TextView;

import com.nispok.snackbar.Snackbar;
import com.nispok.snackbar.SnackbarManager;
import com.nispok.snackbar.enums.SnackbarType;
import com.nispok.snackbar.listeners.ActionClickListener;

import java.util.HashMap;
import java.util.List;

import carpark.sg.com.carparksg.R;
import carpark.sg.com.carparksg.logic.Parser;
import carpark.sg.com.model.Constant;
import carpark.sg.com.model.Favourite;
import carpark.sg.com.model.FavouriteList;
import carpark.sg.com.model.History;
import carpark.sg.com.model.HistoryList;


/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link FragmentRecent.OnFragmentInteractionListener} interface
 * to handle interaction events.
 * Use the {@link FragmentRecent#newInstance} factory method to
 * create an instance of this fragment.
 */
public class FragmentRecent extends Fragment {
    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private final String LOG_TAG = "Fragment Recent log";

    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private MainActivity mActivity;
    private FragmentSearch fragmentSearch;
    private TextView txtError;
    private HistoryList mHistoryList;

    private LinearLayout mParentLayout;
    private RecyclerView mRecyclerView;
    private RecyclerView.Adapter mRecyclerAdapter;
    private RecyclerView.LayoutManager mRecyclerLayoutManager;
    private OnFragmentInteractionListener mListener;

    /**
     * Use this factory method to create a new instance of
     * this fragment using the provided parameters.
     *
     * @param param1 Parameter 1.
     * @param param2 Parameter 2.
     * @return A new instance of fragment FragmentRecent.
     */
    // TODO: Rename and change types and number of parameters
    public static FragmentRecent newInstance(String param1, String param2) {
        FragmentRecent fragment = new FragmentRecent();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    public FragmentRecent() {
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
        View rootView = inflater.inflate(R.layout.fragment_recent, container, false);

        this.initRecentLayout(rootView);
        this.initHistoryList();
        this.initRecyclerView(rootView);

        int historySize = this.getHistoryListSize();
        if(historySize > 0){
            this.setRecyclerRecentView(this.mHistoryList.getList());
            this.showRecyclerRecentView();
        }else{
            this.hideRecyclerRecentView();
            this.showSnackBarMessage(Constant.ERROR_NO_RECENT);
        }

        getMainActivity().toggleDisplayToolbarLogo(false);
        getMainActivity().toggleDisplayToolbarTitle(true);
        getMainActivity().setToolbarTitle(Constant.FRAGMENT_RECENT_TITLE);

        return rootView;
    }



    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);
        try {
            mListener = (OnFragmentInteractionListener) activity;
            this.initMainActivity((MainActivity)activity);
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

    public void refreshRecentAdapter(){
        this.initRecyclerAdapter(this.mHistoryList.getList());
        this.mRecyclerView.setAdapter(null);
        this.mRecyclerView.setAdapter(this.mRecyclerAdapter);
    }

    public HistoryList getHistoryList(){
        return this.mHistoryList;
    }

    private void initMainActivity(MainActivity activity){
        this.mActivity = activity;
    }

    private MainActivity getMainActivity(){
        return this.mActivity;
    }

    private void initRecentLayout(View v){
        this.mParentLayout = (LinearLayout) v.findViewById(R.id.layout_parent_recent);
        this.mParentLayout.setOnTouchListener(this.mViewOnTouchListener);
    }

    private void initHistoryList(){
        this.mHistoryList = HistoryList.getInstance();
    }

    private int getHistoryListSize(){
        return this.mHistoryList.getSize();
    }

    private void initRecyclerAdapter(List<History> mList){
        mRecyclerAdapter = new RecyclerViewRecentAdapter(getMainActivity(), this, mList);
        ((RecyclerViewRecentAdapter)mRecyclerAdapter).setOnItemClickListener(new RecyclerViewRecentAdapter.OnItemClickListener() {
            @Override
            public void onItemClick(View view, int position) {
                String address = getHistoryList().getList().get(position).getName();
                String lat = getHistoryList().getList().get(position).getLatitude();
                String lng = getHistoryList().getList().get(position).getLongitude();
                String radius = Parser.convertIntegerToString(getMainActivity().getSettingRadius());

                getMainActivity().displayFragmentSearch(Constant.SEARCH_HDB_NEARBY_CARPARK_USING_COORDINATE, address, lat, lng, radius);
            }
        });
    }

    private void initRecyclerView(View v){
        this.mRecyclerView = (RecyclerView) v.findViewById(R.id.recycler_view_recent);
        this.mRecyclerView.setOnTouchListener(this.mViewOnTouchListener);
    }

    private void setRecyclerRecentView(List<History> mList){
        this.mRecyclerLayoutManager = new LinearLayoutManager(getMainActivity());
        this.initRecyclerAdapter(mList);
        this.mRecyclerView.setLayoutManager(this.mRecyclerLayoutManager);
        this.mRecyclerView.setAdapter(this.mRecyclerAdapter);
        this.mRecyclerView.addItemDecoration(new DividerItemDecoration(getMainActivity(), DividerItemDecoration.VERTICAL_LIST));
    }

    private void showRecyclerRecentView(){
        this.mRecyclerView.setVisibility(View.VISIBLE);
    }

    private void hideRecyclerRecentView(){
        this.mRecyclerView.setVisibility(View.GONE);
    }

    private void showSnackBarMessage(String message){
        Snackbar mSnackBar = Snackbar.with(getMainActivity())
                .type(SnackbarType.MULTI_LINE)
                .text(message)
                .duration(Snackbar.SnackbarDuration.LENGTH_LONG);

        SnackbarManager.show(mSnackBar, getMainActivity());
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
        public void onFragmentRecentInteraction(Uri uri);
    }

    public void onButtonPressed(Uri uri) {
        if (mListener != null) {
            mListener.onFragmentRecentInteraction(uri);
        }
    }

}
