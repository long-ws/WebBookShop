/*
 Navicat Premium Dump SQL

 Source Server         : local
 Source Server Type    : MySQL
 Source Server Version : 90100 (9.1.0)
 Source Host           : localhost:3306
 Source Schema         : bookshopdbtest

 Target Server Type    : MySQL
 Target Server Version : 90100 (9.1.0)
 File Encoding         : 65001

 Date: 03/06/2026 00:22:45
*/

SET NAMES utf8mb4;
SET FOREIGN_KEY_CHECKS = 0;

DROP DATABASE IF EXISTS `bookshopdbtest`;
CREATE DATABASE `bookshopdbtest` CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci;
USE `bookshopdbtest`;
-- ----------------------------
-- Table structure for cart
-- ----------------------------
DROP TABLE IF EXISTS `cart`;
CREATE TABLE `cart`  (
                         `id` bigint NOT NULL AUTO_INCREMENT,
                         `userId` bigint NOT NULL,
                         `createdAt` datetime NOT NULL,
                         `updatedAt` datetime NULL DEFAULT NULL,
                         PRIMARY KEY (`id`) USING BTREE,
                         INDEX `idx_cart_user`(`userId`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 2 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of cart
-- ----------------------------
INSERT INTO `cart` VALUES (1, -1, '2026-06-02 16:31:25', NULL);

-- ----------------------------
-- Table structure for cart_item
-- ----------------------------
DROP TABLE IF EXISTS `cart_item`;
CREATE TABLE `cart_item`  (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `cartId` bigint NOT NULL,
                              `productId` bigint NOT NULL,
                              `quantity` smallint NOT NULL,
                              `createdAt` datetime NOT NULL,
                              `updatedAt` datetime NULL DEFAULT NULL,
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE INDEX `uq_cartId_productId`(`cartId`, `productId`) USING BTREE,
                              INDEX `idx_cart_item_cart`(`cartId`) USING BTREE,
                              INDEX `idx_cart_item_product`(`productId`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 9 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of cart_item
-- ----------------------------

-- ----------------------------
-- Table structure for category
-- ----------------------------
DROP TABLE IF EXISTS `category`;
CREATE TABLE `category`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                             `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                             `imageName` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                             `isDeleted` bit(1) NOT NULL DEFAULT b'0',
                             `createdAt` datetime NOT NULL,
                             `updatedAt` datetime NULL DEFAULT NULL,
                             PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 16 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of category
-- ----------------------------
INSERT INTO `category` VALUES (1, 'Sách giáo khoa', 'Sách giáo khoa các cấp', 'sach-giao-khoa.jpg', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (2, 'Sách khoa học', 'Sách khoa học các loại', 'sach-khoa-hoc.png', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (3, 'Truyện tranh', 'Truyện tranh manga, comic', 'truyen-tranh.png', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (4, 'Tiểu thuyết', 'Tiểu thuyết VN & nước ngoài', 'tieu-thuyet.png', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (5, 'Truyện ngắn', 'Truyện ngắn hay', 'truyen-ngan.png', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (6, 'Truyện dài', 'Truyện dài nổi tiếng', 'truyen-dai.png', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (7, 'Sách giáo trình', 'Sách giáo trình đại học', 'sach-giao-trinh.png', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (8, 'Báo in', 'Các loại báo in', 'bao-in.png', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (9, 'Tạp chí', 'Tạp chí các loại', 'tap-chi.png', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (10, 'Tập san', 'Tập san trường học', 'tap-san.png', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (11, 'Sách nấu ăn', 'Sách hướng dẫn nấu ăn', 'nau-an.png', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (12, 'Sách kỹ thuật', 'Sách kỹ thuật các ngành', 'sach-ky-thuat.png', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (13, 'Sách nông nghiệp', 'Sách nông nghiệp, trồng trọt', 'sach-nong-nghiep.png', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (14, 'Sách thiếu nhi', 'Sách thiếu nhi, truyện tranh trẻ em', 'sach-thieu-nhi.png', b'0', '2026-06-02 23:20:14', NULL);
INSERT INTO `category` VALUES (15, 'Sách kỹ năng sống', 'Sách phát triển bản thân', 'sach-ky-nang-song.png', b'0', '2026-06-02 23:20:14', NULL);

-- ----------------------------
-- Table structure for email_verify_status
-- ----------------------------
DROP TABLE IF EXISTS `email_verify_status`;
CREATE TABLE `email_verify_status`  (
                                        `id` tinyint NOT NULL,
                                        `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                        `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                        `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                        `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                        `is_active` tinyint NULL DEFAULT 1,
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of email_verify_status
-- ----------------------------
INSERT INTO `email_verify_status` VALUES (0, 'UNVERIFIED', 'Chưa xác thực', 'Email chưa được xác nhận', '2026-06-02 23:20:14', 1);
INSERT INTO `email_verify_status` VALUES (1, 'VERIFIED', 'Đã xác thực', 'Email đã được xác nhận thành công', '2026-06-02 23:20:14', 1);

-- ----------------------------
-- Table structure for gender
-- ----------------------------
DROP TABLE IF EXISTS `gender`;
CREATE TABLE `gender`  (
                           `id` tinyint NOT NULL,
                           `code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                           `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                           `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                           `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                           `is_active` tinyint NULL DEFAULT 1,
                           PRIMARY KEY (`id`) USING BTREE,
                           UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of gender
-- ----------------------------
INSERT INTO `gender` VALUES (0, 'MALE', 'Nam', 'Giới tính Nam', '2026-06-02 23:20:14', 1);
INSERT INTO `gender` VALUES (1, 'FEMALE', 'Nữ', 'Giới tính Nữ', '2026-06-02 23:20:14', 1);

-- ----------------------------
-- Table structure for language_registry
-- ----------------------------
DROP TABLE IF EXISTS `language_registry`;
CREATE TABLE `language_registry`  (
                                      `id` int NOT NULL AUTO_INCREMENT,
                                      `code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                      `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                      `description` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                      `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                      `is_active` tinyint NULL DEFAULT 1,
                                      PRIMARY KEY (`id`) USING BTREE,
                                      UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of language_registry
-- ----------------------------
INSERT INTO `language_registry` VALUES (1, 'vi', 'Vietnamese', 'Tiếng Việt', '2026-06-02 23:20:14', 1);
INSERT INTO `language_registry` VALUES (2, 'en', 'English', 'Tiếng Anh', '2026-06-02 23:20:14', 1);
INSERT INTO `language_registry` VALUES (3, 'ja', 'Japanese', 'Tiếng Nhật', '2026-06-02 23:20:14', 1);
INSERT INTO `language_registry` VALUES (4, 'ko', 'Korean', 'Tiếng Hàn', '2026-06-02 23:20:14', 1);

-- ----------------------------
-- Table structure for oauth_provider
-- ----------------------------
DROP TABLE IF EXISTS `oauth_provider`;
CREATE TABLE `oauth_provider`  (
                                   `id` int NOT NULL AUTO_INCREMENT,
                                   `code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                   `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                   `description` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                   `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                   `is_active` tinyint NULL DEFAULT 1,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of oauth_provider
-- ----------------------------
INSERT INTO `oauth_provider` VALUES (1, 'GOOGLE', 'Google', 'Đăng nhập thông qua Google Account', '2026-06-02 23:20:14', 1);
INSERT INTO `oauth_provider` VALUES (2, 'FACEBOOK', 'Facebook', 'Đăng nhập thông qua Facebook Account', '2026-06-02 23:20:14', 1);

-- ----------------------------
-- Table structure for order_item
-- ----------------------------
DROP TABLE IF EXISTS `order_item`;
CREATE TABLE `order_item`  (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `orderId` bigint NOT NULL,
                               `productId` bigint NOT NULL,
                               `price` float NOT NULL,
                               `discount` float NOT NULL,
                               `quantity` smallint NOT NULL,
                               `createdAt` datetime NOT NULL,
                               `updatedAt` datetime NULL DEFAULT NULL,
                               PRIMARY KEY (`id`) USING BTREE,
                               INDEX `idx_order_item_orders`(`orderId`) USING BTREE,
                               INDEX `idx_order_item_product`(`productId`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 8 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of order_item
-- ----------------------------
INSERT INTO `order_item` VALUES (1, 1, 78, 104405, 0, 1, '2026-06-02 16:31:39', NULL);
INSERT INTO `order_item` VALUES (2, 2, 99, 199763, 20, 1, '2026-06-02 16:46:26', NULL);
INSERT INTO `order_item` VALUES (3, 3, 78, 104405, 0, 1, '2026-06-02 16:49:13', NULL);
INSERT INTO `order_item` VALUES (4, 4, 99, 199763, 20, 1, '2026-06-02 16:52:34', NULL);
INSERT INTO `order_item` VALUES (5, 4, 46, 384754, 20, 1, '2026-06-02 16:52:34', NULL);
INSERT INTO `order_item` VALUES (6, 5, 99, 199763, 20, 1, '2026-06-02 17:07:00', NULL);
INSERT INTO `order_item` VALUES (7, 6, 78, 104405, 0, 1, '2026-06-02 17:10:08', NULL);

-- ----------------------------
-- Table structure for orders
-- ----------------------------
DROP TABLE IF EXISTS `orders`;
CREATE TABLE `orders`  (
                           `id` bigint NOT NULL AUTO_INCREMENT,
                           `userId` bigint NULL DEFAULT NULL,
                           `status` tinyint NOT NULL,
                           `deliveryMethod` tinyint NOT NULL,
                           `deliveryPrice` float NOT NULL,
                           `totalPrice` decimal(15, 2) NULL DEFAULT NULL,
                           `createdAt` datetime NOT NULL,
                           `updatedAt` datetime NULL DEFAULT NULL,
                           PRIMARY KEY (`id`) USING BTREE,
                           INDEX `idx_orders_user`(`userId`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of orders
-- ----------------------------
INSERT INTO `orders` VALUES (1, -1, 1, 1, 20000, 124405.00, '2026-06-02 16:31:39', NULL);
INSERT INTO `orders` VALUES (2, -1, 1, 1, 20000, 139810.40, '2026-06-02 16:46:26', NULL);
INSERT INTO `orders` VALUES (3, -1, 1, 1, 20000, 124405.00, '2026-06-02 16:49:13', NULL);
INSERT INTO `orders` VALUES (4, -1, 1, 1, 20000, 487613.60, '2026-06-02 16:52:34', NULL);
INSERT INTO `orders` VALUES (5, -1, 1, 1, 20000, 179810.40, '2026-06-02 17:07:00', NULL);
INSERT INTO `orders` VALUES (6, -1, 1, 2, 16000, 84405.00, '2026-06-02 17:10:08', NULL);

-- ----------------------------
-- Table structure for payments
-- ----------------------------
DROP TABLE IF EXISTS `payments`;
CREATE TABLE `payments`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `order_id` bigint NOT NULL,
                             `user_id` bigint NULL DEFAULT NULL,
                             `status` tinyint NULL DEFAULT 0 COMMENT '0: Pending, 1: Success, 2: Failed',
                             `created_at` timestamp NULL DEFAULT NULL,
                             `expired_at` timestamp NULL DEFAULT NULL,
                             `vnp_TxnRef` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                             `vnp_TransactionNo` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                             `amount` float NOT NULL,
                             `vnp_ResponseCode` varchar(5) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                             `pay_date` timestamp NULL DEFAULT NULL,
                             `bank_code` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                             `is_expired` tinyint(1) NOT NULL DEFAULT 0,
                             PRIMARY KEY (`id`) USING BTREE,
                             INDEX `idx_vnp_TxnRef`(`vnp_TxnRef`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 6 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of payments
-- ----------------------------
INSERT INTO `payments` VALUES (1, 2, -1, 0, '2026-06-02 16:46:26', '2026-06-02 17:16:26', 'ORDER_-1_2_2606022346', NULL, 139810, NULL, NULL, NULL, 0);
INSERT INTO `payments` VALUES (2, 3, -1, 0, '2026-06-02 16:49:13', '2026-06-02 17:19:13', 'ORDER_-1_3_2606022349', NULL, 124405, NULL, NULL, NULL, 0);
INSERT INTO `payments` VALUES (3, 4, -1, 0, '2026-06-02 16:52:34', '2026-06-02 17:22:34', 'ORDER_-1_4_2606022352', NULL, 487614, NULL, NULL, NULL, 0);
INSERT INTO `payments` VALUES (4, 5, -1, 0, '2026-06-02 17:07:00', '2026-06-02 17:37:00', 'ORDER_-1_5_2606030006', NULL, 179810, NULL, NULL, NULL, 0);
INSERT INTO `payments` VALUES (5, 6, -1, 1, '2026-06-02 17:10:08', '2026-06-02 17:40:08', 'ORDER_-1_6_2606030010', '15566939', 84405, '00', '2026-06-02 17:21:56', 'NCB', 0);

-- ----------------------------
-- Table structure for permission_registry
-- ----------------------------
DROP TABLE IF EXISTS `permission_registry`;
CREATE TABLE `permission_registry`  (
                                        `id` int NOT NULL AUTO_INCREMENT,
                                        `code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                        `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                        `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                        `module` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                        `is_system` tinyint NULL DEFAULT 0,
                                        `is_active` tinyint NULL DEFAULT 1,
                                        `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 52 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of permission_registry
-- ----------------------------
INSERT INTO `permission_registry` VALUES (1, 'role.view', 'Xem role', 'Xem danh sách role', 'ROLE', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (2, 'role.create', 'Tạo role', 'Tạo role mới', 'ROLE', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (3, 'role.update', 'Cập nhật role', 'Cập nhật thông tin role', 'ROLE', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (4, 'role.delete', 'Xóa role', 'Xóa role', 'ROLE', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (5, 'role.assign_permission', 'Gán permission', 'Gán permission cho role', 'ROLE', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (6, 'permission.view', 'Xem permission', 'Xem danh sách permission', 'PERMISSION', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (7, 'permission.create', 'Tạo permission', 'Tạo permission mới', 'PERMISSION', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (8, 'permission.update', 'Cập nhật permission', 'Cập nhật thông tin permission', 'PERMISSION', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (9, 'permission.delete', 'Xóa permission', 'Xóa permission', 'PERMISSION', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (10, 'category.view', 'Xem thể loại', 'Xem danh sách thể loại', 'CATEGORY', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (11, 'category.create', 'Tạo thể loại', 'Tạo thể loại mới', 'CATEGORY', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (12, 'category.update', 'Cập nhật thể loại', 'Cập nhật thông tin thể loại', 'CATEGORY', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (13, 'category.delete', 'Xóa thể loại', 'Xóa thể loại', 'CATEGORY', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (14, 'user.view', 'Xem user', 'Xem thông tin user', 'USER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (15, 'user.detail', 'Xem chi tiết user', 'Xem chi tiết một user', 'USER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (16, 'user.create', 'Tạo user', 'Tạo user mới', 'USER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (17, 'user.update', 'Cập nhật user', 'Cập nhật thông tin user', 'USER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (18, 'user.delete', 'Xóa user', 'Xóa user', 'USER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (19, 'user.assign_role', 'Gán role', 'Gán role cho user', 'USER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (20, 'product.view', 'Xem sản phẩm', 'Xem thông tin sản phẩm', 'PRODUCT', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (21, 'product.create', 'Tạo sản phẩm', 'Tạo sản phẩm mới', 'PRODUCT', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (22, 'product.update', 'Cập nhật sản phẩm', 'Cập nhật thông tin sản phẩm', 'PRODUCT', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (23, 'product.delete', 'Xóa sản phẩm', 'Xóa sản phẩm', 'PRODUCT', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (24, 'order.view', 'Xem đơn hàng', 'Xem thông tin đơn hàng', 'ORDER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (25, 'order.create', 'Tạo đơn hàng', 'Tạo đơn hàng mới', 'ORDER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (26, 'order.update', 'Cập nhật đơn hàng', 'Cập nhật thông tin đơn hàng', 'ORDER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (27, 'order.delete', 'Xóa đơn hàng', 'Xóa đơn hàng', 'ORDER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (28, 'order.view_all', 'Xem tất cả đơn hàng', 'Xem đơn hàng của tất cả user', 'ORDER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (29, 'cart.view', 'Xem giỏ hàng', 'Xem giỏ hàng', 'CART', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (30, 'cart.manage', 'Quản lý giỏ hàng', 'Quản lý giỏ hàng', 'CART', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (31, 'review.view', 'Xem review', 'Xem review sản phẩm', 'REVIEW', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (32, 'review.create', 'Tạo review', 'Tạo review mới', 'REVIEW', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (33, 'review.update', 'Cập nhật review', 'Cập nhật review', 'REVIEW', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (34, 'review.delete', 'Xóa review', 'Xóa review', 'REVIEW', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (35, 'review.moderate', 'Moderate review', 'Duyệt/xóa review', 'REVIEW', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (36, 'voucher.view', 'Xem voucher', 'Xem thông tin voucher', 'VOUCHER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (37, 'voucher.create', 'Tạo voucher', 'Tạo voucher mới', 'VOUCHER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (38, 'voucher.update', 'Cập nhật voucher', 'Cập nhật thông tin voucher', 'VOUCHER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (39, 'voucher.delete', 'Xóa voucher', 'Xóa voucher', 'VOUCHER', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (40, 'report.view', 'Xem báo cáo', 'Xem báo cáo thống kê', 'REPORT', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (41, 'report.export', 'Xuất báo cáo', 'Xuất báo cáo', 'REPORT', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (42, 'shipment.view', 'Xem vận đơn', 'Xem thông tin vận đơn', 'SHIPMENT', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (43, 'shipment.create', 'Tạo vận đơn', 'Tạo vận đơn mới', 'SHIPMENT', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (44, 'shipment.update', 'Cập nhật vận đơn', 'Cập nhật thông tin vận đơn', 'SHIPMENT', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (45, 'shipment.delete', 'Xóa vận đơn', 'Xóa vận đơn', 'SHIPMENT', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (46, 'shipping_config.view', 'Xem cấu hình ship', 'Xem cấu hình vận chuyển', 'SHIPPING_CONFIG', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (47, 'shipping_config.create', 'Tạo cấu hình ship', 'Tạo cấu hình vận chuyển', 'SHIPPING_CONFIG', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (48, 'shipping_config.update', 'Cập nhật cấu hình ship', 'Cập nhật cấu hình vận chuyển', 'SHIPPING_CONFIG', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (49, 'shipping_config.delete', 'Xóa cấu hình ship', 'Xóa cấu hình vận chuyển', 'SHIPPING_CONFIG', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (50, 'settings.view', 'Xem cài đặt', 'Xem cài đặt hệ thống', 'SETTINGS', 1, 1, '2026-06-02 23:20:14');
INSERT INTO `permission_registry` VALUES (51, 'settings.update', 'Cập nhật cài đặt', 'Cập nhật cài đặt hệ thống', 'SETTINGS', 1, 1, '2026-06-02 23:20:14');

-- ----------------------------
-- Table structure for product
-- ----------------------------
DROP TABLE IF EXISTS `product`;
CREATE TABLE `product`  (
                            `id` bigint NOT NULL AUTO_INCREMENT,
                            `name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                            `price` float NOT NULL,
                            `discount` float NOT NULL,
                            `quantity` smallint NOT NULL,
                            `totalBuy` smallint NOT NULL,
                            `author` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                            `pages` smallint NOT NULL,
                            `publisher` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                            `yearPublishing` year NOT NULL,
                            `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                            `imageName` varchar(35) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                            `shop` bit(1) NOT NULL,
                            `isDeleted` bit(1) NOT NULL DEFAULT b'0',
                            `createdAt` datetime NOT NULL,
                            `updatedAt` datetime NULL DEFAULT NULL,
                            `startsAt` datetime NULL DEFAULT NULL,
                            `endsAt` datetime NULL DEFAULT NULL,
                            PRIMARY KEY (`id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 101 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product
-- ----------------------------
INSERT INTO `product` VALUES (1, 'Sách Toyletry', 466183, 0, 86, 86, 'Stafford Hayden', 250, 'NXB Giáo dục', 2013, 'Consequat cupidatat magna nostrud ullamco non commodo esse. Veniam anim ipsum duis cillum cillum exercitation deserunt irure sint eiusmod.', 'temp-10075522682831764585.jpg', b'0', b'0', '2021-03-23 08:22:50', NULL, NULL, NULL);
INSERT INTO `product` VALUES (2, 'Sách Sultrax', 26228, 0, 23, 60, 'Diane Nguyen', 305, 'NXB Đại học Sư phạm TP.HCM', 2011, 'Ipsum consequat reprehenderit amet ullamco dolore consectetur non eiusmod dolor irure sit.', 'temp-17624438115898823949.jpg', b'1', b'0', '2021-12-19 15:03:05', NULL, NULL, NULL);
INSERT INTO `product` VALUES (3, 'Sách Medcom', 294114, 20, 68, 116, 'Byrd Collier', 457, 'NXB Đại học Sư phạm TP.HCM', 2003, 'Cupidatat ipsum ea laborum nisi veniam nulla dolor labore excepteur ad eu.', 'temp-6352099207348952932.jpg', b'1', b'0', '2021-07-31 10:44:48', NULL, NULL, NULL);
INSERT INTO `product` VALUES (4, 'Sách Radiantix', 61888, 20, 46, 195, 'Dodson Wilkinson', 426, 'NXB Đại học Sư phạm TP.HCM', 2019, 'Qui magna ex aute deserunt aliquip mollit labore ad.', 'temp-13862094760385571107.jpg', b'0', b'0', '2021-07-26 17:44:04', NULL, NULL, NULL);
INSERT INTO `product` VALUES (5, 'Sách Prosely', 195442, 0, 52, 178, 'Horne Oneill', 270, 'NXB Giáo dục', 2000, 'Sit cillum ipsum cillum commodo dolor ipsum aliquip id exercitation non proident qui.', 'temp-17624438115898823949.jpg', b'0', b'0', '2021-03-04 01:10:28', NULL, NULL, NULL);
INSERT INTO `product` VALUES (6, 'Sách Xth', 462713, 0, 13, 212, 'Karin Jackson', 186, 'NXB Đại học Sư phạm TP.HCM', 2008, 'Ut pariatur culpa sint aliqua culpa ullamco laboris duis dolore mollit dolor cillum.', 'temp-16741118072528735594.jpg', b'1', b'0', '2021-06-18 05:55:06', NULL, NULL, NULL);
INSERT INTO `product` VALUES (7, 'Sách Liquicom', 488021, 0, 65, 95, 'Johnson Wilkins', 260, 'NXB Đại học Huế', 2003, 'Nostrud minim nostrud duis nostrud cupidatat reprehenderit nostrud dolor amet esse.', 'temp-6243426685116508297.jpg', b'1', b'0', '2021-11-01 20:47:34', NULL, NULL, NULL);
INSERT INTO `product` VALUES (8, 'Sách Supremia', 478294, 0, 60, 316, 'Ayala Rich', 359, 'NXB Đại học Sư phạm Hà Nội', 2006, 'Reprehenderit quis exercitation reprehenderit velit et magna.', 'temp-10075522682831764585.jpg', b'0', b'0', '2021-08-27 02:56:32', NULL, NULL, NULL);
INSERT INTO `product` VALUES (9, 'Sách Cyclonica', 96299, 20, 45, 14, 'Flynn Sanford', 420, 'NXB Đại học Sư phạm Hà Nội', 2013, 'Laborum elit laborum do esse ut id sunt voluptate ut minim sint mollit irure.', 'temp-16741118072528735594.jpg', b'1', b'0', '2021-08-07 21:11:03', NULL, NULL, NULL);
INSERT INTO `product` VALUES (10, 'Sách Envire', 397768, 20, 65, 347, 'Gilda Harris', 321, 'NXB Đại học Sư phạm TP.HCM', 2020, 'Non veniam dolore esse aute officia in nostrud id sint ipsum incididunt qui incididunt.', 'temp-13862094760385571107.jpg', b'1', b'0', '2021-06-07 23:23:46', NULL, NULL, NULL);
INSERT INTO `product` VALUES (11, 'Sách Insuresys', 366716, 0, 50, 305, 'Lolita Cochran', 89, 'NXB Đại học Quốc gia Hà Nội', 2007, 'Magna enim veniam consequat minim.', 'temp-10075522682831764585.jpg', b'1', b'0', '2021-12-10 13:59:07', NULL, NULL, NULL);
INSERT INTO `product` VALUES (12, 'Sách Exostream', 51700, 0, 85, 62, 'Christian Alvarado', 435, 'NXB Đại học Sư phạm Hà Nội', 2018, 'In laborum tempor cupidatat aliqua in pariatur tempor voluptate velit deserunt.', 'temp-17624438115898823949.jpg', b'0', b'0', '2021-12-14 19:28:20', NULL, NULL, NULL);
INSERT INTO `product` VALUES (13, 'Sách Fuelton', 122139, 0, 88, 461, 'Gentry Chapman', 18, 'NXB Đại học Quốc gia Hà Nội', 2005, 'Officia anim mollit culpa ea Lorem dolore commodo incididunt eu pariatur occaecat.', 'temp-16741118072528735594.jpg', b'0', b'0', '2021-06-16 08:20:52', NULL, NULL, NULL);
INSERT INTO `product` VALUES (14, 'Sách Xinware', 247115, 20, 80, 373, 'Sheree Lawson', 124, 'NXB Đại học Sư phạm TP.HCM', 2007, 'Deserunt labore fugiat velit proident proident ex irure incididunt sint anim consequat ipsum dolore dolore.', 'temp-16741118072528735594.jpg', b'1', b'0', '2021-07-31 15:57:52', NULL, NULL, NULL);
INSERT INTO `product` VALUES (15, 'Sách Atomica', 205300, 20, 16, 388, 'Shirley Sawyer', 414, 'NXB Đại học Quốc gia Hà Nội', 2006, 'Eu id sint sunt eiusmod ad magna dolore sint.', 'temp-12235989262213754276.jpg', b'0', b'0', '2021-04-19 14:39:51', NULL, NULL, NULL);
INSERT INTO `product` VALUES (16, 'Sách Quadeebo', 280225, 0, 27, 16, 'Marcia Horne', 486, 'NXB Đại học Sư phạm Hà Nội', 2006, 'Ut exercitation ipsum amet commodo labore duis est excepteur nostrud velit et ex magna.', 'temp-10075522682831764585.jpg', b'1', b'0', '2021-12-17 09:38:24', NULL, NULL, NULL);
INSERT INTO `product` VALUES (17, 'Sách Nexgene', 90044, 20, 96, 165, 'Dixie Middleton', 43, 'NXB Đại học Sư phạm TP.HCM', 2009, 'Veniam qui id occaecat exercitation aliquip occaecat incididunt quis.', 'temp-10075522682831764585.jpg', b'1', b'0', '2022-01-02 23:13:27', NULL, NULL, NULL);
INSERT INTO `product` VALUES (18, 'Sách Gaptec', 281613, 0, 93, 279, 'Dorothea Gonzales', 409, 'NXB Đại học Huế', 2020, 'Ea deserunt esse officia consequat ex nisi laborum laborum.', 'temp-13862094760385571107.jpg', b'0', b'0', '2021-07-04 12:11:08', NULL, NULL, NULL);
INSERT INTO `product` VALUES (19, 'Sách Dadabase', 450859, 20, 90, 474, 'Moore Kim', 113, 'NXB Đại học Sư phạm TP.HCM', 2003, 'Amet laborum do consequat officia cupidatat in nulla occaecat ut aute magna aute eu exercitation.', 'temp-7329036107498680084.jpg', b'0', b'0', '2021-10-25 09:48:48', NULL, NULL, NULL);
INSERT INTO `product` VALUES (20, 'Sách Digial', 187331, 20, 75, 339, 'Laverne Obrien', 194, 'NXB Đại học Huế', 2011, 'Ipsum est amet eiusmod nostrud laborum deserunt consequat qui.', 'temp-3015888053636485125.jpg', b'1', b'0', '2021-11-12 00:28:22', NULL, NULL, NULL);
INSERT INTO `product` VALUES (21, 'Sách Endicil', 396673, 20, 85, 263, 'Hughes Hutchinson', 62, 'NXB Đại học Sư phạm Hà Nội', 2013, 'Cillum non quis sit deserunt dolore laboris enim laboris fugiat reprehenderit id aliqua.', 'temp-12235989262213754276.jpg', b'0', b'0', '2021-03-26 22:11:02', NULL, NULL, NULL);
INSERT INTO `product` VALUES (22, 'Sách Inquala', 116529, 0, 1, 367, 'Hilda Crane', 238, 'NXB Đại học Quốc gia Hà Nội', 1998, 'Esse ipsum minim voluptate consectetur exercitation dolor.', 'temp-12235989262213754276.jpg', b'1', b'0', '2022-01-24 08:00:39', NULL, NULL, NULL);
INSERT INTO `product` VALUES (23, 'Sách Tubalum', 372359, 0, 71, 129, 'Erma Shannon', 15, 'NXB Đại học Sư phạm TP.HCM', 2016, 'Veniam cillum do laboris ipsum incididunt consequat non.', 'temp-6243426685116508297.jpg', b'0', b'0', '2021-07-15 20:01:58', NULL, NULL, NULL);
INSERT INTO `product` VALUES (24, 'Sách Songlines', 293705, 20, 83, 267, 'Paula Duncan', 284, 'NXB Đại học Huế', 2017, 'Nisi consequat do adipisicing nostrud elit aliqua nostrud sunt laborum reprehenderit culpa labore in ea.', 'temp-13862094760385571107.jpg', b'0', b'0', '2021-09-09 05:29:49', NULL, NULL, NULL);
INSERT INTO `product` VALUES (25, 'Sách Evidends', 122006, 0, 62, 309, 'Briggs Burke', 134, 'NXB Đại học Sư phạm TP.HCM', 2011, 'Tempor laborum duis aute enim eiusmod ipsum et labore sunt qui labore.', 'temp-13064240004351430671.jpg', b'1', b'0', '2021-12-11 02:13:04', NULL, NULL, NULL);
INSERT INTO `product` VALUES (26, 'Sách Buzzworks', 131272, 20, 47, 418, 'Wood Franks', 147, 'NXB Đại học Sư phạm Hà Nội', 1996, 'Consequat culpa ad excepteur in ut officia do.', 'temp-16741118072528735594.jpg', b'0', b'0', '2021-02-02 00:42:02', NULL, NULL, NULL);
INSERT INTO `product` VALUES (27, 'Sách Zilla', 45254, 0, 40, 41, 'Imogene Horton', 67, 'NXB Đại học Huế', 2008, 'Voluptate est reprehenderit nostrud deserunt qui ullamco tempor quis officia pariatur ipsum.', 'temp-8262627340495498759.jpg', b'0', b'0', '2021-04-26 11:20:50', NULL, NULL, NULL);
INSERT INTO `product` VALUES (28, 'Sách Fangold', 36717, 20, 19, 407, 'Frankie Mccarthy', 85, 'NXB Giáo dục', 2015, 'Incididunt elit consectetur magna nisi.', 'temp-18128511448457962576.jpg', b'0', b'0', '2021-03-08 07:28:18', NULL, NULL, NULL);
INSERT INTO `product` VALUES (29, 'Sách Immunics', 260516, 0, 87, 176, 'Wheeler Carpenter', 49, 'NXB Đại học Sư phạm TP.HCM', 2012, 'Deserunt excepteur fugiat nisi adipisicing amet esse duis pariatur dolor deserunt dolor mollit.', 'temp-14438611480196141526.jpg', b'0', b'0', '2021-10-14 22:48:40', NULL, NULL, NULL);
INSERT INTO `product` VALUES (30, 'Sách Zillacom', 318403, 0, 12, 71, 'Natalia Wilder', 271, 'NXB Đại học Sư phạm TP.HCM', 2007, 'Id sunt incididunt eiusmod dolore pariatur.', 'temp-12235989262213754276.jpg', b'0', b'0', '2021-12-19 02:05:00', NULL, NULL, NULL);
INSERT INTO `product` VALUES (31, 'Sách Katakana', 231331, 20, 71, 336, 'Linda Stevens', 285, 'NXB Đại học Sư phạm TP.HCM', 2001, 'Elit ad nulla officia fugiat nisi et ex nostrud elit ut.', 'temp-13862094760385571107.jpg', b'1', b'0', '2022-01-01 23:10:09', NULL, NULL, NULL);
INSERT INTO `product` VALUES (32, 'Sách Artiq', 313493, 0, 49, 48, 'Tucker Sargent', 341, 'NXB Đại học Sư phạm TP.HCM', 1997, 'Sit sint sit adipisicing aliqua fugiat eiusmod amet sunt cupidatat laboris cillum.', 'temp-6352099207348952932.jpg', b'0', b'0', '2021-12-08 02:56:41', NULL, NULL, NULL);
INSERT INTO `product` VALUES (33, 'Sách Omnigog', 155599, 20, 19, 316, 'Mullins Thomas', 255, 'NXB Đại học Huế', 2009, 'Cillum amet culpa Lorem elit.', 'temp-8262627340495498759.jpg', b'1', b'0', '2021-11-06 02:48:40', NULL, NULL, NULL);
INSERT INTO `product` VALUES (34, 'Sách Elita', 298548, 0, 65, 439, 'Amber Spence', 262, 'NXB Đại học Huế', 1998, 'Quis fugiat aliqua sit laborum proident ea.', 'temp-18128511448457962576.jpg', b'1', b'0', '2021-03-24 23:12:25', NULL, NULL, NULL);
INSERT INTO `product` VALUES (35, 'Sách Hopeli', 461236, 20, 51, 350, 'Wooten Johnston', 355, 'NXB Đại học Huế', 2018, 'Cupidatat fugiat do minim qui dolor deserunt anim.', 'temp-12235989262213754276.jpg', b'1', b'0', '2021-10-05 08:08:19', NULL, NULL, NULL);
INSERT INTO `product` VALUES (36, 'Sách Vendblend', 495547, 20, 48, 22, 'Mayra Moon', 364, 'NXB Đại học Sư phạm Hà Nội', 1995, 'Aute eiusmod deserunt ipsum eu.', 'temp-6352099207348952932.jpg', b'1', b'0', '2022-01-08 02:42:25', NULL, NULL, NULL);
INSERT INTO `product` VALUES (37, 'Sách Zensure', 288319, 20, 16, 440, 'Hickman Moses', 62, 'NXB Đại học Sư phạm TP.HCM', 2007, 'Minim cillum pariatur ea voluptate laborum dolore duis.', 'temp-8476700387786158058.jpg', b'0', b'0', '2021-05-11 01:50:10', NULL, NULL, NULL);
INSERT INTO `product` VALUES (38, 'Sách Quilk', 183383, 20, 81, 14, 'Elnora Pearson', 326, 'NXB Đại học Huế', 2013, 'Laborum sunt laborum reprehenderit cupidatat esse.', 'temp-10075522682831764585.jpg', b'1', b'0', '2021-11-05 20:54:31', NULL, NULL, NULL);
INSERT INTO `product` VALUES (39, 'Sách Schoolio', 176598, 0, 48, 208, 'Isabella Mcbride', 401, 'NXB Đại học Huế', 2013, 'Consequat amet deserunt qui adipisicing amet id laboris magna reprehenderit ea nisi amet magna.', 'temp-10075522682831764585.jpg', b'1', b'0', '2021-12-27 15:36:00', NULL, NULL, NULL);
INSERT INTO `product` VALUES (40, 'Sách Neocent', 228132, 0, 91, 66, 'Tanya Shaw', 57, 'NXB Đại học Quốc gia Hà Nội', 2000, 'Ea adipisicing ullamco cupidatat do elit nulla officia.', 'temp-7329036107498680084.jpg', b'1', b'0', '2021-06-13 14:22:51', NULL, NULL, NULL);
INSERT INTO `product` VALUES (41, 'Sách Isotrack', 387298, 20, 78, 202, 'Noemi Norris', 179, 'NXB Đại học Quốc gia Hà Nội', 2013, 'Eiusmod in dolor excepteur culpa ipsum eu sint culpa exercitation est qui.', 'temp-7329036107498680084.jpg', b'0', b'0', '2022-01-03 15:33:27', NULL, NULL, NULL);
INSERT INTO `product` VALUES (42, 'Sách Bedlam', 274131, 20, 28, 391, 'Josefa Allison', 31, 'NXB Đại học Huế', 2000, 'Culpa velit pariatur proident exercitation commodo labore exercitation anim dolore qui fugiat.', 'temp-14438611480196141526.jpg', b'1', b'0', '2021-07-21 01:40:24', NULL, NULL, NULL);
INSERT INTO `product` VALUES (43, 'Sách Eternis', 68882, 20, 45, 380, 'Betty Marsh', 63, 'NXB Đại học Sư phạm TP.HCM', 2020, 'Non sint laboris anim elit ipsum.', 'temp-16741118072528735594.jpg', b'0', b'0', '2021-06-09 21:58:26', NULL, NULL, NULL);
INSERT INTO `product` VALUES (44, 'Sách Manufact', 71997, 20, 36, 343, 'Whitney Underwood', 293, 'NXB Đại học Sư phạm Hà Nội', 2013, 'Excepteur nisi minim in eu ad enim duis cillum sit tempor qui irure aliqua.', 'temp-3015888053636485125.jpg', b'1', b'0', '2021-03-30 01:29:57', NULL, NULL, NULL);
INSERT INTO `product` VALUES (45, 'Sách Golistic', 252810, 0, 99, 358, 'Valencia Kent', 166, 'NXB Giáo dục', 2006, 'Laboris est ea incididunt cupidatat.', 'temp-13862094760385571107.jpg', b'0', b'0', '2021-06-07 07:01:21', NULL, NULL, NULL);
INSERT INTO `product` VALUES (46, 'Sách Dognosis', 384754, 20, 57, 484, 'Alisa Waters', 129, 'NXB Đại học Sư phạm TP.HCM', 1997, 'Do qui cillum sint in aute tempor.', 'temp-8262627340495498759.jpg', b'0', b'0', '2022-01-13 20:55:09', NULL, NULL, NULL);
INSERT INTO `product` VALUES (47, 'Sách Magneato', 62859, 20, 76, 213, 'Bell Anthony', 439, 'NXB Đại học Sư phạm TP.HCM', 2002, 'Commodo nisi sint nulla et duis nostrud sunt nulla.', 'temp-7329036107498680084.jpg', b'0', b'0', '2021-09-01 20:00:43', NULL, NULL, NULL);
INSERT INTO `product` VALUES (48, 'Sách Comdom', 320796, 0, 41, 221, 'Bowen Blackburn', 127, 'NXB Giáo dục', 1999, 'Adipisicing laborum fugiat quis et.', 'temp-3984373128647845854.jpg', b'1', b'0', '2021-02-18 02:21:04', NULL, NULL, NULL);
INSERT INTO `product` VALUES (49, 'Sách Cognicode', 397947, 0, 75, 225, 'Strickland Flores', 166, 'NXB Đại học Huế', 2016, 'Dolor exercitation incididunt ea voluptate Lorem irure proident quis ullamco occaecat adipisicing.', 'temp-13064240004351430671.jpg', b'0', b'0', '2021-07-30 09:38:48', NULL, NULL, NULL);
INSERT INTO `product` VALUES (50, 'Sách Lyrichord', 118039, 0, 34, 392, 'Mcconnell Case', 284, 'NXB Đại học Sư phạm Hà Nội', 1996, 'Ea nostrud enim est eu pariatur velit laborum commodo.', 'temp-16741118072528735594.jpg', b'0', b'0', '2021-05-15 04:03:30', NULL, NULL, NULL);
INSERT INTO `product` VALUES (51, 'Sách Comtext', 134415, 20, 72, 480, 'Kristine Weiss', 95, 'NXB Giáo dục', 2021, 'Deserunt exercitation in laborum velit non commodo adipisicing excepteur culpa.', 'temp-13862094760385571107.jpg', b'1', b'0', '2021-09-13 19:22:19', NULL, NULL, NULL);
INSERT INTO `product` VALUES (52, 'Sách Myopium', 349335, 20, 57, 482, 'Mooney Freeman', 71, 'NXB Giáo dục', 2008, 'Eu aliqua exercitation laborum irure ea id officia.', 'temp-7329036107498680084.jpg', b'1', b'0', '2021-07-08 12:49:12', NULL, NULL, NULL);
INSERT INTO `product` VALUES (53, 'Sách Bittor', 388247, 20, 76, 270, 'Foley Payne', 486, 'NXB Giáo dục', 2016, 'Quis fugiat eiusmod deserunt duis eu qui aliqua magna Lorem magna et culpa.', 'temp-18128511448457962576.jpg', b'0', b'0', '2021-12-14 03:59:32', NULL, NULL, NULL);
INSERT INTO `product` VALUES (54, 'Sách Chillium', 144536, 20, 81, 417, 'Wynn Poole', 73, 'NXB Đại học Quốc gia Hà Nội', 1998, 'Eiusmod pariatur aliquip aliqua duis Lorem nisi duis ullamco.', 'temp-12235989262213754276.jpg', b'0', b'0', '2021-08-14 15:42:55', NULL, NULL, NULL);
INSERT INTO `product` VALUES (55, 'Sách Ronelon', 201895, 20, 32, 451, 'Frederick Gilliam', 51, 'NXB Đại học Quốc gia Hà Nội', 2020, 'Ex commodo consectetur proident elit tempor in minim nostrud elit nostrud.', 'temp-16741118072528735594.jpg', b'0', b'0', '2021-06-09 02:42:51', NULL, NULL, NULL);
INSERT INTO `product` VALUES (56, 'Sách Roughies', 124514, 0, 31, 196, 'Pat Atkinson', 50, 'NXB Giáo dục', 2004, 'Aute aliqua dolore qui quis veniam pariatur.', 'temp-12235989262213754276.jpg', b'1', b'0', '2021-05-12 13:14:00', NULL, NULL, NULL);
INSERT INTO `product` VALUES (57, 'Sách Printspan', 153427, 0, 32, 234, 'Bartlett Hampton', 122, 'NXB Đại học Huế', 2013, 'Aliqua reprehenderit consectetur incididunt elit ullamco veniam tempor tempor incididunt labore elit.', 'temp-18128511448457962576.jpg', b'0', b'0', '2021-12-28 05:25:08', NULL, NULL, NULL);
INSERT INTO `product` VALUES (58, 'Sách Quantalia', 101367, 20, 90, 305, 'Cherry Hopper', 409, 'NXB Giáo dục', 2019, 'Ex sunt elit minim commodo minim reprehenderit consectetur ea dolor.', 'temp-14438611480196141526.jpg', b'0', b'0', '2021-06-24 07:23:29', NULL, NULL, NULL);
INSERT INTO `product` VALUES (59, 'Sách Fortean', 243231, 0, 94, 444, 'Tabitha Shepherd', 357, 'NXB Đại học Quốc gia Hà Nội', 2008, 'Amet et elit labore fugiat magna ullamco proident ut excepteur ea elit commodo.', 'temp-16741118072528735594.jpg', b'0', b'0', '2021-09-21 11:22:31', NULL, NULL, NULL);
INSERT INTO `product` VALUES (60, 'Sách Kengen', 262167, 20, 15, 473, 'Cooke Barber', 79, 'NXB Đại học Quốc gia Hà Nội', 1997, 'Proident sint quis culpa do id eiusmod mollit in culpa ut anim.', 'temp-3984373128647845854.jpg', b'0', b'0', '2021-11-22 16:02:54', NULL, NULL, NULL);
INSERT INTO `product` VALUES (61, 'Sách Dyno', 422493, 20, 50, 32, 'Laura Simpson', 354, 'NXB Đại học Quốc gia Hà Nội', 1997, 'Proident pariatur ullamco elit aliquip mollit magna sunt ad eiusmod.', 'temp-13064240004351430671.jpg', b'1', b'0', '2021-09-16 06:34:31', NULL, NULL, NULL);
INSERT INTO `product` VALUES (62, 'Sách Geekmosis', 396421, 0, 96, 210, 'Gayle Castillo', 354, 'NXB Đại học Huế', 2006, 'Anim proident et ex ut irure cillum magna non incididunt ipsum dolor.', 'temp-10075522682831764585.jpg', b'1', b'0', '2021-02-20 05:53:34', NULL, NULL, NULL);
INSERT INTO `product` VALUES (63, 'Sách Earbang', 162841, 20, 21, 47, 'Elliott Pace', 150, 'NXB Đại học Sư phạm TP.HCM', 2017, 'Amet veniam voluptate consequat adipisicing officia et fugiat cillum ut ullamco elit ea do sit.', 'temp-8262627340495498759.jpg', b'1', b'0', '2021-09-02 07:50:35', NULL, NULL, NULL);
INSERT INTO `product` VALUES (64, 'Sách Rodeology', 473815, 0, 10, 370, 'Chelsea Wong', 396, 'NXB Giáo dục', 2009, 'Ipsum magna laborum do officia non quis duis sunt est ipsum.', 'temp-14438611480196141526.jpg', b'0', b'0', '2021-07-18 03:36:32', NULL, NULL, NULL);
INSERT INTO `product` VALUES (65, 'Sách Locazone', 466018, 0, 58, 196, 'Katina Terrell', 294, 'NXB Đại học Quốc gia Hà Nội', 1996, 'Deserunt cillum culpa in anim voluptate quis quis laborum in non.', 'temp-6352099207348952932.jpg', b'1', b'0', '2021-12-08 23:10:27', NULL, NULL, NULL);
INSERT INTO `product` VALUES (66, 'Sách Zentime', 8106, 20, 25, 162, 'Alfreda Randolph', 161, 'NXB Đại học Sư phạm Hà Nội', 2021, 'Quis aliquip minim irure nisi.', 'temp-13862094760385571107.jpg', b'0', b'0', '2021-12-13 11:25:39', NULL, NULL, NULL);
INSERT INTO `product` VALUES (67, 'Sách Xelegyl', 117223, 0, 40, 270, 'Helene Campbell', 340, 'NXB Đại học Huế', 2013, 'Veniam et et fugiat cillum veniam consectetur dolor laborum ipsum aliquip in commodo.', 'temp-8476700387786158058.jpg', b'0', b'0', '2021-09-11 01:01:32', NULL, NULL, NULL);
INSERT INTO `product` VALUES (68, 'Sách Oatfarm', 299156, 0, 43, 90, 'Joyner Jarvis', 10, 'NXB Đại học Huế', 2017, 'Dolore aliquip ullamco id ut cupidatat exercitation nulla non veniam adipisicing do elit id.', 'temp-3984373128647845854.jpg', b'0', b'0', '2021-06-18 17:25:37', NULL, NULL, NULL);
INSERT INTO `product` VALUES (69, 'Sách Dymi', 382118, 20, 50, 381, 'Deirdre Hatfield', 442, 'NXB Giáo dục', 2020, 'Eiusmod anim exercitation aliquip et cupidatat id consectetur exercitation nostrud enim irure mollit non.', 'temp-13064240004351430671.jpg', b'0', b'0', '2021-02-12 15:13:38', NULL, NULL, NULL);
INSERT INTO `product` VALUES (70, 'Sách Injoy', 444695, 20, 71, 176, 'Patty Caldwell', 79, 'NXB Đại học Quốc gia Hà Nội', 2001, 'Quis aute occaecat fugiat mollit eu est sunt eu ipsum do excepteur culpa.', 'temp-13862094760385571107.jpg', b'0', b'0', '2021-11-16 22:29:49', NULL, NULL, NULL);
INSERT INTO `product` VALUES (71, 'Sách Globoil', 375062, 0, 25, 59, 'Steele Henson', 362, 'NXB Đại học Sư phạm Hà Nội', 2016, 'Eiusmod do laboris magna ex eiusmod laboris nostrud aliqua anim.', 'temp-16741118072528735594.jpg', b'1', b'0', '2021-12-22 12:50:21', NULL, NULL, NULL);
INSERT INTO `product` VALUES (72, 'Sách Daido', 170140, 20, 47, 398, 'Dotson Lloyd', 136, 'NXB Đại học Quốc gia Hà Nội', 2012, 'Dolore ipsum occaecat in magna elit Lorem est.', 'temp-16741118072528735594.jpg', b'0', b'0', '2021-06-25 04:03:26', NULL, NULL, NULL);
INSERT INTO `product` VALUES (73, 'Sách Coash', 443683, 20, 59, 367, 'Lillie Hurst', 67, 'NXB Đại học Sư phạm TP.HCM', 2008, 'Veniam duis cupidatat adipisicing sunt sit.', 'temp-3984373128647845854.jpg', b'0', b'0', '2021-12-13 00:50:17', NULL, NULL, NULL);
INSERT INTO `product` VALUES (74, 'Sách Automon', 109059, 0, 70, 60, 'Melissa Hayes', 301, 'NXB Đại học Sư phạm Hà Nội', 2009, 'Dolor ex commodo Lorem fugiat.', 'temp-10075522682831764585.jpg', b'0', b'0', '2021-05-22 05:34:50', NULL, NULL, NULL);
INSERT INTO `product` VALUES (75, 'Sách Genmom', 118544, 0, 79, 119, 'Delores Johnson', 418, 'NXB Giáo dục', 2007, 'Commodo eiusmod est eu eu.', 'temp-18128511448457962576.jpg', b'0', b'0', '2021-02-10 13:36:37', NULL, NULL, NULL);
INSERT INTO `product` VALUES (76, 'Sách Idetica', 27956, 0, 69, 50, 'Veronica Coffey', 104, 'NXB Giáo dục', 2014, 'Aliquip enim commodo dolore nulla dolore mollit exercitation fugiat eu ex commodo.', 'temp-6352099207348952932.jpg', b'0', b'0', '2021-02-13 00:56:55', NULL, NULL, NULL);
INSERT INTO `product` VALUES (77, 'Sách Enerforce', 364658, 0, 89, 207, 'Spencer Marshall', 475, 'NXB Đại học Sư phạm TP.HCM', 1999, 'Id laborum aute duis id excepteur exercitation nostrud ad qui sit.', 'temp-7329036107498680084.jpg', b'0', b'0', '2022-01-04 19:49:25', NULL, NULL, NULL);
INSERT INTO `product` VALUES (78, 'Sách Kenegy', 104405, 0, 91, 406, 'Carrie Boone', 150, 'NXB Đại học Sư phạm TP.HCM', 2011, 'Pariatur ex labore deserunt non deserunt aliqua non reprehenderit elit fugiat elit officia reprehenderit laboris.', 'temp-7329036107498680084.jpg', b'1', b'0', '2022-01-04 10:35:13', NULL, NULL, NULL);
INSERT INTO `product` VALUES (79, 'Sách Krag', 293321, 20, 84, 31, 'Isabelle Justice', 346, 'NXB Đại học Sư phạm TP.HCM', 2008, 'Aute irure consectetur sunt do incididunt.', 'temp-3015888053636485125.jpg', b'0', b'0', '2021-10-25 15:41:19', NULL, NULL, NULL);
INSERT INTO `product` VALUES (80, 'Sách Sealoud', 380540, 0, 73, 268, 'Adele Martinez', 499, 'NXB Đại học Huế', 2019, 'Ut esse aliquip proident excepteur et fugiat ullamco fugiat dolor et velit fugiat sit.', 'temp-7329036107498680084.jpg', b'0', b'0', '2021-08-01 19:32:05', NULL, NULL, NULL);
INSERT INTO `product` VALUES (81, 'Sách Isologics', 268413, 20, 32, 384, 'Jewell Whitfield', 381, 'NXB Đại học Sư phạm Hà Nội', 2010, 'Ut sit fugiat reprehenderit ipsum aliqua incididunt consequat reprehenderit.', 'temp-16741118072528735594.jpg', b'0', b'0', '2021-11-28 16:51:08', NULL, NULL, NULL);
INSERT INTO `product` VALUES (82, 'Sách Exiand', 187140, 20, 54, 320, 'Giles Mcdowell', 280, 'NXB Giáo dục', 2020, 'Consequat reprehenderit reprehenderit aliquip amet aliqua fugiat duis eiusmod est in in nostrud.', 'temp-13862094760385571107.jpg', b'0', b'0', '2021-11-14 02:53:14', NULL, NULL, NULL);
INSERT INTO `product` VALUES (83, 'Sách Terragen', 103129, 0, 51, 388, 'Jackson Fernandez', 264, 'NXB Đại học Quốc gia Hà Nội', 2009, 'Irure deserunt duis ut commodo consequat esse officia.', 'temp-16741118072528735594.jpg', b'0', b'0', '2021-10-29 15:47:05', NULL, NULL, NULL);
INSERT INTO `product` VALUES (84, 'Sách Accidency', 44192, 20, 69, 128, 'Blackburn West', 357, 'NXB Đại học Huế', 2020, 'Nisi esse excepteur sit id adipisicing do voluptate cupidatat voluptate in.', 'temp-13862094760385571107.jpg', b'1', b'0', '2021-02-07 02:11:02', NULL, NULL, NULL);
INSERT INTO `product` VALUES (85, 'Sách Hairport', 367922, 20, 96, 422, 'Harris Zamora', 328, 'NXB Đại học Sư phạm Hà Nội', 2003, 'Est enim veniam proident ad excepteur cillum ex irure magna duis enim exercitation aliquip fugiat.', 'temp-13862094760385571107.jpg', b'0', b'0', '2021-10-24 09:47:35', NULL, NULL, NULL);
INSERT INTO `product` VALUES (86, 'Sách Shepard', 215739, 20, 85, 134, 'Elsie Peters', 198, 'NXB Đại học Huế', 2007, 'Voluptate duis mollit aliquip culpa sint pariatur cillum Lorem.', 'temp-3015888053636485125.jpg', b'1', b'0', '2021-04-21 09:30:10', NULL, NULL, NULL);
INSERT INTO `product` VALUES (87, 'Sách Pigzart', 91054, 20, 16, 420, 'Roach Nielsen', 279, 'NXB Đại học Sư phạm Hà Nội', 2005, 'Excepteur aute Lorem proident excepteur magna sit.', 'temp-13064240004351430671.jpg', b'1', b'0', '2021-03-31 07:39:02', NULL, NULL, NULL);
INSERT INTO `product` VALUES (88, 'Sách Orbaxter', 270543, 0, 54, 140, 'Diaz Howard', 156, 'NXB Đại học Huế', 2000, 'Do aute occaecat qui velit pariatur sit aute Lorem ea fugiat enim ullamco sunt cillum.', 'temp-8476700387786158058.jpg', b'1', b'0', '2021-06-15 14:51:43', NULL, NULL, NULL);
INSERT INTO `product` VALUES (89, 'Sách Cowtown', 29992, 20, 49, 318, 'Mitzi Koch', 191, 'NXB Đại học Sư phạm Hà Nội', 1998, 'Qui elit irure sunt esse voluptate ex laborum.', 'temp-6243426685116508297.jpg', b'0', b'0', '2021-12-21 11:16:02', NULL, NULL, NULL);
INSERT INTO `product` VALUES (90, 'Sách Makingway', 205442, 20, 85, 380, 'Cynthia Rasmussen', 346, 'NXB Đại học Quốc gia Hà Nội', 2015, 'Nisi qui amet ipsum ea nulla dolor nulla.', 'temp-7329036107498680084.jpg', b'1', b'0', '2021-09-02 03:01:38', NULL, NULL, NULL);
INSERT INTO `product` VALUES (91, 'Sách Lunchpad', 129694, 20, 26, 133, 'Bryant Ball', 318, 'NXB Đại học Sư phạm TP.HCM', 2009, 'Quis mollit ipsum ad laboris velit sit est anim ullamco sunt esse.', 'temp-6352099207348952932.jpg', b'1', b'0', '2021-03-25 11:05:04', NULL, NULL, NULL);
INSERT INTO `product` VALUES (92, 'Sách Mediot', 216835, 20, 78, 114, 'Rosemary Sampson', 321, 'NXB Đại học Quốc gia Hà Nội', 1998, 'Ut deserunt esse excepteur aliqua commodo Lorem ad et.', 'temp-6352099207348952932.jpg', b'0', b'0', '2021-05-17 13:49:26', NULL, NULL, NULL);
INSERT INTO `product` VALUES (93, 'Sách Plasto', 215800, 20, 55, 85, 'Santiago Levine', 267, 'NXB Đại học Quốc gia Hà Nội', 2001, 'Amet aliquip sunt in commodo excepteur esse ea aliqua laboris in.', 'temp-13064240004351430671.jpg', b'1', b'0', '2022-01-27 21:49:53', NULL, NULL, NULL);
INSERT INTO `product` VALUES (94, 'Sách Geekola', 332072, 20, 42, 165, 'Austin Cain', 274, 'NXB Đại học Sư phạm TP.HCM', 2010, 'Duis veniam ad nisi nostrud aliquip ex aliquip laboris ipsum eu velit dolor dolor in.', 'temp-14438611480196141526.jpg', b'0', b'0', '2021-04-28 06:56:06', NULL, NULL, NULL);
INSERT INTO `product` VALUES (95, 'Sách Gracker', 268831, 0, 49, 248, 'Vasquez Gallegos', 230, 'NXB Giáo dục', 2006, 'Consequat sunt non aute irure voluptate reprehenderit enim consectetur sit sint sit.', 'temp-12235989262213754276.jpg', b'1', b'0', '2021-10-25 05:55:27', NULL, NULL, NULL);
INSERT INTO `product` VALUES (96, 'Sách Housedown', 307111, 0, 83, 233, 'Haynes Riggs', 390, 'NXB Đại học Huế', 2020, 'Quis magna tempor laboris adipisicing.', 'temp-8476700387786158058.jpg', b'1', b'0', '2021-04-17 06:11:57', NULL, NULL, NULL);
INSERT INTO `product` VALUES (97, 'Sách Zillan', 108354, 0, 81, 307, 'Hood Nieves', 192, 'NXB Đại học Sư phạm TP.HCM', 2012, 'Incididunt labore minim enim qui minim cillum est voluptate veniam eu.', 'temp-8262627340495498759.jpg', b'1', b'0', '2021-07-03 05:51:31', NULL, NULL, NULL);
INSERT INTO `product` VALUES (98, 'Sách Lovepad', 477477, 20, 18, 120, 'Foster Sims', 91, 'NXB Đại học Sư phạm Hà Nội', 2015, 'Anim do laboris duis aute laboris aliquip amet do nulla aliqua.', 'temp-3984373128647845854.jpg', b'1', b'0', '2021-07-09 19:58:01', NULL, NULL, NULL);
INSERT INTO `product` VALUES (99, 'Sách Ontagene', 199763, 20, 46, 86, 'Weeks Charles', 71, 'NXB Đại học Huế', 2009, 'Do in sunt sunt in tempor tempor proident dolor officia irure consequat cupidatat incididunt nulla.', 'temp-8476700387786158058.jpg', b'1', b'0', '2022-01-22 15:29:03', NULL, NULL, NULL);
INSERT INTO `product` VALUES (100, 'Sách Spacewax', 171403, 20, 34, 198, 'Sadie Logan', 85, 'NXB Đại học Huế', 2014, 'Esse non qui dolor consectetur magna consectetur excepteur exercitation nisi eiusmod laboris nulla laborum.', 'temp-12235989262213754276.jpg', b'1', b'0', '2021-08-14 19:14:31', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for product_category
-- ----------------------------
DROP TABLE IF EXISTS `product_category`;
CREATE TABLE `product_category`  (
                                     `productId` bigint NOT NULL,
                                     `categoryId` bigint NOT NULL,
                                     PRIMARY KEY (`productId`, `categoryId`) USING BTREE,
                                     INDEX `idx_product_category_product`(`productId`) USING BTREE,
                                     INDEX `idx_product_category_category`(`categoryId`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of product_category
-- ----------------------------
INSERT INTO `product_category` VALUES (1, 2);
INSERT INTO `product_category` VALUES (2, 14);
INSERT INTO `product_category` VALUES (3, 5);
INSERT INTO `product_category` VALUES (4, 5);
INSERT INTO `product_category` VALUES (5, 5);
INSERT INTO `product_category` VALUES (6, 3);
INSERT INTO `product_category` VALUES (7, 7);
INSERT INTO `product_category` VALUES (8, 6);
INSERT INTO `product_category` VALUES (9, 7);
INSERT INTO `product_category` VALUES (10, 11);
INSERT INTO `product_category` VALUES (11, 13);
INSERT INTO `product_category` VALUES (12, 6);
INSERT INTO `product_category` VALUES (13, 9);
INSERT INTO `product_category` VALUES (14, 15);
INSERT INTO `product_category` VALUES (15, 14);
INSERT INTO `product_category` VALUES (16, 15);
INSERT INTO `product_category` VALUES (17, 9);
INSERT INTO `product_category` VALUES (18, 2);
INSERT INTO `product_category` VALUES (19, 3);
INSERT INTO `product_category` VALUES (20, 10);
INSERT INTO `product_category` VALUES (21, 13);
INSERT INTO `product_category` VALUES (22, 15);
INSERT INTO `product_category` VALUES (23, 8);
INSERT INTO `product_category` VALUES (24, 14);
INSERT INTO `product_category` VALUES (25, 7);
INSERT INTO `product_category` VALUES (26, 1);
INSERT INTO `product_category` VALUES (27, 7);
INSERT INTO `product_category` VALUES (28, 14);
INSERT INTO `product_category` VALUES (29, 9);
INSERT INTO `product_category` VALUES (30, 2);
INSERT INTO `product_category` VALUES (31, 8);
INSERT INTO `product_category` VALUES (32, 1);
INSERT INTO `product_category` VALUES (33, 10);
INSERT INTO `product_category` VALUES (34, 7);
INSERT INTO `product_category` VALUES (35, 2);
INSERT INTO `product_category` VALUES (36, 7);
INSERT INTO `product_category` VALUES (37, 12);
INSERT INTO `product_category` VALUES (38, 1);
INSERT INTO `product_category` VALUES (39, 12);
INSERT INTO `product_category` VALUES (40, 1);
INSERT INTO `product_category` VALUES (41, 9);
INSERT INTO `product_category` VALUES (42, 4);
INSERT INTO `product_category` VALUES (43, 5);
INSERT INTO `product_category` VALUES (44, 5);
INSERT INTO `product_category` VALUES (45, 3);
INSERT INTO `product_category` VALUES (46, 12);
INSERT INTO `product_category` VALUES (47, 3);
INSERT INTO `product_category` VALUES (48, 7);
INSERT INTO `product_category` VALUES (49, 6);
INSERT INTO `product_category` VALUES (50, 6);
INSERT INTO `product_category` VALUES (51, 11);
INSERT INTO `product_category` VALUES (52, 5);
INSERT INTO `product_category` VALUES (53, 9);
INSERT INTO `product_category` VALUES (54, 4);
INSERT INTO `product_category` VALUES (55, 1);
INSERT INTO `product_category` VALUES (56, 10);
INSERT INTO `product_category` VALUES (57, 10);
INSERT INTO `product_category` VALUES (58, 3);
INSERT INTO `product_category` VALUES (59, 8);
INSERT INTO `product_category` VALUES (60, 9);
INSERT INTO `product_category` VALUES (61, 12);
INSERT INTO `product_category` VALUES (62, 10);
INSERT INTO `product_category` VALUES (63, 14);
INSERT INTO `product_category` VALUES (64, 8);
INSERT INTO `product_category` VALUES (65, 6);
INSERT INTO `product_category` VALUES (66, 11);
INSERT INTO `product_category` VALUES (67, 14);
INSERT INTO `product_category` VALUES (68, 5);
INSERT INTO `product_category` VALUES (69, 13);
INSERT INTO `product_category` VALUES (70, 8);
INSERT INTO `product_category` VALUES (71, 13);
INSERT INTO `product_category` VALUES (72, 14);
INSERT INTO `product_category` VALUES (73, 2);
INSERT INTO `product_category` VALUES (74, 1);
INSERT INTO `product_category` VALUES (75, 13);
INSERT INTO `product_category` VALUES (76, 2);
INSERT INTO `product_category` VALUES (77, 2);
INSERT INTO `product_category` VALUES (78, 8);
INSERT INTO `product_category` VALUES (79, 3);
INSERT INTO `product_category` VALUES (80, 8);
INSERT INTO `product_category` VALUES (81, 14);
INSERT INTO `product_category` VALUES (82, 2);
INSERT INTO `product_category` VALUES (83, 1);
INSERT INTO `product_category` VALUES (84, 9);
INSERT INTO `product_category` VALUES (85, 13);
INSERT INTO `product_category` VALUES (86, 14);
INSERT INTO `product_category` VALUES (87, 5);
INSERT INTO `product_category` VALUES (88, 13);
INSERT INTO `product_category` VALUES (89, 1);
INSERT INTO `product_category` VALUES (90, 10);
INSERT INTO `product_category` VALUES (91, 4);
INSERT INTO `product_category` VALUES (92, 2);
INSERT INTO `product_category` VALUES (93, 4);
INSERT INTO `product_category` VALUES (94, 15);
INSERT INTO `product_category` VALUES (95, 11);
INSERT INTO `product_category` VALUES (96, 8);
INSERT INTO `product_category` VALUES (97, 11);
INSERT INTO `product_category` VALUES (98, 10);
INSERT INTO `product_category` VALUES (99, 15);
INSERT INTO `product_category` VALUES (100, 8);

-- ----------------------------
-- Table structure for product_review
-- ----------------------------
DROP TABLE IF EXISTS `product_review`;
CREATE TABLE `product_review`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `userId` bigint NULL DEFAULT NULL,
                                   `productId` bigint NOT NULL,
                                   `ratingScore` tinyint NOT NULL,
                                   `content` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                   `isShow` bit(1) NOT NULL,
                                   `createdAt` datetime NOT NULL,
                                   `updatedAt` datetime NULL DEFAULT NULL,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   INDEX `idx_product_review_user`(`userId`) USING BTREE,
                                   INDEX `idx_product_review_product`(`productId`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of product_review
-- ----------------------------

-- ----------------------------
-- Table structure for provinces
-- ----------------------------
DROP TABLE IF EXISTS `provinces`;
CREATE TABLE `provinces`  (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `province_code` varchar(10) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                              `province_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                              `province_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'tinh',
                              `region` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'MienBac' COMMENT 'MienBac, MienTrung, MienNam',
                              `is_metro_city` tinyint(1) NULL DEFAULT 0,
                              `shipping_zone_id` bigint NULL DEFAULT NULL,
                              `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                              `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              PRIMARY KEY (`id`) USING BTREE,
                              UNIQUE INDEX `province_code`(`province_code`) USING BTREE,
                              INDEX `idx_province_code`(`province_code`) USING BTREE,
                              INDEX `idx_province_region`(`region`) USING BTREE,
                              INDEX `idx_province_zone`(`shipping_zone_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 62 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of provinces
-- ----------------------------
INSERT INTO `provinces` VALUES (1, '01', 'Thành phố Hà Nội', 'thành-phố', 'MienBac', 1, 2, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (2, '79', 'Thành phố Hồ Chí Minh', 'thành-phố', 'MienNam', 1, 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (3, '48', 'Thành phố Đà Nẵng', 'thành-phố', 'MienTrung', 1, 3, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (4, '02', 'Tỉnh Hà Giang', 'tỉnh', 'MienBac', 0, 8, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (5, '04', 'Tỉnh Cao Bằng', 'tỉnh', 'MienBac', 0, 8, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (6, '06', 'Tỉnh Bắc Kạn', 'tỉnh', 'MienBac', 0, 8, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (7, '08', 'Tỉnh Tuyên Quang', 'tỉnh', 'MienBac', 0, 8, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (8, '10', 'Tỉnh Lào Cai', 'tỉnh', 'MienBac', 0, 8, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (9, '11', 'Tỉnh Điện Biên', 'tỉnh', 'MienBac', 0, 8, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (10, '12', 'Tỉnh Lai Châu', 'tỉnh', 'MienBac', 0, 8, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (11, '14', 'Tỉnh Sơn La', 'tỉnh', 'MienBac', 0, 8, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (12, '15', 'Tỉnh Yên Bái', 'tỉnh', 'MienBac', 0, 8, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (13, '17', 'Tỉnh Hòa Bình', 'tỉnh', 'MienBac', 0, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (14, '19', 'Tỉnh Thái Nguyên', 'tỉnh', 'MienBac', 0, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (15, '20', 'Tỉnh Lạng Sơn', 'tỉnh', 'MienBac', 0, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (16, '22', 'Tỉnh Quảng Ninh', 'tỉnh', 'MienBac', 0, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (17, '24', 'Tỉnh Bắc Giang', 'tỉnh', 'MienBac', 0, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (18, '25', 'Tỉnh Phú Thọ', 'tỉnh', 'MienBac', 0, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (19, '26', 'Tỉnh Vĩnh Phúc', 'tỉnh', 'MienBac', 0, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (20, '27', 'Tỉnh Bắc Ninh', 'tỉnh', 'MienBac', 0, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (21, '30', 'Tỉnh Hải Dương', 'tỉnh', 'MienBac', 0, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (22, '31', 'Tỉnh Hưng Yên', 'tỉnh', 'MienBac', 0, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (23, '33', 'Tỉnh Thái Bình', 'tỉnh', 'MienBac', 0, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (24, '34', 'Tỉnh Nam Định', 'tỉnh', 'MienBac', 0, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (25, '35', 'Tỉnh Ninh Bình', 'tỉnh', 'MienBac', 0, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (26, '36', 'Tỉnh Thanh Hóa', 'tỉnh', 'MienTrung', 0, 5, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (27, '38', 'Tỉnh Nghệ An', 'tỉnh', 'MienTrung', 0, 5, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (28, '40', 'Tỉnh Hà Tĩnh', 'tỉnh', 'MienTrung', 0, 5, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (29, '42', 'Tỉnh Quảng Bình', 'tỉnh', 'MienTrung', 0, 5, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (30, '44', 'Tỉnh Quảng Trị', 'tỉnh', 'MienTrung', 0, 5, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (31, '45', 'Tỉnh Thừa Thiên Huế', 'tỉnh', 'MienTrung', 0, 5, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (32, '49', 'Tỉnh Quảng Nam', 'tỉnh', 'MienTrung', 0, 6, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (33, '51', 'Tỉnh Quảng Ngãi', 'tỉnh', 'MienTrung', 0, 6, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (34, '52', 'Tỉnh Bình Định', 'tỉnh', 'MienTrung', 0, 6, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (35, '54', 'Tỉnh Phú Yên', 'tỉnh', 'MienTrung', 0, 6, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (36, '56', 'Tỉnh Khánh Hòa', 'tỉnh', 'MienTrung', 0, 6, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (37, '58', 'Tỉnh Ninh Thuận', 'tỉnh', 'MienTrung', 0, 6, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (38, '60', 'Tỉnh Bình Thuận', 'tỉnh', 'MienTrung', 0, 6, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (39, '62', 'Tỉnh Kon Tum', 'tỉnh', 'MienTrung', 0, 9, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (40, '64', 'Tỉnh Gia Lai', 'tỉnh', 'MienTrung', 0, 9, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (41, '66', 'Tỉnh Đắk Lắk', 'tỉnh', 'MienTrung', 0, 9, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (42, '67', 'Tỉnh Đắk Nông', 'tỉnh', 'MienTrung', 0, 9, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (43, '68', 'Tỉnh Lâm Đồng', 'tỉnh', 'MienTrung', 0, 9, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (44, '70', 'Tỉnh Bình Phước', 'tỉnh', 'MienNam', 0, 9, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (45, '72', 'Tỉnh Tây Ninh', 'tỉnh', 'MienNam', 0, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (46, '75', 'Tỉnh Bình Dương', 'tỉnh', 'MienNam', 0, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (47, '77', 'Tỉnh Đồng Nai', 'tỉnh', 'MienNam', 0, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (48, '78', 'Tỉnh Bà Rịa - Vũng Tàu', 'tỉnh', 'MienNam', 0, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (49, '80', 'Tỉnh Long An', 'tỉnh', 'MienNam', 0, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (50, '82', 'Tỉnh Tiền Giang', 'tỉnh', 'MienNam', 0, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (51, '83', 'Tỉnh Bến Tre', 'tỉnh', 'MienNam', 0, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (52, '84', 'Tỉnh Trà Vinh', 'tỉnh', 'MienNam', 0, 10, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (53, '87', 'Tỉnh Vĩnh Long', 'tỉnh', 'MienNam', 0, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (54, '89', 'Tỉnh Đồng Tháp', 'tỉnh', 'MienNam', 0, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (55, '91', 'Tỉnh An Giang', 'tỉnh', 'MienNam', 0, 10, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (56, '93', 'Tỉnh Kiên Giang', 'tỉnh', 'MienNam', 0, 10, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (57, '94', 'Thành phố Cần Thơ', 'thành-phố', 'MienNam', 0, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (58, '95', 'Tỉnh Hậu Giang', 'tỉnh', 'MienNam', 0, 10, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (59, '96', 'Tỉnh Sóc Trăng', 'tỉnh', 'MienNam', 0, 10, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (60, '97', 'Tỉnh Bạc Liêu', 'tỉnh', 'MienNam', 0, 10, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `provinces` VALUES (61, '98', 'Tỉnh Cà Mau', 'tỉnh', 'MienNam', 0, 10, '2026-06-02 23:22:38', '2026-06-02 23:22:38');

-- ----------------------------
-- Table structure for role_permission_assignment
-- ----------------------------
DROP TABLE IF EXISTS `role_permission_assignment`;
CREATE TABLE `role_permission_assignment`  (
                                               `role_id` int NOT NULL,
                                               `permission_id` int NOT NULL,
                                               `is_active` tinyint NULL DEFAULT 1,
                                               `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                               PRIMARY KEY (`role_id`, `permission_id`) USING BTREE,
                                               INDEX `idx_permission_role`(`permission_id`, `role_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of role_permission_assignment
-- ----------------------------
INSERT INTO `role_permission_assignment` VALUES (2, 1, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 2, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 3, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 4, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 5, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 6, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 7, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 8, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 9, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 10, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 11, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 12, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 13, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 14, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 15, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 16, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 17, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 18, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 19, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 20, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 21, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 22, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 23, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 24, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 25, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 26, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 27, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 28, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 31, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 32, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 33, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 34, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 35, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 36, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 37, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 38, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 39, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 40, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 41, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 42, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 43, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 44, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 45, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 46, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 47, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 48, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (2, 49, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 10, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 11, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 12, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 13, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 20, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 21, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 22, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 23, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 24, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 25, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 26, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 27, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 28, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 31, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 32, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 33, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 34, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (3, 35, 1, '2026-06-02 23:20:14');
INSERT INTO `role_permission_assignment` VALUES (4, 25, 1, '2026-06-02 23:20:14');

-- ----------------------------
-- Table structure for role_registry
-- ----------------------------
DROP TABLE IF EXISTS `role_registry`;
CREATE TABLE `role_registry`  (
                                  `id` int NOT NULL AUTO_INCREMENT,
                                  `code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                  `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                  `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                  `is_system` tinyint NULL DEFAULT 0,
                                  `is_active` tinyint NULL DEFAULT 1,
                                  `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                  `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of role_registry
-- ----------------------------
INSERT INTO `role_registry` VALUES (2, 'ADMIN', 'Quản lý', 'Quản lý trong phạm vi chi nhánh', 0, 1, '2026-06-02 23:20:14', '2026-06-02 23:20:14');
INSERT INTO `role_registry` VALUES (3, 'STAFF', 'Nhân viên', 'Người dùng vận hành hệ thống', 0, 1, '2026-06-02 23:20:14', '2026-06-02 23:20:14');
INSERT INTO `role_registry` VALUES (4, 'CUSTOMER', 'Khách hàng', 'Người mua sắm trực tuyến', 0, 1, '2026-06-02 23:20:14', '2026-06-02 23:20:14');

-- ----------------------------
-- Table structure for shipment_contacts
-- ----------------------------
DROP TABLE IF EXISTS `shipment_contacts`;
CREATE TABLE `shipment_contacts`  (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `shipment_id` bigint NOT NULL,
                                      `contact_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'SHIPPER',
                                      `contact_role` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                      `contact_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                      `contact_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                      `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `idx_shipment_contact_shipment`(`shipment_id`) USING BTREE,
                                      INDEX `idx_shipment_contact_type`(`contact_type`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shipment_contacts
-- ----------------------------

-- ----------------------------
-- Table structure for shipment_tracking
-- ----------------------------
DROP TABLE IF EXISTS `shipment_tracking`;
CREATE TABLE `shipment_tracking`  (
                                      `id` bigint NOT NULL AUTO_INCREMENT,
                                      `shipment_id` bigint NOT NULL,
                                      `status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                      `note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                                      `location` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                      `updated_by` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                      `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                      PRIMARY KEY (`id`) USING BTREE,
                                      INDEX `idx_tracking_shipment`(`shipment_id`) USING BTREE,
                                      INDEX `idx_tracking_status`(`status`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shipment_tracking
-- ----------------------------
INSERT INTO `shipment_tracking` VALUES (1, 1, 'WAITING_PICKUP', 'Đơn hàng đã được tạo, đang chờ lấy hàng', 'Kho hàng - Shop Bán Sách', 'SYSTEM', '2026-06-02 16:31:39');
INSERT INTO `shipment_tracking` VALUES (2, 2, 'WAITING_PICKUP', 'Đơn hàng đã được tạo, đang chờ lấy hàng', 'Kho hàng - Shop Bán Sách', 'SYSTEM', '2026-06-02 16:46:26');
INSERT INTO `shipment_tracking` VALUES (3, 3, 'WAITING_PICKUP', 'Đơn hàng đã được tạo, đang chờ lấy hàng', 'Kho hàng - Shop Bán Sách', 'SYSTEM', '2026-06-02 16:49:13');
INSERT INTO `shipment_tracking` VALUES (4, 4, 'WAITING_PICKUP', 'Đơn hàng đã được tạo, đang chờ lấy hàng', 'Kho hàng - Shop Bán Sách', 'SYSTEM', '2026-06-02 16:52:34');
INSERT INTO `shipment_tracking` VALUES (5, 5, 'WAITING_PICKUP', 'Đơn hàng đã được tạo, đang chờ lấy hàng', 'Kho hàng - Shop Bán Sách', 'SYSTEM', '2026-06-02 17:07:00');
INSERT INTO `shipment_tracking` VALUES (6, 6, 'WAITING_PICKUP', 'Đơn hàng đã được tạo, đang chờ lấy hàng', 'Kho hàng - Shop Bán Sách', 'SYSTEM', '2026-06-02 17:10:08');

-- ----------------------------
-- Table structure for shipments
-- ----------------------------
DROP TABLE IF EXISTS `shipments`;
CREATE TABLE `shipments`  (
                              `id` bigint NOT NULL AUTO_INCREMENT,
                              `order_id` bigint NOT NULL,
                              `shipping_method_id` bigint NULL DEFAULT 1,
                              `tracking_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `receiver_name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `receiver_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `province` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `district` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `ward` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `address_detail` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `total_weight` decimal(10, 2) NULL DEFAULT 0.00,
                              `total_volume` decimal(10, 2) NULL DEFAULT 0.00,
                              `shipping_fee` decimal(12, 2) NULL DEFAULT 0.00,
                              `shipping_status` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'WAITING_PICKUP',
                              `seller_note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                              `customer_note` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                              `shipper_contact` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `estimated_delivery_date` datetime NULL DEFAULT NULL,
                              `shipped_at` datetime NULL DEFAULT NULL,
                              `delivered_at` datetime NULL DEFAULT NULL,
                              `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                              `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                              `provider_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'GHN',
                              `provider_order_code` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `shipper_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `shipper_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              `shipper_avatar` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                              PRIMARY KEY (`id`) USING BTREE,
                              INDEX `idx_shipment_order`(`order_id`) USING BTREE,
                              INDEX `idx_shipment_tracking`(`tracking_code`) USING BTREE,
                              INDEX `idx_shipment_status`(`shipping_status`) USING BTREE,
                              INDEX `fk_shipment_method`(`shipping_method_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shipments
-- ----------------------------
INSERT INTO `shipments` VALUES (1, 1, 1, 'WEB00000001', 'Khach hang', '', 'Kon Tum', 'Huyện Đắk Glei', 'Xã Đắk Long', 'abc', 0.30, 0.00, 20000.00, 'WAITING_PICKUP', '', '', '', '2026-06-05 16:31:39', NULL, NULL, '2026-06-02 16:31:39', NULL, 'GHN', '', NULL, NULL, NULL);
INSERT INTO `shipments` VALUES (2, 2, 1, 'WEB00000002', 'Khach hang', '', 'Phú Yên', 'Huyện Sông Hinh', 'Xã Ea Lâm', 'abc', 0.30, 0.00, 20000.00, 'WAITING_PICKUP', '', '', '', '2026-06-05 16:46:26', NULL, NULL, '2026-06-02 16:46:26', NULL, 'GHN', '', NULL, NULL, NULL);
INSERT INTO `shipments` VALUES (3, 3, 1, 'WEB00000003', 'Khach hang', '', 'Ninh Thuận', 'Huyện Bác Ái', 'Xã Phước Đại', 'abc', 0.30, 0.00, 20000.00, 'WAITING_PICKUP', '', '', '', '2026-06-05 16:49:13', NULL, NULL, '2026-06-02 16:49:13', NULL, 'GHN', '', NULL, NULL, NULL);
INSERT INTO `shipments` VALUES (4, 4, 1, 'WEB00000004', 'Khach hang', '', 'Hậu Giang', 'Huyện Phụng Hiệp', 'Thị trấn Kinh Cùng', 'abc', 0.60, 0.00, 20000.00, 'WAITING_PICKUP', '', '', '', '2026-06-05 16:52:34', NULL, NULL, '2026-06-02 16:52:34', NULL, 'GHN', '', NULL, NULL, NULL);
INSERT INTO `shipments` VALUES (5, 5, 1, 'WEB00000005', 'Khach hang', '', 'Lào Cai', 'Huyện Mường Khương', 'Xã Tả Thàng', 'abc', 0.30, 0.00, 20000.00, 'WAITING_PICKUP', '', '', '', '2026-06-05 17:07:00', NULL, NULL, '2026-06-02 17:07:00', NULL, 'GHN', '', NULL, NULL, NULL);
INSERT INTO `shipments` VALUES (6, 6, 2, 'WEB00000006', 'Khach hang', '', 'Sơn La', 'Huyện Sông Mã', 'Xã Mường Cai', 'abc', 0.30, 0.00, 16000.00, 'WAITING_PICKUP', '', 'huhii', '', '2026-06-05 17:10:08', NULL, NULL, '2026-06-02 17:10:08', NULL, 'GHN', '', NULL, NULL, NULL);

-- ----------------------------
-- Table structure for shipping_fees
-- ----------------------------
DROP TABLE IF EXISTS `shipping_fees`;
CREATE TABLE `shipping_fees`  (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `shipping_method_id` bigint NOT NULL,
                                  `zone_type` enum('INNER','PROVINCIAL','REMOTE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PROVINCIAL',
                                  `min_weight` decimal(10, 2) NOT NULL DEFAULT 0.00,
                                  `max_weight` decimal(10, 2) NOT NULL DEFAULT 999.00,
                                  `base_fee` decimal(12, 2) NOT NULL DEFAULT 0.00,
                                  `fee_per_kg` decimal(12, 2) NOT NULL DEFAULT 0.00,
                                  `price_per_volume` decimal(12, 2) NULL DEFAULT 0.00 COMMENT 'Phí theo thể tích (VND/m3)',
                                  `volumetric_ratio` int NULL DEFAULT 5000 COMMENT 'Hệ số quy đổi thể tích (cm3/kg)',
                                  `estimated_days_min` int NULL DEFAULT 1,
                                  `estimated_days_max` int NULL DEFAULT 3,
                                  `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                  `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `unique_method_zone_weight`(`shipping_method_id`, `zone_type`, `min_weight`) USING BTREE,
                                  INDEX `idx_fee_method`(`shipping_method_id`) USING BTREE,
                                  INDEX `idx_fee_zone`(`zone_type`) USING BTREE,
                                  INDEX `idx_fee_weight`(`min_weight`, `max_weight`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 31 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of shipping_fees
-- ----------------------------
INSERT INTO `shipping_fees` VALUES (1, 1, 'INNER', 0.00, 0.50, 15000.00, 0.00, 2000.00, 5000, 1, 2, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (2, 1, 'INNER', 0.50, 2.00, 15000.00, 8000.00, 2000.00, 5000, 1, 2, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (3, 1, 'INNER', 2.00, 5.00, 15000.00, 10000.00, 2000.00, 5000, 1, 2, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (4, 1, 'INNER', 5.00, 10.00, 15000.00, 12000.00, 2000.00, 5000, 1, 2, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (5, 1, 'INNER', 10.00, 30.00, 15000.00, 15000.00, 2000.00, 5000, 1, 2, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (6, 1, 'PROVINCIAL', 0.00, 0.50, 25000.00, 0.00, 3000.00, 5000, 2, 3, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (7, 1, 'PROVINCIAL', 0.50, 2.00, 25000.00, 12000.00, 3000.00, 5000, 2, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (8, 1, 'PROVINCIAL', 2.00, 5.00, 25000.00, 15000.00, 3000.00, 5000, 2, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (9, 1, 'PROVINCIAL', 5.00, 10.00, 25000.00, 18000.00, 3000.00, 5000, 3, 5, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (10, 1, 'PROVINCIAL', 10.00, 30.00, 25000.00, 22000.00, 3000.00, 5000, 3, 5, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (11, 1, 'REMOTE', 0.00, 0.50, 45000.00, 0.00, 5000.00, 5000, 3, 5, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (12, 1, 'REMOTE', 0.50, 2.00, 45000.00, 18000.00, 5000.00, 5000, 3, 6, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (13, 1, 'REMOTE', 2.00, 5.00, 45000.00, 22000.00, 5000.00, 5000, 4, 6, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (14, 1, 'REMOTE', 5.00, 10.00, 45000.00, 28000.00, 5000.00, 5000, 4, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (15, 1, 'REMOTE', 10.00, 30.00, 45000.00, 35000.00, 5000.00, 5000, 5, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (16, 2, 'INNER', 0.00, 0.50, 12000.00, 0.00, 1500.00, 5000, 2, 3, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (17, 2, 'INNER', 0.50, 2.00, 12000.00, 6000.00, 1500.00, 5000, 2, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (18, 2, 'INNER', 2.00, 5.00, 12000.00, 8000.00, 1500.00, 5000, 3, 4, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (19, 2, 'INNER', 5.00, 10.00, 12000.00, 10000.00, 1500.00, 5000, 3, 5, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (20, 2, 'INNER', 10.00, 30.00, 12000.00, 12000.00, 1500.00, 5000, 4, 5, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (21, 2, 'PROVINCIAL', 0.00, 0.50, 18000.00, 0.00, 2500.00, 5000, 3, 5, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (22, 2, 'PROVINCIAL', 0.50, 2.00, 18000.00, 10000.00, 2500.00, 5000, 3, 5, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (23, 2, 'PROVINCIAL', 2.00, 5.00, 18000.00, 12000.00, 2500.00, 5000, 4, 6, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (24, 2, 'PROVINCIAL', 5.00, 10.00, 18000.00, 15000.00, 2500.00, 5000, 4, 6, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (25, 2, 'PROVINCIAL', 10.00, 30.00, 18000.00, 18000.00, 2500.00, 5000, 5, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (26, 2, 'REMOTE', 0.00, 0.50, 35000.00, 0.00, 4000.00, 5000, 4, 7, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (27, 2, 'REMOTE', 0.50, 2.00, 35000.00, 15000.00, 4000.00, 5000, 5, 8, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (28, 2, 'REMOTE', 2.00, 5.00, 35000.00, 18000.00, 4000.00, 5000, 5, 8, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (29, 2, 'REMOTE', 5.00, 10.00, 35000.00, 22000.00, 4000.00, 5000, 6, 9, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_fees` VALUES (30, 2, 'REMOTE', 10.00, 30.00, 35000.00, 28000.00, 4000.00, 5000, 6, 9, '2026-06-02 23:22:38', '2026-06-02 23:22:38');

-- ----------------------------
-- Table structure for shipping_methods
-- ----------------------------
DROP TABLE IF EXISTS `shipping_methods`;
CREATE TABLE `shipping_methods`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `name` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                     `provider_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT 'GHN',
                                     `estimated_days` int NULL DEFAULT 3,
                                     `price_per_kg` decimal(12, 2) NULL DEFAULT 0.00,
                                     `is_express` tinyint(1) NULL DEFAULT 0,
                                     `express_surcharge` decimal(5, 2) NULL DEFAULT 1.00,
                                     `min_weight_kg` decimal(10, 2) NULL DEFAULT 0.50,
                                     `max_weight_kg` decimal(10, 2) NULL DEFAULT 50.00,
                                     `free_shipping_threshold` decimal(12, 2) NULL DEFAULT 0.00,
                                     `support_phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                     `support_email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                     `ghn_service_id` bigint NULL DEFAULT 0,
                                     `ghn_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                     `ghn_shop_id` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                     `ghn_from_district_id` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                     `ghn_from_ward_code` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                     `api_key` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                     `api_secret` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                     `webhook_token` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                     `status` int NULL DEFAULT 1 COMMENT '1: active, 0: inactive',
                                     `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                     `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     INDEX `idx_provider`(`provider_type`) USING BTREE,
                                     INDEX `idx_status`(`status`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shipping_methods
-- ----------------------------
INSERT INTO `shipping_methods` VALUES (1, 'Giao hàng nhanh', 'GHN', 2, 25000.00, 1, 1.80, 0.50, 30.00, 300000.00, '1900 6365', 'cskh@ghn.vn', 2, NULL, NULL, '1567', '550307', NULL, NULL, NULL, 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_methods` VALUES (2, 'Giao hàng tiêu chuẩn', 'GHN', 4, 18000.00, 0, 1.00, 0.50, 30.00, 500000.00, '1900 6365', 'cskh@ghn.vn', 1, NULL, NULL, '1567', '550307', NULL, NULL, NULL, 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');

-- ----------------------------
-- Table structure for shipping_weight_fees
-- ----------------------------
DROP TABLE IF EXISTS `shipping_weight_fees`;
CREATE TABLE `shipping_weight_fees`  (
                                         `id` bigint NOT NULL AUTO_INCREMENT,
                                         `shipping_method_id` bigint NOT NULL,
                                         `zone_type` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PROVINCIAL',
                                         `min_weight` decimal(10, 2) NOT NULL DEFAULT 0.00,
                                         `max_weight` decimal(10, 2) NOT NULL DEFAULT 5.00,
                                         `base_fee` decimal(12, 2) NOT NULL DEFAULT 0.00,
                                         `fee_per_kg` decimal(12, 2) NOT NULL DEFAULT 0.00,
                                         `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                         `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                         PRIMARY KEY (`id`) USING BTREE,
                                         INDEX `idx_weight_method`(`shipping_method_id`) USING BTREE,
                                         INDEX `idx_weight_zone`(`zone_type`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shipping_weight_fees
-- ----------------------------

-- ----------------------------
-- Table structure for shipping_zones
-- ----------------------------
DROP TABLE IF EXISTS `shipping_zones`;
CREATE TABLE `shipping_zones`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `zone_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                   `zone_type` enum('INNER','PROVINCIAL','REMOTE') CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL DEFAULT 'PROVINCIAL',
                                   `base_fee` decimal(12, 2) NOT NULL DEFAULT 0.00,
                                   `price_per_kg` decimal(12, 2) NOT NULL DEFAULT 0.00,
                                   `price_per_volume` decimal(12, 2) NOT NULL DEFAULT 0.00,
                                   `estimated_days_min` int NOT NULL DEFAULT 2,
                                   `estimated_days_max` int NOT NULL DEFAULT 5,
                                   `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                   `status` int NULL DEFAULT 1 COMMENT '1: active, 0: inactive',
                                   `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                   `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   INDEX `idx_zone_type`(`zone_type`) USING BTREE,
                                   INDEX `idx_status`(`status`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 11 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of shipping_zones
-- ----------------------------
INSERT INTO `shipping_zones` VALUES (1, 'Nội thành TP.HCM', 'INNER', 15000.00, 3000.00, 5000.00, 1, 2, 'Các quận nội thành TP.HCM: Quận 1, 3, 4, 5, 6, 7, 8, 10, 11, 12, Bình Thạnh, Gò Vấp, Phú Nhuận, Tân Bình, Tân Phú, Thủ Đức', 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_zones` VALUES (2, 'Nội thành Hà Nội', 'INNER', 18000.00, 3500.00, 5000.00, 1, 2, 'Các quận nội thành Hà Nội: Ba Đình, Hoàn Kiếm, Hai Bà Trưng, Đống Đa, Tây Hồ, Thanh Xuân, Cầu Giấy, Hoàng Mai, Long Biên, Hà Đông', 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_zones` VALUES (3, 'Nội thành Đà Nẵng', 'INNER', 12000.00, 2500.00, 4000.00, 1, 2, 'Các quận nội thành Đà Nẵng: Hải Châu, Thanh Khê, Sơn Trà, Ngũ Hành Sơn, Liên Chiểu', 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_zones` VALUES (4, 'Miền Bắc - Tỉnh lẻ', 'PROVINCIAL', 25000.00, 5000.00, 8000.00, 2, 4, 'Các tỉnh miền Bắc ngoài Hà Nội: Hải Phòng, Hải Dương, Nam Định, Thái Bình, Ninh Bình, Hưng Yên, Bắc Ninh, Bắc Giang, Lạng Sơn, Quảng Ninh', 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_zones` VALUES (5, 'Miền Trung - Bắc', 'PROVINCIAL', 30000.00, 6000.00, 10000.00, 2, 4, 'Các tỉnh miền Trung phía Bắc: Thanh Hóa, Nghệ An, Hà Tĩnh, Quảng Bình, Quảng Trị, Thừa Thiên Huế', 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_zones` VALUES (6, 'Miền Trung - Nam', 'PROVINCIAL', 30000.00, 6000.00, 10000.00, 2, 4, 'Các tỉnh miền Trung phía Nam: Đà Nẵng, Quảng Nam, Quảng Ngãi, Bình Định, Phú Yên, Khánh Hòa, Ninh Thuận, Bình Thuận', 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_zones` VALUES (7, 'Miền Nam - Tỉnh lẻ', 'PROVINCIAL', 25000.00, 5000.00, 8000.00, 2, 4, 'Các tỉnh miền Nam ngoài TP.HCM: Bình Dương, Đồng Nai, Long An, Tiền Giang, Bến Tre, Vũng Tàu, Tây Ninh, Cần Thơ', 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_zones` VALUES (8, 'Vùng xa miền Bắc', 'REMOTE', 45000.00, 10000.00, 15000.00, 4, 7, 'Các vùng xa miền Bắc: Lào Cai, Yên Bái, Điện Biên, Lai Châu, Sơn La, Hà Giang, Tuyên Quang, Cao Bằng, Bắc Kạn', 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_zones` VALUES (9, 'Vùng xa miền Trung & Tây Nguyên', 'REMOTE', 50000.00, 12000.00, 18000.00, 4, 7, 'Các vùng xa miền Trung & Tây Nguyên: Kon Tum, Gia Lai, Đắk Lắk, Đắk Nông, Lâm Đồng, Bình Phước', 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `shipping_zones` VALUES (10, 'Vùng xa miền Nam', 'REMOTE', 45000.00, 10000.00, 15000.00, 4, 7, 'Các vùng xa miền Nam: Cà Mau, Bạc Liêu, Sóc Trăng, Trà Vinh, Hậu Giang, Kiên Giang, An Giang', 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');

-- ----------------------------
-- Table structure for token_status
-- ----------------------------
DROP TABLE IF EXISTS `token_status`;
CREATE TABLE `token_status`  (
                                 `id` int NOT NULL AUTO_INCREMENT,
                                 `code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                 `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                 `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                 `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                 `is_active` tinyint NULL DEFAULT 1,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 4 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of token_status
-- ----------------------------
INSERT INTO `token_status` VALUES (1, 'ACTIVE', 'Đang hiệu lực', 'Token còn trong thời gian sử dụng', '2026-06-02 23:20:14', 1);
INSERT INTO `token_status` VALUES (2, 'USED', 'Đã sử dụng', 'Token đã được thực hiện thành công', '2026-06-02 23:20:14', 1);
INSERT INTO `token_status` VALUES (3, 'EXPIRED', 'Hết hạn', 'Token đã quá thời gian hiệu lực', '2026-06-02 23:20:14', 1);

-- ----------------------------
-- Table structure for token_type
-- ----------------------------
DROP TABLE IF EXISTS `token_type`;
CREATE TABLE `token_type`  (
                               `id` int NOT NULL AUTO_INCREMENT,
                               `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                               `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                               `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                               `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                               `is_active` tinyint NULL DEFAULT 1,
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of token_type
-- ----------------------------
INSERT INTO `token_type` VALUES (1, 'VERIFY_EMAIL', 'Mã xác thực email', 'Token dùng để xác nhận sở hữu email', '2026-06-02 23:20:14', 1);
INSERT INTO `token_type` VALUES (2, 'RESET_PASSWORD', 'Mã đặt lại mật khẩu', 'Token dùng để khôi phục mật khẩu', '2026-06-02 23:20:14', 1);

-- ----------------------------
-- Table structure for user_account
-- ----------------------------
DROP TABLE IF EXISTS `user_account`;
CREATE TABLE `user_account`  (
                                 `id` bigint NOT NULL AUTO_INCREMENT,
                                 `status_id` int NOT NULL,
                                 `token_version` int NOT NULL DEFAULT 0,
                                 `last_login_at` timestamp NULL DEFAULT NULL,
                                 `remember_token` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                 `remember_expires_at` timestamp NULL DEFAULT NULL,
                                 `deleted_at` timestamp NULL DEFAULT NULL,
                                 `deleted_by` bigint NULL DEFAULT NULL,
                                 `deletion_scheduled_at` timestamp NULL DEFAULT NULL,
                                 `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                 `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`id`) USING BTREE,
                                 INDEX `idx_status`(`status_id`) USING BTREE,
                                 INDEX `idx_deleted`(`deleted_at`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 7 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_account
-- ----------------------------
INSERT INTO `user_account` VALUES (1, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2026-06-02 23:20:14', '2026-06-02 23:20:14');
INSERT INTO `user_account` VALUES (2, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2026-06-02 23:20:14', '2026-06-02 23:20:14');
INSERT INTO `user_account` VALUES (3, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2026-06-02 23:20:14', '2026-06-02 23:20:14');
INSERT INTO `user_account` VALUES (4, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2026-06-02 23:20:14', '2026-06-02 23:20:14');
INSERT INTO `user_account` VALUES (5, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2026-06-02 23:20:14', '2026-06-02 23:20:14');
INSERT INTO `user_account` VALUES (6, 1, 0, NULL, NULL, NULL, NULL, NULL, NULL, '2026-06-02 23:20:14', '2026-06-02 23:20:14');

-- ----------------------------
-- Table structure for user_account_status
-- ----------------------------
DROP TABLE IF EXISTS `user_account_status`;
CREATE TABLE `user_account_status`  (
                                        `id` int NOT NULL AUTO_INCREMENT,
                                        `code` varchar(30) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                        `name` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                        `description` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                        `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                        `is_active` tinyint NULL DEFAULT 1,
                                        PRIMARY KEY (`id`) USING BTREE,
                                        UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_account_status
-- ----------------------------
INSERT INTO `user_account_status` VALUES (1, 'ACTIVE', 'Hoạt động', 'Tài khoản đang hoạt động bình thường', '2026-06-02 23:20:14', 1);
INSERT INTO `user_account_status` VALUES (2, 'LOCKED', 'Bị khóa', 'Tài khoản tạm thời bị khóa do vi phạm hoặc bảo mật', '2026-06-02 23:20:14', 1);
INSERT INTO `user_account_status` VALUES (3, 'DELETED', 'Đã xóa', 'Tài khoản đã bị xóa khỏi hệ thống', '2026-06-02 23:20:14', 1);
INSERT INTO `user_account_status` VALUES (4, 'PENDING', 'Chờ xác thực', 'Tài khoản mới đăng ký, chưa xác thực email', '2026-06-02 23:20:14', 1);

-- ----------------------------
-- Table structure for user_local
-- ----------------------------
DROP TABLE IF EXISTS `user_local`;
CREATE TABLE `user_local`  (
                               `user_id` bigint NOT NULL,
                               `username` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                               `password_hash` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                               `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                               `email_verify_status_id` tinyint NOT NULL DEFAULT 1,
                               `failed_attempts` int NOT NULL DEFAULT 0,
                               `locked_until` timestamp NULL DEFAULT NULL,
                               PRIMARY KEY (`user_id`) USING BTREE,
                               UNIQUE INDEX `username`(`username`) USING BTREE,
                               UNIQUE INDEX `email`(`email`) USING BTREE,
                               INDEX `fk_email_verify_status`(`email_verify_status_id`) USING BTREE,
                               CONSTRAINT `user_local_chk_1` CHECK (`failed_attempts` >= 0)
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_local
-- ----------------------------
INSERT INTO `user_local` VALUES (2, 'user1', '$2a$12$UllaLd399u9rzzFCvwLK8Of5vL1l9MxyC1OCMR1cyfCd4jxoyBqf2', 'dunnmcpherson@recrisys.com', 1, 0, NULL);
INSERT INTO `user_local` VALUES (3, 'user2', '$2a$12$UllaLd399u9rzzFCvwLK8Of5vL1l9MxyC1OCMR1cyfCd4jxoyBqf2', 'foremancarter@recrisys.com', 1, 0, NULL);
INSERT INTO `user_local` VALUES (4, 'user3', '$2a$12$UllaLd399u9rzzFCvwLK8Of5vL1l9MxyC1OCMR1cyfCd4jxoyBqf2', 'feleciacabrera@recrisys.com', 1, 0, NULL);
INSERT INTO `user_local` VALUES (5, 'user4', '$2a$12$UllaLd399u9rzzFCvwLK8Of5vL1l9MxyC1OCMR1cyfCd4jxoyBqf2', 'juliettemcdowell@recrisys.com', 1, 0, NULL);
INSERT INTO `user_local` VALUES (6, 'user5', '$2a$12$UllaLd399u9rzzFCvwLK8Of5vL1l9MxyC1OCMR1cyfCd4jxoyBqf2', 'vilmaspencer@recrisys.com', 1, 0, NULL);

-- ----------------------------
-- Table structure for user_oauth
-- ----------------------------
DROP TABLE IF EXISTS `user_oauth`;
CREATE TABLE `user_oauth`  (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `user_id` bigint NOT NULL,
                               `provider_id` int NOT NULL,
                               `provider_user_id` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                               `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                               `display_name` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                               `avatar_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE INDEX `email`(`email`) USING BTREE,
                               UNIQUE INDEX `uq_provider_user`(`provider_id`, `provider_user_id`) USING BTREE,
                               UNIQUE INDEX `uq_user_provider`(`user_id`, `provider_id`) USING BTREE,
                               INDEX `idx_provider`(`provider_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_oauth
-- ----------------------------

-- ----------------------------
-- Table structure for user_profile
-- ----------------------------
DROP TABLE IF EXISTS `user_profile`;
CREATE TABLE `user_profile`  (
                                 `user_id` bigint NOT NULL,
                                 `fullname` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                 `phone_number` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                 `email` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                 `gender_id` tinyint NULL DEFAULT NULL,
                                 `avatar_url` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL,
                                 `preferred_language_id` int NOT NULL DEFAULT 1,
                                 `updated_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                 PRIMARY KEY (`user_id`) USING BTREE,
                                 UNIQUE INDEX `email`(`email`) USING BTREE,
                                 INDEX `fk_gender`(`gender_id`) USING BTREE,
                                 INDEX `fk_profile_language`(`preferred_language_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_profile
-- ----------------------------
INSERT INTO `user_profile` VALUES (1, 'SYSTEM', NULL, 'system@webbookshop.com', 0, NULL, 1, '2026-06-02 23:20:14');
INSERT INTO `user_profile` VALUES (2, 'Dunn Mcpherson', '0989894900', 'dunnmcpherson@recrisys.com', 0, NULL, 1, '2026-06-02 23:20:14');
INSERT INTO `user_profile` VALUES (3, 'Foreman Carter', '0993194154', 'foremancarter@recrisys.com', 0, NULL, 1, '2026-06-02 23:20:14');
INSERT INTO `user_profile` VALUES (4, 'Felecia Cabrera', '0930174351', 'feleciacabrera@recrisys.com', 1, NULL, 1, '2026-06-02 23:20:14');
INSERT INTO `user_profile` VALUES (5, 'Juliette Mcdowell', '0911925643', 'juliettemcdowell@recrisys.com', 1, NULL, 1, '2026-06-02 23:20:14');
INSERT INTO `user_profile` VALUES (6, 'Vilma Spencer', '0987509391', 'vilmaspencer@recrisys.com', 1, NULL, 1, '2026-06-02 23:20:14');

-- ----------------------------
-- Table structure for user_role_registry
-- ----------------------------
DROP TABLE IF EXISTS `user_role_registry`;
CREATE TABLE `user_role_registry`  (
                                       `user_id` bigint NOT NULL,
                                       `role_id` int NOT NULL,
                                       `assigned_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                       `assigned_by` bigint NULL DEFAULT NULL,
                                       PRIMARY KEY (`user_id`, `role_id`) USING BTREE,
                                       INDEX `fk_ur_role`(`role_id`) USING BTREE,
                                       INDEX `idx_user`(`user_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of user_role_registry
-- ----------------------------
INSERT INTO `user_role_registry` VALUES (2, 2, '2026-06-02 23:20:14', NULL);
INSERT INTO `user_role_registry` VALUES (3, 3, '2026-06-02 23:20:14', NULL);
INSERT INTO `user_role_registry` VALUES (4, 3, '2026-06-02 23:20:14', NULL);
INSERT INTO `user_role_registry` VALUES (5, 4, '2026-06-02 23:20:14', NULL);
INSERT INTO `user_role_registry` VALUES (6, 4, '2026-06-02 23:20:14', NULL);

-- ----------------------------
-- Table structure for user_shipping_addresses
-- ----------------------------
DROP TABLE IF EXISTS `user_shipping_addresses`;
CREATE TABLE `user_shipping_addresses`  (
                                            `id` bigint NOT NULL AUTO_INCREMENT,
                                            `user_id` bigint NOT NULL,
                                            `fullname` varchar(200) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                            `phone` varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                            `province` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                            `district` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                            `ward` varchar(100) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                                            `address_detail` varchar(500) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
                                            `is_default` tinyint(1) NULL DEFAULT 0,
                                            `created_at` datetime NULL DEFAULT CURRENT_TIMESTAMP,
                                            `updated_at` datetime NULL DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
                                            PRIMARY KEY (`id`) USING BTREE,
                                            INDEX `idx_contact_user`(`user_id`) USING BTREE,
                                            INDEX `idx_contact_default`(`user_id`, `is_default`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_shipping_addresses
-- ----------------------------
INSERT INTO `user_shipping_addresses` VALUES (1, 4, 'Juliette Mcdowell', '0911925643', 'TP HCM', 'Quan 1', 'Phuong 1', '123 Le Loi', 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');
INSERT INTO `user_shipping_addresses` VALUES (2, 5, 'Vilma Spencer', '0987509391', 'Ha Noi', 'Hoan Kiem', 'Phuong Hang Bac', '456 Tran Phu', 1, '2026-06-02 23:22:38', '2026-06-02 23:22:38');

-- ----------------------------
-- Table structure for user_token
-- ----------------------------
DROP TABLE IF EXISTS `user_token`;
CREATE TABLE `user_token`  (
                               `id` bigint NOT NULL AUTO_INCREMENT,
                               `user_id` bigint NOT NULL,
                               `token_hash` varchar(191) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                               `type_id` int NOT NULL,
                               `status_id` int NOT NULL,
                               `used_at` timestamp NULL DEFAULT NULL,
                               `expires_at` timestamp NOT NULL,
                               `created_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                               PRIMARY KEY (`id`) USING BTREE,
                               UNIQUE INDEX `token_hash`(`token_hash`) USING BTREE,
                               INDEX `idx_user`(`user_id`) USING BTREE,
                               INDEX `idx_type`(`type_id`) USING BTREE,
                               INDEX `idx_status`(`status_id`) USING BTREE,
                               CONSTRAINT `user_token_chk_1` CHECK (`expires_at` > `created_at`)
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of user_token
-- ----------------------------

-- ----------------------------
-- Table structure for voucher_categories
-- ----------------------------
DROP TABLE IF EXISTS `voucher_categories`;
CREATE TABLE `voucher_categories`  (
                                       `id` bigint NOT NULL AUTO_INCREMENT,
                                       `voucher_id` bigint NOT NULL,
                                       `category_id` bigint NOT NULL,
                                       PRIMARY KEY (`id`) USING BTREE,
                                       UNIQUE INDEX `uq_voucher_category`(`voucher_id`, `category_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of voucher_categories
-- ----------------------------
INSERT INTO `voucher_categories` VALUES (1, 1, 2);
INSERT INTO `voucher_categories` VALUES (2, 1, 3);

-- ----------------------------
-- Table structure for voucher_products
-- ----------------------------
DROP TABLE IF EXISTS `voucher_products`;
CREATE TABLE `voucher_products`  (
                                     `id` bigint NOT NULL AUTO_INCREMENT,
                                     `voucher_id` bigint NOT NULL,
                                     `product_id` bigint NOT NULL,
                                     PRIMARY KEY (`id`) USING BTREE,
                                     UNIQUE INDEX `uq_voucher_product`(`voucher_id`, `product_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 3 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of voucher_products
-- ----------------------------
INSERT INTO `voucher_products` VALUES (1, 2, 10);
INSERT INTO `voucher_products` VALUES (2, 2, 11);

-- ----------------------------
-- Table structure for voucher_usages
-- ----------------------------
DROP TABLE IF EXISTS `voucher_usages`;
CREATE TABLE `voucher_usages`  (
                                   `id` bigint NOT NULL AUTO_INCREMENT,
                                   `order_id` bigint NOT NULL,
                                   `voucher_id` bigint NOT NULL,
                                   `user_id` bigint NOT NULL,
                                   `discount_amount` decimal(12, 2) NULL DEFAULT 0.00,
                                   `applied_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
                                   PRIMARY KEY (`id`) USING BTREE,
                                   UNIQUE INDEX `uq_order_voucher`(`order_id`, `voucher_id`) USING BTREE,
                                   INDEX `fk_voucher_usages_vouchers`(`voucher_id`) USING BTREE,
                                   INDEX `fk_voucher_usages_users`(`user_id`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 5 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of voucher_usages
-- ----------------------------
INSERT INTO `voucher_usages` VALUES (1, 2, 4, -1, 20000.00, '2026-06-02 16:46:26');
INSERT INTO `voucher_usages` VALUES (2, 2, 17, -1, 20000.00, '2026-06-02 16:46:26');
INSERT INTO `voucher_usages` VALUES (3, 6, 4, -1, 20000.00, '2026-06-02 17:10:08');
INSERT INTO `voucher_usages` VALUES (4, 6, 17, -1, 16000.00, '2026-06-02 17:10:08');

-- ----------------------------
-- Table structure for vouchers
-- ----------------------------
DROP TABLE IF EXISTS `vouchers`;
CREATE TABLE `vouchers`  (
                             `id` bigint NOT NULL AUTO_INCREMENT,
                             `code` varchar(50) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                             `name` varchar(255) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                             `description` text CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NOT NULL,
                             `calculation_method` tinyint NOT NULL DEFAULT 0,
                             `apply_to` tinyint NOT NULL DEFAULT 0,
                             `start_date` datetime NOT NULL,
                             `end_date` datetime NOT NULL,
                             `value` double NOT NULL DEFAULT 0,
                             `min_purchase` double NOT NULL DEFAULT 0,
                             `max_discount` double NOT NULL DEFAULT 0,
                             `usage_limit` int NOT NULL DEFAULT 0,
                             `per_user_limit` int NOT NULL DEFAULT 1,
                             `used_count` int NOT NULL DEFAULT 0,
                             `is_active` tinyint(1) NOT NULL DEFAULT 1,
                             PRIMARY KEY (`id`) USING BTREE,
                             UNIQUE INDEX `code`(`code`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 23 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Dynamic;

-- ----------------------------
-- Records of vouchers
-- ----------------------------
INSERT INTO `vouchers` VALUES (1, 'WELCOME50', 'Giảm ngay 50k cho khách hàng mới', 'Áp dụng cho mọi đơn hàng', 1, 0, '2026-05-27 02:26:20', '2026-06-26 02:26:20', 50000, 0, 50000, 1000, 1, 0, 1);
INSERT INTO `vouchers` VALUES (2, 'HELLOSUMMER', 'Đón hè rực rỡ giảm 10% đơn hàng', 'Áp dụng toàn sàn', 0, 0, '2026-05-27 02:26:20', '2026-06-11 02:26:20', 10, 50000, 30000, 500, 1, 0, 1);
INSERT INTO `vouchers` VALUES (3, 'FANSALE15', 'Tri ân cứng - Giảm 15% tổng đơn', 'Áp dụng cho mọi tài khoản', 0, 0, '2026-05-27 02:26:20', '2026-07-11 02:26:20', 15, 100000, 70000, 200, 1, 1, 1);
INSERT INTO `vouchers` VALUES (4, 'EVERYDAY20', 'Ưu đãi mỗi ngày giảm nhẹ 20k', 'Đơn tối thiểu cực thấp', 1, 0, '2026-05-27 02:26:20', '2026-06-03 02:26:20', 20000, 10000, 20000, 1000, 2, 3, 1);
INSERT INTO `vouchers` VALUES (5, 'MIDMONTH05', 'Khuyến mãi giữa tháng giảm 5%', 'Mã giảm giá tự động', 0, 0, '2026-05-27 02:26:20', '2026-05-30 02:26:20', 5, 0, 15000, 999, 1, 1, 1);
INSERT INTO `vouchers` VALUES (6, 'NLUXINCHAO', 'Mã đặc quyền Sinh viên Nông Lâm', 'Ưu đãi độc quyền', 1, 0, '2026-05-27 02:26:20', '2026-07-26 02:26:20', 35000, 20000, 35000, 300, 1, 1, 1);
INSERT INTO `vouchers` VALUES (7, 'PRODTECH12', 'Voucher nhóm hàng Công Nghệ - Giảm 12%', 'Áp dụng danh sách SP công nghệ', 0, 1, '2026-05-27 02:26:20', '2026-06-16 02:26:20', 12, 0, 60000, 150, 1, 0, 1);
INSERT INTO `vouchers` VALUES (8, 'FASHION30K', 'Xả kho hàng Thời Trang - Giảm thẳng 30.000đ', 'Áp dụng cho quần áo, phụ kiện', 1, 1, '2026-05-27 02:26:20', '2026-06-06 02:26:20', 30000, 50000, 30000, 400, 1, 0, 1);
INSERT INTO `vouchers` VALUES (9, 'BOOKSALE25', 'Tuần lễ Sách và Tri Thức - Giảm 25%', 'Áp dụng cho các đầu sách mới', 0, 1, '2026-05-27 02:26:20', '2026-06-10 02:26:20', 25, 0, 40000, 500, 1, 0, 1);
INSERT INTO `vouchers` VALUES (10, 'COSMETIC15', 'Ngày hội làm đẹp - Giảm giá 15k', 'Áp dụng cho các sản phẩm chăm sóc da', 1, 1, '2026-05-27 02:26:20', '2026-06-04 02:26:20', 15000, 0, 15000, 300, 1, 0, 1);
INSERT INTO `vouchers` VALUES (11, 'DRINKLOVER', 'Deal giải nhiệt mùa hè - Giảm 8%', 'Áp dụng cho các loại đồ uống', 0, 1, '2026-05-27 02:26:20', '2026-06-01 02:26:20', 8, 10000, 20000, 1000, 5, 0, 1);
INSERT INTO `vouchers` VALUES (12, 'CATFOOD20', 'Danh mục Đồ Ăn - Giảm ngay 20.000đ', 'Áp dụng cho toàn bộ danh mục thực phẩm', 1, 2, '2026-05-27 02:26:20', '2026-06-08 02:26:20', 20000, 0, 20000, 600, 1, 0, 1);
INSERT INTO `vouchers` VALUES (13, 'CATHOME10', 'Gia dụng thông minh - Giảm mạnh 10%', 'Áp dụng cho ngành hàng nhà cửa & đời sống', 0, 2, '2026-05-27 02:26:20', '2026-06-26 02:26:20', 10, 150000, 100000, 250, 1, 0, 1);
INSERT INTO `vouchers` VALUES (14, 'CATTOY50K', 'Thế giới đồ chơi trẻ em - Giảm 50k', 'Áp dụng đơn danh mục đồ chơi', 1, 2, '2026-05-27 02:26:20', '2026-06-14 02:26:20', 50000, 200000, 50000, 100, 1, 0, 1);
INSERT INTO `vouchers` VALUES (15, 'CATSPORT15', 'Thể thao năng động - Giảm 15%', 'Áp dụng danh mục dụng cụ thể thao', 0, 2, '2026-05-27 02:26:20', '2026-06-21 02:26:20', 15, 100000, 50000, 200, 1, 0, 1);
INSERT INTO `vouchers` VALUES (16, 'CATELEC05', 'Điện máy chính hãng - Giảm 5%', 'Áp dụng cho tivi, tủ lạnh, máy giặt', 0, 2, '2026-05-27 02:26:20', '2026-07-11 02:26:20', 5, 500000, 200000, 50, 1, 0, 1);
INSERT INTO `vouchers` VALUES (17, 'FREESHIP0D', 'Miễn phí vận chuyển toàn quốc đơn từ 0đ', 'Freeship tối đa 15k', 1, 3, '2026-05-27 02:26:20', '2026-06-26 02:26:20', 15000, 0, 15000, 5000, 2, 4, 1);
INSERT INTO `vouchers` VALUES (18, 'SHIPEXTRA30', 'Freeship Extra hỗ trợ phí ship tới 30k', 'Áp dụng cho đơn hàng từ 100k', 1, 3, '2026-05-27 02:26:20', '2026-06-11 02:26:20', 30000, 100000, 30000, 2000, 1, 1, 1);
INSERT INTO `vouchers` VALUES (19, 'HOATOC20K', 'Hỗ trợ giao hàng hỏa tốc giảm 20k', 'Áp dụng cho phương thức giao nhanh', 1, 3, '2026-05-27 02:26:20', '2026-06-03 02:26:20', 20000, 150000, 20000, 1000, 1, 1, 1);
INSERT INTO `vouchers` VALUES (20, 'SHIPVIP50', 'Hỗ trợ vận chuyển đặc biệt giảm 50k', 'Dành cho đơn hàng giá trị lớn', 1, 3, '2026-05-27 02:26:20', '2026-06-26 02:26:20', 50000, 400000, 50000, 500, 1, 0, 1);
INSERT INTO `vouchers` VALUES (21, 'COMINGSOON', '[Sắp mở] Voucher ngày đôi siêu sale', 'Sẽ mở vào tuần tới', 0, 0, '2026-06-03 02:26:20', '2026-06-06 02:26:20', 20, 0, 50000, 100, 1, 0, 1);
INSERT INTO `vouchers` VALUES (22, 'EXPIRED99', '[Hết hạn] Voucher xả hàng tháng trước', 'Đã hết thời hạn sử dụng', 1, 0, '2026-05-12 02:26:20', '2026-05-26 02:26:20', 100000, 0, 100000, 100, 1, 0, 1);

-- ----------------------------
-- Table structure for wishlist_item
-- ----------------------------
DROP TABLE IF EXISTS `wishlist_item`;
CREATE TABLE `wishlist_item`  (
                                  `id` bigint NOT NULL AUTO_INCREMENT,
                                  `userId` bigint NOT NULL,
                                  `productId` bigint NOT NULL,
                                  `createdAt` datetime NOT NULL,
                                  PRIMARY KEY (`id`) USING BTREE,
                                  UNIQUE INDEX `uq_userId_productId`(`userId`, `productId`) USING BTREE,
                                  INDEX `idx_wishlist_item_user`(`userId`) USING BTREE,
                                  INDEX `idx_wishlist_item_product`(`productId`) USING BTREE
) ENGINE = MyISAM AUTO_INCREMENT = 1 CHARACTER SET = utf8mb4 COLLATE = utf8mb4_0900_ai_ci ROW_FORMAT = Fixed;

-- ----------------------------
-- Records of wishlist_item
-- ----------------------------

SET FOREIGN_KEY_CHECKS = 1;
