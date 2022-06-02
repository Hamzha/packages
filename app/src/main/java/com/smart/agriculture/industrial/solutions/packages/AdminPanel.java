package com.smart.agriculture.industrial.solutions.packages;

import android.os.Bundle;

import androidx.appcompat.app.AppCompatActivity;

import com.smart.agriculture.industrial.solutions.packages.ui.admin.AdminPanelFragment;
import com.smart.agriculture.industrial.solutions.packages.ui.login.LoginFragment;
import com.smart.agriculture.industrial.solutions.packages.ui.upperstatus.UpperStatusFragment;
import com.smart.agriculture.industrial.solutions.packages.utils.Constant;

public class AdminPanel extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_admin_panel);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.containerAdmin, AdminPanelFragment.newInstance())
                    .commitNow();
        }

    }


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        finish();
    }
}
