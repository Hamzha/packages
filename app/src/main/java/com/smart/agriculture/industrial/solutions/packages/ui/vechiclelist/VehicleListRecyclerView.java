package com.smart.agriculture.industrial.solutions.packages.ui.vechiclelist;

import android.annotation.SuppressLint;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.applikeysolutions.cosmocalendar.dialog.CalendarDialog;
import com.applikeysolutions.cosmocalendar.model.Day;
import com.applikeysolutions.cosmocalendar.settings.appearance.ConnectedDayIconPosition;
import com.applikeysolutions.cosmocalendar.utils.SelectionType;
import com.smart.agriculture.industrial.solutions.packages.R;
import com.smart.agriculture.industrial.solutions.packages.databinding.VehicleListAdaptorBinding;
import com.smart.agriculture.industrial.solutions.packages.models.Device;
import com.smart.agriculture.industrial.solutions.packages.ui.vehiclelog.VehicleLogsFragment;
import com.smart.agriculture.industrial.solutions.packages.utils.Constant;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;
import com.smart.agriculture.industrial.solutions.packages.utils.interfaces.ItemClickRecall;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;
import java.util.Locale;
import java.util.TimeZone;
import java.util.concurrent.TimeUnit;

import cn.pedant.SweetAlert.SweetAlertDialog;

public class VehicleListRecyclerView extends RecyclerView.Adapter<VehicleListRecyclerView.VehicleListAdaptorHolder> {
    private List<Device> deviceList;
    private Activity context;
    Date startDate, endDate;
    private static final String TAG = ">>>VehicleListRecyclerView";
    static DateFormat format = new SimpleDateFormat("yyyy-MMMM-dd HH:mm:ss", Locale.ENGLISH);

    public VehicleListRecyclerView(List<Device> deviceList, Activity context) {
        this.deviceList = deviceList;
        this.context = context;

    }

    @NonNull
    @Override
    public VehicleListAdaptorHolder onCreateViewHolder(@NonNull ViewGroup parent, int viewType) {
        VehicleListAdaptorBinding vehicleListAdaptorBinding = DataBindingUtil.inflate(LayoutInflater.from(parent.getContext()), R.layout.vehicle_list_adaptor, parent, false);
        return new VehicleListRecyclerView.VehicleListAdaptorHolder(vehicleListAdaptorBinding);
    }

    @Override
    public void onBindViewHolder(@NonNull VehicleListAdaptorHolder holder, int position) {
        Device deviceTmp = deviceList.get(position);

        holder.vehicleListAdaptorBinding.vehicleListAdaptorName.setText(deviceTmp.getName());

        holder.vehicleListAdaptorBinding.setOnItemClick(new ItemClickRecall() {
            @Override
            public void onClick() {

                if (Static.checkInternet(context)) {

//                    Bundle args = new Bundle();
//                    args.putParcelable("Device", deviceTmp);
//                    args.putString("startTime", "asdzxcqwe");
//                    args.putString("endTime", "asdzxcqwe");
//                    vehicleLogsFragment.setArguments(args);
//                    ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
//                            .addToBackStack(Constant.VEHICLE_LOG_FRAGMENT)
//                            .replace(R.id.vehicle_list_fragment, vehicleLogsFragment)
//                            .commit();
                    SingleChoiceDialogAndButtonAction(deviceTmp, context);
//                    context.startActivity(new Intent(context, VehicleLogs.class),bundle);
                } else {
                    final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                    sDialog.setTitle("ERROR");
                    sDialog.setContentText(String.valueOf(R.string.no_internet_response));
                    sDialog.show();
                }

            }
        });
    }

    @Override
    public int getItemCount() {
        return deviceList.size();
    }

    class VehicleListAdaptorHolder extends RecyclerView.ViewHolder {
        VehicleListAdaptorBinding vehicleListAdaptorBinding;

        VehicleListAdaptorHolder(VehicleListAdaptorBinding vehicleListAdaptorBinding) {
            super(vehicleListAdaptorBinding.getRoot());
            this.vehicleListAdaptorBinding = vehicleListAdaptorBinding;
            this.vehicleListAdaptorBinding.executePendingBindings();
        }
    }

