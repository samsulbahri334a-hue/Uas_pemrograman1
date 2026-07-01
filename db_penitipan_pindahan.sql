-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 27, 2026 at 06:07 AM
-- Server version: 10.4.32-MariaDB
-- PHP Version: 8.2.12

SET SQL_MODE = "NO_AUTO_VALUE_ON_ZERO";
START TRANSACTION;
SET time_zone = "+00:00";


/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!40101 SET NAMES utf8mb4 */;

--
-- Database: `db_penitipan_pindahan`
--

-- --------------------------------------------------------

--
-- Table structure for table `barang`
--

CREATE TABLE `barang` (
  `id_barang` int(11) NOT NULL,
  `id_customer` int(11) NOT NULL,
  `nama_barang` varchar(100) NOT NULL,
  `jenis_barang` varchar(50) NOT NULL,
  `berat_barang` decimal(10,2) NOT NULL,
  `status_barang` varchar(50) DEFAULT 'dalam_proses',
  `tanggal_input` timestamp NOT NULL DEFAULT current_timestamp(),
  `keterangan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `barang`
--

INSERT INTO `barang` (`id_barang`, `id_customer`, `nama_barang`, `jenis_barang`, `berat_barang`, `status_barang`, `tanggal_input`, `keterangan`) VALUES
(1, 1, 'Lemari Kayu', 'Furniture', 50.00, 'dalam_proses', '2026-05-22 08:52:13', 'Lemari warna coklat tua, kondisi baik'),
(2, 1, 'Rak Buku', 'Furniture', 25.00, 'dalam_proses', '2026-05-22 08:52:13', 'Rak buku dengan 5 tingkat'),
(3, 2, 'Kulkas LG', 'Elektronik', 80.00, 'dalam_proses', '2026-05-22 08:52:13', 'Kulkas 2 pintu berwarna putih'),
(4, 2, 'Mesin Cuci', 'Elektronik', 70.00, 'dalam_proses', '2026-05-22 08:52:13', 'Mesin cuci otomatis'),
(5, 3, 'Sofa L-Shaped', 'Furniture', 120.00, 'dalam_proses', '2026-05-22 08:52:13', 'Sofa berwarna abu-abu'),
(6, 3, 'Meja Makan', 'Furniture', 45.00, 'dalam_proses', '2026-05-22 08:52:13', 'Meja makan kayu dengan 6 kursi'),
(7, 4, 'AC Split 1PK', 'Elektronik', 30.00, 'dalam_proses', '2026-05-22 08:52:13', 'AC merk Daikin'),
(8, 5, 'Tempat Tidur', 'Furniture', 100.00, 'dalam_proses', '2026-05-22 08:52:13', 'Tempat tidur queen size');

-- --------------------------------------------------------

--
-- Table structure for table `customer`
--

CREATE TABLE `customer` (
  `id_customer` int(11) NOT NULL,
  `nama_customer` varchar(100) NOT NULL,
  `alamat` varchar(255) NOT NULL,
  `no_hp` varchar(15) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(255) NOT NULL,
  `tanggal_daftar` timestamp NOT NULL DEFAULT current_timestamp(),
  `status_customer` varchar(20) DEFAULT 'aktif'
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `customer`
--

INSERT INTO `customer` (`id_customer`, `nama_customer`, `alamat`, `no_hp`, `email`, `password`, `tanggal_daftar`, `status_customer`) VALUES
(1, 'Ahmad Ridho', 'Jl. Merdeka No. 123, Jakarta', '081234567890', 'ahmad@email.com', 'pass123456', '2026-05-22 08:52:13', 'aktif'),
(2, 'Siti Nurhaliza', 'Jl. Sudirman No. 45, Bandung', '081345678901', 'siti@email.com', 'pass123456', '2026-05-22 08:52:13', 'aktif'),
(3, 'Budi Santoso', 'Jl. Ahmad Yani No. 67, Surabaya', '081456789012', 'budi@email.com', 'pass123456', '2026-05-22 08:52:13', 'aktif'),
(4, 'Rina Wijaya', 'Jl. Gatot Subroto No. 89, Medan', '081567890123', 'rina@email.com', 'pass123456', '2026-05-22 08:52:13', 'aktif'),
(5, 'Doni Hermawan', 'Jl. Diponegoro No. 101, Yogyakarta', '081678901234', 'doni@email.com', 'pass123456', '2026-05-22 08:52:13', 'aktif');

-- --------------------------------------------------------

--
-- Table structure for table `pickup`
--

CREATE TABLE `pickup` (
  `id_pickup` int(11) NOT NULL,
  `id_transaksi` int(11) NOT NULL,
  `tanggal_pickup` date NOT NULL,
  `jam_pickup` time NOT NULL,
  `alamat_pickup` varchar(255) NOT NULL,
  `status_pickup` varchar(50) DEFAULT 'dijadwalkan',
  `nama_driver` varchar(100) DEFAULT NULL,
  `no_hp_driver` varchar(15) DEFAULT NULL,
  `kendaraan` varchar(100) DEFAULT NULL,
  `catatan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `pickup`
--

INSERT INTO `pickup` (`id_pickup`, `id_transaksi`, `tanggal_pickup`, `jam_pickup`, `alamat_pickup`, `status_pickup`, `nama_driver`, `no_hp_driver`, `kendaraan`, `catatan`) VALUES
(1, 1, '2024-01-15', '09:00:00', 'Jl. Merdeka No. 123, Jakarta', 'selesai', 'Hendra Wijaya', '082123456789', 'Truk Avanza Putih', NULL),
(2, 2, '2024-01-16', '14:00:00', 'Jl. Merdeka No. 123, Jakarta', 'selesai', 'Hendra Wijaya', '082123456789', 'Truk Avanza Putih', NULL),
(3, 3, '2024-01-18', '10:00:00', 'Jl. Sudirman No. 45, Bandung', 'dijadwalkan', 'Budi Setiawan', '082234567890', 'Truk Fuso', NULL),
(4, 4, '2024-01-20', '11:00:00', 'Jl. Sudirman No. 45, Bandung', 'dalam_perjalanan', 'Bambang Irawan', '082345678901', 'Truk Avanza Merah', NULL),
(5, 5, '2024-01-22', '08:30:00', 'Jl. Ahmad Yani No. 67, Surabaya', 'dijadwalkan', 'Rinto Harahap', '082456789012', 'Truk Canter', NULL),
(6, 6, '2024-01-25', '13:00:00', 'Jl. Ahmad Yani No. 67, Surabaya', 'dijadwalkan', 'Eka Putra', '082567890123', 'Truk Avanza Biru', NULL),
(7, 7, '2024-02-01', '09:30:00', 'Jl. Gatot Subroto No. 89, Medan', 'dalam_perjalanan', 'Santo Wijaya', '082678901234', 'Truk Pickup', NULL),
(8, 8, '2024-02-05', '10:00:00', 'Jl. Diponegoro No. 101, Yogyakarta', 'dijadwalkan', 'Iwan Suardi', '082789012345', 'Truk Fuso', NULL);

-- --------------------------------------------------------

--
-- Table structure for table `tracking`
--

CREATE TABLE `tracking` (
  `id_tracking` int(11) NOT NULL,
  `id_barang` int(11) NOT NULL,
  `tanggal_tracking` timestamp NOT NULL DEFAULT current_timestamp(),
  `lokasi_barang` varchar(255) NOT NULL,
  `status_tracking` varchar(50) NOT NULL,
  `keterangan` text DEFAULT NULL,
  `latitude` varchar(20) DEFAULT NULL,
  `longitude` varchar(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `tracking`
--

INSERT INTO `tracking` (`id_tracking`, `id_barang`, `tanggal_tracking`, `lokasi_barang`, `status_tracking`, `keterangan`, `latitude`, `longitude`) VALUES
(1, 1, '2026-05-22 08:52:13', 'Jakarta - Gudang Packing', 'tiba_gudang', 'Barang diterima dan sedang dikemas', '-6.2088', '106.8456'),
(2, 1, '2026-05-22 08:52:13', 'Jakarta - Dalam Pengiriman', 'dalam_pengiriman', 'Barang sedang dikirim ke tujuan', '-6.2088', '106.8456'),
(3, 1, '2026-05-22 08:52:13', 'Bandung', 'tiba_tujuan', 'Barang tiba di alamat tujuan', '-6.9175', '107.6062'),
(4, 2, '2026-05-22 08:52:13', 'Jakarta - Gudang Packing', 'tiba_gudang', 'Barang diterima dan sedang dikemas', '-6.2088', '106.8456'),
(5, 3, '2026-05-22 08:52:13', 'Bandung - Gudang Packing', 'tiba_gudang', 'Barang diterima dan sedang dikemas', '-6.9175', '107.6062'),
(6, 4, '2026-05-22 08:52:13', 'Bandung - Gudang Penyimpanan', 'disimpan', 'Barang sedang disimpan di gudang', '-6.9175', '107.6062'),
(7, 5, '2026-05-22 08:52:13', 'Surabaya - Dalam Pengiriman', 'dalam_pengiriman', 'Barang sedang dalam perjalanan', '-7.2575', '112.7521'),
(8, 7, '2026-05-22 08:52:13', 'Medan - Gudang Packing', 'tiba_gudang', 'Barang baru tiba di gudang', '3.5952', '98.6722'),
(9, 8, '2026-05-22 08:52:13', 'Yogyakarta - Dalam Pengiriman', 'dalam_pengiriman', 'Barang dalam perjalanan menuju tujuan', '-7.7975', '110.3695');

-- --------------------------------------------------------

--
-- Table structure for table `transaksi`
--

CREATE TABLE `transaksi` (
  `id_transaksi` int(11) NOT NULL,
  `id_customer` int(11) NOT NULL,
  `id_barang` int(11) NOT NULL,
  `jenis_layanan` varchar(50) NOT NULL,
  `tanggal_booking` timestamp NOT NULL DEFAULT current_timestamp(),
  `total_biaya` decimal(12,2) NOT NULL,
  `status_pembayaran` varchar(30) DEFAULT 'belum_dibayar',
  `metode_pembayaran` varchar(50) DEFAULT NULL,
  `tanggal_pembayaran` datetime DEFAULT NULL,
  `catatan` text DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_unicode_ci;

--
-- Dumping data for table `transaksi`
--

INSERT INTO `transaksi` (`id_transaksi`, `id_customer`, `id_barang`, `jenis_layanan`, `tanggal_booking`, `total_biaya`, `status_pembayaran`, `metode_pembayaran`, `tanggal_pembayaran`, `catatan`) VALUES
(1, 1, 1, 'Pindahan Lokal', '2026-05-22 08:52:13', 500000.00, 'sudah_dibayar', 'Transfer Bank', NULL, NULL),
(2, 1, 2, 'Pindahan Lokal', '2026-05-22 08:52:13', 300000.00, 'sudah_dibayar', 'Transfer Bank', NULL, NULL),
(3, 2, 3, 'Pindahan Antar Kota', '2026-05-22 08:52:13', 1500000.00, 'belum_dibayar', NULL, NULL, NULL),
(4, 2, 4, 'Penitipan Barang', '2026-05-22 08:52:13', 200000.00, 'sudah_dibayar', 'E-wallet', NULL, NULL),
(5, 3, 5, 'Pindahan Antar Kota', '2026-05-22 08:52:13', 2000000.00, 'sudah_dibayar', 'Transfer Bank', NULL, NULL),
(6, 3, 6, 'Pindahan Lokal', '2026-05-22 08:52:13', 800000.00, 'belum_dibayar', NULL, NULL, NULL),
(7, 4, 7, 'Pindahan Lokal', '2026-05-22 08:52:13', 400000.00, 'sudah_dibayar', 'E-wallet', NULL, NULL),
(8, 5, 8, 'Pindahan Antar Kota', '2026-05-22 08:52:13', 1200000.00, 'sudah_dibayar', 'Transfer Bank', NULL, NULL);

--
-- Indexes for dumped tables
--

--
-- Indexes for table `barang`
--
ALTER TABLE `barang`
  ADD PRIMARY KEY (`id_barang`),
  ADD KEY `idx_barang_id_customer` (`id_customer`),
  ADD KEY `idx_barang_status` (`status_barang`);

--
-- Indexes for table `customer`
--
ALTER TABLE `customer`
  ADD PRIMARY KEY (`id_customer`),
  ADD UNIQUE KEY `no_hp` (`no_hp`),
  ADD UNIQUE KEY `email` (`email`),
  ADD KEY `idx_customer_email` (`email`),
  ADD KEY `idx_customer_no_hp` (`no_hp`);

--
-- Indexes for table `pickup`
--
ALTER TABLE `pickup`
  ADD PRIMARY KEY (`id_pickup`),
  ADD KEY `idx_pickup_id_transaksi` (`id_transaksi`),
  ADD KEY `idx_pickup_status` (`status_pickup`);

--
-- Indexes for table `tracking`
--
ALTER TABLE `tracking`
  ADD PRIMARY KEY (`id_tracking`),
  ADD KEY `idx_tracking_id_barang` (`id_barang`),
  ADD KEY `idx_tracking_status` (`status_tracking`),
  ADD KEY `idx_tracking_tanggal` (`tanggal_tracking`);

--
-- Indexes for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD PRIMARY KEY (`id_transaksi`),
  ADD KEY `idx_transaksi_id_customer` (`id_customer`),
  ADD KEY `idx_transaksi_id_barang` (`id_barang`),
  ADD KEY `idx_transaksi_status_pembayaran` (`status_pembayaran`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `barang`
--
ALTER TABLE `barang`
  MODIFY `id_barang` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `customer`
--
ALTER TABLE `customer`
  MODIFY `id_customer` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `pickup`
--
ALTER TABLE `pickup`
  MODIFY `id_pickup` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `tracking`
--
ALTER TABLE `tracking`
  MODIFY `id_tracking` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `transaksi`
--
ALTER TABLE `transaksi`
  MODIFY `id_transaksi` int(11) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `barang`
--
ALTER TABLE `barang`
  ADD CONSTRAINT `barang_ibfk_1` FOREIGN KEY (`id_customer`) REFERENCES `customer` (`id_customer`) ON DELETE CASCADE;

--
-- Constraints for table `pickup`
--
ALTER TABLE `pickup`
  ADD CONSTRAINT `pickup_ibfk_1` FOREIGN KEY (`id_transaksi`) REFERENCES `transaksi` (`id_transaksi`) ON DELETE CASCADE;

--
-- Constraints for table `tracking`
--
ALTER TABLE `tracking`
  ADD CONSTRAINT `tracking_ibfk_1` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id_barang`) ON DELETE CASCADE;

--
-- Constraints for table `transaksi`
--
ALTER TABLE `transaksi`
  ADD CONSTRAINT `transaksi_ibfk_1` FOREIGN KEY (`id_customer`) REFERENCES `customer` (`id_customer`) ON DELETE CASCADE,
  ADD CONSTRAINT `transaksi_ibfk_2` FOREIGN KEY (`id_barang`) REFERENCES `barang` (`id_barang`) ON DELETE CASCADE;
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
