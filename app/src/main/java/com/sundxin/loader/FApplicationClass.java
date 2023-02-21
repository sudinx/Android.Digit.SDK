
package com.sundxin.loader;

import android.content.res.Resources;
import com.sundixan.loader.ApplicationClaasss;


public class FApplicationClass extends ApplicationClaasss {


    private static FApplicationClass myApp;
    public static Resources resource;

    @Override
    public void onCreate() {
        super.onCreate();

        resource = getResources();
        myApp = this;


    }




}

