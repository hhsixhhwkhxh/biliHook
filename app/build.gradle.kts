plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "hhsixhhwkhxh.xposed.bilihook"
    compileSdk = 34

    defaultConfig {
        applicationId = "hhsixhhwkhxh.xposed.bilihook"
        minSdk = 28
        targetSdk = 34
        versionCode = 4
        versionName = "Beta v20250715"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    compileOnly(files("libs/classes.jar"))
    implementation("org.luckypray:dexkit:2.0.3")
}