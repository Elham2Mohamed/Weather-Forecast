plugins {
    id("com.android.application")
    id("org.jetbrains.kotlin.android")
    id("kotlin-kapt")
    id("androidx.navigation.safeargs")
}

android {
    namespace = "com.example.weatherforecastapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.weatherforecastapplication"
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
    }
}

dependencies {

    //SafeArgs
    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")

    //Room
    implementation ("androidx.room:room-runtime:2.6.1")
    kapt ("androidx.room:room-compiler:2.6.1")
    implementation ("androidx.room:room-ktx:2.6.1")

    //noinspection GradleDependency
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    //noinspection GradleDependency
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")
    //noinspection LifecycleAnnotationProcessorWithJava8
    kapt ("androidx.lifecycle:lifecycle-compiler:2.7.0")

    //noinspection GradleDependency
    implementation ("androidx.lifecycle:lifecycle-viewmodel-ktx:2.4.0")
    //noinspection GradleDependency
    implementation ("androidx.lifecycle:lifecycle-livedata-ktx:2.4.0")

//workManager
    implementation ("androidx.work:work-runtime-ktx:2.9.0")

    // Dependencies for local unit tests
    testImplementation ("junit:junit:4.13.2")
    testImplementation ("org.hamcrest:hamcrest-all:1.3")
    //noinspection GradleDependency
    testImplementation ("androidx.arch.core:core-testing:2.1.0")
    testImplementation ("org.robolectric:robolectric:4.8")

    // AndroidX Test - JVM testing
    //noinspection GradleDependency
    testImplementation ("androidx.test:core-ktx:1.4.0")
    //testImplementation "androidx.test.ext:junit:$androidXTestExtKotlinRunnerVersion"

    // AndroidX Test - Instrumented testing //Added Item here
    androidTestImplementation ("androidx.test:runner:1.5.2")
    //noinspection GradleDependency
    androidTestImplementation ("androidx.test:rules:1.2.0")
    androidTestImplementation ("androidx.test.espresso:espresso-core:3.5.1")

    //Timber
    implementation ("com.jakewharton.timber:timber:5.0.1")

    // hamcrest
    testImplementation ("org.hamcrest:hamcrest:2.2")
    testImplementation ("org.hamcrest:hamcrest-library:2.2")
    androidTestImplementation ("org.hamcrest:hamcrest:2.2")
    androidTestImplementation ("org.hamcrest:hamcrest-library:2.2")


    // AndroidX and Robolectric
    testImplementation ("androidx.test.ext:junit-ktx:1.1.5")
    testImplementation ("androidx.test:core-ktx:1.5.0")
    testImplementation ("org.robolectric:robolectric:4.8")

    // InstantTaskExecutorRule
    testImplementation ("androidx.arch.core:core-testing:2.2.0")
    androidTestImplementation ("androidx.arch.core:core-testing:2.2.0")

    //kotlinx-coroutines
    implementation ("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
    testImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")
    androidTestImplementation ("org.jetbrains.kotlinx:kotlinx-coroutines-test:1.5.2")

    //animations
//    implementation ("androidx.mediarouter:mediarouter:1.7.0")
//    implementation ("com.android.support:support-annotations:28.0.0")
//    implementation ("com.android.car.ui:car-ui-lib:2.6.0")
    implementation ("com.airbnb.android:lottie:6.0.0")

//Room
    implementation("androidx.room:room-ktx:2.6.1")
    implementation("androidx.room:room-runtime:2.6.1")
    implementation("com.google.firebase:protolite-well-known-types:18.0.0")
    kapt("androidx.room:room-compiler:2.6.1")
//ViewModel & livedata
    implementation("androidx.lifecycle:lifecycle-extensions:2.2.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
//Coroutine
    implementation("org.jetbrains.kotlinx:kotlinx-coroutines-android:1.7.3")
//Retrofit
    implementation ("com.squareup.retrofit2:retrofit:2.9.0")
    implementation ("com.squareup.retrofit2:converter-gson:2.9.0")
//GSON
    implementation ("com.google.code.gson:gson:2.10.1")
    implementation("com.squareup.picasso:picasso:2.71828")

//location
    implementation("com.google.android.gms:play-services-location:21.2.0")

 //map
    implementation("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("joda-time:joda-time:2.10.12")

    implementation ("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation ("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation ("com.google.android.gms:play-services-maps:18.2.0")
    implementation ("com.google.android.libraries.places:places:3.4.0")

    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    implementation ("com.google.android.libraries.places:places:3.4.0")
    implementation ("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.core:core-ktx:1.12.0")
    implementation("androidx.appcompat:appcompat:1.6.1")
    implementation("com.google.android.material:material:1.11.0")
    implementation("androidx.constraintlayout:constraintlayout:2.1.4")
    implementation("androidx.lifecycle:lifecycle-livedata-ktx:2.7.0")
    implementation("androidx.lifecycle:lifecycle-viewmodel-ktx:2.7.0")
    implementation("androidx.navigation:navigation-fragment-ktx:2.7.7")
    implementation("androidx.navigation:navigation-ui-ktx:2.7.7")
    testImplementation("junit:junit:4.13.2")
    androidTestImplementation("androidx.test.ext:junit:1.1.5")
    androidTestImplementation("androidx.test.espresso:espresso-core:3.5.1")
}