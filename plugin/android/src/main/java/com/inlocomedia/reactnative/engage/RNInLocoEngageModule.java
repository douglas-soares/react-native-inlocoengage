package com.inlocomedia.reactnative.engage;

import android.Manifest;
import android.content.Context;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.widget.Toast;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.inlocomedia.android.core.permissions.PermissionResult;
import com.inlocomedia.android.core.permissions.PermissionsListener;
import com.inlocomedia.android.engagement.InLocoEngagement;
import com.inlocomedia.android.engagement.InLocoEngagementOptions;
import com.inlocomedia.android.engagement.PushMessage;
import com.inlocomedia.android.engagement.request.PushProvider;
import com.inlocomedia.android.engagement.user.EngageUser;

import java.util.HashMap;
import java.util.Map;

public class RNInLocoEngageModule extends ReactContextBaseJavaModule {

    private final static String REACT_CLASS = "RNInLocoEngage";

    private final ReactApplicationContext reactContext;

    public RNInLocoEngageModule(ReactApplicationContext reactContext) {
        super(reactContext);
        this.reactContext = reactContext;
    }

    @ReactMethod
    public void init(final String appId, final boolean logsEnabled) {

        InLocoEngagementOptions options = InLocoEngagementOptions.getInstance(reactContext);

        options.setApplicationId(appId);
        options.setLogEnabled(logsEnabled);

        InLocoEngagement.init(reactContext, options);
    }

    @ReactMethod
    public void setUser(final String userId) {
        EngageUser user = new EngageUser(userId);
        InLocoEngagement.setUser(reactContext, user);
    }

    @ReactMethod
    public void clearUser() {
        InLocoEngagement.clearUser(reactContext);
    }

    @ReactMethod
    public void setPushProvider(final String name, final String token) {
        PushProvider pushProvider = new PushProvider.Builder()
                .setName(name)
                .setToken(token)
                .build();

        InLocoEngagement.setPushProvider(reactContext, pushProvider);
    }

    @ReactMethod
    public void setPushNotificationsEnabled(final boolean enabled) {
        InLocoEngagement.setPushNotificationsEnabled(reactContext, enabled);
    }

    @ReactMethod
    public void requestLocationPermission() {
        String[] permissions = {Manifest.permission.ACCESS_FINE_LOCATION};
        InLocoEngagement.requestPermissions(reactContext.getCurrentActivity(), permissions, false, null);
    }

    @ReactMethod
    public void presentNotification(String dataJsonString, String channelId, int notificationId) {
        int smallIconResId = reactContext.getResources().getIdentifier("ic_notification", "mipmap", reactContext.getPackageName());
        if (smallIconResId == 0) {
            smallIconResId = reactContext.getResources().getIdentifier("ic_launcher", "mipmap", reactContext.getPackageName());
            if (smallIconResId == 0) {
                smallIconResId = android.R.drawable.ic_dialog_info;
            }
        }

        final PushMessage pushContent = InLocoEngagement.decodeReceivedMessage(reactContext, dataJsonString);

        if (pushContent != null) {
            InLocoEngagement.presentNotification(
                    reactContext,
                    pushContent,
                    smallIconResId,
                    notificationId,
                    channelId
            );
        }
    }

    @ReactMethod
    public void trackEvent(final String eventName, ReadableMap properties) {
        Map<String, String> propertiesMap = convertToStringStringMap(properties.toHashMap());
        InLocoEngagement.trackEvent(reactContext, eventName, propertiesMap);
    }

    private static Map<String, String> convertToStringStringMap(Map<String,Object> map) {
        Map<String,String> newMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if(entry.getValue() instanceof String){
                newMap.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return newMap;
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }
}