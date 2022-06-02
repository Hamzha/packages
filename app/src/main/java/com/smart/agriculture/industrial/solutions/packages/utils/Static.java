package com.smart.agriculture.industrial.solutions.packages.utils;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.util.Log;
import android.util.Pair;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.LatLngBounds;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.PolygonOptions;
import com.google.maps.android.ui.IconGenerator;
import com.smart.agriculture.industrial.solutions.packages.R;
import com.smart.agriculture.industrial.solutions.packages.models.Circle;
import com.smart.agriculture.industrial.solutions.packages.models.GeoFence;
import com.smart.agriculture.industrial.solutions.packages.models.Polygon;
import com.smart.agriculture.industrial.solutions.packages.utils.interfaces.RetryDialogBox;

import java.util.ArrayList;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import cn.pedant.SweetAlert.SweetAlertDialog;


public final class Static {

    private Static() {
    }

    public static void showToast(String msg, Context context) {
        Toast.makeText(context, "Error Occured, Please try Later", Toast.LENGTH_LONG).show();

    }

    public static void retry(Context context, int statusCode, RetryDialogBox retryDialogBox) {
        String msg = context.getString(R.string.common_Error);
        if (statusCode == Constant.LATE_RESPONSE_CODE) {
            msg = context.getString(R.string.common_Error);
        } else if (statusCode == Constant.NO_INTERNET_CODE) {
            msg = context.getString(R.string.no_internet_response);
        } else if (statusCode == Constant.WRONG_CREDENTIALS_RESPONSE_CODE) {
            msg = context.getString(R.string.invalid_credentials_response);
        } else {
            msg = context.getString(R.string.common_Error);
        }
        DialogInterface.OnClickListener dialogClickListener = (dialog, which) -> {
            if (which == DialogInterface.BUTTON_POSITIVE) {
                //Yes button clicked
                retryDialogBox.callBack(true);
            }
            if (which == DialogInterface.BUTTON_NEGATIVE) {
                //No button clicked
                retryDialogBox.callBack(false);

            }
        };
        final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
        sDialog.setTitle("ERROR");
        sDialog.setContentText(msg);
        sDialog.show();
//        AlertDialog.Builder builder1 = new AlertDialog.Builder(context);
//        builder1.setMessage(msg)/*.setPositiveButton("Yes", dialogClickListener)
//                .setNegativeButton("No", dialogClickListener)*/.show();
    }

    public static void logD(String tag, String msg) {
        Log.d(tag, msg);
    }

    public static ArrayList<LatLng> addGeoFences(Context context, List<GeoFence> geofences) {

        ArrayList<LatLng> latLngArrayList = new ArrayList<>();
        for (int k = 0; k < geofences.size(); k++) {
            GeoFence geoFence = geofences.get(k);
            Pair<Integer, Object> pair = decodeArea(geoFence.getArea(), context);

            final TextView textView = new TextView(context);
            textView.setText(geoFence.getName());
            textView.setTextSize(20);
            final Paint paintText = textView.getPaint();
            final Rect boundsText = new Rect();
            paintText.getTextBounds(geoFence.getName(), 0, textView.length(), boundsText);
            paintText.setTextAlign(Paint.Align.CENTER);
            final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
                    * 20, boundsText.height() + 20 * 5, conf);

            final Canvas canvasText = new Canvas(bmpText);
            paintText.setColor(Color.BLACK);
            @SuppressWarnings("IntegerDivisionInFloatingPointContext") float halfCanvas = (canvasText.getWidth() / 2);
            canvasText.drawText(geoFence.getName(), halfCanvas,
                    canvasText.getHeight() - 20f - boundsText.bottom, paintText);
            LatLngBounds.Builder latLngBounds;

            assert pair != null;
            switch (pair.first) {
                case 1:
                    latLngBounds = new LatLngBounds.Builder();
                    Polygon polygon = (Polygon) pair.second;
                    for (int i = 0; i < polygon.getLatLngs().size(); i++)
                        latLngBounds.include(new LatLng(polygon.getLatLngs().get(i).latitude, polygon.getLatLngs().get(i).longitude));
                    latLngArrayList.add(latLngBounds.build().getCenter());
                    break;
                case 2: //Circle Type
                    Circle circle = (Circle) pair.second;
                    latLngArrayList.add(circle.getLatLng());
                    break;
            }
        }

