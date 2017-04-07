package org.peenyaindustries.piaconnect.service;

import android.app.IntentService;
import android.content.Intent;
import android.support.annotation.Nullable;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.peenyaindustries.piaconnect.activity.MainActivity;
import org.peenyaindustries.piaconnect.app.Config;
import org.peenyaindustries.piaconnect.helper.Constants;
import org.peenyaindustries.piaconnect.helper.PrefManager;
import org.peenyaindustries.piaconnect.network.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class HttpService extends IntentService {

    private static String TAG = HttpService.class.getSimpleName();

    public HttpService() {
        super(HttpService.class.getSimpleName());
    }

    @Override
    protected void onHandleIntent(@Nullable Intent intent) {
        if (intent != null) {
            String otp = intent.getStringExtra("otp");
            String mobile = intent.getStringExtra("mobile");
            Log.e(TAG, mobile);
            verifyOtp(otp, mobile);
        }
    }

    /**
     * Posting OTP to server and activating the user
     *
     * @param otp - OTP received in SMS
     */
    private void verifyOtp(final String otp, final String mobile) {

        StringRequest strReq = new StringRequest(Request.Method.POST, Config.URL_VERIFY_OTP, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);

                try {
                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    if (!error) {
                        // parsing the user profile information
                        JSONObject profileObj = responseObj.getJSONObject("profile");

                        String uid = Constants.NA;
                        String name = Constants.NA;
                        String company = Constants.NA;
                        String email = Constants.NA;
                        String mobile = Constants.NA;
                        String emailVerified = Constants.NA;
                        String mobileVerified = Constants.NA;

                        if (profileObj.has("unique_id")) {
                            uid = profileObj.getString("unique_id");
                        }

                        if (profileObj.has("name")) {
                            name = profileObj.getString("name");
                        }

                        if (profileObj.has("company")) {
                            company = profileObj.getString("company");
                        }

                        if (profileObj.has("email")) {
                            email = profileObj.getString("email");
                        }

                        if (profileObj.has("mobile")) {
                            mobile = profileObj.getString("mobile");
                        }

                        if (profileObj.has("email_verified")) {
                            emailVerified = profileObj.getString("email_verified");
                        }

                        if (profileObj.has("mobile_verified")) {
                            mobileVerified = profileObj.getString("mobile_verified");
                        }

                        PrefManager pref = new PrefManager(getApplicationContext());

                        pref.storeUserProfile(uid, name, company, email, mobile, emailVerified, mobileVerified);

                        Intent intent = new Intent(HttpService.this, MainActivity.class);
                        intent.setFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP);
                        intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                        startActivity(intent);

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();

                    } else {
                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_LONG).show();
                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                error.printStackTrace();
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("mobile", mobile);
                params.put("otp", otp);

                Log.e(TAG, "Posting params: " + params.toString());
                return params;
            }
        };
        VolleySingleton.getInstance(this).addToRequestQueue(strReq);
    }
}
