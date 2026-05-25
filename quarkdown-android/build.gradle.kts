plugins {
    id("com.android.application")
}

android {
    namespace = "com.quarkdown.android"
    compileSdk = 34

    defaultConfig {
        applicationId = "com.quarkdown.android"
        minSdk = 23
        targetSdk = 34
        versionCode = 1
        versionName = rootProject.version.toString()
    }
}
