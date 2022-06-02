package com.smart.agriculture.industrial.solutions.packages.utils;

public final class Constant {
    private Constant() {
    }

    /********************Shared Preferences************************/
    public static final String SP_LOGIN_FILE = "shared_login";
    public static final String SP_LOGIN_CHECK_VAR = "shared_login_bool";
    public static final boolean SP_LOGIN_LOGIN_CHECK_VAR = true;
    public static final boolean SP_LOGIN_LOGOUT_CHECK_VAR = false;
    public static final String SP_LOGIN_EMAIL = "username_login";
    public static final String SP_LOGIN_AUTH = "password_login";

    /******************RetryBox***********************************/
    public static final int WRONG_CREDENTIALS_RESPONSE_CODE = 401;
    public static final int     LATE_RESPONSE_CODE = 411;
    public static final int NO_INTERNET_CODE = 500;
    public static final int REQUEST_NO_EVENT = 800;

    /********************FRAGMENTS*******************************/
    public static final String LOGIN_FRAGMENT = "login_fragment";
    public static final String VEHICLE_FRAGMENT = "vehicle_fragment";
    public static final String FROM_GEO_FENCE_FRAGMENT = "current_geo_fence_fragment";
    public static final String TO_GEO_FENCE_FRAGMENT = "final_geo_fence_fragment";
    public static final String ADMIN_FRAGMENT = "admin_fragment";
    public static final String VEHICLE_LIST_FRAGMENT = "vehicle_list_fragment";
    public static final String VEHICLE_LOG_FRAGMENT = "vehicle_log_fragment";



    /********************Parceable GeoFence Model*****************/
    public static final String GEO_FENCE_MODEL = "geo_fence_model";
    public static final String GEO_FENCE_MODEL_FROM = "geo_fence_model_from";
    public static final String GEO_FENCE_MODEL_TO = "geo_fence_model_to";
    public static final String DEVICE_MODEL = "device_model";
    public static final int SUCCESS_RESPONSE_CODE = 200;



}
