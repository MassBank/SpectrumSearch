package jp.massbank.spectrumsearch.accessor;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.ResultSetMetaData;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Properties;

import jp.massbank.spectrumsearch.entity.constant.SystemProperties;

import org.apache.log4j.Logger;

public class DbAccessor {
	
	private static final Logger LOGGER = Logger.getLogger(DbAccessor.class);
	
	private static String dbURL = "jdbc:derby:" + SystemProperties.getInstance().getDatabasePath(); // Embedded Connection
	private static boolean isExeCustomFunctions;
	protected static Statement stmt = null;
	protected static PreparedStatement pstmt = null;
	protected static Connection conn = null;
	
	public static void createConnection() throws SQLException {
		if (conn == null || conn.isClosed()) {
			Properties connectionProps = new Properties();
			// connectionProps.put("user", "massbank");
			// connectionProps.put("password", "massbank");
			
			// Get a connection
			conn = DriverManager.getConnection(dbURL + ";create=true", connectionProps);
			LOGGER.info("Derby start");
			executeCustomFunctions();
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
	
	public static int rowCount(String sql) {
		int result = 0;
		try {
			createPreparedStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			rs.last();
			result = rs.getRow();
			rs.close();
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				closePreparedStatement();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return result;
	}
	
	public static List<Map<Integer, Object>> execQuery(String sql) {
		List<Map<Integer, Object>> result = new ArrayList<Map<Integer, Object>>();
		try {
			createPreparedStatement(sql);
			ResultSet rs = pstmt.executeQuery();
			ResultSetMetaData rsmd = rs.getMetaData();
			int columnsCount = rsmd.getColumnCount();
			while (rs.next() && columnsCount > 0) {
				Map<Integer, Object> rowResult = new HashMap<Integer, Object>();
				for (int i = 1; i <= columnsCount; i++) {
					rowResult.put(i, rs.getObject(i));
				}
				result.add(rowResult);
			}
			rs.close();
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				closePreparedStatement();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return result;
	}
	
//	public static List<Map<String, Object>> execQuery(String sql) {
//		List<Map<String, Object>> result = new ArrayList<Map<String,Object>>();
//		try {
//			createPreparedStatement(sql);
//			ResultSet rs = pstmt.executeQuery();
//			ResultSetMetaData rsmd = rs.getMetaData();
//			int columnsCount = rsmd.getColumnCount();
//			if (rs.next() && columnsCount > 0) {
//				Map<String, Object> rowResult = new HashMap<String, Object>();
//				for (int i = 0; i < columnsCount; i++) {
//					rowResult.put(rsmd.getColumnName(i), rs.getObject(i));
//				}
//				result.add(rowResult);
//			}
//			rs.close();
//		} catch (SQLException e) {
//			LOGGER.error(e.getMessage(), e);
//		} finally {
//			try {
//				closePreparedStatement();
//			} catch (SQLException e) {
//				LOGGER.error(e.getMessage(), e);
//			}
//		}
//		return result;
//	}
	
	protected void createStatment() throws SQLException {
		if (conn != null || !conn.isClosed()) {
			stmt = conn.createStatement();
		}
	}
	
	protected void closeStatment() throws SQLException {
		if (stmt != null) {
            stmt.close();
        }
	}
	
	protected static void createPreparedStatement(String sql) throws SQLException {
		if (conn != null || !conn.isClosed()) {
			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}
	}
	
	protected static void closePreparedStatement() throws SQLException {
		if (pstmt != null) {
			pstmt.close();
		}
	}

	private static void executeCustomFunctions() {
		if (!isExeCustomFunctions) {
			LOGGER.info("start exec custom functions");
			try {
				conn.createStatement().execute("DROP FUNCTION  CONCAT");
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
			try {
				conn.createStatement().execute("DROP FUNCTION  LPAD");
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
			try {
				conn.createStatement().execute("DROP FUNCTION  CASTDOUBLE");
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
			try {
				conn.createStatement().execute("DROP FUNCTION  CASTINTEGER");
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
			try {
				conn.createStatement().execute(
						"CREATE FUNCTION  CONCAT(DATA VARCHAR(32000)) RETURNS VARCHAR(32000) " +
						"EXTERNAL NAME 'jp.massbank.spectrumsearch.util.DbUtil.concat' " +
						"LANGUAGE JAVA PARAMETER STYLE JAVA"
						);
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
			try {
				conn.createStatement().execute(
						"CREATE FUNCTION  LPAD(DATA VARCHAR(32000), LENGTH INTEGER, PADCHAR CHAR(1)) RETURNS VARCHAR(32000) " +
								"EXTERNAL NAME 'jp.massbank.spectrumsearch.util.DbUtil.lpad' " +
								"LANGUAGE JAVA PARAMETER STYLE JAVA"
						);
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
			try {
				conn.createStatement().execute(
						"CREATE FUNCTION  CASTDOUBLE(DATA DOUBLE) RETURNS VARCHAR(32000) " +
								"EXTERNAL NAME 'jp.massbank.spectrumsearch.util.DbUtil.castDouble' " +
								"LANGUAGE JAVA PARAMETER STYLE JAVA"
						);
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
			try {
				conn.createStatement().execute(
						"CREATE FUNCTION  CASTINTEGER(DATA INT) RETURNS VARCHAR(32000) " +
								"EXTERNAL NAME 'jp.massbank.spectrumsearch.util.DbUtil.castInteger' " +
								"LANGUAGE JAVA PARAMETER STYLE JAVA"
						);
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
			isExeCustomFunctions = true;
			LOGGER.info("end exec custom functions");
		}
	}
	
}
