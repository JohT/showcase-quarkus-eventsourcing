-- Rename the column named "TIMESTAMP" to "EVENTTIMESTAMP" so that there is no conflict with reserved words.
ALTER TABLE "axon_on_microprofile_tryout"."domainevententry" ALTER COLUMN "TIMESTAMP" RENAME TO "EVENTTIMESTAMP";
ALTER TABLE "axon_on_microprofile_tryout"."snapshotevententry" ALTER COLUMN "TIMESTAMP" RENAME TO "EVENTTIMESTAMP";