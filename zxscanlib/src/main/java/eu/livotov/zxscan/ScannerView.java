package eu.livotov.zxscan;

import android.annotation.TargetApi;
import android.content.Context;
import android.hardware.Camera;
import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;

import eu.livotov.labs.android.camview.CAMView;
import eu.livotov.zxscan.core.DecoderThread;
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
    public final static long DEFAULT_DECODE_THROTTLE_MS = 300;

    protected CAMView camera;
    protected ImageView hud;
    protected ScannerViewEventListener scannerViewEventListener;
    protected BarcodeDecoder decoder;
    protected int scannerSoundAudioResource = R.raw.zxscanlib_beep;
    protected boolean playSound = true;
    protected SoundPlayer soundPlayer;
    private volatile long sameCodeRescanProtectionTime = DEFAULT_SAMECODE_RESCAN_PROTECTION_TIME_MS;
    private volatile String lastDataDecoded;
    private volatile long lastDataDecodedTimestamp;
    private volatile long lastDataSubmittedTimestamp;
    private volatile long decodeThrottleMillis = DEFAULT_DECODE_THROTTLE_MS;
    private DecoderThread decoderThread;
    private DecoderResultHandler decoderResultHandler;

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
    }

    protected int getScannerLayoutResource()
    {
        return R.layout.zxscanlib_view_scanner;
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
        initThreadingSubsystem();
        camera.start();
    }

    private void initThreadingSubsystem()
    {
        decoderResultHandler = new DecoderResultHandler();
        decoderThread = new DecoderThread(decoderResultHandler, decoder);
        decoderThread.start();
    }

    public void stopScanner()
    {
        shutodownThreadingSubsystem();
        camera.stop();
    }

    private void shutodownThreadingSubsystem()
    {
        if (decoderThread != null)
        {
            decoderThread.shutdown();
        }

        decoderResultHandler = null;
        decoderThread = null;
    }

    public long getSameCodeRescanProtectionTime()
    {
        return sameCodeRescanProtectionTime;
    }

    public void setSameCodeRescanProtectionTime(long sameCodeRescanProtectionTime)
    {
        this.sameCodeRescanProtectionTime = sameCodeRescanProtectionTime;
    }

    public long getDecodeThrottleMillis()
    {
        return decodeThrottleMillis;
    }

    public void setDecodeThrottleMillis(long throttle)
    {
        this.decodeThrottleMillis = throttle;
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
        shutodownThreadingSubsystem();
        if (scannerViewEventListener != null)
        {
            scannerViewEventListener.onScannerFailure(-1);
        }
    }

    public boolean onPreviewData(final byte[] bytes, final int i, final Camera.Size size)
    {
        final long currentTime = System.currentTimeMillis();

        if (decoderThread != null && currentTime - lastDataSubmittedTimestamp > decodeThrottleMillis)
        {
            lastDataSubmittedTimestamp = currentTime;
            decoderThread.submitBarcodeRecognitionTask(bytes, size.width, size.height);
            return false;
        }
        else
        {
            return true;
        }
    }

    private void processRecognizedBarcode(String data)
    {
        if (TextUtils.isEmpty(lastDataDecoded) || !lastDataDecoded.equalsIgnoreCase(data) || (System.currentTimeMillis() - lastDataDecodedTimestamp) > sameCodeRescanProtectionTime)
        {
            lastDataDecoded = data;
            lastDataDecodedTimestamp = System.currentTimeMillis();
            notifyBarcodeRead(data);
        }

        if (camera != null)
        {
            camera.enablePreviewGrabbing();
        }
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

    private void processEmptyBarcode()
    {
        if (camera != null)
        {
            camera.enablePreviewGrabbing();
        }
    }

    public interface ScannerViewEventListener
    {
        void onScannerReady();

        void onScannerStopped();

        void onScannerFailure(int cameraError);

        boolean onCodeScanned(final String data);
    }

    class DecoderResultHandler extends Handler
    {

        @Override
        public void handleMessage(Message msg)
        {
            if (msg.what == R.id.zxscanlib_core_message_decode_result_ok)
            {
                processRecognizedBarcode((String) msg.obj);
            }
            else if (msg.what == R.id.zxscanlib_core_message_decode_result_nodata)
            {
                processEmptyBarcode();
            }
        }
    }
}
