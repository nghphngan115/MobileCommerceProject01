plugins {
    id("com.android.application")
    id("com.google.gms.google-services")
}

android {
    namespace = "com.group01.plantique"
    compileSdk = 34

    buildFeatures {
        viewBinding = true
    }

    defaultConfig {
        applicationId = "com.group01.plantique"
        minSdk = 31
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
}

dependencies {

    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.activity:activity:1.9.0")
    implementation("com.google.firebase:firebase-database:21.0.0")
    implementation("com.google.firebase:firebase-auth:23.0.0")
    implementation("com.android.car.ui:car-ui-lib:2.6.0")
    implementation("com.squareup.picasso:picasso:2.71828")
    implementation("com.google.firebase:firebase-storage:21.0.0")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
    implementation ("org.mindrot:jbcrypt:0.4")
    implementation ("com.sun.mail:android-mail:1.6.5")
    implementation ("com.google.android.gms:play-services-maps:17.0.1")
    implementation ("com.sun.mail:android-activation:1.6.5" )
    implementation(platform("com.google.firebase:firebase-bom:32.8.1"))
    implementation ("androidx.preference:preference-ktx:1.1.1" )
    implementation ("com.firebaseui:firebase-ui-database:8.0.0" )
    implementation ("com.github.bumptech.glide:glide:4.12.0" )
    implementation ("com.google.code.gson:gson:2.8.8")
    implementation("com.squareup.okhttp3:okhttp:4.12.0")
    implementation ("com.google.firebase:firebase-messaging:23.0.0")
    annotationProcessor ("com.github.bumptech.glide:compiler:4.12.0" )


}