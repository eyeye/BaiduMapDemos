package com.eye.baidumapdemos.app;

import android.app.Activity;
import android.content.Intent;
import android.database.sqlite.SQLiteDatabase;
import android.os.Bundle;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;

import com.baidu.mapapi.map.Geometry;
import com.baidu.mapapi.map.Graphic;
import com.baidu.mapapi.map.GraphicsOverlay;
import com.baidu.mapapi.map.MKMapTouchListener;
import com.baidu.mapapi.map.MapController;
import com.baidu.mapapi.map.MapFragment;
import com.baidu.mapapi.map.Symbol;
import com.baidu.platform.comapi.basestruct.GeoPoint;
import com.baidu.platform.comapi.map.Projection;
import com.eye.baidumapdemos.dao.DaoMaster;
import com.eye.baidumapdemos.dao.DaoSession;
import com.eye.baidumapdemos.dao.Point;
import com.eye.baidumapdemos.dao.PointDao;
import com.eye.baidumapdemos.dao.Route;
import com.eye.baidumapdemos.dao.RouteDao;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;


public class RoutePlannerActivity extends Activity {
    private final static String TAG = RoutePlannerActivity.class.getSimpleName();

    MapFragment     mapFragment    = null;
    Projection      lockProjection = null;
    GraphicsOverlay overlay        = null;

    GeoPoint lastRecoredPoint;
    GeoPoint lastSamplePoint;
    int                 lastRecoredAzimuth = 0;
    ArrayList<GeoPoint> routeArray         = new ArrayList<GeoPoint>();

    private SQLiteDatabase db;
    private DaoMaster      daoMaster;
    private DaoSession     daoSession;
    private RouteDao       routeDao;
    private PointDao       pointDao;

