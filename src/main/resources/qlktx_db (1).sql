-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jun 02, 2026 at 09:31 AM
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
-- Database: `qlktx_db`
--

-- --------------------------------------------------------

--
-- Table structure for table `contracts`
--

CREATE TABLE `contracts` (
  `id` bigint(20) NOT NULL,
  `amount` double DEFAULT NULL,
  `end_date` date DEFAULT NULL,
  `registration_id` bigint(20) DEFAULT NULL,
  `reject_reason` varchar(255) DEFAULT NULL,
  `start_date` date DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `room_id` bigint(20) NOT NULL,
  `student_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Dumping data for table `contracts`
--

INSERT INTO `contracts` (`id`, `amount`, `end_date`, `registration_id`, `reject_reason`, `start_date`, `status`, `room_id`, `student_id`) VALUES
(1, NULL, '2026-10-01', NULL, NULL, '2026-04-01', 'ACTIVE', 1, 1),
(2, NULL, '2026-11-01', NULL, NULL, '2026-05-01', 'ACTIVE', 1, 2),
(3, NULL, '2026-12-01', NULL, NULL, '2026-05-17', 'ACTIVE', 2, 3);

-- --------------------------------------------------------

--
-- Table structure for table `conversations`
--

CREATE TABLE `conversations` (
  `id` bigint(20) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `last_message_preview` varchar(500) DEFAULT NULL,
  `status` enum('ARCHIVED','CLOSED','OPEN') NOT NULL,
  `unread_count` bigint(20) NOT NULL,
  `updated_at` datetime(6) NOT NULL,
  `user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

-- --------------------------------------------------------

--
-- Table structure for table `invoices`
--

CREATE TABLE `invoices` (
  `id` bigint(20) NOT NULL,
  `amount` double DEFAULT NULL,
  `create_date` date DEFAULT NULL,
  `description` varchar(255) DEFAULT NULL,
  `due_date` date DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `student_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Dumping data for table `invoices`
--

INSERT INTO `invoices` (`id`, `amount`, `create_date`, `description`, `due_date`, `status`, `student_id`) VALUES
(1, 500000, '2026-06-01', 'Tiền phòng tháng hiện tại', NULL, 'PENDING', 1),
(2, 500000, '2026-05-01', 'Tiền phòng tháng trước', NULL, 'PAID', 2);

-- --------------------------------------------------------

--
-- Table structure for table `messages`
--

CREATE TABLE `messages` (
  `id` bigint(20) NOT NULL,
  `content` varchar(2000) NOT NULL,
  `created_at` datetime(6) NOT NULL,
  `read_flag` bit(1) NOT NULL,
  `conversation_id` bigint(20) NOT NULL,
  `sender_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

-- --------------------------------------------------------

--
-- Table structure for table `notifications`
--

CREATE TABLE `notifications` (
  `id` bigint(20) NOT NULL,
  `content` text NOT NULL,
  `create_date` datetime(6) DEFAULT NULL,
  `is_read` bit(1) NOT NULL,
  `target_role` varchar(255) DEFAULT NULL,
  `target_user_id` bigint(20) DEFAULT NULL,
  `title` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Dumping data for table `notifications`
--

INSERT INTO `notifications` (`id`, `content`, `create_date`, `is_read`, `target_role`, `target_user_id`, `title`) VALUES
(1, 'Yêu cầu các sinh viên nộp tiền phòng đúng hạn', '2026-05-31 15:26:39.000000', b'0', NULL, NULL, 'Thông báo thu tiền phòng'),
(2, 'Cuối tuần này sẽ tổ chức tổng vệ sinh toàn khu A1 và A2.', '2026-06-01 10:26:39.000000', b'0', NULL, NULL, 'Thông báo vệ sinh chung'),
(3, 'Sinh viên Nguyễn Văn A (SV001) xin đổi từ phòng P101 sang phòng P201. Lý do: 123123', '2026-06-01 15:59:02.000000', b'0', 'ADMIN', NULL, 'Yêu cầu đổi phòng: Nguyễn Văn A'),
(4, 'Sinh viên Nguyễn Văn A (SV001) đã báo cáo thanh toán hóa đơn: Tiền phòng tháng hiện tại. Vui lòng kiểm tra và duyệt.', '2026-06-01 15:59:07.000000', b'0', 'ADMIN', NULL, 'Yêu cầu duyệt hóa đơn: Tiền phòng tháng hiện tại'),
(5, 'nộp tiền điện\n', '2026-06-01 16:00:31.000000', b'0', 'ALL', NULL, 'tiền điện');

-- --------------------------------------------------------

--
-- Table structure for table `notification_reads`
--

CREATE TABLE `notification_reads` (
  `id` bigint(20) NOT NULL,
  `notification_id` bigint(20) NOT NULL,
  `read_at` datetime(6) NOT NULL,
  `reader_role` varchar(255) NOT NULL,
  `reader_user_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Dumping data for table `notification_reads`
--

INSERT INTO `notification_reads` (`id`, `notification_id`, `read_at`, `reader_role`, `reader_user_id`) VALUES
(1, 4, '2026-06-01 16:00:14.000000', 'ADMIN', 0),
(3, 3, '2026-06-01 16:00:14.000000', 'ADMIN', 0),
(4, 5, '2026-06-01 16:00:42.000000', 'ADMIN', 0),
(6, 5, '2026-06-01 16:00:57.000000', 'STUDENT', 1);

-- --------------------------------------------------------

--
-- Table structure for table `registrations`
--

CREATE TABLE `registrations` (
  `id` bigint(20) NOT NULL,
  `request_date` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `room_id` bigint(20) NOT NULL,
  `student_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Dumping data for table `registrations`
--

INSERT INTO `registrations` (`id`, `request_date`, `status`, `room_id`, `student_id`) VALUES
(1, '2026-05-30 15:26:39.000000', 'PENDING', 4, 5);

-- --------------------------------------------------------

--
-- Table structure for table `rooms`
--

CREATE TABLE `rooms` (
  `id` bigint(20) NOT NULL,
  `building` varchar(255) NOT NULL,
  `current_capacity` int(11) NOT NULL,
  `lock_reason` varchar(255) DEFAULT NULL,
  `is_locked` tinyint(1) DEFAULT 0,
  `max_capacity` int(11) NOT NULL,
  `price` double NOT NULL,
  `room_code` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Dumping data for table `rooms`
--

INSERT INTO `rooms` (`id`, `building`, `current_capacity`, `lock_reason`, `is_locked`, `max_capacity`, `price`, `room_code`) VALUES
(1, 'A1', 2, NULL, 0, 4, 500000, 'P101'),
(2, 'A1', 1, NULL, 0, 4, 500000, 'P102'),
(3, 'A2', 1, NULL, 0, 6, 400000, 'P201'),
(4, 'A2', 0, NULL, 0, 6, 400000, 'P202'),
(5, 'c1', 0, NULL, 0, 4, 2500000, '203');

-- --------------------------------------------------------

--
-- Table structure for table `room_transfer_requests`
--

CREATE TABLE `room_transfer_requests` (
  `id` bigint(20) NOT NULL,
  `reason` text DEFAULT NULL,
  `reject_reason` varchar(255) DEFAULT NULL,
  `request_date` datetime(6) DEFAULT NULL,
  `status` varchar(255) DEFAULT NULL,
  `current_room_id` bigint(20) NOT NULL,
  `requested_room_id` bigint(20) NOT NULL,
  `student_id` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Dumping data for table `room_transfer_requests`
--

INSERT INTO `room_transfer_requests` (`id`, `reason`, `reject_reason`, `request_date`, `status`, `current_room_id`, `requested_room_id`, `student_id`) VALUES
(1, '123123', NULL, '2026-06-01 15:59:02.000000', 'PENDING', 1, 3, 1);

-- --------------------------------------------------------

--
-- Table structure for table `students`
--

CREATE TABLE `students` (
  `id` bigint(20) NOT NULL,
  `class_name` varchar(255) NOT NULL,
  `email` varchar(255) DEFAULT NULL,
  `full_name` varchar(255) NOT NULL,
  `phone` varchar(255) NOT NULL,
  `student_code` varchar(255) NOT NULL,
  `room_id` bigint(20) DEFAULT NULL,
  `user_id` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Dumping data for table `students`
--

INSERT INTO `students` (`id`, `class_name`, `email`, `full_name`, `phone`, `student_code`, `room_id`, `user_id`) VALUES
(1, 'K65-CNTT', 's1@gmail.com', 'Nguyễn Văn A', '0987654321', 'SV001', 1, 2),
(2, 'K65-KHMT', 's2@gmail.com', 'Trần Thị B', '0987654322', 'SV002', 1, 3),
(3, 'K64-KTPM', 's3@gmail.com', 'Lê Văn C', '0987654323', 'SV003', 2, 4),
(4, 'K66-HTTT', 's4@gmail.com', 'Phạm Thị D', '0987654324', 'SV004', 3, 5),
(5, 'K65-CNTT', 's5@gmail.com', 'Hoàng Văn E', '0987654325', 'SV005', NULL, 6);

-- --------------------------------------------------------

--
-- Table structure for table `users`
--

CREATE TABLE `users` (
  `id` bigint(20) NOT NULL,
  `password` varchar(255) NOT NULL,
  `role` enum('ADMIN','STUDENT') NOT NULL,
  `username` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_vietnamese_ci;

--
-- Dumping data for table `users`
--

INSERT INTO `users` (`id`, `password`, `role`, `username`) VALUES
(1, '$2a$10$R7JY10QPU5QAUi/3dMqGu.TYAGscHHTbzFVqXNg4RGGUc6KThr2/G', 'ADMIN', 'admin'),
(2, '$2a$10$VpuBoFXG.6boEGFRvaj51.V8hd9KqzzIQfyzjThCcnV8ccbHGSngO', 'STUDENT', 'sv001'),
(3, '$2a$10$Mm6XXo/uKS5eH3.hkPWrhOzaiI5yHsKQPW9N9i/fFuYzL1ZfFj91G', 'STUDENT', 'sv002'),
(4, '$2a$10$7qnq0uV1wSbCDnovM24ExOyByMzpnqnDRHB59hsBrNncX6U5XqPwi', 'STUDENT', 'sv003'),
(5, '$2a$10$4IihgXAdfzEETvo7xJj2AeL65yU5yymHMN3uCKh3E/27RN1YrcR0O', 'STUDENT', 'sv004'),
(6, '$2a$10$.5vB9PQzdeSCHI6FZ.0Ale.5X7aO0IB3wjC0wJvzsBfWyUenlZrjC', 'STUDENT', 'sv005');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `contracts`
--
ALTER TABLE `contracts`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKju1b0xobla9t8oexrb8lpi8jq` (`room_id`),
  ADD KEY `FKrl96fbpbnd5exb9olcswofqmg` (`student_id`);

--
-- Indexes for table `conversations`
--
ALTER TABLE `conversations`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKki1ftldmdun31wg9epciw42c0` (`user_id`);

--
-- Indexes for table `invoices`
--
ALTER TABLE `invoices`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKhgr2h1f3jyw86inwynpvfb9` (`student_id`);

--
-- Indexes for table `messages`
--
ALTER TABLE `messages`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKt492th6wsovh1nush5yl5jj8e` (`conversation_id`),
  ADD KEY `FK4ui4nnwntodh6wjvck53dbk9m` (`sender_id`);

--
-- Indexes for table `notifications`
--
ALTER TABLE `notifications`
  ADD PRIMARY KEY (`id`);

--
-- Indexes for table `notification_reads`
--
ALTER TABLE `notification_reads`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_notification_reader` (`notification_id`,`reader_role`,`reader_user_id`);

--
-- Indexes for table `registrations`
--
ALTER TABLE `registrations`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKu3g0kj078iu5kymvc2h25lt` (`room_id`),
  ADD KEY `FKcmo9lk1tap4hpbawuxhb8qf94` (`student_id`);

--
-- Indexes for table `rooms`
--
ALTER TABLE `rooms`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKejc4trkinbxtajwetru2o8kdo` (`room_code`);

--
-- Indexes for table `room_transfer_requests`
--
ALTER TABLE `room_transfer_requests`
  ADD PRIMARY KEY (`id`),
  ADD KEY `FKfl44f6reopt0vfwllo700d5ap` (`current_room_id`),
  ADD KEY `FK77fjjd5weou11yt5e0axfye7n` (`requested_room_id`),
  ADD KEY `FKbmv12whanaigs0qs12jvevi9s` (`student_id`);

--
-- Indexes for table `students`
--
ALTER TABLE `students`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKcgcf3r5xk73o0etbduc1qxnol` (`student_code`),
  ADD UNIQUE KEY `UKg4fwvutq09fjdlb4bb0byp7t` (`user_id`),
  ADD KEY `FKq8l9dnbc3y02t87sh1d88408j` (`room_id`);

--
-- Indexes for table `users`
--
ALTER TABLE `users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `UKr43af9ap4edm43mmtq01oddj6` (`username`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `contracts`
--
ALTER TABLE `contracts`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=4;

--
-- AUTO_INCREMENT for table `conversations`
--
ALTER TABLE `conversations`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `invoices`
--
ALTER TABLE `invoices`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `messages`
--
ALTER TABLE `messages`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT;

--
-- AUTO_INCREMENT for table `notifications`
--
ALTER TABLE `notifications`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `notification_reads`
--
ALTER TABLE `notification_reads`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `registrations`
--
ALTER TABLE `registrations`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `rooms`
--
ALTER TABLE `rooms`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `room_transfer_requests`
--
ALTER TABLE `room_transfer_requests`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=2;

--
-- AUTO_INCREMENT for table `students`
--
ALTER TABLE `students`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=6;

--
-- AUTO_INCREMENT for table `users`
--
ALTER TABLE `users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `contracts`
--
ALTER TABLE `contracts`
  ADD CONSTRAINT `FKju1b0xobla9t8oexrb8lpi8jq` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`),
  ADD CONSTRAINT `FKrl96fbpbnd5exb9olcswofqmg` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`);

--
-- Constraints for table `conversations`
--
ALTER TABLE `conversations`
  ADD CONSTRAINT `FKpltqvfcbkql9svdqwh0hw4g1d` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`);

--
-- Constraints for table `invoices`
--
ALTER TABLE `invoices`
  ADD CONSTRAINT `FKhgr2h1f3jyw86inwynpvfb9` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`);

--
-- Constraints for table `messages`
--
ALTER TABLE `messages`
  ADD CONSTRAINT `FK4ui4nnwntodh6wjvck53dbk9m` FOREIGN KEY (`sender_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKt492th6wsovh1nush5yl5jj8e` FOREIGN KEY (`conversation_id`) REFERENCES `conversations` (`id`);

--
-- Constraints for table `registrations`
--
ALTER TABLE `registrations`
  ADD CONSTRAINT `FKcmo9lk1tap4hpbawuxhb8qf94` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`),
  ADD CONSTRAINT `FKu3g0kj078iu5kymvc2h25lt` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`);

--
-- Constraints for table `room_transfer_requests`
--
ALTER TABLE `room_transfer_requests`
  ADD CONSTRAINT `FK77fjjd5weou11yt5e0axfye7n` FOREIGN KEY (`requested_room_id`) REFERENCES `rooms` (`id`),
  ADD CONSTRAINT `FKbmv12whanaigs0qs12jvevi9s` FOREIGN KEY (`student_id`) REFERENCES `students` (`id`),
  ADD CONSTRAINT `FKfl44f6reopt0vfwllo700d5ap` FOREIGN KEY (`current_room_id`) REFERENCES `rooms` (`id`);

--
-- Constraints for table `students`
--
ALTER TABLE `students`
  ADD CONSTRAINT `FKdt1cjx5ve5bdabmuuf3ibrwaq` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`),
  ADD CONSTRAINT `FKq8l9dnbc3y02t87sh1d88408j` FOREIGN KEY (`room_id`) REFERENCES `rooms` (`id`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
