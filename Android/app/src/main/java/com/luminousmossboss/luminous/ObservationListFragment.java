package com.luminousmossboss.luminous;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RadioButton;

import com.luminousmossboss.luminous.adapter.ObservationListAdapter;
import com.luminousmossboss.luminous.model.ListItem;
import com.luminousmossboss.luminous.model.Observation;
import com.luminousmossboss.luminous.model.Separator;

import java.io.File;
import java.util.ArrayList;

import info.hoang8f.android.segmented.SegmentedGroup;

/**
 *
 * Created by Brian on 2/1/2015.
 */

public class ObservationListFragment extends Fragment implements View.OnClickListener, BackButtonInterface{

    private Context context;
    private String mTitle;

    private ListView mDrawerList;
    private View rootView;
    private ViewGroup container;

    private ObservationListAdapter adapter;
    private ArrayList<ListItem> listItems;

    private ArrayList<Observation> selectedObservations;
    private DbHandler db;

    private RadioButton mTabPending;
    private RadioButton mTabSynced;


    public ObservationListFragment(){}

    //static method used for setting arguments;

    @Override
    public Boolean allowedBackPressed() {
        return true;
    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        rootView = inflater.inflate(R.layout.fragment_observationlist, container, false);
        this.container = container;
        final Activity activity = getActivity();
        this.db = new DbHandler(activity);
        initList(rootView, container, 0);



        //To be implemented for selecting individual observations
        this.mDrawerList.setOnItemClickListener(new OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                if(activity instanceof MainActivity && listItems.get(position) instanceof Observation) {
                    CharSequence fragTitle = listItems.get(position).getTitle();
                    Fragment fragment = null;
                    fragment = ObservationFragment.newInstance((Observation)listItems.get(position));
                    ((MainActivity) activity).setTitle(fragTitle);
                    ((MainActivity) activity).displayView(fragment);
                }
            }
        });

        // Handle Button Events
        mTabPending = (RadioButton) rootView.findViewById(R.id.tab_pending);
        mTabPending.setOnClickListener(this);
        mTabSynced = (RadioButton) rootView.findViewById(R.id.tab_synced);
        mTabSynced.setOnClickListener(this);

        // Set tab color
        SegmentedGroup tab_host = (SegmentedGroup) rootView.findViewById(R.id.tab_host);
        tab_host.setTintColor(getResources().getColor(R.color.fieldguide_button));

        activity.setTitle(MainActivity.OBSERVATION_LIST_POSITION);
        return rootView;
    }

    /**
     * Handle click events from tab elements
     */
    @Override
    public void onClick(View v) {
        switch (v.getId())
        {
            case R.id.tab_pending:
                initList(rootView, container, 0);
                break;
            case R.id.tab_synced:
                initList(rootView, container, 1);
                break;
        }
    }

    /**
     * Initial Items in ListView
     * @param rootView
     * @param container
     * @param status
     */
    private void initList(View rootView, ViewGroup container, int status) {


        this.mDrawerList = (ListView) rootView.findViewById(R.id.fragment_list);
        this.context = container.getContext();
        this.listItems = new ArrayList<ListItem>();
        ArrayList<ListItem> sileneList = new ArrayList<ListItem>();
        sileneList.add(new Separator("Silene Aculis"));
        ArrayList<ListItem> unknownList = new ArrayList<ListItem>();
        unknownList.add(new Separator("Unknown"));

        //Cursor cursor = db.getAllObservation();
        Cursor cursor = db.getObservationBySyncStatus(status);

        if (cursor.moveToFirst())
            do{
                Uri iconUri = Uri.fromFile(new File(cursor.getString(cursor.getColumnIndex(DbHandler.KEY_PHOTO_PATH))));
                String date= cursor.getString(cursor.getColumnIndex(DbHandler.KEY_TIME_TAKEN));

                Double latitude = cursor.getDouble(cursor.getColumnIndex(DbHandler.KEY_LATITUDE));
                Double longitude = cursor.getDouble(cursor.getColumnIndex(DbHandler.KEY_LONGITUDE));

                int is_silene = cursor.getInt(cursor.getColumnIndex(DbHandler.KEY_IS_SILENE));
                if (is_silene == 1)
                    sileneList.add(new Observation("Silene Aculis", iconUri,date,latitude, longitude));
                else
                    unknownList.add(new Observation("Unknown", iconUri,date,latitude, longitude));

            }while(cursor.moveToNext());

        if (unknownList.size() > 1)
            listItems.addAll(unknownList);
        if (sileneList.size() > 1)
            listItems.addAll(sileneList);

        adapter = new ObservationListAdapter(context, listItems);
        mDrawerList.setAdapter(adapter);
    }
}
