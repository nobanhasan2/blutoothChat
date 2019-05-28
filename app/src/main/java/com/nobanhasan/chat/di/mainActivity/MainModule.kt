package com.nobanhasan.chat.di.mainActivity

import com.myres.noban.mvvmrxjava.di.scopes.PerActivity
import com.nobanhasan.chat.views.MainView
import dagger.Module
import dagger.Provides

@Module
class MainModule(mainView: MainView) {
    private var mainView: MainView =mainView
    @PerActivity
    @Provides
    internal fun provideView(): MainView {
        return mainView
    }
}