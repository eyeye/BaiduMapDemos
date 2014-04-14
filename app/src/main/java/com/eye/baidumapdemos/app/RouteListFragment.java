package com.eye.baidumapdemos.app;

import android.app.Activity;
import android.app.Fragment;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView;
import android.widget.AdapterView;
import android.widget.CursorAdapter;
import android.widget.ListAdapter;
import android.widget.SimpleCursorAdapter;

import com.eye.baidumapdemos.dao.DaoMaster;
import com.eye.baidumapdemos.dao.DaoSession;
import com.eye.baidumapdemos.dao.PointDao;
import com.eye.baidumapdemos.dao.RouteDao;

/**
 * A fragment representing a list of Items.
 * <p />
 * Large screen devices (such as tablets) are supported by replacing the ListView
 * with a GridView.
 * <p />
 * Activities containing this fragment MUST implement the {@link OnFragmentInteractionListener}
 * interface.
 */
public class RouteListFragment
        extends Fragment
        implements AbsListView.OnItemClickListener//,
//        LoaderManager.LoaderCallbacks<Cursor>
{

    private final static String TAG = RouteListFragment.class.getSimpleName();


    // TODO: Rename parameter arguments, choose names that match
    // the fragment initialization parameters, e.g. ARG_ITEM_NUMBER
    private static final String ARG_PARAM1 = "param1";
    private static final String ARG_PARAM2 = "param2";

    // TODO: Rename and change types of parameters
    private String mParam1;
    private String mParam2;

    private OnFragmentInteractionListener mListener;

    /**
     * The fragment's ListView/GridView.
     */
    private AbsListView mListView;

    /**
     * The Adapter which will be used to populate the ListView/GridView with
     * Views.
     */
//    private ListAdapter mAdapter;


    private SQLiteDatabase      db;
    private DaoMaster           daoMaster;
    private DaoSession          daoSession;
    private RouteDao            routeDao;
    private PointDao            pointDao;
    private Cursor              cursor;
    private SimpleCursorAdapter adapter;

    // TODO: Rename and change types of parameters
    public static RouteListFragment newInstance(String param1, String param2) {
        RouteListFragment fragment = new RouteListFragment();
        Bundle args = new Bundle();
        args.putString(ARG_PARAM1, param1);
        args.putString(ARG_PARAM2, param2);
        fragment.setArguments(args);
        return fragment;
    }

    /**
     * Mandatory empty constructor for the fragment manager to instantiate the
     * fragment (e.g. upon screen orientation changes).
     */
    public RouteListFragment() {
    }

    @Override
    public void onActivityCreated(Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        Log.i(TAG, "onActivityCreated");
    }

    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.i(TAG, "onCreate");

        if (getArguments() != null) {
            mParam1 = getArguments().getString(ARG_PARAM1);
            mParam2 = getArguments().getString(ARG_PARAM2);
        }

        // TODO: Change Adapter to display your content
//        mAdapter = new ArrayAdapter<DummyContent.DummyItem>(getActivity(),
//                                                            android.R.layout.simple_list_item_1, android.R.id.text1, DummyContent.ITEMS);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this.getActivity(), "routes-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        routeDao = daoSession.getRouteDao();
        pointDao = daoSession.getPointDao();

//        Route route = new Route(null, new Date(), new Date());
//        routeDao.insert(route);

//        String orderBy = RouteDao.Properties.StartTime.columnName + " COLLATE LOCALIZED ASC";
//        cursor = db.query(routeDao.getTablename(),
//                          routeDao.getAllColumns(),
//                          null, null, null, null,
//                          orderBy);
//
//        String[] from = {RouteDao.Properties.StartTime.columnName, RouteDao.Properties.EndTime.columnName};
//        int[] to = { android.R.id.text1, android.R.id.text2 };
//        adapter = new SimpleCursorAdapter(this.getActivity(), android.R.layout.simple_list_item_2, cursor,
//                                                              from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );

//        try {
//            Log.i(TAG, "initLoader begin");
//            getLoaderManager().initLoader(0, null, this);
//            Log.i(TAG, "initLoader finish");
//        }
//        catch (NullPointerException e)
//        {
//            Log.i(TAG, "initLoader error: " + e);
//        }

    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        Log.i(TAG, "onCreateView");

        View view = inflater.inflate(R.layout.fragment_routelist, container, false);

//        // Set the adapter
//        mListView = (AbsListView) view.findViewById(android.R.id.list);
//        ((AdapterView<ListAdapter>) mListView).setAdapter(adapter);
//
//        // Set OnItemClickListener so we can be notified on item clicks
//        mListView.setOnItemClickListener(this);

        return view;
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.i(TAG, "onResume");
    }

    @Override
    public void onPause() {
        super.onPause();
        Log.i(TAG, "onPause");
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.i(TAG, "onStop");
    }

    @Override
    public void onStart()
    {
        super.onStart();
        Log.i(TAG, "onStart");


        String orderBy = RouteDao.Properties.StartTime.columnName + " COLLATE LOCALIZED ASC";
        cursor = db.query(routeDao.getTablename(),
                                 routeDao.getAllColumns(),
                                 null, null, null, null,
                                 orderBy);

        String[] from = {RouteDao.Properties.StartTime.columnName, RouteDao.Properties.EndTime.columnName};
        int[] to = { android.R.id.text1, android.R.id.text2 };
        adapter = new SimpleCursorAdapter(this.getActivity(), android.R.layout.simple_list_item_2, cursor,
                                                 from, to, CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER );


        // Set the adapter
        mListView = (AbsListView) getActivity().findViewById(android.R.id.list);
        ((AdapterView<ListAdapter>) mListView).setAdapter(adapter);

        // Set OnItemClickListener so we can be notified on item clicks
        mListView.setOnItemClickListener(this);

//        try
//        {
//            getLoaderManager().restartLoader(0, null, this);
//        }
//        catch (NullPointerException e)
//        {
//            e.printStackTrace();
//        }
    }

    @Override
    public void onAttach(Activity activity) {
        super.onAttach(activity);

        Log.i(TAG, "onAttach");

        try {
            mListener = (OnFragmentInteractionListener) activity;
        } catch (ClassCastException e) {
            throw new ClassCastException
                    (
                            activity.toString()
                + " must implement OnFragmentInteractionListener"
                    );
        }

//        try {
//            getLoaderManager().initLoader(0, null, this);
//        }
//        catch (NullPointerException e)
//        {
//            e.printStackTrace();
//        }
//
//        getLoaderManager().restartLoader(0, null, this);
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.i(TAG, "onDetach");
        mListener = null;
    }


    @Override
    public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
        if (null != mListener) {
            // Notify the active callbacks interface (the activity, if the
            // fragment is attached to one) that an item has been selected.
            mListener.onFragmentInteraction(id);
        }
    }

    /**
     * The default content for this Fragment has a TextView that is shown when
     * the list is empty. If you would like to change the text, call this method
     * to supply the text it should use.
     */
