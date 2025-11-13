ALTER TABLE tccs
    ADD COLUMN IF NOT EXISTS google_file_id VARCHAR(255),
    ADD COLUMN IF NOT EXISTS google_web_view_link TEXT,
    ADD COLUMN IF NOT EXISTS google_web_edit_link TEXT,
    ADD COLUMN IF NOT EXISTS google_doc_criado_em TIMESTAMP;

