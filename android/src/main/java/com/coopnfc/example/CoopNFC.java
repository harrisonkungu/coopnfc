package com.coopnfc.example;

import android.util.Log;

import android.nfc.NfcAdapter;
import com.pro100svitlo.creditCardNfcReader.CardNfcAsyncTask;
import com.pro100svitlo.creditCardNfcReader.utils.CardNfcUtils;
import androidx.appcompat.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;

public class CoopNFC {

 private CardNfcAsyncTask mCardNfcAsyncTask;
    private NfcAdapter mNfcAdapter;
    private String mDoNotMoveCardMessage;
    private String mUnknownEmvCardMessage;
    private String mCardWithLockedNfcMessage;
    private boolean mIsScanNow;
    private boolean mIntentFromCreate;
    private CardNfcUtils mCardNfcUtils;
    private AlertDialog mTurnNfcDialog;

    public String echo(String value) {
        Log.i("Echo", value);
        return value;
    }

    public String CallNfc(){
         mNfcAdapter = NfcAdapter.getDefaultAdapter(this);
        if (mNfcAdapter == null){
              String message = "NFC Not Supported by device";
              return message;
        }else if(!mNfcAdapter.isEnabled()){
              String enable = "Please Enable NFC";
              return enable;
        } else {
            mCardNfcUtils = new CardNfcUtils(this);
            initNfcMessages();
            mIntentFromCreate = true;
            // onNewIntent(getIntent());
            mCardNfcAsyncTask = new CardNfcAsyncTask.Builder(this, intent, mIntentFromCreate).build();
            String card = mCardNfcAsyncTask.getCardNumber();
            card = getPrettyCardNumber(card);
            return card


        }
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



  private void showTurnOnNfcDialog(){
        if (mTurnNfcDialog == null) {
            String title = "Device NFC";
            String mess = "Enable NFC?";
            String pos = "Yes, Sure";
            String neg = "Noo!";
            mTurnNfcDialog = new AlertDialog.Builder(this)
                    .setTitle(title)
                    .setMessage(mess)
                    .setPositiveButton(pos, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            // Send the user to the settings page and hope they turn it on
                            if (android.os.Build.VERSION.SDK_INT >= 16) {
                                startActivity(new Intent(android.provider.Settings.ACTION_NFC_SETTINGS));
                            } else {
                                startActivity(new Intent(android.provider.Settings.ACTION_WIRELESS_SETTINGS));
                            }
                        }
                    })
                    .setNegativeButton(neg, new DialogInterface.OnClickListener() {
                        @Override
                        public void onClick(DialogInterface dialogInterface, int i) {
                            onBackPressed();
                        }
                    }).create();
        }
        mTurnNfcDialog.show();
    }

}
// package nepheus.capacitor.nfclaunch;

// import android.content.Intent;
// import android.nfc.NdefMessage;
// import android.nfc.NfcAdapter;
// import android.os.Parcelable;

// public class NFCLaunch {

//     public String readNDefMessage(Intent intent) {
//         Parcelable[] ndefMessageArray = intent.getParcelableArrayExtra(NfcAdapter.EXTRA_NDEF_MESSAGES);
//         NdefMessage ndefMessage = (NdefMessage) ndefMessageArray[0];
//         String message = new String(ndefMessage.getRecords()[0].getPayload());
//         return message;
//     }
// }