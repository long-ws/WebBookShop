package utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnection {

	private static final HikariDataSource dataSource;

	static {
		try {
			HikariConfig config = new HikariConfig();

			// Cấu hình database
			String url = "jdbc:mysql://" + ConstantUtils.SERVER_NAME + ":" + ConstantUtils.DB_PORT + "/"
					+ ConstantUtils.DB_NAME + "?useSSL=false&serverTimezone=UTC&allowPublicKeyRetrieval=true";
			config.setJdbcUrl(url);
			config.setUsername(ConstantUtils.DB_USERNAME);
			config.setPassword(ConstantUtils.DB_PASSWORD);
			config.setDriverClassName("com.mysql.cj.jdbc.Driver");

			config.setMaximumPoolSize(20); // Tối đa 20 connection
			config.setMinimumIdle(5); // Tối thiểu 5 connection rảnh
			config.setConnectionTimeout(5000); // Chờ tối đa 5 giây
			config.setIdleTimeout(300000); // Connection rảnh tối đa 5 phút
			config.setMaxLifetime(1800000); // Connection sống tối đa 30 phút

			// Đặt tên cho pool
			config.setPoolName("DBConnectionPool");

			// Tối ưu hiệu năng
			config.addDataSourceProperty("cachePrepStmts", "true"); // Bật tính năng bộ nhớ đệm cho Prepared Statements
			config.addDataSourceProperty("prepStmtCacheSize", "250"); // Giới hạn số câu lệnh lưu trữ trong bộ nhớ đệm
			config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048"); // Kích thức giới hạn của câu lệnh để lưu vào
																			// bộ nhớ đệm

			dataSource = new HikariDataSource(config);
			System.out.println("[DBConnection] Khởi tạo connection pool");
		} catch (Exception e) {
			throw new RuntimeException("[DBConnection] Không thể khởi tạo connection pool", e);
		}
	}

	/**
	 * Lấy connection từ pool
	 * @return Connection từ pool
	 * @throws SQLException nếu hết connection
	 */
	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	/**
	 * Lấy số connection đang dùng
	 */
	public static int getActiveConnections() {
		return dataSource.getHikariPoolMXBean().getActiveConnections();
	}

	/**
	 * Lấy số connection rảnh
	 */
	public static int getAvailableConnections() {
		return dataSource.getHikariPoolMXBean().getIdleConnections();
	}

	/**
	 * Đóng pool khi ứng dụng dừng
	 */
	public static void closeAll() {
		dataSource.close();
		System.out.println("[DBConnection] Đóng connection pool");
	}

	/**
	 * Lấy DataSource
	 */
	public static DataSource getDataSource() {
		return dataSource;
	}
}