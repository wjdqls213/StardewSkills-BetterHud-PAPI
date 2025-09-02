# StardewSkills (BetterHud + PAPI)

This repo builds `stardewskills-0.3.0-papi.jar` and publishes it as a GitHub Actions artifact.

## Local build (no installs needed)
Windows:
```
gradlew.bat build
```

macOS/Linux:
```
chmod +x gradlew
./gradlew build
```

Output:
```
build/libs/stardewskills-0.3.0-papi.jar
```

## GitHub Actions
- Push this repo to GitHub
- Actions will run automatically and attach the built JAR as an artifact under the workflow run
