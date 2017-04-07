package org.peenyaindustries.piaconnect.activity;


import android.content.Intent;
import android.content.pm.PackageManager;
import android.os.Build;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.design.widget.TextInputLayout;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.Log;
import android.view.KeyEvent;
import android.view.View;
import android.view.WindowManager;
import android.view.inputmethod.InputMethodManager;
import android.support.v4.content.ContextCompat;
import android.support.v4.app.ActivityCompat;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONException;
import org.json.JSONObject;
import org.peenyaindustries.piaconnect.R;
import org.peenyaindustries.piaconnect.app.Config;
import org.peenyaindustries.piaconnect.helper.ConnectionDetector;
import org.peenyaindustries.piaconnect.helper.DatabaseHelper;
import org.peenyaindustries.piaconnect.helper.PrefManager;
import org.peenyaindustries.piaconnect.json.FetchData;
import org.peenyaindustries.piaconnect.json.ParseData;
import org.peenyaindustries.piaconnect.models.Category;
import org.peenyaindustries.piaconnect.models.Post;
import org.peenyaindustries.piaconnect.network.VolleySingleton;
import org.peenyaindustries.piaconnect.service.HttpService;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import static android.Manifest.permission.READ_SMS;
import static android.Manifest.permission.RECEIVE_SMS;

public class RegisterActivity extends AppCompatActivity implements View.OnClickListener, View.OnKeyListener {

    private static String TAG = RegisterActivity.class.getSimpleName();

    private ViewPager viewPager;
    private ViewPagerAdapter adapter;
    private Button btnRequestSms, btnVerifyOtp;
    private EditText inputName, inputCompany, inputEmail, inputMobile, inputOtp;
    private TextInputLayout inputNameLayout, inputCompanyLayout, inputEmailLayout, inputMobileLayout, inputOtpLayout;

    private FetchData f;
    private ParseData p;
    private DatabaseHelper db;
    private List<Post> posts;
    private List<Category> cats;

