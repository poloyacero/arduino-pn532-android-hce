package com.example.application;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.UnsupportedEncodingException;

public class ReadActivity extends AppCompatActivity {
    NfcAdapter nfcAdapter;

    /* access modifiers changed from: protected */
    @Override // androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_read);
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onNewIntent(Intent intent) {
        if (intent.hasExtra("android.nfc.extra.TAG")) {
            Toast.makeText(this, "NFC intent received!", 1).show();
        }
        Parcelable[] parcelables = intent.getParcelableArrayExtra("android.nfc.extra.NDEF_MESSAGES");
        Log.d("NDEF message: ", parcelables.toString());
        if (parcelables == null || parcelables.length <= 0) {
            Toast.makeText(this, "No NDEF messages found!", 0).show();
        } else {
            readTextFromMessage((NdefMessage) parcelables[0]);
        }
        Log.d("Tag: ", ((Tag) intent.getParcelableExtra("android.nfc.extra.TAG")).toString());
        super.onNewIntent(intent);
    }

    private void readTextFromMessage(NdefMessage ndefMessage) {
        NdefRecord[] ndefRecords = ndefMessage.getRecords();
        if (ndefMessage == null || ndefRecords.length <= 0) {
            Toast.makeText(this, "No NDEF records found!", 0).show();
        } else {
            Log.d("NDEF tag content: ", getTextFromNdefRecord(ndefRecords[0]));
        }
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        Intent intent = new Intent(this, ReadActivity.class);
        intent.addFlags(536870912);
        this.nfcAdapter.enableForegroundDispatch(this, PendingIntent.getActivity(this, 0, intent, 0), new IntentFilter[0], null);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
        this.nfcAdapter.disableForegroundDispatch(this);
    }

    public String getTextFromNdefRecord(NdefRecord ndefRecord) {
        try {
            byte[] payload = ndefRecord.getPayload();
            Log.d("payload: ", payload.toString());
            String textEncoding = (payload[0] & 128) == 0 ? "UTF-8" : "UTF-16";
            int languageSize = payload[0] & 51;
            return new String(payload, languageSize + 1, (payload.length - languageSize) - 1, textEncoding);
        } catch (UnsupportedEncodingException e) {
            Log.e("getTextFromNdefRecord", e.getMessage(), e);
            return null;
        }
    }
}
