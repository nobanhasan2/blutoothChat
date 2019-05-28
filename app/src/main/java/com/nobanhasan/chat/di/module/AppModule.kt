package com.nobanhasan.chat.di.module

import android.app.Application
import com.nobanhasan.chat.App
import dagger.Module
import dagger.Provides
import javax.inject.Singleton

@Module
class AppModule (application: Application){
    private var application: Application =application
    @Provides
    @Singleton
    internal fun provideApp(): App {
        return application as App
    }
}