# Compose Desktop (release) ProGuard rules
#
# TestFlight/App Store builds are produced from the "release" desktop distribution, which runs
# ProGuard. Ktor uses ServiceLoader providers for Kotlinx Serialization; if the provider class is
# stripped, the app fails on launch with:
#   io.ktor.serialization.kotlinx.KotlinxSerializationExtensionProvider ... not found
#
# Keep Kotlinx Serialization provider implementations and related JSON support.

-keep class io.ktor.serialization.kotlinx.** { *; }
-keep class io.ktor.serialization.kotlinx.json.** { *; }

# Ktor may reflectively access some bits (warnings seen during ProGuard run).
-dontwarn io.ktor.**

