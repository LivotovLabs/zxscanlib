package eu.livotov.zxscan.core;

import android.os.Handler;
import android.os.Looper;
import android.os.Message;

import java.util.concurrent.CountDownLatch;

import eu.livotov.zxscan.R;
import eu.livotov.zxscan.decoder.BarcodeDecoder;

/**
 * Created by dlivotov on 12/05/2015.
 */
public class DecoderThread extends Thread
{
    private final CountDownLatch handlerInitLatch;
    private Handler decoderHandler;
    private Handler uiHandler;
    private BarcodeDecoder decoder;

    public DecoderThread(Handler uiHandler, BarcodeDecoder decoder)
    {
        handlerInitLatch = new CountDownLatch(1);
        this.uiHandler = uiHandler;
        this.decoder = decoder;
    }

    @Override
    public void run()
    {
        Looper.prepare();
        decoderHandler = new DecoderHandler(uiHandler, decoder);
        handlerInitLatch.countDown();
        Looper.loop();
    }

    public void shutdown()
    {
        final Message message = Message.obtain(getDecoderHandler(), R.id.zxscanlib_core_message_stop);

        if (message != null)
        {
            message.sendToTarget();
        }
    }

    Handler getDecoderHandler()
    {
        try
        {
            handlerInitLatch.await();
        }
        catch (InterruptedException ignored)
        {
        }

        return decoderHandler;
    }

    public void submitBarcodeRecognitionTask(byte[] bytes, int width, int height)
    {
        final Message message = Message.obtain(getDecoderHandler(), R.id.zxscanlib_core_message_decode_data, width, height, bytes);

        if (message != null)
        {
            message.sendToTarget();
        }
    }
}