-- ═══════════════════════════════════════════════════════════════════════════
-- Digital Black Board — Seed Demo Users
-- Only inserts if no users exist yet (checked by application startup)
-- ═══════════════════════════════════════════════════════════════════════════

INSERT INTO users (name, email, password, role, initials, status, joined_date, last_active, courses_count)
SELECT 'Admin User', 'admin@gmail.com', 'Admin@123', 'Admin', 'AU', 'active', CURRENT_DATE, CURRENT_DATE, 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'admin@gmail.com');

INSERT INTO users (name, email, password, role, initials, status, joined_date, last_active, courses_count)
SELECT 'Bhargav', 'ins@gmail.com', 'ins@123', 'Instructor', 'BH', 'active', CURRENT_DATE, CURRENT_DATE, 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'ins@gmail.com');

INSERT INTO users (name, email, password, role, initials, status, joined_date, last_active, courses_count)
SELECT 'Prasanth', 'cc@gmail.com', 'cc@123', 'Content Creator', 'PR', 'active', CURRENT_DATE, CURRENT_DATE, 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'cc@gmail.com');

INSERT INTO users (name, email, password, role, initials, status, joined_date, last_active, courses_count)
SELECT 'Kumar', 'st@gmail.com', 'st@123', 'Student', 'KU', 'active', CURRENT_DATE, CURRENT_DATE, 0
FROM dual WHERE NOT EXISTS (SELECT 1 FROM users WHERE email = 'st@gmail.com');