    public static void SingleChoiceDialogAndButtonAction(final Device device, Activity context) {
        final String[] TRIP_DAYS = new String[]{
                context.getString(R.string.today),
                context.getString(R.string.yesterday),
                context.getString(R.string.hours_24),
                context.getString(R.string.day_7),
                context.getString(R.string.day_30),
                context.getString(R.string.day_60),
                context.getString(R.string.custom_date_60)
        };
        final String[] single_choice_selected = new String[1];
        single_choice_selected[0] = TRIP_DAYS[0];
        AlertDialog.Builder builder = new AlertDialog.Builder(context);
        builder.setTitle(context.getString(R.string.select_history));
        builder.setSingleChoiceItems(TRIP_DAYS, 0, (dialogInterface, i) -> single_choice_selected[0] = TRIP_DAYS[i]);

        builder.setPositiveButton(R.string.OK, (dialogInterface, i) -> {
            @SuppressLint("SimpleDateFormat") SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ss.SSS'Z'");

            if (single_choice_selected[0].equals(context.getString(R.string.today))) {
                Calendar startDate = Calendar.getInstance(TimeZone.getTimeZone("GMT+5"));
                startDate.setTime(Calendar.getInstance().getTime());
                startDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE), 0, 0, 0);

                Calendar endDate = new GregorianCalendar();
                Static.logD(TAG, format.format(startDate.getTime()) + "<---->" + format.format(endDate.getTime()));
                Static.logD(TAG, (startDate.getTime().getTime()) + "<---->" + endDate.getTime().getTime());

                activityCall(device, (Date) (startDate.getTime()), (Date) (endDate.getTime()), context);

            } else if (single_choice_selected[0].equals(context.getString(R.string.yesterday))) {
                Calendar calendar = new GregorianCalendar();
                Calendar startDate = new GregorianCalendar(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH), calendar.get(Calendar.DATE), 0, 0, 0);

                Calendar endDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE), 0, 0, 0);

                startDate.add(Calendar.DAY_OF_MONTH, -1);
                startDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE), 0, 0, 0);

                Static.logD(TAG, format.format(startDate.getTime()) + "<---->" + format.format(endDate.getTime()));
                Static.logD(TAG, (startDate.getTime().getTime()) + "<---->" + endDate.getTime().getTime());

                activityCall(device, startDate.getTime(), endDate.getTime(), context);

            } else if (single_choice_selected[0].equals(context.getString(R.string.hours_24))) {
                Calendar endDate = new GregorianCalendar();
                Calendar startDate = new GregorianCalendar();
                startDate.add(Calendar.DATE, -1);

                startDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE), 0, 0, 0);


                Static.logD(TAG, format.format(startDate.getTime()) + "<---->" + format.format(endDate.getTime()));
                Static.logD(TAG, (startDate.getTime().getTime()) + "<---->" + endDate.getTime().getTime());

                activityCall(device, startDate.getTime(), endDate.getTime(), context);

            } else if (single_choice_selected[0].equals(context.getString(R.string.day_7))) {
                Calendar endDate = new GregorianCalendar();
                Calendar startDate = new GregorianCalendar();
                startDate.add(Calendar.DATE, -7);

                startDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE), 0, 0, 0);

                Static.logD(TAG, format.format(startDate.getTime()) + "<---->" + format.format(endDate.getTime()));
                Static.logD(TAG, (startDate.getTime()) + "<---->" + endDate.getTime());
                activityCall(device, startDate.getTime(), endDate.getTime(), context);

            } else if (single_choice_selected[0].equals(context.getString(R.string.day_30))) {
                Calendar endDate = new GregorianCalendar();
                Calendar startDate = new GregorianCalendar();
                startDate.add(Calendar.DATE, -30);
                startDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE), 0, 0, 0);

                Static.logD(TAG, format.format(startDate.getTime()) + "<---->" + format.format(endDate.getTime()));
                Static.logD(TAG, (startDate.getTime()) + "<---->" + endDate.getTime());
                activityCall(device, startDate.getTime(), endDate.getTime(), context);

            } else if (single_choice_selected[0].equals(context.getString(R.string.day_60))) {
                Calendar endDate = new GregorianCalendar();
                Calendar startDate = new GregorianCalendar();
                startDate.add(Calendar.DATE, -60);
                startDate = new GregorianCalendar(startDate.get(Calendar.YEAR), startDate.get(Calendar.MONTH), startDate.get(Calendar.DATE), 0, 0, 0);

                Static.logD(TAG, format.format(startDate.getTime()) + "<---->" + format.format(endDate.getTime()));
                Static.logD(TAG, (startDate.getTime()) + "<---->" + endDate.getTime());
                activityCall(device, startDate.getTime(), endDate.getTime(), context);

            } else if (single_choice_selected[0].equals(context.getString(R.string.custom_date_60))) {
                selectNewDays(context, device);
            }
        });
        builder.setNegativeButton(R.string.CANCEL, null);
        builder.show();

    }

    private static void activityCall(Device device, Date startDate, Date endDate, Context context) {
        VehicleLogsFragment vehicleLogsFragment = new VehicleLogsFragment();

        Bundle args = new Bundle();
        args.putParcelable("Device", device);
        args.putString("startDate", format.format(startDate.getTime()));
        args.putString("endDate", format.format(endDate.getTime()));
        vehicleLogsFragment.setArguments(args);
        ((AppCompatActivity) context).getSupportFragmentManager().beginTransaction()
                .addToBackStack(Constant.VEHICLE_LOG_FRAGMENT)
                .replace(R.id.vehicle_list_fragment, vehicleLogsFragment)
                .commit();
    }

    private static void selectNewDays(Activity context, Device device) {
        CalendarDialog calendarDialog;
        calendarDialog = new CalendarDialog(context);
        calendarDialog.show();
        calendarDialog.setSelectionType(SelectionType.RANGE);
        calendarDialog.setSelectedDayBackgroundStartColor(context.getResources().getColor(R.color.blue_grey_700));
        calendarDialog.setSelectedDayBackgroundEndColor(context.getResources().getColor(R.color.blue_grey_700));
        calendarDialog.setCalendarOrientation(LinearLayoutManager.VERTICAL);
        calendarDialog.setConnectedDayIconPosition(ConnectedDayIconPosition.TOP);
        calendarDialog.setTitle("Select date Range for Trips");


        calendarDialog.setOnDaysSelectionListener(selectedDays -> {
            if (selectedDays.size() >= 1) {
                //int range = (selectedDays.size() <= 1) ? selectedDays.size() : (selectedDays.size() - 1);
                Day startRange = selectedDays.get(0);

                Day endRange = (selectedDays.size() == 1) ? null : selectedDays.get(selectedDays.size() - 1);

                Date startDate = startRange.getCalendar().getTime();
                Date endDate = (endRange == null) ? getPreviousDate(startDate) : endRange.getCalendar().getTime();

                startDate.setHours(0);
                startDate.setMinutes(0);
                startDate.setSeconds(0);

                endDate.setHours(0);
                endDate.setMinutes(0);
                endDate.setSeconds(0);

                long startDayMili = startDate.getTime();
                long endDayMili = endDate.getTime();

                int limit = 61;

                long days = TimeUnit.MILLISECONDS.toDays(endDayMili - startDayMili);
                if (days < 0) {
                    failPopup(context.getString(R.string.select_at_least_two_days), context);
                } else if (days >= limit) {
                    failPopup(context.getString(R.string.days_limit_exceeds_60), context);
                } else {
                    if (startDate.getTime() < endDate.getTime())
                        activityCall(device, startDate, endDate, context);
                    else
                        activityCall(device, endDate, startDate, context);
                }
            } else {
                Toast.makeText(context, context.getString(R.string.select_at_least_two_days), Toast.LENGTH_LONG).show();
            }
        });
    }

    private static Date getPreviousDate(Date startDate) {
        Calendar tmp = Calendar.getInstance();
        tmp.setTime(startDate);
        tmp.set(Calendar.HOUR, 0);
        tmp.set(Calendar.MINUTE, 0);
        tmp.set(Calendar.SECOND, 0);
        tmp.add(Calendar.DATE, 1);
        return tmp.getTime();
    }

    public static void failPopup(String msg, Activity context) {
        SweetAlertDialog pDialog = new SweetAlertDialog(context, SweetAlertDialog.WARNING_TYPE);
        pDialog.setTitleText(context.getString(R.string.error));
        pDialog.setContentText(msg);
        pDialog.showCancelButton(true);
        pDialog.setCancelable(true);
        pDialog.setOnShowListener(dialogInterface -> {
            SweetAlertDialog sweetAlertDialog = (SweetAlertDialog) dialogInterface;
            TextView text = (TextView) sweetAlertDialog.findViewById(R.id.content_text);
            text.setTextAlignment(View.TEXT_ALIGNMENT_CENTER);
            text.setSingleLine(false);
            text.setMaxLines(10);
            text.setLines(6);
        });
        pDialog.show();
    }

}
