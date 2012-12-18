package eu.livotov.zxscan;

import android.app.Activity;

/**
 * (c) Livotov Labs Ltd. 2012
 * Date: 14.12.12
 */
public interface ZXUserCallback
{

    void onScannerActivityCreated(Activity activity);

    void onScannerActivityDestroyed(Activity activity);

    void onScannerActivityResumed(Activity captureActivity);

    boolean onCodeRead(String code);

}
