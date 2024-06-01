import org.gradle.api.JavaVersion

plugins {
    id("com.android.application")
}

android {
    namespace = "com.example.canonmart"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.canonmart"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
}

dependencies {
    implementation("androidx.appcompat:appcompat:1.4.0")
    implementation("com.google.android.material:material:1.5.0")
    implementation("androidx.activity:activity-ktx:1.4.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.3")
    implementation("com.squareup.retrofit2:retrofit:2.9.0")
    implementation("com.squareup.retrofit2:converter-gson:2.9.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("androidx.compose.ui:ui-tooling-preview:1.0.4")
    implementation("androidx.fragment:fragment:1.3.0")
    implementation("com.github.bumptech.glide:glide:4.12.0")
//    implementation(libs.recyclerview)
    annotationProcessor("com.github.bumptech.glide:compiler:4.12.0")
    implementation("com.airbnb.android:lottie:3.7.2")


    // Test dependencies
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.3")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.4.0")
}
