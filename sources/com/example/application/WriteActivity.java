package com.example.application;

import android.app.PendingIntent;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import java.io.ByteArrayOutputStream;
import java.io.UnsupportedEncodingException;
import java.util.Locale;

public class WriteActivity extends AppCompatActivity {
    NfcAdapter nfcAdapter;

    /* access modifiers changed from: protected */
    @Override // androidx.core.app.ComponentActivity, androidx.appcompat.app.AppCompatActivity, androidx.fragment.app.FragmentActivity
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_write);
        this.nfcAdapter = NfcAdapter.getDefaultAdapter(this);
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onResume() {
        super.onResume();
        enableForegroundDispatch();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onPause() {
        super.onPause();
        disableForegroundDispatch();
    }

    /* access modifiers changed from: protected */
    @Override // androidx.fragment.app.FragmentActivity
    public void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        if (intent.hasExtra("android.nfc.extra.TAG")) {
            Toast.makeText(this, "NFC intent received.", 0).show();
            writeNdefMessage((Tag) intent.getParcelableExtra("android.nfc.extra.TAG"), createNdefMessage("Open Sesame"));
        }
    }

    private void enableForegroundDispatch() {
        this.nfcAdapter.enableForegroundDispatch(this, PendingIntent.getActivity(this, 0, new Intent(this, WriteActivity.class).addFlags(536870912), 0), new IntentFilter[0], null);
    }

    private void disableForegroundDispatch() {
        this.nfcAdapter.disableForegroundDispatch(this);
    }

    private NdefMessage createNdefMessage(String content) {
        return new NdefMessage(new NdefRecord[]{createTextRecord(content)});
    }

    private NdefRecord createTextRecord(String content) {
        try {
            byte[] language = Locale.getDefault().getLanguage().getBytes("UTF-8");
            byte[] text = content.getBytes("UTF-8");
            int languageSize = language.length;
            int textLength = text.length;
            ByteArrayOutputStream payload = new ByteArrayOutputStream(languageSize + 1 + textLength);
            payload.write((byte) (languageSize & 31));
            payload.write(language, 0, languageSize);
            payload.write(text, 0, textLength);
            return new NdefRecord(1, NdefRecord.RTD_TEXT, new byte[0], payload.toByteArray());
        } catch (UnsupportedEncodingException e) {
            Log.e("createTextRecord", e.getMessage());
            return null;
        }
    }

    private void writeNdefMessage(Tag tag, NdefMessage ndefMessage) {
        if (tag == null) {
            try {
                Toast.makeText(this, "Tag object cannot be null", 0).show();
            } catch (Exception e) {
                Log.e("writeNdefMessage", e.getMessage());
            }
        } else {
            Ndef ndef = Ndef.get(tag);
            if (ndef == null) {
                formatTag(tag, ndefMessage);
                return;
            }
            ndef.connect();
            if (!ndef.isWritable()) {
                Toast.makeText(this, "Tag is not writable!", 0).show();
                ndef.close();
                return;
            }
            ndef.writeNdefMessage(ndefMessage);
            ndef.close();
            Toast.makeText(this, "Tag written!", 0).show();
        }
    }

    private void formatTag(Tag tag, NdefMessage ndefMessage) {
        try {
            NdefFormatable ndefFormatable = NdefFormatable.get(tag);
            if (ndefFormatable == null) {
                Toast.makeText(this, "Tag is not ndef formattable!", 0).show();
                return;
            }
            ndefFormatable.connect();
            ndefFormatable.format(ndefMessage);
            ndefFormatable.close();
            Toast.makeText(this, "Tag written!", 0).show();
        } catch (Exception e) {
            Log.e("formatTag", e.getMessage());
        }
    }
}
