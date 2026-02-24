-- Users
insert into app_user(id, first_name, last_name, email_address) values (1, 'Chopper', 'Tony Tony', 'chopper.tonytony@onepiece.com');
insert into app_user(id, first_name, last_name, email_address) values (2, 'Luffy'  , 'Monkey D.', 'luffy.monkey@onpiece.com'     );
insert into app_user(id, first_name, last_name, email_address) values (3, 'Zoro'   , 'Roronoa'  , 'zoro.roronoa@onpiece.com'     );
insert into app_user(id, first_name, last_name, email_address) values (4, 'Robin'  , 'Nico'     , 'robin.nico@onpiece.com'       );
insert into app_user(id, first_name, last_name, email_address) values (5, 'Sanji'  , 'Vinsmoke' , 'sanji.vinsmoke@onpiece.com'   );
insert into app_user(id, first_name, last_name, email_address) values (6, 'Nami'   , 'Cat Burglar', 'nami.navigator@onpiece.com' );
insert into app_user(id, first_name, last_name, email_address) values (7, 'Usopp'  , 'God'      , 'usopp.sniper@onpiece.com'     );
insert into app_user(id, first_name, last_name, email_address) values (8, 'Franky' , 'Cutty Flam', 'franky.shipwright@onpiece.com');
insert into app_user(id, first_name, last_name, email_address) values (9, 'Brook'  , 'Soul King', 'brook.musician@onpiece.com'   );
insert into app_user(id, first_name, last_name, email_address) values (10, 'Jinbe' , 'Knight'   , 'jinbe.fishman@onpiece.com'    );
insert into app_user(id, first_name, last_name, email_address) values (11, 'Ace'   , 'Portgas D.', 'ace.fire@onpiece.com'        );
insert into app_user(id, first_name, last_name, email_address) values (12, 'Sabo'  , 'Revolutionary', 'sabo.flame@onpiece.com'   );
insert into app_user(id, first_name, last_name, email_address) values (13, 'Law'   , 'Trafalgar', 'law.surgeon@onpiece.com'      );
insert into app_user(id, first_name, last_name, email_address) values (14, 'Kid'   , 'Eustass'  , 'kid.captain@onpiece.com'      );
insert into app_user(id, first_name, last_name, email_address) values (15, 'Shanks', 'Red Hair' , 'shanks.emperor@onpiece.com'   );
insert into app_user(id, first_name, last_name, email_address) values (16, 'Mihawk', 'Dracule'  , 'mihawk.swordsman@onpiece.com' );
insert into app_user(id, first_name, last_name, email_address) values (17, 'Hancock', 'Boa'     , 'hancock.empress@onpiece.com'  );
insert into app_user(id, first_name, last_name, email_address) values (18, 'Crocodile', 'Sir'   , 'crocodile.warlord@onpiece.com');
insert into app_user(id, first_name, last_name, email_address) values (19, 'Doflamingo', 'Donquixote', 'doflamingo.joker@onpiece.com');
insert into app_user(id, first_name, last_name, email_address) values (20, 'Katakuri', 'Charlotte', 'katakuri.mochi@onpiece.com' );
insert into app_user(id, first_name, last_name, email_address) values (21, 'Yamato', 'Kozuki'   , 'yamato.oden@onpiece.com'      );
insert into app_user(id, first_name, last_name, email_address) values (22, 'Marco' , 'Phoenix'  , 'marco.firstmate@onpiece.com'  );
insert into app_user(id, first_name, last_name, email_address) values (23, 'Whitebeard', 'Edward', 'whitebeard.emperor@onpiece.com');
insert into app_user(id, first_name, last_name, email_address) values (24, 'Rayleigh', 'Silvers', 'rayleigh.dark@onpiece.com'   );
insert into app_user(id, first_name, last_name, email_address) values (25, 'Garp'  , 'Monkey D.', 'garp.hero@onpiece.com'        );
insert into app_user(id, first_name, last_name, email_address) values (26, 'Smoker', 'White Hunter', 'smoker.marine@onpiece.com' );
insert into app_user(id, first_name, last_name, email_address) values (27, 'Kizaru', 'Borsalino', 'kizaru.admiral@onpiece.com'   );
insert into app_user(id, first_name, last_name, email_address) values (28, 'Aokiji', 'Kuzan'    , 'aokiji.ice@onpiece.com'       );
insert into app_user(id, first_name, last_name, email_address) values (29, 'Akainu', 'Sakazuki' , 'akainu.magma@onpiece.com'     );
insert into app_user(id, first_name, last_name, email_address) values (30, 'Fujitora', 'Issho'  , 'fujitora.gravity@onpiece.com' );

