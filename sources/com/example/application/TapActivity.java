package com.example.application;

import android.content.BroadcastReceiver;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.TextView;
import androidx.appcompat.app.AppCompatActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

public class TapActivity extends AppCompatActivity implements View.OnClickListener {
    private LocalBroadcastManager localBroadcastManager;
    private BroadcastReceiver mMessageReceiver = new BroadcastReceiver() {
        /* class com.example.application.TapActivity.AnonymousClass1 */

        public void onReceive(Context context, Intent intent) {
            TapActivity.this.textTapStatus.setText(intent.getStringExtra("data"));
        }
    };
    private RelativeLayout rlScanBarcode;
    private RelativeLayout rlTapDeviceNFCReader;
    private TextView textTapStatus;

    /* access modifiers changed from: protected */
    @Override // androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tap);
        initViews();
        this.rlTapDeviceNFCReader.setOnClickListener(this);
        this.rlScanBarcode.setOnClickListener(this);
        this.localBroadcastManager = LocalBroadcastManager.getInstance(this);
    }

    private void initViews() {
        this.rlTapDeviceNFCReader = (RelativeLayout) findViewById(R.id.rlTapDeviceNFCReader);
        this.rlScanBarcode = (RelativeLayout) findViewById(R.id.rlScanBarcode);
        this.textTapStatus = (TextView) findViewById(R.id.textTapStatus);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        this.localBroadcastManager.registerReceiver(this.mMessageReceiver, new IntentFilter(ApduService.BROADCAST_INTENT_DATA_RECEIVED));
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
        this.localBroadcastManager.unregisterReceiver(this.mMessageReceiver);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onStop() {
        getPackageManager().setComponentEnabledSetting(new ComponentName(this, ApduService.class), 2, 1);
        super.onStop();
    }

    public void onClick(View view) {
        int id = view.getId();
        if (id == R.id.rlScanBarcode) {
            Log.d("Barcode", "here");
        } else if (id == R.id.rlTapDeviceNFCReader) {
            Log.d("Service", "here");
            getPackageManager().setComponentEnabledSetting(new ComponentName(this, ApduService.class), 1, 1);
        }
    }
}
