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
-- Table structure for table `book_feature_values`
--

DROP TABLE IF EXISTS `book_feature_values`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `book_feature_values` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `BOOK_ID` bigint DEFAULT NULL,
  `BOOK_FEATURE_ID` bigint DEFAULT NULL,
  `VALUE` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `BOOK_FEATURE_VALUES_FK1` (`BOOK_ID`),
  KEY `BOOK_FEATURE_VALUES_BOOK__FK1` (`BOOK_FEATURE_ID`),
  CONSTRAINT `BOOK_FEATURE_VALUES_BOOK__FK1` FOREIGN KEY (`BOOK_FEATURE_ID`) REFERENCES `book_feature` (`ID`),
  CONSTRAINT `BOOK_FEATURE_VALUES_FK1` FOREIGN KEY (`BOOK_ID`) REFERENCES `book` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=49 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `book_feature_values`
--

LOCK TABLES `book_feature_values` WRITE;
/*!40000 ALTER TABLE `book_feature_values` DISABLE KEYS */;
INSERT INTO `book_feature_values` VALUES (1,1,1,'English'),(2,1,2,'520'),(3,1,3,'978-1617297571'),(4,1,5,'2021'),(5,1,6,'Paperback'),(6,2,1,'English'),(7,2,2,'450'),(8,2,3,'978-1260463415'),(9,2,4,'11th Edition'),(10,2,6,'Hardcover'),(11,3,1,'English'),(12,3,2,'380'),(13,3,3,'978-0385539761'),(14,3,5,'2022'),(15,3,6,'Hardcover'),(16,4,1,'English'),(17,4,2,'250'),(18,4,3,'978-0141439600'),(19,4,5,'2019'),(20,4,6,'Paperback'),(21,5,1,'English'),(22,5,2,'416'),(23,5,3,'978-0134685991'),(24,5,4,'3rd Edition'),(25,5,6,'Paperback'),(26,6,1,'English'),(27,6,2,'464'),(28,6,3,'978-0132350884'),(29,6,5,'2008'),(30,6,6,'Paperback'),(31,7,1,'English'),(32,7,2,'310'),(33,7,3,'978-1633697728'),(34,7,5,'2020'),(35,7,6,'Hardcover'),(36,8,1,'English'),(37,8,2,'390'),(38,8,3,'978-1426221774'),(39,8,5,'2023'),(40,8,6,'Hardcover'),(41,1,4,'1st Edition'),(42,3,4,'2nd Edition'),(43,4,4,'1st Edition'),(44,6,4,'1st Edition'),(45,7,4,'1st Edition'),(46,8,4,'1st Edition'),(47,2,5,'2020'),(48,5,5,'1993');
/*!40000 ALTER TABLE `book_feature_values` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-02 12:58:24
