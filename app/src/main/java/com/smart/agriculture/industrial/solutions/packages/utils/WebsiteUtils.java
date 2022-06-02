package com.smart.agriculture.industrial.solutions.packages.utils;

public class WebsiteUtils {
    private WebsiteUtils() {
    }

    /*********************************BASE URL********************************************/
    public static final String BASE_URL = "http://smartagriculturesolutions.com:9999/api/";
//    public static final String BASE_URL = "http://192.168.10.150:9999/api/";

    /*********************************Website Constant***********************************/
    static final String DEVICES = "devices?";
    static final String FROM = "&from=";
    public static final String EMAIL = "email";
    public static final String AUTH = "password";
    static final String USER_ID = "userid";
    static final String USER = "devices/";

    static final String EVENTS = "reports/events?";
    static final String DEVICE_ID = "deviceId=";
    static final String TO = "&to=";
    static final String SESSION = "session?";
    static final String POSITION = "positions?";
    static final String GEO_FENCES = "geofences";
    static final String POSITION_ID = "id";
    static final String COMMANDS_SEND_ID = "commands/send?";
    static final String COMMANDS_SEND = "commands/send";


}
