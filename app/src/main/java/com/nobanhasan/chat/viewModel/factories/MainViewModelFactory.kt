package com.nobanhasan.chat.viewModel.factories

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.nobanhasan.chat.viewModel.MainViewModel
import com.nobanhasan.chat.views.MainView

/**
 * Created by
 * Noban Hasan on
 * 5/26/19.
 */
class MainViewModelFactory(private val mainView: MainView) : ViewModelProvider.Factory{
    override fun <T : ViewModel?> create(modelClass: Class<T>): T {
        return MainViewModel(mainView) as T
    }
}