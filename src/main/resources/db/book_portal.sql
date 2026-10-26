-- phpMyAdmin SQL Dump
-- version 5.2.1
-- https://www.phpmyadmin.net/
--
-- Host: 127.0.0.1
-- Generation Time: Jul 01, 2026 at 04:13 PM
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
-- Database: `book_portal`
--

-- --------------------------------------------------------

--
-- Table structure for table `app_apis`
--

CREATE TABLE `app_apis` (
  `apis_pk` bigint(20) NOT NULL,
  `api_url` varchar(255) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `app_apis`
--

INSERT INTO `app_apis` (`apis_pk`, `api_url`) VALUES
(30, '/admin/apis'),
(31, '/admin/apis/add'),
(33, '/admin/apis/delete'),
(32, '/admin/apis/edit/**'),
(14, '/admin/authors'),
(15, '/admin/authors/add'),
(17, '/admin/authors/delete'),
(16, '/admin/authors/edit/**'),
(26, '/admin/distributors'),
(27, '/admin/distributors/add'),
(29, '/admin/distributors/delete'),
(28, '/admin/distributors/edit/**'),
(5, '/admin/panel'),
(56, '/admin/privileges/**'),
(57, '/admin/privileges/api/whoami'),
(22, '/admin/publishers'),
(23, '/admin/publishers/add'),
(25, '/admin/publishers/delete'),
(24, '/admin/publishers/edit/**'),
(18, '/admin/user-types'),
(19, '/admin/user-types/add'),
(21, '/admin/user-types/delete'),
(20, '/admin/user-types/edit/**'),
(4, '/admin/users'),
(2, '/admin/users/add'),
(3, '/admin/users/delete'),
(1, '/admin/users/edit/**'),
(6, '/advanced-search'),
(7, '/advanced-search-author'),
(8, '/advanced-search-publisher'),
(9, '/advanced-search-title'),
(45, '/api/search'),
(42, '/author'),
(48, '/authors'),
(49, '/authors/**'),
(46, '/books'),
(47, '/books/search'),
(50, '/categories'),
(51, '/categories/**'),
(11, '/change-password'),
(52, '/compare'),
(10, '/dashboard'),
(13, '/edit-profile'),
(37, '/forgot'),
(38, '/forgot/login-info'),
(35, '/login'),
(36, '/logout'),
(43, '/publisher'),
(53, '/publishers'),
(54, '/publishers/**'),
(34, '/register'),
(41, '/search-author'),
(39, '/search-publisher'),
(40, '/search-title'),
(55, '/search/**'),
(44, '/title'),
(12, '/user-options');

-- --------------------------------------------------------

--
-- Table structure for table `app_groups`
--

CREATE TABLE `app_groups` (
  `groups_pk` bigint(20) NOT NULL,
  `group_name` varchar(100) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `app_groups`
--

INSERT INTO `app_groups` (`groups_pk`, `group_name`) VALUES
(1, 'ADMIN_GROUP'),
(2, 'USER_GROUP');

-- --------------------------------------------------------

--
-- Table structure for table `app_groups_apis`
--

CREATE TABLE `app_groups_apis` (
  `id` bigint(20) NOT NULL,
  `groups_fk` bigint(20) NOT NULL,
  `apis_fk` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `app_groups_apis`
--

INSERT INTO `app_groups_apis` (`id`, `groups_fk`, `apis_fk`) VALUES
(25, 1, 1),
(23, 1, 2),
(24, 1, 3),
(22, 1, 4),
(13, 1, 5),
(26, 1, 6),
(27, 1, 7),
(28, 1, 8),
(29, 1, 9),
(40, 1, 10),
(38, 1, 11),
(54, 1, 12),
(41, 1, 13),
(5, 1, 14),
(6, 1, 15),
(8, 1, 16),
(7, 1, 17),
(18, 1, 18),
(19, 1, 19),
(21, 1, 20),
(20, 1, 21),
(14, 1, 22),
(15, 1, 23),
(17, 1, 24),
(16, 1, 25),
(9, 1, 26),
(10, 1, 27),
(12, 1, 28),
(11, 1, 29),
(1, 1, 30),
(2, 1, 31),
(4, 1, 32),
(3, 1, 33),
(49, 1, 34),
(44, 1, 35),
(45, 1, 36),
(42, 1, 37),
(43, 1, 38),
(51, 1, 39),
(52, 1, 40),
(50, 1, 41),
(31, 1, 42),
(46, 1, 43),
(53, 1, 44),
(30, 1, 45),
(34, 1, 46),
(35, 1, 47),
(32, 1, 48),
(33, 1, 49),
(36, 1, 50),
(37, 1, 51),
(39, 1, 52),
(47, 1, 53),
(48, 1, 54),
(81, 1, 55),
(83, 1, 56),
(84, 1, 57),
(64, 2, 6),
(65, 2, 7),
(66, 2, 8),
(67, 2, 9),
(76, 2, 10),
(74, 2, 11),
(80, 2, 12),
(77, 2, 13),
(70, 2, 46),
(71, 2, 47),
(68, 2, 48),
(69, 2, 49),
(72, 2, 50),
(73, 2, 51),
(75, 2, 52),
(78, 2, 53),
(79, 2, 54),
(82, 2, 55);

-- --------------------------------------------------------

--
-- Table structure for table `app_groups_users`
--

CREATE TABLE `app_groups_users` (
  `id` bigint(20) NOT NULL,
  `groups_fk` bigint(20) NOT NULL,
  `users_fk` bigint(20) NOT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `app_groups_users`
--

INSERT INTO `app_groups_users` (`id`, `groups_fk`, `users_fk`) VALUES
(1, 1, 1),
(2, 1, 3),
(8, 2, 5),
(9, 2, 7);

-- --------------------------------------------------------

--
-- Table structure for table `app_users`
--

CREATE TABLE `app_users` (
  `ID` bigint(20) NOT NULL,
  `FIRST_NAME` varchar(100) DEFAULT NULL,
  `LAST_NAME` varchar(100) DEFAULT NULL,
  `EMAIL` varchar(100) DEFAULT NULL,
  `USER_NAME` varchar(100) DEFAULT NULL,
  `PASSWORD` varchar(100) DEFAULT NULL,
  `SECRET_QUESTION_ID` bigint(20) DEFAULT NULL,
  `SECRET_ANSWER` varchar(100) DEFAULT NULL,
  `USER_TYPE_ID` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `app_users`
--

INSERT INTO `app_users` (`ID`, `FIRST_NAME`, `LAST_NAME`, `EMAIL`, `USER_NAME`, `PASSWORD`, `SECRET_QUESTION_ID`, `SECRET_ANSWER`, `USER_TYPE_ID`) VALUES
(1, 'Adminn', 'User', 'adminuser@gmail.com', 'admin', '$2a$10$2dk4yZQopa.b.o.OHQOcvex2efQHH1hKpH.77xT.WQFnbfJymVM9C', 1, 'Arya School', 1),
(3, 'Ritesh', 'Sharma', 'riteshsharma@gmail.com', 'ritesh', '$2a$10$zUBmgLWif0JO0WWLykHAi.NkoI5rHUQkWI3YN6ow7t5trIjCdZmuu', 3, 'Jaipur', 1),
(5, 'Ashley', 'Jonson', 'ashley@gmail.com', 'ashley', '$2a$10$RvXv1E0PD00FLyaBZBh1JOX75UWHCdWjaVphU6IxvUsKbTacd9vAu', 3, 'Washington', 2),
(7, 'romania', 'james', 'romaniajames@gmail.com', 'roman', '$2a$10$iWe5woqFIKeA42TgVrmCSeRA8zRMla4iIkaCLf0w34fUQBxqx5tx6', 5, '9123456789', 2);

-- --------------------------------------------------------

--
-- Table structure for table `author`
--

CREATE TABLE `author` (
  `ID` bigint(20) NOT NULL,
  `FIRST_NAME` varchar(100) DEFAULT NULL,
  `LAST_NAME` varchar(100) DEFAULT NULL,
  `EMAIL` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `author`
--

INSERT INTO `author` (`ID`, `FIRST_NAME`, `LAST_NAME`, `EMAIL`) VALUES
(1, 'Johns', 'Doeth', 'john.doe@gmail.com'),
(2, 'Emily', 'Smith', 'emily.smith@gmail.com'),
(3, 'Robert', 'Brown', 'robert.brown@gmail.com'),
(4, 'James', 'Gosling', 'james@gmail.com'),
(5, 'Joshua', 'Bloch', 'bloch@gmail.com'),
(6, 'Robert', 'Martin', 'unclebob@gmail.com'),
(7, 'Elons', 'Musk', 'elon@gmail.com');

-- --------------------------------------------------------

--
-- Table structure for table `book`
--

CREATE TABLE `book` (
  `ID` bigint(20) NOT NULL,
  `TITLE` varchar(100) DEFAULT NULL,
  `BOOK_CATEGORY_ID` bigint(20) DEFAULT NULL,
  `THUMBNAIL_PATH` varchar(100) DEFAULT NULL,
  `IMAGE_PATH` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `book`
--

INSERT INTO `book` (`ID`, `TITLE`, `BOOK_CATEGORY_ID`, `THUMBNAIL_PATH`, `IMAGE_PATH`) VALUES
(1, 'Spring Boot in Action', 1, '/thumb/spring.jpg', '/images/spring.jpg'),
(2, 'Java Complete Guide', 1, '/thumb/java.jpg', '/images/java.jpg'),
(3, 'Science of Space', 3, '/thumb/space.jpg', '/images/space.jpg'),
(4, 'Fiction Stories', 2, '/thumb/fiction.jpg', '/images/fiction.jpg'),
(5, 'Effective Java', 1, '/thumb/java.jpg', '/images/ejava.jpg'),
(6, 'Clean Code', 1, '/thumb/clean.jpg', '/images/clean.jpg'),
(7, 'Business Growth', 5, '/thumb/biz.jpg', '/images/biz.jpg'),
(8, 'Future of Space', 4, '/thumb/space.jpg', '/images/fspace.jpg');

-- --------------------------------------------------------

--
-- Table structure for table `book_author`
--

CREATE TABLE `book_author` (
  `ID` bigint(20) NOT NULL,
  `BOOK_ID` bigint(20) DEFAULT NULL,
  `AUTHOR_ID` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `book_author`
--

INSERT INTO `book_author` (`ID`, `BOOK_ID`, `AUTHOR_ID`) VALUES
(1, 1, 1),
(2, 2, 1),
(3, 3, 3),
(12, 4, 4),
(13, 5, 2),
(14, 6, 3),
(15, 7, 4),
(16, 8, 1);

-- --------------------------------------------------------

--
-- Table structure for table `book_category`
--

CREATE TABLE `book_category` (
  `ID` bigint(20) NOT NULL,
  `CATEGORY` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `book_category`
--

INSERT INTO `book_category` (`ID`, `CATEGORY`) VALUES
(1, 'Technology'),
(2, 'Fiction'),
(3, 'Science'),
(4, 'Programming'),
(5, 'History'),
(6, 'Motivation'),
(7, 'Science Fiction'),
(8, 'Business');

-- --------------------------------------------------------

--
-- Table structure for table `book_feature`
--

CREATE TABLE `book_feature` (
  `ID` bigint(20) NOT NULL,
  `FEATURE_NAME` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `book_feature`
--

INSERT INTO `book_feature` (`ID`, `FEATURE_NAME`) VALUES
(1, 'LANGUAGE'),
(2, 'PAGES'),
(3, 'ISBN'),
(4, 'EDITION'),
(5, 'PUBLISH_YEAR'),
(6, 'FORMAT');

-- --------------------------------------------------------

--
-- Table structure for table `book_feature_values`
--

CREATE TABLE `book_feature_values` (
  `ID` bigint(20) NOT NULL,
  `BOOK_ID` bigint(20) DEFAULT NULL,
  `BOOK_FEATURE_ID` bigint(20) DEFAULT NULL,
  `VALUE` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `book_feature_values`
--

INSERT INTO `book_feature_values` (`ID`, `BOOK_ID`, `BOOK_FEATURE_ID`, `VALUE`) VALUES
(1, 1, 1, 'English'),
(2, 1, 2, '520'),
(3, 1, 3, '978-1617297571'),
(4, 1, 5, '2021'),
(5, 1, 6, 'Paperback'),
(6, 2, 1, 'English'),
(7, 2, 2, '450'),
(8, 2, 3, '978-1260463415'),
(9, 2, 4, '11th Edition'),
(10, 2, 6, 'Hardcover'),
(11, 3, 1, 'English'),
(12, 3, 2, '380'),
(13, 3, 3, '978-0385539761'),
(14, 3, 5, '2022'),
(15, 3, 6, 'Hardcover'),
(16, 4, 1, 'English'),
(17, 4, 2, '250'),
(18, 4, 3, '978-0141439600'),
(19, 4, 5, '2019'),
(20, 4, 6, 'Paperback'),
(21, 5, 1, 'English'),
(22, 5, 2, '416'),
(23, 5, 3, '978-0134685991'),
(24, 5, 4, '3rd Edition'),
(25, 5, 6, 'Paperback'),
(26, 6, 1, 'English'),
(27, 6, 2, '464'),
(28, 6, 3, '978-0132350884'),
(29, 6, 5, '2008'),
(30, 6, 6, 'Paperback'),
(31, 7, 1, 'English'),
(32, 7, 2, '310'),
(33, 7, 3, '978-1633697728'),
(34, 7, 5, '2020'),
(35, 7, 6, 'Hardcover'),
(36, 8, 1, 'English'),
(37, 8, 2, '390'),
(38, 8, 3, '978-1426221774'),
(39, 8, 5, '2023'),
(40, 8, 6, 'Hardcover'),
(41, 1, 4, '1st Edition'),
(42, 3, 4, '2nd Edition'),
(43, 4, 4, '1st Edition'),
(44, 6, 4, '1st Edition'),
(45, 7, 4, '1st Edition'),
(46, 8, 4, '1st Edition'),
(47, 2, 5, '2020'),
(48, 5, 5, '1993');

-- --------------------------------------------------------

--
-- Table structure for table `book_publisher`
--

CREATE TABLE `book_publisher` (
  `ID` bigint(20) NOT NULL,
  `BOOK_ID` bigint(20) DEFAULT NULL,
  `PUBLISHER_ID` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `book_publisher`
--

INSERT INTO `book_publisher` (`ID`, `BOOK_ID`, `PUBLISHER_ID`) VALUES
(1, 1, 1),
(2, 2, 2),
(3, 3, 3),
(20, 4, 2),
(21, 5, 1),
(22, 6, 1),
(23, 7, 2),
(24, 8, 2);

-- --------------------------------------------------------

--
-- Table structure for table `entity_type`
--

CREATE TABLE `entity_type` (
  `ID` bigint(20) NOT NULL,
  `NAME` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `entity_type`
--

INSERT INTO `entity_type` (`ID`, `NAME`) VALUES
(1, 'PUBLISHER'),
(2, 'DISTRIBUTOR');

-- --------------------------------------------------------

--
-- Table structure for table `publisher_distributor`
--

CREATE TABLE `publisher_distributor` (
  `ID` bigint(20) NOT NULL,
  `NAME` varchar(100) DEFAULT NULL,
  `ADDRESS` varchar(100) DEFAULT NULL,
  `TELEPHONE` varchar(100) DEFAULT NULL,
  `FAX` varchar(100) DEFAULT NULL,
  `EMAIL` varchar(100) DEFAULT NULL,
  `WEB_SITE` varchar(100) DEFAULT NULL,
  `ENTITY_TYPE_ID` bigint(20) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `publisher_distributor`
--

INSERT INTO `publisher_distributor` (`ID`, `NAME`, `ADDRESS`, `TELEPHONE`, `FAX`, `EMAIL`, `WEB_SITE`, `ENTITY_TYPE_ID`) VALUES
(1, 'OReilly Media', 'New York, USA', '1234567890', '111222333', 'contact@oreilly.com', 'https://oreilly.com', 1),
(2, 'Penguin Random House', 'London, UK', '9876543210', '444555666', 'info@penguin.com', 'https://penguinrandomhouse.com', 1),
(3, 'Springer Publications', 'Berlin, Germany', '5556667777', '888999000', 'support@springer.com', 'https://springer.com', 1),
(4, 'Amazon Distribution', 'Delhi, India', '9998887776', '000111222', 'delivery@amazon.com', 'https://amazon.com', 2),
(5, 'Packt Publishing', 'Birmingham, UK', '1111111111', '222222222', 'support@packt.com', 'https://packtpub.com', 1),
(6, 'McGraw Hill', 'New York, USA', '3333333333', '444444444', 'info@mcgrawhill.com', 'https://mcgrawhill.com', 1);

-- --------------------------------------------------------

--
-- Table structure for table `secret_question`
--

CREATE TABLE `secret_question` (
  `ID` bigint(20) NOT NULL,
  `QUESTION` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `secret_question`
--

INSERT INTO `secret_question` (`ID`, `QUESTION`) VALUES
(1, 'What is your first school name?'),
(2, 'What is your pet name?'),
(3, 'What is your favorite city?'),
(4, 'What is your mother’s maiden name?'),
(5, 'What was your first mobile number?');

-- --------------------------------------------------------

--
-- Table structure for table `user_types`
--

CREATE TABLE `user_types` (
  `ID` bigint(20) NOT NULL,
  `TYPE` varchar(100) DEFAULT NULL
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_general_ci;

--
-- Dumping data for table `user_types`
--

INSERT INTO `user_types` (`ID`, `TYPE`) VALUES
(1, 'ADMIN'),
(2, 'USER');

--
-- Indexes for dumped tables
--

--
-- Indexes for table `app_apis`
--
ALTER TABLE `app_apis`
  ADD PRIMARY KEY (`apis_pk`),
  ADD UNIQUE KEY `uk_api_url` (`api_url`);

--
-- Indexes for table `app_groups`
--
ALTER TABLE `app_groups`
  ADD PRIMARY KEY (`groups_pk`),
  ADD UNIQUE KEY `uk_group_name` (`group_name`);

--
-- Indexes for table `app_groups_apis`
--
ALTER TABLE `app_groups_apis`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_group_api` (`groups_fk`,`apis_fk`),
  ADD KEY `fk_aga_api` (`apis_fk`);

--
-- Indexes for table `app_groups_users`
--
ALTER TABLE `app_groups_users`
  ADD PRIMARY KEY (`id`),
  ADD UNIQUE KEY `uk_group_user` (`groups_fk`,`users_fk`),
  ADD KEY `fk_agu_user` (`users_fk`);

--
-- Indexes for table `app_users`
--
ALTER TABLE `app_users`
  ADD PRIMARY KEY (`ID`),
  ADD UNIQUE KEY `uq_users_username` (`USER_NAME`),
  ADD UNIQUE KEY `uq_users_email` (`EMAIL`),
  ADD KEY `USERS_FK1` (`SECRET_QUESTION_ID`),
  ADD KEY `USERS_FK2` (`USER_TYPE_ID`);

--
-- Indexes for table `author`
--
ALTER TABLE `author`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `book`
--
ALTER TABLE `book`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `BOOK_BOOK_CATEGORY_FK1` (`BOOK_CATEGORY_ID`);

--
-- Indexes for table `book_author`
--
ALTER TABLE `book_author`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `BOOK_AUTHORS_FK1` (`AUTHOR_ID`),
  ADD KEY `BOOK_AUTHORS_BOOK_FK1` (`BOOK_ID`);

--
-- Indexes for table `book_category`
--
ALTER TABLE `book_category`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `book_feature`
--
ALTER TABLE `book_feature`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `book_feature_values`
--
ALTER TABLE `book_feature_values`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `BOOK_FEATURE_VALUES_FK1` (`BOOK_ID`),
  ADD KEY `BOOK_FEATURE_VALUES_BOOK__FK1` (`BOOK_FEATURE_ID`);

--
-- Indexes for table `book_publisher`
--
ALTER TABLE `book_publisher`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `BOOK_PUBLISHER_FK2` (`PUBLISHER_ID`),
  ADD KEY `fk_bookpublisher_book` (`BOOK_ID`);

--
-- Indexes for table `entity_type`
--
ALTER TABLE `entity_type`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `publisher_distributor`
--
ALTER TABLE `publisher_distributor`
  ADD PRIMARY KEY (`ID`),
  ADD KEY `PUBLISHER_DISTRIBUTOR_ENT_FK1` (`ENTITY_TYPE_ID`);

--
-- Indexes for table `secret_question`
--
ALTER TABLE `secret_question`
  ADD PRIMARY KEY (`ID`);

--
-- Indexes for table `user_types`
--
ALTER TABLE `user_types`
  ADD PRIMARY KEY (`ID`);

--
-- AUTO_INCREMENT for dumped tables
--

--
-- AUTO_INCREMENT for table `app_apis`
--
ALTER TABLE `app_apis`
  MODIFY `apis_pk` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=58;

--
-- AUTO_INCREMENT for table `app_groups`
--
ALTER TABLE `app_groups`
  MODIFY `groups_pk` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=3;

--
-- AUTO_INCREMENT for table `app_groups_apis`
--
ALTER TABLE `app_groups_apis`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=95;

--
-- AUTO_INCREMENT for table `app_groups_users`
--
ALTER TABLE `app_groups_users`
  MODIFY `id` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=10;

--
-- AUTO_INCREMENT for table `app_users`
--
ALTER TABLE `app_users`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=29;

--
-- AUTO_INCREMENT for table `author`
--
ALTER TABLE `author`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=16;

--
-- AUTO_INCREMENT for table `book`
--
ALTER TABLE `book`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `book_author`
--
ALTER TABLE `book_author`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=17;

--
-- AUTO_INCREMENT for table `book_category`
--
ALTER TABLE `book_category`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=9;

--
-- AUTO_INCREMENT for table `book_feature`
--
ALTER TABLE `book_feature`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `book_feature_values`
--
ALTER TABLE `book_feature_values`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=49;

--
-- AUTO_INCREMENT for table `book_publisher`
--
ALTER TABLE `book_publisher`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=25;

--
-- AUTO_INCREMENT for table `entity_type`
--
ALTER TABLE `entity_type`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- AUTO_INCREMENT for table `publisher_distributor`
--
ALTER TABLE `publisher_distributor`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `secret_question`
--
ALTER TABLE `secret_question`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=11;

--
-- AUTO_INCREMENT for table `user_types`
--
ALTER TABLE `user_types`
  MODIFY `ID` bigint(20) NOT NULL AUTO_INCREMENT, AUTO_INCREMENT=7;

--
-- Constraints for dumped tables
--

--
-- Constraints for table `app_groups_apis`
--
ALTER TABLE `app_groups_apis`
  ADD CONSTRAINT `fk_aga_api` FOREIGN KEY (`apis_fk`) REFERENCES `app_apis` (`apis_pk`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_aga_group` FOREIGN KEY (`groups_fk`) REFERENCES `app_groups` (`groups_pk`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `app_groups_users`
--
ALTER TABLE `app_groups_users`
  ADD CONSTRAINT `fk_agu_group` FOREIGN KEY (`groups_fk`) REFERENCES `app_groups` (`groups_pk`) ON DELETE CASCADE ON UPDATE CASCADE,
  ADD CONSTRAINT `fk_agu_user` FOREIGN KEY (`users_fk`) REFERENCES `app_users` (`ID`) ON DELETE CASCADE ON UPDATE CASCADE;

--
-- Constraints for table `app_users`
--
ALTER TABLE `app_users`
  ADD CONSTRAINT `USERS_FK1` FOREIGN KEY (`SECRET_QUESTION_ID`) REFERENCES `secret_question` (`ID`),
  ADD CONSTRAINT `USERS_FK2` FOREIGN KEY (`USER_TYPE_ID`) REFERENCES `user_types` (`ID`);

--
-- Constraints for table `book`
--
ALTER TABLE `book`
  ADD CONSTRAINT `BOOK_BOOK_CATEGORY_FK1` FOREIGN KEY (`BOOK_CATEGORY_ID`) REFERENCES `book_category` (`ID`);

--
-- Constraints for table `book_author`
--
ALTER TABLE `book_author`
  ADD CONSTRAINT `BOOK_AUTHORS_BOOK_FK1` FOREIGN KEY (`BOOK_ID`) REFERENCES `book` (`ID`),
  ADD CONSTRAINT `BOOK_AUTHORS_FK1` FOREIGN KEY (`AUTHOR_ID`) REFERENCES `author` (`ID`);

--
-- Constraints for table `book_feature_values`
--
ALTER TABLE `book_feature_values`
  ADD CONSTRAINT `BOOK_FEATURE_VALUES_BOOK__FK1` FOREIGN KEY (`BOOK_FEATURE_ID`) REFERENCES `book_feature` (`ID`),
  ADD CONSTRAINT `BOOK_FEATURE_VALUES_FK1` FOREIGN KEY (`BOOK_ID`) REFERENCES `book` (`ID`);

--
-- Constraints for table `book_publisher`
--
ALTER TABLE `book_publisher`
  ADD CONSTRAINT `BOOK_PUBLISHER_FK2` FOREIGN KEY (`PUBLISHER_ID`) REFERENCES `publisher_distributor` (`ID`),
  ADD CONSTRAINT `fk_bookpublisher_book` FOREIGN KEY (`BOOK_ID`) REFERENCES `book` (`ID`);

--
-- Constraints for table `publisher_distributor`
--
ALTER TABLE `publisher_distributor`
  ADD CONSTRAINT `PUBLISHER_DISTRIBUTOR_ENT_FK1` FOREIGN KEY (`ENTITY_TYPE_ID`) REFERENCES `entity_type` (`ID`);
COMMIT;

/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
