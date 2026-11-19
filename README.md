# SecureAuth
Java 21 / Spring Boot / MySQL / React passwordless email authentication demo.

## Run on Windows PowerShell
Create database in MySQL Workbench: `CREATE DATABASE secureauth;`
Enable Google 2-Step Verification and create a Gmail App Password if your account permits it. Do not commit secrets.

In backend terminal:
```powershell
cd backend
$env:DB_USER="root"
$env:DB_PASSWORD="your-mysql-password"
$env:MAIL_USER="your-email@gmail.com"
$env:MAIL_PASSWORD="your-google-app-password"
$bytes = New-Object byte[] 48
$rng = [System.Security.Cryptography.RandomNumberGenerator]::Create()
$rng.GetBytes($bytes)
$env:JWT_SECRET = [Convert]::ToBase64String($bytes)
mvn spring-boot:run
```
In second terminal from project root:
```powershell
cd frontend
npm install
npm run dev
```
Open http://localhost:5173. Java 21+, Maven, MySQL and Node required. Port 9595 is backend. If port 5173 is in use, stop the previous Vite server or update CORS and the port.

Registration and sign-in share one passwordless flow. New users are created only after verification. Existing account names are preserved. OTPs are hashed, expire after five minutes, and can be used once. The JWT is valid for 15 minutes; dashboard data is fetched with it and the token is not persisted. Logout clears the UI; it does not revoke an already issued token. Refresh requires sign-in again.

## Check manually
Register, receive email, verify, inspect account in MySQL. Try wrong code, five wrong attempts, expiry, resend cooldown, replay of used code, GET /api/me without a token (401), logout, sign in again, and mobile viewport.

## Scope
Local educational demo, not production-ready. OTP state is in memory and resets on restart. Add distributed rate limits (including IP and email), durable challenges with Redis, HTTPS, token revocation/session management, automated tests, abuse detection and monitoring before deployment. No SMTP delivery or live full-stack execution was verified in the generation environment.
