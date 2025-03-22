// 📄 data/AppContainer.kt
package com.example.ass2_travaler.data

import com.example.ass2_travaler.network.CityApiService

import retrofit2.Retrofit
import retrofit2.converter.gson.GsonConverterFactory

interface AppContainer {
    val cityRepository: TravelRepository
}

class DefaultAppContainer : AppContainer {
    private val baseUrl = "https://yuqiwu04.github.io/my_api_repo/"

    /**
     * 使用 Kotlin 序列化构建 Retrofit 实例
     */
    private val retrofit = Retrofit.Builder()
        .baseUrl(baseUrl)
        .addConverterFactory(GsonConverterFactory.create()) // 添加这行
        .build()

    /**
     * 城市数据接口服务
     */
    private val cityApiService: CityApiService by lazy {
        retrofit.create(CityApiService::class.java)
    }

    /**
     * 依赖注入实现
     */
    override val cityRepository: TravelRepository by lazy {
        NetworkCityRepository(cityApiService)
    }
}