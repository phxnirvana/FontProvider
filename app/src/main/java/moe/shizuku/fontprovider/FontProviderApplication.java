package moe.shizuku.fontprovider;

import android.app.Application;

/**
 * Created by rikka on 2017/10/3.
 */

public class FontProviderApplication extends Application {

    @Override
    public void onCreate() {
        super.onCreate();

        FontManager.init(this);
    }
}