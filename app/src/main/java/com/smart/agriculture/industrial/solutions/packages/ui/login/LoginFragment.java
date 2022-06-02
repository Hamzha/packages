package com.smart.agriculture.industrial.solutions.packages.ui.login;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProviders;

import com.smart.agriculture.industrial.solutions.packages.HomeActivity;
import com.smart.agriculture.industrial.solutions.packages.MainMenu;
import com.smart.agriculture.industrial.solutions.packages.R;
import com.smart.agriculture.industrial.solutions.packages.databinding.LoginFragmentBinding;
import com.smart.agriculture.industrial.solutions.packages.models.Device;
import com.smart.agriculture.industrial.solutions.packages.models.User;
import com.smart.agriculture.industrial.solutions.packages.ui.ViewModel;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;

import java.net.CookieManager;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Objects;

import cn.pedant.SweetAlert.SweetAlertDialog;
import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

import static android.content.Context.MODE_PRIVATE;

public class LoginFragment extends Fragment {

    private static final String MY_PREFS_NAME = "LOGIN_PACKAGES";
    private LoginFragmentBinding loginFragmentBinding;
    private ViewModel mViewModel;
    private User userModel;
    private Context context;
    private static final String TAG = ">>>LoginFragment";

    public static LoginFragment newInstance() {
        return new LoginFragment();
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        // Creates a button that mimics a crash when clicked
        userModel = new User();

        SharedPreferences sharedPreferences = Objects.requireNonNull(getContext()).getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE);
        String username = sharedPreferences.getString("username", "empty");//"No name defined" is the default value.
        String password = sharedPreferences.getString("password", "empty");//"No name defined" is the default value.

        Static.logD(TAG+"<><>",username + " - " + password);
        if(!username.equals("empty")){
//            loginFragmentBinding.loginEmailEditText.setText(username);
            userModel.setEmail(username);
        }
        if(!password.equals("empty")){
//            loginFragmentBinding.loginPasswordEditType.setText(password);
            userModel.setPassword(password);
        }



        loginFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.login_fragment, null, false);
        View view = loginFragmentBinding.getRoot();
        loginFragmentBinding.setUser(userModel);
        context = getActivity();
        loginFragmentBinding.loginDoneBtn.setOnClickListener(this::onClick);
        return view;
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        // TODO: Use the ViewModel


        Objects.requireNonNull(((AppCompatActivity) Objects.requireNonNull(getActivity())).getSupportActionBar()).setTitle("Login");
    }


    private void onClick(View view) {

        if (!Static.checkInternet(context)) {
            final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
            sDialog.setTitle("ERROR");
            sDialog.setContentText(getResources().getString(R.string.no_internet_response));
            sDialog.show();
        } else {

            if (userModel.checkEmptyField(loginFragmentBinding)) {
                Static.logD(TAG, "EMPTY FIELDS Or");
            } else {
                loginFragmentBinding.loginDoneBtn.setVisibility(View.INVISIBLE);
                loginFragmentBinding.loginProgressBarAvi.setVisibility(View.VISIBLE);
                mViewModel.login(userModel).enqueue(new Callback<User>() {
                    @Override
                    public void onResponse(Call<User> call, Response<User> responseUserModel) {
                        try {
                            if (responseUserModel.code() == 200) {
                                loginResponse(responseUserModel);
                            } else {

                                loginFragmentBinding.loginProgressBarAvi.setVisibility(View.GONE);
                                loginFragmentBinding.loginDoneBtn.setVisibility(View.VISIBLE);

                                final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                                sDialog.setTitle("ERROR");
                                sDialog.setContentText(getResources().getString(R.string.invalid_credentials_response));
                                sDialog.show();

                            }
                        } catch (Exception ex) {
                            final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                            sDialog.setTitle("ERROR");
                            sDialog.setContentText(getResources().getString(R.string.common_Error));
                            sDialog.show();
                        }
                    }

                    @Override
                    public void onFailure(Call<User> call, Throwable t) {
                        Static.logD(TAG, t.getMessage());

                        loginFragmentBinding.loginDoneBtn.setVisibility(View.VISIBLE);
                        loginFragmentBinding.loginProgressBarAvi.setVisibility(View.INVISIBLE);
                        final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                        sDialog.setTitle("ERROR");
                        sDialog.setContentText(getResources().getString(R.string.common_Error));
                        sDialog.show();

                    }
                });
            }
        }
    }


    private void
    intiHashMap(List<Device> devices) {


        @SuppressLint("UseSparseArrays") Map<Long, Device> devicesFinal = new HashMap<>();
        for (int i = 0; i < devices.size(); i++) {
            devicesFinal.put(devices.get(i).getId(), devices.get(i));
        }

        mViewModel.setDeviceModel(devicesFinal);

    }

    private void onResponsePrivate(Response<User> response, Context context) {
        if (response.isSuccessful() && response.body() != null) {

            SharedPreferences.Editor editor = getContext().getSharedPreferences(MY_PREFS_NAME, MODE_PRIVATE).edit();
            editor.putString("username", String.valueOf(loginFragmentBinding.loginEmailEditText.getText().toString()));
            editor.putString("password", String.valueOf(loginFragmentBinding.loginPasswordEditType.getText().toString()));
            editor.apply();


            if (response.body().getAttributes().getRoles() != null && response.body().getAttributes().getRoles().toLowerCase().contains("admin")) {
                Static.logD(TAG, "ADMIN");
                startActivity(new Intent(getActivity(), MainMenu.class));

            } else {
                startActivity(new Intent(getActivity(), HomeActivity.class));
                Objects.requireNonNull(getActivity()).finish();
            }
        } else {
            loginFragmentBinding.loginDoneBtn.setVisibility(View.VISIBLE);
            loginFragmentBinding.loginProgressBarAvi.setVisibility(View.INVISIBLE);
            Static.retry(context, response.code(), bool -> {
                if (!bool) {
                    Objects.requireNonNull(getActivity()).finish();
                }
                {
                    loginFragmentBinding.loginProgressBarAvi.setVisibility(View.GONE);

                }
            });
        }
    }


    private void loginResponse(Response<User> responseUserModel) {
        if (responseUserModel.isSuccessful() || responseUserModel.body() == null) {
            mViewModel.getDevices().enqueue(new Callback<List<Device>>() {
                @Override
                public void onResponse(Call<List<Device>> call, Response<List<Device>> responseDevices) {

                    if (responseDevices.code() == 200) {
                        userDeviceResponse(responseDevices, responseUserModel);
                        CookieManager.setDefault(new CookieManager());
                    } else {
                        final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                        sDialog.setTitle("ERROR");
                        sDialog.setContentText(getResources().getString(R.string.common_Error));
                        sDialog.show();
                    }
                }

                @Override
                public void onFailure(Call<List<Device>> call, Throwable t) {
                    final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                    sDialog.setTitle("ERROR");
                    sDialog.setContentText(getResources().getString(R.string.common_Error));

                    sDialog.show();

                }
            });
        } else {
            final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
            sDialog.setTitle("ERROR");
            sDialog.setContentText(getResources().getString(R.string.common_Error));

            sDialog.show();
        }
    }


    private void userDeviceResponse(Response<List<Device>> responseDevices, Response<User> responseUserModel) {
        if (responseDevices.isSuccessful()) {
            if (responseDevices.body() != null) {
                intiHashMap(responseDevices.body());
                onResponsePrivate(responseUserModel, context);
            } else {
                final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
                sDialog.setTitle("ERROR");
                sDialog.setContentText(getResources().getString(R.string.common_Error));

                sDialog.show();
            }
        } else {
            final SweetAlertDialog sDialog = new SweetAlertDialog(context, SweetAlertDialog.ERROR_TYPE);
            sDialog.setTitle("ERROR");
            sDialog.setContentText(getResources().getString(R.string.common_Error));

            sDialog.show();
        }
    }

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        if (loginFragmentBinding != null) {
            loginFragmentBinding.loginDoneBtn.setVisibility(View.VISIBLE);
            loginFragmentBinding.loginProgressBarAvi.setVisibility(View.INVISIBLE);
        }
    }

    @Override
    public void onResume() {
        super.onResume();
        if (loginFragmentBinding != null) {
            loginFragmentBinding.loginDoneBtn.setVisibility(View.VISIBLE);
            loginFragmentBinding.loginProgressBarAvi.setVisibility(View.INVISIBLE);
        }
    }
}