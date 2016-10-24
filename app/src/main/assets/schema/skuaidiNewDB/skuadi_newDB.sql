create table user_message_list ( 
				id integer primary key autoincrement,lyid varchar,target_id varchar,exp_no varchar,
				brand varchar,liuyan_type integer,counterman_id varchar,content varchar,
				content_type varchar,speak_time varchar,speaker_id varchar,speaker_role integer,nrCount varchar,
				tip varchar,user_phone varchar,cache_id integer,cm_nr_flag integer,isReadStateUpload integer);
				

create table message_detail_list
				(id integer primary key autoincrement,
				lyid integer,ldid varchar,exp_no varchar,tip varchar,counterman_id varchar,
				speaker_role integer,content varchar,content_type varchar,speak_time varchar,
				voice_length varchar,cache_id integer,pic_path varchar,user_name varchar,
				voice_path varchar,recording_name varchar);
				



create table user_order_list
				(
				id integer primary key autoincrement,
				order_id varchar,order_type varchar,exp_no varchar,counterman_id varchar,
				user_name varchar,user_phone varchar,address varchar,place_order_time varchar,
				ps varchar,newIm integer,isRead integer,type varchar,order_state_cname varchar,is_need_sync integer,
				real_pay varchar,isReadStateUpload integer,inform_sender_when_sign varchar,senderName varchar,
				senderPhone varchar,senderAddress varchar,articleInfo varchar,addressHead varchar);	
					
create table order_detail_list
				(
				id integer primary key autoincrement,
				oiid varchar,speak_id varchar,order_id varchar,counterman_id varchar,
				user_name varchar,speak_time varchar,speakRole integer,content varchar,
				contentType integer,voiceLength integer,txtContent varchar,voiceContent varchar);

create table customer_tobedeleted_id_list (id integer primary key autoincrement,customer_id varchar,customer_phone varchar,user_id varchar);

create table customer_list 
			(id integer primary key autoincrement,customer_id varchar,customer_phone varchar,customer_name 	varchar,customer_address varchar,customer_remark varchar,customer_addTime,is_need_update integer,customerGroup integer,user_id varchar,customer_tel varchar, tags varchar);

create table E3signedTypes (id integer primary key autoincrement, signedType varchar not null,company varchar); 
 
insert into [E3signedTypes] values(1, '邮件签收章','sto');
insert into [E3signedTypes] values(2, '本人签收','sto');
insert into [E3signedTypes] values(3, '门卫签收','sto');
insert into [E3signedTypes] values(4, '前台签收','sto');
insert into [E3signedTypes] values(5, '家人签收','sto');
insert into [E3signedTypes] values(6, '同事代签','sto');
insert into [E3signedTypes] values(7, '物管代签','sto');
insert into [E3signedTypes] values(8, '代理点代签','sto');
insert into [E3signedTypes] values(9, '学校代理点签收','sto');
insert into [E3signedTypes] values(10, '补签','sto');

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
isUpload int,
station_name,
problem_desc varchar,
isCache int,
latitude varchar,
longitude varchar,
phone_number varchar,
order_weight DOUBLE,
resType int,
thirdBranch varchar,
thirdBranchId varchar
);

create table if not exists bad_description (id integer primary key autoincrement, description varchar not null, company varchar, job_number varchar);

create table order_cache
				(
				id integer primary key autoincrement,user_name varchar,user_mobile varchar,user_address varchar,voice_name varchar,note varchr);
create table draft_box	(id integer primary key autoincrement,
draft_id varchar,
draft_save_time varchar,
sms_content varchar,
sms_content_title varchar,
sms_status varchar,
sms_id varchar,
send_timing varchar,
number varchar,
phone_number varchar,
order_number varchar,
isgunscan varchar,
user_phoneNum varchar,
normal_exit_status varchar);	

create table draft_box_cloud_voice(
id integer primary key autoincrement,
draft_id varchar,
draft_save_time varchar,
phone_number varchar,
order_number varchar,
number varchar,
model_id varchar,
model_title varchar,
user_phoneNum varchar,
normal_exit_status varchar); 	

create table save_unnormal_exit_draftinfo(
id integer primary key autoincrement,
draft_id varchar,
from_data varchar,
draft_no varchar,
draft_phoneNumber varchar,
draft_orderNumber varchar,
draft_position varchar,
draft_positionNo varchar);	

create table send_msg_content(
id integer primary key autoincrement,
sms_id varchar,
user_id varchar,
model_id varchar,
model_content varchar,
model_status varchar,
send_timing varchar,
save_time varchar,
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