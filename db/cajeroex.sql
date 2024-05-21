-- MySQL dump 10.13  Distrib 8.0.22, for macos10.15 (x86_64)
--
-- Host: localhost    Database: cajeroex
-- ------------------------------------------------------
-- Server version	8.0.22

create database cajero_examen;
use cajero_examen;

/*!40101 SET @OLD_CHARACTER_SET_CLIENT=@@CHARACTER_SET_CLIENT */;
/*!40101 SET @OLD_CHARACTER_SET_RESULTS=@@CHARACTER_SET_RESULTS */;
/*!40101 SET @OLD_COLLATION_CONNECTION=@@COLLATION_CONNECTION */;
/*!50503 SET NAMES utf8mb4 */;
/*!40103 SET @OLD_TIME_ZONE=@@TIME_ZONE */;
/*!40103 SET TIME_ZONE='+00:00' */;
/*!40014 SET @OLD_UNIQUE_CHECKS=@@UNIQUE_CHECKS, UNIQUE_CHECKS=0 */;
/*!40014 SET @OLD_FOREIGN_KEY_CHECKS=@@FOREIGN_KEY_CHECKS, FOREIGN_KEY_CHECKS=0 */;
/*!40101 SET @OLD_SQL_MODE=@@SQL_MODE, SQL_MODE='NO_AUTO_VALUE_ON_ZERO' */;
/*!40111 SET @OLD_SQL_NOTES=@@SQL_NOTES, SQL_NOTES=0 */;

--
-- Table structure for table `cred_corte`
--

