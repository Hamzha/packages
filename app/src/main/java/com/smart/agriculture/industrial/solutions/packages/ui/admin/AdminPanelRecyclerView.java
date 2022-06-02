package com.smart.agriculture.industrial.solutions.packages.ui.admin;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.os.AsyncTask;
import android.view.LayoutInflater;
import android.view.ViewGroup;

import androidx.annotation.NonNull;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.RecyclerView;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonParser;
import com.smart.agriculture.industrial.solutions.packages.R;
import com.smart.agriculture.industrial.solutions.packages.databinding.AdminPadenAdaptorBinding;
import com.smart.agriculture.industrial.solutions.packages.models.Device;
import com.smart.agriculture.industrial.solutions.packages.models.Position;
import com.smart.agriculture.industrial.solutions.packages.ui.ViewModel;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;
import com.smart.agriculture.industrial.solutions.packages.utils.interfaces.AdminPanelClickRecall;

import java.io.IOException;
import java.util.List;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Response;

public class AdminPanelRecyclerView extends RecyclerView.Adapter<AdminPanelRecyclerView.AdminPanelAdaptorHolder> {
    private List<Device> deviceList;
    private Activity context;
    private ViewModel viewModel;
    private static final String TAG = "AdminPanelRecyclerView";

    AdminPanelRecyclerView(List<Device> deviceList, Activity context, ViewModel mViewModel, List<Position> positionList) {
        this.deviceList = deviceList;
        this.context = context;
        this.viewModel = mViewModel;
    }


    void setData(List<Device> devices) {
        this.deviceList = devices;
    }

