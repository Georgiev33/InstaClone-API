CREATE DATABASE  IF NOT EXISTS `instagram` /*!40100 DEFAULT CHARACTER SET utf8mb3 */ /*!80016 DEFAULT ENCRYPTION='N' */;
USE `instagram`;
-- MySQL dump 10.13  Distrib 8.0.30, for Win64 (x86_64)
--
-- Host: localhost    Database: instagram
-- ------------------------------------------------------
-- Server version	8.0.30

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
-- Table structure for table `authorities`
--

DROP TABLE IF EXISTS `authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `authorities` (
  `id` int NOT NULL AUTO_INCREMENT,
  `authority` varchar(50) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `authorities_id_uindex` (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `authorities`
--

LOCK TABLES `authorities` WRITE;
/*!40000 ALTER TABLE `authorities` DISABLE KEYS */;
INSERT INTO `authorities` VALUES (1,'user'),(2,'admin');
/*!40000 ALTER TABLE `authorities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `ban_history`
--

DROP TABLE IF EXISTS `ban_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `ban_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `banned_user_id` int NOT NULL,
  `admin_id` int NOT NULL,
  `reason` varchar(100) NOT NULL,
  `ban_start_date` datetime DEFAULT NULL,
  `ban_end_date` datetime NOT NULL,
  `is_banned` tinyint NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `ban_history_users_id_fk` (`banned_user_id`),
  KEY `ban_history_users_id_fk_2` (`admin_id`),
  CONSTRAINT `ban_history_users_id_fk` FOREIGN KEY (`banned_user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `ban_history_users_id_fk_2` FOREIGN KEY (`admin_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=16 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `ban_history`
--

LOCK TABLES `ban_history` WRITE;
/*!40000 ALTER TABLE `ban_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `ban_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `banned_users`
--

DROP TABLE IF EXISTS `banned_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `banned_users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `banned_id` int NOT NULL,
  `admin_id` int NOT NULL,
  `reason` varchar(200) NOT NULL,
  `ban_start_date` datetime NOT NULL,
  `ban_end_date` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `banned_users_users_id_fk` (`banned_id`),
  KEY `banned_users_users_id_fk_2` (`admin_id`),
  CONSTRAINT `banned_users_users_id_fk` FOREIGN KEY (`banned_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `banned_users_users_id_fk_2` FOREIGN KEY (`admin_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=17 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `banned_users`
--

LOCK TABLES `banned_users` WRITE;
/*!40000 ALTER TABLE `banned_users` DISABLE KEYS */;
/*!40000 ALTER TABLE `banned_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `blacklisted_tokens`
--

DROP TABLE IF EXISTS `blacklisted_tokens`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `blacklisted_tokens` (
  `token` varchar(200) NOT NULL,
  PRIMARY KEY (`token`),
  UNIQUE KEY `token_UNIQUE` (`token`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `blacklisted_tokens`
--

LOCK TABLES `blacklisted_tokens` WRITE;
/*!40000 ALTER TABLE `blacklisted_tokens` DISABLE KEYS */;
INSERT INTO `blacklisted_tokens` VALUES ('eyJhbGciOiJIUzI1NiJ9.eyJVU0VSX0lEIjozLCJzdWIiOiJvcGEzMyIsImlhdCI6MTY4MTg0NDc1MCwiZXhwIjoxNjgxODQ2MTkwfQ.zxsfc0_0M3-DFk_NX9twskrB_Zi4JuMoca16pj9n7UQ'),('eyJhbGciOiJIUzI1NiJ9.eyJVU0VSX0lEIjozLCJzdWIiOiJvcGEzMyIsImlhdCI6MTY4MTg0NDM4NiwiZXhwIjoxNjgxODQ1ODI2fQ.t0za19sKYIppZ7Zra4d2sZKeMD-5hBU0LZ7vu_OIan4'),('eyJhbGciOiJIUzI1NiJ9.eyJVU0VSX0lEIjozLCJzdWIiOiJvcGEzMyIsImlhdCI6MTY4MTg0NzIxMSwiZXhwIjoxNjgxODQ4NjUxfQ.3nsulqKFhUyoRFMqo9331SrmARSB6k7wO8yBqHmmEAI'),('eyJhbGciOiJIUzI1NiJ9.eyJVU0VSX0lEIjozLCJzdWIiOiJvcGEzMyIsImlhdCI6MTY4MTgzMDMyOSwiZXhwIjoxNjgxODMxNzY5fQ.A3Zuyw66ddZeKtJ7iZzZ-JPsrgacdqP0VWty3D03Blg'),('eyJhbGciOiJIUzI1NiJ9.eyJVU0VSX0lEIjozLCJzdWIiOiJvcGEzMyIsImlhdCI6MTY4MTgzMjAwOCwiZXhwIjoxNjgxODMzNDQ4fQ.-tcsUz0fCPyhjKe9BxMpFgZLYbPNkM1uV1hW8UhsZhw'),('eyJhbGciOiJIUzI1NiJ9.eyJVU0VSX0lEIjozLCJzdWIiOiJvcGEzMyIsImlhdCI6MTY4MTUxMzU4NCwiZXhwIjoxNjgxNTE1MDI0fQ.XtOFtrQTswetLHE4NTugIJwGbp5g1esL1isLZZjREfc'),('eyJhbGciOiJIUzI1NiJ9.eyJVU0VSX0lEIjozLCJzdWIiOiJvcGEzMyIsImlhdCI6MTY4MTUxNjE5MiwiZXhwIjoxNjgxNTE3NjMyfQ.Sutb2ZbPPf1NagZNarypLZdcSDgHu14tVVRsjMP8lDE');
/*!40000 ALTER TABLE `blacklisted_tokens` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comment_user_tag`
--

DROP TABLE IF EXISTS `comment_user_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comment_user_tag` (
  `user_id` int NOT NULL,
  `comment_id` int NOT NULL,
  UNIQUE KEY `comment_user_tag_user_id_comment_id_uindex` (`user_id`,`comment_id`),
  KEY `comment_user_tag_comments_id_fk` (`comment_id`),
  CONSTRAINT `comment_user_tag_comments_id_fk` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comment_user_tag_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comment_user_tag`
--

LOCK TABLES `comment_user_tag` WRITE;
/*!40000 ALTER TABLE `comment_user_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `comment_user_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `comments`
--

DROP TABLE IF EXISTS `comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `comments` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `content` varchar(500) NOT NULL,
  `post_id` int NOT NULL,
  `replied_comment_id` int DEFAULT NULL,
  `created_at` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `comment_user_fk_idx` (`user_id`),
  KEY `comment_post_fk_idx` (`post_id`),
  KEY `replied_comment_comment_fk` (`replied_comment_id`),
  CONSTRAINT `comment_post_fk` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `comment_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `replied_comment_comment_fk` FOREIGN KEY (`replied_comment_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `comments`
--

LOCK TABLES `comments` WRITE;
/*!40000 ALTER TABLE `comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `followers`
--

DROP TABLE IF EXISTS `followers`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `followers` (
  `user_id` int NOT NULL,
  `following_id` int NOT NULL,
  KEY `follower_user_fk_idx` (`user_id`) /*!80000 INVISIBLE */,
  KEY `following_user_fk_idx` (`following_id`),
  CONSTRAINT `follower_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `following_users_fk` FOREIGN KEY (`following_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `followers`
--

LOCK TABLES `followers` WRITE;
/*!40000 ALTER TABLE `followers` DISABLE KEYS */;
/*!40000 ALTER TABLE `followers` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hashtags`
--

DROP TABLE IF EXISTS `hashtags`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hashtags` (
  `id` int NOT NULL AUTO_INCREMENT,
  `tag_name` varchar(50) NOT NULL,
  PRIMARY KEY (`id`)
) ENGINE=InnoDB AUTO_INCREMENT=25 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hashtags`
--

LOCK TABLES `hashtags` WRITE;
/*!40000 ALTER TABLE `hashtags` DISABLE KEYS */;
INSERT INTO `hashtags` VALUES (1,'\"fun\"'),(2,'\"cool\"'),(3,'\"dumbhashtag\"'),(4,'fun'),(5,'cool'),(6,'dumbhashtag'),(7,'newhashtag'),(8,'kur'),(21,'wow'),(22,'bow'),(23,'venko'),(24,'bye');
/*!40000 ALTER TABLE `hashtags` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hashtags_comments`
--

DROP TABLE IF EXISTS `hashtags_comments`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hashtags_comments` (
  `comment_id` int NOT NULL,
  `hashtag_id` int NOT NULL,
  UNIQUE KEY `hashtags_comments_comment_id_hashtag_id_uindex` (`comment_id`,`hashtag_id`),
  KEY `hasgtags_comments_hashtags_id_fk` (`hashtag_id`),
  CONSTRAINT `hasgtags_comments_hashtags_id_fk` FOREIGN KEY (`hashtag_id`) REFERENCES `hashtags` (`id`) ON DELETE CASCADE,
  CONSTRAINT `hashtags_comments_comments_id_fk` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hashtags_comments`
--

LOCK TABLES `hashtags_comments` WRITE;
/*!40000 ALTER TABLE `hashtags_comments` DISABLE KEYS */;
/*!40000 ALTER TABLE `hashtags_comments` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `hashtags_posts`
--

DROP TABLE IF EXISTS `hashtags_posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `hashtags_posts` (
  `post_id` int NOT NULL,
  `tag_id` int NOT NULL,
  UNIQUE KEY `unique_key` (`post_id`,`tag_id`),
  KEY `hashtags_posts_posts_fk_idx` (`post_id`),
  KEY `hashtags_posts_hashtags_fk_idx` (`tag_id`),
  CONSTRAINT `hashtags_posts_hashtags_fk` FOREIGN KEY (`tag_id`) REFERENCES `hashtags` (`id`) ON DELETE CASCADE,
  CONSTRAINT `hashtags_posts_posts_fk` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `hashtags_posts`
--

LOCK TABLES `hashtags_posts` WRITE;
/*!40000 ALTER TABLE `hashtags_posts` DISABLE KEYS */;
/*!40000 ALTER TABLE `hashtags_posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `notifications`
--

DROP TABLE IF EXISTS `notifications`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `notifications` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `notification` varchar(200) NOT NULL,
  `date_created` datetime NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `users_notifications_id_uindex` (`id`),
  KEY `notifications_users_id_fk` (`user_id`),
  CONSTRAINT `notifications_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=35 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `notifications`
--

LOCK TABLES `notifications` WRITE;
/*!40000 ALTER TABLE `notifications` DISABLE KEYS */;
/*!40000 ALTER TABLE `notifications` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `post_user_tag`
--

DROP TABLE IF EXISTS `post_user_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `post_user_tag` (
  `user_id` int NOT NULL,
  `post_id` int NOT NULL,
  UNIQUE KEY `unique_tag_id` (`user_id`,`post_id`),
  KEY `tag_user_fk_idx` (`user_id`),
  KEY `tag_post_fk_idx` (`post_id`),
  CONSTRAINT `tag_post_fk` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `tag_user_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `post_user_tag`
--

LOCK TABLES `post_user_tag` WRITE;
/*!40000 ALTER TABLE `post_user_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `post_user_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts`
--

DROP TABLE IF EXISTS `posts`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `caption` varchar(45) DEFAULT NULL,
  `is_deleted` tinyint NOT NULL,
  `date_created` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `posts_users_fk_idx` (`user_id`),
  CONSTRAINT `posts_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=68 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts`
--

LOCK TABLES `posts` WRITE;
/*!40000 ALTER TABLE `posts` DISABLE KEYS */;
/*!40000 ALTER TABLE `posts` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `posts_content`
--

DROP TABLE IF EXISTS `posts_content`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `posts_content` (
  `id` int NOT NULL AUTO_INCREMENT,
  `post_id` int NOT NULL,
  `content_url` varchar(200) NOT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `posts_conent_posts_fk_idx` (`post_id`),
  CONSTRAINT `posts_conent_posts_fk` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=76 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `posts_content`
--

LOCK TABLES `posts_content` WRITE;
/*!40000 ALTER TABLE `posts_content` DISABLE KEYS */;
/*!40000 ALTER TABLE `posts_content` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `report_history`
--

DROP TABLE IF EXISTS `report_history`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `report_history` (
  `id` int NOT NULL AUTO_INCREMENT,
  `reporter_id` int NOT NULL,
  `reported_id` int NOT NULL,
  `status` tinyint NOT NULL,
  `reason` varchar(200) NOT NULL,
  `admin_id` int NOT NULL,
  PRIMARY KEY (`id`),
  KEY `report_history_reported_users_id_fk_` (`reported_id`),
  KEY `report_history_reporter_users_id_fk` (`reporter_id`),
  CONSTRAINT `report_history_reported_users_id_fk_` FOREIGN KEY (`reported_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `report_history_reporter_users_id_fk` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `report_history`
--

LOCK TABLES `report_history` WRITE;
/*!40000 ALTER TABLE `report_history` DISABLE KEYS */;
/*!40000 ALTER TABLE `report_history` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `reported_users`
--

DROP TABLE IF EXISTS `reported_users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `reported_users` (
  `reporter_id` int NOT NULL,
  `reported_id` int NOT NULL,
  `reason` varchar(200) NOT NULL,
  `id` int NOT NULL AUTO_INCREMENT,
  PRIMARY KEY (`id`),
  UNIQUE KEY `unique_reporter_id_reported_id` (`reporter_id`,`reported_id`),
  UNIQUE KEY `unique_reported_id_reporter_id` (`reporter_id`,`reported_id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  KEY `fk_reported_users_reported_user_users_user_id_idx` (`reporter_id`),
  KEY `fk_reported_users_reported_id_user_users_user_id_idx` (`reported_id`),
  CONSTRAINT `fk_reported_users_reported_id_user_users_user_id` FOREIGN KEY (`reported_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_reported_users_reporter_id_user_users_user_id` FOREIGN KEY (`reporter_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=15 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `reported_users`
--

LOCK TABLES `reported_users` WRITE;
/*!40000 ALTER TABLE `reported_users` DISABLE KEYS */;
/*!40000 ALTER TABLE `reported_users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stories`
--

DROP TABLE IF EXISTS `stories`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stories` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `is_deleted` tinyint NOT NULL,
  `date_created` datetime NOT NULL,
  `expiration_date` datetime NOT NULL,
  `content_url` varchar(300) NOT NULL,
  PRIMARY KEY (`id`),
  KEY `posts_users_fk_idx` (`user_id`),
  CONSTRAINT `stories_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB AUTO_INCREMENT=39 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stories`
--

LOCK TABLES `stories` WRITE;
/*!40000 ALTER TABLE `stories` DISABLE KEYS */;
/*!40000 ALTER TABLE `stories` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stories_hashtag`
--

DROP TABLE IF EXISTS `stories_hashtag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stories_hashtag` (
  `story_id` int NOT NULL,
  `hashtag_id` int NOT NULL,
  UNIQUE KEY `stories_hashtag_story_id_hashtag_id_uindex` (`story_id`,`hashtag_id`),
  KEY `stories_hashtag_hashtags_id_fk` (`hashtag_id`),
  CONSTRAINT `stories_hashtag_hashtags_id_fk` FOREIGN KEY (`hashtag_id`) REFERENCES `hashtags` (`id`) ON DELETE CASCADE,
  CONSTRAINT `stories_hashtag_stories_id_fk` FOREIGN KEY (`story_id`) REFERENCES `stories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stories_hashtag`
--

LOCK TABLES `stories_hashtag` WRITE;
/*!40000 ALTER TABLE `stories_hashtag` DISABLE KEYS */;
/*!40000 ALTER TABLE `stories_hashtag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `stories_user_tag`
--

DROP TABLE IF EXISTS `stories_user_tag`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `stories_user_tag` (
  `story_id` int NOT NULL,
  `user_id` int NOT NULL,
  UNIQUE KEY `story_user_tag_story_id_user_id_uindex` (`story_id`,`user_id`),
  KEY `stories_user_tag_users_id_fk` (`user_id`),
  CONSTRAINT `stories_user_tag_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE,
  CONSTRAINT `story_user_tag_stories_id_fk` FOREIGN KEY (`story_id`) REFERENCES `stories` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `stories_user_tag`
--

LOCK TABLES `stories_user_tag` WRITE;
/*!40000 ALTER TABLE `stories_user_tag` DISABLE KEYS */;
/*!40000 ALTER TABLE `stories_user_tag` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `user_authorities`
--

DROP TABLE IF EXISTS `user_authorities`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `user_authorities` (
  `user_id` int NOT NULL,
  `autority_id` int NOT NULL,
  PRIMARY KEY (`user_id`,`autority_id`),
  KEY `fk_user_autorities_authorities_idx` (`autority_id`),
  CONSTRAINT `fk_user_autorities_authorities` FOREIGN KEY (`autority_id`) REFERENCES `authorities` (`id`) ON DELETE CASCADE,
  CONSTRAINT `fk_user_autorities_user` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `user_authorities`
--

LOCK TABLES `user_authorities` WRITE;
/*!40000 ALTER TABLE `user_authorities` DISABLE KEYS */;
INSERT INTO `user_authorities` VALUES (-1,2);
/*!40000 ALTER TABLE `user_authorities` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `id` int NOT NULL AUTO_INCREMENT,
  `username` varchar(45) NOT NULL,
  `email` varchar(100) NOT NULL,
  `password` varchar(100) NOT NULL,
  `verification_code` varchar(64) NOT NULL,
  `is_verified` tinyint NOT NULL,
  `is_private` tinyint NOT NULL,
  `bio` varchar(200) DEFAULT NULL,
  PRIMARY KEY (`id`),
  UNIQUE KEY `id_UNIQUE` (`id`),
  UNIQUE KEY `username_UNIQUE` (`username`),
  UNIQUE KEY `email_UNIQUE` (`email`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES (-1,'cron_admin','cronadmin@instagram.com','$2a$10$EIK1I61RO5UxDzfz2OLFyuAReZ9HPqTHiy9awdJjGfti4l8I27Ep','666',0,1,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_comments_reactions`
--

DROP TABLE IF EXISTS `users_comments_reactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users_comments_reactions` (
  `user_id` int NOT NULL,
  `comment_id` int NOT NULL,
  `status` tinyint NOT NULL,
  KEY `reactions_users_fk_idx` (`user_id`),
  KEY `reactions_comments_fk_idx` (`comment_id`),
  KEY `unique_key` (`user_id`,`comment_id`),
  CONSTRAINT `reactions_comments_fk` FOREIGN KEY (`comment_id`) REFERENCES `comments` (`id`) ON DELETE CASCADE,
  CONSTRAINT `reactions_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_comments_reactions`
--

LOCK TABLES `users_comments_reactions` WRITE;
/*!40000 ALTER TABLE `users_comments_reactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `users_comments_reactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_posts_reactions`
--

DROP TABLE IF EXISTS `users_posts_reactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users_posts_reactions` (
  `user_id` int NOT NULL,
  `post_id` int NOT NULL,
  `status` tinyint NOT NULL,
  UNIQUE KEY `unique_key` (`user_id`,`post_id`),
  KEY `users_posts_reactions_users_fk_idx` (`user_id`),
  KEY `users_posts_reactions_posts_fk_idx` (`post_id`),
  CONSTRAINT `users_posts_reactions_posts_fk` FOREIGN KEY (`post_id`) REFERENCES `posts` (`id`) ON DELETE CASCADE,
  CONSTRAINT `users_posts_reactions_users_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_posts_reactions`
--

LOCK TABLES `users_posts_reactions` WRITE;
/*!40000 ALTER TABLE `users_posts_reactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `users_posts_reactions` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_queries`
--

DROP TABLE IF EXISTS `users_queries`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users_queries` (
  `id` int NOT NULL AUTO_INCREMENT,
  `user_id` int NOT NULL,
  `search_query` varchar(200) NOT NULL,
  `date_created` datetime NOT NULL,
  PRIMARY KEY (`id`),
  KEY `users_queries_users_id_fk` (`user_id`),
  CONSTRAINT `users_queries_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_queries`
--

LOCK TABLES `users_queries` WRITE;
/*!40000 ALTER TABLE `users_queries` DISABLE KEYS */;
/*!40000 ALTER TABLE `users_queries` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `users_stories_reactions`
--

DROP TABLE IF EXISTS `users_stories_reactions`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users_stories_reactions` (
  `user_id` int NOT NULL,
  `story_id` int NOT NULL,
  `status` tinyint NOT NULL,
  UNIQUE KEY `users_stories_reactions_user_id_story_id_uindex` (`user_id`,`story_id`),
  KEY `users_stories_reactions_stories_id_fk` (`story_id`),
  CONSTRAINT `users_stories_reactions_stories_id_fk` FOREIGN KEY (`story_id`) REFERENCES `stories` (`id`) ON DELETE CASCADE,
  CONSTRAINT `users_stories_reactions_users_id_fk` FOREIGN KEY (`user_id`) REFERENCES `users` (`id`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users_stories_reactions`
--

LOCK TABLES `users_stories_reactions` WRITE;
/*!40000 ALTER TABLE `users_stories_reactions` DISABLE KEYS */;
/*!40000 ALTER TABLE `users_stories_reactions` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2023-05-23 14:49:26
