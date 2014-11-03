package eu.livotov.zxscan;

import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import eu.livotov.zxscan.R;
import eu.livotov.zxscan.ScannerView;

/**
 * (c) Livotov Labs Ltd. 2012
 * Date: 03/11/2014
 */
public class ScannerFragment extends Fragment implements ScannerView.ScannerViewEventListener
{
    protected ScannerView scanner;
    protected ScannerView.ScannerViewEventListener scannerViewEventListener;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_scanner, container, false);
        return rootView;
    }

    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        scanner = (ScannerView)view.findViewById(R.id.zxscanlib_camera);
        scanner.setScannerViewEventListener(this);
    }

    public ScannerView.ScannerViewEventListener getScannerViewEventListener()
    {
        return scannerViewEventListener;
    }

    public void setScannerViewEventListener(final ScannerView.ScannerViewEventListener scannerViewEventListener)
    {
        this.scannerViewEventListener = scannerViewEventListener;
    }

    public void onResume()
    {
        super.onResume();
        scanner.startScanner();
    }

    public void onPause()
    {
        scanner.stopScanner();
        super.onPause();
    }

    public ScannerView getScanner()
    {
        return scanner;
    }

    public boolean onCodeScanned(final String data)
    {
        if (scannerViewEventListener!=null)
        {
            scannerViewEventListener.onCodeScanned(data);
        }

        return true;
    }
}
