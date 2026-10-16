# Gradebook — Online Quiz System

Full-stack starter project: **Spring Boot (Java) + PostgreSQL** backend, plain **HTML/CSS/JS** frontend.

```
quiz-app/
├── backend/     Spring Boot REST API (Java 17, Maven)
└── frontend/    Static HTML/CSS/JS — no build step required
```

## What's included

- JWT authentication (register/login), passwords hashed with BCrypt
- Roles: `STUDENT` (default on registration) and `ADMIN` (promoted manually — see below)
- Quiz + question CRUD (admin only)
- Timed attempts: client-side countdown **and** independent server-side time-limit
  enforcement, so tampering with the browser timer doesn't grant extra time
- Auto-grading on submit, with a per-question answer review
- Correct answers are never sent to the client before grading — the
  `QuestionForStudentDTO` the frontend receives has no `correctOption` field
- Results dashboard for admins, per quiz

## Prerequisites

- Java 17+
- Maven 3.8+
- PostgreSQL 14+ running locally

## 1. Set up the database

```sql
CREATE DATABASE quizdb;
```

`backend/src/main/resources/application.properties` points at
`jdbc:postgresql://localhost:5432/quizdb` with username/password `postgres`/`postgres`
— change these to match your local setup. Tables are created automatically on
first run (`spring.jpa.hibernate.ddl-auto=update`); `backend/schema-reference.sql`
documents the resulting schema if you want to read it without starting the app.

## 2. Run the backend

```bash
cd backend
mvn spring-boot:run
```

The API starts on `http://localhost:8080`.

## 3. Run the frontend

The frontend is static files — no npm, no build step. Simplest option:

```bash
cd frontend
python3 -m http.server 5500
```

Then open `http://localhost:5500`. (Any static server works — VS Code's "Live
Server" extension, `npx serve`, etc. If you use a different port or tool,
update the allowed CORS origins in `SecurityConfig.java` to match.)

## 4. Create your first admin account

Registration through the UI always creates a `STUDENT` account (on purpose —
so nobody can self-promote to admin through the API). To get an admin login:

1. Register normally through the frontend.
2. Promote that user directly in the database:
   ```sql
   UPDATE users SET role = 'ADMIN' WHERE username = 'your_username';
   ```
3. Log out and log back in (so a fresh JWT is issued with the `ADMIN` role).
4. You'll now see an **Admin** link in the nav — create quizzes and add
   questions there.

## API overview

| Method | Path                              | Auth        | Purpose                          |
|--------|-----------------------------------|-------------|-----------------------------------|
| POST   | `/api/auth/register`              | none        | Create a STUDENT account          |
| POST   | `/api/auth/login`                 | none        | Get a JWT                         |
| GET    | `/api/quizzes`                    | any user    | List quizzes                      |
| POST   | `/api/quizzes/{id}/start`         | any user    | Start an attempt, get questions   |
| POST   | `/api/attempts/{id}/submit`       | any user    | Submit answers, get graded result |
| GET    | `/api/attempts/{id}/result`       | any user    | Re-fetch a past result            |
| POST   | `/api/admin/quizzes`              | ADMIN       | Create a quiz                     |
| POST   | `/api/admin/quizzes/{id}/questions` | ADMIN     | Add a question to a quiz          |
| GET    | `/api/admin/quizzes/{id}/results` | ADMIN       | List all attempts for a quiz      |

All authenticated requests need `Authorization: Bearer <token>`.

## Where to go from here

Natural next steps, roughly in order of effort:

- **Randomize question order** per attempt (shuffle in `QuizService.startAttempt`)
- **One-attempt-per-quiz** enforcement (unique constraint on `user_id + quiz_id`
  in `attempts`, checked in `startAttempt`)
- **Leaderboard** per quiz (`AttemptRepository` query sorted by score/time)
- **Question categories/tags** and difficulty filtering
- **Switch `ddl-auto` to `validate`** and manage schema changes with Flyway
  once the schema stabilizes
- Swap the plain-JS frontend for React/Vue if the UI grows past a few pages
