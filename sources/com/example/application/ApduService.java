package com.example.application;

import android.content.Intent;
import android.nfc.cardemulation.HostApduService;
import android.os.Bundle;
import android.util.Log;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import java.util.Arrays;

public class ApduService extends HostApduService {
    public static final String BROADCAST_INTENT_DATA_RECEIVED = "DATA_RECEIVED";
    private static final byte[] DATA_RESPONSE_OK = HexStringToByteArray("01");
    private static final String PAYLOAD = "456789";
    private static final byte RESPONSE_COMMAND = 1;
    private static final byte[] SELECT_AID_COMMAND = {0, -92, 4, 0, 6, -16, -85, -51, -17, 18, 52};
    private static final byte[] SELECT_OK_SW = HexStringToByteArray("9000");
    private static final String TAG = "APDU Service";
    private static final byte[] UNKNOWN_CMD_SW = HexStringToByteArray("0000");

    public byte[] processCommandApdu(byte[] commandApdu, Bundle extras) {
        Log.d(TAG, "Received APDU - " + ByteArrayToHexString(commandApdu));
        Log.d(TAG, "Received byte - " + ByteToHexString(commandApdu[0]));
        if (Arrays.equals(SELECT_AID_COMMAND, commandApdu)) {
            byte[] payloadBytes = PAYLOAD.getBytes();
            Log.d(TAG, "Payload: " + ByteArrayToHexString(payloadBytes));
            return ConcatArrays(SELECT_OK_SW, payloadBytes);
        } else if (commandApdu[0] == 1) {
            Log.d("FROM ARDUINO: ", ByteToHexString(commandApdu[0]));
            notifyDataReceived(commandApdu[0]);
            return DATA_RESPONSE_OK;
        } else {
            Log.d("END: ", ByteArrayToHexString(commandApdu));
            return UNKNOWN_CMD_SW;
        }
    }

    private void notifyDataReceived(byte data) {
        if (data == 1) {
            Intent intent = new Intent(BROADCAST_INTENT_DATA_RECEIVED);
            intent.putExtra("data", "Unlocked");
            LocalBroadcastManager.getInstance(getApplicationContext()).sendBroadcast(intent);
        }
    }

    public static String ByteToHexString(byte value) {
        StringBuilder stringBuilder = new StringBuilder(1);
        stringBuilder.append(String.format("%02x", Byte.valueOf(value)));
        return stringBuilder.toString();
    }

    public static String ByteArrayToHexString(byte[] values) {
        StringBuilder stringBuilder = new StringBuilder(values.length);
        int length = values.length;
        for (int i = 0; i < length; i++) {
            stringBuilder.append(String.format("%02x", Byte.valueOf(values[i])));
        }
        return stringBuilder.toString();
    }

    public static byte[] HexStringToByteArray(String s) throws IllegalArgumentException {
        int len = (s.length() / 2) * 2;
        byte[] data = new byte[(len / 2)];
        for (int i = 0; i < len; i += 2) {
            data[i / 2] = (byte) ((Character.digit(s.charAt(i), 16) << 4) + Character.digit(s.charAt(i + 1), 16));
        }
        return data;
    }

    public static byte[] ConcatArrays(byte[] first, byte[]... rest) {
        int totalLength = first.length;
        for (byte[] array : rest) {
            totalLength += array.length;
        }
        byte[] result = Arrays.copyOf(first, totalLength);
        int offset = first.length;
        for (byte[] array2 : rest) {
            System.arraycopy(array2, 0, result, offset, array2.length);
            offset += array2.length;
        }
        return result;
    }

    public void onDeactivated(int reason) {
        Log.d(TAG, "Link deactivated: " + reason);
    }

    public void onDestroy() {
        Log.d(TAG, "Stop");
    }
}
