package com.smart.agriculture.industrial.solutions.packages.utils;

public class URLS {
    private URLS() {
    }

    public static String devices() {
        return WebsiteUtils.BASE_URL + WebsiteUtils.DEVICES + WebsiteUtils.USER_ID + "=" + WebsiteUtils.FROM;
    }

    public static String events(int deviceId, String startDate, String endDate) {
        return WebsiteUtils.BASE_URL + WebsiteUtils.EVENTS + WebsiteUtils.DEVICE_ID + deviceId + WebsiteUtils.FROM + startDate + WebsiteUtils.TO + endDate;
    }

    public static String delete() {
        return WebsiteUtils.BASE_URL + WebsiteUtils.SESSION;
    }

    public static String positions() {
        return WebsiteUtils.BASE_URL + WebsiteUtils.POSITION;
    }

    public static String position(long id) {
        return WebsiteUtils.BASE_URL + WebsiteUtils.POSITION + WebsiteUtils.POSITION_ID + "=" + id;
    }

    public static String geoFence() {
        return WebsiteUtils.BASE_URL + WebsiteUtils.GEO_FENCES;
    }

    public static String positionByDeviceId(String id) {
        return WebsiteUtils.BASE_URL + WebsiteUtils.POSITION + "deviceId=" + id;
    }

    public static String Command(long deviceId) {
        return WebsiteUtils.BASE_URL + WebsiteUtils.COMMANDS_SEND_ID + WebsiteUtils.DEVICE_ID + deviceId;
    }

    public static String Command() {
        return WebsiteUtils.BASE_URL + WebsiteUtils.COMMANDS_SEND;
    }


    public static String user(long id) {
        return WebsiteUtils.BASE_URL + WebsiteUtils.USER + id;
    }
}
