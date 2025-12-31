CREATE UNIQUE INDEX uc_payments_order_id ON payments(order_id);
CREATE UNIQUE INDEX uc_payments_user_id ON payments(user_id);
CREATE UNIQUE INDEX uc_payments_status ON payments(status);
CREATE UNIQUE INDEX uc_payments_time_stamp ON payments(time_stamp);