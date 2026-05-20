/*
 Navicat Premium Dump SQL

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 90100 (9.1.0)
 Source Host           : localhost:3306
 Source Schema         : bookshopdb

 Target Server Type    : MySQL
 Target Server Version : 90100 (9.1.0)
 File Encoding         : 65001

 Date: 21/05/2026 02:48:46
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
  `id` bigint NOT NULL AUTO_INCREMENT,
  `userId` bigint NOT NULL,
  `status` tinyint NOT NULL,
  `deliveryMethod` tinyint NOT NULL,
  `deliveryPrice` float NOT NULL,
  `totalPrice` float NULL DEFAULT NULL,
  `createdAt` datetime NOT NULL,
  `updatedAt` datetime NULL DEFAULT NULL,
  PRIMARY KEY (`id`) USING BTREE,
  INDEX `idx_orders_user`(`userId`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 61 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = FIXED;

SET FOREIGN_KEY_CHECKS = 1;
