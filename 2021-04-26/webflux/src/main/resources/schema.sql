create table if not exists  producer (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    street VARCHAR(255),
    zip VARCHAR(255),
    city VARCHAR(255)
);

create table if not exists  product (
    id INTEGER NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(255),
    price DECIMAL(10,2),
    producer_id INTEGER NOT NULL,
    constraint fk_producer FOREIGN KEY (producer_id) references producer(id)
    on delete cascade
    on update restrict
);
