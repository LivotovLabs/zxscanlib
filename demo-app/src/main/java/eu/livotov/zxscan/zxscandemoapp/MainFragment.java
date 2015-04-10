package eu.livotov.zxscan.zxscandemoapp;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Toast;

import eu.livotov.zxscan.ScannerView;

/**
 * A placeholder fragment containing a simple view.
 */
public class MainFragment extends Fragment implements View.OnClickListener, ScannerView.ScannerViewEventListener
{

    View btnOpenScannerInSeparateActivity, btnOpenEmbeddedScanner, btnCloseScanner, embeddedScannerRoot, waitLabel;
    ScannerView embeddedScanner;
    private String lastEmbeddedScannerScannedData;
    private long lastEmbeddedScannerScannedDataTimestamp;

    public MainFragment()
    {
    }

    public void onClick(final View v)
    {
        switch (v.getId())
        {
            case R.id.btnTestInActivity:
                startActivityForResult(new Intent(getActivity(), ScanActivity.class), 12345);
                break;

            case R.id.btnTestInView:
                startEmbeddedScanner();
                break;

            case R.id.btnStopScanner:
                stopEmbeddedScanner();
                break;
        }
    }

    private void startEmbeddedScanner()
    {
        embeddedScannerRoot.setVisibility(View.VISIBLE);
        waitLabel.setVisibility(View.VISIBLE);
        embeddedScanner.startScanner();
    }

    private void stopEmbeddedScanner()
    {
        embeddedScanner.stopScanner();
        embeddedScannerRoot.setVisibility(View.INVISIBLE);
    }

    public void onActivityResult(final int requestCode, final int resultCode, final Intent data)
    {
        if (requestCode == 12345 && resultCode == Activity.RESULT_OK)
        {
            displayScannedResult(data.getStringExtra(ScanActivity.RESULT_EXTRA_STR));
        }
        else
        {
            super.onActivityResult(requestCode, resultCode, data);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState)
    {
        View rootView = inflater.inflate(R.layout.fragment_main, container, false);
        return rootView;
    }

    public void onViewCreated(final View view, final Bundle savedInstanceState)
    {
        super.onViewCreated(view, savedInstanceState);
        btnOpenScannerInSeparateActivity = view.findViewById(R.id.btnTestInActivity);
        btnOpenEmbeddedScanner = view.findViewById(R.id.btnTestInView);
        btnCloseScanner = view.findViewById(R.id.btnStopScanner);
        embeddedScanner = (ScannerView) view.findViewById(R.id.scanner);
        embeddedScannerRoot = view.findViewById(R.id.scannerRoot);
        waitLabel = view.findViewById(R.id.waitLabel);

        btnOpenScannerInSeparateActivity.setOnClickListener(this);
        btnOpenEmbeddedScanner.setOnClickListener(this);
        btnCloseScanner.setOnClickListener(this);

        embeddedScanner.setScannerViewEventListener(this);
    }

    public void onPause()
    {
        stopEmbeddedScanner();
        super.onPause();
    }

    private void displayScannedResult(final String data)
    {
        Toast.makeText(getActivity(), "Data scanned: " + data, Toast.LENGTH_SHORT).show();
    }

    @Override
    public void onScannerReady()
    {
        if (waitLabel.getVisibility() == View.VISIBLE)
        {
            waitLabel.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onScannerFailure(int cameraError)
    {
        Toast.makeText(getActivity(), getString(R.string.camera_error, cameraError), Toast.LENGTH_LONG).show();
        startEmbeddedScanner();
    }

    public boolean onCodeScanned(final String data)
    {
        // As we run embedded scanner in continuous mode, we have to add same code protection here in order to avoid
        // generating a lot of same-code scan events
        if (data != null)
        {
            if (data.equalsIgnoreCase(lastEmbeddedScannerScannedData) && System.currentTimeMillis() - lastEmbeddedScannerScannedDataTimestamp < 1000)
            {
                return false;
            }
            else
            {
                displayScannedResult(data);
                lastEmbeddedScannerScannedData = data;
                lastEmbeddedScannerScannedDataTimestamp = System.currentTimeMillis();
                return true;
            }
        }

        return false;
    }
}
