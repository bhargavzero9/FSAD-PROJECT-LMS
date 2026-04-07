@echo off
REM ── Digital Black Board LMS — Spring Boot Runner ──────────────────────────
REM First-time setup: runs Maven compile + spring-boot:run
REM Subsequent runs: just starts the app

setlocal
set MAVEN_HOME=%~dp0apache-maven-3.9.6
set PATH=%MAVEN_HOME%\bin;%PATH%

echo.
echo ╔═══════════════════════════════════════════════════════════════╗
echo ║     Digital Black Board LMS — Spring Boot Backend            ║
echo ╚═══════════════════════════════════════════════════════════════╝
echo.

mvn spring-boot:run
