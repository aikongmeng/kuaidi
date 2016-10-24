alter table E3signedTypes add company varchar;
update E3signedTypes set company='sto';
alter table E3_order add station_name varchar;
alter table E3_order add problem_desc varchar;