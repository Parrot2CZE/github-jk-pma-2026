plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myapp008"
    compileSdk {
        version = release(37)
    }

    defaultConfig {
        applicationId = "com.example.myapp008"
        minSdk = 24
        targetSdk = 37
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            optimization {
                enable = false
            }
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    buildFeatures {
        viewBinding = true
    }

}

dependencies {
    implementation(libs.androidx.activity.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.core.ktx)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(libs.androidx.junit)
    // Knihovna pro Preferences DataStore
    implementation("androidx.datastore:datastore-preferences:1.1.1")

    // lifecycleScope + repeatOnLifecycle (korutiny navázané na životní cyklus)
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.8.7")
}