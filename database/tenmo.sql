BEGIN TRANSACTION;

DROP TABLE IF EXISTS tenmo_user, account, transactions, transaction_status;

DROP SEQUENCE IF EXISTS seq_user_id, seq_account_id;

-- Sequence to start user_id values at 1001 instead of 1
CREATE SEQUENCE seq_user_id
  INCREMENT BY 1
  START WITH 1001
  NO MAXVALUE;

CREATE TABLE tenmo_user (
	user_id int NOT NULL DEFAULT nextval('seq_user_id'),
	username varchar(50) NOT NULL,
	password_hash varchar(200) NOT NULL,
	CONSTRAINT PK_tenmo_user PRIMARY KEY (user_id),
	CONSTRAINT UQ_username UNIQUE (username)
); 
-- Sequence to start account_id values at 2001 instead of 1
-- Note: Use similar sequences with unique starting values for additional tables
CREATE SEQUENCE seq_account_id
  INCREMENT BY 1
  START WITH 2001
  NO MAXVALUE;

CREATE TABLE account (
	account_id int NOT NULL DEFAULT nextval('seq_account_id'),
	user_id int NOT NULL,
	balance decimal(13, 2) NOT NULL,
	CONSTRAINT PK_account PRIMARY KEY (account_id),
	CONSTRAINT FK_account_tenmo_user FOREIGN KEY (user_id) REFERENCES tenmo_user (user_id)
);

CREATE TABLE transactions (
	transaction_id serial NOT NULL, 
	account_out int NOT null, 
	account_in int NOT null,
	amount decimal (13, 2) NOT NULL,
	is_requesting boolean NOT NULL,
	CONSTRAINT PK_transactions PRIMARY KEY (transaction_id)
);

CREATE TABLE transaction_status (
	status_id serial NOT NULL,
	transaction_id serial NOT NULL, 
	status boolean DEFAULT null,
	CONSTRAINT PK_transaction_status PRIMARY KEY (status_id),
	CONSTRAINT FK_transaction_status_transaction FOREIGN KEY (transaction_id) REFERENCES transactions (transaction_id)
); 

ALTER TABLE transactions ADD CONSTRAINT FK_transactions_account_from FOREIGN KEY (account_in) REFERENCES account(account_id); 
ALTER TABLE transactions ADD CONSTRAINT FK_transactions_account_to FOREIGN KEY (account_out) REFERENCES account(account_id);

COMMIT;
