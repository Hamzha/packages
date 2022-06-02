package com.smart.agriculture.industrial.solutions.packages;

import androidx.appcompat.app.AppCompatActivity;

import android.os.Bundle;

import com.smart.agriculture.industrial.solutions.packages.ui.menu.MainMenuFragment;

public class MainMenu extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setContentView(R.layout.main_menu_activity);
        if (savedInstanceState == null) {
            getSupportFragmentManager().beginTransaction()
                    .replace(R.id.container, MainMenuFragment.newInstance())
                    .commitNow();
        }
    }
}