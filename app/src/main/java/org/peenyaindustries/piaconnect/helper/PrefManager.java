package org.peenyaindustries.piaconnect.helper;

import android.content.Context;
import android.content.SharedPreferences;

import java.util.HashMap;

public class PrefManager {

    // Shared Preferences
    private SharedPreferences pref;

    // Editor for Shared preferences
    private SharedPreferences.Editor editor;

    // Context
    private Context _context;

    // Shared pref mode
    private int PRIVATE_MODE = 0;

    // Shared preferences file name
    private static final String PREF_NAME = "PIAConnect";

    // All Shared Preferences Keys
    private static final String KEY_IS_WAITING_FOR_SMS = "IsWaitingForSms";
    private static final String KEY_MOBILE_NUMBER = "mobile_number";
    private static final String KEY_OTP = "otp";
    private static final String KEY_IS_LOGGED_IN = "isLoggedIn";
    private static final String KEY_UNIQUE_ID = "unique_id";
    public static final String KEY_NAME = "name";
    private static final String KEY_COMPANY = "company";
    public static final String KEY_EMAIL = "email";
    private static final String KEY_MOBILE = "mobile";
    private static final String KEY_EMAIL_VERIFIED = "email_verified";
    private static final String KEY_MOBILE_VERIFIED = "mobile_verified";

    private static final String KEY_POST_ID = "post_id";

    public PrefManager(Context context) {
        this._context = context;
        pref = _context.getSharedPreferences(PREF_NAME, PRIVATE_MODE);
        editor = pref.edit();
    }

    public void setIsWaitingForSms(boolean isWaiting) {
        editor.putBoolean(KEY_IS_WAITING_FOR_SMS, isWaiting);
        editor.commit();
    }

    public boolean isWaitingForSms() {
        return pref.getBoolean(KEY_IS_WAITING_FOR_SMS, false);
    }

    public void setMobileNumber(String mobileNumber) {
        editor.putString(KEY_MOBILE_NUMBER, mobileNumber);
        editor.commit();
    }

    public String getMobileNumber() {
        return pref.getString(KEY_MOBILE_NUMBER, null);
    }

    public void setOtp(String otp) {
        editor.putString(KEY_OTP, otp);
        editor.commit();
    }

    public String getOtp() {
        return pref.getString(KEY_OTP, null);
    }

    public void setPostId(String postId) {
        editor.putString(KEY_POST_ID, postId);
        editor.commit();
    }

    public String getPostId() {
        return pref.getString(KEY_POST_ID, null);
    }

    public void storeUserProfile(String uid, String name, String company, String email, String mobile, String emailVerified, String mobileVerified) {
        editor.putString(KEY_UNIQUE_ID, uid);
        editor.putString(KEY_NAME, name);
        editor.putString(KEY_COMPANY, company);
        editor.putString(KEY_EMAIL, email);
        editor.putString(KEY_MOBILE, mobile);
        editor.putString(KEY_EMAIL_VERIFIED, emailVerified);
        editor.putString(KEY_MOBILE_VERIFIED, mobileVerified);
        editor.putBoolean(KEY_IS_LOGGED_IN, true);
        editor.commit();
    }

    public boolean isLoggedIn() {
        return pref.getBoolean(KEY_IS_LOGGED_IN, false);
    }

    public void clearSession() {
        editor.clear();
        editor.commit();
    }

    public HashMap<String, String> getUserDetails() {
        HashMap<String, String> profile = new HashMap<>();
        profile.put("unique_id", pref.getString(KEY_UNIQUE_ID, null));
        profile.put("name", pref.getString(KEY_NAME, null));
        profile.put("company", pref.getString(KEY_COMPANY, null));
        profile.put("email", pref.getString(KEY_EMAIL, null));
        profile.put("mobile", pref.getString(KEY_MOBILE, null));
        profile.put("email_verified", pref.getString(KEY_EMAIL_VERIFIED, null));
        profile.put("mobile_verified", pref.getString(KEY_MOBILE_VERIFIED, null));
        return profile;
    }
}
