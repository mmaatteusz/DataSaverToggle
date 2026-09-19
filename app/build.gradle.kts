plugins {
    id("com.android.application")
}

android {
    namespace = "pl.mateusz.datasavertoggle"
    compileSdk = 36

    defaultConfig {
        applicationId = "pl.mateusz.datasavertoggle"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
        }
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
}
