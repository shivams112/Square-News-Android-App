package io.github.shivams112.squarenews;

import android.app.Application;

/**
 * Created by Shivam
 */

public class NewsApplication extends Application {

    private static NewsApplication mApplicationInstance;

    @Override
    public void onCreate() {
        super.onCreate();
        mApplicationInstance = this;
    }

    public static NewsApplication getApplicationInstance(){
        if(mApplicationInstance==null){
            mApplicationInstance = new NewsApplication();
        }
        return mApplicationInstance;
    }
}
