package com.smart.agriculture.industrial.solutions.packages.utils.services;

import android.app.Activity;
import android.app.Application;

import com.smart.agriculture.industrial.solutions.packages.models.Device;
import com.smart.agriculture.industrial.solutions.packages.models.Event;
import com.smart.agriculture.industrial.solutions.packages.models.GeoFence;
import com.smart.agriculture.industrial.solutions.packages.models.Position;
import com.smart.agriculture.industrial.solutions.packages.models.User;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;
import com.smart.agriculture.industrial.solutions.packages.utils.URLS;

import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class Repository {

    private ApiInterface apiInterface;
    private Activity activity;
    private static final String TAG = ">>>Repository";
    Repository(Application application, Activity activity) {
        this.activity = activity;
        this.apiInterface = ApiClient.getInstance(application).getApi();
    }

    public Repository(Application application) {
        apiInterface = ApiClient.getInstance(application).getApi();
    }

    public Call<User> login(Map<String, String> usernameMap, Map<String, String> passwordMap) {
        return apiInterface.login(usernameMap, passwordMap);
    }

    public Call<List<Device>> getDevices() {
        return apiInterface.getDevices(URLS.devices());
    }


    public Call<String> getDevicesRaw() {
        return apiInterface.getDevicesRaw(URLS.devices());
    }

    public Call<List<Event>> getEvents(int deviceId, String startDate, String endDate) {
        Static.logD(TAG,URLS.events(deviceId, startDate, endDate));
        return apiInterface.getEvents(URLS.events(deviceId, startDate, endDate));
    }
    public Call<String> getCommands(long id){
            return apiInterface.getCommands(URLS.Command(id)) ;

    }
    //
//    public Call<String> destroySession() {
//        return apiInterface.destroySession(URLS.delete());
//    }
//
//
    public Call<List<GeoFence>> getGeoFence() {
        return apiInterface.getGeoFence(URLS.geoFence());
    }

    public Call<List<Position>> getPositions(){
        return apiInterface.getPositions(URLS.positions());
    }


    public Call<List<Position>> getPosition(long id){
        return apiInterface.getPositions(URLS.position(id));
    }

    public Call<String> setCommands(long id, String body) {
        Static.logD(TAG, body);
        return apiInterface.setCommands(URLS.user(id),body);

    }


//
//    private static class SetDevices extends AsyncTask<Activity, Void, Call<List<Device>>> {
//        private WeakReference<Activity> activityReference;
//        ApiInterface apiInterface;
//
//        SetDevices(Activity activity){
//            this.activityReference = new WeakReference<>(activity);
//        }
//
//
//        @Override
//        protected Call<List<Device>> doInBackground(Activity... activities) {
//            apiInterface = ApiClient.getInstance(this.activityReference).getApi();
//            Call<List<Device>> test = apiInterface.getDevices(URLS.devices());
//            return test;
//        }
//    }


}
