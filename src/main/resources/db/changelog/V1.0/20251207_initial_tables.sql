--liquibase formatted sql

--changeset LizavetaLiakh:pmt1_tables
CREATE TABLE payments(
    id BIGINT PRIMARY KEY NOT NULL,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(15) NOT NULL,
    time_stamp TIMESTAMP NOT NULL,
    payment_amount DECIMAL(10, 2) NOT NULL
);