CREATE DATABASE bookshopdb;
USE bookshopdb;

CREATE TABLE user_account_status (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE, -- Mã code cho status
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255), -- Mô tả trạng thái tài khoản
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active TINYINT DEFAULT 1
);

CREATE TABLE email_verify_status (
    id TINYINT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- Mã code cho status
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255), -- Mô tả trạng thái cho việc xác nhận
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active TINYINT DEFAULT 1
);

CREATE TABLE oauth_provider (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE, -- Mã code cho provider
    name VARCHAR(50) NOT NULL,
    description VARCHAR(100), -- Mô tả provider
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active TINYINT DEFAULT 1
);

CREATE TABLE token_type (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(50) NOT NULL UNIQUE, -- Mã code cho kiểu dữ liệu của token
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255), -- Mô tả kiểu dữ liệu
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active TINYINT DEFAULT 1
);

CREATE TABLE token_status (
    id INT AUTO_INCREMENT PRIMARY KEY, 
    code VARCHAR(30) NOT NULL UNIQUE, -- Mã code cho trạng thái token 
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255), -- Mô tả trạng thải token
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active TINYINT DEFAULT 1
);

CREATE TABLE gender (
    id TINYINT PRIMARY KEY,
    code VARCHAR(20) NOT NULL UNIQUE, -- Mã code cho giới tính
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255), -- Mô tả giới tính
	created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active TINYINT DEFAULT 1
);

CREATE TABLE language_registry (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(10) NOT NULL UNIQUE, -- Mã code cho ngôn ngữ
    name VARCHAR(50) NOT NULL,
    description VARCHAR(50) NOT NULL, -- Mô tả ngôn ngữ
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    is_active TINYINT DEFAULT 1
);

CREATE TABLE role_registry (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(30) NOT NULL UNIQUE, -- Mã code cho role
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255), -- Mô tả cho role
    is_system TINYINT DEFAULT 0,
    is_active TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP
);

CREATE TABLE permission_registry (
    id INT AUTO_INCREMENT PRIMARY KEY,
    code VARCHAR(100) NOT NULL UNIQUE, -- Mã code cho permission
    name VARCHAR(50) NOT NULL,
    description VARCHAR(255), -- Mô tả permission làm gì
    module VARCHAR(50), -- Đánh dấu phân theo module
    is_system TINYINT DEFAULT 0,
    is_active TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP
);

CREATE TABLE role_permission_assignment (
    role_id INT NOT NULL,
    permission_id INT NOT NULL,
	is_active TINYINT DEFAULT 1,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    PRIMARY KEY (role_id, permission_id),
    CONSTRAINT fk_rp_role FOREIGN KEY (role_id) REFERENCES role_registry(id) ON DELETE CASCADE,
    CONSTRAINT fk_rp_permission FOREIGN KEY (permission_id) REFERENCES permission_registry(id) ON DELETE CASCADE,
    
    INDEX idx_permission_role (permission_id, role_id)
);

CREATE TABLE user_account (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    status_id INT NOT NULL,
    
    token_version INT NOT NULL DEFAULT 0,
    last_login_at TIMESTAMP NULL,
    
    remember_token VARCHAR(255) NULL,
    remember_expires_at TIMESTAMP NULL,
    
    deleted_at TIMESTAMP NULL,
    deleted_by BIGINT NULL,
    deletion_scheduled_at TIMESTAMP NULL,
    
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_user_status FOREIGN KEY (status_id) REFERENCES user_account_status(id),
    
    INDEX idx_status (status_id),
    INDEX idx_deleted (deleted_at)
);

CREATE TABLE user_role_registry (
    user_id BIGINT NOT NULL,
    role_id INT NOT NULL,
    assigned_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    assigned_by BIGINT NULL,
    
    PRIMARY KEY (user_id, role_id),
    CONSTRAINT fk_ur_user FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
    CONSTRAINT fk_ur_role FOREIGN KEY (role_id) REFERENCES role_registry(id) ON DELETE CASCADE,
    INDEX idx_user (user_id)
);

CREATE TABLE user_local (
    user_id BIGINT PRIMARY KEY,
    username VARCHAR(50) NOT NULL UNIQUE,
    password_hash VARCHAR(255) NOT NULL,
    email VARCHAR(100) NOT NULL UNIQUE,
    email_verify_status_id TINYINT NOT NULL DEFAULT 1,
    
    failed_attempts INT NOT NULL DEFAULT 0,
    locked_until TIMESTAMP NULL,
    
    CHECK (failed_attempts >= 0),
    CONSTRAINT fk_local_user FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
    CONSTRAINT fk_email_verify_status FOREIGN KEY (email_verify_status_id) REFERENCES email_verify_status(id)
);

CREATE TABLE user_oauth (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    provider_id INT NOT NULL,
    provider_user_id VARCHAR(191) NOT NULL,
    
    email VARCHAR(100) NOT NULL UNIQUE,
    display_name VARCHAR(100),
    avatar_url TEXT,
    
    UNIQUE KEY uq_provider_user (provider_id, provider_user_id),
    UNIQUE KEY uq_user_provider (user_id, provider_id),
    
    CONSTRAINT fk_oauth_user FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
    CONSTRAINT fk_oauth_provider FOREIGN KEY (provider_id) REFERENCES oauth_provider(id),
    
    INDEX idx_provider (provider_id)
);

CREATE TABLE user_token (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    
    token_hash VARCHAR(191) NOT NULL UNIQUE,
    type_id INT NOT NULL,
    status_id INT NOT NULL,
    
    used_at TIMESTAMP NULL,
    expires_at TIMESTAMP NOT NULL,
    created_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP,
    
    CONSTRAINT fk_token_user FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
    CONSTRAINT fk_token_type FOREIGN KEY (type_id) REFERENCES token_type(id),
    CONSTRAINT fk_token_status FOREIGN KEY (status_id) REFERENCES token_status(id),
    
    CHECK (expires_at > created_at),
    
    INDEX idx_user (user_id),
    INDEX idx_type (type_id),
    INDEX idx_status (status_id)
);

	CREATE TABLE user_profile (
		user_id BIGINT PRIMARY KEY,
		fullname VARCHAR(100),
		phone_number VARCHAR(20),
		email VARCHAR(100) NOT NULL UNIQUE,
		gender_id TINYINT,
		avatar_url TEXT,
		preferred_language_id INT NOT NULL DEFAULT 1,
		updated_at TIMESTAMP DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
		
		CONSTRAINT fk_profile_user FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE,
		CONSTRAINT fk_gender FOREIGN KEY (gender_id) REFERENCES gender(id),
		CONSTRAINT fk_profile_language FOREIGN KEY (preferred_language_id) REFERENCES language_registry(id)
	);

CREATE TABLE category
(
    id          BIGINT       NOT NULL AUTO_INCREMENT,
    name        VARCHAR(100) NOT NULL,
    description TEXT         NULL,
    imageName   VARCHAR(35)  NULL,
    isDeleted   BIT          NOT NULL DEFAULT 0,
    createdAt   DATETIME     NOT NULL,
    updatedAt   DATETIME     NULL,
    PRIMARY KEY (id)
);

CREATE TABLE product (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(100) NOT NULL,
    price FLOAT NOT NULL,
    discount FLOAT NOT NULL,
    quantity SMALLINT NOT NULL,
    totalBuy SMALLINT NOT NULL,
    author VARCHAR(50) NOT NULL,
    pages SMALLINT NOT NULL,
    publisher VARCHAR(100) NOT NULL,
    yearPublishing YEAR NOT NULL,
    description TEXT NULL,
    imageName VARCHAR(35) NULL,
    shop BIT NOT NULL,
    isDeleted BIT NOT NULL DEFAULT 0,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NULL,
    startsAt DATETIME NULL,
    endsAt DATETIME NULL
);

