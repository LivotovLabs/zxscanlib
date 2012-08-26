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

    public final static String SCANNED_RESULT = Intents.Scan.RESULT;

    public final static void scan(Activity ctx, int requestCode)
    {
        Intent scanIntent = new Intent(Intents.Scan.ACTION);
        ctx.startActivityForResult(scanIntent, requestCode);
    }

    public final static String getScannedCode(Intent resultData)
    {
        return resultData != null ? resultData.getStringExtra(SCANNED_RESULT) : null;
    }
}
