insert into design8or_db.user(id, first_name, last_name, email_address, is_lead) values (1, 'Chopper', 'TONY TONY', 'chopper.tonytony@onepiece.com' , false);
insert into design8or_db.user(id, first_name, last_name, email_address, is_lead) values (2, 'Luffy'  , 'MONKEY D.', 'luffy.monkey@onpiece.com'      , true);
insert into design8or_db.user(id, first_name, last_name, email_address, is_lead) values (3, 'Zoro'   , 'RORONOA'  , 'zoro.roronoa@onpiece.com'      , false);
insert into design8or_db.user(id, first_name, last_name, email_address, is_lead) values (4, 'Robin'  , 'NICO'     , 'robin.nico@onpiece.com'        , false);
insert into design8or_db.user(id, first_name, last_name, email_address, is_lead) values (5, 'Sandji' , 'VINSMOKE' , 'sandji.vinsmoke@onpiece.com'   , false);

insert into design8or_db.pool(id, start_date, end_date, status) values (1, current_timestamp() - 20, current_timestamp() - 10, 'ENDED');
insert into design8or_db.pool(id, start_date, end_date, status) values (2, current_timestamp() - 10, current_timestamp() - 1, 'ENDED');
insert into design8or_db.pool(id, start_date, end_date, status) values (3, current_timestamp() - 1 , null, 'STARTED'); --Current Pool

insert into design8or_db.assignment(user_id, pool_id, assignment_date) values (2, 3, current_timestamp());

insert into design8or_db.designation(id, user_id, status, designation_date, user_response_date) values (1, 1, 'PENDING', current_timestamp(), null);
insert into design8or_db.designation(id, user_id, status, designation_date, user_response_date) values (2, 1, 'ACCEPTED', current_timestamp() - 30, current_timestamp());

insert into design8or_db.app_parameter(id, rotation_time, st_req_check_period_mins) values (1, DATEADD('MINUTE', 0.5, CURRENT_TIME()), 10);

insert into design8or_db.subscription(id, endpoint, auth, p256dh) values (1, 'https://updates.push.services.mozilla.com/wpush/v2/gAAAAABcPiOX6tDTL0Bw71YmkhFC6BIX9gqONNi5G9Kt-tZazQaQaDXy9aU1dlu05hn_M83QZAG934MKcEf5SHmNb_dgGy3gmgGDFIwLaqxkZY7VIZWW2MkiN2Y9eykWsddpvnAQpJgoliCbhXlYgSABRBySIbjEbNjZhrjpMk3mhu7rCh8kV44', '2qgYJgh4VpltYBflQ1UQSg', 'BLHL6tGJCc3NQW_Btq_izk9sbmh_FrJ8ybfYfe6UxD2XiDBv6gWitDvQPt3o-g4IXYSkrnA0t6nCncitW5F0-VQ');