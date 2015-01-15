package jp.massbank.spectrumsearch.accessor;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;

import org.apache.log4j.Logger;

public abstract class AbstractDbAccessor<T> extends DbAccessor {
	
	private static final Logger LOGGER = Logger.getLogger(AbstractDbAccessor.class);
	
	//http://db.apache.org/derby/integrate/plugin_help/derby_app.html
	//http://zetcode.com/db/apachederbytutorial/jdbc/
	
//	private static String dbURL = "jdbc:derby:./src/test/testdata/massbankdb";
//	private static String dbURL = "jdbc:derby:" + SystemProperties.getInstance().getDatabasePath(); // Embedded Connection
//	private static Connection conn = null;
//	private static Statement stmt = null;
//	private static PreparedStatement prepStmt = null;
	
	protected abstract T convert(ResultSet rs) throws SQLException;
	public abstract void insert(T t);
	public abstract void deleteAll();
	public abstract void dropTable();
	public abstract void createTable();
	
	protected T uniqueGeneric(String sql) {
		List<T> list = listGeneric(sql);
		T result = null;
		if (list.size() > 0) {
			result = list.get(0);
		}
		return result;
	}
	
	protected List<T> listGeneric(String sql) {
		List<T> result = new ArrayList<T>();
		try {
//			createConnection();
			createStatment();
			result = listResult(sql);
        } catch (SQLException e) {
        	errorLog(sql, e);
        } finally {
        	try {
        		closeStatment();
//	        	closeConnection();
        	} catch (SQLException e) {
        		errorLog(sql, e);
        	}
        }
		return result;
	}
	
	protected List<String> listString(String sql) {
		List<String> result = new ArrayList<String>();
		try {
//			createConnection();
			createStatment();
			result = listResultString(sql);
		} catch (SQLException e) {
			errorLog(sql, e);
		} finally {
			try {
				closeStatment();
//	        	closeConnection();
			} catch (SQLException e) {
				errorLog(sql, e);
			}
		}
		return result;
	}
	
	protected boolean insert(String sql) {
		try {
			createStatment();
			return stmt.execute(sql);
        } catch (SQLException e) {
        	errorLog(sql, e);
        } finally {
        	try {
        		closeStatment();
        	} catch (SQLException e) {
        		errorLog(sql, e);
        	}
        }
		return false;
	}
	
	protected void addBatch(String sql) {
		try {
			createStatment();
			stmt.addBatch(sql);
		} catch (SQLException e) {
			errorLog(sql, e);
		}
	}
	
	protected int insertWithReturn(String sql) {
		try {
//			createConnection();
			createPreparedStatement(sql);
			int affectedRows = pstmt.executeUpdate();
			
			if (affectedRows == 0) {
	            throw new SQLException("Creating failed, no rows affected.");
	        }
			
			try (ResultSet generatedKeys = pstmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                return generatedKeys.getInt(1);
	            } else {
	                throw new SQLException("Creating user failed, no ID obtained.");
	            }
	        }
			
		} catch (SQLException e) {
			errorLog(sql, e);
		} finally {
			try {
				closePreparedStatement();
//				closeConnection();
			} catch (SQLException e) {
				errorLog(sql, e);
			}
		}
		
		return 0;
	}
	
	protected boolean delete(String sql) {
		try {
//			createConnection();
			createStatment();
			return stmt.execute(sql);
        } catch (SQLException e) {
        	errorLog(sql, e);
        } finally {
        	try {
        		closeStatment();
//	        	closeConnection();
        	} catch (SQLException e) {
        		errorLog(sql, e);
        	}
        }
		return false;
	}
	
	protected void executeStatement(String sql) {
		try {
//			createConnection();
			createStatment();
			stmt.execute(sql);
		} catch (SQLException e) {
			if ("X0Y32".equals(e.getSQLState())) {
				LOGGER.info(sql);
				LOGGER.info("Table already exist");
			} else {
				errorLog(sql, e);
			}
		} finally {
			try {
				closeStatment();
//				closeConnection();
			} catch (SQLException e) {
				errorLog(sql, e);
			}
		}
	}
	
	// private methods
	
//	private void createConnection() {
//        try {
//        	if (conn == null || conn.isClosed()) {
//	        	Properties connectionProps = new Properties();
//	        	// connectionProps.put("user", "massbank");
//	        	// connectionProps.put("password", "massbank");
//	        	
//	            // Get a connection
//	            conn = DriverManager.getConnection(dbURL + ";create=true", connectionProps); 
//        	}
//        } catch (Exception e) {
//        	LOGGER.error(e.getMessage(), e);
//        }
//    }
	
//	private void closeConnection() {
//		if (conn != null) {
//	        try {
//	        	// shutdown single database
//	            DriverManager.getConnection(dbURL + ";shutdown=true");
//	            conn.close();
//	        } catch (SQLException e) {
//	        	if ("08006".equals(e.getSQLState())) {
//	        		LOGGER.info("Derby shut down normally");
//	        	} else {
//	        		LOGGER.error(e.getMessage(), e);
//	        	}
//	        }
//		}           
//    }
	
	private List<T> listResult(String sql) throws SQLException {
		List<T> result = new ArrayList<T>();
		if (stmt != null) {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				result.add(convert(rs));
			}
			if (rs != null) {
				rs.close();
			}
		}
		return result;
	}
	
	private List<String> listResultString(String sql) throws SQLException {
		List<String> result = new ArrayList<String>();
		if (stmt != null) {
			ResultSet rs = stmt.executeQuery(sql);
			while(rs.next()) {
				result.add(rs.getString(1));
			}
			if (rs != null) {
				rs.close();
			}
		}
		return result;
	}
	
}
