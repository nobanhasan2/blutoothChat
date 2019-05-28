package com.nobanhasan.chat;

import android.app.Activity;
import android.app.Application;

import androidx.annotation.NonNull;

import com.nobanhasan.chat.di.component.AppComponent;
import com.nobanhasan.chat.di.component.DaggerAppComponent;
import com.nobanhasan.chat.di.module.AppModule;

import org.jetbrains.annotations.Contract;

public class App extends Application {

    public static App instance;

    public AppComponent appComponent;

    @Contract(pure = true)
    public static synchronized App getApp() {
        return instance;
    }

    public static App getApplication(@NonNull Activity activity) {
        return (App) activity.getApplication();
    }

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;

        //Initialize Dagger
        appComponent = DaggerAppComponent
                .builder()
                .appModule(new AppModule(this))
                .build();
    }
}
