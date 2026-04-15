# LearnApp

![Android CI](https://github.com/Sebastian90Sonntag/LearnApp/actions/workflows/android.yml/badge.svg)

A collaborative Android learning platform built with Java and Firebase.

> Developed in a one-week Extreme Programming sprint.

## Features

- User management with score tracking and ranking
- Custom list adapters for user display
- Firebase Crashlytics integration
- Secure storage with AndroidX Security Crypto

## Tech Stack

| Component | Detail |
|-----------|--------|
| Language | Java |
| Build System | Gradle (Groovy) |
| Min SDK | 29 (Android 10) |
| Target SDK | 32 |
| Backend | Firebase |

## Getting Started

```bash
git clone https://github.com/Sebastian90Sonntag/LearnApp.git
```

1. Add your `google-services.json` to `app/`
2. Open in Android Studio
3. Sync Gradle and run

## Tests

```bash
./gradlew test
```

Unit tests cover the `User` model (comparison, getters/setters).

## CI

GitHub Actions runs **build** and **lint** checks on every push and PR to `main`.

## Team

- Sebastian Sonntag
- Alexander
- Ikram

## License

See [LICENSE.txt](LICENSE.txt)
