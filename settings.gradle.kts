pluginManagement {
    repositories {
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/central") }
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }
        google {}
        mavenCentral()
        gradlePluginPortal()
    }
    resolutionStrategy {
        eachPlugin {
            if (requested.id.id == "com.github.aasitnikov.fat-aar") {
                useModule("com.github.aasitnikov:fat-aar-android:${requested.version}")
            }
        }
    }
}
dependencyResolutionManagement {
    repositoriesMode.set(RepositoriesMode.FAIL_ON_PROJECT_REPOS)
    repositories {
        maven { setUrl("https://jitpack.io") }
        maven { setUrl("https://developer.huawei.com/repo") }
        maven { setUrl("https://developer.hihonor.com/repo") }
        maven { setUrl("https://maven.aliyun.com/repository/public") }
        maven { setUrl("https://maven.aliyun.com/repository/central") }
        maven { setUrl("https://maven.aliyun.com/repository/gradle-plugin") }
        google()
        mavenCentral()
    }
}

rootProject.name = "TrackingAndroidSdk"
include(":app", ":sdk")