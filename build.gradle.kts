// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{

    repositories {
        google()
        mavenCentral()
        maven { url = uri("https://jitpack.io") }
    }

    dependencies{
        classpath ("org.jetbrains.kotlin:kotlin-serialization:1.8.10")

    }
}


plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
}