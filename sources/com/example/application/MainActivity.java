package com.example.application;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.os.Bundle;
import android.view.View;
import android.widget.RelativeLayout;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;

public class MainActivity extends AppCompatActivity implements View.OnClickListener {
    NfcAdapter nfcAdapter;
    private RelativeLayout rlRead;
    private RelativeLayout rlTap;
    private RelativeLayout rlWrite;

    /* access modifiers changed from: protected */
    @Override // androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        initViews();
        this.rlRead.setOnClickListener(this);
        this.rlWrite.setOnClickListener(this);
        this.rlTap.setOnClickListener(this);
        NfcAdapter defaultAdapter = NfcAdapter.getDefaultAdapter(this);
        this.nfcAdapter = defaultAdapter;
        if (defaultAdapter == null || !defaultAdapter.isEnabled()) {
            Toast.makeText(this, "NFC not available.", 1).show();
        } else {
            Toast.makeText(this, "NFC available!", 1).show();
        }
    }

    private void initViews() {
        this.rlRead = (RelativeLayout) findViewById(R.id.rlReadNFCTAG);
        this.rlWrite = (RelativeLayout) findViewById(R.id.rlWriteNFCTAG);
        this.rlTap = (RelativeLayout) findViewById(R.id.rlTapNFCTAG);
    }

    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.rlReadNFCTAG /*{ENCODED_INT: 2131230855}*/:
                startActivity(new Intent(this, ReadActivity.class));
                return;
            case R.id.rlTapNFCTAG /*{ENCODED_INT: 2131230859}*/:
                startActivity(new Intent(this, TapActivity.class));
                return;
            case R.id.rlWriteNFCTAG /*{ENCODED_INT: 2131230860}*/:
                startActivity(new Intent(this, WriteActivity.class));
                return;
            default:
                return;
        }
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
    }

    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
    }
}
