-- Course Progress Tracking Table
CREATE TABLE IF NOT EXISTS class_progress (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    user_id UUID NOT NULL REFERENCES users(id) ON DELETE CASCADE,
    lesson_id UUID NOT NULL REFERENCES lessons(id) ON DELETE CASCADE,
    course_id UUID NOT NULL REFERENCES courses(id) ON DELETE CASCADE,
    watched_seconds INTEGER DEFAULT 0,
    total_seconds INTEGER DEFAULT 0,
    is_completed BOOLEAN DEFAULT false,
    last_position_seconds INTEGER DEFAULT 0,
    last_watched_at TIMESTAMP,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    UNIQUE(user_id, lesson_id)
);

CREATE INDEX IF NOT EXISTS idx_class_progress_user ON class_progress(user_id);
CREATE INDEX IF NOT EXISTS idx_class_progress_lesson ON class_progress(lesson_id);
CREATE INDEX IF NOT EXISTS idx_class_progress_course ON class_progress(course_id);