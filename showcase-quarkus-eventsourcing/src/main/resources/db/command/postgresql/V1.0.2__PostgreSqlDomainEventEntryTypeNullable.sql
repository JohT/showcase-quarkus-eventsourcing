-- Removing "NOT NULL" on a column is done using "DROP NOT NULL" for PostgreSQL databases:
ALTER TABLE "axon_on_microprofile_tryout"."domainevententry" ALTER COLUMN TYPE DROP NOT NULL;