package jp.massbank.spectrumsearch.db.accessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;

import jp.massbank.spectrumsearch.entity.constant.SystemProperties;

import org.apache.log4j.Logger;

public class DbAccessor {
	
	private static final Logger LOGGER = Logger.getLogger(DbAccessor.class);
	
	private static String dbURL = "jdbc:derby:" + SystemProperties.getInstance().getDatabasePath(); // Embedded Connection
	protected static Connection conn = null;
	
	public static void createConnection() throws SQLException {
		if (conn == null || conn.isClosed()) {
			Properties connectionProps = new Properties();
			// connectionProps.put("user", "massbank");
			// connectionProps.put("password", "massbank");
			
			// Get a connection
			conn = DriverManager.getConnection(dbURL + ";create=true", connectionProps); 
		}
    }
	
	public static void setAutoCommit(boolean status) throws SQLException {
		conn.setAutoCommit(status);
	}
	
	public static void commit() throws SQLException {
		conn.commit();
	}
	
	public static void rollback() throws SQLException {
		conn.rollback();
	}
	
	public static void closeConnection() throws SQLException {
		if (conn != null) {
	        try {
	        	// shutdown single database
	            DriverManager.getConnection(dbURL + ";shutdown=true");
	            conn.close();
	        } catch (SQLException e) {
	        	if ("08006".equals(e.getSQLState())) {
	        		LOGGER.info("Derby shut down normally");
	        	}
	        }
		}           
    }

}
