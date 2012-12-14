package eu.livotov.zxscan;

import android.app.Activity;
import com.google.zxing.client.android.CaptureActivity;

/**
 * (c) Livotov Labs Ltd. 2012
 * Date: 14.12.12
 */
public interface ZXUserCallback
{
    void onScannerActivityCreated(Activity activity);

    void onScannerActivityDestroyed(Activity activity);

    void onCodeRecognized(String code);

    void onScannerActivityResumed(Activity captureActivity);
}
