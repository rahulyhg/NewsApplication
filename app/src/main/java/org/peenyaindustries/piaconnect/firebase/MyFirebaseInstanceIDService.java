package org.peenyaindustries.piaconnect.firebase;

import android.content.Intent;
import android.content.SharedPreferences;
import android.support.v4.content.LocalBroadcastManager;
import android.util.Log;

import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.FirebaseInstanceIdService;

import org.peenyaindustries.piaconnect.app.Config;
import org.peenyaindustries.piaconnect.callback.VolleyResponseCallback;
import org.peenyaindustries.piaconnect.helper.PrefManager;
import org.peenyaindustries.piaconnect.json.FetchData;

public class MyFirebaseInstanceIDService extends FirebaseInstanceIdService {

    @Override
    public void onTokenRefresh() {
        String token = FirebaseInstanceId.getInstance().getToken();
        storeToken(token);

        // Saving reg id to shared preferences
        storeRegIdInPref(token);

        // Notify UI that registration has completed, so the progress indicator can be hidden.
        Intent registrationComplete = new Intent(Config.REGISTRATION_COMPLETE);
        registrationComplete.putExtra("token", token);
        LocalBroadcastManager.getInstance(this).sendBroadcast(registrationComplete);
    }

    private void storeToken(String token) {
        PrefManager pref = new PrefManager(this);
        String[] param = {token, pref.getMobileNumber()};
        FetchData f = new FetchData(this);
        f.postData(new VolleyResponseCallback() {
            @Override
            public void onSuccess(String response) {
                Log.e("TokenInsertResponse : ", response);
            }
        }, Config.URL_STORE_TOKEN, param);
    }

    private void storeRegIdInPref(String token) {
        SharedPreferences pref = getApplicationContext().getSharedPreferences(Config.SHARED_PREF, 0);
        SharedPreferences.Editor editor = pref.edit();
        editor.putString("regId", token);
        editor.commit();
    }
}