    private ProgressBar progressBar;
    private PrefManager pref;
    private ImageButton btnEditMobile;
    private TextView txtEditMobile;
    private LinearLayout layoutEditMobile;
    private ScrollView layoutSms, layoutOtp;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        pref = new PrefManager(this);
        f = new FetchData(this);
        p = new ParseData(this);
        db = new DatabaseHelper(this);
        posts = new ArrayList<>();
        cats = new ArrayList<>();

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            createPermissions();
        }

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.returningUser);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                // Click action
                Intent intent = new Intent(RegisterActivity.this, ReturningUserActivity.class);
                finish();
                startActivity(intent);
            }
        });

        viewPager = (ViewPager) findViewById(R.id.viewPagerVertical);
        inputName = (EditText) findViewById(R.id.inputName);
        inputCompany = (EditText) findViewById(R.id.inputCompany);
        inputEmail = (EditText) findViewById(R.id.inputEmail);
        inputMobile = (EditText) findViewById(R.id.inputMobile);
        inputOtp = (EditText) findViewById(R.id.inputOtp);
        btnRequestSms = (Button) findViewById(R.id.btn_request_sms);
        btnVerifyOtp = (Button) findViewById(R.id.btn_verify_otp);
        progressBar = (ProgressBar) findViewById(R.id.progressBar);
        btnEditMobile = (ImageButton) findViewById(R.id.btn_edit_mobile);
        txtEditMobile = (TextView) findViewById(R.id.txt_edit_mobile);
        layoutEditMobile = (LinearLayout) findViewById(R.id.layout_edit_mobile);
        layoutSms = (ScrollView) findViewById(R.id.layoutSms);
        layoutOtp = (ScrollView) findViewById(R.id.layout_otp);

        inputNameLayout = (TextInputLayout) findViewById(R.id.input_layout_name);
        inputCompanyLayout = (TextInputLayout) findViewById(R.id.input_layout_company);
        inputEmailLayout = (TextInputLayout) findViewById(R.id.input_layout_email);
        inputMobileLayout = (TextInputLayout) findViewById(R.id.input_layout_mobile);
        inputOtpLayout = (TextInputLayout) findViewById(R.id.input_layout_otp);

        //keyboard management
        inputName.setOnKeyListener(this);
        inputCompany.setOnKeyListener(this);
        inputEmail.setOnKeyListener(this);
        inputMobile.setOnKeyListener(this);
        inputOtp.setOnKeyListener(this);
        layoutSms.setOnClickListener(this);
        layoutOtp.setOnClickListener(this);

        //setup error display
        inputName.addTextChangedListener(new MyTextWatcher(inputName));
        inputCompany.addTextChangedListener(new MyTextWatcher(inputCompany));
        inputEmail.addTextChangedListener(new MyTextWatcher(inputEmail));
        inputMobile.addTextChangedListener(new MyTextWatcher(inputMobile));
        inputOtp.addTextChangedListener(new MyTextWatcher(inputOtp));

        // view click listeners
        btnEditMobile.setOnClickListener(this);
        btnRequestSms.setOnClickListener(this);
        btnVerifyOtp.setOnClickListener(this);

        // hiding the edit mobile number
        layoutEditMobile.setVisibility(View.GONE);

        // Checking for user session
        // if user is already logged in, take him to main activity
        if (pref.isLoggedIn()) {
            Intent intent = new Intent(RegisterActivity.this, MainActivity.class);
            intent.setFlags(Intent.FLAG_ACTIVITY_NEW_TASK | Intent.FLAG_ACTIVITY_CLEAR_TOP);
            startActivity(intent);

            finish();
        }

        adapter = new ViewPagerAdapter();
        viewPager.setAdapter(adapter);
        viewPager.addOnPageChangeListener(new ViewPager.OnPageChangeListener() {
            @Override
            public void onPageScrolled(int position, float positionOffset, int positionOffsetPixels) {
            }

            @Override
            public void onPageSelected(int position) {
            }

            @Override
            public void onPageScrollStateChanged(int state) {

            }
        });


        /**
         * Checking if the device is waiting for sms
         * showing the user OTP screen
         */
        if (pref.isWaitingForSms()) {
            viewPager.setCurrentItem(1);
            layoutEditMobile.setVisibility(View.VISIBLE);
        }
    }

    public boolean isConnected() {
        ConnectionDetector connection = new ConnectionDetector(getApplicationContext());
        Boolean isInternetActive = connection.isConnectedToInternet();
        if (isInternetActive) {
            return true;
        } else {
            return false;
        }
    }
    public void createPermissions() {
        String permission = READ_SMS;
        String permission2 = RECEIVE_SMS;
        if (ContextCompat.checkSelfPermission(getApplicationContext(), permission) != PackageManager.PERMISSION_GRANTED || ContextCompat.checkSelfPermission(getApplicationContext(), permission2) != PackageManager.PERMISSION_GRANTED ) {
            ActivityCompat.requestPermissions(this,
                    new String[]{READ_SMS, RECEIVE_SMS}, 1);
        }

    }

    @Override
    public void onClick(View v) {
        InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
        switch (v.getId()) {
            case R.id.btn_request_sms:
                if (isConnected()) {
                    validateForm();
                } else {
                    Toast.makeText(this, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                }

                break;

            case R.id.btn_verify_otp:
                txtEditMobile.setText(pref.getMobileNumber());

                if (isConnected()) {
                    verifyOtp(pref.getMobileNumber());
                } else {
                    Toast.makeText(this, "Please Check your Internet Connection", Toast.LENGTH_SHORT).show();
                }
                break;

            case R.id.btn_edit_mobile:
                viewPager.setCurrentItem(0);
                layoutEditMobile.setVisibility(View.GONE);
                pref.setIsWaitingForSms(false);
                break;
            case R.id.layoutSms:
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                break;
            case R.id.layout_otp:
                imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
                break;
        }
    }

    /**
     * Validating user details form
     */
    private void validateForm() {
        String name = inputName.getText().toString().trim();
        String company = inputCompany.getText().toString().trim();
        String email = inputEmail.getText().toString().trim();
        String mobile = inputMobile.getText().toString().trim();

        // validating empty name and email
        if (!validateName()) {
            return;
        }

        if (!validateCompany()) {
            return;
        }

        if (!validateEmail()) {
            return;
        }

        // validating mobile number
        // it should be of 10 digits length
        if (validateMobile()) {

            // request for sms
            progressBar.setVisibility(View.VISIBLE);

            // saving the mobile number in shared preferences
            pref.setMobileNumber(mobile);

            // requesting for sms
            requestForSMS(name, company, email, mobile);

        }
    }

    /**
     * Method initiates the SMS request on the server
     *
     * @param name   user name
     * @param email  user email address
     * @param mobile user valid mobile number
     */
    private void requestForSMS(final String name, final String company, final String email, final String mobile) {
        StringRequest strReq = new StringRequest(Request.Method.POST,
                Config.URL_REQUEST_SMS, new Response.Listener<String>() {

            @Override
            public void onResponse(String response) {
                Log.d(TAG, response);

                try {
                    JSONObject responseObj = new JSONObject(response);

                    // Parsing json object response
                    // response will be a json object
                    boolean error = responseObj.getBoolean("error");
                    String message = responseObj.getString("message");

                    // checking for error, if not error SMS is initiated
                    // device should receive it shortly
                    if (!error) {
                        // boolean flag saying device is waiting for sms
                        pref.setIsWaitingForSms(true);

                        // moving the screen to next pager item i.e otp screen
                        viewPager.setCurrentItem(1);
                        txtEditMobile.setText(pref.getMobileNumber());
                        layoutEditMobile.setVisibility(View.VISIBLE);

                        Toast.makeText(getApplicationContext(), message, Toast.LENGTH_SHORT).show();

                    } else {
                        Toast.makeText(getApplicationContext(),
                                message,
                                Toast.LENGTH_LONG).show();
                    }

                    // hiding the progress bar
                    progressBar.setVisibility(View.GONE);

                } catch (JSONException e) {
                    e.printStackTrace();
                    progressBar.setVisibility(View.GONE);
                }

            }
        }, new Response.ErrorListener() {

            @Override
            public void onErrorResponse(VolleyError error) {
                progressBar.setVisibility(View.GONE);
            }
        }) {

            /**
             * Passing user parameters to our server
             * @return
             */
            @Override
            protected Map<String, String> getParams() {
                Map<String, String> params = new HashMap<String, String>();
                params.put("name", name);
                params.put("company", company);
                params.put("email", email);
                params.put("mobile", mobile);

                return params;
            }

        };

        // Adding request to request queue
        VolleySingleton.getInstance(this).addToRequestQueue(strReq);
    }

    /**
     * sending the OTP to server and activating the user
     */
    private void verifyOtp(final String mobile) {
        String otp = inputOtp.getText().toString().trim();

        if (validateOtp()) {
            Intent grapprIntent = new Intent(getApplicationContext(), HttpService.class);
            grapprIntent.putExtra("otp", otp);
            grapprIntent.putExtra("mobile", mobile);
            startService(grapprIntent);
        }
    }

    public void termsConditions(View view) {
        startActivity(new Intent(this, TermsConditionsActivity.class));
    }

    private class ViewPagerAdapter extends PagerAdapter {

        @Override
        public int getCount() {
            return 2;
        }

        @Override
        public boolean isViewFromObject(View view, Object object) {
            return view == ((View) object);
        }

        public Object instantiateItem(View collection, int position) {

            int resId = 0;
            switch (position) {
                case 0:
                    resId = R.id.layoutSms;
                    break;
                case 1:
                    resId = R.id.layout_otp;
                    break;
            }
            return findViewById(resId);
        }
    }

    private class MyTextWatcher implements TextWatcher {

        private View view;

        public MyTextWatcher(View view) {
            this.view = view;
        }

        @Override
        public void beforeTextChanged(CharSequence s, int start, int count, int after) {

        }

        @Override
        public void onTextChanged(CharSequence s, int start, int before, int count) {

        }

        @Override
        public void afterTextChanged(Editable s) {
            switch (view.getId()) {
                case R.id.inputName:
                    validateName();
                    break;
                case R.id.inputCompany:
                    validateCompany();
                    break;
                case R.id.inputEmail:
                    validateEmail();
                    break;
                case R.id.inputMobile:

                    validateMobile();
                    break;
                case R.id.inputOtp:
                    validateOtp();
                    break;
            }
        }
    }

    private void requestFocus(View view) {
        if (view.requestFocus()) {
            getWindow().setSoftInputMode(WindowManager.LayoutParams.SOFT_INPUT_STATE_ALWAYS_VISIBLE);
        }
    }

    private boolean validateName() {
        if (inputName.getText().toString().trim().isEmpty()) {
            inputNameLayout.setError("Name cannot be empty");
            requestFocus(inputName);
            return false;
        } else {
            inputNameLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateCompany() {
        if (inputCompany.getText().toString().trim().isEmpty()) {
            inputCompanyLayout.setError("Company Title cannot be empty.");
            requestFocus(inputCompany);
            return false;
        } else {
            inputCompanyLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateEmail() {
        String email = inputEmail.getText().toString().trim();

        if (email.isEmpty() || !isValidEmail(email)) {
            inputEmailLayout.setError("Enter valid email address. example@domain.com");
            requestFocus(inputEmail);
            return false;
        } else {
            inputEmailLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateMobile() {
        String mobile = inputMobile.getText().toString().trim();

        if (mobile.isEmpty() || !isValidPhoneNumber(mobile)) {
            inputMobileLayout.setError("Enter valid 10 digit mobile number");
            requestFocus(inputMobile);
            return false;
        } else {
            inputMobileLayout.setErrorEnabled(false);
        }

        return true;
    }

    private boolean validateOtp() {
        if (inputOtp.getText().toString().trim().isEmpty()) {
            inputOtpLayout.setError("This Field is required");
            requestFocus(inputOtp);
            return false;
        } else {
            inputOtpLayout.setErrorEnabled(false);
        }

        return true;
    }

    /**
     * Email Validation
     *
     * @param email
     * @return
     */
    private static boolean isValidEmail(String email) {
        return !TextUtils.isEmpty(email) && android.util.Patterns.EMAIL_ADDRESS.matcher(email).matches();
    }

    /**
     * Regex to validate the mobile number
     * mobile number should be of 10 digits length
     *
     * @param mobile
     * @return
     */
    private static boolean isValidPhoneNumber(String mobile) {
        String regEx = "^[0-9]{10}$";
        return mobile.matches(regEx);
    }

    @Override
    public boolean onKey(View v, int keyCode, KeyEvent event) {

        if (keyCode == KeyEvent.KEYCODE_ENTER && event.getAction() == KeyEvent.ACTION_DOWN) {
            InputMethodManager imm = (InputMethodManager) getSystemService(INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(getCurrentFocus().getWindowToken(), 0);
            if (pref.isWaitingForSms()) {
                verifyOtp(pref.getMobileNumber());
            } else {
                validateForm();
            }
        }
        return false;
    }

}
