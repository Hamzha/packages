package com.smart.agriculture.industrial.solutions.packages;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.smart.agriculture.industrial.solutions.packages.models.GeoFence;
import com.smart.agriculture.industrial.solutions.packages.ui.fromgeofence.FromGeoFenceFragment;
import com.smart.agriculture.industrial.solutions.packages.ui.upperstatus.UpperStatusFragment;
import com.smart.agriculture.industrial.solutions.packages.utils.Constant;
import com.smart.agriculture.industrial.solutions.packages.utils.interfaces.GeoFenceInterface;

public class HomeActivity extends AppCompatActivity implements GeoFenceInterface {

    UpperStatusFragment upperStatusFragment;
    FromGeoFenceFragment fromGeoFenceFragment;
    private static final String TAG = "HomeActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_home);


        upperStatusFragment = new UpperStatusFragment();
        fromGeoFenceFragment = new FromGeoFenceFragment();

        getSupportFragmentManager().beginTransaction()
                .addToBackStack(Constant.FROM_GEO_FENCE_FRAGMENT)
                .replace(R.id.activity_main_2_upper_fragment, upperStatusFragment)
                .replace(R.id.activity_main_2_lower_fragment, fromGeoFenceFragment)
                .commit();
    }


    @Override
    public void geoFenceModelFromToUpper(GeoFence geoFenceModel) {
        UpperStatusFragment upperStatusFragment = (UpperStatusFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_2_upper_fragment);
        assert upperStatusFragment != null;
        upperStatusFragment.updateFromField(geoFenceModel);

    }

    @Override
    public void geoFenceModelUpperToTo(GeoFence geoFenceModel) {
        UpperStatusFragment upperStatusFragment = (UpperStatusFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_2_upper_fragment);
        assert upperStatusFragment != null;
        upperStatusFragment.updateToField(geoFenceModel);
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();
        int myFragment = this.getSupportFragmentManager().getBackStackEntryCount();
        if (myFragment == 0) {
            // add your code here
            finish();
        }
        if (myFragment == 1) {
            UpperStatusFragment upperStatusFragment = (UpperStatusFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_2_upper_fragment);
            assert upperStatusFragment != null;
            upperStatusFragment.updateFromField(null);
        }
        if (myFragment == 2) {
            UpperStatusFragment upperStatusFragment = (UpperStatusFragment) getSupportFragmentManager().findFragmentById(R.id.activity_main_2_upper_fragment);
            assert upperStatusFragment != null;
            upperStatusFragment.updateToField(null);
        }
    }
}