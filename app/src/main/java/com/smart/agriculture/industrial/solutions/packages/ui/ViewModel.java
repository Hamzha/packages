package com.smart.agriculture.industrial.solutions.packages.ui;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

import com.smart.agriculture.industrial.solutions.packages.Singleton;
import com.smart.agriculture.industrial.solutions.packages.models.Device;
import com.smart.agriculture.industrial.solutions.packages.models.Event;
import com.smart.agriculture.industrial.solutions.packages.models.GeoFence;
import com.smart.agriculture.industrial.solutions.packages.models.Position;
import com.smart.agriculture.industrial.solutions.packages.models.User;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;
import com.smart.agriculture.industrial.solutions.packages.utils.WebsiteUtils;
import com.smart.agriculture.industrial.solutions.packages.utils.services.Repository;

import org.json.JSONObject;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import retrofit2.Call;

public class ViewModel extends AndroidViewModel {
    private Repository repository;
    private Context context;
    private static final String TAG = ">>>ViewModel";
    public ViewModel(@NonNull Application application) {
        super(application);
        this.repository = new Repository(application);
        this.context = application;
    }

    public Call<User> login(User userModel) {
        Map<String, String> emailMap = new HashMap<>();
        emailMap.put(WebsiteUtils.EMAIL, userModel.getEmail());
        Map<String, String> passwordMap = new HashMap<>();
        passwordMap.put(WebsiteUtils.AUTH, userModel.getPassword());
        return this.repository.login(emailMap, passwordMap);
    }

    public Call<List<Device>> getDevices() {
        return this.repository.getDevices();
    }

    public Call<String> getDevicesRaw() {
        return this.repository.getDevicesRaw();
    }

    public void setDeviceModel(Map<Long, Device> deviceModel) {
        ((Singleton) this.context).setDevicesFinal(deviceModel);
    }

    public Map<Long, Device> getDevicesModels() {
        return ((Singleton) this.context).getDevicesFinal();
    }


    public Call<List<GeoFence>> getGeoFence() {
        return repository.getGeoFence();
    }


    public void setGeoFences(Map<Integer, GeoFence> longGeoFenceModelMap) {
        ((Singleton) this.context).setGeoFences(longGeoFenceModelMap);
    }

    public Call<List<Event>> getEvents(int deviceId, String startTime, String endTime) {


        return repository.getEvents(deviceId, startTime, endTime);
    }

    public Map<Integer, GeoFence> getGeoFencesModel() {
        return ((Singleton) this.context).getGeoFences();
    }

    public Call<List<Position>> getPosition() {
        return repository.getPositions();
    }

    public Call<List<Position>> getPosition(long id) {
        return repository.getPosition(id);
    }


    public Call<String> getCommands(long id) {
        return repository.getCommands(id);
    }

    public Call<String> setCommand(long id, String body) {
        return repository.setCommands(id,body);
    }
    public void onMotorActionClicked() {
        Static.logD(TAG,"Exit");
    }
    public void onMotorLogsClicked() {
    }
}

