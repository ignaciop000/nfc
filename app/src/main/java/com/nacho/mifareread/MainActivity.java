package com.nacho.mifareread;

import android.content.Intent;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.MifareClassic;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import java.io.IOException;

public class MainActivity extends AppCompatActivity {
    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        resolveIntent(getIntent());
    }

    /**
     * Treat NfcAdapter.ACTION_TECH_DISCOVERED incoming intents and start the tag
     * reading.
     *
     * @param intent
     */
    private void resolveIntent(Intent intent) {
        if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {

//            Get Tag object from intent
            Tag tagFromIntent = intent.getParcelableExtra(NfcAdapter.EXTRA_TAG);
//            Recover the MifareClassic object from the Tag object
            MifareClassic tag = MifareClassic.get(tagFromIntent);
            try {
                tag.connect();
                if (tag.isConnected()) {
//                Get general information
                    int sectorCount = tag.getSectorCount();
                    Log.i(TAG, "Sector Count: " + sectorCount);
                    int tagSize = tag.getSize();
                    Log.i(TAG, "Tag Size: " + tagSize);

                    for (int secId = 0; secId < sectorCount; secId++) {
                        boolean auth = false;
//                        0x00 0x00 0x00 0x00 0x00 0x00
//                        0xA0 0xB0 0xC0 0xD0 0xE0 0xF0
//                        0xA1 0xB1 0xC1 0xD1 0xE1 0xF1
//                        0xB0 0xB1 0xB2 0xB3 0xB4 0xB5
//                        0xAA 0xBB 0xCC 0xDD 0xEE 0xFF
//                        MifareClassic.KEY_MIFARE_APPLICATION_DIRECTORY
//                        MifareClassic.KEY_NFC_FORUM
//                        MifareClassic.KEY_DEFAULT
                        byte[] keyA = MifareClassic.KEY_DEFAULT;
                        byte[] keyB = MifareClassic.KEY_DEFAULT;
                        auth = tag.authenticateSectorWithKeyA(secId, keyA);
                        Log.i(TAG, "Authenticate KeyA: " + auth);
                        auth = tag.authenticateSectorWithKeyB(secId, keyB);
                        Log.i(TAG, "Authenticate KeyB: " + auth);
//                    Get the number of blocks for the sector « secId »
                        int blockCountInSector = tag.getBlockCountInSector(secId);
                        Log.i(TAG, "Block Count Sector " + secId + ": " + blockCountInSector);
//                    Read the block number « blkId »
                        for (int blkId = 0; blkId <= blockCountInSector; blkId++) {
                            byte[] block = tag.readBlock(blkId);
                            String s_block = NFCUtils.ByteArrayToHexString(block);
                            Log.i(TAG, "[ Sector: " + secId + " Block: " + blkId + " ]: " + s_block);
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
            }

        }
    }

}