-- Pools (removed status column - pool status now derived from endDate: null = active, not null = ended)
insert into pool(id, start_date, end_date) values (1, current_timestamp() - 200, current_timestamp() - 190);
insert into pool(id, start_date, end_date) values (2, current_timestamp() - 190, current_timestamp() - 180);
insert into pool(id, start_date, end_date) values (3, current_timestamp() - 180, current_timestamp() - 170);
insert into pool(id, start_date, end_date) values (4, current_timestamp() - 170, current_timestamp() - 160);
insert into pool(id, start_date, end_date) values (5, current_timestamp() - 160, current_timestamp() - 150);
insert into pool(id, start_date, end_date) values (6, current_timestamp() - 150, current_timestamp() - 140);
insert into pool(id, start_date, end_date) values (7, current_timestamp() - 140, current_timestamp() - 130);
insert into pool(id, start_date, end_date) values (8, current_timestamp() - 130, current_timestamp() - 120);
insert into pool(id, start_date, end_date) values (9, current_timestamp() - 120, current_timestamp() - 110);
insert into pool(id, start_date, end_date) values (10, current_timestamp() - 110, current_timestamp() - 100);
insert into pool(id, start_date, end_date) values (11, current_timestamp() - 100, current_timestamp() - 90);
insert into pool(id, start_date, end_date) values (12, current_timestamp() - 90, current_timestamp() - 80);
insert into pool(id, start_date, end_date) values (13, current_timestamp() - 80, current_timestamp() - 70);
insert into pool(id, start_date, end_date) values (14, current_timestamp() - 70, current_timestamp() - 60);
insert into pool(id, start_date, end_date) values (15, current_timestamp() - 60, current_timestamp() - 50);
insert into pool(id, start_date, end_date) values (16, current_timestamp() - 50, current_timestamp() - 40);
insert into pool(id, start_date, end_date) values (17, current_timestamp() - 40, current_timestamp() - 30);
insert into pool(id, start_date, end_date) values (18, current_timestamp() - 30, current_timestamp() - 20);
insert into pool(id, start_date, end_date) values (19, current_timestamp() - 20, current_timestamp() - 10);
insert into pool(id, start_date, end_date) values (20, current_timestamp() - 1, null); -- Current active pool (endDate is null)

-- Assignments for past pools (showing different designation outcomes)
-- Pool 1: User 1 accepted automatically
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (1, 1, 1, current_timestamp() - 200, 'ACCEPTED', 'AUTOMATIC', current_timestamp() - 200, current_timestamp() - 199, 0);

-- Pool 2: User 2 accepted manually
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (2, 2, 2, current_timestamp() - 190, 'ACCEPTED', 'MANUAL', current_timestamp() - 190, current_timestamp() - 190, 0);

-- Pool 3: User 3 declined, User 4 accepted
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (3, 3, 3, current_timestamp() - 180, 'DECLINED', 'AUTOMATIC', current_timestamp() - 180, current_timestamp() - 179, 0);
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (4, 4, 3, current_timestamp() - 179, 'ACCEPTED', 'AUTOMATIC', current_timestamp() - 179, current_timestamp() - 178, 0);

