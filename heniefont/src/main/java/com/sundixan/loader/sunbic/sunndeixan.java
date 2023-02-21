package com.sundixan.loader.sunbic;

import android.app.Activity;
import android.widget.Toast;

import com.android.volley.DefaultRetryPolicy;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.StringRequest;
import com.android.volley.toolbox.Volley;
import com.sundixan.loader.gooesason;
import com.sundixan.loader.Connectivity;
import com.sundxin.jesdenias.R;

import org.json.JSONObject;

public class sunndeixan {

    public static Activity activity;
    public static sunndeixan getLoadAsds;
    public static String mode = "";

    public static String app_VPN_Base_Carrier;
    public static String app_VPN_Access_Token;
    public static String app_VPN_Auth;


    public sunndeixan(Activity activity1) {
        activity = activity1;
    }

    public static sunndeixan getInstance(Activity activity1) {
        activity = activity1;
        if (getLoadAsds == null) {
            getLoadAsds = new sunndeixan(activity);
        }
        return getLoadAsds;
    }

    public void requestvpn(Activity activity, String Sword) {
        if (Connectivity.isConnected(activity)) {
            try {
                mode = gooesason.Logd(activity.getString(R.string.fl67dgdghtjd8545) + Sword);
            } catch (Exception e) {
                e.printStackTrace();
            }
            RequestQueue queue = Volley.newRequestQueue(activity);
            StringRequest stringRequest = new StringRequest(Request.Method.GET, mode, new Response.Listener<String>() {
                @Override
                public void onResponse(String response) {
                    try {
                        JSONObject jsonObject = new JSONObject(response);
                        if (jsonObject.getInt("success") == 1) {

                            app_VPN_Base_Carrier = jsonObject.getString("app_VPN_Base_Carrier");
                            app_VPN_Access_Token = jsonObject.getString("app_VPN_Access_Token");
                            app_VPN_Auth = jsonObject.getString("app_VPN_Auth");

                            lovtine.getInstance(activity).Initiate_call(activity, app_VPN_Access_Token, app_VPN_Auth, app_VPN_Base_Carrier);

                        } else {
                            Toast.makeText(activity, "Not Found Data!!!", Toast.LENGTH_LONG).show();
                        }

                    } catch (Exception e) {
                        Toast.makeText(activity, "Something went wrong!!!", Toast.LENGTH_LONG).show();
                    }
                }
            }, new Response.ErrorListener() {
                @Override
                public void onErrorResponse(VolleyError volleyError) {
                    Toast.makeText(activity, "Error: Something went wrong!!!", Toast.LENGTH_LONG).show();

                }
            });
            stringRequest.setRetryPolicy(new DefaultRetryPolicy(5000,
                    DefaultRetryPolicy.DEFAULT_MAX_RETRIES,
                    DefaultRetryPolicy.DEFAULT_BACKOFF_MULT));
            queue.add(stringRequest);
        } else {
            Toast.makeText(activity, "Please Check Your Internet Connection!!!", Toast.LENGTH_LONG).show();
        }
    }
}
