plugins {
    alias(libs.plugins.androidApplication)
}

android {
    namespace = "hhsixhhwkhxh.bilibili"
    compileSdk = 34

    defaultConfig {
        applicationId = "hhsixhhwkhxh.bilibili"
        minSdk = 29
        targetSdk = 34
        versionCode = 6
        versionName = "Beta v20250817"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"

        //添加版本信息
        buildConfigField("String", "VERSION_NAME", "\"$versionName\"")
        buildConfigField("int", "VERSION_CODE", "$versionCode")

        //添加构建时间戳
        buildConfigField("long", "BUILD_TIME", "${System.currentTimeMillis()}L")

    }

    buildTypes {
        release {

            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
            signingConfig = signingConfigs.getByName("debug")

        }
        debug {

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    buildFeatures {
        buildConfig = true
    }
    buildTypes {
        getByName("debug") {
            buildConfigField("Boolean", "IS_DEBUG", "true")
        }
        getByName("release") {
            buildConfigField("Boolean", "IS_DEBUG", "false")
        }

    }

}

dependencies {

    implementation(libs.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
    compileOnly(files("libs/XposedBridgeAPI-89.jar"))
    implementation("org.luckypray:dexkit:2.0.3")
}