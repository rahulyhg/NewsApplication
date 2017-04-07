package org.peenyaindustries.piaconnect.app;

public class Config {

    //Server URL configuration
    private static final String IP = "peenyaindustries.org";
    private static final String BASE_URL_AUTH = "http://" + IP + "/pia_connect/";
    public static final String URL_REQUEST_SMS = BASE_URL_AUTH + "request_sms.php";
    public static final String URL_REQUEST_SMS_RETURNING_USER = BASE_URL_AUTH + "request_sms_returning_user.php";
    public static final String URL_VERIFY_OTP = BASE_URL_AUTH + "/verify_otp.php";
    public static final String URL_STORE_TOKEN = BASE_URL_AUTH + "/store_token.php";

    //SMS Provider identification
    public static final String SMS_ORIGIN = "PIAPIA";

    //special character to prefix with OTP
    public static final String OTP_DELIMITER = ":";

    //Posts from Wordpress
    private static final String BASE_URL_NEWS = "http://www.peenyaindustries.org/api/";
    public static final String URL_POST_CATEGORIES = BASE_URL_NEWS + "get_category_posts/?count=-1&id=";
    public static final String URL_CATEGORIES = BASE_URL_NEWS + "get_category_index";
    public static final String URL_POST_ALL = BASE_URL_NEWS + "get_posts/?count=-1";
    public static final String URL_POST = BASE_URL_NEWS + "get_posts/?count=1";
    public static final String URL_POST_RECENT = BASE_URL_NEWS + "get_posts/?count=";
    public static final String URL_POST_COMMENT = BASE_URL_NEWS + "submit_comment";
    public static final String URL_TEST = "https://jsonplaceholder.typicode.com/posts";

    //Push Notifications
    // global topic to receive app wide push notifications
    public static final String TOPIC_GLOBAL = "global";

    // broadcast receiver intent filters
    public static final String REGISTRATION_COMPLETE = "registrationComplete";
    public static final String PUSH_NOTIFICATION = "pushNotification";

    // id to handle the notification in the notification tray
    public static final int NOTIFICATION_ID = 100;
    public static final int NOTIFICATION_ID_BIG_IMAGE = 101;

    public static final String SHARED_PREF = "ah_firebase";
}
