package com.smart.agriculture.industrial.solutions.packages.utils.services;

import com.smart.agriculture.industrial.solutions.packages.models.Device;
import com.smart.agriculture.industrial.solutions.packages.models.Event;
import com.smart.agriculture.industrial.solutions.packages.models.GeoFence;
import com.smart.agriculture.industrial.solutions.packages.models.Position;
import com.smart.agriculture.industrial.solutions.packages.models.User;

import org.json.JSONObject;

import java.util.List;
import java.util.Map;

import retrofit2.Call;
import retrofit2.http.Body;
import retrofit2.http.FormUrlEncoded;
import retrofit2.http.GET;
import retrofit2.http.Headers;
import retrofit2.http.POST;
import retrofit2.http.PUT;
import retrofit2.http.QueryMap;
import retrofit2.http.Url;

public interface ApiInterface {
    @Headers({"Content-Type: application/x-www-form-urlencoded"})

    @POST("session")
    Call<User> login(@QueryMap Map<String, String> param, @QueryMap Map<String, String> password);

    @GET
    Call<List<Device>> getDevices(@Url String url);

    @GET
    Call<String> getDevicesRaw(@Url String url);

    @GET
    Call<List<Event>> getEvents(@Url String url);

    @GET
    Call<List<GeoFence>> getGeoFence(@Url String url);

    @Headers({"Accept: application/json"})
    @GET
    Call<List<Position>> getPositions(@Url String url);

    @GET
    Call<String> getCommands(@Url String url);

    @Headers({"Content-Type: application/json"})
    @PUT
    Call<String> setCommands(@Url String url,@Body String body);

}
