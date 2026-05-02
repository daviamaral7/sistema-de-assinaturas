ALTER TABLE projects
    ADD COLUMN active BOOLEAN NOT NULL DEFAULT TRUE;

ALTER TABLE projects
    ALTER COLUMN active DROP DEFAULT;

CREATE INDEX idx_projects_active ON projects(active);