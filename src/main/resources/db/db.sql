-- Tabel CUSTOMER
CREATE TABLE customer (
                          id_customer VARCHAR(50) PRIMARY KEY,
                          nama VARCHAR(100) NOT NULL,
                          no_telp VARCHAR(15),
                          created_at TIMESTAMP,
                          updated_at TIMESTAMP,
                          deleted_at TIMESTAMP
);

CREATE TABLE pesanan (
                         id_pesanan VARCHAR(36) PRIMARY KEY,
                         id_customer VARCHAR(36) NOT NULL, -- Foreign key untuk relasi ke tabel customer
                         no_faktur VARCHAR(5) NOT NULL,
                         tipe_cucian ENUM('Super Express 3 Jam Komplit', 'Express 1 Hari', 'Standar 2 Hari', 'Reguler 3 Hari', 'Laundry Satuan') NOT NULL,
                         jenis_cucian ENUM('Komplit', 'Cuci Lipat', 'Setrika') NOT NULL,
                         jenis_barang VARCHAR(100) NULL,
                         qty DOUBLE NULL,
                         harga DOUBLE NOT NULL,
                         tipe_pembayaran ENUM('Cash', 'QRIS') NOT NULL,
                         status_bayar ENUM('Belum Lunas', 'Lunas') NOT NULL,
                         status_order ENUM('Pickup', 'Cuci', 'Selesai') NOT NULL,
                         tgl_masuk TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                         tgl_selesai TIMESTAMP NULL,
                         catatan VARCHAR(255) NULL,
                         deleted_at TIMESTAMP NULL,

                         CONSTRAINT fk_customer
                             FOREIGN KEY (id_customer)
                                 REFERENCES customer(id_customer)
                                 ON UPDATE CASCADE
                                 ON DELETE cascade
);


CREATE TABLE detail_pesanan_satuan (
                                       id_detail VARCHAR(36) PRIMARY KEY,
                                       id_customer VARCHAR(36) NOT NULL,
                                       no_faktur VARCHAR(9) NOT NULL,
                                       tipe_pembayaran ENUM('Cash', 'QRIS') NOT NULL,
                                       status_bayar ENUM('Belum Lunas', 'Lunas') NOT NULL,
                                       status_order ENUM('Pickup', 'Cuci', 'Selesai') NOT NULL,
                                       tgl_masuk TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
                                       tgl_selesai TIMESTAMP NULL,
                                       catatan VARCHAR(255) NULL,
                                       deleted_at TIMESTAMP NULL,

                                       CONSTRAINT fk_customer_satuan
                                           FOREIGN KEY (id_customer)
                                               REFERENCES customer(id_customer)
                                               ON UPDATE CASCADE
                                               ON DELETE CASCADE
);


CREATE TABLE pesanan_satuan (
                                id_pesanan_satuan VARCHAR(36) PRIMARY KEY,
                                id_detail VARCHAR(36) NOT NULL,
                                kategori_barang ENUM('Bed Cover', 'Boneka', 'Bantal/Guling') NOT NULL,
                                ukuran ENUM('Kecil', 'Sedang', 'Besar') NOT NULL,
                                jenis_layanan ENUM('Standar 3 Hari', 'Express 1 Hari') NOT NULL,
                                harga DOUBLE NOT NULL,
                                qty DOUBLE NOT NULL,

                                FOREIGN KEY (id_detail)
                                    REFERENCES detail_pesanan_satuan(id_detail)
                                    ON UPDATE CASCADE
                                    ON DELETE CASCADE
);