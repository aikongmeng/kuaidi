ALTER TABLE customer_list ADD user_id VARCHAR;
ALTER TABLE customer_tobedeleted_id_list ADD user_id VARCHAR;
DELETE from customer_list;
DELETE from customer_tobedeleted_id_list;