package com.smart.agriculture.industrial.solutions.packages;

import androidx.appcompat.app.AppCompatActivity;

import android.app.Fragment;
import android.os.Bundle;

import com.smart.agriculture.industrial.solutions.packages.ui.login.LoginFragment;
import com.smart.agriculture.industrial.solutions.packages.utils.Constant;
import com.smart.agriculture.industrial.solutions.packages.utils.Static;

public class MainActivity extends AppCompatActivity {

    private static final String TAG = "MainActivity";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, LoginFragment.newInstance())
                    .commitNow();
        }
    }

    @Override
    public void onBackPressed() {
        super.onBackPressed();

        Fragment myFragment = getFragmentManager().findFragmentByTag(Constant.FROM_GEO_FENCE_FRAGMENT);
        if (myFragment != null && myFragment.isVisible()) {
            // add your code here
            Static.logD(TAG, "VISIBLE" + myFragment.toString());

            Static.logD(TAG, Constant.VEHICLE_FRAGMENT);
        } else {
            Static.logD(TAG, "VISIBLE" + Constant.VEHICLE_FRAGMENT);
        }
    }
}
