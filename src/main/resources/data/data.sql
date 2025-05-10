insert into user(id, first_name, last_name, email_address) values (1, 'Chopper', 'Tony Tony', 'chopper.tonytony@onepiece.com');
insert into user(id, first_name, last_name, email_address) values (2, 'Luffy'  , 'Monkey D.', 'luffy.monkey@onpiece.com'     );
insert into user(id, first_name, last_name, email_address) values (3, 'Zoro'   , 'Roronoa'  , 'zoro.roronoa@onpiece.com'     );
insert into user(id, first_name, last_name, email_address) values (4, 'Robin'  , 'Nico'     , 'robin.nico@onpiece.com'       );
insert into user(id, first_name, last_name, email_address) values (5, 'Sandji' , 'Vinsmoke' , 'sandji.vinsmoke@onpiece.com'  );

insert into pool(id, start_date, end_date, status) values (1, current_timestamp() - 20, current_timestamp() - 10, 'ENDED');
insert into pool(id, start_date, end_date, status) values (2, current_timestamp() - 10, current_timestamp() - 1, 'ENDED');
insert into pool(id, start_date, end_date, status) values (3, current_timestamp() - 1 , null, 'STARTED'); --Current Pool

-- TODO Add assignments for past pools
--insert into assignment(user_id, pool_id, assignment_date) values (1, 3, current_timestamp());

insert into designation(id, user_id, status, designation_date, user_response_date) values (1, 1, 'PENDING', current_timestamp(), null);
insert into designation(id, user_id, status, designation_date, user_response_date) values (2, 1, 'ACCEPTED', current_timestamp() - 30, current_timestamp());

insert into subscription(id, endpoint, auth, p256dh) values (1,'https://fcm.googleapis.com/fcm/send/euk_xuj3yc0:APA91bGxe51K6d9ISyYnBDsb8lAmO2LsVGgXogF5kgxyn4wxPehuqlZuwtZNf_tKKWMheskVFy79IPqeWTsE1Y10jRhbEMeKpFBVbMJ_u2M1XlHtWZULOdzkxOTxbsQCSxQ_TX5Y2wMV','04pI3N9c5uS3O9yxoev0WA','BAG6aC0Y_R-Lp3qCUgDI9_YFD_kFfk9hw6OW_vMgGzdUciP0qJuq4EgvIvGLsiVMJiKlwAeaeg9xhyPwbMskuT8');