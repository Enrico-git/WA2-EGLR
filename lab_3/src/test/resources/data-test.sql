-- Populate Database with
-- -- 3 customers
-- -- 3 wallets (1 per customer)
-- -- -- 2 transaction (from Customer1 to Customer2, from Customer1 to Customer3)
-- insert into user(id, username, password, email, is_enabled, roles) values(pk_sequence.nextval, 'alice_in_wonderland',
--                                                                          'alices_password', 'alice_inwonderland@mail.com',
--                                                                          0, 'CUSTOMER');
insert into user(id, username, password, email, is_enabled, roles) values(pk_sequence.nextval, 'alice_in_wonderland',
                                                                         '$2y$12$wkxtInM./JlzaTnFAL8edukEf5xt/7tSEc2BCHEd0UYqy.tnphv4m', 'alice_inwonderland@mail.com',
                                                                         0, 'CUSTOMER');
insert into user(id, username, password, email, is_enabled, roles) values(pk_sequence.nextval, 'the_march_hare',
                                                                         'hares_password', 'march_hare@mail.com',
                                                                         0, 'CUSTOMER');
insert into user(id, username, password, email, is_enabled, roles) values(pk_sequence.nextval, 'mad_hatter',
                                                                         'hatters_password', 'mad_hatter@mail.com',
                                                                         0, 'CUSTOMER');

insert into customer(id, name, surname, address, email, user) values(pk_sequence.nextval, 'Alice', 'Pleasance Liddell', 'Wonderland, 2', 'alice_inwonderland@mail.com', 1);
insert into customer(id, name, surname, address, email, user) values(pk_sequence.nextval, 'The March', 'Hare', 'Dining Table, 1', 'march_hare@mail.com', 2);
insert into customer(id, name, surname, address, email, user) values(pk_sequence.nextval, 'The Mad', 'Hatter', 'Dining Table, 2', 'mad_hatter@mail.com', 3);
insert into wallet(id, balance, customer) values(pk_sequence.nextval, 1000, 4);
insert into wallet(id, balance, customer) values(pk_sequence.nextval, 750, 5);
insert into wallet(id, balance, customer) values(pk_sequence.nextval, 500, 6);
insert into transaction(id, amount, timestamp, sender, receiver) values (pk_sequence.nextval, 50, '2020-01-01 00:00:00', 7, 8);
insert into transaction(id, amount, timestamp, sender, receiver) values (pk_sequence.nextval, 250, '2020-01-01 00:00:00', 7, 9);
