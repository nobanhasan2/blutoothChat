package com.nobanhasan.chat.di.component

import com.nobanhasan.chat.di.mainActivity.MainComponent
import com.nobanhasan.chat.di.mainActivity.MainModule
import com.nobanhasan.chat.di.module.AppModule
import dagger.Component
import javax.inject.Singleton

@Singleton
@Component(modules = [AppModule::class])
interface AppComponent {
    operator fun plus(mainModule: MainModule): MainComponent
}