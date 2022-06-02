package com.smart.agriculture.industrial.solutions.packages.ui.menu;

import androidx.appcompat.app.AppCompatActivity;
import androidx.databinding.DataBindingUtil;
import androidx.lifecycle.ViewModelProviders;

import android.content.Intent;
import android.os.Bundle;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;

import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.smart.agriculture.industrial.solutions.packages.AdminPanel;
import com.smart.agriculture.industrial.solutions.packages.HomeActivity;
import com.smart.agriculture.industrial.solutions.packages.R;
import com.smart.agriculture.industrial.solutions.packages.VehicleList;
import com.smart.agriculture.industrial.solutions.packages.databinding.MainFragmentBinding;
import com.smart.agriculture.industrial.solutions.packages.models.Event;
import com.smart.agriculture.industrial.solutions.packages.models.Route;
import com.smart.agriculture.industrial.solutions.packages.ui.ViewModel;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;

import java.util.Objects;

public class MainMenuFragment extends Fragment {

    private ViewModel mViewModel;
    MainFragmentBinding mainFragmentBinding;
    public static MainMenuFragment newInstance() {
        return new MainMenuFragment();
    }

    private static final String TAG = ">>>MainMenuFragment";
    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container,
                             @Nullable Bundle savedInstanceState) {
        ViewModel event = new ViewModel(getActivity().getApplication());
        mainFragmentBinding = DataBindingUtil.inflate(inflater, R.layout.main_fragment, null, false);
        View view = mainFragmentBinding.getRoot();
        mainFragmentBinding.setViewModel(event);
        mainFragmentBinding.menuLogs.setOnClickListener(this::onclickLog);
        mainFragmentBinding.menuRoutes.setOnClickListener(this::onclickAdmin);

        return view;
    }

    private void onclickAdmin(View view) {
        startActivity(new Intent(getActivity(), AdminPanel .class));
    }

    private void onclickLog(View view) {
        startActivity(new Intent(getActivity(), VehicleList.class));
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);
        mViewModel = ViewModelProviders.of(this).get(ViewModel.class);
        // TODO: Use the ViewModel
    }

}