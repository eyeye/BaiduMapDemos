package com.eye.baidumapdemos;

import java.io.File;

import de.greenrobot.daogenerator.DaoGenerator;
import de.greenrobot.daogenerator.Entity;
import de.greenrobot.daogenerator.Property;
import de.greenrobot.daogenerator.Schema;
import de.greenrobot.daogenerator.ToMany;

public class GeoDaoGenerator {
    static final String OutputPackage = "com.eye.baidumapdemos.dao";
    static final String OutputDirPath = "app/src/main/java";


    public static void main(String args[]) throws Exception
    {
        Schema schema = new Schema(3, OutputPackage);

        Entity route = schema.addEntity("Route");
        Entity point = schema.addEntity("Point");

        route.addIdProperty();
        route.addDateProperty("startTime");
        route.addDateProperty("endTime");

        point.addIdProperty();
        Property pointTime = point.addDateProperty("time").getProperty();
        point.addIntProperty("latitude");
        point.addIntProperty("longtitude");

        Property routeProperty = point.addLongProperty("routeId").getProperty();
        point.addToOne(route, routeProperty);

        ToMany routeToPoints = route.addToMany(point, routeProperty, "points");
        routeToPoints.orderAsc(pointTime);

        try {
            File file = new File(OutputDirPath);
            if (!file.exists())
            {
                if (!file.mkdirs())
                {
                    System.out.println("Can't make dirs");
                }
            }

            new DaoGenerator().generateAll(schema, OutputDirPath);
        }
        catch (Exception e)
        {
            e.printStackTrace();
        }

    }
}