CREATE TABLE product_category (
    productId BIGINT NOT NULL,
    categoryId BIGINT NOT NULL,
    PRIMARY KEY (productId, categoryId),
    INDEX idx_product_category_product (productId),
    INDEX idx_product_category_category (categoryId),
    CONSTRAINT fk_product_category_product FOREIGN KEY (productId) REFERENCES product(id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_product_category_category FOREIGN KEY (categoryId) REFERENCES category(id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE product_review (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NULL,
    productId BIGINT NOT NULL,
    ratingScore TINYINT NOT NULL,
    content TEXT NOT NULL,
    isShow BIT NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NULL,
    INDEX idx_product_review_user (userId),
    INDEX idx_product_review_product (productId),
    CONSTRAINT fk_product_review_user FOREIGN KEY (userId) REFERENCES user_account(id) ON DELETE SET NULL ON UPDATE NO ACTION,
    CONSTRAINT fk_product_review_product FOREIGN KEY (productId) REFERENCES product(id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE IF NOT EXISTS cart (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NULL,
    INDEX idx_cart_user (userId),
    CONSTRAINT fk_cart_user FOREIGN KEY (userId) REFERENCES user_account(id) ON DELETE CASCADE ON UPDATE NO ACTION
);

CREATE TABLE cart_item (
    id BIGINT NOT NULL AUTO_INCREMENT,
    cartId BIGINT NOT NULL,
    productId BIGINT NOT NULL,
    quantity SMALLINT NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NULL,
    PRIMARY KEY (id),
    UNIQUE KEY uq_cartId_productId (cartId, productId),
    INDEX idx_cart_item_cart (cartId),
    INDEX idx_cart_item_product (productId),
    CONSTRAINT fk_cart_item_cart FOREIGN KEY (cartId) REFERENCES cart(id) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT fk_cart_item_product FOREIGN KEY (productId) REFERENCES product(id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE wishlist_item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NOT NULL,
    productId BIGINT NOT NULL,
    createdAt DATETIME NOT NULL,
    UNIQUE KEY uq_userId_productId (userId, productId),
    INDEX idx_wishlist_item_user (userId),
    INDEX idx_wishlist_item_product (productId),
    CONSTRAINT fk_wishlist_item_user FOREIGN KEY (userId) REFERENCES user_account(id) ON DELETE CASCADE ON UPDATE NO ACTION,
    CONSTRAINT fk_wishlist_item_product FOREIGN KEY (productId) REFERENCES product(id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

DROP TABLE IF EXISTS orders;
CREATE TABLE orders (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    userId BIGINT NULL,
    status TINYINT NOT NULL,
    deliveryMethod TINYINT NOT NULL,
    deliveryPrice decimal(15, 2) NOT NULL,
    shipDiscount decimal(15, 2) NULL DEFAULT NULL,
    productDiscount decimal(15, 2) NULL DEFAULT NULL,
    totalProductPrice decimal(15, 2) NULL DEFAULT NULL,
    totalPrice decimal(15, 2) NULL DEFAULT NULL,
    shipping_address_id bigint NULL DEFAULT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NULL,
    INDEX idx_orders_user (userId),
    CONSTRAINT fk_orders_user FOREIGN KEY (userId) REFERENCES user_account(id) ON DELETE SET NULL ON UPDATE NO ACTION
    CONSTRAINT fk_orders_shipping_address FOREIGN KEY (shipping_address_id)
        REFERENCES user_shipping_addresses(id) ON DELETE SET NULL ON UPDATE NO ACTION
);

CREATE TABLE order_item (
    id BIGINT NOT NULL AUTO_INCREMENT PRIMARY KEY,
    orderId BIGINT NOT NULL,
    productId BIGINT NOT NULL,
    price FLOAT NOT NULL,
    discount FLOAT NOT NULL,
    quantity SMALLINT NOT NULL,
    createdAt DATETIME NOT NULL,
    updatedAt DATETIME NULL,
    INDEX idx_order_item_orders (orderId),
    INDEX idx_order_item_product (productId),
    CONSTRAINT fk_order_item_orders FOREIGN KEY (orderId) REFERENCES orders(id) ON DELETE NO ACTION ON UPDATE NO ACTION,
    CONSTRAINT fk_order_item_product FOREIGN KEY (productId) REFERENCES product(id) ON DELETE NO ACTION ON UPDATE NO ACTION
);

CREATE TABLE shipping_methods (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    name VARCHAR(200) NOT NULL,
    provider_type VARCHAR(50) DEFAULT 'GHN',
    estimated_days INT DEFAULT 3,
    price_per_kg DECIMAL(12,2) DEFAULT 0,
    is_express BOOLEAN DEFAULT FALSE,
    express_surcharge DECIMAL(5,2) DEFAULT 1.0,
    min_weight_kg DECIMAL(10,2) DEFAULT 0.5,
    max_weight_kg DECIMAL(10,2) DEFAULT 50.00,
    free_shipping_threshold DECIMAL(12,2) DEFAULT 0,
    support_phone VARCHAR(20),
    support_email VARCHAR(100),
    ghn_service_id BIGINT DEFAULT 0,
    ghn_token VARCHAR(500),
    ghn_shop_id VARCHAR(100),
    ghn_from_district_id VARCHAR(20),
    ghn_from_ward_code VARCHAR(20),
    api_key VARCHAR(500),
    api_secret VARCHAR(500),
    webhook_token VARCHAR(500),
    status INT DEFAULT 1 COMMENT '1: active, 0: inactive',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_provider (provider_type),
    INDEX idx_status (status)
);

CREATE TABLE shipping_zones (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    zone_name VARCHAR(100) NOT NULL,
    zone_type ENUM('INNER', 'PROVINCIAL', 'REMOTE') NOT NULL DEFAULT 'PROVINCIAL',
    base_fee DECIMAL(12,2) NOT NULL DEFAULT 0,
    price_per_kg DECIMAL(12,2) NOT NULL DEFAULT 0,
    price_per_volume DECIMAL(12,2) NOT NULL DEFAULT 0,
    estimated_days_min INT NOT NULL DEFAULT 2,
    estimated_days_max INT NOT NULL DEFAULT 5,
    description VARCHAR(255),
    status INT DEFAULT 1 COMMENT '1: active, 0: inactive',
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_zone_type (zone_type),
    INDEX idx_status (status)
);

CREATE TABLE provinces (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    province_code VARCHAR(10) NOT NULL UNIQUE,
    province_name VARCHAR(100) NOT NULL,
    province_type VARCHAR(50) DEFAULT 'tinh',
    region VARCHAR(20) DEFAULT 'MienBac' COMMENT 'MienBac, MienTrung, MienNam',
    is_metro_city BOOLEAN DEFAULT FALSE,
    shipping_zone_id BIGINT,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_province_code (province_code),
    INDEX idx_province_region (region),
    INDEX idx_province_zone (shipping_zone_id),
    CONSTRAINT fk_province_zone FOREIGN KEY (shipping_zone_id) REFERENCES shipping_zones(id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE shipping_fees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shipping_method_id BIGINT NOT NULL,
    zone_type ENUM('INNER', 'PROVINCIAL', 'REMOTE') NOT NULL DEFAULT 'PROVINCIAL',
    min_weight DECIMAL(10,2) NOT NULL DEFAULT 0,
    max_weight DECIMAL(10,2) NOT NULL DEFAULT 999,
    base_fee DECIMAL(12,2) NOT NULL DEFAULT 0,
    fee_per_kg DECIMAL(12,2) NOT NULL DEFAULT 0,
    price_per_volume DECIMAL(12,2) DEFAULT 0 COMMENT 'Phí theo thể tích (VND/m3)',
    volumetric_ratio INT DEFAULT 5000 COMMENT 'Hệ số quy đổi thể tích (cm3/kg)',
    estimated_days_min INT DEFAULT 1,
    estimated_days_max INT DEFAULT 3,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_fee_method (shipping_method_id),
    INDEX idx_fee_zone (zone_type),
    INDEX idx_fee_weight (min_weight, max_weight),
    CONSTRAINT fk_fee_method FOREIGN KEY (shipping_method_id) REFERENCES shipping_methods(id) ON DELETE CASCADE ON UPDATE CASCADE,
    UNIQUE KEY unique_method_zone_weight (shipping_method_id, zone_type, min_weight)
);

CREATE TABLE shipments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    shipping_method_id BIGINT DEFAULT 1,
    tracking_code VARCHAR(100),
    receiver_name VARCHAR(200),
    receiver_phone VARCHAR(20),
    province VARCHAR(100),
    district VARCHAR(100),
    ward VARCHAR(100),
    address_detail VARCHAR(500),
    total_weight DECIMAL(10,2) DEFAULT 0,
    total_volume DECIMAL(10,2) DEFAULT 0,
    shipping_fee DECIMAL(12,2) DEFAULT 0,
    shipping_status VARCHAR(50) DEFAULT 'WAITING_PICKUP',
    seller_note TEXT,
    customer_note TEXT,
    shipper_contact VARCHAR(100),
    estimated_delivery_date DATETIME,
    shipped_at DATETIME,
    delivered_at DATETIME,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    provider_type VARCHAR(50) DEFAULT 'GHN',
    provider_order_code VARCHAR(100),
    shipper_name VARCHAR(100),
    shipper_phone VARCHAR(20),
    shipper_avatar VARCHAR(500),
    INDEX idx_shipment_order (order_id),
    INDEX idx_shipment_tracking (tracking_code),
    INDEX idx_shipment_status (shipping_status),
    CONSTRAINT fk_shipment_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_shipment_method FOREIGN KEY (shipping_method_id) REFERENCES shipping_methods(id) ON DELETE SET NULL ON UPDATE CASCADE
);

CREATE TABLE shipment_tracking (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shipment_id BIGINT NOT NULL,
    status VARCHAR(50) NOT NULL,
    note TEXT,
    location VARCHAR(200),
    updated_by VARCHAR(100),
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_tracking_shipment (shipment_id),
    INDEX idx_tracking_status (status),
    CONSTRAINT fk_tracking_shipment FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS user_shipping_addresses;
CREATE TABLE user_shipping_addresses (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    user_id BIGINT NOT NULL,
    fullname VARCHAR(200) NOT NULL,
    phone VARCHAR(20) NOT NULL,
    province VARCHAR(100) NOT NULL,
    district VARCHAR(100) NOT NULL,
    ward VARCHAR(100) NOT NULL,
    province_id int NULL DEFAULT NULL,
    district_id int NULL DEFAULT NULL,
    ward_code varchar(20) CHARACTER SET utf8mb4 COLLATE utf8mb4_0900_ai_ci NULL DEFAULT NULL,
    address_detail VARCHAR(500),
    is_default BOOLEAN DEFAULT FALSE,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_contact_user (user_id),
    INDEX idx_contact_default (user_id, is_default),
    CONSTRAINT fk_contact_user FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE shipping_weight_fees (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shipping_method_id BIGINT NOT NULL,
    zone_type VARCHAR(50) NOT NULL DEFAULT 'PROVINCIAL',
    min_weight DECIMAL(10,2) NOT NULL DEFAULT 0.00,
    max_weight DECIMAL(10,2) NOT NULL DEFAULT 5.00,
    base_fee DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    fee_per_kg DECIMAL(12,2) NOT NULL DEFAULT 0.00,
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    INDEX idx_weight_method (shipping_method_id),
    INDEX idx_weight_zone (zone_type),
    CONSTRAINT fk_weight_method FOREIGN KEY (shipping_method_id) REFERENCES shipping_methods(id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS payments;
CREATE TABLE payments (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    order_id BIGINT NOT NULL,
    user_id BIGINT NOT NULL,
    status INT NOT NULL DEFAULT 0 COMMENT '0=Pending, 1=Success, 2=Failed',
    amount DECIMAL(15,2) NOT NULL DEFAULT 0.00,
    vnp_TxnRef VARCHAR(100),
    vnp_TransactionNo VARCHAR(100),
    vnp_ResponseCode VARCHAR(10),
    bank_code VARCHAR(50),
    pay_date DATETIME,
    created_at datetime NULL DEFAULT NULL,
    expired_at datetime NULL DEFAULT NULL,
    updated_at DATETIME DEFAULT CURRENT_TIMESTAMP ON UPDATE CURRENT_TIMESTAMP,
    is_expired tinyint(1) NOT NULL DEFAULT 0,
    INDEX idx_payment_order (order_id),
    INDEX idx_payment_user (user_id),
    INDEX idx_payment_txn_ref (vnp_txn_ref),
    CONSTRAINT fk_payment_order FOREIGN KEY (order_id) REFERENCES orders(id) ON DELETE CASCADE ON UPDATE CASCADE,
    CONSTRAINT fk_payment_user FOREIGN KEY (user_id) REFERENCES user_account(id) ON DELETE CASCADE ON UPDATE CASCADE
);

CREATE TABLE shipment_contacts (
    id BIGINT AUTO_INCREMENT PRIMARY KEY,
    shipment_id BIGINT NOT NULL,
    contact_type VARCHAR(50) NOT NULL DEFAULT 'SHIPPER',
    contact_role VARCHAR(50),
    contact_name VARCHAR(200),
    contact_phone VARCHAR(20),
    created_at DATETIME DEFAULT CURRENT_TIMESTAMP,
    INDEX idx_shipment_contact_shipment (shipment_id),
    INDEX idx_shipment_contact_type (contact_type),
    CONSTRAINT fk_shipment_contact_shipment FOREIGN KEY (shipment_id) REFERENCES shipments(id) ON DELETE CASCADE ON UPDATE CASCADE
);

DROP TABLE IF EXISTS `vouchers`;
CREATE TABLE `vouchers`  (
     `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
     `code` varchar(50) NOT NULL,
     `name` varchar(255) NOT NULL,
     `description` text NOT NULL,
     `calculation_method` tinyint NOT NULL DEFAULT 0,
     `apply_to` tinyint NOT NULL DEFAULT 0,
     `start_date` datetime NOT NULL,
     `end_date` datetime NOT NULL,
     `value` DECIMAL(15, 2) NOT NULL DEFAULT 0,
     `min_purchase` DECIMAL(15, 2) NOT NULL DEFAULT 0,
     `max_discount` DECIMAL(15, 2) NOT NULL DEFAULT 0,
     `usage_limit` int NOT NULL DEFAULT 0,
     `per_user_limit` int NOT NULL DEFAULT 1,
     `used_count` int NOT NULL DEFAULT 0,
     `is_active` tinyint(1) NOT NULL DEFAULT 1,
     UNIQUE INDEX `code`(`code`) USING BTREE
);

DROP TABLE IF EXISTS `voucher_products`;
CREATE TABLE `voucher_products`  (
     `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
     `voucher_id` bigint NOT NULL,
     `product_id` bigint NOT NULL,
     UNIQUE INDEX `uq_voucher_product`(`voucher_id`, `product_id`) USING BTREE
);

DROP TABLE IF EXISTS `voucher_categories`;
CREATE TABLE `voucher_categories`  (
   `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
   `voucher_id` bigint NOT NULL,
   `category_id` bigint NOT NULL,
   UNIQUE INDEX `uq_voucher_category`(`voucher_id`, `category_id`) USING BTREE
);

DROP TABLE IF EXISTS `voucher_usages`;
CREATE TABLE `voucher_usages`  (
   `id` bigint NOT NULL AUTO_INCREMENT PRIMARY KEY,
   `order_id` bigint NOT NULL,
   `voucher_id` bigint NOT NULL,
   `user_id` bigint NOT NULL,
   `discount_amount` decimal(15, 2) NULL DEFAULT 0.00,
   `voucher_type` tinyint NULL DEFAULT NULL,
   `applied_at` timestamp NULL DEFAULT CURRENT_TIMESTAMP,
   UNIQUE INDEX `uq_order_voucher`(`order_id`, `voucher_id`) USING BTREE,
   INDEX `fk_voucher_usages_vouchers`(`voucher_id`) USING BTREE,
   INDEX `fk_voucher_usages_users`(`user_id`) USING BTREE
);


-- insert data

-- 1. Trạng thái tài khoản
INSERT INTO user_account_status (code, name, description) VALUES
('ACTIVE', 'Hoạt động', 'Tài khoản đang hoạt động bình thường'),
('LOCKED', 'Bị khóa', 'Tài khoản tạm thời bị khóa do vi phạm hoặc bảo mật'),
('DELETED', 'Đã xóa', 'Tài khoản đã bị xóa khỏi hệ thống'),
('PENDING', 'Chờ xác thực', 'Tài khoản mới đăng ký, chưa xác thực email');

-- 2. Trạng thái xác thực Email
INSERT INTO email_verify_status (id, code, name, description) VALUES
(0, 'UNVERIFIED', 'Chưa xác thực', 'Email chưa được xác nhận'),
(1, 'VERIFIED', 'Đã xác thực', 'Email đã được xác nhận thành công');

-- 3. Cổng đăng nhập OAuth
INSERT INTO oauth_provider (code, name, description) VALUES
('GOOGLE', 'Google', 'Đăng nhập thông qua Google Account'),
('FACEBOOK', 'Facebook', 'Đăng nhập thông qua Facebook Account');

-- 4. Loại Token
INSERT INTO token_type (code, name, description) VALUES
('VERIFY_EMAIL', 'Mã xác thực email', 'Token dùng để xác nhận sở hữu email'),
('RESET_PASSWORD', 'Mã đặt lại mật khẩu', 'Token dùng để khôi phục mật khẩu');

-- 5. Trạng thái Token
INSERT INTO token_status (code, name, description) VALUES
('ACTIVE', 'Đang hiệu lực', 'Token còn trong thời gian sử dụng'),
('USED', 'Đã sử dụng', 'Token đã được thực hiện thành công'),
('EXPIRED', 'Hết hạn', 'Token đã quá thời gian hiệu lực');

-- 6. Giới tính
INSERT INTO gender (id, code, name, description) VALUES
(0, 'MALE', 'Nam', 'Giới tính Nam'),
(1, 'FEMALE', 'Nữ', 'Giới tính Nữ');

-- 7. Ngôn ngữ
INSERT INTO language_registry (code, name, description) VALUES
('vi', 'Vietnamese', 'Tiếng Việt'),
('en', 'English', 'Tiếng Anh'),
('ja', 'Japanese', 'Tiếng Nhật'),
('ko', 'Korean', 'Tiếng Hàn');

-- 8. Vai trò (Roles)
INSERT INTO role_registry (id, code, name, description, is_system) VALUES
(2, 'ADMIN', 'Quản lý', 'Quản lý trong phạm vi chi nhánh', 0),
(3, 'STAFF', 'Nhân viên', 'Người dùng vận hành hệ thống', 0),
(4, 'CUSTOMER', 'Khách hàng', 'Người mua sắm trực tuyến', 0);

-- 9. Quyền hạn (Permissions)
INSERT INTO permission_registry (code, name, description, module, is_system) VALUES
-- Role Module
('role.view', 'Xem role', 'Xem danh sách role', 'ROLE', 1),
('role.create', 'Tạo role', 'Tạo role mới', 'ROLE', 1),
('role.update', 'Cập nhật role', 'Cập nhật thông tin role', 'ROLE', 1),
('role.delete', 'Xóa role', 'Xóa role', 'ROLE', 1),
('role.assign_permission', 'Gán permission', 'Gán permission cho role', 'ROLE', 1),

-- Permission Module
('permission.view', 'Xem permission', 'Xem danh sách permission', 'PERMISSION', 1),
('permission.create', 'Tạo permission', 'Tạo permission mới', 'PERMISSION', 1),
('permission.update', 'Cập nhật permission', 'Cập nhật thông tin permission', 'PERMISSION', 1),
('permission.delete', 'Xóa permission', 'Xóa permission', 'PERMISSION', 1),

-- Category Module
('category.view', 'Xem thể loại', 'Xem danh sách thể loại', 'CATEGORY', 1),
('category.create', 'Tạo thể loại', 'Tạo thể loại mới', 'CATEGORY', 1),
('category.update', 'Cập nhật thể loại', 'Cập nhật thông tin thể loại', 'CATEGORY', 1),
('category.delete', 'Xóa thể loại', 'Xóa thể loại', 'CATEGORY', 1),

-- User Module
('user.view', 'Xem user', 'Xem thông tin user', 'USER', 1),
('user.detail', 'Xem chi tiết user', 'Xem chi tiết một user', 'USER', 1),
('user.create', 'Tạo user', 'Tạo user mới', 'USER', 1),
('user.update', 'Cập nhật user', 'Cập nhật thông tin user', 'USER', 1),
('user.delete', 'Xóa user', 'Xóa user', 'USER', 1),
('user.assign_role', 'Gán role', 'Gán role cho user', 'USER', 1),

-- Product Module
('product.view', 'Xem sản phẩm', 'Xem thông tin sản phẩm', 'PRODUCT', 1),
('product.create', 'Tạo sản phẩm', 'Tạo sản phẩm mới', 'PRODUCT', 1),
('product.update', 'Cập nhật sản phẩm', 'Cập nhật thông tin sản phẩm', 'PRODUCT', 1),
('product.delete', 'Xóa sản phẩm', 'Xóa sản phẩm', 'PRODUCT', 1),

-- Order Module
('order.view', 'Xem đơn hàng', 'Xem thông tin đơn hàng', 'ORDER', 1),
('order.create', 'Tạo đơn hàng', 'Tạo đơn hàng mới', 'ORDER', 1),
('order.update', 'Cập nhật đơn hàng', 'Cập nhật thông tin đơn hàng', 'ORDER', 1),
('order.delete', 'Xóa đơn hàng', 'Xóa đơn hàng', 'ORDER', 1),
('order.view_all', 'Xem tất cả đơn hàng', 'Xem đơn hàng của tất cả user', 'ORDER', 1),

-- Cart Module
('cart.view', 'Xem giỏ hàng', 'Xem giỏ hàng', 'CART', 1),
('cart.manage', 'Quản lý giỏ hàng', 'Quản lý giỏ hàng', 'CART', 1),

-- Review Module
('review.view', 'Xem review', 'Xem review sản phẩm', 'REVIEW', 1),
('review.create', 'Tạo review', 'Tạo review mới', 'REVIEW', 1),
('review.update', 'Cập nhật review', 'Cập nhật review', 'REVIEW', 1),
('review.delete', 'Xóa review', 'Xóa review', 'REVIEW', 1),
('review.moderate', 'Moderate review', 'Duyệt/xóa review', 'REVIEW', 1),

-- Voucher Module
('voucher.view', 'Xem voucher', 'Xem thông tin voucher', 'VOUCHER', 1),
('voucher.create', 'Tạo voucher', 'Tạo voucher mới', 'VOUCHER', 1),
('voucher.update', 'Cập nhật voucher', 'Cập nhật thông tin voucher', 'VOUCHER', 1),
('voucher.delete', 'Xóa voucher', 'Xóa voucher', 'VOUCHER', 1),

-- Report Module
('report.view', 'Xem báo cáo', 'Xem báo cáo thống kê', 'REPORT', 1),
('report.export', 'Xuất báo cáo', 'Xuất báo cáo', 'REPORT', 1),

-- Shipment Module
('shipment.view', 'Xem vận đơn', 'Xem thông tin vận đơn', 'SHIPMENT', 1),
('shipment.create', 'Tạo vận đơn', 'Tạo vận đơn mới', 'SHIPMENT', 1),
('shipment.update', 'Cập nhật vận đơn', 'Cập nhật thông tin vận đơn', 'SHIPMENT', 1),
('shipment.delete', 'Xóa vận đơn', 'Xóa vận đơn', 'SHIPMENT', 1),

-- Shipping Config Module
('shipping_config.view', 'Xem cấu hình ship', 'Xem cấu hình vận chuyển', 'SHIPPING_CONFIG', 1),
('shipping_config.create', 'Tạo cấu hình ship', 'Tạo cấu hình vận chuyển', 'SHIPPING_CONFIG', 1),
('shipping_config.update', 'Cập nhật cấu hình ship', 'Cập nhật cấu hình vận chuyển', 'SHIPPING_CONFIG', 1),
('shipping_config.delete', 'Xóa cấu hình ship', 'Xóa cấu hình vận chuyển', 'SHIPPING_CONFIG', 1),

-- Settings Module
('settings.view', 'Xem cài đặt', 'Xem cài đặt hệ thống', 'SETTINGS', 1),
('settings.update', 'Cập nhật cài đặt', 'Cập nhật cài đặt hệ thống', 'SETTINGS', 1);

-- 10. Gán quyền cho Role (Role Permissions)
-- ADMIN (Quản lý User, Role, Permission, Product, Order, Report, Category, Review, Voucher, Shipment, ShippingConfig)
INSERT INTO role_permission_assignment (role_id, permission_id)
SELECT 2, id FROM permission_registry WHERE module IN ('USER', 'ROLE', 'PERMISSION', 'PRODUCT', 'ORDER', 'REPORT', 'CATEGORY', 'REVIEW', 'VOUCHER', 'SHIPMENT', 'SHIPPING_CONFIG');

-- STAFF (Quản lý Product, Category, Order, Review)
INSERT INTO role_permission_assignment (role_id, permission_id)
SELECT 3, id FROM permission_registry WHERE module IN ('PRODUCT', 'CATEGORY', 'ORDER', 'REVIEW');

-- CUSTOMER
INSERT INTO role_permission_assignment (role_id, permission_id)
SELECT 4, id FROM permission_registry WHERE code IN ('order.create');

-- 15. USER ACCOUNTS (Tài khoản người dùng)
-- Thứ tự insert quan trọng: 
-- 1. user_account (bảng cha) -> 2. user_profile -> 3. user_local -> 4. user_role_registry

-- BƯỚC 1: Tạo tài khoản gốc trong user_account (TẤT CẢ users trước)
INSERT INTO user_account (id, status_id) VALUES 
(1, 1),
(2, 1), (3, 1), (4, 1), (5, 1), (6, 1);

-- BƯỚC 2: Thêm thông tin cá nhân vào user_profile
INSERT INTO user_profile (user_id, fullname, phone_number, email, gender_id, preferred_language_id) VALUES
(1, 'SYSTEM', NULL,'system@webbookshop.com', 0, 1),
(2, 'Dunn Mcpherson', '0989894900', 'dunnmcpherson@recrisys.com', 0, 1),
(3, 'Foreman Carter', '0993194154', 'foremancarter@recrisys.com', 0, 1),
(4, 'Felecia Cabrera', '0930174351', 'feleciacabrera@recrisys.com', 1, 1),
(5, 'Juliette Mcdowell', '0911925643', 'juliettemcdowell@recrisys.com', 1, 1),
(6, 'Vilma Spencer', '0987509391', 'vilmaspencer@recrisys.com', 1, 1);

-- BƯỚC 3: Thêm thông tin đăng nhập vào user_local
-- Password hash: $2a$12$UllaLd399u9rzzFCvwLK8Of5vL1l9MxyC1OCMR1cyfCd4jxoyBqf2
INSERT INTO user_local (user_id, username, password_hash, email, email_verify_status_id) VALUES
(2, 'user1', '$2a$12$UllaLd399u9rzzFCvwLK8Of5vL1l9MxyC1OCMR1cyfCd4jxoyBqf2', 'dunnmcpherson@recrisys.com', 1),
(3, 'user2', '$2a$12$UllaLd399u9rzzFCvwLK8Of5vL1l9MxyC1OCMR1cyfCd4jxoyBqf2', 'foremancarter@recrisys.com', 1),
(4, 'user3', '$2a$12$UllaLd399u9rzzFCvwLK8Of5vL1l9MxyC1OCMR1cyfCd4jxoyBqf2', 'feleciacabrera@recrisys.com', 1),
(5, 'user4', '$2a$12$UllaLd399u9rzzFCvwLK8Of5vL1l9MxyC1OCMR1cyfCd4jxoyBqf2', 'juliettemcdowell@recrisys.com', 1),
(6, 'user5', '$2a$12$UllaLd399u9rzzFCvwLK8Of5vL1l9MxyC1OCMR1cyfCd4jxoyBqf2', 'vilmaspencer@recrisys.com', 1);

-- BƯỚC 4: Phân quyền (Role Assignment) - PHẢI SAU user_account
INSERT INTO user_role_registry (user_id, role_id) VALUES
(2, 2), -- user1: ADMIN
(3, 3), -- user2: STAFF
(4, 3), -- user3: STAFF
(5, 4), -- user4: CUSTOMER
(6, 4); -- user5: CUSTOMER

-- =============================================
-- PART 8: SEED DATA - Categories
-- =============================================

INSERT INTO category (name, description, imageName, isDeleted, createdAt) VALUES
('Sách giáo khoa', 'Sách giáo khoa các cấp', 'sach-giao-khoa.jpg', 0, NOW()),
('Sách khoa học', 'Sách khoa học các loại', 'sach-khoa-hoc.png', 0, NOW()),
('Truyện tranh', 'Truyện tranh manga, comic', 'truyen-tranh.png', 0, NOW()),
('Tiểu thuyết', 'Tiểu thuyết VN & nước ngoài', 'tieu-thuyet.png', 0, NOW()),
('Truyện ngắn', 'Truyện ngắn hay', 'truyen-ngan.png', 0, NOW()),
('Truyện dài', 'Truyện dài nổi tiếng', 'truyen-dai.png', 0, NOW()),
('Sách giáo trình', 'Sách giáo trình đại học', 'sach-giao-trinh.png', 0, NOW()),
('Báo in', 'Các loại báo in', 'bao-in.png', 0, NOW()),
('Tạp chí', 'Tạp chí các loại', 'tap-chi.png', 0, NOW()),
('Tập san', 'Tập san trường học', 'tap-san.png', 0, NOW()),
('Sách nấu ăn', 'Sách hướng dẫn nấu ăn', 'nau-an.png', 0, NOW()),
('Sách kỹ thuật', 'Sách kỹ thuật các ngành', 'sach-ky-thuat.png', 0, NOW()),
('Sách nông nghiệp', 'Sách nông nghiệp, trồng trọt', 'sach-nong-nghiep.png', 0, NOW()),
('Sách thiếu nhi', 'Sách thiếu nhi, truyện tranh trẻ em', 'sach-thieu-nhi.png', 0, NOW()),
('Sách kỹ năng sống', 'Sách phát triển bản thân', 'sach-ky-nang-song.png', 0, NOW());

-- =============================================
-- PART 9: SEED DATA - Products
-- =============================================

INSERT INTO product (name, price, discount, quantity, totalBuy, author, pages, publisher, yearPublishing, description, imageName, shop, createdAt, updatedAt, startsAt, endsAt) VALUES
('Sách Toyletry', 466183, 0, 86, 86, 'Stafford Hayden', 250, 'NXB Giáo dục', 2013, 'Consequat cupidatat magna nostrud ullamco non commodo esse. Veniam anim ipsum duis cillum cillum exercitation deserunt irure sint eiusmod.', 'temp-10075522682831764585.jpg', 0, '2021-03-23 08:22:50', NULL, NULL, NULL),
('Sách Sultrax', 26228, 0, 23, 60, 'Diane Nguyen', 305, 'NXB Đại học Sư phạm TP.HCM', 2011, 'Ipsum consequat reprehenderit amet ullamco dolore consectetur non eiusmod dolor irure sit.', 'temp-17624438115898823949.jpg', 1, '2021-12-19 15:03:05', NULL, NULL, NULL),
('Sách Medcom', 294114, 20, 68, 116, 'Byrd Collier', 457, 'NXB Đại học Sư phạm TP.HCM', 2003, 'Cupidatat ipsum ea laborum nisi veniam nulla dolor labore excepteur ad eu.', 'temp-6352099207348952932.jpg', 1, '2021-07-31 10:44:48', NULL, NULL, NULL),
('Sách Radiantix', 61888, 20, 46, 195, 'Dodson Wilkinson', 426, 'NXB Đại học Sư phạm TP.HCM', 2019, 'Qui magna ex aute deserunt aliquip mollit labore ad.', 'temp-13862094760385571107.jpg', 0, '2021-07-26 17:44:04', NULL, NULL, NULL),
('Sách Prosely', 195442, 0, 52, 178, 'Horne Oneill', 270, 'NXB Giáo dục', 2000, 'Sit cillum ipsum cillum commodo dolor ipsum aliquip id exercitation non proident qui.', 'temp-17624438115898823949.jpg', 0, '2021-03-04 01:10:28', NULL, NULL, NULL),
('Sách Xth', 462713, 0, 13, 212, 'Karin Jackson', 186, 'NXB Đại học Sư phạm TP.HCM', 2008, 'Ut pariatur culpa sint aliqua culpa ullamco laboris duis dolore mollit dolor cillum.', 'temp-16741118072528735594.jpg', 1, '2021-06-18 05:55:06', NULL, NULL, NULL),
('Sách Liquicom', 488021, 0, 65, 95, 'Johnson Wilkins', 260, 'NXB Đại học Huế', 2003, 'Nostrud minim nostrud duis nostrud cupidatat reprehenderit nostrud dolor amet esse.', 'temp-6243426685116508297.jpg', 1, '2021-11-01 20:47:34', NULL, NULL, NULL),
('Sách Supremia', 478294, 0, 60, 316, 'Ayala Rich', 359, 'NXB Đại học Sư phạm Hà Nội', 2006, 'Reprehenderit quis exercitation reprehenderit velit et magna.', 'temp-10075522682831764585.jpg', 0, '2021-08-27 02:56:32', NULL, NULL, NULL),
('Sách Cyclonica', 96299, 20, 45, 14, 'Flynn Sanford', 420, 'NXB Đại học Sư phạm Hà Nội', 2013, 'Laborum elit laborum do esse ut id sunt voluptate ut minim sint mollit irure.', 'temp-16741118072528735594.jpg', 1, '2021-08-07 21:11:03', NULL, NULL, NULL),
('Sách Envire', 397768, 20, 65, 347, 'Gilda Harris', 321, 'NXB Đại học Sư phạm TP.HCM', 2020, 'Non veniam dolore esse aute officia in nostrud id sint ipsum incididunt qui incididunt.', 'temp-13862094760385571107.jpg', 1, '2021-06-07 23:23:46', NULL, NULL, NULL),
('Sách Insuresys', 366716, 0, 50, 305, 'Lolita Cochran', 89, 'NXB Đại học Quốc gia Hà Nội', 2007, 'Magna enim veniam consequat minim.', 'temp-10075522682831764585.jpg', 1, '2021-12-10 13:59:07', NULL, NULL, NULL),
('Sách Exostream', 51700, 0, 85, 62, 'Christian Alvarado', 435, 'NXB Đại học Sư phạm Hà Nội', 2018, 'In laborum tempor cupidatat aliqua in pariatur tempor voluptate velit deserunt.', 'temp-17624438115898823949.jpg', 0, '2021-12-14 19:28:20', NULL, NULL, NULL),
('Sách Fuelton', 122139, 0, 88, 461, 'Gentry Chapman', 18, 'NXB Đại học Quốc gia Hà Nội', 2005, 'Officia anim mollit culpa ea Lorem dolore commodo incididunt eu pariatur occaecat.', 'temp-16741118072528735594.jpg', 0, '2021-06-16 08:20:52', NULL, NULL, NULL),
('Sách Xinware', 247115, 20, 80, 373, 'Sheree Lawson', 124, 'NXB Đại học Sư phạm TP.HCM', 2007, 'Deserunt labore fugiat velit proident proident ex irure incididunt sint anim consequat ipsum dolore dolore.', 'temp-16741118072528735594.jpg', 1, '2021-07-31 15:57:52', NULL, NULL, NULL),
('Sách Atomica', 205300, 20, 16, 388, 'Shirley Sawyer', 414, 'NXB Đại học Quốc gia Hà Nội', 2006, 'Eu id sint sunt eiusmod ad magna dolore sint.', 'temp-12235989262213754276.jpg', 0, '2021-04-19 14:39:51', NULL, NULL, NULL),
('Sách Quadeebo', 280225, 0, 27, 16, 'Marcia Horne', 486, 'NXB Đại học Sư phạm Hà Nội', 2006, 'Ut exercitation ipsum amet commodo labore duis est excepteur nostrud velit et ex magna.', 'temp-10075522682831764585.jpg', 1, '2021-12-17 09:38:24', NULL, NULL, NULL),
('Sách Nexgene', 90044, 20, 96, 165, 'Dixie Middleton', 43, 'NXB Đại học Sư phạm TP.HCM', 2009, 'Veniam qui id occaecat exercitation aliquip occaecat incididunt quis.', 'temp-10075522682831764585.jpg', 1, '2022-01-02 23:13:27', NULL, NULL, NULL),
('Sách Gaptec', 281613, 0, 93, 279, 'Dorothea Gonzales', 409, 'NXB Đại học Huế', 2020, 'Ea deserunt esse officia consequat ex nisi laborum laborum.', 'temp-13862094760385571107.jpg', 0, '2021-07-04 12:11:08', NULL, NULL, NULL),
('Sách Dadabase', 450859, 20, 90, 474, 'Moore Kim', 113, 'NXB Đại học Sư phạm TP.HCM', 2003, 'Amet laborum do consequat officia cupidatat in nulla occaecat ut aute magna aute eu exercitation.', 'temp-7329036107498680084.jpg', 0, '2021-10-25 09:48:48', NULL, NULL, NULL),
('Sách Digial', 187331, 20, 75, 339, 'Laverne Obrien', 194, 'NXB Đại học Huế', 2011, 'Ipsum est amet eiusmod nostrud laborum deserunt consequat qui.', 'temp-3015888053636485125.jpg', 1, '2021-11-12 00:28:22', NULL, NULL, NULL),
('Sách Endicil', 396673, 20, 85, 263, 'Hughes Hutchinson', 62, 'NXB Đại học Sư phạm Hà Nội', 2013, 'Cillum non quis sit deserunt dolore laboris enim laboris fugiat reprehenderit id aliqua.', 'temp-12235989262213754276.jpg', 0, '2021-03-26 22:11:02', NULL, NULL, NULL),
('Sách Inquala', 116529, 0, 11, 367, 'Hilda Crane', 238, 'NXB Đại học Quốc gia Hà Nội', 1998, 'Esse ipsum minim voluptate consectetur exercitation dolor.', 'temp-12235989262213754276.jpg', 1, '2022-01-24 08:00:39', NULL, NULL, NULL),
('Sách Tubalum', 372359, 0, 71, 129, 'Erma Shannon', 15, 'NXB Đại học Sư phạm TP.HCM', 2016, 'Veniam cillum do laboris ipsum incididunt consequat non.', 'temp-6243426685116508297.jpg', 0, '2021-07-15 20:01:58', NULL, NULL, NULL),
('Sách Songlines', 293705, 20, 83, 267, 'Paula Duncan', 284, 'NXB Đại học Huế', 2017, 'Nisi consequat do adipisicing nostrud elit aliqua nostrud sunt laborum reprehenderit culpa labore in ea.', 'temp-13862094760385571107.jpg', 0, '2021-09-09 05:29:49', NULL, NULL, NULL),
('Sách Evidends', 122006, 0, 62, 309, 'Briggs Burke', 134, 'NXB Đại học Sư phạm TP.HCM', 2011, 'Tempor laborum duis aute enim eiusmod ipsum et labore sunt qui labore.', 'temp-13064240004351430671.jpg', 1, '2021-12-11 02:13:04', NULL, NULL, NULL),
('Sách Buzzworks', 131272, 20, 47, 418, 'Wood Franks', 147, 'NXB Đại học Sư phạm Hà Nội', 1996, 'Consequat culpa ad excepteur in ut officia do.', 'temp-16741118072528735594.jpg', 0, '2021-02-02 00:42:02', NULL, NULL, NULL),
('Sách Zilla', 45254, 0, 40, 41, 'Imogene Horton', 67, 'NXB Đại học Huế', 2008, 'Voluptate est reprehenderit nostrud deserunt qui ullamco tempor quis officia pariatur ipsum.', 'temp-8262627340495498759.jpg', 0, '2021-04-26 11:20:50', NULL, NULL, NULL),
('Sách Fangold', 36717, 20, 19, 407, 'Frankie Mccarthy', 85, 'NXB Giáo dục', 2015, 'Incididunt elit consectetur magna nisi.', 'temp-18128511448457962576.jpg', 0, '2021-03-08 07:28:18', NULL, NULL, NULL),
('Sách Immunics', 260516, 0, 87, 176, 'Wheeler Carpenter', 49, 'NXB Đại học Sư phạm TP.HCM', 2012, 'Deserunt excepteur fugiat nisi adipisicing amet esse duis pariatur dolor deserunt dolor mollit.', 'temp-14438611480196141526.jpg', 0, '2021-10-14 22:48:40', NULL, NULL, NULL),
('Sách Zillacom', 318403, 0, 12, 71, 'Natalia Wilder', 271, 'NXB Đại học Sư phạm TP.HCM', 2007, 'Id sunt incididunt eiusmod dolore pariatur.', 'temp-12235989262213754276.jpg', 0, '2021-12-19 02:05:00', NULL, NULL, NULL),
('Sách Katakana', 231331, 20, 71, 336, 'Linda Stevens', 285, 'NXB Đại học Sư phạm TP.HCM', 2001, 'Elit ad nulla officia fugiat nisi et ex nostrud elit ut.', 'temp-13862094760385571107.jpg', 1, '2022-01-01 23:10:09', NULL, NULL, NULL),
('Sách Artiq', 313493, 0, 49, 48, 'Tucker Sargent', 341, 'NXB Đại học Sư phạm TP.HCM', 1997, 'Sit sint sit adipisicing aliqua fugiat eiusmod amet sunt cupidatat laboris cillum.', 'temp-6352099207348952932.jpg', 0, '2021-12-08 02:56:41', NULL, NULL, NULL),
('Sách Omnigog', 155599, 20, 19, 316, 'Mullins Thomas', 255, 'NXB Đại học Huế', 2009, 'Cillum amet culpa Lorem elit.', 'temp-8262627340495498759.jpg', 1, '2021-11-06 02:48:40', NULL, NULL, NULL),
('Sách Elita', 298548, 0, 65, 439, 'Amber Spence', 262, 'NXB Đại học Huế', 1998, 'Quis fugiat aliqua sit laborum proident ea.', 'temp-18128511448457962576.jpg', 1, '2021-03-24 23:12:25', NULL, NULL, NULL),
('Sách Hopeli', 461236, 20, 51, 350, 'Wooten Johnston', 355, 'NXB Đại học Huế', 2018, 'Cupidatat fugiat do minim qui dolor deserunt anim.', 'temp-12235989262213754276.jpg', 1, '2021-10-05 08:08:19', NULL, NULL, NULL),
('Sách Vendblend', 495547, 20, 48, 22, 'Mayra Moon', 364, 'NXB Đại học Sư phạm Hà Nội', 1995, 'Aute eiusmod deserunt ipsum eu.', 'temp-6352099207348952932.jpg', 1, '2022-01-08 02:42:25', NULL, NULL, NULL),
('Sách Zensure', 288319, 20, 16, 440, 'Hickman Moses', 62, 'NXB Đại học Sư phạm TP.HCM', 2007, 'Minim cillum pariatur ea voluptate laborum dolore duis.', 'temp-8476700387786158058.jpg', 0, '2021-05-11 01:50:10', NULL, NULL, NULL),
('Sách Quilk', 183383, 20, 81, 14, 'Elnora Pearson', 326, 'NXB Đại học Huế', 2013, 'Laborum sunt laborum reprehenderit cupidatat esse.', 'temp-10075522682831764585.jpg', 1, '2021-11-05 20:54:31', NULL, NULL, NULL),
('Sách Schoolio', 176598, 0, 48, 208, 'Isabella Mcbride', 401, 'NXB Đại học Huế', 2013, 'Consequat amet deserunt qui adipisicing amet id laboris magna reprehenderit ea nisi amet magna.', 'temp-10075522682831764585.jpg', 1, '2021-12-27 15:36:00', NULL, NULL, NULL),
('Sách Neocent', 228132, 0, 91, 66, 'Tanya Shaw', 57, 'NXB Đại học Quốc gia Hà Nội', 2000, 'Ea adipisicing ullamco cupidatat do elit nulla officia.', 'temp-7329036107498680084.jpg', 1, '2021-06-13 14:22:51', NULL, NULL, NULL),
('Sách Isotrack', 387298, 20, 78, 202, 'Noemi Norris', 179, 'NXB Đại học Quốc gia Hà Nội', 2013, 'Eiusmod in dolor excepteur culpa ipsum eu sint culpa exercitation est qui.', 'temp-7329036107498680084.jpg', 0, '2022-01-03 15:33:27', NULL, NULL, NULL),
('Sách Bedlam', 274131, 20, 28, 391, 'Josefa Allison', 31, 'NXB Đại học Huế', 2000, 'Culpa velit pariatur proident exercitation commodo labore exercitation anim dolore qui fugiat.', 'temp-14438611480196141526.jpg', 1, '2021-07-21 01:40:24', NULL, NULL, NULL),
('Sách Eternis', 68882, 20, 45, 380, 'Betty Marsh', 63, 'NXB Đại học Sư phạm TP.HCM', 2020, 'Non sint laboris anim elit ipsum.', 'temp-16741118072528735594.jpg', 0, '2021-06-09 21:58:26', NULL, NULL, NULL),
('Sách Manufact', 71997, 20, 36, 343, 'Whitney Underwood', 293, 'NXB Đại học Sư phạm Hà Nội', 2013, 'Excepteur nisi minim in eu ad enim duis cillum sit tempor qui irure aliqua.', 'temp-3015888053636485125.jpg', 1, '2021-03-30 01:29:57', NULL, NULL, NULL),
('Sách Golistic', 252810, 0, 99, 358, 'Valencia Kent', 166, 'NXB Giáo dục', 2006, 'Laboris est ea incididunt cupidatat.', 'temp-13862094760385571107.jpg', 0, '2021-06-07 07:01:21', NULL, NULL, NULL),
('Sách Dognosis', 384754, 20, 58, 484, 'Alisa Waters', 129, 'NXB Đại học Sư phạm TP.HCM', 1997, 'Do qui cillum sint in aute tempor.', 'temp-8262627340495498759.jpg', 0, '2022-01-13 20:55:09', NULL, NULL, NULL),
('Sách Magneato', 62859, 20, 76, 213, 'Bell Anthony', 439, 'NXB Đại học Sư phạm TP.HCM', 2002, 'Commodo nisi sint nulla et duis nostrud sunt nulla.', 'temp-7329036107498680084.jpg', 0, '2021-09-01 20:00:43', NULL, NULL, NULL),
('Sách Comdom', 320796, 0, 41, 221, 'Bowen Blackburn', 127, 'NXB Giáo dục', 1999, 'Adipisicing laborum fugiat quis et.', 'temp-3984373128647845854.jpg', 1, '2021-02-18 02:21:04', NULL, NULL, NULL),
('Sách Cognicode', 397947, 0, 75, 225, 'Strickland Flores', 166, 'NXB Đại học Huế', 2016, 'Dolor exercitation incididunt ea voluptate Lorem irure proident quis ullamco occaecat adipisicing.', 'temp-13064240004351430671.jpg', 0, '2021-07-30 09:38:48', NULL, NULL, NULL),
('Sách Lyrichord', 118039, 0, 34, 392, 'Mcconnell Case', 284, 'NXB Đại học Sư phạm Hà Nội', 1996, 'Ea nostrud enim est eu pariatur velit laborum commodo.', 'temp-16741118072528735594.jpg', 0, '2021-05-15 04:03:30', NULL, NULL, NULL),
('Sách Comtext', 134415, 20, 72, 480, 'Kristine Weiss', 95, 'NXB Giáo dục', 2021, 'Deserunt exercitation in laborum velit non commodo adipisicing excepteur culpa.', 'temp-13862094760385571107.jpg', 1, '2021-09-13 19:22:19', NULL, NULL, NULL),
('Sách Myopium', 349335, 20, 57, 482, 'Mooney Freeman', 71, 'NXB Giáo dục', 2008, 'Eu aliqua exercitation laborum irure ea id officia.', 'temp-7329036107498680084.jpg', 1, '2021-07-08 12:49:12', NULL, NULL, NULL),
('Sách Bittor', 388247, 20, 76, 270, 'Foley Payne', 486, 'NXB Giáo dục', 2016, 'Quis fugiat eiusmod deserunt duis eu qui aliqua magna Lorem magna et culpa.', 'temp-18128511448457962576.jpg', 0, '2021-12-14 03:59:32', NULL, NULL, NULL),
('Sách Chillium', 144536, 20, 81, 417, 'Wynn Poole', 73, 'NXB Đại học Quốc gia Hà Nội', 1998, 'Eiusmod pariatur aliquip aliqua duis Lorem nisi duis ullamco.', 'temp-12235989262213754276.jpg', 0, '2021-08-14 15:42:55', NULL, NULL, NULL),
('Sách Ronelon', 201895, 20, 32, 451, 'Frederick Gilliam', 51, 'NXB Đại học Quốc gia Hà Nội', 2020, 'Ex commodo consectetur proident elit tempor in minim nostrud elit nostrud.', 'temp-16741118072528735594.jpg', 0, '2021-06-09 02:42:51', NULL, NULL, NULL),
('Sách Roughies', 124514, 0, 31, 196, 'Pat Atkinson', 50, 'NXB Giáo dục', 2004, 'Aute aliqua dolore qui quis veniam pariatur.', 'temp-12235989262213754276.jpg', 1, '2021-05-12 13:14:00', NULL, NULL, NULL),
('Sách Printspan', 153427, 0, 32, 234, 'Bartlett Hampton', 122, 'NXB Đại học Huế', 2013, 'Aliqua reprehenderit consectetur incididunt elit ullamco veniam tempor tempor incididunt labore elit.', 'temp-18128511448457962576.jpg', 0, '2021-12-28 05:25:08', NULL, NULL, NULL),
('Sách Quantalia', 101367, 20, 90, 305, 'Cherry Hopper', 409, 'NXB Giáo dục', 2019, 'Ex sunt elit minim commodo minim reprehenderit consectetur ea dolor.', 'temp-14438611480196141526.jpg', 0, '2021-06-24 07:23:29', NULL, NULL, NULL),
('Sách Fortean', 243231, 0, 94, 444, 'Tabitha Shepherd', 357, 'NXB Đại học Quốc gia Hà Nội', 2008, 'Amet et elit labore fugiat magna ullamco proident ut excepteur ea elit commodo.', 'temp-16741118072528735594.jpg', 0, '2021-09-21 11:22:31', NULL, NULL, NULL),
('Sách Kengen', 262167, 20, 15, 473, 'Cooke Barber', 79, 'NXB Đại học Quốc gia Hà Nội', 1997, 'Proident sint quis culpa do id eiusmod mollit in culpa ut anim.', 'temp-3984373128647845854.jpg', 0, '2021-11-22 16:02:54', NULL, NULL, NULL),
('Sách Dyno', 422493, 20, 50, 32, 'Laura Simpson', 354, 'NXB Đại học Quốc gia Hà Nội', 1997, 'Proident pariatur ullamco elit aliquip mollit magna sunt ad eiusmod.', 'temp-13064240004351430671.jpg', 1, '2021-09-16 06:34:31', NULL, NULL, NULL),
('Sách Geekmosis', 396421, 0, 96, 210, 'Gayle Castillo', 354, 'NXB Đại học Huế', 2006, 'Anim proident et ex ut irure cillum magna non incididunt ipsum dolor.', 'temp-10075522682831764585.jpg', 1, '2021-02-20 05:53:34', NULL, NULL, NULL),
('Sách Earbang', 162841, 20, 21, 47, 'Elliott Pace', 150, 'NXB Đại học Sư phạm TP.HCM', 2017, 'Amet veniam voluptate consequat adipisicing officia et fugiat cillum ut ullamco elit ea do sit.', 'temp-8262627340495498759.jpg', 1, '2021-09-02 07:50:35', NULL, NULL, NULL),
('Sách Rodeology', 473815, 0, 10, 370, 'Chelsea Wong', 396, 'NXB Giáo dục', 2009, 'Ipsum magna laborum do officia non quis duis sunt est ipsum.', 'temp-14438611480196141526.jpg', 0, '2021-07-18 03:36:32', NULL, NULL, NULL),
('Sách Locazone', 466018, 0, 58, 196, 'Katina Terrell', 294, 'NXB Đại học Quốc gia Hà Nội', 1996, 'Deserunt cillum culpa in anim voluptate quis quis laborum in non.', 'temp-6352099207348952932.jpg', 1, '2021-12-08 23:10:27', NULL, NULL, NULL),
('Sách Zentime', 8106, 20, 25, 162, 'Alfreda Randolph', 161, 'NXB Đại học Sư phạm Hà Nội', 2021, 'Quis aliquip minim irure nisi.', 'temp-13862094760385571107.jpg', 0, '2021-12-13 11:25:39', NULL, NULL, NULL),
('Sách Xelegyl', 117223, 0, 40, 270, 'Helene Campbell', 340, 'NXB Đại học Huế', 2013, 'Veniam et et fugiat cillum veniam consectetur dolor laborum ipsum aliquip in commodo.', 'temp-8476700387786158058.jpg', 0, '2021-09-11 01:01:32', NULL, NULL, NULL),
('Sách Oatfarm', 299156, 0, 43, 90, 'Joyner Jarvis', 10, 'NXB Đại học Huế', 2017, 'Dolore aliquip ullamco id ut cupidatat exercitation nulla non veniam adipisicing do elit id.', 'temp-3984373128647845854.jpg', 0, '2021-06-18 17:25:37', NULL, NULL, NULL),
('Sách Dymi', 382118, 20, 50, 381, 'Deirdre Hatfield', 442, 'NXB Giáo dục', 2020, 'Eiusmod anim exercitation aliquip et cupidatat id consectetur exercitation nostrud enim irure mollit non.', 'temp-13064240004351430671.jpg', 0, '2021-02-12 15:13:38', NULL, NULL, NULL),
('Sách Injoy', 444695, 20, 71, 176, 'Patty Caldwell', 79, 'NXB Đại học Quốc gia Hà Nội', 2001, 'Quis aute occaecat fugiat mollit eu est sunt eu ipsum do excepteur culpa.', 'temp-13862094760385571107.jpg', 0, '2021-11-16 22:29:49', NULL, NULL, NULL),
('Sách Globoil', 375062, 0, 25, 59, 'Steele Henson', 362, 'NXB Đại học Sư phạm Hà Nội', 2016, 'Eiusmod do laboris magna ex eiusmod laboris nostrud aliqua anim.', 'temp-16741118072528735594.jpg', 1, '2021-12-22 12:50:21', NULL, NULL, NULL),
('Sách Daido', 170140, 20, 47, 398, 'Dotson Lloyd', 136, 'NXB Đại học Quốc gia Hà Nội', 2012, 'Dolore ipsum occaecat in magna elit Lorem est.', 'temp-16741118072528735594.jpg', 0, '2021-06-25 04:03:26', NULL, NULL, NULL),
('Sách Coash', 443683, 20, 59, 367, 'Lillie Hurst', 67, 'NXB Đại học Sư phạm TP.HCM', 2008, 'Veniam duis cupidatat adipisicing sunt sit.', 'temp-3984373128647845854.jpg', 0, '2021-12-13 00:50:17', NULL, NULL, NULL),
('Sách Automon', 109059, 0, 70, 60, 'Melissa Hayes', 301, 'NXB Đại học Sư phạm Hà Nội', 2009, 'Dolor ex commodo Lorem fugiat.', 'temp-10075522682831764585.jpg', 0, '2021-05-22 05:34:50', NULL, NULL, NULL),
('Sách Genmom', 118544, 0, 79, 119, 'Delores Johnson', 418, 'NXB Giáo dục', 2007, 'Commodo eiusmod est eu eu.', 'temp-18128511448457962576.jpg', 0, '2021-02-10 13:36:37', NULL, NULL, NULL),
('Sách Idetica', 27956, 0, 69, 50, 'Veronica Coffey', 104, 'NXB Giáo dục', 2014, 'Aliquip enim commodo dolore nulla dolore mollit exercitation fugiat eu ex commodo.', 'temp-6352099207348952932.jpg', 0, '2021-02-13 00:56:55', NULL, NULL, NULL),
('Sách Enerforce', 364658, 0, 89, 207, 'Spencer Marshall', 475, 'NXB Đại học Sư phạm TP.HCM', 1999, 'Id laborum aute duis id excepteur exercitation nostrud ad qui sit.', 'temp-7329036107498680084.jpg', 0, '2022-01-04 19:49:25', NULL, NULL, NULL),
('Sách Kenegy', 104405, 0, 92, 406, 'Carrie Boone', 150, 'NXB Đại học Sư phạm TP.HCM', 2011, 'Pariatur ex labore deserunt non deserunt aliqua non reprehenderit elit fugiat elit officia reprehenderit laboris.', 'temp-7329036107498680084.jpg', 1, '2022-01-04 10:35:13', NULL, NULL, NULL),
('Sách Krag', 293321, 20, 84, 31, 'Isabelle Justice', 346, 'NXB Đại học Sư phạm TP.HCM', 2008, 'Aute irure consectetur sunt do incididunt.', 'temp-3015888053636485125.jpg', 0, '2021-10-25 15:41:19', NULL, NULL, NULL),
('Sách Sealoud', 380540, 0, 73, 268, 'Adele Martinez', 499, 'NXB Đại học Huế', 2019, 'Ut esse aliquip proident excepteur et fugiat ullamco fugiat dolor et velit fugiat sit.', 'temp-7329036107498680084.jpg', 0, '2021-08-01 19:32:05', NULL, NULL, NULL),
('Sách Isologics', 268413, 20, 32, 384, 'Jewell Whitfield', 381, 'NXB Đại học Sư phạm Hà Nội', 2010, 'Ut sit fugiat reprehenderit ipsum aliqua incididunt consequat reprehenderit.', 'temp-16741118072528735594.jpg', 0, '2021-11-28 16:51:08', NULL, NULL, NULL),
('Sách Exiand', 187140, 20, 54, 320, 'Giles Mcdowell', 280, 'NXB Giáo dục', 2020, 'Consequat reprehenderit reprehenderit aliquip amet aliqua fugiat duis eiusmod est in in nostrud.', 'temp-13862094760385571107.jpg', 0, '2021-11-14 02:53:14', NULL, NULL, NULL),
('Sách Terragen', 103129, 0, 51, 388, 'Jackson Fernandez', 264, 'NXB Đại học Quốc gia Hà Nội', 2009, 'Irure deserunt duis ut commodo consequat esse officia.', 'temp-16741118072528735594.jpg', 0, '2021-10-29 15:47:05', NULL, NULL, NULL),
('Sách Accidency', 44192, 20, 69, 128, 'Blackburn West', 357, 'NXB Đại học Huế', 2020, 'Nisi esse excepteur sit id adipisicing do voluptate cupidatat voluptate in.', 'temp-13862094760385571107.jpg', 1, '2021-02-07 02:11:02', NULL, NULL, NULL),
('Sách Hairport', 367922, 20, 96, 422, 'Harris Zamora', 328, 'NXB Đại học Sư phạm Hà Nội', 2003, 'Est enim veniam proident ad excepteur cillum ex irure magna duis enim exercitation aliquip fugiat.', 'temp-13862094760385571107.jpg', 0, '2021-10-24 09:47:35', NULL, NULL, NULL),
('Sách Shepard', 215739, 20, 85, 134, 'Elsie Peters', 198, 'NXB Đại học Huế', 2007, 'Voluptate duis mollit aliquip culpa sint pariatur cillum Lorem.', 'temp-3015888053636485125.jpg', 1, '2021-04-21 09:30:10', NULL, NULL, NULL),
('Sách Pigzart', 91054, 20, 16, 420, 'Roach Nielsen', 279, 'NXB Đại học Sư phạm Hà Nội', 2005, 'Excepteur aute Lorem proident excepteur magna sit.', 'temp-13064240004351430671.jpg', 1, '2021-03-31 07:39:02', NULL, NULL, NULL),
('Sách Orbaxter', 270543, 0, 54, 140, 'Diaz Howard', 156, 'NXB Đại học Huế', 2000, 'Do aute occaecat qui velit pariatur sit aute Lorem ea fugiat enim ullamco sunt cillum.', 'temp-8476700387786158058.jpg', 1, '2021-06-15 14:51:43', NULL, NULL, NULL),
('Sách Cowtown', 29992, 20, 49, 318, 'Mitzi Koch', 191, 'NXB Đại học Sư phạm Hà Nội', 1998, 'Qui elit irure sunt esse voluptate ex laborum.', 'temp-6243426685116508297.jpg', 0, '2021-12-21 11:16:02', NULL, NULL, NULL),
('Sách Makingway', 205442, 20, 85, 380, 'Cynthia Rasmussen', 346, 'NXB Đại học Quốc gia Hà Nội', 2015, 'Nisi qui amet ipsum ea nulla dolor nulla.', 'temp-7329036107498680084.jpg', 1, '2021-09-02 03:01:38', NULL, NULL, NULL),
('Sách Lunchpad', 129694, 20, 26, 133, 'Bryant Ball', 318, 'NXB Đại học Sư phạm TP.HCM', 2009, 'Quis mollit ipsum ad laboris velit sit est anim ullamco sunt esse.', 'temp-6352099207348952932.jpg', 1, '2021-03-25 11:05:04', NULL, NULL, NULL),
('Sách Mediot', 216835, 20, 78, 114, 'Rosemary Sampson', 321, 'NXB Đại học Quốc gia Hà Nội', 1998, 'Ut deserunt esse excepteur aliqua commodo Lorem ad et.', 'temp-6352099207348952932.jpg', 0, '2021-05-17 13:49:26', NULL, NULL, NULL),
('Sách Plasto', 215800, 20, 55, 85, 'Santiago Levine', 267, 'NXB Đại học Quốc gia Hà Nội', 2001, 'Amet aliquip sunt in commodo excepteur esse ea aliqua laboris in.', 'temp-13064240004351430671.jpg', 1, '2022-01-27 21:49:53', NULL, NULL, NULL),
('Sách Geekola', 332072, 20, 42, 165, 'Austin Cain', 274, 'NXB Đại học Sư phạm TP.HCM', 2010, 'Duis veniam ad nisi nostrud aliquip ex aliquip laboris ipsum eu velit dolor dolor in.', 'temp-14438611480196141526.jpg', 0, '2021-04-28 06:56:06', NULL, NULL, NULL),
('Sách Gracker', 268831, 0, 49, 248, 'Vasquez Gallegos', 230, 'NXB Giáo dục', 2006, 'Consequat sunt non aute irure voluptate reprehenderit enim consectetur sit sint sit.', 'temp-12235989262213754276.jpg', 1, '2021-10-25 05:55:27', NULL, NULL, NULL),
('Sách Housedown', 307111, 0, 83, 233, 'Haynes Riggs', 390, 'NXB Đại học Huế', 2020, 'Quis magna tempor laboris adipisicing.', 'temp-8476700387786158058.jpg', 1, '2021-04-17 06:11:57', NULL, NULL, NULL),
('Sách Zillan', 108354, 0, 81, 307, 'Hood Nieves', 192, 'NXB Đại học Sư phạm TP.HCM', 2012, 'Incididunt labore minim enim qui minim cillum est voluptate veniam eu.', 'temp-8262627340495498759.jpg', 1, '2021-07-03 05:51:31', NULL, NULL, NULL),
('Sách Lovepad', 477477, 20, 18, 120, 'Foster Sims', 91, 'NXB Đại học Sư phạm Hà Nội', 2015, 'Anim do laboris duis aute laboris aliquip amet do nulla aliqua.', 'temp-3984373128647845854.jpg', 1, '2021-07-09 19:58:01', NULL, NULL, NULL),
('Sách Ontagene', 199763, 20, 51, 86, 'Weeks Charles', 71, 'NXB Đại học Huế', 2009, 'Do in sunt sunt in tempor tempor proident dolor officia irure consequat cupidatat incididunt nulla.', 'temp-8476700387786158058.jpg', 1, '2022-01-22 15:29:03', NULL, NULL, NULL),
('Sách Spacewax', 171403, 20, 34, 198, 'Sadie Logan', 85, 'NXB Đại học Huế', 2014, 'Esse non qui dolor consectetur magna consectetur excepteur exercitation nisi eiusmod laboris nulla laborum.', 'temp-12235989262213754276.jpg', 1, '2021-08-14 19:14:31', NULL, NULL, NULL);

-- =============================================
-- PART 10: SEED DATA - Product Categories
-- =============================================

INSERT INTO product_category (productId, categoryId) VALUES
(1, 2), (2, 14), (3, 5), (4, 5), (5, 5), (6, 3), (7, 7), (8, 6), (9, 7), (10, 11),
(11, 13), (12, 6), (13, 9), (14, 15), (15, 14), (16, 15), (17, 9), (18, 2), (19, 3), (20, 10),
(21, 13), (22, 15), (23, 8), (24, 14), (25, 7), (26, 1), (27, 7), (28, 14), (29, 9), (30, 2),
(31, 8), (32, 1), (33, 10), (34, 7), (35, 2), (36, 7), (37, 12), (38, 1), (39, 12), (40, 1),
(41, 9), (42, 4), (43, 5), (44, 5), (45, 3), (46, 12), (47, 3), (48, 7), (49, 6), (50, 6),
(51, 11), (52, 5), (53, 9), (54, 4), (55, 1), (56, 10), (57, 10), (58, 3), (59, 8), (60, 9),
(61, 12), (62, 10), (63, 14), (64, 8), (65, 6), (66, 11), (67, 14), (68, 5), (69, 13), (70, 8),
(71, 13), (72, 14), (73, 2), (74, 1), (75, 13), (76, 2), (77, 2), (78, 8), (79, 3), (80, 8),
(81, 14), (82, 2), (83, 1), (84, 9), (85, 13), (86, 14), (87, 5), (88, 13), (89, 1), (90, 10),
(91, 4), (92, 2), (93, 4), (94, 15), (95, 11), (96, 8), (97, 11), (98, 10), (99, 15), (100, 8);

-- =============================================
-- PART 11: SEED DATA - Carts
-- =============================================

INSERT INTO cart (userId, createdAt, updatedAt) VALUES
(4, '2021-12-30 15:39:19', NULL),
(5, '2021-12-18 20:35:59', NULL);

-- =============================================
-- PART 12: SEED DATA - Cart Items
-- =============================================

INSERT INTO cart_item (cartId, productId, quantity, createdAt, updatedAt) VALUES
(2, 55, 3, '2021-07-13 10:21:51', NULL),
(2, 36, 2, '2021-07-05 07:21:45', NULL),
(1, 7, 3, '2021-04-14 19:36:26', NULL),
(1, 70, 2, '2021-02-20 18:51:20', NULL),
(1, 27, 4, '2021-11-24 00:05:40', NULL);

-- =============================================
-- PART 13: SEED DATA - Orders
-- =============================================

INSERT INTO orders (userId, status, deliveryMethod, deliveryPrice, createdAt, updatedAt) VALUES
(4, 3, 1, 10000, '2021-12-01 08:34:38', NULL),
(5, 2, 2, 50000, '2021-07-05 10:52:51', NULL),
(4, 1, 1, 10000, '2021-10-11 09:15:13', NULL),
(5, 3, 2, 50000, '2022-01-06 15:05:11', NULL),
(4, 2, 1, 10000, '2021-09-25 14:06:36', NULL),
(4, 3, 2, 50000, '2021-09-18 02:22:12', NULL),
(5, 1, 1, 10000, '2021-04-08 15:13:25', NULL),
(4, 3, 2, 50000, '2021-04-06 22:47:57', NULL),
(5, 3, 1, 10000, '2021-04-03 11:40:27', NULL),
(4, 2, 2, 50000, '2021-10-22 15:49:15', NULL),
(5, 2, 1, 10000, '2021-03-29 01:04:10', NULL),
(4, 2, 2, 50000, '2021-06-11 15:01:12', NULL),
(4, 3, 1, 10000, '2021-03-10 04:45:49', NULL),
(5, 1, 2, 50000, '2021-12-06 11:32:16', NULL),
(4, 3, 1, 10000, '2021-07-01 07:11:33', NULL),
(5, 3, 2, 50000, '2021-10-24 17:39:58', NULL),
(4, 1, 1, 10000, '2021-02-17 19:18:55', NULL),
(4, 3, 2, 50000, '2021-09-12 00:13:50', NULL),
(4, 2, 1, 10000, '2021-02-18 11:26:18', NULL),
(5, 1, 2, 50000, '2021-04-12 02:25:34', NULL),
(4, 1, 1, 10000, '2021-12-17 08:21:32', NULL),
(5, 1, 2, 50000, '2022-01-28 16:19:00', NULL),
(4, 1, 1, 10000, '2021-04-14 09:36:21', NULL),
(5, 1, 2, 50000, '2021-04-05 19:56:13', NULL),
(4, 2, 1, 10000, '2021-12-08 17:07:23', NULL);

-- =============================================
-- PART 14: SEED DATA - Order Items
-- =============================================

INSERT INTO order_item (orderId, productId, price, discount, quantity, createdAt, updatedAt) VALUES
(1, 78, 286587, 0, 4, '2021-06-27 03:11:05', NULL),
(2, 21, 29619, 0, 2, '2021-03-26 10:39:47', NULL),
(3, 69, 299814, 0, 4, '2021-07-31 12:54:03', NULL),
(4, 2, 181582, 0, 5, '2021-07-15 11:58:11', NULL),
(5, 79, 355067, 20, 1, '2021-08-28 20:28:16', NULL),
(6, 41, 116744, 0, 1, '2021-04-09 09:53:33', NULL),
(7, 3, 276065, 20, 5, '2021-08-16 21:48:06', NULL),
(8, 87, 421713, 20, 5, '2021-05-15 09:36:10', NULL),
(9, 90, 480454, 20, 3, '2021-09-18 01:58:18', NULL),
(10, 82, 280519, 0, 4, '2022-01-08 19:44:56', NULL),
(11, 22, 422703, 20, 1, '2021-12-01 00:24:12', NULL),
(12, 99, 89288, 0, 4, '2021-11-14 20:04:04', NULL),
(13, 52, 192906, 0, 1, '2021-04-08 04:50:44', NULL),
(14, 84, 380080, 0, 2, '2021-03-02 10:40:10', NULL),
(15, 5, 274595, 20, 2, '2021-10-24 21:14:35', NULL),
(16, 14, 51752, 0, 2, '2021-04-03 00:40:34', NULL),
(17, 20, 61648, 20, 4, '2021-04-23 14:29:56', NULL),
(18, 42, 252357, 20, 3, '2021-07-12 06:02:37', NULL),
(19, 3, 230576, 0, 2, '2021-08-15 14:07:26', NULL),
(20, 12, 186136, 0, 5, '2021-03-10 07:43:13', NULL),
(21, 88, 237111, 0, 1, '2021-02-28 12:28:58', NULL),
(22, 17, 418046, 20, 2, '2021-09-24 05:09:52', NULL),
(23, 59, 104758, 20, 2, '2021-10-25 00:07:51', NULL),
(24, 79, 392777, 20, 4, '2021-04-01 10:27:29', NULL),
(25, 38, 51737, 20, 2, '2021-07-30 23:59:07', NULL);

-- =============================================
-- PART 15: SEED DATA - Wishlist
-- =============================================

INSERT INTO wishlist_item (userId, productId, createdAt) VALUES
(4, 1, '2021-07-21 02:09:42'),
(4, 2, '2021-04-11 17:08:10'),
(4, 3, '2021-08-24 21:06:10'),
(5, 4, '2021-10-25 10:00:44'),
(5, 5, '2021-06-10 18:29:11'),
(5, 6, '2021-10-22 04:19:32'),
(4, 7, '2021-09-22 19:37:18'),
(4, 8, '2021-04-27 00:52:04'),
(5, 9, '2021-12-10 18:11:25'),
(4, 10, '2021-11-16 20:54:23'),
(4, 11, '2021-06-10 03:54:36'),
(5, 12, '2022-01-11 23:15:34'),
(4, 13, '2021-06-03 16:02:59'),
(5, 14, '2021-11-12 13:26:01'),
(5, 15, '2021-05-25 04:12:50');

-- PART 16: SEED DATA - Shipping Methods

INSERT INTO shipping_methods (id, name, provider_type, estimated_days, price_per_kg, is_express, express_surcharge, min_weight_kg, max_weight_kg, free_shipping_threshold, support_phone, support_email, ghn_service_id, ghn_from_district_id, ghn_from_ward_code, status)
VALUES
(1, 'Giao hàng nhanh', 'GHN', 2, 25000.00, 1, 1.8, 0.5, 30, 300000, '1900 6365', 'cskh@ghn.vn', 2, '1567', '550307', 1);

INSERT INTO shipping_methods (id, name, provider_type, estimated_days, price_per_kg, is_express, express_surcharge, min_weight_kg, max_weight_kg, free_shipping_threshold, support_phone, support_email, ghn_service_id, ghn_from_district_id, ghn_from_ward_code, status)
VALUES
(2, 'Giao hàng tiêu chuẩn', 'GHN', 4, 18000.00, 0, 1.0, 0.5, 30, 500000, '1900 6365', 'cskh@ghn.vn', 1, '1567', '550307', 1);

-- PART 17: SEED DATA - Shipping Zones

INSERT INTO shipping_zones (zone_name, zone_type, base_fee, price_per_kg, price_per_volume, estimated_days_min, estimated_days_max, description, status) VALUES
('Nội thành TP.HCM', 'INNER', 15000.00, 3000.00, 5000.00, 1, 2, 'Các quận nội thành TP.HCM: Quận 1, 3, 4, 5, 6, 7, 8, 10, 11, 12, Bình Thạnh, Gò Vấp, Phú Nhuận, Tân Bình, Tân Phú, Thủ Đức', 1),
('Nội thành Hà Nội', 'INNER', 18000.00, 3500.00, 5000.00, 1, 2, 'Các quận nội thành Hà Nội: Ba Đình, Hoàn Kiếm, Hai Bà Trưng, Đống Đa, Tây Hồ, Thanh Xuân, Cầu Giấy, Hoàng Mai, Long Biên, Hà Đông', 1),
('Nội thành Đà Nẵng', 'INNER', 12000.00, 2500.00, 4000.00, 1, 2, 'Các quận nội thành Đà Nẵng: Hải Châu, Thanh Khê, Sơn Trà, Ngũ Hành Sơn, Liên Chiểu', 1),
('Miền Bắc - Tỉnh lẻ', 'PROVINCIAL', 25000.00, 5000.00, 8000.00, 2, 4, 'Các tỉnh miền Bắc ngoài Hà Nội: Hải Phòng, Hải Dương, Nam Định, Thái Bình, Ninh Bình, Hưng Yên, Bắc Ninh, Bắc Giang, Lạng Sơn, Quảng Ninh', 1),
('Miền Trung - Bắc', 'PROVINCIAL', 30000.00, 6000.00, 10000.00, 2, 4, 'Các tỉnh miền Trung phía Bắc: Thanh Hóa, Nghệ An, Hà Tĩnh, Quảng Bình, Quảng Trị, Thừa Thiên Huế', 1),
('Miền Trung - Nam', 'PROVINCIAL', 30000.00, 6000.00, 10000.00, 2, 4, 'Các tỉnh miền Trung phía Nam: Đà Nẵng, Quảng Nam, Quảng Ngãi, Bình Định, Phú Yên, Khánh Hòa, Ninh Thuận, Bình Thuận', 1),
('Miền Nam - Tỉnh lẻ', 'PROVINCIAL', 25000.00, 5000.00, 8000.00, 2, 4, 'Các tỉnh miền Nam ngoài TP.HCM: Bình Dương, Đồng Nai, Long An, Tiền Giang, Bến Tre, Vũng Tàu, Tây Ninh, Cần Thơ', 1),
('Vùng xa miền Bắc', 'REMOTE', 45000.00, 10000.00, 15000.00, 4, 7, 'Các vùng xa miền Bắc: Lào Cai, Yên Bái, Điện Biên, Lai Châu, Sơn La, Hà Giang, Tuyên Quang, Cao Bằng, Bắc Kạn', 1),
('Vùng xa miền Trung & Tây Nguyên', 'REMOTE', 50000.00, 12000.00, 18000.00, 4, 7, 'Các vùng xa miền Trung & Tây Nguyên: Kon Tum, Gia Lai, Đắk Lắk, Đắk Nông, Lâm Đồng, Bình Phước', 1),
('Vùng xa miền Nam', 'REMOTE', 45000.00, 10000.00, 15000.00, 4, 7, 'Các vùng xa miền Nam: Cà Mau, Bạc Liêu, Sóc Trăng, Trà Vinh, Hậu Giang, Kiên Giang, An Giang', 1);

-- PART 18: SEED DATA - Provinces (63 tỉnh thành VN)

INSERT INTO provinces (province_code, province_name, province_type, region, is_metro_city, shipping_zone_id) VALUES
-- TP Trung ương
('01', 'Thành phố Hà Nội', 'thành-phố', 'MienBac', 1, 2),
('79', 'Thành phố Hồ Chí Minh', 'thành-phố', 'MienNam', 1, 1),
('48', 'Thành phố Đà Nẵng', 'thành-phố', 'MienTrung', 1, 3),
-- Miền Bắc
('02', 'Tỉnh Hà Giang', 'tỉnh', 'MienBac', 0, 8),
('04', 'Tỉnh Cao Bằng', 'tỉnh', 'MienBac', 0, 8),
('06', 'Tỉnh Bắc Kạn', 'tỉnh', 'MienBac', 0, 8),
('08', 'Tỉnh Tuyên Quang', 'tỉnh', 'MienBac', 0, 8),
('10', 'Tỉnh Lào Cai', 'tỉnh', 'MienBac', 0, 8),
('11', 'Tỉnh Điện Biên', 'tỉnh', 'MienBac', 0, 8),
('12', 'Tỉnh Lai Châu', 'tỉnh', 'MienBac', 0, 8),
('14', 'Tỉnh Sơn La', 'tỉnh', 'MienBac', 0, 8),
('15', 'Tỉnh Yên Bái', 'tỉnh', 'MienBac', 0, 8),
('17', 'Tỉnh Hòa Bình', 'tỉnh', 'MienBac', 0, 4),
('19', 'Tỉnh Thái Nguyên', 'tỉnh', 'MienBac', 0, 4),
('20', 'Tỉnh Lạng Sơn', 'tỉnh', 'MienBac', 0, 4),
('22', 'Tỉnh Quảng Ninh', 'tỉnh', 'MienBac', 0, 4),
('24', 'Tỉnh Bắc Giang', 'tỉnh', 'MienBac', 0, 4),
('25', 'Tỉnh Phú Thọ', 'tỉnh', 'MienBac', 0, 4),
('26', 'Tỉnh Vĩnh Phúc', 'tỉnh', 'MienBac', 0, 4),
('27', 'Tỉnh Bắc Ninh', 'tỉnh', 'MienBac', 0, 4),
('30', 'Tỉnh Hải Dương', 'tỉnh', 'MienBac', 0, 4),
('31', 'Tỉnh Hưng Yên', 'tỉnh', 'MienBac', 0, 4),
('33', 'Tỉnh Thái Bình', 'tỉnh', 'MienBac', 0, 4),
('34', 'Tỉnh Nam Định', 'tỉnh', 'MienBac', 0, 4),
('35', 'Tỉnh Ninh Bình', 'tỉnh', 'MienBac', 0, 4),
('36', 'Tỉnh Thanh Hóa', 'tỉnh', 'MienTrung', 0, 5),
('38', 'Tỉnh Nghệ An', 'tỉnh', 'MienTrung', 0, 5),
('40', 'Tỉnh Hà Tĩnh', 'tỉnh', 'MienTrung', 0, 5),
-- Miền Trung
('42', 'Tỉnh Quảng Bình', 'tỉnh', 'MienTrung', 0, 5),
('44', 'Tỉnh Quảng Trị', 'tỉnh', 'MienTrung', 0, 5),
('45', 'Tỉnh Thừa Thiên Huế', 'tỉnh', 'MienTrung', 0, 5),
('49', 'Tỉnh Quảng Nam', 'tỉnh', 'MienTrung', 0, 6),
('51', 'Tỉnh Quảng Ngãi', 'tỉnh', 'MienTrung', 0, 6),
('52', 'Tỉnh Bình Định', 'tỉnh', 'MienTrung', 0, 6),
('54', 'Tỉnh Phú Yên', 'tỉnh', 'MienTrung', 0, 6),
('56', 'Tỉnh Khánh Hòa', 'tỉnh', 'MienTrung', 0, 6),
('58', 'Tỉnh Ninh Thuận', 'tỉnh', 'MienTrung', 0, 6),
('60', 'Tỉnh Bình Thuận', 'tỉnh', 'MienTrung', 0, 6),
('62', 'Tỉnh Kon Tum', 'tỉnh', 'MienTrung', 0, 9),
('64', 'Tỉnh Gia Lai', 'tỉnh', 'MienTrung', 0, 9),
('66', 'Tỉnh Đắk Lắk', 'tỉnh', 'MienTrung', 0, 9),
('67', 'Tỉnh Đắk Nông', 'tỉnh', 'MienTrung', 0, 9),
('68', 'Tỉnh Lâm Đồng', 'tỉnh', 'MienTrung', 0, 9),
-- Miền Nam
('70', 'Tỉnh Bình Phước', 'tỉnh', 'MienNam', 0, 9),
('72', 'Tỉnh Tây Ninh', 'tỉnh', 'MienNam', 0, 7),
('75', 'Tỉnh Bình Dương', 'tỉnh', 'MienNam', 0, 7),
('77', 'Tỉnh Đồng Nai', 'tỉnh', 'MienNam', 0, 7),
('78', 'Tỉnh Bà Rịa - Vũng Tàu', 'tỉnh', 'MienNam', 0, 7),
('80', 'Tỉnh Long An', 'tỉnh', 'MienNam', 0, 7),
('82', 'Tỉnh Tiền Giang', 'tỉnh', 'MienNam', 0, 7),
('83', 'Tỉnh Bến Tre', 'tỉnh', 'MienNam', 0, 7),
('84', 'Tỉnh Trà Vinh', 'tỉnh', 'MienNam', 0, 10),
('87', 'Tỉnh Vĩnh Long', 'tỉnh', 'MienNam', 0, 7),
('89', 'Tỉnh Đồng Tháp', 'tỉnh', 'MienNam', 0, 7),
('91', 'Tỉnh An Giang', 'tỉnh', 'MienNam', 0, 10),
('93', 'Tỉnh Kiên Giang', 'tỉnh', 'MienNam', 0, 10),
('94', 'Thành phố Cần Thơ', 'thành-phố', 'MienNam', 0, 7),
('95', 'Tỉnh Hậu Giang', 'tỉnh', 'MienNam', 0, 10),
('96', 'Tỉnh Sóc Trăng', 'tỉnh', 'MienNam', 0, 10),
('97', 'Tỉnh Bạc Liêu', 'tỉnh', 'MienNam', 0, 10),
('98', 'Tỉnh Cà Mau', 'tỉnh', 'MienNam', 0, 10);

-- PART 19: SEED DATA - Shipping Fees

-- GIAO HANG NHANH (ID=1)
-- Nội thành
INSERT INTO shipping_fees (shipping_method_id, zone_type, min_weight, max_weight, base_fee, fee_per_kg, price_per_volume, volumetric_ratio, estimated_days_min, estimated_days_max) VALUES
(1, 'INNER', 0.00, 0.50, 15000, 0, 2000, 5000, 1, 2),
(1, 'INNER', 0.50, 2.00, 15000, 8000, 2000, 5000, 1, 2),
(1, 'INNER', 2.00, 5.00, 15000, 10000, 2000, 5000, 1, 2),
(1, 'INNER', 5.00, 10.00, 15000, 12000, 2000, 5000, 1, 2),
(1, 'INNER', 10.00, 30.00, 15000, 15000, 2000, 5000, 1, 2);

-- Tỉnh lẻ
INSERT INTO shipping_fees (shipping_method_id, zone_type, min_weight, max_weight, base_fee, fee_per_kg, price_per_volume, volumetric_ratio, estimated_days_min, estimated_days_max) VALUES
(1, 'PROVINCIAL', 0.00, 0.50, 25000, 0, 3000, 5000, 2, 3),
(1, 'PROVINCIAL', 0.50, 2.00, 25000, 12000, 3000, 5000, 2, 4),
(1, 'PROVINCIAL', 2.00, 5.00, 25000, 15000, 3000, 5000, 2, 4),
(1, 'PROVINCIAL', 5.00, 10.00, 25000, 18000, 3000, 5000, 3, 5),
(1, 'PROVINCIAL', 10.00, 30.00, 25000, 22000, 3000, 5000, 3, 5);

-- Vùng xa
INSERT INTO shipping_fees (shipping_method_id, zone_type, min_weight, max_weight, base_fee, fee_per_kg, price_per_volume, volumetric_ratio, estimated_days_min, estimated_days_max) VALUES
(1, 'REMOTE', 0.00, 0.50, 45000, 0, 5000, 5000, 3, 5),
(1, 'REMOTE', 0.50, 2.00, 45000, 18000, 5000, 5000, 3, 6),
(1, 'REMOTE', 2.00, 5.00, 45000, 22000, 5000, 5000, 4, 6),
(1, 'REMOTE', 5.00, 10.00, 45000, 28000, 5000, 5000, 4, 7),
(1, 'REMOTE', 10.00, 30.00, 45000, 35000, 5000, 5000, 5, 7);

-- GIAO HANG TIEU CHUAN (ID=2)
-- Nội thành
INSERT INTO shipping_fees (shipping_method_id, zone_type, min_weight, max_weight, base_fee, fee_per_kg, price_per_volume, volumetric_ratio, estimated_days_min, estimated_days_max) VALUES
(2, 'INNER', 0.00, 0.50, 12000, 0, 1500, 5000, 2, 3),
(2, 'INNER', 0.50, 2.00, 12000, 6000, 1500, 5000, 2, 4),
(2, 'INNER', 2.00, 5.00, 12000, 8000, 1500, 5000, 3, 4),
(2, 'INNER', 5.00, 10.00, 12000, 10000, 1500, 5000, 3, 5),
(2, 'INNER', 10.00, 30.00, 12000, 12000, 1500, 5000, 4, 5);

-- Tỉnh lẻ
INSERT INTO shipping_fees (shipping_method_id, zone_type, min_weight, max_weight, base_fee, fee_per_kg, price_per_volume, volumetric_ratio, estimated_days_min, estimated_days_max) VALUES
(2, 'PROVINCIAL', 0.00, 0.50, 18000, 0, 2500, 5000, 3, 5),
(2, 'PROVINCIAL', 0.50, 2.00, 18000, 10000, 2500, 5000, 3, 5),
(2, 'PROVINCIAL', 2.00, 5.00, 18000, 12000, 2500, 5000, 4, 6),
(2, 'PROVINCIAL', 5.00, 10.00, 18000, 15000, 2500, 5000, 4, 6),
(2, 'PROVINCIAL', 10.00, 30.00, 18000, 18000, 2500, 5000, 5, 7);

-- Vùng xa
INSERT INTO shipping_fees (shipping_method_id, zone_type, min_weight, max_weight, base_fee, fee_per_kg, price_per_volume, volumetric_ratio, estimated_days_min, estimated_days_max) VALUES
(2, 'REMOTE', 0.00, 0.50, 35000, 0, 4000, 5000, 4, 7),
(2, 'REMOTE', 0.50, 2.00, 35000, 15000, 4000, 5000, 5, 8),
(2, 'REMOTE', 2.00, 5.00, 35000, 18000, 4000, 5000, 5, 8),
(2, 'REMOTE', 5.00, 10.00, 35000, 22000, 4000, 5000, 6, 9),
(2, 'REMOTE', 10.00, 30.00, 35000, 28000, 4000, 5000, 6, 9);

--Voucher
INSERT INTO `vouchers` VALUES (1, 'WELCOME50', 'Giảm ngay 50k cho khách hàng mới', 'Áp dụng cho mọi đơn hàng', 1, 0, '2026-05-27 02:26:20', '2026-06-26 02:26:20', 50000, 0, 50000, 1000, 1, 4, 1);
INSERT INTO `vouchers` VALUES (2, 'HELLOSUMMER', 'Đón hè rực rỡ giảm 10% đơn hàng', 'Áp dụng toàn sàn', 0, 0, '2026-05-27 02:26:20', '2026-06-11 02:26:20', 10, 5000, 30000, 500, 1, 0, 1);
INSERT INTO `vouchers` VALUES (23, 'FREESHIP15K', 'Freeship 15k', 'Freeship 15k', 1, 3, '2026-06-04 07:41:00', '2026-06-30 07:41:00', 15000, 0, 15000, 100, 2, 5, 1);

SELECT '========================================';
SELECT '  DATABASE SETUP COMPLETE' AS status;
SELECT '  Total Tables: 22' AS info;
SELECT '  Compatible with MySQL 8.0 CE' AS version;
SELECT '========================================';
