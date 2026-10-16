-- Reference schema — for understanding/manual setup only.
-- Hibernate (spring.jpa.hibernate.ddl-auto=update) creates/updates these tables
-- automatically from the entity classes, so you do NOT need to run this by hand
-- to get the app running. Keep it around as documentation, or switch to
-- ddl-auto=validate and use this (turned into a migration tool like Flyway) once
-- your schema stabilizes.

CREATE TABLE users (
    id BIGSERIAL PRIMARY KEY,
    username VARCHAR(255) NOT NULL UNIQUE,
    email VARCHAR(255) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    role VARCHAR(20) NOT NULL DEFAULT 'STUDENT' -- ADMIN | STUDENT
);

CREATE TABLE quizzes (
    id BIGSERIAL PRIMARY KEY,
    title VARCHAR(255) NOT NULL,
    description TEXT,
    duration_minutes INTEGER NOT NULL,
    created_by BIGINT REFERENCES users(id),
    created_at TIMESTAMP NOT NULL DEFAULT now()
);

CREATE TABLE questions (
    id BIGSERIAL PRIMARY KEY,
    quiz_id BIGINT NOT NULL REFERENCES quizzes(id) ON DELETE CASCADE,
    question_text TEXT NOT NULL,
    option_a VARCHAR(500) NOT NULL,
    option_b VARCHAR(500) NOT NULL,
    option_c VARCHAR(500) NOT NULL,
    option_d VARCHAR(500) NOT NULL,
    correct_option CHAR(1) NOT NULL -- A | B | C | D
);

CREATE TABLE attempts (
    id BIGSERIAL PRIMARY KEY,
    user_id BIGINT NOT NULL REFERENCES users(id),
    quiz_id BIGINT NOT NULL REFERENCES quizzes(id),
    score INTEGER,
    started_at TIMESTAMP NOT NULL DEFAULT now(),
    submitted_at TIMESTAMP
);

CREATE TABLE answers (
    id BIGSERIAL PRIMARY KEY,
    attempt_id BIGINT NOT NULL REFERENCES attempts(id) ON DELETE CASCADE,
    question_id BIGINT NOT NULL REFERENCES questions(id),
    selected_option CHAR(1),
    correct BOOLEAN NOT NULL DEFAULT false
);

-- ---------------------------------------------------------------------------
-- Creating the first ADMIN user
-- ---------------------------------------------------------------------------
-- The /api/auth/register endpoint always creates STUDENT accounts on purpose
-- (so random signups can't grant themselves admin). To create your first
-- admin: register normally through the app, then promote that account by
-- hand:
--
--   UPDATE users SET role = 'ADMIN' WHERE username = 'your_admin_username';
--
-- After that, log in again so a fresh JWT is issued with the ADMIN role.
