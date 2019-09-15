-- Command-Side
select * from axon_on_microprofile_tryout.domainevententry;
select * from axon_on_microprofile_tryout.snapshotevententry;
select payloadtype, metadata->>'traceId' from axon_on_microprofile_tryout.domainevententry;

-- Query-Side
select * from axon_on_microprofile_query_tryout.account;
select * from axon_on_microprofile_query_tryout.nickname;
select * from axon_on_microprofile_query_tryout.tokenentry;

select * from axon_on_microprofile_tryout.flyway_history;

-- Reset everything inside the schemas
drop schema axon_on_microprofile_tryout cascade;
drop schema axon_on_microprofile_query_tryout cascade;
drop schema "AXON_ON_MICROPROFILE_TRYOUT" cascade;
drop schema "AXON_ON_MICROPROFILE_QUERY_TRYOUT" cascade;