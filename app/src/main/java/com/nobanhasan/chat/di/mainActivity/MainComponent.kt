package com.nobanhasan.chat.di.mainActivity

import com.myres.noban.mvvmrxjava.di.scopes.PerActivity
import com.nobanhasan.chat.views.MainActivity
import dagger.Subcomponent

@PerActivity
@Subcomponent(modules = [MainModule::class])
interface MainComponent {
    fun inject(mainActivity: MainActivity)
}