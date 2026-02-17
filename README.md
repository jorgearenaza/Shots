# EspressoShots (rebuild)

Minimal clean rebuild of the app `EspressoShots` using:
- Kotlin + Jetpack Compose + Material3
- Room for persistence
- DataStore for settings
- Navigation Compose
- MVVM (ViewModel + Repository + DAO)

Quick local steps (Android Studio Panda 1 | 2025.3.1 Patch 1):

1. Open the project in Android Studio.
2. Let Gradle sync/download dependencies.
3. Build / Run `app` (Run > app) or from terminal:

```bash
chmod +x ./gradlew
./gradlew assembleDebug
```

Create branch and PR (example):

```bash
git checkout -b rebuild/espresso-shots
git add .
git commit -m "chore(rebuild): initial EspressoShots clean implementation"
git push -u origin rebuild/espresso-shots
# then create PR via GitHub UI or CLI:
gh pr create --title "Rebuild: EspressoShots" --body "MVVM + Compose + Room + DataStore minimal implementation" --base master
```

Notes:
- DI is implemented via a simple `ServiceLocator` to keep setup minimal. I can convert to Hilt on request.
- If you hit SDK/JDK issues, ensure Android Studio uses JDK 17+ and SDK Platform 36 installed.
