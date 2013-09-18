package eu.livotov.zxscan;

import android.app.Activity;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.text.TextUtils;
import com.google.zxing.client.android.CaptureActivity;
import com.google.zxing.client.android.Intents;
import com.google.zxing.client.android.camera.FrontLightMode;

import java.util.List;

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
    private static int customScanSound = 0;
    private static boolean safeMode = false;
    private static boolean playSoundOnRead = true;
    private static boolean vibrateOnRead = true;
    private static boolean useExternalApplicationIfAvailable = false;
    private static boolean blockCameraRotation = true;
    private static FrontLightMode frontLightMode = FrontLightMode.AUTO;
    private static AutofocusMode autofocusMode = AutofocusMode.On;
    private static ZXUserCallback userCallback;
    private static Class captureActivityClass;

    public final static void scan(Activity ctx, int requestCode)
    {
        Intent scanIntent = useExternalApplicationIfAvailable ? getExternalApplicationIntent(ctx) : null;

        if (scanIntent == null)
        {
            scanIntent = new Intent(ctx, captureActivityClass != null ? captureActivityClass : CaptureActivity.class);
        }

        ctx.startActivityForResult(scanIntent, requestCode);
    }

    private static Intent getExternalApplicationIntent(final Context ctx)
    {
        PackageManager pm = ctx.getPackageManager();
        Intent intent = new Intent(Intents.Scan.ACTION);

        List<ResolveInfo> availableApps = pm.queryIntentActivities(intent, PackageManager.MATCH_DEFAULT_ONLY);

        for (ResolveInfo availableApp : availableApps)
        {
            final String packageName = availableApp.activityInfo.packageName;
            if (!TextUtils.isEmpty(packageName))
            {
                if (packageName.contains("com.google.zxing.client.android") || packageName.contains("com.srowen.bs.android"))
                {
                    return intent;
                }
            }
        }

        return null;
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

    public static int getCustomScanSound()
    {
        return customScanSound;
    }

    public static void setCustomScanSound(final int customScanSound)
    {
        ZXScanHelper.customScanSound = customScanSound;
    }

    public static boolean isUseExternalApplicationIfAvailable()
    {
        return useExternalApplicationIfAvailable;
    }

    public static void setUseExternalApplicationIfAvailable(final boolean b)
    {
        useExternalApplicationIfAvailable = b;
    }

    public static boolean isPlaySoundOnRead()
    {
        return playSoundOnRead;
    }

    public static void setPlaySoundOnRead(final boolean playSoundOnRead)
    {
        ZXScanHelper.playSoundOnRead = playSoundOnRead;
    }

    public static boolean isVibrateOnRead()
    {
        return vibrateOnRead;
    }

    public static boolean isSafeMode()
    {
        return safeMode;
    }

    public static void setSafeMode(final boolean safeMode)
    {
        ZXScanHelper.safeMode = safeMode;
    }

    public static void setVibrateOnRead(final boolean vibrateOnRead)
    {
        ZXScanHelper.vibrateOnRead = vibrateOnRead;
    }

    public static AutofocusMode getAutofocusMode()
    {
        return autofocusMode;
    }

    public static void setAutofocusMode(final AutofocusMode autofocusMode)
    {
        ZXScanHelper.autofocusMode = autofocusMode;
    }

    public static boolean isBlockCameraRotation()
    {
        return blockCameraRotation;
    }

    public static void setBlockCameraRotation(final boolean blockCameraRotation)
    {
        ZXScanHelper.blockCameraRotation = blockCameraRotation;
    }


    public static void setCustomActivityClass(Class<? extends CaptureActivity> captureActivityClass)
    {
        ZXScanHelper.captureActivityClass = captureActivityClass;
    }

    public static FrontLightMode getFrontLightMode()
    {
        return frontLightMode;
    }

    public static void setFrontLightMode(final FrontLightMode frontLightMode)
    {
        ZXScanHelper.frontLightMode = frontLightMode;
    }
}
