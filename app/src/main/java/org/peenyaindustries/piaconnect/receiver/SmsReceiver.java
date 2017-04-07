package org.peenyaindustries.piaconnect.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.os.Build;
import android.os.Bundle;
import android.telephony.SmsMessage;
import android.util.Log;

import org.peenyaindustries.piaconnect.app.Config;
import org.peenyaindustries.piaconnect.helper.PrefManager;
import org.peenyaindustries.piaconnect.service.HttpService;

public class SmsReceiver extends BroadcastReceiver {

    private static final String TAG = SmsReceiver.class.getSimpleName();

    public void onReceive(Context context, Intent intent)
    {
        Bundle myBundle = intent.getExtras();
        SmsMessage [] messages = null;

        try {
            if (myBundle != null)
            {
                Object [] pdus = (Object[]) myBundle.get("pdus");

                messages = new SmsMessage[pdus.length];

                for (int i = 0; i < messages.length; i++)
                {
                    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
                        String format = myBundle.getString("format");
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i], format);
                    }
                    else {
                        messages[i] = SmsMessage.createFromPdu((byte[]) pdus[i]);
                    }
                    String senderAddress = messages[i].getDisplayOriginatingAddress();
                    String message = messages[i].getDisplayMessageBody();

                    Log.e(TAG, "Received SMS: " + message + ", Sender: " + senderAddress);

                 // if the SMS is not from our gateway, ignore the message
                  if (!senderAddress.toLowerCase().contains(Config.SMS_ORIGIN.toLowerCase())) {
                      return;
                  }

                    // verification code from sms
                    String verificationCode = getVerificationCode(message);

                    Log.e(TAG, "OTP received: " + verificationCode);
                    PrefManager pref = new PrefManager(context);
                    Intent httpIntent = new Intent(context, HttpService.class);
                    httpIntent.putExtra("otp", verificationCode);
                    httpIntent.putExtra("mobile", pref.getMobileNumber());
                    Log.e(TAG, " Number " + pref.getMobileNumber());
                    context.startService(httpIntent);

                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * Getting the OTP from sms message body
     * ':' is the separator of OTP from the message
     *
     * @param message
     * @return
     */
    private String getVerificationCode(String message) {
        String code = null;
        int index = message.indexOf(Config.OTP_DELIMITER);

        if (index != -1) {
            int start = index + 2;
            int length = 6;
            code = message.substring(start, start + length);
            return code;
        }

        return code;
    }
}
