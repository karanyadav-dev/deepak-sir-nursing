-- Add new columns to courses table
ALTER TABLE courses ADD COLUMN IF NOT EXISTS course_type VARCHAR(100);
ALTER TABLE courses ADD COLUMN IF NOT EXISTS duration VARCHAR(100);
ALTER TABLE courses ADD COLUMN IF NOT EXISTS featured BOOLEAN NOT NULL DEFAULT false;

CREATE INDEX IF NOT EXISTS idx_courses_type ON courses(course_type);