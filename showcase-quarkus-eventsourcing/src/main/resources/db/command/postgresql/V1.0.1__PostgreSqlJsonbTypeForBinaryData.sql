-- This script uses PostgreSql database specific column type "JSONB" for extended JSON support of binary data
ALTER TABLE "axon_on_microprofile_tryout"."domainevententry" ADD COLUMN METADATA jsonb;
ALTER TABLE "axon_on_microprofile_tryout"."domainevententry" ADD COLUMN PAYLOAD jsonb NOT NULL;

ALTER TABLE "axon_on_microprofile_tryout"."snapshotevententry" ADD COLUMN METADATA jsonb;
ALTER TABLE "axon_on_microprofile_tryout"."snapshotevententry" ADD COLUMN PAYLOAD jsonb NOT NULL;

ALTER TABLE "axon_on_microprofile_tryout"."sagaentry" ADD COLUMN SERIALIZEDSAGA jsonb;