package com.example.nikmul19.medicine;

import android.content.Context;
import android.util.Log;
import android.widget.Toast;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.Volley;

import org.json.JSONArray;

import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Map;

public class VolleyResponseclass {
    RequestQueue requestQueue;
    String url = "http://www.healthos.co/api/v1/autocomplete/medicines/brands/CROCIN";
    Context ctx;


    public VolleyResponseclass(Context ctx) {
        this.ctx = ctx;
    }

    public void getData() {
        requestQueue = Volley.newRequestQueue(ctx);
        Map<String, String> prams = new HashMap<>();
        prams.put("Authorization", "Bearer 5f3d47539d55831124d8632e846248344b671b3ea650b1e39a93d591f6b0f7fb");
        JsonArrayRequest jsonArrayRequest = new JsonArrayRequest(Request.Method.GET, url, null, new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                Log.e("Response ", response + "");
              //  Toast.makeText(ctx,response+"",Toast.LENGTH_SHORT).show();
            }
        }, new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {
               // Toast.makeText(ctx,error+"error",Toast.LENGTH_SHORT).show();
                Log.e("Error ", error + "");
            }
        }) {
            /** Passing some request headers* */
            @Override
            public Map getHeaders() throws AuthFailureError {
                HashMap headers = new HashMap();
                headers.put("Content-Type", "application/json");
                headers.put("Authorization", "Bearer 5f3d47539d55831124d8632e846248344b671b3ea650b1e39a93d591f6b0f7fb");
                return headers;
            }

        };
        requestQueue.add(jsonArrayRequest);
    }
}
