plugins {
    alias(libs.plugins.androidLibrary)
    alias(libs.plugins.jetbrainsKotlinAndroid)
    alias(libs.plugins.jetbrainsKotlinKapt)
    id("maven-publish")
}

android {
    namespace = "com.shujushuo.tracking.sdk"
    compileSdk = 34

    defaultConfig {
        minSdk = 24

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
        sourceCompatibility = JavaVersion.VERSION_11 // 使用 Java 11
        targetCompatibility = JavaVersion.VERSION_11
    }

    kotlinOptions {
        jvmTarget = "11" // Kotlin JVM 目标版本
    }
}

dependencies {

    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.appcompat)
    implementation(libs.material)
    testImplementation(libs.junit)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)

    // Room 依赖
    implementation(libs.room.runtime)
    implementation(libs.room.ktx)
    kapt(libs.room.compiler)

    // Retrofit 依赖
    implementation(libs.retrofit)
    implementation(libs.converter.gson)

    // Gson 依赖
    implementation(libs.gson)

    // Kotlin Coroutines 依赖
    implementation(libs.kotlinx.coroutines.core)
    implementation(libs.kotlinx.coroutines.android)

    // WorkManager 依赖
    implementation(libs.work.runtime.ktx)

    // OAID
    implementation(libs.oaid)
}
//
afterEvaluate {
    println("Available components: ${components.names}")

    publishing {
        publications {
            create<MavenPublication>("release") {
                from(components["release"]) // 引用 'release' 组件
                groupId = "com.shujushuo.tracking" // 你的 groupId
                artifactId = "tracking-android-sdk" // 你的 artifactId
                version = "0.2" // 版本号

                pom {
                    name.set("Tracking Android SDK")
                    description.set("A tracking library for Android applications.")
                    url.set("https://github.com/shujushuo/tracking-android-sdk")
                    licenses {
                        license {
                            name.set("The Apache License, Version 2.0")
                            url.set("http://www.apache.org/licenses/LICENSE-2.0.txt")
                        }
                    }
                    developers {
                        developer {
                            id.set("shujushuo")
                            name.set("Shu Jushuo")
                            email.set("jiangzhx@gmail.com")
                        }
                    }
                    scm {
                        connection.set("scm:git:git://github.com/shujushuo/tracking-android-sdk.git")
                        developerConnection.set("scm:git:ssh://github.com/shujushuo/tracking-android-sdk.git")
                        url.set("https://github.com/shujushuo/tracking-android-sdk")
                    }
                }
            }
        }
    }

    // 确保 publishToMavenLocal 任务依赖 assembleRelease
    tasks.named("publishToMavenLocal").configure {
        dependsOn("assembleRelease")
    }
}