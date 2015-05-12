package eu.livotov.zxscan.core;

import android.os.Handler;
import android.os.Message;
import android.text.TextUtils;
import android.util.Log;

import eu.livotov.zxscan.R;
import eu.livotov.zxscan.decoder.BarcodeDecoder;

/**
 * Created by dlivotov on 12/05/2015.
 */
public class DecoderHandler extends Handler
{
    private static final String TAG = DecoderHandler.class.getSimpleName();

    private Handler uiHandler;
    private BarcodeDecoder decoder;
    private boolean running = true;

    DecoderHandler(Handler uiHandler, BarcodeDecoder decoder)
    {
        this.uiHandler = uiHandler;
        this.decoder = decoder;
    }

    @Override
    public void handleMessage(Message message)
    {
        if (!running)
        {
            return;
        }

        if (message.what == R.id.zxscanlib_core_message_decode_data)
        {
            decode((byte[]) message.obj, message.arg1, message.arg2);

        }
    }

    private void decode(byte[] data, int width, int height)
    {
        try
        {
            final String result = decoder.decode(data, width, height);

            if (!TextUtils.isEmpty(result))
            {
                Message.obtain(uiHandler, R.id.zxscanlib_core_message_decode_result_ok, result).sendToTarget();
            }
            else
            {
                Message.obtain(uiHandler, R.id.zxscanlib_core_message_decode_result_nodata).sendToTarget();
            }
        }
        catch (Throwable err)
        {
            Log.e(TAG, err.getMessage(), err);
            Message.obtain(uiHandler, R.id.zxscanlib_core_message_decode_result_nodata).sendToTarget();
        }
    }
}
