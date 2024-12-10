plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
    id("androidx.room")
}



android {
    namespace = "com.example.mad_app"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.mad_app"
        minSdk = 28
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner = "androidx.test.runner.AndroidJUnitRunner"
    }

    buildTypes {
        release {
            isMinifyEnabled = false
            proguardFiles(getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    room {
        schemaDirectory("$projectDir/schemas")
    }

}


dependencies {

    //splash API
    implementation("androidx.core:core-splashscreen:1.0.1")
    implementation ("com.github.LottieFiles:dotlottie-android:0.5.0")
    implementation ("com.airbnb.android:lottie:6.6.0")


    //firebase
    implementation("com.google.firebase:firebase-analytics")
    implementation ("com.google.firebase:firebase-auth:22.1.1")
    implementation ("com.google.firebase:firebase-firestore:24.9.0")
    implementation(platform("com.google.firebase:firebase-bom:33.7.0"))
    implementation("com.google.firebase:firebase-firestore")
    implementation("com.google.firebase:firebase-database:21.0.0")

    //gson
    implementation ("com.google.code.gson:gson:2.8.9")


    implementation ("androidx.work:work-runtime:2.7.1")
    implementation ("androidx.core:core-ktx:1.10.1")

    implementation ("androidx.cardview:cardview:1.0.0")
    implementation ("androidx.core:core:1.10.0")
    implementation("androidx.appcompat:appcompat:1.7.0")
    implementation("com.google.android.material:material:1.12.0")
    implementation("androidx.constraintlayout:constraintlayout:2.2.0")
    implementation("androidx.legacy:legacy-support-v4:1.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.2.1")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.6.1")
}