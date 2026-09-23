# LearnApp 📱🚀

![Android CI](https://github.com/Sebastian90Sonntag/LearnApp/actions/workflows/android.yml/badge.svg)

**LearnApp** is a modern Android learning application built with **Java**, implementing clean **MVVM (Model-View-ViewModel) Architecture**, **LiveData**, **ViewBinding**, and secure **JWT (JSON Web Token)** REST API backend communication.

---

## 🌟 Key Features

- **MVVM Architecture**: Clear separation of concerns between UI (`Fragment`), Presentation (`ViewModel`), and Data Layer (`Repository` & `SessionManager`).
- **JWT & PBKDF2 Security**: `Authorization: Bearer <jwt_token>` header authentication with server-side PBKDF2 password hashing and per-user salting.
- **Full User Auth Cycle**: Login, Registration, and Password Recovery / Reset flows.
- **Interactive Quiz Module**: Real-time quiz question retrieval, answer evaluation, and status/rating submission.
- **Leaderboard / Scoreboard**: User ranking list sorted dynamically by performance.
- **Profile & Avatar Upload**: Profile management with base64 image encoding and avatar image upload to backend static storage.
- **Built-in REST Server**: Zero-dependency, lightweight Node.js REST server (`server/server.js`).

---

## 🏗️ Architecture Overview

```
com.graphicdesigncoding.learnapp/
├── api/                # Network & Session (CallAPI, SessionManager, ApiConfig, Crypt)
├── repository/         # Data Repositories (AuthRepository, QuizRepository, ScoreboardRepository, ProfileRepository, Resource)
├── viewmodel/          # ViewModels (LoginViewModel, RegisterViewModel, QuizViewModel, ScoreboardViewModel, ProfileViewModel, RecoverViewModel)
├── forms/ (View)       # UI Fragments (LoginForm, RegisterForm, QuizForm, ScoreboardForm, ProfileForm, RecoverForm)
├── user/               # Data Models (User, UserItem)
└── MainActivity.java   # NavHostFragment host & extended toolbar control
```

---

## 🔌 API & Backend Compatibility Matrix

All REST API endpoints have been verified for **100% compatibility** between the Android App (`com.graphicdesigncoding.learnapp`) and the Node.js REST API server (`server/server.js`).

| Endpoint                        | Method       | Auth Required  | App Handler                             | Server Response                          | Status          |
|---------------------------------|--------------|----------------|-----------------------------------------|------------------------------------------|-----------------|
| `/api/v1/auth/login`            | `POST`       | Public         | `AuthRepository.login()`                | `200 OK { token, user }`                 | ✅ 100% Verified |
| `/api/v1/auth/register`         | `POST`       | Public         | `AuthRepository.register()`             | `201 Created { token, user }`            | ✅ 100% Verified |
| `/api/v1/auth/forgot-password`  | `POST`       | Public         | `AuthRepository.requestPasswordReset()` | `200 OK { message }`                     | ✅ 100% Verified |
| `/api/v1/auth/reset-password`   | `POST`       | Public         | `AuthRepository.confirmPasswordReset()` | `200 OK { message }`                     | ✅ 100% Verified |
| `/api/v1/quiz/question`         | `GET`/`POST` | **Bearer JWT** | `QuizRepository.fetchCurrentQuestion()` | `200 OK { questionId, title, answer }`   | ✅ 100% Verified |
| `/api/v1/quiz/question`         | `POST`       | **Bearer JWT** | `QuizRepository.submitAnswerRating()`  | `200 OK { message }`                     | ✅ 100% Verified |
| `/api/v1/scoreboard`            | `GET`/`POST` | **Bearer JWT** | `ScoreboardRepository.fetchScoreboard()`| `200 OK { data: [...] }`                 | ✅ 100% Verified |
| `/api/v1/profile`               | `GET`        | **Bearer JWT** | `ProfileRepository.fetchProfile()`      | `200 OK { username, email, image_link }` | ✅ 100% Verified |
| `/api/v1/profile/avatar`        | `POST`       | **Bearer JWT** | `ProfileRepository.uploadAvatar()`      | `200 OK { image_link }`                  | ✅ 100% Verified |

---

## 🚀 Getting Started

### Prerequisites

- **Android Studio** (Ladybug / Jellyfish or newer)
- **JDK 17+**
- **Node.js** (for running backend server)

### Running the REST Backend Server

```bash
# Start the backend server (listens on http://0.0.0.0:8080)
node server/server.js
```

### Running the Android Application

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Sebastian90Sonntag/LearnApp.git
   cd LearnApp
   ```

2. **Configure Host IP**:
   Edit `app/src/main/java/com/graphicdesigncoding/learnapp/api/ApiConfig.java`:
   - **Android Emulator**: `public static String BASE_URL = "http://10.0.2.2:8080";`
   - **Physical Device**: Change `10.0.2.2` to your machine's LAN IP address (e.g. `http://192.168.1.100:8080`).

3. **Build & Run**:
   Use Android Studio or Gradle CLI:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 💻 Android Studio Shared Run Configurations

Shared run configurations are checked into `.idea/runConfigurations/` according to standard JetBrains / Android Studio project conventions:

1. **`app`** (`app.xml`):
   - **Type**: `AndroidRunConfigurationType`
   - **Module**: `LearnApp.app`
   - **Target**: Launches `MainActivity` on connected Android Emulator or physical device after executing Gradle `BeforeRunTask` build.

2. **`Backend Server`** (`backend_server.xml`):
   - **Type**: `NodeJSConfigurationType`
   - **Target**: Launches `server/server.js` directly from Android Studio using the system Node.js interpreter.

3. **`Backend Server (npm)`** (`backend_server_npm.xml`):
   - **Type**: `js.build_tools.npm`
   - **Target**: Executes `npm start` inside `server/package.json` for seamless 1-click execution in Android Studio without requiring separate terminal steps.

---

## 🛡️ Security Specifications

- **JWT HMAC-SHA256**: Authenticated endpoints validate tokens passed via `Authorization: Bearer <jwt_token>`. Tokens carry a 24-hour expiration timestamp.
- **PBKDF2 Password Encryption**: Server-side password hashing uses `crypto.pbkdf2Sync` with 1,000 iterations, 64-byte key length, and unique per-user 16-byte random salts.
- **Network Security Configuration**: Cleartext HTTP traffic is explicitly permitted for local development addresses (`10.0.2.2`, `localhost`, `127.0.0.1`) in `network_security_config.xml`.

---

## 📄 License

See [LICENSE.txt](LICENSE.txt).
