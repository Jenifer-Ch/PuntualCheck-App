plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "ec.edu.puntualcheck"
    compileSdk {
        version = release(36)
    }

    defaultConfig {
        applicationId = "ec.edu.puntualcheck"
        minSdk = 24
        targetSdk = 36
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
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Networking (API Laravel)
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.okhttp3:logging-interceptor:4.10.0")

    // QR y Cámara
    implementation("com.journeyapps:zxing-android-embedded:4.3.0")
    implementation("com.google.zxing:core:3.4.1")

    // Imágenes y Logo
    implementation("com.github.bumptech.glide:glide:4.15.1")

    // Firebase (Para Notificaciones)
    implementation(platform("com.google.firebase:firebase-bom:32.7.0"))
    implementation("com.google.firebase:firebase-messaging")

    // Almacenamiento Seguro (Tokens)
    implementation("androidx.security:security-crypto:1.1.0-alpha06")

    // Swipe Refresh (Para el Pull-to-refresh que pediste)
    implementation("androidx.swiperefreshlayout:swiperefreshlayout:1.1.0")
}