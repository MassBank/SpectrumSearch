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
			LOGGER.debug("Derby start");
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
	        		LOGGER.debug("Derby shut down normally");
	        	}
	        }
		}           
    }
	
//	public static int rowCount(String sql) {
//		int result = 0;
//		try {
//			createPreparedStatement(sql);
//			ResultSet rs = pstmt.executeQuery();
//			rs.last();
//			result = rs.getRow();
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
	
	public static void execUpdate(String sql) {
		try {
			createPreparedStatement(sql);
			pstmt.executeUpdate();
		} catch (SQLException e) {
			errorLog(sql, e);
		} finally {
			try {
				closePreparedStatement();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
	}

	public static void execute(String sql) {
		try {
			createPreparedStatement(sql);
			pstmt.execute();
		} catch (SQLException e) {
			errorLog(sql, e);
		} finally {
			try {
				closePreparedStatement();
			} catch (SQLException e) {
				LOGGER.error(sql, e);
			}
		}
	}
	
	public static List<Map<Integer, Object>> execResultQuery(String sql) {
		List<Map<Integer, Object>> result = new ArrayList<Map<Integer, Object>>();
		try {
			createPreparedStatement(sql);
			result = getQueryResult();
		} catch (SQLException e) {
			errorLog(sql, e);
		} finally {
			try {
				closePreparedStatement();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return result;
	}
	
	private static List<Map<Integer, Object>> getQueryResult() throws SQLException {
		List<Map<Integer, Object>> result = new ArrayList<Map<Integer, Object>>();
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
	
	public static int[] executeBatch() throws SQLException {
		int[] result = new int[0];
		if (stmt != null && !stmt.isClosed()) {
			result = stmt.executeBatch();
		}
		return result;
	}
	
	public static int[] executeBatchAndCloseStatment() throws SQLException {
		int[] result = new int[0];
		if (stmt != null && !stmt.isClosed()) {
			result = stmt.executeBatch();
			stmt.close();
		}
		return result;
	}
	
	protected void createStatment() throws SQLException {
		if ((stmt == null || stmt.isClosed()) && 
				(conn != null && !conn.isClosed())) {
			stmt = conn.createStatement();
		}
	}
	
	protected void closeStatment() throws SQLException {
		if (stmt != null && !stmt.isClosed()) {
            stmt.close();
        }
	}
	
	protected static void createPreparedStatement(String sql) throws SQLException {
		if (conn != null && !conn.isClosed()) {
//			pstmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
			
//			long key = -1L;
//			PreparedStatement statement = connection.prepareStatement();
//			statement.executeUpdate(YOUR_SQL_HERE, PreparedStatement.RETURN_GENERATED_KEYS);
//			ResultSet rs = statement.getGeneratedKeys();
//			if (rs != null && rs.next()) {
//			    key = rs.getLong(1);
//			}
			
			pstmt = conn.prepareStatement(sql);
		}
	}
	
	protected static void closePreparedStatement() throws SQLException {
		if (pstmt != null && !pstmt.isClosed()) {
			pstmt.close();
		}
	}
	
	protected static void executeAndClosePreparedStatement() throws SQLException {
		if (pstmt != null && !pstmt.isClosed()) {
			pstmt.execute();
			pstmt.close();
		}
	}
	
	protected static void executeBatchAndClosePreparedStatement() throws SQLException {
		if (pstmt != null && !pstmt.isClosed()) {
			pstmt.executeBatch();
			pstmt.close();
		}
	}
	
	protected static void errorLog(String sql, SQLException e) {
		if ("X0Y32".equals(e.getSQLState())) { // table already exist
			LOGGER.error("Table/View already exist: SQL ===> " + sql);
			LOGGER.error(e.getMessage());
		} else if ("42X05".equals(e.getSQLState())) { // table does not exist
			LOGGER.error("Table/View does not exist: SQL ===> " + sql);
			LOGGER.error(e.getMessage());
		} else if ("42Y55".equals(e.getSQLState())) { // function doesn't exist.
			LOGGER.error("SQL ===> " + sql);
			LOGGER.error(e.getMessage());
		} else if ("42X65".equals(e.getSQLState())) { // index doesn't exist.
			LOGGER.error("SQL ===> " + sql);
			LOGGER.error(e.getMessage());
		} else {
			LOGGER.error("SQL ===> " + sql);
			LOGGER.error(e.getMessage(), e);
		}
	}

	private static void executeCustomFunctions() {
		if (!isExeCustomFunctions) {
			LOGGER.debug("start exec custom functions");
			execute("DROP FUNCTION CONCAT");
			execute("DROP FUNCTION LPAD");
			execute("DROP FUNCTION CASTDOUBLE");
			execute("DROP FUNCTION REGEXP_LIKE");
			execute("DROP FUNCTION CASTINTEGER");
			execute("DROP DERBY AGGREGATE STRMAX RESTRICT");
			
			execute("CREATE FUNCTION  CONCAT(DATA VARCHAR(32000)) RETURNS VARCHAR(32000) " +
					"EXTERNAL NAME 'jp.massbank.spectrumsearch.util.DbUtil.concat' " +
					"LANGUAGE JAVA PARAMETER STYLE JAVA");
			execute("CREATE FUNCTION  LPAD(DATA VARCHAR(32000), LENGTH INTEGER, PADCHAR CHAR(1)) RETURNS VARCHAR(32000) " +
					"EXTERNAL NAME 'jp.massbank.spectrumsearch.util.DbUtil.lpad' " +
					"LANGUAGE JAVA PARAMETER STYLE JAVA");
			execute("CREATE FUNCTION  CASTDOUBLE(DATA DOUBLE) RETURNS VARCHAR(32000) " +
					"EXTERNAL NAME 'jp.massbank.spectrumsearch.util.DbUtil.castDouble' " +
					"LANGUAGE JAVA PARAMETER STYLE JAVA");
			execute("CREATE FUNCTION  REGEXP_LIKE(DATA VARCHAR(32000), PATTERN VARCHAR(32000)) RETURNS BOOLEAN " +
					"EXTERNAL NAME 'jp.massbank.spectrumsearch.util.DbUtil.regexplike' " +
					"LANGUAGE JAVA PARAMETER STYLE JAVA");
			execute("CREATE FUNCTION  CASTINTEGER(DATA INT) RETURNS VARCHAR(32000) " +
					"EXTERNAL NAME 'jp.massbank.spectrumsearch.util.DbUtil.castInteger' " +
					"LANGUAGE JAVA PARAMETER STYLE JAVA");
			execute("CREATE DERBY AGGREGATE STRMAX FOR VARCHAR(32000) RETURNS VARCHAR(32000) " +
					"EXTERNAL NAME 'jp.massbank.spectrumsearch.util.StrMax'");
			isExeCustomFunctions = true;
			LOGGER.debug("end exec custom functions");
		}
	}
	
}
