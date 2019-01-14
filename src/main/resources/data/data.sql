insert into desig8or_db.user(id, first_name, last_name, email_address, is_lead) values (1, 'Chopper', 'TONY TONY', 'chopper.tonytony@onepiece.com' , false);
insert into desig8or_db.user(id, first_name, last_name, email_address, is_lead) values (2, 'Luffy'  , 'MONKEY D.', 'luffy.monkey@onpiece.com'      , true);
insert into desig8or_db.user(id, first_name, last_name, email_address, is_lead) values (3, 'Zoro'   , 'RORONOA'  , 'zoro.roronoa@onpiece.com'      , false);
insert into desig8or_db.user(id, first_name, last_name, email_address, is_lead) values (4, 'Robin'  , 'NICO'     , 'robin.nico@onpiece.com'        , false);
insert into desig8or_db.user(id, first_name, last_name, email_address, is_lead) values (5, 'Sandji' , 'VINSMOKE' , 'sandji.vinsmoke@onpiece.com'   , false);

insert into desig8or_db.pool(id, start_date, end_date) values (1, current_timestamp() - 20, current_timestamp() - 10);
insert into desig8or_db.pool(id, start_date, end_date) values (2, current_timestamp() - 10, current_timestamp() - 1);
insert into desig8or_db.pool(id, start_date, end_date) values (3, current_timestamp() - 1 , null);

insert into desig8or_db.assignment(user_id, pool_id, assignment_date) values (2, 3, current_timestamp());

insert into desig8or_db.designation(id, user_id, status, designation_date, user_response_date, is_current) values (1, 1, 'PENDING', current_timestamp(), null, true);

insert into desig8or_db.app_parameter(id, rotation_time) values (1, DATEADD('MINUTE', 2, CURRENT_TIME()));