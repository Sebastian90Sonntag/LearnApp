# Security Policy

## Security Architecture

LearnApp implements modern security practices across both the Android application and backend REST server:

- **JWT Authentication**: Secured via `Authorization: Bearer <jwt_token>` header. Tokens expire after 24 hours and are signed using HMAC-SHA256.
- **Password Security**: Passwords are hashed server-side using **PBKDF2** (SHA-512, 1000 iterations, 64-byte key length) with unique per-user cryptographically random salts.
- **Network Security Configuration**: Android network security config (`app/src/main/res/xml/network_security_config.xml`) restricts cleartext HTTP traffic strictly to local development host addresses (`10.0.2.2`, `localhost`, `127.0.0.1`).

## Supported Versions

| Version     | Supported          |
|-------------|--------------------|
| Main (v2.x) | :white_check_mark: |
| < 2.0       | :x:                |

## Reporting a Vulnerability

If you discover a potential security vulnerability within LearnApp or the REST server, please report it responsibly:

1. Do **not** open a public GitHub issue.
2. Send security reports directly via private email or contact the project maintainer (`Sebastian90Sonntag`).
3. Include detailed steps or proof-of-concept to help reproduce the issue.
4. Reports are typically acknowledged within 48 hours.

