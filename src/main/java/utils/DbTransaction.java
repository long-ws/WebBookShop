package utils;

import java.sql.Connection;
import java.sql.SQLException;

public final class DbTransaction {

	private DbTransaction() {
	}

	public static <T> T run(TransactionCallback<T> callback) throws SQLException {
		try (Connection conn = DBConnection.getConnection()) {
			boolean originalAutoCommit = conn.getAutoCommit();
			conn.setAutoCommit(false);
			try {
				T result = callback.doInTransaction(conn);
				conn.commit();
				return result;
			} catch (SQLException e) {
				conn.rollback();
				throw e;
			} catch (RuntimeException e) {
				conn.rollback();
				throw e;
			} finally {
				conn.setAutoCommit(originalAutoCommit);
			}
		}
	}

	public static void runVoid(final TransactionCallback<Void> callback) throws SQLException {
		DbTransaction.run(new TransactionCallback<Void>() {
			@Override
			public Void doInTransaction(Connection conn) throws SQLException {
				callback.doInTransaction(conn);
				return null;
			}
		});
	}
}