-- Pool 4: Broadcast model - User 6 accepted first, User 5 and 7 invalidated
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (5, 5, 4, current_timestamp() - 170, 'INVALIDATED', 'AUTOMATIC', current_timestamp() - 170, current_timestamp() - 168, 0);
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (6, 6, 4, current_timestamp() - 170, 'ACCEPTED', 'AUTOMATIC', current_timestamp() - 170, current_timestamp() - 169, 0);
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (7, 7, 4, current_timestamp() - 170, 'INVALIDATED', 'AUTOMATIC', current_timestamp() - 170, current_timestamp() - 168, 0);

-- Pool 5: User 8 accepted
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (8, 8, 5, current_timestamp() - 160, 'ACCEPTED', 'AUTOMATIC', current_timestamp() - 160, current_timestamp() - 159, 0);

-- Pool 19: User 10 accepted (most recent ended pool)
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (10, 10, 19, current_timestamp() - 20, 'ACCEPTED', 'AUTOMATIC', current_timestamp() - 20, current_timestamp() - 19, 0);

-- Current Pool (Pool 20) - Active pool with mixed states showing the new model
-- User 1 has accepted designation and is the current lead
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (11, 1, 20, current_timestamp() - 1, 'ACCEPTED', 'AUTOMATIC', current_timestamp() - 1, current_timestamp(), 0);

-- Users 2-10 are participants (joined pool) but not yet designated
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (12, 2, 20, current_timestamp() - 1, null, null, null, null, 0);
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (13, 3, 20, current_timestamp() - 1, null, null, null, null, 0);
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (14, 4, 20, current_timestamp() - 1, null, null, null, null, 0);
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (15, 5, 20, current_timestamp() - 1, null, null, null, null, 0);
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (16, 6, 20, current_timestamp() - 1, null, null, null, null, 0);
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (17, 7, 20, current_timestamp() - 1, null, null, null, null, 0);
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (18, 8, 20, current_timestamp() - 1, null, null, null, null, 0);
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (19, 9, 20, current_timestamp() - 1, null, null, null, null, 0);
insert into assignment(id, user_id, pool_id, assignment_date, designation_status, designation_type, designated_at, responded_at, version)
values (20, 10, 20, current_timestamp() - 1, null, null, null, null, 0);

-- Users 11-30 are candidates (not yet in the pool)

-- Browser push notification subscription
insert into subscription(id, endpoint, auth, p256dh)
values (1,'https://fcm.googleapis.com/fcm/send/euk_xuj3yc0:APA91bGxe51K6d9ISyYnBDsb8lAmO2LsVGgXogF5kgxyn4wxPehuqlZuwtZNf_tKKWMheskVFy79IPqeWTsE1Y10jRhbEMeKpFBVbMJ_u2M1XlHtWZULOdzkxOTxbsQCSxQ_TX5Y2wMV','04pI3N9c5uS3O9yxoev0WA','BAG6aC0Y_R-Lp3qCUgDI9_YFD_kFfk9hw6OW_vMgGzdUciP0qJuq4EgvIvGLsiVMJiKlwAeaeg9xhyPwbMskuT8');

-- Configuration data (migrated from application.yml)
insert into configuration(id, config_key, config_value, description)
values (1, 'rotation.cron-expression', '0 0 10 ? * *', 'Cron expression for rotation schedule (Every day at 10 AM)');
insert into configuration(id, config_key, config_value, description)
values (2, 'rotation.stale-req-check-time-in-mins', '30', 'Time in minutes before a designation request is considered stale');
insert into configuration(id, config_key, config_value, description)
values (3, 'browser-push-notification.public-key', 'BOsT9tYV7LuR21EAs5AZ6qCwb9zsD8RRApInBOwVJwD5EOzYfoyPfbP0M0CYXI14lscch43JSiPO45b-B_n-dJo', 'Public key for browser push notifications');
insert into configuration(id, config_key, config_value, description)
values (4, 'browser-push-notification.private-key', '7W5kxKIZUZ8yysvSUbW2mtNPou9HmpajsES2JRmAZO0', 'Private key for browser push notifications');
insert into configuration(id, config_key, config_value, description)
values (5, 'browser-push-notification.subject', 'Designation Event', 'Subject for browser push notifications');
