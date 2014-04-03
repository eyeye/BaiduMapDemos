package com.eye.baidumapdemos.app;

import com.baidu.platform.comapi.basestruct.GeoPoint;


/**
 * Created by EYE on 2014/4/2.
 */


public class GeoCalculator
{
    final static double R = 6371004.0;




    static double distance(GeoPoint begin, GeoPoint end)
    {
        double result;
        double longtitudeBegin    = (begin.getLongitudeE6()/1E6) * Math.PI/180;
        double latitudeBegin     = (begin.getLatitudeE6()/1E6) * Math.PI/180;

        double longtitudeEnd    = (end.getLongitudeE6()/1E6) * Math.PI/180;
        double latitudeEnd     = (end.getLatitudeE6()/1E6) * Math.PI/180;

        result = R * Math.acos( (Math.sin(latitudeBegin)*Math.sin(latitudeEnd) + Math.cos(latitudeBegin)*Math.cos(latitudeEnd)*Math.cos(longtitudeEnd - longtitudeBegin)) );
        return result;
    }


    static int azimuth(GeoPoint begin, GeoPoint end)
    {
        double forwardAzimuth;
        double longtitudeBegin    = (begin.getLongitudeE6()/1E6) * Math.PI/180;
        double latitudeBegin     = (begin.getLatitudeE6()/1E6) * Math.PI/180;

        double longtitudeEnd    = (end.getLongitudeE6()/1E6) * Math.PI/180;
        double latitudeEnd     = (end.getLatitudeE6()/1E6) * Math.PI/180;

        forwardAzimuth = Math.atan((Math.cos(latitudeEnd) * Math.sin(longtitudeEnd - longtitudeBegin)) / (Math.cos(latitudeBegin) * Math.sin(latitudeEnd) - Math.sin(latitudeBegin) * Math.cos(latitudeEnd) * Math.cos(longtitudeEnd - longtitudeBegin)));

        if(latitudeEnd < latitudeBegin)
        {
            if(longtitudeEnd > longtitudeBegin)
            {
                forwardAzimuth += Math.PI;
            }
            else
            {
                forwardAzimuth -= Math.PI;
            }
        }

        return (int)(forwardAzimuth*180*1E6/Math.PI);
    }


    static GeoPoint location(GeoPoint begin, double azimuth, double distance)
    {
        double longtitudeBegin  = (begin.getLongitudeE6()/1E6) * Math.PI/180;
        double latitudeBegin    = (begin.getLatitudeE6()/1E6) * Math.PI/180;

        double latitude, longtitude;
        latitude = Math.asin(Math.sin(latitudeBegin) * Math.cos(distance / R) + Math.cos(latitudeBegin) * Math.cos(azimuth) * Math.sin(distance / R));
        longtitude = longtitudeBegin + Math.atan((Math.cos(latitudeBegin) * Math.sin(azimuth) * Math.sin(distance / R)) / (Math.cos(distance / R) - Math.sin(latitudeBegin) * Math.sin(latitude)));

        int latitudeInt = (int)(latitude*180*1E6/Math.PI);
        int longtitudeInt = (int)(longtitude*180*1E6/Math.PI);

        return new GeoPoint(latitudeInt, longtitudeInt);
    }

}



