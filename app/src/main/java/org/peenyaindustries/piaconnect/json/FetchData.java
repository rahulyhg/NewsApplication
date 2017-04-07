package org.peenyaindustries.piaconnect.json;

import android.content.Context;
import android.util.Log;

import com.android.volley.AuthFailureError;
import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;

import org.peenyaindustries.piaconnect.callback.VolleyResponseCallback;
import org.peenyaindustries.piaconnect.network.VolleySingleton;

import java.util.HashMap;
import java.util.Map;

public class FetchData {

    private Context context;

    public FetchData(Context context) {
        this.context = context;
    }

    public void fetchData(final VolleyResponseCallback callback, String URL) {

        StringRequest strReq = new StringRequest(Request.Method.GET, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                if (response != null && response.length() > 0) {

                    callback.onSuccess(response);

                }
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(context.getClass().getSimpleName(), "onFetchErrorResponse : " + error.getMessage());
            }
        });
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(strReq);
    }

    public void postData(final VolleyResponseCallback callback, String URL, final String[] param) {

        StringRequest strReq = new StringRequest(Request.Method.POST, URL, new Response.Listener<String>() {
            @Override
            public void onResponse(String response) {
                callback.onSuccess(response);
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
                Log.e(context.getClass().getSimpleName(), "onPostErrorResponse : " + error.getMessage());
            }
        }) {
            @Override
            protected Map<String, String> getParams() throws AuthFailureError {
                HashMap<String, String> params = new HashMap<>();
                params.put("token", param[0]);
                params.put("mobile", param[1]);
                return params;
            }
        };
        strReq.setRetryPolicy(new DefaultRetryPolicy(5000,
                DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
        VolleySingleton.getInstance(context).addToRequestQueue(strReq);
    }
}
