plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "appuni.explore.ar"
    compileSdk = 36

    defaultConfig {
        minSdk = 24
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
}

dependencies {
    implementation(project(":domain"))
    implementation(project(":coordinates"))
    implementation(libs.sceneview)
    implementation(libs.activity)
}
