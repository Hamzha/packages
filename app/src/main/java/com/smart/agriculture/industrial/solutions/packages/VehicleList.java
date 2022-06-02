package com.smart.agriculture.industrial.solutions.packages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.smart.agriculture.industrial.solutions.packages.ui.vechiclelist.VehicleListFragment;
import com.smart.agriculture.industrial.solutions.packages.utils.Constant;

public class VehicleList extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.vehicle_list_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.vehicle_list_fragment, VehicleListFragment.newInstance())
                    .commitNow();
        }
    }
}