//    public void setEmptyText(CharSequence emptyText) {
//        View emptyView = mListView.getEmptyView();
//
//        if (emptyText instanceof TextView) {
//            ((TextView) emptyView).setText(emptyText);
//        }
//    }

    /**
    * This interface must be implemented by activities that contain this
    * fragment to allow an interaction in this fragment to be communicated
    * to the activity and potentially other fragments contained in that
    * activity.
    * <p>
    * See the Android Training lesson <a href=
    * "http://developer.android.com/training/basics/fragments/communicating.html"
    * >Communicating with Other Fragments</a> for more information.
    */
    public interface OnFragmentInteractionListener {
        // TODO: Update argument type and name
        public void onFragmentInteraction(long id);
    }
//
//
//
//    @Override
//    public Loader onCreateLoader(int i, Bundle bundle) {
//        Log.i(TAG, "onCreateLoader");
//        String orderBy = RouteDao.Properties.StartTime.columnName + " COLLATE LOCALIZED ASC";
//        return new CursorLoader(getActivity(), null, null, null, null, orderBy);
//    }
//
//    @Override
//    public void onLoadFinished(Loader<Cursor> cursorLoader, Cursor cursor) {
//        Log.i(TAG, "onLoadFinished");
//        adapter.swapCursor(cursor);
//    }
//
//
//    @Override
//    public void onLoaderReset(Loader loader) {
//        Log.i(TAG, "onLoaderReset");
//        adapter.swapCursor(null);
//    }
}
