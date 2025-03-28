package com.example.ass2_travaler

import android.app.Application


import com.example.ass2_travaler.data.AppContainer
import com.example.ass2_travaler.data.DefaultAppContainer

class TravalerApplication : Application() {
    lateinit var container: AppContainer

    override fun onCreate() {
        super.onCreate()
        container = DefaultAppContainer(this)


    }

}