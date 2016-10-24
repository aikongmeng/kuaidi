create table send_msg_content(
id integer primary key autoincrement,
sms_id varchar,
user_id varchar,
model_id varchar,
model_content varchar,
model_status varchar,
save_time varchar,
send_timing varchar,
auto_send_voice_model_id varchar,
synchronize_gun_scan_status varchar,
iscrash_status varchar);

create table send_msg_number(
id integer primary key autoincrement,
sms_id varchar,
_id integer,
user_id varchar,
no varchar,
phone_number varchar,
order_number varchar);

create table cache_contacts(
id integer primary key autoincrement,
phone varchar,
name varchar,
address varchar,
note varchar,
time varchar);

create table save_no(
id integer primary key autoincrement,
save_time varchar,
save_from varchar,
save_letter varchar,
save_number integer,
save_userPhone varchar);