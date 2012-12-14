package eu.livotov.zxscan;

import android.app.Activity;
import android.content.Intent;
import com.google.zxing.client.android.Intents;

/**
 * Created with IntelliJ IDEA.
 * User: dlivotov
 * Date: 26.08.12
 * Time: 21:14
 * To change this template use File | Settings | File Templates.
 */
public class ZXScanHelper
{

    private static int customScanLayout = 0;
    private static ZXUserCallback userCallback;

    public final static void setCustomScanAction(final String action)
    {
        Intents.Scan.ACTION = action;
    }

    public static void setDefaultScanAction()
    {
        Intents.Scan.resetScanAction();
    }

    public final static void scan(Activity ctx, int requestCode)
    {
        Intent scanIntent = new Intent(Intents.Scan.ACTION);
        ctx.startActivityForResult(scanIntent, requestCode);
    }

    public final static String getScannedCode(Intent resultData)
    {
        return resultData != null ? resultData.getStringExtra(Intents.Scan.RESULT) : null;
    }

    public static void setCustomScanLayout(final int customScanLayoutRes)
    {
        ZXScanHelper.customScanLayout = customScanLayoutRes;
    }

    public static int getCustomScanLayout()
    {
        return customScanLayout;
    }

    public static ZXUserCallback getUserCallback()
    {
        return userCallback;
    }

    public static void setUserCallback(final ZXUserCallback userCallback)
    {
        ZXScanHelper.userCallback = userCallback;
    }
}
