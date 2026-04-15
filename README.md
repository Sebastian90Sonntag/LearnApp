# 📚 LearnApp

Eine Android-Lernplattform, entwickelt als Einwochenprojekt im Rahmen von **Extreme Programming** bei [GraphicDesignCoding](https://github.com/graphicdesigncoding).

## Über das Projekt

Die LearnApp wurde als kollaboratives Projekt konzipiert, um Lerninhalte mobil zugänglich zu machen. Das Projekt entstand in einer intensiven Entwicklungswoche mit Fokus auf agile Methoden und Teamarbeit.

## Features

- 📖 Lernmodule mit strukturierten Inhalten
- 🧭 Fragment-basierte Navigation
- 🔐 Verschlüsselte Datenhaltung (AndroidX Security Crypto)
- 📊 Grid-basiertes Layout für Übersichtlichkeit
- 🔥 Firebase Crashlytics Integration

## Tech Stack

| Technologie | Details |
|-------------|---------|
| **Sprache** | Java |
| **Plattform** | Android (API 29+) |
| **Build** | Gradle |
| **UI** | Material Design, ViewBinding, ConstraintLayout, GridLayout |
| **Navigation** | AndroidX Navigation Component |
| **Sicherheit** | AndroidX Security Crypto |
| **Monitoring** | Firebase Crashlytics |

## Projektstruktur

```
LearnApp/
├── app/              # Hauptanwendung
├── mylibrary/        # Wiederverwendbare Bibliothek
├── build.gradle
├── settings.gradle
└── LICENSE.txt
```

## Schnellstart

1. Projekt in **Android Studio** öffnen
2. Gradle Sync abwarten
3. Auf Emulator oder physischem Gerät starten (min. API 29)

```bash
./gradlew assembleDebug
```

## Team

Entwickelt von **Sebastian**, **Alexander** und **Ikram** im Rahmen von GraphicDesignCoding.

## Lizenz

Siehe [LICENSE.txt](LICENSE.txt)
