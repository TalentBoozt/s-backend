-- =========================================================================
-- DATABASE MIGRATION: ADD TECHNICAL SEO INFRASTRUCTURE FIELDS
-- Target: PostgreSQL
-- Mappings: courses, instructor_profiles
-- =========================================================================

-- 1. EXTEND COURSES SCHEMA WITH TECHNICAL SEO COLUMNS
ALTER TABLE courses ADD COLUMN IF NOT EXISTS seo_slug VARCHAR(255) UNIQUE;
ALTER TABLE courses ADD COLUMN IF NOT EXISTS seo_title VARCHAR(150);
ALTER TABLE courses ADD COLUMN IF NOT EXISTS seo_description VARCHAR(255);
ALTER TABLE courses ADD COLUMN IF NOT EXISTS seo_keywords VARCHAR(255);
ALTER TABLE courses ADD COLUMN IF NOT EXISTS schema_json_ld TEXT;
ALTER TABLE courses ADD COLUMN IF NOT EXISTS localized_lang_group_id VARCHAR(100);
ALTER TABLE courses ADD COLUMN IF NOT EXISTS canonical_url VARCHAR(255);
ALTER TABLE courses ADD COLUMN IF NOT EXISTS indexable BOOLEAN DEFAULT TRUE;
ALTER TABLE courses ADD COLUMN IF NOT EXISTS published_at TIMESTAMP WITH TIME ZONE DEFAULT CURRENT_TIMESTAMP;

-- 2. EXTEND INSTRUCTOR_PROFILES SCHEMA WITH TECHNICAL SEO COLUMNS
ALTER TABLE instructor_profiles ADD COLUMN IF NOT EXISTS seo_slug VARCHAR(255) UNIQUE;
ALTER TABLE instructor_profiles ADD COLUMN IF NOT EXISTS schema_json_ld TEXT;
ALTER TABLE instructor_profiles ADD COLUMN IF NOT EXISTS canonical_url VARCHAR(255);
ALTER TABLE instructor_profiles ADD COLUMN IF NOT EXISTS indexable BOOLEAN DEFAULT TRUE;

-- 3. ESTABLISH UNIQUE INDEXES FOR HIGH-SPEED BOT QUERYING
CREATE UNIQUE INDEX IF NOT EXISTS idx_courses_seo_slug ON courses(seo_slug);
CREATE UNIQUE INDEX IF NOT EXISTS idx_instructor_profiles_seo_slug ON instructor_profiles(seo_slug);

-- 4. ADDITIONAL OPTIMIZATIONS FOR LOCALIZATION GROUPS AND INDEXABLE STATS
CREATE INDEX IF NOT EXISTS idx_courses_lang_group ON courses(localized_lang_group_id);
CREATE INDEX IF NOT EXISTS idx_courses_indexable_pub ON courses(indexable, published_at DESC);
CREATE INDEX IF NOT EXISTS idx_instructor_profiles_indexable ON instructor_profiles(indexable);
