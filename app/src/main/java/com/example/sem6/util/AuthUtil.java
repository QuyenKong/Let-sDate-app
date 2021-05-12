package com.example.sem6.util;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;

import com.auth0.android.jwt.JWT;
import com.example.sem6.R;
import com.example.sem6.activities.LoginActivity;
import com.example.sem6.adapters.UserAdapter;
import com.example.sem6.dto.PagedResponse;
import com.example.sem6.dto.PagedResponse;
import com.example.sem6.dto.RestResponse;
import com.example.sem6.models.User;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

import java.io.IOException;
import java.util.List;

public class AuthUtil {
    public static boolean isAuthenticated(Context context) {
        return getToken(context) != null;
    }

    public static void bindAuthGuard(Context context) {
        if (isAuthenticated(context))
            return;
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    public static void setToken(Context context, String token) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_auth), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.putString("token", token);
        editor.apply();
    }

    public static String getToken(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_auth), Context.MODE_PRIVATE);
        return sharedPref.getString("token", null);
    }

    private static String getTokenClaim(Context context, String key) {
        JWT jwt = new JWT(getToken(context));
        return jwt.getClaim(key).asString();
    }

    private static void clearToken(Context context) {
        SharedPreferences sharedPref = context.getSharedPreferences(
                context.getString(R.string.preference_auth), Context.MODE_PRIVATE);
        SharedPreferences.Editor editor = sharedPref.edit();
        editor.clear();
        editor.apply();
    }

    public static void logout(Context context) {
        clearToken(context);
        context.startActivity(new Intent(context, LoginActivity.class));
    }

    public static void getAuthUser(Context context, GetAuthUserListener listener) {
        try {
            String id = getTokenClaim(context, "sub");
            HttpClient.getInstance(context).get(
                    "/auth/me",
                    null,
                    response -> {
                        try {
                            RestResponse<User> res = new ObjectMapper()
                                    .readValue(
                                            response.toString(),
                                            new TypeReference<RestResponse<User>>() {});
                            User user = res.getData();
                            listener.handle(user);
                        } catch (IOException e) {
                            e.printStackTrace();
                        }
                    },
                    null);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    public static boolean isVisitor(Context context) {
        String role = getTokenClaim(context, "role");
        return role.equals("VISITOR");
    }

    public static String getRole(Context context) {
        return getTokenClaim(context, "role");
    }

    public static long getUID(Context context) {
        return Long.valueOf(getTokenClaim(context, "sub"));
    }

    public interface GetAuthUserListener {
        void handle(User user);
    }
}
