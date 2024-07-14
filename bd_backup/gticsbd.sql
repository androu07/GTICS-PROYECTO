-- MySQL dump 10.13  Distrib 8.0.32, for Win64 (x86_64)
--
-- Host: localhost    Database: gticsbd
-- ------------------------------------------------------
-- Server version	8.0.32

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
-- Table structure for table `carrito`
--

DROP TABLE IF EXISTS `carrito`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `carrito` (
  `medicamentos_id_medicamentos` int NOT NULL,
  `usuario_id_usuario` int NOT NULL,
  `cantidad` int DEFAULT '1',
  `numero_pedido` varchar(45) DEFAULT NULL,
  `estado_de_compra` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`medicamentos_id_medicamentos`,`usuario_id_usuario`),
  KEY `fk_usuario_has_medicamentos_medicamentos1_idx` (`medicamentos_id_medicamentos`),
  KEY `fk_carrito_usuario1_idx` (`usuario_id_usuario`),
  CONSTRAINT `fk_carrito_usuario1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_usuario_has_medicamentos_medicamentos1` FOREIGN KEY (`medicamentos_id_medicamentos`) REFERENCES `medicamentos` (`id_medicamentos`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `carrito`
--

LOCK TABLES `carrito` WRITE;
/*!40000 ALTER TABLE `carrito` DISABLE KEYS */;
/*!40000 ALTER TABLE `carrito` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicamentos`
--

DROP TABLE IF EXISTS `medicamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicamentos` (
  `id_medicamentos` int NOT NULL AUTO_INCREMENT,
  `descripcion` varchar(400) DEFAULT NULL,
  `nombre` varchar(45) DEFAULT NULL,
  `foto` longblob,
  `inventario` int DEFAULT NULL,
  `precio_unidad` double DEFAULT NULL,
  `fecha_ingreso` date DEFAULT NULL,
  `categoria` varchar(45) DEFAULT NULL,
  `dosis` varchar(45) DEFAULT NULL,
  `borrado_logico` int DEFAULT NULL,
  PRIMARY KEY (`id_medicamentos`)
) ENGINE=InnoDB AUTO_INCREMENT=26 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicamentos`
--

LOCK TABLES `medicamentos` WRITE;
/*!40000 ALTER TABLE `medicamentos` DISABLE KEYS */;
/*!40000 ALTER TABLE `medicamentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicamentos_del_pedido`
--

DROP TABLE IF EXISTS `medicamentos_del_pedido`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicamentos_del_pedido` (
  `id_medicamentos_del_pedido` int NOT NULL AUTO_INCREMENT,
  `nombre_medicamento` varchar(45) DEFAULT NULL,
  `costo_medicamento` double DEFAULT NULL,
  `cantidad` int DEFAULT NULL,
  `pedidos_paciente_idpedidos_paciente` int NOT NULL,
  `pedidos_paciente_usuario_id_usuario` int NOT NULL,
  PRIMARY KEY (`id_medicamentos_del_pedido`,`pedidos_paciente_idpedidos_paciente`,`pedidos_paciente_usuario_id_usuario`),
  KEY `fk_medicamentos_del_pedido_pedidos_paciente1_idx` (`pedidos_paciente_idpedidos_paciente`,`pedidos_paciente_usuario_id_usuario`),
  KEY `FK6254fi91xksw2lfvh52jdsgg2` (`pedidos_paciente_usuario_id_usuario`),
  CONSTRAINT `FK6254fi91xksw2lfvh52jdsgg2` FOREIGN KEY (`pedidos_paciente_usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`),
  CONSTRAINT `fk_medicamentos_del_pedido_pedidos_paciente1` FOREIGN KEY (`pedidos_paciente_idpedidos_paciente`, `pedidos_paciente_usuario_id_usuario`) REFERENCES `pedidos_paciente` (`idpedidos_paciente`, `usuario_id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicamentos_del_pedido`
--

LOCK TABLES `medicamentos_del_pedido` WRITE;
/*!40000 ALTER TABLE `medicamentos_del_pedido` DISABLE KEYS */;
/*!40000 ALTER TABLE `medicamentos_del_pedido` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `medicamentos_recojo`
--

DROP TABLE IF EXISTS `medicamentos_recojo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `medicamentos_recojo` (
  `idmedicamentos_recojo` int NOT NULL AUTO_INCREMENT,
  `nombre_medicamento` varchar(45) DEFAULT NULL,
  `costo_medicamento` double DEFAULT NULL,
  `cantidad` int DEFAULT NULL,
  `pedidos_paciente_recojo_idpedidos_paciente_recojo` int NOT NULL,
  `pedidos_paciente_recojo_usuario_id_usuario` int NOT NULL,
  PRIMARY KEY (`idmedicamentos_recojo`,`pedidos_paciente_recojo_idpedidos_paciente_recojo`,`pedidos_paciente_recojo_usuario_id_usuario`),
  KEY `fk_medicamentos_recojo_pedidos_paciente_recojo1_idx` (`pedidos_paciente_recojo_idpedidos_paciente_recojo`,`pedidos_paciente_recojo_usuario_id_usuario`),
  KEY `FKhnvcm2y6it02wnfudgv7brqk8` (`pedidos_paciente_recojo_usuario_id_usuario`),
  CONSTRAINT `FK42s1trfymav7oqclaaccfasnk` FOREIGN KEY (`pedidos_paciente_recojo_idpedidos_paciente_recojo`) REFERENCES `pedidos_paciente` (`idpedidos_paciente`),
  CONSTRAINT `fk_medicamentos_recojo_pedidos_paciente_recojo1` FOREIGN KEY (`pedidos_paciente_recojo_idpedidos_paciente_recojo`, `pedidos_paciente_recojo_usuario_id_usuario`) REFERENCES `pedidos_paciente_recojo` (`idpedidos_paciente_recojo`, `usuario_id_usuario`),
  CONSTRAINT `FKhnvcm2y6it02wnfudgv7brqk8` FOREIGN KEY (`pedidos_paciente_recojo_usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `medicamentos_recojo`
--

LOCK TABLES `medicamentos_recojo` WRITE;
/*!40000 ALTER TABLE `medicamentos_recojo` DISABLE KEYS */;
/*!40000 ALTER TABLE `medicamentos_recojo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos_paciente`
--

DROP TABLE IF EXISTS `pedidos_paciente`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos_paciente` (
  `idpedidos_paciente` int NOT NULL AUTO_INCREMENT,
  `nombre_paciente` varchar(45) DEFAULT NULL,
  `apellido_paciente` varchar(45) DEFAULT NULL,
  `medico_que_atiende` varchar(45) DEFAULT NULL,
  `seguro` varchar(45) DEFAULT NULL,
  `direccion` varchar(90) DEFAULT NULL,
  `distrito` varchar(45) DEFAULT NULL,
  `telefono` int DEFAULT NULL,
  `dni` int DEFAULT NULL,
  `hora_de_entrega` varchar(45) DEFAULT NULL,
  `receta` blob,
  `costo_total` double DEFAULT NULL,
  `tipo_de_pedido` varchar(45) DEFAULT NULL,
  `fecha_solicitud` varchar(45) DEFAULT NULL,
  `fecha_entrega` varchar(45) DEFAULT NULL,
  `validacion_del_pedido` varchar(45) DEFAULT NULL,
  `comentario` varchar(180) DEFAULT NULL,
  `fecha_validacion` varchar(45) DEFAULT NULL,
  `estado_del_pedido` varchar(45) DEFAULT NULL,
  `numero_tracking` varchar(45) DEFAULT NULL,
  `aviso_vencimiento` varchar(45) DEFAULT NULL,
  `metodo_pago` varchar(45) DEFAULT NULL,
  `usuario_id_usuario` int NOT NULL,
  PRIMARY KEY (`idpedidos_paciente`,`usuario_id_usuario`),
  KEY `fk_pedidos_paciente_usuario1_idx` (`usuario_id_usuario`),
  CONSTRAINT `fk_pedidos_paciente_usuario1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=11 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos_paciente`
--

LOCK TABLES `pedidos_paciente` WRITE;
/*!40000 ALTER TABLE `pedidos_paciente` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedidos_paciente` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos_paciente_recojo`
--

DROP TABLE IF EXISTS `pedidos_paciente_recojo`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos_paciente_recojo` (
  `idpedidos_paciente_recojo` int NOT NULL AUTO_INCREMENT,
  `nombre_paciente` varchar(45) DEFAULT NULL,
  `apellido_paciente` varchar(45) DEFAULT NULL,
  `medico_que_atiende` varchar(45) DEFAULT NULL,
  `seguro` varchar(45) DEFAULT NULL,
  `telefono` int DEFAULT NULL,
  `dni` int DEFAULT NULL,
  `receta` varchar(45) DEFAULT NULL,
  `costo_total` double DEFAULT NULL,
  `tipo_de_pedido` varchar(45) DEFAULT NULL,
  `fecha_solicitud` varchar(45) DEFAULT NULL,
  `fecha_entrega` varchar(45) DEFAULT NULL,
  `validacion_del_pedido` varchar(45) DEFAULT NULL,
  `comentario` varchar(180) DEFAULT NULL,
  `fecha_validacion` varchar(45) DEFAULT NULL,
  `estado_del_pedido` varchar(45) DEFAULT NULL,
  `numero_tracking` varchar(45) DEFAULT NULL,
  `aviso_vencimiento` varchar(45) DEFAULT NULL,
  `sede_de_recojo` varchar(45) DEFAULT NULL,
  `usuario_id_usuario` int NOT NULL,
  PRIMARY KEY (`idpedidos_paciente_recojo`,`usuario_id_usuario`),
  KEY `fk_pedidos_paciente_recojo_usuario1_idx` (`usuario_id_usuario`),
  CONSTRAINT `fk_pedidos_paciente_recojo_usuario1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=3 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos_paciente_recojo`
--

LOCK TABLES `pedidos_paciente_recojo` WRITE;
/*!40000 ALTER TABLE `pedidos_paciente_recojo` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedidos_paciente_recojo` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos_paciente_recojo_o_presencial`
--

DROP TABLE IF EXISTS `pedidos_paciente_recojo_o_presencial`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos_paciente_recojo_o_presencial` (
  `idpedidos_paciente_recojo_o_presencial` int NOT NULL AUTO_INCREMENT,
  `nombre_paciente` varchar(45) DEFAULT NULL,
  `apellido_paciente` varchar(45) DEFAULT NULL,
  `medico_que_atiende` varchar(45) DEFAULT NULL,
  `seguro` varchar(45) DEFAULT NULL,
  `telefono` int DEFAULT NULL,
  `dni` int DEFAULT NULL,
  `receta` varchar(45) DEFAULT NULL,
  `costo_total` double DEFAULT NULL,
  `tipo_de_pedido` varchar(45) DEFAULT NULL,
  `fecha_solicitud` varchar(45) DEFAULT NULL,
  `fecha_entrega` varchar(45) DEFAULT NULL,
  `validacion_del_pedido` varchar(45) DEFAULT NULL,
  `comentario` varchar(180) DEFAULT NULL,
  `fecha_validacion` varchar(45) DEFAULT NULL,
  `estado_del_pedido` varchar(45) DEFAULT NULL,
  `numero_tracking` varchar(45) DEFAULT NULL,
  `aviso_vencimiento` varchar(45) DEFAULT NULL,
  `sede_de_recojo` varchar(45) DEFAULT NULL,
  `usuario_id_usuario` int NOT NULL,
  PRIMARY KEY (`idpedidos_paciente_recojo_o_presencial`,`usuario_id_usuario`),
  KEY `fk_pedidos_paciente_recojo_o_presencial_usuario1_idx` (`usuario_id_usuario`),
  CONSTRAINT `fk_pedidos_paciente_recojo_o_presencial_usuario1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos_paciente_recojo_o_presencial`
--

LOCK TABLES `pedidos_paciente_recojo_o_presencial` WRITE;
/*!40000 ALTER TABLE `pedidos_paciente_recojo_o_presencial` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedidos_paciente_recojo_o_presencial` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos_reposicion`
--

DROP TABLE IF EXISTS `pedidos_reposicion`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos_reposicion` (
  `id_pedidos_reposicion` int NOT NULL AUTO_INCREMENT,
  `usuario_id_usuario` int NOT NULL,
  `fecha_solicitud` date DEFAULT NULL,
  `costo_total` double DEFAULT NULL,
  `fecha_entrega` date DEFAULT NULL,
  `estado_de_reposicion` varchar(45) DEFAULT NULL,
  `proveedor_id_proveedor` int NOT NULL,
  PRIMARY KEY (`id_pedidos_reposicion`,`usuario_id_usuario`,`proveedor_id_proveedor`),
  KEY `fk_ventas_usuario1_idx` (`usuario_id_usuario`),
  KEY `fk_pedidos_reposicion_proveedor1_idx` (`proveedor_id_proveedor`),
  CONSTRAINT `fk_pedidos_reposicion_proveedor1` FOREIGN KEY (`proveedor_id_proveedor`) REFERENCES `proveedor` (`id_proveedor`),
  CONSTRAINT `fk_ventas_usuario1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos_reposicion`
--

LOCK TABLES `pedidos_reposicion` WRITE;
/*!40000 ALTER TABLE `pedidos_reposicion` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedidos_reposicion` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `pedidos_reposicion_has_medicamentos`
--

DROP TABLE IF EXISTS `pedidos_reposicion_has_medicamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `pedidos_reposicion_has_medicamentos` (
  `pedidos_reposicion_id_pedidos_reposicion` int NOT NULL,
  `pedidos_reposicion_usuario_id_usuario` int NOT NULL,
  `pedidos_reposicion_proveedor_id_proveedor` int NOT NULL,
  `medicamentos_id_medicamentos` int NOT NULL,
  `cantidad` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`pedidos_reposicion_id_pedidos_reposicion`,`pedidos_reposicion_usuario_id_usuario`,`pedidos_reposicion_proveedor_id_proveedor`,`medicamentos_id_medicamentos`),
  KEY `fk_pedidos_reposicion_has_medicamentos_medicamentos1_idx` (`medicamentos_id_medicamentos`),
  KEY `fk_pedidos_reposicion_has_medicamentos_pedidos_reposicion1_idx` (`pedidos_reposicion_id_pedidos_reposicion`,`pedidos_reposicion_usuario_id_usuario`,`pedidos_reposicion_proveedor_id_proveedor`),
  CONSTRAINT `fk_pedidos_reposicion_has_medicamentos_medicamentos1` FOREIGN KEY (`medicamentos_id_medicamentos`) REFERENCES `medicamentos` (`id_medicamentos`),
  CONSTRAINT `fk_pedidos_reposicion_has_medicamentos_pedidos_reposicion1` FOREIGN KEY (`pedidos_reposicion_id_pedidos_reposicion`, `pedidos_reposicion_usuario_id_usuario`, `pedidos_reposicion_proveedor_id_proveedor`) REFERENCES `pedidos_reposicion` (`id_pedidos_reposicion`, `usuario_id_usuario`, `proveedor_id_proveedor`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `pedidos_reposicion_has_medicamentos`
--

LOCK TABLES `pedidos_reposicion_has_medicamentos` WRITE;
/*!40000 ALTER TABLE `pedidos_reposicion_has_medicamentos` DISABLE KEYS */;
/*!40000 ALTER TABLE `pedidos_reposicion_has_medicamentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `proveedor`
--

DROP TABLE IF EXISTS `proveedor`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `proveedor` (
  `id_proveedor` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `descripcion` varchar(250) DEFAULT NULL,
  PRIMARY KEY (`id_proveedor`)
) ENGINE=InnoDB AUTO_INCREMENT=13 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `proveedor`
--

LOCK TABLES `proveedor` WRITE;
/*!40000 ALTER TABLE `proveedor` DISABLE KEYS */;
/*!40000 ALTER TABLE `proveedor` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `roles`
--

DROP TABLE IF EXISTS `roles`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `roles` (
  `id_roles` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id_roles`)
) ENGINE=InnoDB AUTO_INCREMENT=5 DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `roles`
--

LOCK TABLES `roles` WRITE;
/*!40000 ALTER TABLE `roles` DISABLE KEYS */;
INSERT INTO `roles` VALUES (1,'Superadmin'),(2,'Admin'),(3,'Farmacista'),(4,'Paciente');
/*!40000 ALTER TABLE `roles` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sede`
--

DROP TABLE IF EXISTS `sede`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sede` (
  `id_sede` int NOT NULL AUTO_INCREMENT,
  `nombre` varchar(45) DEFAULT NULL,
  `estado` varchar(45) DEFAULT NULL,
  PRIMARY KEY (`id_sede`)
) ENGINE=InnoDB AUTO_INCREMENT=12 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sede`
--

LOCK TABLES `sede` WRITE;
/*!40000 ALTER TABLE `sede` DISABLE KEYS */;
/*!40000 ALTER TABLE `sede` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `sede_has_medicamentos`
--

DROP TABLE IF EXISTS `sede_has_medicamentos`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `sede_has_medicamentos` (
  `sede_id_sede` int NOT NULL,
  `medicamentos_id_medicamentos` int NOT NULL,
  PRIMARY KEY (`sede_id_sede`,`medicamentos_id_medicamentos`),
  KEY `fk_sede_has_medicamentos_medicamentos1_idx` (`medicamentos_id_medicamentos`),
  KEY `fk_sede_has_medicamentos_sede1_idx` (`sede_id_sede`),
  CONSTRAINT `fk_sede_has_medicamentos_medicamentos1` FOREIGN KEY (`medicamentos_id_medicamentos`) REFERENCES `medicamentos` (`id_medicamentos`),
  CONSTRAINT `fk_sede_has_medicamentos_sede1` FOREIGN KEY (`sede_id_sede`) REFERENCES `sede` (`id_sede`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `sede_has_medicamentos`
--

LOCK TABLES `sede_has_medicamentos` WRITE;
/*!40000 ALTER TABLE `sede_has_medicamentos` DISABLE KEYS */;
/*!40000 ALTER TABLE `sede_has_medicamentos` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spring_session`
--

DROP TABLE IF EXISTS `spring_session`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spring_session` (
  `PRIMARY_ID` char(36) NOT NULL,
  `SESSION_ID` char(36) NOT NULL,
  `CREATION_TIME` bigint NOT NULL,
  `LAST_ACCESS_TIME` bigint NOT NULL,
  `MAX_INACTIVE_INTERVAL` int NOT NULL,
  `EXPIRY_TIME` bigint NOT NULL,
  `PRINCIPAL_NAME` varchar(100) DEFAULT NULL,
  PRIMARY KEY (`PRIMARY_ID`),
  UNIQUE KEY `SPRING_SESSION_IX1` (`SESSION_ID`),
  KEY `SPRING_SESSION_IX2` (`EXPIRY_TIME`),
  KEY `SPRING_SESSION_IX3` (`PRINCIPAL_NAME`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spring_session`
--

LOCK TABLES `spring_session` WRITE;
/*!40000 ALTER TABLE `spring_session` DISABLE KEYS */;
INSERT INTO `spring_session` VALUES ('8b02282f-65df-45cb-a2a0-b0811079d4f3','86b25563-ca43-4464-b30b-53a8cb55694d',1717099854843,1717100676405,1800,1717102476405,'rodlu@gmail.com');
/*!40000 ALTER TABLE `spring_session` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `spring_session_attributes`
--

DROP TABLE IF EXISTS `spring_session_attributes`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `spring_session_attributes` (
  `SESSION_PRIMARY_ID` char(36) NOT NULL,
  `ATTRIBUTE_NAME` varchar(200) NOT NULL,
  `ATTRIBUTE_BYTES` blob NOT NULL,
  PRIMARY KEY (`SESSION_PRIMARY_ID`,`ATTRIBUTE_NAME`),
  CONSTRAINT `SPRING_SESSION_ATTRIBUTES_FK` FOREIGN KEY (`SESSION_PRIMARY_ID`) REFERENCES `spring_session` (`PRIMARY_ID`) ON DELETE CASCADE
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb4 COLLATE=utf8mb4_0900_ai_ci ROW_FORMAT=DYNAMIC;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `spring_session_attributes`
--

LOCK TABLES `spring_session_attributes` WRITE;
/*!40000 ALTER TABLE `spring_session_attributes` DISABLE KEYS */;
INSERT INTO `spring_session_attributes` VALUES ('8b02282f-65df-45cb-a2a0-b0811079d4f3','org.springframework.security.web.csrf.HttpSessionCsrfTokenRepository.CSRF_TOKEN',_binary '¬\í\0sr\06org.springframework.security.web.csrf.DefaultCsrfTokenZ\ï·\È/¢û\Õ\0L\0\nheaderNamet\0Ljava/lang/String;L\0\rparameterNameq\0~\0L\0tokenq\0~\0xpt\0X-CSRF-TOKENt\0_csrft\0$ba8f73a7-1e41-4447-abc9-4f90aab5ca03'),('8b02282f-65df-45cb-a2a0-b0811079d4f3','SPRING_SECURITY_CONTEXT',_binary '¬\í\0sr\0=org.springframework.security.core.context.SecurityContextImpl\0\0\0\0\0\0X\0L\0authenticationt\02Lorg/springframework/security/core/Authentication;xpsr\0Oorg.springframework.security.authentication.UsernamePasswordAuthenticationToken\0\0\0\0\0\0X\0L\0credentialst\0Ljava/lang/Object;L\0	principalq\0~\0xr\0Gorg.springframework.security.authentication.AbstractAuthenticationTokenÓª(~nGd\0Z\0\rauthenticatedL\0authoritiest\0Ljava/util/Collection;L\0detailsq\0~\0xpsr\0&java.util.Collections$UnmodifiableListü%1µ\ìŽ\0L\0listt\0Ljava/util/List;xr\0,java.util.Collections$UnmodifiableCollectionB\0€\Ë^÷\0L\0cq\0~\0xpsr\0java.util.ArrayListx\Ò™\Ça\0I\0sizexp\0\0\0w\0\0\0sr\0Borg.springframework.security.core.authority.SimpleGrantedAuthority\0\0\0\0\0\0X\0L\0rolet\0Ljava/lang/String;xpt\0Pacientexq\0~\0\rsr\0Horg.springframework.security.web.authentication.WebAuthenticationDetails\0\0\0\0\0\0X\0L\0\rremoteAddressq\0~\0L\0	sessionIdq\0~\0xpt\00:0:0:0:0:0:0:1t\0$29cecf9c-25cf-49c1-917c-1dc7e677939bpsr\02org.springframework.security.core.userdetails.User\0\0\0\0\0\0X\0Z\0accountNonExpiredZ\0accountNonLockedZ\0credentialsNonExpiredZ\0enabledL\0authoritiest\0Ljava/util/Set;L\0passwordq\0~\0L\0usernameq\0~\0xpsr\0%java.util.Collections$UnmodifiableSet€’Ñ›€U\0\0xq\0~\0\nsr\0java.util.TreeSetÝ˜P“•\í‡[\0\0xpsr\0Forg.springframework.security.core.userdetails.User$AuthorityComparator\0\0\0\0\0\0X\0\0xpw\0\0\0q\0~\0xpt\0rodlu@gmail.com'),('8b02282f-65df-45cb-a2a0-b0811079d4f3','SPRING_SECURITY_SAVED_REQUEST',_binary '¬\í\0sr\0Aorg.springframework.security.web.savedrequest.DefaultSavedRequest\0\0\0\0\0\0X\0I\0\nserverPortL\0contextPatht\0Ljava/lang/String;L\0cookiest\0Ljava/util/ArrayList;L\0headerst\0Ljava/util/Map;L\0localesq\0~\0L\0matchingRequestParameterNameq\0~\0L\0methodq\0~\0L\0\nparametersq\0~\0L\0pathInfoq\0~\0L\0queryStringq\0~\0L\0\nrequestURIq\0~\0L\0\nrequestURLq\0~\0L\0schemeq\0~\0L\0\nserverNameq\0~\0L\0servletPathq\0~\0xp\0\0t\0\0sr\0java.util.ArrayListx\Ò™\Ça\0I\0sizexp\0\0\0\0w\0\0\0\0xsr\0java.util.TreeMapÁö>-%j\æ\0L\0\ncomparatort\0Ljava/util/Comparator;xpsr\0*java.lang.String$CaseInsensitiveComparatorw\\}\\P\å\Î\0\0xpw\0\0\0t\0acceptsq\0~\0\0\0\0w\0\0\0t\0‡text/html,application/xhtml+xml,application/xml;q=0.9,image/avif,image/webp,image/apng,*/*;q=0.8,application/signed-exchange;v=b3;q=0.7xt\0accept-encodingsq\0~\0\0\0\0w\0\0\0t\0gzip, deflate, br, zstdxt\0accept-languagesq\0~\0\0\0\0w\0\0\0t\0en,es-ES;q=0.9,es;q=0.8xt\0\nconnectionsq\0~\0\0\0\0w\0\0\0t\0\nkeep-alivext\0hostsq\0~\0\0\0\0w\0\0\0t\0localhost:8080xt\0purposesq\0~\0\0\0\0w\0\0\0t\0prefetchxt\0	sec-ch-uasq\0~\0\0\0\0w\0\0\0t\0A\"Google Chrome\";v=\"125\", \"Chromium\";v=\"125\", \"Not.A/Brand\";v=\"24\"xt\0sec-ch-ua-mobilesq\0~\0\0\0\0w\0\0\0t\0?0xt\0sec-ch-ua-platformsq\0~\0\0\0\0w\0\0\0t\0	\"Windows\"xt\0sec-fetch-destsq\0~\0\0\0\0w\0\0\0t\0documentxt\0sec-fetch-modesq\0~\0\0\0\0w\0\0\0t\0navigatext\0sec-fetch-sitesq\0~\0\0\0\0w\0\0\0t\0nonext\0sec-fetch-usersq\0~\0\0\0\0w\0\0\0t\0?1xt\0sec-purposesq\0~\0\0\0\0w\0\0\0t\0prefetch;prerenderxt\0upgrade-insecure-requestssq\0~\0\0\0\0w\0\0\0t\01xt\0\nuser-agentsq\0~\0\0\0\0w\0\0\0t\0oMozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/125.0.0.0 Safari/537.36xxsq\0~\0\0\0\0w\0\0\0sr\0java.util.Locale~ø`œ0ù\ì\0I\0hashcodeL\0countryq\0~\0L\0\nextensionsq\0~\0L\0languageq\0~\0L\0scriptq\0~\0L\0variantq\0~\0xpÿÿÿÿq\0~\0q\0~\0t\0enq\0~\0q\0~\0xsq\0~\0>ÿÿÿÿt\0ESq\0~\0t\0esq\0~\0q\0~\0xsq\0~\0>ÿÿÿÿq\0~\0q\0~\0q\0~\0Cq\0~\0q\0~\0xxt\0continuet\0GETsq\0~\0pw\0\0\0\0xppt\0/paciente/iniciot\0%http://localhost:8080/paciente/iniciot\0httpt\0	localhostt\0/paciente/inicio'),('8b02282f-65df-45cb-a2a0-b0811079d4f3','usuario',_binary '¬\í\0sr\0!com.example.webapp.entity.Usuario!s£z\í÷bÀ\0I\0borrado_logicoI\0dniI\0idL\0	apellidost\0Ljava/lang/String;L\0codigo_colegiaturaq\0~\0L\0\ncontrasenaq\0~\0L\0correoq\0~\0L\0	direccionq\0~\0L\0distritoq\0~\0L\0estadoq\0~\0L\0estado_solicitudq\0~\0L\0fecha_creaciont\0Ljava/sql/Date;L\0imagenq\0~\0L\0motivo_rechazoq\0~\0L\0nombresq\0~\0L\0\nreferenciaq\0~\0L\0rolt\0!Lcom/example/webapp/entity/Roles;L\0seguroq\0~\0L\0telefonoq\0~\0xp\0\0\0\0\0Ö‡\0\0\0t\0Lujant\0No tienet\0<$2a$10$u828b590H.p5.N5UZnLRxeVZrIiC/f/9/3AEREiwFeg9k9MLSt37Wt\0rodlu@gmail.compt\0San Juan de Luriganchot\01t\0Aceptadosr\0\rjava.sql.DateúFh?5f—\0\0xr\0java.util.DatehjKYt\0\0xpw\0\0(9`€xppt\0Andrespsr\0com.example.webapp.entity.Roles4˜4ÿ¢T\0I\0idL\0nombreq\0~\0xp\0\0\0t\0Pacientet\0Rimacp');
/*!40000 ALTER TABLE `spring_session_attributes` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `tarjeta`
--

DROP TABLE IF EXISTS `tarjeta`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `tarjeta` (
  `id_tarjeta` int NOT NULL,
  `numero` int DEFAULT NULL,
  `fecha_caduca` date DEFAULT NULL,
  `cci` int DEFAULT NULL,
  PRIMARY KEY (`id_tarjeta`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `tarjeta`
--

LOCK TABLES `tarjeta` WRITE;
/*!40000 ALTER TABLE `tarjeta` DISABLE KEYS */;
/*!40000 ALTER TABLE `tarjeta` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario`
--

DROP TABLE IF EXISTS `usuario`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario` (
  `id_usuario` int NOT NULL AUTO_INCREMENT,
  `nombres` varchar(45) NOT NULL,
  `apellidos` varchar(45) NOT NULL,
  `correo` varchar(45) NOT NULL,
  `dni` int NOT NULL,
  `codigo_colegiatura` varchar(45) DEFAULT NULL,
  `distrito` varchar(45) DEFAULT NULL,
  `seguro` varchar(45) DEFAULT NULL,
  `estado` int DEFAULT NULL,
  `contrasena` varchar(100) NOT NULL,
  `foto` longblob,
  `fecha_creacion` date DEFAULT NULL,
  `estado_solicitud` varchar(45) DEFAULT NULL,
  `motivo_rechazo` varchar(250) DEFAULT NULL,
  `borrado_logico` int DEFAULT NULL,
  `direccion` varchar(255) DEFAULT NULL,
  `imagen` varchar(255) DEFAULT NULL,
  `referencia` varchar(255) DEFAULT NULL,
  `telefono` varchar(255) DEFAULT NULL,
  `id_roles` int NOT NULL,
  PRIMARY KEY (`id_usuario`,`id_roles`),
  KEY `fk_usuario_roles1_idx` (`id_roles`),
  CONSTRAINT `fk_usuario_roles1` FOREIGN KEY (`id_roles`) REFERENCES `roles` (`id_roles`)
) ENGINE=InnoDB AUTO_INCREMENT=43 DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario`
--

LOCK TABLES `usuario` WRITE;
/*!40000 ALTER TABLE `usuario` DISABLE KEYS */;
INSERT INTO `usuario` VALUES (1,'Andres','Lujan','rodlu@gmail.com',1234567,'No tiene','San Juan de Lurigancho','Rimac',1,'$2a$10$u828b590H.p5.N5UZnLRxeVZrIiC/f/9/3AEREiwFeg9k9MLSt37W',NULL,'2024-04-29','Aceptado',NULL,0,NULL,NULL,NULL,NULL,4);
/*!40000 ALTER TABLE `usuario` ENABLE KEYS */;
UNLOCK TABLES;

--
-- Table structure for table `usuario_has_sede`
--

DROP TABLE IF EXISTS `usuario_has_sede`;
/*!40101 SET @saved_cs_client     = @@character_set_client */;
/*!50503 SET character_set_client = utf8mb4 */;
CREATE TABLE `usuario_has_sede` (
  `usuario_id_usuario` int NOT NULL,
  `sede_id_sede` int NOT NULL,
  PRIMARY KEY (`usuario_id_usuario`,`sede_id_sede`),
  KEY `fk_usuario_has_sede_sede1_idx` (`sede_id_sede`),
  KEY `fk_usuario_has_sede_usuario1_idx` (`usuario_id_usuario`),
  CONSTRAINT `fk_usuario_has_sede_sede1` FOREIGN KEY (`sede_id_sede`) REFERENCES `sede` (`id_sede`),
  CONSTRAINT `fk_usuario_has_sede_usuario1` FOREIGN KEY (`usuario_id_usuario`) REFERENCES `usuario` (`id_usuario`)
) ENGINE=InnoDB DEFAULT CHARSET=utf8mb3;
/*!40101 SET character_set_client = @saved_cs_client */;

--
-- Dumping data for table `usuario_has_sede`
--

LOCK TABLES `usuario_has_sede` WRITE;
/*!40000 ALTER TABLE `usuario_has_sede` DISABLE KEYS */;
/*!40000 ALTER TABLE `usuario_has_sede` ENABLE KEYS */;
UNLOCK TABLES;
/*!40103 SET TIME_ZONE=@OLD_TIME_ZONE */;

/*!40101 SET SQL_MODE=@OLD_SQL_MODE */;
/*!40014 SET FOREIGN_KEY_CHECKS=@OLD_FOREIGN_KEY_CHECKS */;
/*!40014 SET UNIQUE_CHECKS=@OLD_UNIQUE_CHECKS */;
/*!40101 SET CHARACTER_SET_CLIENT=@OLD_CHARACTER_SET_CLIENT */;
/*!40101 SET CHARACTER_SET_RESULTS=@OLD_CHARACTER_SET_RESULTS */;
/*!40101 SET COLLATION_CONNECTION=@OLD_COLLATION_CONNECTION */;
/*!40111 SET SQL_NOTES=@OLD_SQL_NOTES */;

-- Dump completed on 2024-05-30 15:26:45
