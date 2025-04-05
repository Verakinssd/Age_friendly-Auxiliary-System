plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "com.example.myapplication"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.example.myapplication"
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
}

dependencies {
    implementation("com.github.arimorty:floatingsearchview:2.1.1")
    implementation("org.apache.commons:commons-text:1.10.0")
    implementation("com.android.support:recyclerview-v7:24.2.1")
    implementation ("com.github.bumptech.glide:glide:4.15.1")  // Glide 依赖
    annotationProcessor ("com.github.bumptech.glide:compiler:4.15.1")  // Glide 注解处理器
//    implementation("io.minio:minio:8.0.0")
//    implementation("io.jsonwebtoken:jjwt:0.12.6")
//    implementation("com.google.guava:guava:32.0.1-jre")
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
    implementation(libs.constraintlayout)
    implementation("cn.bigmodel.openapi:oapi-java-sdk:release-V4-2.3.0")
    //implementation (files ("listenablefuture-1.0.jar"))
    implementation(files("libs/litepal-2.0.0-src.jar"))
    testImplementation(libs.junit)
    androidTestImplementation(libs.ext.junit)
    androidTestImplementation(libs.espresso.core)
}