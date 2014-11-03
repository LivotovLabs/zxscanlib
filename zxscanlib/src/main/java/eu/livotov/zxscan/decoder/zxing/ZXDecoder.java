package eu.livotov.zxscan.decoder.zxing;

import com.google.zxing.*;
import com.google.zxing.common.HybridBinarizer;
import eu.livotov.zxscan.decoder.BarcodeDecoder;

import java.util.Collection;
import java.util.EnumMap;
import java.util.EnumSet;
import java.util.Map;

/**
 * (c) Livotov Labs Ltd. 2012
 * Date: 03/11/2014
 */
public class ZXDecoder implements BarcodeDecoder
{

    final static Collection<BarcodeFormat> ONE_D_FORMATS = EnumSet.of(BarcodeFormat.CODE_39,
                                                                      BarcodeFormat.CODE_93,
                                                                      BarcodeFormat.CODE_128,
                                                                      BarcodeFormat.ITF,
                                                                      BarcodeFormat.CODABAR,
                                                                      BarcodeFormat.UPC_A,
                                                                      BarcodeFormat.UPC_E,
                                                                      BarcodeFormat.EAN_13,
                                                                      BarcodeFormat.EAN_8,
                                                                      BarcodeFormat.RSS_14,
                                                                      BarcodeFormat.RSS_EXPANDED);

    final static Collection<BarcodeFormat> QR_CODE_FORMATS = EnumSet.of(BarcodeFormat.QR_CODE);
    final static Collection<BarcodeFormat> DATA_MATRIX_FORMATS = EnumSet.of(BarcodeFormat.DATA_MATRIX);

    private MultiFormatReader reader;
    protected Map<DecodeHintType, Object> hints = new EnumMap<DecodeHintType, Object>(DecodeHintType.class);


    public ZXDecoder()
    {
        reader = new MultiFormatReader();

        Collection<BarcodeFormat> decodeFormats = EnumSet.noneOf(BarcodeFormat.class);
        decodeFormats.addAll(QR_CODE_FORMATS);
        decodeFormats.addAll(ONE_D_FORMATS);
        decodeFormats.addAll(DATA_MATRIX_FORMATS);

        hints.put(DecodeHintType.POSSIBLE_FORMATS, decodeFormats);
        hints.put(DecodeHintType.CHARACTER_SET, "utf-8");

        reader.setHints(hints);
    }

    public String decode(final byte[] image, final int width, final int height)
    {
        Result result = null;
        BinaryBitmap bitmap = new BinaryBitmap(new HybridBinarizer(new PlanarYUVLuminanceSource(image, width, height, 0, 0, width, height, false)));
        try
        {
            result = reader.decodeWithState(bitmap);
        } catch (ReaderException re)
        {
        } finally
        {
            reader.reset();
        }

        if (result != null)
        {
            return result.getText();
        }

        return null;
    }

}
