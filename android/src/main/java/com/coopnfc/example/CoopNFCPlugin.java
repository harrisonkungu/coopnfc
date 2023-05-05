package com.coopnfc.example;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.nfc.FormatException;
import android.nfc.NdefMessage;
import android.nfc.NdefRecord;
import android.nfc.NfcAdapter;
import android.nfc.Tag;
import android.nfc.tech.IsoDep;
import android.nfc.tech.Ndef;
import android.nfc.tech.NdefFormatable;
import android.nfc.tech.NfcA;
import android.util.Log;

import androidx.appcompat.app.AppCompatActivity;

import com.getcapacitor.JSObject;
import com.getcapacitor.Plugin;
import com.getcapacitor.PluginCall;
import com.getcapacitor.PluginMethod;
import com.getcapacitor.annotation.CapacitorPlugin;
import com.getcapacitor.annotation.Permission;

import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;
import java.io.IOException;

@CapacitorPlugin(
        name = "CoopNFC",
        permissions = {
                @Permission(
                        strings = { "android.permission.NFC" },
                        alias = "nfc"
                )
        }
)

public class CoopNFCPlugin extends Plugin implements CardNfcAsyncTask.CardNfcInterface{

    private CoopNFC implementation;
    private Context context;
    private Intent intent;
    private AppCompatActivity activity;
    NfcAdapter nfcAdapter;
    private String message;

    private CardNfcUtils mCardNfcUtils;

    private CardNfcAsyncTask mCardNfcAsyncTask;
    private boolean mIntentFromCreate;

    private boolean cardReady = false;

    private PluginCall caller;
    private PendingIntent pendingIntent;

    @Override
    public void load() {
        implementation = new CoopNFC();
        context = getContext();
        intent = new Intent(getContext(), getClass());
        activity = getActivity();
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
    }



    @Override
    @PluginMethod()
    public void handleOnNewIntent(Intent intent) {
        super.handleOnNewIntent(intent);
        Log.d("NFC Status", "Ready to read----->1");
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter == null){
            Log.d("NFC Status", "Ready to read----->2");
            message = "NFC Not Supported by device";
        } else {
            Log.d("NFC Status", "Ready to read----->3");
            if (NfcAdapter.ACTION_TECH_DISCOVERED.equals(intent.getAction())) {
                Log.d("NFC Status", "Ready to read----->4");
                mCardNfcUtils = new CardNfcUtils(activity);
                mIntentFromCreate = true;
                mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate).build();
                Log.d("NFC Status", "Ready to read----->");

            }
        }


    }



    @Override
    public void onPause(boolean multitasking) {
        Log.d("NFC", "App is in background");
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(activity);
        }
    }

    @Override
    public void onResume(boolean multitasking) {
        Log.d("NFC", "App is in foreground");
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);

//        String[][] techListsArray = new String[][] {
//                new String[] { NfcA.class.getName() },
//                new String[] { NfcB.class.getName() },
//                new String[] { NfcF.class.getName() },
//                new String[] { NfcV.class.getName() },
//                new String[] { IsoDep.class.getName() },
//                new String[] { MifareClassic.class.getName() },
//                new String[] { MifareUltralight.class.getName() }
//        };
//
        String[][] techListsArray = new String[][] {
                new String[] { IsoDep.class.getName() }
        };

        if (nfcAdapter != null) {
            IntentFilter[] filters = new IntentFilter[] {
                    new IntentFilter(NfcAdapter.ACTION_NDEF_DISCOVERED)
            };
            nfcAdapter.enableForegroundDispatch(activity, pendingIntent, filters, techListsArray);
        }
    }


    @PluginMethod()
    public void stopNfcListening(PluginCall call) {
        if (nfcAdapter == null) {
            call.reject("NFC adapter is null");
            return;
        }

        nfcAdapter.disableForegroundDispatch(getActivity());
        call.resolve();
    }

    @PluginMethod
    public void waitNFC(PluginCall call) {
        String res = implementation.CallNfc(context , activity, intent);
         //String res = implementation.getNfcStatus(context);
        JSObject ret = new JSObject();
        ret.put("result", res);
        call.resolve(ret);
    }

    @PluginMethod
    public void NFCStatus(PluginCall call) {
//        String res = implementation.getNfcStatus();
//        JSObject ret = new JSObject();
//        ret.put("result", res);
//        call.resolve(ret);
    }



    @PluginMethod
    public void echo(PluginCall call) {
        String value = call.getString("value");
        JSObject ret = new JSObject();
        ret.put("value", "Default Web Implementation");
        saveCall(call);
        call.resolve(ret);
    }

    @SuppressLint("MissingPermission")
    @PluginMethod()
    public void startNfcListening(PluginCall call) {
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter == null){
            message = "NFC Not Supported by device";
        }else if(!nfcAdapter.isEnabled()){
            message = "Please Enable NFC";
        } else {

            mCardNfcUtils = new CardNfcUtils(activity);
            mIntentFromCreate = true;
            mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate).build();
            Log.d("NFC Status", "Ready to read");
            cardReady = true;
//            String card = mCardNfcAsyncTask.getCardNumber();
        }
    }

    private void scan() {

        Log.d("test------<>>","message2222");
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter == null){
            message = "NFC Not Supported by device";
        }else if(!nfcAdapter.isEnabled()){
            message = "Please Enable NFC";
        } else {
            Log.d("test------<>>","message22277777");
            mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate).build();
            mCardNfcUtils = new CardNfcUtils(activity);
            getActivity()
                    .runOnUiThread(
                            () ->{
                                Log.d("test------<>>","message2222999999");
                    if (cardReady ==true ){
                        Log.d("test------<>>","message34343434343434");
                        PluginCall call = getSavedCall();
                        String card = mCardNfcAsyncTask.getCardNumber();
                        Log.d("NFC Status", "Ready to read "+card);
                    }

        });

        }
    }



    @PluginMethod
    public void startScan(PluginCall call) {
        saveCall(call);
        scan();
    }


    @Override
    public void startNfcReadCard() {

    }

    @Override
    public void cardIsReadyToRead() {
        cardReady = true;
        Log.d("cardIsReadyToRead","message2222999999=====>>");
        JSObject jsObject = new JSObject();
        jsObject.put("hasContent", true);
        jsObject.put("content", mCardNfcAsyncTask.getCardNumber());
        jsObject.put("cardType", mCardNfcAsyncTask.getCardType());
        jsObject.put("cardNumber", mCardNfcAsyncTask.getCardNumber());
        jsObject.put("expiryDate", mCardNfcAsyncTask.getCardExpireDate());
        PluginCall call = getSavedCall();

        if (call != null) {
            Log.i("cardIsReadyToRead","message2222999999=====>>   "+mCardNfcAsyncTask.getCardNumber());
            if (call.isKeptAlive()) {
                    call.resolve(jsObject);
            } else {
                call.resolve(jsObject);
                //destroy nfc here
            }
        } else {
            //destroy nfc here
        }

//        String card = mCardNfcAsyncTask.getCardNumber();
//        String expiredDate = mCardNfcAsyncTask.getCardExpireDate();
//        String cardType = mCardNfcAsyncTask.getCardType();
//        Log.d("---------->>>came here", "I executed this far" +card);
    }

    @Override
    public void doNotMoveCardSoFast() {

    }

    @Override
    public void unknownEmvCard() {

    }

    @Override
    public void cardWithLockedNfc() {

    }

    @Override
    public void finishNfcReadCard() {
        mCardNfcAsyncTask = null;
    }



}



