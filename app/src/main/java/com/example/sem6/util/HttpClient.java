package com.example.sem6.util;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.LruCache;

import androidx.annotation.Nullable;

import com.android.volley.AuthFailureError;
import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.toolbox.ImageLoader;
import com.android.volley.toolbox.JsonObjectRequest;
import com.android.volley.toolbox.Volley;
import com.example.sem6.R;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import org.json.JSONObject;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class HttpClient {
    private static HttpClient instance;
    private RequestQueue requestQueue;
    private ImageLoader imageLoader;
    private static Context ctx;

    private HttpClient(Context context) {
        ctx = context;
        requestQueue = getRequestQueue();

        imageLoader = new ImageLoader(requestQueue,
                new ImageLoader.ImageCache() {
                    private final LruCache<String, Bitmap>
                            cache = new LruCache<String, Bitmap>(20);

                    @Override
                    public Bitmap getBitmap(String url) {
                        return cache.get(url);
                    }

                    @Override
                    public void putBitmap(String url, Bitmap bitmap) {
                        cache.put(url, bitmap);
                    }
                });
    }

    public static synchronized HttpClient getInstance(Context context) {
        if (instance == null) {
            instance = new HttpClient(context);
        }
        return instance;
    }

    public RequestQueue getRequestQueue() {
        if (requestQueue == null) {
            // getApplicationContext() is key, it keeps you from leaking the
            // Activity or BroadcastReceiver if someone passes one in.
            requestQueue = Volley.newRequestQueue(ctx.getApplicationContext());
        }
        return requestQueue;
    }

    public <T> void addToRequestQueue(Request<T> req) {
        getRequestQueue().add(req);
    }

    public ImageLoader getImageLoader() {
        return imageLoader;
    }

    public void get(
            String uri,
            Map<String, String> params,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.GET,
                ctx.getString(R.string.base_url) + uri,
                null,
                listener,
                errorListener) {
            @Nullable
            @Override
            protected Map<String, String> getParams() {
                return params;
            }

            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>(super.getHeaders());
                if (AuthUtil.isAuthenticated(ctx)) {
                    headers.put("Authorization", "Bearer " + AuthUtil.getToken(ctx));
                }
                return headers;
            }
        };
        instance.addToRequestQueue(request);
    }

    public void post(
            String uri,
            JSONObject jsonRequest,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.POST,
                ctx.getString(R.string.base_url) + uri,
                jsonRequest,
                listener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>(super.getHeaders());
                if (AuthUtil.isAuthenticated(ctx)) {
                    headers.put("Authorization", "Bearer " + AuthUtil.getToken(ctx));
                }
                return headers;
            }
        };
        instance.addToRequestQueue(request);
    }

    public void put(
            String uri,
            JSONObject jsonRequest,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.PUT,
                ctx.getString(R.string.base_url) + uri,
                jsonRequest,
                listener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>(super.getHeaders());
                if (AuthUtil.isAuthenticated(ctx)) {
                    headers.put("Authorization", "Bearer " + AuthUtil.getToken(ctx));
                }
                return headers;
            }
        };
        instance.addToRequestQueue(request);
    }

    public void delete(
            String uri,
            Response.Listener<JSONObject> listener,
            Response.ErrorListener errorListener) {
        JsonObjectRequest request = new JsonObjectRequest(
                Request.Method.DELETE,
                ctx.getString(R.string.base_url) + uri,
                null,
                listener,
                errorListener) {
            @Override
            public Map<String, String> getHeaders() throws AuthFailureError {
                Map<String, String> headers = new HashMap<>(super.getHeaders());
                if (AuthUtil.isAuthenticated(ctx)) {
                    headers.put("Authorization", "Bearer " + AuthUtil.getToken(ctx));
                }
                return headers;
            }
        };
        instance.addToRequestQueue(request);
    }
}
