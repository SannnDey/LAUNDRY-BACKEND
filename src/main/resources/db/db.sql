-- Tabel CUSTOMER
CREATE TABLE customer (
                          id_customer VARCHAR(50) PRIMARY KEY,
                          nama VARCHAR(100) NOT NULL,
                          no_telp VARCHAR(15),
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP,
                          deleted_at TIMESTAMP
);