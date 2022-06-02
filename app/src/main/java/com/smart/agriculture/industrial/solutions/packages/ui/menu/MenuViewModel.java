package com.smart.agriculture.industrial.solutions.packages.ui.menu;

import android.app.Application;
import android.content.Context;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MenuViewModel extends AndroidViewModel {
    Context context;
    private MyCustomCallback myCustomCallback;
    public MenuViewModel(@NonNull Application application) {
        super(application);
        context = application;
    }
    public interface MyCustomCallback{
        void actionIsSuccessful();
        void actionFailed();
    }
}
