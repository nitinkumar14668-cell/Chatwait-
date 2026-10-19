# GitHub Actions - Signed Android APK & AAB Build Guide

Bhai, aapke Android project ke liye **GitHub Actions CI/CD Workflow** setup kar diya gaya hai (`.github/workflows/build-apk.yml`). 

Is workflow ki madad se aap **1-click me Signed Release APK aur AAB (Google Play Store ke liye)** build kar sakte hain.

---

## 🚀 1. GitHub Par Action Kaise Chalayein?

### Option A: Automatic Build on Push
Jab bhi aap GitHub par code push karenge (`git push origin main`), GitHub Actions **automatically start** ho jayega aur signed APK build kar dega!

### Option B: Manual 1-Click Trigger (Workflow Dispatch)
Agar aapko manually build trigger karna hai:
1. Apne GitHub Repository me jayein.
2. Upar **"Actions"** tab par click karein.
3. Left sidebar me **"Build and Sign Android APK"** select karein.
4. Right side me **"Run workflow"** button par click karein:
   - **Build Type**: `release`, `debug`, ya `both` select karein.
   - **Build AAB**: Check karein agar Play Store ke liye `.aab` file chahiye.
5. Click **"Run workflow"**.

---

## 📥 2. Signed APK Kaise Download Karein?

1. GitHub ke **Actions** tab me jayein.
2. Running ya completed workflow run par click karein (e.g. `Build and Sign Android APK`).
3. Page ke bilkul neeche scroll karein **Artifacts** section me:
   - **`ChatWait-Signed-Release-APK`**: Aapka signed, ready-to-install Android Release APK!
   - **`ChatWait-Debug-APK`**: Testing ke liye debug APK.
   - **`ChatWait-Release-AAB`**: Google Play Store upload ke liye Android App Bundle.
4. Click karke download karein aur apne Android phone me install karein!

---

## 🔐 3. Custom Production Keystore (Optional)

Workflow me **Smart Fallback** laga hua hai: Agar aapne koi secret set nahi kiya hai, tab bhi workflow automatically ek valid release keystore generate karke APK ko sign kar deta hai (build kabhi fail nahi hoga!).

Lekin agar aapko apna **official personal keystore** use karna hai:

### Step 1: Apne PC par Keystore generate karein:
```bash
keytool -genkeypair -v -keystore my-upload-key.jks -alias upload -keyalg RSA -keysize 2048 -validity 10000
```

### Step 2: Keystore ko Base64 me convert karein:
- **Linux/Mac**:
  ```bash
  base64 -w 0 my-upload-key.jks > keystore_base64.txt
  ```
- **Windows (PowerShell)**:
  ```powershell
  [Convert]::ToBase64String([IO.File]::ReadAllBytes("my-upload-key.jks")) | Out-File -Encoding ASCII keystore_base64.txt
  ```

### Step 3: GitHub Secrets me add karein:
1. GitHub repo me jayein $\rightarrow$ **Settings** $\rightarrow$ **Secrets and variables** $\rightarrow$ **Actions**.
2. **New repository secret** par click karein aur ye add karein:
   - `KEYSTORE_BASE64`: `keystore_base64.txt` ka pura text
   - `STORE_PASSWORD`: Aapka keystore password
   - `KEY_PASSWORD`: Aapka key password
   - `KEY_ALIAS`: `upload` (ya jo alias aapne set kiya tha)

---

## 🏷️ 4. GitHub Release Automatic Creation (Tags)
Jab aap GitHub par version tag push karenge:
```bash
git tag v1.0.0
git push origin v1.0.0
```
Toh GitHub Actions automatically ek **GitHub Release** create karega aur usme direct downloadable `.apk` aur `.aab` files attach kar dega!
