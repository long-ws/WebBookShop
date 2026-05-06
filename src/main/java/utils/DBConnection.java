package utils;

import java.sql.Connection;
import java.sql.SQLException;

import com.mysql.cj.jdbc.MysqlDataSource;

public class DBConnection {

    private static MysqlDataSource dataSource;

    static {
        dataSource = new MysqlDataSource();
        dataSource.setServerName(ConstantUtils.SERVER_NAME);
        dataSource.setPort(ConstantUtils.DB_PORT);
        dataSource.setDatabaseName(ConstantUtils.DB_NAME);
        dataSource.setUser(ConstantUtils.DB_USERNAME);
        dataSource.setPassword(ConstantUtils.DB_PASSWORD);

    }

    public static Connection getConnection() throws SQLException {
        return dataSource.getConnection();
    }
}
