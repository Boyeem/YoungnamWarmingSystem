-- MySQL dump 10.13  Distrib 8.0.36, for Win64 (x86_64)
--
-- Host: 127.0.0.1    Database: disaster_db
-- ------------------------------------------------------
-- Server version	8.0.37

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
-- Table structure for table `users`
--

DROP TABLE IF EXISTS `users`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `users` (
  `username` varchar(50) NOT NULL,
  `assigned_at` datetime DEFAULT NULL,
  `admin_role` tinyint(1) DEFAULT '0',
  `password` varchar(50) DEFAULT NULL,
  PRIMARY KEY (`username`),
  UNIQUE KEY `username` (`username`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `users`
--

LOCK TABLES `users` WRITE;
/*!40000 ALTER TABLE `users` DISABLE KEYS */;
INSERT INTO `users` VALUES ('가벼운돌멩이','2025-06-03 15:36:29',0,NULL),('감정없는감성','2025-06-03 22:06:57',0,NULL),('감춰진고양이','2025-06-03 22:07:50',0,NULL),('거꾸로피는꽃','2025-06-03 22:07:57',0,NULL),('걷는구름','2025-06-03 22:08:46',0,NULL),('고요한울음','2025-06-03 22:09:25',0,NULL),('고장난행복','2025-06-03 22:09:27',0,NULL),('그리운소음','2025-06-03 22:12:01',0,NULL),('까만무지개','2025-06-03 22:12:02',0,NULL),('까먹은기억','2025-06-03 22:12:02',0,NULL),('깨어난꿈','2025-06-04 14:05:47',0,NULL),('깨지지않는거울','2025-06-04 14:06:26',0,NULL),('깨진달콤함','2025-06-04 14:07:02',0,NULL),('깨진시간','2025-06-04 14:09:31',0,NULL),('꺾이지않는부드러움','2025-06-04 14:09:36',0,NULL),('날아가는책상','2025-06-04 14:30:09',0,NULL),('노래하는냉면','2025-06-04 14:36:04',0,NULL),('노래하는벽','2025-06-04 14:40:47',0,NULL),('녹아내리는생각','2025-06-04 14:41:04',0,NULL),('느린번개',NULL,0,NULL),('단단한구름',NULL,0,NULL),('달려라거북이',NULL,0,NULL),('달리는소파',NULL,0,NULL),('달리는침대',NULL,0,NULL),('달콤한고통',NULL,0,NULL),('따뜻한눈물방울',NULL,0,NULL),('떠나는정지',NULL,0,NULL),('떨어지는미소',NULL,0,NULL),('떨어진웃음',NULL,0,NULL),('뜨개질로봇',NULL,0,NULL),('뜨거운비눗방울',NULL,0,NULL),('뜨거운얼음',NULL,0,NULL),('말없는고함',NULL,0,NULL),('말하는고요',NULL,0,NULL),('멈춘시계',NULL,0,NULL),('멍한천둥',NULL,0,NULL),('무거운구름솜',NULL,0,NULL),('무거운풍선',NULL,0,NULL),('무너진별빛',NULL,0,NULL),('무지갯빛그림자',NULL,0,NULL),('무표정고양이',NULL,0,NULL),('미끄러운모래성',NULL,0,NULL),('미소짓는분노',NULL,0,NULL),('미소짓는잠',NULL,0,NULL),('반짝이는분노',NULL,0,NULL),('반짝이는슬픔',NULL,0,NULL),('반쯤찬사이다',NULL,0,NULL),('배고픈냉장고',NULL,0,NULL),('벙쪄있는태풍',NULL,0,NULL),('불안한평화',NULL,0,NULL),('불타는눈',NULL,0,NULL),('불타는얼음조각',NULL,0,NULL),('빛나는그림자',NULL,0,NULL),('빛나는어둠',NULL,0,NULL),('사라지는목소리',NULL,0,NULL),('소리없는박수',NULL,0,NULL),('속삭이는소음',NULL,0,NULL),('속삭이는천둥',NULL,0,NULL),('숨겨진빛',NULL,0,NULL),('슬픈샤워기',NULL,0,NULL),('슬픈풍선',NULL,0,NULL),('어두운햇빛',NULL,0,NULL),('우는사탕',NULL,0,NULL),('우는햇살',NULL,0,NULL),('울지않는눈물',NULL,0,NULL),('웃고있는가면',NULL,0,NULL),('웃는눈물',NULL,0,NULL),('웃는비',NULL,0,NULL),('웃는한숨',NULL,0,NULL),('잊혀진소리',NULL,0,NULL),('자라는돌',NULL,0,NULL),('잔잔한폭풍우',NULL,0,NULL),('잠든폭죽',NULL,0,NULL),('잠자는라면',NULL,0,NULL),('잠자는햇살',NULL,0,NULL),('젖은구름',NULL,0,NULL),('젖은번개',NULL,0,NULL),('젖은불꽃',NULL,0,NULL),('조용한아우성',NULL,0,NULL),('조용한파도',NULL,0,NULL),('조용한폭죽',NULL,0,NULL),('조용한폭풍',NULL,0,NULL),('종이로된강철',NULL,0,NULL),('지워진추억',NULL,0,NULL),('차가운노래',NULL,0,NULL),('차가운모닥불',NULL,0,NULL),('차가운햇살',NULL,0,NULL),('춤추는고목',NULL,0,NULL),('춤추는바위',NULL,0,NULL),('타오르는눈물',NULL,0,NULL),('투명한돌멩이',NULL,0,NULL),('폭풍속꽃잎',NULL,0,NULL),('행복한바지',NULL,0,NULL),('혼자인군중',NULL,0,NULL),('화난토끼인형',NULL,0,NULL),('흐르는거울',NULL,0,NULL),('흐린달빛',NULL,0,NULL),('흔들리는정적',NULL,0,NULL),('흔들리지않는젤리',NULL,0,NULL),('흩날리는한숨',NULL,0,NULL);
/*!40000 ALTER TABLE `users` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2025-06-05 17:22:34
