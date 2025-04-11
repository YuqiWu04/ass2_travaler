
import com.android.build.api.dsl.AndroidResources

plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    id("kotlinx-serialization")
    id("com.google.devtools.ksp")
}

android {
    namespace = "com.example.ass2_travaler"
    compileSdk = 35
    packaging {
        resources {
            excludes += "**/.DS_Store"
        }
    }
    defaultConfig {
        applicationId = "com.example.ass2_travaler"
        minSdk = 35
        targetSdk = 35
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }



    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
        }
    }


    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
    }

    aaptOptions {
        ignoreAssetsPattern = "!*.webp:!*.png:!*.jpg"
        ignoreAssetsPattern  = "!.DS_Store:!.localized"
        ignoreAssetsPattern  = "!.DS_Store:!Thumbs.db:!desktop.ini"
        additionalParameters .add("--no-version-vectors")
    }

}

dependencies {
    // ===== AndroidX base =====

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)

    implementation("androidx.lifecycle:lifecycle-viewmodel-compose:2.6.2")

    // ===== Compose =====

    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.ui)
    implementation(libs.androidx.ui.graphics)
    implementation(libs.androidx.ui.tooling.preview)
    implementation(libs.androidx.material3)
    implementation(libs.androidx.activity.compose)
    //  LiveData  Compose
    implementation(libs.androidx.runtime.livedata)

    // ===== test =====
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.ui.test.junit4)
    debugImplementation(libs.androidx.ui.tooling)
    debugImplementation(libs.androidx.ui.test.manifest)
    // JUnit 4
    testImplementation ("junit:junit:4.13.2")

    // AndroidX Core Testing
    testImplementation ("androidx.arch.core:core-testing:2.1.0")

    // Kotlin
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.10.1")
    // ===== Kotlinx Serialization =====
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-core:1.6.0")
    implementation("org.jetbrains.kotlinx:kotlinx-serialization-json:1.6.0")

    // ===== Retrofit  =====
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    // Gson transfer
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    // Kotlinx Serialization
    implementation("com.jakewharton.retrofit:retrofit2-kotlinx-serialization-converter:1.0.0")

    // ===== Coil  =====

    implementation("io.coil-kt.coil3:coil-compose:3.1.0")
    implementation("io.coil-kt.coil3:coil-network-okhttp:3.1.0")

    // ===== Navigation Compose =====
    implementation("androidx.navigation:navigation-compose:2.7.4")

    // ===== Kotlin  =====
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.3")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")

    implementation ("androidx.room:room-runtime:2.6.1")

    // Kotlin
    implementation ("androidx.room:room-ktx:2.6.1")

    // kotlin-kapt
    ksp("androidx.room:room-compiler:2.6.1")
    implementation("org.jetbrains.kotlinx:kotlinx-metadata-jvm:0.6.0")

    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.8.7")
}
