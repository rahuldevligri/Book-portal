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
-- Table structure for table `publisher_distributor`
--

DROP TABLE IF EXISTS `publisher_distributor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `publisher_distributor` (
  `ID` bigint NOT NULL AUTO_INCREMENT,
  `NAME` varchar(100) DEFAULT NULL,
  `ADDRESS` varchar(100) DEFAULT NULL,
  `TELEPHONE` varchar(100) DEFAULT NULL,
  `FAX` varchar(100) DEFAULT NULL,
  `EMAIL` varchar(100) DEFAULT NULL,
  `WEB_SITE` varchar(100) DEFAULT NULL,
  `ENTITY_TYPE_ID` bigint DEFAULT NULL,
  PRIMARY KEY (`ID`),
  KEY `PUBLISHER_DISTRIBUTOR_ENT_FK1` (`ENTITY_TYPE_ID`),
  CONSTRAINT `PUBLISHER_DISTRIBUTOR_ENT_FK1` FOREIGN KEY (`ENTITY_TYPE_ID`) REFERENCES `entity_type` (`ID`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `publisher_distributor`
--

LOCK TABLES `publisher_distributor` WRITE;
/*!40000 ALTER TABLE `publisher_distributor` DISABLE KEYS */;
INSERT INTO `publisher_distributor` VALUES (1,'OReilly Media','New York, USA','1234567890','111222333','contact@oreilly.com','https://oreilly.com',1),(2,'Penguin Random House','London, UK','9876543210','444555666','info@penguin.com','https://penguinrandomhouse.com',1),(3,'Springer Publications','Berlin, Germany','5556667777','888999000','support@springer.com','https://springer.com',1),(4,'Amazon Distribution','Delhi, India','9998887776','000111222','delivery@amazon.com','https://amazon.com',2),(5,'Packt Publishing','Birmingham, UK','1111111111','222222222','support@packt.com','https://packtpub.com',1),(6,'McGraw Hill','New York, USA','3333333333','444444444','info@mcgrawhill.com','https://mcgrawhill.com',1);
/*!40000 ALTER TABLE `publisher_distributor` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-02 13:00:34
