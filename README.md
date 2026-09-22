# LearnApp

![Android CI](https://github.com/Sebastian90Sonntag/LearnApp/actions/workflows/android.yml/badge.svg)

**LearnApp** is a modern Android learning platform built with **Java**, adopting clean **MVVM (Model-View-ViewModel) Architecture**, **LiveData**, and **JWT (JSON Web Token)** REST API authentication.

---

## 🌟 Key Features

- **MVVM Architecture**: Decoupled presentation, domain logic, and data layers using `ViewModel`, `Repository`, and `LiveData`.
- **JWT & PBKDF2 Security**: Standardized `Authorization: Bearer <jwt_token>` header authentication with server-side PBKDF2 password hashing & salt.
- **User Authentication**: Registration, Login, and Password Recovery flows.
- **Interactive Quiz Module**: Dynamic question fetching, answer submission, and rating tracking.
- **Live Scoreboard**: User ranking & score tracking.
- **Profile & Avatar Upload**: Base64 image processing & profile picture management.
- **Built-in Backend REST Server**: Lightweight, zero-dependency Node.js REST API server (`server/server.js`).

---

## 🏗️ Architecture Overview

```
com.graphicdesigncoding.learnapp/
├── api/                # CallAPI, SessionManager, ApiConfig, Crypt
├── repository/         # AuthRepository, QuizRepository, ScoreboardRepository, ProfileRepository, Resource
├── viewmodel/          # LoginViewModel, RegisterViewModel, QuizViewModel, ScoreboardViewModel, ProfileViewModel, RecoverViewModel
├── forms/ (View)       # LoginForm, RegisterForm, QuizForm, ScoreboardForm, ProfileForm, RecoverForm
├── user/               # User data models & list adapters
└── MainActivity.java   # NavHostFragment host & App shell
```

---

## 🛠️ Backend REST API (`server/server.js`)

The project includes a zero-dependency Node.js REST API backend supporting versioned `/api/v1/...` endpoints.

### API Endpoints

| Method | Endpoint | Auth Required | Description |
|--------|----------|---------------|-------------|
| `POST` | `/api/v1/auth/login` | No | User authentication & JWT generation |
| `POST` | `/api/v1/auth/register` | No | User registration & account creation |
| `POST` | `/api/v1/auth/forgot-password` | No | Request password recovery code |
| `POST` | `/api/v1/auth/reset-password` | No | Reset password with recovery token |
| `GET`/`POST` | `/api/v1/quiz/question` | **Bearer JWT** | Fetch quiz question / submit answer rating |
| `GET`/`POST` | `/api/v1/scoreboard` | **Bearer JWT** | Fetch top user rankings |
| `GET` | `/api/v1/profile` | **Bearer JWT** | Get current user profile |
| `POST` | `/api/v1/profile/avatar` | **Bearer JWT** | Upload profile picture avatar |

### Starting the Backend Server

```bash
# Start the Node.js REST API server (listens on http://0.0.0.0:8080)
node server/server.js
```

---

## 🚀 Getting Started with the Android App

### Prerequisites

- **Android Studio** (Ladybug / Jellyfish or newer)
- **JDK 17+**
- **Node.js** (for running `server/server.js`)

### Setup & Configuration

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/Sebastian90Sonntag/LearnApp.git
   cd LearnApp
   ```

2. **Configure API Base URL**:
   Open `app/src/main/java/com/graphicdesigncoding/learnapp/api/ApiConfig.java`:
   - **Android Emulator**: Uses `http://10.0.2.2:8080` (default).
   - **Physical Device**: Change `10.0.2.2` to your computer's local Wi-Fi IP address (e.g. `http://192.168.1.100:8080`).

3. **Run Backend Server**:
   ```bash
   node server/server.js
   ```

4. **Build & Run App**:
   Use Android Studio or run Gradle from the command line:
   ```bash
   ./gradlew assembleDebug
   ```

---

## 💻 Shared Run Configurations

Shared Android Studio run configurations are provided in `.idea/runConfigurations/`:
- `app`: Run Android App build & deployment.
- `backend_server`: Run the Node.js REST API server.

---

## 🛡️ Security

- **JWT Tokens**: 24-hour expiration HMAC-SHA256 signed tokens.
- **Password Protection**: Passwords are never stored in plain text; hashed server-side with PBKDF2 (1000 iterations, 64-byte key length, unique per-user salt).
- **Cleartext Traffic**: Configured via `network_security_config.xml` for local development host domains (`10.0.2.2`, `localhost`, `127.0.0.1`).

---

## 📄 License

See [LICENSE.txt](LICENSE.txt).

