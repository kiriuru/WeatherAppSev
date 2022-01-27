package com.kiriuru.weatherappsev

import android.app.Application
import com.kiriuru.weatherappsev.di.AppComponent
import com.kiriuru.weatherappsev.di.DaggerAppComponent

//Инициализация Даггера
open class App : Application() {
    val appComponent: AppComponent by lazy {
        initializeComponent()
    }

    open fun initializeComponent(): AppComponent {
        return DaggerAppComponent.factory().create(applicationContext)
    }
}