DROP TABLE IF EXISTS `cred_corte`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `cred_corte` (
  `id_cc` int NOT NULL AUTO_INCREMENT,
  `id_cred` int NOT NULL,
  `fchco_cc` date NOT NULL,
  `min_cc` decimal(10,2) NOT NULL,
  `pagd_cc` decimal(10,2) NOT NULL,
  PRIMARY KEY (`id_cc`),
  KEY `id_cred_cc` (`id_cred`),
  CONSTRAINT `id_cred_cc` FOREIGN KEY (`id_cred`) REFERENCES `credito` (`id_cred`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `cred_corte`
--

LOCK TABLES `cred_corte` WRITE;
/*!40000 ALTER TABLE `cred_corte` DISABLE KEYS */;
/*!40000 ALTER TABLE `cred_corte` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `credito`
--

DROP TABLE IF EXISTS `credito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `credito` (
  `id_cred` int NOT NULL AUTO_INCREMENT,
  `crds_cred` decimal(10,2) NOT NULL,
  `sald_cred` decimal(10,2) NOT NULL DEFAULT '0.00',
  `pgmin_cred` decimal(10,2) NOT NULL,
  `num_tar` varchar(16) NOT NULL,
  PRIMARY KEY (`id_cred`),
  UNIQUE KEY `num_tar` (`num_tar`),
  CONSTRAINT `num_tar_cred` FOREIGN KEY (`num_tar`) REFERENCES `tarjeta` (`num_tar`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `credito`
--

LOCK TABLES `credito` WRITE;
/*!40000 ALTER TABLE `credito` DISABLE KEYS */;
/*!40000 ALTER TABLE `credito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `debito`
--

DROP TABLE IF EXISTS `debito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `debito` (
  `id_deb` int NOT NULL AUTO_INCREMENT,
  `sald_deb` decimal(10,2) DEFAULT '0.00',
  `num_tar` varchar(16) NOT NULL,
  PRIMARY KEY (`id_deb`),
  UNIQUE KEY `num_tar` (`num_tar`),
  CONSTRAINT `num_tar_deb` FOREIGN KEY (`num_tar`) REFERENCES `tarjeta` (`num_tar`) ON DELETE CASCADE ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `debito`
--

LOCK TABLES `debito` WRITE;
/*!40000 ALTER TABLE `debito` DISABLE KEYS */;
/*!40000 ALTER TABLE `debito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `deposito`
--

DROP TABLE IF EXISTS `deposito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `deposito` (
  `id_dep` int NOT NULL AUTO_INCREMENT,
  `id_movE` varchar(50) NOT NULL,
  `id_movR` varchar(50) NOT NULL,
  PRIMARY KEY (`id_dep`),
  KEY `id_movE_dep` (`id_movE`),
  KEY `id_movR_dep` (`id_movR`),
  CONSTRAINT `id_movE_dep` FOREIGN KEY (`id_movE`) REFERENCES `movimiento` (`id_mov`) ON UPDATE CASCADE,
  CONSTRAINT `id_movR_dep` FOREIGN KEY (`id_movR`) REFERENCES `movimiento` (`id_mov`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `deposito`
--

LOCK TABLES `deposito` WRITE;
/*!40000 ALTER TABLE `deposito` DISABLE KEYS */;
/*!40000 ALTER TABLE `deposito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `movimiento`
--

DROP TABLE IF EXISTS `movimiento`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `movimiento` (
  `id_mov` varchar(24) NOT NULL,
  `tipo_mov` varchar(3) NOT NULL,
  `mont_mov` decimal(10,2) NOT NULL,
  `fch_mov` datetime NOT NULL,
  `num_tar` varchar(16) NOT NULL,
  PRIMARY KEY (`id_mov`),
  KEY `num_tar_mov` (`num_tar`),
  CONSTRAINT `num_tar_mov` FOREIGN KEY (`num_tar`) REFERENCES `tarjeta` (`num_tar`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `movimiento`
--

LOCK TABLES `movimiento` WRITE;
/*!40000 ALTER TABLE `movimiento` DISABLE KEYS */;
/*!40000 ALTER TABLE `movimiento` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pag_credito`
--

DROP TABLE IF EXISTS `pag_credito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pag_credito` (
  `id_pc` int NOT NULL AUTO_INCREMENT,
  `id_mov` varchar(50) NOT NULL,
  `id_cc` int NOT NULL,
  PRIMARY KEY (`id_pc`),
  KEY `id_mov_pc` (`id_mov`),
  KEY `id_cc_pg` (`id_cc`),
  CONSTRAINT `id_cc_pg` FOREIGN KEY (`id_cc`) REFERENCES `cred_corte` (`id_cc`) ON UPDATE CASCADE,
  CONSTRAINT `id_mov_pc` FOREIGN KEY (`id_mov`) REFERENCES `movimiento` (`id_mov`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pag_credito`
--

LOCK TABLES `pag_credito` WRITE;
/*!40000 ALTER TABLE `pag_credito` DISABLE KEYS */;
/*!40000 ALTER TABLE `pag_credito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pag_servicio`
--

DROP TABLE IF EXISTS `pag_servicio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pag_servicio` (
  `id_pg` int NOT NULL AUTO_INCREMENT,
  `id_mov` varchar(50) NOT NULL,
  `id_serv` int NOT NULL,
  `ref_pg` varchar(100) NOT NULL,
  PRIMARY KEY (`id_pg`),
  KEY `id_mov_pg` (`id_mov`),
  KEY `id_serv_pg` (`id_serv`),
  CONSTRAINT `id_mov_pg` FOREIGN KEY (`id_mov`) REFERENCES `movimiento` (`id_mov`) ON UPDATE CASCADE,
  CONSTRAINT `id_serv_pg` FOREIGN KEY (`id_serv`) REFERENCES `servicio` (`id_serv`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pag_servicio`
--

LOCK TABLES `pag_servicio` WRITE;
/*!40000 ALTER TABLE `pag_servicio` DISABLE KEYS */;
/*!40000 ALTER TABLE `pag_servicio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `retiro`
--

DROP TABLE IF EXISTS `retiro`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `retiro` (
  `id_ret` int NOT NULL AUTO_INCREMENT,
  `id_term` int NOT NULL DEFAULT '1',
  `id_mov` varchar(50) NOT NULL,
  PRIMARY KEY (`id_ret`),
  KEY `id_term_ret` (`id_term`),
  KEY `id_mov_ret` (`id_mov`),
  CONSTRAINT `id_mov_ret` FOREIGN KEY (`id_mov`) REFERENCES `movimiento` (`id_mov`) ON UPDATE CASCADE,
  CONSTRAINT `id_term_ret` FOREIGN KEY (`id_term`) REFERENCES `terminal` (`id_term`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `retiro`
--

LOCK TABLES `retiro` WRITE;
/*!40000 ALTER TABLE `retiro` DISABLE KEYS */;
/*!40000 ALTER TABLE `retiro` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `servicio`
--

DROP TABLE IF EXISTS `servicio`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `servicio` (
  `id_serv` int NOT NULL AUTO_INCREMENT,
  `nom_serv` varchar(50) NOT NULL,
  PRIMARY KEY (`id_serv`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `servicio`
--

LOCK TABLES `servicio` WRITE;
/*!40000 ALTER TABLE `servicio` DISABLE KEYS */;
/*!40000 ALTER TABLE `servicio` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tarjeta`
--

DROP TABLE IF EXISTS `tarjeta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tarjeta` (
  `num_tar` varchar(16) NOT NULL,
  `pin_tar` varchar(255) NOT NULL,
  `cad_tar` date NOT NULL,
  `tip_tar` varchar(1) NOT NULL,
  `id_titu` int NOT NULL,
  PRIMARY KEY (`num_tar`),
  KEY `id_titu_tar` (`id_titu`),
  CONSTRAINT `id_titu_tar` FOREIGN KEY (`id_titu`) REFERENCES `titular` (`id_titu`) ON UPDATE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tarjeta`
--

LOCK TABLES `tarjeta` WRITE;
/*!40000 ALTER TABLE `tarjeta` DISABLE KEYS */;
/*!40000 ALTER TABLE `tarjeta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `terminal`
--

DROP TABLE IF EXISTS `terminal`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `terminal` (
  `id_term` int NOT NULL,
  `fond_term` decimal(10,2) NOT NULL DEFAULT '100000.00',
  PRIMARY KEY (`id_term`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `terminal`
--

LOCK TABLES `terminal` WRITE;
/*!40000 ALTER TABLE `terminal` DISABLE KEYS */;
INSERT INTO `terminal` VALUES (1,100000.00);
/*!40000 ALTER TABLE `terminal` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `titular`
--

DROP TABLE IF EXISTS `titular`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `titular` (
  `id_titu` int NOT NULL AUTO_INCREMENT,
  `nom_titu` varchar(75) NOT NULL,
  PRIMARY KEY (`id_titu`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `titular`
--

LOCK TABLES `titular` WRITE;
/*!40000 ALTER TABLE `titular` DISABLE KEYS */;
/*!40000 ALTER TABLE `titular` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-05-19 13:42:06

insert into titular (nom_titu) values('Juan Carlos Perez Gonzalez');
insert into titular (nom_titu) values('Karen Amelia Jones West');
insert into titular (nom_titu) values('Joaquin Bayron Vela Gomez');
select * from titular;

insert into tarjeta (num_tar, pin_tar, cad_tar, tip_tar, id_titu) values ('1234567890123456','5678','2026-05-19','d',1);
insert into tarjeta (num_tar, pin_tar, cad_tar, tip_tar, id_titu) values ('0987654321098765','4321','2030-10-22','c',2);
insert into tarjeta (num_tar, pin_tar, cad_tar, tip_tar, id_titu) values ('1212121212121212','7890','2028-02-01','d',3);

insert into servicio (nom_serv) values ('Agua');
insert into servicio (nom_serv) values ('Luz');
insert into servicio (nom_serv) values ('Gas');
select * from servicio;

insert into movimiento (id_mov,tipo_mov,mont_mov,fch_mov,num_tar) values ('1','s',200,'2024-05-20 23:35:00','1234567890123456');
select * from movimiento;

insert into pag_servicio (id_mov,id_serv,ref_pg) values ('1',2,'22222222222');
select * from pag_servicio;

select * from terminal