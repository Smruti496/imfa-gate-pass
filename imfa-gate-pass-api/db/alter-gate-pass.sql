-- ALTER TABLE for existing gate_pass table
-- This script adds missing columns to support the IMFA Gate Pass Control System
-- To be executed by DBA on production database

ALTER TABLE public.gate_pass
    ADD COLUMN IF NOT EXISTS pass_no        text,
    ADD COLUMN IF NOT EXISTS location       text,
    ADD COLUMN IF NOT EXISTS gate           text,
    ADD COLUMN IF NOT EXISTS check_in_time  text,
    ADD COLUMN IF NOT EXISTS check_out_time text;

-- Verify columns were added
-- \d public.gate_pass

-- Atomic pass number sequence
CREATE SEQUENCE IF NOT EXISTS gate_pass_seq START 1;
-- Add unique constraint on pass_no
ALTER TABLE public.gate_pass ADD CONSTRAINT IF NOT EXISTS gate_pass_pass_no_uq UNIQUE (pass_no);

-- Analytics: visitor gender
ALTER TABLE public.gate_pass ADD COLUMN IF NOT EXISTS gender text;
