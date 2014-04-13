package com.eye.baidumapdemos.app;

import android.app.Application;
import android.util.Log;
import android.widget.Toast;

import com.baidu.mapapi.BMapManager;
import com.baidu.mapapi.MKGeneralListener;
import com.baidu.mapapi.map.MKEvent;

/**
 * Created by eye on 14-4-13.
 */
public class MainApp extends Application
{
    private final static String TAG = MainApp.class.getSimpleName();

    private static MainApp ourInstance = new MainApp();


    private BMapManager mapManager = null;

    public BMapManager getMapManager()
    {

        return mapManager;
    }



    public static MainApp getInstance()
    {
        Log.i(TAG, "getInstance");
        if (ourInstance == null)
        {
            ourInstance = new MainApp();
        }
        return ourInstance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        if (mapManager==null)
        {
            mapManager = new BMapManager(this);
        }

        if (!mapManager.init(new GeneralListener())) {
            Toast.makeText(MainApp.getInstance().getApplicationContext(),
                    "BMapManager  初始化错误!", Toast.LENGTH_LONG).show();
        }

        Log.i(TAG, "onCreate mapManager");
    }

    static class GeneralListener implements MKGeneralListener
    {
        @Override
        public void onGetNetworkState(int iError) {
            if (iError == MKEvent.ERROR_NETWORK_CONNECT) {
                Toast.makeText(MainApp.getInstance().getApplicationContext(), "您的网络出错啦！",
                        Toast.LENGTH_LONG).show();
            }
            else if (iError == MKEvent.ERROR_NETWORK_DATA) {
                Toast.makeText(MainApp.getInstance().getApplicationContext(), "输入正确的检索条件！",
                        Toast.LENGTH_LONG).show();
            }
        }

        @Override
        public void onGetPermissionState(int iError)
        {
            //非零值表示key验证未通过
            if (iError != 0) {
                //授权Key错误：
                Toast.makeText(MainApp.getInstance().getApplicationContext(),
                        "请在 DemoApplication.java文件输入正确的授权Key,并检查您的网络连接是否正常！error: "+iError, Toast.LENGTH_LONG).show();
            }
            else
            {
                Toast.makeText(MainApp.getInstance().getApplicationContext(),
                        "key认证成功", Toast.LENGTH_LONG).show();
            }
        }
    }
}
