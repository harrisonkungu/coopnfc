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

//    private CoopNFC implementation;
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

    private boolean mIsScanNow;

    @Override
    public void load() {
//        implementation = new CoopNFC();
        context = getContext();
        intent = new Intent(getContext(), getClass());
        activity = getActivity();
        nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        pendingIntent = PendingIntent.getActivity(context, 0, intent, PendingIntent.FLAG_UPDATE_CURRENT);
        mCardNfcUtils = new CardNfcUtils(activity);
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
    public void handleOnPause() {
        Log.d("NFC", "App is in background");
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(context);
        if (nfcAdapter != null) {
            nfcAdapter.disableForegroundDispatch(activity);
        }
    }

    @Override
    public void handleOnResume() {
        mIntentFromCreate = false;
        if (nfcAdapter != null && !nfcAdapter.isEnabled()){
        } else if (nfcAdapter != null){
            if (!mIsScanNow){

            }
            mCardNfcUtils.enableDispatch();
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
//        String res = implementation.CallNfc(context , activity, intent);
//         //String res = implementation.getNfcStatus(context);
//        JSObject ret = new JSObject();
//        ret.put("result", res);
//        call.resolve(ret);
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
        try{
            nfcAdapter = NfcAdapter.getDefaultAdapter(context);
            if (nfcAdapter == null){
                nfcNotAvailable();
            }else if(!nfcAdapter.isEnabled()){
                nfcDisabled();
            } else {
                mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate).build();
                mCardNfcUtils = new CardNfcUtils(activity);
                getActivity()
                        .runOnUiThread(
                                () ->{
                                    if (cardReady ==true ){
                                        PluginCall call = getSavedCall();
                                        String card = mCardNfcAsyncTask.getCardNumber();
                                    }
                                });
            }
        }catch(Exception ex){
            Log.e("EXCEPTION", "An exception occurred during reading NFC");
        }

    }



    @PluginMethod
    public void startScan(PluginCall call) {
        saveCall(call);
        scan();
    }


    @Override
    public void startNfcReadCard() {
        Log.d("start reading 1", "ready to start reading nfc here");
    }

    @Override
    public void cardIsReadyToRead() {
        try{
            cardReady = true;
            JSObject jsObject = new JSObject();
            jsObject.put("hasContent", true);
            jsObject.put("content", mCardNfcAsyncTask.getCardNumber());
            jsObject.put("cardType", mCardNfcAsyncTask.getCardType());
            jsObject.put("cardNumber", mCardNfcAsyncTask.getCardNumber());
            jsObject.put("expiryDate", mCardNfcAsyncTask.getCardExpireDate());
            PluginCall call = getSavedCall();
            if (call != null) {
                if (call.isKeptAlive()) {
                    call.resolve(jsObject);
                } else {
                    call.resolve(jsObject);
                    mCardNfcAsyncTask = null;
                }
            } else {

            }
        }catch( Exception ex){
            Log.e("EXCEPTION","An exception occured during reading" +ex);
        }

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
        Log.d("Finished --", "I am done with reading");

        if (nfcAdapter != null) {
            nfcAdapter.disableReaderMode(activity);
            nfcAdapter = null;
        }
        mCardNfcAsyncTask = null;
    }

    private void nfcDisabled(){
        finishNfcReadCard();
        JSObject jsObject = new JSObject();
        jsObject.put("hasContent", true);
        jsObject.put("content", "NFC Disabled");
        jsObject.put("cardType", "CARD_ERR");
        jsObject.put("cardNumber", "CARD_ERR");
        jsObject.put("expiryDate", "CARD_ERR");
        PluginCall call = getSavedCall();
        call.resolve(jsObject);
    }

    private void nfcNotAvailable(){
        finishNfcReadCard();
        JSObject jsObject = new JSObject();
        jsObject.put("hasContent", true);
        jsObject.put("content", "NFC Not Available");
        jsObject.put("cardType", "CARD_ERR");
        jsObject.put("cardNumber", "CARD_ERR");
        jsObject.put("expiryDate", "CARD_ERR");
        PluginCall call = getSavedCall();
        call.resolve(jsObject);
    }


}