    private Route route = null;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_route_planner);

        DaoMaster.DevOpenHelper helper = new DaoMaster.DevOpenHelper(this, "routes-db", null);
        db = helper.getWritableDatabase();
        daoMaster = new DaoMaster(db);
        daoSession = daoMaster.newSession();

        routeDao = daoSession.getRouteDao();
        pointDao = daoSession.getPointDao();


        final Intent intent = getIntent();
        long id = intent.getLongExtra("ROUTE_ID", 0);
        if (id != 0)
        {
            Log.i(TAG, "Load route points");
            route = routeDao.load(id);
        }
        else
        {
            route = null;
        }

        if (savedInstanceState == null) {
            mapFragment = new MapFragment();

            getFragmentManager().beginTransaction()
                                .add(R.id.container, mapFragment)
                                .commit();
        }


    }

    @Override
    protected void onResume() {
        super.onResume();
        Log.d(TAG, "onResume");

        MapController controller = mapFragment.getMapView().getController();
        controller.setCenter(new GeoPoint((int) (39.945 * 1E6), (int) (116.404 * 1E6)));
        controller.setZoom(13);

        if (overlay == null) {
            overlay = new GraphicsOverlay(mapFragment.getMapView());
            mapFragment.getMapView().getOverlays().add(overlay);
        }

        if (route != null)
        {
            List<Point> points = route.getPoints();

            GeoPoint prevPoint = new GeoPoint(points.get(0).getLatitude(), points.get(0).getLongtitude());
            for (Point point : points)
            {
                GeoPoint geoPoint = new GeoPoint(point.getLatitude(), point.getLongtitude());

                Geometry pointGeometry = new Geometry();
                pointGeometry.setPoint(geoPoint, 4);

                Symbol pointSymbol = new Symbol();
                Symbol.Color pointColor = pointSymbol.new Color();
                pointColor.red = 0;
                pointColor.green = 0;
                pointColor.blue = 255;
                pointColor.alpha = 200;
                pointSymbol.setLineSymbol(pointColor, 6);

                Graphic pointGraphic = new Graphic(pointGeometry, pointSymbol);
                overlay.setData(pointGraphic);

                Geometry lineGeometry = new Geometry();
                lineGeometry.setPolyLine(new GeoPoint[]{prevPoint, geoPoint});
                prevPoint = geoPoint;

                Symbol lineSymbol = new Symbol();
                Symbol.Color lineColor = lineSymbol.new Color();
                lineColor.red = 255;
                lineColor.green = 0;
                lineColor.blue = 0;
                lineColor.alpha = 128;
                lineSymbol.setLineSymbol(lineColor, 4);

                Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);

                overlay.setData(lineGraphic);

                mapFragment.getMapView().refresh();
            }
        }


        mapFragment.getMapView().regMapTouchListner(new MKMapTouchListener()
        {
            @Override
            public void onMapClick(GeoPoint geoPoint)
            {
                Log.i(TAG, "Click Longitude: " + geoPoint.getLongitudeE6() + " Latitude: " + geoPoint.getLatitudeE6());

                if (!mapFragment.getMapView().getController().isScrollGesturesEnabled())
                {
                    mapFragment.getMapView().getController().setScrollGesturesEnabled(true);
                    lockProjection = null;

                    route.setEndTime(new Date());
                    routeDao.insert(route);

                    for (GeoPoint point : routeArray)
                    {
                        Point startPoint = new Point(null, new Date(), point.getLatitudeE6(), point.getLongitudeE6(), route.getId());
                        pointDao.insert(startPoint);
                    }

                    route = null;
                    routeArray.clear();
                }
            }

            @Override
            public void onMapDoubleClick(GeoPoint geoPoint)
            {
                Log.i(TAG, "DoubleClick Longitude: " + geoPoint.getLongitudeE6() + " Latitude: " + geoPoint.getLatitudeE6());

                if (!mapFragment.getMapView().getController().isScrollGesturesEnabled())
                {
                    mapFragment.getMapView().getController().setScrollGesturesEnabled(true);
                    lockProjection = null;
                }
            }

            @Override
            public void onMapLongClick(GeoPoint geoPoint)
            {
                Log.i(TAG, "LongClick Longitude: " + geoPoint.getLongitudeE6() + " Latitude: " + geoPoint.getLatitudeE6());
                mapFragment.getMapView().getController().setScrollGesturesEnabled(false);
                lockProjection = mapFragment.getMapView().getProjection();

                lastRecoredAzimuth = 0;
                lastRecoredPoint = geoPoint;
                lastSamplePoint = geoPoint;

                overlay.removeAll();
                mapFragment.getMapView().refresh();

                route = new Route();
                route.setStartTime(new Date());

//                Point startPoint = new Point(null, new Date(), geoPoint.getLatitudeE6(), geoPoint.getLongitudeE6(), route.getId());
//                pointDao.insert(startPoint);
                routeArray.clear();
                routeArray.add(geoPoint);

            }
        });


        mapFragment.getMapView().setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View view, MotionEvent motionEvent) {
                Log.i(TAG, "Touch X: " + motionEvent.getX() + "Y: " + motionEvent.getY());

                if (lockProjection != null) {
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
                    pointSymbol.setLineSymbol(pointColor, 4);

                    Graphic pointGraphic = new Graphic(pointGeometry, pointSymbol);
                    overlay.setData(pointGraphic);

                    double distance = GeoCalculator.distance(lastRecoredPoint, point);
                    int azimuth = GeoCalculator.azimuth(lastSamplePoint, point);
                    int azimuthDelta = Math.abs(azimuth - lastRecoredAzimuth);

                    if ((distance > 1000) || ((azimuthDelta > (30 * 1E6)) && (distance > 10))) {
                        Log.i(TAG, "distance: " + distance + "  azimuth: " + azimuth + "  azimuthDelta: " + azimuthDelta);

                        Geometry lineGeometry = new Geometry();
                        lineGeometry.setPolyLine(new GeoPoint[]{lastRecoredPoint, lastSamplePoint});

                        Symbol lineSymbol = new Symbol();
                        Symbol.Color lineColor = lineSymbol.new Color();
                        lineColor.red = 0;
                        lineColor.green = 0;
                        lineColor.blue = 255;
                        lineColor.alpha = 200;
                        lineSymbol.setLineSymbol(lineColor, 4);

                        Graphic lineGraphic = new Graphic(lineGeometry, lineSymbol);

                        overlay.setData(lineGraphic);

                        lastRecoredPoint = lastSamplePoint;
                        lastRecoredAzimuth = azimuth;

//                        Point newPoint = new Point(null, new Date(), lastSamplePoint.getLatitudeE6(), lastSamplePoint.getLongitudeE6(), route.getId());
//                        pointDao.insert(newPoint);
                        routeArray.add(lastSamplePoint);
                    }

                    mapFragment.getMapView().refresh();


                    lastSamplePoint = point;
                }

                return false;
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.route_planner, menu);
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
//    public static class PlaceholderFragment extends Fragment {
//
//        public PlaceholderFragment() {
//        }
//
//        @Override
//        public View onCreateView(LayoutInflater inflater, ViewGroup container,
//                Bundle savedInstanceState) {
//            View rootView = inflater.inflate(R.layout.fragment_route_planner, container, false);
//            return rootView;
//        }
//    }
}
