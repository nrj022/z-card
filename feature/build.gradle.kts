plugins {
    alias(libs.plugins.android.library)
    alias(libs.plugins.kotlin.android)
    alias(libs.plugins.kotlin.compose)
    alias(libs.plugins.ksp)
    alias(libs.plugins.hilt)
    alias(libs.plugins.kotlin.serialization)
    alias(libs.plugins.roborazzi)
}

android {
    namespace = "com.zcard.feature"
    compileSdk = 36

    defaultConfig {
        minSdk = 26

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
        sourceCompatibility = JavaVersion.VERSION_11
        targetCompatibility = JavaVersion.VERSION_11
    }
    kotlinOptions {
        jvmTarget = "11"
    }
    buildFeatures {
        compose = true
        viewBinding = true
    }
    testOptions {
        unitTests {
            isIncludeAndroidResources = true
        }
    }
}

// 스크린샷 테스트는 Roborazzi 태스크(record/compare/verify) 또는 roborazzi.test.* 속성 지정 시에만 실행
val runScreenshotTests = gradle.startParameter.taskNames.any { it.contains("Roborazzi", ignoreCase = true) } ||
    providers.gradlePropertiesPrefixedBy("roborazzi.test").get().isNotEmpty()

tasks.withType<Test>().configureEach {
    if (!runScreenshotTests) {
        exclude("**/screenshot/**")
    }
}

dependencies {

    implementation(group="", name = "unityLibrary-release", ext = "aar")
    implementation(project(":core:designsystem"))
    implementation(project(":core:domain"))

    // Color Picker
    implementation(libs.nrj022.compose.color.picker)

    // Coil for gif
    implementation(libs.coil.compose)
    implementation(libs.coil.gif)

    // Json
    implementation(libs.kotlinx.serialization.json)

    // Hilt
    implementation(libs.google.hilt.android)
    implementation(libs.androidx.activity)
    ksp(libs.google.hilt.compiler)
    implementation(libs.androidx.hilt.navigation.compose)

    implementation(libs.androidx.cardview)
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.lifecycle.runtime.ktx)
    implementation(libs.androidx.fragment.fragment.ktx)
    implementation(libs.androidx.activity.compose)
    implementation(platform(libs.androidx.compose.bom))
    implementation(libs.androidx.compose.material.icons.extended)
    implementation(libs.androidx.compose.ui)
    implementation(libs.androidx.compose.ui.graphics)
    implementation(libs.androidx.compose.ui.tooling.preview)
    implementation(libs.androidx.compose.material3)
    implementation(libs.material)
    implementation(libs.androidx.appcompat)
    implementation(libs.androidx.constraintlayout)
    implementation(libs.androidx.navigation.ui.ktx)
    testImplementation(libs.junit)
    testImplementation(libs.kotlinx.coroutines.test) // Coroutine 테스트
    testImplementation(libs.mockk) // Repository mocking
    testImplementation(libs.turbine) // Flow 테스트
    testImplementation(kotlin("test"))
    testImplementation(libs.robolectric) // 스크린샷 테스트
    testImplementation(libs.roborazzi)
    testImplementation(platform(libs.androidx.compose.bom))
    testImplementation(libs.androidx.compose.ui.test.junit4)
    androidTestImplementation(libs.androidx.junit)
    androidTestImplementation(libs.androidx.espresso.core)
    androidTestImplementation(platform(libs.androidx.compose.bom))
    androidTestImplementation(libs.androidx.compose.ui.test.junit4)
    debugImplementation(libs.androidx.compose.ui.tooling)
    debugImplementation(libs.androidx.compose.ui.test.manifest)
}