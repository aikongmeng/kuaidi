create table E3_order (id integer primary key autoincrement,
order_number varchar,
type_E3 varchar,
wayBillType_E3 varchar,
type varchar,
type_extra varchar,
action varchar,
action_desc varchar,
operatorCode varchar,
sender_name varchar,
courier_job_no varchar,
company varchar,
scan_time varchar,
upload_time long,
picPath varchar,
firmname varchar,
isUpload int);

alter table customer_list add customerGroup integer;
alter table user_order_list add inform_sender_when_sign varchar;