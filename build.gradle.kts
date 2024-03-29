// Top-level build file where you can add configuration options common to all sub-projects/modules.
buildscript {
    repositories {
        google()
        mavenCentral()
    }
    dependencies {
        classpath ("com.android.tools.build:gradle:8.1.2")
        classpath ("org.jetbrains.kotlin:kotlin-gradle-plugin:1.6.21")
        classpath ("com.google.dagger:hilt-android-gradle-plugin:2.44")
        classpath("com.google.gms:google-services:4.4.0")
    }
}
plugins {
    id("com.android.application") version "8.1.2" apply false
    id("org.jetbrains.kotlin.android") version "1.8.10" apply false
    id("com.android.library") version "8.1.2" apply false
    id("com.google.dagger.hilt.android") version "2.44" apply false
    id ("org.jetbrains.kotlin.plugin.serialization") version "1.6.21"

}