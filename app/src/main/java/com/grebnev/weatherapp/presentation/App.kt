package com.grebnev.weatherapp.presentation

import android.app.Application
import com.grebnev.weatherapp.di.ApplicationComponent
import com.grebnev.weatherapp.di.DaggerApplicationComponent

class App : Application() {

    lateinit var applicationComponent: ApplicationComponent

    override fun onCreate() {
        super.onCreate()
        applicationComponent = DaggerApplicationComponent.factory().create(this)
    }
}