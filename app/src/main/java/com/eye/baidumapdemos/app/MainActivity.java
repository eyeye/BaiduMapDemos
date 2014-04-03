package com.eye.baidumapdemos.app;

import android.app.Activity;
import android.app.Fragment;
import android.content.Context;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MKEvent;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapFragment;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.Projection;

public class MainActivity extends Activity {

    private final static String TAG = MainActivity.class.getSimpleName();

    BMapManager mBMapManager = null;
    MapFragment map;
    Projection lockProjection = null;
    GraphicsOverlay overlay = null;

    GeoPoint lastRecoredPoint;
    GeoPoint lastSamplePoint;
    int lastRecoredAzimuth = 0;


    public void initEngineManager(Context context) {
        if (mBMapManager == null) {
            mBMapManager = new BMapManager(context);
        }

        if ( !mBMapManager.init(new BaiduListener()) )
        {
            Log.i(TAG, "BMapManager  初始化错误");
        }
        else
        {
            Log.i(TAG, "BMapManager  初始化成功");
        }
    }

    static class BaiduListener implements MKGeneralListener
    {
        @Override
        public void onGetNetworkState(int error)
        {
            if (error == MKEvent.ERROR_NETWORK_CONNECT)
            {
                Log.i(TAG, "MKEvent.ERROR_NETWORK_CONNECT");
            }
            else if (error == MKEvent.ERROR_NETWORK_DATA)
            {
                Log.i(TAG, "MKEvent.ERROR_NETWORK_DATA");
            }
        }

        @Override
        public void onGetPermissionState(int error)
        {
            Log.i(TAG, "onGetPermissionState: " + error);
        }
    }

