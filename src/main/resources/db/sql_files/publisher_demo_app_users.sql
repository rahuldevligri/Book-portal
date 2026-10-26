-- MySQL dump 10.13  Distrib 8.0.40, for Win64 (x86_64)
--
-- Host: 80.89.238.23    Database: publisher_demo
-- ------------------------------------------------------
-- Server version	8.0.44

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `app_users`
--

DROP TABLE IF EXISTS `app_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `app_users` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `FIRST_NAME` varchar(100) DEFAULT NULL,
  `LAST_NAME` varchar(100) DEFAULT NULL,
  `EMAIL` varchar(100) DEFAULT NULL,
  `USER_NAME` varchar(100) DEFAULT NULL,
  `PASSWORD` varchar(100) DEFAULT NULL,
  `SECRET_QUESTION_ID` bigint DEFAULT NULL,
  `SECRET_ANSWER` varchar(100) DEFAULT NULL,
  `USER_TYPE_ID` bigint DEFAULT NULL,
  PRIMARY KEY (`ID`),
  UNIQUE KEY `uq_users_username` (`USER_NAME`),
  UNIQUE KEY `uq_users_email` (`EMAIL`),
  KEY `USERS_FK1` (`SECRET_QUESTION_ID`),
  KEY `USERS_FK2` (`USER_TYPE_ID`),
  CONSTRAINT `USERS_FK1` FOREIGN KEY (`SECRET_QUESTION_ID`) REFERENCES `secret_question` (`ID`),
  CONSTRAINT `USERS_FK2` FOREIGN KEY (`USER_TYPE_ID`) REFERENCES `user_types` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=29 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_users`
--

LOCK TABLES `app_users` WRITE;
/*!40000 ALTER TABLE `app_users` DISABLE KEYS */;
INSERT INTO `app_users` VALUES (1,'Adminn','User','adminuser@gmail.com','admin','$2a$10$2dk4yZQopa.b.o.OHQOcvex2efQHH1hKpH.77xT.WQFnbfJymVM9C',1,'Arya School',1),(3,'Ritesh','Sharma','riteshsharma@gmail.com','ritesh','$2a$10$zUBmgLWif0JO0WWLykHAi.NkoI5rHUQkWI3YN6ow7t5trIjCdZmuu',3,'Jaipur',1),(5,'Ashley','Jonson','ashley@gmail.com','ashley','$2a$10$RvXv1E0PD00FLyaBZBh1JOX75UWHCdWjaVphU6IxvUsKbTacd9vAu',3,'Washington',2),(7,'romania','james','romaniajames@gmail.com','roman','$2a$10$iWe5woqFIKeA42TgVrmCSeRA8zRMla4iIkaCLf0w34fUQBxqx5tx6',5,'9123456789',2);
/*!40000 ALTER TABLE `app_users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-02 12:59:07
