package jp.massbank.spectrumsearch.db;

import static org.junit.Assert.*;

import java.sql.Connection;
import java.sql.SQLException;
import java.util.List;

import org.apache.log4j.Logger;
import org.junit.Test;

public class DbAccessorTest {
  static final Logger LOGGER = Logger.getLogger(DbAccessorTest.class);

  @Test
  public void testGetConnection() throws SQLException {

    Connection conn = DbAccessor.getConnection();
    LOGGER.info(conn.getMetaData().getURL());
  }

  @Test
  public void testGetAllInstrument() throws SQLException {
    DbAccessor.getConnection();
    List<Instrument> result = DbAccessor.getAllInstrument();
    LOGGER.info(result);
  }
  
  @Test
  public void testGetMsType() throws SQLException {
    DbAccessor.getConnection();
    List<String> result = DbAccessor.getMsType();
    LOGGER.info(result);
  }
  
  @Test
  public void testGetSpectrumNameByName() throws SQLException {
    DbAccessor.getConnection();
    List<String> result = DbAccessor.getSpectrumNameByName(null, null);
    LOGGER.info(result.size());
    for (String str : result){
      
      LOGGER.info(str);
    }
    result = DbAccessor.getSpectrumNameByName("Dime", "end");
    LOGGER.info(result.size());
    for (String str : result){
      LOGGER.info(str);
    }
    result = DbAccessor.getSpectrumNameByName("sine", "start");
    LOGGER.info(result.size());
    for (String str : result){
      LOGGER.info(str);
    }

  }
  
  @Test
  public void testGetSpectrumData() throws SQLException {
    DbAccessor.getConnection();
    String result = DbAccessor.getSpectrumData("KO000001", false, 0);
    LOGGER.info(result);
  }
}