    @Override
    protected void onResume()
    {
        super.onResume();
        Log.d(TAG, "onResume");


        MapController controller = map.getMapView().getController();
        controller.setCenter(new GeoPoint((int)(39.945 * 1E6), (int)(116.404 * 1E6)));
        controller.setZoom(13);

        if (overlay == null)
        {
            overlay = new GraphicsOverlay(map.getMapView());
            map.getMapView().getOverlays().add(overlay);
        }



//        Geometry geometry = new Geometry();
//        GeoPoint point = new GeoPoint((int)(39.945 * 1E6), (int)(116.404 * 1E6));
//        geometry.setPoint(point, 20);
//
//        Symbol symbol = new Symbol();
//        Symbol.Color color = symbol.new Color();
//        color.red = 0;
//        color.green = 127;
//        color.blue = 255;
//        color.alpha = 80;
//        symbol.setPointSymbol(color);
//
//        Graphic graphic = new Graphic(geometry, symbol);
//
//        overlay.setData(graphic);



        map.getMapView().regMapTouchListner(new MKMapTouchListener()
        {
            @Override
            public void onMapClick(GeoPoint geoPoint)
            {
                Log.i(TAG, "Click Longitude: " + geoPoint.getLongitudeE6() + " Latitude: " + geoPoint.getLatitudeE6());

                if (!map.getMapView().getController().isScrollGesturesEnabled())
                {
                    map.getMapView().getController().setScrollGesturesEnabled(true);
                    lockProjection = null;
                }
            }

            @Override
            public void onMapDoubleClick(GeoPoint geoPoint)
            {
                Log.i(TAG, "DoubleClick Longitude: " + geoPoint.getLongitudeE6() + " Latitude: " + geoPoint.getLatitudeE6());

                if (!map.getMapView().getController().isScrollGesturesEnabled())
                {
                    map.getMapView().getController().setScrollGesturesEnabled(true);
                    lockProjection = null;
                }
            }

            @Override
            public void onMapLongClick(GeoPoint geoPoint)
            {
                Log.i(TAG, "LongClick Longitude: " + geoPoint.getLongitudeE6() + " Latitude: " + geoPoint.getLatitudeE6());
                map.getMapView().getController().setScrollGesturesEnabled(false);
                lockProjection = map.getMapView().getProjection();

                lastRecoredAzimuth = 0;
                lastRecoredPoint = geoPoint;
                lastSamplePoint = geoPoint;

                overlay.removeAll();
                map.getMapView().refresh();

//                if (map.getMapView().getController().isScrollGesturesEnabled())
//                {
//                    map.getMapView().getController().setScrollGesturesEnabled(false);
//                }
//                else
//                {
//                    map.getMapView().getController().setScrollGesturesEnabled(true);
//                }
            }
        });


        map.getMapView().setOnTouchListener(new View.OnTouchListener()
        {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent)
            {
                Log.i(TAG, "Touch X: " + motionEvent.getX() + "Y: " + motionEvent.getY());

                if (lockProjection != null)
                {
                    GeoPoint point = lockProjection.fromPixels((int) motionEvent.getX(), (int) motionEvent.getY());
//                    Log.i(TAG, "Touch Longitude: " + geoPoint.getLongitudeE6() + " Latitude: " + geoPoint.getLatitudeE6());


//                    Log.i(TAG, "distance: " + distance + "      azimuth: " + azimuth);

//                    GeoPoint point = new GeoPoint(geoPoint.getLatitudeE6(), geoPoint.getLongitudeE6());

                    Geometry pointGeometry = new Geometry();
                    pointGeometry.setPoint(point, 4);

                    Symbol pointSymbol = new Symbol();
                    Symbol.Color pointColor = pointSymbol.new Color();
                    pointColor.red = 0;
                    pointColor.green = 255;
                    pointColor.blue = 0;
                    pointColor.alpha = 200;
//                        symbol.setPointSymbol(color);
                    pointSymbol.setLineSymbol(pointColor, 4);

                    Graphic pointGraphic = new Graphic(pointGeometry, pointSymbol);
                    overlay.setData(pointGraphic);



                    double distance = GeoCalculator.distance(lastRecoredPoint, point);
                    int azimuth = GeoCalculator.azimuth(lastSamplePoint, point);
//                    lastSamplePoint = geoPoint;
                    int azimuthDelta = Math.abs(azimuth - lastRecoredAzimuth);

                    if ( (distance > 1000) || ( (azimuthDelta>(30*1E6))&&(distance>10) ) )
                    {
                        Log.i(TAG, "distance: " + distance + "  azimuth: " + azimuth + "  azimuthDelta: " + azimuthDelta);

                        Geometry lineGeometry = new Geometry();
//                        geometry.setPoint(point, 4);
                        lineGeometry.setPolyLine(new GeoPoint[]{lastRecoredPoint, lastSamplePoint});

                        Symbol lineSymbol = new Symbol();
                        Symbol.Color lineColor = lineSymbol.new Color();
                        lineColor.red = 0;
                        lineColor.green = 0;
                        lineColor.blue = 255;
                        lineColor.alpha = 200;
//                        symbol.setPointSymbol(color);
                        lineSymbol.setLineSymbol(lineColor, 4);

                        Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);

                        overlay.setData(lineGraphic);
//                        map.getMapView().refresh();

                        lastRecoredPoint = lastSamplePoint;
                        lastRecoredAzimuth = azimuth;
                    }

                    map.getMapView().refresh();


                    lastSamplePoint = point;
                }

                return false;
            }
        });
    }

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (savedInstanceState == null)
        {
            initEngineManager(getApplicationContext());
            map = MapFragment.newInstance();

//            getFragmentManager().beginTransaction()
//                    .add(R.id.container, new PlaceholderFragment())
//                    .commit();

            getFragmentManager().beginTransaction()
                    .add(R.id.container, map)
                    .commit();
        }


    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();
        if (id == R.id.action_settings) {
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    /**
     * A placeholder fragment containing a simple view.
     */
    public static class PlaceholderFragment extends Fragment {

        public PlaceholderFragment() {
        }

        @Override
        public View onCreateView(LayoutInflater inflater, ViewGroup container,
                Bundle savedInstanceState) {
            View rootView = inflater.inflate(R.layout.fragment_main, container, false);
            return rootView;
        }
    }
}
