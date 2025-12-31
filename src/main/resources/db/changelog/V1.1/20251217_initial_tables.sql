--liquibase formatted sql

--changeset LizavetaLiakh:pmt1_tables
CREATE TABLE payments(
    id BIGSERIAL PRIMARY KEY,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status VARCHAR(15) NOT NULL,
    time_stamp TIMESTAMP NOT NULL,
    payment_amount DECIMAL(10, 2) NOT NULL
);