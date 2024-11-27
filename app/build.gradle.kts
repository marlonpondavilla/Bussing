plugins {
    alias(libs.plugins.android.application)
    id("com.google.gms.google-services")
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
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)

    implementation ("androidx.appcompat:appcompat:1.7.0") // AppCompat library
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4") // ConstraintLayout
    implementation ("com.firebaseui:firebase-ui-auth:8.0.0") // Firebase UI Auth
    implementation ("com.google.firebase:firebase-auth:21.1.0") // Firebase Authentication
    implementation ("com.google.firebase:firebase-analytics:21.1.0") // Firebase Analytics (optional)

    // Firebase BOM (Bill of Materials)
    implementation (platform("com.google.firebase:firebase-bom:33.6.0"))

    // Firebase SDK
    implementation ("com.google.firebase:firebase-auth")

}
