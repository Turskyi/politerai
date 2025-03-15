[![Stand With Ukraine](https://raw.githubusercontent.com/vshymanskyy/StandWithUkraine/main/banner-direct-single.svg)](https://stand-with-ukraine.pp.ua)
[![Upload to Firebase App Distribution](https://github.com/Turskyi/politerai/actions/workflows/compose_ci.yml/badge.svg)](https://github.com/Turskyi/politerai/actions/workflows/compose_ci.yml)
[![Codemagic build status](https://api.codemagic.io/apps/656e850f56b6e401cb0544dc/android-kmm-workflow/status_badge.svg)](https://codemagic.io/apps/656e850f56b6e401cb0544dc/android-kmm-workflow/latest_build)
![GitHub release (latest by date)](https://img.shields.io/github/v/release/Turskyi/politerai)
<img alt="GitHub commit activity" src="https://img.shields.io/github/commit-activity/m/Turskyi/politerai">

# Politer AI

This is a Kotlin Multiplatform project focusing on Android, iOS, and Desktop.
**Kotlin Multiplatform** is a broad technology that enables cross-platform
development with Kotlin.

## PROJECT SPECIFICATION

• Programming language: [Kotlin](https://kotlinlang.org/);

•
Framework: [Compose Multiplatform](https://www.jetbrains.com/lp/compose-multiplatform/);

• SDK: [Kotlin Multiplatform](https://kotlinlang.org/docs/multiplatform.html);

• Interface: [Compose](https://developer.android.com/jetpack/compose);

• Version control system: [Git](https://git-scm.com);

• Git Hosting Service: [GitHub](https://github.com);

• Operating system: [Android](https://www.android.com/);

• HTTP client: [Ktor](https://ktor.io);

• Reactive
programming: [Coroutines](https://developer.android.com/kotlin/coroutines);

• CI/CD: [GitHub Actions](https://docs.github.com/en/actions) is used to deliver
new Android
Package (APK)
to [Firebase App Distribution](https://firebase.google.com/docs/app-distribution)
after every push to the **dev** branch, [Codemagic](https://codemagic.io/start/)
is used to deliver
new release app bundle to **Google Play** after every push to **master** branch;

• App testing platforms:
[Firebase App Distribution](https://appdistribution.firebase.dev/i/353f302e0032e469);

• App store:
[Google Play](https://play.google.com/store/apps/details?id=com.turskyi.politerai);

• Architectural pattern:
[Monolith](https://learn.microsoft.com/en-us/dotnet/architecture/modern-web-apps-azure/common-web-application-architectures#all-in-one-applications);

### Folder structure

* `/composeApp` is for code shared across Compose Multiplatform applications.
  It has several folders:
    - `commonMain` is for code that is common for all targets.
  - Other folders are for Kotlin code that will be compiled for only the
    platform indicated in the
      folder name.
* `/iosApp` has iOS applications.

**Compose Multiplatform** is a focused library that enables cross-platform UI
development with
Jetpack Compose.

• **Code Readability:** code is easily readable with no unnecessary blank
lines, no unused variables or methods, and no commented-out code, all
variables, methods, and resource IDs are descriptively named such that another
developer reading the code can easily understand their function.

• Screenshots:

<!--suppress CheckImageSize -->
<img src="screenshots/PixelTablet2023.png" width="800"  alt="screenshot of the home page">
<img src="screenshots/PixelXL2023.png" width="200"  alt="screenshot">

## Credits

This project is based on the
[Get started with Compose Multiplatform –
tutorial](https://www.jetbrains.com/help/kotlin-multiplatform-dev/compose-multiplatform-getting-started.html)
by [JetBrains](https://github.com/JetBrains).