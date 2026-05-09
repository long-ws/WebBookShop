package utils;

import java.sql.Connection;
import java.sql.SQLException;

import javax.sql.DataSource;

import com.zaxxer.hikari.HikariConfig;
import com.zaxxer.hikari.HikariDataSource;

public class DBConnection {

	private static final HikariDataSource dataSource;

	static {
		HikariConfig config = new HikariConfig();

		config.setJdbcUrl("jdbc:mysql://" + ConstantUtils.SERVER_NAME + ":" + ConstantUtils.DB_PORT + "/"
				+ ConstantUtils.DB_NAME
				+ "?useUnicode=true"
				+ "&characterEncoding=UTF-8"
				+ "&characterSetResults=UTF-8"
				+ "&connectionCollation=utf8mb4_unicode_ci"
				+ "&useSSL=false"
				+ "&serverTimezone=Asia/Ho_Chi_Minh"
				+ "&allowPublicKeyRetrieval=true");
		config.setUsername(ConstantUtils.DB_USERNAME);
		config.setPassword(ConstantUtils.DB_PASSWORD);
		config.setDriverClassName("com.mysql.cj.jdbc.Driver");

		config.setMaximumPoolSize(20);
		config.setMinimumIdle(5);
		config.setConnectionTimeout(5000);
		config.setIdleTimeout(300000);
		config.setMaxLifetime(1800000);

		config.setPoolName("DBConnectionPool");

		config.addDataSourceProperty("cachePrepStmts", "true");
		config.addDataSourceProperty("prepStmtCacheSize", "250");
		config.addDataSourceProperty("prepStmtCacheSqlLimit", "2048");

		dataSource = new HikariDataSource(config);
		System.out.println("[DBConnection] Khoi tao connection pool thanh cong");
	}

	public static Connection getConnection() throws SQLException {
		return dataSource.getConnection();
	}

	public static int getActiveConnections() {
		return dataSource.getHikariPoolMXBean().getActiveConnections();
	}

	public static int getAvailableConnections() {
		return dataSource.getHikariPoolMXBean().getIdleConnections();
	}

	public static void closeAll() {
		dataSource.close();
		System.out.println("[DBConnection] HikariCP pool closed");
	}

	public static DataSource getDataSource() {
		return dataSource;
	}
}
