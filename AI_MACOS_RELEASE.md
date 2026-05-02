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

### 4. Entitlements Alignment

The `com.apple.application-identifier` in the signed binary **must** match the
provisioning profile exactly (e.g., `TEAMID.bundle.id`). Mismatches result in
rejection.
**Action**: Keep `entitlements.plist` simple or aligned with the profile's
expected values.

### 5. Manual Provisioning Profile Embedding

Compose Multiplatform might not always embed the profile correctly for the App
Store's subcomponent check.
**Action**: Manually copy the profile to
`Politer AI.app/Contents/embedded.provisionprofile` and sign it before signing
the main bundle.

## Step-by-Step Execution Command

```bash
# 1. Clean quarantine
xattr -r -d com.apple.quarantine .

# 2. Build via Gradle
./gradlew :composeApp:packageReleasePkg

# 3. Manual Fixes & Signing (If Gradle defaults fail)
# (Align Info.plist)
/usr/libexec/PlistBuddy -c "Set :LSMinimumSystemVersion 12.0" "composeApp/build/compose/binaries/main-release/app/Politer AI.app/Contents/Info.plist"

# (Manual Embed & Sign)
cp composeApp/src/desktopMain/entitlements/app.provisionprofile "composeApp/build/compose/binaries/main-release/app/Politer AI.app/Contents/embedded.provisionprofile"
codesign -s "3rd Party Mac Developer Application: DMYTRO TURSKYI (26QZ8BPZFL)" -vvvv --timestamp --options runtime --prefix com.turskyi. --force "composeApp/build/compose/binaries/main-release/app/Politer AI.app/Contents/embedded.provisionprofile"
codesign -s "3rd Party Mac Developer Application: DMYTRO TURSKYI (26QZ8BPZFL)" -vvvv --timestamp --options runtime --prefix com.turskyi. --entitlements composeApp/src/desktopMain/entitlements/entitlements.plist --force --deep "composeApp/build/compose/binaries/main-release/app/Politer AI.app"

# (Re-package)
productbuild --component "composeApp/build/compose/binaries/main-release/app/Politer AI.app" /Applications --sign "3rd Party Mac Developer Installer: DMYTRO TURSKYI (26QZ8BPZFL)" "composeApp/build/compose/binaries/main-release/pkg/Politer AI-manual.pkg"

# 4. Upload
xcrun altool --upload-app -f "composeApp/build/compose/binaries/main-release/pkg/Politer AI-manual.pkg" -t macos -u "dmytro.turskyi@gmail.com" -p "bhse-sbex-dofp-aqfy" --provider-public-id "48e54131-a786-4846-a40d-b5dacfbf27b2"
```
