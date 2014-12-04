package jp.massbank.spectrumsearch.db.accessor;

import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
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
	private static Statement stmt = null;
	private static PreparedStatement prepStmt = null;
	
	protected abstract T convert(ResultSet rs) throws SQLException;
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
        } catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
        } finally {
        	try {
        		closeStatment();
//	        	closeConnection();
        	} catch (SQLException e) {
        		LOGGER.error(e.getMessage(), e);
        	}
        }
		return result;
	}
	
	protected List<String> listString(String sql) {
		List<String> result = new ArrayList<String>();
		try {
//			createConnection();
			createStatment();
			result = listResulrString(sql);
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				closeStatment();
//	        	closeConnection();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		return result;
	}
	
	protected boolean insert(String sql) {
		try {
//			createConnection();
			createStatment();
			return stmt.execute(sql);
        } catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
        } finally {
        	try {
        		closeStatment();
//	        	closeConnection();
        	} catch (SQLException e) {
        		LOGGER.error(e.getMessage(), e);
        	}
        }
		return false;
	}
	
	protected void addBatch(String sql) {
		try {
			createStatment();
			stmt.addBatch(sql);
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	public void executeBatch() {
		try {
			stmt.executeBatch();
		} catch (SQLException e) {
			LOGGER.error(e.getMessage(), e);
		}
	}
	
	protected int insertWithReturn(String sql) {
		try {
//			createConnection();
			createPreparedStatement(sql);
			int affectedRows = prepStmt.executeUpdate();
			
			if (affectedRows == 0) {
	            throw new SQLException("Creating failed, no rows affected.");
	        }
			
			try (ResultSet generatedKeys = prepStmt.getGeneratedKeys()) {
	            if (generatedKeys.next()) {
	                return generatedKeys.getInt(1);
	            } else {
	                throw new SQLException("Creating user failed, no ID obtained.");
	            }
	        }
			
		} catch (Exception e) {
			LOGGER.error(e.getMessage(), e);
		} finally {
			try {
				closePreparedStatement();
//				closeConnection();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
			}
		}
		
		return 0;
	}
	
	protected boolean delete(String sql) {
		try {
//			createConnection();
			createStatment();
			return stmt.execute(sql);
        } catch (Exception e) {
        	LOGGER.error(e.getMessage(), e);
        } finally {
        	try {
        		closeStatment();
//	        	closeConnection();
        	} catch (SQLException e) {
        		LOGGER.error(e.getMessage(), e);
        	}
        }
		return false;
	}
	
	protected void execStmt(String sql) {
		try {
//			createConnection();
			createStatment();
			stmt.execute(sql);
		} catch (SQLException e) {
			if ("X0Y32".equals(e.getSQLState())) {
				LOGGER.info(sql);
				LOGGER.info("Table already exist");
			} else {
				LOGGER.error(e.getMessage(), e);
			}
		} finally {
			try {
				closeStatment();
//				closeConnection();
			} catch (SQLException e) {
				LOGGER.error(e.getMessage(), e);
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
	
	private void createStatment() throws SQLException {
		if (conn != null || !conn.isClosed()) {
			stmt = conn.createStatement();
		}
	}
	
	private void closeStatment() throws SQLException {
		if (stmt != null) {
            stmt.close();
        }
	}
	
	private void createPreparedStatement(String sql) throws SQLException {
		if (conn != null || !conn.isClosed()) {
			prepStmt = conn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS);
		}
	}
	
	private void closePreparedStatement() throws SQLException {
		if (prepStmt != null) {
			prepStmt.close();
		}
	}
	
	private T uniqueResult(String sql) throws SQLException {
		List<T> result = listResult(sql);
		if (result.size() > 0) {
			return result.get(0);
		}
		return null;
	}
	
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
	
	private List<String> listResulrString(String sql) throws SQLException {
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
