-- Populate Database with
-- -- 3 customers
-- -- 3 wallets (1 per customer)
-- -- -- 2 transaction (from Customer1 to Customer2, from Customer1 to Customer3)
--- passwords: Alices_password1
--              Hares_password1
--              Hatters_password1
insert into user(id, username, password, email, is_enabled, roles) values(pk_sequence.nextval, 'alice_in_wonderland',
                                                                          '{bcrypt}$2a$10$PBp/YvYi0NoYnG/erDcn4uHSREj0cYmDAgl4yWD86mnSPcFRg1NMe', 'alice_inwonderland@mail.com',
                                                                          1, 'CUSTOMER');
insert into user(id, username, password, email, is_enabled, roles) values(pk_sequence.nextval, 'the_march_hare',
                                                                          '{bcrypt}$2a$10$2LK2qJA8A85zLGZNEtAnQuy2l5ts5WlGvZiA/VO7iyEwFB8NE2QeS', 'march_hare@mail.com',
                                                                         1, 'CUSTOMER');

insert into user(id, username, password, email, is_enabled, roles) values(pk_sequence.nextval, 'mad_hatter',
                                                                         '{bcrypt}$2a$10$ZpJLsDK9JDGtwGfVA0x79eaz.2Duue/9N2hFm0LaLbPDJJNGWdGgS', 'mad_hatter@mail.com',
                                                                         1, 'CUSTOMER');

insert into customer(id, name, surname, address, email, user) values(pk_sequence.nextval, 'Alice', 'Pleasance Liddell', 'Wonderland, 2', 'alice_inwonderland@mail.com', 1);
insert into customer(id, name, surname, address, email, user) values(pk_sequence.nextval, 'The March', 'Hare', 'Dining Table, 1', 'march_hare@mail.com', 2);
insert into customer(id, name, surname, address, email, user) values(pk_sequence.nextval, 'The Mad', 'Hatter', 'Dining Table, 2', 'mad_hatter@mail.com', 3);
insert into wallet(id, balance, customer) values(pk_sequence.nextval, 1000, 4);
insert into wallet(id, balance, customer) values(pk_sequence.nextval, 750, 5);
insert into wallet(id, balance, customer) values(pk_sequence.nextval, 500, 6);
insert into transaction(id, amount, timestamp, sender, receiver) values (pk_sequence.nextval, 50, '2020-01-01 00:00:00', 7, 8);
insert into transaction(id, amount, timestamp, sender, receiver) values (pk_sequence.nextval, 250, '2020-01-01 00:00:00', 7, 9);
