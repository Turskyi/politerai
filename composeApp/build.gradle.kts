import org.jetbrains.compose.desktop.application.dsl.TargetFormat
import java.util.Properties

plugins {
    alias(libs.plugins.kotlinMultiplatform)
    alias(libs.plugins.androidApplication)
    alias(libs.plugins.composeMultiplatform)
    alias(libs.plugins.composeCompiler)
    kotlin("plugin.serialization").version("1.9.21")
    id("com.google.gms.google-services")
    id("com.google.firebase.crashlytics")
}

val keystorePropertiesFile: File = rootProject.file("key.properties")
val keystoreProperties: Properties = Properties()
keystoreProperties.load(keystorePropertiesFile.inputStream())

kotlin {
    androidTarget {
        compilations.all {
            kotlinOptions {
                jvmTarget = "1.8"
            }
        }
    }

    jvm("desktop")

    listOf(
        iosX64(),
        iosArm64(),
        iosSimulatorArm64()
    ).forEach { iosTarget ->
        iosTarget.binaries.framework {
            baseName = "ComposeApp"
            isStatic = true
        }
    }

    sourceSets {
        val desktopMain by getting
        desktopMain.dependencies {
            implementation(compose.desktop.currentOs)
            implementation(libs.ktor.client.cio)
        }

        androidMain.dependencies {
            implementation(libs.compose.ui)
            implementation(libs.compose.ui.tooling.preview)
            implementation(libs.androidx.activity.compose)
            implementation(libs.ktor.client.android)
        }

        commonMain.dependencies {
            implementation(compose.runtime)
            implementation(compose.foundation)
            implementation(compose.material)
            implementation(compose.components.resources)
            implementation(libs.kotlinx.coroutines.core)
            implementation(libs.ktor.client.core)
            implementation(libs.ktor.client.content.negotiation)
            implementation(libs.ktor.serialization.kotlinx.json)
            implementation(libs.ktor.client.json)
        }
        iosMain.dependencies {
            implementation(libs.ktor.client.darwin)
        }
    }
}

android {
    namespace = libs.versions.applicationId.get()
    compileSdk = libs.versions.android.compileSdk.get().toInt()

    sourceSets["main"].manifest.srcFile("src/androidMain/AndroidManifest.xml")
    sourceSets["main"].res.srcDirs("src/androidMain/res")

    defaultConfig {
        applicationId = libs.versions.applicationId.get()
        minSdk = libs.versions.android.minSdk.get().toInt()
        targetSdk = libs.versions.android.targetSdk.get().toInt()
        versionCode = libs.versions.versionCode.get().toInt()
        versionName = libs.versions.versionName.get()
    }
    signingConfigs {
        register("release") {
            if (System.getenv()["FCI_BUILD_ID"] != null) { // FCI_BUILD_ID is exported by Codemagic
                storeFile =
                    System.getenv()["CM_KEYSTORE_PATH"]?.let { file(it) }
                storePassword = System.getenv()["CM_KEYSTORE_PASSWORD"]
                keyAlias =
                    keystoreProperties["SIGNING_KEY_RELEASE_KEY"] as? String
                        ?: throw IllegalStateException("keyAlias is missing or invalid")
                keyPassword = System.getenv()["CM_KEY_PASSWORD"]
            } else {
                storeFile = file(
                    path = keystoreProperties["storeFile"] as? String
                        ?: throw IllegalStateException("storeFile is missing or invalid"),
                )
                storePassword = keystoreProperties["storePassword"] as? String
                    ?: throw IllegalStateException("storePassword is missing or invalid")
                keyAlias = keystoreProperties["keyAlias"] as? String
                    ?: throw IllegalStateException(
                        "keyAlias is missing or invalid"
                    )
                keyPassword = keystoreProperties["keyPassword"] as? String
                    ?: throw IllegalStateException("keyPassword is missing or invalid")
            }
        }
        register("dev") {
            storeFile = file(
                path = keystoreProperties["SIGNING_KEY_DEBUG_PATH"] as? String
                    ?: throw IllegalStateException(
                        "SIGNING_KEY_DEBUG_PATH for storeFile is missing or invalid",
                    ),
            )
            storePassword =
                keystoreProperties["SIGNING_KEY_DEBUG_PASSWORD"] as? String
                    ?: throw IllegalStateException("storePassword is missing or invalid")
            keyAlias = keystoreProperties["SIGNING_KEY_DEBUG_KEY"] as? String
                ?: throw IllegalStateException(
                    "keyAlias is missing or invalid"
                )
            keyPassword =
                keystoreProperties["SIGNING_KEY_DEBUG_KEY_PASSWORD"] as? String
                    ?: throw IllegalStateException("keyPassword is missing or invalid")
        }
        register("production") {
            storeFile = file(
                path = keystoreProperties["SIGNING_KEY_RELEASE_PATH"] as? String
                    ?: throw IllegalStateException(
                        "SIGNING_KEY_RELEASE_PATH for storeFile is missing or invalid",
                    ),
            )
            storePassword =
                keystoreProperties["SIGNING_KEY_RELEASE_PASSWORD"] as? String
                    ?: throw IllegalStateException("storePassword is missing or invalid")
            keyAlias = keystoreProperties["SIGNING_KEY_RELEASE_KEY"] as? String
                ?: throw IllegalStateException("keyAlias is missing or invalid")
            keyPassword =
                keystoreProperties["SIGNING_KEY_RELEASE_KEY_PASSWORD"] as? String
                    ?: throw IllegalStateException("keyPassword is missing or invalid")
        }
    }

    buildFeatures {
        compose = true
    }
    composeOptions {
        kotlinCompilerExtensionVersion = libs.versions.compose.compiler.get()
    }
    packaging {
        resources {
            excludes += "/META-INF/{AL2.0,LGPL2.1}"
        }
    }
    buildTypes {
        getByName("release") {
            isMinifyEnabled = false
            signingConfig =
                if (System.getenv()["CI"] == "true") { // CI=true is exported by Codemagic
                    signingConfigs.getByName("release")
                } else {
                    signingConfigs.getByName("production")
                }
        }
        getByName("debug") {
            signingConfig = signingConfigs.getByName("dev")
        }
    }
    compileOptions {
        sourceCompatibility = JavaVersion.VERSION_1_8
        targetCompatibility = JavaVersion.VERSION_1_8
    }
    dependencies {
        debugImplementation(libs.compose.ui.tooling)
    }
}
dependencies {
    implementation(libs.androidx.core.ktx)
    implementation(libs.androidx.core)
    implementation(libs.firebase.crashlytics)
    implementation(libs.firebase.analytics)
}

compose.desktop {
    application {
        mainClass = "MainKt"

        nativeDistributions {
            targetFormats(TargetFormat.Dmg, TargetFormat.Msi, TargetFormat.Deb)
            packageName = libs.versions.dockName.get()
            packageVersion = libs.versions.versionName.get()

            macOS {
                iconFile.set(project.file("../composeApp/src/desktopMain/icons/icon.icns"))
                bundleID = libs.versions.applicationId.get()
                dockName = libs.versions.dockName.get()
            }
            windows {
                iconFile.set(project.file("../composeApp/src/desktopMain/icons/icon.ico"))
            }
            linux {
                iconFile.set(project.file("../composeApp/src/desktopMain/icons/icon.png"))
            }
        }
    }
}