    @NonNull
    @Override
    public AdminPanelAdaptorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        AdminPadenAdaptorBinding adminPadenAdaptorBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.admin_paden_adaptor, parent, false);
        return new AdminPanelRecyclerView.AdminPanelAdaptorHolder(adminPadenAdaptorBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull AdminPanelAdaptorHolder holder, int position) {
        if (deviceList.size() != 0) {
            Device deviceTmp = deviceList.get(position);

            holder.adminPadenAdaptorBinding.geoFenceAdaptorDeviceNameTextView.setText(deviceTmp.getName());

            Static.logD(TAG,deviceTmp.toString());
            if(deviceTmp.getAttributes().getStatus() == null){
                holder.adminPadenAdaptorBinding.geoFenceAdaptorDeviceDescriptionTextView.setText("N/A");
            }
            else if ( deviceTmp.getAttributes().getStatus().toLowerCase().equals("active")) {
                holder.adminPadenAdaptorBinding.geoFenceAdaptorDeviceDescriptionTextView.setText("Current Status: Activated");
            } else
                holder.adminPadenAdaptorBinding.geoFenceAdaptorDeviceDescriptionTextView.setText("Current Status: Deactivated");
            holder.adminPadenAdaptorBinding.setOnItemClick(new AdminPanelClickRecall() {
                @Override
                public void activate() {
                    if (Static.checkInternet(context)) {
                        RetryAsyncTask re = new RetryAsyncTask(deviceTmp, viewModel, "active");
                        re.execute();
                    } else {
                        final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                        sDialog.setTitle("ERROR");
                        sDialog.setContentText(String.valueOf(R.string.no_internet_response));
                        sDialog.show();
                    }
                }

                @Override
                public void deActivate() {
                    if (Static.checkInternet(context)) {
                        RetryAsyncTask re = new RetryAsyncTask(deviceTmp, viewModel, "inactive");
                        re.execute();
                    } else {
                        final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                        sDialog.setTitle("ERROR");
                        sDialog.setContentText(String.valueOf(R.string.no_internet_response));
                        sDialog.show();
                    }
                }
            });
        }
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }


    class AdminPanelAdaptorHolder extends RecyclerView.ViewHolder {
        AdminPadenAdaptorBinding adminPadenAdaptorBinding;

        AdminPanelAdaptorHolder(AdminPadenAdaptorBinding adminPadenAdaptorBinding) {
            super(adminPadenAdaptorBinding.getRoot());
            this.adminPadenAdaptorBinding = adminPadenAdaptorBinding;
            this.adminPadenAdaptorBinding.executePendingBindings();
        }
    }


    @SuppressLint("StaticFieldLeak")
    class RetryAsyncTask extends AsyncTask<Void, Void, Boolean> {
        Device device;
        ViewModel viewModel;
        String activation;

        RetryAsyncTask(Device device, ViewModel viewModel, String activate) {
            this.device = device;
            this.viewModel = viewModel;
            this.activation = activate;
        }

        @SuppressLint("ShowToast")
        @Override
        protected Boolean doInBackground(Void... voids) {
            try {


                Response<String> commands = viewModel.getDevicesRaw().execute();

                if ((commands.code() == 200 || commands.code() == 202) && commands.body() != null) {

                    JsonArray jsonArray = new JsonParser().parse(commands.body()).getAsJsonArray();
                    for (JsonElement jsonObject : jsonArray) {     //Splitting response to JSONArray and treverse each object
                        String jsnObj = jsonObject.toString();
                        try {
                            if (jsnObj.contains(Long.toString(device.getId()))) {        //Json object contains ENGINE ON
                                if (this.activation.equals("active")) {
                                    if (jsnObj.contains("inactive"))
                                        jsnObj = jsnObj.replace("inactive", "active");
                                } else if (this.activation.equals("inactive")) {
                                    if (jsnObj.contains("inactive")) {
                                    } else if (jsnObj.contains("active"))
                                        jsnObj = jsnObj.replace("active", "inactive");
                                }

                                Response<String> response = viewModel.setCommand(device.getId(), jsnObj).execute();
                                Static.logD(TAG, response.code() + response.message());
                                if (response.code() == 200 || response.code() == 202) {
                                    return true;
                                } else
                                    return false;
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                }
            } catch (IOException e) {
                e.printStackTrace();
                return false;

            }
            return false;

        }

        @Override
        protected void onPostExecute(Boolean aBoolean) {
            super.onPostExecute(aBoolean);
            if (aBoolean) {
                final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.SUCCESS_TYPE);
                sDialog.setTitle("Success");
                sDialog.setContentText("Your Instructions is queued.\nScroll down to refresh");

                sDialog.show();
            } else {
                final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                sDialog.setTitle("ERROR");
                sDialog.setContentText(String.valueOf(R.string.common_Error));

                sDialog.show();

            }

        }

    }

//    private void getPosition(ViewModel viewModel) {
//
//        try {
//            if (Static.checkInternet(context)) {
//
//            } else {
//                final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
//                sDialog.setTitle("ERROR");
//                sDialog.setContentText("Please try again after some time.");
//                sDialog.show();
//            }
//
//        } catch (Exception e) {
//
//            final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
//            sDialog.setTitle("ERROR");
//            sDialog.setContentText("Please Chcek yout internet connection.");
//            sDialog.show();
//
//
//        }
//    }
//
//    private static void JSONReplace(JSONObject obj, String keyMain, String valueMain, String
//            newValue) throws Exception {
//        // We need to know keys of Jsonobject
//        Iterator iterator = obj.keys();
//        String key;
//        while (iterator.hasNext()) {
//            key = (String) iterator.next();
//            // if object is just string we change value in key
//            if ((obj.optJSONArray(key) == null) && (obj.optJSONObject(key) == null)) {
//                if ((key.equals(keyMain)) && (obj.get(key).toString().equals(valueMain))) {
//                    // put new value
//                    obj.put(key, newValue);
//                    return;
//                }
//            }
//
//            // if it's jsonobject
//            if (obj.optJSONObject(key) != null) {
//                JSONReplace(obj.getJSONObject(key), keyMain, valueMain, newValue);
//            }
//
//            // if it's jsonarray
//            if (obj.optJSONArray(key) != null) {
//                JSONArray jArray = obj.getJSONArray(key);
//                for (int i = 0; i < jArray.length(); i++) {
//                    JSONReplace(jArray.getJSONObject(i), keyMain, valueMain, newValue);
//                }
//            }
//        }
//    }
//
//
//    public static JSONObject setProperty(JSONObject js1, String keys, String valueNew) throws JSONException {
//        String[] keyMain = keys.split("\\.");
//        for (String keym : keyMain) {
//            Iterator iterator = js1.keys();
//            String key = null;
//            while (iterator.hasNext()) {
//                key = (String) iterator.next();
//                if ((js1.optJSONArray(key) == null) && (js1.optJSONObject(key) == null)) {
//                    if ((key.equals(keym))) {
//                        js1.put(key, valueNew);
//                        return js1;
//                    }
//                }
//                if (js1.optJSONObject(key) != null) {
//                    if ((key.equals(keym))) {
//                        js1 = js1.getJSONObject(key);
//                        break;
//                    }
//                }
//                if (js1.optJSONArray(key) != null) {
//                    JSONArray jArray = js1.getJSONArray(key);
//                    for (int i = 0; i < jArray.length(); i++) {
//                        js1 = jArray.getJSONObject(i);
//                    }
//                    break;
//                }
//            }
//        }
//        return js1;
//    }
}
