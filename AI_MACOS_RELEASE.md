# macOS App Store Release Guide (AI Agent Focused)

This guide provides the technical nuances required for an AI agent to
successfully release the macOS version of this Compose Multiplatform app to the
Mac App Store.

## Prerequisites & Credentials

To execute the release, you need access to the following (ask the user if not
found):

1. **3rd Party Mac Developer Application Certificate**: For signing the `.app`
   bundle.
2. **3rd Party Mac Developer Installer Certificate**: For signing the `.pkg`
   package.
3. **Mac App Store Provisioning Profile**: For `com.turskyi.politerai`.
4. **App-Specific Password**: For uploading via `altool`.
5. **Apple ID**: `dmytro.turskyi@gmail.com`.
6. **Provider ID**: `48e54131-a786-4846-a40d-b5dacfbf27b2`.

## Critical Nuances

### 1. Quarantine Attributes

Apple strictly rejects packages containing files with the `com.apple.quarantine`
extended attribute.
**Action**: Run `xattr -r -d com.apple.quarantine .` before packaging.

### 2. Mandatory Icon Size

The Mac App Store requires a 1024x1024 (512pt @2x) icon in the `.icns` file.
**Current Fix**: We generated this from the Android `mipmap-xxxhdpi` asset using
`sips` and `iconutil`.

### 3. arm64 Support & Deployment Target

To support only `arm64` (Apple Silicon) without Intel (`x86_64`), the
`LSMinimumSystemVersion` in `Info.plist` **must** be set to `12.0` or higher.

### 4. Entitlements Alignment (CRITICAL)

The `com.apple.application-identifier` in the signed binary **must** match the
provisioning profile exactly for TestFlight eligibility.
**Manual Submission Fix**: If the profile and App ID mismatch, remove
`com.apple.application-identifier` from `entitlements.plist` to allow successful
upload, though this may impact TestFlight.

### 5. Manual Provisioning Profile Embedding

Compose Multiplatform might not always embed the profile correctly for the App
Store's subcomponent check.
**Action**: Manually copy the profile to
`Politer AI.app/Contents/embedded.provisionprofile` and sign it before signing
the main bundle.

## Step-by-Step Execution Command

```bash
# 0. Confirm the version you intend to ship
# (Source of truth for versionName in this repo)
grep -n 'versionName' gradle/libs.versions.toml

# 1. Hard-clean build outputs to avoid re-packaging an older .app/.pkg
./gradlew :composeApp:clean
rm -rf composeApp/build/compose/binaries/main-release

# 2. Clean quarantine (Apple rejects archives with quarantine xattrs)
xattr -r -d com.apple.quarantine .

# 3. Build via Gradle
# NOTE: This task may fail while trying to codesign the embedded app.provisionprofile.
# If it fails, you can still use the partially-produced .app bundle in the next steps.
./gradlew :composeApp:packageReleasePkg || true

# 3. Manual Fixes & Signing

# (Extract variables for readability)
APP_NAME="Politer AI"
APP_BUNDLE="composeApp/build/compose/binaries/main-release/app/${APP_NAME}.app"
IDENTITY="3rd Party Mac Developer Application: DMYTRO TURSKYI (26QZ8BPZFL)"
IDENTITY_INSTALLER="3rd Party Mac Developer Installer: DMYTRO TURSKYI (26QZ8BPZFL)"
ENTITLEMENTS="composeApp/src/desktopMain/entitlements/entitlements.plist"
CHILD_ENTITLEMENTS="composeApp/src/desktopMain/entitlements/child-entitlements.plist"
PROVISIONING_PROFILE="composeApp/src/desktopMain/entitlements/app.provisionprofile"

# (Preflight: verify you are not about to ship a stale build)
/usr/libexec/PlistBuddy -c "Print :CFBundleShortVersionString" "${APP_BUNDLE}/Contents/Info.plist"
/usr/libexec/PlistBuddy -c "Print :CFBundleVersion" "${APP_BUNDLE}/Contents/Info.plist"

# (Preflight: ensure LSApplicationCategoryType is valid; "Unknown" is rejected by App Store Connect)
/usr/libexec/PlistBuddy -c "Print :LSApplicationCategoryType" "${APP_BUNDLE}/Contents/Info.plist" || true

# (Remove problematic subcomponent from the Compose build)
# IMPORTANT: if you re-package without cleaning, you can accidentally upload an older version.
rm -f "${APP_BUNDLE}/Contents/app.provisionprofile"

# (Align Info.plist)
/usr/libexec/PlistBuddy -c "Set :LSMinimumSystemVersion 12.0" "${APP_BUNDLE}/Contents/Info.plist"
# ITSAppUsesNonExemptEncryption should already be set by Gradle, but double check
/usr/libexec/PlistBuddy -c "Delete :ITSAppUsesNonExemptEncryption" "${APP_BUNDLE}/Contents/Info.plist" || true
/usr/libexec/PlistBuddy -c "Add :ITSAppUsesNonExemptEncryption bool false" "${APP_BUNDLE}/Contents/Info.plist"

# (Manual Embed Provisioning Profile)
cp "${PROVISIONING_PROFILE}" "${APP_BUNDLE}/Contents/embedded.provisionprofile"

# (Deep Signing Fix: Sign all `dylibs` and executables manually)
# 1. Sign app-native libraries (Compose/Skiko lives here)
find "${APP_BUNDLE}/Contents/app" -type f \( -name "*.dylib" -o -name "*.so" -o -name "*.jnilib" \) -exec codesign -s "${IDENTITY}" -vvvv --timestamp --options runtime --force {} \;

# 2. Sign JRE libraries
find "${APP_BUNDLE}/Contents/runtime" -type f \( -name "*.dylib" -o -name "*.so" \) -exec codesign -s "${IDENTITY}" -vvvv --timestamp --options runtime --force {} \;

# 3. Sign `jspawnhelper` with child entitlements (inheriting sandbox)
JSPAWNHELPER="${APP_BUNDLE}/Contents/runtime/Contents/Home/lib/jspawnhelper"
if [ -f "${JSPAWNHELPER}" ]; then
    codesign -s "${IDENTITY}" -vvvv --timestamp --options runtime --entitlements "${CHILD_ENTITLEMENTS}" --force "${JSPAWNHELPER}"
fi

# 4. Sign the app bundle itself (must be last)
codesign -s "${IDENTITY}" -vvvv --timestamp --options runtime --entitlements "${ENTITLEMENTS}" --force "${APP_BUNDLE}"

# (Preflight: ensure App Store signature checks will pass)
codesign --verify --deep --strict --verbose=4 "${APP_BUNDLE}"

# (Re-package)
mkdir -p "composeApp/build/compose/binaries/main-release/pkg"
OUTPKG="composeApp/build/compose/binaries/main-release/pkg/PoliterAI-$(/usr/libexec/PlistBuddy -c 'Print :CFBundleShortVersionString' "${APP_BUNDLE}/Contents/Info.plist")-manual.pkg"
productbuild --component "${APP_BUNDLE}" /Applications --sign "${IDENTITY_INSTALLER}" "${OUTPKG}"

# (Preflight: verify pkg embeds the expected version before upload)
tmpdir="$(mktemp -d)"
pkgutil --expand "${OUTPKG}" "${tmpdir}/pkg"
sed -n '1,40p' "${tmpdir}/pkg/Distribution"

# 4. Upload
xcrun altool --upload-app -f "${OUTPKG}" -t macos -u "dmytro.turskyi@gmail.com" -p "bhse-sbex-dofp-aqfy" --provider-public-id "48e54131-a786-4846-a40d-b5dacfbf27b2"
```