        return latLngArrayList;
    }

    private static Pair<Integer, Object> decodeArea(String area, Context context) {
        int areaType = 0;

        if (area.contains("polygon".toUpperCase())) {
            areaType = 1;
        } else if (area.contains("circle".toUpperCase())) areaType = 2;


        switch (areaType) {
            case 1:     ////////// Polygon
                final String regexPolygon = "\\(\\((.*?)\\)\\)";
                final Pattern patternPolygon = Pattern.compile(regexPolygon, Pattern.MULTILINE);
                Matcher matcher = patternPolygon.matcher(area);
                List<LatLng> latLngs = new LinkedList<>();

                if (matcher.find()) {

                    String latlnString = matcher.group(1);

                    String[] latlng = latlnString.split(",");

                    for (String ltln : latlng) {

                        String[] tmp = ltln.trim().split(" ");
                        double Lat = Double.valueOf(tmp[0]);
                        double Lon = Double.valueOf(tmp[1]);
                        latLngs.add(new LatLng(Lat, Lon));
                    }
                    return new Pair<>(areaType, new Polygon(latLngs));
                }
                break;
            case 2: //////////    Circle
                final String regexCircle = context.getString(R.string.regixCircle);
                final Pattern patternCircle = Pattern.compile(regexCircle, Pattern.MULTILINE);
                Matcher matcherCircle = patternCircle.matcher(area);

                if (matcherCircle.find()) {
                    String[] str = matcherCircle.group(1).split(",");

                    String[] LatLong = str[0].trim().split(" ");

                    double Lat = Double.valueOf(LatLong[0].trim());

                    double Lon = Double.valueOf(LatLong[1].trim());

                    double radius = Double.valueOf(str[1].trim());
                    return new Pair<>(areaType, new Circle(radius, new LatLng(Lat, Lon)));
                }
                break;
        }
        return null;
    }

    public static GoogleMap addGeoFences(Context context, GoogleMap mMap, Map<Integer, GeoFence> geofences) {


        for (Map.Entry<Integer, GeoFence> entry : geofences.entrySet()) {
            GeoFence geoFence = entry.getValue();
            Pair<Integer, Object> pair = decodeArea(geoFence.getArea(), context);

            final TextView textView = new TextView(context);
            textView.setText(geoFence.getDescription());
            textView.setTextSize(20);
            final Paint paintText = textView.getPaint();
            final Rect boundsText = new Rect();
            paintText.getTextBounds(geoFence.getDescription(), 0, textView.length(), boundsText);
            paintText.setTextAlign(Paint.Align.CENTER);
            final Bitmap.Config conf = Bitmap.Config.ARGB_8888;
            final Bitmap bmpText = Bitmap.createBitmap(boundsText.width() + 2
                    * 20, boundsText.height() + 20 * 5, conf);

            final Canvas canvasText = new Canvas(bmpText);
            paintText.setColor(Color.BLACK);
            @SuppressWarnings("IntegerDivisionInFloatingPointContext") float halfCanvas = (canvasText.getWidth() / 2);
            canvasText.drawText(geoFence.getDescription(), halfCanvas,
                    canvasText.getHeight() - 20f - boundsText.bottom, paintText);

            assert pair != null;
            switch (pair.first) {
                case 1:
                    Polygon polygon = (Polygon) pair.second;
                    IconGenerator iconGenerator = new IconGenerator(context);
                    mMap.addMarker(new MarkerOptions()
                            .position(polygon.getLatLngs().get(0))
                            //.icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                            .anchor(0.5f, 1).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(geoFence.getDescription()))));
                    if (geoFence.getGeofenceAttributes().getColor() != null) {
                        String[] color = geoFence.getGeofenceAttributes().getColor().split("#");
                        PolygonOptions test = new PolygonOptions().addAll(polygon.getLatLngs()).fillColor(Color.parseColor("#37" + color[1]));
                        test.strokeColor(0);
                        mMap.addPolygon(test);

                    } else {
                        PolygonOptions test = new PolygonOptions().addAll(polygon.getLatLngs()).fillColor(context.getResources().getColor(R.color.transparent_home));
                        test.strokeColor(0);
                        mMap.addPolygon(test);
                    }
                    break;

                case 2: //Circle Type
                    Circle circle = (Circle) pair.second;
                    iconGenerator = new IconGenerator(context);
                    mMap.addMarker(new MarkerOptions()
                            .position(circle.getLatLng())
                            //.icon(BitmapDescriptorFactory.fromBitmap(bmpText))
                            .anchor(0.5f, 1).icon(BitmapDescriptorFactory.fromBitmap(iconGenerator.makeIcon(geoFence.getDescription()))));

                    mMap.addCircle(new CircleOptions()
                            .radius(circle.getRadius())
                            .center(circle.getLatLng())
                            .fillColor(context.getResources().getColor(R.color.transparent_home))
                    );
                    break;
            }

            if (decodeArea(geoFence.getArea(), context) == null) {

                return mMap;
            }
        }


        return mMap;
    }

    public static boolean checkInternet(Context context) {
        boolean connected = false;
        ConnectivityManager connectivityManager = (ConnectivityManager) context.getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_MOBILE).getState() == NetworkInfo.State.CONNECTED ||
                connectivityManager.getNetworkInfo(ConnectivityManager.TYPE_WIFI).getState() == NetworkInfo.State.CONNECTED) {
            //we are connected to a network
            connected = true;
        } else
            connected = false;
        return connected;
    }

}
