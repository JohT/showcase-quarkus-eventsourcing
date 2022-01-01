-- Removing "NOT NULL" on a column is done using "SET NULL" for H2 databases:
ALTER TABLE "axon_on_microprofile_tryout"."domainevententry" ALTER COLUMN TYPE SET NULL;