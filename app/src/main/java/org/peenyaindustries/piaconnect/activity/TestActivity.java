package org.peenyaindustries.piaconnect.activity;

import android.app.ProgressDialog;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;
import org.peenyaindustries.piaconnect.R;
import org.peenyaindustries.piaconnect.app.Config;
import org.peenyaindustries.piaconnect.network.VolleySingleton;

public class TestActivity extends AppCompatActivity {

    private static final String TAG = TestActivity.class.getSimpleName();
    private ProgressDialog pDialog;
    private TextView text;
    private String info;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_test);

        pDialog = new ProgressDialog(this);
        pDialog.setMessage("Please wait...");
        pDialog.setCancelable(false);

        text = (TextView) findViewById(R.id.text);
    }

    private void showpDialog() {
        if (!pDialog.isShowing())
            pDialog.show();
    }

    private void hidepDialog() {
        if (pDialog.isShowing())
            pDialog.dismiss();
    }

    public void allPosts(View view) {
        fetchData(new DataCallback() {
            @Override
            public void onSuccess(String response) {
                showpDialog();
                try {
                    JSONArray jsonArray = new JSONArray(response);
                    int count = 0;
                    for (int i = 0; i < jsonArray.length(); i++) {
                        count++;
                        JSONObject jsonObject = jsonArray.getJSONObject(i);

                        String post_id = jsonObject.getString("id");
                        //JSONArray category = jsonObject.getJSONArray("categories");
                        //String categories = category.toString();
                        //String one = categories.replace("[", "");
                        //categories = one.replace("]", "");
                        //String[] post_cat = categories.split(Pattern.quote(","));

                        //for (int j = 0; post_cat.length > 0; j++) {
                        String url = Config.URL_POST_CATEGORIES + post_id;
                        final int finalI = i;
                        getPostsByCategories(new DataCallback() {
                            @Override
                            public void onSuccess(String response) {

                                try {
                        JSONArray array = new JSONArray(response);
                        for (int i = 0; array.length() > 0; i++) {
                            JSONObject obj = array.getJSONObject(i);
                            text.append(obj.getString("id") + "\n");
                            Log.e(TAG, " - " + obj.getString("id") + " - " + i);

                        }
                                } catch (Exception e) {
                                    e.printStackTrace();
                                }
                            }
                        }, url);

                        //}


                    }
                } catch (JSONException e) {
                    e.printStackTrace();
                }
                hidepDialog();
            }
        });
    }

    public void fetchData(final DataCallback callback) {

        StringRequest request = new StringRequest(Request.Method.GET, Config.URL_CATEGORIES, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                try {
                    callback.onSuccess(response);
//                    JSONArray jsonArray = new JSONArray(response);
//                    int count = 0;
//                    for (int i = 0; i < jsonArray.length(); i++) {
//                        count++;
//                        JSONObject jsonObject = jsonArray.getJSONObject(i);
//
//                        String post_id = jsonObject.getString("id");
//                        //JSONArray category = jsonObject.getJSONArray("categories");
//                        //String categories = category.toString();
//                        //String one = categories.replace("[", "");
//                        //categories = one.replace("]", "");
//                        //String[] post_cat = categories.split(Pattern.quote(","));
//
//                        //for (int j = 0; post_cat.length > 0; j++) {
//                            String url = Config.URL_POST_CATEGORIES + post_id;
//                            ArrayList<String> array = getPostsByCategories(url, String.valueOf(count));
//
//                        //}
//
//
//                    }
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage() + "");
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(request);
    }

    private void getPostsByCategories(final DataCallback callback, String url) {

        StringRequest postReq = new StringRequest(Request.Method.GET, url, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {
                    try {
//                        JSONArray array = new JSONArray(response);
//                        for (int i = 0; array.length() > 0; i++) {
//                            JSONObject obj = array.getJSONObject(i);
//
//                            Log.e(TAG, " - " + obj.getString("id"));
                            callback.onSuccess(response);
//                        }
                    } catch (Exception e) {
                        e.printStackTrace();
                    }
                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(TAG, error.getMessage() + "");
            }
        });
        VolleySingleton.getInstance(this).addToRequestQueue(postReq);

    }

    public interface DataCallback {
        void onSuccess(String response);
    }
}
