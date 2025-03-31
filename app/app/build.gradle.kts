plugins {
    alias(libs.plugins.android.application)
    alias(libs.plugins.kotlin.android)
    id("org.jetbrains.kotlin.plugin.compose")
}

android {
    namespace = "com.example.vent"
    compileSdk = 35

    defaultConfig {
        applicationId = "com.example.vent"
        minSdk = 24
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        compose = true // Enable Compose
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.5.14"
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.volley)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation(libs.androidx.foundation.android)
    implementation(libs.media3.common.ktx)
    implementation(libs.itext7.core)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    debugImplementation(libs.androidx.ui.tooling)
    //Jetpack compose dependencies
    implementation(libs.androidx.activity.compose)
    implementation(libs.androidx.compose.bom.v20230800)
    implementation(libs.androidx.compose.ui.ui)
    implementation(libs.ui.graphics)
    implementation(libs.androidx.compose.ui.ui.tooling.preview)
    implementation("androidx.compose.material3:material3:1.3.1")
    // Volley
    implementation(libs.volley) // Use the latest version
    //Core KTX
    implementation("androidx.core:core-ktx:1.13.1")
    // required, else it doesn't work at all.
    implementation("androidx.work:work-runtime:2.9.1")


}