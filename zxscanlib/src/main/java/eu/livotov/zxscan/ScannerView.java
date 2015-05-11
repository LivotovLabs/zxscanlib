package eu.livotov.zxscan;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import eu.livotov.labs.android.camview.CAMView;
import eu.livotov.zxscan.decoder.BarcodeDecoder;
import eu.livotov.zxscan.decoder.zxing.ZXDecoder;
import eu.livotov.zxscan.util.SoundPlayer;

/**
 * (c) Livotov Labs Ltd. 2012
 * Date: 03/11/2014
 */
public class ScannerView extends FrameLayout implements CAMView.CAMViewListener
{
    public final static long DEFAULT_SAMECODE_RESCAN_PROTECTION_TIME_MS = 5000;
    private volatile long sameCodeRescanProtectionTime = DEFAULT_SAMECODE_RESCAN_PROTECTION_TIME_MS;
    protected CAMView camera;
    protected ImageView hud;
    protected ScannerViewEventListener scannerViewEventListener;
    protected BarcodeDecoder decoder;
    protected int scannerSoundAudioResource = R.raw.beep;
    protected boolean playSound = true;
    protected SoundPlayer soundPlayer;
    private volatile String lastDataDecoded;
    private volatile long lastDataDecodedTimestamp;
    private Handler decodingHandler;

    public ScannerView(final Context context)
    {
        super(context);
        initUI();
    }

    protected void initUI()
    {
        final View root = LayoutInflater.from(getContext()).inflate(getScannerLayoutResource(), this);
        camera = (CAMView) root.findViewById(R.id.zxscanlib_camera);
        hud = (ImageView) root.findViewById(R.id.cameraHud);
        camera.setCamViewListener(this);
        decoder = new ZXDecoder();
        soundPlayer = new SoundPlayer(getContext());
        decodingHandler = new Handler();
    }

    protected int getScannerLayoutResource()
    {
        return R.layout.view_scanner;
    }

    public ScannerView(final Context context, final AttributeSet attrs)
    {
        super(context, attrs);
        initUI();
    }

    public ScannerView(final Context context, final AttributeSet attrs, final int defStyleAttr)
    {
        super(context, attrs, defStyleAttr);
        initUI();
    }

    @TargetApi(21)
    public ScannerView(final Context context, final AttributeSet attrs, final int defStyleAttr, final int defStyleRes)
    {
        super(context, attrs, defStyleAttr, defStyleRes);
        initUI();
    }

    public void startScanner()
    {
        lastDataDecoded = null;
        camera.start();
    }

    public void stopScanner()
    {
        camera.stop();
    }

    public long getSameCodeRescanProtectionTime()
    {
        return sameCodeRescanProtectionTime;
    }

    public void setSameCodeRescanProtectionTime(long sameCodeRescanProtectionTime)
    {
        this.sameCodeRescanProtectionTime = sameCodeRescanProtectionTime;
    }

    public CAMView getCamera()
    {
        return camera;
    }

    public ScannerViewEventListener getScannerViewEventListener()
    {
        return scannerViewEventListener;
    }

    public void setScannerViewEventListener(final ScannerViewEventListener scannerViewEventListener)
    {
        this.scannerViewEventListener = scannerViewEventListener;
    }

    public void setScannerSoundAudioResource(final int scannerSoundAudioResource)
    {
        this.scannerSoundAudioResource = scannerSoundAudioResource;
    }

    public boolean isPlaySound()
    {
        return playSound;
    }

    public void setPlaySound(final boolean playSound)
    {
        this.playSound = playSound;
    }

    public void setHudImageResource(int res)
    {
        if (hud != null)
        {
            hud.setBackgroundResource(res);
            setHudVisible(res != 0);
        }
    }

    public void setHudVisible(boolean visible)
    {
        if (hud != null)
        {
            hud.setVisibility(visible ? VISIBLE : INVISIBLE);
        }
    }

    public void onCameraReady(Camera camera)
    {
        if (scannerViewEventListener != null)
        {
            scannerViewEventListener.onScannerReady();
        }
    }

    public void onCameraStopped()
    {
        if (scannerViewEventListener != null)
        {
            scannerViewEventListener.onScannerStopped();
        }
    }

    public void onCameraError(final int err, final Camera camera)
    {
        if (scannerViewEventListener != null)
        {
            scannerViewEventListener.onScannerFailure(err);
        }
    }

    public void onCameraOpenError(final Throwable err)
    {
        if (scannerViewEventListener != null)
        {
            scannerViewEventListener.onScannerFailure(-1);
        }
    }

    public boolean onPreviewData(final byte[] bytes, final int i, final Camera.Size size)
    {
        new Thread(new Runnable()
        {
            @Override
            public void run()
            {
                try
                {
                    final String data = decoder.decode(bytes, size.width, size.height);

                    if (!TextUtils.isEmpty(data) && (lastDataDecoded == null || !lastDataDecoded.equalsIgnoreCase(data) || (System.currentTimeMillis() - lastDataDecodedTimestamp) > sameCodeRescanProtectionTime))
                    {
                        lastDataDecoded = data;
                        lastDataDecodedTimestamp = System.currentTimeMillis();

                        decodingHandler.post(new Runnable()
                        {
                            @Override
                            public void run()
                            {
                                notifyBarcodeRead(data);
                            }
                        });
                    }
                }
                catch (Throwable err)
                {
                    Log.e(ScannerView.class.getSimpleName(), "Decoding error: " + err.getMessage(), err);
                }
                finally
                {
                    camera.enablePreviewGrabbing();
                }
            }
        }).start();

        return false;
    }

    protected void notifyBarcodeRead(final String data)
    {
        if (scannerViewEventListener != null)
        {
            if (!TextUtils.isEmpty(data))
            {
                if (scannerViewEventListener.onCodeScanned(data))
                {
                    beep();
                }
            }
        }
    }

    private void beep()
    {
        if (playSound && scannerSoundAudioResource != 0)
        {
            soundPlayer.playRawResource(scannerSoundAudioResource, false);
        }
    }

    public interface ScannerViewEventListener
    {
        void onScannerReady();

        void onScannerStopped();

        void onScannerFailure(int cameraError);

        boolean onCodeScanned(final String data);
    }
}
