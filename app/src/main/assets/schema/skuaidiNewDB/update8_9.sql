alter table user_order_list add senderName varchar;
alter table user_order_list add senderPhone varchar;
alter table user_order_list add senderAddress varchar;
alter table user_order_list add articleInfo varchar;
alter table user_order_list add addressHead varchar;
alter table E3_order add isCache int;
create table if not exists bad_description (id integer primary key autoincrement, description varchar not null, company varchar, job_number varchar); 