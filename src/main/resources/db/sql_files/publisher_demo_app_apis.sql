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
-- Table structure for table `app_apis`
--

DROP TABLE IF EXISTS `app_apis`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `app_apis` (
  `apis_pk` bigint NOT NULL AUTO_INCREMENT,
  `api_url` varchar(255) NOT NULL,
  PRIMARY KEY (`apis_pk`),
  UNIQUE KEY `uk_api_url` (`api_url`)
) ENGINE=InnoDB AUTO_INCREMENT=58 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `app_apis`
--

LOCK TABLES `app_apis` WRITE;
/*!40000 ALTER TABLE `app_apis` DISABLE KEYS */;
INSERT INTO `app_apis` VALUES (30,'/admin/apis'),(31,'/admin/apis/add'),(33,'/admin/apis/delete'),(32,'/admin/apis/edit/**'),(14,'/admin/authors'),(15,'/admin/authors/add'),(17,'/admin/authors/delete'),(16,'/admin/authors/edit/**'),(26,'/admin/distributors'),(27,'/admin/distributors/add'),(29,'/admin/distributors/delete'),(28,'/admin/distributors/edit/**'),(5,'/admin/panel'),(56,'/admin/privileges/**'),(57,'/admin/privileges/api/whoami'),(22,'/admin/publishers'),(23,'/admin/publishers/add'),(25,'/admin/publishers/delete'),(24,'/admin/publishers/edit/**'),(18,'/admin/user-types'),(19,'/admin/user-types/add'),(21,'/admin/user-types/delete'),(20,'/admin/user-types/edit/**'),(4,'/admin/users'),(2,'/admin/users/add'),(3,'/admin/users/delete'),(1,'/admin/users/edit/**'),(6,'/advanced-search'),(7,'/advanced-search-author'),(8,'/advanced-search-publisher'),(9,'/advanced-search-title'),(45,'/api/search'),(42,'/author'),(48,'/authors'),(49,'/authors/**'),(46,'/books'),(47,'/books/search'),(50,'/categories'),(51,'/categories/**'),(11,'/change-password'),(52,'/compare'),(10,'/dashboard'),(13,'/edit-profile'),(37,'/forgot'),(38,'/forgot/login-info'),(35,'/login'),(36,'/logout'),(43,'/publisher'),(53,'/publishers'),(54,'/publishers/**'),(34,'/register'),(41,'/search-author'),(39,'/search-publisher'),(40,'/search-title'),(55,'/search/**'),(44,'/title'),(12,'/user-options');
/*!40000 ALTER TABLE `app_apis` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2026-03-02 13:00:44
