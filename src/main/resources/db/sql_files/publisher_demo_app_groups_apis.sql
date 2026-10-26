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
-- Table structure for table `app_groups_apis`
--

DROP TABLE IF EXISTS `app_groups_apis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `app_groups_apis` (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `groups_fk` bigint NOT NULL,
  `apis_fk` bigint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `uk_group_api` (`groups_fk`,`apis_fk`),
  KEY `fk_aga_api` (`apis_fk`),
  CONSTRAINT `fk_aga_api` FOREIGN KEY (`apis_fk`) REFERENCES `app_apis` (`apis_pk`) ON DELETE CASCADE ON UPDATE CASCADE,
  CONSTRAINT `fk_aga_group` FOREIGN KEY (`groups_fk`) REFERENCES `app_groups` (`groups_pk`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=95 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_groups_apis`
--

LOCK TABLES `app_groups_apis` WRITE;
/*!40000 ALTER TABLE `app_groups_apis` DISABLE KEYS */;
INSERT INTO `app_groups_apis` VALUES (25,1,1),(23,1,2),(24,1,3),(22,1,4),(13,1,5),(26,1,6),(27,1,7),(28,1,8),(29,1,9),(40,1,10),(38,1,11),(54,1,12),(41,1,13),(5,1,14),(6,1,15),(8,1,16),(7,1,17),(18,1,18),(19,1,19),(21,1,20),(20,1,21),(14,1,22),(15,1,23),(17,1,24),(16,1,25),(9,1,26),(10,1,27),(12,1,28),(11,1,29),(1,1,30),(2,1,31),(4,1,32),(3,1,33),(49,1,34),(44,1,35),(45,1,36),(42,1,37),(43,1,38),(51,1,39),(52,1,40),(50,1,41),(31,1,42),(46,1,43),(53,1,44),(30,1,45),(34,1,46),(35,1,47),(32,1,48),(33,1,49),(36,1,50),(37,1,51),(39,1,52),(47,1,53),(48,1,54),(81,1,55),(83,1,56),(84,1,57),(64,2,6),(65,2,7),(66,2,8),(67,2,9),(76,2,10),(74,2,11),(80,2,12),(77,2,13),(70,2,46),(71,2,47),(68,2,48),(69,2,49),(72,2,50),(73,2,51),(75,2,52),(78,2,53),(79,2,54),(82,2,55);
/*!40000 ALTER TABLE `app_groups_apis` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-02 13:00:15
