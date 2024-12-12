plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")  // Ensure this is applied
}

android {
    namespace = "marlon.dev.bussing"
    compileSdk = 34

    defaultConfig {
        applicationId = "marlon.dev.bussing"
        minSdk = 24
        targetSdk = 34
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
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }

    buildFeatures {
        viewBinding = true
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.lifecycle.livedata.ktx)
    implementation(libs.lifecycle.viewmodel.ktx)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.legacy.support.v4)
    implementation(libs.activity)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    // Remove these explicit versions if you're using the Firebase BOM
    implementation ("androidx.appcompat:appcompat:1.7.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")

    // Firebase UI and Authentication
    implementation ("com.firebaseui:firebase-ui-auth:8.0.0")
    implementation ("com.google.firebase:firebase-auth")  // Use BOM to manage versions
    implementation ("com.google.firebase:firebase-analytics")  // Optional, for Firebase Analytics

    // Firebase BOM (Bill of Materials)
    implementation (platform("com.google.firebase:firebase-bom:33.6.0"))  // BOM automatically manages versions of Firebase libraries

    // Add Google Play Services Auth for Google Sign-In
    implementation ("com.google.android.gms:play-services-auth:20.1.0")  // Ensure this is added for Google Sign-In support

    // Material Component latest version
    implementation ("com.google.android.material:material:1.9.0")
}
