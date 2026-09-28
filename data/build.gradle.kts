plugins {
    alias(libs.plugins.android.library)
}

android {
    namespace = "appuni.explore.data"
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
    testImplementation(libs.junit)
    testImplementation("org.json:json:20240303")
}

tasks.withType<Test>().configureEach {
    doFirst {
        val jsonJar = classpath.files.firstOrNull { file -> file.name.startsWith("json-") }
        if (jsonJar != null) {
            classpath = files(jsonJar) + classpath
        }
    }
}
