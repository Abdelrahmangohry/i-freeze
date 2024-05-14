plugins {
    id("com.android.library")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    // hilt
    // kapt
    id ("kotlin-kapt")
    id ("kotlinx-serialization")
    id ("kotlin-parcelize")
}

android {
    namespace = "com.ifreeze.data"
    compileSdk = 34

    defaultConfig {
        minSdk = 28

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        consumerProguardFiles("consumer-rules.pro")
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
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")

    // retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation(platform("com.squareup.okhttp3:okhttp-bom:4.9.1"))
    implementation ("com.squareup.okhttp3:okhttp-urlconnection")
    implementation( "com.squareup.okhttp3:logging-interceptor")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation( "org.jetbrains.kotlinx:kotlinx-serialization-json:1.5.1")


    // hilt
    implementation( "com.google.dagger:hilt-android:2.44")
    implementation("androidx.test:monitor:1.6.0")

    kapt("com.google.dagger:hilt-android-compiler:2.44")

    val room_version = "2.4.2" // Use the latest version

    implementation ("androidx.room:room-runtime:$room_version")
    annotationProcessor ("androidx.room:room-compiler:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation( "androidx.room:room-ktx:$room_version")

}
kapt {
    correctErrorTypes = true
}