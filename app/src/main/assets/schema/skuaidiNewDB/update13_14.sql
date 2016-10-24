alter table draft_box add normal_exit_status varchar;

create table draft_box_cloud_voice(
id integer primary key autoincrement,
draft_id varchar,
draft_save_time varchar,
phone_number varchar,
number varchar,
model_id varchar,
model_title varchar,
user_phoneNum varchar,
normal_exit_status);

create table save_unnormal_exit_draftinfo(
id integer primary key autoincrement,
draft_id varchar,
from_data varchar,
draft_no varchar,
draft_phoneNumber varchar,
draft_orderNumber varchar,
draft_position varchar,
draft_positionNo varchar);