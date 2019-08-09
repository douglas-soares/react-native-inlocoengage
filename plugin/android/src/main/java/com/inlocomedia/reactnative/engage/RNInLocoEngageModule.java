package com.inlocomedia.reactnative.engage;

import android.Manifest;
import android.location.Address;
import android.util.Log;

import com.facebook.react.bridge.ReactApplicationContext;
import com.facebook.react.bridge.ReactContextBaseJavaModule;
import com.facebook.react.bridge.ReactMethod;
import com.facebook.react.bridge.ReadableMap;
import com.inlocomedia.android.engagement.InLocoEngagement;
import com.inlocomedia.android.engagement.InLocoEngagementOptions;
import com.inlocomedia.android.engagement.PushMessage;
import com.inlocomedia.android.engagement.request.PushProvider;
import com.inlocomedia.android.engagement.user.EngageUser;

import java.util.HashMap;
import java.util.Locale;
import java.util.Map;

public class RNInLocoEngageModule extends ReactContextBaseJavaModule {

    private final static String REACT_CLASS = "RNInLocoEngage";

    private static String ADDRESS_LOCALE_KEY = "locale";
    private static String ADDRESS_COUNTRY_NAME_KEY = "countryName";
    private static String ADDRESS_COUNTRY_CODE_KEY = "countryCode";
    private static String ADDRESS_ADMIN_AREA_KEY = "adminArea";
    private static String ADDRESS_SUBADMIN_AREA_KEY = "subAdminArea";
    private static String ADDRESS_LOCALITY_KEY = "locality";
    private static String ADDRESS_SUB_LOCALITY_KEY = "subLocality";
    private static String ADDRESS_THOROUGHFARE_KEY = "thoroughfare";
    private static String ADDRESS_SUB_THOROUGHFARE_KEY = "subThoroughfare";
    private static String ADDRESS_POSTAL_CODE_KEY = "postalCode";
    private static String ADDRESS_LATITUDE_KEY = "latitude";
    private static String ADDRESS_LONGITUDE_KEY = "longitude";
    private static String ADDRESS_LINE = "address_line";

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

    @ReactMethod
    public void setUserAddress(ReadableMap addressMap) {
        Address address = convertMapToAddress(addressMap);
        InLocoEngagement.setUserAddress(reactContext, address);
    }

    @ReactMethod
    public void clearUserAddress() {
        InLocoEngagement.clearUserAddress(reactContext);
    }

    private static Address convertMapToAddress(ReadableMap map) {
        Locale locale = localeFromString(map.getString(ADDRESS_LOCALE_KEY));
        Address address = new Address(locale != null ? locale : new Locale("en", "US"));

        if (map.hasKey(ADDRESS_COUNTRY_NAME_KEY)) {
            address.setCountryName(map.getString(ADDRESS_COUNTRY_NAME_KEY));
        }
        if (map.hasKey(ADDRESS_COUNTRY_CODE_KEY)) {
            address.setCountryCode(map.getString(ADDRESS_COUNTRY_CODE_KEY));
        }
        if (map.hasKey(ADDRESS_ADMIN_AREA_KEY)) {
            address.setAdminArea(map.getString(ADDRESS_ADMIN_AREA_KEY));
        }
        if (map.hasKey(ADDRESS_SUBADMIN_AREA_KEY)) {
            address.setSubAdminArea(map.getString(ADDRESS_SUBADMIN_AREA_KEY));
        }
        if (map.hasKey(ADDRESS_LOCALITY_KEY)) {
            address.setLocality(map.getString(ADDRESS_LOCALITY_KEY));
        }
        if (map.hasKey(ADDRESS_SUB_LOCALITY_KEY)) {
            address.setSubLocality(map.getString(ADDRESS_SUB_LOCALITY_KEY));
        }
        if (map.hasKey(ADDRESS_THOROUGHFARE_KEY)) {
            address.setThoroughfare(map.getString(ADDRESS_THOROUGHFARE_KEY));
        }
        if (map.hasKey(ADDRESS_SUB_THOROUGHFARE_KEY)) {
            address.setSubThoroughfare(map.getString(ADDRESS_SUB_THOROUGHFARE_KEY));
        }
        if (map.hasKey(ADDRESS_POSTAL_CODE_KEY)) {
            address.setPostalCode(map.getString(ADDRESS_POSTAL_CODE_KEY));
        }
        if (map.hasKey(ADDRESS_LINE)) {
            address.setAddressLine(0, map.getString(ADDRESS_LINE));
        }
        if (map.hasKey(ADDRESS_LATITUDE_KEY)) {
            address.setLatitude(map.getDouble(ADDRESS_LATITUDE_KEY));
        }
        if (map.hasKey(ADDRESS_LONGITUDE_KEY)) {
            address.setLongitude(map.getDouble(ADDRESS_LONGITUDE_KEY));
        }

        return address;
    }

    private static Map<String, String> convertToStringStringMap(Map<String, Object> map) {
        Map<String, String> newMap = new HashMap<>();
        for (Map.Entry<String, Object> entry : map.entrySet()) {
            if (entry.getValue() instanceof String) {
                newMap.put(entry.getKey(), (String) entry.getValue());
            }
        }
        return newMap;
    }

    private static Locale localeFromString(String locale) {
        try {
            String[] parts = locale.split("-", -1);
            if (parts.length == 1) return new Locale(parts[0]);
            else if (parts.length == 2
                     || (parts.length == 3 && parts[2].startsWith("#")))
                return new Locale(parts[0], parts[1]);
            else return new Locale(parts[0], parts[1], parts[2]);
        } catch (Throwable t) {
            return null;
        }
    }

    @Override
    public String getName() {
        return REACT_CLASS;
    }
}