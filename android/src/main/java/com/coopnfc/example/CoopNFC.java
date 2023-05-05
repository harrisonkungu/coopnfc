package com.coopnfc.example;

import android.app.Activity;
import android.app.PendingIntent;
import android.util.Log;

import android.nfc.NfcAdapter;

import com.getcapacitor.PluginCall;
import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;
import androidx.appcompat.app.AlertDialog;

import android.content.Intent;
import android.content.Context;
import com.getcapacitor.Plugin;
import android.widget.Toast;  

import androidx.appcompat.app.AppCompatActivity;


public class CoopNFC extends Plugin  implements CardNfcAsyncTask.CardNfcInterface{

    private CardNfcAsyncTask mCardNfcAsyncTask;
//  private NfcAdapter mNfcAdapter;
    private String mDoNotMoveCardMessage;
    private String mUnknownEmvCardMessage;
    private String mCardWithLockedNfcMessage;
    private boolean mIsScanNow;
    private boolean mIntentFromCreate;
    private CardNfcUtils mCardNfcUtils;
    private AlertDialog mTurnNfcDialog;
    private Context mContext;

    NfcAdapter nfcAdapter;
    PendingIntent pendingIntent;
    Intent intent;
    AppCompatActivity activity;

    private String message;

    protected static final int REQUEST_NFC = 1993;
    private static final String TAG = "NFC card plugin";


//  @Override
//     protected void onNewIntent(Intent intent) {
//         // super.onNewIntent(intent);
//         if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
//             mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate).build();
//         }
//     }

    @Override
    public void load() {
        mContext = mContext;
        activity = getActivity();
    }

    @Override
    public void onPause(boolean multitasking) {

    }

    @Override
    public void onResume(boolean multitasking) {

    }

//    @Override
//    protected void onNewIntent(Intent intent) {
//        super.onNewIntent(intent);
//        if (mNfcAdapter != null && mNfcAdapter.isEnabled()) {
//            mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate)
//                    .build();
//        }
//    }

    public String echo(String value) {
        Log.i("Echo", value);
        return value;
    }


    public String getNfcStatus(Context cont) {
        NfcAdapter nfcAdapter = NfcAdapter.getDefaultAdapter(cont);
        if (nfcAdapter == null) {
            return "NFC Not Supported by device";
        } else if (!nfcAdapter.isEnabled()) {
            return "NFC Not enabled";
        } else {
            return "NFC available";
        }
    }

    private void initNfcAdapter(PluginCall call) {
        //Initialise NfcAdapter
        nfcAdapter = NfcAdapter.getDefaultAdapter(getContext());
        //If no NfcAdapter, display that the device has no NFC
        if (nfcAdapter == null){
            Log.d(TAG, "NO NFC Capabilities");
            call.reject("NO NFC Capabilities");
        } else {
            //PendingIntent.getActivity(Context,requestcode(identifier for
            //                           intent),intent,int)
            pendingIntent = PendingIntent.getActivity(activity,0,new Intent(activity, activity.getClass()).addFlags(Intent.FLAG_ACTIVITY_SINGLE_TOP),0);
        }
    }


    public String CallNfc(Context cont , Activity act, Intent intt){

//        try{
             nfcAdapter = NfcAdapter.getDefaultAdapter(cont);
            if (nfcAdapter == null){
                message = "NFC Not Supported by device";
            }else if(!nfcAdapter.isEnabled()){
                message = "Please Enable NFC";
            } else {
                mCardNfcUtils = new CardNfcUtils(act);
                initNfcMessages();
                mIntentFromCreate = true;
//                onNewIntent(getIntent());
//                CardNfcAsyncTask.Builder builder  = new CardNfcAsyncTask.Builder(this, intt,true);
//                CardNfcAsyncTask asyncTask = builder.build();
//                String card = asyncTask.getCardNumber();
//                Log.d("info", "came here -->.");
//                String prettycard = getPrettyCardNumber(card);
//                return prettycard;

                  mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intt, mIntentFromCreate).build();
                Log.d("-------->>>", String.valueOf(mCardNfcAsyncTask.getCardNumber()));
                message = "Card Number " +String.valueOf(mCardNfcAsyncTask.getCardNumber());

            }
//        }catch(Exception e){
//            return "NFC Error" +e;
//        }


        return message;
    }


  private String getPrettyCardNumber(String card){
        String div = " - ";
        return  card.substring(0,4) + div + card.substring(4,8) + div + card.substring(8,12) +div + card.substring(12,16);
    }


    private void initNfcMessages(){
        mDoNotMoveCardMessage = "Do Not Move Card";
        mCardWithLockedNfcMessage = "This card has NFC Locked";
        mUnknownEmvCardMessage = "Unknown ENV Card";
    }

   public void finishNfcReadCard() {
        mCardNfcAsyncTask = null;
        mIsScanNow = false;
    }

    public void cardWithLockedNfc() {
        showSnackBar("Card Locked");
    }
    private void showSnackBar(String message){
        Toast.makeText(mContext, message ,Toast.LENGTH_SHORT).show();
    }

     public void unknownEmvCard() {
        showSnackBar("Unknown Card");
    }
    public void doNotMoveCardSoFast() {
        showSnackBar("Do not move card");
    }

     public void cardIsReadyToRead() {
        Log.d("Card Ready ", "Card is ready to scan");
        String card = mCardNfcAsyncTask.getCardNumber();
        card = getPrettyCardNumber(card);
        String expiredDate = mCardNfcAsyncTask.getCardExpireDate();
        String cardType = mCardNfcAsyncTask.getCardType();
        showSnackBar(card);
        String prettycard = getPrettyCardNumber(card);
        getCardNo(prettycard);
    }
    public String getCardNo(String prettycard){
        return prettycard;
    }

      public void startNfcReadCard() {
        mIsScanNow = true;
        // mProgressDialog.show();
    }

//   private void showTurnOnNfcDialog(){
//         if (mTurnNfcDialog == null) {
//             String title = "Device NFC";
//             String mess = "Enable NFC?";
//             String pos = "Yes, Sure";
//             String neg = "Noo!";
//             mTurnNfcDialog = new AlertDialog.Builder(this)
//                     .setTitle(title)
//                     .setMessage(mess)
//                     .setPositiveButton(pos, new DialogInterface.OnClickListener() {
//                         @Override
//                         public void onClick(DialogInterface dialogInterface, int i) {
//                             // Send the user to the settings page and hope they turn it on
//                             if (android.os.Build.VERSION.SDK_INT >= 16) {
//                                 startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
//                             } else {
//                                 startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
//                             }
//                         }
//                     })
//                     .setNegativeButton(neg, new DialogInterface.OnClickListener() {
//                         @Override
//                         public void onClick(DialogInterface dialogInterface, int i) {
//                             // onBackPressed();
//                         }
//                     }).create();
//         }
//         mTurnNfcDialog.show();
//     }

 }
