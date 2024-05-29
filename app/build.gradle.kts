plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("com.google.dagger.hilt.android")
    // hilt
    // kapt
    id ("kotlin-kapt")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.ifreeze.applock"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.ifreeze.applock"
        minSdk = 28
        targetSdk = 33
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
        vectorDrawables {
            useSupportLibrary = true
        }
    }

    flavorDimensions.add("I-freeze")
    productFlavors {
        create("beta") {
            dimension = "I-freeze"
            applicationId = "com.ifreeze.applock"
//            buildConfigField("String", "appName", "My QA App")
        }
        create("prod") {
            dimension = "I-freeze"
            applicationId = "com.ifreeze.applock"
//            buildConfigField("String", "appName", "My Prod App")
        }
    }
    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"),
                "proguard-rules.pro"
            )
            signingConfig = signingConfigs.getByName("debug")
        }
        debug{

        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }
    kotlinOptions {
        jvmTarget = "17"
    }
    buildFeatures {
        viewBinding = true
        buildConfig = true
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = "1.4.3"
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {

    implementation("androidx.core:core-ktx:1.9.0")
    implementation("androidx.lifecycle:lifecycle-runtime-ktx:2.6.1")
    implementation("androidx.activity:activity-compose:1.8.0")
    implementation("androidx.compose.ui:ui")
    implementation("androidx.compose.ui:ui-graphics")
    implementation("androidx.compose.ui:ui-tooling-preview")
    implementation("androidx.compose.material3:material3")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.9.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.constraintlayout:constraintlayout-core:1.0.4")
    implementation("androidx.work:work-runtime-ktx:2.8.1")
    implementation("com.google.firebase:firebase-auth:22.3.0")
    implementation(platform("androidx.compose:compose-bom:2023.03.00"))
    implementation("androidx.hilt:hilt-common:1.2.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    androidTestImplementation("androidx.compose.ui:ui-test-junit4")
    androidTestImplementation(platform("androidx.compose:compose-bom:2023.03.00"))
    debugImplementation("androidx.compose.ui:ui-tooling")
    debugImplementation("androidx.compose.ui:ui-test-manifest")
    implementation("com.google.dagger:hilt-android:2.44")
    implementation("androidx.hilt:hilt-navigation-compose:1.1.0-alpha01")
    kapt("com.google.dagger:hilt-android-compiler:2.44")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-core:1.7.0")
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.0")
    implementation("androidx.fragment:fragment-ktx:1.5.7")
    implementation("androidx.activity:activity-ktx:1.7.1")
    implementation("io.coil-kt:coil:2.4.0")
    implementation("androidx.navigation:navigation-runtime:2.7.0")
    implementation("com.github.bumptech.glide:glide:4.14.2")
    implementation("com.squareup.retrofit2:converter-scalars:2.9.0")
    implementation("com.google.android.gms:play-services-location:21.1.0")
    implementation("com.google.code.gson:gson:2.10")
    kapt ("com.github.bumptech.glide:compiler:4.12.0")
    // Turbine is a small testing library for kotlinx.coroutines
    testImplementation("app.cash.turbine:turbine:0.7.0")
    // mockito
    testImplementation("org.mockito:mockito-inline:2.21.0")
    testImplementation("org.mockito:mockito-core:4.0.0")
    testImplementation("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.1")
    implementation("androidx.navigation:navigation-compose:2.7.4")
    implementation(project(":domain"))
    implementation(project(":base"))
    implementation(project(":data"))
    implementation(project(":di"))
    // pagination

    val room_version = "2.4.2" // Use the latest version

    implementation ("androidx.room:room-runtime:$room_version")
    kapt ("androidx.room:room-compiler:$room_version")
    annotationProcessor ("androidx.room:room-compiler:$room_version")

    // optional - Kotlin Extensions and Coroutines support for Room
    implementation( "androidx.room:room-ktx:$room_version")

    implementation("androidx.paging:paging-runtime-ktx:3.2.0")

    implementation("androidx.compose.material:material-icons-extended:1.5.4")

    implementation("androidx.hilt:hilt-work:1.0.0")

}
kapt {
    correctErrorTypes = true
}