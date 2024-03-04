// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript{

    repositories {
        google()
        mavenCentral()
    }

    dependencies{
        classpath ("org.jetbrains.kotlin:kotlin-serialization:1.9.0")
        classpath("com.google.gms:google-services:4.4.1")
    }
}


plugins {
    id("com.android.application") version "8.1.4" apply false
    id("org.jetbrains.kotlin.android") version "1.9.0" apply false
    id("com.google.gms.google-services") version "4.4.0" apply false
}