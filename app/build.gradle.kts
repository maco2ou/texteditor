
plugins {
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.kotlinAndroid)
}

android {
    namespace = "com.macoou.texteditor"
    compileSdk = 34

    dependenciesInfo {
        includeInApk = false
        includeInBundle = false
    }


    buildTypes {
        getByName("debug") {
            isMinifyEnabled = false
            isCrunchPngs = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
        getByName("release") {
            isMinifyEnabled = false
            isCrunchPngs = false
            proguardFiles(
                getDefaultProguardFile("proguard-android-optimize.txt"), "proguard-rules.pro"
            )
        }
    }

    defaultConfig {
        applicationId = "com.macoou.texteditor"
        minSdk = 26
        targetSdk = 34
        versionCode = 1
        versionName = "1.0"

        testInstrumentationRunner="androidx.test.runner.AndroidJUnitRunner"
    }

    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_17
        targetCompatibility = JavaVersion.VERSION_17
        isCoreLibraryDesugaringEnabled = true
    }

    buildFeatures {
        viewBinding = true
    }

    kotlinOptions {
        jvmTarget = "17"
    }
}

dependencies {
    implementation(libs.appcompat)
    implementation(libs.material)
    implementation(libs.constraintlayout)
    implementation(libs.navigation.fragment)
    implementation(libs.navigation.ui)
    implementation(libs.asynclayoutinflater)
    implementation(libs.navigation.fragment.ktx)
    implementation(libs.navigation.ui.ktx)
    implementation(libs.activity)
    implementation(libs.commons.vfs2)
    implementation(libs.jsch)
    implementation(libs.ext.junit)
    testImplementation(libs.junit.junit)
    androidTestImplementation(libs.junit.junit)
    coreLibraryDesugaring(libs.desugar.jdk.libs)
    implementation(libs.utilcode)
    implementation(project(":libsettings"))
    implementation(project(":libRunner"))
    implementation(project(":libEditor"))
    implementation(libs.sshj)
    implementation(libs.commons.net)
    implementation(libs.gson)
    implementation(libs.jcodings)
    implementation(libs.joni)
    implementation(libs.snakeyaml.engine)
    implementation(libs.jdt.annotation)
    implementation(libs.juniversalchardet)
    implementation(libs.expandable.fab)

}
