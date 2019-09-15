-- This script uses H2 database specific column type "BLOB" for binary data
ALTER TABLE "axon_on_microprofile_tryout"."domainevententry" ADD COLUMN METADATA blob;
ALTER TABLE "axon_on_microprofile_tryout"."domainevententry" ADD COLUMN PAYLOAD blob NOT NULL;

ALTER TABLE "axon_on_microprofile_tryout"."snapshotevententry" ADD COLUMN METADATA blob;
ALTER TABLE "axon_on_microprofile_tryout"."snapshotevententry" ADD COLUMN PAYLOAD blob NOT NULL;

ALTER TABLE "axon_on_microprofile_tryout"."sagaentry" ADD COLUMN SERIALIZEDSAGA blob;