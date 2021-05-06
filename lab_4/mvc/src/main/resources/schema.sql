DROP TABLE IF EXISTS product;
CREATE OR REPLACE SEQUENCE pk_sequence START WITH 1 INCREMENT BY 1;

CREATE TABLE product(
     id BIGINT auto_increment,
     name VARCHAR(255) NOT NULL,
     category VARCHAR(255) NOT NULL,
     price DECIMAL(15,2) NOT NULL,
     quantity BIGINT NOT NULL,
     version BIGINT NOT NULL,
     primary key (id)
);