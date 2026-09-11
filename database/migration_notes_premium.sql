-- Add premium flag to notes
ALTER TABLE notes ADD COLUMN IF NOT EXISTS is_premium BOOLEAN NOT NULL DEFAULT false;
ALTER TABLE notes ADD COLUMN IF NOT EXISTS preview_content TEXT;

CREATE INDEX IF NOT EXISTS idx_notes_premium ON notes(is_premium);