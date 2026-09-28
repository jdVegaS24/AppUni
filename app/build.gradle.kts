plugins {
    alias(libs.plugins.android.application)
}

android {
    namespace = "appuni.explore"
    compileSdk = 36

    defaultConfig {
        applicationId = "appuni.explore"
        minSdk = 24
        targetSdk = 36
        versionCode = 1
        versionName = "1.0"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
    }

    sourceSets {
        getByName("main") {
            java.srcDir("src/main/java")
        }
        getByName("test") {
            java.srcDir("src/test/java")
        }
    }

    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":data"))
    implementation(project(":ar"))
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.activity)
}
