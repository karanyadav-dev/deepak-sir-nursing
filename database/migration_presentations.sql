CREATE TABLE IF NOT EXISTS presentations (
    id UUID PRIMARY KEY DEFAULT uuid_generate_v4(),
    title VARCHAR(200) NOT NULL,
    description VARCHAR(500),
    file_url VARCHAR(500) NOT NULL,
    file_size BIGINT DEFAULT 0,
    slide_count INTEGER DEFAULT 0,
    thumbnail_url VARCHAR(500),
    subject_id UUID REFERENCES subjects(id) ON DELETE SET NULL,
    published BOOLEAN NOT NULL DEFAULT false,
    active BOOLEAN NOT NULL DEFAULT true,
    created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
);

CREATE INDEX IF NOT EXISTS idx_presentations_subject ON presentations(subject_id);