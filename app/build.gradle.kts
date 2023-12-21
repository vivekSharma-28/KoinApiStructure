plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("org.jetbrains.kotlin.plugin.serialization")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.koinapistructure"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.koinapistructure"
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
    kotlinOptions {
        jvmTarget = "1.8"
    }
    buildFeatures {
        viewBinding = true
        dataBinding = true
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.10.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    //FireBase Google Login
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation ("com.google.firebase:firebase-bom:32.7.0")
    implementation ("com.google.android.gms:play-services-auth:20.7.0")
    implementation ("com.google.android.gms:play-services-plus:17.0.0")

    //Hawk
    implementation("com.orhanobut:hawk:2.0.1")

    //Koin
    implementation("io.insert-koin:koin-core:3.2.2")
    implementation("io.insert-koin:koin-android:3.2.2")

    //Retrofit
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.okhttp3:okhttp:5.0.0-alpha.2")
    implementation("com.squareup.okhttp3:logging-interceptor:5.0.0-alpha.1")

    //Loader
    implementation("com.github.emreesen27:Android-Nested-Progress:v1.0.2")

    //Permission X
    implementation("com.guolindev.permissionx:permissionx:1.7.1")

    //Shimmer Effect
    implementation ("com.facebook.shimmer:shimmer:0.5.0")

    //Glide
    implementation("com.github.bumptech.glide:glide:4.14.2")

    //Navigation
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.5")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.5")

    //Camera
    implementation("androidx.camera:camera-core:1.0.0-rc02")
    implementation("androidx.camera:camera-camera2:1.0.0-rc02")
    implementation("androidx.camera:camera-lifecycle:1.0.0-rc02")
    implementation("androidx.camera:camera-view:1.4.0-alpha02")
    implementation("androidx.camera:camera-extensions:1.4.0-alpha02")

}